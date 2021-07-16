package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.seatsort;

import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.xlk.takstarpaperlessmanage.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/7/13.
 * @desc
 */
public class PreViewImageAdapter extends BaseQuickAdapter<InterfaceFile.pbui_Item_MeetDirFileDetailInfo, BaseViewHolder> {
    List<Integer> selectedIds = new ArrayList<>();

    public void setSelected(int mediaId) {
        if (selectedIds.contains(mediaId)) {
            selectedIds.remove(selectedIds.indexOf(mediaId));
        } else {
            selectedIds.add(mediaId);
        }
        notifyDataSetChanged();
    }
    public List<Integer> getSelectedIds() {
        List<Integer> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (selectedIds.contains(getData().get(i).getMediaid())) {
                temps.add(getData().get(i).getMediaid());
            }
        }
        return temps;
    }

    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> getSelectedFiles() {
        List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = getData().get(i);
            if (selectedIds.contains(item.getMediaid())) {
                temps.add(item);
            }
        }
        return temps;
    }

    public boolean isSelectedAll() {
        return getSelectedIds().size() == getData().size();
    }

    public void setSelectedAll(boolean all) {
        selectedIds.clear();
        if (all) {
            for (int i = 0; i < getData().size(); i++) {
                selectedIds.add(getData().get(i).getMediaid());
            }
        }
        notifyDataSetChanged();
    }

    public PreViewImageAdapter(@Nullable List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> data) {
        super(R.layout.item_preview_picture_file, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceFile.pbui_Item_MeetDirFileDetailInfo item) {
        holder.setText(R.id.item_view_2, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_3, item.getName().toStringUtf8())
                .setText(R.id.item_view_4, String.valueOf(item.getMediaid()));
        boolean isSelected = selectedIds.contains(item.getMediaid());
        CheckBox cb = holder.getView(R.id.item_view_1);
        cb.setChecked(isSelected);
    }
}
