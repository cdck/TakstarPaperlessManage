package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.annotate;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/27.
 * @desc
 */
interface AnnotateContract {
    interface View extends BaseContract.View{
        void updateFileList();
    }
    interface Presenter extends BaseContract.Presenter{
        void queryFile();
    }
}
