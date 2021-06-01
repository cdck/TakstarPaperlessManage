package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.vote;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.EventMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
public class VotePresenter extends BasePresenter<VoteContract.View> implements VoteContract.Presenter {
    private int vote_type;
    public List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> voteInfos =new ArrayList<>();

    public VotePresenter(VoteContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //投票变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO_VALUE:
                LogUtils.i(TAG, "busEvent 投票变更通知");
                queryVote();
                break;
            default:
                break;
        }
    }

    @Override
    public void setVoteType(int vote_type) {
        this.vote_type = vote_type;
    }

    @Override
    public void queryVote() {
        InterfaceVote.pbui_Type_MeetVoteDetailInfo info = jni.queryVoteByType(vote_type);
        voteInfos.clear();
        if (info != null) {
            voteInfos.addAll(info.getItemList());
        }
        mView.updateVoteList();
    }
}
