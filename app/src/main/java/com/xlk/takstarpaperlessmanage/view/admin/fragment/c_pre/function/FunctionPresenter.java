package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.function;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeetfunction;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.bean.FunctionBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2021/5/22.
 * @desc
 */
class FunctionPresenter extends BasePresenter<FunctionContract.View> implements FunctionContract.Presenter {
    public List<FunctionBean> allMeetFunction = new ArrayList<>();
    public List<FunctionBean> meetFunction = new ArrayList<>();
    public List<FunctionBean> hideMeetFunction = new ArrayList<>();

    public FunctionPresenter(FunctionContract.View view) {
        super(view);
        initAllMeetingFunction();
    }

    private void initAllMeetingFunction() {
        for (int i = 0; i < 8; i++) {
            FunctionBean item = new FunctionBean(i);
            switch (i) {
                case 0:
                    item.setFuncode(InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_AGENDA_BULLETIN_VALUE);
                    break;
                case 1:
                    item.setFuncode(InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_MATERIAL_VALUE);
                    break;
                case 2:
                    item.setFuncode(InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_POSTIL_VALUE);
                    break;
                case 3:
                    item.setFuncode(InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_MESSAGE_VALUE);
                    break;
                case 4:
                    item.setFuncode(InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_VIDEOSTREAM_VALUE);
                    break;
                case 5:
                    item.setFuncode(InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WHITEBOARD_VALUE);
                    break;
                case 6:
                    item.setFuncode(InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WEBBROWSER_VALUE);
                    break;
                case 7:
                    item.setFuncode(InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_SIGNINRESULT_VALUE);
                    break;
                default:
                    break;
            }
            allMeetFunction.add(item);
        }
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            //会议功能变更通知
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FUNCONFIG_VALUE:
                LogUtils.i(TAG, "busEvent 会议功能变更通知");
                queryMeetingFunction();
                break;
            default:
                break;
        }
    }

    @Override
    public void queryMeetingFunction() {
        InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo info = jni.queryMeetFunction();
        meetFunction.clear();
        hideMeetFunction.clear();
        if (info != null) {
            List<InterfaceMeetfunction.pbui_Item_MeetFunConfigDetailInfo> itemList = info.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                InterfaceMeetfunction.pbui_Item_MeetFunConfigDetailInfo item = itemList.get(i);
                int position = item.getPosition();
                int funcode = item.getFuncode();
                if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_AGENDA_BULLETIN_VALUE
                        || funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_MATERIAL_VALUE
                        || funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_POSTIL_VALUE
                        || funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_MESSAGE_VALUE
                        || funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_VIDEOSTREAM_VALUE
                        || funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WHITEBOARD_VALUE
                        || funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WEBBROWSER_VALUE
                        || funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_SIGNINRESULT_VALUE
                ) {
                    meetFunction.add(new FunctionBean(funcode, position));
                }
            }
        }
        for (int i = 0; i < allMeetFunction.size(); i++) {
            FunctionBean item = allMeetFunction.get(i);
            boolean isHide = true;
            for (int j = 0; j < meetFunction.size(); j++) {
                if (meetFunction.get(j).getFuncode() == item.getFuncode()) {
                    isHide = false;
                    break;
                }
            }
            LogUtils.e(TAG, "queryFunction isHide=" + isHide);
            if (isHide) {
                item.setPosition(hideMeetFunction.size());
                hideMeetFunction.add(item);
            }
        }
        mView.updateFunctionList();
    }
}
