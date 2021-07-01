package com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.meet;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.MeetingManageAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.DateUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.xlk.takstarpaperlessmanage.model.Constant.election_entry;
import static com.xlk.takstarpaperlessmanage.model.Constant.s2b;

/**
 * @author Created by xlk on 2021/5/18.
 * @desc 会前设置-会议管理
 */
public class MeetingManageFragment extends BaseFragment<MeetingManagePresenter> implements MeetingManageContract.View {
    /**
     * =0当前会议，=1历史会议，=2模板会议，=3全部会议
     */
    private int currentTypeIndex = 0;
    private MeetingManageAdapter meetingManageAdapter;
    private RecyclerView rv_meeting;
    private Spinner sp_meet_type;
    private List<InterfaceMeet.pbui_Item_MeetMeetInfo> currentMeetings = new ArrayList<>();
    private PopupWindow modifyPop;
    private ArrayAdapter<String> spRoomadApter;
    private ArrayAdapter<String> spConfidentialAdapter;
    private ArrayAdapter<String> spSignInTypeAdapter;
    private TimePickerView mStartTimePickerView;
    private long currentStartTime;
    private TimePickerView mEndTimePickerView;
    private long currentEndTime;
    private TextView tv_start_time;
    private TextView tv_end_time;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_meeting_manage;
    }

    @Override
    protected void initView(View inflate) {
        sp_meet_type = inflate.findViewById(R.id.sp_meet_type);
        sp_meet_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.i("onItemSelected position=" + position);
                currentTypeIndex = position;
                updateMeetingList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        rv_meeting = inflate.findViewById(R.id.rv_meeting);
        inflate.findViewById(R.id.btn_modify).setOnClickListener(v -> {
            InterfaceMeet.pbui_Item_MeetMeetInfo info = meetingManageAdapter.getSelectedMeeting();
            if (info == null) {
                ToastUtil.showShort(R.string.please_select_meeting_first);
                return;
            }
            showModifyPop(info);
        });
        inflate.findViewById(R.id.btn_delete).setOnClickListener(v -> {
            InterfaceMeet.pbui_Item_MeetMeetInfo info = meetingManageAdapter.getSelectedMeeting();
            if (info == null) {
                ToastUtil.showShort(R.string.please_select_meeting_first);
                return;
            }
            jni.delMeeting(info);
        });
        //切换到该会议编辑
        inflate.findViewById(R.id.btn_switch_meeting).setOnClickListener(v -> {
            int meetId = meetingManageAdapter.getSelectedMeetingId();
            if (meetId != -1) {
                jni.modifyContextProperties(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_CURMEETINGID_VALUE, meetId);
            } else {
                ToastUtils.showShort(R.string.please_choose_meeting_first);
            }
        });
    }

    private void showModifyPop(InterfaceMeet.pbui_Item_MeetMeetInfo info) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_modify_meeting, null, false);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        modifyPop = PopUtil.createPopupWindow(inflate, width * 2 / 3, height * 2 / 3, rv_meeting, Gravity.CENTER, width1 / 2, 0);

        EditText edt_meeting_name = inflate.findViewById(R.id.edt_meeting_name);
        Spinner sp_room = inflate.findViewById(R.id.sp_room);
        Spinner sp_confidentiality = inflate.findViewById(R.id.sp_confidentiality);
        tv_start_time = inflate.findViewById(R.id.tv_start_time);
        tv_end_time = inflate.findViewById(R.id.tv_end_time);
        Spinner sp_sign_in_type = inflate.findViewById(R.id.sp_sign_in_type);
        LinearLayout ll_pwd = inflate.findViewById(R.id.ll_pwd);
        EditText edt_meeting_pwd = inflate.findViewById(R.id.edt_meeting_pwd);
        EditText edt_reservation = inflate.findViewById(R.id.edt_reservation);

        currentStartTime = info.getStartTime();
        currentEndTime = info.getEndTime();
        edt_meeting_name.setText(info.getName().toStringUtf8());
        sp_room.setAdapter(spRoomadApter);
        int position = presenter.getRoomPosition(info.getRoomId());
        sp_room.setSelection(position);
        sp_confidentiality.setAdapter(spConfidentialAdapter);
        sp_confidentiality.setSelection(info.getSecrecy());
        tv_start_time.setText(DateUtil.secondsFormat(info.getStartTime(), "yyyy/MM/dd HH:mm"));
        tv_end_time.setText(DateUtil.secondsFormat(info.getEndTime(), "yyyy/MM/dd HH:mm"));
        sp_sign_in_type.setAdapter(spSignInTypeAdapter);
        sp_sign_in_type.setSelection(info.getSigninType());
        sp_sign_in_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3 || position == 4) {
                    //有会议密码签到
                    ll_pwd.setVisibility(View.VISIBLE);
                } else {
                    ll_pwd.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ll_pwd.setVisibility((info.getSigninType() == 3 || info.getSigninType() == 4) ? View.VISIBLE : View.GONE);
        edt_meeting_pwd.setText(info.getOnepswSignin().toStringUtf8());
        edt_reservation.setText(info.getOrdername().toStringUtf8());

        tv_start_time.setOnClickListener(v -> {
            if (currentStartTime != 0) {
                java.util.Calendar instance = java.util.Calendar.getInstance();
                instance.setTimeInMillis(currentStartTime * 1000);
                mStartTimePickerView.setDate(instance);
            }
            mStartTimePickerView.show(v);
        });
        tv_end_time.setOnClickListener(v -> {
            if (currentStartTime != 0) {
                java.util.Calendar instance = java.util.Calendar.getInstance();
                instance.setTimeInMillis(currentEndTime * 1000);
                mEndTimePickerView.setDate(instance);
            }
            mEndTimePickerView.show(v);
        });
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> modifyPop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String meetingName = edt_meeting_name.getText().toString();
            String pwd = edt_meeting_pwd.getText().toString().trim();
            String orderName = edt_reservation.getText().toString().trim();
            int selectedItemPosition = sp_room.getSelectedItemPosition();
            InterfaceRoom.pbui_Item_MeetRoomDetailInfo room = presenter.allRooms.get(selectedItemPosition);
            if (meetingName.isEmpty()) {
                ToastUtil.showShort(R.string.please_enter_meeting_name_first);
                return;
            }
            if (ll_pwd.getVisibility() == View.VISIBLE) {
                if (pwd.isEmpty()) {
                    ToastUtil.showShort(R.string.please_enter_meeting_password_first);
                    return;
                }
            }
            int signInTypeIndex = sp_sign_in_type.getSelectedItemPosition();
            InterfaceMeet.pbui_Item_MeetMeetInfo.Builder builder = InterfaceMeet.pbui_Item_MeetMeetInfo.newBuilder();
            builder.setId(info.getId());
            builder.setName(s2b(meetingName));
            builder.setRoomname(room.getName());
            builder.setRoomId(room.getRoomid());
            builder.setSecrecy(sp_confidentiality.getSelectedItemPosition());
            builder.setStartTime(currentStartTime);
            builder.setEndTime(currentEndTime);
            builder.setSigninType(signInTypeIndex);
            builder.setManagerid(info.getManagerid());
            if (signInTypeIndex == 3 || signInTypeIndex == 4) {
                if (!pwd.isEmpty()) {
                    builder.setOnepswSignin(s2b(pwd));
                } else {
                    ToastUtil.showShort(R.string.please_enter_meeting_password_first);
                    return;
                }
            }
            builder.setStatus(info.getStatus());
            builder.setOrdername(s2b(orderName));
            jni.modifyMeeting(builder.build());
            modifyPop.dismiss();
        });
    }

    @Override
    protected MeetingManagePresenter initPresenter() {
        return new MeetingManagePresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryAllMeeting();
        presenter.queryMeetingRoom();
        initSpinnerAdapter();
        initTimePicker();
    }

    @Override
    protected void onShow() {
        initial();
    }

    private void initTimePicker() {
        mStartTimePickerView = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                currentStartTime = date.getTime() / 1000;
                if (tv_start_time != null) {
                    tv_start_time.setText(DateUtil.millisecondsFormat(date.getTime(), "yyyy/MM/dd/ HH:mm"));
                }
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, false})
                //默认设置false ，内部实现将DecorView 作为它的父控件
                .isDialog(true)
                .addOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                .setItemVisibleCount(5)
                .setLineSpacingMultiplier(2.0f)
                .isAlphaGradient(true)
                .build();

        mEndTimePickerView = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                currentEndTime = date.getTime() / 1000;
                if (tv_end_time != null) {
                    tv_end_time.setText(DateUtil.millisecondsFormat(date.getTime(), "yyyy/MM/dd/ HH:mm"));
                }
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, false})
                //默认设置false ，内部实现将DecorView 作为它的父控件
                .isDialog(true)
                .addOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                .setItemVisibleCount(5)
                .setLineSpacingMultiplier(2.0f)
                .isAlphaGradient(true)
                .build();
    }

    private void initSpinnerAdapter() {
        spConfidentialAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_checked_black_text, getResources().getStringArray(R.array.yes_or_no));
        spSignInTypeAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_checked_black_text, getResources().getStringArray(R.array.sign_in_type));
    }

    @Override
    public void updateRoomList() {
        List<String> rooms = new ArrayList<>();
        for (int i = 0; i < presenter.allRooms.size(); i++) {
            InterfaceRoom.pbui_Item_MeetRoomDetailInfo room = presenter.allRooms.get(i);
            if (!room.getName().toStringUtf8().isEmpty()) {
                rooms.add(room.getName().toStringUtf8());
            }
        }
        spRoomadApter = new ArrayAdapter<String>(getContext(), R.layout.spinner_checked_black_text, rooms);
    }

    @Override
    public void updateMeetingList() {
        currentMeetings.clear();
        if (currentTypeIndex == 3) {
            currentMeetings.addAll(presenter.allMeetings);
        } else {
            for (int i = 0; i < presenter.allMeetings.size(); i++) {
                InterfaceMeet.pbui_Item_MeetMeetInfo info = presenter.allMeetings.get(i);
                int status = info.getStatus();
                switch (currentTypeIndex) {
                    case 0: {
                        if (status == InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_Start_VALUE) {
                            currentMeetings.add(info);
                        }
                    }
                    case 1: {
                        if (status == InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_End_VALUE) {
                            currentMeetings.add(info);
                        }
                    }
                    case 2: {
                        if (status == InterfaceMacro.Pb_MeetStatus.Pb_MEETING_MODEL_VALUE) {
                            currentMeetings.add(info);
                        }
                    }
                }
            }
        }

        if (meetingManageAdapter == null) {
            meetingManageAdapter = new MeetingManageAdapter(currentMeetings);
            meetingManageAdapter.addChildClickViewIds(R.id.operation_view_1, R.id.operation_view_2);
            rv_meeting.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_meeting.addItemDecoration(new RvItemDecoration(getContext()));
            rv_meeting.setAdapter(meetingManageAdapter);
            meetingManageAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    meetingManageAdapter.setSelectedId(currentMeetings.get(position).getId());
                }
            });
            meetingManageAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                    InterfaceMeet.pbui_Item_MeetMeetInfo info = currentMeetings.get(position);
                    boolean isView1 = view.getId() == R.id.operation_view_1;
                    if (isView1) {
                        switch (info.getStatus()) {
                            case InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_PAUSE_VALUE:
                            case InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_Ready_VALUE: {
                                jni.modifyMeetingStatus(info.getId(), InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_Start_VALUE);
                                break;
                            }
                            case InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_Start_VALUE: {
                                jni.modifyMeetingStatus(info.getId(), InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_PAUSE_VALUE);
                                break;
                            }
                            case InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_End_VALUE: {
                                jni.delMeeting(info);
                                break;
                            }
                        }
                    } else {
                        jni.modifyMeetingStatus(info.getId(), InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_End_VALUE);
                    }
                }
            });
        } else {
            meetingManageAdapter.notifyDataSetChanged();
        }
    }
}
