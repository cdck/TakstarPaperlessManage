package com.xlk.takstarpaperlessmanage.helper.archive;

/**
 * @author Created by xlk on 2021/7/9.
 * @desc
 */
public class ConsumptionTask {
    /*
       子ID，在列队中保持唯一。对应的是一条数据。
     */
    public String taskNo;

    /**
     * 父ID，通常以组的方式出现，关联一组相关的数据
     */
    public String planNo;

    /**
     * 超时时间， 定义任务的最长执行时间，0为无效时间，不能设置为负数，毫秒
     */
    public long timeOut;

    /**
     *  false：该任务未返回结果，true：该任务已经有结果了。
     */
    public boolean isResult;

    /**
     *  该任务是不是超时引起的任务失败
     */
    public boolean isTimeOut;

    public OnTimeOutLinsenet onTimeOutLinsenet;


    public void setOnTimeOutLinsenet(OnTimeOutLinsenet onTimeOutLinsenet) {
        this.onTimeOutLinsenet = onTimeOutLinsenet;
    }

    /**
     *  打开超时等待
     */
    public void openTime(){
        new TaskThread().setTask(ConsumptionTask.this).run();
    }



    /**
     *  监听任务是否超时
     */
    public interface OnTimeOutLinsenet{

        /**
         *  超时未执行
         * @param task
         */
        void timeOut(ConsumptionTask task);
    }
}
