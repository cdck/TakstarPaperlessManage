package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.devicemanage;

import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.ClientAdapter;
import com.xlk.takstarpaperlessmanage.adapter.DeviceManageAdapter;
import com.xlk.takstarpaperlessmanage.adapter.LocalFileAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.IniUtil;
import com.xlk.takstarpaperlessmanage.util.LogUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xlk.takstarpaperlessmanage.model.Constant.s2b;

/**
 * @author Created by xlk on 2021/5/6.
 * @desc
 */
public class DeviceManageFragment extends BaseFragment<DeviceManagePresenter> implements DeviceManageContract.View, View.OnClickListener {
    private Button btnModify;
    private Button btnDelete;
    private Button btnSetGuestMode;
    private Button btnParameterConfiguration;
    private Button btnPlatformTesting;
    private RecyclerView rvDevice;
    private DeviceManageAdapter deviceManageAdapter;
    private PopupWindow modifyPop, deletePop, guestModePop, parameterConfigurationPop, platformTestingPop;
    private LinearLayout root_view;
    private ClientAdapter clientAdapter;

    private List<File> currentFiles = new ArrayList<>();
    private RecyclerView rv_current_file;
    private LocalFileAdapter localFileAdapter;
    private Timer timer;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device_manage;
    }

    @Override
    protected void initView(View inflate) {
        root_view = (LinearLayout) inflate.findViewById(R.id.root_view);
        btnModify = (Button) inflate.findViewById(R.id.btn_modify);
        btnDelete = (Button) inflate.findViewById(R.id.btn_delete);
        btnSetGuestMode = (Button) inflate.findViewById(R.id.btn_set_guest_mode);
        btnParameterConfiguration = (Button) inflate.findViewById(R.id.btn_parameter_configuration);
        btnPlatformTesting = (Button) inflate.findViewById(R.id.btn_platform_testing);
        rvDevice = (RecyclerView) inflate.findViewById(R.id.rv_device);
        btnModify.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnSetGuestMode.setOnClickListener(this);
        btnParameterConfiguration.setOnClickListener(this);
        btnPlatformTesting.setOnClickListener(this);
    }

    @Override
    protected DeviceManagePresenter initPresenter() {
        return new DeviceManagePresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryDevice();
    }

    @Override
    public void updateDeviceList() {
        if (deviceManageAdapter == null) {
            deviceManageAdapter = new DeviceManageAdapter(presenter.deviceInfos);
            rvDevice.setLayoutManager(new LinearLayoutManager(getContext()));
            rvDevice.addItemDecoration(new RvItemDecoration(getContext()));
            rvDevice.setAdapter(deviceManageAdapter);
            deviceManageAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    LogUtils.i("点击的索引=" + position);
                    deviceManageAdapter.setSelected(presenter.deviceInfos.get(position).getDevcieid());
                }
            });
        } else {
            deviceManageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_modify: {
                showModifyPop(deviceManageAdapter.getSelectedDevice());
                break;
            }
            case R.id.btn_delete: {
                showDeletePop(deviceManageAdapter.getSelectedDevice());
                break;
            }
            case R.id.btn_set_guest_mode: {
                showGuestModePop(deviceManageAdapter.getSelectedDevice());
                break;
            }
            case R.id.btn_parameter_configuration: {
                showParameterConfigurationPop();
                break;
            }
            case R.id.btn_platform_testing: {
                showPlatformTestingPop(deviceManageAdapter.getSelectedDevice());
                break;
            }
            default:
                break;
        }
    }

    private boolean isUp = true;
    private int testCount = 0;

    private void showPlatformTestingPop(InterfaceDevice.pbui_Item_DeviceDetailInfo device) {
        if (device == null) {
            ToastUtils.showShort(R.string.please_select_device_first);
            return;
        }
        if (!Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetClient_VALUE, device.getDevcieid())) {
            ToastUtils.showShort(R.string.please_select_the_conference_terminal_device);
            return;
        }
        LogUtils.i("showGuestModePop deviceFlag=" + device.getDeviceflag());
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_platform_testing, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        int height1 = rv_navigation.getHeight();
        platformTestingPop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height / 2, rvDevice, Gravity.CENTER, width1 / 2, 0);
        EditText edtSecond = inflate.findViewById(R.id.edt_second);
        TextView tvTestCount = inflate.findViewById(R.id.tv_test_count);
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> platformTestingPop.dismiss());
        inflate.findViewById(R.id.btn_exit).setOnClickListener(v -> platformTestingPop.dismiss());
        platformTestingPop.setOnDismissListener(() -> {
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }
            testCount = 0;
            isUp = true;
        });
        inflate.findViewById(R.id.btn_start).setOnClickListener(v -> {
            String trim = edtSecond.getText().toString().trim();
            if (trim.isEmpty()) {
                ToastUtil.showShort(R.string.please_enter_second_first);
                return;
            }
            int second = Integer.parseInt(trim);
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    testCount++;
                    isUp = !isUp;
                    int value = InterfaceMacro.Pb_LiftFlag.Pb_LIFT_FLAG_MACHICE_VALUE | InterfaceMacro.Pb_LiftFlag.Pb_LIFT_FLAG_MIC_VALUE;
                    int oper = isUp ? InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_LIFTUP_VALUE : InterfaceMacro.Pb_DeviceControlFlag.Pb_DEVICECONTORL_LIFTDOWN_VALUE;
                    jni.executeTerminalControl(oper, value, 0, device.getDevcieid());
                    getActivity().runOnUiThread(() -> tvTestCount.setText(getString(R.string.already_test, testCount)));
                }
            }, 0, second * 1000);
        });
    }

    @Override
    public void updateClientList() {
        if (clientAdapter == null) {
            clientAdapter = new ClientAdapter(presenter.clientDevices);
        } else {
            clientAdapter.notifyDataSetChanged();
        }
    }

    private void showParameterConfigurationPop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_parameter_configuration, null, false);
        parameterConfigurationPop = PopUtil.createCoverPopupWindow(inflate, root_view, popWidth, popHeight, popX, popY);
        ViewHolder holder = new ViewHolder(inflate);
        holderEvent(holder);
    }

    private void holderEvent(ViewHolder holder) {
        defaultLocalIni(holder);
        holder.edtCacheLocation.setKeyListener(null);
        holder.rv_client.setLayoutManager(new LinearLayoutManager(getContext()));
        holder.rv_client.addItemDecoration(new RvItemDecoration(getContext()));
        holder.rv_client.setAdapter(clientAdapter);
        clientAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                clientAdapter.setSelected(presenter.clientDevices.get(position).getDevcieid());
                holder.cbAllDevice.setChecked(clientAdapter.isCheckAll());
            }
        });
        holder.cbAllDevice.setOnClickListener(v -> {
            boolean checked = holder.cbAllDevice.isChecked();
            holder.cbAllDevice.setChecked(checked);
            clientAdapter.checkAll(checked);
        });
        holder.cbAllParameters.setOnClickListener(v -> {
            boolean checked = holder.cbAllParameters.isChecked();
            holder.cbAllParameters.setChecked(checked);
            holder.cbUseServerIp.setChecked(checked);
            holder.cbUsePort.setChecked(checked);
            holder.cbUseCacheLocation.setChecked(checked);
            holder.cbUseCacheSize.setChecked(checked);
            holder.cbUseHardwareCoding.setChecked(checked);
            holder.cbUseHardwareDecoding.setChecked(checked);
            holder.cbUseEnableDebug.setChecked(checked);
            holder.cbUseOpenCamera.setChecked(checked);
            holder.cbUseOpenMic.setChecked(checked);
            holder.cbUseDisableMulticast.setChecked(checked);
            holder.cbUseConvertSmoothFormats.setChecked(checked);
            holder.cbUseCodingMode.setChecked(checked);
            holder.cbUseScreenStream.setChecked(checked);
            holder.cbUseCameraStream.setChecked(checked);
        });
        holder.ivClose.setOnClickListener(v -> parameterConfigurationPop.dismiss());
        holder.btnCancel.setOnClickListener(v -> parameterConfigurationPop.dismiss());
        holder.btnModifyCacheLocation.setOnClickListener(v -> {
            showCacheDirPop(holder.edtCacheLocation, holder.edtCacheLocation.getText().toString());
        });
        holder.btnModify.setOnClickListener(v -> {
            List<Integer> deviceIds = clientAdapter.getSelectedIds();
            if (deviceIds.isEmpty()) {
                ToastUtil.showShort(R.string.please_select_device_first);
                return;
            }
            String restart = holder.cbReboot.isChecked() ? "1" : "0";
            String jsonStr = "{\"restart\":" + restart + "," + "\"item\":[";
            String itemStr = "";
            //Ip地址
            if (holder.cbUseServerIp.isChecked()) {
                String ipStr = holder.edtServerIp.getText().toString().trim();
                if (!RegexUtils.isIP(ipStr)) {
                    ToastUtil.showShort(R.string.ip_address_format_error);
                    return;
                }
                itemStr += "{"
                        + "\"section\":\"areaaddr\","
                        + "\"key\":\"area0ip\","
                        + "\"value\":\"" + ipStr + "\""
                        + "}"
                ;
            }
            //端口号
            if (holder.cbUsePort.isChecked()) {
                String port = holder.edtPort.getText().toString().trim();
                if (TextUtils.isEmpty(port)) {
                    ToastUtil.showShort(R.string.please_enter_port_first);
                    return;
                }
                if (!itemStr.isEmpty()) {
                    itemStr += ",";
                }
                itemStr += "{"
                        + "\"section\":\"areaaddr\","
                        + "\"key\":\"area0port\","
                        + "\"value\":\"" + port + "\""
                        + "}"
                ;
            }
            //缓存位置
            if (holder.cbUseCacheLocation.isChecked()) {
                String cache_dir = holder.edtCacheLocation.getText().toString().trim();
                if (TextUtils.isEmpty(cache_dir)) {
                    ToastUtil.showShort(R.string.please_choose_cache_dir_first);
                    return;
                }
                if (!itemStr.isEmpty()) {
                    itemStr += ",";
                }
                itemStr += "{"
                        + "\"section\":\"Buffer Dir\","
                        + "\"key\":\"mediadir\","
                        + "\"value\":\"" + cache_dir + "\""
                        + "}"
                ;
            }
            //缓存大小
            if (holder.cbUseCacheSize.isChecked()) {
                String cache_size = holder.edtCacheSize.getText().toString().trim();
                if (TextUtils.isEmpty(cache_size)) {
                    ToastUtil.showShort(R.string.please_enter_cache_size_first);
                    return;
                }
                if (!itemStr.isEmpty()) {
                    itemStr += ",";
                }
                itemStr += "{"
                        + "\"section\":\"Buffer Dir\","
                        + "\"key\":\"mediadirsize\","
                        + "\"value\":\"" + cache_size + "\""
                        + "}"
                ;
            }
            //硬件编码
            if (holder.cbUseHardwareCoding.isChecked()) {
                boolean checked = holder.cbHardwareCoding.isChecked();
                if (!itemStr.isEmpty()) {
                    itemStr += ",";
                }
                itemStr += "{"
                        + "\"section\":\"debug\","
                        + "\"key\":\"hwencode\","
                        + "\"value\":\"" + (checked ? "1" : "0") + "\""
                        + "}";
            }
            //硬件解码
            if (holder.cbUseHardwareDecoding.isChecked()) {
                boolean checked = holder.cbHardwareDecoding.isChecked();
                if (!itemStr.isEmpty()) {
                    itemStr += ",";
                }
                itemStr += "{"
                        + "\"section\":\"debug\","
                        + "\"key\":\"hwdecode\","
                        + "\"value\":\"" + (checked ? "1" : "0") + "\""
                        + "}";
            }
            //启动调试
            if (holder.cbUseEnableDebug.isChecked()) {
                boolean checked = holder.cbEnableDebug.isChecked();
                if (!itemStr.isEmpty()) {
                    itemStr += ",";
                }
                itemStr += "{"
                        + "\"section\":\"debug\","
                        + "\"key\":\"console\","
                        + "\"value\":\"" + (checked ? "1" : "0") + "\""
                        + "}";
            }
            //是否开启摄像头
            if (holder.cbUseOpenCamera.isChecked()) {
                boolean checked = holder.cbOpenCamera.isChecked();
                if (!itemStr.isEmpty()) {
                    itemStr += ",";
                }
                itemStr += "{"
                        + "\"section\":\"debug\","
                        + "\"key\":\"camaracap\","
                        + "\"value\":\"" + (checked ? "1" : "0") + "\""
                        + "}";
            }
            //是否开启麦克风
            if (holder.cbUseOpenMic.isChecked()) {
                boolean checked = holder.cbOpenMic.isChecked();
                if (!itemStr.isEmpty()) {
                    itemStr += ",";
                }
                itemStr += "{"
                        + "\"section\":\"debug\","
                        + "\"key\":\"videoaudio\","
                        + "\"value\":\"" + (checked ? "1" : "0") + "\""
                        + "}";
            }
            //禁用组播
            if (holder.cbUseDisableMulticast.isChecked()) {
                boolean checked = holder.cbDisableMulticast.isChecked();
                if (!itemStr.isEmpty()) {
                    itemStr += ",";
                }
                itemStr += "{"
                        + "\"section\":\"debug\","
                        + "\"key\":\"disablemulticast\","
                        + "\"value\":\"" + (checked ? "1" : "0") + "\""
                        + "}";
            }
            //上传高清视频时转换
            if (holder.cbUseConvertSmoothFormats.isChecked()) {
                boolean checked = holder.cbConvertSmoothFormats.isChecked();
                if (!itemStr.isEmpty()) {
                    itemStr += ",";
                }
                itemStr += "{"
                        + "\"section\":\"debug\","
                        + "\"key\":\"mediatranscode\","
                        + "\"value\":\"" + (checked ? "1" : "0") + "\""
                        + "}";
            }
            //编码模式选择和TCP模式是否开启
            if (holder.cbUseCodingMode.isChecked()) {
                if (!itemStr.isEmpty()) {
                    itemStr += ",";
                }
                int position = holder.spCodingMode.getSelectedItemPosition();
                itemStr += "{"
                        + "\"section\":\"debug\","
                        + "\"key\":\"encmode\","
                        + "\"value\":\"" + position + "\""
                        + "},";
                boolean checked = holder.cbTcp.isChecked();
                itemStr += "{"
                        + "\"section\":\"selfinfo\","
                        + "\"key\":\"streamprotol\","
                        + "\"value\":\"" + (checked ? "1" : "0") + "\""
                        + "}";
            }
            //桌面同屏流
            if (holder.cbUseScreenStream.isChecked()) {
                if (!itemStr.isEmpty()) {
                    itemStr += ",";
                }
                int index = holder.spScreenStream.getSelectedItemPosition();
                itemStr += "{"
                        + "\"section\":\"debug\","
                        + "\"key\":\"video0\","
                        + "\"value\":\"" + (index == 2 ? -1 : index) + "\""
                        + "},";
                int selectedItemPosition = holder.spScreenSize.getSelectedItemPosition();
                String width, height;
                if (selectedItemPosition == 1) {
                    width = "1280";
                    height = "720";
                } else if (selectedItemPosition == 2) {
                    width = "720";
                    height = "640";
                } else if (selectedItemPosition == 3) {
                    width = "480";
                    height = "320";
                } else {
                    width = "1920";
                    height = "1080";
                }
                itemStr += "{"
                        + "\"section\":\"debug\","
                        + "\"key\":\"stream2width\","
                        + "\"value\":\"" + width + "\""
                        + "},";
                itemStr += "{"
                        + "\"section\":\"debug\","
                        + "\"key\":\"stream2height\","
                        + "\"value\":\"" + height + "\""
                        + "}";
            }
            //摄像头流
            if (holder.cbUseCameraStream.isChecked()) {
                if (!itemStr.isEmpty()) {
                    itemStr += ",";
                }
                int index = holder.spCameraStream.getSelectedItemPosition();
                itemStr += "{"
                        + "\"section\":\"debug\","
                        + "\"key\":\"video1\","
                        + "\"value\":\"" + (index == 2 ? -1 : index) + "\""
                        + "},";
                int selectedItemPosition = holder.spCameraSize.getSelectedItemPosition();
                String width, height;
                if (selectedItemPosition == 1) {
                    width = "1280";
                    height = "720";
                } else if (selectedItemPosition == 2) {
                    width = "720";
                    height = "640";
                } else if (selectedItemPosition == 3) {
                    width = "480";
                    height = "320";
                } else {
                    width = "1920";
                    height = "1080";
                }
                itemStr += "{"
                        + "\"section\":\"debug\","
                        + "\"key\":\"stream3width\","
                        + "\"value\":\"" + width + "\""
                        + "},";
                itemStr += "{"
                        + "\"section\":\"debug\","
                        + "\"key\":\"stream3height\","
                        + "\"value\":\"" + height + "\""
                        + "}";
            }
            if (itemStr.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_modify_param);
                return;
            }
            jsonStr += itemStr + "]}";
            jni.remoteConfig(deviceIds, jsonStr);
            parameterConfigurationPop.dismiss();
        });
    }

    /**
     * 展示目录列表
     *
     * @param edt
     */
    private void showCacheDirPop(final EditText edt, String dir) {
        String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String currentDir = TextUtils.isEmpty(dir) ? rootDir : dir;
        currentFiles.clear();
        currentFiles.addAll(FileUtils.listFilesInDirWithFilter(currentDir, dirFilter));
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_catalog, null);
        PopupWindow dirPop = PopUtil.createHalfPop(inflate, root_view);
        EditText edt_current_dir = inflate.findViewById(R.id.edt_current_dir);
        edt_current_dir.setKeyListener(null);
        edt_current_dir.setText(currentDir);

        rv_current_file = inflate.findViewById(R.id.rv_current_file);
        localFileAdapter = new LocalFileAdapter(R.layout.item_local_dir, currentFiles);
        rv_current_file.addItemDecoration(new RvItemDecoration(getContext()));
        rv_current_file.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_current_file.setAdapter(localFileAdapter);
        localFileAdapter.setOnItemClickListener((adapter, view, position) -> {
            File file = currentFiles.get(position);
            edt_current_dir.setText(file.getAbsolutePath());
            edt_current_dir.setSelection(edt_current_dir.getText().toString().length());
            List<File> files = FileUtils.listFilesInDirWithFilter(file, dirFilter);
            currentFiles.clear();
            currentFiles.addAll(files);
            localFileAdapter.notifyDataSetChanged();
        });
        inflate.findViewById(R.id.iv_back).setOnClickListener(v -> {
            String dirPath = edt_current_dir.getText().toString().trim();
            if (dirPath.equals(rootDir)) {
                ToastUtils.showShort(R.string.current_dir_root);
                return;
            }
            File file = new File(dirPath);
            File parentFile = file.getParentFile();
            edt_current_dir.setText(parentFile.getAbsolutePath());
            LogUtils.i(TAG, "showChooseDir 上一级的目录=" + parentFile.getAbsolutePath());
            List<File> files = FileUtils.listFilesInDirWithFilter(parentFile, dirFilter);
            currentFiles.clear();
            currentFiles.addAll(files);
            localFileAdapter.notifyDataSetChanged();
        });
        inflate.findViewById(R.id.btn_determine).setOnClickListener(v -> {
            String text = edt_current_dir.getText().toString();
            edt.setText(text);
            edt.setSelection(text.length());
            dirPop.dismiss();
        });
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            dirPop.dismiss();
        });
    }

    private FileFilter dirFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory() && !pathname.getName().startsWith(".");
        }
    };

    private void defaultLocalIni(ViewHolder holder) {
        IniUtil ini = IniUtil.getInstance();
        String ip = ini.get("areaaddr", "area0ip");
        holder.edtServerIp.setText(ip);
        String port = ini.get("areaaddr", "area0port");
        holder.edtPort.setText(port);
        String configdir = ini.get("Buffer Dir", "mediadir");
        String mediadirsize = ini.get("Buffer Dir", "mediadirsize");
        holder.edtCacheLocation.setText(configdir);
        //设置光标在最末尾
        holder.edtCacheLocation.setSelection(configdir.length());
        holder.edtCacheSize.setText(mediadirsize);

        //是否开启显卡硬编码 0关闭 1开启 默认0
        String hwencode = ini.get("debug", "hwencode");
        holder.cbHardwareCoding.setChecked(hwencode.equals("1"));

        //是否开启显卡硬解码 0关闭 1开启 默认0
        String hwdecode = ini.get("debug", "hwdecode");
        holder.cbHardwareDecoding.setChecked(hwdecode.equals("1"));

        //是否开启调试窗口 0关闭 1开启 默认0
        String console = ini.get("debug", "console");
        holder.cbEnableDebug.setChecked(console.equals("1"));

        //是否开启USB视频设备采集 0关闭 1开启
        String camaracap = ini.get("debug", "camaracap");
        holder.cbOpenCamera.setChecked(camaracap.equals("1"));

        //是否启动桌面采集声卡回放 0关闭 1开启 启用将扬声器输出捕获并附加到2号视频通道 当videoaudio=1启用时 注:优先使用输入音频附加
        String shareaudio = ini.get("debug", "shareaudio");
        holder.cbOpenMic.setChecked(shareaudio.equals("1"));

        //等于1表示禁用组播
        String disablemulticast = ini.get("debug", "disablemulticast");
        holder.cbDisableMulticast.setChecked(disablemulticast.equals("1"));

        //上传超高清视频时转换流畅格式 0关闭 1开启
        String mediatranscode = ini.get("debug", "mediatranscode");
        holder.cbConvertSmoothFormats.setChecked(mediatranscode.equals("1"));

        //设置视频流stream(index)[width|height]最大宽高 宽高同时设置才会生效
        String stream2width = ini.get("debug", "stream2width");
        String stream2height = ini.get("debug", "stream2height");
        if (!stream2width.isEmpty() && !stream2height.isEmpty()) {
            try {
                int width = Integer.parseInt(stream2width);
                int height = Integer.parseInt(stream2height);
                int index = 0;
                if (width == 1920 && height == 1080) {
                    index = 0;
                } else if (width == 1280 && height == 720) {
                    index = 1;
                } else if (width == 720 && height == 480) {
                    index = 2;
                } else if (width == 480 && height == 360) {
                    index = 3;
                }
                holder.spScreenSize.setSelection(index);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //设置视频流stream(index)[width|height]最大宽高 宽高同时设置才会生效
        String stream3width = ini.get("debug", "stream3width");
        String stream3height = ini.get("debug", "stream3height");
        if (!stream3width.isEmpty() && !stream3height.isEmpty()) {
            try {
                int width = Integer.parseInt(stream3width);
                int height = Integer.parseInt(stream3height);
                int index = 0;
                if (width == 1920 && height == 1080) {
                    index = 0;
                } else if (width == 1280 && height == 720) {
                    index = 1;
                } else if (width == 720 && height == 480) {
                    index = 2;
                } else if (width == 480 && height == 360) {
                    index = 3;
                }
                holder.spCameraSize.setSelection(index);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //设置流编码模式0高质量 1中等 2低带宽 默认0
        String encmode = ini.get("debug", "encmode");
        try {
            int mode = Integer.parseInt(encmode);
            holder.spCodingMode.setSelection(mode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //是否启用TCP传输流数据 1表示开启 0表示关闭 默认0
        String tcp = ini.get("selfinfo", "streamprotol");
        holder.cbTcp.setChecked(tcp.equals("1"));

    }

    private void showGuestModePop(InterfaceDevice.pbui_Item_DeviceDetailInfo device) {
        if (device == null) {
            ToastUtils.showShort(R.string.please_select_device_first);
            return;
        }
        if (!Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetClient_VALUE, device.getDevcieid())) {
            ToastUtils.showShort(R.string.please_select_the_conference_terminal_device);
            return;
        }
        LogUtils.i("showGuestModePop deviceFlag=" + device.getDeviceflag());
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_set_guest_mode, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        int height1 = rv_navigation.getHeight();
        guestModePop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rvDevice, Gravity.CENTER, width1 / 2, 0);
        RadioButton rb_normal_mode = inflate.findViewById(R.id.rb_normal_mode);
        RadioButton rb_guest_mode = inflate.findViewById(R.id.rb_guest_mode);
        boolean isGuestMode = (device.getDeviceflag() & InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_GUESTMODE_VALUE) == InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_GUESTMODE_VALUE;
        rb_guest_mode.setChecked(isGuestMode);
        rb_normal_mode.setChecked(!isGuestMode);
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> guestModePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> guestModePop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            int deviceflag = device.getDeviceflag();
            int newFlag = deviceflag;
            if (rb_normal_mode.isChecked()) {
                //正常模式
                if ((deviceflag & InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_GUESTMODE_VALUE)
                        == InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_GUESTMODE_VALUE) {
                    newFlag -= InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_GUESTMODE_VALUE;
                }
            } else {
                //访客模式
                newFlag = deviceflag | InterfaceMacro.Pb_MeetDeviceFlag.Pb_MEETDEVICE_FLAG_GUESTMODE_VALUE;
            }
            jni.modifyDeviceFlag(device.getDevcieid(), newFlag);
            guestModePop.dismiss();
        });
    }

    private void showDeletePop(InterfaceDevice.pbui_Item_DeviceDetailInfo device) {
        if (device == null) {
            ToastUtils.showShort(R.string.please_select_device_first);
            return;
        }
        if (device.getNetstate() == 1) {
            ToastUtils.showShort(R.string.please_select_offline_device);
            return;
        }
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_delete, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        int height1 = rv_navigation.getHeight();
        deletePop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rvDevice, Gravity.CENTER, width1 / 2, 0);
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> deletePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> deletePop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            jni.deleteDevice(device.getDevcieid());
            deletePop.dismiss();
        });
    }

    private void showModifyPop(InterfaceDevice.pbui_Item_DeviceDetailInfo device) {
        if (device == null) {
            ToastUtils.showShort(R.string.please_select_device_first);
            return;
        }
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_modify_device, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        int height1 = rv_navigation.getHeight();
        modifyPop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rvDevice, Gravity.CENTER, width1 / 2, 0);
        EditText edt_device_name = inflate.findViewById(R.id.edt_device_name);
        EditText edt_device_ip = inflate.findViewById(R.id.edt_device_ip);
        EditText edt_lift_id = inflate.findViewById(R.id.edt_lift_id);
        EditText edt_mic_id = inflate.findViewById(R.id.edt_mic_id);

        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> modifyPop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> modifyPop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String name = edt_device_name.getText().toString().trim();
            String ip = edt_device_ip.getText().toString().trim();
            String lift = edt_lift_id.getText().toString().trim();
            String mic = edt_mic_id.getText().toString().trim();
            int liftId = 0, micId = 0;
            try {
                liftId = Integer.parseInt(lift);
                micId = Integer.parseInt(mic);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(name)) {
                ToastUtils.showShort(R.string.please_enter_device_name);
                return;
            }
            int modifyFlag = InterfaceMacro.Pb_DeviceModifyFlag.Pb_DEVICE_MODIFYFLAG_NAME_VALUE
                    | InterfaceMacro.Pb_DeviceModifyFlag.Pb_DEVICE_MODIFYFLAG_IPADDR_VALUE
                    | InterfaceMacro.Pb_DeviceModifyFlag.Pb_DEVICE_MODIFYFLAG_LIFTRES_VALUE;
            List<InterfaceDevice.pbui_SubItem_DeviceIpAddrInfo> ipInfos = new ArrayList<>();
            InterfaceDevice.pbui_SubItem_DeviceIpAddrInfo ipInfo = InterfaceDevice.pbui_SubItem_DeviceIpAddrInfo.newBuilder()
                    .setIp(s2b(ip))
                    .setPort(device.getIpinfo(0).getPort())
                    .build();
            ipInfos.add(ipInfo);
            InterfaceDevice.pbui_DeviceModInfo build = InterfaceDevice.pbui_DeviceModInfo.newBuilder()
                    .setDeviceflag(modifyFlag)
                    .setDevcieid(device.getDevcieid())
                    .setDevname(s2b(name))
                    .addAllIpinfo(ipInfos)
                    .setLiftgroupres0(liftId)
                    .setLiftgroupres1(micId)
                    .setDeviceflag(device.getDeviceflag())
                    .build();
            jni.modifyDeviceInfo(build);
            modifyPop.dismiss();
        });
    }

    public static class ViewHolder {
        public View rootView;
        private ImageView ivClose;
        private RecyclerView rv_client;
        private CheckBox cbUseServerIp;
        private EditText edtServerIp;
        private CheckBox cbUsePort;
        private EditText edtPort;
        private CheckBox cbUseCacheLocation;
        private EditText edtCacheLocation;
        private Button btnModifyCacheLocation;
        private CheckBox cbUseCacheSize;
        private EditText edtCacheSize;
        private CheckBox cbUseHardwareCoding;
        private CheckBox cbHardwareCoding;
        private CheckBox cbUseHardwareDecoding;
        private CheckBox cbHardwareDecoding;
        private CheckBox cbUseEnableDebug;
        private CheckBox cbEnableDebug;
        private CheckBox cbUseOpenCamera;
        private CheckBox cbOpenCamera;
        private CheckBox cbUseOpenMic;
        private CheckBox cbOpenMic;
        private CheckBox cbUseDisableMulticast;
        private CheckBox cbDisableMulticast;
        private CheckBox cbUseConvertSmoothFormats;
        private CheckBox cbConvertSmoothFormats;
        private CheckBox cbUseCodingMode;
        private Spinner spCodingMode;
        private CheckBox cbTcp;
        private CheckBox cbUseScreenStream;
        private Spinner spScreenStream;
        private Spinner spScreenSize;
        private CheckBox cbUseCameraStream;
        private Spinner spCameraStream;
        private Spinner spCameraSize;
        private CheckBox cbAllDevice;
        private CheckBox cbAllParameters;
        private CheckBox cbReboot;
        private Button btnCancel;
        private Button btnModify;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            ivClose = (ImageView) rootView.findViewById(R.id.iv_close);
            rv_client = (RecyclerView) rootView.findViewById(R.id.rv_client);
            cbUseServerIp = (CheckBox) rootView.findViewById(R.id.cb_use_server_ip);
            edtServerIp = (EditText) rootView.findViewById(R.id.edt_server_ip);
            cbUsePort = (CheckBox) rootView.findViewById(R.id.cb_use_port);
            edtPort = (EditText) rootView.findViewById(R.id.edt_port);
            cbUseCacheLocation = (CheckBox) rootView.findViewById(R.id.cb_use_cache_location);
            edtCacheLocation = (EditText) rootView.findViewById(R.id.edt_cache_location);
            btnModifyCacheLocation = (Button) rootView.findViewById(R.id.btn_modify_cache_location);
            cbUseCacheSize = (CheckBox) rootView.findViewById(R.id.cb_use_cache_size);
            edtCacheSize = (EditText) rootView.findViewById(R.id.edt_cache_size);
            cbUseHardwareCoding = (CheckBox) rootView.findViewById(R.id.cb_use_hardware_coding);
            cbHardwareCoding = (CheckBox) rootView.findViewById(R.id.cb_hardware_coding);
            cbUseHardwareDecoding = (CheckBox) rootView.findViewById(R.id.cb_use_hardware_decoding);
            cbHardwareDecoding = (CheckBox) rootView.findViewById(R.id.cb_hardware_decoding);
            cbUseEnableDebug = (CheckBox) rootView.findViewById(R.id.cb_use_enable_debug);
            cbEnableDebug = (CheckBox) rootView.findViewById(R.id.cb_enable_debug);
            cbUseOpenCamera = (CheckBox) rootView.findViewById(R.id.cb_use_open_camera);
            cbOpenCamera = (CheckBox) rootView.findViewById(R.id.cb_open_camera);
            cbUseOpenMic = (CheckBox) rootView.findViewById(R.id.cb_use_open_mic);
            cbOpenMic = (CheckBox) rootView.findViewById(R.id.cb_open_mic);
            cbUseDisableMulticast = (CheckBox) rootView.findViewById(R.id.cb_use_disable_multicast);
            cbDisableMulticast = (CheckBox) rootView.findViewById(R.id.cb_disable_multicast);
            cbUseConvertSmoothFormats = (CheckBox) rootView.findViewById(R.id.cb_use_convert_smooth_formats);
            cbConvertSmoothFormats = (CheckBox) rootView.findViewById(R.id.cb_convert_smooth_formats);
            cbUseCodingMode = (CheckBox) rootView.findViewById(R.id.cb_use_coding_mode);
            spCodingMode = (Spinner) rootView.findViewById(R.id.sp_coding_mode);
            cbTcp = (CheckBox) rootView.findViewById(R.id.cb_tcp);
            cbUseScreenStream = (CheckBox) rootView.findViewById(R.id.cb_use_screen_stream);
            spScreenStream = (Spinner) rootView.findViewById(R.id.sp_screen_stream);
            spScreenSize = (Spinner) rootView.findViewById(R.id.sp_screen_size);
            cbUseCameraStream = (CheckBox) rootView.findViewById(R.id.cb_use_camera_stream);
            spCameraStream = (Spinner) rootView.findViewById(R.id.sp_camera_stream);
            spCameraSize = (Spinner) rootView.findViewById(R.id.sp_camera_size);
            cbAllDevice = (CheckBox) rootView.findViewById(R.id.cb_all_device);
            cbAllParameters = (CheckBox) rootView.findViewById(R.id.cb_all_parameters);
            cbReboot = (CheckBox) rootView.findViewById(R.id.cb_reboot);
            btnCancel = (Button) rootView.findViewById(R.id.btn_cancel);
            btnModify = (Button) rootView.findViewById(R.id.btn_modify);
        }
    }
}
