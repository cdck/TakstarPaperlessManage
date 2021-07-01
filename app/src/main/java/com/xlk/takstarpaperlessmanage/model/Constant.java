package com.xlk.takstarpaperlessmanage.model;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.google.protobuf.ByteString;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.xlk.takstarpaperlessmanage.App;
import com.xlk.takstarpaperlessmanage.R;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Created by xlk on 2021/4/21.
 * @desc
 */
public class Constant {
    public static final String root_dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TakstarPaperlessManage/";
    public static final String file_dir = root_dir + "file/";
    public static final String logcat_dir = file_dir + "logcat/";
    public static final String crash_dir = file_dir + "crash/";
    public static final String config_dir = file_dir + "config/";
    public static final String export_dir = file_dir + "export/";
    public static final String record_video_dir = file_dir + "RecordVideo/";

    public static final String DIR_ARCHIVE_TEMP = file_dir + "Conference archive cache directory/";
    public static final String DIR_ARCHIVE_ZIP = file_dir + "Conference archive/";


    /**
     * 导出常用参会人时选取目录
     */
    public static final int CHOOSE_DIR_TYPE_EXPORT_OFTEN_MEMBER = 1;
    /**
     * 导出投票信息文件目录
     */
    public static final int CHOOSE_DIR_TYPE_EXPORT_VOTE = 2;
    /**
     * 导出所有的文件评分
     */
    public static final int CHOOSE_DIR_TYPE_EXPORT_SCORE = 3;
    /**
     * 导出绑定座位
     */
    public static final int CHOOSE_DIR_TYPE_EXPORT_BIND_SEAT = 4;
    /**
     * 导出参会人员
     */
    public static final int CHOOSE_DIR_TYPE_EXPORT_MEMBER = 5;
    /**
     * 导出签到信息PDF
     */
    public static final int CHOOSE_DIR_TYPE_EXPORT_SIGN_IN_PDF = 6;
    /**
     * 导出评分详情
     */
    public static final int CHOOSE_DIR_TYPE_EXPORT_SOCRE_RESULT = 7;


    /**
     * 发送广播时的action和extra
     */
    public static final String ACTION_START_SCREEN_RECORD = "action_start_screen_record";
    public static final String ACTION_STOP_SCREEN_RECORD = "action_stop_screen_record";
    /**
     * 退出应用时发送广播通知停止掉屏幕录制
     */
    public static final String ACTION_STOP_SCREEN_RECORD_WHEN_EXIT_APP = "action_stop_screen_record_when_exit_app";
    /**
     * 要采集的类型，值为2或3，屏幕或摄像头
     */
    public static final String EXTRA_CAPTURE_TYPE = "extra_capture_type";


    /**
     * 主界面背景图
     */
    public static final String MAIN_BG_PNG_TAG = "main_bg";
    /**
     * 主界面logo图标
     */
    public static final String MAIN_LOGO_PNG_TAG = "main_logo";
    /**
     * 会议界面背景图
     */
    public static final String MEET_BG_PNG_TAG = "meet_bg";
    /**
     * 会议室背景图
     */
    public static final String ROOM_BG_PNG_TAG = "room_bg";
    /**
     * 公告背景图
     */
    public static final String NOTICE_BG_PNG_TAG = "notice_bg";
    /**
     * 公告logo图标
     */
    public static final String NOTICE_LOGO_PNG_TAG = "notice_logo";
    /**
     * 投影界面背景图
     */
    public static final String PROJECTIVE_BG_PNG_TAG = "projective_bg";
    /**
     * 投影界面logo图标
     */
    public static final String PROJECTIVE_LOGO_PNG_TAG = "projective_logo";
    /**
     * 下载桌牌背景图片
     */
    public static final String DOWNLOAD_TABLE_CARD_BG = "download_table_card_bg";

    /**
     * 下载标识：下载无进度通知
     */
    public static final String DOWNLOAD_NO_INFORM = "download_no_inform";
    public static final String DOWNLOAD_OPEN_FILE = "download_open_file";
    public static final String DOWNLOAD_NORMAL = "download_normal";
    /**
     * 下载录制视频
     */
    public static final String DOWNLOAD_RECORD_VIDEO = "download_record_video";

    /**
     * 归档文件时的下载标识
     */
    public static final String ARCHIVE_DOWNLOAD_FILE = "archive_download_file";
    /**
     * 归档议程文件下载标识
     */
    public static final String ARCHIVE_AGENDA_FILE = "archive_agenda_file";
    /**
     * 归档共享文件下载标识
     */
    public static final String ARCHIVE_SHARE_FILE = "archive_share_file";
    /**
     * 归档批注文件下载标识
     */
    public static final String ARCHIVE_ANNOTATION_FILE = "archive_annotation_file";
    /**
     * 归档会议资料下载标识
     */
    public static final String ARCHIVE_MEET_DATA_FILE = "archive_meet_data_file";

    //上传文件时的标识
    public static final String UPLOAD_CHOOSE_FILE = "upload_choose_file";
    public static final String UPLOAD_DRAW_PIC = "upload_draw_pic";
    /**
     * wps操作保存时上传的文件
     */
    public static final String UPLOAD_WPS_FILE = "upload_wps_file";
    /**
     * 上传背景图片时的标识
     */
    public static final String UPLOAD_BACKGROUND_IMAGE = "upload_background_image";
    /**
     * 上传桌牌背景图片时的标识
     */
    public static final String UPLOAD_TABLE_CARD_BACKGROUND_IMAGE = "upload_table_card_background_image";
    /**
     * 上传会议发布文件
     */
    public static final String UPLOAD_PUBLISH_FILE = "upload_publish_file";
    /**
     * 上传升级文件
     */
    public static final String UPLOAD_UPGRADE_FILE = "upload_upgrade_file";
    /**
     * 上传议程文件
     */
    public static final String UPLOAD_AGENDA_FILE = "upload_agenda_file";
    /**
     * 上传评分文件
     */
    public static final String UPLOAD_SCORE_FILE = "upload_score_file";


    /**
     * 发起播放的类型
     */
    public static final String EXTRA_VIDEO_ACTION = "extra_video_action";
    /**
     * 发起播放的设备ID
     */
    public static final String EXTRA_VIDEO_DEVICE_ID = "extra_video_device_id";
    /**
     * 发起播放的设备id流通道
     */
    public static final String EXTRA_VIDEO_SUB_ID = "extra_video_sub_id";
    /**
     * 发起播放的文件类型
     */
    public static final String EXTRA_VIDEO_SUBTYPE = "extra_video_subtype";
    /**
     * 播放的媒体id
     */
    public static final String EXTRA_VIDEO_MEDIA_ID = "extra_video_media_id";


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
     * 共享目录固定id
     */
    public static final int SHARE_DIR_ID = 1;
    /**
     * 批注目录固定id
     */
    public static final int ANNOTATION_DIR_ID = 2;

    /**
     * 后台控制端功能码
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



    /* **** **  权限码  ** **** */
    /**
     * 同屏权限
     */
    public static final int permission_code_screen = InterfaceMacro.Pb_MemberPermissionPropertyID.Pb_memperm_sscreen_VALUE;
    /**
     * 投影权限
     */
    public static final int permission_code_projection = InterfaceMacro.Pb_MemberPermissionPropertyID.Pb_memperm_projective_VALUE;
    /**
     * 上传权限
     */
    public static final int permission_code_upload = InterfaceMacro.Pb_MemberPermissionPropertyID.Pb_memperm_upload_VALUE;
    /**
     * 下载权限
     */
    public static final int permission_code_download = InterfaceMacro.Pb_MemberPermissionPropertyID.Pb_memperm_download_VALUE;
    /**
     * 投票权限
     */
    public static final int permission_code_vote = InterfaceMacro.Pb_MemberPermissionPropertyID.Pb_memperm_vote_VALUE;

    /**
     * String 转 ByteString
     *
     * @param str 字符串
     * @return com.google.protobuf.ByteString
     */
    public static ByteString s2b(String str) {
        return ByteString.copyFrom(str, Charset.forName("UTF-8"));
    }

    /**
     * 判断权限码是否有某一权限
     *
     * @param permission 当前权限
     * @param code       某一权限的权限码
     */
    public static boolean isHasPermission(int permission, int code) {
        return (permission & code) == code;
    }

    /**
     * 获取会议功能名称
     *
     * @param context 上下文
     * @param funCode 会议功能码
     */
    public static String getFunctionString(Context context, int funCode) {
        switch (funCode) {
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_AGENDA_BULLETIN_VALUE:
                return context.getString(R.string.meeting_agenda);
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_MATERIAL_VALUE:
                return context.getString(R.string.meeting_material);
//            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_SHAREDFILE_VALUE:
//                return context.getString(R.string.meeting_shared_file);
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_POSTIL_VALUE:
                return context.getString(R.string.meeting_annotation_file);
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_MESSAGE_VALUE:
                return context.getString(R.string.meeting_chat);
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_VIDEOSTREAM_VALUE:
                return context.getString(R.string.meeting_live_video);
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WHITEBOARD_VALUE:
                return context.getString(R.string.meeting_art_board);
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WEBBROWSER_VALUE:
                return context.getString(R.string.meeting_web_browsing);
//            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_VOTERESULT_VALUE:
//                return context.getString(R.string.meeting_questionnaire);
            case InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_SIGNINRESULT_VALUE:
                return context.getString(R.string.meeting_sign_in_information);
            default:
                return context.getString(R.string.unrecognized) + funCode;
        }
    }

    /**
     * 获取会议签到类型名称
     *
     * @param type InterfaceMacro.Pb_MeetSignType
     */
    public static String getMeetSignInTypeName(int type) {
        switch (type) {
            case InterfaceMacro.Pb_MeetSignType.Pb_signin_psw_VALUE:
                return App.appContext.getString(R.string.personal_password_signin);
            case InterfaceMacro.Pb_MeetSignType.Pb_signin_photo_VALUE:
                return App.appContext.getString(R.string.handwriting_signin);
            case InterfaceMacro.Pb_MeetSignType.Pb_signin_onepsw_VALUE:
                return App.appContext.getString(R.string.meeting_password_signin);
            default:
                return App.appContext.getString(R.string.direct_signin);
        }
    }

    /**
     * 获取参会人身份名称
     *
     * @param context 上下文
     * @param role    身份代码  InterfaceMacro.Pb_MeetMemberRole
     * @return 秘书、管理员...
     */
    public static String getMemberRoleName(Context context, int role) {
        switch (role) {
            case InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_normal_VALUE:
                return context.getString(R.string.member_role_ordinary);
            case InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_compere_VALUE:
                return context.getString(R.string.member_role_host);
            case InterfaceMacro.Pb_MeetMemberRole.Pb_role_member_secretary_VALUE:
                return context.getString(R.string.member_role_secretary);
            case InterfaceMacro.Pb_MeetMemberRole.Pb_role_admin_VALUE:
                return context.getString(R.string.member_role_admin);
            default:
                return context.getString(R.string.none);
        }
    }

    /**
     * 获取会议状态
     *
     * @param context 上下文
     * @param status  会议状态码
     * @return 未开始、正在进行、已结束、暂停中、模板会议
     */
    public static String getMeetingStatus(Context context, int status) {
        switch (status) {
            case InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_Ready_VALUE: {
                return context.getString(R.string.meeting_status_ready);
            }
            case InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_Start_VALUE: {
                return context.getString(R.string.meeting_status_start);
            }
            case InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_End_VALUE: {
                return context.getString(R.string.meeting_status_end);
            }
            case InterfaceMacro.Pb_MeetStatus.Pb_MEETING_STATUS_PAUSE_VALUE: {
                return context.getString(R.string.meeting_status_pause);
            }
            default:
                return context.getString(R.string.meeting_status_model);
        }
    }

    /**
     * 获取该设备的类型名称
     *
     * @return 返回空表示是未识别的设备（服务器设备）
     */
    public static String getDeviceTypeName(Context context, int deviceId) {
        if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetDBServer_VALUE, deviceId)) {
            //会议数据库设备
            return context.getString(R.string.database_dev);
        } else if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetService_VALUE, deviceId)) {
            //会议茶水设备
            return context.getString(R.string.tea_dev);
        } else if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetProjective_VALUE, deviceId)) {
            //会议投影设备
            return context.getString(R.string.pro_dev);
        } else if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetCapture_VALUE, deviceId)) {
            //会议流采集设备
            return context.getString(R.string.capture_dev);
        } else if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetClient_VALUE, deviceId)) {
            //会议终端设备
            return context.getString(R.string.client_dev);
        } else if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetVideoClient_VALUE, deviceId)) {
            //会议视屏对讲客户端
            return context.getString(R.string.video_chat_dev);
        } else if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetPublish_VALUE, deviceId)) {
            //会议发布
            return context.getString(R.string.release_dev);
        } else if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DEVICE_MEET_PHPCLIENT_VALUE, deviceId)) {
            //PHP中转数据设备
        } else if (isThisDevType(InterfaceMacro.Pb_DeviceIDType.Pb_DeviceIDType_MeetShare_VALUE, deviceId)) {
            //会议一键同屏设备
            return context.getString(R.string.screen_dev);
        }
        return "";
    }

    public static final int DEVICE_MEET_ID_MASK = 0xfffc0000;

    /**
     * 判断设备是否是当前{@param type}类型
     *
     * @param type  类型
     * @param devId 设备ID
     * @see InterfaceMacro.Pb_DeviceIDType
     */
    public static boolean isThisDevType(int type, int devId) {
        return (devId & Constant.DEVICE_MEET_ID_MASK) == type;
    }

    /**
     * 获取操作界面名称
     */
    public static String getInterfaceStateName(Context context, int state) {
        if (state == InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MainFace_VALUE) {
            return context.getString(R.string.face_main);
        } else if (state == InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MemFace_VALUE) {
            return context.getString(R.string.face_meet);
        } else if (state == InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_AdminFace_VALUE) {
            return context.getString(R.string.face_back);
        } else if (state == InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_VoteFace_VALUE) {
            return context.getString(R.string.face_vote);
        }
        return "";
    }

    /**
     * 判断文件是否为发布文件
     *
     * @param attrib 文件属性id
     * @return
     */
    public static boolean isReleaseFile(int attrib) {
        return (attrib & InterfaceMacro.Pb_MeetFileAttrib.Pb_MEETFILE_ATTRIB_PUBLISH_VALUE)
                == InterfaceMacro.Pb_MeetFileAttrib.Pb_MEETFILE_ATTRIB_PUBLISH_VALUE;
    }

    /**
     * 计算字符串的md5值
     *
     * @param string 字符串
     */
    public static String s2md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String parseAscii(String str) {
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();
        for (int i = 0; i < bs.length; i++)
            sb.append(toHex(bs[i]));
        return sb.toString();
    }

    public static String toHex(int n) {
        StringBuilder sb = new StringBuilder();
        if (n / 16 == 0) {
            return toHexUtil(n);
        } else {
            String t = toHex(n / 16);
            int nn = n % 16;
            sb.append(t).append(toHexUtil(nn));
        }
        return sb.toString();
    }

    private static String toHexUtil(int n) {
        String rt = "";
        switch (n) {
            case 10:
                rt += "A";
                break;
            case 11:
                rt += "B";
                break;
            case 12:
                rt += "C";
                break;
            case 13:
                rt += "D";
                break;
            case 14:
                rt += "E";
                break;
            case 15:
                rt += "F";
                break;
            default:
                rt += n;
        }
        return rt;
    }

    /**
     * 获取投票的状态
     */
    public static String getVoteState(Context context, int votestate) {
        if (votestate == InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_notvote_VALUE) {
            return context.getString(R.string.vote_state_not_initiated);
        } else if (votestate == InterfaceMacro.Pb_MeetVoteStatus.Pb_vote_voteing_VALUE) {
            return context.getString(R.string.vote_state_ongoing);
        } else {
            return context.getString(R.string.vote_state_has_ended);
        }
    }

    /**
     * 获取投票信息
     *
     * @param context
     * @param type
     * @return
     */
    public static String getVoteType(Context context, int type) {
        String[] stringArray = context.getResources().getStringArray(R.array.vote_type_spinner);
        return stringArray[type];
//        switch (type) {
//            case InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_SINGLE_VALUE: {
//                return context.getString(R.string.vote_type_1);
//            }
//            case InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_4_5_VALUE: {
//                return context.getString(R.string.vote_type_2);
//            }
//            case InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_3_5_VALUE: {
//                return context.getString(R.string.vote_type_3);
//            }
//            case InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_2_5_VALUE: {
//                return context.getString(R.string.vote_type_4);
//            }
//            case InterfaceMacro.Pb_MeetVote_SelType.Pb_VOTE_TYPE_2_3_VALUE: {
//                return context.getString(R.string.vote_type_5);
//            }
//            default:
//                return context.getString(R.string.vote_type_0);
//        }
    }

    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    //编码类型
    /**
     * VP8 video (i.e. video in .webm)
     */
    public static final String MIME_VIDEO_VP8 = "video/x-vnd.on2.vp8";
    /**
     * VP9 video (i.e. video in .webm)
     */
    public static final String MIME_VIDEO_VP9 = "video/x-vnd.on2.vp9";
    /**
     * H.263 video
     */
    public static final String MIME_VIDEO_3GPP = "video/3gpp";
    /**
     * SCREEN_HEIGHT.264/AVC video
     */
    public static final String MIME_VIDEO_AVC = "video/avc";

    /**
     * SCREEN_HEIGHT.265/HEVC video
     */
    public static final String MIME_VIDEO_HEVC = "video/hevc";
    /**
     * MPEG4 video
     */
    public static final String MIME_VIDEO_MPEG4 = "video/mp4v-es";

    /**
     * 获取指定的MimeType
     *
     * @param codecId 后台回调ID
     * @return MimeType
     */
    public static String getMimeType(int codecId) {
        switch (codecId) {
            case 12:
            case 13:
                return MIME_VIDEO_MPEG4;
            case 139:
            case 140:
                return MIME_VIDEO_VP8;
            case 167:
            case 168:
                return MIME_VIDEO_VP9;
            case 173:
            case 174:
                return MIME_VIDEO_HEVC;
            default:
                return MIME_VIDEO_AVC;
        }
    }

    //文件类别

    public static boolean isPicture(int mediaId) {
        return (mediaId & MAIN_TYPE_BITMASK) == MEDIA_FILE_TYPE_PICTURE;
    }

    public static boolean isRecord(int mediaId) {
        return (mediaId & MAIN_TYPE_BITMASK) == MEDIA_FILE_TYPE_RECORD;
    }

    //  大类
    /**
     * 音频
     */
    public static final int MEDIA_FILE_TYPE_AUDIO = 0x00000000;
    /**
     * 视频
     */
    public static final int MEDIA_FILE_TYPE_VIDEO = 0x20000000;
    /**
     * 录制
     */
    public static final int MEDIA_FILE_TYPE_RECORD = 0x40000000;
    /**
     * 图片
     */
    public static final int MEDIA_FILE_TYPE_PICTURE = 0x60000000;
    /**
     * 升级
     */
    public static final int MEDIA_FILE_TYPE_UPDATE = 0xe0000000;
    /**
     * 临时文件
     */
    public static final int MEDIA_FILE_TYPE_TEMP = 0x80000000;
    /**
     * 其它文件
     */
    public static final int MEDIA_FILE_TYPE_OTHER = 0xa0000000;
    public static final int MAIN_TYPE_BITMASK = 0xe0000000;
    //  小类
    /**
     * PCM文件
     */
    public static final int MEDIA_FILE_TYPE_PCM = 0x01000000;
    /**
     * MP3文件
     */
    public static final int MEDIA_FILE_TYPE_MP3 = 0x02000000;
    /**
     * WAV文件
     */
    public static final int MEDIA_FILE_TYPE_ADPCM = 0x03000000;
    /**
     * FLAC文件
     */
    public static final int MEDIA_FILE_TYPE_FLAC = 0x04000000;
    /**
     * MP4文件
     */
    public static final int MEDIA_FILE_TYPE_MP4 = 0x07000000;
    /**
     * MKV文件
     */
    public static final int MEDIA_FILE_TYPE_MKV = 0x08000000;
    /**
     * RMVB文件
     */
    public static final int MEDIA_FILE_TYPE_RMVB = 0x09000000;
    /**
     * RM文件
     */
    public static final int MEDIA_FILE_TYPE_RM = 0x0a000000;
    /**
     * AVI文件
     */
    public static final int MEDIA_FILE_TYPE_AVI = 0x0b000000;
    /**
     * bmp文件
     */
    public static final int MEDIA_FILE_TYPE_BMP = 0x0c000000;
    /**
     * jpeg文件
     */
    public static final int MEDIA_FILE_TYPE_JPEG = 0x0d000000;
    /**
     * png文件
     */
    public static final int MEDIA_FILE_TYPE_PNG = 0x0e000000;
    /**
     * 其它文件
     */
    public static final int MEDIA_FILE_TYPE_OTHER_SUB = 0x10000000;

    public static final int SUB_TYPE_BITMASK = 0x1f000000;

}
