package com.android.deskclock.util;

import com.android.deskclock.DeskClockApp;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class ClockCompatUtil {
    private static final String SHUTDOWN_ALARM_SERVICE_NAME = "com.android.deskclock.util.ShutdownAlarm";
    private static final String TAG = "DC:ClockCompatUtil";

    public static void setSystemWakeUpTime(long j) {
        try {
            Method method = Class.forName("miui.security.SecurityManager").getMethod("setWakeUpTime", String.class, Long.TYPE);
            method.setAccessible(true);
            method.invoke(DeskClockApp.getAppDEContext().getSystemService("security"), SHUTDOWN_ALARM_SERVICE_NAME, Long.valueOf(j));
            Log.f(TAG, "Set Shutdown Alarm Success.");
        } catch (Exception e) {
            Log.e(TAG, "setSystemWakeUpTime error:" + e);
        }
    }
}
