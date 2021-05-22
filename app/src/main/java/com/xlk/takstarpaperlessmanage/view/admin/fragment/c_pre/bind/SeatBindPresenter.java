package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.bind;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.bean.MemberRoleBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
class SeatBindPresenter extends BasePresenter<SeatBindContract.View> implements SeatBindContract.Presenter {

    public List<InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo> seatData = new ArrayList<>();
    public List<MemberRoleBean> memberRoleBeans = new ArrayList<>();
    private Timer timer;
    private TimerTask task;

    public SeatBindPresenter(SeatBindContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会场底图下载完成
            case EventType.BUS_ROOM_BG: {
                String currentRoomBgFilePath = (String) msg.getObjects()[0];
                int mediaId = (int) msg.getObjects()[1];
                mView.updateRoomBg(currentRoomBgFilePath, mediaId);
                break;
            }
            //参会人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE: {
                LogUtils.i(TAG, "BusEvent -->" + "参会人员变更通知");
                queryMember();
                break;
            }
            //会议排位变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT_VALUE: {
                LogUtils.i(TAG, "busEvent " + "会议排位变更通知");
                executeLater();
//                queryPlaceRanking();
//                queryMember();
                break;
            }
            //界面配置变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETFACECONFIG_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(bytes);
                int id = pbui_meetNotifyMsg.getId();
                int opermethod = pbui_meetNotifyMsg.getOpermethod();
                LogUtils.i(TAG, "BusEvent 界面配置变更通知 id=" + id + ",opermethod=" + opermethod);
//                if (id == InterfaceMacro.Pb_MeetFaceID.Pb_MEET_FACE_SeatIcoShow_GEO_VALUE) {
//                    queryRoomIcon();
//                }
                break;
            }
            //会场信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(bytes);
                int id = pbui_meetNotifyMsg.getId();
                int opermethod = pbui_meetNotifyMsg.getOpermethod();
                LogUtils.i(TAG, "BusEvent 会场信息变更通知 id=" + id + ",opermethod=" + opermethod);
                if (queryCurrentRoomId() == id && id != 0) {
                    queryRoomBgPic();
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
                    queryMember();
                    task.cancel();
                    timer.cancel();
                    task = null;
                    timer = null;
                }
            };
            LogUtils.i("800 毫秒之后查询");
            timer.schedule(task, 800);
        }
    }

    @Override
    public void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo info = jni.queryMember();
        memberRoleBeans.clear();
        if (info != null) {
            List<InterfaceMember.pbui_Item_MemberDetailInfo> itemList = info.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                memberRoleBeans.add(new MemberRoleBean(itemList.get(i)));
            }
        }
        queryPlaceRanking();
    }

    @Override
    public void queryRoomBgPic() {
        try {
            int mediaId = jni.queryMeetRoomProperty(queryCurrentRoomId());
            String fileName = jni.queryFileNameByMediaId(mediaId);
            String filePath = Constant.config_dir + fileName;
            boolean fileExists = FileUtils.isFileExists(filePath);
            if (fileExists) {
                mView.updateRoomBg(filePath, mediaId);
                return;
            } else if (mediaId != 0) {
                jni.downloadFile(filePath, mediaId, 1, 0, Constant.ROOM_BG_PNG_TAG);
                return;
            }
            queryPlaceRanking();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    public void queryPlaceRanking() {
        InterfaceRoom.pbui_Type_MeetRoomDevSeatDetailInfo info = jni.placeDeviceRankingInfo(queryCurrentRoomId());
        seatData.clear();
        if (info != null) {
            seatData.addAll(info.getItemList());
            for (int i = 0; i < memberRoleBeans.size(); i++) {
                MemberRoleBean bean = memberRoleBeans.get(i);
                for (int j = 0; j < seatData.size(); j++) {
                    InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo item = seatData.get(j);
                    if (item.getMemberid() == bean.getMember().getPersonid()) {
                        bean.setSeat(item);
                        break;
                    }
                }
            }
        }
        mView.updateMemberList();
        mView.updateSeatDataList();
    }
}
