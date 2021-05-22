package com.xlk.takstarpaperlessmanage.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfacePerson;
import com.xlk.takstarpaperlessmanage.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/13.
 * @desc
 */
public class AttendeeAdapter extends BaseQuickAdapter<InterfacePerson.pbui_Item_PersonDetailInfo, BaseViewHolder> {
    public AttendeeAdapter( @Nullable List<InterfacePerson.pbui_Item_PersonDetailInfo> data) {
        super(R.layout.item_attendee, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfacePerson.pbui_Item_PersonDetailInfo item) {
        holder.setText(R.id.item_view_1,String.valueOf(holder.getLayoutPosition()+1))
                .setText(R.id.item_view_2,item.getName().toStringUtf8())
                .setText(R.id.item_view_3,item.getCompany().toStringUtf8())
                .setText(R.id.item_view_4,item.getJob().toStringUtf8())
                .setText(R.id.item_view_5,item.getPhone().toStringUtf8())
                .setText(R.id.item_view_6,item.getEmail().toStringUtf8())
                .setText(R.id.item_view_7,item.getPassword().toStringUtf8())
                .setText(R.id.item_view_8,item.getComment().toStringUtf8());
    }
}
