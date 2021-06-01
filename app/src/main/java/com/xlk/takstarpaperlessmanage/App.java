package com.xlk.takstarpaperlessmanage;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.projection.MediaProjection;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.util.MyRejectedExecutionHandler;
import com.xlk.takstarpaperlessmanage.util.NamingThreadFactory;
import com.xlk.takstarpaperlessmanage.view.main.MainActivity;
import com.xlk.takstarpaperlessmanage.view.service.BackService;
import com.xlk.takstarpaperlessmanage.view.service.ScreenRecorder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * @author Created by xlk on 2021/4/21.
 * @desc
 */
public class App extends Application {
    public static List<Activity> activities = new ArrayList<>();
    public static boolean isDebug = false;
    public static boolean read2file = false;
    public static Context appContext;

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
    public static MediaProjection mMediaProjection;
    private ScreenRecorder recorder;
    public static int screen_width, screen_height, dpi;
    public static int maxBitRate = 1000 * 1000;
    public static LocalBroadcastManager lbm;
    public static int camera_width = 1280, camera_height = 720;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        CrashUtils.init(Constant.crash_dir);
//        CrashHandler.getInstance().init(this);
        LogUtils.Config config = LogUtils.getConfig();
        config.setLog2FileSwitch(true);
        config.setDir(Constant.logcat_dir);
        config.setSaveDays(7);
        initScreenParam();
        lbm = LocalBroadcastManager.getInstance(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_START_SCREEN_RECORD);
        filter.addAction(Constant.ACTION_STOP_SCREEN_RECORD);
        filter.addAction(Constant.ACTION_STOP_SCREEN_RECORD_WHEN_EXIT_APP);
        lbm.registerReceiver(receiver, filter);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                activities.add(activity);
                if (activity.getClass().getName().equals(MainActivity.class.getName())) {
//                    ServiceUtils.startService(BackService.class);
                    Intent backService = new Intent(activity, BackService.class);
                    startService(backService);
                }
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

    private void initScreenParam() {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager window = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        window.getDefaultDisplay().getMetrics(metric);
        screen_width = metric.widthPixels;
        screen_height = metric.heightPixels;
        dpi = metric.densityDpi;
        if (dpi > 320) {
            dpi = 320;
        }
        LogUtils.i("initScreenParam : 屏幕大小 width=" + screen_width + ",height=" + screen_height);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int type = intent.getIntExtra(Constant.EXTRA_CAPTURE_TYPE, 0);
            LogUtils.e("onReceive :   --> type= " + type + " , action = " + action);
            if (action.equals(Constant.ACTION_START_SCREEN_RECORD)) {
                LogUtils.e("screen_shot --> ");
                startScreenRecord();
            } else if (action.equals(Constant.ACTION_STOP_SCREEN_RECORD)) {
                LogUtils.e("stop_screen_shot --> ");
                if (stopScreenRecord()) {
                    LogUtils.i("stopStreamInform: 屏幕录制已停止..");
                } else {
                    LogUtils.e("stopStreamInform: 屏幕录制停止失败 ");
                }
            } else if (action.equals(Constant.ACTION_STOP_SCREEN_RECORD_WHEN_EXIT_APP)) {
                LogUtils.i("onReceive 退出APP时停止屏幕录制----");
                stopScreenRecord();
            }
        }
    };

    private boolean stopScreenRecord() {
        if (recorder != null) {
            recorder.quit();
            recorder = null;
            return true;
        } else {
            return false;
        }
    }

    private void startScreenRecord() {
        if (stopScreenRecord()) {
            LogUtils.i("startScreenRecord: 屏幕录制已停止");
        } else {
            if (mMediaProjection == null) {
                return;
            }
            if (recorder != null) {
                recorder.quit();
            }
            if (recorder == null) {
                recorder = new ScreenRecorder(screen_width, screen_height, maxBitRate, dpi, mMediaProjection, "");
            }
            recorder.start();//启动录屏线程
            LogUtils.i("startScreenRecord: 开启屏幕录制");
        }
    }
}
