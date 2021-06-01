package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.device;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/24.
 * @desc
 */
interface DeviceControlContract {
    interface View extends BaseContract.View{
        void updateRv();

        void updateRoleRv();
    }
    interface Presenter extends BaseContract.Presenter{
        void queryRankInfo();
    }

}
