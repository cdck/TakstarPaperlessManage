package com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.attendee;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfacePerson;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.util.JxlUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/13.
 * @desc
 */
public class AttendeePresenter extends BasePresenter<AttendeeContract.View> implements AttendeeContract.Presenter {

    public List<InterfacePerson.pbui_Item_PersonDetailInfo> attendees = new ArrayList<>();

    public AttendeePresenter(AttendeeContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //常用人员变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLE_VALUE: {
                if (msg.getMethod() == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    byte[] bytes = (byte[]) msg.getObjects()[0];
                    InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(bytes);
                    int id = pbui_meetNotifyMsg.getId();
                    int opermethod = pbui_meetNotifyMsg.getOpermethod();
                    LogUtils.i( "BusEvent 常用人员变更通知 id=" + id + ",opermethod=" + opermethod);
                    queryAttendee();
                }
                break;
            }
            case EventType.RESULT_DIR_PATH:{
                int dirType = (int) msg.getObjects()[0];
                String dirPath = (String) msg.getObjects()[1];
                if(dirType== Constant.CHOOSE_DIR_TYPE_EXPORT_OFTEN_MEMBER){
                    mView.updateExportDirPath(dirPath);
                }
                break;
            }
        }
    }

    @Override
    public void queryAttendee() {
        InterfacePerson.pbui_Type_PersonDetailInfo info = jni.queryAttendee();
        attendees.clear();
        if (info != null) {
            attendees.addAll(info.getItemList());
        }
        mView.updateAttendeeList();
    }
}
