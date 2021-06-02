package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.agenda;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceAgenda;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.AgendaAdapter;
import com.xlk.takstarpaperlessmanage.adapter.PictureFileAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.DateUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xlk.takstarpaperlessmanage.model.Constant.election_entry;
import static com.xlk.takstarpaperlessmanage.model.Constant.s2b;

/**
 * @author Created by xlk on 2021/5/18.
 * @desc
 */
public class AgendaFragment extends BaseFragment<AgendaPresenter> implements AgendaContract.View, View.OnClickListener {
    private CheckBox cbFile;
    private TextView tvFileName;
    private Button btnChooseFile;
    private CheckBox cbAgendaPreparation;
    private Button btnLoadAgenda;
    private CheckBox cbTimelineAgenda;
    private RecyclerView rvAgenda;
    private Button btnAugment, btnDelete, btnSave;
    private EditText edtAgendaContent;
    private AgendaAdapter agendaAdapter;
    private PopupWindow agendaFilePop;
    private PictureFileAdapter agendaFileAdapter;
    private int currentMediaId;
    private ArrayAdapter<String> spBindDirAdapter;
    private PopupWindow augmentPop;
    private TimePickerView mStartTimePickerView;
    private long currentStartTime;
    private TimePickerView mEndTimePickerView;
    private long currentEndTime;
    private TextView tv_start_time;
    private TextView tv_end_time;
    private final int TXT_REQUEST_CODE = 1;
    private final int AGENDA_REQUEST_CODE = 2;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_agenda;
    }

    @Override
    protected void initView(View inflate) {
        cbFile = (CheckBox) inflate.findViewById(R.id.cb_file);
        tvFileName = (TextView) inflate.findViewById(R.id.tv_file_name);
        btnChooseFile = (Button) inflate.findViewById(R.id.btn_choose_file);
        cbAgendaPreparation = (CheckBox) inflate.findViewById(R.id.cb_agenda_preparation);
        edtAgendaContent = (EditText) inflate.findViewById(R.id.edt_agenda_content);
        btnLoadAgenda = (Button) inflate.findViewById(R.id.btn_load_agenda);
        cbTimelineAgenda = (CheckBox) inflate.findViewById(R.id.cb_timeline_agenda);
        rvAgenda = (RecyclerView) inflate.findViewById(R.id.rv_agenda);
        btnAugment = (Button) inflate.findViewById(R.id.btn_augment);
        btnDelete = (Button) inflate.findViewById(R.id.btn_delete);
        btnSave = (Button) inflate.findViewById(R.id.btn_save);
        cbFile.setOnClickListener(this);
        cbAgendaPreparation.setOnClickListener(this);
        cbTimelineAgenda.setOnClickListener(this);

        btnChooseFile.setOnClickListener(this);
        btnLoadAgenda.setOnClickListener(this);
        btnAugment.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    protected AgendaPresenter initPresenter() {
        return new AgendaPresenter(this);
    }

    @Override
    protected void initial() {
        cbFile.setChecked(false);
        cbAgendaPreparation.setChecked(false);
        cbTimelineAgenda.setChecked(false);
        currentMediaId = 0;
        edtAgendaContent.setText("");
        presenter.agendas.clear();
        if (agendaAdapter != null) agendaAdapter.notifyDataSetChanged();

        presenter.queryAgenda();
        presenter.queryShareFile();
        presenter.queryMeetDir();
        initTimePicker();
    }

    private void initTimePicker() {
        mStartTimePickerView = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                currentStartTime = date.getTime() / 1000;
                if (tv_start_time != null) {
                    tv_start_time.setText(DateUtil.millisecondsFormat(date.getTime(), "yyyy/MM/dd/ HH:mm"));
                }
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, false})
                //默认设置false ，内部实现将DecorView 作为它的父控件
                .isDialog(true)
                .addOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                .setItemVisibleCount(5)
                .setLineSpacingMultiplier(2.0f)
                .isAlphaGradient(true)
                .build();

        mEndTimePickerView = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                currentEndTime = date.getTime() / 1000;
                if (tv_end_time != null) {
                    tv_end_time.setText(DateUtil.millisecondsFormat(date.getTime(), "yyyy/MM/dd/ HH:mm"));
                }
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, false})
                //默认设置false ，内部实现将DecorView 作为它的父控件
                .isDialog(true)
                .addOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                .setItemVisibleCount(5)
                .setLineSpacingMultiplier(2.0f)
                .isAlphaGradient(true)
                .build();
    }

    @Override
    public void updateAgendaText(String content) {
        cbAgendaPreparation.setChecked(true);
        cbFile.setChecked(false);
        cbTimelineAgenda.setChecked(false);
        edtAgendaContent.setText(content);
    }

    @Override
    public void updateAgendaFile(int mediaid, String fileName) {
        cbFile.setChecked(true);
        cbTimelineAgenda.setChecked(false);
        cbAgendaPreparation.setChecked(false);
        currentMediaId = mediaid;
        tvFileName.setText(fileName);
    }

    @Override
    public void updateAgendaFileList() {
        if (agendaFileAdapter != null) {
            agendaFileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateDirList() {
        List<String> temps = new ArrayList<>();
        for (int i = 0; i < presenter.dirInfos.size(); i++) {
            temps.add(presenter.dirInfos.get(i).getName().toStringUtf8());
        }
        spBindDirAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_checked_black_text, temps);
    }

    @Override
    public void updateAgendaList() {
        cbTimelineAgenda.setChecked(true);
        cbFile.setChecked(false);
        cbAgendaPreparation.setChecked(false);
        if (agendaAdapter == null) {
            agendaAdapter = new AgendaAdapter(presenter.agendas);
            rvAgenda.setLayoutManager(new LinearLayoutManager(getContext()));
            rvAgenda.addItemDecoration(new RvItemDecoration(getContext()));
            rvAgenda.setAdapter(agendaAdapter);
            agendaAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    agendaAdapter.setSelectedId(presenter.agendas.get(position).getAgendaid());
                }
            });
        } else {
            agendaAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cb_file: {
                boolean checked = cbFile.isChecked();
                cbFile.setChecked(checked);
                if (checked) {
                    cbAgendaPreparation.setChecked(!checked);
                    cbTimelineAgenda.setChecked(!checked);
                }
                break;
            }
            case R.id.cb_agenda_preparation: {
                boolean checked = cbAgendaPreparation.isChecked();
                cbAgendaPreparation.setChecked(checked);
                if (checked) {
                    cbFile.setChecked(!checked);
                    cbTimelineAgenda.setChecked(!checked);
                }
                break;
            }
            case R.id.cb_timeline_agenda: {
                boolean checked = cbTimelineAgenda.isChecked();
                cbTimelineAgenda.setChecked(checked);
                if (checked) {
                    cbFile.setChecked(!checked);
                    cbAgendaPreparation.setChecked(!checked);
                }
                break;
            }
            case R.id.btn_choose_file: {
                showAgendaFilePop();
                break;
            }
            case R.id.btn_load_agenda: {
                chooseLocalFile(TXT_REQUEST_CODE);
                break;
            }
            case R.id.btn_augment: {
                showAugmentPop();
                break;
            }
            case R.id.btn_delete: {
                InterfaceAgenda.pbui_ItemAgendaTimeInfo selectedAgenda = agendaAdapter.getSelectedAgenda();
                if (selectedAgenda == null) {
                    ToastUtil.showShort(R.string.please_choose_agenda_first);
                    return;
                }
                jni.delAgenda(selectedAgenda);
                break;
            }
            case R.id.btn_save: {
                if (cbFile.isChecked()) {
                    //文件议程
                    if (currentMediaId != 0) {
                        jni.modifyFileAgenda(currentMediaId);
                    } else {
                        ToastUtils.showShort(R.string.please_choose_file_first);
                    }
                } else if (cbAgendaPreparation.isChecked()) {
                    //文本议程
                    String content = edtAgendaContent.getText().toString();
                    if (content.isEmpty()) {
                        ToastUtils.showShort(R.string.please_enter_content_first);
                        return;
                    }
                    jni.modifyTextAgenda(content);
                } else if (cbTimelineAgenda.isChecked()) {
                    //时间轴式议程
                    if (presenter.agendas.isEmpty()) {
                        ToastUtils.showShort(R.string.please_add_agenda_first);
                        return;
                    }
                    jni.modifyTimelineAgenda(presenter.agendas);
                } else {
                    ToastUtils.showShort(R.string.please_choose_agenda_first);
                }
                break;
            }
        }
    }

    private void showAugmentPop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_augment_agenda, null, false);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        augmentPop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, rvAgenda, Gravity.CENTER, width1 / 2, 0);
        tv_start_time = inflate.findViewById(R.id.tv_start_time);
        tv_end_time = inflate.findViewById(R.id.tv_end_time);
        Spinner sp_dir = inflate.findViewById(R.id.sp_dir);
        EditText edt_description = inflate.findViewById(R.id.edt_description);

        long milliseconds = System.currentTimeMillis();
        tv_start_time.setText(DateUtil.millisecondsFormat(milliseconds, "yyyy/MM/dd/ HH:mm"));
        tv_end_time.setText(DateUtil.millisecondsFormat(milliseconds, "yyyy/MM/dd/ HH:mm"));
        currentStartTime = milliseconds / 1000;
        currentEndTime = milliseconds / 1000;
        sp_dir.setAdapter(spBindDirAdapter);
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> augmentPop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> augmentPop.dismiss());
        tv_start_time.setOnClickListener(v -> {
            if (currentStartTime != 0) {
                java.util.Calendar instance = java.util.Calendar.getInstance();
                instance.setTimeInMillis(currentStartTime * 1000);
                mStartTimePickerView.setDate(instance);
            }
            mStartTimePickerView.show(v);
        });
        tv_end_time.setOnClickListener(v -> {
            if (currentStartTime != 0) {
                java.util.Calendar instance = java.util.Calendar.getInstance();
                instance.setTimeInMillis(currentEndTime * 1000);
                mEndTimePickerView.setDate(instance);
            }
            mEndTimePickerView.show(v);
        });
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String description = edt_description.getText().toString();
            if (description.isEmpty()) {
                ToastUtil.showShort(R.string.please_enter_content_first);
                return;
            }
            int selectedItemPosition = sp_dir.getSelectedItemPosition();
            int dirId = presenter.dirInfos.get(selectedItemPosition).getId();
            InterfaceAgenda.pbui_ItemAgendaTimeInfo build = InterfaceAgenda.pbui_ItemAgendaTimeInfo.newBuilder()
                    .setDirid(dirId)
                    .setStatus(InterfaceMacro.Pb_AgendaStatus.Pb_MEETAGENDA_STATUS_IDLE_VALUE)
                    .setStartutctime(currentStartTime)
                    .setEndutctime(currentEndTime)
                    .setDesctext(s2b(description))
                    .build();
            jni.addAgenda(build);
            augmentPop.dismiss();
        });
    }

    private void showAgendaFilePop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_bg_picture, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        agendaFilePop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, rvAgenda, Gravity.CENTER, width1 / 2, 0);
        RecyclerView rv_file = inflate.findViewById(R.id.rv_file);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.agenda_file));

        agendaFileAdapter = new PictureFileAdapter(presenter.sharedFiles);
        rv_file.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_file.addItemDecoration(new RvItemDecoration(getContext()));
        rv_file.setAdapter(agendaFileAdapter);
        CheckBox cbAll = inflate.findViewById(R.id.cb_all);
        cbAll.setOnClickListener(v -> {
            boolean checked = cbAll.isChecked();
            cbAll.setChecked(checked);
            agendaFileAdapter.setSelectedAll(checked);
        });
        agendaFileAdapter.addChildClickViewIds(R.id.item_view_5);
        agendaFileAdapter.setOnItemClickListener((adapter, view, position) -> {
            agendaFileAdapter.setSelected(presenter.sharedFiles.get(position).getMediaid());
            cbAll.setChecked(agendaFileAdapter.isSelectedAll());
        });
        agendaFileAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo info = presenter.sharedFiles.get(position);
            jni.deleteMeetDirFile(Constant.SHARE_DIR_ID, info);
        });
        inflate.findViewById(R.id.btn_upload).setOnClickListener(v -> {
            chooseLocalFile(AGENDA_REQUEST_CODE);
        });
        inflate.findViewById(R.id.btn_delete).setOnClickListener(v -> {
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> selectedFiles = agendaFileAdapter.getSelectedFiles();
            if (selectedFiles.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_file_first);
                return;
            }
            jni.deleteMeetDirFile(Constant.SHARE_DIR_ID, selectedFiles);
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> agendaFilePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> agendaFilePop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> selectedFiles = agendaFileAdapter.getSelectedFiles();
            if (selectedFiles.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_file_first);
                return;
            }
            if (selectedFiles.size() > 1) {
                ToastUtil.showShort(R.string.can_only_choose_one);
                return;
            }
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo info = selectedFiles.get(0);
            currentMediaId = info.getMediaid();
            tvFileName.setText(info.getName().toStringUtf8());
            agendaFilePop.dismiss();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            File file = UriUtils.uri2File(uri);
            if (file == null) {
                return;
            }
            String fileName = file.getName();
            String absolutePath = file.getAbsolutePath();
            if (requestCode == TXT_REQUEST_CODE) {
                if (fileName.endsWith(".txt")) {
                    long l = System.currentTimeMillis();
                    String content = FileIOUtils.readFile2String(file);
                    LogUtils.e("读取" + fileName + "文件文本用时=" + (System.currentTimeMillis() - l));
                    edtAgendaContent.setText(content);
                } else {
                    ToastUtils.showShort(R.string.please_choose_txt_file);
                }
            } else if (requestCode == AGENDA_REQUEST_CODE) {
                if (fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith(".pdf")) {
                    LogUtils.i(TAG, "onActivityResult 上传议程文件=" + absolutePath);
                    jni.uploadFile(0, Constant.SHARE_DIR_ID, InterfaceMacro.Pb_MeetFileAttrib.Pb_MEETFILE_ATTRIB_BACKGROUND_VALUE,
                            fileName, absolutePath, 0, Constant.UPLOAD_AGENDA_FILE);
                } else {
                    ToastUtils.showShort(R.string.can_only_choose_agenda_file);
                }
            }
        }
    }
}
