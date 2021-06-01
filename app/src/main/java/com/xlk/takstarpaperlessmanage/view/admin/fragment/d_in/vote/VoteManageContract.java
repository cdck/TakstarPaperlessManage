package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.vote;

import com.mogujie.tt.protobuf.InterfaceVote;
import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/24.
 * @desc
 */
interface VoteManageContract {
    interface View extends BaseContract.View{
        void updateVoteList();

        void updateMemberDetailedList();

        void showSubmittedPop(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote);
    }
    interface Presenter extends BaseContract.Presenter{
        void setVoteType(int vote_type);

        void queryVote();

        void queryMemberDetailed();

        void queryVoteSubmittedMember(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote);
    }
}
