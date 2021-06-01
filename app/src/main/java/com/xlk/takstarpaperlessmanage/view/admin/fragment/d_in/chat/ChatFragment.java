package com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.chat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.ChatMemberAdapter;
import com.xlk.takstarpaperlessmanage.adapter.ChatMessageAdapter;
import com.xlk.takstarpaperlessmanage.adapter.ChatVideoMemberAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;
import com.xlk.takstarpaperlessmanage.model.bean.ChatDeviceMember;
import com.xlk.takstarpaperlessmanage.model.bean.ChatMessage;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.ui.gl.VideoChatView;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/5/25.
 * @desc
 */
public class ChatFragment extends BaseFragment<ChatPresenter> implements ChatContract.View {
    private TextView tvMeetingName;
    private RecyclerView rvMember;
    private TextView tvMemberName;
    private RecyclerView rvMessage;
    private EditText edtMessage;
    private ChatMemberAdapter chatMemberAdapter;
    public static boolean isOnChatPage = false;
    private ChatMessageAdapter chatMessageAdapter;
    private int work_state = 0;//=0空闲,=1寻呼中，=2对讲中
    private VideoChatView video_chat_view;
    private PopupWindow chatPop;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void initView(View inflate) {
        tvMeetingName = (TextView) inflate.findViewById(R.id.tv_meeting_name);
        rvMember = (RecyclerView) inflate.findViewById(R.id.rv_member);
        tvMemberName = (TextView) inflate.findViewById(R.id.tv_member_name);
        rvMessage = (RecyclerView) inflate.findViewById(R.id.rv_message);
        edtMessage = (EditText) inflate.findViewById(R.id.edt_message);
        inflate.findViewById(R.id.iv_file).setOnClickListener(v -> {

        });
        inflate.findViewById(R.id.iv_cut).setOnClickListener(v -> {

        });
        inflate.findViewById(R.id.iv_camera).setOnClickListener(v -> {
//            showChatVideoPop();
        });
        inflate.findViewById(R.id.btn_send).setOnClickListener(v -> {
            int selectedMemberId = chatMemberAdapter.getSelectedMemberId();
            if (selectedMemberId == -1) {
                ToastUtil.showShort(R.string.please_choose_member_first);
                return;
            }
            String text = edtMessage.getText().toString();
            if (text.isEmpty()) {
                ToastUtil.showShort(R.string.please_enter_content_first);
                return;
            }
            jni.sendChatMessage(text, InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Message.getNumber(), selectedMemberId);
            edtMessage.setText("");
            presenter.addImMessage(selectedMemberId, new ChatMessage(1, "", GlobalValue.localMemberId
                    , System.currentTimeMillis() / 1000, text));
        });
    }

    @Override
    protected ChatPresenter initPresenter() {
        return new ChatPresenter(this);
    }

    @Override
    protected void initial() {
        isOnChatPage = true;
        presenter.queryDeviceMeet();
        presenter.queryMember();
        presenter.refreshUnreadCount();
    }

    private void showChatVideoPop() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_chat_video, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        chatPop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, rvMessage, Gravity.CENTER, width1 / 2, 0);
        CheckBox cb_all = inflate.findViewById(R.id.cb_all);
        CheckBox cb_ask = inflate.findViewById(R.id.cb_ask);
        RecyclerView rv_pop_member = inflate.findViewById(R.id.rv_pop_member);
        video_chat_view = inflate.findViewById(R.id.video_chat_view);
        ChatVideoMemberAdapter memberAdapter = new ChatVideoMemberAdapter(presenter.deviceMembers);
        rv_pop_member.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_pop_member.addItemDecoration(new RvItemDecoration(getContext()));
        rv_pop_member.setAdapter(memberAdapter);
        memberAdapter.setOnItemClickListener((adapter, view, position) -> {
            memberAdapter.check(presenter.deviceMembers.get(position).getMemberDetailInfo().getPersonid());
            cb_all.setChecked(memberAdapter.isCheckedAll());
        });
        cb_all.setOnClickListener(v -> {
            boolean checked = cb_all.isChecked();
            cb_all.setChecked(checked);
            memberAdapter.checkAll(checked);
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> chatPop.dismiss());
        inflate.findViewById(R.id.btn_stop).setOnClickListener(v -> {

        });
        inflate.findViewById(R.id.btn_launch).setOnClickListener(v -> {
            List<Integer> selectedMemberIds = memberAdapter.getSelectedMemberIds();
            if (selectedMemberIds.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_member_first);
                return;
            }
            int flag = InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_VIDEO_VALUE |//视频
                    InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_AUDIO_VALUE;//音频
            if (cb_ask.isChecked()) {
                flag = flag | InterfaceDevice.Pb_DeviceInviteFlag.Pb_DEVICE_INVITECHAT_FLAG_ASK_VALUE;//询问
            }
            presenter.setOperDeviceId(GlobalValue.localDeviceId);
            jni.deviceIntercom(selectedMemberIds, flag);
            List<Integer> ids = new ArrayList<>();
            ids.add(10);
            ids.add(11);
            video_chat_view.createPlayView(ids);
        });
    }

    @Override
    public void setVideoDecode(Object[] objs) {
        if (chatPop != null && chatPop.isShowing()) {
            video_chat_view.setVideoDecode(objs);
        }
    }

    @Override
    public void setYuv(Object[] objs) {
        if (chatPop != null && chatPop.isShowing()) {
            video_chat_view.setYuv(objs);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        isOnChatPage = !hidden;
        super.onHiddenChanged(hidden);
    }

    @Override
    public void updateMeetName(String meetName) {
        tvMeetingName.setText(meetName);
    }

    @Override
    public void updateMemberList() {
        if (chatMemberAdapter == null) {
            chatMemberAdapter = new ChatMemberAdapter(presenter.deviceMembers);
            rvMember.setLayoutManager(new LinearLayoutManager(getContext()));
            rvMember.setAdapter(chatMemberAdapter);
            chatMemberAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    ChatDeviceMember chatDeviceMember = presenter.deviceMembers.get(position);
                    chatDeviceMember.setCount(0);
                    chatDeviceMember.setLastCheckTime(System.currentTimeMillis() / 1000);
                    int memberId = chatDeviceMember.getMemberDetailInfo().getPersonid();
                    chatMemberAdapter.setSelectedMember(memberId);
                    tvMemberName.setText(chatDeviceMember.getMemberDetailInfo().getName().toStringUtf8());
                    presenter.setCurrentMemberId(memberId);
                    presenter.updateChatMessageData();
                }
            });
        } else {
            chatMemberAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateMessageList(List<ChatMessage> currentMessages) {
        if (chatMessageAdapter == null) {
            chatMessageAdapter = new ChatMessageAdapter(getContext(), currentMessages);
            rvMessage.setLayoutManager(new LinearLayoutManager(getContext()));
            rvMessage.setAdapter(chatMessageAdapter);
        } else {
            chatMessageAdapter.notifyDataSetChanged();
        }
        if (currentMessages.size() > 0) {
            rvMessage.smoothScrollToPosition(currentMessages.size() - 1);
        }
    }
}
