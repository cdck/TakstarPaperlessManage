package com.xlk.takstarpaperlessmanage.adapter;

import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.bean.MemberDirPermissionBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/20.
 * @desc
 */
public class DirPermissionAdapter extends BaseQuickAdapter<MemberDirPermissionBean, BaseViewHolder> {
    public DirPermissionAdapter( @Nullable List<MemberDirPermissionBean> data) {
        super(R.layout.item_dir_permission, data);
    }
    List<Integer> checks = new ArrayList<>();

    public void setCheck(int id) {
        for (int i = 0; i < getData().size(); i++) {
            MemberDirPermissionBean item = getData().get(i);
            if (item.getMember().getPersonid() == id) {
                item.setBlacklist(!item.isBlacklist());
                break;
            }
        }
        notifyDataSetChanged();
    }

    public List<Integer> getChecks() {
        checks.clear();
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).isBlacklist()) {
                //添加是黑名单的
                checks.add(getData().get(i).getMember().getPersonid());
            }
        }
        return checks;
    }

    public void setCheckAll(boolean all) {
        for (int i = 0; i < getData().size(); i++) {
            MemberDirPermissionBean item = getData().get(i);
            item.setBlacklist(!all);
        }
        notifyDataSetChanged();
    }

    public boolean isCheckAll() {
        int count = 0;
        for (int i = 0; i < getData().size(); i++) {
            MemberDirPermissionBean item = getData().get(i);
            if (!item.isBlacklist()) {
                count++;
            }
        }
        return count != 0 && count == getData().size();
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, MemberDirPermissionBean item) {
        CheckBox view = holder.getView(R.id.item_view_1);
        holder.setText(R.id.item_view_2,item.getMember().getName().toStringUtf8())
                .setText(R.id.item_view_3,item.getMember().getCompany().toStringUtf8())
                .setText(R.id.item_view_4,item.getMember().getJob().toStringUtf8());
        //不是黑名单的进行勾选
        view.setChecked(!item.isBlacklist());
    }
}
