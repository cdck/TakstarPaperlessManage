package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.table;

import com.mogujie.tt.protobuf.InterfaceTablecard;
import com.xlk.takstarpaperlessmanage.base.BaseContract;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/22.
 * @desc
 */
interface TableContract {
    interface View extends BaseContract.View{
        void updateTableCardBg(String filePath);

        void updateTableCard();

        void updateBackgroundImageList();
    }
    interface Presenter extends BaseContract.Presenter{
        void queryTableCard();

        void save(int modflag, List<InterfaceTablecard.pbui_Item_MeetTableCardDetailInfo> tableCardData);

        void clearBackgroundImage();

        void queryBackgroundImage();
    }
}
