package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.device;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.DeviceControlAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.bean.DevControlBean;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;

import java.util.ArrayList;
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
        inflate.findViewById(R.id.btn_administrator_settings).setOnClickListener(v -> {
            List<DevControlBean> checkedDevice = deviceControlAdapter.getCheckedDevice();
            if (checkedDevice.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_device_first);
                return;
            }
//            if (checkedDevice.size() > 1) {
//                ToastUtils.showShort(R.string.can_only_choose_one_administrator);
//                return;
//            }
            List<InterfaceRoom.pbui_Item_MeetSeatDetailInfo> temps = new ArrayList<>();
            for (int i = 0; i < checkedDevice.size(); i++) {
                DevControlBean devControlBean = checkedDevice.get(i);
                InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo info = devControlBean.getSeatInfo();
                int devcieid = info.getDevid();
                int memberid = info.getMemberid();
                boolean isClientDevice = Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetClient_VALUE, devcieid);
                LogUtils.e("管理员设定：设备名称=" + info.getDevname().toStringUtf8() + ",memberid=" + memberid + ",devcieid=" + devcieid + ",isClientDevice=" + isClientDevice);
                InterfaceRoom.pbui_Item_MeetSeatDetailInfo build = InterfaceRoom.pbui_Item_MeetSeatDetailInfo.newBuilder()
                        .setNameId(memberid)
                        .setSeatid(devcieid)
                        .setRole(InterfaceMacro.Pb_MeetMemberRole.Pb_role_admin_VALUE)
                        .build();
                temps.add(build);
                if (memberid == 0 || !isClientDevice) {
                    ToastUtils.showShort(R.string.can_only_choose_bound_client_device);
                    return;
                }
            }
            jni.modifyMeetRanking(temps);
            /*
            DevControlBean devControlBean = checkedDevice.get(0);
            InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo info = devControlBean.getSeatInfo();
            int devcieid = info.getDevid();
            int memberid = info.getMemberid();
            boolean isClientDevice = Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetClient_VALUE, devcieid);
            LogUtils.e("设备名称=" + info.getDevname().toStringUtf8() + ",memberid=" + memberid + ",devcieid=" + devcieid + ",isClientDevice=" + isClientDevice);
            if (memberid == 0 || !isClientDevice) {
                ToastUtils.showShort(R.string.can_only_choose_bound_client_device);
                return;
            }
            jni.modifyMeetRanking(memberid, InterfaceMacro.Pb_MeetMemberRole.Pb_role_admin_VALUE, devcieid);
            */
        });
        //网络唤醒
        inflate.findViewById(R.id.btn_wake_up).setOnClickListener(v -> {
            List<Integer> checkIds = deviceControlAdapter.getCheckIds();
            if (checkIds.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_device_first);
                return;
            }
            jni.wakeOnLan(checkIds);
        });
        //外部文档打开
        inflate.findViewById(R.id.btn_externally).setOnClickListener(v -> {
            List<DevControlBean> checkedDevice = deviceControlAdapter.getCheckedDevice();
            for (int i = 0; i < checkedDevice.size(); i++) {
                DevControlBean devControlBean = checkedDevice.get(i);
                InterfaceDevice.pbui_Item_DeviceDetailInfo item = devControlBean.getDeviceInfo();
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
        });
        //带宽测试
        inflate.findViewById(R.id.btn_broadband_testing).setOnClickListener(v -> {
            
        });
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
