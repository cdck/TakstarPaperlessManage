package com.xlk.takstarpaperlessmanage.util;


import android.text.TextUtils;

import com.blankj.utilcode.util.FileUtils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.nio.charset.Charset;

/**
 * @author Created by xlk on 2021/7/7.
 * @desc
 */
public class Zip4jUtil {
    /**
     * 对文件夹加密
     *
     * @param folder          文件夹全路径
     * @param destZipFile     全路径eg: filename.zip
     * @param password        密码
     * @param fileNameCharset 当前手机的文件名编码（解决压缩后的文件乱码问题）
     */
    public static void zipFile2_9_0(File folder, String destZipFile, String password, Charset fileNameCharset) {
        if (!folder.exists()) {
            return;
        }
        ZipParameters parameters = new ZipParameters();
        // 压缩方式
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        // 压缩级别
        parameters.setCompressionLevel(CompressionLevel.FAST);
        ZipFile zipFile = new ZipFile(destZipFile);
        zipFile.setCharset(fileNameCharset);
        // 加密方式
        if (!TextUtils.isEmpty(password)) {
            //设置表示文件将被加密的标志
            parameters.setEncryptFiles(true);
            //设置用于加密文件的加密方法
            parameters.setEncryptionMethod(EncryptionMethod.AES);
            //设置 AES 加密密钥的密钥强度 默认为KEY_STRENGTH_256
            parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
            //设置密码Ad
            zipFile.setPassword(password.toCharArray());
        }
        try {
            addFile(zipFile, parameters, folder);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static void zipFolder1_3_1(File folder, String destFile, String password, Charset charset) {
//        try {
//            ZipParameters parameters = new ZipParameters();
//            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
//            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
//            if (!TextUtils.isEmpty(password)) {
//                parameters.setEncryptFiles(true);
//                parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
//                parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
//                parameters.setPassword(password);
//            }
//            ZipFile zipFile = new ZipFile(destFile);
//            Charset charset = Charset.defaultCharset();
//            System.out.println("charset:" + charset.name() + ",toString=" + charset.toString());
//            zipFile.setFileNameCharset("GBK");
//            addFile(zipFile, parameters, folder);
//        } catch (ZipException e) {
//            e.printStackTrace();
//        }
    }

    private static void addFile(ZipFile zipFile, ZipParameters parameters, File addFile) throws ZipException {
        if (addFile.isDirectory()) {
            File[] files = addFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        zipFile.addFolder(file, parameters);
                    } else {
                        zipFile.addFile(file, parameters);
                    }
                }
            }
        } else {
            zipFile.addFile(addFile, parameters);
        }
    }
}
