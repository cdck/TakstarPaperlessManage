package com.xlk.takstarpaperlessmanage.view.play;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Environment;
import android.util.Range;
import android.view.Surface;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfacePlaymedia;
import com.mogujie.tt.protobuf.InterfaceStop;
import com.xlk.takstarpaperlessmanage.App;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;
import com.xlk.takstarpaperlessmanage.model.bean.MediaBean;
import com.xlk.takstarpaperlessmanage.ui.gl.MyGLSurfaceView;
import com.xlk.takstarpaperlessmanage.util.DateUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static com.xlk.takstarpaperlessmanage.model.Constant.RESOURCE_ID_0;

/**
 * @author Created by xlk on 2021/5/20.
 * @desc
 */
public class PlayPresenter extends BasePresenter<PlayContract.View> implements PlayContract.Presenter {

    private Surface mSurface;

    private int mStatus;
    private int mMediaId;
    private int currentPre;
    private int length;
    LinkedBlockingQueue<MediaBean> queue = new LinkedBlockingQueue<>();
    private MediaCodec mediaCodec;
    private int initW, initH;
    private MediaCodec.BufferInfo info;
    private MediaFormat mediaFormat;
    private String saveMimeType = "";
    private boolean isStop;
    private long lastPushTime;//最后有数据的时间
    long framepersecond = 80;//估计每秒的播放时间 单位：毫秒
    private releaseThread timeThread;
    private boolean isShareing;//是否正在同屏中
    private List<Integer> currentShareIds = new ArrayList<>();

    private final List<InterfaceMember.pbui_Item_MemberDetailInfo> memberInfos = new ArrayList<>();
    public List<InterfaceDevice.pbui_Item_DeviceDetailInfo> onLineProjectors = new ArrayList<>();
    //    public List<DevMember> onLineMember = new ArrayList<>();
    private int mValue;

    public PlayPresenter(PlayContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            case EventType.BUS_YUV_DISPLAY: {
                Object[] objs = msg.getObjects();
                int res = (int) objs[0];
                int w = (int) objs[1];
                int h = (int) objs[2];
                byte[] y = (byte[]) objs[3];
                byte[] u = (byte[]) objs[4];
                byte[] v = (byte[]) objs[5];
                mView.updateYuv(w, h, y, u, v);
                break;
            }
            case EventType.BUS_VIDEO_DECODE: {
                Object[] objs = msg.getObjects();
                int iskeyframe = (int) objs[0];
                int res = (int) objs[1];
                int codecid = (int) objs[2];
                int width = (int) objs[3];
                int height = (int) objs[4];
                byte[] packet = (byte[]) objs[5];
                long pts = (long) objs[6];
                byte[] codecdata = (byte[]) objs[7];
                String mimeType = Constant.getMimeType(codecid);
                if (packet != null) {
                    lastPushTime = System.currentTimeMillis();
                    length = packet.length;
                    LogUtils.d(TAG, "getEventMessage :  mimeType --> " + mimeType + "，宽高：" + width + "," + height + ", pts=" + pts);
                    if (!saveMimeType.equals(mimeType) || initW != width || initH != height || mediaCodec == null) {
                        if (mediaCodec != null) {
                            //调用stop方法使其进入 uninitialzed 状态，这样才可以重新配置MediaCodec
                            mediaCodec.stop();
                        }
                        saveMimeType = mimeType;
                        initCodec(width, height, codecdata);
                    }
                    read2file(packet, codecdata);
                }
                mediaCodecDecode(packet, length, pts, iskeyframe);
                if (timeThread == null && !isStop) {
                    timeThread = new releaseThread();
                    timeThread.start();
                }
                break;
            }
            //播放进度通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAYPOSINFO_VALUE: {
                byte[] datas = (byte[]) msg.getObjects()[0];
                InterfacePlaymedia.pbui_Type_PlayPosCb playPos = InterfacePlaymedia.pbui_Type_PlayPosCb.parseFrom(datas);
                int mediaId = playPos.getMediaId();
                //当status>0时，为文件ID号
                int status = playPos.getStatus();
                int per = playPos.getPer();
                int sec = playPos.getSec();
                this.mMediaId = mediaId;
                this.mStatus = status;
                currentPre = per;
                //只有在播放中才更新进度相关UI
                if (status == 0) {
                    byte[] timedata = jni.queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_TIME.getNumber(),
                            this.mMediaId);
                    InterfaceBase.pbui_CommonInt32uProperty commonInt32uProperty = InterfaceBase.pbui_CommonInt32uProperty.parseFrom(timedata);
                    int propertyval = commonInt32uProperty.getPropertyval();
                    mView.updateProgressUi(per, DateUtil.convertPlayTime((long) sec * 1000), DateUtil.convertPlayTime((long) propertyval));

                    byte[] fileName = jni.queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_NAME.getNumber(),
                            this.mMediaId);
                    InterfaceBase.pbui_CommonTextProperty pbui_commonTextProperty = InterfaceBase.pbui_CommonTextProperty.parseFrom(fileName);
                    mView.updateTopTitle(pbui_commonTextProperty.getPropertyval().toStringUtf8());
                }
                if (status == 0 || status == 1) mView.updateAnimator(status);
                break;
            }
            //停止播放通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STOPPLAY_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    byte[] o = (byte[]) msg.getObjects()[0];
                    InterfaceStop.pbui_Type_MeetStopPlay stopPlay = InterfaceStop.pbui_Type_MeetStopPlay.parseFrom(o);
                    if (stopPlay.getRes() == 0) {
                        LogUtils.d(TAG, "BusEvent -->" + "停止播放通知");
                        mView.close();
                    }
                }
                break;
            }
            //设备寄存器变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE: {
                LogUtils.d(TAG, "BusEvent -->" + "设备寄存器变更通知");

                break;
            }
            //参会人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE: {
                LogUtils.d(TAG, "BusEvent -->" + "参会人员变更通知");
                queryMember();
                break;
            }
            //界面状态变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS_VALUE: {
                LogUtils.d(TAG, "BusEvent -->" + "界面状态变更通知");
                queryMember();
                break;
            }
            default:
                break;
        }
    }


    /**
     * 初始化解码器
     *
     * @param w         宽
     * @param h         高
     * @param codecdata pps/sps 编码配置数据
     */
    private void initCodec(int w, int h, byte[] codecdata) {
        try {
            mView.setCodecType(1);
            //1.创建了一个编解码器，此时编解码器处于未初始化状态（Uninitialized）
            mediaCodec = MediaCodec.createDecoderByType(saveMimeType);
            /**  宽高要判断是否是解码器所支持的范围  */
            MediaCodecInfo.CodecCapabilities capabilitiesForType = mediaCodec.getCodecInfo().getCapabilitiesForType(saveMimeType);
            MediaCodecInfo.VideoCapabilities videoCapabilities = capabilitiesForType.getVideoCapabilities();
            Range<Integer> supportedWidths = videoCapabilities.getSupportedWidths();
            Integer upper = supportedWidths.getUpper();
            Integer lower = supportedWidths.getLower();
            Range<Integer> supportedHeights = videoCapabilities.getSupportedHeights();
            Integer upper1 = supportedHeights.getUpper();
            Integer lower1 = supportedHeights.getLower();
            initW = w;
            initH = h;
            w = supportedWidths.clamp(w);
            h = supportedHeights.clamp(h);
            LogUtils.e(TAG, "initCodec :   --> " + upper + ", " + lower + " ,,高：" + upper1 + ", " + lower1);
            initMediaFormat(w, h, codecdata);
            boolean formatSupported = capabilitiesForType.isFormatSupported(mediaFormat);
            LogUtils.i(TAG, "initCodec :  是否支持 --> " + formatSupported);
            info = new MediaCodec.BufferInfo();
            try {
                //2.对编解码器进行配置，这将使编解码器转为配置状态（Configured）
                mediaCodec.configure(mediaFormat, mSurface, null, 0);
            } catch (IllegalArgumentException e) {
                String message = e.getMessage();
                String string = e.toString();
                String localizedMessage = e.getLocalizedMessage();
                LogUtils.e(TAG, "initCodec  configure方法异常捕获 --> \n" + message + "\n toString: " + string + "\n localizedMessage: " + localizedMessage);
                e.printStackTrace();
            } catch (MediaCodec.CodecException e) {
                //可能是由于media内容错误、硬件错误、资源枯竭等原因所致
                //可恢复错误（recoverable errors）：如果isRecoverable() 方法返回true,然后就可以调用stop(),configure(...),以及start()方法进行修复
                //短暂错误（transient errors）：如果isTransient()方法返回true,资源短时间内不可用，这个方法可能会在一段时间之后重试。
                //isRecoverable()和isTransient()方法不可能同时都返回true。
                LogUtils.e(TAG, "initCodec :   -->可恢复错误： " + e.isRecoverable() + ",短暂错误：" + e.isTransient());
            }
            //3.调用start()方法使其转入执行状态（Executing）
            mediaCodec.start();
            initMediaMuxer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedOutputStream outputStream;

    private void initMediaMuxer() throws IOException {
        if (!App.read2file) return;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.mp4");
        if (file.exists()) {
            file.delete();
        }
//        muxer = new MediaMuxer(savepath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        outputStream = new BufferedOutputStream(new FileOutputStream(file));
    }

    public void read2file(byte[] outData, byte[] codecdata) {
        if (!App.read2file) return;
        try {
            outputStream.write(outData, 0, outData.length);
            outputStream.write(codecdata, 0, codecdata.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMediaFormat(int w, int h, byte[] codecdata) {
        LogUtils.e(TAG, "initMediaFormat :   --> " + (mediaFormat == null));
        mediaFormat = MediaFormat.createVideoFormat(saveMimeType, w, h);
        mediaFormat.setInteger(MediaFormat.KEY_WIDTH, w);
        mediaFormat.setInteger(MediaFormat.KEY_HEIGHT, h);
        //设置最大输出大小
        mediaFormat.setLong(MediaFormat.KEY_MAX_INPUT_SIZE, w * h);
        if (codecdata != null) {
            mediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(codecdata));
            mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(codecdata));
        }
        mediaCodec.getCodecInfo().getCapabilitiesForType(saveMimeType).isFormatSupported(mediaFormat);
    }

    private void mediaCodecDecode(byte[] bytes, int size, long pts, int iskeyframe) {
        if (isStop) return;
        if (bytes != null && bytes.length > 0) {
            //把网络接收到的视频数据先加入到队列中
            queue.offer(new MediaBean(bytes, size, pts, iskeyframe));
        } else {
            //bytes为null也不能立马返回，需要处理从视频队列中送数据到解码buffer 和 解码好的视频的显示
        }
        int queuesize = queue.size();
        LogUtils.i(TAG, " mediaCodecDecode -->queuesize: " + queuesize);
        if (queuesize > 500) {
            //当解码速度太慢，导致视频数据积累太多，这种情况下要处理丢包，丢包的策略把前面的关键帧组全部丢包，保留后面两个关键帧组
            //丢帧必须按照I帧P帧连续的丢，否则会造成花屏的情况
            int keyframenum = 0;
            MediaBean poll;
            //先统计队列中有多少个关键帧
            for (int ni = 0; ni < queuesize; ++ni) {
                poll = queue.peek();
                if (poll.getIskeyframe() == 1)
                    keyframenum++;
            }
            for (int ni = 0; ni < queuesize; ++ni) {
                poll = queue.peek();
                if (poll.getIskeyframe() == 1) {
                    keyframenum--;
                    if (keyframenum < 2) {
                        //将该帧放回队列头，因为丢包已经丢了前面的关键帧组，保留后面的两个组
                        break;
                    }
                }
                LogUtils.e(TAG, "mediaCodecDecode 其它帧在此丢掉 -->");
                //其它帧在此丢掉,不处理
                queue.poll();
            }
            //重新计算队列大小
            queuesize = queue.size();
        }
        //判断解码器是否初始化完成
        if (mediaCodec == null)
            return;
        //队列中有视频帧，检查解码队列中是否有空闲可用的buffer，有则取视频帧送进去解码
        if (queuesize > 0) {
            int inputBufferIndex = -1;
            try {
                inputBufferIndex = mediaCodec.dequeueInputBuffer(0);
                if (inputBufferIndex >= 0) {
                    //有空闲可用的解码buffer
                    ByteBuffer byteBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
                    byteBuffer.clear();
                    //将视频队列中的头取出送到解码队列中
                    MediaBean poll = queue.poll();
                    byteBuffer.put(poll.getBytes());
                    mediaCodec.queueInputBuffer(inputBufferIndex, 0, poll.getSize(), poll.getPts(), 0);
                }
            } catch (IllegalStateException e) {
                //如果解码出错，需要提示用户或者程序自动重新初始化解码
                mediaCodec = null;
                return;
            }
        }
        //判断解码显示buffer是否初始化完成
        if (info == null)
            return;
        //判断下一帧的播放时间是否已经到了
//        if (System.currentTimeMillis() - lastplaytime < framepersecond) {
//            return;
//        }
        int index = mediaCodec.dequeueOutputBuffer(info, 0);
        if (index >= 0) {
            ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(index);
            outputBuffer.position(info.offset);
            outputBuffer.limit(info.offset + info.size);
            LogUtils.i(TAG, "mediaCodecDecode --> dequeueOutputBuffer：查看info：" + index
                    + "\nflags：" + info.flags + ", offset：" + info.offset + ", size：" + info.size
                    + ", presentationTimeUs：" + info.presentationTimeUs);
//            mediaCodec.releaseOutputBuffer(index, info.presentationTimeUs);
            //如果配置编码器时指定了有效的surface，传true将此输出缓冲区显示在surface
            mediaCodec.releaseOutputBuffer(index, true);
        }
    }

    @Override
    public void queryMember() {

    }

    @Override
    public void setSurface(Surface surface) {
        mSurface = surface;
    }

    @Override
    public void releasePlay() {
        if (timeThread != null) {
            timeThread.interrupt();
            timeThread = null;
        }
        releaseMediaCodec();
    }

    /**
     * 释放资源
     */
    private void releaseMediaCodec() {
        App.threadPool.execute(() -> {
            if (mediaCodec != null) {
                try {
                    LogUtils.e(TAG, "releaseMediaCodec :   --> ");
                    mediaCodec.reset();
                    //调用stop()方法使编解码器返回到未初始化状态（Uninitialized），此时这个编解码器可以再次重新配置
                    mediaCodec.stop();
                    //调用flush()方法使编解码器重新返回到刷新子状态（Flushed）
                    mediaCodec.flush();
                    //使用完编解码器后，你必须调用release()方法释放其资源
                    mediaCodec.release();
                } catch (MediaCodec.CodecException e) {
                    LogUtils.e(TAG, "run :  CodecException --> " + e.getMessage());
                } catch (IllegalStateException e) {
                    LogUtils.e(TAG, "run :  IllegalStateException --> " + e.getMessage());
                } catch (Exception e) {
                    LogUtils.e(TAG, "run :  Exception --> " + e.getMessage());
                }
            }
            mediaCodec = null;
            mediaFormat = null;
        });
    }

    @Override
    public String queryDevName(int deivceid) {
        InterfaceDevice.pbui_Item_DeviceDetailInfo deviceDetailInfo = jni.queryDevInfoById(deivceid);
        if (deviceDetailInfo != null) {
            return deviceDetailInfo.getDevname().toStringUtf8();
        }
        return "";
    }

    @Override
    public void playOrPause() {
        List<Integer> devIds = new ArrayList<>();
        devIds.add(GlobalValue.localDeviceId);
        if (isShareing) {
            devIds.addAll(currentShareIds);
        }
        if (isPlaying()) {
            jni.setPlayStop(RESOURCE_ID_0, devIds);
        } else {
            jni.setPlayRecover(RESOURCE_ID_0, devIds);
        }
    }

    @Override
    public void stopPlay() {
        if (isShareing) {
            List<Integer> res = new ArrayList<>();
            res.add(RESOURCE_ID_0);
            jni.stopResourceOperate(res, currentShareIds);
            currentShareIds.clear();
            isShareing = false;
        }
    }

    @Override
    public void cutVideoImg() {
        if (isPlaying()) {
            List<Integer> devIds = new ArrayList<>();
            devIds.add(GlobalValue.localDeviceId);
            jni.setPlayStop(RESOURCE_ID_0, devIds);
        }
    }

    /**
     * 跟去播放状态判断是否播放中
     *
     * @return true 播放中
     */
    private boolean isPlaying() {
        switch (mStatus) {
            //播放中
            case 0:
                return true;
            //暂停
            case 1:
                return false;
            //停止
            case 2:
                return false;
            //恢复
            case 3:
                return true;
            default:
                return true;
        }
    }

    @Override
    public void setPlayPlace(int progress) {
        List<Integer> devIds = new ArrayList<>();
        devIds.add(GlobalValue.localDeviceId);
        if (isShareing) {
            devIds.addAll(currentShareIds);
        }
        jni.setPlayPlace(RESOURCE_ID_0, progress, devIds, mValue, 0);
    }

    @Override
    public void mediaPlayOperate(List<Integer> ids, int value) {
        for (int id : ids) {
            if (!currentShareIds.contains(id)) {
                currentShareIds.add(id);
            }
        }
        isShareing = true;
        List<Integer> temps = new ArrayList<>(currentShareIds);
        temps.add(GlobalValue.localDeviceId);
        mValue = value;
        jni.mediaPlayOperate(mMediaId, temps, currentPre, RESOURCE_ID_0, value, 0);
    }

    @Override
    public void releaseMediaRes() {

    }


    class releaseThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isStop) {
                //距离上次有数据的时间超过了framepersecond毫秒就进行手动发送
                if (System.currentTimeMillis() - lastPushTime >= framepersecond) {
                    LogUtils.v(TAG, "releaseThread 手动发送空数据 -->");
                    EventBus.getDefault().post(new EventMessage.Builder()
                            .type(EventType.BUS_VIDEO_DECODE)
                            .objects(0, 0, 0, 0, 0, null, 1L, null)
                            .build()
                    );
                    try {
                        sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
