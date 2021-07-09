package com.xlk.takstarpaperlessmanage.view.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.base.BaseActivity;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;
import com.xlk.takstarpaperlessmanage.util.PopUtil;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.attendee.AttendeeFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.devicemanage.DeviceManageFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.other.OtherFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.roommanage.RoomManageFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.seatsort.SeatSortFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.a_setup.secretary.SecretaryManageFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.b_reservation.ReserveFragment;

import com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.meet.MeetingManageFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.agenda.AgendaFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.member.MemberFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.material.MaterialFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.camera.CameraFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.rate.RateFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.vote.VoteFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.bind.SeatBindFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.table.TableFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.c_pre.function.FunctionFragment;

import com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.camera.CameraControlFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.chat.ChatFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.device.DeviceControlFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.rate.RateManageFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.screen.ScreenManageFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.vote.VoteManageFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.annotate.AnnotateFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.archive.ArchiveFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.sign.SignFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.statistical.StatisticsFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.video.VideoManageFragment;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.vote.VoteResultFragment;
import com.xlk.takstarpaperlessmanage.view.admin.node.AdminChildNode;
import com.xlk.takstarpaperlessmanage.view.admin.node.AdminNodeAdapter;
import com.xlk.takstarpaperlessmanage.view.admin.node.AdminParentNode;
import com.xlk.takstarpaperlessmanage.view.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdminActivity extends BaseActivity<AdminPresenter> implements AdminContract.View {
    private TextView tv_temp_use;
    private TextView tv_time, tv_date, tv_online, tv_navigation_parent, tv_navigation_child;
    private RecyclerView rv_navigation;
    private FrameLayout fl_admin;
    List<BaseNode> allNode = new ArrayList<>();
    private AdminNodeAdapter nodeAdapter;
    /**
     * 当前选中的功能码
     */
    private int currentFunctionCode = -1;
    private RelativeLayout rl_welcome;
    private LinearLayout ll_content;
    private DeviceManageFragment deviceManageFragment;
    private RoomManageFragment roomManageFragment;
    private SeatSortFragment seatSortFragment;
    private SecretaryManageFragment secretaryManageFragment;
    private AttendeeFragment attendeeFragment;
    private OtherFragment otherFragment;
    private ReserveFragment reserveFragment;
    private MeetingManageFragment meetingManageFragment;
    private AgendaFragment agendaFragment;
    private MemberFragment memberFragment;
    private MaterialFragment materialFragment;
    private CameraFragment cameraFragment;
    private VoteFragment voteFragment;
    private RateFragment rateFragment;
    private SeatBindFragment seatBindFragment;
    private TableFragment tableFragment;
    private FunctionFragment functionFragment;
    private DeviceControlFragment deviceControlFragment;
    private VoteManageFragment voteManageFragment;
    private RateManageFragment rateManageFragment;
    private ChatFragment chatFragment;
    private CameraControlFragment cameraControlFragment;
    private ScreenManageFragment screenManageFragment;
    private SignFragment signFragment;
    private AnnotateFragment annotateFragment;
    private VoteResultFragment voteResultFragment;
    private ArchiveFragment archiveFragment;
    private VideoManageFragment videoManageFragment;
    private StatisticsFragment statisticsFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_admin;
    }

    @Override
    protected AdminPresenter initPresenter() {
        return new AdminPresenter(this, this);
    }

    @Override
    public void initView() {
        rv_navigation = findViewById(R.id.rv_navigation);
        tv_temp_use = findViewById(R.id.tv_temp_use);
        fl_admin = findViewById(R.id.fl_admin);
        tv_time = findViewById(R.id.tv_time);
        tv_date = findViewById(R.id.tv_date);
        tv_online = findViewById(R.id.tv_online);
        rl_welcome = findViewById(R.id.rl_welcome);
        ll_content = findViewById(R.id.ll_content);
        tv_navigation_parent = findViewById(R.id.tv_navigation_parent);
        tv_navigation_child = findViewById(R.id.tv_navigation_child);
        findViewById(R.id.iv_close).setOnClickListener(v -> {
            exitAdmin();
        });
        findViewById(R.id.iv_min).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        exitAdmin();
    }

    private void exitAdmin() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.exit_application_tip)
                .setPositiveButton(R.string.define, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                    System.exit(0);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        updateOnlineStatus();
        jni.modifyContextProperties(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_ROLE_VALUE,
                InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_AdminFace_VALUE);
        initNodeAdapter();
        presenter.updateCurrentMeeting();
        byte[] bytes = jni.queryDevicePropertiesById(InterfaceMacro.Pb_MeetDevicePropertyID.Pb_MEETDEVICE_PROPERTY_NAME_VALUE, GlobalValue.localDeviceId);
        if (bytes != null) {
            try {
                InterfaceDevice.pbui_DeviceStringProperty info = InterfaceDevice.pbui_DeviceStringProperty.parseFrom(bytes);
                LogUtils.i("本机设备名称=" + info.getPropertytext().toStringUtf8() + ",设备ID=" + GlobalValue.localDeviceId);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
        updateNavigationAndFragment();
    }

    @Override
    public void updateMeetingName(String meetingName) {
        tv_temp_use.setText(meetingName);
    }

    private void initNodeAdapter() {
        allNode.clear();
        AdminParentNode parentNode0 = new AdminParentNode(Constant.admin_system_settings);
        List<BaseNode> childNodes0 = new ArrayList<>();
        childNodes0.add(new AdminChildNode(Constant.device_management));
        childNodes0.add(new AdminChildNode(Constant.meeting_room_management));
        childNodes0.add(new AdminChildNode(Constant.seat_arrangement));
        childNodes0.add(new AdminChildNode(Constant.secretary_management));
        childNodes0.add(new AdminChildNode(Constant.commonly_participant));
        childNodes0.add(new AdminChildNode(Constant.other_setting));
        parentNode0.setChildNodes(childNodes0);

        AdminParentNode parentNode1 = new AdminParentNode(Constant.admin_meeting_reservation);
        List<BaseNode> childNodes1 = new ArrayList<>();
        childNodes1.add(new AdminChildNode(Constant.meeting_reservation));
        parentNode1.setChildNodes(childNodes1);

        AdminParentNode parentNode2 = new AdminParentNode(Constant.admin_before_meeting);
        List<BaseNode> childNodes2 = new ArrayList<>();
        childNodes2.add(new AdminChildNode(Constant.meeting_management));
        childNodes2.add(new AdminChildNode(Constant.meeting_agenda));
        childNodes2.add(new AdminChildNode(Constant.meeting_member));
        childNodes2.add(new AdminChildNode(Constant.meeting_material));
        childNodes2.add(new AdminChildNode(Constant.camera_management));
        childNodes2.add(new AdminChildNode(Constant.vote_entry));
        childNodes2.add(new AdminChildNode(Constant.election_entry));
        childNodes2.add(new AdminChildNode(Constant.score_entry));
        childNodes2.add(new AdminChildNode(Constant.seat_bind));
        childNodes2.add(new AdminChildNode(Constant.table_display));
        childNodes2.add(new AdminChildNode(Constant.meeting_function));
        parentNode2.setChildNodes(childNodes2);

        AdminParentNode parentNode3 = new AdminParentNode(Constant.admin_current_meeting);
        List<BaseNode> childNodes3 = new ArrayList<>();
        childNodes3.add(new AdminChildNode(Constant.device_control));
        childNodes3.add(new AdminChildNode(Constant.vote_management));
        childNodes3.add(new AdminChildNode(Constant.election_management));
        childNodes3.add(new AdminChildNode(Constant.score_management));
        childNodes3.add(new AdminChildNode(Constant.meeting_chat));
        childNodes3.add(new AdminChildNode(Constant.camera_control));
        childNodes3.add(new AdminChildNode(Constant.screen_management));
        childNodes3.add(new AdminChildNode(Constant.meeting_minutes));
        parentNode3.setChildNodes(childNodes3);

        AdminParentNode parentNode4 = new AdminParentNode(Constant.admin_after_meeting);
        List<BaseNode> childNodes4 = new ArrayList<>();
        childNodes4.add(new AdminChildNode(Constant.sign_in_info));
        childNodes4.add(new AdminChildNode(Constant.annotation_view));
        childNodes4.add(new AdminChildNode(Constant.vote_result));
        childNodes4.add(new AdminChildNode(Constant.election_result));
        childNodes4.add(new AdminChildNode(Constant.meeting_archive));
        childNodes4.add(new AdminChildNode(Constant.meeting_statistics));
        childNodes4.add(new AdminChildNode(Constant.score_view));
        childNodes4.add(new AdminChildNode(Constant.video_management));
        parentNode4.setChildNodes(childNodes4);

        allNode.add(parentNode0);
        allNode.add(parentNode1);
        allNode.add(parentNode2);
        allNode.add(parentNode3);
        allNode.add(parentNode4);

        nodeAdapter = new AdminNodeAdapter(allNode);
        rv_navigation.setLayoutManager(new LinearLayoutManager(this));
        rv_navigation.setAdapter(nodeAdapter);
        nodeAdapter.setNodeClickListener(new AdminNodeAdapter.NodeClickListener() {
            @Override
            public void onClick(int id) {
                LogUtils.i("点击的功能id=" + id);
                nodeAdapter.notifyDataSetChanged();
                currentFunctionCode = id;
                updateNavigationAndFragment();
            }
        });
    }

    private void updateNavigationAndFragment() {
        if (currentFunctionCode == -1) {
            rl_welcome.setVisibility(View.VISIBLE);
            ll_content.setVisibility(View.GONE);
            return;
        }
        rl_welcome.setVisibility(View.GONE);
        ll_content.setVisibility(View.VISIBLE);
        String navigation_parent = "";
        String navigation_child = "";
        if (currentFunctionCode > Constant.admin_after_meeting) {
            //会后查看
            navigation_parent = getString(R.string.after_meeting);
            switch (currentFunctionCode) {
                case Constant.sign_in_info:
                    navigation_child = getString(R.string.sign_in_info);
                    break;
                case Constant.annotation_view:
                    navigation_child = getString(R.string.annotation_view);
                    break;
                case Constant.vote_result:
                    navigation_child = getString(R.string.vote_result);
                    break;
                case Constant.election_result:
                    navigation_child = getString(R.string.election_result);
                    break;
                case Constant.meeting_archive:
                    navigation_child = getString(R.string.meeting_archive);
                    break;
                case Constant.meeting_statistics:
                    navigation_child = getString(R.string.meeting_statistics);
                    break;
                case Constant.score_view:
                    navigation_child = getString(R.string.score_view);
                    break;
                case Constant.video_management:
                    navigation_child = getString(R.string.video_management);
                    break;
            }
        } else if (currentFunctionCode > Constant.admin_current_meeting) {
            //会中管理
            navigation_parent = getString(R.string.current_meeting);
            switch (currentFunctionCode) {
                case Constant.device_control:
                    navigation_child = getString(R.string.device_control);
                    break;
                case Constant.vote_management:
                    navigation_child = getString(R.string.vote_management);
                    break;
                case Constant.election_management:
                    navigation_child = getString(R.string.election_management);
                    break;
                case Constant.score_management:
                    navigation_child = getString(R.string.score_management);
                    break;
                case Constant.meeting_chat:
                    navigation_child = getString(R.string.meeting_chat);
                    break;
                case Constant.camera_control:
                    navigation_child = getString(R.string.camera_control);
                    break;
                case Constant.screen_management:
                    navigation_child = getString(R.string.screen_management);
                    break;
                case Constant.meeting_minutes:
                    navigation_child = getString(R.string.meeting_minutes);
                    break;
            }
        } else if (currentFunctionCode > Constant.admin_before_meeting) {
            //会前设置
            navigation_parent = getString(R.string.before_meeting);
            switch (currentFunctionCode) {
                case Constant.meeting_management:
                    navigation_child = getString(R.string.meeting_management);
                    break;
                case Constant.meeting_agenda:
                    navigation_child = getString(R.string.meeting_agenda);
                    break;
                case Constant.meeting_member:
                    navigation_child = getString(R.string.meeting_member);
                    break;
                case Constant.meeting_material:
                    navigation_child = getString(R.string.meeting_material);
                    break;
                case Constant.camera_management:
                    navigation_child = getString(R.string.camera_management);
                    break;
                case Constant.vote_entry:
                    navigation_child = getString(R.string.vote_entry);
                    break;
                case Constant.election_entry:
                    navigation_child = getString(R.string.election_entry);
                    break;
                case Constant.score_entry:
                    navigation_child = getString(R.string.score_entry);
                    break;
                case Constant.seat_bind:
                    navigation_child = getString(R.string.seat_bind);
                    break;
                case Constant.table_display:
                    navigation_child = getString(R.string.table_display);
                    break;
                case Constant.meeting_function:
                    navigation_child = getString(R.string.meeting_function);
                    break;
            }
        } else if (currentFunctionCode > Constant.admin_meeting_reservation) {
            //会议预约
            navigation_parent = getString(R.string.meeting_reservation);
            switch (currentFunctionCode) {
                case Constant.meeting_reservation:
                    navigation_child = getString(R.string.meeting_reservation);
                    break;
            }
        } else if (currentFunctionCode > Constant.admin_system_settings) {
            //系统设置
            navigation_parent = getString(R.string.system_settings);
            switch (currentFunctionCode) {
                case Constant.device_management:
                    navigation_child = getString(R.string.device_management);
                    break;
                case Constant.meeting_room_management:
                    navigation_child = getString(R.string.meeting_room_management);
                    break;
                case Constant.seat_arrangement:
                    navigation_child = getString(R.string.seat_arrangement);
                    break;
                case Constant.secretary_management:
                    navigation_child = getString(R.string.secretary_management);
                    break;
                case Constant.commonly_participant:
                    navigation_child = getString(R.string.commonly_participant);
                    break;
                case Constant.other_setting:
                    navigation_child = getString(R.string.other_setting);
                    break;
            }
        }
        tv_navigation_parent.setText(navigation_parent);
        tv_navigation_child.setText(navigation_child);
        showFragment(currentFunctionCode);
    }

    private void showFragment(int functionCode) {
        LogUtils.i("showFragment functionCode=" + functionCode);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        hideFragment(ft);
        switch (functionCode) {
            //设备管理
            case Constant.device_management: {
                if (deviceManageFragment == null) {
                    deviceManageFragment = new DeviceManageFragment();
                    ft.add(R.id.fl_admin, deviceManageFragment);
                }
                ft.show(deviceManageFragment);
                break;
            }
            //会议室管理
            case Constant.meeting_room_management: {
                if (roomManageFragment == null) {
                    roomManageFragment = new RoomManageFragment();
                    ft.add(R.id.fl_admin, roomManageFragment);
                }
                ft.show(roomManageFragment);
                break;
            }
            //座位排布
            case Constant.seat_arrangement: {
                if (seatSortFragment == null) {
                    seatSortFragment = new SeatSortFragment();
                    ft.add(R.id.fl_admin, seatSortFragment);
                }
                ft.show(seatSortFragment);
                break;
            }
            //秘书管理
            case Constant.secretary_management: {
                if (secretaryManageFragment == null) {
                    secretaryManageFragment = new SecretaryManageFragment();
                    ft.add(R.id.fl_admin, secretaryManageFragment);
                }
                ft.show(secretaryManageFragment);
                break;
            }
            case Constant.commonly_participant: {
                if (attendeeFragment == null) {
                    attendeeFragment = new AttendeeFragment();
                    ft.add(R.id.fl_admin, attendeeFragment);
                }
                ft.show(attendeeFragment);
                break;
            }
            case Constant.other_setting: {
                if (otherFragment == null) {
                    otherFragment = new OtherFragment();
                    ft.add(R.id.fl_admin, otherFragment);
                }
                ft.show(otherFragment);
                break;
            }
            case Constant.meeting_reservation: {
                if (reserveFragment == null) {
                    reserveFragment = new ReserveFragment();
                    ft.add(R.id.fl_admin, reserveFragment);
                }
                ft.show(reserveFragment);
                break;
            }
            case Constant.meeting_management: {
                if (meetingManageFragment == null) {
                    meetingManageFragment = new MeetingManageFragment();
                    ft.add(R.id.fl_admin, meetingManageFragment);
                }
                ft.show(meetingManageFragment);
                break;
            }
            case Constant.meeting_agenda: {
                if (agendaFragment == null) {
                    agendaFragment = new AgendaFragment();
                    ft.add(R.id.fl_admin, agendaFragment);
                }
                ft.show(agendaFragment);
                break;
            }
            case Constant.meeting_member: {
                if (memberFragment == null) {
                    memberFragment = new MemberFragment();
                    ft.add(R.id.fl_admin, memberFragment);
                }
                ft.show(memberFragment);
                break;
            }
            case Constant.meeting_material: {
                if (materialFragment == null) {
                    materialFragment = new MaterialFragment();
                    ft.add(R.id.fl_admin, materialFragment);
                }
                ft.show(materialFragment);
                break;
            }
            case Constant.camera_management: {
                if (cameraFragment == null) {
                    cameraFragment = new CameraFragment();
                    ft.add(R.id.fl_admin, cameraFragment);
                }
                ft.show(cameraFragment);
                break;
            }
            case Constant.vote_entry: {
                Bundle bundle = new Bundle();
                bundle.putInt("vote_type", InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE);
                if (voteFragment == null) {
                    voteFragment = new VoteFragment();
                    ft.add(R.id.fl_admin, voteFragment);
                }
                voteFragment.setArguments(bundle);
                ft.show(voteFragment);
                break;
            }
            case Constant.election_entry: {
                Bundle bundle = new Bundle();
                bundle.putInt("vote_type", InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_election_VALUE);
                if (voteFragment == null) {
                    voteFragment = new VoteFragment();
                    ft.add(R.id.fl_admin, voteFragment);
                }
                voteFragment.setArguments(bundle);
                ft.show(voteFragment);
                break;
            }
            //评分录入
            case Constant.score_entry: {
                if (rateFragment == null) {
                    rateFragment = new RateFragment();
                    ft.add(R.id.fl_admin, rateFragment);
                }
                ft.show(rateFragment);
                break;
            }
            case Constant.seat_bind: {
                if (seatBindFragment == null) {
                    seatBindFragment = new SeatBindFragment();
                    ft.add(R.id.fl_admin, seatBindFragment);
                }
                ft.show(seatBindFragment);
                break;
            }
            case Constant.table_display: {
                if (tableFragment == null) {
                    tableFragment = new TableFragment();
                    ft.add(R.id.fl_admin, tableFragment);
                }
                ft.show(tableFragment);
                break;
            }
            case Constant.meeting_function: {
                if (functionFragment == null) {
                    functionFragment = new FunctionFragment();
                    ft.add(R.id.fl_admin, functionFragment);
                }
                ft.show(functionFragment);
                break;
            }
            case Constant.device_control: {
                if (deviceControlFragment == null) {
                    deviceControlFragment = new DeviceControlFragment();
                    ft.add(R.id.fl_admin, deviceControlFragment);
                }
                ft.show(deviceControlFragment);
                break;
            }
            case Constant.vote_management: {
                Bundle bundle = new Bundle();
                bundle.putInt("vote_type", InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE);
                if (voteManageFragment == null) {
                    voteManageFragment = new VoteManageFragment();
                    ft.add(R.id.fl_admin, voteManageFragment);
                }
                voteManageFragment.setArguments(bundle);
                ft.show(voteManageFragment);
                break;
            }
            case Constant.election_management: {
                Bundle bundle = new Bundle();
                bundle.putInt("vote_type", InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_election_VALUE);
                if (voteManageFragment == null) {
                    voteManageFragment = new VoteManageFragment();
                    ft.add(R.id.fl_admin, voteManageFragment);
                }
                voteManageFragment.setArguments(bundle);
                ft.show(voteManageFragment);
                break;
            }
            //评分管理
            case Constant.score_management: {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isManage", true);
                if (rateManageFragment == null) {
                    rateManageFragment = new RateManageFragment();
                    ft.add(R.id.fl_admin, rateManageFragment);
                }
                rateManageFragment.setArguments(bundle);
                ft.show(rateManageFragment);
                break;
            }
            case Constant.meeting_chat: {
                if (chatFragment == null) {
                    chatFragment = new ChatFragment();
                    ft.add(R.id.fl_admin, chatFragment);
                }
                ft.show(chatFragment);
                break;
            }
            case Constant.camera_control: {
                if (cameraControlFragment == null) {
                    cameraControlFragment = new CameraControlFragment();
                    ft.add(R.id.fl_admin, cameraControlFragment);
                }
                ft.show(cameraControlFragment);
                break;
            }
            case Constant.screen_management: {
                if (screenManageFragment == null) {
                    screenManageFragment = new ScreenManageFragment();
                    ft.add(R.id.fl_admin, screenManageFragment);
                }
                ft.show(screenManageFragment);
                break;
            }
//            case Constant.meeting_minutes: {
//                break;
//            }
            case Constant.sign_in_info: {
                if (signFragment == null) {
                    signFragment = new SignFragment();
                    ft.add(R.id.fl_admin, signFragment);
                }
                ft.show(signFragment);
                break;
            }
            case Constant.annotation_view: {
                if (annotateFragment == null) {
                    annotateFragment = new AnnotateFragment();
                    ft.add(R.id.fl_admin, annotateFragment);
                }
                ft.show(annotateFragment);
                break;
            }
            case Constant.vote_result: {
                Bundle bundle = new Bundle();
                bundle.putInt("vote_type", InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_vote_VALUE);
                if (voteResultFragment == null) {
                    voteResultFragment = new VoteResultFragment();
                    ft.add(R.id.fl_admin, voteResultFragment);
                }
                voteResultFragment.setArguments(bundle);
                ft.show(voteResultFragment);
                break;
            }
            case Constant.election_result: {
                Bundle bundle = new Bundle();
                bundle.putInt("vote_type", InterfaceMacro.Pb_MeetVoteType.Pb_VOTE_MAINTYPE_election_VALUE);
                if (voteResultFragment == null) {
                    voteResultFragment = new VoteResultFragment();
                    ft.add(R.id.fl_admin, voteResultFragment);
                }
                voteResultFragment.setArguments(bundle);
                ft.show(voteResultFragment);
                break;
            }
            case Constant.meeting_archive: {
                if (archiveFragment == null) {
                    archiveFragment = new ArchiveFragment();
                    ft.add(R.id.fl_admin, archiveFragment);
                }
                ft.show(archiveFragment);
                break;
            }
            //会议统计
            case Constant.meeting_statistics: {
                if (statisticsFragment == null) {
                    statisticsFragment = new StatisticsFragment();
                    ft.add(R.id.fl_admin, statisticsFragment);
                }
                ft.show(statisticsFragment);
                break;
            }
            //评分查看
            case Constant.score_view: {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isManage", false);
                if (rateManageFragment == null) {
                    rateManageFragment = new RateManageFragment();
                    ft.add(R.id.fl_admin, rateManageFragment);
                }
                rateManageFragment.setArguments(bundle);
                ft.show(rateManageFragment);
                break;
            }
            //录像管理
            case Constant.video_management: {
                if (videoManageFragment == null) {
                    videoManageFragment = new VideoManageFragment();
                    ft.add(R.id.fl_admin, videoManageFragment);
                }
                ft.show(videoManageFragment);
                break;
            }
        }
        ft.commitAllowingStateLoss();//允许状态丢失，其他完全一样
    }

    private void hideFragment(FragmentTransaction ft) {
        if (deviceManageFragment != null) ft.hide(deviceManageFragment);
        if (roomManageFragment != null) ft.hide(roomManageFragment);
        if (seatSortFragment != null) ft.hide(seatSortFragment);
        if (secretaryManageFragment != null) ft.hide(secretaryManageFragment);
        if (attendeeFragment != null) ft.hide(attendeeFragment);
        if (otherFragment != null) ft.hide(otherFragment);
        if (reserveFragment != null) ft.hide(reserveFragment);
        if (meetingManageFragment != null) ft.hide(meetingManageFragment);
        if (agendaFragment != null) ft.hide(agendaFragment);
        if (memberFragment != null) ft.hide(memberFragment);
        if (materialFragment != null) ft.hide(materialFragment);
        if (cameraFragment != null) ft.hide(cameraFragment);
        if (voteFragment != null) ft.hide(voteFragment);
        if (rateFragment != null) ft.hide(rateFragment);
        if (seatBindFragment != null) ft.hide(seatBindFragment);
        if (tableFragment != null) ft.hide(tableFragment);
        if (functionFragment != null) ft.hide(functionFragment);
        if (deviceControlFragment != null) ft.hide(deviceControlFragment);
        if (voteManageFragment != null) ft.hide(voteManageFragment);
        if (rateManageFragment != null) ft.hide(rateManageFragment);
        if (chatFragment != null) ft.hide(chatFragment);
        if (cameraControlFragment != null) ft.hide(cameraControlFragment);
        if (screenManageFragment != null) ft.hide(screenManageFragment);
        if (signFragment != null) ft.hide(signFragment);
        if (annotateFragment != null) ft.hide(annotateFragment);
        if (voteResultFragment != null) ft.hide(voteResultFragment);
        if (archiveFragment != null) ft.hide(archiveFragment);
        if (videoManageFragment != null) ft.hide(videoManageFragment);
        if (statisticsFragment != null) ft.hide(statisticsFragment);
    }

    @Override
    public void updateTime(String[] adminTime) {
        tv_time.setText(adminTime[0]);
        tv_date.setText(adminTime[1]);
    }

    @Override
    public void updateOnlineStatus() {
        tv_online.setText(jni.isOnline() ? getString(R.string.online) : getString(R.string.offline));
    }
}