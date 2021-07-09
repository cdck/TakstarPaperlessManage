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
        void updateArchiveInform(List<ArchiveInform> archiveInforms);

        void updateArchiveDirPath(String dirPath);
    }

    interface Presenter extends BaseContract.Presenter {
        void queryAllData();

        boolean hasStarted();

        void setEncryption(boolean encryption);

        void archiveAll();

        void archiveSelected(boolean checked, boolean checked1, boolean checked2, boolean checked3, boolean checked4, boolean checked5, boolean checked6);

        void cancelArchive();

        void cancelArchive(boolean cancel);

        void setPassword(String pwd);

        BasicInformationTask.Info getBasicInformationTaskInfo();

        MemberTask.Info getMemberTaskInfo();

        SignInTask.Info getSignInTaskInfo();

        VoteTask.Info getVoteTaskInfo();

        void addDownloadShareFileTask(LineUpTaskHelp lineUpTaskHelp);

        void addDownloadAnnotationFileTask(LineUpTaskHelp lineUpTaskHelp);

        void addDownloadMeetDataFileTask(LineUpTaskHelp lineUpTaskHelp);
    }
}
