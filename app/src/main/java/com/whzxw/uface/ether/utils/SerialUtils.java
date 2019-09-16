package com.whzxw.uface.ether.utils;

import android.os.Build;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 串号工具类
 */
public class SerialUtils {
    @Deprecated
    public static String getDeviceSerial() {
        String serial [] = null;
        try {
            Class clazz = Class.forName("android.os.Build");
            Class paraTypes = Class.forName("java.lang.String");
            Method method = clazz.getDeclaredMethod("getString", paraTypes);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            serial  = ((String) method.invoke(new Build(), "ro.serialno")).split(",");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return serial[0];
    }

    /**
     * 以字符串的方式读取序列号
     * @param filePath the file path
     * @return string string
     * @throws IOException the io exception
     */
    public static String loadFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
        char[] buf = new char[1024];
        int numRead;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    private static String mDeviceKey = "";

    public static String getDeviceKey() {
        if (TextUtils.isEmpty(mDeviceKey)) {
            String deviceKey = null;
            try {
                String mac = loadFileAsString("/data/deviceSN")
                        .replaceAll("\n", "")
                        .replaceAll(" ", "")
                        .trim();
                if (mac != null && mac.length() == 16) {
                    String subDeviceKey = mac.substring(0, 7);
                    if (!"84E0F42".equals(subDeviceKey)) {
                        return null;
                    }
                    deviceKey = mac;
                    mDeviceKey = deviceKey;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return deviceKey;
            }
        } else {
            return mDeviceKey;
        }
    }


}
