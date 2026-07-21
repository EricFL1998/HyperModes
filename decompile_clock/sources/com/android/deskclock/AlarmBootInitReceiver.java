package com.android.deskclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.Process;
import android.os.SystemClock;
import android.text.TextUtils;
import com.android.deskclock.addition.monitor.MonitorHelper;
import com.android.deskclock.alarm.alert.AlarmService;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.timer.TimerService;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.ShutdownAlarm;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.log.ExLogger;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class AlarmBootInitReceiver extends BroadcastReceiver {
    private static final String TAG = "DC:AlarmBootInitReceiver";
    private SharedPreferences mSharedPref;

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, final Intent intent) {
        final BroadcastReceiver.PendingResult pendingResultGoAsync = goAsync();
        final PowerManager.WakeLock wakeLockCreatePartialWakeLock = AlarmAlertWakeLock.createPartialWakeLock(context);
        wakeLockCreatePartialWakeLock.acquire();
        AsyncHandler.post(new Runnable() { // from class: com.android.deskclock.AlarmBootInitReceiver.1
            @Override // java.lang.Runnable
            public void run() {
                AlarmBootInitReceiver.this.handleIntent(context, intent);
                pendingResultGoAsync.finish();
                wakeLockCreatePartialWakeLock.release();
            }
        });
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
        if (action.equals("android.intent.action.LOCKED_BOOT_COMPLETED") || action.equals("miui.action.LOCKED_WAKE_CLOCK")) {
            handleBoot(context);
            AlarmHelper.setNextAlert(context);
            AlarmHelper.initSleepNotification(context);
            AlarmHelper.registerWakeAlarm(context);
            Process.killProcess(Process.getUidForName(BuildConfig.APPLICATION_ID));
            return;
        }
        if (action.equals("android.intent.action.BOOT_COMPLETED") || action.equals("miui.action.WAKE_CLOCK")) {
            if (FBEUtil.isUserUnlocked(context)) {
                Log.d("Move the DataBaseFile and SharedPrefFile");
                FBEUtil.moveData(context, FBEUtil.createDeviceProtectedStorageContext(context));
                StatHelper.init(context);
                OneTrackStatHelper.init(context);
                ExLogger.getInstance().addCEStorageLog();
                ExLogger.getInstance().copyCEStorageLogToCE();
            }
            AlarmHelper.setNextAlert(context);
            AlarmHelper.initSleepNotification(context);
            AlarmHelper.setZenMode(context);
            AlarmHelper.registerWakeAlarm(context);
            Process.killProcess(Process.getUidForName(BuildConfig.APPLICATION_ID));
        }
    }

    private void handleBoot(Context context) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        MonitorHelper.boot(jCurrentTimeMillis, SystemClock.elapsedRealtime());
        PrefUtil.setBootTime(jCurrentTimeMillis);
        Util.saveClockTimeOffset(this.mSharedPref);
        long jCurrentTimeMillis2 = System.currentTimeMillis();
        if (AlarmHelper.isShutdownAlarmEnabled(context)) {
            alertShutdownAlarm(context, jCurrentTimeMillis2);
        }
        AlarmHelper.saveSnoozeAlert(context, -1, -1L);
        AlarmHelper.disableExpiredAlarms(context, jCurrentTimeMillis2);
        int i = this.mSharedPref.getInt(TimerDao.KEY_STATE, 0);
        long j = this.mSharedPref.getLong(TimerDao.KEY_END_TIME, 0L) - System.currentTimeMillis();
        Log.d("AlarmBootInitReceiver timeRemained = " + j + " timerState = " + i);
        if (i != 1 || j <= 1000 || j >= TimerDao.TIMER_MAX_LENGTH) {
            return;
        }
        Intent intent = new Intent(context, (Class<?>) TimerService.class);
        intent.putExtra(AlarmClockExtras.TIMER_INTENT_EXTRA, j);
        long timerDuration = TimerDao.getTimerDuration();
        if (timerDuration != 0) {
            j = timerDuration;
        }
        intent.putExtra(AlarmClockExtras.TIMER_INTENT_EXTRA_DURATION, j);
        context.startForegroundService(intent);
    }

    private void alertShutdownAlarm(Context context, long j) {
        Log.f(TAG, "AlarmBootInitReceiver, looking up for shut-down alarm");
        Cursor alarmsCursor = AlarmHelper.getAlarmsCursor(context.getContentResolver());
        try {
            if (alarmsCursor != null) {
                try {
                    long recentAlarmAlertTime = PrefUtil.getRecentAlarmAlertTime();
                    while (alarmsCursor.moveToNext()) {
                        Alarm alarm = new Alarm(alarmsCursor);
                        if (alarm.time == 0) {
                            alarm.time = calculateAlarmTimeToday(context, alarm);
                        }
                        if (bootFromAlarm(alarm, j, recentAlarmAlertTime)) {
                            Log.f(TAG, "AlarmBootInitReceiver: shut down alarm found: " + alarm.toString());
                            Intent intent = new Intent(context, (Class<?>) AlarmService.class);
                            intent.setAction(AlarmHelper.ALARM_ALERT_ACTION);
                            Parcel parcelObtain = Parcel.obtain();
                            alarm.writeToParcel(parcelObtain, 0);
                            parcelObtain.setDataPosition(0);
                            intent.putExtra(AlarmHelper.ALARM_RAW_DATA, parcelObtain.marshall());
                            parcelObtain.recycle();
                            context.startService(intent);
                            if (ShutdownAlarm.getShutdownAlarmClockOffset(DeskClockApp.getAppDEContext()) == 300) {
                                StatHelper.recordCountEventWithDevice(StatHelper.CATEGORY_ALARM_PLAY, StatHelper.KEY_SHUT_DOWN_ALARM_EXPIRED_NEW);
                                OneTrackStatHelper.trackTriggerEvent(OneTrackStatHelper.ALERT_SHUTDOWN_ALARM_EXPIRED);
                                break;
                            } else {
                                StatHelper.recordCountEventWithDevice(StatHelper.CATEGORY_ALARM_PLAY, StatHelper.KEY_SHUT_DOWN_ALARM_EXPIRED);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.f(TAG, "AlarmBootInitReceiver, looking up for shut-down alarm error: " + e.getMessage());
                }
            }
        } finally {
            alarmsCursor.close();
        }
    }

    private static long calculateAlarmTimeToday(Context context, Alarm alarm) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(11, alarm.hour);
        calendar.set(12, alarm.minutes);
        calendar.set(13, 0);
        calendar.set(14, 0);
        int nextAlarm = alarm.daysOfWeek.getNextAlarm(context, calendar);
        if (nextAlarm > 0) {
            calendar.add(7, nextAlarm);
        }
        if (alarm.skipTime >= calendar.getTimeInMillis()) {
            calendar.add(6, 1);
        }
        return calendar.getTimeInMillis();
    }

    private boolean bootFromAlarm(Alarm alarm, long j, long j2) {
        long j3 = alarm.time;
        if (!alarm.enabled || j3 <= j2) {
            return false;
        }
        long j4 = j - j3;
        return j4 >= 0 && j4 <= 300000;
    }
}
