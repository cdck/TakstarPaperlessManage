package com.xlk.takstarpaperlessmanage.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.xlk.takstarpaperlessmanage.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/18.
 * @desc
 */
public class ShareFileAdapter extends BaseQuickAdapter<InterfaceFile.pbui_Item_MeetDirFileDetailInfo, BaseViewHolder> {
    public ShareFileAdapter(@Nullable List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> data) {
        super(R.layout.item_picture_file, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceFile.pbui_Item_MeetDirFileDetailInfo pbui_item_meetDirFileDetailInfo) {

    }
}
