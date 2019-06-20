package com.whzxw.uface.ether.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.whzxw.uface.ether.EtherApp;

import java.util.Calendar;

/**
 * 闹钟设计
 */
public class AlarmManagerUtils {
    public static final int REQUEST_CODE_ALARM = 0x000789;

    public static final String ACTION_ALRAM = "com.whzxw.uface.ether.alarm";


    public static final void start( ){
        AlarmManager systemService = (AlarmManager) EtherApp.context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction(ACTION_ALRAM);
        PendingIntent pi = PendingIntent.getBroadcast(EtherApp.context, REQUEST_CODE_ALARM, intent, PendingIntent.FLAG_IMMUTABLE);
        // 一个小时一次的广播
        systemService.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 1000*60*60, pi);
    }
}
