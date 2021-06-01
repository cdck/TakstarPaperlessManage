package com.xlk.takstarpaperlessmanage.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.bean.SubmitMember;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/24.
 * @desc
 */
public class SubmitMemberAdapter extends BaseQuickAdapter<SubmitMember, BaseViewHolder> {
    public SubmitMemberAdapter(@Nullable List<SubmitMember> data) {
        super(R.layout.item_vote_submit_member, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, SubmitMember item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getMemberInfo().getMembername().toStringUtf8())
                .setText(R.id.item_view_3, item.getAnswer());
    }
}
