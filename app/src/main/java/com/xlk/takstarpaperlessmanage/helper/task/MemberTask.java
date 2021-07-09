package com.xlk.takstarpaperlessmanage.helper.task;

import com.blankj.utilcode.util.LogUtils;
import com.xlk.takstarpaperlessmanage.helper.archive.ConsumptionTask;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.bean.MemberRoleBean;
import com.xlk.takstarpaperlessmanage.util.JxlUtil;

import java.util.List;

/**
 * @author Created by xlk on 2021/7/9.
 * @desc 归档参会人员信息
 */
public class MemberTask extends ConsumptionTask implements Runnable {
    private final Info info;

    public MemberTask(Info info) {
        this.info = info;
    }

    @Override
    public void run() {
        try {
            LogUtils.i("归档参会人员信息");
            long l = System.currentTimeMillis();
            Thread.sleep(2000);
            if (info.memberRoleBeans != null && !info.memberRoleBeans.isEmpty()) {
                JxlUtil.exportMemberInfo("归档参会人信息", Constant.DIR_ARCHIVE_TEMP, info.memberRoleBeans);
            }
            isResult = true;
            LogUtils.i("归档参会人员信息，用时=" + (System.currentTimeMillis() - l));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Info {
        List<MemberRoleBean> memberRoleBeans;

        public Info(List<MemberRoleBean> memberRoleBeans) {
            this.memberRoleBeans = memberRoleBeans;
        }
    }
}
