package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.agenda;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/18.
 * @desc
 */
interface AgendaContract {
    interface View extends BaseContract.View{
        void updateAgendaText(String content);

        void updateAgendaFile(int mediaid, String fileName);

        void updateAgendaList();

        void updateAgendaFileList();

        void updateDirList();
    }
    interface Presenter extends BaseContract.Presenter{
        void queryAgenda();

        void queryShareFile();

        void queryMeetDir();
    }
}
