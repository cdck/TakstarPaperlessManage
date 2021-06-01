package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.archive;

import com.xlk.takstarpaperlessmanage.base.BaseContract;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/28.
 * @desc
 */
interface ArchiveContract {
    interface View extends BaseContract.View {
        void updateArchiveInform(List<ArchiveInform> archiveInforms);

        /**
         * 更新会议基本信息归档状态
         *
         * @param status 等待、正在导出、完成
         */
        void updateMeetingBasicInformation(String status);

        /**
         * 更新参会人员信息归档状态
         *
         * @param status 等待、正在导出、完成
         */
        void updateAttendeeInformation(String status);

        /**
         * 更新会议签到信息归档状态
         *
         * @param status 等待、正在导出、完成
         */
        void updateConferenceSignInInformation(String status);

        /**
         * 更新会议投票结果归档状态
         *
         * @param status 等待、正在导出、完成
         */
        void updateMeetingVoteResult(String status);

        void updateShardFile(String status);

        void updateAnnotateFile(String status);

        void updateMeetingMaterial(String status);

        void setAllWaitingStatus();
    }

    interface Presenter extends BaseContract.Presenter {
        void queryAllData();

        boolean hasStarted();

        void setEncryption(boolean encryption);

        void archiveAll();

        void archiveSelected(boolean checked, boolean checked1, boolean checked2, boolean checked3, boolean checked4, boolean checked5, boolean checked6);
    }
}
