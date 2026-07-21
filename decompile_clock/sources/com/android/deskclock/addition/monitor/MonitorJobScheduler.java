package com.android.deskclock.addition.monitor;

import android.content.ContentValues;
import android.content.Intent;
import androidx.core.app.JobIntentService;
import com.android.deskclock.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class MonitorJobScheduler extends JobIntentService {
    public static final String ALARM_ID = "alarmId";
    public static final String ALARM_TIME = "alarmTime";
    public static final String ELAPSED_TIME = "elapsedTime";
    public static final String MODIFY_CONTENT = "modifyContent";
    public static final String MODIFY_TYPE = "modifyType";
    public static final int MONITOR_JOB_ID = 2;
    public static final String PLAY_TIME = "playTime";
    public static final String SCREEN_LOCKED = "screenLocked";
    public static final String SHOW_TIME = "showTime";
    private static final String TAG = "DC:MonitorJobScheduler";
    public static final String TIME = "time";
    public static final String TYPE = "type";
    public static final int TYPE_ALERT = 4;
    public static final int TYPE_BOOT = 1;
    public static final int TYPE_IDLE = 0;
    public static final int TYPE_MODIFY = 3;
    public static final int TYPE_REPORT = 5;
    public static final int TYPE_RESET = 6;
    public static final int TYPE_SHOW = 8;
    public static final int TYPE_SHUTDOWN = 2;
    public static final int TYPE_SHUTDOWN_ALARM_STATE = 9;
    public static final int TYPE_TIME_SET = 7;

    @Override // androidx.core.app.JobIntentService
    protected void onHandleWork(Intent intent) {
        try {
            switch (intent.getIntExtra("type", 0)) {
                case 1:
                    long longExtra = intent.getLongExtra("time", 0L);
                    long longExtra2 = intent.getLongExtra(ELAPSED_TIME, 0L);
                    if (longExtra > 0 && longExtra2 > 0) {
                        MonitorImpl.monitorBoot(longExtra, longExtra2);
                    } else {
                        MonitorImpl.reset();
                    }
                    break;
                case 2:
                    long longExtra3 = intent.getLongExtra("time", 0L);
                    if (longExtra3 > 0) {
                        MonitorImpl.monitorShutdown(longExtra3);
                    } else {
                        MonitorImpl.reset();
                    }
                    break;
                case 3:
                    int intExtra = intent.getIntExtra(MODIFY_TYPE, -1);
                    int intExtra2 = intent.getIntExtra(ALARM_ID, -1);
                    long longExtra4 = intent.getLongExtra("time", 0L);
                    ContentValues contentValues = (ContentValues) intent.getParcelableExtra(MODIFY_CONTENT);
                    if (intExtra != -1 && intExtra2 != -1 && longExtra4 > 0) {
                        MonitorImpl.monitorModify(intExtra, longExtra4, intExtra2, contentValues);
                    } else {
                        MonitorImpl.reset();
                    }
                    break;
                case 4:
                    int intExtra3 = intent.getIntExtra(ALARM_ID, -1);
                    long longExtra5 = intent.getLongExtra(ALARM_TIME, 0L);
                    long longExtra6 = intent.getLongExtra(PLAY_TIME, 0L);
                    boolean booleanExtra = intent.getBooleanExtra(SCREEN_LOCKED, true);
                    if (intExtra3 != -1 && longExtra5 > 0 && longExtra6 > 0) {
                        MonitorImpl.monitorAlert(intExtra3, longExtra5, longExtra6, booleanExtra);
                    }
                    break;
                case 5:
                    MonitorImpl.trickMonitorReport();
                    break;
                case 6:
                    MonitorImpl.reset();
                    break;
                case 7:
                    long longExtra7 = intent.getLongExtra("time", 0L);
                    long longExtra8 = intent.getLongExtra(ELAPSED_TIME, 0L);
                    if (longExtra7 > 0 && longExtra8 > 0) {
                        MonitorImpl.monitorTimeSet(longExtra7, longExtra8);
                    } else {
                        MonitorImpl.reset();
                    }
                    break;
                case 8:
                    int intExtra4 = intent.getIntExtra(ALARM_ID, -1);
                    long longExtra9 = intent.getLongExtra(ALARM_TIME, 0L);
                    long longExtra10 = intent.getLongExtra(SHOW_TIME, 0L);
                    if (intExtra4 != -1 && longExtra9 > 0 && longExtra10 > 0) {
                        MonitorImpl.monitorShow(intExtra4, longExtra9, longExtra10);
                    }
                    break;
                case 9:
                    long longExtra11 = intent.getLongExtra("time", 0L);
                    if (longExtra11 > 0) {
                        MonitorImpl.monitorShutDownAlarmState(longExtra11);
                    } else {
                        MonitorImpl.reset();
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "onHandleWork error: " + e.getMessage());
        }
    }
}
