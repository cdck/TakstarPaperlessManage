package com.xlk.takstarpaperlessmanage.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;

import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.utils.AutoSizeUtils;
import me.jessyan.autosize.utils.ScreenUtils;

/**
 * @author Created by xlk on 2021/7/13.
 * @desc
 */
public class SizeUtil {
    /**
     * @ 获取当前手机屏幕的尺寸(单位:像素)
     */
    public static float getDeviceScreenSize(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int densityDpi = displayMetrics.densityDpi;
        float scaledDensity = displayMetrics.scaledDensity;
        float density = displayMetrics.density;
        float xdpi = displayMetrics.xdpi;
        float ydpi = displayMetrics.ydpi;
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        // 这样可以计算屏幕的物理尺寸
        float width2 = (width / xdpi) * (width / xdpi);
        float height2 = (height / ydpi) * (width / xdpi);
        float size = (float) Math.sqrt(width2 + height2);

        double ppi = Math.sqrt((width * width) + (height * height)) / size;

        float v = (float) (30 / (ppi / 160));
        LogUtils.e("屏幕信息："
                + ",density=" + density
                + " ,width=" + width
                + " ,height=" + height
                + " ,scaledDensity=" + scaledDensity
                + " ,xdpi=" + xdpi
                + " ,ydpi=" + ydpi
                + " ,densityDpi=" + densityDpi
                + " ,屏幕对角线=" + size
                + " ,ppi=" + ppi
                + " ,30px=" + v + "dp"
                + "\n" + displayMetrics.toString()
        );
        return size;
    }
}
