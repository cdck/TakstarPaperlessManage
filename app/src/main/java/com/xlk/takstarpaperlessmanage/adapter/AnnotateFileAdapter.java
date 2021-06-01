package com.xlk.takstarpaperlessmanage.adapter;

import android.content.res.Resources;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.util.FileUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/27.
 * @desc
 */
public class AnnotateFileAdapter extends BaseQuickAdapter<InterfaceFile.pbui_Item_MeetDirFileDetailInfo, BaseViewHolder> {
    List<Integer> checkIds = new ArrayList<>();

    public AnnotateFileAdapter(@Nullable List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> data) {
        super(R.layout.item_annotate_file, data);
    }

    public void check(int mediaId) {
        if (checkIds.contains(mediaId)) {
            checkIds.remove(checkIds.indexOf(mediaId));
        } else {
            checkIds.add(mediaId);
        }
        notifyDataSetChanged();
    }

    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> getCheckedFiles() {
        List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (checkIds.contains(getData().get(i).getMediaid())) {
                temps.add(getData().get(i));
            }
        }
        return temps;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceFile.pbui_Item_MeetDirFileDetailInfo item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getName().toStringUtf8())
                .setText(R.id.item_view_3, FileUtil.formatFileSize(item.getSize()))
                .setText(R.id.item_view_4, FileUtil.getFileTypeName(getContext(), item.getName().toStringUtf8()))
                .setText(R.id.item_view_5, item.getUploaderName().toStringUtf8());
        boolean isSelected = checkIds.contains(item.getMediaid());
        Resources resources = getContext().getResources();
        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor)
                .setTextColor(R.id.item_view_3, textColor)
                .setTextColor(R.id.item_view_4, textColor)
                .setTextColor(R.id.item_view_5, textColor);
        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
        holder.setBackgroundColor(R.id.item_root_view, backgroundColor);
    }
}
