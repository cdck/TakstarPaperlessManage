package com.xlk.takstarpaperlessmanage.view.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDownload;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfacePlaymedia;
import com.mogujie.tt.protobuf.InterfaceUpload;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;
import com.xlk.takstarpaperlessmanage.model.JniHelper;
import com.xlk.takstarpaperlessmanage.model.WpsModel;
import com.xlk.takstarpaperlessmanage.receiver.WpsReceiver;
import com.xlk.takstarpaperlessmanage.util.FileUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;
import com.xlk.takstarpaperlessmanage.view.play.PlayActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import androidx.annotation.Nullable;

/**
 * @author Created by xlk on 2021/5/12.
 * @desc
 */
public class BackService extends Service {
    JniHelper jni = JniHelper.getInstance();
    private WpsReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBusEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            case EventType.BUS_EXPORT_SEAT_SUCCESSFUL: {
                String filePath = (String) msg.getObjects()[0];
                ToastUtil.showLong(getString(R.string.export_successful_, filePath));
                break;
            }
            //平台下载
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DOWNLOAD_VALUE: {
                downloadInform(msg);
                break;
            }
            //上传进度通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_UPLOAD_VALUE: {
                uploadInform(msg);
                break;
            }
            //处理WPS广播监听
            case EventType.BUS_WPS_RECEIVER: {
                boolean isopen = (boolean) msg.getObjects()[0];
                if (isopen) {
                    registerWpsBroadCase();
                } else {
                    unregisterWpsBroadCase();
                }
                break;
            }
            //媒体播放通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY_VALUE: {
                LogUtils.i("onBusEvent 媒体播放通知");
                mediaPlayInform(msg);
                break;
            }
            //流播放通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY_VALUE: {
                LogUtils.i("onBusEvent 流播放通知");
                streamPlayInform(msg);
                break;
            }
        }
    }

    private void mediaPlayInform(EventMessage msg) throws InvalidProtocolBufferException {
        byte[] datas = (byte[]) msg.getObjects()[0];
        InterfacePlaymedia.pbui_Type_MeetMediaPlay mediaPlay = InterfacePlaymedia.pbui_Type_MeetMediaPlay.parseFrom(datas);
        int res = mediaPlay.getRes();
        if (res != 0) {
            //只处理资源ID为0的播放资源
            return;
        }
        int mediaid = mediaPlay.getMediaid();
        int createdeviceid = mediaPlay.getCreatedeviceid();
        int triggerid = mediaPlay.getTriggerid();
        int triggeruserval = mediaPlay.getTriggeruserval();
        boolean isMandatory = triggeruserval == InterfaceMacro.Pb_TriggerUsedef.Pb_EXCEC_USERDEF_FLAG_NOCREATEWINOPER_VALUE;
        int type = mediaid & Constant.MAIN_TYPE_BITMASK;
        int subtype = mediaid & Constant.SUB_TYPE_BITMASK;
        if (type == Constant.MEDIA_FILE_TYPE_AUDIO || type == Constant.MEDIA_FILE_TYPE_VIDEO) {
            LogUtils.i("mediaPlayInform -->" + "媒体播放通知：isVideoPlaying= " + GlobalValue.isVideoPlaying);
            if (createdeviceid != GlobalValue.localDeviceId) {
                GlobalValue.isMandatoryPlaying = isMandatory;
            }
            GlobalValue.haveNewPlayInform = true;
            startActivity(new Intent(this, PlayActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    .putExtra(Constant.EXTRA_VIDEO_ACTION, InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY_VALUE)
                    .putExtra(Constant.EXTRA_VIDEO_SUBTYPE, subtype)
                    .putExtra(Constant.EXTRA_VIDEO_MEDIA_ID, mediaid)
            );
        } else {
            LogUtils.i("mediaPlayInform -->" + "媒体播放通知：下载文件后打开");
        }
    }

    private void streamPlayInform(EventMessage msg) {

    }

    private void registerWpsBroadCase() {
        if (receiver == null) {
            receiver = new WpsReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(WpsModel.Reciver.ACTION_SAVE);
            filter.addAction(WpsModel.Reciver.ACTION_CLOSE);
            filter.addAction(WpsModel.Reciver.ACTION_HOME);
//            filter.addAction(WpsModel.Reciver.ACTION_BACK);
            registerReceiver(receiver, filter);
        }
    }

    private void unregisterWpsBroadCase() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private void uploadInform(EventMessage msg) throws InvalidProtocolBufferException {
        byte[] datas = (byte[]) msg.getObjects()[0];
        InterfaceUpload.pbui_TypeUploadPosCb uploadPosCb = InterfaceUpload.pbui_TypeUploadPosCb.parseFrom(datas);
        String pathName = uploadPosCb.getPathname().toStringUtf8();
        String userStr = uploadPosCb.getUserstr().toStringUtf8();
        int status = uploadPosCb.getStatus();
        int mediaId = uploadPosCb.getMediaId();
        int per = uploadPosCb.getPer();
        int uploadflag = uploadPosCb.getUploadflag();
        int userval = uploadPosCb.getUserval();
        byte[] bytes = jni.queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_NAME.getNumber(), mediaId);
        InterfaceBase.pbui_CommonTextProperty pbui_commonTextProperty = InterfaceBase.pbui_CommonTextProperty.parseFrom(bytes);
        String fileName = pbui_commonTextProperty.getPropertyval().toStringUtf8();
        LogUtils.i("uploadInform -->" + "上传进度：" + per + "\npathName= " + pathName);
        if (status == InterfaceMacro.Pb_Upload_State.Pb_UPLOADMEDIA_FLAG_UPLOADING_VALUE) {
            ToastUtils.showShort(getString(R.string.upload_progress, fileName, per));
        } else if (status == InterfaceMacro.Pb_Upload_State.Pb_UPLOADMEDIA_FLAG_HADEND_VALUE) {
            //上传结束
            if (userStr.equals(Constant.UPLOAD_DRAW_PIC)) {
                //从画板上传的图片
                FileUtils.delete(pathName);
            } else if (userStr.equals(Constant.UPLOAD_PUBLISH_FILE)) {
                //上传会议发布文件完毕
//                EventBus.getDefault().post(new EventMessage.Builder().type(Constant.BUS_UPLOAD_RELEASE_FILE_FINISH).build());
            }
            ToastUtils.showShort(getString(R.string.upload_completed, fileName));
            LogUtils.i("uploadInform -->" + fileName + " 上传完毕");
        } else if (status == InterfaceMacro.Pb_Upload_State.Pb_UPLOADMEDIA_FLAG_NOSERVER_VALUE) {
            LogUtils.e("uploadInform -->" + " 没找到可用的服务器");
        } else if (status == InterfaceMacro.Pb_Upload_State.Pb_UPLOADMEDIA_FLAG_ISBEING_VALUE) {
            LogUtils.i("uploadInform -->" + pathName + " 已经存在");
        }
    }

    private void downloadInform(EventMessage msg) throws InvalidProtocolBufferException {
        byte[] data2 = (byte[]) msg.getObjects()[0];
        InterfaceDownload.pbui_Type_DownloadCb pbui_type_downloadCb = InterfaceDownload.pbui_Type_DownloadCb.parseFrom(data2);
        int mediaid = pbui_type_downloadCb.getMediaid();
        int progress = pbui_type_downloadCb.getProgress();
        int nstate = pbui_type_downloadCb.getNstate();
        int err = pbui_type_downloadCb.getErr();
        String filepath = pbui_type_downloadCb.getPathname().toStringUtf8();
        String userStr = pbui_type_downloadCb.getUserstr().toStringUtf8();
        String fileName = filepath.substring(filepath.lastIndexOf("/") + 1).toLowerCase();
        LogUtils.i("downloadInform userStr=" + userStr + ",进度=" + progress + ",nstate=" + nstate);
        if (nstate == InterfaceMacro.Pb_Download_State.Pb_STATE_MEDIA_DOWNLOAD_WORKING_VALUE) {
            if (
                    !userStr.equals(Constant.ROOM_BG_PNG_TAG)//会场底图
                            //主页背景
                            && !userStr.equals(Constant.MAIN_BG_PNG_TAG)
                            //主页logo
                            && !userStr.equals(Constant.MAIN_LOGO_PNG_TAG)
                            //子界面背景
                            && !userStr.equals(Constant.MEET_BG_PNG_TAG)
                            //公告背景
                            && !userStr.equals(Constant.NOTICE_BG_PNG_TAG)
                            //公告logo
                            && !userStr.equals(Constant.NOTICE_LOGO_PNG_TAG)
                            //投影背景
                            && !userStr.equals(Constant.PROJECTIVE_BG_PNG_TAG)
                            //投影logo
                            && !userStr.equals(Constant.PROJECTIVE_LOGO_PNG_TAG)
//                            //下载议程文件
//                            && !userStr.equals(Constant.DOWNLOAD_AGENDA_FILE)
//                            //归档文件
//                            && !userStr.equals(Constant.ARCHIVE_DOWNLOAD_FILE)
//                            //归档议程文件
//                            && !userStr.equals(Constant.ARCHIVE_AGENDA_FILE)
            ) {
                ToastUtils.showShort(getString(R.string.file_downloaded_percent, fileName, progress + "%"));
            }
//            if (userStr.equals(Constant.ARCHIVE_DOWNLOAD_FILE) || userStr.equals(Constant.ARCHIVE_AGENDA_FILE)) {
//                EventBus.getDefault().post(new EventMessage.Builder().type(EventType.ARCHIVE_BUS_DOWNLOAD_FILE).objects(mediaid, fileName, progress).build());
//            }
        } else if (nstate == InterfaceMacro.Pb_Download_State.Pb_STATE_MEDIA_DOWNLOAD_EXIT_VALUE) {
            //下载退出---不管成功与否,下载结束最后一次的状态都是这个
//            if (GlobalValue.downloadingFiles.contains(mediaid)) {
//                int index = GlobalValue.downloadingFiles.indexOf(mediaid);
//                GlobalValue.downloadingFiles.remove(index);
//            }
            File file = new File(filepath);
            if (file.exists()) {
                LogUtils.i("BusEvent -->" + "下载完成：" + filepath);
                switch (userStr) {
                    case Constant.MAIN_BG_PNG_TAG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_MAIN_BG).objects(filepath, mediaid).build());
                        break;
                    case Constant.MAIN_LOGO_PNG_TAG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_MAIN_LOGO).objects(filepath, mediaid).build());
                        break;
                    case Constant.MEET_BG_PNG_TAG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_MEET_BG).objects(filepath, mediaid).build());
                        break;
                    case Constant.NOTICE_BG_PNG_TAG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_BULLETIN_BG).objects(filepath, mediaid).build());
                        break;
                    case Constant.NOTICE_LOGO_PNG_TAG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_BULLETIN_LOGO).objects(filepath, mediaid).build());
                        break;
                    case Constant.PROJECTIVE_BG_PNG_TAG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_PROJECTIVE_BG).objects(filepath, mediaid).build());
                        break;
                    case Constant.PROJECTIVE_LOGO_PNG_TAG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_PROJECTIVE_LOGO).objects(filepath, mediaid).build());
                        break;
                    case Constant.ROOM_BG_PNG_TAG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_ROOM_BG).objects(filepath, mediaid).build());
                        break;
//                    //下载的议程文件
//                    case Constant.DOWNLOAD_AGENDA_FILE:
//                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_AGENDA_FILE).objects(filepath, mediaid).build());
//                        break;
                    //下载完成后需要打开的文件
                    case Constant.DOWNLOAD_OPEN_FILE:
//                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_OPEN_FILE).objects(filepath, mediaid).build());
                        FileUtil.openFile(this, filepath);
                        break;
//                    //会议资料文件下载完成
//                    case Constant.DOWNLOAD_MATERIAL_FILE:
//                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_MATERIAL_FILE).objects(filepath, mediaid).build());
//                        break;
//                    //归档议程文件，下载成功
//                    case Constant.ARCHIVE_AGENDA_FILE:
//                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.ARCHIVE_BUS_AGENDA_FILE).objects(filepath, mediaid).build());
//                        break;
                    //桌牌背景图片，下载完成
                    case Constant.DOWNLOAD_TABLE_CARD_BG:
                        EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_TABLE_CARD_BG).objects(filepath, mediaid).build());
                        break;
                    default:
                        break;
                }
            } else {
                LogUtils.i("downloadInform 没有找到文件 filepath=" + filepath);
            }
        } else {
            LogUtils.i("downloadInform 下载状态：" + nstate + ", 下载错误码：" + err + ", 文件名：" + fileName);
        }
    }
}