package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.vote;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.MemberDetailAdapter;
import com.xlk.takstarpaperlessmanage.adapter.SubmitMemberAdapter;
import com.xlk.takstarpaperlessmanage.adapter.VoteManageAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.bean.ExportSubmitMember;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.DateUtil;
import com.xlk.takstarpaperlessmanage.util.JxlUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/5/24.
 * @desc
 */
public class VoteManageFragment extends BaseFragment<VoteManagePresenter> implements VoteManageContract.View {

    private RecyclerView rv_content;
    private TextView tv_vote_type, tv_vote_or_election;
    private int voteType;
    private VoteManageAdapter voteManageAdapter;
    private boolean isVote;
    private MemberDetailAdapter memberDetailAdapter;
    private EditText edt_save_address;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_vote_manage;
    }

    @Override
    protected void initView(View inflate) {
        rv_content = inflate.findViewById(R.id.rv_content);
        tv_vote_type = inflate.findViewById(R.id.tv_vote_type);
        tv_vote_or_election = inflate.findViewById(R.id.tv_vote_or_election);
    }

    @Override
    protected VoteManagePresenter initPresenter() {
        return new VoteManagePresenter(this);
    }

    @Override
    protected void initial() {
        voteType = getArguments().getInt("vote_type");
        isVote = voteType == InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE;
        tv_vote_or_election.setText(isVote ? getString(R.string.vote_count) : getString(R.string.election_count));
        tv_vote_type.setText(isVote ? getString(R.string.vote_content) : getString(R.string.election_content));
        presenter.queryMemberDetailed();
        presenter.setVoteType(voteType);
        presenter.queryVote();
    }

    @Override
    protected void onShow() {
        initial();
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
    public void updateVoteList() {
        if (voteManageAdapter == null) {
            voteManageAdapter = new VoteManageAdapter(presenter.voteInfos);
            voteManageAdapter.addChildClickViewIds(R.id.operation_view_1, R.id.operation_view_2, R.id.operation_view_3);
            rv_content.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_content.addItemDecoration(new RvItemDecoration(getContext()));
            rv_content.setAdapter(voteManageAdapter);
            voteManageAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                    InterfaceVote.pbui_Item_MeetVoteDetailInfo item = presenter.voteInfos.get(position);
                    int votestate = item.getVotestate();
                    if (view.getId() == R.id.operation_view_1) {//发起
                        if (votestate == InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_notvote_VALUE) {
                            showMemberPop(item);
                        } else {
                            ToastUtils.showShort(R.string.please_choose_not_launch_vote);
                        }
                    } else if (view.getId() == R.id.operation_view_2) {//结束
                        if (votestate == InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_voteing_VALUE) {
                            jni.stopVote(item.getVoteid());
                        } else {
                            ToastUtils.showShort(R.string.please_choose_ongoing_vote);
                        }
                    } else {//查看
                        if (votestate == InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_notvote_VALUE) {
                            ToastUtil.showShort(R.string.cannot_view_notvote);
                            return;
                        }
                        presenter.queryVoteSubmittedMember(item);
                    }
                }
            });
        } else {
            voteManageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showSubmittedPop(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_view_vote, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rv_content, Gravity.CENTER, width1 / 2, 0);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        RecyclerView rv_content = inflate.findViewById(R.id.rv_content);
        SubmitMemberAdapter submitMemberAdapter = new SubmitMemberAdapter(presenter.submitMembers);
        rv_content.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_content.addItemDecoration(new RvItemDecoration(getContext()));
        rv_content.setAdapter(submitMemberAdapter);
        tv_title.setText(isVote ? getString(R.string.vote_details) : getString(R.string.election_details));
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_export).setOnClickListener(v -> {
            showExportFilePop(vote);
            pop.dismiss();
//            String[] strings = presenter.queryYd(vote);
//            String createTime = DateUtil.nowDate();
//            ExportSubmitMember exportSubmitMember = new ExportSubmitMember(vote.getContent().toStringUtf8(), createTime, strings[0], strings[1], strings[2], strings[3], presenter.submitMembers);
//            JxlUtil.exportSubmitMember(exportSubmitMember);
        });
    }

    private void showExportFilePop(InterfaceVote.pbui_Item_MeetVoteDetailInfo vote) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_export_config, null);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop  = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rv_content, Gravity.CENTER, width1 / 2, 0);
//        PopupWindow pop = PopUtil.createHalfPop(inflate, rv_content);
        EditText edt_file_name = inflate.findViewById(R.id.edt_file_name);
        edt_save_address = inflate.findViewById(R.id.edt_save_address);
        edt_save_address.setKeyListener(null);
        edt_save_address.setText(Constant.export_dir);
        edt_save_address.setSelection(Constant.export_dir.length());
        inflate.findViewById(R.id.btn_choose_dir).setOnClickListener(v -> {
            String currentDirPath = edt_save_address.getText().toString().trim();
            if (currentDirPath.isEmpty()) {
                currentDirPath = Constant.root_dir;
            }
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.CHOOSE_DIR_PATH).objects(Constant.CHOOSE_DIR_TYPE_EXPORT_VOTE_MANAGE, currentDirPath).build());
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
            String[] strings = presenter.queryYd(vote);
            String createTime = DateUtil.nowDate();
            ExportSubmitMember exportSubmitMember = new ExportSubmitMember(vote.getContent().toStringUtf8(), createTime, strings[0], strings[1], strings[2], strings[3], presenter.submitMembers);
            JxlUtil.exportSubmitMember(fileName, addr, exportSubmitMember);
            pop.dismiss();
        });
    }

    @Override
    public void updateExportDirPath(String dirPath) {
        if (edt_save_address != null) {
            edt_save_address.setText(dirPath);
            edt_save_address.setSelection(dirPath.length());
        }
    }

    private void showMemberPop(InterfaceVote.pbui_Item_MeetVoteDetailInfo item) {
        memberDetailAdapter.setChooseAll(false);
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_vote_member, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, rv_content, Gravity.CENTER, width1 / 2, 0);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.choose_vote_member));
        CheckBox cb_all = inflate.findViewById(R.id.item_view_1);
        RecyclerView rv_content = inflate.findViewById(R.id.rv_content);
        rv_content.setAdapter(memberDetailAdapter);
        rv_content.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_content.addItemDecoration(new RvItemDecoration(getContext()));
        memberDetailAdapter.setOnItemClickListener((adapter, view, position) -> {
            memberDetailAdapter.setChoose(presenter.members.get(position).getMemberid());
            cb_all.setChecked(memberDetailAdapter.isChooseAll());
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
            jni.launchVote(memberIds, item.getVoteid(), item.getTimeouts());
            pop.dismiss();
        });
    }
}
