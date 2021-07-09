package com.xlk.takstarpaperlessmanage.view.admin;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/4/21.
 * @desc
 */
public interface AdminContract {
    interface View extends BaseContract.View {
        void updateTime(String[] adminTime);

        void updateOnlineStatus();

        void updateMeetingName(String meetingName);
    }

    interface Presenter extends BaseContract.Presenter {

        void updateCurrentMeeting();
    }
}
