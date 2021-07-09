package com.xlk.takstarpaperlessmanage.helper.archive;

import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @param <T> 任务  任务/ 需要排队的任务， 支持扩展
 * @desc https://blog.csdn.net/u014478555/article/details/108235021
 */
public class LineUpTaskHelp<T extends ConsumptionTask> {
    /**
     * 排队容器, 需要排队的才会进入此列表
     */
    private LinkedList<T> lineUpBeans;

    /**
     * 执行下一个任务
     */
    private OnTaskListener onTaskListener;

    public LineUpTaskHelp<T> setOnTaskListener(OnTaskListener<T> onTaskListener) {
        this.onTaskListener = onTaskListener;
        return this;
    }

    private static LineUpTaskHelp lineUpTaskHelp;

    private LineUpTaskHelp() {
        // app 只会执行一直，知道释放
        lineUpBeans = new LinkedList<>();
    }

    public static LineUpTaskHelp getInstance() {
        if (lineUpTaskHelp == null) {
            synchronized (LineUpTaskHelp.class) {
                if (lineUpTaskHelp == null) {
                    lineUpTaskHelp = new LineUpTaskHelp();
                }
            }
        }
        return lineUpTaskHelp;
    }

    public int getTaskCount() {
        return lineUpBeans != null ? lineUpBeans.size() : 0;
    }

    /**
     * 加入排队
     *
     * @param task 一个任务
     */
    public void addTask(T task) {
        if (lineUpBeans != null) {
            Log.e("Post", "任务加入排队中：" + task.taskNo);
            if (!checkTask()) {
                if (task.timeOut > 0) setTimeOut(task);
                if (onTaskListener != null) onTaskListener.exNextTask(task);
            }
            lineUpBeans.addLast(task);
        }
    }

    /**
     * 插入一个任务任务。
     *
     * @param task  需要插队的任务
     * @param index 插入到表中的位置，0代表第一个
     */
    public void insertTask(T task, int index) {
        if (index < 0) return;
        if (lineUpBeans != null) {
            if (index <= lineUpBeans.size()) {
                if (!checkTask()) {
                    // 当前没有任务执行, 立即执行
                    if (task.timeOut > 0) setTimeOut(task); // 开启超时
                    if (onTaskListener != null) onTaskListener.exNextTask(task);
                }
                Log.e("Post", " 插入任务列队成功，插入的位置：" + index + " 插入的任务ID为：" + task.taskNo);
                if (index == 0) {
                    lineUpBeans.addFirst(task); // 插入任务到第一个元素的位置
                } else {
                    lineUpBeans.add(index, task); // 插入元素到指定位置
                }
            } else {
                Log.e("Post", index + "   " + lineUpBeans.size());
            }

        }
    }

    /**
     * 设置任务超时时间。
     *
     * @param task
     */
    public void setTimeOut(T task) {

        task.setOnTimeOutLinsenet(new ConsumptionTask.OnTimeOutLinsenet() {
            @Override
            public void timeOut(ConsumptionTask task) {
                // 设置超时监听
                if (onTaskListener != null) {
                    onTaskListener.timeOut(task);
                }
            }
        });
        task.openTime(); // 打开超时等待。如果任务在规定时间未执行完成，则抛弃该任务
    }

    /**
     * 检查列表中是否有任务正在执行。
     *
     * @return true 表示有任务正在执行。false可立即执行改任务。
     */
    public boolean checkTask() {
        boolean isTask = false;
        if (lineUpBeans != null) {
            if (lineUpBeans.size() > 0) {
                for (int i = 0; i < lineUpBeans.size(); i++) {
                    if (!lineUpBeans.get(i).isResult) {
                        // 是否还有未执行的任务
                        isTask = true;
                        break;
                    }
                }
            }
        }
        return isTask;
    }

    /**
     * 得到下一个执行的计划，
     *
     * @return 返回下一个将要执行的任务， 返回null ,代表没有任务可以执行了
     */
    public T getFirst() {
        T task = null;
        if (lineUpBeans != null) {
            for (int i = 0; i < lineUpBeans.size(); i++) {
                if (!lineUpBeans.get(i).isResult) {
                    // 找到还没有未执行的任务
                    task = lineUpBeans.get(i);
                    break;
                }
            }
        }
        return task;
    }

    /**
     * 执行成功之后，删除排队中的任务。
     *
     * @param task
     */
    private void deleteTask(T task) {
        if (lineUpBeans != null && task != null) {
            LogUtils.i("deleteTask " + task.taskNo);
            deleteTaskNoAll(task.taskNo);
        }
    }

    /**
     * 删除对应计划id的所有任务
     *
     * @param planNo
     */
    public void deletePlanNoAll(String planNo) {
        if (lineUpBeans != null) {
            Iterator iterator = lineUpBeans.iterator();
            if (TextUtils.isEmpty(planNo)) return;
            while (iterator.hasNext()) {
                ConsumptionTask task = (ConsumptionTask) iterator.next();
                if (task.planNo.equals(planNo)) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 删除对应任务id的项
     *
     * @param taskNo
     */
    public void deleteTaskNoAll(String taskNo) {
        if (lineUpBeans != null) {
            Iterator iterator = lineUpBeans.iterator();
            if (TextUtils.isEmpty(taskNo)) return;
            while (iterator.hasNext()) {
                ConsumptionTask task = (ConsumptionTask) iterator.next();
                if (task.taskNo.equals(taskNo)) {
                    iterator.remove();
                    Log.e("Post", "移除" + taskNo + "成功");
                    break;
                }
            }
        }
    }

    public void deleteAndCancelAllTask() {
        if (lineUpBeans != null) {
            Iterator iterator = lineUpBeans.iterator();
            while (iterator.hasNext()) {
                ConsumptionTask task = (ConsumptionTask) iterator.next();
                iterator.remove();
                Log.e("Post", "移除任务--" + task.taskNo + "--成功");
            }
        }
    }

    /**
     * 外部调用， 当执行完成一个任务调用
     */
    public void exOk(T task) {
        deleteTask(task); // 删除已经执行完成的任务。
        if (lineUpBeans != null) {
            T consumptionTask = getFirst();
            if (consumptionTask != null) {
                // 发现还有任务
                if (onTaskListener != null) {
                    ///
                    if (consumptionTask.timeOut > 0) {
                        setTimeOut(consumptionTask); // 开启超时
                    }
                    onTaskListener.exNextTask(consumptionTask);
                }
            } else {
                if (onTaskListener != null) {
                    onTaskListener.noTask();
                }
            }
        }
    }

    /**
     * 当第一个任务执行完成，发现列队中还存在任务， 将继续执行下一个任务
     */
    public interface OnTaskListener<T extends ConsumptionTask> {
        /**
         * 执行下一个任务
         *
         * @param task
         */
        void exNextTask(T task);

        /**
         * 所有任务执行完成
         */
        void noTask();

        /**
         * 超时未执行
         *
         * @param task
         */
        void timeOut(T task);
    }
}
