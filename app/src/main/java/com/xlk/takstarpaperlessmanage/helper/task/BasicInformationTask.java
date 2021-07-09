package com.xlk.takstarpaperlessmanage.helper.task;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.xlk.takstarpaperlessmanage.helper.archive.ConsumptionTask;
import com.xlk.takstarpaperlessmanage.model.Constant;

/**
 * @author Created by xlk on 2021/7/9.
 * @desc 归档会议基本信息
 */
public class BasicInformationTask extends ConsumptionTask implements Runnable {

    private final Info info;


    public BasicInformationTask(Info info) {
        this.info = info;
    }

    @Override
    public void run() {
        LogUtils.i("归档会议基本信息");
        long l = System.currentTimeMillis();
        if (!info.conferenceBasicInformation.isEmpty()) {
            FileIOUtils.writeFileFromString(Constant.DIR_ARCHIVE_TEMP + "会议基本信息.txt", info.conferenceBasicInformation);
        }
        if (!info.agendaText.isEmpty()) {
            FileIOUtils.writeFileFromString(Constant.DIR_ARCHIVE_TEMP + "会议议程信息.txt", info.agendaText);
        }
        if (!info.bulletinText.isEmpty()) {
            FileIOUtils.writeFileFromString(Constant.DIR_ARCHIVE_TEMP + "会议公告信息.txt", info.bulletinText);
        }
        isResult = true;
        LogUtils.i("归档会议基本信息，用时=" + (System.currentTimeMillis() - l));
//        if (info.agendaMediaId != 0) {
//            JniHelper jni = JniHelper.getInstance();
//            byte[] bytes = jni.queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_NAME.getNumber(), info.agendaMediaId);
//            try {
//                InterfaceBase.pbui_CommonTextProperty textProperty = InterfaceBase.pbui_CommonTextProperty.parseFrom(bytes);
//                String fileName = textProperty.getPropertyval().toStringUtf8();
//                LogUtils.i("downloadAgendaFile 获取到文件议程 -->媒体id=" + info.agendaMediaId + ", 文件名=" + fileName);
//                FileUtils.createOrExistsDir(Constant.DIR_ARCHIVE_TEMP);
//                boolean fileExists = FileUtils.isFileExists(Constant.DIR_ARCHIVE_TEMP + fileName);
//                if (!fileExists) {
//                    jni.downloadFile(Constant.DIR_ARCHIVE_TEMP + fileName, info.agendaMediaId, 1, 0,
//                            Constant.ARCHIVE_AGENDA_FILE);
//                }
//            } catch (InvalidProtocolBufferException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public static class Info {
        //会议基本信息
        String conferenceBasicInformation;
        //议程文本内容
        String agendaText;
        //议程文件媒体id
        int agendaMediaId;
        //会议公告信息
        String bulletinText;

        public Info(String conferenceBasicInformation, String agendaText, int agendaMediaId, String bulletinText) {
            this.conferenceBasicInformation = conferenceBasicInformation;
            this.agendaText = agendaText;
            this.agendaMediaId = agendaMediaId;
            this.bulletinText = bulletinText;
        }
    }
}
