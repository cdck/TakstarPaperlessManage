package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.sign;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/27.
 * @desc
 */
interface SignContract {
    interface View extends BaseContract.View{
        void updateSignList(int signInCount);
    }
    interface Presenter extends BaseContract.Presenter{
        void queryMember();

        void deleteSignIn(List<Integer> memberIds);
    }
}
