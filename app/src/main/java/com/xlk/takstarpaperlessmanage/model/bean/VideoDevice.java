package com.xlk.takstarpaperlessmanage.model.bean;

import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceVideo;

/**
 * @author Created by xlk on 2021/5/25.
 * @desc
 */
public class VideoDevice {
    InterfaceVideo.pbui_Item_MeetVideoDetailInfo videoDetailInfo;
    InterfaceDevice.pbui_Item_DeviceDetailInfo deviceDetailInfo;

    public VideoDevice(InterfaceVideo.pbui_Item_MeetVideoDetailInfo videoDetailInfo, InterfaceDevice.pbui_Item_DeviceDetailInfo deviceDetailInfo) {
        this.videoDetailInfo = videoDetailInfo;
        this.deviceDetailInfo = deviceDetailInfo;
    }

    public InterfaceVideo.pbui_Item_MeetVideoDetailInfo getVideoDetailInfo() {
        return videoDetailInfo;
    }

    public void setVideoDetailInfo(InterfaceVideo.pbui_Item_MeetVideoDetailInfo videoDetailInfo) {
        this.videoDetailInfo = videoDetailInfo;
    }

    public InterfaceDevice.pbui_Item_DeviceDetailInfo getDeviceDetailInfo() {
        return deviceDetailInfo;
    }

    public void setDeviceDetailInfo(InterfaceDevice.pbui_Item_DeviceDetailInfo deviceDetailInfo) {
        this.deviceDetailInfo = deviceDetailInfo;
    }

    public String getName() {
        return videoDetailInfo.getDevicename().toStringUtf8() + "-" + videoDetailInfo.getName().toStringUtf8();
    }
}
