package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.video;

import com.mogujie.tt.protobuf.InterfaceFile;
import com.xlk.takstarpaperlessmanage.base.BaseContract;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/29.
 * @desc
 */
interface VideoManageContract {
    interface View extends BaseContract.View{
        void updateVideoFileList();
    }
    interface Presenter extends BaseContract.Presenter{
        void queryVideoFile();
    }
}
