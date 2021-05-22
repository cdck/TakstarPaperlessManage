package com.xlk.takstarpaperlessmanage.model.bean;

import com.mogujie.tt.protobuf.InterfaceFilescorevote;
import com.mogujie.tt.protobuf.InterfaceMember;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
public class ScoreMember {
    InterfaceFilescorevote.pbui_Type_Item_FileScoreMemberStatistic score;
    InterfaceMember.pbui_Item_MeetMemberDetailInfo member;

    public ScoreMember(InterfaceFilescorevote.pbui_Type_Item_FileScoreMemberStatistic score, InterfaceMember.pbui_Item_MeetMemberDetailInfo member) {
        this.score = score;
        this.member = member;
    }

    public InterfaceFilescorevote.pbui_Type_Item_FileScoreMemberStatistic getScore() {
        return score;
    }

    public void setScore(InterfaceFilescorevote.pbui_Type_Item_FileScoreMemberStatistic score) {
        this.score = score;
    }

    public InterfaceMember.pbui_Item_MeetMemberDetailInfo getMember() {
        return member;
    }

    public void setMember(InterfaceMember.pbui_Item_MeetMemberDetailInfo member) {
        this.member = member;
    }
}
