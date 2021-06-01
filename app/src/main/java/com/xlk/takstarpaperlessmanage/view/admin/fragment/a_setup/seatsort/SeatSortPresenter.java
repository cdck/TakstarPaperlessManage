package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.seatsort;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceFaceconfig;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Created by xlk on 2021/5/12.
 * @desc
 */
public class SeatSortPresenter extends BasePresenter<SeatSortContract.View> implements SeatSortContract.Presenter {
    public List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> meetRooms = new ArrayList<>();
    /**
     * 所有的设备
     */
    private List<InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo> seatData = new ArrayList<>();
    /**
     * 所有背景图片文件
     */
    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> pictureData = new ArrayList<>();
    private int currentRoomId = -1;
    private Timer timer;
    private TimerTask task;

    public SeatSortPresenter(SeatSortContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会场信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(bytes);
                int id = pbui_meetNotifyMsg.getId();
                int opermethod = pbui_meetNotifyMsg.getOpermethod();
                LogUtils.i( "BusEvent 会场信息变更通知 id=" + id + ",opermethod=" + opermethod);
                if (currentRoomId != 0 && currentRoomId == id) {
                    queryRoomBg(id);
                }
                queryRoom();
                break;
            }
            case EventType.BUS_ROOM_BG: {
                String currentRoomBgFilePath = (String) msg.getObjects()[0];
                int mediaId = (int) msg.getObjects()[1];
                mView.updateRoomBg(currentRoomBgFilePath, mediaId);
                break;
            }
            //会议目录文件变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    byte[] bytes = (byte[]) msg.getObjects()[0];
                    InterfaceBase.pbui_MeetNotifyMsgForDouble info = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(bytes);
                    int opermethod = info.getOpermethod();
                    int id = info.getId();
                    int subid = info.getSubid();
                    LogUtils.i(TAG, "BusEvent 会议目录文件变更通知 id=" + id + ",subId=" + subid + ",opermethod=" + opermethod);
                    queryBgPicture();
                }
                break;
            }
            //会场设备信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE_VALUE: {
                byte[] o = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsgForDouble pbui_meetNotifyMsgForDouble = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(o);
                int id = pbui_meetNotifyMsgForDouble.getId();
                int subid = pbui_meetNotifyMsgForDouble.getSubid();
                int opermethod = pbui_meetNotifyMsgForDouble.getOpermethod();
                LogUtils.i(TAG, "BusEvent 会场设备信息变更通知 -->id=" + id + ",subId=" + subid + ",opermethod=" + opermethod);
                if (currentRoomId == id && currentRoomId != 0) {
//                    placeDeviceRankingInfo(id);
                    executeLater();
                }
                break;
            }
            //界面配置变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETFACECONFIG_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(bytes);
                int id = pbui_meetNotifyMsg.getId();
                int opermethod = pbui_meetNotifyMsg.getOpermethod();
                LogUtils.i(TAG, "BusEvent 界面配置变更通知 id=" + id + ",opermethod=" + opermethod);
                if (id == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_SeatIcoShow_GEO_VALUE) {
                    queryRoomIcon();
                }
                break;
            }
        }
    }

    private void executeLater() {
        //解决短时间内收到很多通知，查询很多次的问题
        if (timer == null) {
            timer = new Timer();
            LogUtils.i("创建timer");
            task = new TimerTask() {
                @Override
                public void run() {
                    placeDeviceRankingInfo(currentRoomId);
                    task.cancel();
                    timer.cancel();
                    task = null;
                    timer = null;
                }
            };
            LogUtils.i("800毫秒之后查询");
            timer.schedule(task, 800);
        }else {
            LogUtils.i("正在执行。。。");
        }
    }

    @Override
    public void queryRoom() {
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo info = jni.queryMeetingRooms();
        meetRooms.clear();
        if (info != null) {
            for (int i = 0; i < info.getItemList().size(); i++) {
                InterfaceRoom.pbui_Item_MeetRoomDetailInfo room = info.getItemList().get(i);
                LogUtils.i(TAG, "queryRoom 会场id=" + room.getRoomid() + ",会场名称=" + room.getName().toStringUtf8());
                if (!room.getName().toStringUtf8().isEmpty()) {
                    meetRooms.add(room);
                }
            }
        }
        mView.updateMeetRoomList();
    }

    @Override
    public void queryRoomIcon() {
        InterfaceFaceconfig.pbui_Type_FaceConfigInfo pbui_type_faceConfigInfo = jni.queryInterFaceConfiguration();
        boolean showFlag = true;
        if (pbui_type_faceConfigInfo != null) {
            List<InterfaceFaceconfig.pbui_Item_FaceTextItemInfo> textList = pbui_type_faceConfigInfo.getTextList();
            for (int i = 0; i < textList.size(); i++) {
                InterfaceFaceconfig.pbui_Item_FaceTextItemInfo item = textList.get(i);
                int faceid = item.getFaceid();
                int flag = item.getFlag();
                if (InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_SeatIcoShow_GEO_VALUE == faceid) {
                    showFlag = (InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE
                            == (flag & InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE));
                    break;
                }
            }
        }
        mView.updateShowIcon(!showFlag);
    }

    @Override
    public void queryRoomBg(int roomId) {
        try {
            int mediaId = jni.queryMeetRoomProperty(roomId);
            if (mediaId != 0) {
                FileUtils.createOrExistsDir(Constant.config_dir);
                jni.downloadFile(Constant.config_dir + Constant.ROOM_BG_PNG_TAG + ".png", mediaId, 1, 0, Constant.ROOM_BG_PNG_TAG);
                return;
            }
            mView.cleanRoomBg();
            placeDeviceRankingInfo(roomId);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    public void placeDeviceRankingInfo(int roomId) {
        LogUtils.i(TAG, "placeDeviceRankingInfo roomId=" + roomId);
        currentRoomId = roomId;
        InterfaceRoom.pbui_Type_MeetRoomDevSeatDetailInfo info = jni.placeDeviceRankingInfo(roomId);
        seatData.clear();
        if (info != null) {
            seatData.addAll(info.getItemList());
        }
        mView.updateSeatData(seatData);
    }

    /**
     * 设置显示/隐藏座位图标
     *
     * @param hideIcon =true设置隐藏，=false设置显示
     */
    @Override
    public void setHideIcon(boolean hideIcon) {
        LogUtils.i("setHideIcon hideIcon=" + hideIcon);
        InterfaceFaceconfig.pbui_Item_FaceTextItemInfo.Builder builder = InterfaceFaceconfig.pbui_Item_FaceTextItemInfo.newBuilder();
        builder.setFaceid(InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_SeatIcoShow_GEO_VALUE);
        if (!hideIcon) {
            //设置显示
            builder.setFlag(InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_SHOW_VALUE
                    | InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_TEXT_VALUE);
        } else {
            //设置隐藏
            builder.setFlag(InterfaceMacro.Pb_MeetFaceFlag.Pb_MEET_FACEFLAG_TEXT_VALUE);
        }
        InterfaceFaceconfig.pbui_Item_FaceTextItemInfo build = builder.build();
        byte[] bytes = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.newBuilder()
                .addText(build)
                .build().toByteArray();
        jni.modifyInterfaceConfig(bytes);
    }

    @Override
    public void queryBgPicture() {
        InterfaceFile.pbui_TypePageResQueryrFileInfo pbui_typePageResQueryrFileInfo = jni.queryFile(0, InterfaceMacro.Pb_MeetFileQueryFlag.Pb_MEET_FILETYPE_QUERYFLAG_ATTRIB_VALUE
                , 0, 0, 0, InterfaceMacro.Pb_MeetFileAttrib.Pb_MEETFILE_ATTRIB_BACKGROUND_VALUE, 1, 0);
        pictureData.clear();
        if (pbui_typePageResQueryrFileInfo != null) {
            pictureData.addAll(pbui_typePageResQueryrFileInfo.getItemList());
            for (int i = 0; i < pictureData.size(); i++) {
                String name = pictureData.get(i).getName().toStringUtf8();
                LogUtils.i(TAG, "queryBgPicture 背景图片文件名=" + name);
            }
        }
        LogUtils.i(TAG, "queryBgPicture itemList.size=" + pictureData.size());
        mView.updatePictureList();
    }
}
