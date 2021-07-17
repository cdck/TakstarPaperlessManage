package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.rate;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.protobuf.ByteString;
import com.mogujie.tt.protobuf.InterfaceFilescorevote;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.RateAdapter;
import com.xlk.takstarpaperlessmanage.adapter.UploadFileAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.bean.ScoreFileBean;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.JxlUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import static com.xlk.takstarpaperlessmanage.model.Constant.s2b;

/**
 * @author Created by xlk on 2021/5/24.
 * @desc 会前设置-投票录入
 */
public class RateFragment extends BaseFragment<RatePresenter> implements RateContract.View {

    private RecyclerView rv_content;
    private RateAdapter rateAdapter;
    List<ScoreFileBean> uploadFiles = new ArrayList<>();
    private UploadFileAdapter uploadFileAdapter;
    private EditText edt_save_address;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rate;
    }

    @Override
    protected void initView(View inflate) {
        rv_content = inflate.findViewById(R.id.rv_content);
        inflate.findViewById(R.id.btn_add).setOnClickListener(v -> showAddOrModifyPop(null));
        inflate.findViewById(R.id.btn_import).setOnClickListener(v -> {
            chooseLocalFile(REQUEST_CODE_IMPORT_SCORE_XLS);
        });
        inflate.findViewById(R.id.btn_export).setOnClickListener(v -> {
            if (presenter.fileScores.isEmpty()) {
                ToastUtils.showShort(R.string.no_data_to_export);
                return;
            }
            showExportFilePop();
        });
    }

    private void showExportFilePop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_export_config, null);
        PopupWindow pop = PopUtil.createHalfPop(inflate, rv_content);
        EditText edt_file_name = inflate.findViewById(R.id.edt_file_name);
        edt_save_address = inflate.findViewById(R.id.edt_save_address);
        edt_save_address.setKeyListener(null);
        inflate.findViewById(R.id.btn_choose_dir).setOnClickListener(v -> {
            String currentDirPath = edt_save_address.getText().toString().trim();
            if (currentDirPath.isEmpty()) {
                currentDirPath = Constant.root_dir;
            }
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.CHOOSE_DIR_PATH).objects(Constant.CHOOSE_DIR_TYPE_EXPORT_SCORE, currentDirPath).build());
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String fileName = edt_file_name.getText().toString().trim();
            String addr = edt_save_address.getText().toString().trim();
            if (fileName.isEmpty() || addr.isEmpty()) {
                ToastUtil.showShort(R.string.please_enter_file_name_and_addr);
                return;
            }
            JxlUtil.exportFileScore(fileName, addr, presenter.fileScores);
            pop.dismiss();
        });
    }

    @Override
    public void updateExportDirPath(String dirPath) {
        edt_save_address.setText(dirPath);
    }

    @Override
    protected RatePresenter initPresenter() {
        return new RatePresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryMemberDetailed();
        presenter.queryFileScore();
        uploadFileAdapter = new UploadFileAdapter(uploadFiles);
    }

    @Override
    public void updateMemberDetailedList() {

    }

    @Override
    public void updateFileScoreList() {
        if (rateAdapter == null) {
            rateAdapter = new RateAdapter(presenter.fileScores);
            rateAdapter.addChildClickViewIds(R.id.operation_view_1, R.id.operation_view_2);
            rv_content.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_content.addItemDecoration(new RvItemDecoration(getContext()));
            rv_content.setAdapter(rateAdapter);
            rateAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                    InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore item = presenter.fileScores.get(position);
                    if (view.getId() == R.id.operation_view_1) {//修改
                        showAddOrModifyPop(item);
                    } else {//删除
                        showDeletePop(item);
                    }
                }
            });
        } else {
            rateAdapter.notifyDataSetChanged();
        }
    }

    private void showAddOrModifyPop(InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_modify_rate, null, false);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width / 2, height * 2 / 3, rv_content, Gravity.CENTER, width1 / 2, 0);
        boolean isAdd = item == null;
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(isAdd ? getString(R.string.create_score) : getString(R.string.modify_score));
        EditText edt_rate_description = inflate.findViewById(R.id.edt_rate_description);
        CheckBox cb_use_file_name = inflate.findViewById(R.id.cb_use_file_name);
        TextView tv_add_score_hint = inflate.findViewById(R.id.tv_add_score_hint);
        Spinner sp_notation = inflate.findViewById(R.id.sp_notation);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_checked_gray_text,
                getResources().getStringArray(R.array.yes_or_no));
        sp_notation.setAdapter(arrayAdapter);

        EditText edt_score_a = inflate.findViewById(R.id.edt_score_a);
        EditText edt_score_b = inflate.findViewById(R.id.edt_score_b);
        EditText edt_score_c = inflate.findViewById(R.id.edt_score_c);
        EditText edt_score_d = inflate.findViewById(R.id.edt_score_d);
        RecyclerView rv_file = inflate.findViewById(R.id.rv_file);
        rv_file.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_file.addItemDecoration(new RvItemDecoration(getContext()));
        rv_file.setAdapter(uploadFileAdapter);
        uploadFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                uploadFileAdapter.choose(uploadFiles.get(position).getFilePath());
            }
        });
        if (!isAdd) {
            tv_add_score_hint.setVisibility(View.GONE);
            int mode = item.getMode();
            int fileid = item.getFileid();
            if (fileid != 0) {
                String fileName = jni.queryFileNameByMediaId(fileid);
                uploadFiles.add(new ScoreFileBean(fileid, fileName));
                uploadFileAdapter.notifyDataSetChanged();
            }
            edt_rate_description.setText(item.getContent().toStringUtf8());
            List<ByteString> itemList = item.getVoteTextList();
            for (int i = 0; i < itemList.size(); i++) {
                String text = itemList.get(i).toStringUtf8();
                if (i == 0) edt_score_a.setText(text);
                if (i == 1) edt_score_b.setText(text);
                if (i == 2) edt_score_c.setText(text);
                if (i == 3) edt_score_d.setText(text);
            }
            sp_notation.setSelection(mode);
        }
        inflate.findViewById(R.id.btn_upload).setOnClickListener(v -> chooseLocalFile(REQUEST_CODE_IMPORT_SCORE_FILE));
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String content = edt_rate_description.getText().toString();
            int modeIndex = sp_notation.getSelectedItemPosition();
            String a = edt_score_a.getText().toString();
            String b = edt_score_b.getText().toString();
            String c = edt_score_c.getText().toString();
            String d = edt_score_d.getText().toString();
            if (a.isEmpty() || b.isEmpty() || c.isEmpty()) {
                ToastUtil.showShort(R.string.first_three_options_must_be_filled_in);
                return;
            }
            if (uploadFiles.isEmpty()) {
                ToastUtil.showShort(R.string.please_add_score_file_first);
                return;
            }
            List<ByteString> answers = new ArrayList<>();
            answers.add(s2b(a));
            answers.add(s2b(b));
            answers.add(s2b(c));
            if (!d.isEmpty()) answers.add(s2b(d));
            if (isAdd) {
                List<InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore> addScores = new ArrayList<>();
                if (uploadFiles.size() > 1) {
                    for (ScoreFileBean bean : uploadFiles) {
                        String filePath = bean.getFilePath();
                        InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore build = InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore.newBuilder()
                                .setContent(s2b(filePath.substring(filePath.lastIndexOf("/") + 1)))
                                .setFileid(bean.getMediaId())
                                .setMode(modeIndex)
                                .setSelectcount(answers.size())
                                .setTimeouts(300)
                                .addAllVoteText(answers).build();
                        addScores.add(build);
                    }
                } else {
                    ScoreFileBean bean = uploadFiles.get(0);
                    if (cb_use_file_name.isChecked()) {
                        String filePath = bean.getFilePath();
                        content = filePath.substring(filePath.lastIndexOf("/") + 1);
                    }
                    if (content.isEmpty()) {
                        ToastUtil.showShort(R.string.please_enter_score_content_first);
                        return;
                    }
                    InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore build = InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore.newBuilder()
                            .setContent(s2b(content))
                            .setFileid(bean.getMediaId())
                            .setMode(modeIndex)
                            .setSelectcount(answers.size())
                            .setTimeouts(300)
                            .addAllVoteText(answers).build();
                    addScores.add(build);
                }
                jni.addScore(0, addScores);
            } else {
                if (uploadFiles.size() > 1) {
                    ToastUtil.showShort(R.string.only_can_use_one_file);
                    return;
                }
                ScoreFileBean bean = uploadFiles.get(0);
                if (cb_use_file_name.isChecked()) {
                    content = bean.getFilePath();
                }
                if (content.isEmpty()) {
                    ToastUtil.showShort(R.string.please_enter_score_content_first);
                    return;
                }
                InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore build = InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore.newBuilder()
                        .setContent(s2b(content))
                        .setFileid(bean.getMediaId())
                        .setMode(modeIndex)
                        .setVoteid(item.getVoteid())
                        .setVotestate(item.getVotestate())
                        .setSelectcount(answers.size())
                        .setTimeouts(item.getTimeouts())
                        .setEndtime(item.getEndtime())
                        .addAllVoteText(answers)
                        .build();
                jni.modifyScore(item.getFileid(), build);
            }
            pop.dismiss();
        });
        inflate.findViewById(R.id.btn_delete).setOnClickListener(v -> {
            List<ScoreFileBean> selectedFiles = uploadFileAdapter.getSelectedFiles();
            if (selectedFiles.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_file_first);
                return;
            }
            Iterator<ScoreFileBean> iterator = uploadFiles.iterator();
            while (iterator.hasNext()) {
                ScoreFileBean next = iterator.next();
                if (selectedFiles.contains(next)) {
                    iterator.remove();
                }
            }
            uploadFileAdapter.notifyDataSetChanged();
        });
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                uploadFiles.clear();
            }
        });
    }

    private void showDeletePop(InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_delete, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rv_content, Gravity.CENTER, width1 / 2, 0);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        TextView tv_hint = inflate.findViewById(R.id.tv_hint);
        tv_title.setText(getString(R.string.delete));
        tv_hint.setText(getString(R.string.delete_rate_hint));
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            jni.delScore(item.getVoteid());
            pop.dismiss();
        });
    }

    @Override
    public void updateScoreSubmitMemberList() {

    }

    @Override
    public void addFile2List(String filePath, int mediaId) {
        for (int i = 0; i < uploadFiles.size(); i++) {
            ScoreFileBean bean = uploadFiles.get(i);
            if (bean.getFilePath().equals(filePath) && bean.getMediaId() == mediaId) {
                ToastUtil.showShort(R.string.file_already_exists);
                return;
            }
        }
        uploadFiles.add(new ScoreFileBean(mediaId, filePath));
        uploadFileAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            File file = UriUtils.uri2File(uri);
            if (file != null && file.exists()) {
                if (requestCode == REQUEST_CODE_IMPORT_SCORE_FILE) {
                    jni.uploadFile(InterfaceMacro.Pb_Upload_Flag.Pb_MEET_UPLOADFLAG_ZERO_VALUE, 0,
                            InterfaceMacro.Pb_MeetFileAttrib.Pb_MEETFILE_ATTRIB_ZERO_VALUE,
                            file.getName(), file.getAbsolutePath(), 0, Constant.UPLOAD_SCORE_FILE);
                } else if (requestCode == REQUEST_CODE_IMPORT_SCORE_XLS) {
                    if (file.getName().endsWith(".xls")) {
                        List<InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore> items = JxlUtil.readScoreXls(file);
                        if (items.isEmpty()) {
                            ToastUtil.showShort(R.string.error_empty_format);
                            return;
                        }
                        jni.addScore(0, items);
                    } else {
                        ToastUtil.showLong(R.string.please_choose_xls_file);
                    }
                }
            }
        }
    }
}
