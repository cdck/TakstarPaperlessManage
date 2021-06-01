package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.rate;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/24.
 * @desc
 */
interface RateContract {
    interface View extends BaseContract.View{
        void updateMemberDetailedList();

        void updateFileScoreList();

        void updateScoreSubmitMemberList();

        void addFile2List(String filePath, int mediaId);
    }
    interface Presenter extends BaseContract.Presenter{

    }
}
