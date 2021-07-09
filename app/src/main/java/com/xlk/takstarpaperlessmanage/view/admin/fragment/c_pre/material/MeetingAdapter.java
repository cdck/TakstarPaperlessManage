package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.material;

import android.content.res.Resources;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.xlk.takstarpaperlessmanage.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/7/3.
 * @desc
 */
public class MeetingAdapter extends BaseQuickAdapter<InterfaceMeet.pbui_Item_MeetMeetInfo, BaseViewHolder> {
    private int selectedMeetingId = -1;

    public MeetingAdapter(int layoutResId, @Nullable List<InterfaceMeet.pbui_Item_MeetMeetInfo> data) {
        super(layoutResId, data);
    }

    public void choose(int meetingId) {
        selectedMeetingId = meetingId;
        notifyDataSetChanged();
    }

    public int getSelectedMeetingId() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getId() == selectedMeetingId) {
                return selectedMeetingId;
            }
        }
        return -1;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceMeet.pbui_Item_MeetMeetInfo item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getName().toStringUtf8());
        boolean isSelected = selectedMeetingId == item.getId();
        Resources resources = getContext().getResources();
        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor);
        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
        View view = holder.getView(R.id.item_root_view);
        view.setBackgroundColor(backgroundColor);
    }
}
