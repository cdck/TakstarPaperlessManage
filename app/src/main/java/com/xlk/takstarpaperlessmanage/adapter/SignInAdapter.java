package com.xlk.takstarpaperlessmanage.adapter;

import android.content.res.Resources;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mogujie.tt.protobuf.InterfaceSignin;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.bean.SignInBean;
import com.xlk.takstarpaperlessmanage.util.DateUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/27.
 * @desc
 */
public class SignInAdapter extends BaseQuickAdapter<SignInBean, BaseViewHolder> {
    public SignInAdapter(@Nullable List<SignInBean> data) {
        super(R.layout.item_sign, data);
    }

    List<Integer> checkIds = new ArrayList<>();

    public void check(int memberId) {
        if (checkIds.contains(memberId)) {
            checkIds.remove(checkIds.indexOf(memberId));
        } else {
            checkIds.add(memberId);
        }
        notifyDataSetChanged();
    }

    public List<Integer> getCheckIds() {
        List<Integer> temps = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (checkIds.contains(getData().get(i).getMember().getPersonid())) {
                temps.add(getData().get(i).getMember().getPersonid());
            }
        }
        return temps;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, SignInBean item) {
        InterfaceSignin.pbui_Item_MeetSignInDetailInfo sign = item.getSign();
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getMember().getName().toStringUtf8())
                .setText(R.id.item_view_3, sign != null ? DateUtil.secondsFormat(sign.getUtcseconds(), "yyyy/MM/dd HH:mm") : "")
                .setText(R.id.item_view_4, sign != null ?
                        getContext().getString(R.string.already_checked_in) :
                        getContext().getString(R.string.uncheckin))
                .setText(R.id.item_view_5, sign != null ? Constant.getMeetSignInTypeName(sign.getSigninType()) : "");
        boolean isSelected = checkIds.contains(item.getMember().getPersonid());
        Resources resources = getContext().getResources();
        int textColor = isSelected ? resources.getColor(R.color.white) : resources.getColor(R.color.text_default_color);
        holder.setTextColor(R.id.item_view_1, textColor)
                .setTextColor(R.id.item_view_2, textColor)
                .setTextColor(R.id.item_view_3, textColor)
                .setTextColor(R.id.item_view_4, textColor)
                .setTextColor(R.id.item_view_5, textColor);
        int backgroundColor = isSelected ? resources.getColor(R.color.admin_child_selected_bg) : resources.getColor(R.color.white);
        holder.setBackgroundColor(R.id.item_view_1, backgroundColor)
                .setBackgroundColor(R.id.item_view_2, backgroundColor)
                .setBackgroundColor(R.id.item_view_3, backgroundColor)
                .setBackgroundColor(R.id.item_view_4, backgroundColor)
                .setBackgroundColor(R.id.item_view_5, backgroundColor);
    }
}
