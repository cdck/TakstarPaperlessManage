package com.xlk.takstarpaperlessmanage.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.protobuf.ByteString;
import com.mogujie.tt.protobuf.InterfaceFilescorevote;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.JniHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/24.
 * @desc
 */
public class RateAdapter extends BaseQuickAdapter<InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore, BaseViewHolder> {
    public RateAdapter(@Nullable List<InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore> data) {
        super(R.layout.item_rate, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore item) {
        List<ByteString> voteTextList = item.getVoteTextList();
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getContent().toStringUtf8())
                .setText(R.id.item_view_3, JniHelper.getInstance().queryFileNameByMediaId(item.getFileid()))
                .setText(R.id.item_view_4, voteTextList.size() > 0 ? voteTextList.get(0).toStringUtf8() : "")
                .setText(R.id.item_view_5, voteTextList.size() > 1 ? voteTextList.get(1).toStringUtf8() : "")
                .setText(R.id.item_view_6, voteTextList.size() > 2 ? voteTextList.get(2).toStringUtf8() : "")
                .setText(R.id.item_view_7, voteTextList.size() > 3 ? voteTextList.get(3).toStringUtf8() : "")
                .setText(R.id.item_view_8, item.getMode() == 1 ? getContext().getString(R.string.yes) : getContext().getString(R.string.no))
                .setText(R.id.item_view_9, Constant.getVoteState(getContext(), item.getVotestate()));
    }
}
