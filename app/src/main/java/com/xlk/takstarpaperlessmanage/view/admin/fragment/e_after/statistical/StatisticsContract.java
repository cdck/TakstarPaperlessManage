package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.statistical;

import com.mogujie.tt.protobuf.InterfaceStatistic;
import com.xlk.takstarpaperlessmanage.base.BaseContract;

/**
 * @author Created by xlk on 2021/5/29.
 * @desc
 */
interface StatisticsContract {
    interface View extends BaseContract.View{
        void updateCount(InterfaceStatistic.pbui_Type_MeetStatisticInfo info);

        void updateCountByTime(InterfaceStatistic.pbui_Type_MeetQuarterStatisticInfo info);
    }
    interface Presenter extends BaseContract.Presenter{
        void queryMeetingStatistics();
    }
}
