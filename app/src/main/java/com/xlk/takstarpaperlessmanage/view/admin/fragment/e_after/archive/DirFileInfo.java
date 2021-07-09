package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.archive;

import com.mogujie.tt.protobuf.InterfaceFile;

import java.util.List;

/**
 * @author Created by xlk on 2021/7/9.
 * @desc
 */
public class DirFileInfo {
    int dirId;
    int parId;
    String dirName;
    List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> files;

    public DirFileInfo(int dirId, String dirName, List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> files) {
        this.dirId = dirId;
        this.dirName = dirName;
        this.files = files;
    }

    public DirFileInfo(int dirId, int parId, String dirName, List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> files) {
        this.dirId = dirId;
        this.parId = parId;
        this.dirName = dirName;
        this.files = files;
    }

    public int getDirId() {
        return dirId;
    }

    public int getParId() {
        return parId;
    }

    public String getDirName() {
        return dirName;
    }

    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> getFiles() {
        return files;
    }
}
