package com.xlk.takstarpaperlessmanage;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.util.CrashHandler;
import com.xlk.takstarpaperlessmanage.util.MyRejectedExecutionHandler;
import com.xlk.takstarpaperlessmanage.util.NamingThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Created by xlk on 2021/4/21.
 * @desc
 */
public class App extends Application {
    public static List<Activity> activities = new ArrayList<>();
    public static boolean isDebug = true;
    static {
        System.loadLibrary("avcodec-57");
        System.loadLibrary("avdevice-57");
        System.loadLibrary("avfilter-6");
        System.loadLibrary("avformat-57");
        System.loadLibrary("avutil-55");
        System.loadLibrary("postproc-54");
        System.loadLibrary("swresample-2");
        System.loadLibrary("swscale-4");
        System.loadLibrary("SDL2");
        System.loadLibrary("main");
        System.loadLibrary("NetClient");
        System.loadLibrary("Codec");
        System.loadLibrary("ExecProc");
        System.loadLibrary("Device-OpenSles");
        System.loadLibrary("meetcoreAnd");
        System.loadLibrary("PBmeetcoreAnd");
        System.loadLibrary("meetAnd");
        System.loadLibrary("native-lib");
        System.loadLibrary("z");
    }
    public static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            1,
            Runtime.getRuntime().availableProcessors() + 1,
            10L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100),
            new NamingThreadFactory("paperless-manage-t"),
            new MyRejectedExecutionHandler()
    );

    @Override
    public void onCreate() {
        super.onCreate();
        CrashUtils.init(Constant.crash_dir);
//        CrashHandler.getInstance().init(this);
        LogUtils.Config config = LogUtils.getConfig();
        config.setLog2FileSwitch(true);
        config.setDir(Constant.logcat_dir);
        config.setSaveDays(7);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                activities.add(activity);
                LogUtils.d("activityLife", "onActivityCreated " + activity + ",Activity数量=" + activities.size() + logAxt());
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                LogUtils.i("activityLife", "onActivityStarted " + activity);
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                LogUtils.i("activityLife", "onActivityResumed " + activity);

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                LogUtils.i("activityLife", "onActivityPaused " + activity);

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                LogUtils.i("activityLife", "onActivityStopped " + activity);

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                LogUtils.i("activityLife", "onActivitySaveInstanceState " + activity);

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                activities.remove(activity);
                LogUtils.e("activityLife", "onActivityDestroyed " + activity + ",Activity数量=" + activities.size() + logAxt());
            }
        });
    }

    private String logAxt() {
        StringBuilder sb = new StringBuilder();
        sb.append("打印所有的Activity:\n");
        for (Activity activity : activities) {
            sb.append(activity.getCallingPackage()).append("  #  ").append(activity).append("\n");
        }
        return sb.toString();
    }
}
