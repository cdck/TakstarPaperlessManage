package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.camera;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceVideo;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.CameraAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xlk.takstarpaperlessmanage.model.Constant.s2b;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
public class CameraFragment extends BaseFragment<CameraPresenter> implements CameraContract.View {

    private RecyclerView rv_available_camera;
    private RecyclerView rv_optional_equipment;
    private CameraAdapter availableAdapter, optionalAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_camera_manage;
    }

    @Override
    protected void initView(View inflate) {
        rv_available_camera = inflate.findViewById(R.id.rv_available_camera);
        rv_optional_equipment = inflate.findViewById(R.id.rv_optional_equipment);
        inflate.findViewById(R.id.btn_modify).setOnClickListener(v -> {
            List<InterfaceVideo.pbui_Item_MeetVideoDetailInfo> selectedCameras = availableAdapter.getSelectedCameras();
            if (selectedCameras.isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_available_camera_first);
                return;
            }
            if (selectedCameras.size() > 1) {
                ToastUtils.showShort(R.string.can_only_choose_one);
                return;
            }
            showModifyPop(selectedCameras.get(0));
        });
        inflate.findViewById(R.id.btn_add).setOnClickListener(v -> {
            if (optionalAdapter.getSelectedCameras().isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_optional_device_first);
                return;
            }
            jni.addMeetVideo(optionalAdapter.getSelectedCameras());
        });
        inflate.findViewById(R.id.btn_remove).setOnClickListener(v -> {
            if (availableAdapter.getSelectedCameras().isEmpty()) {
                ToastUtils.showShort(R.string.please_choose_available_camera_first);
                return;
            }
            jni.deleteMeetVideo(availableAdapter.getSelectedCameras());
        });
    }

    private void showModifyPop(InterfaceVideo.pbui_Item_MeetVideoDetailInfo item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_modify_file, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, rv_available_camera, Gravity.CENTER, width1 / 2, 0);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        TextView tv_name = inflate.findViewById(R.id.tv_name);
        EditText edt_name = inflate.findViewById(R.id.edt_name);

        tv_title.setText(getString(R.string.modify));
        tv_name.setVisibility(View.VISIBLE);
        tv_name.setText(getString(R.string.device_name_));
        edt_name.setText(item.getName().toStringUtf8());
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String newName = edt_name.getText().toString();
            if (newName.isEmpty()) {
                ToastUtil.showShort(R.string.please_enter_name_first);
                return;
            }
            InterfaceVideo.pbui_Item_MeetVideoDetailInfo build = InterfaceVideo.pbui_Item_MeetVideoDetailInfo.newBuilder()
                    .setId(item.getId())
                    .setAddr(item.getAddr())
                    .setDeviceid(item.getDeviceid())
                    .setDevicename(item.getDevicename())
                    .setSubid(item.getSubid())
                    .setName(s2b(newName))
                    .build();
            jni.modifyMeetVideo(build);
            pop.dismiss();
        });
    }

    @Override
    protected CameraPresenter initPresenter() {
        return new CameraPresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryMeetVideo();
    }

    @Override
    public void updateAvailableCameraList() {
        if (availableAdapter == null) {
            availableAdapter = new CameraAdapter(presenter.availableCameras);
            rv_available_camera.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_available_camera.addItemDecoration(new RvItemDecoration(getContext()));
            rv_available_camera.setAdapter(availableAdapter);
            availableAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    availableAdapter.choose(presenter.availableCameras.get(position).getId());
                }
            });
        } else {
            availableAdapter.customNotify();
        }
    }

    @Override
    public void updateOptionalCameraList() {
        if (optionalAdapter == null) {
            optionalAdapter = new CameraAdapter(presenter.optionalCameras);
            rv_optional_equipment.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_optional_equipment.addItemDecoration(new RvItemDecoration(getContext()));
            rv_optional_equipment.setAdapter(optionalAdapter);
            optionalAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    optionalAdapter.choose(presenter.optionalCameras.get(position).getId());
                }
            });
        } else {
            optionalAdapter.customNotify();
        }
    }
}
