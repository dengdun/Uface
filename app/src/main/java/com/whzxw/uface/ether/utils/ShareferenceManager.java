package com.whzxw.uface.ether.utils;

import com.whzxw.uface.ether.EtherApp;

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

    /**
     * 开启通知
     * @return
     */
    public static String getStartAppUrl() {
        return (String)ShareUtils.get(EtherApp.context, "startApp", "http://www.192.168.1.1:8080/");
    }

    /**
     * 识别成功通知
     * @return
     */
    public static String getsendRecoResultUrl() {
        return (String)ShareUtils.get(EtherApp.context, "resulturl", "http://www.192.168.1.1:8080/");
    }


}
