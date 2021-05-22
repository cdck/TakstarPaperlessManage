package com.xlk.takstarpaperlessmanage.adapter;

import android.content.res.Resources;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
public class VoteAdapter extends BaseQuickAdapter<InterfaceVote.pbui_Item_MeetVoteDetailInfo, BaseViewHolder> {
    public VoteAdapter(@Nullable List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> data) {
        super(R.layout.item_vote, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceVote.pbui_Item_MeetVoteDetailInfo item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getContent().toStringUtf8())
                .setText(R.id.item_view_3, item.getMode() == 1 ? getContext().getString(R.string.yes) : getContext().getString(R.string.no))
                .setText(R.id.item_view_4, Constant.getVoteState(getContext(), item.getVotestate()));
//        boolean isSelected = selectedId == item.getPersonid();
//        Resources resources = getContext().getResources();
//        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
//        holder.setTextColor(R.id.item_view_1, textColor)
//                .setTextColor(R.id.item_view_2, textColor)
//                .setTextColor(R.id.item_view_3, textColor)
//                .setTextColor(R.id.item_view_4, textColor)
//                .setTextColor(R.id.item_view_5, textColor)
//                .setTextColor(R.id.item_view_6, textColor)
//                .setTextColor(R.id.item_view_7, textColor)
//                .setTextColor(R.id.item_view_8, textColor);
//        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
//        holder.setBackgroundColor(R.id.item_view_1, backgroundColor)
//                .setBackgroundColor(R.id.item_view_2, backgroundColor)
//                .setBackgroundColor(R.id.item_view_3, backgroundColor)
//                .setBackgroundColor(R.id.item_view_4, backgroundColor)
//                .setBackgroundColor(R.id.item_view_5, backgroundColor)
//                .setBackgroundColor(R.id.item_view_6, backgroundColor)
//                .setBackgroundColor(R.id.item_view_7, backgroundColor)
//                .setBackgroundColor(R.id.item_view_8, backgroundColor);
    }
}
