package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.archive;

import com.mogujie.tt.protobuf.InterfaceFile;

/**
 * @author Created by xlk on 2021/7/9.
 * @desc
 */
public class DownloadOtherFileInfo {
    int dirId;
    String dirName;
    InterfaceFile.pbui_Item_MeetDirFileDetailInfo item;

    public DownloadOtherFileInfo(int dirId, String dirName, InterfaceFile.pbui_Item_MeetDirFileDetailInfo item) {
        this.dirId = dirId;
        this.dirName = dirName;
        this.item = item;
    }

    public int getDirId() {
        return dirId;
    }

    public String getDirName() {
        return dirName;
    }

    public InterfaceFile.pbui_Item_MeetDirFileDetailInfo getItem() {
        return item;
    }
}
