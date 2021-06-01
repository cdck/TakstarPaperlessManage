package com.xlk.takstarpaperlessmanage.adapter;

import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.bean.DeviceMember;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/25.
 * @desc
 */
public class ScreenMemberAdapter extends BaseQuickAdapter<DeviceMember, BaseViewHolder> {
    List<Integer> ids = new ArrayList<>();
    public ScreenMemberAdapter( @Nullable List<DeviceMember> data) {
        super(R.layout.item_same_screen_member, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, DeviceMember item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition()+1));
        holder.setText(R.id.item_view_2, item.getMemberDetailInfo().getName().toStringUtf8());
        CheckBox view = holder.getView(R.id.item_view_1);
        view.setChecked(ids.contains(item.getDeviceDetailInfo().getDevcieid()));
    }

    public List<Integer> getSelectedDeviceIds() {
        return ids;
    }

    /**
     * 作为单选adapter时才使用
     *
     * @return
     */
    public DeviceMember getChoose() {
        for (int i = 0; i < getData().size(); i++) {
            DeviceMember DeviceMember = getData().get(i);
            if (ids.contains(DeviceMember.getDeviceDetailInfo().getDevcieid())) {
                return DeviceMember;
            }
        }
        return null;
    }

    public List<Integer> getChooseMemberIds() {
        List<Integer> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            DeviceMember DeviceMember = getData().get(i);
            if (ids.contains(DeviceMember.getDeviceDetailInfo().getDevcieid())) {
                temps.add(DeviceMember.getMemberDetailInfo().getPersonid());
            }
        }
        return temps;
    }

    public void notifyChecks() {
        List<Integer> temp = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (ids.contains(getData().get(i).getDeviceDetailInfo().getDevcieid())) {
                temp.add(getData().get(i).getDeviceDetailInfo().getDevcieid());
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
                ids.add(getData().get(i).getDeviceDetailInfo().getDevcieid());
            }
        }
        notifyDataSetChanged();
    }

    public void clearChoose() {
        ids.clear();
    }
}
