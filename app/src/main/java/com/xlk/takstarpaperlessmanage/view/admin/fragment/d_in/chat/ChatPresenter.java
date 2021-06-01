package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.chat;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceStop;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;
import com.xlk.takstarpaperlessmanage.model.bean.ChatDeviceMember;
import com.xlk.takstarpaperlessmanage.model.bean.ChatMessage;
import com.xlk.takstarpaperlessmanage.util.LogUtil;
import com.xlk.takstarpaperlessmanage.view.admin.AdminPresenter;

import java.util.ArrayList;
import java.util.List;

import static com.xlk.takstarpaperlessmanage.model.Constant.RESOURCE_ID_10;
import static com.xlk.takstarpaperlessmanage.model.Constant.RESOURCE_ID_11;
import static com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.chat.ChatFragment.isOnChatPage;

/**
 * @author Created by xlk on 2021/5/25.
 * @desc
 */
class ChatPresenter extends BasePresenter<ChatContract.View> implements ChatContract.Presenter {

    private List<InterfaceMember.pbui_Item_MemberDetailInfo> members = new ArrayList<>();
    public List<ChatDeviceMember> deviceMembers = new ArrayList<>();
    private int currentMemberId;
    List<ChatMessage> currentMessages = new ArrayList<>();
    private int mOperdeviceid;

    public ChatPresenter(ChatContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会议交流
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETIM_VALUE: {
                if (isOnChatPage) {
                    byte[] o = (byte[]) msg.getObjects()[0];
                    InterfaceIM.pbui_Type_MeetIM meetIM = InterfaceIM.pbui_Type_MeetIM.parseFrom(o);
                    LogUtils.i(TAG, "busEvent 收到会议交流信息=" + meetIM.getMembername().toStringUtf8() + "," + meetIM.getMsg().toStringUtf8());
                    //文本类消息
                    if (meetIM.getMsgtype() == InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Message_VALUE) {
                        int memberid = meetIM.getMemberid();
                        ChatMessage newImMsg = new ChatMessage(0, meetIM.getMembername().toStringUtf8(), memberid, meetIM.getUtcsecond(), meetIM.getMsg().toStringUtf8());
                        addImMessage(memberid, newImMsg);
                        if (memberid != currentMemberId) {
                            updateMember(memberid, meetIM.getUtcsecond());
                        }
                    }
                }
                break;
            }
            //设备会议信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEFACESHOW_VALUE: {
                LogUtils.i(TAG, "BusEvent -->" + "设备会议信息变更通知");
                queryDeviceMeet();
                break;
            }
            //参会人变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE:
                //界面状态变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS_VALUE:
                //设备寄存器变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE: {
                queryMember();
                break;
            }
            //设备交互信息
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REQUESTINVITE_VALUE) {
                    byte[] o2 = (byte[]) msg.getObjects()[0];
                    InterfaceDevice.pbui_Type_DeviceChat info = InterfaceDevice.pbui_Type_DeviceChat.parseFrom(o2);
                    int inviteflag = info.getInviteflag();
                    int operdeviceid = info.getOperdeviceid();
                    LogUtil.i(TAG, "BusEvent -->" + "收到设备对讲的通知 inviteflag = " + inviteflag + ", operdeviceid= " + operdeviceid);

                } else if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REQUESTPRIVELIGE_VALUE) {
                    LogUtil.i(TAG, "BusEvent -->" + "收到参会人权限请求");
//                    byte[] o2 = (byte[]) msg.getObjects()[0];
//                    InterfaceDevice.pbui_Type_MeetRequestPrivilegeNotify info = InterfaceDevice.pbui_Type_MeetRequestPrivilegeNotify.parseFrom(o2);
//                    mView.applyPermissionsInform(info);
                }
                break;
            }
            //后台播放数据 DECODE
            case EventType.BUS_VIDEO_DECODE: {
                Object[] objs = msg.getObjects();
                int obj = (int) objs[1];
                LogUtil.v(TAG, "BusEvent 收到数据 --> resid = " + obj);
                mView.setVideoDecode(objs);
                break;
            }
            //后台播放数据 YUV
            case EventType.BUS_YUV_DISPLAY: {
                Object[] objs1 = msg.getObjects();
                int o3 = (int) objs1[0];
                LogUtil.v(TAG, "BusEvent 收到数据 --> resid = " + o3);
                mView.setYuv(objs1);
                break;
            }
            //停止资源通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STOPPLAY_VALUE: {
                byte[] o2 = (byte[]) msg.getObjects()[0];
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CLOSE_VALUE) {
                    //停止资源通知
                    InterfaceStop.pbui_Type_MeetStopResWork stopResWork = InterfaceStop.pbui_Type_MeetStopResWork.parseFrom(o2);
                    List<Integer> resList = stopResWork.getResList();
                    for (int resid : resList) {
                        LogUtil.i(TAG, "BusEvent -->" + "停止资源通知 resid: " + resid);
                        if ((resid == RESOURCE_ID_10 || resid == RESOURCE_ID_11) && mOperdeviceid == GlobalValue.localDeviceId) {
                            LogUtil.i(TAG, "BusEvent -->" + "工作状态下，自己是发起端，且自己的播放资源停止了");
//                            if (work_state != 0) {
                            LogUtil.i(TAG, "BusEvent -->" + "停止设备对讲");
                            jni.stopDeviceIntercom(mOperdeviceid);
//                            }
                        }
                    }
                } else if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    //停止播放通知
                    InterfaceStop.pbui_Type_MeetStopPlay stopPlay = InterfaceStop.pbui_Type_MeetStopPlay.parseFrom(o2);
                    int resid = stopPlay.getRes();
                    int createdeviceid = stopPlay.getCreatedeviceid();
                    LogUtil.i(TAG, "BusEvent -->" + "停止播放通知 resid= " + resid + ", createdeviceid= " + createdeviceid);
                    if ((resid == RESOURCE_ID_10 || resid == RESOURCE_ID_11) && mOperdeviceid == GlobalValue.localDeviceId) {
                        LogUtil.i(TAG, "BusEvent -->" + "工作状态下，自己是发起端，且自己的播放资源停止了");
//                        if (work_state != 0) {
                        LogUtil.i(TAG, "BusEvent -->" + "停止设备对讲");
                        jni.stopDeviceIntercom(mOperdeviceid);
//                        }
                    }
                }
                break;
            }
        }
    }

    @Override
    public void setOperDeviceId(int localDeviceId) {
        mOperdeviceid = localDeviceId;
    }

    public void updateMember(int memberid, long utcsecond) {
        for (int i = 0; i < deviceMembers.size(); i++) {
            ChatDeviceMember item = deviceMembers.get(i);
            if (item.getMemberDetailInfo().getPersonid() == memberid) {
                long lastCheckTime = item.getLastCheckTime();
                if (utcsecond > lastCheckTime) {
                    if (memberid != currentMemberId) {
                        item.setCount(item.getCount() + 1);
                    } else {
                        item.setCount(0);
                    }
                }
            }
        }
        mView.updateMemberList();
    }

    public void addImMessage(int memberid, ChatMessage newImMsg) {
        LogUtils.i(TAG, "addImMessage memberId=" + memberid);
        List<ChatMessage> myChatMessages;
        if (AdminPresenter.AllChatMessages.containsKey(memberid)) {
            myChatMessages = AdminPresenter.AllChatMessages.get(memberid);
            LogUtils.i(TAG, "addImMessage 获取消息数据 " + myChatMessages.size());
        } else {
            myChatMessages = new ArrayList<>();
            LogUtils.i(TAG, "addImMessage 新建消息数据 " + myChatMessages.size());
        }
        myChatMessages.add(newImMsg);
        AdminPresenter.AllChatMessages.put(memberid, myChatMessages);
        updateChatMessageData();
    }


    @Override
    public void queryDeviceMeet() {
        InterfaceDevice.pbui_Type_DeviceFaceShowDetail info = jni.queryDeviceMeetInfo();
        if (info != null) {
            String meetName = info.getMeetingname().toStringUtf8();
            mView.updateMeetName(meetName);
        }
    }

    @Override
    public void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo info = jni.queryMember();
        members.clear();
        if (info != null) {
            members = info.getItemList();
        }
        queryDevice();
    }

    private void queryDevice() {
        InterfaceDevice.pbui_Type_DeviceDetailInfo info = jni.queryDeviceInfo();
        List<ChatDeviceMember> temps = new ArrayList<>();
        temps.addAll(deviceMembers);
        deviceMembers.clear();
        if (info != null) {
            List<InterfaceDevice.pbui_Item_DeviceDetailInfo> pdevList = info.getPdevList();
            for (int i = 0; i < pdevList.size(); i++) {
                InterfaceDevice.pbui_Item_DeviceDetailInfo dev = pdevList.get(i);
                int devcieid = dev.getDevcieid();
                int memberid = dev.getMemberid();
                int netstate = dev.getNetstate();
                int facestate = dev.getFacestate();
                //设备不在主界面并且是在线的
                if (facestate != 0 && netstate == 1 && devcieid != GlobalValue.localDeviceId) {
                    for (int j = 0; j < members.size(); j++) {
                        InterfaceMember.pbui_Item_MemberDetailInfo member = members.get(j);
                        if (member.getPersonid() == memberid) {
                            deviceMembers.add(new ChatDeviceMember(dev, member));
                        }
                    }
                }
            }
        }
        for (int i = 0; i < temps.size(); i++) {
            ChatDeviceMember temp = temps.get(i);
            for (int j = 0; j < deviceMembers.size(); j++) {
                ChatDeviceMember item = deviceMembers.get(j);
                if (temp.getMemberDetailInfo().getPersonid() == item.getMemberDetailInfo().getPersonid()) {
                    item.setLastCheckTime(temp.getLastCheckTime());
                    break;
                }
            }
        }
        mView.updateMemberList();
    }

    @Override
    public void setCurrentMemberId(int memberId) {
        currentMemberId = memberId;
    }

    @Override
    public void updateChatMessageData() {
        currentMessages.clear();
        if (AdminPresenter.AllChatMessages.containsKey(currentMemberId)) {
            List<ChatMessage> items = AdminPresenter.AllChatMessages.get(currentMemberId);
            currentMessages.addAll(items);
        }
        LogUtils.i(TAG, "updateChatMessageData 消息个数=" + currentMessages.size());
        mView.updateMessageList(currentMessages);
    }

    @Override
    public void refreshUnreadCount() {
        for (int i = 0; i < deviceMembers.size(); i++) {
            ChatDeviceMember item = deviceMembers.get(i);
            long lastCheckTime = item.getLastCheckTime();
            int personid = item.getMemberDetailInfo().getPersonid();
            int newCount = 0;
            if (AdminPresenter.AllChatMessages.containsKey(personid)) {
                List<ChatMessage> chatMessages = AdminPresenter.AllChatMessages.get(personid);
                for (int j = 0; j < chatMessages.size(); j++) {
                    ChatMessage info = chatMessages.get(j);
                    if (info.getUtcSecond() > lastCheckTime) {
                        newCount++;
                    }
                }
            }
            if (currentMemberId == personid) {
                newCount = 0;
            }
            item.setCount(newCount);
        }
        mView.updateMemberList();
    }
}
