package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.rate;

import com.mogujie.tt.protobuf.InterfaceFilescorevote;
import com.xlk.takstarpaperlessmanage.model.bean.ScoreMember;

import java.util.List;

/**
 * @author Created by xlk on 2021/7/5.
 * @desc
 */
public class PdfRateInfo {
    String fileName;
    InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore score;
    List<ScoreMember> scoreMembers;

    public PdfRateInfo(String fileName, InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore score, List<ScoreMember> scoreMembers) {
        this.fileName = fileName;
        this.score = score;
        this.scoreMembers = scoreMembers;
    }

    public String getFileName() {
        return fileName;
    }

    public InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore getScore() {
        return score;
    }

    public List<ScoreMember> getScoreMembers() {
        return scoreMembers;
    }
}
