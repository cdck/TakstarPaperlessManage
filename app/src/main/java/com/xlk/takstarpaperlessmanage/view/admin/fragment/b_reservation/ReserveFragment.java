package com.xlk.takstarpaperlessmanage.view.admin.fragment.b_reservation;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfacePerson;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.util.DateUtil;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xlk.takstarpaperlessmanage.model.Constant.s2b;

/**
 * @author Created by xlk on 2021/5/15.
 * @desc
 */
public class ReserveFragment extends BaseFragment<ReservePresenter> implements ReserveContract.View, CalendarView.OnYearChangeListener
        , CalendarView.OnMonthChangeListener, CalendarView.OnCalendarSelectListener
        , CalendarView.OnWeekChangeListener, CalendarView.OnViewChangeListener {

    private CalendarLayout calendar_layout;
    private CalendarView calendar_view;
    private TextView tv_current_month;
    private Button btn_month;
    private Button btn_week;
    private int mYear;
    private TextView tv_sun_name, tv_mon_name, tv_tue_name, tv_wed_name, tv_thu_name, tv_fri_name, tv_sat_name;
    private TextView tv_sun_time, tv_mon_time, tv_tue_time, tv_wed_time, tv_thu_time, tv_fri_time, tv_sat_time;
    private TextView tv_sun_ordernamer, tv_mon_ordernamer, tv_tue_ordernamer, tv_wed_ordernamer, tv_thu_ordernamer, tv_fri_ordernamer, tv_sat_ordernamer;
    private LinearLayout ll_sun, ll_mon, ll_tue, ll_wed, ll_thu, ll_fri, ll_sat;
    private boolean mIsMonthView = true;
    private FloatingActionButton fab;
    private PopupWindow pop;
    private TimePickerView mStartTimePickerView, mEndTimePickerView;
    /**
     * 单位：秒
     */
    private long currentStartTime, currentEndTime;
    private TextView edt_start_time, edt_end_time;
    private ArrayAdapter<String> spRoomadApter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reserve;
    }

    @Override
    protected void initView(View inflate) {
        calendar_layout = (CalendarLayout) inflate.findViewById(R.id.calendar_layout);
        calendar_view = (CalendarView) inflate.findViewById(R.id.calendar_view);
        fab = (FloatingActionButton) inflate.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            showModifyCreateMeetingPop(null);
        });
        ll_sun = (LinearLayout) inflate.findViewById(R.id.ll_sun);
        tv_sun_name = (TextView) inflate.findViewById(R.id.tv_sun_name);
        tv_sun_time = (TextView) inflate.findViewById(R.id.tv_sun_time);
        tv_sun_ordernamer = (TextView) inflate.findViewById(R.id.tv_sun_ordernamer);

        ll_mon = (LinearLayout) inflate.findViewById(R.id.ll_mon);
        tv_mon_name = (TextView) inflate.findViewById(R.id.tv_mon_name);
        tv_mon_time = (TextView) inflate.findViewById(R.id.tv_mon_time);
        tv_mon_ordernamer = (TextView) inflate.findViewById(R.id.tv_mon_ordernamer);

        ll_tue = (LinearLayout) inflate.findViewById(R.id.ll_tue);
        tv_tue_name = (TextView) inflate.findViewById(R.id.tv_tue_name);
        tv_tue_time = (TextView) inflate.findViewById(R.id.tv_tue_time);
        tv_tue_ordernamer = (TextView) inflate.findViewById(R.id.tv_tue_ordernamer);

        ll_wed = (LinearLayout) inflate.findViewById(R.id.ll_wed);
        tv_wed_name = (TextView) inflate.findViewById(R.id.tv_wed_name);
        tv_wed_time = (TextView) inflate.findViewById(R.id.tv_wed_time);
        tv_wed_ordernamer = (TextView) inflate.findViewById(R.id.tv_wed_ordernamer);

        ll_thu = (LinearLayout) inflate.findViewById(R.id.ll_thu);
        tv_thu_name = (TextView) inflate.findViewById(R.id.tv_thu_name);
        tv_thu_time = (TextView) inflate.findViewById(R.id.tv_thu_time);
        tv_thu_ordernamer = (TextView) inflate.findViewById(R.id.tv_thu_ordernamer);

        ll_fri = (LinearLayout) inflate.findViewById(R.id.ll_fri);
        tv_fri_name = (TextView) inflate.findViewById(R.id.tv_fri_name);
        tv_fri_time = (TextView) inflate.findViewById(R.id.tv_fri_time);
        tv_fri_ordernamer = (TextView) inflate.findViewById(R.id.tv_fri_ordernamer);

        ll_sat = (LinearLayout) inflate.findViewById(R.id.ll_sat);
        tv_sat_name = (TextView) inflate.findViewById(R.id.tv_sat_name);
        tv_sat_time = (TextView) inflate.findViewById(R.id.tv_sat_time);
        tv_sat_ordernamer = (TextView) inflate.findViewById(R.id.tv_sat_ordernamer);
        calendar_view.setOnYearChangeListener(this);
        calendar_view.setOnMonthChangeListener(this);
        calendar_view.setOnWeekChangeListener(this);
        calendar_view.setOnCalendarSelectListener(this);
        calendar_view.setOnViewChangeListener(this);
        inflate.findViewById(R.id.iv_pre_month).setOnClickListener(v -> {
            calendar_view.scrollToPre(true);
        });
        tv_current_month = (TextView) inflate.findViewById(R.id.tv_current_month);
        tv_current_month.setOnClickListener(v -> {
            if (!calendar_layout.isExpand()) {
                calendar_layout.expand();
                btn_month.setSelected(true);
                btn_week.setSelected(false);
                return;
            }
            calendar_view.showYearSelectLayout(mYear);
            tv_current_month.setText(String.valueOf(mYear));
        });
        inflate.findViewById(R.id.iv_next_month).setOnClickListener(v -> {
            calendar_view.scrollToNext(true);
        });
        btn_month = (Button) inflate.findViewById(R.id.btn_month);
        btn_week = (Button) inflate.findViewById(R.id.btn_week);
        btn_month.setSelected(true);
        btn_month.setOnClickListener(v -> {
            boolean selected = btn_month.isSelected();
            if (selected) {
                calendar_layout.shrink();
            } else {
                calendar_layout.expand();
            }
            btn_month.setSelected(!selected);
            btn_week.setSelected(selected);
        });
        btn_week.setOnClickListener(v -> {
            boolean selected = btn_week.isSelected();
            if (selected) {
                calendar_layout.expand();
            } else {
                calendar_layout.shrink();
            }
            btn_week.setSelected(!selected);
            btn_month.setSelected(selected);
        });
        mYear = calendar_view.getCurYear();
        tv_current_month.setText(calendar_view.getCurYear() + "年" + calendar_view.getCurMonth() + "月");
    }

    private void showModifyCreateMeetingPop(InterfaceMeet.pbui_Item_MeetMeetInfo item) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.pop_meeting, null, false);
        View ll_content = getActivity().findViewById(R.id.ll_content);
        View rv_navigation = getActivity().findViewById(R.id.rv_navigation);
        int width = ll_content.getWidth();
        int height = ll_content.getHeight();
        int width1 = rv_navigation.getWidth();
        pop = PopUtil.createPopupWindow(inflate, width / 2, height / 2, calendar_layout, Gravity.CENTER, width1 / 2, 0);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        boolean isAdd = item == null;
        tv_title.setText(isAdd ? getString(R.string.add) : getString(R.string.preview_or_modify));
        EditText edt_name = inflate.findViewById(R.id.edt_name);
        Spinner sp_confidentiality = inflate.findViewById(R.id.sp_confidentiality);
        edt_start_time = inflate.findViewById(R.id.edt_start_time);
        edt_end_time = inflate.findViewById(R.id.edt_end_time);
        Spinner sp_sign_in_type = inflate.findViewById(R.id.sp_sign_in_type);
        Spinner sp_meet_room = inflate.findViewById(R.id.sp_meet_room);
        sp_meet_room.setAdapter(spRoomadApter);
        if (!isAdd) {
            currentStartTime = item.getStartTime();
            currentEndTime = item.getEndTime();
            int position = presenter.getRoomPosition(item.getRoomId());
            LogUtils.e("会议室所在的索引位=" + position);
            if (position != -1) {
                sp_meet_room.setSelection(position);
            }
            edt_name.setText(item.getName().toStringUtf8());
            edt_start_time.setText(DateUtil.secondsFormat(item.getStartTime(), "yyyy年MM月dd日 HH:mm"));
            edt_end_time.setText(DateUtil.secondsFormat(item.getEndTime(), "yyyy年MM月dd日 HH:mm"));
            sp_confidentiality.setSelection(item.getSecrecy());
            sp_sign_in_type.setSelection(item.getSigninType());
        }
        edt_start_time.setOnClickListener(v -> {
            if (currentStartTime != 0) {
                java.util.Calendar instance = java.util.Calendar.getInstance();
                instance.setTimeInMillis(currentStartTime * 1000);
                mStartTimePickerView.setDate(instance);
            }
            mStartTimePickerView.show(edt_start_time);
        });
        edt_end_time.setOnClickListener(v -> {
            if (currentEndTime != 0) {
                java.util.Calendar instance = java.util.Calendar.getInstance();
                instance.setTimeInMillis(currentEndTime * 1000);
                mEndTimePickerView.setDate(instance);
            }
            mEndTimePickerView.show(edt_end_time);
        });
        inflate.findViewById(R.id.iv_close).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(v -> pop.dismiss());
        inflate.findViewById(R.id.btn_define).setOnClickListener(v -> {
            String name = edt_name.getText().toString().trim();
            int confidentiality = sp_confidentiality.getSelectedItemPosition();
            int sign_in_type = sp_sign_in_type.getSelectedItemPosition();
            int roomid = presenter.allRooms.get(sp_meet_room.getSelectedItemPosition()).getRoomid();
            String roomName = presenter.allRooms.get(sp_meet_room.getSelectedItemPosition()).getName().toStringUtf8();
            LogUtils.e("当前选中的会议室ID=" + roomid);
            if (TextUtils.isEmpty(name)) {
                ToastUtil.showShort(R.string.please_enter_the_content_to_be_modified);
                return;
            }
            if (currentStartTime == 0 || currentEndTime == 0) {
                ToastUtil.showShort(R.string.please_choose_time);
                return;
            }
            if (currentStartTime >= currentEndTime) {
                ToastUtil.showShort(R.string.time_error);
                return;
            }
            InterfaceMeet.pbui_Item_MeetMeetInfo.Builder builder = InterfaceMeet.pbui_Item_MeetMeetInfo.newBuilder();
            builder
                    .setName(s2b(name))
                    .setRoomId(roomid)
                    .setRoomname(s2b(roomName))
                    .setSecrecy(confidentiality)
                    .setStartTime(currentStartTime)
                    .setEndTime(currentEndTime)
                    .setSigninType(sign_in_type)
                    .build();
            if (isAdd) {
                jni.addMeeting(builder.build());
            } else {
                builder.setId(item.getId());
                builder.setManagerid(item.getManagerid());
                builder.setOnepswSignin(item.getOnepswSignin());
                builder.setStatus(item.getStatus());
                builder.setOrdername(item.getOrdername());
                jni.modifyMeeting(builder.build());
            }
            pop.dismiss();
        });
    }

    @Override
    protected ReservePresenter initPresenter() {
        return new ReservePresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryMeeting();
        presenter.queryMeetingRoom();
        initTimePicker();
    }

    private void initTimePicker() {
        mStartTimePickerView = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                currentStartTime = date.getTime() / 1000;
                if (edt_start_time != null) {
                    edt_start_time.setText(DateUtil.millisecondsFormat(date.getTime(), "yyyy年MM月dd日 HH:mm"));
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
                if (edt_end_time != null) {
                    edt_end_time.setText(DateUtil.millisecondsFormat(date.getTime(), "yyyy年MM月dd日 HH:mm"));
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

    @Override
    public void onYearChange(int year) {
        LogUtils.i("onYearChange year=" + year);
        tv_current_month.setText(String.valueOf(year));
    }

    @Override
    public void onMonthChange(int year, int month) {
        mYear = year;
        tv_current_month.setText(year + "年" + month + "月");
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {
        LogUtils.i("onCalendarOutOfRange " + calendar.toString());
    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        LogUtils.i("onCalendarSelect " + calendar.toString() + ",isClick=" + isClick);
        mYear = calendar.getYear();
        tv_current_month.setText(calendar.getYear() + "年" + calendar.getMonth() + "月");
        if (calendar.hasScheme() && isClick) {
            InterfaceMeet.pbui_Item_MeetMeetInfo info = (InterfaceMeet.pbui_Item_MeetMeetInfo) calendar.getSchemes().get(0).getObj();
            showModifyCreateMeetingPop(info);
        }
    }

    @Override
    public void onWeekChange(List<Calendar> weekCalendars) {
        LogUtils.i("onWeekChange " + weekCalendars.size());
    }

    @Override
    public void onViewChange(boolean isMonthView) {
        mIsMonthView = isMonthView;
        LogUtils.e("onViewChange", "  ---  " + (isMonthView ? "月视图" : "周视图") + ", ll_sun是否为null=" + (ll_sun == null));
        if (isMonthView) {

        } else {
            if (ll_sun == null) return;
            ll_sun.setVisibility(View.INVISIBLE);
            ll_mon.setVisibility(View.INVISIBLE);
            ll_tue.setVisibility(View.INVISIBLE);
            ll_wed.setVisibility(View.INVISIBLE);
            ll_thu.setVisibility(View.INVISIBLE);
            ll_fri.setVisibility(View.INVISIBLE);
            ll_sat.setVisibility(View.INVISIBLE);
            List<Calendar> currentWeekCalendars = calendar_view.getCurrentWeekCalendars();
            for (int i = 0; i < currentWeekCalendars.size(); i++) {
                Calendar calendar = currentWeekCalendars.get(i);
                int year = calendar.getYear();
                int month = calendar.getMonth();
                int day = calendar.getDay();
                LogUtils.e("====" + calendar.toString());
                List<Calendar.Scheme> schemes = calendar.getSchemes();
                if (schemes != null) {
                    SpannableStringBuilder ssb = new SpannableStringBuilder();
                    Calendar.Scheme scheme = schemes.get(0);
                    InterfaceMeet.pbui_Item_MeetMeetInfo item = (InterfaceMeet.pbui_Item_MeetMeetInfo) scheme.getObj();
                    String meetingName = item.getName().toStringUtf8();
                    String orderName = item.getOrdername().toStringUtf8();
                    long startTime = item.getStartTime();
                    long endTime = item.getEndTime();
                    boolean isToday = (Integer.parseInt(DateUtil.convertYear(endTime)) == year)
                            && (Integer.parseInt(DateUtil.convertMonth(endTime)) == month)
                            && (Integer.parseInt(DateUtil.convertDay(endTime)) == day);
                    if (i == 0) {
                        ll_sun.setVisibility(View.VISIBLE);
                        tv_sun_name.setText(meetingName);
                        tv_sun_time.setText(isToday ? "时间：" + DateUtil.convertHour(startTime) + "-" + DateUtil.convertHour(endTime)
                                : "时间：" + DateUtil.convertHour(startTime) + "-" + DateUtil.secondFormatDateTime(endTime));
                        ssb.append("预约人  ").append(orderName);
                        ssb.setSpan(new BackgroundColorSpan(Color.argb(100, 52, 117, 211)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_sun_ordernamer.setText(ssb);
                    } else if (i == 1) {
                        ll_mon.setVisibility(View.VISIBLE);
                        tv_mon_name.setText(meetingName);
                        tv_mon_time.setText(isToday ? "时间：" + DateUtil.convertHour(startTime) + "-" + DateUtil.convertHour(endTime)
                                : "时间：" + DateUtil.convertHour(startTime) + "-" + DateUtil.secondFormatDateTime(endTime));
                        ssb.append("预约人  ").append(orderName);
                        ssb.setSpan(new BackgroundColorSpan(Color.argb(100, 52, 117, 211)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_mon_ordernamer.setText(ssb);
                    } else if (i == 2) {
                        ll_tue.setVisibility(View.VISIBLE);
                        tv_tue_name.setText(meetingName);
                        tv_tue_time.setText(isToday ? "时间：" + DateUtil.convertHour(startTime) + "-" + DateUtil.convertHour(endTime)
                                : "时间：" + DateUtil.convertHour(startTime) + "-" + DateUtil.secondFormatDateTime(endTime));
                        ssb.append("预约人  ").append(orderName);
                        ssb.setSpan(new BackgroundColorSpan(Color.argb(100, 52, 117, 211)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_tue_ordernamer.setText(ssb);
                    } else if (i == 3) {
                        ll_wed.setVisibility(View.VISIBLE);
                        tv_wed_name.setText(meetingName);
                        tv_wed_time.setText(isToday ? "时间：" + DateUtil.convertHour(startTime) + "-" + DateUtil.convertHour(endTime)
                                : "时间：" + DateUtil.convertHour(startTime) + "-" + DateUtil.secondFormatDateTime(endTime));
                        ssb.append("预约人  ").append(orderName);
                        ssb.setSpan(new BackgroundColorSpan(Color.argb(100, 52, 117, 211)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_wed_ordernamer.setText(ssb);
                    } else if (i == 4) {
                        ll_thu.setVisibility(View.VISIBLE);
                        tv_thu_name.setText(meetingName);
                        tv_thu_time.setText(isToday ? "时间：" + DateUtil.convertHour(startTime) + "-" + DateUtil.convertHour(endTime)
                                : "时间：" + DateUtil.convertHour(startTime) + "-" + DateUtil.secondFormatDateTime(endTime));
                        ssb.append("预约人  ").append(orderName);
                        ssb.setSpan(new BackgroundColorSpan(Color.argb(100, 52, 117, 211)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_thu_ordernamer.setText(ssb);
                    } else if (i == 5) {
                        ll_fri.setVisibility(View.VISIBLE);
                        tv_fri_name.setText(meetingName);
                        tv_fri_time.setText(isToday ? "时间：" + DateUtil.convertHour(startTime) + "-" + DateUtil.convertHour(endTime)
                                : "时间：" + DateUtil.convertHour(startTime) + "-" + DateUtil.secondFormatDateTime(endTime));
                        ssb.append("预约人  ").append(orderName);
                        ssb.setSpan(new BackgroundColorSpan(Color.argb(100, 52, 117, 211)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_fri_ordernamer.setText(ssb);
                    } else if (i == 6) {
                        ll_sat.setVisibility(View.VISIBLE);
                        tv_sat_name.setText(meetingName);
                        tv_sat_time.setText(isToday ? "时间：" + DateUtil.convertHour(startTime) + "-" + DateUtil.convertHour(endTime)
                                : "时间：" + DateUtil.convertHour(startTime) + "-" + DateUtil.secondFormatDateTime(endTime));
                        ssb.append("预约人  ").append(orderName);
                        ssb.setSpan(new BackgroundColorSpan(Color.argb(100, 52, 117, 211)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_sat_ordernamer.setText(ssb);
                    }
                }
            }
        }
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
        spRoomadApter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, rooms);
        spRoomadApter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public void updateMeetings() {
        LogUtils.i("updateMeetings");
        Map<String, Calendar> map = new HashMap<>();
        for (int i = 0; i < presenter.allMeetings.size(); i++) {
            InterfaceMeet.pbui_Item_MeetMeetInfo item = presenter.allMeetings.get(i);
            long sTime = item.getStartTime();
            long eTime = item.getEndTime();
            String meetingName = item.getName().toStringUtf8();
            String ordername = item.getOrdername().toStringUtf8();
            int year = Integer.parseInt(DateUtil.convertYear(sTime));
            int month = Integer.parseInt(DateUtil.convertMonth(sTime));
            int day = Integer.parseInt(DateUtil.convertDay(sTime));
            map.put(getSchemeCalendar(year, month, day, 0xFF40db25, meetingName).toString(),
                    getSchemeCalendar(year, month, day, 0xFF40db25, item));
        }
        calendar_view.setSchemeDate(map);
        onViewChange(mIsMonthView);
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, InterfaceMeet.pbui_Item_MeetMeetInfo item) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(item.getName().toStringUtf8());
        List<Calendar.Scheme> schemes = new ArrayList<>();
        Calendar.Scheme scheme = new Calendar.Scheme(Color.GREEN, item.getName().toStringUtf8());
        scheme.setObj(item);
        schemes.add(scheme);
        calendar.setSchemes(schemes);
        return calendar;
    }
}
