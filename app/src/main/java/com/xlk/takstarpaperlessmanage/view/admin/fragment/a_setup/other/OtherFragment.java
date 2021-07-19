package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.other;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.PictureFileAdapter;
import com.xlk.takstarpaperlessmanage.adapter.UrlAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.bean.MainInterfaceBean;
import com.xlk.takstarpaperlessmanage.ui.ColorPickerDialog;
import com.xlk.takstarpaperlessmanage.ui.InterfaceDragView;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.AfterTextWatcher;
import com.xlk.takstarpaperlessmanage.util.FileUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xlk.takstarpaperlessmanage.model.Constant.s2b;
import static com.xlk.takstarpaperlessmanage.model.Constant.s2md5;

/**
 * @author Created by xlk on 2021/5/14.
 * @desc
 */
public class OtherFragment extends BaseFragment<OtherPresenter> implements OtherContract.View, View.OnClickListener {
    private EditText edtWebsite;
    private EditText edtCompanyName;
    private EditText edtCompanyRelease;
    private EditText edtOldPassword;
    private EditText edtNewPassword;
    private EditText edtEnsureNewPassword;
    private PictureFileAdapter releasefileAdapter, bgfileAdapter, upgradefileAdapter;
    private PopupWindow releasefilePop;
    private final int RELEASE_FILE_REQUEST_CODE = 1;
    private final int PICTURE_FILE_REQUEST_CODE = 2;
    private final int UPDATE_FILE_REQUEST_CODE = 3;
    private int currentReleaseFileMediaId;

    private final int POP_TAG_MAIN = 1;
    private final int POP_TAG_PROJECTIVE = 2;
    private final int POP_TAG_NOTICE = 3;
    private int CURRENT_POP_TAG;
    private PopupWindow interfacePop;
    private InterfaceDragView dragView;
    private LinearLayout ll_size, ll_sp;
    private EditText pop_edt_width, pop_edt_height, pop_edt_text_size;
    private ImageView pop_iv_text_color;
    private Spinner pop_sp_bold, pop_sp_show, pop_sp_font, pop_sp_align;
    private PopupWindow bgPicturePop, upgradePop;
    private RecyclerView rv_pop_url;
    private UrlAdapter urlAdapter;
    private PopupWindow urlPop;
    private EditText edt_file_address, edt_export_address, edt_video_address, edt_archive_address;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_other_setting;
    }

    @Override
    protected void initView(View inflate) {
        edtWebsite = (EditText) inflate.findViewById(R.id.edt_website);
        edtWebsite.setKeyListener(null);
        inflate.findViewById(R.id.btn_website).setOnClickListener(this);
        edtCompanyName = (EditText) inflate.findViewById(R.id.edt_company_name);
        inflate.findViewById(R.id.btn_company_name).setOnClickListener(this);
        edtCompanyRelease = (EditText) inflate.findViewById(R.id.edt_company_release);
        inflate.findViewById(R.id.btn_company_release).setOnClickListener(this);
        edtOldPassword = (EditText) inflate.findViewById(R.id.edt_old_password);
        edtNewPassword = (EditText) inflate.findViewById(R.id.edt_new_password);
        edtEnsureNewPassword = (EditText) inflate.findViewById(R.id.edt_ensure_new_password);
        inflate.findViewById(R.id.btn_password).setOnClickListener(this);
        inflate.findViewById(R.id.btn_change_main_interface).setOnClickListener(this);
        inflate.findViewById(R.id.btn_change_projection_interface).setOnClickListener(this);
        inflate.findViewById(R.id.btn_change_ad_interface).setOnClickListener(this);
        inflate.findViewById(R.id.btn_equipment_upgrade).setOnClickListener(this);
        inflate.findViewById(R.id.btn_system_file_management).setOnClickListener(this);
    }

    @Override
    protected OtherPresenter initPresenter() {
        return new OtherPresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryLocalAdmin();
        presenter.queryWebUrl();
        presenter.queryReleaseFile();
        presenter.queryUpgradeFile();
        presenter.queryInterFaceConfiguration();
        presenter.queryBigFile();
    }

    @Override
    public void updateWebUrlList() {
        if (presenter.allUrls.isEmpty()) {
            edtWebsite.setText("");
        } else {
            edtWebsite.setText(presenter.allUrls.get(0).getName().toStringUtf8());
        }
        if (urlPop != null && urlPop.isShowing()) {
            urlAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateReleaseFileList() {
        if (releasefileAdapter != null) {
            releasefileAdapter.customNotify(currentReleaseFileMediaId);
//            releasefileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateUpgradeFileList() {

    }

    @Override
    public void updateBgFileList() {
        if (bgfileAdapter != null) {
            bgfileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateReleaseFileName(String fileName, int mediaid) {
        getActivity().runOnUiThread(() -> {
            currentReleaseFileMediaId = mediaid;
            edtCompanyRelease.setText(fileName);
            updateReleaseFileList();
        });
    }

    @Override
    public void updateCompany(String company) {
        getActivity().runOnUiThread(() -> edtCompanyName.setText(company));
    }

    @Override
    public void updateInterface() {
        if (interfacePop != null && interfacePop.isShowing()) {
            getActivity().runOnUiThread(() -> {
                switch (CURRENT_POP_TAG) {
                    case POP_TAG_MAIN:
                        dragView.initInterfaceView(presenter.mainInterfaceBeans);
                        break;
                    case POP_TAG_PROJECTIVE:
                        dragView.initInterfaceView(presenter.projectiveInterfaceBeans);
                        break;
                    case POP_TAG_NOTICE:
                        dragView.initInterfaceView(presenter.noticeInterfaceBeans);
                        break;
                    default:
                        break;
                }
            });
        }
    }

    @Override
    public void updateMainBgImg(String filePath) {
        if (interfacePop != null && interfacePop.isShowing()) {
            if (dragView != null) {
                if (CURRENT_POP_TAG == POP_TAG_MAIN) {
                    LogUtils.i(TAG, "updateMainBgImg filePath=" + filePath);
                    getActivity().runOnUiThread(() -> {
                        Drawable drawable = Drawable.createFromPath(filePath);
                        dragView.setBackground(drawable);
                    });
                }
            }
        }
    }

    @Override
    public void updateMainLogoImg(String filePath) {
        LogUtils.e("updateMainLogoImg filePath=" + filePath);
        if (interfacePop != null && interfacePop.isShowing()) {
            getActivity().runOnUiThread(() -> {
                dragView.updateLogoImg(POP_TAG_MAIN, filePath);
            });
        }
    }

    @Override
    public void updateProjectiveBgImg(String filePath) {
        if (interfacePop != null && interfacePop.isShowing()) {
            if (dragView != null) {
                LogUtils.i(TAG, "updateProjectiveBgImg filePath=" + filePath);
                if (CURRENT_POP_TAG == POP_TAG_PROJECTIVE) {
                    getActivity().runOnUiThread(() -> {
                        Drawable drawable = Drawable.createFromPath(filePath);
                        dragView.setBackground(drawable);
                    });
                }
            }
        }
    }

    @Override
    public void updateProjectiveLogoImg(String filePath) {
        if (interfacePop != null && interfacePop.isShowing()) {
            getActivity().runOnUiThread(() -> {
                dragView.updateLogoImg(POP_TAG_PROJECTIVE, filePath);
            });
        }
    }

    @Override
    public void updateNoticeBgImg(String filePath) {
        if (interfacePop != null && interfacePop.isShowing()) {
            if (dragView != null) {
                LogUtils.i(TAG, "updateNoticeBgImg filePath=" + filePath);
                if (CURRENT_POP_TAG == POP_TAG_NOTICE) {
                    getActivity().runOnUiThread(() -> {
                        Drawable drawable = Drawable.createFromPath(filePath);
                        dragView.setBackground(drawable);
                    });
                }
            }
        }
    }

    @Override
    public void updateNoticeLogoImg(String filePath) {
        if (interfacePop != null && interfacePop.isShowing()) {
            getActivity().runOnUiThread(() -> {
                dragView.updateLogoImg(POP_TAG_NOTICE, filePath);
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_website: {
                showUrlPop();
                break;
            }
            case R.id.btn_company_name: {
                String companyName = edtCompanyName.getText().toString();
                if (companyName.isEmpty()) {
                    ToastUtil.showShort(R.string.please_enter_name_first);
                    return;
                }
                showDefineModifyCompanyNamePop(companyName);
                break;
            }
            case R.id.btn_company_release: {
                showReleaseFilePop();
                break;
            }
            case R.id.btn_password: {
                modifyAdminPassword();
                break;
            }
            case R.id.btn_change_main_interface: {
                CURRENT_POP_TAG = POP_TAG_MAIN;
                showInterfacePop(CURRENT_POP_TAG);
                presenter.queryInterFaceConfiguration();
                break;
            }
            case R.id.btn_change_projection_interface: {
                CURRENT_POP_TAG = POP_TAG_PROJECTIVE;
                showInterfacePop(CURRENT_POP_TAG);
                presenter.queryInterFaceConfiguration();
                break;
            }
            case R.id.btn_change_ad_interface: {
                CURRENT_POP_TAG = POP_TAG_NOTICE;
                showInterfacePop(CURRENT_POP_TAG);
                presenter.queryInterFaceConfiguration();
                break;
            }
            case R.id.btn_equipment_upgrade: {
                showUpgradePop();
                break;
            }
            case R.id.btn_system_file_management: {
                showFileManagePop();
                break;
            }
        }
    }

    private void showFileManagePop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_file_manage, null);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, edtCompanyName, Gravity.CENTER, width1 / 2, 0);
        edt_file_address = inflate.findViewById(R.id.edt_file_address);
        edt_export_address = inflate.findViewById(R.id.edt_export_address);
        edt_video_address = inflate.findViewById(R.id.edt_video_address);
        edt_archive_address = inflate.findViewById(R.id.edt_archive_address);
        edt_file_address.setKeyListener(null);
        edt_export_address.setKeyListener(null);
        edt_video_address.setKeyListener(null);
        edt_archive_address.setKeyListener(null);

        edt_file_address.setText(Constant.download_dir);
        edt_file_address.setSelection(Constant.download_dir.length());

        edt_export_address.setText(Constant.export_dir);
        edt_export_address.setSelection(Constant.export_dir.length());

        edt_video_address.setText(Constant.video_dir);
        edt_video_address.setSelection(Constant.video_dir.length());

        edt_archive_address.setText(Constant.archive_zip_dir);
        edt_archive_address.setSelection(Constant.archive_zip_dir.length());
        inflate.findViewById(R.id.btn_file_dir).setOnClickListener(v -> {
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.CHOOSE_DIR_PATH).objects(Constant.CHOOSE_DIR_TYPE_DEFAULT_DOWNLOAD, edt_file_address.getText().toString().trim()).build());
        });
        inflate.findViewById(R.id.btn_export_dir).setOnClickListener(v -> {
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.CHOOSE_DIR_PATH).objects(Constant.CHOOSE_DIR_TYPE_DEFAULT_EXPORT, edt_export_address.getText().toString().trim()).build());
        });
        inflate.findViewById(R.id.btn_video_dir).setOnClickListener(v -> {
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.CHOOSE_DIR_PATH).objects(Constant.CHOOSE_DIR_TYPE_DEFAULT_VIDEO, edt_video_address.getText().toString().trim()).build());
        });
        inflate.findViewById(R.id.btn_archive_dir).setOnClickListener(v -> {
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.CHOOSE_DIR_PATH).objects(Constant.CHOOSE_DIR_TYPE_DEFAULT_ARCHIVE, edt_archive_address.getText().toString().trim()).build());
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String downloadPath = edt_file_address.getText().toString().trim();
            String exportPath = edt_export_address.getText().toString().trim();
            String videoPath = edt_video_address.getText().toString().trim();
            String archivePath = edt_archive_address.getText().toString().trim();
            if (TextUtils.isEmpty(downloadPath)) {
                ToastUtil.showShort(R.string.please_enter_download_dir);
                return;
            }
            if (TextUtils.isEmpty(exportPath)) {
                ToastUtil.showShort(R.string.please_enter_export_dir);
                return;
            }
            if (TextUtils.isEmpty(videoPath)) {
                ToastUtil.showShort(R.string.please_enter_video_dir);
                return;
            }
            if (TextUtils.isEmpty(archivePath)) {
                ToastUtil.showShort(R.string.please_enter_archivePath_dir);
                return;
            }
            Constant.download_dir = downloadPath;
            Constant.export_dir = exportPath;
            Constant.video_dir = videoPath;
            Constant.archive_zip_dir = archivePath;
            pop.dismiss();
        });
    }

    @Override
    public void updateExportDirPath(int dirType, String dirPath) {
        dirPath += "/";
        switch (dirType) {
            case Constant.CHOOSE_DIR_TYPE_DEFAULT_DOWNLOAD: {
                if (edt_file_address != null) {
                    edt_file_address.setText(dirPath);
                    edt_file_address.setSelection(dirPath.length());
                }
                break;
            }
            case Constant.CHOOSE_DIR_TYPE_DEFAULT_EXPORT: {
                if (edt_export_address != null) {
                    edt_export_address.setText(dirPath);
                    edt_export_address.setSelection(dirPath.length());
                }
                break;
            }
            case Constant.CHOOSE_DIR_TYPE_DEFAULT_VIDEO: {
                if (edt_video_address != null) {
                    edt_video_address.setText(dirPath);
                    edt_video_address.setSelection(dirPath.length());
                }
                break;
            }
            case Constant.CHOOSE_DIR_TYPE_DEFAULT_ARCHIVE: {
                if (edt_archive_address != null) {
                    edt_archive_address.setText(dirPath);
                    edt_archive_address.setSelection(dirPath.length());
                }
                break;
            }
        }
    }

    /**
     * 确定是否要修改公司名弹窗
     *
     * @param companyName 新输入的公司名称
     */
    private void showDefineModifyCompanyNamePop(String companyName) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_define, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width / 2, height / 3, edtCompanyName, Gravity.CENTER, width1 / 2, 0);
        TextView tv_content = inflate.findViewById(R.id.tv_content);
        tv_content.setText(getString(R.string.tip_define_to_modify_company_name));
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            presenter.modifyCompanyName(companyName);
            pop.dismiss();
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
    }

    private void showUrlPop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_url, null);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        urlPop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, edtCompanyName, Gravity.CENTER, width1 / 2, 0);

        rv_pop_url = inflate.findViewById(R.id.rv_pop_url);
        EditText edt_pop_name = inflate.findViewById(R.id.edt_pop_name);
        EditText edt_pop_url = inflate.findViewById(R.id.edt_pop_url);
        urlAdapter = new UrlAdapter(R.layout.item_other_url, presenter.allUrls);
        rv_pop_url.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_pop_url.setAdapter(urlAdapter);
        urlAdapter.setOnItemClickListener((adapter, view, position) -> {
            InterfaceBase.pbui_Item_UrlDetailInfo item = presenter.allUrls.get(position);
            urlAdapter.setSelect(item.getId());
            edt_pop_name.setText(item.getName().toStringUtf8());
            edt_pop_url.setText(item.getAddr().toStringUtf8());
        });
        inflate.findViewById(R.id.btn_pop_add).setOnClickListener(v -> {
            String name = edt_pop_name.getText().toString().trim();
            String addr = edt_pop_url.getText().toString().trim();
            if (name.isEmpty() || addr.isEmpty()) {
                ToastUtil.showShort(R.string.please_enter_url);
                return;
            }
            InterfaceBase.pbui_Item_UrlDetailInfo build = InterfaceBase.pbui_Item_UrlDetailInfo.newBuilder()
                    .setName(s2b(name))
                    .setAddr(s2b(addr))
                    .build();
            jni.addUrl(build);
        });
        inflate.findViewById(R.id.btn_pop_modify).setOnClickListener(v -> {
            int selectedId = urlAdapter.getSelectedId();
            if (selectedId == -1) {
                ToastUtil.showShort(R.string.please_choose_url_first);
                return;
            }
            String name = edt_pop_name.getText().toString().trim();
            String addr = edt_pop_url.getText().toString().trim();
            if (name.isEmpty() || addr.isEmpty()) {
                ToastUtil.showShort(R.string.please_enter_url);
                return;
            }
            InterfaceBase.pbui_Item_UrlDetailInfo build = InterfaceBase.pbui_Item_UrlDetailInfo.newBuilder()
                    .setId(selectedId)
                    .setName(s2b(name))
                    .setAddr(s2b(addr))
                    .build();
            jni.modifyUrl(build);
        });
        inflate.findViewById(R.id.btn_pop_delete).setOnClickListener(v -> {
            InterfaceBase.pbui_Item_UrlDetailInfo selectUrl = urlAdapter.getSelectUrl();
            if (selectUrl == null) {
                ToastUtil.showShort(R.string.please_choose_url_first);
                return;
            }
            jni.delUrl(selectUrl);
        });
        inflate.findViewById(R.id.btn_pop_close).setOnClickListener(v -> {
            urlPop.dismiss();
        });
    }

    private void showUpgradePop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_release_file, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        upgradePop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, edtCompanyName, Gravity.CENTER, width1 / 2, 0);
        RecyclerView rv_file = inflate.findViewById(R.id.rv_file);
        upgradefileAdapter = new PictureFileAdapter(presenter.upgradeFiles);
        rv_file.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_file.addItemDecoration(new RvItemDecoration(getContext()));
        rv_file.setAdapter(upgradefileAdapter);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.upgrade_file));
        inflate.findViewById(R.id.btn_cancel_release).setVisibility(View.GONE);
        CheckBox cbAll = inflate.findViewById(R.id.cb_all);
        cbAll.setOnClickListener(v -> {
            boolean checked = cbAll.isChecked();
            cbAll.setChecked(checked);
            upgradefileAdapter.setSelectedAll(checked);
        });
        upgradefileAdapter.addChildClickViewIds(R.id.item_view_5);
        upgradefileAdapter.setOnItemClickListener((adapter, view, position) -> {
            upgradefileAdapter.setSelected(presenter.upgradeFiles.get(position).getMediaid());
            cbAll.setChecked(upgradefileAdapter.isSelectedAll());
        });
        upgradefileAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo info = presenter.upgradeFiles.get(position);
            if (view.getId() == R.id.item_view_5) {
                jni.deleteMeetDirFile(0, info);
            }
        });
        //上传
        inflate.findViewById(R.id.btn_upload).setOnClickListener(v -> {
            chooseLocalFile(UPDATE_FILE_REQUEST_CODE);
        });
        //删除
        inflate.findViewById(R.id.btn_delete).setOnClickListener(v -> {
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> selectedFiles = upgradefileAdapter.getSelectedFiles();
            if (selectedFiles.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_file_first);
                return;
            }
            jni.deleteMeetDirFile(0, selectedFiles);
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> upgradePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> upgradePop.dismiss());
        inflate.findViewById(R.id.btn_release).setOnClickListener(v -> {
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> selectedFiles = upgradefileAdapter.getSelectedFiles();
            if (selectedFiles.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_file_first);
                return;
            }
            if (selectedFiles.size() > 1) {
                ToastUtil.showShort(R.string.can_only_choose_one);
                return;
            }
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo info = selectedFiles.get(0);
            jni.updateDevice(info.getMediaid());
        });
    }

    private void modifyAdminPassword() {
        String oldPwd = edtOldPassword.getText().toString().trim();
        String newPwd = edtNewPassword.getText().toString().trim();
        String ensureNewPwd = edtEnsureNewPassword.getText().toString().trim();
        if (newPwd.equals(ensureNewPwd)) {
            showDefinePop(oldPwd, newPwd);
        } else {
            ToastUtil.showShort(R.string.the_password_entered_twice_does_not_match);
        }
    }

    /**
     * 确定是否要修改登录密码弹窗
     *
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     */
    private void showDefinePop(String oldPwd, String newPwd) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_define, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width / 2, height / 3, edtCompanyName, Gravity.CENTER, width1 / 2, 0);
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            presenter.modifyLocalAdminPassword(s2md5(oldPwd), s2md5(newPwd));
            pop.dismiss();
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
    }

    private void showReleaseFilePop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_release_file, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        releasefilePop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, edtCompanyName, Gravity.CENTER, width1 / 2, 0);
        RecyclerView rv_file = inflate.findViewById(R.id.rv_file);
        releasefileAdapter = new PictureFileAdapter(presenter.releaseFileData, true, currentReleaseFileMediaId);
        rv_file.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_file.addItemDecoration(new RvItemDecoration(getContext()));
        rv_file.setAdapter(releasefileAdapter);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.release_file));
        CheckBox cbAll = inflate.findViewById(R.id.cb_all);
        cbAll.setOnClickListener(v -> {
            boolean checked = cbAll.isChecked();
            cbAll.setChecked(checked);
            releasefileAdapter.setSelectedAll(checked);
        });
        releasefileAdapter.addChildClickViewIds(R.id.item_view_5, R.id.item_view_6);
        releasefileAdapter.setOnItemClickListener((adapter, view, position) -> {
            releasefileAdapter.setSelected(presenter.releaseFileData.get(position).getMediaid());
            cbAll.setChecked(releasefileAdapter.isSelectedAll());
        });
        releasefileAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo info = presenter.releaseFileData.get(position);
            if (view.getId() == R.id.item_view_5) {
                jni.deleteMeetDirFile(0, info);
            } else {
                presenter.saveReleaseFile(0);
            }
        });
        //上传
        inflate.findViewById(R.id.btn_upload).setOnClickListener(v -> {
            chooseLocalFile(RELEASE_FILE_REQUEST_CODE);
        });
        //删除
        inflate.findViewById(R.id.btn_delete).setOnClickListener(v -> {
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> selectedFiles = releasefileAdapter.getSelectedFiles();
            if (selectedFiles.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_file_first);
                return;
            }
            jni.deleteMeetDirFile(0, selectedFiles);
        });
        //取消发布
        inflate.findViewById(R.id.btn_cancel_release).setOnClickListener(v -> {
            presenter.saveReleaseFile(0);
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> releasefilePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> releasefilePop.dismiss());
        inflate.findViewById(R.id.btn_release).setOnClickListener(v -> {
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> selectedFiles = releasefileAdapter.getSelectedFiles();
            if (selectedFiles.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_file_first);
                return;
            }
            if (selectedFiles.size() > 1) {
                ToastUtil.showShort(R.string.can_only_choose_one);
                return;
            }
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo info = selectedFiles.get(0);
            presenter.saveReleaseFile(info.getMediaid());
//            FileUtils.createOrExistsDir(Constant.config_dir);
//            jni.creationFileDownload(Constant.config_dir + Constant.ROOM_BG_PNG_TAG + ".png",
//                    info.getMediaid(), 1, 0, Constant.ROOM_BG_PNG_TAG);
//            releasefilePop.dismiss();
        });
    }

    private void showInterfacePop(int tag) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_interface, null);
        interfacePop = PopUtil.createPopupWindow(inflate, ScreenUtils.getScreenWidth() * 3 / 4, ScreenUtils.getScreenHeight() * 3 / 4, edtCompanyName);
        dragView = inflate.findViewById(R.id.interface_view);
        ll_size = inflate.findViewById(R.id.ll_size);
        ll_sp = inflate.findViewById(R.id.ll_sp);
        pop_edt_width = inflate.findViewById(R.id.edt_width);
        pop_edt_height = inflate.findViewById(R.id.edt_height);
        pop_edt_text_size = inflate.findViewById(R.id.edt_text_size);
        pop_iv_text_color = inflate.findViewById(R.id.iv_text_color);
        CheckBox cb_all = inflate.findViewById(R.id.cb_all);
//        cb_all.setVisibility(tag == POP_TAG_MAIN ? View.GONE : View.VISIBLE);
        pop_sp_bold = inflate.findViewById(R.id.sp_bold);
        pop_sp_show = inflate.findViewById(R.id.sp_show);
        pop_sp_font = inflate.findViewById(R.id.sp_font);
        pop_sp_align = inflate.findViewById(R.id.sp_align);
        pop_sp_bold.setOnItemSelectedListener(mainSpSelectedListener);
        pop_sp_show.setOnItemSelectedListener(mainSpSelectedListener);
        pop_sp_font.setOnItemSelectedListener(mainSpSelectedListener);
        pop_sp_align.setOnItemSelectedListener(mainSpSelectedListener);
        dragView.post(() -> {
            switch (tag) {
                case POP_TAG_MAIN:
                    dragView.initInterfaceView(presenter.mainInterfaceBeans);
                    break;
                case POP_TAG_PROJECTIVE:
                    dragView.initInterfaceView(presenter.projectiveInterfaceBeans);
                    break;
                case POP_TAG_NOTICE:
                    dragView.initInterfaceView(presenter.noticeInterfaceBeans);
                    break;
                default:
                    break;
            }
        });
        dragView.setViewClickListener(new InterfaceDragView.ViewClickListener() {
            @Override
            public void onClick(MainInterfaceBean data, int width, int height) {
                LogUtils.d(TAG, "onClick 选中的view信息=" + data.toString());
                pop_edt_width.setText(String.valueOf(width));
                pop_edt_height.setText(String.valueOf(height));
                pop_edt_text_size.setText(String.valueOf(data.getFontSize()));
                pop_iv_text_color.setBackgroundColor(data.getColor());
                int fontFlag = data.getFontFlag();
                boolean isBold = (fontFlag & InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_BOLD_VALUE)
                        == InterfaceMacro.Pb_MeetFaceFontFlag.Pb_MEET_FONTFLAG_BOLD_VALUE;
                pop_sp_bold.setSelection(isBold ? 1 : 0);
                int flag = data.getFlag();
                boolean isShow = (flag & InterfaceMacro.Pb_TableCardFlag.Pb_MEET_TABLECARDFLAG_SHOW_VALUE)
                        == InterfaceMacro.Pb_TableCardFlag.Pb_MEET_TABLECARDFLAG_SHOW_VALUE;
                pop_sp_show.setSelection(isShow ? 1 : 0);
                int position = getSpinnerFontPositionByName(data.getFontName());
                pop_sp_font.setSelection(position);
                pop_sp_align.setSelection(getSpinnerAlignPosition(data.getAlign()));
            }

            @Override
            public void onHide(boolean hide) {
                ll_size.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
                ll_sp.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
            }
        });
        cb_all.setOnClickListener(v -> {
            boolean checked = cb_all.isChecked();
            cb_all.setChecked(checked);
            dragView.showAll(CURRENT_POP_TAG, checked);
        });
        //字体颜色
        pop_iv_text_color.setOnClickListener(v -> {
            new ColorPickerDialog(getContext(), color -> getActivity().runOnUiThread(() -> {
                dragView.updateTextColor(color);
                pop_iv_text_color.setBackgroundColor(color);
            }), Color.BLACK).show();
        });
        //宽
        pop_edt_width.addTextChangedListener(new AfterTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()) {
                    getActivity().runOnUiThread(() -> {
                        int width = Integer.parseInt(s.toString().trim());
                        LogUtils.i(TAG, "afterTextChanged 宽改变=" + width);
                        dragView.updateViewWidth(width);
                    });
                }
            }
        });
        //高
        pop_edt_height.addTextChangedListener(new AfterTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()) {
                    getActivity().runOnUiThread(() -> {
                        int height = Integer.parseInt(s.toString().trim());
                        LogUtils.i(TAG, "afterTextChanged 高改变=" + height);
                        dragView.updateViewHeight(height);
                    });
                }
            }
        });
        //字体大小
        pop_edt_text_size.addTextChangedListener(new AfterTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()) {
                    getActivity().runOnUiThread(() -> {
                        int size = Integer.parseInt(s.toString().trim());
                        LogUtils.i(TAG, "afterTextChanged 字体大小改变=" + size);
                        dragView.updateTextSize(size);
                    });
                }
            }
        });
        //背景图
        inflate.findViewById(R.id.btn_bg).setOnClickListener(v -> {
            showPictureFilePop(tag, false);
        });
        //logo图
        inflate.findViewById(R.id.btn_logo).setOnClickListener(v -> {
            showPictureFilePop(tag, true);
        });
        //取消Logo
        inflate.findViewById(R.id.btn_cancel_logo).setOnClickListener(v -> {
            switch (tag) {
                case POP_TAG_MAIN:
                    presenter.saveMainLogo(0);
                    break;
                case POP_TAG_PROJECTIVE:
                    presenter.saveProjectiveLogo(0);
                    break;
                case POP_TAG_NOTICE:
                    presenter.saveNoticeLogo(0);
                    break;
                default:
                    break;
            }
        });
        //保存
        inflate.findViewById(R.id.btn_save).setOnClickListener(v -> {
            List<MainInterfaceBean> data = dragView.getData();
            presenter.saveInterfaceConfig(data);
        });
        //复位
        inflate.findViewById(R.id.btn_reset).setOnClickListener(v -> {
            dragView.reset();
        });
    }

    private void showPictureFilePop(int tag, boolean isLogo) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_bg_picture, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        bgPicturePop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, edtCompanyName, Gravity.CENTER, width1 / 2, 0);
        RecyclerView rv_file = inflate.findViewById(R.id.rv_file);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(isLogo ? getString(R.string.choose_logo_icon) : getString(R.string.choose_background_picture));
        bgfileAdapter = new PictureFileAdapter(presenter.bgFiles);
        rv_file.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_file.addItemDecoration(new RvItemDecoration(getContext()));
        rv_file.setAdapter(bgfileAdapter);
        CheckBox cbAll = inflate.findViewById(R.id.cb_all);
        cbAll.setOnClickListener(v -> {
            boolean checked = cbAll.isChecked();
            cbAll.setChecked(checked);
            bgfileAdapter.setSelectedAll(checked);
        });
        bgfileAdapter.addChildClickViewIds(R.id.item_view_5);
        bgfileAdapter.setOnItemClickListener((adapter, view, position) -> {
            bgfileAdapter.setSelected(presenter.bgFiles.get(position).getMediaid());
            cbAll.setChecked(bgfileAdapter.isSelectedAll());
        });
        bgfileAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo info = presenter.bgFiles.get(position);
            jni.deleteMeetDirFile(0, info);
        });
        inflate.findViewById(R.id.btn_upload).setOnClickListener(v -> {
            chooseLocalFile(PICTURE_FILE_REQUEST_CODE);
        });
        inflate.findViewById(R.id.btn_delete).setOnClickListener(v -> {
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> selectedFiles = bgfileAdapter.getSelectedFiles();
            if (selectedFiles.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_file_first);
                return;
            }
            jni.deleteMeetDirFile(0, selectedFiles);
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> bgPicturePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> bgPicturePop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> selectedFiles = bgfileAdapter.getSelectedFiles();
            if (selectedFiles.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_file_first);
                return;
            }
            if (selectedFiles.size() > 1) {
                ToastUtil.showShort(R.string.can_only_choose_one);
                return;
            }
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo info = selectedFiles.get(0);
            int mediaid = info.getMediaid();
            switch (tag) {
                case POP_TAG_MAIN:
                    if (isLogo) {
                        presenter.saveMainLogo(mediaid);
                    } else {
                        presenter.saveMainBg(mediaid);
                    }
                    break;
                case POP_TAG_PROJECTIVE:
                    if (isLogo) {
                        presenter.saveProjectiveLogo(mediaid);
                    } else {
                        presenter.saveProjectiveBg(mediaid);
                    }
                    break;
                case POP_TAG_NOTICE:
                    if (isLogo) {
                        presenter.saveNoticeLogo(mediaid);
                    } else {
                        presenter.saveNoticeBg(mediaid);
                    }
                    break;
                default:
                    break;
            }
            bgPicturePop.dismiss();
        });
    }

    private AdapterView.OnItemSelectedListener mainSpSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (parent.getId()) {
                case R.id.sp_bold: {
                    dragView.updateTextBold(position);
                    break;
                }
                case R.id.sp_show: {
                    dragView.updateViewShow(position);
                    break;
                }
                case R.id.sp_font: {
                    String fontName = (String) pop_sp_font.getSelectedItem();
                    dragView.updateTextFont(fontName);
                    break;
                }
                case R.id.sp_align: {
                    dragView.updateTextAlign(position);
                    break;
                }
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private int getSpinnerAlignPosition(int align) {
        switch (align) {
            //左对齐
            case InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_LEFT_VALUE:
                return 1;
            //右对齐
            case InterfaceMacro.Pb_FontAlignFlag.Pb_MEET_FONTALIGNFLAG_RIGHT_VALUE:
                return 2;
            //居中对齐
            default:
                return 0;
        }
    }

    /**
     * 根据字体名称获取Spinner索引
     *
     * @param fontName 字体名称 “宋体”..
     */
    private int getSpinnerFontPositionByName(String fontName) {
        switch (fontName) {
            case "黑体":
                return 1;
            case "楷体":
                return 2;
            case "隶书":
                return 3;
            case "微软雅黑":
                return 4;
            case "小楷":
                return 5;
            default:
                return 0;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            File file = UriUtils.uri2File(data.getData());
            if (!FileUtils.isFileExists(file)) {
                LogUtils.e(TAG, "onActivityResult 获取文件路径失败 requestCode=" + requestCode);
                return;
            }
            String absolutePath = file.getAbsolutePath();
            String fileName = file.getName();
            if (requestCode == RELEASE_FILE_REQUEST_CODE) {//上传发布文件
                jni.uploadFile(0, 0, InterfaceMacro.Pb_MeetFileAttrib.Pb_MEETFILE_ATTRIB_PUBLISH_VALUE,
                        fileName, absolutePath, 0, Constant.UPLOAD_PUBLISH_FILE);
            } else if (requestCode == PICTURE_FILE_REQUEST_CODE) {//上传背景图片
                if (FileUtil.isPicture(fileName)) {
                    jni.uploadFile(0, 0, InterfaceMacro.Pb_MeetFileAttrib.Pb_MEETFILE_ATTRIB_BACKGROUND_VALUE,
                            fileName, absolutePath, 0, Constant.UPLOAD_BACKGROUND_IMAGE);
                }
            } else if (requestCode == UPDATE_FILE_REQUEST_CODE) {
                if (fileName.endsWith(".upz")) {
                    jni.uploadFile(0, 0, InterfaceMacro.Pb_MeetFileAttrib.Pb_MEETFILE_ATTRIB_DEVICEUPDATE_VALUE,
                            fileName, absolutePath, 0, Constant.UPLOAD_UPGRADE_FILE);
                } else {
                    ToastUtils.showShort(R.string.please_choose_upz_file);
                }
            }
        }
    }

}
