package com.xlk.takstarpaperlessmanage.adapter;

import android.content.res.Resources;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.bean.DeviceMember;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/26.
 * @desc
 */
public class ProAdapter extends BaseQuickAdapter<InterfaceDevice.pbui_Item_DeviceDetailInfo, BaseViewHolder> {
    public ProAdapter(@Nullable List<InterfaceDevice.pbui_Item_DeviceDetailInfo> data) {
        super(R.layout.item_single_view, data);
//        super(R.layout.item_single_button, data);
    }

    List<Integer> checkIds = new ArrayList<>();

    public void check(int devId) {
        if (checkIds.contains(devId)) {
            checkIds.remove(checkIds.indexOf(devId));
        } else {
            checkIds.add(devId);
        }
        notifyDataSetChanged();
    }

    public List<Integer> getCheckedIds() {
        List<Integer> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (checkIds.contains(getData().get(i).getDevcieid())) {
                temps.add(getData().get(i).getDevcieid());
            }
        }
        return temps;
    }

    public void checkAll(boolean all) {
        checkIds.clear();
        if (all) {
            for (int i = 0; i < getData().size(); i++) {
                checkIds.add(getData().get(i).getDevcieid());
            }
        }
        notifyDataSetChanged();
    }

    public boolean isCheckAll() {
        return getCheckedIds().size() == getData().size();
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceDevice.pbui_Item_DeviceDetailInfo item) {
//        Button item_single_btn = holder.getView(R.id.item_single_btn);
//        item_single_btn.setText(item.getDevname().toStringUtf8());
//        boolean isSelected = checkIds.contains(item.getDevcieid());
//        item_single_btn.setSelected(isSelected);

        holder.setText(R.id.item_view_1, item.getDevname().toStringUtf8());
        boolean isSelected = checkIds.contains(item.getDevcieid());
        Resources resources = getContext().getResources();
        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
        holder.setTextColor(R.id.item_view_1, textColor);
        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
        holder.setBackgroundColor(R.id.item_view_1, backgroundColor);
    }
}
