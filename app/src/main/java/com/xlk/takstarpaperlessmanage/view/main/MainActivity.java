package com.xlk.takstarpaperlessmanage.view.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.mogujie.tt.protobuf.InterfaceAdmin;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.base.BaseActivity;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;
import com.xlk.takstarpaperlessmanage.util.SpHelper;
import com.xlk.takstarpaperlessmanage.view.admin.AdminActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    private com.google.android.material.textfield.TextInputEditText edtUser;
    private com.google.android.material.textfield.TextInputEditText edtPwd;
    private android.widget.CheckBox cbRemember;

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
            String user = edtUser.getText().toString().trim();
            String pwd = edtPwd.getText().toString().trim();
            if (user.isEmpty() || pwd.isEmpty()) {
                ToastUtils.showShort(R.string.please_enter_name_and_password);
                return;
            }
            SpHelper.setData(this, SpHelper.key_user, user);
            SpHelper.setData(this, SpHelper.key_remember, cbRemember.isChecked());
            SpHelper.setData(this, SpHelper.key_password, cbRemember.isChecked() ? pwd : "");
            jni.login(user, pwd, 1, 0);
        });
        findViewById(R.id.btn_config).setOnClickListener(v -> {

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
                            start();
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean never) {

                    }
                });
    }

    private void start() {
        if (!GlobalValue.initializationIsOver) {
            initConfigFile();
            jni.javaInitSys(DeviceUtils.getUniqueDeviceId());
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