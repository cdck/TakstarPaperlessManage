package com.xlk.takstarpaperlessmanage.adapter;

import android.content.res.Resources;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.util.FileUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/20.
 * @desc
 */
public class DirFileAdapter extends BaseQuickAdapter<InterfaceFile.pbui_Item_MeetDirFileDetailInfo, BaseViewHolder> {
    private final boolean isSort;

    public DirFileAdapter(@Nullable List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> data) {
        this(data, false);
    }

    public DirFileAdapter(@Nullable List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> data, boolean isSort) {
        super(R.layout.item_dir_file, data);
        this.isSort = isSort;
    }

    private int selectedId = -1;

    public void setSelectedId(int id) {
        selectedId = id;
        notifyDataSetChanged();
    }

    public int getSelectedId() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getMediaid() == selectedId) {
                return selectedId;
            }
        }
        return -1;
    }

    public InterfaceFile.pbui_Item_MeetDirFileDetailInfo getSelected() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getMediaid() == selectedId) {
                return getData().get(i);
            }
        }
        return null;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceFile.pbui_Item_MeetDirFileDetailInfo item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, String.valueOf(item.getMediaid()))
                .setText(R.id.item_view_3, item.getName().toStringUtf8())
                .setText(R.id.item_view_4, FileUtil.formatFileSize(item.getSize()))
                .setText(R.id.item_view_5, FileUtil.getFileTypeName(getContext(), item.getName().toStringUtf8()))
                .setText(R.id.item_view_6, item.getUploaderName().toStringUtf8());
        boolean isSelected = selectedId == item.getMediaid();
        Resources resources = getContext().getResources();
        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor)
                .setTextColor(R.id.item_view_3, textColor)
                .setTextColor(R.id.item_view_4, textColor)
                .setTextColor(R.id.item_view_5, textColor)
                .setTextColor(R.id.item_view_6, textColor);
        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
        holder.getView(R.id.item_root_view).setBackgroundColor(backgroundColor);
        holder.getView(R.id.ll_operation_view).setVisibility(isSort ? View.GONE : View.VISIBLE);
//        holder.setBackgroundColor(R.id.item_view_1, backgroundColor)
//                .setBackgroundColor(R.id.item_view_2, backgroundColor)
//                .setBackgroundColor(R.id.item_view_3, backgroundColor)
//                .setBackgroundColor(R.id.item_view_4, backgroundColor)
//                .setBackgroundColor(R.id.item_view_5, backgroundColor)
//                .setBackgroundColor(R.id.item_view_6, backgroundColor);
    }
}
