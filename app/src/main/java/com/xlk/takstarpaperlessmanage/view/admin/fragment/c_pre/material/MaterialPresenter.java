package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.material;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.bean.MemberDirPermissionBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/20.
 * @desc
 */
public class MaterialPresenter extends BasePresenter<MaterialContract.View> implements MaterialContract.Presenter {
    public List<InterfaceFile.pbui_Item_MeetDirDetailInfo> dirInfos = new ArrayList<>();
    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> dirFiles = new ArrayList<>();
    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> historyDirFiles = new ArrayList<>();
    public List<InterfaceFile.pbui_Item_MeetDirFileDetailInfo> sortFiles = new ArrayList<>();
    private List<MemberDirPermissionBean> memberDirPermissionBeans = new ArrayList<>();

    private int currentDirId;
    private int currentMeetingId;
    /**
     * 所有的会议
     */
    public List<InterfaceMeet.pbui_Item_MeetMeetInfo> meetings = new ArrayList<>();
    private int currentHistoryDirId;


    public MaterialPresenter(MaterialContract.View view) {
        super(view);
    }

    @Override
    public void initial() {
        queryMember();
        queryMeetDir();
        currentMeetingId = getCurrentMeetingId();
        queryAllMeeting();
    }

    @Override
    public void setCurrentHistoryDirId(int dirId) {
        currentHistoryDirId = dirId;
        LogUtils.i(TAG, "currentHistoryDirId=" + dirId);
    }

    @Override
    public void switchMeeting(int meetingId) {
        jni.modifyContextProperties(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_CURMEETINGID_VALUE, meetingId);
        queryMeetDir();
    }

    @Override
    public void exit() {
        switchMeeting(currentMeetingId);
    }

    private void queryAllMeeting() {
        InterfaceMeet.pbui_Type_MeetMeetInfo info = jni.queryMeeting();
        meetings.clear();
        if (info != null) {
            List<InterfaceMeet.pbui_Item_MeetMeetInfo> itemList = info.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceMeet.pbui_Item_MeetMeetInfo item = itemList.get(i);
                if (item.getId() != currentMeetingId) {
                    meetings.add(item);
                }
            }
        }
        mView.updateMeetingList();
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会议目录变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY_VALUE: {
                LogUtils.i(TAG, "busEvent 会议目录变更通知");
                queryMeetDir();
                break;
            }
            //会议目录文件变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsgForDouble info = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(bytes);
                int opermethod = info.getOpermethod();
                int id = info.getId();
                int subid = info.getSubid();
                LogUtils.i(TAG, "busEvent 会议目录文件变更通知 id=" + id + ",subid=" + subid + ",opermethod=" + opermethod);
                if (id != 0) {
                    if (currentDirId == id || currentHistoryDirId == id) {
                        queryMeetDirFile(id);
                    }
                }
                break;
            }
            //会议目录权限变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYRIGHT_VALUE: {
                byte[] bytes = (byte[]) msg.getObjects()[0];
                InterfaceBase.pbui_MeetNotifyMsg info = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(bytes);
                int id = info.getId();
                int opermethod = info.getOpermethod();
                LogUtils.i(TAG, "busEvent 会议目录权限变更通知 id=" + id + ",opermethod=" + opermethod);
                if (id != 0 && id == currentDirId) {
                    queryMeetDirPermission(id);
                }
                break;
            }
            //参会人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER_VALUE: {
                LogUtils.i(TAG, "BusEvent -->" + "参会人员变更通知");
                queryMember();
                break;
            }
        }
    }

    @Override
    public void queryMeetDir() {
        InterfaceFile.pbui_Type_MeetDirDetailInfo info = jni.queryMeetDir();
        dirInfos.clear();
        if (info != null) {
            List<InterfaceFile.pbui_Item_MeetDirDetailInfo> itemList = info.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceFile.pbui_Item_MeetDirDetailInfo item = itemList.get(i);
                if (item.getParentid() == 0) {
                    dirInfos.add(item);
                }
            }
        }
        mView.updateDirList();
    }

    @Override
    public void setCurrentDirId(int dirId) {
        currentDirId = dirId;
    }

    @Override
    public void queryMeetDirFile(int dirId) {
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo info = jni.queryMeetDirFile(dirId);
        dirFiles.clear();
        historyDirFiles.clear();
        if (info != null) {
            dirFiles.addAll(info.getItemList());
            if (currentHistoryDirId == dirId) {
                historyDirFiles.addAll(info.getItemList());
            }
        }
        if (currentDirId == dirId) {
            mView.updateFileList();
        }
        mView.updateHistoryFileList();
        queryMeetDirPermission(dirId);
    }

    @Override
    public void queryMember() {
        InterfaceMember.pbui_Type_MemberDetailInfo info = jni.queryMember();
//        List<MemberDirPermissionBean> temps = new ArrayList<>();
//        temps.addAll(memberDirPermissionBeans);
        memberDirPermissionBeans.clear();
        if (info != null) {
            List<InterfaceMember.pbui_Item_MemberDetailInfo> itemList = info.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceMember.pbui_Item_MemberDetailInfo item = itemList.get(i);
                MemberDirPermissionBean e = new MemberDirPermissionBean(item);
                memberDirPermissionBeans.add(e);
            }
        }
    }

    public void queryMeetDirPermission(int dirId) {
        InterfaceFile.pbui_Type_MeetDirRightDetailInfo info = jni.queryMeetDirPermission(dirId);
        if (info != null) {
            List<Integer> memberidList = info.getMemberidList();
            for (int i = 0; i < memberDirPermissionBeans.size(); i++) {
                MemberDirPermissionBean bean = memberDirPermissionBeans.get(i);
                bean.setBlacklist(memberidList.contains(bean.getMember().getPersonid()));
                LogUtils.e("queryMeetDirPermission", bean.toString());
            }
        } else {
            for (int i = 0; i < memberDirPermissionBeans.size(); i++) {
                MemberDirPermissionBean bean = memberDirPermissionBeans.get(i);
                bean.setBlacklist(false);
            }
        }
    }

    @Override
    public List<MemberDirPermissionBean> getDirPermission() {
        List<MemberDirPermissionBean> dirPers = new ArrayList<>();
        dirPers.addAll(memberDirPermissionBeans);
        return dirPers;
    }
}
