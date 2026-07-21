package com.android.deskclock;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;
import com.android.deskclock.addition.monitor.MonitorHelper;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmDataHelper;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class AlarmInitReceiver extends BroadcastReceiver {
    private static final String ACTION_MIUI_ALARM_CHANGED = "miui.intent.action.ALARM_CHANGED";
    private static final String TAG = "DC:AlarmInitReceiver";
    private SharedPreferences mSharedPref;

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, final Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        final BroadcastReceiver.PendingResult pendingResultGoAsync = goAsync();
        final PowerManager.WakeLock wakeLockCreatePartialWakeLock = AlarmAlertWakeLock.createPartialWakeLock(context);
        wakeLockCreatePartialWakeLock.acquire();
        if (TextUtils.equals(intent.getAction(), "android.intent.action.TIME_SET")) {
            AsyncHandler.postDelayed(new Runnable() { // from class: com.android.deskclock.AlarmInitReceiver.1
                @Override // java.lang.Runnable
                public void run() {
                    PowerManager.WakeLock wakeLock;
                    try {
                        try {
                            AlarmInitReceiver.this.handleIntent(context, intent);
                            BroadcastReceiver.PendingResult pendingResult = pendingResultGoAsync;
                            if (pendingResult != null) {
                                pendingResult.finish();
                            }
                            wakeLock = wakeLockCreatePartialWakeLock;
                            if (wakeLock == null) {
                                return;
                            }
                        } catch (Exception e) {
                            Log.d(AlarmInitReceiver.TAG, "receive ALARM_CHANGED exception:" + e);
                            BroadcastReceiver.PendingResult pendingResult2 = pendingResultGoAsync;
                            if (pendingResult2 != null) {
                                pendingResult2.finish();
                            }
                            wakeLock = wakeLockCreatePartialWakeLock;
                            if (wakeLock == null) {
                                return;
                            }
                        }
                        wakeLock.release();
                    } catch (Throwable th) {
                        BroadcastReceiver.PendingResult pendingResult3 = pendingResultGoAsync;
                        if (pendingResult3 != null) {
                            pendingResult3.finish();
                        }
                        PowerManager.WakeLock wakeLock2 = wakeLockCreatePartialWakeLock;
                        if (wakeLock2 != null) {
                            wakeLock2.release();
                        }
                        throw th;
                    }
                }
            }, 100L);
        } else {
            AsyncHandler.post(new Runnable() { // from class: com.android.deskclock.AlarmInitReceiver.2
                @Override // java.lang.Runnable
                public void run() {
                    AlarmInitReceiver.this.handleIntent(context, intent);
                    pendingResultGoAsync.finish();
                    wakeLockCreatePartialWakeLock.release();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleIntent(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        Log.f(TAG, "receive action: " + action);
        if (this.mSharedPref == null) {
            this.mSharedPref = FBEUtil.getDefaultSharedPreferences(context);
        }
        if (action.equals("android.intent.action.ACTION_SHUTDOWN")) {
            MonitorHelper.shutdown(System.currentTimeMillis());
            this.mSharedPref.edit().putString("snooze_repeat_count_remainder", this.mSharedPref.getString("snooze_repeat_count", "3")).apply();
            return;
        }
        if (action.equals("android.intent.action.TIMEZONE_CHANGED")) {
            MonitorHelper.timeSet(System.currentTimeMillis(), SystemClock.elapsedRealtime());
            Log.i("update non-repeating alarm's alarm time for timezone changed.");
            updateNonRepeatingAlarm(context);
            AlarmHelper.setNextAlert(context);
            AlarmHelper.initSleepNotification(context);
            AlarmHelper.setZenMode(context);
            AlarmHelper.registerWakeAlarm(context);
            return;
        }
        if (action.equals("android.intent.action.TIME_SET")) {
            MonitorHelper.timeSet(System.currentTimeMillis(), SystemClock.elapsedRealtime());
            Util.adjustStopwatchBaseTime(this.mSharedPref);
            AlarmHelper.setNextAlert(context);
            AlarmHelper.initSleepNotification(context);
            AlarmHelper.setZenMode(context);
            AlarmHelper.registerWakeAlarm(context);
            return;
        }
        if (action.equals("miui.intent.action.ALARM_CHANGED")) {
            try {
                ShiftAlarmDataHelper.resetCache();
                AlarmHelper.setNextAlert(context);
                AlarmHelper.initSleepNotification(context);
                AlarmHelper.setZenMode(context);
                AlarmHelper.registerWakeAlarm(context);
                return;
            } catch (Throwable th) {
                Log.d(TAG, "receive ALARM_CHANGED exception:" + th);
                return;
            }
        }
        Log.e(TAG, "receive unexpected action");
    }

    private void keepAliveForComingAlarm(Context context) {
        Alarm alarmCalculateNextAlert = AlarmHelper.calculateNextAlert(context);
        if (alarmCalculateNextAlert == null || alarmCalculateNextAlert.time > System.currentTimeMillis() + 180000) {
            return;
        }
        Intent intent = new Intent(context, (Class<?>) KeepLiveService.class);
        intent.putExtra(AlarmHelper.ALARM_INTENT_EXTRA, alarmCalculateNextAlert);
        context.startService(intent);
    }

    private static void updateNonRepeatingAlarm(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursorQuery = contentResolver.query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS, Alarm.Columns.WHERE_ENABLED_AND_NON_REPEAT, null, null);
        if (cursorQuery != null) {
            while (cursorQuery.moveToNext()) {
                try {
                    Alarm alarm = new Alarm(cursorQuery);
                    long timeInMillis = AlarmHelper.calculateAlarmTime(context, alarm.hour, alarm.minutes, alarm.daysOfWeek).getTimeInMillis();
                    ContentValues contentValuesCreateContentValues = AlarmHelper.createContentValues(context, alarm);
                    contentValuesCreateContentValues.put("alarmtime", Long.valueOf(timeInMillis));
                    contentResolver.update(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarm.id), contentValuesCreateContentValues, null, null);
                } catch (Throwable th) {
                    cursorQuery.close();
                    throw th;
                }
            }
            cursorQuery.close();
        }
    }
}
