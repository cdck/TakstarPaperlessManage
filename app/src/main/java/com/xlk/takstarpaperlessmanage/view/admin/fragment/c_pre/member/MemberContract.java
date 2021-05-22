package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.member;

import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.takstarpaperlessmanage.base.BaseContract;
import com.xlk.takstarpaperlessmanage.model.bean.MemberRoleBean;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/19.
 * @desc
 */
interface MemberContract {
    interface View extends BaseContract.View {
        void updateMemberList();

        void updateCommonlyMemberList();

        void updateMemberPermissionList();

        void updateMemberRoleList();
    }

    interface Presenter extends BaseContract.Presenter {
        void queryMember();

        List<InterfaceMember.pbui_Item_MemberDetailInfo> getSortMembers();

        void queryCommonlyMember();

        void queryMemberPermission();
    }
}
