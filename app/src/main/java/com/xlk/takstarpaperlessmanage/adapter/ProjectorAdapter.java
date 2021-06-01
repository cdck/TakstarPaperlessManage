package com.xlk.takstarpaperlessmanage.adapter;

import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.xlk.takstarpaperlessmanage.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/25.
 * @desc
 */
public class ProjectorAdapter extends BaseQuickAdapter<InterfaceDevice.pbui_Item_DeviceDetailInfo, BaseViewHolder> {
    public ProjectorAdapter( @Nullable List<InterfaceDevice.pbui_Item_DeviceDetailInfo> data) {
        super(R.layout.item_same_screen_member, data);
    }
    List<Integer> ids = new ArrayList<>();
    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceDevice.pbui_Item_DeviceDetailInfo item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition()+1));
        holder.setText(R.id.item_view_2, item.getDevname().toStringUtf8());
        CheckBox view = holder.getView(R.id.item_view_1);
        view.setChecked(ids.contains(item.getDevcieid()));
    }
    public List<Integer> getChooseIds() {
        return ids;
    }

    public void notifyChecks() {
        List<Integer> temp = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (ids.contains(getData().get(i).getDevcieid())) {
                temp.add(getData().get(i).getDevcieid());
            }
        }
        ids = temp;
        notifyDataSetChanged();
    }

    public void choose(int devId) {
        if (ids.contains(devId)) {
            ids.remove(ids.indexOf(devId));
        } else {
            ids.add(devId);
        }
        notifyDataSetChanged();
    }

    public boolean isChooseAll() {
        return getData().size() == ids.size();
    }

    public void setChooseAll(boolean isAll) {
        ids.clear();
        if (isAll) {
            for (int i = 0; i < getData().size(); i++) {
                ids.add(getData().get(i).getDevcieid());
            }
        }
        notifyDataSetChanged();
    }
}
