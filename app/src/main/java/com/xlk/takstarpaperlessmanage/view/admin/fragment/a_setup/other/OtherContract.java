package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.other;

import com.xlk.takstarpaperlessmanage.base.BaseContract;
import com.xlk.takstarpaperlessmanage.model.bean.MainInterfaceBean;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/14.
 * @desc
 */
public interface OtherContract {
    interface View extends BaseContract.View {
        void updateWebUrlList();

        void updateCompany(String company);

        void updateReleaseFileList();

        void updateBgFileList();

        void updateReleaseFileName(String fileName, int mediaid);

        void updateInterface();

        void updateUpgradeFileList();

        void updateMainBgImg(String filePath);

        void updateMainLogoImg(String filePath);

        void updateProjectiveBgImg(String filePath);

        void updateProjectiveLogoImg(String filePath);

        void updateNoticeBgImg(String filePath);

        void updateNoticeLogoImg(String filePath);
    }

    interface Presenter extends BaseContract.Presenter {
        void queryWebUrl();

        void queryReleaseFile();

        void queryInterFaceConfiguration();

        void queryBigFile();

        void modifyCompanyName(String companyName);

        void saveReleaseFile(int mediaId);

        void queryLocalAdmin();

        void modifyLocalAdminPassword(String oldp, String newp);

        void saveMainLogo(int mediaId);

        void saveProjectiveLogo(int mediaId);

        void saveNoticeLogo(int mediaId);

        void saveInterfaceConfig(List<MainInterfaceBean> data);

        void saveMainBg(int mediaId);

        void saveProjectiveBg(int mediaId);

        void saveNoticeBg(int mediaId);

        void queryUpgradeFile();
    }
}
