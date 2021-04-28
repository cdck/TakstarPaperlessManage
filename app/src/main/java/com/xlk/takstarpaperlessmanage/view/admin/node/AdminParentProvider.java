package com.xlk.takstarpaperlessmanage.view.admin.node;

import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.core.view.ViewCompat;

/**
 * @author Created by xlk on 2021/4/20.
 * @desc
 */
public class AdminParentProvider extends BaseNodeProvider {
    @Override
    public int getItemViewType() {
        return AdminNodeAdapter.NODE_TYPE_PARENT;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_admin_parent;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, BaseNode node) {
        AdminParentNode parentNode = (AdminParentNode) node;
        TextView item_tv_name = baseViewHolder.getView(R.id.item_tv_name);
        int id = parentNode.getId();
        switch (id) {
            //系统设置
            case Constant.admin_system_settings: {
                item_tv_name.setText(R.string.system_settings);
                break;
            }
            //会议预约
            case Constant.admin_meeting_reservation: {
                item_tv_name.setText(R.string.meeting_reservation);
                break;
            }
            //会前设置
            case Constant.admin_before_meeting: {
                item_tv_name.setText(R.string.before_meeting);
                break;
            }
            //会中管理
            case Constant.admin_current_meeting: {
                item_tv_name.setText(R.string.current_meeting);
                break;
            }
            //会后查看
            case Constant.admin_after_meeting: {
                item_tv_name.setText(R.string.after_meeting);
                break;
            }
            default:
                break;
        }
        setArrowSpin(baseViewHolder, node, false);
    }


    @Override
    public void convert(@NotNull BaseViewHolder helper, @NotNull BaseNode data, @NotNull List<?> payloads) {
        for (Object payload : payloads) {
            if (payload instanceof Integer && (int) payload == AdminNodeAdapter.EXPAND_COLLAPSE_PAYLOAD) {
                // 增量刷新，使用动画变化箭头
                setArrowSpin(helper, data, true);
            }
        }
    }

    private void setArrowSpin(BaseViewHolder helper, BaseNode data, boolean isAnimate) {
        AdminParentNode entity = (AdminParentNode) data;
        ImageView imageView = helper.getView(R.id.item_iv_expand);
        if (entity.isExpanded()) {
            if (isAnimate) {
                ViewCompat.animate(imageView).setDuration(200)
                        .setInterpolator(new DecelerateInterpolator())
                        .rotation(180f)
                        .start();
            } else {
                imageView.setRotation(180f);
            }
        } else {
            if (isAnimate) {
                ViewCompat.animate(imageView).setDuration(200)
                        .setInterpolator(new DecelerateInterpolator())
                        .rotation(0f)
                        .start();
            } else {
                imageView.setRotation(0f);
            }
        }
    }

    @Override
    public void onClick(@NotNull BaseViewHolder helper, @NotNull View view, BaseNode data, int position) {
        // 这里使用payload进行增量刷新（避免整个item刷新导致的闪烁，不自然）
        if (getAdapter() != null) {
            getAdapter().expandOrCollapse(position, true, true, AdminNodeAdapter.EXPAND_COLLAPSE_PAYLOAD);
        } else {
            LogUtils.e("onClick 方法中AdminNodeAdapter为null");
        }
    }
}
