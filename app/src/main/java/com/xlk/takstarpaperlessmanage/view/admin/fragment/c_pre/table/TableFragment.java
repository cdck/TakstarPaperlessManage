package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.table;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.blankj.utilcode.util.LogUtils;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceTablecard;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.ui.ColorPickerDialog;
import com.xlk.takstarpaperlessmanage.ui.table.TableCardBean;
import com.xlk.takstarpaperlessmanage.ui.table.TableCardView;
import com.xlk.takstarpaperlessmanage.util.AfterTextWatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/22.
 * @desc
 */
public class TableFragment extends BaseFragment<TablePresenter> implements TableContract.View, AdapterView.OnItemSelectedListener {
    private LinearLayout rootView;
    private TableCardView tableCardView;
    private Spinner spFont;
    private EditText edtSize;
    private Spinner spBold;
    private Spinner spAlign;
    private Spinner spHide;
    private ImageView ivColor;
    private EditText edtHeight;
    List<TableCardBean> datas = new ArrayList<>();
    private int touchIndex;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_table_card;
    }

    @Override
    protected void initView(View inflate) {
        rootView = (LinearLayout) inflate.findViewById(R.id.root_view);
        tableCardView = (TableCardView) inflate.findViewById(R.id.table_card_view);
        spFont = (Spinner) inflate.findViewById(R.id.sp_font);
        edtSize = (EditText) inflate.findViewById(R.id.edt_size);
        spBold = (Spinner) inflate.findViewById(R.id.sp_bold);
        spAlign = (Spinner) inflate.findViewById(R.id.sp_align);
        spHide = (Spinner) inflate.findViewById(R.id.sp_hide);
        ivColor = (ImageView) inflate.findViewById(R.id.iv_color);
        edtHeight = (EditText) inflate.findViewById(R.id.edt_height);
        spFont.setOnItemSelectedListener(this);
        spBold.setOnItemSelectedListener(this);
        spAlign.setOnItemSelectedListener(this);
        spHide.setOnItemSelectedListener(this);
        edtSize.addTextChangedListener(new AfterTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()) {
                    getActivity().runOnUiThread(() -> {
                        int size = Integer.parseInt(s.toString().trim());
                        tableCardView.updateViewTextSize(touchIndex, size);
                    });
                }
            }
        });
        edtHeight.addTextChangedListener(new AfterTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()) {
                    getActivity().runOnUiThread(() -> {
                        float size = Float.parseFloat(s.toString().trim());
                        if (size > 100) {
                            size = 100;
                            edtHeight.setText(String.valueOf(size));
                        }
                        tableCardView.updateViewHeight(touchIndex, size);
                    });
                }
            }
        });
        ivColor.setOnClickListener(v -> {
            new ColorPickerDialog(getContext(), color -> {
                ivColor.setBackgroundColor(color);
                tableCardView.updateViewColor(touchIndex, color);
            }, Color.BLACK).show();
        });
        inflate.findViewById(R.id.btn_import_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        inflate.findViewById(R.id.btn_delete_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        inflate.findViewById(R.id.btn_delete_bg).setOnClickListener(v -> {
            presenter.clearBackgroundImage();
            tableCardView.setBackgroundColor(Color.WHITE);
        });
        inflate.findViewById(R.id.btn_save_default).setOnClickListener(v -> presenter.save(InterfaceTablecard.Pb_TableCard_ModifyFlag.Pb_TABLECARD_MODFLAG_SETDEFAULT_VALUE,
                tableCardView.getTableCardData()));
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> presenter.queryTableCard());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> presenter.save(InterfaceTablecard.Pb_TableCard_ModifyFlag.Pb_TABLECARD_MODFLAG_ZERO_VALUE,
                tableCardView.getTableCardData()));
    }

    @Override
    protected TablePresenter initPresenter() {
        return new TablePresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryBackgroundImage();
        tableCardView.post(() -> {
            int width = tableCardView.getWidth();
            int height = tableCardView.getHeight();
            LogUtils.i("桌牌区域大小 width=" + width + ",height=" + height);
            presenter.queryTableCard();
        });
        tableCardView.setViewClickListener((index, item) -> {
            touchIndex = index;
            InterfaceTablecard.pbui_Item_MeetTableCardDetailInfo tableCardBean = presenter.tables.get(index);
            String fontName = tableCardBean.getFontname().toStringUtf8();
            int position;
            switch (fontName) {
                case "黑体":
                    position = 1;
                    break;
                case "楷体":
                    position = 2;
                    break;
                case "隶书":
                    position = 3;
                    break;
                case "微软雅黑":
                    position = 4;
                    break;
                case "小楷":
                    position = 5;
                    break;
                default:
                    position = 0;
                    break;
            }
            spFont.setSelection(position);
            edtSize.setText(String.valueOf(tableCardBean.getFontsize()));
            int flag = tableCardBean.getFlag();
            boolean isBold = (flag & InterfaceMacro.Pb_TableCardFlag.Pb_MEET_TABLECARDFLAG_BOLD_VALUE)
                    == InterfaceMacro.Pb_TableCardFlag.Pb_MEET_TABLECARDFLAG_BOLD_VALUE;
            boolean isShow = (flag & InterfaceMacro.Pb_TableCardFlag.Pb_MEET_TABLECARDFLAG_SHOW_VALUE)
                    == InterfaceMacro.Pb_TableCardFlag.Pb_MEET_TABLECARDFLAG_SHOW_VALUE;
            spBold.setSelection(isBold ? 1 : 0);
            int align = tableCardBean.getAlign();
            int alignPosition;
            switch (align) {
                //左对齐
                case InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_LEFT_VALUE:
                    alignPosition = 1;
                    break;
                //右对齐
                case InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_RIGHT_VALUE:
                    alignPosition = 2;
                    break;
                //默认居中
                default:
                    //居中对齐
                    alignPosition = 0;
                    break;
            }
            spAlign.setSelection(alignPosition);
            //注意：spinner表示的是是否隐藏
            spHide.setSelection(isShow ? 0 : 1);
            float dy = tableCardBean.getRy() - tableCardBean.getLy();
            edtHeight.setText(String.valueOf(dy));
            LogUtils.e("当前点击view的索引=" + index + ",flag=" + flag);
        });
    }

    @Override
    public void updateBackgroundImageList() {

    }

    @Override
    public void updateTableCardBg(String filePath) {
        LogUtils.i("更新桌牌背景 filePath=" + filePath);
        Drawable drawable = Drawable.createFromPath(filePath);
        tableCardView.setBackground(drawable);
    }

    @Override
    public void updateTableCard() {
        InterfaceTablecard.pbui_Item_MeetTableCardDetailInfo top = presenter.tables.get(0);
        InterfaceTablecard.pbui_Item_MeetTableCardDetailInfo middle = presenter.tables.get(1);
        InterfaceTablecard.pbui_Item_MeetTableCardDetailInfo bottom = presenter.tables.get(2);
        datas.clear();
        datas.add(new TableCardBean(top.getFontname().toStringUtf8(), top.getFontsize(), top.getFontcolor(), top.getLx(),
                top.getLy(), top.getRx(), top.getRy(), top.getFlag(), top.getAlign(), top.getType()));

        datas.add(new TableCardBean(middle.getFontname().toStringUtf8(), middle.getFontsize(), middle.getFontcolor(), middle.getLx(),
                middle.getLy(), middle.getRx(), middle.getRy(), middle.getFlag(), middle.getAlign(), middle.getType()));

        datas.add(new TableCardBean(bottom.getFontname().toStringUtf8(), bottom.getFontsize(), bottom.getFontcolor(), bottom.getLx(),
                bottom.getLy(), bottom.getRx(), bottom.getRy(), bottom.getFlag(), bottom.getAlign(), bottom.getType()));
        for (int i = 0; i < datas.size(); i++) {
            LogUtils.i("查询的数据：" + datas.get(i).toString());
        }
        tableCardView.initView(datas);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_font: {
                tableCardView.updateViewFont(touchIndex, (String) spFont.getSelectedItem());
                break;
            }
            case R.id.sp_bold: {
                tableCardView.updateViewBold(touchIndex, position);
                break;
            }
            case R.id.sp_align: {
                tableCardView.updateViewAlign(touchIndex, position);
                break;
            }
            case R.id.sp_hide: {
                tableCardView.updateViewHide(touchIndex, position);
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
