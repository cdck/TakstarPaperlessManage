package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.vote;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.blankj.utilcode.util.UriUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.google.protobuf.ByteString;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.VoteAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.JxlUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xlk.takstarpaperlessmanage.model.Constant.s2b;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
public class VoteFragment extends BaseFragment<VotePresenter> implements VoteContract.View {

    private RecyclerView rv_vote;
    private VoteAdapter voteAdapter;
    private int vote_type;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_vote;
    }

    @Override
    protected void initView(View inflate) {
        rv_vote = inflate.findViewById(R.id.rv_vote);
        inflate.findViewById(R.id.btn_add).setOnClickListener(v -> {
            if (vote_type == InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE) {
                showAddModifyVotePop(null);
            } else {
                showAddModifyElectionPop(null);
            }
        });
        inflate.findViewById(R.id.btn_import).setOnClickListener(v -> {
            chooseLocalFile(REQUEST_CODE_IMPORT_VOTE);
        });
        inflate.findViewById(R.id.btn_export).setOnClickListener(v -> {
            boolean isVote = vote_type == InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE;
            if (presenter.voteInfos.isEmpty()) {
                ToastUtil.showShort(R.string.no_data_to_export);
                return;
            }
            JxlUtil.exportVoteInfo(presenter.voteInfos,
                    isVote ? getString(R.string.vote_information) : getString(R.string.election_information),
                    isVote ? getString(R.string.vote_content) : getString(R.string.election_content)
            );
        });
    }

    @Override
    protected VotePresenter initPresenter() {
        return new VotePresenter(this);
    }

    @Override
    protected void initial() {
        vote_type = getArguments().getInt("vote_type");
        presenter.setVoteType(vote_type);
        presenter.queryVote();
    }

    @Override
    protected void onShow() {
        initial();
    }

    @Override
    public void updateVoteList() {
        if (voteAdapter == null) {
            voteAdapter = new VoteAdapter(presenter.voteInfos);
            voteAdapter.addChildClickViewIds(R.id.operation_view_1, R.id.operation_view_2);
            rv_vote.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_vote.addItemDecoration(new RvItemDecoration(getContext()));
            rv_vote.setAdapter(voteAdapter);
            voteAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                    InterfaceVote.pbui_Item_MeetVoteDetailInfo item = presenter.voteInfos.get(position);
                    if (view.getId() == R.id.operation_view_1) {
                        if (vote_type == InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE) {
                            showAddModifyVotePop(item);
                        } else {
                            showAddModifyElectionPop(item);
                        }
                    } else {//删除
                        showDeletePop(item);
                    }
                }
            });
        } else {
            voteAdapter.notifyDataSetChanged();
        }
    }

    private void showDeletePop(InterfaceVote.pbui_Item_MeetVoteDetailInfo item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_delete, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rv_vote, Gravity.CENTER, width1 / 2, 0);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        TextView tv_hint = inflate.findViewById(R.id.tv_hint);
        ImageView iv_icon = inflate.findViewById(R.id.iv_icon);
        tv_hint.setText(vote_type == InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE
                ? getString(R.string.delete_vote_hint) : getString(R.string.delete_election_hint));
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            jni.deleteVote(item.getVoteid());
            pop.dismiss();
        });
    }

    private void showAddModifyVotePop(InterfaceVote.pbui_Item_MeetVoteDetailInfo item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_modify_vote, null, false);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, rv_vote, Gravity.CENTER, width1 / 2, 0);
        boolean isAdd = item == null;
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(isAdd ? getString(R.string.create_vote) : getString(R.string.modify_vote));
        EditText edt_content = inflate.findViewById(R.id.edt_content);
        Spinner sp_notation = inflate.findViewById(R.id.sp_notation);
        if (!isAdd) {
            int mode = item.getMode();
            edt_content.setText(item.getContent().toStringUtf8());
            sp_notation.setSelection(mode);
        }
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String content = edt_content.getText().toString();
            int modeIndex = sp_notation.getSelectedItemPosition();
            if (content.isEmpty()) {
                ToastUtil.showShort(R.string.please_enter_election_content_first);
                return;
            }
            List<ByteString> answers = new ArrayList<>();
            answers.add(s2b("赞成"));
            answers.add(s2b("反对"));
            answers.add(s2b("弃权"));
            InterfaceVote.pbui_Item_MeetOnVotingDetailInfo.Builder builder = InterfaceVote.pbui_Item_MeetOnVotingDetailInfo.newBuilder()
                    .setContent(s2b(content))
                    .setType(InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_SINGLE_VALUE)
                    .setMaintype(InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE)
                    .setMode(modeIndex)
                    .setSelectcount(answers.size())
                    .addAllText(answers);
            if (isAdd) {
                builder.setTimeouts(300);
                jni.createVote(builder.build());
            } else {
                builder.setTimeouts(item.getTimeouts());
                builder.setVoteid(item.getVoteid());
                jni.modifyVote(builder.build());
            }
            pop.dismiss();
        });
    }

    private void showAddModifyElectionPop(InterfaceVote.pbui_Item_MeetVoteDetailInfo item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_modify_election, null, false);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, rv_vote, Gravity.CENTER, width1 / 2, 0);
        boolean isAdd = item == null;
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(isAdd ? getString(R.string.create_election) : getString(R.string.modify_election));
        EditText edt_content = inflate.findViewById(R.id.edt_content);
        Spinner sp_type = inflate.findViewById(R.id.sp_type);
        Spinner sp_notation = inflate.findViewById(R.id.sp_notation);
        LinearLayout ll_option_a = inflate.findViewById(R.id.ll_option_a);
        LinearLayout ll_option_b = inflate.findViewById(R.id.ll_option_b);
        LinearLayout ll_option_c = inflate.findViewById(R.id.ll_option_c);
        LinearLayout ll_option_d = inflate.findViewById(R.id.ll_option_d);
        LinearLayout ll_option_e = inflate.findViewById(R.id.ll_option_e);
        EditText edt_option_a = inflate.findViewById(R.id.edt_option_a);
        EditText edt_option_b = inflate.findViewById(R.id.edt_option_b);
        EditText edt_option_c = inflate.findViewById(R.id.edt_option_c);
        EditText edt_option_d = inflate.findViewById(R.id.edt_option_d);
        EditText edt_option_e = inflate.findViewById(R.id.edt_option_e);
        if (!isAdd) {
            int mode = item.getMode();
            int type = item.getType();
            edt_content.setText(item.getContent().toStringUtf8());
            List<InterfaceVote.pbui_SubItem_VoteItemInfo> itemList = item.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceVote.pbui_SubItem_VoteItemInfo info = itemList.get(i);
                String text = info.getText().toStringUtf8();
                if (i == 0) edt_option_a.setText(text);
                if (i == 1) edt_option_b.setText(text);
                if (i == 2) edt_option_c.setText(text);
                if (i == 3) edt_option_d.setText(text);
                if (i == 4) edt_option_e.setText(text);
            }
            sp_notation.setSelection(mode);
            sp_type.setSelection(type);
        }
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String content = edt_content.getText().toString();
            int modeIndex = sp_notation.getSelectedItemPosition();
            int typeIndex = sp_type.getSelectedItemPosition();
            String a = edt_option_a.getText().toString();
            String b = edt_option_b.getText().toString();
            String c = edt_option_c.getText().toString();
            String d = edt_option_d.getText().toString();
            String e = edt_option_e.getText().toString();
            if (content.isEmpty()) {
                ToastUtil.showShort(R.string.please_enter_election_content_first);
                return;
            }
            if (typeIndex == 2 || typeIndex == 3 || typeIndex == 4) {
                //必须填写5个答案
                if (a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty() || e.isEmpty()) {
                    ToastUtil.showShort(R.string.please_enter_the_option_in_full);
                    return;
                }
            } else {
                if (a.isEmpty() || b.isEmpty() || c.isEmpty()) {
                    ToastUtil.showShort(R.string.first_three_options_must_be_filled_in);
                    return;
                }
            }
            List<ByteString> answers = new ArrayList<>();
            answers.add(s2b(a));
            answers.add(s2b(b));
            answers.add(s2b(c));
            if (!d.isEmpty()) answers.add(s2b(d));
            if (!e.isEmpty()) answers.add(s2b(e));
            InterfaceVote.pbui_Item_MeetOnVotingDetailInfo.Builder builder = InterfaceVote.pbui_Item_MeetOnVotingDetailInfo.newBuilder()
                    .setContent(s2b(content))
                    .setMaintype(InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_election_VALUE)
                    .setMode(modeIndex)
                    .setType(typeIndex)
                    .setSelectcount(answers.size())
                    .addAllText(answers);
            if (isAdd) {
                builder.setTimeouts(300);
                jni.createVote(builder.build());
            } else {
                builder.setTimeouts(item.getTimeouts());
                builder.setVoteid(item.getVoteid());
                jni.modifyVote(builder.build());
            }
            pop.dismiss();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_IMPORT_VOTE) {
                Uri uri = data.getData();
                File file = UriUtils.uri2File(uri);
                if (file != null) {
                    List<InterfaceVote.pbui_Item_MeetOnVotingDetailInfo> votes = JxlUtil.readVoteXls(file.getAbsolutePath(), vote_type);
                    jni.createVote(votes);
                }
            }
        }
    }
}
