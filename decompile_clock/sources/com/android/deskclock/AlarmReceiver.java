package com.android.deskclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.PowerManager;
import com.android.deskclock.addition.weather.WeatherUtils;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.alarm.bedtime.HealthDataUtil;
import com.android.deskclock.alarm.bedtime.ZenModeUtil;
import com.android.deskclock.alarm.lifepost.LifePostUtils;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmDataHelper;
import com.android.deskclock.common.BroadcastReceiverActions;
import com.android.deskclock.settings.AlarmSettingsActivity;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Notification.BackScreenNotificationUtil;
import com.android.deskclock.util.NotificationUtil;
import com.android.deskclock.util.SleepModeUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;

/* JADX INFO: loaded from: classes.dex */
public class AlarmReceiver extends BroadcastReceiver {
    private static final long BROADCAST_DELAY_TIME = 240000;
    private static final String TAG = "DC:AlarmReceiver";
    private Alarm mAlarm;
    private SharedPreferences mSharedPreferences;

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, final Intent intent) {
        final BroadcastReceiver.PendingResult pendingResultGoAsync = goAsync();
        final PowerManager.WakeLock wakeLockCreatePartialWakeLock = AlarmAlertWakeLock.createPartialWakeLock(context);
        wakeLockCreatePartialWakeLock.acquire();
        AsyncHandler.post(new Runnable() { // from class: com.android.deskclock.AlarmReceiver.1
            @Override // java.lang.Runnable
            public void run() {
                AlarmReceiver.this.handleIntent(context, intent);
                pendingResultGoAsync.finish();
                wakeLockCreatePartialWakeLock.release();
            }
        });
    }

    private static void reShowSnoozeAlarmNotification(Context context, Alarm alarm) {
        NotificationUtil.showSnoozeNotification(context, alarm, Util.getSnoozeMinutes(context));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleIntent(final Context context, Intent intent) {
        AlarmHelper.setMiuiWallpaperManager(Util.getMiuiWallpaperManager());
        if (this.mSharedPreferences == null) {
            this.mSharedPreferences = FBEUtil.getDefaultSharedPreferences(context);
        }
        if (intent.hasExtra(AlarmHelper.ALARM_INTENT_EXTRA)) {
            this.mAlarm = (Alarm) intent.getParcelableExtra(AlarmHelper.ALARM_INTENT_EXTRA);
        }
        Log.f(TAG, "AlarmReceiver, action: " + intent.getAction());
        if (AlarmHelper.ACTION_SNOOZE_CANCEL.equals(intent.getAction())) {
            cancelSnoozeAlarm(context, this.mAlarm);
            return;
        }
        if (AlarmHelper.ACTION_ALARM_CANCEL.equals(intent.getAction())) {
            Alarm alarm = this.mAlarm;
            if (alarm != null) {
                if (alarm.daysOfWeek.isRepeatSet() && this.mAlarm.type != 2) {
                    AlarmHelper.skipAlarmForOnce(context, this.mAlarm.id);
                    if (this.mAlarm.id == Integer.MIN_VALUE) {
                        AlarmHelper.registerWakeAlarm(context);
                    }
                } else if (this.mAlarm.type == 2) {
                    ShiftAlarmDataHelper.enableShiftAlarm(this.mAlarm.id, false, this.mAlarm.time);
                } else {
                    AlarmHelper.enableAlarm(context, this.mAlarm.id, false);
                }
            }
            StatHelper.alarmEvent(StatHelper.EVENT_DISMISS_ALARM_ARRIVING_NOTIFICATION);
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_ARRIVING_NOTIFICATION_DISMISS_CLICK);
            return;
        }
        if (AlarmHelper.ALARM_ARRIVING_ACTION.equals(intent.getAction())) {
            Alarm alarmFromRawDataIntent = AlarmHelper.parseAlarmFromRawDataIntent(intent);
            if (alarmFromRawDataIntent == null || alarmFromRawDataIntent.time <= System.currentTimeMillis()) {
                return;
            }
            NotificationUtil.showAlarmArrivingNotification(context, alarmFromRawDataIntent);
            return;
        }
        if (AlarmHelper.SLEEP_ALARM_NOTIFICATION_ACTION.equals(intent.getAction())) {
            Alarm wakeAlarm = BedtimeUtil.getWakeAlarm(context);
            long longExtra = intent.getLongExtra("sleep_time", 0L);
            if (wakeAlarm != null && longExtra + BROADCAST_DELAY_TIME >= System.currentTimeMillis()) {
                HealthDataUtil.setScheduleState(context, HealthDataUtil.SLEEP_RECORD_BEGIN);
                if (BedtimeUtil.getNotificationAdvTime(context) != -1) {
                    NotificationUtil.showSleepNotification(context);
                }
                AlarmHelper.setNextSleepNotification(context, wakeAlarm);
                return;
            }
            AlarmHelper.setSleepNotification(context);
            return;
        }
        if (AlarmHelper.ALARM_ARRIVING_TRANSPARENT_ACTION.equals(intent.getAction())) {
            Alarm alarmFromRawDataIntent2 = AlarmHelper.parseAlarmFromRawDataIntent(intent);
            if (alarmFromRawDataIntent2 != null) {
                SleepModeUtil.exitSleepMode(context);
                WeatherUtils.updateWeatherBroadcast(alarmFromRawDataIntent2, context, false);
                long j = alarmFromRawDataIntent2.time - BROADCAST_DELAY_TIME;
                long jCurrentTimeMillis = System.currentTimeMillis() + 60000;
                if (jCurrentTimeMillis >= j) {
                    LifePostUtils.executeLifePostDataLoadTask(alarmFromRawDataIntent2);
                    return;
                } else {
                    AlarmHelper.setDatePrepareAlarm(context, alarmFromRawDataIntent2, jCurrentTimeMillis + ((long) (Math.random() * (j - jCurrentTimeMillis))));
                    return;
                }
            }
            return;
        }
        if (AlarmHelper.ALARM_DATA_PREPARE_ACTION.equals(intent.getAction())) {
            Alarm alarmFromRawDataIntent3 = AlarmHelper.parseAlarmFromRawDataIntent(intent);
            if (alarmFromRawDataIntent3 != null) {
                LifePostUtils.executeLifePostDataLoadTask(alarmFromRawDataIntent3);
                return;
            }
            return;
        }
        if (AlarmHelper.ACTION_ENTER_ZENMODE.equals(intent.getAction())) {
            if (System.currentTimeMillis() <= intent.getLongExtra("sleep_time", 0L) + BROADCAST_DELAY_TIME) {
                new Handler().postDelayed(new Runnable() { // from class: com.android.deskclock.AlarmReceiver.2
                    @Override // java.lang.Runnable
                    public void run() {
                        ZenModeUtil.enterZenMode(context);
                        Log.f(AlarmReceiver.TAG, "enterZenMode ");
                    }
                }, BedtimeUtil.getNotificationAdvTime(context) == 0 ? 2000L : 0L);
                return;
            } else {
                AlarmHelper.setZenMode(context);
                return;
            }
        }
        if (AlarmHelper.ACTION_REACH_WAKE_TIME.equals(intent.getAction())) {
            BedtimeUtil.doInWakeTime(context);
            AlarmHelper.registerWakeAlarm(context);
            return;
        }
        if (AlarmHelper.ALARM_ARRIVING_TRANSPARENT_ACTION_FOR_XIAOAI.equals(intent.getAction())) {
            final Alarm alarmFromRawDataIntent4 = AlarmHelper.parseAlarmFromRawDataIntent(intent);
            AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.AlarmReceiver.3
                @Override // java.lang.Runnable
                public void run() {
                    XiaoAiRingtoneHelper.resetEnableValue();
                    Alarm alarm2 = alarmFromRawDataIntent4;
                    if (alarm2 == null || !XiaoAiRingtoneHelper.isXiaoAiAlarm(context, alarm2.id)) {
                        return;
                    }
                    XiaoAiRingtoneHelper.sendBroadCastForUpdate(context, alarmFromRawDataIntent4);
                }
            });
            return;
        }
        if (AlarmHelper.XIAOAI_RINGTONE_QUERY_ACTION.equals(intent.getAction())) {
            Alarm alarmFromRawDataIntent5 = AlarmHelper.parseAlarmFromRawDataIntent(intent);
            if (alarmFromRawDataIntent5 != null) {
                XiaoAiRingtoneHelper.loadAlertUri(alarmFromRawDataIntent5);
                return;
            }
            return;
        }
        if (XiaoAiRingtoneHelper.ACTION_HANDLE_NOT_SURE_ALERT_ACTION.equals(intent.getAction())) {
            XiaoAiRingtoneHelper.resetEnableValue();
            XiaoAiRingtoneHelper.handleNotSureAlarm();
        } else if (BroadcastReceiverActions.ACTION_SUB_BACK_SCREEN_ALARM_CANCEL.equals(intent.getAction())) {
            cancelSnoozeAlarm(context, this.mAlarm);
        }
    }

    private void cancelSnoozeAlarm(Context context, Alarm alarm) {
        context.sendBroadcast(new Intent(AlarmHelper.ACTION_SNOOZE_CANCEL_AGAIN));
        if (alarm == null) {
            Log.e(TAG, "Unable to parse Alarm from intent.");
            AlarmHelper.saveSnoozeAlert(context, -1, -1L);
            AlarmHelper.setNextAlert(context);
        } else {
            AlarmHelper.cancelSnoozedAlarm(context, alarm.id);
            Log.f(TAG, "Cancel snooze alarm, id " + alarm.id);
            AlarmHelper.tryDeleteOneshotAlarm(context, alarm);
            AlarmHelper.setNextAlert(context);
        }
        BackScreenNotificationUtil.clearAlarmNotification(context);
        AlarmSettingsActivity.resetSnoozeRepeatCountRemind();
        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_SNOOZED_DISMISS_FROM_NOTIFICATION_CLICK);
        StatHelper.alarmEvent(StatHelper.EVENT_ALARM_SNOOZED_DISMISS_NOTIFICATION);
    }
}
