package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.screen;

import android.text.PrecomputedText;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.ProAdapter;
import com.xlk.takstarpaperlessmanage.adapter.SourceMemberAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/5/26.
 * @desc
 */
public class ScreenManageFragment extends BaseFragment<ScreenManagePresenter> implements ScreenManageContract.View {
    private LinearLayout rootView;
    private RecyclerView rvSource;
    private CheckBox cbPro;
    private RecyclerView rvProDev;
    private CheckBox cbMember;
    private RecyclerView rvMember;
    private CheckBox cbMandatory;
    private SourceMemberAdapter sourceMemberAdapter;
    private ProAdapter proAdapter;
    private SourceMemberAdapter targetMemberAdapter;
    private List<Integer> launchedTargetDevIds;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_screen_manage;
    }

    @Override
    protected void initView(View inflate) {
        rootView = (LinearLayout) inflate.findViewById(R.id.root_view);
        rvSource = (RecyclerView) inflate.findViewById(R.id.rv_source);
        cbPro = (CheckBox) inflate.findViewById(R.id.cb_pro);
        rvProDev = (RecyclerView) inflate.findViewById(R.id.rv_pro_dev);
        cbMember = (CheckBox) inflate.findViewById(R.id.cb_member);
        rvMember = (RecyclerView) inflate.findViewById(R.id.rv_member);
        cbMandatory = (CheckBox) inflate.findViewById(R.id.cb_mandatory);
        cbPro.setOnClickListener(v -> {
            boolean checked = cbPro.isChecked();
            cbPro.setChecked(checked);
            proAdapter.checkAll(checked);
        });
        cbMember.setOnClickListener(v -> {
            boolean checked = cbMember.isChecked();
            cbMember.setChecked(checked);
            targetMemberAdapter.checkAll(checked);
        });
        inflate.findViewById(R.id.btn_preview).setOnClickListener(v -> {
            List<Integer> checkedIds = sourceMemberAdapter.getCheckedIds();
            if (checkedIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_screen_source_first);
                return;
            }
            Integer devId = checkedIds.get(0);
            if (devId == GlobalValue.localDeviceId) {
                ToastUtils.showShort(R.string.cannot_preview_your_own_screen);
                return;
            }
            List<Integer> resIds = new ArrayList<>();
            resIds.add(Constant.RESOURCE_ID_0);
            List<Integer> devIds = new ArrayList<>();
            devIds.add(GlobalValue.localDeviceId);
            jni.streamPlay(devId, 2, 0, resIds, devIds);
        });
        inflate.findViewById(R.id.btn_stop_preview).setOnClickListener(v -> {
        });
        inflate.findViewById(R.id.btn_refresh).setOnClickListener(v -> {
            initial();
            sourceMemberAdapter.checkAll(false);
            proAdapter.checkAll(false);
            targetMemberAdapter.checkAll(false);
        });
        inflate.findViewById(R.id.btn_stop_task).setOnClickListener(v -> {
            if (launchedTargetDevIds == null || launchedTargetDevIds.isEmpty()) {
                ToastUtils.showShort(R.string.no_task);
                return;
            }
            List<Integer> resIds = new ArrayList<>();
            resIds.add(Constant.RESOURCE_ID_0);
            jni.stopResourceOperate(0, launchedTargetDevIds);
            launchedTargetDevIds.clear();
        });
        inflate.findViewById(R.id.btn_launch_screen).setOnClickListener(v -> {
            List<Integer> sourceDevIds = sourceMemberAdapter.getCheckedIds();
            if (sourceDevIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_screen_source_first);
                return;
            }
            List<Integer> targetDevIds = proAdapter.getCheckedIds();
            targetDevIds.addAll(targetMemberAdapter.getCheckedIds());
            if (targetDevIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_target_screen_first);
                return;
            }
            List<Integer> resIds = new ArrayList<>();
            launchedTargetDevIds = targetDevIds;
            resIds.add(Constant.RESOURCE_ID_0);
            int triggeruserval = cbMandatory.isChecked() ? InterfaceMacro.Pb_TriggerUsedef.Pb_EXCEC_USERDEF_FLAG_NOCREATEWINOPER_VALUE : 0;
            jni.streamPlay(sourceDevIds.get(0), 2, triggeruserval, resIds, targetDevIds);
        });
    }

    @Override
    protected ScreenManagePresenter initPresenter() {
        return new ScreenManagePresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryMember();
    }

    @Override
    public void updateList() {
        if (sourceMemberAdapter == null) {
            sourceMemberAdapter = new SourceMemberAdapter(presenter.sourceMembers, true);
            rvSource.setLayoutManager(new LinearLayoutManager(getContext()));
            rvSource.addItemDecoration(new RvItemDecoration(getContext()));
            rvSource.setAdapter(sourceMemberAdapter);
            sourceMemberAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    sourceMemberAdapter.check(presenter.sourceMembers.get(position).getDeviceDetailInfo().getDevcieid());
                }
            });
        } else {
            sourceMemberAdapter.notifyDataSetChanged();
        }
        if (targetMemberAdapter == null) {
            targetMemberAdapter = new SourceMemberAdapter(presenter.targetMembers, false);
            rvMember.setLayoutManager(new LinearLayoutManager(getContext()));
            rvMember.addItemDecoration(new RvItemDecoration(getContext()));
            rvMember.setAdapter(targetMemberAdapter);
            targetMemberAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    targetMemberAdapter.check(presenter.targetMembers.get(position).getDeviceDetailInfo().getDevcieid());
                    cbMember.setChecked(targetMemberAdapter.isCheckAll());
                }
            });
        } else {
            targetMemberAdapter.notifyDataSetChanged();
        }
        if (proAdapter == null) {
            proAdapter = new ProAdapter(presenter.onLineProjectors);
            rvProDev.setLayoutManager(new LinearLayoutManager(getContext()));
            rvProDev.addItemDecoration(new RvItemDecoration(getContext()));
            rvProDev.setAdapter(proAdapter);
            proAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    proAdapter.check(presenter.onLineProjectors.get(position).getDevcieid());
                    cbPro.setChecked(proAdapter.isCheckAll());
                }
            });
        } else {
            proAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        launchedTargetDevIds.clear();
    }
}
