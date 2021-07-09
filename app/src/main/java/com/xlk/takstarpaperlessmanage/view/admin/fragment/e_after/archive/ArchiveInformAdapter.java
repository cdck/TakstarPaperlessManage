package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.archive;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.takstarpaperlessmanage.R;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author Created by xlk on 2020/10/28.
 * @desc
 */
public class ArchiveInformAdapter extends BaseQuickAdapter<ArchiveInform, BaseViewHolder> {
    public ArchiveInformAdapter(int layoutResId, @Nullable List<ArchiveInform> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ArchiveInform item) {
        helper.setText(R.id.item_view_1, item.getContent())
                .setText(R.id.item_view_2, item.getResult());

        int textColor = getContext().getColor(R.color.text_color_black);
        helper.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor);

        int backgroundColor = getContext().getColor(R.color.white);
        helper.setBackgroundColor(R.id.item_view_1, backgroundColor)
                .setBackgroundColor(R.id.item_view_2, backgroundColor);
    }
}
