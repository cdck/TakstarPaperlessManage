package com.xlk.takstarpaperlessmanage.view.admin.fragment.b_reservation;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/15.
 * @desc
 */
interface ReserveContract {
     interface View extends BaseContract.View{
         void updateMeetings();

         void updateRoomList();
     }
     interface Presenter extends BaseContract.Presenter{
         void queryMeeting();

         void queryMeetingRoom();

         int getRoomPosition(int roomId);
     }
}
