package com.xlk.takstarpaperlessmanage.util;

import android.util.Log;

import com.xlk.takstarpaperlessmanage.App;

/**
 * @author Created by xlk on 2021/4/21.
 * @desc
 */
public class LogUtil {

    public static void d(String tag, String msg) {
        if (App.isDebug) {
            Log.d("LogUtil# " + tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (App.isDebug) {
            Log.e("LogUtil# " + tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (App.isDebug) {
            Log.i("LogUtil# " + tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (App.isDebug) {
            Log.v("LogUtil# " + tag, msg);
        }
    }
}
