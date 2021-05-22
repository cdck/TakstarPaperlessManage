package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.material;

import com.xlk.takstarpaperlessmanage.base.BaseContract;
import com.xlk.takstarpaperlessmanage.model.bean.MemberDirPermissionBean;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/20.
 * @desc
 */
interface MaterialContract {
    interface View extends BaseContract.View {
        void updateDirList();

        void updateFileList();
    }

    interface Presenter extends BaseContract.Presenter {
        void queryMeetDir();

        void queryMember();

        void queryMeetDirFile(int dirId);

        void setCurrentDirId(int dirId);

        List<MemberDirPermissionBean> getDirPermission();
    }
}
