package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.vote;

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
import com.xlk.takstarpaperlessmanage.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/24.
 * @desc
 */
class VoteManagePresenter extends BasePresenter<VoteManageContract.View> implements VoteManageContract.Presenter {
    private int voteType;
    public List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> voteInfos = new ArrayList<>();
    public List<InterfaceMember.pbui_Item_MeetMemberDetailInfo> members = new ArrayList<>();
    public List<SubmitMember> submitMembers = new ArrayList<>();

    public VoteManagePresenter(VoteManageContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            case EventType.RESULT_DIR_PATH:{
                int dirType = (int) msg.getObjects()[0];
                String dirPath = (String) msg.getObjects()[1];
                if(dirType== Constant.CHOOSE_DIR_TYPE_EXPORT_VOTE_MANAGE){
                    mView.updateExportDirPath(dirPath);
                }
                break;
            }
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
            default:
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
    public void setVoteType(int vote_type) {
        voteType = vote_type;
    }

    @Override
    public void queryVote() {
        InterfaceVote.pbui_Type_MeetVoteDetailInfo info = jni.queryVoteByType(voteType);
        voteInfos.clear();
        if (info != null) {
            voteInfos.addAll(info.getItemList());
        }
        mView.updateVoteList();
    }

    @Override
    public void queryVoteSubmittedMember(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote) {
        InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo info = jni.querySubmittedVoters(vote.getVoteid());
        if (info != null) {
            submitMembers.clear();
            List<InterfaceVote.pbui_SubItem_VoteItemInfo> optionInfo = vote.getItemList();
            List<InterfaceVote.pbui_Item_MeetVoteSignInDetailInfo> submittedMembers = info.getItemList();
            for (int i = 0; i < submittedMembers.size(); i++) {
                InterfaceVote.pbui_Item_MeetVoteSignInDetailInfo item = submittedMembers.get(i);
                InterfaceMember.pbui_Item_MeetMemberDetailInfo memberInfo = null;
                String chooseText = "";
                for (int j = 0; j < members.size(); j++) {
                    if (members.get(j).getMemberid() == item.getId()) {
                        memberInfo = members.get(j);
                        break;
                    }
                }
                if (memberInfo == null) {
                    LogUtil.d(TAG, "querySubmittedVoters -->" + "没有找打提交人名字");
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
            mView.showSubmittedPop(vote);
        }
    }

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
        LogUtil.d(TAG, "queryYd :  应到人数: " + yingDaoStr + "，实到：" + shiDaoStr + ", 已投人数: " + yiTouStr + "， 未投：" + weiTouStr);
        return new String[]{yingDaoStr, shiDaoStr, yiTouStr, weiTouStr};
    }
}
