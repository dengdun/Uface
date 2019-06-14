package com.whzxw.uface.ether.utils;

import android.content.Context;

/**
 * 微信辅助类
 */
public class XlogUitls {

    public static void init(Context c) {
//        final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
//        final String logPath = SDCARD + "/whzxw/mars/log";
//
//// this is necessary, or may crash for SIGBUS
//        final String cachePath = c.getFilesDir() + "/xlog";
//
////init xlog
//        if (BuildConfig.DEBUG) {
//            Xlog.appenderOpen(Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, cachePath, logPath, "MarsSample", 0, "");
//            Xlog.setConsoleLogOpen(true);
//
//        } else {
//            Xlog.appenderOpen(Xlog.LEVEL_INFO, Xlog.AppednerModeSync, cachePath, logPath, "MarsSample", 0, "");
//            Xlog.setConsoleLogOpen(false);
//        }
//
//        Log.setLogImp(new Xlog());

    }

    public static void close() {
//        Log.appenderClose();
    }
}
