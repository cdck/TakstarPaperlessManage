package com.xlk.takstarpaperlessmanage.model;

/**
 * @author Created by xlk on 2021/4/21.
 * @desc
 */
public class EventType {
    public static final int BUS_BASE = 10000;

    /**
     * 自定义EventBus消息type
     * 主界面背景图片下载完成通知
     */
    public static final int BUS_MAIN_BG = BUS_BASE + 1;
    public static final int BUS_MAIN_LOGO = BUS_BASE + 2;
    public static final int BUS_MEET_BG = BUS_BASE + 3;
    public static final int BUS_MEET_LOGO = BUS_BASE + 4;
    public static final int BUS_BULLETIN_BG = BUS_BASE + 5;
    public static final int BUS_BULLETIN_LOGO = BUS_BASE + 6;
    public static final int BUS_ROOM_BG = BUS_BASE + 7;
    public static final int BUS_SHARE_PIC = BUS_BASE + 8;
    public static final int BUS_SCREEN_SHOT = BUS_BASE + 9;
    public static final int BUS_PROJECTIVE_BG = BUS_BASE + 10;
    public static final int BUS_PROJECTIVE_LOGO = BUS_BASE + 11;
    public static final int BUS_YUV_DISPLAY = BUS_BASE + 12;
    public static final int BUS_VIDEO_DECODE = BUS_BASE + 13;
    /**
     * 通知打开/关闭WPS操作监听广播
     */
    public static final int BUS_WPS_RECEIVER = BUS_BASE + 14;
    /**
     * 查看图片文件
     */
    public static final int BUS_PREVIEW_IMAGE = BUS_BASE + 15;
    /**
     * 座位信息导出成功通知
     */
    public static final int BUS_EXPORT_SUCCESSFUL = BUS_BASE + 16;
    /**
     * 桌牌背景图片下载完成
     */
    public static final int BUS_TABLE_CARD_BG = BUS_BASE + 17;
    /**
     * 上传评分文件完成通知
     */
    public static final int BUS_UPLOAD_SCORE_FILE_FINISH = BUS_BASE + 18;
    /**
     * 采集摄像头
     */
    public static final int BUS_COLLECT_CAMERA_START = BUS_BASE + 19;
    /**
     * 停止采集摄像头
     */
    public static final int BUS_COLLECT_CAMERA_STOP = BUS_BASE + 20;
    /**
     *
     */
    public static final int BUS_ARCHIVE_SHARE_FILE = BUS_BASE + 21;
    public static final int BUS_ARCHIVE_ANNOTATION_FILE = BUS_BASE + 22;
    public static final int BUS_ARCHIVE_MEET_DATA_FILE = BUS_BASE + 23;
    public static final int BUS_ARCHIVE_AGENDA_FILE = BUS_BASE + 24;
}
