package com.xlk.takstarpaperlessmanage.adapter;

import android.content.res.Resources;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.takstarpaperlessmanage.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/19.
 * @desc
 */
public class MemberAdapter extends BaseQuickAdapter<InterfaceMember.pbui_Item_MemberDetailInfo, BaseViewHolder> {
    private final boolean hideOperation;
    private int selectedId = -1;

    public MemberAdapter(@Nullable List<InterfaceMember.pbui_Item_MemberDetailInfo> data) {
        this(data, false);
    }

    public MemberAdapter(@Nullable List<InterfaceMember.pbui_Item_MemberDetailInfo> data, boolean hideOperation) {
        super(R.layout.item_member, data);
        this.hideOperation = hideOperation;
    }

    public void setSelectedId(int memberId) {
        selectedId = memberId;
        notifyDataSetChanged();
    }

    public int getSelectedId() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getPersonid() == selectedId) {
                return selectedId;
            }
        }
        return -1;
    }

    public InterfaceMember.pbui_Item_MemberDetailInfo getSelectedMember() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getPersonid() == selectedId) {
                return getData().get(i);
            }
        }
        return null;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceMember.pbui_Item_MemberDetailInfo item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getName().toStringUtf8())
                .setText(R.id.item_view_3, item.getCompany().toStringUtf8())
                .setText(R.id.item_view_4, item.getJob().toStringUtf8())
                .setText(R.id.item_view_5, item.getPhone().toStringUtf8())
                .setText(R.id.item_view_6, item.getEmail().toStringUtf8())
                .setText(R.id.item_view_7, item.getPassword().toStringUtf8())
                .setText(R.id.item_view_8, item.getComment().toStringUtf8());
        boolean isSelected = selectedId == item.getPersonid();
        Resources resources = getContext().getResources();
        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor)
                .setTextColor(R.id.item_view_3, textColor)
                .setTextColor(R.id.item_view_4, textColor)
                .setTextColor(R.id.item_view_5, textColor)
                .setTextColor(R.id.item_view_6, textColor)
                .setTextColor(R.id.item_view_7, textColor)
                .setTextColor(R.id.item_view_8, textColor);
        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
        View view = holder.getView(R.id.item_root_view);
        view.setBackgroundColor(backgroundColor);
        holder.getView(R.id.ll_operation_view).setVisibility(hideOperation ? View.GONE : View.VISIBLE);

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
