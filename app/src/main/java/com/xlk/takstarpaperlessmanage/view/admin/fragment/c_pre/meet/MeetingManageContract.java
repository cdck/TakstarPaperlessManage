package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.meet;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/18.
 * @desc
 */
interface MeetingManageContract {
    interface View extends BaseContract.View{
        void updateMeetingList();

        void updateRoomList();
    }
    interface Presenter extends BaseContract.Presenter{
        void queryAllMeeting();
        void queryMeetingRoom();

        int getRoomPosition(int roomId);
    }
}
