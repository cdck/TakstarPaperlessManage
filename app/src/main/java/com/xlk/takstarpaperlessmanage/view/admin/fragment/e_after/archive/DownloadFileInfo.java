package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.archive;

/**
 * @author Created by xlk on 2021/5/28.
 * @desc
 */
class DownloadFileInfo {
    int mediaId;
    String fileName;

    public DownloadFileInfo(int mediaId, String fileName) {
        this.mediaId = mediaId;
        this.fileName = fileName;
    }

    public int getMediaId() {
        return mediaId;
    }

    public String getFileName() {
        return fileName;
    }
}
