package com.xlk.takstarpaperlessmanage.view.main;

import com.mogujie.tt.protobuf.InterfaceAdmin;
import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/4/21.
 * @desc
 */
public interface MainContract {
    interface View extends BaseContract.View {
        void loginBack(InterfaceAdmin.pbui_Type_AdminLogonStatus info);
    }

    interface Presenter extends BaseContract.Presenter {
    }
}
