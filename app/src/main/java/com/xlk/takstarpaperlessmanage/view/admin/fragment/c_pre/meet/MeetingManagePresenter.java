package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.meet;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.EventMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/18.
 * @desc
 */
public class MeetingManagePresenter extends BasePresenter<MeetingManageContract.View> implements MeetingManageContract.Presenter {
    public List<InterfaceMeet.pbui_Item_MeetMeetInfo> allMeetings = new ArrayList<>();
    public List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> allRooms = new ArrayList<>();

    public MeetingManagePresenter(MeetingManageContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会议信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE: {
                LogUtils.i(TAG, "busEvent 会议信息变更通知");
                queryAllMeeting();
                break;
            }
            //会场信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE: {
                LogUtils.i(TAG, "BusEvent 会场信息变更通知");
                queryMeetingRoom();
                break;
            }
        }
    }

    @Override
    public void queryAllMeeting() {
        InterfaceMeet.pbui_Type_MeetMeetInfo info = jni.queryMeeting();
        allMeetings.clear();
        if (info != null) {
            allMeetings.addAll(info.getItemList());
        }
        mView.updateMeetingList();
    }

    @Override
    public void queryMeetingRoom() {
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo info = jni.queryMeetingRooms();
        allRooms.clear();
        if (info != null) {
            List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> itemList = info.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceRoom.pbui_Item_MeetRoomDetailInfo item = itemList.get(i);
                if (!item.getName().toStringUtf8().isEmpty()) {
                    allRooms.add(item);
                }
            }
        }
        mView.updateRoomList();
    }

    @Override
    public int getRoomPosition(int roomId) {
        for (int i = 0; i < allRooms.size(); i++) {
            if (allRooms.get(i).getRoomid() == roomId) {
                return i;
            }
        }
        return -1;
    }
}
