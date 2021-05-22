package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.seatsort;

import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.base.BaseContract;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/12.
 * @desc
 */
public interface SeatSortContract {
    interface View extends BaseContract.View{
        void updateMeetRoomList();

        void updateShowIcon(boolean show);

        void cleanRoomBg();

        void updateSeatData(List<InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo> seatData);

        void updateRoomBg(String currentRoomBgFilePath, int mediaId);

        void updatePictureList();
    }
    interface Presenter extends BaseContract.Presenter{
        void queryRoom();

        void queryRoomIcon();

        void queryRoomBg(int roomid);

        void setHideIcon(boolean hide);

        void queryBgPicture();
    }
}
