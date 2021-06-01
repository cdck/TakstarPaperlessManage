package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.screen;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/26.
 * @desc
 */
interface ScreenManageContract {
    interface View extends BaseContract.View{
        void updateList();
    }
    interface Presenter extends BaseContract.Presenter{
        void queryMember();
    }
}
