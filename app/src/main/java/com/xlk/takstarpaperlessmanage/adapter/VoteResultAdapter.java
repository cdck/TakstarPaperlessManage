package com.xlk.takstarpaperlessmanage.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.util.DateUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/28.
 * @desc
 */
public class VoteResultAdapter extends BaseQuickAdapter<InterfaceVote.pbui_Item_MeetVoteDetailInfo, BaseViewHolder> {
    public VoteResultAdapter( @Nullable List<InterfaceVote.pbui_Item_MeetVoteDetailInfo> data) {
        super(R.layout.item_vote_result, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceVote.pbui_Item_MeetVoteDetailInfo item) {
        List<InterfaceVote.pbui_SubItem_VoteItemInfo> itemList = item.getItemList();
        String ret = "";
        for (int i = 0; i < itemList.size(); i++) {
            InterfaceVote.pbui_SubItem_VoteItemInfo info = itemList.get(i);
            if (i != 0) ret += "|";
            ret += info.getSelcnt();
        }
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getContent().toStringUtf8())
                .setText(R.id.item_view_3, DateUtil.calculateTime(item.getTimeouts()))
                .setText(R.id.item_view_4, item.getMode() == 1 ? getContext().getString(R.string.yes) : getContext().getString(R.string.no))
                .setText(R.id.item_view_5, ret)
                .setText(R.id.item_view_6, Constant.getVoteState(getContext(), item.getVotestate()));
    }
}
