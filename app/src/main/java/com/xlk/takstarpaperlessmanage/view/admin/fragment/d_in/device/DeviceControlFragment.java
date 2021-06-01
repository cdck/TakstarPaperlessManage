package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.device;

import android.text.PrecomputedText;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.DeviceControlAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/5/24.
 * @desc
 */
public class DeviceControlFragment extends BaseFragment<DeviceControlPresenter> implements DeviceControlContract.View {

    private DeviceControlAdapter deviceControlAdapter;
    private LinearLayout rootView;
    private Spinner spinner;
    private CheckBox cbAll;
    private RecyclerView rvContent;
    /**
     * 默认选中的是升降机
     */
    private int chooseType = InterfaceMacro.Pb_LiftFlag.Pb_LIFT_FLAG_MACHICE_VALUE;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device_control;
    }

    @Override
    protected void initView(View inflate) {
        rootView = (LinearLayout) inflate.findViewById(R.id.root_view);
        spinner = (Spinner) inflate.findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_blue_checked_text, R.id.tv_spinner);
        spinnerAdapter.add("升降机");
        spinnerAdapter.add("话筒");
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e("选中的是---" + (position == 0 ? "升降机" : "话筒"));
                chooseType = position == 0 ? InterfaceMacro.Pb_LiftFlag.Pb_LIFT_FLAG_MACHICE_VALUE : InterfaceMacro.Pb_LiftFlag.Pb_LIFT_FLAG_MIC_VALUE;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        rvContent = (RecyclerView) inflate.findViewById(R.id.rv_content);
        cbAll = (CheckBox) inflate.findViewById(R.id.cb_all);
        cbAll.setOnClickListener(v -> {
            boolean checked = cbAll.isChecked();
            cbAll.setChecked(checked);
            deviceControlAdapter.checkAll(checked);
        });
        //上升
        inflate.findViewById(R.id.btn_rise).setOnClickListener(v -> {
            List<Integer> checkIds = deviceControlAdapter.getCheckIds();
            if (checkIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_device_first);
                return;
            }
            jni.executeTerminalControl(InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_LIFTUP_VALUE, chooseType, 0, checkIds);
        });
        //停止
        inflate.findViewById(R.id.btn_stop).setOnClickListener(v -> {
            List<Integer> checkIds = deviceControlAdapter.getCheckIds();
            if (checkIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_device_first);
                return;
            }
            jni.executeTerminalControl(InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_LIFTSTOP_VALUE, chooseType, 0, checkIds);
        });
        //下降
        inflate.findViewById(R.id.btn_down).setOnClickListener(v -> {
            List<Integer> checkIds = deviceControlAdapter.getCheckIds();
            if (checkIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_device_first);
                return;
            }
            jni.executeTerminalControl(InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_LIFTDOWN_VALUE, chooseType, 0, checkIds);
        });
        //软件重开
        inflate.findViewById(R.id.btn_software_reboot).setOnClickListener(v -> {
            List<Integer> checkIds = deviceControlAdapter.getCheckIds();
            if (checkIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_device_first);
                return;
            }
            jni.executeTerminalControl(InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_PROGRAMRESTART_VALUE, 0, 0, checkIds);
        });
        //重启
        inflate.findViewById(R.id.btn_reboot).setOnClickListener(v -> {
            List<Integer> checkIds = deviceControlAdapter.getCheckIds();
            if (checkIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_device_first);
                return;
            }
            jni.executeTerminalControl(InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_REBOOT_VALUE, 0, 0, checkIds);
        });
        //关机
        inflate.findViewById(R.id.btn_shutdown).setOnClickListener(v -> {
            List<Integer> checkIds = deviceControlAdapter.getCheckIds();
            if (checkIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_device_first);
                return;
            }
            jni.executeTerminalControl(InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_SHUTDOWN_VALUE, 0, 0, checkIds);
        });
        //辅助签到
        inflate.findViewById(R.id.btn_sign_in).setOnClickListener(v -> {
            List<Integer> checkIds = deviceControlAdapter.getCheckIds();
            if (checkIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_device_first);
                return;
            }
            jni.assistedSignIn(checkIds);
        });
        //管理员设定
        inflate.findViewById(R.id.btn_administrator_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        inflate.findViewById(R.id.btn_wake_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> checkIds = deviceControlAdapter.getCheckIds();
                if (checkIds.isEmpty()) {
                    ToastUtils.showShort(R.string.please_choose_device_first);
                    return;
                }
                jni.wakeOnLan(checkIds);
            }
        });
        //外部文档打开
        inflate.findViewById(R.id.btn_externally).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<InterfaceDevice.pbui_Item_DeviceDetailInfo> checkedDevice = deviceControlAdapter.getCheckedDevice();
                for (int i = 0; i < checkedDevice.size(); i++) {
                    InterfaceDevice.pbui_Item_DeviceDetailInfo item = checkedDevice.get(i);
                    int deviceflag = item.getDeviceflag();
                    int newFlag = deviceflag;
                    if ((deviceflag & InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_OPENOUTSIDE_VALUE)
                            == InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_OPENOUTSIDE_VALUE) {
                        newFlag -= InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_OPENOUTSIDE_VALUE;
                    } else {
                        newFlag += InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_OPENOUTSIDE_VALUE;
                    }
                    jni.modifyDeviceFlag(item.getDevcieid(), newFlag);
                }
            }
        });
        inflate.findViewById(R.id.btn_broadband_testing).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );
    }

    @Override
    protected DeviceControlPresenter initPresenter() {
        return new DeviceControlPresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryMember();
    }

    @Override
    public void updateRv() {
        if (deviceControlAdapter == null) {
            deviceControlAdapter = new DeviceControlAdapter(presenter.devControlBeans);
            rvContent.setLayoutManager(new LinearLayoutManager(getContext()));
            rvContent.addItemDecoration(new RvItemDecoration(getContext()));
            rvContent.setAdapter(deviceControlAdapter);
            deviceControlAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    deviceControlAdapter.check(presenter.devControlBeans.get(position).getDeviceInfo().getDevcieid());
                    cbAll.setChecked(deviceControlAdapter.isCheckAll());
                }
            });
        } else {
            deviceControlAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateRoleRv() {

    }
}
