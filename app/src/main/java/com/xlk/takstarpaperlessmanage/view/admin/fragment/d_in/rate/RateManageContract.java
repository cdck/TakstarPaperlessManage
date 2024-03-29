package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.rate;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
interface RateManageContract {
    interface View extends BaseContract.View{
        void updateFileScoreList();

        void updateMemberDetailedList();

        void updateScoreSubmitMemberList();

        void updateExportDirPath(String dirPath);
    }
    interface Presenter extends BaseContract.Presenter{
        void queryFileScore();

        void queryMemberDetailed();

        void queryScoreSubmittedScore(int voteid);
    }
}
