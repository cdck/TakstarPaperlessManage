package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.camera;

import com.xlk.takstarpaperlessmanage.base.BaseContract;
import com.xlk.takstarpaperlessmanage.model.bean.VideoDevice;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/25.
 * @desc
 */
interface CameraControlContract {
    interface View extends BaseContract.View{
        void updateRv(List<VideoDevice> videoDevs);

        void notifyOnLineAdapter();

        void updateDecode(Object[] objs);

        void updateYuv(Object[] objs);

        void stopResWork(int resid);
    }
    interface Presenter extends BaseContract.Presenter{
        void watch(VideoDevice videoDevice, int selectResId);
    }
}
