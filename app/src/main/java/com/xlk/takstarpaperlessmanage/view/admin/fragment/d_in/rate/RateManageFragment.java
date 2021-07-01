package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.rate;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceFilescorevote;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.FileScoreAdapter;
import com.xlk.takstarpaperlessmanage.adapter.MemberDetailAdapter;
import com.xlk.takstarpaperlessmanage.adapter.ScoreSubmitMemberAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.FileUtil;
import com.xlk.takstarpaperlessmanage.util.JxlUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc 会中管理-评分管理 会后查看-评分查看
 */
public class RateManageFragment extends BaseFragment<RateManagePresenter> implements RateManageContract.View {

    private LinearLayout root_view;
    private RecyclerView rv_file_score;
    private FileScoreAdapter fileScoreAdapter;
    private MemberDetailAdapter memberDetailAdapter;
    private ScoreSubmitMemberAdapter scoreSubmitMemberAdapter;
    private boolean isManage;
    private EditText edt_save_address;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rate_manage;
    }

    @Override
    protected void initView(View inflate) {
        root_view = inflate.findViewById(R.id.root_view);
        rv_file_score = inflate.findViewById(R.id.rv_file_score);
    }

    @Override
    protected RateManagePresenter initPresenter() {
        return new RateManagePresenter(this);
    }

    @Override
    protected void initial() {
        isManage = getArguments().getBoolean("isManage");
        fileScoreAdapter = null;
        LogUtils.e("是否是评分管理界面 isManage=" + isManage);
        presenter.queryMemberDetailed();
        presenter.queryFileScore();
    }

    @Override
    protected void onShow() {
        initial();
    }

    @Override
    public void updateScoreSubmitMemberList() {
        if (scoreSubmitMemberAdapter == null) {
            scoreSubmitMemberAdapter = new ScoreSubmitMemberAdapter(presenter.scoreMembers);
        } else {
            scoreSubmitMemberAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateMemberDetailedList() {
        if (memberDetailAdapter == null) {
            memberDetailAdapter = new MemberDetailAdapter(presenter.members);
        } else {
            memberDetailAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateFileScoreList() {
        if (fileScoreAdapter == null) {
            fileScoreAdapter = new FileScoreAdapter(presenter.fileScores, isManage);
            fileScoreAdapter.addChildClickViewIds(R.id.operation_view_1, R.id.operation_view_2, R.id.operation_view_3);
            rv_file_score.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_file_score.addItemDecoration(new RvItemDecoration(getContext()));
            rv_file_score.setAdapter(fileScoreAdapter);
            fileScoreAdapter.setOnItemChildClickListener((adapter, view, position) -> {
                InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore item = presenter.fileScores.get(position);
                if (view.getId() == R.id.operation_view_1) {//发起
                    if (item.getVotestate() == InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_notvote_VALUE) {
                        showMemberPop(item);
                    } else {
                        ToastUtil.showShort(R.string.please_choose_notvote);
                    }
                } else if (view.getId() == R.id.operation_view_2) {//结束
                    if (item.getVotestate() == InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_voteing_VALUE) {
                        jni.stopScore(item.getVoteid());
                    } else {
                        ToastUtil.showShort(R.string.please_choose_voteing);
                    }
                } else {//查看
                    presenter.queryScoreSubmittedScore(item.getVoteid());
                    viewFileScore(item);
                }
            });
        } else {
            fileScoreAdapter.notifyDataSetChanged();
        }
    }

    private void viewFileScore(InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_file_score, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, root_view, Gravity.CENTER, width1 / 2, 0);

        TextView tv_content = inflate.findViewById(R.id.tv_content);
        tv_content.setText(item.getContent().toStringUtf8());
        TextView tv_file_name = inflate.findViewById(R.id.tv_file_name);
        TextView tv_view_file = inflate.findViewById(R.id.tv_view_file);
        String fileName = jni.queryFileNameByMediaId(item.getFileid());
        tv_file_name.setText(fileName);
        tv_view_file.setVisibility(fileName.isEmpty() ? View.GONE : View.VISIBLE);
        tv_view_file.setOnClickListener(v -> {
            String filePath = Constant.file_dir + fileName;
            boolean fileExists = FileUtils.isFileExists(filePath);
            if (fileExists) {
                FileUtil.openFile(getContext(), filePath);
            } else {
                jni.downloadFile(filePath, item.getFileid(), 1, 0, Constant.DOWNLOAD_OPEN_FILE);
            }
        });

        RecyclerView rv_rate_file = inflate.findViewById(R.id.rv_rate_file);
        TextView tv_notation = inflate.findViewById(R.id.tv_notation);
        tv_notation.setText(item.getMode() == 1 ? getString(R.string.yes) : getString(R.string.no));
        TextView tv_yprs = inflate.findViewById(R.id.tv_yprs);
        tv_yprs.setText(String.valueOf(item.getShouldmembernum()));
        TextView tv_yiprs = inflate.findViewById(R.id.tv_yiprs);
        tv_yiprs.setText(String.valueOf(item.getRealmembernum()));
        TextView tv_a_score = inflate.findViewById(R.id.tv_a_score);
        TextView tv_b_score = inflate.findViewById(R.id.tv_b_score);
        TextView tv_c_score = inflate.findViewById(R.id.tv_c_score);
        TextView tv_d_score = inflate.findViewById(R.id.tv_d_score);
        List<Integer> itemsumscoreList = item.getItemsumscoreList();
        int total = 0;
        for (int i = 0; i < itemsumscoreList.size(); i++) {
            String text = String.valueOf(itemsumscoreList.get(i));
            if (i == 0) tv_a_score.setText(text);
            if (i == 1) tv_b_score.setText(text);
            if (i == 2) tv_c_score.setText(text);
            if (i == 3) tv_d_score.setText(text);
            total += itemsumscoreList.get(i);
        }
        TextView tv_total_score = inflate.findViewById(R.id.tv_total_score);
        tv_total_score.setText(String.valueOf(total));
        TextView tv_average_score = inflate.findViewById(R.id.tv_average_score);
        if (item.getRealmembernum() > 0) {
            double div = Constant.div(total, item.getRealmembernum(), 2);
            tv_average_score.setText(String.valueOf(div));
        }
        RecyclerView rv_member = inflate.findViewById(R.id.rv_member);
        if (scoreSubmitMemberAdapter == null) {
            scoreSubmitMemberAdapter = new ScoreSubmitMemberAdapter(presenter.scoreMembers);
        }
        rv_member.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_member.addItemDecoration(new RvItemDecoration(getContext()));
        rv_member.setAdapter(scoreSubmitMemberAdapter);
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_export).setOnClickListener(v -> {
            if (presenter.scoreMembers.isEmpty()) {
                ToastUtil.showShort(R.string.no_data_to_export);
                return;
            }
//            JxlUtil.exportSingleScoreResult(item);
            showExportFilePop(item);
            pop.dismiss();
        });
    }

    private void showExportFilePop(InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_export_config, null);
        PopupWindow pop = PopUtil.createHalfPop(inflate, root_view);
        EditText edt_file_name = inflate.findViewById(R.id.edt_file_name);
        edt_save_address = inflate.findViewById(R.id.edt_save_address);
        edt_save_address.setKeyListener(null);
        inflate.findViewById(R.id.btn_choose_dir).setOnClickListener(v -> {
            String currentDirPath = edt_save_address.getText().toString().trim();
            if (currentDirPath.isEmpty()) {
                currentDirPath = Constant.root_dir;
            }
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.CHOOSE_DIR_PATH).objects(Constant.CHOOSE_DIR_TYPE_EXPORT_SOCRE_RESULT, currentDirPath).build());
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
            JxlUtil.exportSingleScoreResult(fileName, addr, item);
            pop.dismiss();
        });
    }

    @Override
    public void updateExportDirPath(String dirPath) {
        if (edt_save_address != null) {
            edt_save_address.setText(dirPath);
        }
    }

    private void showMemberPop(InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore item) {
        memberDetailAdapter.setChooseAll(false);
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_vote_member, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, root_view, Gravity.CENTER, width1 / 2, 0);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.choose_rate_member));
        CheckBox cb_all = inflate.findViewById(R.id.item_view_1);
        RecyclerView rv_content = inflate.findViewById(R.id.rv_content);
        rv_content.setAdapter(memberDetailAdapter);
        rv_content.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_content.addItemDecoration(new RvItemDecoration(getContext()));
        memberDetailAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                memberDetailAdapter.setChoose(presenter.members.get(position).getMemberid());
                cb_all.setChecked(memberDetailAdapter.isChooseAll());
            }
        });
        cb_all.setOnClickListener(v -> {
            boolean checked = cb_all.isChecked();
            cb_all.setChecked(checked);
            memberDetailAdapter.setChooseAll(checked);
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            List<Integer> memberIds = memberDetailAdapter.getSelectedIds();
            if (memberIds.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_member_first);
                return;
            }
            jni.launchScore(item.getVoteid(), InterfaceMacro.Pb_VoteStartFlag.Pb_MEET_VOTING_FLAG_ZERO_VALUE, 0, memberIds);
            pop.dismiss();
        });
    }

}
