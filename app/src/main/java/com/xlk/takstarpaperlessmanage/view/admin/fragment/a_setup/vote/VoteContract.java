package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.vote;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
interface VoteContract {
    interface View extends BaseContract.View{
        void updateVoteList();
    }
    interface Presenter extends BaseContract.Presenter{
        void setVoteType(int vote_type);

        void queryVote();
    }
}
