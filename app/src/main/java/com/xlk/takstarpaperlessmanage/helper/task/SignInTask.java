package com.xlk.takstarpaperlessmanage.helper.task;

import com.blankj.utilcode.util.LogUtils;
import com.xlk.takstarpaperlessmanage.helper.archive.ConsumptionTask;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.bean.SignInBean;
import com.xlk.takstarpaperlessmanage.util.JxlUtil;

import java.util.List;

/**
 * @author Created by xlk on 2021/7/9.
 * @desc 归档会议签到信息
 */
public class SignInTask extends ConsumptionTask implements Runnable {
    private final Info info;

    public SignInTask(Info info) {
        this.info = info;
    }

    @Override
    public void run() {
        LogUtils.i("归档会议签到信息");
        long l = System.currentTimeMillis();
        if (!info.data.isEmpty()) {
            JxlUtil.exportArchiveSignIn(Constant.DIR_ARCHIVE_TEMP, info.data);
        }
        isResult = true;
        LogUtils.i("归档会议签到信息，用时=" + (System.currentTimeMillis() - l));
    }

    public static class Info {
        List<SignInBean> data;

        public Info(List<SignInBean> data) {
            this.data = data;
        }
    }
}
