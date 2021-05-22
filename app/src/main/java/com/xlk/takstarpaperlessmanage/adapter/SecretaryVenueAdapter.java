package com.xlk.takstarpaperlessmanage.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/13.
 * @desc
 */
public class SecretaryVenueAdapter extends BaseQuickAdapter<InterfaceRoom.pbui_Item_MeetRoomDetailInfo, BaseViewHolder> {

    private List<Integer> selectedIds = new ArrayList<>();

    public SecretaryVenueAdapter(@Nullable List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> data) {
        super(R.layout.item_secretary_venue, data);
    }

    public void setSelected(int roomId) {
        if (selectedIds.contains(roomId)) {
            selectedIds.remove(selectedIds.indexOf(roomId));
        } else {
            selectedIds.add(roomId);
        }
        notifyDataSetChanged();
    }

    public void checkAll(boolean all) {
        selectedIds.clear();
        if (all) {
            for (int i = 0; i < getData().size(); i++) {
                selectedIds.add(getData().get(i).getRoomid());
            }
        }
        notifyDataSetChanged();
    }

    public boolean isCheckAll() {
        return getData().size() == selectedIds.size();
    }

    public List<Integer> getSelectedIds() {
        List<Integer> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (selectedIds.contains(getData().get(i).getRoomid())) {
                temps.add(getData().get(i).getRoomid());
            }
        }
        return temps;
    }

    public void notifySelected() {
        List<Integer> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (selectedIds.contains(getData().get(i).getRoomid())) {
                temps.add(getData().get(i).getRoomid());
            }
        }
        selectedIds.clear();
        selectedIds.addAll(temps);
        notifyDataSetChanged();
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceRoom.pbui_Item_MeetRoomDetailInfo item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getName().toStringUtf8())
                .setText(R.id.item_view_3, item.getAddr().toStringUtf8());

        boolean isSelected = selectedIds.contains(item.getRoomid());
        int textColor = isSelected ? getContext().getColor(R.color.white) : getContext().getColor(R.color.text_default_color);
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor)
                .setTextColor(R.id.item_view_3, textColor);
        int backgroundColor = isSelected ? getContext().getColor(R.color.admin_child_selected_bg) : getContext().getColor(R.color.white);
        holder.setBackgroundColor(R.id.item_view_1, backgroundColor)
                .setBackgroundColor(R.id.item_view_2, backgroundColor)
                .setBackgroundColor(R.id.item_view_3, backgroundColor);
    }
}
