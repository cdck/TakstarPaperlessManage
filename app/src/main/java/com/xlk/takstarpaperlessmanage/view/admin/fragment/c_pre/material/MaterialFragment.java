package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.material;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.DirAdapter;
import com.xlk.takstarpaperlessmanage.adapter.DirFileAdapter;
import com.xlk.takstarpaperlessmanage.adapter.DirPermissionAdapter;
import com.xlk.takstarpaperlessmanage.adapter.MemberAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;
import com.xlk.takstarpaperlessmanage.model.bean.MemberDirPermissionBean;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.FileUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xlk.takstarpaperlessmanage.model.Constant.s2b;

/**
 * @author Created by xlk on 2021/5/20.
 * @desc
 */
public class MaterialFragment extends BaseFragment<MaterialPresenter> implements MaterialContract.View {
    private RecyclerView rvDir;
    private Button btnModify;
    private Button btnDelete;
    private Button btnDirectoryPermissions;
    private Button btnAdd;
    private Button btnModifyFile;
    private Button btnDeleteFile;
    private Button btnSortMaterial;
    private Button btnImportHistory;
    private RecyclerView rvFile;
    private DirAdapter dirAdapter;
    private DirFileAdapter dirFileAdapter;
    private PopupWindow modifyFilePop;
    private PopupWindow deleteFilePop;
    private int currentDirId;
    private final int REQUEST_CODE_UPLOAD_FILE = 1;
    private DirFileAdapter sortFileAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_material;
    }

    @Override
    protected void initView(View inflate) {
        rvDir = (RecyclerView) inflate.findViewById(R.id.rv_dir);
        rvFile = (RecyclerView) inflate.findViewById(R.id.rv_file);
        btnModify = (Button) inflate.findViewById(R.id.btn_modify);
        btnDelete = (Button) inflate.findViewById(R.id.btn_delete);
        btnDirectoryPermissions = (Button) inflate.findViewById(R.id.btn_directory_permissions);
        btnAdd = (Button) inflate.findViewById(R.id.btn_add);
        btnModifyFile = (Button) inflate.findViewById(R.id.btn_modify_file);
        btnDeleteFile = (Button) inflate.findViewById(R.id.btn_delete_file);
        btnSortMaterial = (Button) inflate.findViewById(R.id.btn_sort_material);
        btnImportHistory = (Button) inflate.findViewById(R.id.btn_import_history);
        btnModify.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnDirectoryPermissions.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnModifyFile.setOnClickListener(this);
        btnDeleteFile.setOnClickListener(this);
        btnSortMaterial.setOnClickListener(this);
        btnImportHistory.setOnClickListener(this);
    }

    @Override
    protected MaterialPresenter initPresenter() {
        return new MaterialPresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryMember();
        presenter.queryMeetDir();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_modify: {
                InterfaceFile.pbui_Item_MeetDirDetailInfo selectedDir = dirAdapter.getSelectedDir();
                if (selectedDir == null) {
                    ToastUtils.showShort(R.string.please_choose_dir_first);
                    return;
                }
                if (selectedDir.getId() == Constant.SHARE_DIR_ID || selectedDir.getId() == Constant.ANNOTATION_DIR_ID) {
                    ToastUtil.showShort(R.string.can_not_modify_share_and_annotation);
                    return;
                }
                showModifyFilePop(null, selectedDir);
                break;
            }
            case R.id.btn_delete: {
                InterfaceFile.pbui_Item_MeetDirDetailInfo selectedDir = dirAdapter.getSelectedDir();
                if (selectedDir == null) {
                    ToastUtils.showShort(R.string.please_choose_dir_first);
                    return;
                }
                if (selectedDir.getId() == Constant.SHARE_DIR_ID || selectedDir.getId() == Constant.ANNOTATION_DIR_ID) {
                    ToastUtil.showShort(R.string.can_not_delete_share_and_annotation);
                    return;
                }
                showDeleteFilePop(null, selectedDir);
                break;
            }
            case R.id.btn_directory_permissions: {
                presenter.queryMeetDirPermission(currentDirId);
                showDirPermission(presenter.getDirPermission());
                break;
            }
            case R.id.btn_add: {
                chooseLocalFile(REQUEST_CODE_UPLOAD_FILE);
                break;
            }
            case R.id.btn_modify_file: {
                break;
            }
            case R.id.btn_delete_file: {
                break;
            }
            case R.id.btn_sort_material: {
                presenter.sortFiles.clear();
                presenter.sortFiles.addAll(presenter.dirFiles);
                if (!presenter.sortFiles.isEmpty()) {
                    showSortPop();
                }
                break;
            }
            case R.id.btn_import_history: {
                break;
            }
        }
    }

    private void showDirPermission(List<MemberDirPermissionBean> datas) {
        for (int i = 0; i < datas.size(); i++) {
            MemberDirPermissionBean bean = datas.get(i);
            LogUtils.i(bean.toString());
        }
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_dir_permission, null, false);
        int dp_10 = ConvertUtils.dp2px(10);
        int dp_20 = ConvertUtils.dp2px(20);
        View top_view = getActivity().findViewById(R.id.top_view);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        View ll_navigation = getActivity().findViewById(R.id.ll_navigation);
        int top_viewH = top_view.getHeight();
        int rv_navigationW = rv_navigation.getWidth();
        int ll_navigationH = ll_navigation.getHeight();
        int x = rv_navigationW + dp_10 + dp_10 + dp_10 + dp_10;
        int y = top_viewH + ll_navigationH + dp_10 + dp_10 + dp_20 + dp_10;
        View fl_admin = getActivity().findViewById(R.id.fl_admin);
        int width = fl_admin.getWidth();
        int height = fl_admin.getHeight();
        PopupWindow pop = PopUtil.createPopupWindowAt(inflate, width - dp_20 - dp_20 - dp_10, height - dp_20 - dp_20,
                false, btnAdd, Gravity.TOP | Gravity.START, x, y);

        RecyclerView rv_content = inflate.findViewById(R.id.rv_content);
        CheckBox cb_check_all = inflate.findViewById(R.id.cb_check_all);
        DirPermissionAdapter dirPermissionAdapter = new DirPermissionAdapter(datas);
        rv_content.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_content.addItemDecoration(new RvItemDecoration(getContext()));
        rv_content.setAdapter(dirPermissionAdapter);
        dirPermissionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                dirPermissionAdapter.setCheck(datas.get(position).getMember().getPersonid());
                cb_check_all.setChecked(dirPermissionAdapter.isCheckAll());
            }
        });
        cb_check_all.setOnClickListener(v -> {
            boolean checked = cb_check_all.isChecked();
            cb_check_all.setChecked(checked);
            dirPermissionAdapter.setCheckAll(checked);
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            List<Integer> checks = dirPermissionAdapter.getChecks();
            jni.saveMeetDirPermission(currentDirId, checks);
            pop.dismiss();
        });
    }

    private void showSortPop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_sort_file, null, false);
        int dp_10 = ConvertUtils.dp2px(10);
        int dp_20 = ConvertUtils.dp2px(20);
        View top_view = getActivity().findViewById(R.id.top_view);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        View ll_navigation = getActivity().findViewById(R.id.ll_navigation);
        int top_viewH = top_view.getHeight();
        int rv_navigationW = rv_navigation.getWidth();
        int ll_navigationH = ll_navigation.getHeight();
        int x = rv_navigationW + dp_10 + dp_10 + dp_10 + dp_10;
        int y = top_viewH + ll_navigationH + dp_10 + dp_10 + dp_20 + dp_10;
        View fl_admin = getActivity().findViewById(R.id.fl_admin);
        int width = fl_admin.getWidth();
        int height = fl_admin.getHeight();
        LogUtils.i("showParameterConfigurationPop width=" + width + ",height=" + height + ",dp10=" + dp_10);
        PopupWindow sortFilePop = PopUtil.createPopupWindowAt(inflate, width - dp_20 - dp_20 - dp_10, height - dp_20 - dp_20,
                false, btnAdd, Gravity.TOP | Gravity.START, x, y);

        RecyclerView rv_content = inflate.findViewById(R.id.rv_content);
        sortFileAdapter = new DirFileAdapter(presenter.sortFiles, true);
        rv_content.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_content.addItemDecoration(new RvItemDecoration(getContext()));
        rv_content.setAdapter(sortFileAdapter);
        sortFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                sortFileAdapter.setSelectedId(presenter.sortFiles.get(position).getMediaid());
            }
        });

        inflate.findViewById(R.id.btn_move_up).setOnClickListener(v -> {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo selected = sortFileAdapter.getSelected();
            if (selected == null) {
                ToastUtil.showShort(R.string.please_choose_file_first);
                return;
            }
            int index = 0;
            for (int i = 0; i < presenter.sortFiles.size(); i++) {
                if (selected.getMediaid() == presenter.sortFiles.get(i).getMediaid()) {
                    index = i;
                    break;
                }
            }
            if (index == 0) {
                //要上移的目标已经是第一项，则移动到最下方
                presenter.sortFiles.remove(index);
                presenter.sortFiles.add(selected);
            } else {
                Collections.swap(presenter.sortFiles, index, index - 1);
            }
            sortFileAdapter.notifyDataSetChanged();
        });
        inflate.findViewById(R.id.btn_move_down).setOnClickListener(v -> {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo selected = sortFileAdapter.getSelected();
            if (selected == null) {
                ToastUtils.showShort(R.string.please_choose_member_first);
                return;
            }
            int index = 0;
            for (int i = 0; i < presenter.sortFiles.size(); i++) {
                if (selected.getMediaid() == presenter.sortFiles.get(i).getMediaid()) {
                    index = i;
                    break;
                }
            }
            if (index == presenter.sortFiles.size() - 1) {
                //要下移的目标已经是最后一项，则进行移动到最上方
                List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> temps = new ArrayList<>();
                presenter.sortFiles.remove(index);
                temps.add(selected);
                temps.addAll(presenter.sortFiles);
                presenter.sortFiles.clear();
                presenter.sortFiles.addAll(temps);
                temps.clear();
            } else {
                Collections.swap(presenter.sortFiles, index, index + 1);
            }
            sortFileAdapter.notifyDataSetChanged();
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> sortFilePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> sortFilePop.dismiss());
        inflate.findViewById(R.id.btn_save).setOnClickListener(v -> {
            List<Integer> ids = new ArrayList<>();
            for (int i = 0; i < presenter.sortFiles.size(); i++) {
                ids.add(presenter.sortFiles.get(i).getMediaid());
            }
            jni.modifyMeetDirFileSort(currentDirId, ids);
            sortFilePop.dismiss();
        });
    }

    @Override
    public void updateDirList() {
        if (dirAdapter == null) {
            dirAdapter = new DirAdapter(presenter.dirInfos);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvDir.setLayoutManager(layoutManager);
            rvDir.setAdapter(dirAdapter);
            dirAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    currentDirId = presenter.dirInfos.get(position).getId();
                    dirAdapter.setSelectedId(currentDirId);
                    presenter.setCurrentDirId(currentDirId);
                    presenter.queryMeetDirFile(currentDirId);
                }
            });
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.dir_footer_view, rvDir, false);
            dirAdapter.addFooterView(inflate);
            inflate.findViewById(R.id.footer_view).setOnClickListener(v -> {
                LogUtils.e("点击脚布局");
            });
            if (!presenter.dirInfos.isEmpty()) {
                currentDirId = presenter.dirInfos.get(0).getId();
                dirAdapter.setSelectedId(currentDirId);
                presenter.setCurrentDirId(currentDirId);
                presenter.queryMeetDirFile(currentDirId);
            }
        } else {
            dirAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateFileList() {
        if (dirFileAdapter == null) {
            dirFileAdapter = new DirFileAdapter(presenter.dirFiles);
            rvFile.setLayoutManager(new LinearLayoutManager(getContext()));
            rvFile.addItemDecoration(new RvItemDecoration(getContext()));
            rvFile.setAdapter(dirFileAdapter);
            dirFileAdapter.addChildClickViewIds(R.id.operation_view_1, R.id.operation_view_2, R.id.operation_view_3);
            dirFileAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                    InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = presenter.dirFiles.get(position);
                    switch (view.getId()) {
                        //修改
                        case R.id.operation_view_1: {
                            showModifyFilePop(item, null);
                            break;
                        }
                        //删除
                        case R.id.operation_view_2: {
                            showDeleteFilePop(item, null);
                            break;
                        }
                        //查看
                        case R.id.operation_view_3: {
                            String fileName = item.getName().toStringUtf8();
                            if (FileUtil.isAudio(fileName)) {
                                LogUtils.e("播放文件=" + fileName);
                                jni.mediaPlayOperate(item.getMediaid(), GlobalValue.localDeviceId,
                                        0, Constant.RESOURCE_ID_0, 0, 0);
                                return;
//                                Bundle bundle = new Bundle();
//                                bundle.putString("filePath", filePath);
//                                bundle.putString("fileName", fileName);
//                                context.startActivity(new Intent(context, LocalPlayActivity.class)
//                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                        .putExtras(bundle));
                            }
                            String filePath = Constant.file_dir + fileName;
                            boolean fileExists = FileUtils.isFileExists(filePath);
                            if (fileExists) {
                                FileUtil.openFile(getContext(), filePath);
                            } else {
                                jni.downloadFile(filePath, item.getMediaid(), 1, 0, Constant.DOWNLOAD_OPEN_FILE);
                            }
                            break;
                        }
                    }
                }
            });
        } else {
            dirFileAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 是否删除目录/目录文件
     *
     * @param dirFile 文件
     * @param dir     目录
     */
    private void showDeleteFilePop(InterfaceFile.pbui_Item_MeetDirFileDetailInfo dirFile, InterfaceFile.pbui_Item_MeetDirDetailInfo dir) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_delete, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        deleteFilePop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rvFile, Gravity.CENTER, width1 / 2, 0);
        TextView tv_hint = inflate.findViewById(R.id.tv_hint);
        boolean isDelFile = dirFile != null;
        tv_hint.setText(isDelFile ? getString(R.string.delete_file_hint) : getString(R.string.delete_dir_hint));
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> deleteFilePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> deleteFilePop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            if (isDelFile) {
                jni.deleteMeetDirFile(currentDirId, dirFile);
            } else {
                jni.deleteMeetDir(dir);
            }
            deleteFilePop.dismiss();
        });
    }

    /**
     * 修改文件名称/目录名称
     *
     * @param dirFile 文件
     * @param dir     目录
     */
    private void showModifyFilePop(InterfaceFile.pbui_Item_MeetDirFileDetailInfo dirFile, InterfaceFile.pbui_Item_MeetDirDetailInfo dir) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_modify_file, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        modifyFilePop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rvFile, Gravity.CENTER, width1 / 2, 0);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        EditText edt_name = inflate.findViewById(R.id.edt_name);

        boolean isModFile = dirFile != null;
        tv_title.setText(isModFile ? getString(R.string.modify_file_name) : getString(R.string.modify_dir_name));
        edt_name.setText(isModFile ? dirFile.getName().toStringUtf8() : dir.getName().toStringUtf8());
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> modifyFilePop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> modifyFilePop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String newName = edt_name.getText().toString();
            if (newName.isEmpty()) {
                ToastUtil.showShort(R.string.please_enter_name_first);
                return;
            }
            if (isModFile) {
                String fileName = dirFile.getName().toStringUtf8();
                String suffix = fileName.substring(fileName.lastIndexOf("."));
                if (!newName.endsWith(suffix)) {
                    ToastUtil.showShort(getString(R.string.file_name_hint, suffix));
                    return;
                }
                InterfaceFile.pbui_Item_MeetDirFileDetailInfo build = InterfaceFile.pbui_Item_MeetDirFileDetailInfo.newBuilder()
                        .setName(s2b(newName))
                        .setUploaderRole(dirFile.getUploaderRole())
                        .setUploaderName(dirFile.getUploaderName())
                        .setUploaderid(dirFile.getUploaderid())
                        .setSize(dirFile.getSize())
                        .setMstime(dirFile.getMstime())
                        .setMediaid(dirFile.getMediaid())
                        .setFilepos(dirFile.getFilepos())
                        .setAttrib(dirFile.getAttrib())
                        .build();
                jni.modifyMeetDirFile(currentDirId, build);
            } else {
                InterfaceFile.pbui_Item_MeetDirDetailInfo build = InterfaceFile.pbui_Item_MeetDirDetailInfo.newBuilder()
                        .setName(s2b(newName))
                        .setParentid(dir.getParentid())
                        .setId(dir.getId())
                        .setFilenum(dir.getFilenum())
                        .build();
                jni.modifyMeetDir(build);
            }
            modifyFilePop.dismiss();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            File file = UriUtils.uri2File(uri);
            if (file == null) {
                return;
            }
            if (requestCode == REQUEST_CODE_UPLOAD_FILE) {
                jni.uploadFile(InterfaceMacro.Pb_Upload_Flag.Pb_MEET_UPLOADFLAG_ZERO_VALUE,
                        currentDirId, 0, file.getName(), file.getAbsolutePath(),
                        0, Constant.UPLOAD_CHOOSE_FILE);
            }
        }
    }
}
