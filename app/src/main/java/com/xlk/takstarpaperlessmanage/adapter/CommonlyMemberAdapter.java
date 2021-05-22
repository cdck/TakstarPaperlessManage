package com.xlk.takstarpaperlessmanage.adapter;

import android.content.res.Resources;
import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfacePerson;
import com.xlk.takstarpaperlessmanage.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/19.
 * @desc
 */
public class CommonlyMemberAdapter extends BaseQuickAdapter<InterfacePerson.pbui_Item_PersonDetailInfo, BaseViewHolder> {
    List<Integer> selectedIds = new ArrayList<>();

    public CommonlyMemberAdapter(@Nullable List<InterfacePerson.pbui_Item_PersonDetailInfo> data) {
        super(R.layout.item_commonly_member, data);
    }

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
            if (selectedIds.contains(getData().get(i).getPersonid())) {
                temps.add(getData().get(i).getPersonid());
            }
        }
        return temps;
    }

    public List<InterfacePerson.pbui_Item_PersonDetailInfo> getSelectedMembers() {
        List<InterfacePerson.pbui_Item_PersonDetailInfo> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (selectedIds.contains(getData().get(i).getPersonid())) {
                temps.add(getData().get(i));
            }
        }
        return temps;
    }

    public void checkAll(boolean all) {
        selectedIds.clear();
        if (all) {
            for (int i = 0; i < getData().size(); i++) {
                selectedIds.add(getData().get(i).getPersonid());
            }
        }
        notifyDataSetChanged();
    }

    public boolean isCheckAll() {
        return getSelectedIds().size() == getData().size();
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfacePerson.pbui_Item_PersonDetailInfo item) {
        holder.setText(R.id.item_view_2, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_3, item.getName().toStringUtf8())
                .setText(R.id.item_view_4, item.getCompany().toStringUtf8())
                .setText(R.id.item_view_5, item.getJob().toStringUtf8())
                .setText(R.id.item_view_6, item.getPhone().toStringUtf8())
                .setText(R.id.item_view_7, item.getEmail().toStringUtf8())
                .setText(R.id.item_view_8, item.getPassword().toStringUtf8())
                .setText(R.id.item_view_9, item.getComment().toStringUtf8());
        boolean isSelected = selectedIds.contains(item.getPersonid());
        CheckBox item_view_1 = holder.getView(R.id.item_view_1);
        item_view_1.setChecked(isSelected);
//        Resources resources = getContext().getResources();
//        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
//        holder.setTextColor(R.id.item_view_1, textColor)
//                .setTextColor(R.id.item_view_2, textColor)
//                .setTextColor(R.id.item_view_3, textColor)
//                .setTextColor(R.id.item_view_4, textColor)
//                .setTextColor(R.id.item_view_5, textColor)
//                .setTextColor(R.id.item_view_6, textColor)
//                .setTextColor(R.id.item_view_7, textColor)
//                .setTextColor(R.id.item_view_8, textColor);
//        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
//        holder.setBackgroundColor(R.id.item_view_1, backgroundColor)
//                .setBackgroundColor(R.id.item_view_2, backgroundColor)
//                .setBackgroundColor(R.id.item_view_3, backgroundColor)
//                .setBackgroundColor(R.id.item_view_4, backgroundColor)
//                .setBackgroundColor(R.id.item_view_5, backgroundColor)
//                .setBackgroundColor(R.id.item_view_6, backgroundColor)
//                .setBackgroundColor(R.id.item_view_7, backgroundColor)
//                .setBackgroundColor(R.id.item_view_8, backgroundColor);
    }
}
