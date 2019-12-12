package com.whzxw.uface.ether.http;

import com.uniubi.uface.ether.BuildConfig;

/**
 * @author dengdun on 2019-12-11.
 * 请求列表
 */
public class UriProvider {
    public final static String HOST =
            BuildConfig.DEBUG ? "http://wa8iwr.natappfree.cc" : "http://192.168.10.150:8082";

    //首页宣传图片地址
    public final static String HOME_ADVERTISE = HOST + "/locker/img/logo/1";

    //首页左上角logo地址
    public final static String HOME_TOP_LEFT_LOGO = HOST + "/locker/img/logo/0";
}
