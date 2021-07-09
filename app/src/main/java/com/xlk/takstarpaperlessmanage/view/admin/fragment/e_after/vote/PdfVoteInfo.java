package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.vote;

import com.xlk.takstarpaperlessmanage.model.bean.SubmitMember;

import java.util.List;

/**
 * @author Created by xlk on 2021/7/3.
 * @desc
 */
public class PdfVoteInfo {
    String title;
    int yingdao;
    int shidao;
    int yitou;
    int weitou;
    List<SubmitMember> submitMembers;

    public PdfVoteInfo(String title, int yingdao, int shidao, int yitou, int weitou, List<SubmitMember> submitMembers) {
        this.title = title;
        this.yingdao = yingdao;
        this.shidao = shidao;
        this.yitou = yitou;
        this.weitou = weitou;
        this.submitMembers = submitMembers;
    }

    public String getTitle() {
        return title;
    }

    public int getYingdao() {
        return yingdao;
    }

    public int getShidao() {
        return shidao;
    }

    public int getYitou() {
        return yitou;
    }

    public int getWeitou() {
        return weitou;
    }

    public List<SubmitMember> getSubmitMembers() {
        return submitMembers;
    }
}
