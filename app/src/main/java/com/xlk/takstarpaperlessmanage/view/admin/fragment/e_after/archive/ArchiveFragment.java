package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.archive;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xlk.takstarpaperlessmanage.App;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;

import java.util.List;

import kotlin.collections.ArrayDeque;

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
    private LinearLayout llStatus0;
    private TextView tvStatus0;
    private LinearLayout llStatus1;
    private TextView tvStatus1;
    private LinearLayout llStatus2;
    private TextView tvStatus2;
    private LinearLayout llStatus3;
    private TextView tvStatus3;
    private LinearLayout llStatus4;
    private TextView tvStatus4;
    private LinearLayout llStatus5;
    private TextView tvStatus5;
    private LinearLayout llStatus6;
    private TextView tvStatus6;
    private CheckBox cbEncryption;
    private Button btnCancel;
    private Button btnArchive;

    private ArchiveTask archiveTask;
    private Thread archiveThread;
    private volatile boolean exitFlag = true;

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
        llStatus0 = (LinearLayout) inflate.findViewById(R.id.ll_status_0);
        tvStatus0 = (TextView) inflate.findViewById(R.id.tv_status_0);
        llStatus1 = (LinearLayout) inflate.findViewById(R.id.ll_status_1);
        tvStatus1 = (TextView) inflate.findViewById(R.id.tv_status_1);
        llStatus2 = (LinearLayout) inflate.findViewById(R.id.ll_status_2);
        tvStatus2 = (TextView) inflate.findViewById(R.id.tv_status_2);
        llStatus3 = (LinearLayout) inflate.findViewById(R.id.ll_status_3);
        tvStatus3 = (TextView) inflate.findViewById(R.id.tv_status_3);
        llStatus4 = (LinearLayout) inflate.findViewById(R.id.ll_status_4);
        tvStatus4 = (TextView) inflate.findViewById(R.id.tv_status_4);
        llStatus5 = (LinearLayout) inflate.findViewById(R.id.ll_status_5);
        tvStatus5 = (TextView) inflate.findViewById(R.id.tv_status_5);
        llStatus6 = (LinearLayout) inflate.findViewById(R.id.ll_status_6);
        tvStatus6 = (TextView) inflate.findViewById(R.id.tv_status_6);
        cbEncryption = (CheckBox) inflate.findViewById(R.id.cb_encryption);
        btnCancel = (Button) inflate.findViewById(R.id.btn_cancel);
        btnArchive = (Button) inflate.findViewById(R.id.btn_archive);
        edtOutput.setText(Constant.DIR_ARCHIVE_TEMP);
        edtOutput.setKeyListener(null);
        btnCancel.setOnClickListener(v -> {
            exitFlag = false;
            if (archiveThread != null) {
                archiveThread.interrupt();
            }
        });
        archiveTask = new ArchiveTask();
        archiveThread = new Thread(archiveTask);
        btnArchive.setOnClickListener(v -> {
            if (presenter.hasStarted()) {
                ToastUtils.showShort(R.string.please_wait_archive_complete_first);
                return;
            }
            archiveThread.start();

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

    class ArchiveTask implements Runnable {

        @Override
        public void run() {
            while (exitFlag) {
                if (archiveThread.isInterrupted()) {
                    LogUtils.e("archiveThread isInterrupted");
                    break;
                }
                try {
                    boolean isEncryption = cbEncryption.isChecked();
                    presenter.setEncryption(isEncryption);
                    if (cbArchiveAll.isChecked()) {
                        presenter.archiveAll();
                    } else {
                        presenter.archiveSelected(cbMeetingBasicInformation.isChecked(),
                                cbMemberInformation.isChecked(), cbSignInformation.isChecked(),
                                cbVoteResult.isChecked(), cbSharedFile.isChecked(),
                                cbAnnotateFile.isChecked(), cbMeetingMaterial.isChecked());
                    }
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
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

        });
    }

    @Override
    public void setAllWaitingStatus() {
        getActivity().runOnUiThread(() -> {
            tvStatus0.setText("等待");
            tvStatus1.setText("等待");
            tvStatus2.setText("等待");
            tvStatus3.setText("等待");
            tvStatus4.setText("等待");
            tvStatus5.setText("等待");
            tvStatus6.setText("等待");
        });
    }

    @Override
    public void updateMeetingBasicInformation(String status) {
        getActivity().runOnUiThread(() -> {
            tvStatus0.setText(status);
        });
    }

    @Override
    public void updateAttendeeInformation(String status) {
        getActivity().runOnUiThread(() -> {
            tvStatus1.setText(status);
        });
    }

    @Override
    public void updateConferenceSignInInformation(String status) {
        getActivity().runOnUiThread(() -> {
            tvStatus2.setText(status);
        });
    }

    @Override
    public void updateMeetingVoteResult(String status) {
        getActivity().runOnUiThread(() -> {
            tvStatus3.setText(status);
        });
    }

    @Override
    public void updateShardFile(String status) {
        getActivity().runOnUiThread(() -> {
            tvStatus4.setText(status);
        });
    }

    @Override
    public void updateAnnotateFile(String status) {
        getActivity().runOnUiThread(() -> {
            tvStatus5.setText(status);
        });
    }

    @Override
    public void updateMeetingMaterial(String status) {
        getActivity().runOnUiThread(() -> {
            tvStatus6.setText(status);
        });
    }
}
