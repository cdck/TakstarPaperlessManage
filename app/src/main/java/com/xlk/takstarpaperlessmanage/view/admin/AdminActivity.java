package com.xlk.takstarpaperlessmanage.view.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.base.BaseActivity;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.view.admin.node.AdminChildNode;
import com.xlk.takstarpaperlessmanage.view.admin.node.AdminNodeAdapter;
import com.xlk.takstarpaperlessmanage.view.admin.node.AdminParentNode;
import com.xlk.takstarpaperlessmanage.view.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdminActivity extends BaseActivity<AdminPresenter> implements AdminContract.View {
    private TextView tv_time, tv_date, tv_online;
    private RecyclerView rv_navigation;
    private FrameLayout fl_admin;
    List<BaseNode> allNode = new ArrayList<>();
    private AdminNodeAdapter nodeAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_admin;
    }

    @Override
    protected AdminPresenter initPresenter() {
        return new AdminPresenter(this);
    }

    @Override
    public void initView() {
        rv_navigation = findViewById(R.id.rv_navigation);
        fl_admin = findViewById(R.id.fl_admin);
        tv_time = findViewById(R.id.tv_time);
        tv_date = findViewById(R.id.tv_date);
        tv_online = findViewById(R.id.tv_online);
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
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        updateOnlineStatus();
        jni.modifyContextProperties(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_ROLE_VALUE,
                InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_AdminFace_VALUE);
        initNodeAdapter();
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
            }
        });
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