package com.xlk.takstarpaperlessmanage.adapter;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.util.DateUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/18.
 * @desc
 */
public class MeetingManageAdapter extends BaseQuickAdapter<InterfaceMeet.pbui_Item_MeetMeetInfo, BaseViewHolder> {
    private int selectedId = -1;

    public MeetingManageAdapter(@Nullable List<InterfaceMeet.pbui_Item_MeetMeetInfo> data) {
        super(R.layout.item_meeting_manage, data);
    }

    public void setSelectedId(int meetId) {
        selectedId = meetId;
        notifyDataSetChanged();
    }

    public int getSelectedMeetingId() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getId() == selectedId) {
                return selectedId;
            }
        }
        return -1;
    }

    public InterfaceMeet.pbui_Item_MeetMeetInfo getSelectedMeeting() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getId() == selectedId) {
                return getData().get(i);
            }
        }
        return null;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceMeet.pbui_Item_MeetMeetInfo item) {
        int status = item.getStatus();
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getName().toStringUtf8())
                .setText(R.id.item_view_3, Constant.getMeetingStatus(getContext(), status))
                .setText(R.id.item_view_4, item.getRoomname().toStringUtf8())
                .setText(R.id.item_view_5, item.getSecrecy() == 1 ? getContext().getString(R.string.yes) : getContext().getString(R.string.no))
                .setText(R.id.item_view_6, DateUtil.secondsFormat(item.getStartTime(), "yyyy/MM/dd") + "\n" + DateUtil.secondsFormat(item.getStartTime(), "HH:mm"))
                .setText(R.id.item_view_7, DateUtil.secondsFormat(item.getEndTime(), "yyyy/MM/dd") + "\n" + DateUtil.secondsFormat(item.getEndTime(), "HH:mm"))
                .setText(R.id.item_view_8, Constant.getMeetSignInTypeName(item.getSigninType()))
                .setText(R.id.item_view_9, item.getOrdername().toStringUtf8())
        ;
        TextView operation_view_1 = holder.getView(R.id.operation_view_1);
        TextView operation_view_2 = holder.getView(R.id.operation_view_2);
        operation_view_1.setVisibility(View.INVISIBLE);
        operation_view_2.setVisibility(View.INVISIBLE);
        String text = "";
        Drawable drawable = null;
        switch (status) {
            case InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_PAUSE_VALUE:
            case InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_Ready_VALUE: {
                operation_view_1.setVisibility(View.VISIBLE);
                operation_view_2.setVisibility(View.VISIBLE);
                text = getContext().getString(R.string.start);
                drawable = getContext().getResources().getDrawable(R.drawable.ic_start);
                break;
            }
            case InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_Start_VALUE: {
                operation_view_1.setVisibility(View.VISIBLE);
                operation_view_2.setVisibility(View.VISIBLE);
                text = getContext().getString(R.string.pause);
                drawable = getContext().getResources().getDrawable(R.drawable.ic_pause);
                break;
            }
            case InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_End_VALUE: {
                operation_view_1.setVisibility(View.VISIBLE);
                operation_view_2.setVisibility(View.VISIBLE);
                text = getContext().getString(R.string.delete);
                drawable = getContext().getResources().getDrawable(R.drawable.ic_delete);
                break;
            }
        }
        operation_view_1.setText(text);
        if (drawable != null) {
            operation_view_1.setText(text);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            operation_view_1.setCompoundDrawables(drawable, null, null, null);
        }

        boolean isSelected = item.getId() == selectedId;
        Resources resources = getContext().getResources();

        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor)
                .setTextColor(R.id.item_view_3, textColor)
                .setTextColor(R.id.item_view_4, textColor)
                .setTextColor(R.id.item_view_5, textColor)
                .setTextColor(R.id.item_view_6, textColor)
                .setTextColor(R.id.item_view_7, textColor)
                .setTextColor(R.id.item_view_8, textColor)
                .setTextColor(R.id.item_view_9, textColor);
        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
        holder.setBackgroundColor(R.id.item_view_1, backgroundColor)
                .setBackgroundColor(R.id.item_view_2, backgroundColor)
                .setBackgroundColor(R.id.item_view_3, backgroundColor)
                .setBackgroundColor(R.id.item_view_4, backgroundColor)
                .setBackgroundColor(R.id.item_view_5, backgroundColor)
                .setBackgroundColor(R.id.item_view_6, backgroundColor)
                .setBackgroundColor(R.id.item_view_7, backgroundColor)
                .setBackgroundColor(R.id.item_view_8, backgroundColor)
                .setBackgroundColor(R.id.item_view_9, backgroundColor);
    }
}
