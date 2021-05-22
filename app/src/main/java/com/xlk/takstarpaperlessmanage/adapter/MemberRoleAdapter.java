package com.xlk.takstarpaperlessmanage.adapter;

import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.bean.MemberRoleBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/20.
 * @desc
 */
public class MemberRoleAdapter extends BaseQuickAdapter<MemberRoleBean, BaseViewHolder> {
    public MemberRoleAdapter(@Nullable List<MemberRoleBean> data) {
        super(R.layout.item_member_role, data);
    }

    List<Integer> selectedIds = new ArrayList<>();

    public void setSelectedId(int memberId) {
        if (selectedIds.contains(memberId)) {
            selectedIds.remove(selectedIds.indexOf(memberId));
        } else {
            selectedIds.add(memberId);
        }
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedIds() {
        List<Integer> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (selectedIds.contains(getData().get(i).getMember().getPersonid())) {
                temps.add(getData().get(i).getMember().getPersonid());
            }
        }
        return temps;
    }

    public List<MemberRoleBean> getSelectedMembers() {
        List<MemberRoleBean> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (selectedIds.contains(getData().get(i).getMember().getPersonid())) {
                temps.add(getData().get(i));
            }
        }
        return temps;
    }

    public void checkAll(boolean all) {
        selectedIds.clear();
        if (all) {
            for (int i = 0; i < getData().size(); i++) {
                MemberRoleBean memberRoleBean = getData().get(i);
                if (memberRoleBean.getSeat() != null) {
                    selectedIds.add(memberRoleBean.getMember().getPersonid());
                }
            }
        }
        notifyDataSetChanged();
    }

    public boolean isCheckAll() {
        int canChooseCount = 0;
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getSeat() != null) {
                canChooseCount++;
            }
        }
        return canChooseCount != 0 && getSelectedIds().size() == canChooseCount;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, MemberRoleBean memberRoleBean) {
        InterfaceMember.pbui_Item_MemberDetailInfo member = memberRoleBean.getMember();
        InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo seat = memberRoleBean.getSeat();
        holder.setText(R.id.item_view_2, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_3, member.getName().toStringUtf8())
                .setText(R.id.item_view_4, member.getCompany().toStringUtf8())
                .setText(R.id.item_view_5, member.getJob().toStringUtf8())
                .setText(R.id.item_view_6, seat != null ? seat.getDevname().toStringUtf8() : "")
                .setText(R.id.item_view_7, seat != null ? Constant.getMemberRoleName(getContext(), seat.getRole()) : "");
        boolean isSelected = selectedIds.contains(member.getPersonid());
        CheckBox item_view_1 = holder.getView(R.id.item_view_1);
        item_view_1.setChecked(isSelected);
    }
}
