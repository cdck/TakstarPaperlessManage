package com.xlk.takstarpaperlessmanage.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xlk.takstarpaperlessmanage.BuildConfig;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.WpsModel;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.DecimalFormat;

import androidx.core.content.FileProvider;

/**
 * @author Created by xlk on 2021/5/14.
 * @desc
 */
public class FileUtil {

    public static boolean isDoc(String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        return fileName.endsWith(".doc") ||
                fileName.endsWith(".docx") ||
                fileName.endsWith(".txt") ||
                fileName.endsWith(".log") ||
                fileName.endsWith(".pdf");
    }

    public static boolean isPPT(String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        return fileName.endsWith(".pptx") ||
                fileName.endsWith(".ppt");
    }

    public static boolean isXLS(String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        return fileName.endsWith(".xls") ||
                fileName.endsWith(".xlsx");
    }

    public static boolean isDocument(String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        return isDoc(fileName) || isPPT(fileName) || isXLS(fileName);
    }

    public static boolean isPicture(String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        return fileName.endsWith(".jpg") ||
                fileName.endsWith(".png") ||
                fileName.endsWith(".gif") ||
                fileName.endsWith(".img") ||
                fileName.endsWith(".img") ||
                fileName.endsWith(".jpeg");
    }

    public static boolean isAudio(String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        return fileName.endsWith(".mp4") ||
                fileName.endsWith(".mp3") ||
                fileName.endsWith(".3gp") ||
                fileName.endsWith(".rmvb") ||
                fileName.endsWith(".avi") ||
                fileName.endsWith(".mkv") ||
                fileName.endsWith(".flv");
    }

    public static boolean isVideo(String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        return fileName.endsWith(".mp4") ||
                fileName.endsWith(".3gp") ||
                fileName.endsWith(".rmvb") ||
                fileName.endsWith(".avi") ||
                fileName.endsWith(".mkv") ||
                fileName.endsWith(".flv");

    }

    public static boolean isOther(String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;
        return !isDocument(fileName) && !isPicture(fileName) && !isAudio(fileName);
    }


    /**
     * 自动转换文件大小 22B 22KB 22MB 22GB
     *
     * @param fileS 文件的大小 file.size() 获取的值
     * @return
     */
    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 根据文件名获取文件描述类型
     *
     * @param context  上下文
     * @param fileName 文件名
     * @return 文档/图片/音频/其它
     */
    public static String getFileTypeName(Context context, String fileName) {
        if (isPicture(fileName)) {
            return context.getString(R.string.picture);
        } else if (isDocument(fileName)) {
            return context.getString(R.string.document);
        } else if (isAudio(fileName)) {
            return context.getString(R.string.audio);
        } else {
            return context.getString(R.string.other);
        }
    }

    public static void openFile(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            LogUtils.e("openFile 文件不存在 filePath=" + filePath);
            return;
        }
        String fileName = file.getName();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (isPicture(fileName)) {
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_PREVIEW_IMAGE).objects(file.getAbsolutePath()).build());
        } else if (isDocument(fileName)) {
            //通知注册WPS操作监听广播
            EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_WPS_RECEIVER).objects(true).build());
            //如果是文档类文件并且不是pdf文件，设置只能使用WPS软件打开
            Bundle bundle = new Bundle();
            bundle.putString(WpsModel.OPEN_MODE, WpsModel.OpenMode.NORMAL); // 打开模式
//            bundle.putBoolean(WpsModel.ENTER_REVISE_MODE, true); // 以修订模式打开文档

            bundle.putBoolean(WpsModel.SEND_CLOSE_BROAD, true); // 文件关闭时是否发送广播
            bundle.putBoolean(WpsModel.SEND_SAVE_BROAD, true); // 文件保存时是否发送广播
            bundle.putBoolean(WpsModel.HOMEKEY_DOWN, true); // 单击home键是否发送广播
            bundle.putBoolean(WpsModel.BACKKEY_DOWN, true); // 单击back键是否发送广播

            bundle.putBoolean(WpsModel.SAVE_PATH, true); // 文件这次保存的路径
            bundle.putString(WpsModel.THIRD_PACKAGE, WpsModel.PackageName.NORMAL); // 第三方应用的包名，用于对改应用合法性的验证
//            bundle.putBoolean(WpsModel.CLEAR_TRACE, true);// 清除打开记录
//            bundle.putBoolean(CLEAR_FILE, true); //关闭后删除打开文件
            intent.setClassName(WpsModel.PackageName.NORMAL, WpsModel.ClassName.NORMAL);
            intent.putExtras(bundle);
            if (Build.VERSION.SDK_INT > 23) {//android 7.0以上时，URI不能直接暴露
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//标签，授予目录临时共享权限
                Uri uriForFile = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                intent.setDataAndType(uriForFile, "application/vnd.android.package-archive");
            } else {
                Uri uri = Uri.fromFile(file);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            }
            try {
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            openLocalFile(context, file);
        }
    }

    /**
     * 调用系统应用打开指定文件
     *
     * @param context 上下文对象
     * @param file    文件
     */
    public static void openLocalFile(Context context, File file) {
        if (file == null || !file.exists()) {
            LogUtils.e("将要打开的文件不存在");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {//android 7.0以上时，URI不能直接暴露
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//标签，授予目录临时共享权限
            Uri uriForFile = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            intent.setDataAndType(uriForFile, getMIMEType(file));
        } else {
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, getMIMEType(file));
        }
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0)
            return type;
        /* 获取文件的后缀名 */
        String fileType = fName.substring(dotIndex).toLowerCase();
        if (fileType == null || "".equals(fileType))
            return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIMES.length; i++) {
            if (fileType.equals(MIMES[i][0]))
                type = MIMES[i][1];
        }
        return type;
    }

    private static final String[][] MIMES = {
            //{后缀名，    MIME类型}
            {".3gp", "video/3gpp"}, {".apk", "application/vnd.android.package-archive"}, {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"}, {".bin", "application/octet-stream"}, {".bmp", "image/bmp"},
            {".c", "text/plain"}, {".class", "application/octet-stream"}, {".conf", "text/plain"},
            {".cpp", "text/plain"}, {".doc", "application/msword"}, {".docx", "application/msword"},
            {".exe", "application/octet-stream"}, {".gif", "image/gif"}, {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"}, {".h", "text/plain"}, {".htm", "text/html"},
            {".html", "text/html"}, {".jar", "application/java-archive"}, {".java", "text/plain"},
            {".jpeg", "image/jpeg"}, {".JPEG", "image/jpeg"}, {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"}, {".log", "text/plain"}, {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"}, {".m4b", "audio/mp4a-latm"}, {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"}, {".m4v", "video/x-m4v"}, {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"}, {".mp3", "audio/x-mpeg"}, {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"}, {".mpe", "video/mpeg"}, {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"}, {".mpg4", "video/mp4"}, {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"}, {".ogg", "audio/ogg"}, {".pdf", "application/pdf"},
            {".png", "image/png"}, {".pps", "application/vnd.ms-powerpoint"}, {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.ms-powerpoint"}, {".prop", "text/plain"}, {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"}, {".rmvb", "audio/x-pn-realaudio"}, {".rtf", "application/rtf"},
            {".sh", "text/plain"}, {".tar", "application/x-tar"}, {".tgz", "application/x-compressed"},
            {".txt", "text/plain"}, {".wav", "audio/x-wav"}, {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"}, {".wps", "application/vnd.ms-works"}, {".xml", "text/plain"},
            {".z", "application/x-compress"}, {".zip", "application/zip"}, {"", "*/*"}
    };
}
