/*
 * Copyright 2017-2032 the original yangqingxiang.
 */
package com.farmcn.utils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * @ClassName: FileUtils 
 * @Description: 文件处理工具类
 * @author 杨庆祥
 * @date 2018年3月8日 下午8:50:06 
 *  
 */
public class FileUtils {

    /**
     * @Description: 计算文件的 MD5值
     * @param @param file File
     * @param @return 设定文件 
     * @return String 返回类型 
     * @throws
     */
    public static String getFileMD5(File file) {
        return getFileHashCode(file, "MD5");
    }

    /**
     * @Description: 计算文件的 SHA-1值
     * @param @param file
     * @param @return 设定文件 
     * @return String 返回类型 
     * @throws
     */
    public static String getFileSha1(File file) {
        return getFileHashCode(file, "SHA-1");
    }

    /**
     * @Description: 获取文件的hash编码
     * @param file File
     * @param hashCode String 编码类型
     * @return String
     */
    public static String getFileHashCode(File file, String hashCode) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[8192];
        int len;
        try {
            digest = MessageDigest.getInstance(hashCode);
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @Description: 获取文件创建时间
     * @param @param filepath
     * @param @return 设定文件 
     * @return String 返回类型 
     * @throws
     */
    public static String getCreateDate(String filepath) {
        Path path = Paths.get(filepath);
        BasicFileAttributeView basicview = Files.getFileAttributeView(path, BasicFileAttributeView.class,
                LinkOption.NOFOLLOW_LINKS);
        BasicFileAttributes attr;
        try {
            attr = basicview.readAttributes();
            Date createDate = new Date(attr.creationTime().toMillis());
            DateFormat formater = new SimpleDateFormat("yyyyMMdd");
            return formater.format(createDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "19700101";
    }

    /**
     * @Description: 对比两个文件是否是同一个文件
     * @param @param file1 File
     * @param @param file2 File
     * @param @return 设定文件  
     * @return boolean 返回类型 
     * @throws
     */
    public static boolean compareFile(File file1, File file2) {
        String file1Md5 = getFileMD5(file1);
        String file2Md5 = getFileMD5(file2);
        if (file1Md5 == null || file2Md5 == null) {
            return false;
        }
        return file1Md5.equals(file2Md5);
    }

    /**
     * @Description: 移动文件
     * @param @param sourceFile String 文件绝对路径
     * @param @param targetPath String 移动到的文件路径
     * @param @param newName String 移动后新文件名，空的话默认原文件名
     * @return void 返回类型 
     * @throws
     */
    public static void moveFile(String sourceFile, String targetPath, String newName) {

        File dpFile = new File(sourceFile);
        if (!dpFile.exists() || !dpFile.isFile()) {
            throw new RuntimeException("文件“" + sourceFile + "”不存在！");
        }

        File folder = new File(targetPath);
        if (!folder.isDirectory()) {
            throw new RuntimeException("“" + targetPath + "”不是有效的文件夹！");
        }

        // 如果文件夹不存在，则创建文件夹
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 移动后的新名字
        if (newName == null || newName.trim().length() == 0) {
            newName = dpFile.getName();
        }

        File newFile = new File(targetPath + File.separator + newName);
        dpFile.renameTo(newFile);
    }
}
