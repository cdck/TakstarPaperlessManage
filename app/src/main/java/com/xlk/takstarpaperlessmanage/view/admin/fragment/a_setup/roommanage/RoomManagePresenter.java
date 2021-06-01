package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.roommanage;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.App;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/12.
 * @desc
 */
public class RoomManagePresenter extends BasePresenter<RoomManageContract.View> implements RoomManageContract.Presenter {

    private final Context context;
    public List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> meetRooms = new ArrayList<>();
    public List<InterfaceDevice.pbui_Item_DeviceDetailInfo> roomDevices = new ArrayList<>();
    public List<InterfaceDevice.pbui_Item_DeviceDetailInfo> otherDevices = new ArrayList<>();
    /**
     * 所有会议室的设备id
     */
    private HashMap<Integer, List<Integer>> roomDevIds = new HashMap<>();
    /**
     * 当前选中的会议室ID
     */
    private int currentRoomId = 0;
    private List<Integer> removeIds = new ArrayList<>();
    private List<Integer> addIds = new ArrayList<>();

    public RoomManagePresenter(Context context, RoomManageContract.View view) {
        super(view);
        this.context = context;
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会场信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    LogUtils.i(TAG, "BusEvent 会场信息变更通知");
                    queryRoom();
                }
                break;
            }
            //会场设备信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    byte[] bytes = (byte[]) msg.getObjects()[0];
                    InterfaceBase.pbui_MeetNotifyMsgForDouble pbui_meetNotifyMsgForDouble = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(bytes);
                    int id = pbui_meetNotifyMsgForDouble.getId();
                    int opermethod = pbui_meetNotifyMsgForDouble.getOpermethod();
                    int subid = pbui_meetNotifyMsgForDouble.getSubid();
                    LogUtils.i(TAG, "BusEvent 会场设备信息变更通知: opermethod=" + opermethod + ",id=" + id + ",subId=" + subid);
                    queryDeviceByRoomId(id);
                }
                break;
            }
            //设备寄存器变更通知
//            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE: {
//                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
//                    if (currentRoomId != 0) {
//                        for (int i = 0; i < meetRooms.size(); i++) {
//                            if (meetRooms.get(i).getRoomid() == currentRoomId) {
//                                queryDevice(currentRoomId);
//                                break;
//                            }
//                        }
//                    }
//                }
//                break;
//            }
        }
    }

    @Override
    public void queryRoom() {
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo info = jni.queryMeetingRooms();
        meetRooms.clear();
        if (info != null) {
            for (int i = 0; i < info.getItemList().size(); i++) {
                InterfaceRoom.pbui_Item_MeetRoomDetailInfo room = info.getItemList().get(i);
                LogUtils.i(TAG, "queryRoom 会场id=" + room.getRoomid() + ",会场名称=" + room.getName().toStringUtf8());
                if (!room.getName().toStringUtf8().isEmpty()) {
                    meetRooms.add(room);
                }
            }
        }
        mView.updateMeetRoomList();
        for (InterfaceRoom.pbui_Item_MeetRoomDetailInfo item : meetRooms) {
            queryDeviceByRoomId(item.getRoomid());
        }
    }

    void queryDeviceByRoomId(int roomId) {
        InterfaceRoom.pbui_Type_MeetRoomDevSeatDetailInfo info = jni.placeDeviceRankingInfo(roomId);
        List<Integer> tempRoomDevIds = new ArrayList<>();
        roomDevIds.put(roomId, tempRoomDevIds);
        if (info != null) {
            List<InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo> itemList = info.getItemList();
            for (InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo item : itemList) {
                tempRoomDevIds.add(item.getDevid());
            }
            roomDevIds.put(roomId, tempRoomDevIds);
        }
        //当前选中的会议室有变更的时候进行更新
//        if (currentRoomId != 0 && currentRoomId == roomId) {
//            queryDevice(roomId);
//        }
    }

    public void setCurrentRoomId(int roomId) {
        currentRoomId = roomId;
        //清空添加和移除列表
        addIds.clear();
        removeIds.clear();
    }

    @Override
    public void queryDevice(int roomId) {
        LogUtils.i(TAG, "queryAllDevice roomId=" + roomId);
        InterfaceDevice.pbui_Type_DeviceDetailInfo info = jni.queryDeviceInfo();
        roomDevices.clear();
        otherDevices.clear();
        //当前会议室中的设备id
        List<Integer> currentRoomIds = roomDevIds.get(roomId);
        if (currentRoomIds != null) {
            //存放已经存在会议室中的设备id
            List<Integer> judgeIds = new ArrayList<>();
            for (List<Integer> next : roomDevIds.values()) {
                judgeIds.addAll(next);
            }
            if (info != null) {
                List<InterfaceDevice.pbui_Item_DeviceDetailInfo> pdevList = info.getPdevList();
                for (InterfaceDevice.pbui_Item_DeviceDetailInfo item : pdevList) {
                    int devcieid = item.getDevcieid();
                    //当前设备存在于当前会议室中
                    if (currentRoomIds.contains(devcieid)) {
                        roomDevices.add(item);
                        //当前设备不存在于任何会议室中
                    } else if (!judgeIds.contains(devcieid)) {
                        //过滤掉未识别的设备（服务器）
                        if (!Constant.getDeviceTypeName(context, devcieid).isEmpty()
                                //过滤掉会议数据库设备
                                && !Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetDBServer_VALUE, devcieid)
                                //过滤掉茶水设备
                                && !Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetService_VALUE, devcieid)
                                //过滤会议流采集设备
                                && !Constant.isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetCapture_VALUE, devcieid)
                        ) {
                            otherDevices.add(item);
                        } else {
                            LogUtils.e("queryAllDevice 过滤掉的设备：" + item.getDevcieid()
                                    + "，设备类型：" + Constant.getDeviceTypeName(App.appContext, item.getDevcieid())
                                    + ",name=" + item.getDevname().toStringUtf8());
                        }
                    }
                }
            }
        }
        mView.updateRoomDeviceRv();
        mView.updateOtherDeviceRv();
    }

    @Override
    public void addDevice2Room(List<Integer> deviceIds) {
        List<InterfaceDevice.pbui_Item_DeviceDetailInfo> temps = new ArrayList<>();
        Iterator<InterfaceDevice.pbui_Item_DeviceDetailInfo> iterator = otherDevices.iterator();
        while (iterator.hasNext()) {
            InterfaceDevice.pbui_Item_DeviceDetailInfo dev = iterator.next();
            int devcieid = dev.getDevcieid();
            if (deviceIds.contains(devcieid)) {
                LogUtils.e("移除列表=" + removeIds.toString());
                if (removeIds.contains(devcieid)) {
                    Integer remove = removeIds.remove(removeIds.indexOf(devcieid));
                    LogUtils.e("从移除列表中删除id=" + devcieid + ",remove=" + remove + ",removeIds.size=" + removeIds.size());
                } else {
                    addIds.add(devcieid);
                }
                temps.add(dev);
                iterator.remove();
            }
        }
        roomDevices.addAll(temps);
        mView.updateRoomDeviceRv();
        mView.updateOtherDeviceRv();
//        jni.addDevice2Room(currentRoomId, deviceIds);
    }

    @Override
    public void removeDeviceFromRoom(List<Integer> deviceIds) {
        List<InterfaceDevice.pbui_Item_DeviceDetailInfo> temps = new ArrayList<>();
        Iterator<InterfaceDevice.pbui_Item_DeviceDetailInfo> iterator = roomDevices.iterator();
        while (iterator.hasNext()) {
            InterfaceDevice.pbui_Item_DeviceDetailInfo dev = iterator.next();
            int devcieid = dev.getDevcieid();
            if (deviceIds.contains(devcieid)) {
                LogUtils.i("添加列表=" + addIds.toString());
                if (addIds.contains(devcieid)) {
                    LogUtils.i("从添加列表中删除id=" + devcieid);
                    addIds.remove(addIds.indexOf(devcieid));
                } else {
                    removeIds.add(devcieid);
                }
                temps.add(dev);
                iterator.remove();
            }
        }
        otherDevices.addAll(temps);
        mView.updateRoomDeviceRv();
        mView.updateOtherDeviceRv();
//        jni.removeDeviceFromRoom(currentRoomId, deviceIds);
    }

    @Override
    public void defineModify() {
        LogUtils.e("会议室设备修改 addIds=" + addIds.toString() + ",removeIds=" + removeIds.toString());
        boolean successful = false;
        if (!addIds.isEmpty()) {
            jni.addDevice2Room(currentRoomId, addIds);
            successful = true;
            addIds.clear();
        }
        if (!removeIds.isEmpty()) {
            jni.removeDeviceFromRoom(currentRoomId, removeIds);
            successful = true;
            removeIds.clear();
        }
        ToastUtil.showShort(successful ? R.string.modified_successfully : R.string.no_changes);
    }
}
