package com.xlk.takstarpaperlessmanage.adapter;

import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfacePerson;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.bean.MemberPermissionBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/19.
 * @desc
 */
public class MemberPermissionAdapter extends BaseQuickAdapter<MemberPermissionBean, BaseViewHolder> {
    public MemberPermissionAdapter(@Nullable List<MemberPermissionBean> data) {
        super(R.layout.item_member_permission, data);
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
            if (selectedIds.contains(getData().get(i).getMemberId())) {
                temps.add(getData().get(i).getMemberId());
            }
        }
        return temps;
    }

    public List<MemberPermissionBean> getSelectedMembers() {
        List<MemberPermissionBean> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (selectedIds.contains(getData().get(i).getMemberId())) {
                temps.add(getData().get(i));
            }
        }
        return temps;
    }

    public void checkAll(boolean all) {
        selectedIds.clear();
        if (all) {
            for (int i = 0; i < getData().size(); i++) {
                selectedIds.add(getData().get(i).getMemberId());
            }
        }
        notifyDataSetChanged();
    }

    public boolean isCheckAll() {
        return getSelectedIds().size() == getData().size();
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, MemberPermissionBean item) {
        holder.setText(R.id.item_view_2, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_3, item.getName())
                .setText(R.id.item_view_4, Constant.isHasPermission(item.getPermission(), Constant.permission_code_screen) ? "√" : "")
                .setText(R.id.item_view_5, Constant.isHasPermission(item.getPermission(), Constant.permission_code_projection) ? "√" : "")
                .setText(R.id.item_view_6, Constant.isHasPermission(item.getPermission(), Constant.permission_code_upload) ? "√" : "")
                .setText(R.id.item_view_7, Constant.isHasPermission(item.getPermission(), Constant.permission_code_download) ? "√" : "")
                .setText(R.id.item_view_8, Constant.isHasPermission(item.getPermission(), Constant.permission_code_vote) ? "√" : "");
        boolean isSelected = selectedIds.contains(item.getMemberId());
        CheckBox item_view_1 = holder.getView(R.id.item_view_1);
        item_view_1.setChecked(isSelected);
    }
}
