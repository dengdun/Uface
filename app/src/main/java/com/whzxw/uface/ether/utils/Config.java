package com.whzxw.uface.ether.utils;

import android.os.Environment;

import java.io.File;

/**
 * 配置文件
 */
public class Config {
    public static final String BASE_DIR = new File(Environment.getExternalStorageState(), "/whzxw/").getPath();
    /**
     *  初始化保存的文件地址
     */
    public static void initBaseFilePath() {
        File file = new File(Environment.getExternalStorageDirectory(), "/whzxw");
        if (!file.exists()) {
            file.mkdirs();
        }
    }


}
