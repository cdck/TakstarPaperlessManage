package com.xlk.takstarpaperlessmanage.helper.task;

import com.blankj.utilcode.util.LogUtils;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.xlk.takstarpaperlessmanage.helper.archive.ConsumptionTask;
import com.xlk.takstarpaperlessmanage.helper.archive.LineUpTaskHelp;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.util.JxlUtil;

import java.util.List;

/**
 * @author Created by xlk on 2021/7/9.
 * @desc 归档会议投票结果
 */
public class VoteTask extends ConsumptionTask implements Runnable {
    private final Info info;

    public VoteTask(Info info) {
        this.info = info;
        thread = new Thread(this);
    }

    @Override
    public void run() {
        try {
            LogUtils.i("归档会议投票结果 当前线程id=" + Thread.currentThread().getId() + "-" + Thread.currentThread().getName());
            long l = System.currentTimeMillis();
            Thread.sleep(1000);
            if (!info.voteData.isEmpty()) {
                JxlUtil.exportArchiveVote(Constant.DIR_ARCHIVE_TEMP, info.voteData, info.memberSize, true);
            }
            if (!info.electionData.isEmpty()) {
                JxlUtil.exportArchiveVote(Constant.DIR_ARCHIVE_TEMP, info.electionData, info.memberSize, false);
            }
            isResult = true;
            LogUtils.i("归档会议投票结果，用时=" + (System.currentTimeMillis() - l));
            LineUpTaskHelp.getInstance().exOk(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Info {
        List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> voteData;
        List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> electionData;
        int memberSize;

        public Info(List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> voteData, List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> electionData, int memberSize) {
            this.voteData = voteData;
            this.electionData = electionData;
            this.memberSize = memberSize;
        }
    }
}
