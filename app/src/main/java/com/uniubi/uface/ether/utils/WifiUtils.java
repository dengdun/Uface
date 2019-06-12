package com.uniubi.uface.ether.utils;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.uniubi.uface.ether.EtherApp;

import static android.content.Context.WIFI_SERVICE;

public class WifiUtils {
    public static final String getLocalIPAddress() {
        WifiManager wifiMgr = (WifiManager) EtherApp.context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipAddress = Formatter.formatIpAddress(ip);
        return ipAddress;
    }
}
