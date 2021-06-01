package com.xlk.takstarpaperlessmanage.model.bean;

import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMember;

/**
 * @author Created by xlk on 2021/5/25.
 * @desc
 */
public class ChatDeviceMember {
    InterfaceDevice.pbui_Item_DeviceDetailInfo deviceDetailInfo;
    InterfaceMember.pbui_Item_MemberDetailInfo memberDetailInfo;
    /**
     * 未读消息数量
     */
    int count;
    /**
     * 最后一次查看该参会人发送的消息，用于记录未读消息
     */
    long lastCheckTime;

    public ChatDeviceMember(InterfaceDevice.pbui_Item_DeviceDetailInfo deviceDetailInfo, InterfaceMember.pbui_Item_MemberDetailInfo memberDetailInfo) {
        this.deviceDetailInfo = deviceDetailInfo;
        this.memberDetailInfo = memberDetailInfo;
    }

    public InterfaceDevice.pbui_Item_DeviceDetailInfo getDeviceDetailInfo() {
        return deviceDetailInfo;
    }

    public void setDeviceDetailInfo(InterfaceDevice.pbui_Item_DeviceDetailInfo deviceDetailInfo) {
        this.deviceDetailInfo = deviceDetailInfo;
    }

    public InterfaceMember.pbui_Item_MemberDetailInfo getMemberDetailInfo() {
        return memberDetailInfo;
    }

    public void setMemberDetailInfo(InterfaceMember.pbui_Item_MemberDetailInfo memberDetailInfo) {
        this.memberDetailInfo = memberDetailInfo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(long lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }
}
