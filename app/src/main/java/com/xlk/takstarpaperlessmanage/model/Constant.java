package com.xlk.takstarpaperlessmanage.model;

import android.os.Environment;

import com.google.protobuf.ByteString;

import java.nio.charset.Charset;

/**
 * @author Created by xlk on 2021/4/21.
 * @desc
 */
public class Constant {
    public static final String root_dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TakstarPaperlessManage/";
    public static final String logcat_dir = root_dir + "logcat";
    public static final String crash_dir = root_dir + "crash";

    public static final int RESOURCE_ID_0 = 0;
    public static final int RESOURCE_ID_1 = 1;
    public static final int RESOURCE_ID_2 = 2;
    public static final int RESOURCE_ID_3 = 3;
    public static final int RESOURCE_ID_4 = 4;
    public static final int RESOURCE_ID_10 = 10;
    public static final int RESOURCE_ID_11 = 11;

    public static final int SCREEN_SUB_ID = 2;
    public static final int CAMERA_SUB_ID = 3;

    /**
     * 后台控制端
     */
    //系统设置
    public static final int admin_system_settings = 10000;
    public static final int device_management = admin_system_settings + 1;
    public static final int meeting_room_management = admin_system_settings + 2;
    public static final int seat_arrangement = admin_system_settings + 3;
    public static final int secretary_management = admin_system_settings + 4;
    public static final int commonly_participant = admin_system_settings + 5;
    public static final int other_setting = admin_system_settings + 6;
    //会议预约
    public static final int admin_meeting_reservation = 20000;
    public static final int meeting_reservation = admin_meeting_reservation + 1;
    //会前设置
    public static final int admin_before_meeting = 30000;
    public static final int meeting_management = admin_before_meeting + 1;
    public static final int meeting_agenda = admin_before_meeting + 2;
    public static final int meeting_member = admin_before_meeting + 3;
    public static final int meeting_material = admin_before_meeting + 4;
    public static final int camera_management = admin_before_meeting + 5;
    public static final int vote_entry = admin_before_meeting + 6;
    public static final int election_entry = admin_before_meeting + 7;
    public static final int score_entry = admin_before_meeting + 8;
    public static final int seat_bind = admin_before_meeting + 9;
    public static final int table_display = admin_before_meeting + 10;
    public static final int meeting_function = admin_before_meeting + 11;
    //会中管理
    public static final int admin_current_meeting = 40000;
    public static final int device_control = admin_current_meeting + 1;
    public static final int vote_management = admin_current_meeting + 2;
    public static final int election_management = admin_current_meeting + 3;
    public static final int score_management = admin_current_meeting + 4;
    public static final int meeting_chat = admin_current_meeting + 5;
    public static final int camera_control = admin_current_meeting + 6;
    public static final int screen_management = admin_current_meeting + 7;
    public static final int meeting_minutes = admin_current_meeting + 8;
    //会后查看
    public static final int admin_after_meeting = 50000;
    public static final int sign_in_info = admin_after_meeting + 1;
    public static final int annotation_view = admin_after_meeting + 2;
    public static final int vote_result = admin_after_meeting + 3;
    public static final int election_result = admin_after_meeting + 4;
    public static final int meeting_archive = admin_after_meeting + 5;
    public static final int meeting_statistics = admin_after_meeting + 6;
    public static final int score_view = admin_after_meeting + 7;
    public static final int video_management = admin_after_meeting + 8;


    /**
     * String 转 ByteString
     *
     * @param str 字符串
     * @return com.google.protobuf.ByteString
     */
    public static ByteString s2b(String str) {
        return ByteString.copyFrom(str, Charset.forName("UTF-8"));
    }
}
