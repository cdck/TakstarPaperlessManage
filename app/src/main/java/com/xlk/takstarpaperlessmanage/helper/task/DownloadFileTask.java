package com.xlk.takstarpaperlessmanage.helper.task;

import com.blankj.utilcode.util.FileUtils;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.xlk.takstarpaperlessmanage.helper.archive.ConsumptionTask;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.JniHelper;

import java.io.File;

/**
 * @author Created by xlk on 2021/7/9.
 * @desc
 */
public class DownloadFileTask extends ConsumptionTask implements Runnable {
    private final Info info;
    private final String tag;
    private int count = 0;

    public DownloadFileTask(Info info) {
        this("", info);
    }

    public DownloadFileTask(String tag, Info info) {
        this.tag = tag;
        this.info = info;
    }

    @Override
    public void run() {
        String fileName = info.data.getName().toStringUtf8();
        String filePath = Constant.DIR_ARCHIVE_TEMP + info.dirName + "/" + fileName;
        JniHelper.getInstance().downloadFile(getFilePath(filePath),
                info.data.getMediaid(), 1, 0, info.userStr);
        isResult = true;
    }

    private String getFilePath(String filePath) {
        boolean fileExists = FileUtils.isFileExists(filePath);
        if (fileExists) {
            File file = new File(filePath);
            String parent = file.getParent();
            String fileNameNoExtension = FileUtils.getFileNameNoExtension(filePath);
            String fileExtension = FileUtils.getFileExtension(filePath);
            return getFilePath(parent + "/" + fileNameNoExtension + "（" + (++count) + "）." + fileExtension);
        }
        return filePath;
    }

    public static class Info {
        String dirName;
        String userStr;
        InterfaceFile.pbui_Item_MeetDirFileDetailInfo data;

        public Info(String dirName, String userStr, InterfaceFile.pbui_Item_MeetDirFileDetailInfo data) {
            this.dirName = dirName;
            this.userStr = userStr;
            this.data = data;
        }
    }
}
