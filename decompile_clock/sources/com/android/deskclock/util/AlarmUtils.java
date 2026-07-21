package com.android.deskclock.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.android.deskclock.DeskClockTabActivity;

/* JADX INFO: loaded from: classes.dex */
public class AlarmUtils {
    public static final String TAG = "DC:AlarmUtils";
    public static boolean alarmAlertStatus;
    public static boolean alarmRingForXiaoAi;
    public static boolean timerRingForXiaoAi;

    public static final void setAlarm(Context context, long j, Intent intent) {
        if (context == null || intent == null) {
            return;
        }
        Log.f(TAG, "setAlarm  triggerAtMillis: " + j + " action: " + intent.getAction());
        try {
            ((AlarmManager) context.getSystemService("alarm")).setAlarmClock(new AlarmManager.AlarmClockInfo(j, PendingIntent.getActivity(context, 0, new Intent(context, (Class<?>) DeskClockTabActivity.class), 201326592)), PendingIntent.getBroadcast(context, 0, intent, 201326592));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setServiceAlarm(Context context, long j, Intent intent) {
        if (context == null || intent == null) {
            return;
        }
        PendingIntent foregroundService = PendingIntent.getForegroundService(context, 0, intent, 201326592);
        try {
            ((AlarmManager) context.getSystemService("alarm")).setAlarmClock(new AlarmManager.AlarmClockInfo(j, PendingIntent.getActivity(context, 0, new Intent(context, (Class<?>) DeskClockTabActivity.class), 201326592)), foregroundService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void cancelServiceAlarm(Context context, Intent intent) {
        if (context == null || intent == null) {
            return;
        }
        ((AlarmManager) context.getSystemService("alarm")).cancel(PendingIntent.getForegroundService(context, 0, intent, 201326592));
    }

    public static final void cancelAlarm(Context context, String str) {
        if (context == null || TextUtils.isEmpty(str)) {
            return;
        }
        Intent intent = new Intent(str);
        intent.setPackage(context.getPackageName());
        cancelAlarm(context, intent);
    }

    public static final void cancelAlarm(final Context context, final Intent intent) {
        if (context == null || intent == null) {
            return;
        }
        new Thread(new Runnable() { // from class: com.android.deskclock.util.AlarmUtils.1
            @Override // java.lang.Runnable
            public void run() {
                ((AlarmManager) context.getSystemService("alarm")).cancel(PendingIntent.getBroadcast(context, 0, intent, 201326592));
            }
        }).start();
    }
}
