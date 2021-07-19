package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.devicemanage;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/6.
 * @desc
 */
public class DeviceManagePresenter extends BasePresenter<DeviceManageContract.View> implements DeviceManageContract.Presenter {
    public List<InterfaceDevice.pbui_Item_DeviceDetailInfo> deviceInfos = new ArrayList<>();
    public List<InterfaceDevice.pbui_Item_DeviceDetailInfo> clientDevices = new ArrayList<>();

    public DeviceManagePresenter(DeviceManageContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            case EventType.RESULT_DIR_PATH: {
                int dirType = (int) msg.getObjects()[0];
                String dirPath = (String) msg.getObjects()[1];
                if (dirType == Constant.CHOOSE_DIR_TYPE_CACHE) {
                    mView.updateExportDirPath(dirPath);
                }
                break;
            }
            //会场设备信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE_VALUE:
                //设备寄存器变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE:
                //界面状态变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS_VALUE: {
                queryDevice();
                break;
            }
        }
    }

    @Override
    public void queryDevice() {
        InterfaceDevice.pbui_Type_DeviceDetailInfo info = jni.queryDeviceInfo();
        deviceInfos.clear();
        clientDevices.clear();
        if (info != null) {
            List<InterfaceDevice.pbui_Item_DeviceDetailInfo> pdevList = info.getPdevList();
            deviceInfos.addAll(pdevList);
            for (int i = 0; i < pdevList.size(); i++) {
                InterfaceDevice.pbui_Item_DeviceDetailInfo dev = pdevList.get(i);
                int devcieid = dev.getDevcieid();
                int facestate = dev.getFacestate();
                int netstate = dev.getNetstate();
                int deviceflag = dev.getDeviceflag();
                if (Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetClient_VALUE, devcieid)
                        && netstate == 1) {
                    clientDevices.add(dev);
                }
            }
        }
        mView.updateDeviceList();
        mView.updateClientList();
    }
}
