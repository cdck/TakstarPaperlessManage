package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.camera;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
interface CameraContract {
    interface View extends BaseContract.View{
        void updateAvailableCameraList();

        void updateOptionalCameraList();
    }
    interface Presenter extends BaseContract.Presenter{
        void queryMeetVideo();
    }
}
