package com.xlk.takstarpaperlessmanage.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/11.
 * @desc
 */
public class ClientAdapter extends BaseQuickAdapter<InterfaceDevice.pbui_Item_DeviceDetailInfo, BaseViewHolder> {
    /**
     * 是否隐藏掉第四个控件
     */
    private final boolean hide;
    private List<Integer> selectedIds = new ArrayList<>();

    public ClientAdapter(@Nullable List<InterfaceDevice.pbui_Item_DeviceDetailInfo> data) {
        this(data, false);
    }

    public ClientAdapter(@Nullable List<InterfaceDevice.pbui_Item_DeviceDetailInfo> data, boolean hide) {
        super(R.layout.item_client, data);
        this.hide = hide;
    }


    public void setSelected(int devId) {
        if (selectedIds.contains(devId)) {
            selectedIds.remove(selectedIds.indexOf(devId));
        } else {
            selectedIds.add(devId);
        }
        notifyDataSetChanged();
    }

    public void checkAll(boolean all) {
        selectedIds.clear();
        if (all) {
            for (int i = 0; i < getData().size(); i++) {
                selectedIds.add(getData().get(i).getDevcieid());
            }
        }
        notifyDataSetChanged();
    }

    public boolean isCheckAll() {
        return getData().size() == selectedIds.size();
    }

    public List<Integer> getSelectedIds() {
        List<Integer> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (selectedIds.contains(getData().get(i).getDevcieid())) {
                temps.add(getData().get(i).getDevcieid());
            }
        }
        return temps;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceDevice.pbui_Item_DeviceDetailInfo info) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, info.getDevname().toStringUtf8())
                .setText(R.id.item_view_3, Constant.getDeviceTypeName(getContext(), info.getDevcieid()))
                .setText(R.id.item_view_4, info.getNetstate() == 1
                        ? getContext().getString(R.string.online)
                        : getContext().getString(R.string.offline));

        boolean isSelected = selectedIds.contains(info.getDevcieid());
        int textColor = isSelected ? getContext().getColor(R.color.white) : getContext().getColor(R.color.text_default_color);
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor)
                .setTextColor(R.id.item_view_3, textColor)
                .setTextColor(R.id.item_view_4, textColor);
        int backgroundColor = isSelected ? getContext().getColor(R.color.admin_child_selected_bg) : getContext().getColor(R.color.white);
        holder.setBackgroundColor(R.id.item_view_1, backgroundColor)
                .setBackgroundColor(R.id.item_view_2, backgroundColor)
                .setBackgroundColor(R.id.item_view_3, backgroundColor)
                .setBackgroundColor(R.id.item_view_4, backgroundColor);

        holder.getView(R.id.item_view_4).setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    public void notifySelected() {
        List<Integer> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (selectedIds.contains(getData().get(i).getDevcieid())) {
                temps.add(getData().get(i).getDevcieid());
            }
        }
        selectedIds.clear();
        selectedIds.addAll(temps);
        notifyDataSetChanged();
    }
}
