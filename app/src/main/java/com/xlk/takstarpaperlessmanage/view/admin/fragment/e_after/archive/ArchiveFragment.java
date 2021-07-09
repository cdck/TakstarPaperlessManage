package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.archive;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.helper.task.BasicInformationTask;
import com.xlk.takstarpaperlessmanage.helper.archive.ConsumptionTask;
import com.xlk.takstarpaperlessmanage.helper.archive.LineUpTaskHelp;
import com.xlk.takstarpaperlessmanage.helper.task.DownloadFileTask;
import com.xlk.takstarpaperlessmanage.helper.task.MemberTask;
import com.xlk.takstarpaperlessmanage.helper.task.SignInTask;
import com.xlk.takstarpaperlessmanage.helper.task.VoteTask;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/5/28.
 * @desc
 */
public class ArchiveFragment extends BaseFragment<ArchivePresenter> implements ArchiveContract.View {
    private LinearLayout rootView;
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
    private Thread archiveThread;
    private RecyclerView rv_operate;
    private ArchiveInformAdapter informAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_archive;
    }

    @Override
    protected void initView(View inflate) {
        rootView = (LinearLayout) inflate.findViewById(R.id.root_view);
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
            if (presenter.hasStarted()) {
                ToastUtils.showShort(R.string.already_archive);
                return;
            }
            boolean checked = cbEncryption.isChecked();
            cbEncryption.setChecked(checked);
            if (!checked) {
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
        });
        btnCancel = (Button) inflate.findViewById(R.id.btn_cancel);
        btnArchive = (Button) inflate.findViewById(R.id.btn_archive);
        edtOutput.setKeyListener(null);
        edtOutput.setText(Constant.DIR_ARCHIVE_ZIP);
        edtOutput.setSelection(Constant.DIR_ARCHIVE_ZIP.length());
        btnModify.setOnClickListener(v -> {
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.CHOOSE_DIR_PATH)
                    .objects(Constant.CHOOSE_DIR_TYPE_ARCHIVE, Constant.root_dir).build());
        });
        btnCancel.setOnClickListener(v -> {
            if (lineUpTaskHelp != null) {
                lineUpTaskHelp.deleteAndCancelAllTask();
            }
            /*
            App.threadPool.execute(() -> {
                jni.clearDownload();
                presenter.cancelArchive(true);
                if (archiveThread != null && archiveThread.getState() == Thread.State.RUNNABLE) {
                    try {
                        archiveThread.interrupt();
                        LogUtils.e("archiveThread interrupt");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        archiveThread = null;
                        LogUtils.e("archiveThread设置为null");
                    }
                }
                presenter.cancelArchive();
                presenter.cancelArchive(false);
            });
            */
        });
        btnArchive.setOnClickListener(v -> {
            start();
            /*
            if (presenter.hasStarted()) {
                ToastUtils.showShort(R.string.please_wait_archive_complete_first);
                return;
            }
            if (archiveThread != null) {
                try {
                    archiveThread.interrupt();
                    LogUtils.e("archiveThread interrupt");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    archiveThread = null;
                    LogUtils.e("archiveThread设置为null");
                }
            }
            archiveThread = new ArchiveThread();
            archiveThread.start();
            */
//            App.threadPool.execute(() -> {
//                boolean isEncryption = cbEncryption.isChecked();
//                presenter.setEncryption(isEncryption);
//                if (cbArchiveAll.isChecked()) {
//                    presenter.archiveAll();
//                } else {
//                    presenter.archiveSelected(cbMeetingBasicInformation.isChecked(),
//                            cbMemberInformation.isChecked(), cbSignInformation.isChecked(),
//                            cbVoteResult.isChecked(), cbSharedFile.isChecked(),
//                            cbAnnotateFile.isChecked(), cbMeetingMaterial.isChecked());
//                }
//            });
        });
    }

    LineUpTaskHelp lineUpTaskHelp;

    private void start() {
        initLineUpTaskHelp();
        if (cbMeetingBasicInformation.isChecked()) {
            BasicInformationTask task = new BasicInformationTask(presenter.getBasicInformationTaskInfo());
            task.taskNo = "会议基本信息";
//            task.timeOut = 30 * 1000;
            lineUpTaskHelp.addTask(task);
        }
        if (cbMemberInformation.isChecked()) {
            MemberTask task = new MemberTask(presenter.getMemberTaskInfo());
            task.taskNo = "参会人员信息";
//            task.timeOut = 30 * 1000;
            lineUpTaskHelp.addTask(task);
        }
        if (cbSignInformation.isChecked()) {
            SignInTask task = new SignInTask(presenter.getSignInTaskInfo());
            task.taskNo = "会议签到信息";
//            task.timeOut = 30 * 1000;
            lineUpTaskHelp.addTask(task);
        }
        if (cbVoteResult.isChecked()) {
            VoteTask task = new VoteTask(presenter.getVoteTaskInfo());
            task.taskNo = "会议投票结果";
//            task.timeOut = 30 * 1000;
            lineUpTaskHelp.addTask(task);
        }
        if (cbSharedFile.isChecked()) {
            presenter.addDownloadShareFileTask(lineUpTaskHelp);
        }
        if (cbAnnotateFile.isChecked()) {
            presenter.addDownloadAnnotationFileTask(lineUpTaskHelp);
        }
        if (cbMeetingMaterial.isChecked()) {
            presenter.addDownloadMeetDataFileTask(lineUpTaskHelp);
        }
        presenter.addNextDownloadShareFileTask();
    }

    private void initLineUpTaskHelp() {
        lineUpTaskHelp = LineUpTaskHelp.getInstance();
        lineUpTaskHelp.setOnTaskListener(new LineUpTaskHelp.OnTaskListener() {
            @Override
            public void exNextTask(ConsumptionTask task) {
                LogUtils.d("exNextTask " + task.taskNo + ",剩余任务数量=" + lineUpTaskHelp.getTaskCount());
                if (task instanceof BasicInformationTask) {
                    BasicInformationTask basicInformationTask = (BasicInformationTask) task;
                    basicInformationTask.run();
                } else if (task instanceof MemberTask) {
                    MemberTask memberTask = (MemberTask) task;
                    memberTask.run();
                } else if (task instanceof SignInTask) {
                    SignInTask signInTask = (SignInTask) task;
                    signInTask.run();
                } else if (task instanceof VoteTask) {
                    VoteTask voteTask = (VoteTask) task;
                    voteTask.run();
                } else if (task instanceof DownloadFileTask) {
                    DownloadFileTask downloadFileTask = (DownloadFileTask) task;
                    downloadFileTask.run();
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

    class ArchiveThread extends Thread {

        public ArchiveThread() {
            super("ArchiveThread");
        }

        @Override
        public void run() {
            try {
                boolean isEncryption = cbEncryption.isChecked();
                presenter.setEncryption(isEncryption);
                LogUtils.e("开始归档 当前线程id=" + Thread.currentThread().getId() + "-" + Thread.currentThread().getName());
                if (cbArchiveAll.isChecked()) {
                    presenter.archiveAll();
                } else {
                    presenter.archiveSelected(cbMeetingBasicInformation.isChecked(),
                            cbMemberInformation.isChecked(), cbSignInformation.isChecked(),
                            cbVoteResult.isChecked(), cbSharedFile.isChecked(),
                            cbAnnotateFile.isChecked(), cbMeetingMaterial.isChecked());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    @Override
    public void updateArchiveInform(List<ArchiveInform> archiveInforms) {
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
                ArchiveInform archiveInform = archiveInforms.get(lastIndex);
                if (archiveInform.getContent().equals("压缩完毕")) {
                    rv_operate.scrollToPosition(lastIndex);
                }
            }
        });
    }
}
