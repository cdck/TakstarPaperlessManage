package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.vote;

import com.mogujie.tt.protobuf.InterfaceVote;
import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/28.
 * @desc
 */
interface VoteResultContract {
    interface View extends BaseContract.View{
        void updateVoteList();

        void showSubmittedPop(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote);

        void showChartPop(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote);

        void updateExportDirPath(String dirPath);
    }
    interface Presenter extends BaseContract.Presenter{
        void setVoteType(int voteType);

        void queryVote();

        void queryMemberDetailed();

        String[] queryYd(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote);
    }
}
