package com.xlk.takstarpaperlessmanage.adapter;

import android.content.res.Resources;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.bean.MemberRoleBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/22.
 * @desc
 */
public class SeatBindMemberAdapter extends BaseQuickAdapter<MemberRoleBean, BaseViewHolder> {
    public SeatBindMemberAdapter(@Nullable List<MemberRoleBean> data) {
        super(R.layout.item_bind_member, data);
    }
    private int selectedId;

    public void setSelected(int memberId) {
        selectedId = memberId;
        notifyDataSetChanged();
    }

    public int getSelectedId() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getMember().getPersonid() == selectedId) {
                return selectedId;
            }
        }
        return -1;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, MemberRoleBean item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getMember().getName().toStringUtf8());

        boolean isSelected = selectedId == item.getMember().getPersonid();
        boolean isBind = item.getSeat() != null && item.getMember().getPersonid() != 0;

        Resources resources = getContext().getResources();
        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
        if (isBind) {
            textColor = resources.getColor(R.color.red);
        }
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor);
        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
        holder.setBackgroundColor(R.id.item_view_1, backgroundColor)
                .setBackgroundColor(R.id.item_view_2, backgroundColor);
    }
}
