package com.xlk.takstarpaperlessmanage.view.admin;

import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.util.DateUtil;

/**
 * @author Created by xlk on 2021/4/21.
 * @desc
 */
public class AdminPresenter extends BasePresenter<AdminContract.View> implements AdminContract.Presenter {
    public AdminPresenter(AdminContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()){
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
            default:break;
        }
    }
}
