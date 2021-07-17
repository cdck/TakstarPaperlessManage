package com.xlk.takstarpaperlessmanage.helper.task;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ZipUtils;
import com.xlk.takstarpaperlessmanage.helper.archive.ConsumptionTask;
import com.xlk.takstarpaperlessmanage.helper.archive.LineUpTaskHelp;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.util.FileUtil;
import com.xlk.takstarpaperlessmanage.util.Zip4jUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * @author Created by xlk on 2021/7/9.
 * @desc 压缩文件
 */
public class ZipFileTask extends ConsumptionTask implements Runnable {
    private final Info info;
    private int count;
    private String zipFilePath;
    private boolean isInterrupted;
    private String password;

    public ZipFileTask(Info info) {
        this.info = info;
        thread = new Thread(this);
    }

    public String getFilePath() {
        return zipFilePath;
    }

    /**
     * 是否点击了取消
     */
    public boolean isInterrupted() {
        return isInterrupted;
    }

    public String getPassword() {
        return password.isEmpty() ? "" : "随机密码：" + password;
    }

    @Override
    public void run() {
        try {
            File srcFile = new File(Constant.DIR_ARCHIVE_TEMP);
            if (!srcFile.exists()) {
                LogUtils.e("zipThread 没有找到这个目录=" + Constant.DIR_ARCHIVE_TEMP);
                return;
            }
            LogUtils.i("zipThread 开始压缩 当前线程id=" + Thread.currentThread().getId() + "-" + Thread.currentThread().getName());
            FileUtils.createOrExistsDir(info.dirPath);
            zipFilePath = getFilePath(info.dirPath + "会议归档");
            Properties properties = new Properties(System.getProperties());
            Charset charset = Charset.defaultCharset();
            LogUtils.d("charset.name=" + charset.name()
                    + ",当前系统编码:" + properties.getProperty("file.encoding")
                    + ",当前系统语言:" + properties.getProperty("user.language")
            );
            LogUtils.e("是否加密=" + info.isEncryption);
            long l = System.currentTimeMillis();
            if (info.isEncryption) {
                File file = new File(Constant.DIR_ARCHIVE_TEMP);
                File firstFile = FileUtil.findFirstFile(Constant.DIR_ARCHIVE_TEMP);
                LogUtils.e("firstFile=" + firstFile.getAbsolutePath());
                String fileCharsetSimple = FileUtils.getFileCharsetSimple(firstFile);
                password = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
                LogUtils.e("文件名的编码格式：" + fileCharsetSimple + ",压缩随机密码：" + password);
                Zip4jUtil.zipFile2_9_0(file, zipFilePath, password, Charset.forName("GBK"));
            } else {
                ZipUtils.zipFile(Constant.DIR_ARCHIVE_TEMP, zipFilePath);
            }
            LogUtils.e("压缩结束----用时=" + (System.currentTimeMillis() - l) + ",isInterrupted=" + thread.isInterrupted() + ",zipFilePath=" + zipFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            LogUtils.i("删除临时目录" + ",isInterrupted=" + thread.isInterrupted());
            FileUtils.delete(Constant.DIR_ARCHIVE_TEMP);
            isResult = true;
            isInterrupted = thread.isInterrupted();
//            if (isInterrupted) {
//                //点击取消的时候无法中断压缩方法，只能在压缩后处理
//                File file = new File(zipFilePath);
//                boolean delete = file.delete();
////                boolean delete = FileUtils.delete(zipFilePath);
//                LogUtils.e("点击取消的时候无法中断压缩方法，只能在压缩后处理:删除压缩文件 isDelete=" + delete);
//            }
            LineUpTaskHelp.getInstance().exOk(this);
        }
    }


    /**
     * @param filePathNoExtension 没有后缀的文件路径 eg: 内部存储/文件/文本文件
     */
    private String getFilePath(String filePathNoExtension) {
        boolean fileExists = FileUtils.isFileExists(filePathNoExtension + ".zip");
        if (fileExists) {
            return getFilePath(info.dirPath + "会议归档(" + (++count) + ")");
        }
        return filePathNoExtension + ".zip";
    }

    public static class Info {
        String dirPath;
        boolean isEncryption;

        public Info(String dirPath, boolean isEncryption) {
            this.dirPath = dirPath;
            this.isEncryption = isEncryption;
        }
    }
}
