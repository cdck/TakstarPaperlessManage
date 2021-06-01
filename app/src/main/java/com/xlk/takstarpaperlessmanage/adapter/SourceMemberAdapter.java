package com.xlk.takstarpaperlessmanage.adapter;

import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.bean.DeviceMember;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/26.
 * @desc
 */
public class SourceMemberAdapter extends BaseQuickAdapter<DeviceMember, BaseViewHolder> {
    List<Integer> checkIds = new ArrayList<>();
    private final boolean isSingle;

    public SourceMemberAdapter(@Nullable List<DeviceMember> data, boolean isSingle) {
        super(R.layout.item_single_button, data);
        this.isSingle = isSingle;
    }

    public void check(int devId) {
        if (checkIds.contains(devId)) {
            checkIds.remove(checkIds.indexOf(devId));
        } else {
            if (isSingle) {
                checkIds.clear();
            }
            checkIds.add(devId);
        }
        notifyDataSetChanged();
    }

    public List<Integer> getCheckedIds() {
        List<Integer> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            DeviceMember deviceMember = getData().get(i);
            if (checkIds.contains(deviceMember.getDeviceDetailInfo().getDevcieid())) {
                temps.add(deviceMember.getDeviceDetailInfo().getDevcieid());
            }
        }
        return temps;
    }

    public void checkAll(boolean all) {
        checkIds.clear();
        if (all) {
            for (int i = 0; i < getData().size(); i++) {
                checkIds.add(getData().get(i).getDeviceDetailInfo().getDevcieid());
            }
        }
        notifyDataSetChanged();
    }
    public boolean isCheckAll(){
        return getCheckedIds().size()==getData().size();
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, DeviceMember item) {
        InterfaceDevice.pbui_Item_DeviceDetailInfo dev = item.getDeviceDetailInfo();
        InterfaceMember.pbui_Item_MemberDetailInfo member = item.getMemberDetailInfo();
        Button item_single_btn = holder.getView(R.id.item_single_btn);
        item_single_btn.setText(member.getName().toStringUtf8());
        boolean isSelected = checkIds.contains(dev.getDevcieid());
        item_single_btn.setSelected(isSelected);
    }
}
