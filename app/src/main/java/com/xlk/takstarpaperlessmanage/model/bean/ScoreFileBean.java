package com.xlk.takstarpaperlessmanage.model.bean;

/**
 * @author Created by xlk on 2021/6/1.
 * @desc
 */
public class ScoreFileBean {
    int mediaId;
    String filePath;

    public ScoreFileBean(int mediaId, String filePath) {
        this.mediaId = mediaId;
        this.filePath = filePath;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
