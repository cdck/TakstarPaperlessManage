package com.xlk.takstarpaperlessmanage.adapter;

import android.content.res.Resources;
import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.bean.DevControlBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/24.
 * @desc
 */
public class DeviceControlAdapter extends BaseQuickAdapter<DevControlBean, BaseViewHolder> {
    public DeviceControlAdapter(@Nullable List<DevControlBean> data) {
        super(R.layout.item_device_control, data);
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

    public List<Integer> getCheckIds() {
        List<Integer> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (checkIds.contains(getData().get(i).getDeviceInfo().getDevcieid())) {
                temps.add(getData().get(i).getDeviceInfo().getDevcieid());
            }
        }
        return temps;
    }

    public List<DevControlBean> getCheckedDevice() {
        List<DevControlBean> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            DevControlBean devControlBean = getData().get(i);
            InterfaceDevice.pbui_Item_DeviceDetailInfo dev = devControlBean.getDeviceInfo();
            if (checkIds.contains(dev.getDevcieid())) {
                temps.add(devControlBean);
            }
        }
        return temps;
    }

    public void checkAll(boolean all) {
        checkIds.clear();
        if (all) {
            for (int i = 0; i < getData().size(); i++) {
                checkIds.add(getData().get(i).getDeviceInfo().getDevcieid());
            }
        }
        notifyDataSetChanged();
    }

    public boolean isCheckAll() {
        return getCheckIds().size() == getData().size();
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, DevControlBean item) {
        InterfaceDevice.pbui_Item_DeviceDetailInfo dev = item.getDeviceInfo();
        boolean online = dev.getNetstate() == 1;
        InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo seatInfo = item.getSeatInfo();
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, String.valueOf(dev.getDevcieid()))
                .setText(R.id.item_view_3, dev.getDevname().toStringUtf8())
                .setText(R.id.item_view_4, Constant.getDeviceTypeName(getContext(), dev.getDevcieid()))
                .setText(R.id.item_view_5, isOut(dev.getDeviceflag()) ? "√" : "")
                .setText(R.id.item_view_6, dev.getIpinfoList().get(0).getIp().toStringUtf8())
                .setText(R.id.item_view_7, online ? getContext().getString(R.string.online) : getContext().getString(R.string.offline))
                .setText(R.id.item_view_8, seatInfo != null ? seatInfo.getMembername().toStringUtf8() : "")
                .setText(R.id.item_view_9, Constant.getInterfaceStateName(getContext(), dev.getFacestate()));
        boolean isSelected = checkIds.contains(dev.getDevcieid());
        CheckBox view = holder.getView(R.id.item_view_1);
        view.setChecked(isSelected);
    }


    public boolean isOut(int flag) {
        return InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_OPENOUTSIDE_VALUE == (flag & InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_OPENOUTSIDE_VALUE);
    }
}
