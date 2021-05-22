package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.rate;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceFilescorevote;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.bean.ScoreMember;
import com.xlk.takstarpaperlessmanage.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
class RatePresenter extends BasePresenter<RateContract.View> implements RateContract.Presenter {

    public List<InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore> fileScores = new ArrayList<>();
    public List<InterfaceMember.pbui_Item_MeetMemberDetailInfo> members = new ArrayList<>();
    public List<ScoreMember> scoreMembers = new ArrayList<>();
    private int currentVoteId;

    public RatePresenter(RateContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //投票变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTE_VALUE: {
                LogUtil.d(TAG, "BusEvent -->" + "投票变更通知");
                queryFileScore();
                break;
            }
            //会议评分
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTESIGN_VALUE: {
                byte[] data = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                int id = pbui_meetNotifyMsg.getId();
                int opermethod = pbui_meetNotifyMsg.getOpermethod();
                LogUtil.d(TAG, "BusEvent -->" + "会议评分变更通知 id= " + id + ", opermethod= " + opermethod);
                if (opermethod == 1 && id == currentVoteId) {
                    queryScoreSubmittedScore(id);
                }
                break;
            }
            //设备变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE:
                //参会人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE:
                //参会人员权限变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERPERMISSION_VALUE:
                //界面状态变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS_VALUE:
                queryMemberDetailed();
                break;
        }
    }

    @Override
    public void queryMemberDetailed() {
        InterfaceMember.pbui_Type_MeetMemberDetailInfo info = jni.queryMemberDetailed();
        members.clear();
        if (info != null) {
            List<InterfaceMember.pbui_Item_MeetMemberDetailInfo> itemList = info.getItemList();
            members.addAll(itemList);
        }
        mView.updateMemberDetailedList();
    }

    @Override
    public void queryFileScore() {
        InterfaceFilescorevote.pbui_Type_UserDefineFileScore info = jni.queryFileScore();
        fileScores.clear();
        if (info != null) {
            fileScores.addAll(info.getItemList());
        }
        mView.updateFileScoreList();
    }

    @Override
    public void queryScoreSubmittedScore(int voteid) {
        currentVoteId = voteid;
        InterfaceFilescorevote.pbui_Type_UserDefineFileScoreMemberStatistic info = jni.queryScoreSubmittedScore(voteid);
        scoreMembers.clear();
        if (info != null) {
            List<InterfaceFilescorevote.pbui_Type_Item_FileScoreMemberStatistic> itemList = info.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceFilescorevote.pbui_Type_Item_FileScoreMemberStatistic score = itemList.get(i);
                int memberid = score.getMemberid();
                for (int j = 0; j < members.size(); j++) {
                    if (members.get(j).getMemberid() == memberid) {
                        scoreMembers.add(new ScoreMember(score, members.get(j)));
                    }
                }
            }
        }
        mView.updateScoreSubmitMemberList();
    }
}
