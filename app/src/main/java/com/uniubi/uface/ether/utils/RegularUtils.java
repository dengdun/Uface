package com.uniubi.uface.ether.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式
 */
public class RegularUtils {

    /**
     * 获取URL里面的HOST地址
     * @param url
     * @return 返回baseUrl地址
     */
    public static String getHost(String url) {
        Pattern pattern = Pattern.compile("^(?<=)(http(s)?://[a-zA-Z0-9\\.\\:]*)");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }
}
