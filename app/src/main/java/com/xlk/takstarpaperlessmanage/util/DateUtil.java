package com.xlk.takstarpaperlessmanage.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Created by xlk on 2020/11/28.
 * @desc
 */
public class DateUtil {
    /**
     * 将当前获取的时间戳转换成详细日期时间
     *
     * @return 返回格式 2021年5月18日16时41分13秒
     */
    public static String nowDate() {
        Date tTime = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        return format.format(tTime);
    }

    /**
     * @param millisecond 单位 毫秒
     *                    时区设置：SimpleDateFormat对象.setTimeZone(TimeZone.getTimeZone("GTM"));
     */
    public static String[] convertAdminTime(long millisecond) {
        Date tTime = new Date(millisecond);

        SimpleDateFormat tim = new SimpleDateFormat("HH:mm");
        tim.setTimeZone(TimeZone.getTimeZone("GTM"));
        String time = tim.format(tTime);

        //只有一个E 则解析出来是 周几，4个E则是星期几
        SimpleDateFormat day = new SimpleDateFormat("yyyy/MM/dd");
        day.setTimeZone(TimeZone.getTimeZone("GTM"));
        String date = day.format(tTime);

        return new String[]{time, date};
    }


    /**
     * 转换成 2020/07/22 09:40
     *
     * @param seconds 单位:秒
     */
    public static String secondFormatDateTime(long seconds) {
        return millisecondFormatDateTime(seconds * 1000);
    }

    /**
     * 转换成 2020/07/22 09:40
     *
     * @param milliseconds 单位:毫秒
     */
    public static String millisecondFormatDateTime(long milliseconds) {
        return millisecondsFormat(milliseconds, "yyyy/MM/dd HH:mm");
    }

    /**
     * 装换成 年
     *
     * @param seconds 秒
     */
    public static String convertYear(long seconds) {
        return secondsFormat(seconds, "YYYY");
    }

    /**
     * 装换成 月
     *
     * @param seconds 秒
     */
    public static String convertMonth(long seconds) {
        return secondsFormat(seconds, "MM");
    }

    /**
     * 装换成 日
     *
     * @param seconds 秒
     */
    public static String convertDay(long seconds) {
        Date date = new Date(seconds * 1000);
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GTM"));
        return timeFormat.format(date);
    }

    /**
     * 装换成 11:30
     *
     * @param seconds 秒
     */
    public static String convertHour(long seconds) {
        return secondsFormat(seconds, "HH:mm");
    }

    /**
     * 装换成播放时间  10:30:33
     * @param milliseconds 毫秒
     * @return 10:30:33
     */
    public static String convertPlayTime(long milliseconds) {
        return millisecondsFormat(milliseconds, "HH:mm:ss");
    }

    /**
     * @param seconds 秒数
     * @param pattern 时间格式
     * @return
     */
    public static String secondsFormat(long seconds, String pattern) {
        return millisecondsFormat(seconds * 1000, pattern);
    }

    /**
     * @param milliseconds 毫秒
     * @param pattern      时间格式
     * @return
     */
    public static String millisecondsFormat(long milliseconds, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(milliseconds));
    }
}
