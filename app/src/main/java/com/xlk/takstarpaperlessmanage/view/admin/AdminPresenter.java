package com.xlk.takstarpaperlessmanage.view.admin;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;
import com.xlk.takstarpaperlessmanage.model.bean.ChatMessage;
import com.xlk.takstarpaperlessmanage.util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.shinichi.library.ImagePreview;

import static com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.chat.ChatFragment.isOnChatPage;

/**
 * @author Created by xlk on 2021/4/21.
 * @desc
 */
public class AdminPresenter extends BasePresenter<AdminContract.View> implements AdminContract.Presenter {

    private final Context context;
    List<String> picPath = new ArrayList<>();
    public static HashMap<Integer, List<ChatMessage>> AllChatMessages = new HashMap<>();

    public AdminPresenter(AdminContract.View view, Context context) {
        super(view);
        this.context = context;
        //强制缓存会议信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE);
        // 缓存会议排位
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT_VALUE);
        //缓存会议桌牌
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTABLECARD_VALUE);
        // 缓存会议室
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE);
        // 缓存会场设备
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE_VALUE);
        //缓存会议目录
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY_VALUE);
        //会议目录文件
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE);
        //缓存会议评分
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCOREVOTESIGN_VALUE);
        // 缓存参会人信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE);
        //缓存参会人权限
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERPERMISSION_VALUE);
        //缓存投票信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO_VALUE);
        //人员签到
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN_VALUE);
        //公告信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET_VALUE);
        //会议视频
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO_VALUE);
        //会议管理员
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN_VALUE);
        //会议管理员控制的会场
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MANAGEROOM_VALUE);
        //缓存会议目录权限
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYRIGHT_VALUE);
        //缓存任务
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTASK_VALUE);
        //缓存界面配置信息
        jni.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETFACECONFIG_VALUE);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会议信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO_VALUE:
                //会议排位变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT_VALUE:
                //会场信息变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM_VALUE: {
                GlobalValue.currentMeetingId = getCurrentMeetingId();
                LogUtils.i(TAG, "BusEvent 当前的会议ID=" + GlobalValue.currentMeetingId);
                break;
            }
            //时间回调
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_TIME_VALUE: {
                Object[] objs = msg.getObjects();
                byte[] data = (byte[]) objs[0];
                InterfaceBase.pbui_Time pbui_time = InterfaceBase.pbui_Time.parseFrom(data);
                //微秒 转换成毫秒 除以 1000
                String[] adminTime = DateUtil.convertAdminTime(pbui_time.getUsec() / 1000);
                mView.updateTime(adminTime);
                break;
            }
            //设备寄存器变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO_VALUE: {
                LogUtils.i(TAG, "BusEvent 设备寄存器变更通知");
                mView.updateOnlineStatus();
                break;
            }
            //会议交流信息
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETIM_VALUE: {
                if (!isOnChatPage) {
                    byte[] o = (byte[]) msg.getObjects()[0];
                    InterfaceIM.pbui_Type_MeetIM meetIM = InterfaceIM.pbui_Type_MeetIM.parseFrom(o);
                    LogUtils.i(TAG, "busEvent 收到会议交流信息 参会人id=" + meetIM.getMemberid() + ",名称=" + meetIM.getMembername().toStringUtf8() + ",内容=" + meetIM.getMsg().toStringUtf8());
                    //文本类消息
                    if (meetIM.getMsgtype() == InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Message_VALUE) {
                        int memberid = meetIM.getMemberid();
                        ChatMessage newImMsg = new ChatMessage(0, meetIM.getMembername().toStringUtf8(), memberid, meetIM.getUtcsecond(), meetIM.getMsg().toStringUtf8());
                        List<ChatMessage> myChatMessages;
                        if (AllChatMessages.containsKey(memberid)) {
                            myChatMessages = AllChatMessages.get(memberid);
                            LogUtils.i(TAG, "busEvent 获取消息数据 " + myChatMessages.size());
                        } else {
                            myChatMessages = new ArrayList<>();
                            LogUtils.i(TAG, "busEvent 新建消息数据");
                        }
                        myChatMessages.add(newImMsg);
                        AllChatMessages.put(memberid, myChatMessages);
                    }
                }
                break;
            }
            //打开下载完成的图片
            case EventType.BUS_PREVIEW_IMAGE: {
                String filepath = (String) msg.getObjects()[0];
                LogUtils.i(TAG, "BusEvent 将要打开的图片路径：" + filepath);
                int index = 0;
                if (!picPath.contains(filepath)) {
                    picPath.add(filepath);
                    index = picPath.size() - 1;
                } else {
                    for (int i = 0; i < picPath.size(); i++) {
                        if (picPath.get(i).equals(filepath)) {
                            index = i;
                        }
                    }
                }
                previewImage(index);
                break;
            }
        }
    }

    private void previewImage(int index) {
        if (picPath.isEmpty()) {
            return;
        }
        ImagePreview.getInstance()
                .setContext(context)
                //设置图片地址集合
                .setImageList(picPath)
                //设置开始的索引
                .setIndex(index)
                //设置是否显示下载按钮
                .setShowDownButton(false)
                //设置是否显示关闭按钮
                .setShowCloseButton(false)
                //设置是否开启下拉图片退出
                .setEnableDragClose(true)
                //设置是否开启上拉图片退出
                .setEnableUpDragClose(true)
                //设置是否开启点击图片退出
                .setEnableClickClose(true)
                .setShowErrorToast(true)
                .start();
    }

}
