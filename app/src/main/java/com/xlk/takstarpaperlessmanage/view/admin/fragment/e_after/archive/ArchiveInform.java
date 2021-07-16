package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.archive;

/**
 * @author Created by xlk on 2020/10/27.
 * @desc
 */
public class ArchiveInform {
    /**
     * 0=共享资料，1=批注资料，2=会议资料
     */
    int type = -1;
    int mediaId;
    String content;
    String result;
    /**
     * 当type=2时有效
     */
    String dirName;

    public ArchiveInform(int type, int mediaId, String dirName, String content, String result) {
        this.type = type;
        this.mediaId = mediaId;
        this.dirName = dirName;
        this.content = content;
        this.result = result;
    }

    public ArchiveInform(int type, int mediaId, String content, String result) {
        this.type = type;
        this.mediaId = mediaId;
        this.content = content;
        this.result = result;
    }

    public ArchiveInform(String content, String result) {
        this.content = content;
        this.result = result;
    }

    public boolean isThis(int type, String content, int mediaId) {
        return this.type == type && this.content.equals(content) && this.mediaId == mediaId;
    }

    public boolean isThis(int type, String content, int mediaId, String dirName) {
        return this.type == type && this.content.equals(content) && this.mediaId == mediaId && this.dirName.equals(dirName);
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
