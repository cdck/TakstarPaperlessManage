package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.statistical;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceStatistic;
import com.xlk.takstarpaperlessmanage.base.BasePresenter;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;

/**
 * @author Created by xlk on 2021/5/29.
 * @desc
 */
class StatisticsPresenter extends BasePresenter<StatisticsContract.View> implements StatisticsContract.Presenter {
    public StatisticsPresenter(StatisticsContract.View view) {
        super(view);
    }

    @Override
    protected void busEvent(EventMessage msg) throws InvalidProtocolBufferException {
        switch (msg.getType()) {
            case InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSTATISTIC_VALUE: {
                int method = msg.getMethod();
                byte[] bytes = (byte[]) msg.getObjects()[0];
                if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY_VALUE) {
                    //返回查询会议统计通知
                    InterfaceStatistic.pbui_Type_MeetStatisticInfo info = InterfaceStatistic.pbui_Type_MeetStatisticInfo.parseFrom(bytes);
                    mView.updateCount(info);
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ASK_VALUE) {
                    //返回按时间段查询的会议统计通知
                    InterfaceStatistic.pbui_Type_MeetQuarterStatisticInfo info = InterfaceStatistic.pbui_Type_MeetQuarterStatisticInfo.parseFrom(bytes);
                    mView.updateCountByTime(info);
                }
                break;
            }
            case EventType.RESULT_DIR_PATH:{
                int dirType = (int) msg.getObjects()[0];
                String dirPath = (String) msg.getObjects()[1];
                if(dirType== Constant.CHOOSE_DIR_TYPE_EXPORT_STATISTICS){
                    mView.updateExportDirPath(dirPath);
                }
                break;
            }
        }
    }

    @Override
    public void queryMeetingStatistics() {
        jni.queryMeetingStatistics(0);
    }
}
