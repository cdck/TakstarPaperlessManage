package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.camera;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceVideo;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.EventMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/21.
 * @desc
 */
class CameraPresenter extends BasePresenter<CameraContract.View> implements CameraContract.Presenter {

    public List<InterfaceVideo.pbui_Item_MeetVideoDetailInfo> availableCameras = new ArrayList<>();
    public List<InterfaceVideo.pbui_Item_MeetVideoDetailInfo> optionalCameras = new ArrayList<>();

    public CameraPresenter(CameraContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会议视频变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO_VALUE:
                LogUtils.i(TAG, "busEvent 会议视频变更通知");
                queryMeetVideo();
                break;
            //会场设备信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE_VALUE:
                LogUtils.i(TAG, "busEvent 会场设备信息变更通知");
                queryMeetVideo();
                break;
            default:
                break;
        }
    }

    @Override
    public void queryMeetVideo() {
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo info = jni.queryMeetVideo();
        availableCameras.clear();
        if (info != null) {
            availableCameras.addAll(info.getItemList());
        }
        queryAllCamera();
    }

    private void queryAllCamera() {
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo info = jni.queryPlaceStream(queryCurrentRoomId());
        optionalCameras.clear();
        if (info != null) {
            List<InterfaceVideo.pbui_Item_MeetVideoDetailInfo> itemList = info.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceVideo.pbui_Item_MeetVideoDetailInfo item = itemList.get(i);
                boolean isAvailable = false;
                for (int j = 0; j < availableCameras.size(); j++) {
                    InterfaceVideo.pbui_Item_MeetVideoDetailInfo available = availableCameras.get(j);
                    if (item.getDeviceid() == available.getDeviceid() && item.getSubid() == available.getSubid()) {
                        isAvailable = true;
                        break;
                    }
                }
                if (!isAvailable) {
                    optionalCameras.add(item);
                }
            }
        }
        mView.updateAvailableCameraList();
        mView.updateOptionalCameraList();
    }
}
