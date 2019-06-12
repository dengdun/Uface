package com.uniubi.uface.ether.utils;

import com.uniubi.uface.ether.EtherApp;

/**
 * 配置管理工具类
 * 主要对ShareUtils再次封装
 */
public class ShareferenceManager {

    /**
     * 设置是否是第一次运行
     * @param isFirstRun
     */
    public static final void setFirstRun(boolean isFirstRun) {
        ShareUtils.put(EtherApp.context, "first_run", isFirstRun);
    }

    /**
     * 获取当前是否是第一次运行的状态
     * @return
     */
    public static final boolean firstRun() {
        return (Boolean) ShareUtils.get(EtherApp.context, "first_run", false);
    }

    public static String getStartAppUrl() {
        return (String)ShareUtils.get(EtherApp.context, "startApp", "");
    }

}