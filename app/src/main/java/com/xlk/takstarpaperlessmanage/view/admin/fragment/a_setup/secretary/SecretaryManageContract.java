package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.secretary;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/13.
 * @desc
 */
public interface SecretaryManageContract {
    interface View extends BaseContract.View {
        void updateAdminList();

        void updateControlledRoomList();

        void updateOptionalRoomList();
    }

    interface Presenter extends BaseContract.Presenter {
        void queryAdmin();

        void setCurrentAdminId(int adminid);

        void queryRoomsByAdminId(int adminid);

        void defineModify();

        void addRoom2Admin(List<Integer> roomIds);

        void removeRoomFromAdmin(List<Integer> roomIds);

        void savePreviousStep();

        void repeal();
    }
}
