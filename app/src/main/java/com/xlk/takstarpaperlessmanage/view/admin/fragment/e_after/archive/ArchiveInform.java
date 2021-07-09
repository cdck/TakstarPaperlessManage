package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.archive;

/**
 * @author Created by xlk on 2020/10/27.
 * @desc
 */
public class ArchiveInform {
    /**
     * 0=共享资料，1=批注资料，2=会议资料
     */
    int type;
    int mediaId;
    String content;
    String result;

    public ArchiveInform(int type, int id, String content, String result) {
        this.type = type;
        this.mediaId = id;
        this.content = content;
        this.result = result;
    }

    public ArchiveInform(String content, String result) {
        this.content = content;
        this.result = result;
    }

    public int getType() {
        return type;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
