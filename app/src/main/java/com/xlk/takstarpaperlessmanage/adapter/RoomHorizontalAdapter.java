package com.xlk.takstarpaperlessmanage.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/12.
 * @desc
 */
public class RoomHorizontalAdapter extends BaseQuickAdapter<InterfaceRoom.pbui_Item_MeetRoomDetailInfo, BaseViewHolder> {
    private int selectedRoomId = -1;

    public RoomHorizontalAdapter(@Nullable List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> data) {
        super(R.layout.item_horizontal_room, data);
    }

    public void setSelectedRoomId(int roomId) {
        selectedRoomId = roomId;
        notifyDataSetChanged();
    }
    public int getSelectedRoomId(){
        for (int i = 0; i < getData().size(); i++) {
            if(getData().get(i).getRoomid()==selectedRoomId){
                return selectedRoomId;
            }
        }
        return -1;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceRoom.pbui_Item_MeetRoomDetailInfo item) {
        holder.setText(R.id.item_view_1, item.getName().toStringUtf8());
        View view = holder.getView(R.id.item_view_1);
//        RelativeLayout item_root_view = holder.getView(R.id.item_root_view);
//        item_root_view.setSelected(selectedRoomId == item.getRoomid());
        view.setSelected(selectedRoomId == item.getRoomid());
    }
}
