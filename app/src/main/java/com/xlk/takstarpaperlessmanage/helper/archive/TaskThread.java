package com.xlk.takstarpaperlessmanage.helper.archive;

import android.os.Handler;
import android.os.Looper;

/**
 * @author Created by xlk on 2021/7/9.
 * @desc 记录任务超时
 */
public class TaskThread {
    private ConsumptionTask task;

    /**
     *
     */
    private Handler handler;

    public TaskThread(){
        handler = new Handler(Looper.getMainLooper());
    }

    public TaskThread setTask(ConsumptionTask task) {
        this.task = task;
        return this;
    }

    public ConsumptionTask getTask() {
        return task;
    }

    public void run() {
        if (task.timeOut == 0) return;
        //  Log.e("Post", "记录当前任务，任务id:" + task.taskNo + "超时时间" + (task.timeOut / 1000) + "s");
        // handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, task.timeOut);
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!task.isResult) {
                // 设置的超时时间到了，但是还没有结果
                task.isTimeOut = true; // 是超时引起的错误。
                task.onTimeOutLinsenet.timeOut(getTask());
            }
            // 记得移除
            handler.removeCallbacks(runnable);
        }
    };
}
