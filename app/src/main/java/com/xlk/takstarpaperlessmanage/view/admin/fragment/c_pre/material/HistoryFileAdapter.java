package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.material;

import android.content.res.Resources;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.util.FileUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/7/3.
 * @desc
 */
class HistoryFileAdapter extends BaseQuickAdapter<InterfaceFile.pbui_Item_MeetDirFileDetailInfo, BaseViewHolder> {
    private int selectedMediaId = -1;

    public HistoryFileAdapter(int layoutResId, @Nullable List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> data) {
        super(layoutResId, data);
    }

    public void choose(int mediaid) {
        selectedMediaId = mediaid;
        notifyDataSetChanged();
    }

    public InterfaceFile.pbui_Item_MeetDirFileDetailInfo getSelectedFile() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getMediaid() == selectedMediaId) {
                return getData().get(i);
            }
        }
        return null;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceFile.pbui_Item_MeetDirFileDetailInfo item) {
        holder.setText(R.id.item_view_1, String.valueOf(item.getMediaid()))
                .setText(R.id.item_view_2, item.getName().toStringUtf8())
                .setText(R.id.item_view_3, FileUtil.formatFileSize(item.getSize()))
                .setText(R.id.item_view_4, FileUtil.getFileTypeName(getContext(), item.getName().toStringUtf8()))
                .setText(R.id.item_view_5, item.getUploaderName().toStringUtf8());
        boolean isSelected = selectedMediaId == item.getMediaid();
        Resources resources = getContext().getResources();
        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor)
                .setTextColor(R.id.item_view_3, textColor)
                .setTextColor(R.id.item_view_4, textColor)
                .setTextColor(R.id.item_view_5, textColor);
        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
        View view = holder.getView(R.id.item_root_view);
        view.setBackgroundColor(backgroundColor);
    }

}
