package com.xlk.takstarpaperlessmanage.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.JniHelper;
import com.xlk.takstarpaperlessmanage.model.WpsModel;
import com.xlk.takstarpaperlessmanage.view.admin.AdminActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * @author xlk
 * @date 2020/4/26
 * @desc wps 文件处理的广播
 */
public class WpsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        switch (action) {
            //关闭文件时的广播
            case WpsModel.Reciver.ACTION_CLOSE:
                //通知注销掉WPS广播
                EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_WPS_RECEIVER).objects(false).build());
                String closeFile = intent.getStringExtra(WpsModel.ReciverExtra.CLOSEFILE);
                String thirdPackage1 = intent.getStringExtra(WpsModel.ReciverExtra.THIRDPACKAGE);
                LogUtils.e("onReceive :  关闭文件收到广播 --> closeFile：" + closeFile + ", \n thirdPackage：" + thirdPackage1);
                jump2meet(context);
                break;
            //home键广播
            case WpsModel.Reciver.ACTION_HOME:
                //通知注销掉WPS广播
                EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_WPS_RECEIVER).objects(false).build());
                break;
            //保存文件时的广播
            case WpsModel.Reciver.ACTION_SAVE:
                EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_WPS_RECEIVER).objects(false).build());
                jump2meet(context);
                String openFile = intent.getStringExtra(WpsModel.ReciverExtra.OPENFILE);
                String thirdPackage = intent.getStringExtra(WpsModel.ReciverExtra.THIRDPACKAGE);
                String savePath = intent.getStringExtra(WpsModel.ReciverExtra.SAVEPATH);
                LogUtils.e("onReceive :  保存键广播 --> openfile： " + openFile + "\n thirdPackage：" + thirdPackage + "\n savePath：" + savePath);
                File file = new File(savePath);
                String fileName = file.getName();
                File fileByPath = FileUtils.getFileByPath(savePath);
                if (!fileByPath.getParent().endsWith(".recovery")) {
                    JniHelper.getInstance().uploadFile(0, Constant.ANNOTATION_DIR_ID, 0, fileName, savePath, 0, Constant.UPLOAD_WPS_FILE);
                }
                break;
            default:
                break;
        }
    }


    private void jump2meet(Context context) {
        Intent intent = new Intent(context, AdminActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
