package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.screen;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.bean.DeviceMember;
import com.xlk.takstarpaperlessmanage.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/26.
 * @desc
 */
class ScreenManagePresenter extends BasePresenter<ScreenManageContract.View> implements ScreenManageContract.Presenter {

    private List<InterfaceMember.pbui_Item_MemberDetailInfo> members = new ArrayList<>();
    public List<InterfaceDevice.pbui_Item_DeviceDetailInfo> onLineProjectors = new ArrayList<>();
    public List<DeviceMember> sourceMembers = new ArrayList<>();
    public List<DeviceMember> targetMembers = new ArrayList<>();

    public ScreenManagePresenter(ScreenManageContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE://设备寄存器变更通知
                LogUtil.d(TAG, "BusEvent -->" + "设备寄存器变更通知 ");
                queryDevice();
                break;
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE://参会人员变更通知
                LogUtil.d(TAG, "BusEvent -->" + "参会人员变更通知");
                queryMember();
                break;
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS_VALUE://界面状态变更通知
                LogUtil.d(TAG, "BusEvent -->" + "界面状态变更通知");
                queryDevice();
                break;
        }
    }

    @Override
    public void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo info = jni.queryMember();
        members.clear();
        if (info != null) {
            members.addAll(info.getItemList());
        }
        queryDevice();
    }

    private void queryDevice() {
        InterfaceDevice.pbui_Type_DeviceDetailInfo info = jni.queryDeviceInfo();
        onLineProjectors.clear();
        targetMembers.clear();
        sourceMembers.clear();
        if (info != null) {
            List<InterfaceDevice.pbui_Item_DeviceDetailInfo> pdevList = info.getPdevList();
            for (int i = 0; i < pdevList.size(); i++) {
                InterfaceDevice.pbui_Item_DeviceDetailInfo dev = pdevList.get(i);
                int netstate = dev.getNetstate();
                int devcieid = dev.getDevcieid();
                int facestate = dev.getFacestate();
                if (netstate != 1) continue;
                if (Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetProjective_VALUE, devcieid)) {
                    onLineProjectors.add(dev);
                } else {
                    for (int j = 0; j < members.size(); j++) {
                        InterfaceMember.pbui_Item_MemberDetailInfo member = members.get(j);
                        if (member.getPersonid() == dev.getMemberid()) {
                            DeviceMember devMember = new DeviceMember(dev, member);
                            targetMembers.add(devMember);
                            sourceMembers.add(devMember);
                            break;
                        }
                    }
                }
            }
        }
        mView.updateList();
    }
}
