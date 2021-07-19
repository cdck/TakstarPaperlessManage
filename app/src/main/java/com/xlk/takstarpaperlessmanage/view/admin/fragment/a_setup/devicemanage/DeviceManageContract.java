package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.devicemanage;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/6.
 * @desc
 */
interface DeviceManageContract {
    interface View extends BaseContract.View {

        void updateDeviceList();

        /**
         * 更新参数配置中在线终端列表
         */
        void updateClientList();

        void updateExportDirPath(String dirPath);
    }

    interface Presenter extends BaseContract.Presenter {
        void queryDevice();
    }
}
