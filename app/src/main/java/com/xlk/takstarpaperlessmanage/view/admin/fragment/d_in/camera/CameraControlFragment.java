package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.camera;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceVideo;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.MeetLiveVideoAdapter;
import com.xlk.takstarpaperlessmanage.adapter.ProjectorAdapter;
import com.xlk.takstarpaperlessmanage.adapter.ScreenMemberAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;
import com.xlk.takstarpaperlessmanage.model.bean.VideoDevice;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.ui.gl.CustomVideoView;
import com.xlk.takstarpaperlessmanage.ui.gl.ViewClickListener;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * @author Created by xlk on 2021/5/25.
 * @desc
 */
public class CameraControlFragment extends BaseFragment<CameraControlPresenter> implements CameraControlContract.View, ViewClickListener {

    private RecyclerView rv_video_list;
    private CustomVideoView customVideoView;
    private MeetLiveVideoAdapter meetLiveVideoAdapter;
    private ScreenMemberAdapter memberAdapter;
    private ProjectorAdapter projectorAdapter;
    private int width, height;
    private List<Integer> screenDeviceIds;
    private List<Integer> proDeviceIds;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_camera_control;
    }

    @Override
    protected void initView(View inflate) {
        rv_video_list = inflate.findViewById(R.id.rv_video_list);
        customVideoView = (CustomVideoView) inflate.findViewById(R.id.custom_video_view);
        customVideoView.setViewClickListener(this);
        inflate.findViewById(R.id.btn_play).setOnClickListener(v -> {
            VideoDevice videoDevice = meetLiveVideoAdapter.getSelected();
            if (videoDevice == null) {
                ToastUtil.showShort(R.string.please_choose_device_first);
                return;
            }
            int selectResId = customVideoView.getSelectResId();
            if (selectResId != -1) {
                presenter.watch(videoDevice, selectResId);
            } else {
                ToastUtil.showShort(R.string.please_choose_preview_window);
            }
        });
        inflate.findViewById(R.id.btn_stop).setOnClickListener(v -> {
            int selectResId = customVideoView.getSelectResId();
            if (selectResId != -1) {
                List<Integer> devIds = new ArrayList<>();
                devIds.add(GlobalValue.localDeviceId);
                jni.stopResourceOperate(selectResId, devIds);
            } else {
                ToastUtil.showShort(R.string.please_choose_preview_window);
            }
        });
        inflate.findViewById(R.id.btn_launch_screen).setOnClickListener(v -> {
            VideoDevice videoDevice = meetLiveVideoAdapter.getSelected();
            if (videoDevice == null) {
                ToastUtil.showShort(R.string.please_choose_device_first);
                return;
            }
            showScreenPop(videoDevice, true);
        });
        inflate.findViewById(R.id.btn_stop_screen).setOnClickListener(v -> {
            if (screenDeviceIds == null || screenDeviceIds.isEmpty()) {
                ToastUtil.showShort(R.string.no_targets_being_on_the_same_screen);
                return;
            }
            List<Integer> temps = new ArrayList<>();
            temps.add(Constant.RESOURCE_ID_0);
            jni.stopResourceOperate(temps, screenDeviceIds);
        });
        inflate.findViewById(R.id.btn_launch_pro).setOnClickListener(v -> {
            VideoDevice videoDevice = meetLiveVideoAdapter.getSelected();
            if (videoDevice == null) {
                ToastUtil.showShort(R.string.please_choose_device_first);
                return;
            }
            showProPop(videoDevice);
        });
        inflate.findViewById(R.id.btn_stop_pro).setOnClickListener(v -> {
            if (proDeviceIds == null || proDeviceIds.isEmpty()) {
                ToastUtil.showShort(R.string.no_targets_being_projected);
                return;
            }
            List<Integer> temps = new ArrayList<>();
            temps.add(Constant.RESOURCE_ID_0);
            jni.stopResourceOperate(temps, proDeviceIds);
        });
    }

    private void showProPop(VideoDevice videoDevice) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_pro, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, rv_video_list, Gravity.CENTER, width1 / 2, 0);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        CheckBox cb_all = inflate.findViewById(R.id.cb_all);
        CheckBox cb_mandatory = inflate.findViewById(R.id.cb_mandatory);
        CheckBox cb_full = inflate.findViewById(R.id.cb_full);
        CheckBox cb_res1 = inflate.findViewById(R.id.cb_res1);
        CheckBox cb_res2 = inflate.findViewById(R.id.cb_res2);
        CheckBox cb_res3 = inflate.findViewById(R.id.cb_res3);
        CheckBox cb_res4 = inflate.findViewById(R.id.cb_res4);
        RecyclerView rv_projection = inflate.findViewById(R.id.rv_projection);
        cb_full.setOnClickListener(v -> {
            boolean checked = cb_full.isChecked();
            cb_full.setChecked(checked);
            if (checked) {
                cb_res1.setChecked(false);
                cb_res2.setChecked(false);
                cb_res3.setChecked(false);
                cb_res4.setChecked(false);
            }
        });
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_full.setChecked(false);
                }
            }
        };
        cb_res1.setOnCheckedChangeListener(listener);
        cb_res2.setOnCheckedChangeListener(listener);
        cb_res3.setOnCheckedChangeListener(listener);
        cb_res4.setOnCheckedChangeListener(listener);
        rv_projection.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        rv_projection.setAdapter(projectorAdapter);
        projectorAdapter.setOnItemClickListener((adapter, view, position) -> {
            projectorAdapter.choose(presenter.onLineProjectors.get(position).getDevcieid());
            cb_all.setChecked(projectorAdapter.isChooseAll());
        });
        cb_all.setOnClickListener(v -> {
            boolean checked = cb_all.isChecked();
            cb_all.setChecked(checked);
            projectorAdapter.setChooseAll(checked);
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_launch_pro).setOnClickListener(v -> {
            proDeviceIds = projectorAdapter.getChooseIds();
            if (proDeviceIds.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_projection_first);
                return;
            }
            List<Integer> resIds = new ArrayList<>();
            if (cb_full.isChecked()) {
                resIds.add(Constant.RESOURCE_ID_0);
            } else {
                if (cb_res1.isChecked()) resIds.add(Constant.RESOURCE_ID_1);
                if (cb_res2.isChecked()) resIds.add(Constant.RESOURCE_ID_2);
                if (cb_res3.isChecked()) resIds.add(Constant.RESOURCE_ID_3);
                if (cb_res4.isChecked()) resIds.add(Constant.RESOURCE_ID_4);
            }
            if (resIds.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_res_first);
                return;
            }
            InterfaceVideo.pbui_Item_MeetVideoDetailInfo videoDetailInfo = videoDevice.getVideoDetailInfo();
            int deviceid = videoDetailInfo.getDeviceid();
            int subid = videoDetailInfo.getSubid();
            int triggeruserval = 0;
            if (cb_mandatory.isChecked()) {//是否强制
                triggeruserval = InterfaceMacro.Pb_TriggerUsedef.Pb_EXCEC_USERDEF_FLAG_NOCREATEWINOPER_VALUE;
            }
            jni.streamPlay(deviceid, subid, triggeruserval, resIds, screenDeviceIds);
            pop.dismiss();
        });
    }

    private void showScreenPop(VideoDevice videoDevice, boolean isLaunch) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_screen_member, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        PopupWindow pop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, rv_video_list, Gravity.CENTER, width1 / 2, 0);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(isLaunch ? getString(R.string.choose_same_screen_target) : getString(R.string.choose_projection_target));
        CheckBox cb_all = inflate.findViewById(R.id.cb_all);
        CheckBox cb_all_online = inflate.findViewById(R.id.cb_all_online);
        CheckBox cb_mandatory = inflate.findViewById(R.id.cb_mandatory);
        RecyclerView rv_member = inflate.findViewById(R.id.rv_member);
        rv_member.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_member.addItemDecoration(new RvItemDecoration(getContext()));
        rv_member.setAdapter(memberAdapter);
        memberAdapter.setOnItemClickListener((adapter, view, position) -> {
            memberAdapter.choose(presenter.onLineMember.get(position).getDeviceDetailInfo().getDevcieid());
            cb_all.setChecked(memberAdapter.isChooseAll());
        });
        cb_all.setOnClickListener(v -> {
            boolean checked = cb_all.isChecked();
            cb_all.setChecked(checked);
            memberAdapter.setChooseAll(checked);
        });
        RecyclerView rv_pro = inflate.findViewById(R.id.rv_pro);
        rv_pro.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        rv_pro.setAdapter(projectorAdapter);
        projectorAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                projectorAdapter.choose(presenter.onLineProjectors.get(position).getDevcieid());
            }
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_launch_screen).setOnClickListener(v -> {
            screenDeviceIds = memberAdapter.getSelectedDeviceIds();
            screenDeviceIds.addAll(projectorAdapter.getChooseIds());
            if (screenDeviceIds.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_member_first);
                return;
            }
            List<Integer> temps = new ArrayList<>();
            temps.add(Constant.RESOURCE_ID_0);
            if (isLaunch) {
                InterfaceVideo.pbui_Item_MeetVideoDetailInfo videoDetailInfo = videoDevice.getVideoDetailInfo();
                int deviceid = videoDetailInfo.getDeviceid();
                int subid = videoDetailInfo.getSubid();
                int triggeruserval = 0;
                if (cb_mandatory.isChecked()) {//是否强制同屏
                    triggeruserval = InterfaceMacro.Pb_TriggerUsedef.Pb_EXCEC_USERDEF_FLAG_NOCREATEWINOPER_VALUE;
                }
                jni.streamPlay(deviceid, subid, triggeruserval, temps, screenDeviceIds);
            } else {
                jni.stopResourceOperate(temps, screenDeviceIds);
            }
            pop.dismiss();
        });
    }

    @Override
    protected CameraControlPresenter initPresenter() {
        return new CameraControlPresenter(this);
    }

    @Override
    protected void initial() {
        memberAdapter = new ScreenMemberAdapter(presenter.onLineMember);
        projectorAdapter = new ProjectorAdapter(presenter.onLineProjectors);
        customVideoView.post(() -> {
            width = customVideoView.getWidth();
            height = customVideoView.getHeight();
            start();
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) stop();
        else start();
    }

    private void start() {
        presenter.initVideoRes(width, height);
        customVideoView.createView();
        presenter.queryDeviceInfo();
    }

    private void stop() {
        List<Integer> resIds = new ArrayList<>();
        resIds.add(Constant.RESOURCE_ID_1);
        resIds.add(Constant.RESOURCE_ID_2);
        resIds.add(Constant.RESOURCE_ID_3);
        resIds.add(Constant.RESOURCE_ID_4);
        jni.stopResourceOperate(resIds, GlobalValue.localDeviceId);
        customVideoView.clearAll();
        presenter.releaseVideoRes();
    }

    @Override
    public void updateRv(List<VideoDevice> videoDevs) {
        if (meetLiveVideoAdapter == null) {
            meetLiveVideoAdapter = new MeetLiveVideoAdapter(videoDevs);
            rv_video_list.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_video_list.setAdapter(meetLiveVideoAdapter);
            meetLiveVideoAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    VideoDevice videoDevice = videoDevs.get(position);
                    InterfaceVideo.pbui_Item_MeetVideoDetailInfo videoDetailInfo = videoDevice.getVideoDetailInfo();
                    meetLiveVideoAdapter.setSelected(videoDetailInfo.getDeviceid(), videoDetailInfo.getId());
                }
            });
        } else {
            meetLiveVideoAdapter.notifySelect();
        }
    }

    @Override
    public void notifyOnLineAdapter() {
        if (memberAdapter != null) {
            memberAdapter.notifyDataSetChanged();
            memberAdapter.notifyChecks();
        }
        if (projectorAdapter != null) {
            projectorAdapter.notifyDataSetChanged();
            projectorAdapter.notifyChecks();
        }
    }

    @Override
    public void updateDecode(Object[] objs) {
        customVideoView.setVideoDecode(objs);
    }

    @Override
    public void updateYuv(Object[] objs) {
        customVideoView.setYuv(objs);
    }

    @Override
    public void stopResWork(int resid) {
        customVideoView.stopResWork(resid);
    }

    long oneTime, twoTime, threeTime, fourTime;

    @Override
    public void click(int res) {
        switch (res) {
            case Constant.RESOURCE_ID_1:
                customVideoView.setSelectResId(res);
                if (System.currentTimeMillis() - oneTime < 500) {
                    customVideoView.zoom(res);
                } else {
                    oneTime = System.currentTimeMillis();
                }
                break;
            case Constant.RESOURCE_ID_2:
                customVideoView.setSelectResId(res);
                if (System.currentTimeMillis() - twoTime < 500) {
                    customVideoView.zoom(res);
                } else {
                    twoTime = System.currentTimeMillis();
                }
                break;
            case Constant.RESOURCE_ID_3:
                customVideoView.setSelectResId(res);
                if (System.currentTimeMillis() - threeTime < 500) {
                    customVideoView.zoom(res);
                } else {
                    threeTime = System.currentTimeMillis();
                }
                break;
            case Constant.RESOURCE_ID_4:
                customVideoView.setSelectResId(res);
                if (System.currentTimeMillis() - fourTime < 500) {
                    customVideoView.zoom(res);
                } else {
                    fourTime = System.currentTimeMillis();
                }
                break;
            default:
                break;
        }
    }
}
