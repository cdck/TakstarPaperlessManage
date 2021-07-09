package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.secretary;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceAdmin;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/13.
 * @desc
 */
public class SecretaryManagePresenter extends BasePresenter<SecretaryManageContract.View> implements SecretaryManageContract.Presenter {
    public List<InterfaceAdmin.pbui_Item_AdminDetailInfo> adminInfos = new ArrayList<>();
    public HashMap<Integer, List<Integer>> allAdminControllableRooms = new HashMap<>();//管理员可控的会场
    public List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> controlledRooms = new ArrayList<>();//可控会场
    public List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> optionalRooms = new ArrayList<>();//剩下的会场

    public List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> preControlledRooms = new ArrayList<>();
    public List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> preOptionalRooms = new ArrayList<>();
    private int currentAdminId;
    private List<Integer> removeIds = new ArrayList<>();
    private List<Integer> addIds = new ArrayList<>();

    public SecretaryManagePresenter(SecretaryManageContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //管理员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    byte[] bytes = (byte[]) msg.getObjects()[0];
                    InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(bytes);
                    int opermethod = pbui_meetNotifyMsg.getOpermethod();
                    int id = pbui_meetNotifyMsg.getId();
                    LogUtils.i("BusEvent 管理员变更通知 id=" + id + ", opermethod=" + opermethod);
                    queryAdmin();
                }
                break;
            }
            //会议管理员控制的会场变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MANAGEROOM_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    byte[] bytes = (byte[]) msg.getObjects()[0];
                    InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(bytes);
                    int opermethod = pbui_meetNotifyMsg.getOpermethod();
                    int id = pbui_meetNotifyMsg.getId();
                    LogUtils.i("BusEvent 会议管理员控制的会场变更通知 id=" + id + ", opermethod=" + opermethod);
                    queryAdmin();
                }
                break;
            }
            //会场信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    byte[] bytes = (byte[]) msg.getObjects()[0];
                    InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(bytes);
                    int opermethod = pbui_meetNotifyMsg.getOpermethod();
                    int id = pbui_meetNotifyMsg.getId();
                    LogUtils.i("BusEvent 会场信息变更通知 id=" + id + ", opermethod=" + opermethod);
                    queryAdmin();
                }
                break;
            }
        }
    }

    @Override
    public void queryAdmin() {
        InterfaceAdmin.pbui_TypeAdminDetailInfo pbui_typeAdminDetailInfo = jni.queryAdmin();
        adminInfos.clear();
        if (pbui_typeAdminDetailInfo != null) {
            adminInfos.addAll(pbui_typeAdminDetailInfo.getItemList());
        }
        mView.updateAdminList();
        for (int i = 0; i < adminInfos.size(); i++) {
            InterfaceAdmin.pbui_Item_AdminDetailInfo info = adminInfos.get(i);
            LogUtils.i("queryAdmin 查询到的用户：adminId=" + info.getAdminid() + ", adminName=" + info.getAdminname().toStringUtf8() + ",adminPwd=" + info.getPw().toStringUtf8());
            queryControllableRooms(info.getAdminid());
        }
    }

    /**
     * 查询管理员可控的会场
     *
     * @param adminId 管理员id
     */
    public void queryControllableRooms(int adminId) {
        InterfaceAdmin.pbui_Type_MeetManagerRoomDetailInfo info = jni.queryAdminRoom(adminId);
        List<Integer> temps = new ArrayList<>();
        if (info != null) {
            temps.addAll(info.getRoomidList());
        }
        LogUtils.i("queryControllableRooms adminId=" + adminId + ", 可控的会场id=" + temps);
        allAdminControllableRooms.put(adminId, temps);
    }

    @Override
    public void setCurrentAdminId(int adminid) {
        currentAdminId = adminid;
        //清空添加和移除列表
        addIds.clear();
        removeIds.clear();
    }

    @Override
    public void queryRoomsByAdminId(int adminid) {
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo info = jni.queryMeetingRooms();
        controlledRooms.clear();
        optionalRooms.clear();
        //当前管理员所有可控会场id
        List<Integer> currentControllableRooms = allAdminControllableRooms.get(adminid);
        if (info != null) {
            List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> itemList = info.getItemList();
            if (adminid == 1) {
                //root用户默认可控全部会场
                controlledRooms.addAll(itemList);
            } else {
                for (InterfaceRoom.pbui_Item_MeetRoomDetailInfo item : itemList) {
                    LogUtils.i("queryAllRooms 会场id=" + item.getRoomid() + ",会场名称=" + item.getName().toStringUtf8());
                    if (currentControllableRooms != null && currentControllableRooms.contains(item.getRoomid())) {
                        //当前会场在当前的管理员控制下
                        controlledRooms.add(item);
                    } else {
                        //当前会场不在当前管理员的控制下
                        optionalRooms.add(item);
                    }
                }
            }
        }
        mView.updateControlledRoomList();
        mView.updateOptionalRoomList();
    }

    @Override
    public void repeal() {
        controlledRooms.clear();
        controlledRooms.addAll(preControlledRooms);
        optionalRooms.clear();
        optionalRooms.addAll(preOptionalRooms);
        mView.updateOptionalRoomList();
        mView.updateControlledRoomList();
    }

    @Override
    public void savePreviousStep() {
        preControlledRooms.clear();
        preControlledRooms.addAll(controlledRooms);
        preOptionalRooms.clear();
        preOptionalRooms.addAll(optionalRooms);
    }

    @Override
    public void addRoom2Admin(List<Integer> roomIds) {
        List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> temps = new ArrayList<>();
        Iterator<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> iterator = optionalRooms.iterator();
        while (iterator.hasNext()) {
            InterfaceRoom.pbui_Item_MeetRoomDetailInfo next = iterator.next();
            int roomid = next.getRoomid();
            if (roomIds.contains(roomid)) {
                if (removeIds.contains(roomid)) {
                    removeIds.remove(removeIds.indexOf(roomid));
                } else {
                    addIds.add(roomid);
                }
                temps.add(next);
                iterator.remove();
            }
        }
        controlledRooms.addAll(temps);
        mView.updateControlledRoomList();
        mView.updateOptionalRoomList();
    }

    @Override
    public void removeRoomFromAdmin(List<Integer> roomIds) {
        if (currentAdminId == 1) {
            ToastUtil.showShort(R.string.unable_to_modify_root_account);
            return;
        }
        List<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> temps = new ArrayList<>();
        Iterator<InterfaceRoom.pbui_Item_MeetRoomDetailInfo> iterator = controlledRooms.iterator();
        while (iterator.hasNext()) {
            InterfaceRoom.pbui_Item_MeetRoomDetailInfo next = iterator.next();
            int roomid = next.getRoomid();
            if (roomIds.contains(roomid)) {
                if (addIds.contains(roomid)) {
                    addIds.remove(addIds.indexOf(roomid));
                } else {
                    removeIds.add(roomid);
                }
                temps.add(next);
                iterator.remove();
            }
        }
        optionalRooms.addAll(temps);
        mView.updateControlledRoomList();
        mView.updateOptionalRoomList();
    }

    @Override
    public void defineModify() {
        LogUtils.e("管理员会场修改 addIds=" + addIds.toString() + ",removeIds=" + removeIds.toString());
        boolean successful = false;
        if (!addIds.isEmpty() || !removeIds.isEmpty()) {
            successful = true;
            addIds.clear();
            removeIds.clear();
            List<Integer> temps = new ArrayList<>();
            for (int i = 0; i < controlledRooms.size(); i++) {
                temps.add(controlledRooms.get(i).getRoomid());
            }
            jni.saveAdminRoom(currentAdminId, temps);
        }
        ToastUtil.showShort(successful ? R.string.modified_successfully : R.string.no_changes);
    }
}
