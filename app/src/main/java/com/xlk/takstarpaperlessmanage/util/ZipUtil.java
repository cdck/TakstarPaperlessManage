package com.xlk.takstarpaperlessmanage.util;

import android.text.TextUtils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipOutputStream;

/**
 * @author Created by xlk on 2021/7/7.
 * @desc
 */
public class ZipUtil {
    /**
     * 对文件夹加密
     *
     * @param folder      文件夹全路径
     * @param destZipFile 全路径eg: filename.zip
     * @param password    密码
     * @return
     */
    public static void doZipFilesWithPassword(File folder, String destZipFile, String password) {
        if (!folder.exists()) {
            return;
        }
        ZipParameters parameters = new ZipParameters();
        // 压缩方式
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        // 压缩级别
        parameters.setCompressionLevel(CompressionLevel.FAST);
        ZipFile zipFile = new ZipFile(destZipFile);
//        Properties initProp = new Properties(System.getProperties());
        Charset charset = Charset.defaultCharset();
        System.out.println("charset:" + charset.name() + ",toString=" + charset.toString());
        zipFile.setCharset(charset);
        // 加密方式
        if (!TextUtils.isEmpty(password)) {
            //设置表示文件将被加密的标志
            parameters.setEncryptFiles(true);
            //设置用于加密文件的加密方法
            parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
            //设置 AES 加密密钥的密钥强度 默认为KEY_STRENGTH_256
//            parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_128);
            //设置密码Ad
            zipFile.setPassword(password.toCharArray());
        }
        try {
            List<File> existFileList = new ArrayList<>();
            if (folder.isDirectory()) {
                existFileList.addAll(Arrays.asList(folder.listFiles()));
                zipFile.addFiles(existFileList, parameters);
            } else {
                zipFile.addFile(folder, parameters);
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static void zipFolder(File folder, String destFile, String password)  {
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(folder));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
