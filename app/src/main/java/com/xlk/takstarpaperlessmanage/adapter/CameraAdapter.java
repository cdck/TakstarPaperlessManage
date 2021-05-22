package com.xlk.takstarpaperlessmanage.adapter;

import android.content.res.Resources;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceVideo;
import com.xlk.takstarpaperlessmanage.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
public class CameraAdapter extends BaseQuickAdapter<InterfaceVideo.pbui_Item_MeetVideoDetailInfo, BaseViewHolder> {
    List<Integer> selectedIds = new ArrayList<>();

    public CameraAdapter(@Nullable List<InterfaceVideo.pbui_Item_MeetVideoDetailInfo> data) {
        super(R.layout.item_camera, data);
    }


    public void choose(int id) {
        if (selectedIds.contains(id)) {
            selectedIds.remove(selectedIds.indexOf(id));
        } else {
            selectedIds.add(id);
        }
        notifyDataSetChanged();
    }

    public List<InterfaceVideo.pbui_Item_MeetVideoDetailInfo> getSelectedCameras() {
        List<InterfaceVideo.pbui_Item_MeetVideoDetailInfo> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (selectedIds.contains(getData().get(i).getId())) {
                temps.add(getData().get(i));
            }
        }
        return temps;
    }

    public List<Integer> getSelectedIds() {
        List<Integer> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (selectedIds.contains(getData().get(i).getId())) {
                temps.add(getData().get(i).getId());
            }
        }
        return temps;
    }

    public void customNotify() {
        List<Integer> currents = getSelectedIds();
        selectedIds.clear();
        selectedIds.addAll(currents);
        notifyDataSetChanged();
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceVideo.pbui_Item_MeetVideoDetailInfo item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getName().toStringUtf8())
                .setText(R.id.item_view_3, item.getDevicename().toStringUtf8())
                .setText(R.id.item_view_4, String.valueOf(item.getSubid()))
                .setText(R.id.item_view_5, item.getAddr().toStringUtf8());
        boolean isSelected = selectedIds.contains(item.getId());
        Resources resources = getContext().getResources();
        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor)
                .setTextColor(R.id.item_view_3, textColor)
                .setTextColor(R.id.item_view_4, textColor)
                .setTextColor(R.id.item_view_5, textColor);
        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
        holder.setBackgroundColor(R.id.item_view_1, backgroundColor)
                .setBackgroundColor(R.id.item_view_2, backgroundColor)
                .setBackgroundColor(R.id.item_view_3, backgroundColor)
                .setBackgroundColor(R.id.item_view_4, backgroundColor)
                .setBackgroundColor(R.id.item_view_5, backgroundColor);
    }
}
