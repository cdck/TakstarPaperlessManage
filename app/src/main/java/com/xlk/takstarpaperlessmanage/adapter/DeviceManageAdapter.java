package com.xlk.takstarpaperlessmanage.adapter;

import android.content.res.Resources;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/6.
 * @desc 系统设置-设备管理-设备列表
 */
public class DeviceManageAdapter extends BaseQuickAdapter<InterfaceDevice.pbui_Item_DeviceDetailInfo, BaseViewHolder> {
    private int selectedId = -1;

    public DeviceManageAdapter(@Nullable List<InterfaceDevice.pbui_Item_DeviceDetailInfo> data) {
        super(R.layout.item_device_manage, data);
    }

    public void setSelected(int id) {
        if (selectedId == id) selectedId = -1;
        else selectedId = id;
        notifyDataSetChanged();
    }

    public int getSelectedId() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getDevcieid() == selectedId) {
                return selectedId;
            }
        }
        return -1;
    }

    public InterfaceDevice.pbui_Item_DeviceDetailInfo getSelectedDevice() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getDevcieid() == selectedId) {
                return getData().get(i);
            }
        }
        return null;
    }


    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceDevice.pbui_Item_DeviceDetailInfo item) {
        List<InterfaceDevice.pbui_SubItem_DeviceIpAddrInfo> ipinfoList = item.getIpinfoList();
        int deviceflag = item.getDeviceflag();
        boolean isGuestMode = (deviceflag & InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_GUESTMODE_VALUE) == InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_GUESTMODE_VALUE;
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, String.valueOf(item.getDevcieid()))
                .setText(R.id.item_view_3, item.getDevname().toStringUtf8())
                .setText(R.id.item_view_4, Constant.getDeviceTypeName(getContext(), item.getDevcieid()))
                .setText(R.id.item_view_5, item.getHardversion() + "." + item.getSoftversion())
                .setText(R.id.item_view_6, ipinfoList != null ? ipinfoList.get(0).getIp().toStringUtf8() : "")
                .setText(R.id.item_view_7, ipinfoList != null ? ipinfoList.get(0).getPort() + "" : "")
                .setText(R.id.item_view_8, isGuestMode ? getContext().getString(R.string.on) : getContext().getString(R.string.off))
                .setText(R.id.item_view_9, item.getNetstate() == 1 ? getContext().getString(R.string.online) : getContext().getString(R.string.offline))
                .setText(R.id.item_view_10, Constant.getInterfaceStateName(getContext(), item.getFacestate()))
                .setText(R.id.item_view_11, String.valueOf(item.getLiftgroupres0()))
                .setText(R.id.item_view_12, String.valueOf(item.getLiftgroupres1()))
        ;
        boolean isSelected = item.getDevcieid() == selectedId;
        Resources resources = getContext().getResources();
//        View item_view_root = holder.getView(R.id.item_view_root);
//        item_view_root.setBackgroundColor(bgColor);

        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor)
                .setTextColor(R.id.item_view_3, textColor)
                .setTextColor(R.id.item_view_4, textColor)
                .setTextColor(R.id.item_view_5, textColor)
                .setTextColor(R.id.item_view_6, textColor)
                .setTextColor(R.id.item_view_7, textColor)
                .setTextColor(R.id.item_view_8, textColor)
                .setTextColor(R.id.item_view_9, textColor)
                .setTextColor(R.id.item_view_10, textColor)
                .setTextColor(R.id.item_view_11, textColor)
                .setTextColor(R.id.item_view_12, textColor);
        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
        holder.setBackgroundColor(R.id.item_view_1, backgroundColor)
                .setBackgroundColor(R.id.item_view_2, backgroundColor)
                .setBackgroundColor(R.id.item_view_3, backgroundColor)
                .setBackgroundColor(R.id.item_view_4, backgroundColor)
                .setBackgroundColor(R.id.item_view_5, backgroundColor)
                .setBackgroundColor(R.id.item_view_6, backgroundColor)
                .setBackgroundColor(R.id.item_view_7, backgroundColor)
                .setBackgroundColor(R.id.item_view_8, backgroundColor)
                .setBackgroundColor(R.id.item_view_9, backgroundColor)
                .setBackgroundColor(R.id.item_view_10, backgroundColor)
                .setBackgroundColor(R.id.item_view_11, backgroundColor)
                .setBackgroundColor(R.id.item_view_12, backgroundColor);
    }
}
