package com.xlk.takstarpaperlessmanage.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceAdmin;
import com.xlk.takstarpaperlessmanage.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/13.
 * @desc
 */
public class AdminAdapter extends BaseQuickAdapter<InterfaceAdmin.pbui_Item_AdminDetailInfo, BaseViewHolder> {
    public AdminAdapter( @Nullable List<InterfaceAdmin.pbui_Item_AdminDetailInfo> data) {
        super(R.layout.item_admin_user, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceAdmin.pbui_Item_AdminDetailInfo item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getAdminname().toStringUtf8())
                .setText(R.id.item_view_3, item.getPw().toStringUtf8())
                .setText(R.id.item_view_4, item.getPhone().toStringUtf8())
                .setText(R.id.item_view_5, item.getEmail().toStringUtf8())
                .setText(R.id.item_view_6, item.getComment().toStringUtf8())
        ;
    }
}
