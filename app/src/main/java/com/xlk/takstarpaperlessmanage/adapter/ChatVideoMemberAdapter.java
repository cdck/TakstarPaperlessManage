package com.xlk.takstarpaperlessmanage.adapter;

import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.bean.ChatDeviceMember;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/25.
 * @desc
 */
public class ChatVideoMemberAdapter extends BaseQuickAdapter<ChatDeviceMember, BaseViewHolder> {
    List<Integer> selectedMemberIds = new ArrayList<>();

    public ChatVideoMemberAdapter(@Nullable List<ChatDeviceMember> data) {
        super(R.layout.item_chat_video_member, data);
    }

    public void check(int memberId) {
        if (selectedMemberIds.contains(memberId)) {
            selectedMemberIds.remove(selectedMemberIds.indexOf(memberId));
        } else {
            selectedMemberIds.add(memberId);
        }
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedMemberIds() {
        List<Integer> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (selectedMemberIds.contains(getData().get(i).getMemberDetailInfo().getPersonid())) {
                temps.add(getData().get(i).getMemberDetailInfo().getPersonid());
            }
        }
        return temps;
    }

    public void checkAll(boolean all) {
        selectedMemberIds.clear();
        if (all) {
            for (int i = 0; i < getData().size(); i++) {
                selectedMemberIds.add(getData().get(i).getMemberDetailInfo().getPersonid());
            }
        }
        notifyDataSetChanged();
    }

    public boolean isCheckedAll() {
        return getSelectedMemberIds().size() == getData().size();
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, ChatDeviceMember item) {
        CheckBox view = holder.getView(R.id.item_view_1);
        view.setText(item.getMemberDetailInfo().getName().toStringUtf8());
        view.setChecked(selectedMemberIds.contains(item.getMemberDetailInfo().getPersonid()));
    }
}
