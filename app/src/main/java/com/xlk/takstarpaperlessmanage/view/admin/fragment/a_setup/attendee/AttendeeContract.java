package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.attendee;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/13.
 * @desc
 */
public interface AttendeeContract {
    interface View extends BaseContract.View{
        void updateAttendeeList();
    }
    interface Presenter extends BaseContract.Presenter{
        void queryAttendee();

        void exportAttendee();
    }
}
