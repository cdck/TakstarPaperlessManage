package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.archive;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xlk.takstarpaperlessmanage.App;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.helper.task.BasicInformationTask;
import com.xlk.takstarpaperlessmanage.helper.archive.ConsumptionTask;
import com.xlk.takstarpaperlessmanage.helper.archive.LineUpTaskHelp;
import com.xlk.takstarpaperlessmanage.helper.task.DownloadFileTask;
import com.xlk.takstarpaperlessmanage.helper.task.MemberTask;
import com.xlk.takstarpaperlessmanage.helper.task.SignInTask;
import com.xlk.takstarpaperlessmanage.helper.task.VoteTask;
import com.xlk.takstarpaperlessmanage.helper.task.ZipFileTask;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/5/28.
 * @desc
 */
public class ArchiveFragment extends BaseFragment<ArchivePresenter> implements ArchiveContract.View {
    private CheckBox cbArchiveAll;
    private CheckBox cbMeetingBasicInformation;
    private CheckBox cbMemberInformation;
    private CheckBox cbSignInformation;
    private CheckBox cbVoteResult;
    private CheckBox cbSharedFile;
    private CheckBox cbAnnotateFile;
    private CheckBox cbMeetingMaterial;
    private EditText edtOutput;
    private Button btnModify;
    private CheckBox cbEncryption;
    private Button btnCancel;
    private Button btnArchive;
    private RecyclerView rv_operate;
    private ArchiveInformAdapter informAdapter;
    LineUpTaskHelp lineUpTaskHelp;
    /**
     * 当前是否正在压缩中
     */
    private boolean isCompressing;
    /**
     * 当前是否正在归档中
     */
    private boolean isDownloading;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_archive;
    }

    @Override
    protected void initView(View inflate) {
        cbArchiveAll = (CheckBox) inflate.findViewById(R.id.cb_archive_all);
        cbMeetingBasicInformation = (CheckBox) inflate.findViewById(R.id.cb_meeting_basic_information);
        cbMemberInformation = (CheckBox) inflate.findViewById(R.id.cb_member_information);
        cbSignInformation = (CheckBox) inflate.findViewById(R.id.cb_sign_information);
        cbVoteResult = (CheckBox) inflate.findViewById(R.id.cb_vote_result);
        cbSharedFile = (CheckBox) inflate.findViewById(R.id.cb_shared_file);
        cbAnnotateFile = (CheckBox) inflate.findViewById(R.id.cb_annotate_file);
        cbMeetingMaterial = (CheckBox) inflate.findViewById(R.id.cb_meeting_material);
        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            if (isChecked) {
                if (cbMeetingBasicInformation.isChecked() && cbMemberInformation.isChecked() && cbSignInformation.isChecked() &&
                        cbVoteResult.isChecked() && cbSharedFile.isChecked() && cbAnnotateFile.isChecked() && cbMeetingMaterial.isChecked()
                ) {
                    cbArchiveAll.setChecked(true);
                }
            } else {
                cbArchiveAll.setChecked(false);
            }
        };
        cbArchiveAll.setOnClickListener(v -> {
            boolean isChecked = cbArchiveAll.isChecked();
            cbArchiveAll.setChecked(isChecked);
            cbMeetingBasicInformation.setChecked(isChecked);
            cbMemberInformation.setChecked(isChecked);
            cbSignInformation.setChecked(isChecked);
            cbVoteResult.setChecked(isChecked);
            cbSharedFile.setChecked(isChecked);
            cbAnnotateFile.setChecked(isChecked);
            cbMeetingMaterial.setChecked(isChecked);
        });
        cbMeetingBasicInformation.setOnCheckedChangeListener(listener);
        cbMemberInformation.setOnCheckedChangeListener(listener);
        cbSignInformation.setOnCheckedChangeListener(listener);
        cbVoteResult.setOnCheckedChangeListener(listener);
        cbSharedFile.setOnCheckedChangeListener(listener);
        cbAnnotateFile.setOnCheckedChangeListener(listener);
        cbMeetingMaterial.setOnCheckedChangeListener(listener);

        edtOutput = (EditText) inflate.findViewById(R.id.edt_output);
        btnModify = (Button) inflate.findViewById(R.id.btn_modify);
        rv_operate = (RecyclerView) inflate.findViewById(R.id.rv_operate);
//        llStatus0 = (LinearLayout) inflate.findViewById(R.id.ll_status_0);
//        tvStatus0 = (TextView) inflate.findViewById(R.id.tv_status_0);
//        llStatus1 = (LinearLayout) inflate.findViewById(R.id.ll_status_1);
//        tvStatus1 = (TextView) inflate.findViewById(R.id.tv_status_1);
//        llStatus2 = (LinearLayout) inflate.findViewById(R.id.ll_status_2);
//        tvStatus2 = (TextView) inflate.findViewById(R.id.tv_status_2);
//        llStatus3 = (LinearLayout) inflate.findViewById(R.id.ll_status_3);
//        tvStatus3 = (TextView) inflate.findViewById(R.id.tv_status_3);
//        llStatus4 = (LinearLayout) inflate.findViewById(R.id.ll_status_4);
//        tvStatus4 = (TextView) inflate.findViewById(R.id.tv_status_4);
//        llStatus5 = (LinearLayout) inflate.findViewById(R.id.ll_status_5);
//        tvStatus5 = (TextView) inflate.findViewById(R.id.tv_status_5);
//        llStatus6 = (LinearLayout) inflate.findViewById(R.id.ll_status_6);
//        tvStatus6 = (TextView) inflate.findViewById(R.id.tv_status_6);
        cbEncryption = (CheckBox) inflate.findViewById(R.id.cb_encryption);
        cbEncryption.setOnClickListener(v -> {
            boolean checked = cbEncryption.isChecked();
            if (isCompressing) {
                ToastUtils.showShort(R.string.already_compressing);
                cbEncryption.setChecked(!checked);
                return;
            }
            cbEncryption.setChecked(checked);
            presenter.setEncryption(checked);
            /*
            if (!checked) {
                presenter.setEncryption(false);
                presenter.setPassword("");
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.please_enter_password);
                View view = LayoutInflater.from(getContext()).inflate(R.layout.enter_pwd_layout, null, false);
                EditText edt_content = view.findViewById(R.id.edt_content);
                builder.setView(view);
                builder.setPositiveButton(R.string.define, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pwd = edt_content.getText().toString().trim();
                        if (pwd.length() != 6) {
                            ToastUtil.showShort(R.string.error_password);
                            cbEncryption.setChecked(false);
                            return;
                        }
                        presenter.setEncryption(true);
                        presenter.setPassword(pwd);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        presenter.setPassword("");
                        cbEncryption.setChecked(false);
                    }
                });
                builder.create().show();
            }
             */
        });
        btnCancel = (Button) inflate.findViewById(R.id.btn_cancel);
        btnArchive = (Button) inflate.findViewById(R.id.btn_archive);
        edtOutput.setKeyListener(null);
        edtOutput.setText(Constant.archive_zip_dir);
        edtOutput.setSelection(Constant.archive_zip_dir.length());
        btnModify.setOnClickListener(v -> {
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.CHOOSE_DIR_PATH)
                    .objects(Constant.CHOOSE_DIR_TYPE_ARCHIVE, Constant.root_dir).build());
        });
        btnCancel.setOnClickListener(v -> {
            if (lineUpTaskHelp != null) {
                App.threadPool.execute(() -> {
                    //先清除将要下载的文件
                    presenter.clearShouldDownloadFiles();
                    boolean delete = FileUtils.delete(Constant.DIR_ARCHIVE_TEMP);
                    LogUtils.i("通过删除源文件的方法中断压缩 delete=" + delete);
//                    //获取到当前正在执行的任务
//                    ConsumptionTask currentTask = lineUpTaskHelp.getFirst();
//                    //停止/中断当前执行的任务
//                    if (currentTask != null) {
//                        currentTask.cancel();
//                    }
                    isDownloading = false;
                    /*
                    for (int i = 0; i < archiveInforms.size(); i++) {
                        ArchiveInform archiveInform = archiveInforms.get(i);
                        if (archiveInform.getContent().equals("压缩文件")) {
                            archiveInform.setResult("已中断任务");
                            break;
                        }
                    }
                    updateArchiveInform(archiveInforms);
                    */
                });
            }
        });
        btnArchive.setOnClickListener(v -> {
            if (isDownloading || isCompressing) {
                ToastUtils.showShort(R.string.currently_in_the_archives);
            } else {
                start();
            }
        });
    }

    private void start() {
        archiveInforms.clear();
        updateArchiveInform(archiveInforms);
        boolean hasTask = false;
        initLineUpTaskHelp();
        long l = System.currentTimeMillis();
        if (cbMeetingBasicInformation.isChecked()) {
            hasTask = true;
            BasicInformationTask task = new BasicInformationTask(presenter.getBasicInformationTaskInfo());
            task.taskNo = "会议基本信息";
            lineUpTaskHelp.addTask(task);
        }
        if (cbMemberInformation.isChecked()) {
            hasTask = true;
            MemberTask task = new MemberTask(presenter.getMemberTaskInfo());
            task.taskNo = "参会人员信息";
            lineUpTaskHelp.addTask(task);
        }
        if (cbSignInformation.isChecked()) {
            hasTask = true;
            SignInTask task = new SignInTask(presenter.getSignInTaskInfo());
            task.taskNo = "会议签到信息";
            lineUpTaskHelp.addTask(task);
        }
        if (cbVoteResult.isChecked()) {
            hasTask = true;
            VoteTask task = new VoteTask(presenter.getVoteTaskInfo());
            task.taskNo = "会议投票结果";
            lineUpTaskHelp.addTask(task);
        }
        LogUtils.e("导出资料用时：" + (System.currentTimeMillis() - l));
        if (cbSharedFile.isChecked()) {
            hasTask = true;
            presenter.addShouldDownloadShareFiles(lineUpTaskHelp);
        }
        if (cbAnnotateFile.isChecked()) {
            hasTask = true;
            presenter.addShouldDownloadAnnotationFiles(lineUpTaskHelp);
        }
        if (cbMeetingMaterial.isChecked()) {
            hasTask = true;
            presenter.addShouldDownloadMeetDataFiles(lineUpTaskHelp);
        }
        if (!hasTask) {
            ToastUtils.showShort(R.string.please_choose_task_first);
            return;
        }
        isDownloading = true;
        presenter.addNextDownloadShareFileTask(0);
    }

    private void initLineUpTaskHelp() {
        lineUpTaskHelp = LineUpTaskHelp.getInstance();
        lineUpTaskHelp.setOnTaskListener(new LineUpTaskHelp.OnTaskListener() {
            @Override
            public void exNextTask(ConsumptionTask task) {
                LogUtils.d("exNextTask " + task.taskNo + ",剩余任务数量=" + lineUpTaskHelp.getTaskCount());
                if (task instanceof BasicInformationTask) {
                    archiveInforms.add(new ArchiveInform("会议基本信息", "进行中..."));
                    updateArchiveInform(archiveInforms);
                    BasicInformationTask basicInformationTask = (BasicInformationTask) task;
                    basicInformationTask.thread.start();
                } else if (task instanceof MemberTask) {
                    archiveInforms.add(new ArchiveInform("参会人员信息", "进行中..."));
                    updateArchiveInform(archiveInforms);
                    MemberTask memberTask = (MemberTask) task;
                    memberTask.thread.start();
                } else if (task instanceof SignInTask) {
                    archiveInforms.add(new ArchiveInform("会议签到信息", "进行中..."));
                    updateArchiveInform(archiveInforms);
                    SignInTask signInTask = (SignInTask) task;
                    signInTask.thread.start();
                } else if (task instanceof VoteTask) {
                    archiveInforms.add(new ArchiveInform("会议投票结果", "进行中..."));
                    updateArchiveInform(archiveInforms);
                    VoteTask voteTask = (VoteTask) task;
                    voteTask.thread.start();
                } else if (task instanceof DownloadFileTask) {
                    DownloadFileTask downloadFileTask = (DownloadFileTask) task;
                    //没有使用线程，直接在主线程中调用
                    downloadFileTask.run();
                } else if (task instanceof ZipFileTask) {
                    //压缩开始、下载停止
                    isDownloading = false;
                    isCompressing = true;
                    archiveInforms.add(new ArchiveInform("压缩文件", "进行中..."));
                    updateArchiveInform(archiveInforms);
                    ZipFileTask zipFileTask = (ZipFileTask) task;
                    zipFileTask.thread.start();
                }
            }

            @Override
            public void exComplete(ConsumptionTask task) {
                LogUtils.e("exComplete 执行完毕：" + task.taskNo);
                if (task instanceof BasicInformationTask) {
                    for (int i = 0; i < archiveInforms.size(); i++) {
                        if (archiveInforms.get(i).getContent().equals("会议基本信息")) {
                            archiveInforms.get(i).setResult("完成");
                            break;
                        }
                    }
                    updateArchiveInform(archiveInforms);
                } else if (task instanceof MemberTask) {
                    for (int i = 0; i < archiveInforms.size(); i++) {
                        if (archiveInforms.get(i).getContent().equals("参会人员信息")) {
                            archiveInforms.get(i).setResult("完成");
                            break;
                        }
                    }
                    updateArchiveInform(archiveInforms);
                } else if (task instanceof SignInTask) {
                    for (int i = 0; i < archiveInforms.size(); i++) {
                        if (archiveInforms.get(i).getContent().equals("会议签到信息")) {
                            archiveInforms.get(i).setResult("完成");
                            break;
                        }
                    }
                    updateArchiveInform(archiveInforms);
                } else if (task instanceof VoteTask) {
                    for (int i = 0; i < archiveInforms.size(); i++) {
                        if (archiveInforms.get(i).getContent().equals("会议投票结果")) {
                            archiveInforms.get(i).setResult("完成");
                            break;
                        }
                    }
                    updateArchiveInform(archiveInforms);
                } else if (task instanceof ZipFileTask) {
                    ZipFileTask zipFileTask = (ZipFileTask) task;
                    //在压缩的时候如果点击了取消，但是无法中断，只能在压缩完成后处理
                    for (int i = 0; i < archiveInforms.size(); i++) {
                        if (archiveInforms.get(i).getContent().equals("压缩文件")) {
                            archiveInforms.get(i).setResult(zipFileTask.isInterrupted() ? "已中断任务" : "完成");
                            break;
                        }
                    }
                    if (!zipFileTask.isInterrupted()) {
                        archiveInforms.add(new ArchiveInform("位置：" + zipFileTask.getFilePath(), zipFileTask.getPassword()));
                    } else {
                        FileUtils.delete(zipFileTask.getFilePath());
                    }
                    updateArchiveInform(archiveInforms);
                    isCompressing = false;
                }
            }

            @Override
            public void noTask() {
                LogUtils.e("noTask 所有任务执行完成");
            }

            @Override
            public void timeOut(ConsumptionTask task) {
                LogUtils.e(task.taskNo + "超时");
                lineUpTaskHelp.exOk(task);
            }
        });
    }

    @Override
    public void updateArchiveDirPath(String dirPath) {
        edtOutput.setText(dirPath);
        edtOutput.setSelection(dirPath.length());
    }

    @Override
    protected ArchivePresenter initPresenter() {
        return new ArchivePresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryAllData();
    }

    @Override
    protected void onShow() {
        initial();
    }

    List<ArchiveInform> archiveInforms = new ArrayList<>();

    @Override
    public void updateArchiveInform(int type, String fileName, int mediaId, String dirName, int progress) {
        boolean isHas = false;
        for (int i = 0; i < archiveInforms.size(); i++) {
            if (type == 2) {
                if (archiveInforms.get(i).isThis(type, fileName, mediaId, dirName)) {
                    if (progress == -1) {
                        archiveInforms.get(i).setResult("已中断");
                    } else {
                        archiveInforms.get(i).setResult("当前进度" + progress + "%");
                    }
                    isHas = true;
                    break;
                }
            } else {
                if (archiveInforms.get(i).isThis(type, fileName, mediaId)) {
                    if (progress == -1) {
                        archiveInforms.get(i).setResult("已中断");
                    } else {
                        archiveInforms.get(i).setResult("当前进度" + progress + "%");
                    }
                    isHas = true;
                    break;
                }
            }
        }
        if (!isHas) {
            if (type == 2) {
                if (progress == -1) {
                    archiveInforms.add(new ArchiveInform(type, mediaId, dirName, fileName, "已中断"));
                } else {
                    archiveInforms.add(new ArchiveInform(type, mediaId, dirName, fileName, "当前进度" + progress + "%"));
                }
            } else {
                if (progress == -1) {
                    archiveInforms.add(new ArchiveInform(type, mediaId, fileName, "已中断"));
                } else {
                    archiveInforms.add(new ArchiveInform(type, mediaId, fileName, "当前进度" + progress + "%"));
                }
            }
        }
        updateArchiveInform(archiveInforms);
        if (progress == -1) {
            FileUtils.delete(Constant.DIR_ARCHIVE_TEMP);
            isDownloading = false;
        }
    }

    private void updateArchiveInform(List<ArchiveInform> archiveInforms) {
        getActivity().runOnUiThread(() -> {
            if (informAdapter == null) {
                informAdapter = new ArchiveInformAdapter(R.layout.item_admin_archive_operate, archiveInforms);
                rv_operate.setLayoutManager(new LinearLayoutManager(getContext()));
                rv_operate.setAdapter(informAdapter);
            } else {
                informAdapter.notifyDataSetChanged();
            }
            if (!archiveInforms.isEmpty()) {
                int lastIndex = archiveInforms.size() - 1;
                rv_operate.scrollToPosition(lastIndex);
            }
        });
    }
}
