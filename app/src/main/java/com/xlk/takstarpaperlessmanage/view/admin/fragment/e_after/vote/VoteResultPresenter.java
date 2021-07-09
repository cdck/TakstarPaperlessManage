package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.vote;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.bean.SubmitMember;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/28.
 * @desc
 */
class VoteResultPresenter extends BasePresenter<VoteResultContract.View> implements VoteResultContract.Presenter {
    private int mVoteType;
    public List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> voteInfos = new ArrayList<>();
    private List<InterfaceMember.pbui_Item_MeetMemberDetailInfo> memberDetails = new ArrayList<>();
    public List<SubmitMember> submitMembers = new ArrayList<>();

    public VoteResultPresenter(VoteResultContract.View view) {
        super(view);
    }

    @Override
    public void setVoteType(int voteType) {
        mVoteType = voteType;
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //投票变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO_VALUE: {
                LogUtils.i(TAG, "busEvent 投票变更通知");
                queryVote();
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
            case EventType.RESULT_DIR_PATH:{
                int dirType = (int) msg.getObjects()[0];
                String dirPath = (String) msg.getObjects()[1];
                if(dirType== Constant.CHOOSE_DIR_TYPE_EXPORT_VOTE_SUBMIT){
                    mView.updateExportDirPath(dirPath);
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void queryMemberDetailed() {
        InterfaceMember.pbui_Type_MeetMemberDetailInfo info = jni.queryMemberDetailed();
        memberDetails.clear();
        if (info != null) {
            memberDetails.addAll(info.getItemList());
        }
    }

    @Override
    public void queryVote() {
        InterfaceVote.pbui_Type_MeetVoteDetailInfo info = jni.queryVoteByType(mVoteType);
        voteInfos.clear();
        if (info != null) {
            voteInfos.addAll(info.getItemList());
        }
        mView.updateVoteList();
    }

    /**
     * 查询投票提交人
     *
     * @param vote          投票
     * @param operationType =0展示详情，=1展示图表，=其它则只进行查询投票提交人
     */
    public void querySubmittedVoters(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote, int operationType) {
        InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo info = jni.querySubmittedVoters(vote.getVoteid());
        if (info == null) {
            return;
        }
        submitMembers.clear();
        List<InterfaceVote.pbui_SubItem_VoteItemInfo> optionInfo = vote.getItemList();
        List<InterfaceVote.pbui_Item_MeetVoteSignInDetailInfo> submittedMembers = info.getItemList();
        for (int i = 0; i < submittedMembers.size(); i++) {
            InterfaceVote.pbui_Item_MeetVoteSignInDetailInfo item = submittedMembers.get(i);
            InterfaceMember.pbui_Item_MeetMemberDetailInfo memberInfo = null;
            String chooseText = "";
            for (int j = 0; j < memberDetails.size(); j++) {
                if (memberDetails.get(j).getMemberid() == item.getId()) {
                    memberInfo = memberDetails.get(j);
                    break;
                }
            }
            if (memberInfo == null) {
                LogUtils.d(TAG, "querySubmittedVoters -->" + "没有找打提交人名字");
                break;
            }
            int selcnt = item.getSelcnt();
            //int变量的二进制表示的字符串
            String string = Integer.toBinaryString(selcnt);
            //查找字符串中为1的索引位置
            int length = string.length();
            int selectedItem = 0;
            for (int j = 0; j < length; j++) {
                char c = string.charAt(j);
                //将 char 装换成int型整数
                int a = c - '0';
                if (a == 1) {
                    //索引从0开始
                    selectedItem = length - j - 1;
                    for (int k = 0; k < optionInfo.size(); k++) {
                        if (k == selectedItem) {
                            InterfaceVote.pbui_SubItem_VoteItemInfo voteOptionsInfo = optionInfo.get(k);
                            String text = voteOptionsInfo.getText().toStringUtf8();
                            if (chooseText.length() == 0) {
                                chooseText = text;
                            } else {
                                chooseText += " | " + text;
                            }
                        }
                    }
                }
            }
            submitMembers.add(new SubmitMember(memberInfo, item, chooseText));
        }
        LogUtils.e("当前投票的提交人数=" + submitMembers.size());
        if (operationType == 0) {
            mView.showSubmittedPop(vote);
        } else if (operationType == 1) {
            mView.showChartPop(vote);
        }
    }

    @Override
    public String[] queryYd(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote) {
        InterfaceBase.pbui_CommonInt32uProperty yingDaoInfo = jni.queryVoteSubmitterProperty(vote.getVoteid(), 0, InterfaceMacro.Pb_MeetVotePropertyID.Pb_MEETVOTE_PROPERTY_ATTENDNUM.getNumber());
        InterfaceBase.pbui_CommonInt32uProperty yiTouInfo = jni.queryVoteSubmitterProperty(vote.getVoteid(), 0, InterfaceMacro.Pb_MeetVotePropertyID.Pb_MEETVOTE_PROPERTY_VOTEDNUM.getNumber());
        InterfaceBase.pbui_CommonInt32uProperty shiDaoInfo = jni.queryVoteSubmitterProperty(vote.getVoteid(), 0, InterfaceMacro.Pb_MeetVotePropertyID.Pb_MEETVOTE_PROPERTY_CHECKINNUM.getNumber());
        int yingDao = yingDaoInfo == null ? 0 : yingDaoInfo.getPropertyval();
        int yiTou = yiTouInfo == null ? 0 : yiTouInfo.getPropertyval();
        int shiDao = shiDaoInfo == null ? 0 : shiDaoInfo.getPropertyval();
        String yingDaoStr = "应到：" + yingDao + "人 ";
        String shiDaoStr = "实到：" + shiDao + "人 ";
        String yiTouStr = "已投：" + yiTou + "人 ";
        String weiTouStr = "未投：" + (yingDao - yiTou) + "人";
        LogUtils.d(TAG, "queryYd :  应到人数: " + yingDaoStr + "，实到：" + shiDaoStr + ", 已投人数: " + yiTouStr + "， 未投：" + weiTouStr);
        return new String[]{yingDaoStr, shiDaoStr, yiTouStr, weiTouStr};
    }
}
