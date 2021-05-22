package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.member;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfacePerson;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.bean.MemberPermissionBean;
import com.xlk.takstarpaperlessmanage.model.bean.MemberRoleBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/19.
 * @desc
 */
public class MemberPresenter extends BasePresenter<MemberContract.View> implements MemberContract.Presenter {

    public List<InterfaceMember.pbui_Item_MemberDetailInfo> members = new ArrayList<>();
    public List<InterfaceMember.pbui_Item_MemberDetailInfo> sortMembers = new ArrayList<>();
    public List<MemberRoleBean> memberRoleBeans = new ArrayList<>();
    public List<MemberPermissionBean> memberPermissionBeans = new ArrayList<>();
    public List<InterfacePerson.pbui_Item_PersonDetailInfo> commonlyMembers = new ArrayList<>();

    public MemberPresenter(MemberContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //参会人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE: {
                LogUtils.i(TAG, "BusEvent --> 参会人员变更通知");
                queryMember();
                break;
            }
            //常用人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLE_VALUE: {
                LogUtils.i(TAG, "BusEvent -->" + "常用人员变更通知");
                queryCommonlyMember();
                break;
            }
            //参会人员权限变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERPERMISSION_VALUE: {
                queryMemberPermission();
                break;
            }
            //会议排位变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT_VALUE: {
                LogUtils.i(TAG, "busEvent " + "会议排位变更通知");
                queryPlaceRanking();
                break;
            }
        }
    }

    @Override
    public void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo info = jni.queryMember();
        members.clear();
        memberRoleBeans.clear();
        if (info != null) {
            members.addAll(info.getItemList());
            for (int i = 0; i < members.size(); i++) {
                memberRoleBeans.add(new MemberRoleBean(members.get(i)));
            }
        }
        mView.updateMemberList();
        queryPlaceRanking();
    }

    /**
     * 会场设备排位详细信息
     */
    public void queryPlaceRanking() {
        InterfaceRoom.pbui_Type_MeetRoomDevSeatDetailInfo info = jni.placeDeviceRankingInfo(queryCurrentRoomId());
        if (info != null) {
            for (int i = 0; i < memberRoleBeans.size(); i++) {
                MemberRoleBean bean = memberRoleBeans.get(i);
                for (int j = 0; j < info.getItemList().size(); j++) {
                    InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo item = info.getItemList().get(j);
                    if (item.getMemberid() == bean.getMember().getPersonid()) {
                        bean.setSeat(item);
                        break;
                    }
                }
            }
        }
        mView.updateMemberRoleList();
    }

    @Override
    public void queryCommonlyMember() {
        InterfacePerson.pbui_Type_PersonDetailInfo info = jni.queryCommonlyMember();
        commonlyMembers.clear();
        if (info != null) {
            commonlyMembers.addAll(info.getItemList());
        }
        mView.updateCommonlyMemberList();
    }

    @Override
    public void queryMemberPermission() {
        InterfaceMember.pbui_Type_MemberPermission info = jni.queryMemberPermissions();
        memberPermissionBeans.clear();
        if (info != null) {
            List<InterfaceMember.pbui_Item_MemberPermission> itemList = info.getItemList();
            for (int i = 0; i < members.size(); i++) {
                InterfaceMember.pbui_Item_MemberDetailInfo member = members.get(i);
                for (int j = 0; j < itemList.size(); j++) {
                    if (itemList.get(j).getMemberid() == member.getPersonid()) {
                        memberPermissionBeans.add(new MemberPermissionBean(itemList.get(j).getMemberid(),
                                itemList.get(j).getPermission(), member.getName().toStringUtf8()));
                    }
                }
            }
        }
        mView.updateMemberPermissionList();
    }

    @Override
    public List<InterfaceMember.pbui_Item_MemberDetailInfo> getSortMembers() {
        sortMembers.clear();
        sortMembers.addAll(members);
        return sortMembers;
    }
}
