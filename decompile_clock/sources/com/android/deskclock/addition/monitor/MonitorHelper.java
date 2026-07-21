package com.android.deskclock.addition.monitor;

import android.content.ContentValues;
import android.content.Intent;
import androidx.core.app.JobIntentService;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class MonitorHelper {
    private static final String TAG = "DC:MonitorHelper";

    private MonitorHelper() {
    }

    public static void shutdown(long j) {
        if (MonitorImpl.isSupport()) {
            Intent intent = new Intent();
            intent.putExtra("type", 2);
            intent.putExtra("time", j);
            enqueueWork(intent);
        }
    }

    public static void boot(long j, long j2) {
        if (MonitorImpl.isSupport()) {
            Intent intent = new Intent();
            intent.putExtra("type", 1);
            intent.putExtra("time", j);
            intent.putExtra(MonitorJobScheduler.ELAPSED_TIME, j2);
            enqueueWork(intent);
        }
    }

    public static void timeSet(long j, long j2) {
        if (MonitorImpl.isSupport()) {
            Intent intent = new Intent();
            intent.putExtra("type", 7);
            intent.putExtra("time", j);
            intent.putExtra(MonitorJobScheduler.ELAPSED_TIME, j2);
            enqueueWork(intent);
        }
    }

    public static void reset() {
        if (MonitorImpl.isSupport()) {
            Intent intent = new Intent();
            intent.putExtra("type", 6);
            enqueueWork(intent);
        }
    }

    public static void report() {
        if (MonitorImpl.isSupport()) {
            Intent intent = new Intent();
            intent.putExtra("type", 5);
            enqueueWork(intent);
        }
    }

    public static void alert(int i, long j, long j2, boolean z) {
        if (MonitorImpl.isSupport()) {
            Intent intent = new Intent();
            intent.putExtra("type", 4);
            intent.putExtra(MonitorJobScheduler.ALARM_ID, i);
            intent.putExtra(MonitorJobScheduler.ALARM_TIME, j);
            intent.putExtra(MonitorJobScheduler.PLAY_TIME, j2);
            intent.putExtra(MonitorJobScheduler.SCREEN_LOCKED, z);
            enqueueWork(intent);
        }
    }

    public static void show(int i, long j, long j2) {
        if (MonitorImpl.isSupport()) {
            Intent intent = new Intent();
            intent.putExtra("type", 8);
            intent.putExtra(MonitorJobScheduler.ALARM_ID, i);
            intent.putExtra(MonitorJobScheduler.ALARM_TIME, j);
            intent.putExtra(MonitorJobScheduler.SHOW_TIME, j2);
            enqueueWork(intent);
        }
    }

    public static void setShutDownAlarmState() {
        if (MonitorImpl.isSupport()) {
            Intent intent = new Intent();
            intent.putExtra("type", 9);
            intent.putExtra("time", System.currentTimeMillis());
            enqueueWork(intent);
        }
    }

    public static void modify(int i, long j, int i2, ContentValues contentValues) {
        if (MonitorImpl.isSupport()) {
            Intent intent = new Intent();
            intent.putExtra("type", 3);
            intent.putExtra(MonitorJobScheduler.ALARM_ID, i2);
            intent.putExtra(MonitorJobScheduler.MODIFY_TYPE, i);
            intent.putExtra(MonitorJobScheduler.MODIFY_CONTENT, contentValues);
            intent.putExtra("time", j);
            enqueueWork(intent);
        }
    }

    private static void enqueueWork(Intent intent) {
        try {
            JobIntentService.enqueueWork(DeskClockApp.getAppDEContext(), (Class<?>) MonitorJobScheduler.class, 2, intent);
        } catch (Exception e) {
            Log.e(TAG, "enqueueWork error: " + e.getMessage());
        }
    }
}
