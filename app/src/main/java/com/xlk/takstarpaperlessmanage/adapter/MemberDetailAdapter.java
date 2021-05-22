package com.xlk.takstarpaperlessmanage.adapter;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
public class MemberDetailAdapter extends BaseQuickAdapter<InterfaceMember.pbui_Item_MeetMemberDetailInfo, BaseViewHolder> {
    public MemberDetailAdapter(@Nullable List<InterfaceMember.pbui_Item_MeetMemberDetailInfo> data) {
        super(R.layout.item_member_detail, data);
    }

    List<Integer> ids = new ArrayList<>();

    public void setChoose(int memberId) {
        if (ids.contains(memberId)) {
            ids.remove(ids.indexOf(memberId));
        } else {
            boolean isCan = false;
            for (int i = 0; i < getData().size(); i++) {
                InterfaceMember.pbui_Item_MeetMemberDetailInfo info = getData().get(i);
                if (memberId == info.getMemberid()) {
                    isCan = isCanChoose(info);
                    break;
                }
            }
            if (isCan) {
                ids.add(memberId);
            } else {
                ToastUtils.showShort(R.string.can_not_vote);
            }
        }
        notifyDataSetChanged();
    }


    public List<Integer> getCanChooseIds() {
        List<Integer> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            InterfaceMember.pbui_Item_MeetMemberDetailInfo info = getData().get(i);
            if (isCanChoose(info)) {
                temps.add(info.getMemberid());
            }
        }
        return temps;
    }

    public List<Integer> getSelectedIds() {
        List<Integer> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (ids.contains(getData().get(i).getMemberid())) {
                temps.add(getData().get(i).getMemberid());
            }
        }
        return temps;
    }

    public boolean isChooseAll() {
        return ids.size() == getCanChooseIds().size();
    }

    public void setChooseAll(boolean all) {
        ids.clear();
        if (all) {
            for (int i = 0; i < getData().size(); i++) {
                InterfaceMember.pbui_Item_MeetMemberDetailInfo info = getData().get(i);
                if (isCanChoose(info)) {
                    ids.add(info.getMemberid());
                }
            }
        }
        notifyDataSetChanged();
    }

    private boolean isCanChoose(InterfaceMember.pbui_Item_MeetMemberDetailInfo info) {
        boolean online = info.getMemberdetailflag() == InterfaceMember.Pb_MemberDetailFlag.Pb_MEMBERDETAIL_FLAG_ONLINE_VALUE;
        //在线，已绑定设备，有权限
        return info.getDevid() != 0 && online && Constant.isHasPermission(info.getPermission(), Constant.permission_code_vote);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceMember.pbui_Item_MeetMemberDetailInfo item) {
        String state;
        boolean ishas = Constant.isHasPermission(item.getPermission(), Constant.permission_code_vote);
        boolean isonline = item.getMemberdetailflag() == InterfaceMember.Pb_MemberDetailFlag.Pb_MEMBERDETAIL_FLAG_ONLINE_VALUE;
        boolean isCan = false;
        int facestatus = item.getFacestatus();
        if (item.getDevid() == 0) {
            state = getContext().getString(R.string.not_bind_dev);
        } else {
            if (isonline) {
                state = getContext().getString(R.string.online);
                if (facestatus != InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MemFace_VALUE) {
                    state += " / " + getContext().getString(R.string.not_on_meet);
                }
                if (ishas) {//有权限
                    isCan = true;
                }
            } else {
                state = getContext().getString(R.string.offline);
            }
        }
        LinearLayout item_root_view = holder.getView(R.id.item_root_view);
        item_root_view.setBackgroundColor(Color.TRANSPARENT);

        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getMembername().toStringUtf8())
                .setText(R.id.item_view_3, item.getDevname().toStringUtf8())
                .setText(R.id.item_view_4, state)
                .setText(R.id.item_view_5, ishas ? "√" : getContext().getString(R.string.no_permission));
        boolean isSelected = ids.contains(item.getMemberid());
        CheckBox view = holder.getView(R.id.item_view_1);
        view.setChecked(isSelected);
    }
}
