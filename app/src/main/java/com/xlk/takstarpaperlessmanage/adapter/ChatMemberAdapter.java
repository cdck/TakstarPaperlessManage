package com.xlk.takstarpaperlessmanage.adapter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.bean.ChatDeviceMember;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/25.
 * @desc
 */
public class ChatMemberAdapter extends BaseQuickAdapter<ChatDeviceMember, BaseViewHolder> {
    private int selectedMemberId = -1;

    public void setSelectedMember(int memberId) {
        selectedMemberId = memberId;
        notifyDataSetChanged();
    }

    public int getSelectedMemberId() {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getMemberDetailInfo().getPersonid() == selectedMemberId) {
                return selectedMemberId;
            }
        }
        return -1;
    }

    public ChatMemberAdapter(@Nullable List<ChatDeviceMember> data) {
        super(R.layout.item_chat_member, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, ChatDeviceMember item) {
        helper.setText(R.id.item_tv_name, getContext().getString(R.string.member_, item.getMemberDetailInfo().getName().toStringUtf8()))
                .setText(R.id.item_tv_job, item.getMemberDetailInfo().getJob().toStringUtf8());
        int count = item.getCount();
        TextView item_tv_count = helper.getView(R.id.item_tv_count);
        LinearLayout item_chat_root = helper.getView(R.id.item_chat_root);
        item_tv_count.setText(String.valueOf(count));
        item_tv_count.setVisibility(count > 0 ? View.VISIBLE : View.INVISIBLE);
        item_chat_root.setSelected(selectedMemberId == item.getMemberDetailInfo().getPersonid());
    }
}
