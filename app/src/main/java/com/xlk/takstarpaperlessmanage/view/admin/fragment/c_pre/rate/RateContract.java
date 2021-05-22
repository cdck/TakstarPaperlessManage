package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.rate;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
interface RateContract {
    interface View extends BaseContract.View{
        void updateFileScoreList();

        void updateMemberDetailedList();

        void updateScoreSubmitMemberList();
    }
    interface Presenter extends BaseContract.Presenter{
        void queryFileScore();

        void queryMemberDetailed();

        void queryScoreSubmittedScore(int voteid);
    }
}
