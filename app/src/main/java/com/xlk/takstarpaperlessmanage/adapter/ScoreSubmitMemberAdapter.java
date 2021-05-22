package com.xlk.takstarpaperlessmanage.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceFilescorevote;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.bean.ScoreMember;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
public class ScoreSubmitMemberAdapter extends BaseQuickAdapter<ScoreMember, BaseViewHolder> {
    public ScoreSubmitMemberAdapter(@Nullable List<ScoreMember> data) {
        super(R.layout.item_score_submit_member, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, ScoreMember item) {
        InterfaceFilescorevote.pbui_Type_Item_FileScoreMemberStatistic score = item.getScore();
        List<Integer> scoreList = score.getScoreList();
        int total = 0;
        for (int i = 0; i < scoreList.size(); i++) {
            total += scoreList.get(i);
        }
        double div = Constant.div(total, scoreList.size(), 2);
        holder.setText(R.id.item_view_1, item.getMember().getMembername().toStringUtf8())
                .setText(R.id.item_view_2, String.valueOf(scoreList.get(0)))
                .setText(R.id.item_view_3, String.valueOf(scoreList.get(1)))
                .setText(R.id.item_view_4, String.valueOf(scoreList.get(2)))
                .setText(R.id.item_view_5, String.valueOf(scoreList.get(3)))
                .setText(R.id.item_view_6, String.valueOf(total))
                .setText(R.id.item_view_7, String.valueOf(div));
    }
}
