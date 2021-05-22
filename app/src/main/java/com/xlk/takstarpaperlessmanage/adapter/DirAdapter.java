package com.xlk.takstarpaperlessmanage.adapter;

import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.xlk.takstarpaperlessmanage.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/20.
 * @desc
 */
public class DirAdapter extends BaseQuickAdapter<InterfaceFile.pbui_Item_MeetDirDetailInfo, BaseViewHolder> {
    public DirAdapter(@Nullable List<InterfaceFile.pbui_Item_MeetDirDetailInfo> data) {
        super(R.layout.item_horizontal_room, data);
    }

    private int selectedId = -1;

    public void setSelectedId(int dirId) {
        selectedId = dirId;
        notifyDataSetChanged();
    }

    public int getSelectedId() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getId() == selectedId) {
                return selectedId;
            }
        }
        return -1;
    }

    public InterfaceFile.pbui_Item_MeetDirDetailInfo getSelectedDir() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getId() == selectedId) {
                return getData().get(i);
            }
        }
        return null;
    }


    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceFile.pbui_Item_MeetDirDetailInfo item) {
        Button item_view_1 = holder.getView(R.id.item_view_1);
        item_view_1.setText(item.getName().toStringUtf8());
        item_view_1.setSelected(item.getId() == selectedId);
    }
}
