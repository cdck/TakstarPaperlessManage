package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.chat;

import com.xlk.takstarpaperlessmanage.base.BaseContract;
import com.xlk.takstarpaperlessmanage.model.bean.ChatMessage;

import java.util.List;

/**
 * @author Created by xlk on 2021/5/25.
 * @desc
 */
interface ChatContract {
    interface View extends BaseContract.View{
        void updateMeetName(String meetName);

        void updateMemberList();

        void updateMessageList(List<ChatMessage> currentMessages);

        void setVideoDecode(Object[] objs);

        void setYuv(Object[] objs);
    }
    interface Presenter extends BaseContract.Presenter{
        void queryDeviceMeet();

        void queryMember();

        void setCurrentMemberId(int memberId);

        void updateChatMessageData();

        void refreshUnreadCount();

        void setOperDeviceId(int localDeviceId);
    }
}
