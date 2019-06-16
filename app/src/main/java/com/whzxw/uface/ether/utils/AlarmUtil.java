package com.whzxw.uface.ether.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmUtil {
    private static final String TAG = "AlarmUtil";

    public static void controlAlarm(Context context, long startTime, long matchId, Intent nextIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) matchId, nextIntent,
                PendingIntent.FLAG_ONE_SHOT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
    }

    public static void cancelAlarm(Context context, String action,long matchId) {
        Intent intent = new Intent(action);
        PendingIntent sender = PendingIntent.getBroadcast(
                context, (int) matchId, intent, 0);

        // And cancel the alarm.
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

}
