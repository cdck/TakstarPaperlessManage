package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.function;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/22.
 * @desc
 */
interface FunctionContract {
    interface View extends BaseContract.View{
        void updateFunctionList();
    }
    interface Presenter extends BaseContract.Presenter{
        void queryMeetingFunction();
    }
}
