package com.xlk.takstarpaperlessmanage.adapter;

import android.content.res.Resources;

import com.blankj.utilcode.util.FileUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.bean.ScoreFileBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/25.
 * @desc
 */
public class UploadFileAdapter extends BaseQuickAdapter<ScoreFileBean, BaseViewHolder> {
    List<String> selectedFiles = new ArrayList<>();

    public UploadFileAdapter(@Nullable List<ScoreFileBean> data) {
        super(R.layout.item_upload_file, data);
    }

    public void choose(String filepath) {
        if (selectedFiles.contains(filepath)) {
            selectedFiles.remove(selectedFiles.indexOf(filepath));
        } else {
            selectedFiles.add(filepath);
        }
        notifyDataSetChanged();
    }

    public List<ScoreFileBean> getSelectedFiles() {
        List<ScoreFileBean> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (selectedFiles.contains(getData().get(i))) {
                temps.add(getData().get(i));
            }
        }
        return temps;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, ScoreFileBean bean) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, FileUtils.getFileName(bean.getFilePath()));
        boolean isSelected = selectedFiles.contains(bean.getFilePath());
        Resources resources = getContext().getResources();
        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor);
        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
        holder.setBackgroundColor(R.id.item_view_1, backgroundColor)
                .setBackgroundColor(R.id.item_view_2, backgroundColor);
    }
}
