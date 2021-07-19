package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.member;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfacePerson;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.App;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.CommonlyMemberAdapter;
import com.xlk.takstarpaperlessmanage.adapter.MemberAdapter;
import com.xlk.takstarpaperlessmanage.adapter.MemberPermissionAdapter;
import com.xlk.takstarpaperlessmanage.adapter.MemberRoleAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.bean.MemberPermissionBean;
import com.xlk.takstarpaperlessmanage.model.bean.MemberRoleBean;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.JxlUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import static com.xlk.takstarpaperlessmanage.model.Constant.election_entry;
import static com.xlk.takstarpaperlessmanage.model.Constant.s2b;

/**
 * @author Created by xlk on 2021/5/19.
 * @desc
 */
public class MemberFragment extends BaseFragment<MemberPresenter> implements MemberContract.View {
    private LinearLayout rootView;
    private RecyclerView rvContent;
    private MemberAdapter memberAdapter;
    private PopupWindow addOrModifyPop;
    private PopupWindow sortMemberPop;
    private PopupWindow deletePop;
    private final int REQUEST_CODE_MEMBER = 1;
    private PopupWindow exportPop;
    private CommonlyMemberAdapter commonlyMemberAdapter;
    private PopupWindow commonlyMemberPop;
    private PopupWindow memberPermissionPop;
    private MemberPermissionAdapter permissionAdapter;
    private MemberRoleAdapter memberRoleAdapter;
    private PopupWindow memberRolePop;
    private EditText edt_save_address;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_member;
    }

    @Override
    protected void initView(View inflate) {
        rootView = (LinearLayout) inflate.findViewById(R.id.root_view);
        inflate.findViewById(R.id.btn_add).setOnClickListener(this);
        inflate.findViewById(R.id.btn_sort).setOnClickListener(this);
        inflate.findViewById(R.id.btn_import).setOnClickListener(this);
        inflate.findViewById(R.id.btn_export).setOnClickListener(this);
        inflate.findViewById(R.id.btn_export_commonly).setOnClickListener(this);
        inflate.findViewById(R.id.btn_import_commonly).setOnClickListener(this);
        inflate.findViewById(R.id.btn_permission).setOnClickListener(this);
        inflate.findViewById(R.id.btn_role).setOnClickListener(this);
        rvContent = (RecyclerView) inflate.findViewById(R.id.rv_content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add: {
                showAddorModifyPop(null);
                break;
            }
            case R.id.btn_sort: {
                showSortPop(presenter.getSortMembers());
                break;
            }
            case R.id.btn_import: {
                chooseLocalFile(REQUEST_CODE_MEMBER);
                break;
            }
            case R.id.btn_export: {
                if (presenter.memberRoleBeans.isEmpty()) {
                    ToastUtils.showShort(R.string.no_data_to_export);
                    return;
                }
                showExportFilePop();
//                if (JxlUtil.exportMemberInfo(presenter.memberRoleBeans)) {
//                    ToastUtils.showShort(R.string.export_successful);
//                } else {
//                    ToastUtils.showShort(R.string.export_failed);
//                }
                break;
            }
            case R.id.btn_export_commonly: {
                createCommonlyMember();
                break;
            }
            case R.id.btn_import_commonly: {
                showCommonlyMemberPop();
                break;
            }
            case R.id.btn_permission: {
                showPermissionPop();
                break;
            }
            case R.id.btn_role: {
                showMemberRolePop();
                break;
            }
        }
    }

    private void showExportFilePop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_export_config, null);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop  = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rootView, Gravity.CENTER, width1 / 2, 0);
//        PopupWindow pop = PopUtil.createHalfPop(inflate, rvContent);
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
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.CHOOSE_DIR_PATH).objects(Constant.CHOOSE_DIR_TYPE_EXPORT_MEMBER, currentDirPath).build());
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
            App.threadPool.execute(() -> {
                String filePath = JxlUtil.exportMemberInfo(fileName, addr, presenter.memberRoleBeans);
                EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_EXPORT_SUCCESSFUL).objects(filePath).build());
            });
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

    @Override
    protected MemberPresenter initPresenter() {
        return new MemberPresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryMember();
        presenter.queryCommonlyMember();
        presenter.queryMemberPermission();
    }

    @Override
    public void updateMemberList() {
        if (memberAdapter == null) {
            memberAdapter = new MemberAdapter(presenter.members);
            memberAdapter.addChildClickViewIds(R.id.operation_view_1, R.id.operation_view_2);
            rvContent.setLayoutManager(new LinearLayoutManager(getContext()));
            rvContent.addItemDecoration(new RvItemDecoration(getContext()));
            rvContent.setAdapter(memberAdapter);
            memberAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    memberAdapter.setSelectedId(presenter.members.get(position).getPersonid());
                }
            });
            memberAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                    InterfaceMember.pbui_Item_MemberDetailInfo item = presenter.members.get(position);
                    if (view.getId() == R.id.operation_view_1) {//修改
                        showAddorModifyPop(item);
                    } else {//删除
                        showDeletePop(item);
                    }
                }
            });
        } else {
            memberAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateCommonlyMemberList() {
        if (commonlyMemberAdapter == null) {
            commonlyMemberAdapter = new CommonlyMemberAdapter(presenter.commonlyMembers);
        } else {
            commonlyMemberAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateMemberPermissionList() {
        if (permissionAdapter == null) {
            permissionAdapter = new MemberPermissionAdapter(presenter.memberPermissionBeans);
        } else {
            permissionAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateMemberRoleList() {
        if (memberRoleAdapter == null) {
            memberRoleAdapter = new MemberRoleAdapter(presenter.memberRoleBeans);
        } else {
            memberRoleAdapter.notifyDataSetChanged();
        }
    }

    private void showDeletePop(InterfaceMember.pbui_Item_MemberDetailInfo info) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_delete, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        deletePop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rootView, Gravity.CENTER, width1 / 2, 0);
        TextView tv_hint = inflate.findViewById(R.id.tv_hint);
        tv_hint.setText(getString(R.string.delete_member_hint));
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> deletePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> deletePop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            jni.delMember(info);
            deletePop.dismiss();
        });
    }

    private void showSortPop(List<InterfaceMember.pbui_Item_MemberDetailInfo> sortMembers) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_sort_member, null, false);
        initialPopupWindowXY();
        sortMemberPop = PopUtil.createCoverPopupWindow(inflate, rootView, popWidth, popHeight, popX, popY);
        RecyclerView rv_content = inflate.findViewById(R.id.rv_content);
        MemberAdapter sortMemberAdapter = new MemberAdapter(sortMembers, true);
        rv_content.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_content.addItemDecoration(new RvItemDecoration(getContext()));
        rv_content.setAdapter(sortMemberAdapter);
        sortMemberAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                sortMemberAdapter.setSelectedId(sortMembers.get(position).getPersonid());
            }
        });

        inflate.findViewById(R.id.btn_move_up).setOnClickListener(v -> {
            InterfaceMember.pbui_Item_MemberDetailInfo selectedMember = sortMemberAdapter.getSelectedMember();
            if (selectedMember == null) {
                ToastUtil.showShort(R.string.please_choose_member_first);
                return;
            }
            int index = 0;
            for (int i = 0; i < sortMembers.size(); i++) {
                if (selectedMember.getPersonid() == sortMembers.get(i).getPersonid()) {
                    index = i;
                    break;
                }
            }
            if (index == 0) {
                //要上移的目标已经是第一项，则移动到最下方
                sortMembers.remove(index);
                sortMembers.add(selectedMember);
            } else {
                Collections.swap(sortMembers, index, index - 1);
            }
            sortMemberAdapter.notifyDataSetChanged();
        });
        inflate.findViewById(R.id.btn_move_down).setOnClickListener(v -> {
            InterfaceMember.pbui_Item_MemberDetailInfo selectedMember = sortMemberAdapter.getSelectedMember();
            if (selectedMember == null) {
                ToastUtils.showShort(R.string.please_choose_member_first);
                return;
            }
            int index = 0;
            for (int i = 0; i < sortMembers.size(); i++) {
                if (selectedMember.getPersonid() == sortMembers.get(i).getPersonid()) {
                    index = i;
                    break;
                }
            }
            if (index == sortMembers.size() - 1) {
                //要下移的目标已经是最后一项，则进行移动到最上方
                List<InterfaceMember.pbui_Item_MemberDetailInfo> temps = new ArrayList<>();
                sortMembers.remove(index);
                temps.add(selectedMember);
                temps.addAll(sortMembers);
                sortMembers.clear();
                sortMembers.addAll(temps);
                temps.clear();
            } else {
                Collections.swap(sortMembers, index, index + 1);
            }
            sortMemberAdapter.notifyDataSetChanged();
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> sortMemberPop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> sortMemberPop.dismiss());
        inflate.findViewById(R.id.btn_save).setOnClickListener(v -> {
            List<Integer> ids = new ArrayList<>();
            for (int i = 0; i < sortMembers.size(); i++) {
                ids.add(sortMembers.get(i).getPersonid());
            }
            jni.modifyMemberSort(ids);
            sortMemberPop.dismiss();
        });
    }

    private void showAddorModifyPop(InterfaceMember.pbui_Item_MemberDetailInfo item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_modify_member, null, false);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        addOrModifyPop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, rootView, Gravity.CENTER, width1 / 2, 0);
        boolean isAdd = item == null;
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(isAdd ? getString(R.string.augment) : getString(R.string.modify));
        EditText edt_name = inflate.findViewById(R.id.edt_name);
        EditText edt_unit = inflate.findViewById(R.id.edt_unit);
        EditText edt_job = inflate.findViewById(R.id.edt_job);
        EditText edt_phone = inflate.findViewById(R.id.edt_phone);
        EditText edt_email = inflate.findViewById(R.id.edt_email);
        EditText edt_pwd = inflate.findViewById(R.id.edt_pwd);
        EditText edt_remarks = inflate.findViewById(R.id.edt_remarks);
        if (!isAdd) {
            edt_name.setText(item.getName().toStringUtf8());
            edt_unit.setText(item.getCompany().toStringUtf8());
            edt_job.setText(item.getJob().toStringUtf8());
            edt_phone.setText(item.getPhone().toStringUtf8());
            edt_email.setText(item.getEmail().toStringUtf8());
            edt_pwd.setText(item.getPassword().toStringUtf8());
            edt_remarks.setText(item.getComment().toStringUtf8());
        }
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> addOrModifyPop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> addOrModifyPop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String name = edt_name.getText().toString();
            String unit = edt_unit.getText().toString();
            String job = edt_job.getText().toString();
            String phone = edt_phone.getText().toString();
            String email = edt_email.getText().toString();
            String pwd = edt_pwd.getText().toString();
            String remarks = edt_remarks.getText().toString();
            if (name.isEmpty()) {
                ToastUtil.showShort(R.string.please_enter_name_first);
                return;
            }
            String regex = "^((13[0-9])|(14[579])|(15[0-35-9])|(16[2567])|(17[0-35-8])|(18[0-9])|(19[0-35-9]))\\d{8}$";
//            String regex="/^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$/";

            if (!phone.isEmpty() && !Pattern.matches(regex, phone)) {
                ToastUtil.showShort(R.string.incorrect_phone_format);
                return;
            }
            if (!email.isEmpty() && !RegexUtils.isEmail(email)) {
                ToastUtil.showShort(R.string.incorrect_email_format);
                return;
            }
            InterfaceMember.pbui_Item_MemberDetailInfo.Builder builder = InterfaceMember.pbui_Item_MemberDetailInfo.newBuilder();
            builder.setName(s2b(name));
            builder.setCompany(s2b(unit));
            builder.setCompany(s2b(job));
            builder.setPhone(s2b(phone));
            builder.setEmail(s2b(email));
            builder.setPassword(s2b(pwd));
            builder.setComment(s2b(remarks));
            if (isAdd) {
                jni.addMember(builder.build());
            } else {
                builder.setPersonid(item.getPersonid());
                jni.modifyMember(builder.build());
            }
            addOrModifyPop.dismiss();
        });
    }

    private void createCommonlyMember() {
        InterfaceMember.pbui_Item_MemberDetailInfo selectedMember = memberAdapter.getSelectedMember();
        if (selectedMember == null) {
            ToastUtils.showShort(R.string.please_choose_member_first);
            return;
        }
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_delete, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        exportPop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rootView, Gravity.CENTER, width1 / 2, 0);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        ImageView iv_icon = inflate.findViewById(R.id.iv_icon);
        TextView tv_hint = inflate.findViewById(R.id.tv_hint);
        iv_icon.setVisibility(View.GONE);
        tv_title.setText(getString(R.string.export_to_commonly_member));
        tv_hint.setText(getString(R.string.export_to_commonly_member_hint));
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> exportPop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> exportPop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            InterfacePerson.pbui_Item_PersonDetailInfo build = InterfacePerson.pbui_Item_PersonDetailInfo.newBuilder()
                    .setName(selectedMember.getName())
                    .setCompany(selectedMember.getCompany())
                    .setJob(selectedMember.getJob())
                    .setComment(selectedMember.getComment())
                    .setPhone(selectedMember.getPhone())
                    .setEmail(selectedMember.getEmail())
                    .setPassword(selectedMember.getPassword())
                    .build();
            jni.addCommonlyMember(build);
            exportPop.dismiss();
        });

    }

    private void showCommonlyMemberPop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_commonly_member, null, false);
        initialPopupWindowXY();
        commonlyMemberPop = PopUtil.createCoverPopupWindow(inflate, rootView, popWidth, popHeight, popX, popY);
        RecyclerView rv_content = inflate.findViewById(R.id.rv_content);
        CheckBox cb_all = inflate.findViewById(R.id.cb_all);
        rv_content.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_content.addItemDecoration(new RvItemDecoration(getContext()));
        rv_content.setAdapter(commonlyMemberAdapter);
        commonlyMemberAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                commonlyMemberAdapter.setSelectedId(presenter.commonlyMembers.get(position).getPersonid());
                cb_all.setChecked(commonlyMemberAdapter.isCheckAll());
            }
        });
        cb_all.setOnClickListener(v -> {
            boolean checked = cb_all.isChecked();
            cb_all.setChecked(checked);
            commonlyMemberAdapter.checkAll(checked);
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> commonlyMemberPop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> commonlyMemberPop.dismiss());
        inflate.findViewById(R.id.btn_augment).setOnClickListener(v -> {
            List<InterfacePerson.pbui_Item_PersonDetailInfo> selectedMembers = commonlyMemberAdapter.getSelectedMembers();
            if (selectedMembers.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_member_first);
                return;
            }
            List<InterfaceMember.pbui_Item_MemberDetailInfo> adds = new ArrayList<>();
            for (int i = 0; i < selectedMembers.size(); i++) {
                InterfacePerson.pbui_Item_PersonDetailInfo item = selectedMembers.get(i);
                InterfaceMember.pbui_Item_MemberDetailInfo build = InterfaceMember.pbui_Item_MemberDetailInfo.newBuilder()
                        .setComment(item.getComment())
                        .setPassword(item.getPassword())
                        .setEmail(item.getEmail())
                        .setPhone(item.getPhone())
                        .setCompany(item.getCompany())
                        .setJob(item.getJob())
                        .setName(item.getName())
                        .build();
                adds.add(build);
            }
            jni.addMember(adds);
            commonlyMemberPop.dismiss();
        });
    }

    private void showPermissionPop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_member_permission, null, false);
        initialPopupWindowXY();
        memberPermissionPop = PopUtil.createCoverPopupWindow(inflate, rootView, popWidth, popHeight, popX, popY);
        RecyclerView rv_content = inflate.findViewById(R.id.rv_content);
        CheckBox cb_all_member = inflate.findViewById(R.id.cb_all_member);
        rv_content.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_content.addItemDecoration(new RvItemDecoration(getContext()));
        rv_content.setAdapter(permissionAdapter);
        permissionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                permissionAdapter.setSelectedId(presenter.memberPermissionBeans.get(position).getMemberId());
                cb_all_member.setChecked(permissionAdapter.isCheckAll());
            }
        });
        cb_all_member.setOnClickListener(v -> {
            boolean checked = cb_all_member.isChecked();
            cb_all_member.setChecked(checked);
            permissionAdapter.checkAll(checked);
        });
        CheckBox cb_screen_permission = inflate.findViewById(R.id.cb_screen_permission);
        CheckBox cb_projection_permission = inflate.findViewById(R.id.cb_projection_permission);
        CheckBox cb_upload_permission = inflate.findViewById(R.id.cb_upload_permission);
        CheckBox cb_download_permission = inflate.findViewById(R.id.cb_download_permission);
        CheckBox cb_vote_permission = inflate.findViewById(R.id.cb_vote_permission);
        CheckBox cb_all_permission = inflate.findViewById(R.id.cb_all_permission);
        cb_all_permission.setOnClickListener(v -> {
            boolean checked = cb_all_permission.isChecked();
            cb_all_permission.setChecked(checked);
            cb_screen_permission.setChecked(checked);
            cb_projection_permission.setChecked(checked);
            cb_upload_permission.setChecked(checked);
            cb_download_permission.setChecked(checked);
            cb_vote_permission.setChecked(checked);
        });
        cb_screen_permission.setOnClickListener(v -> {
            boolean checked = cb_screen_permission.isChecked();
            cb_screen_permission.setChecked(checked);
            boolean pro = cb_projection_permission.isChecked();
            boolean upload = cb_upload_permission.isChecked();
            boolean download = cb_download_permission.isChecked();
            boolean vote = cb_vote_permission.isChecked();
            cb_all_permission.setChecked(checked && pro && upload && download && vote);
        });
        cb_projection_permission.setOnClickListener(v -> {
            boolean checked = cb_projection_permission.isChecked();
            cb_projection_permission.setChecked(checked);
            boolean pro = cb_screen_permission.isChecked();
            boolean upload = cb_upload_permission.isChecked();
            boolean download = cb_download_permission.isChecked();
            boolean vote = cb_vote_permission.isChecked();
            cb_all_permission.setChecked(checked && pro && upload && download && vote);
        });
        cb_upload_permission.setOnClickListener(v -> {
            boolean checked = cb_upload_permission.isChecked();
            cb_upload_permission.setChecked(checked);
            boolean pro = cb_screen_permission.isChecked();
            boolean upload = cb_projection_permission.isChecked();
            boolean download = cb_download_permission.isChecked();
            boolean vote = cb_vote_permission.isChecked();
            cb_all_permission.setChecked(checked && pro && upload && download && vote);
        });
        cb_download_permission.setOnClickListener(v -> {
            boolean checked = cb_download_permission.isChecked();
            cb_download_permission.setChecked(checked);
            boolean pro = cb_screen_permission.isChecked();
            boolean upload = cb_projection_permission.isChecked();
            boolean download = cb_upload_permission.isChecked();
            boolean vote = cb_vote_permission.isChecked();
            cb_all_permission.setChecked(checked && pro && upload && download && vote);
        });
        cb_vote_permission.setOnClickListener(v -> {
            boolean checked = cb_vote_permission.isChecked();
            cb_vote_permission.setChecked(checked);
            boolean pro = cb_screen_permission.isChecked();
            boolean upload = cb_projection_permission.isChecked();
            boolean download = cb_upload_permission.isChecked();
            boolean vote = cb_download_permission.isChecked();
            cb_all_permission.setChecked(checked && pro && upload && download && vote);
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> memberPermissionPop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> memberPermissionPop.dismiss());
        inflate.findViewById(R.id.btn_modify).setOnClickListener(v -> {
            boolean screen = cb_screen_permission.isChecked();
            boolean pro = cb_projection_permission.isChecked();
            boolean upload = cb_upload_permission.isChecked();
            boolean download = cb_download_permission.isChecked();
            boolean vote = cb_vote_permission.isChecked();
            int permission = 0;
            if (screen) permission = permission | Constant.permission_code_screen;
            if (pro) permission = permission | Constant.permission_code_projection;
            if (upload) permission = permission | Constant.permission_code_upload;
            if (download) permission = permission | Constant.permission_code_download;
            if (vote) permission = permission | Constant.permission_code_vote;
            List<MemberPermissionBean> selectedMembers = permissionAdapter.getSelectedMembers();
            List<InterfaceMember.pbui_Item_MemberPermission> temps = new ArrayList<>();
            for (int i = 0; i < selectedMembers.size(); i++) {
                MemberPermissionBean item = selectedMembers.get(i);
                InterfaceMember.pbui_Item_MemberPermission build = InterfaceMember.pbui_Item_MemberPermission.newBuilder()
                        .setMemberid(item.getMemberId())
                        .setPermission(permission)
                        .build();
                temps.add(build);
            }
            jni.saveMemberPermissions(temps);
        });
    }

    private void showMemberRolePop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_member_role, null, false);
        initialPopupWindowXY();
        memberRolePop = PopUtil.createCoverPopupWindow(inflate, rootView, popWidth, popHeight, popX, popY);
        RecyclerView rv_content = inflate.findViewById(R.id.rv_content);
        CheckBox cb_all_member = inflate.findViewById(R.id.cb_all_member);
        Spinner sp_member_role = inflate.findViewById(R.id.sp_member_role);

        rv_content.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_content.addItemDecoration(new RvItemDecoration(getContext()));
        rv_content.setAdapter(memberRoleAdapter);
        memberRoleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                MemberRoleBean memberRoleBean = presenter.memberRoleBeans.get(position);
                if (memberRoleBean.getSeat() == null) {
                    ToastUtil.showShort(R.string.unbound_seats_cannot_be_selected);
                    return;
                }
                memberRoleAdapter.setSelectedId(memberRoleBean.getMember().getPersonid());
                cb_all_member.setChecked(memberRoleAdapter.isCheckAll());
            }
        });
        cb_all_member.setOnClickListener(v -> {
            boolean checked = cb_all_member.isChecked();
            cb_all_member.setChecked(checked);
            memberRoleAdapter.checkAll(checked);
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> memberRolePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> memberRolePop.dismiss());
        inflate.findViewById(R.id.btn_modify).setOnClickListener(v -> {
            int index = sp_member_role.getSelectedItemPosition();
            int role = 0;
            switch (index) {
                case 1: {
                    role = InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_normal_VALUE;
                    break;
                }
                case 2: {
                    role = InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_compere_VALUE;
                    break;
                }
                case 3: {
                    role = InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_secretary_VALUE;
                    break;
                }
                case 4: {
                    role = InterfaceMacro.Pb_MeetMemberRole.Pb_role_admin_VALUE;
                    break;
                }
            }
            List<MemberRoleBean> selectedMembers = memberRoleAdapter.getSelectedMembers();
            if (selectedMembers.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_member_first);
                return;
            }
            if (role == InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_compere_VALUE) {
                //主持人
                if (selectedMembers.size() > 1) {
                    ToastUtil.showShort(R.string.can_only_choose_one_host);
                    return;
                }
            }
            List<InterfaceRoom.pbui_Item_MeetSeatDetailInfo> temps = new ArrayList<>();
            for (int i = 0; i < selectedMembers.size(); i++) {
                MemberRoleBean bean = selectedMembers.get(i);
                InterfaceRoom.pbui_Item_MeetSeatDetailInfo build = InterfaceRoom.pbui_Item_MeetSeatDetailInfo.newBuilder()
                        .setNameId(bean.getMember().getPersonid())
                        .setSeatid(bean.getSeat().getDevid())
                        .setRole(role)
                        .build();
                temps.add(build);
            }
            jni.modifyMeetRanking(temps);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_MEMBER) {
            File file = UriUtils.uri2File(data.getData());
            if (file != null && file.isFile()) {
                if (file.getName().endsWith(".xls")) {
                    List<InterfaceMember.pbui_Item_MemberDetailInfo> members = JxlUtil.readMemberInfo(file.getAbsolutePath());
                    jni.addMember(members);
                } else {
                    ToastUtils.showShort(R.string.please_choose_xls_file);
                }
            }
        }
    }
}
