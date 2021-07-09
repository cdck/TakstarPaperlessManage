package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.material;

import android.content.res.Resources;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.xlk.takstarpaperlessmanage.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/7/3.
 * @desc
 */
class HistoryDirAdapter extends BaseQuickAdapter<InterfaceFile.pbui_Item_MeetDirDetailInfo, BaseViewHolder> {
    private int selectedDirId = -1;

    public HistoryDirAdapter(int layoutResId, @Nullable List<InterfaceFile.pbui_Item_MeetDirDetailInfo> data) {
        super(layoutResId, data);
    }

    public void choose(int dirId) {
        selectedDirId = dirId;
        notifyDataSetChanged();
    }

    public InterfaceFile.pbui_Item_MeetDirDetailInfo getSelectedDir() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getId() == selectedDirId) {
                return getData().get(i);
            }
        }
        return null;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceFile.pbui_Item_MeetDirDetailInfo item) {
        holder.setText(R.id.item_view_1, String.valueOf(item.getId()))
                .setText(R.id.item_view_2, item.getName().toStringUtf8());
        boolean isSelected = selectedDirId == item.getId();
        Resources resources = getContext().getResources();
        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor);
        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
        View view = holder.getView(R.id.item_root_view);
        view.setBackgroundColor(backgroundColor);
    }
}
