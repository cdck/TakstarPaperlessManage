package com.xlk.takstarpaperlessmanage.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.math.MathUtils;
import com.google.protobuf.ByteString;
import com.mogujie.tt.protobuf.InterfaceFilescorevote;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.JniHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
public class FileScoreAdapter extends BaseQuickAdapter<InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore, BaseViewHolder> {
    /**
     * =false评分查看，=true评分管理
     */
    private final boolean isManage;

    public FileScoreAdapter(@Nullable List<InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore> data, boolean isManage) {
        super(R.layout.item_file_score, data);
        this.isManage = isManage;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore item) {
        holder.setText(R.id.item_view_1, String.valueOf(holder.getLayoutPosition() + 1))
                .setText(R.id.item_view_2, item.getContent().toStringUtf8())
                .setText(R.id.item_view_3, JniHelper.getInstance().queryFileNameByMediaId(item.getFileid()))
                .setText(R.id.item_view_4, item.getShouldmembernum() + "/" + item.getRealmembernum())
                .setText(R.id.item_view_5, item.getMode() == 1 ? getContext().getString(R.string.yes) : getContext().getString(R.string.no))
                .setText(R.id.item_view_6, getScore(item))
                .setText(R.id.item_view_7, Constant.getVoteState(getContext(), item.getVotestate()));
        View operation_view_1 = holder.getView(R.id.operation_view_1);
        View operation_view_2 = holder.getView(R.id.operation_view_2);
        operation_view_1.setVisibility(isManage ? View.VISIBLE : View.GONE);
        operation_view_2.setVisibility(isManage ? View.VISIBLE : View.GONE);
    }

    public String getScore(InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore item) {
        List<Integer> itemsumscoreList = item.getItemsumscoreList();
        StringBuilder sb = new StringBuilder();
        double count = 0;
        for (int i : itemsumscoreList) {
            count += i;
            sb.append(i).append("|");
        }
        //总分
        sb.append(count);
        //平均分
        sb.append("|").append(Constant.div(count, itemsumscoreList.size(), 2));
        return sb.toString();
    }


}
