package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.archive;

import com.xlk.takstarpaperlessmanage.base.BaseContract;
import com.xlk.takstarpaperlessmanage.helper.task.BasicInformationTask;
import com.xlk.takstarpaperlessmanage.helper.archive.LineUpTaskHelp;
import com.xlk.takstarpaperlessmanage.helper.task.MemberTask;
import com.xlk.takstarpaperlessmanage.helper.task.SignInTask;
import com.xlk.takstarpaperlessmanage.helper.task.VoteTask;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/28.
 * @desc
 */
interface ArchiveContract {
    interface View extends BaseContract.View {

        void updateArchiveDirPath(String dirPath);

        void updateArchiveInform(int type, String fileName,int mediaId,String dirName, int progress);
    }

    interface Presenter extends BaseContract.Presenter {
        void queryAllData();

        void setEncryption(boolean encryption);

        BasicInformationTask.Info getBasicInformationTaskInfo();

        MemberTask.Info getMemberTaskInfo();

        SignInTask.Info getSignInTaskInfo();

        VoteTask.Info getVoteTaskInfo();

        void addShouldDownloadShareFiles(LineUpTaskHelp lineUpTaskHelp);

        void addShouldDownloadAnnotationFiles(LineUpTaskHelp lineUpTaskHelp);

        void addShouldDownloadMeetDataFiles(LineUpTaskHelp lineUpTaskHelp);

        /**
         * 清空将要下载的文件
         */
        void clearShouldDownloadFiles();
    }
}
