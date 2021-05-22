package com.xlk.takstarpaperlessmanage.adapter;

import android.content.res.Resources;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceAgenda;
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
public class AgendaAdapter extends BaseQuickAdapter<InterfaceAgenda.pbui_ItemAgendaTimeInfo, BaseViewHolder> {
    private int selectedId = -1;

    public AgendaAdapter(@Nullable List<InterfaceAgenda.pbui_ItemAgendaTimeInfo> data) {
        super(R.layout.item_agenda, data);
    }

    public void setSelectedId(int agendaId) {
        selectedId = agendaId;
        notifyDataSetChanged();
    }

    public int getSelectedId() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getAgendaid() == selectedId) {
                return selectedId;
            }
        }
        return -1;
    }

    public InterfaceAgenda.pbui_ItemAgendaTimeInfo getSelectedAgenda() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getAgendaid() == selectedId) {
                return getData().get(i);
            }
        }
        return null;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceAgenda.pbui_ItemAgendaTimeInfo item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getDesctext().toStringUtf8())
                .setText(R.id.item_view_3, DateUtil.secondsFormat(item.getStartutctime(), "yyyy/MM/dd HH:mm"))
                .setText(R.id.item_view_4, DateUtil.secondsFormat(item.getEndutctime(), "yyyy/MM/dd HH:mm"))
        ;
        boolean isSelected = item.getAgendaid() == selectedId;
        Resources resources = getContext().getResources();
        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor)
                .setTextColor(R.id.item_view_3, textColor)
                .setTextColor(R.id.item_view_4, textColor);
        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
        holder.setBackgroundColor(R.id.item_view_1, backgroundColor)
                .setBackgroundColor(R.id.item_view_2, backgroundColor)
                .setBackgroundColor(R.id.item_view_3, backgroundColor)
                .setBackgroundColor(R.id.item_view_4, backgroundColor);
    }
}
