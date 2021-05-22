package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.roommanage;

import com.mogujie.tt.protobuf.InterfaceDevice;
import com.xlk.takstarpaperlessmanage.base.BaseContract;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/12.
 * @desc
 */
public interface RoomManageContract  {
    interface View extends BaseContract.View{
        void updateMeetRoomList();

        void updateRoomDeviceRv();

        void updateOtherDeviceRv();
    }
    interface Presenter extends BaseContract.Presenter{
        void queryRoom();

        void queryDevice(int roomId);

        void addDevice2Room(List<Integer> deviceIds);

        void removeDeviceFromRoom(List<Integer> deviceIds);

        void defineModify();
    }
}
