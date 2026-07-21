package com.android.deskclock.util;

import android.content.Context;
import android.provider.Settings;

/* JADX INFO: loaded from: classes.dex */
public class ShutdownAlarm {
    public static final long DEFAULT_ALARM_OFFSET = 120;
    public static final long DYNAMIC_ALARM_OFFSET = 300;
    public static final String SHUTDOWN_ALARM_CLOCK_OFFSET = "shutdown_alarm_clock_offset";
    public static final long SHUT_DOWN_ALARM_TIME_OFFSET = 600;
    private static final String TAG = "DC:ShutdownAlarm";
    private static final int VERSION_DYNAMIC_OFFSET = 1;
    private static final int VERSION_STATIC_OFFSET = 0;

    private static void writeWakeAlarm(long j) {
        if (Util.supportShutdownAlarm()) {
            Log.f(TAG, "Set Shutdown Alarm. System will boot at " + Util.formatTimeForLog(1000 * j));
            ClockCompatUtil.setSystemWakeUpTime(j);
        } else {
            Log.f(TAG, "Shutdown Alarm not supported");
        }
    }

    public static void disableWakeAlarm() {
        writeWakeAlarm(0L);
    }

    public static void setWakeAlarm(Context context, long j) {
        long j2 = 300;
        if (Util.isVersionBelowS() && getMiuiDynamicOffsetVersion(context) != 1) {
            j2 = 120;
        }
        if (j - (System.currentTimeMillis() / 1000) >= 600) {
            setShutdownAlarmClockOffset(context, j2);
            writeWakeAlarm(j - getShutdownAlarmClockOffset(context));
        } else {
            writeWakeAlarm(0L);
        }
    }

    private static int getMiuiDynamicOffsetVersion(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo("com.miui.rom", 128).metaData.getInt("MiuiGlobalActions.isSupportShutdownAlarmDynamicOffset", 0);
        } catch (Exception unused) {
            return 0;
        }
    }

    private static void setShutdownAlarmClockOffset(Context context, long j) {
        try {
            Settings.System.putString(context.getContentResolver(), SHUTDOWN_ALARM_CLOCK_OFFSET, String.valueOf(j));
        } catch (Exception e) {
            Log.i("Put String alarmClockOffset error: " + e.getMessage());
        }
    }

    public static long getShutdownAlarmClockOffset(Context context) {
        try {
            String string = Settings.System.getString(context.getContentResolver(), SHUTDOWN_ALARM_CLOCK_OFFSET);
            if (string != null) {
                return Long.parseLong(string);
            }
            return 120L;
        } catch (Exception unused) {
            return 120L;
        }
    }
}
