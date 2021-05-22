package com.xlk.takstarpaperlessmanage.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/12.
 * @desc 系统设置-会议室管理
 */
public class RoomAdapter extends BaseQuickAdapter<InterfaceRoom.pbui_Item_MeetRoomDetailInfo, BaseViewHolder> {
    public RoomAdapter(@Nullable List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> data) {
        super(R.layout.item_room_manage, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceRoom.pbui_Item_MeetRoomDetailInfo item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getName().toStringUtf8())
                .setText(R.id.item_view_3, item.getAddr().toStringUtf8())
                .setText(R.id.item_view_4, item.getComment().toStringUtf8());
    }
}
