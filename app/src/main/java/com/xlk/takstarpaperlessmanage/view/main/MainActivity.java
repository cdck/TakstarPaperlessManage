package com.xlk.takstarpaperlessmanage.view.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.mogujie.tt.protobuf.InterfaceAdmin;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.App;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.base.BaseActivity;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;
import com.xlk.takstarpaperlessmanage.util.IniUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.SizeUtil;
import com.xlk.takstarpaperlessmanage.util.SpHelper;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;
import com.xlk.takstarpaperlessmanage.view.admin.AdminActivity;

import java.io.File;
import java.util.List;

import androidx.annotation.Nullable;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    private com.google.android.material.textfield.TextInputEditText edtUser;
    private com.google.android.material.textfield.TextInputEditText edtPwd;
    private android.widget.CheckBox cbRemember;
    private final int REQUEST_CODE_READ_FRAME_BUFFER = 1;
    private MediaProjectionManager mMediaProjectionManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected MainPresenter initPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public void initView() {
        edtUser = (TextInputEditText) findViewById(R.id.edt_user);
        edtPwd = (TextInputEditText) findViewById(R.id.edt_pwd);
        cbRemember = (CheckBox) findViewById(R.id.cb_remember);
        Boolean remember = (Boolean) SpHelper.getData(this, SpHelper.key_remember, false);
        cbRemember.setChecked(remember);
        String userSp = (String) SpHelper.getData(this, SpHelper.key_user, "");
        edtUser.setText(userSp);
        edtUser.setSelection(userSp.length());
        if (remember) {
            String pwdSp = (String) SpHelper.getData(this, SpHelper.key_password, "");
            edtPwd.setText(pwdSp);
            edtPwd.setSelection(pwdSp.length());
        }
        findViewById(R.id.btn_login).setOnClickListener(v -> {
            if (!GlobalValue.initializationIsOver) {
                ToastUtils.showShort(R.string.not_yet_initialized);
                return;
            }
            String user = edtUser.getText().toString().trim();
            String pwd = edtPwd.getText().toString().trim();
            if (user.isEmpty() || pwd.isEmpty()) {
                ToastUtils.showShort(R.string.please_enter_name_and_password);
                return;
            }
            if (!presenter.isHasAdminName(user)) {
                ToastUtils.showShort(R.string.username_does_not_exist);
                return;
            }
            SpHelper.setData(this, SpHelper.key_user, user);
            SpHelper.setData(this, SpHelper.key_remember, cbRemember.isChecked());
            SpHelper.setData(this, SpHelper.key_password, cbRemember.isChecked() ? pwd : "");
            jni.login(user, pwd, 1, 0);
        });
        findViewById(R.id.btn_config).setOnClickListener(v -> {
            showConfigPop();
        });
    }

    private void showConfigPop() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.pop_config, null, false);
        PopupWindow pop = PopUtil.createHalfPop(inflate, edtUser);
        EditText edt_ip = inflate.findViewById(R.id.edt_ip);
        EditText edt_port = inflate.findViewById(R.id.edt_port);
        EditText edt_code_rate = inflate.findViewById(R.id.edt_code_rate);
        EditText edt_cache_size = inflate.findViewById(R.id.edt_cache_size);
        CheckBox cb_code_filter = inflate.findViewById(R.id.cb_code_filter);
        CheckBox cb_mic = inflate.findViewById(R.id.cb_mic);
        CheckBox cb_disable_multicast = inflate.findViewById(R.id.cb_disable_multicast);
        CheckBox cb_tcp = inflate.findViewById(R.id.cb_tcp);

        IniUtil ini = IniUtil.getInstance();
        if (ini.loadFile()) {
            String ipStr = ini.get("areaaddr", "area0ip");
            String portStr = ini.get("areaaddr", "area0port");
            String maxBitRate = ini.get("other", "maxBitRate");
            String mediadirsize = ini.get("Buffer Dir", "mediadirsize");

            cb_code_filter.setChecked(ini.get("nosdl", "disablebsf").equals("0"));
            cb_mic.setChecked(ini.get("debug", "videoaudio").equals("1"));
            cb_disable_multicast.setChecked(ini.get("debug", "disablemulticast").equals("1"));
            cb_tcp.setChecked(ini.get("selfinfo", "streamprotol").equals("1"));

            edt_ip.setText(ipStr);
            edt_port.setText(portStr);
            edt_code_rate.setText(maxBitRate);
            edt_cache_size.setText(mediadirsize);
        }
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String ip = edt_ip.getText().toString().trim();
            String port = edt_port.getText().toString().trim();
            String code_rate = edt_code_rate.getText().toString().trim();
            String cache_size = edt_cache_size.getText().toString().trim();
            if (ip.isEmpty() || port.isEmpty() || code_rate.isEmpty() || cache_size.isEmpty()) {
                ToastUtil.showShort(R.string.please_enter_the_full_content);
                return;
            }
            if (!RegexUtils.isIP(ip)) {
                ToastUtil.showShort(R.string.ip_address_format_error);
                return;
            }
            int i = Integer.parseInt(code_rate);
            if (i < 500 || i > 10000) {
                ToastUtils.showShort(R.string.tip_bitrate_scope);
                return;
            }
            ini.put("areaaddr", "area0ip", ip);
            ini.put("areaaddr", "area0port", port);
            ini.put("other", "maxBitRate", code_rate);
            ini.put("Buffer Dir", "mediadirsize", cache_size);

            ini.put("nosdl", "disablebsf", cb_code_filter.isChecked() ? "0" : "1");
            ini.put("debug", "videoaudio", cb_mic.isChecked() ? "1" : "0");
            ini.put("debug", "disablemulticast", cb_disable_multicast.isChecked() ? "1" : "0");
            ini.put("selfinfo", "streamprotol", cb_tcp.isChecked() ? "1" : "0");
            ini.store();
            AppUtils.relaunchApp(true);
            pop.dismiss();
        });
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        applyPermissions();
    }

    private void applyPermissions() {
        XXPermissions.with(this)
                .permission(
                        Permission.WRITE_EXTERNAL_STORAGE,
                        Permission.READ_EXTERNAL_STORAGE,
//                        Permission.MANAGE_EXTERNAL_STORAGE,
                        Permission.READ_PHONE_STATE
                )
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean all) {
                        if (all) {
                            FileUtils.createOrExistsDir(Constant.config_dir);
                            FileUtils.createOrExistsDir(Constant.download_dir);
                            FileUtils.createOrExistsDir(Constant.export_dir);
                            FileUtils.createOrExistsDir(Constant.video_dir);
                            start();
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean never) {

                    }
                });
    }

    private void start() {
        SizeUtil.getDeviceScreenSize(this);
        if (App.mMediaProjection == null) {
            mMediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_READ_FRAME_BUFFER);
        } else {
            if (!GlobalValue.initializationIsOver) {
                initConfigFile();
                jni.javaInitSys(DeviceUtils.getUniqueDeviceId());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_READ_FRAME_BUFFER) {
            if (resultCode == Activity.RESULT_OK) {
                App.mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
                start();
            } else {
                start();
            }
        }
    }

    private void initConfigFile() {
        long l = System.currentTimeMillis();
        LogUtils.d("进入initConfigFile");
        FileUtils.createOrExistsDir(Constant.root_dir);
        boolean exists = FileUtils.isFileExists(Constant.root_dir + "client.ini");
        if (!exists) {
            ResourceUtils.copyFileFromAssets("client.ini", Constant.root_dir + "/client.ini");
        }
        IniUtil.getInstance().loadFile();
        File file = new File(Constant.root_dir + "client.dev");
        if (file.exists()) {
            file.delete();
        }
        ResourceUtils.copyFileFromAssets("client.dev", Constant.root_dir + "/client.dev");
        LogUtils.i(TAG, "initConfigFile 用时=" + (System.currentTimeMillis() - l));
    }

    @Override
    public void loginBack(InterfaceAdmin.pbui_Type_AdminLogonStatus info) {
        //管理员登陆状态
        int err = info.getErr();
        int adminid = info.getAdminid();
        String adminname = info.getAdminname().toStringUtf8();
        int sessionid = info.getSessionid();
        LogUtils.i(TAG, "loginStatus adminid:" + adminid + ",adminname:" + adminname + ",sessionid:" + sessionid + ",err:" + err);
        switch (err) {
            //登陆成功
            case InterfaceMacro.Pb_AdminLogonStatus.Pb_ADMINLOGON_ERR_NONE_VALUE:
                ToastUtils.showShort(R.string.login_successful);
                Intent intent = new Intent(this, AdminActivity.class);
//                intent.putExtra(EXTRA_ADMIN_ID, adminid);
//                intent.putExtra(EXTRA_ADMIN_NAME, adminname);
//                if (GlobalValue.localMeetingId != 0) {
//                    jni.modifyContextProperties(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_CURMEETINGID_VALUE
//                            , GlobalValue.localMeetingId);
//                }
                presenter.unregister();
                startActivity(intent);
                finish();
                break;
            //密码错误
            case InterfaceMacro.Pb_AdminLogonStatus.Pb_ADMINLOGON_ERR_PSW_VALUE:
                ToastUtils.showShort(R.string.wrong_password);
                break;
            //服务器异常
            case InterfaceMacro.Pb_AdminLogonStatus.Pb_ADMINLOGON_ERR_EXCPT_SV_VALUE:
                ToastUtils.showShort(R.string.server_exception);
                break;
            //数据库异常
            case InterfaceMacro.Pb_AdminLogonStatus.Pb_ADMINLOGON_ERR_EXCPT_DB_VALUE:
                ToastUtils.showShort(R.string.database_exception);
                break;
            default:
                break;
        }
    }
}