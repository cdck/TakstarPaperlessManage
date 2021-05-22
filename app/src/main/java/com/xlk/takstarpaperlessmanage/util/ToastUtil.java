package com.xlk.takstarpaperlessmanage.util;

import android.widget.Toast;

import com.xlk.takstarpaperlessmanage.App;

/**
 * @author Created by xlk on 2021/5/7.
 * @desc
 */
public class ToastUtil {
    public static void showLong(String msg) {
        Toast.makeText(App.appContext, msg, Toast.LENGTH_LONG).show();
    }

    public static void showLong(int resid) {
        showLong(App.appContext.getString(resid));
    }

    public static void showShort(String msg) {
        Toast.makeText(App.appContext, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(int resid) {
        showShort(App.appContext.getString(resid));
    }
}
