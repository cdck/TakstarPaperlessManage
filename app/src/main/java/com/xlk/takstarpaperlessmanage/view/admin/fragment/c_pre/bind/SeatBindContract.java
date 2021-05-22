package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.bind;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
interface SeatBindContract {
    interface View extends BaseContract.View{
        void updateMemberList();

        void updateSeatDataList();

        void updateRoomBg(String filePath, int mediaId);
    }
    interface Presenter extends BaseContract.Presenter{
        void queryMember();

        void queryRoomBgPic();
    }
}
