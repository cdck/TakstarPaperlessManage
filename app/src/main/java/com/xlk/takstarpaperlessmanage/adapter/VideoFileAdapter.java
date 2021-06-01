package com.xlk.takstarpaperlessmanage.adapter;

import android.content.res.Resources;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.util.DateUtil;
import com.xlk.takstarpaperlessmanage.util.FileUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/29.
 * @desc
 */
public class VideoFileAdapter extends BaseQuickAdapter<InterfaceFile.pbui_Item_MeetDirFileDetailInfo, BaseViewHolder> {
    public VideoFileAdapter(@Nullable List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> data) {
        super(R.layout.item_video_file, data);
    }

    private int checkId = -1;

    public void check(int mediaId) {
        if (checkId != mediaId) checkId = mediaId;
        else checkId = -1;
        notifyDataSetChanged();
    }

    public InterfaceFile.pbui_Item_MeetDirFileDetailInfo getCheckedVideoFile() {
        for (int i = 0; i < getData().size(); i++) {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = getData().get(i);
            if (item.getMediaid() == checkId) {
                return item;
            }
        }
        return null;
    }


    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceFile.pbui_Item_MeetDirFileDetailInfo item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getName().toStringUtf8())
                .setText(R.id.item_view_3, FileUtil.formatFileSize(item.getSize()))
                .setText(R.id.item_view_4, DateUtil.calculateTime(item.getMstime() / 1000));
        boolean isSelected = checkId == item.getMediaid();
        Resources resources = getContext().getResources();
        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor)
                .setTextColor(R.id.item_view_3, textColor)
                .setTextColor(R.id.item_view_4, textColor);
        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
        holder.setBackgroundColor(R.id.item_root_view, backgroundColor);
//                .setBackgroundColor(R.id.item_view_2, backgroundColor)
//                .setBackgroundColor(R.id.item_view_3, backgroundColor)
//                .setBackgroundColor(R.id.item_view_4, backgroundColor)
//                .setBackgroundColor(R.id.item_view_5, backgroundColor)
//                .setBackgroundColor(R.id.item_view_6, backgroundColor)
//                .setBackgroundColor(R.id.item_view_7, backgroundColor)
//                .setBackgroundColor(R.id.item_view_8, backgroundColor);
    }
}
