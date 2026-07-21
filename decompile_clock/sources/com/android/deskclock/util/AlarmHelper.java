package com.android.deskclock.util;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.widget.Toast;
import androidx.fragment.app.FragmentManager;
import com.android.deskclock.Alarm;
import com.android.deskclock.AlarmBootInitReceiver;
import com.android.deskclock.AlarmClockExtras;
import com.android.deskclock.BuildConfig;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.R2;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.weather.WeatherUtils;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper;
import com.android.deskclock.alarm.AlarmClockFragment;
import com.android.deskclock.alarm.AlarmColorLightManager;
import com.android.deskclock.alarm.ReflectUtil;
import com.android.deskclock.alarm.alert.AlarmService;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.alarm.bedtime.HealthDataUtil;
import com.android.deskclock.alarm.bedtime.SleepAlarmTable;
import com.android.deskclock.alarm.bedtime.ZenModeUtil;
import com.android.deskclock.alarm.lifepost.LifePostUtils;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmAlertHelper;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmDataHelper;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmGroup;
import com.android.deskclock.appaf.AppSearchUtil;
import com.android.deskclock.compat.ClockCompat;
import com.android.deskclock.timer.TimerService;
import com.android.deskclock.util.Notification.BackScreenNotificationUtil;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.util.themeringtone.RingtoneHelper;
import com.miui.miwallpaper.MiuiWallpaperManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import miuix.os.AsyncTaskWithProgress;

/* JADX INFO: loaded from: classes.dex */
public class AlarmHelper {
    public static final String ACTION_ALARM_CANCEL = "action.alarm_cancel";
    public static final String ACTION_ALARM_DISMISS = "action.alarm_dismiss";
    public static final String ACTION_ALARM_DISMISS_FROM_ADDITIONS = "action.alarm_dismiss_additions";
    public static final String ACTION_ALARM_SNOOZE = "action.alarm_snooze";
    public static final String ACTION_ALARM_SNOOZE_FROM_ADDITIONS = "action.alarm_snooze_additions";
    public static final String ACTION_ALERT_UI_DISMISS = "com.android.deskclock.ALERT_UI_DISMISS";
    public static final String ACTION_ENTER_ZENMODE = "com.android.deskclock.ENTER_ZENMODE";
    public static final String ACTION_KEY_DISMISS_ALARM_TO_XIAOAI = "com.miui.voiceassist.action.key_dismiss_alarm_to_xiaoai";
    public static final String ACTION_KEY_DISMISS_TIMER_TO_XIAOAI = "com.miui.voiceassist.action.key_dismiss_timer_to_xiaoai";
    public static final String ACTION_REACH_WAKE_TIME = "com.android.deskclock.REACH_WAKE_TIME";
    public static final String ACTION_SNOOZE_CANCEL = "action.snooze_cancel";
    public static final String ACTION_SNOOZE_CANCEL_AGAIN = "action.snooze_cancel_again";
    public static final String ACTION_TIMER_ALARM_TYPE = "action.timer_alarm_type";
    public static final String ACTION_TIMER_DISMISS = "action.timer_dismiss";
    public static final String ACTION_TIMER_DURATION = "action.timer_duration";
    public static final String ACTION_TIMER_NAME = "action.timer_name";
    public static final String ACTION_TIMER_OFF = "action.timer_off";
    public static final String ACTION_TIMER_START = "action.timer.start";
    public static final String ACTION_TIMER_STOP = "action.timer.stop";
    public static final String ALARM_ALERT_ACTION = "com.android.deskclock.ALARM_ALERT";
    public static final String ALARM_ALERT_SILENT = "silent";
    public static final String ALARM_ALERT_STATUS = "alarm_alert_status";
    public static final String ALARM_ARRIVING_ACTION = "com.android.deskclock.ALARM_ARRIVING";
    public static final String ALARM_ARRIVING_TRANSPARENT_ACTION = "com.android.deskclock.ALARM_ARRIVING_TRANSPARENT";
    public static final String ALARM_ARRIVING_TRANSPARENT_ACTION_FOR_XIAOAI = "com.android.deskclock.ALARM_ARRIVING_TRANSPARENT_FOR_XIAOAI";
    public static final String ALARM_DATA_PREPARE_ACTION = "com.android.deskclock.ALARM_DATA_PREPARE";
    public static final String ALARM_DISMISS_ACTION = "com.android.deskclock.ALARM_DISMISS";
    public static final String ALARM_DONE_ACTION = "com.android.deskclock.ALARM_DONE";
    public static final String ALARM_EXTRA_SHOW_WHEN_LOCKED = "intent.extra.alarm.show_when_locked";
    public static final String ALARM_INTENT_EXTRA = "intent.extra.alarm";
    public static final String ALARM_RAW_DATA = "intent.extra.alarm_raw";
    public static final String ALARM_SNOOZE_ACTION = "com.android.deskclock.ALARM_SNOOZE";
    public static final String ALARM_STOP_ACTION = "com.android.deskclock.ALARM_STOP";
    public static final long ARRIVING_ALARM_DURATION = 3600000;
    public static final long ARRIVING_ALARM_TRANSPARENT_DURATION = 900000;
    private static final long ARRIVING_ALARM_TRANSPARENT_FOR_XIAO_AI_ADVANCE = 1800000;
    private static final String DM12 = "Ehma";
    private static final String DM24 = "EHm";
    public static final String FUNCTION_TIMER_DISMISS = "function.timer_dismiss";
    public static final int INVALID_ALARM_ID = -1;
    public static final String KEY_SLEEP_TIME = "sleep_time";
    public static final String LOCKED_SCREEN_ALARM_IS_SHOW = "locked_screen_alarm_is_show";
    public static final String LOCKED_SCREEN_ALARM_STATUS = "locked_screen_alarm_status";
    private static final String M12 = "h:mm aa";
    private static final String M12_ALARM = "h:mm";
    public static final String M24 = "HH:mm";
    public static final String NEXT_ALARM_LONG_ID = "next_alarm_clock_id";
    public static final String NEXT_ALARM_LONG_TIME = "next_alarm_clock_long";
    private static final String PREF_ONESHOT_ALARMS = "oneshot_alarms";
    public static final String PREF_SNOOZE_IDS = "snooze_ids";
    private static final String PREF_SNOOZE_TIME = "snooze_time";
    public static final String SETTINGS_NEXT_ALARM_TIME = "next_alarm_clock_formatted";
    public static final int SLEEP_ALARM_ID = -2147483647;
    public static final String SLEEP_ALARM_NOTIFICATION_ACTION = "com.android.deskclock.SLEEP_ALARM_NOTIFICATION";
    private static final String TAG = "DC:AlarmHelper";
    public static final int TIMER_ALARM_ID = -2;
    public static final long UPDATE_WEATHER_DURATION = 1800000;
    public static final int WAKE_ALARM_ID = Integer.MIN_VALUE;
    public static final String XIAOAI_RINGTONE_QUERY_ACTION = "com.android.deskclock.XIAOAI_RINGTONE_QUERY_ACTION";
    private static boolean is24HourMode;
    private static MiuiWallpaperManager mMiuiWallpaperManager;

    public static long addAlarm(Context context, Alarm alarm) {
        Log.d("AlarmHelper addAlarm()");
        alarm.id = (int) ContentUris.parseId(context.getContentResolver().insert(Alarm.Columns.CONTENT_URI, createContentValues(context, alarm)));
        long jCalculateAlarmTime = calculateAlarmTime(context, alarm);
        setNextAlert(context);
        RingtoneHelper.handleAlert(alarm);
        return jCalculateAlarmTime;
    }

    public static void deleteAlarm(Context context, boolean z, Integer... numArr) {
        for (Integer num : numArr) {
            deleteAlarmInDb(context, num.intValue());
        }
        setNextAlert(context);
    }

    public static void deleteAlarm(Context context, Integer... numArr) {
        deleteAlarm(context, true, numArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void deleteAlarmInDb(Context context, int i) {
        if (context == null) {
            return;
        }
        ContentResolver contentResolver = context.getContentResolver();
        if (i != -1) {
            disableSnoozeAlert(context, i);
            contentResolver.delete(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, i), "", null);
        }
    }

    public static void deleteAlarmsFromAppSearch(int[] iArr) {
        if (iArr == null) {
            return;
        }
        for (int i : iArr) {
            AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).deleteAppSearchAlarm(i);
        }
    }

    /* JADX WARN: Type inference failed for: r5v1, types: [com.android.deskclock.util.AlarmHelper$1] */
    public static void deleteAlarmsAsync(final Context context, final int[] iArr, FragmentManager fragmentManager) {
        if (iArr == null || context == null) {
            return;
        }
        if (iArr.length < 10) {
            new AsyncTask<Void, Void, Void>() { // from class: com.android.deskclock.util.AlarmHelper.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public Void doInBackground(Void... voidArr) {
                    int i = 0;
                    while (true) {
                        int[] iArr2 = iArr;
                        if (i >= iArr2.length) {
                            return null;
                        }
                        if (!ShiftAlarmDataHelper.isShiftAlarm(iArr2[i])) {
                            AlarmHelper.deleteAlarmInDb(context, iArr[i]);
                        } else {
                            ShiftAlarmDataHelper.deleteShiftAlarmByAlarmId(iArr[i]);
                        }
                        i++;
                    }
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(Void r1) {
                    AlarmHelper.setNextAlert(context);
                }
            }.execute(new Void[0]);
        } else {
            new AsyncTaskWithProgress<Void, Void>(fragmentManager) { // from class: com.android.deskclock.util.AlarmHelper.2
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public Void doInBackground(Void... voidArr) {
                    int i = 0;
                    while (true) {
                        int[] iArr2 = iArr;
                        if (i >= iArr2.length) {
                            return null;
                        }
                        if (!ShiftAlarmDataHelper.isShiftAlarm(iArr2[i])) {
                            AlarmHelper.deleteAlarmInDb(context, iArr[i]);
                        } else {
                            ShiftAlarmDataHelper.deleteShiftAlarmByAlarmId(iArr[i]);
                        }
                        publishProgress(new Integer[]{Integer.valueOf((int) ((i / iArr.length) * 100.0f))});
                        i++;
                    }
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // miuix.os.AsyncTaskWithProgress, android.os.AsyncTask
                public void onPostExecute(Void r1) {
                    super.onPostExecute(r1);
                    AlarmHelper.setNextAlert(context);
                }
            }.setCancelable(false).setIndeterminate(false).setMaxProgress(100).setProgressStyle(1).execute(new Void[0]);
        }
    }

    public static Cursor getAlarmsCursor(ContentResolver contentResolver) {
        return contentResolver.query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS, "type=?", new String[]{String.valueOf(0)}, Alarm.Columns.DEFAULT_SORT_ORDER);
    }

    public static Cursor getFilteredAlarmsCursor(ContentResolver contentResolver) {
        return contentResolver.query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS, Alarm.Columns.WHERE_ENABLED, null, null);
    }

    public static Cursor getFilteredOrSkippedAlarmsCursor(ContentResolver contentResolver) {
        return contentResolver.query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS, Alarm.Columns.WHERE_ENABLED_OR_SKIPPED, null, null);
    }

    public static ContentValues createContentValues(Context context, Alarm alarm) {
        long jCalculateAlarmTime;
        ContentValues contentValues = new ContentValues();
        if (!alarm.daysOfWeek.isRepeatSet()) {
            jCalculateAlarmTime = calculateAlarmTime(context, alarm);
        } else {
            if (alarm.enabled) {
                alarm.skipTime = 0L;
            }
            if (alarm.skipTime != 0) {
                if (alarm.skipTime != calculateAlarmTime(context, alarm.hour, alarm.minutes, alarm.daysOfWeek).getTimeInMillis()) {
                    alarm.skipTime = 0L;
                }
            }
            jCalculateAlarmTime = 0;
        }
        contentValues.put("enabled", Integer.valueOf(alarm.enabled ? 1 : 0));
        contentValues.put("hour", Integer.valueOf(alarm.hour));
        contentValues.put("minutes", Integer.valueOf(alarm.minutes));
        contentValues.put("alarmtime", Long.valueOf(jCalculateAlarmTime));
        contentValues.put("daysofweek", Integer.valueOf(alarm.daysOfWeek.getCoded()));
        contentValues.put("vibrate", Boolean.valueOf(alarm.vibrate));
        contentValues.put("message", alarm.label);
        contentValues.put("skiptime", Long.valueOf(alarm.skipTime));
        contentValues.put("deleteAfterUse", Integer.valueOf(alarm.deleteAfterUse ? 1 : 0));
        String string = "silent";
        contentValues.put("alert", alarm.alert == null || AlarmClockExtras.NO_RINGTONE.equals(alarm.alert.toString()) ? "silent" : alarm.alert.toString());
        StringBuilder sbAppend = new StringBuilder("AlarmHelper createContentValues(), alarm.enabled = ").append(alarm.enabled).append(",alarm.hour= ").append(alarm.hour).append(",alarm.minutes = ").append(alarm.minutes).append(", time = ").append(jCalculateAlarmTime).append(", alarm.daysOfWeek.getCoded() = ").append(alarm.daysOfWeek.getCoded()).append(",alarm.vibrate = ").append(alarm.vibrate).append(",alarm.skipTime = ").append(alarm.skipTime).append(",alarm.alert = ");
        if (alarm.alert != null) {
            string = alarm.alert.toString();
        }
        Log.d(sbAppend.append(string).append(",alarm.deleteAfterUse = ").append(alarm.deleteAfterUse).toString());
        return contentValues;
    }

    public static Alarm getAlarm(ContentResolver contentResolver, int i) {
        Cursor cursorQuery;
        if (i == Integer.MIN_VALUE) {
            cursorQuery = BedtimeUtil.queryWakeAlarm(DeskClockApp.getAppDEContext());
        } else {
            cursorQuery = contentResolver.query(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, i), Alarm.Columns.ALARM_QUERY_COLUMNS, null, null, null);
        }
        Alarm alarm = null;
        if (cursorQuery != null) {
            try {
                alarm = cursorQuery.moveToFirst() ? new Alarm(cursorQuery) : null;
            } finally {
                cursorQuery.close();
            }
        }
        return alarm;
    }

    public static long setAlarm(Context context, Alarm alarm) {
        context.getContentResolver().update(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarm.id), createContentValues(context, alarm), null, null);
        long jCalculateAlarmTime = calculateAlarmTime(context, alarm);
        if (alarm.enabled) {
            disableSnoozeAlert(context, alarm.id);
        }
        setNextAlert(context);
        return jCalculateAlarmTime;
    }

    public static void enableAlarm(Context context, int i, boolean z) {
        enableAlarmInternal(context, i, z, false);
        setNextAlert(context);
    }

    public static void skipAlarmForOnce(Context context, int i) {
        enableAlarmInternal(context, i, false, true);
        setNextAlert(context);
    }

    private static void enableAlarmInternal(Context context, int i, boolean z, boolean z2) {
        enableAlarmInternal(context, getAlarm(context.getContentResolver(), i), z, z2);
    }

    private static void enableAlarmInternal(Context context, Alarm alarm, boolean z, boolean z2) {
        if (alarm == null) {
            return;
        }
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues(2);
        contentValues.put("enabled", Integer.valueOf(z ? 1 : 0));
        if (z || !z2) {
            contentValues.put("skiptime", (Integer) 0);
        } else {
            contentValues.put("skiptime", Long.valueOf(calculateAlarmTime(context, alarm)));
        }
        if (z) {
            contentValues.put("alarmtime", Long.valueOf(!alarm.daysOfWeek.isRepeatSet() ? calculateAlarmTime(context, alarm) : 0L));
        } else {
            disableSnoozeAlert(context, alarm.id);
        }
        if (alarm.id == Integer.MIN_VALUE) {
            contentResolver.update(SleepAlarmTable.CONTENT_URI, contentValues, null, null);
            if (z) {
                Log.f(TAG, "cancel ACTION_REACH_WAKE_TIME");
                AlarmUtils.cancelAlarm(context, ACTION_REACH_WAKE_TIME);
            }
        } else {
            contentResolver.update(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarm.id), contentValues, null, null);
        }
        Log.f(String.format("enableAlarmInternal id:%d, enabled:%s, skip:%s", Integer.valueOf(alarm.id), Boolean.valueOf(z), Boolean.valueOf(z2)));
    }

    public static Alarm calculateSnoozeAlarmNextAlert(Context context) {
        HashSet hashSet = new HashSet();
        SharedPreferences sharedPreferences = FBEUtil.getSharedPreferences(context, AlarmClockFragment.PREFERENCES, 0);
        long j = Long.MAX_VALUE;
        Alarm alarm = null;
        for (String str : Collections.unmodifiableSet(sharedPreferences.getStringSet(PREF_SNOOZE_IDS, new HashSet()))) {
            Log.i("DC:calculateNextAlert", "snoozedAlarm contains : " + str);
            int i = Integer.parseInt(str);
            if (i != -2) {
                Alarm alarm2 = getAlarm(context.getContentResolver(), i);
                updateAlarmTimeForSnooze(sharedPreferences, alarm2);
                if (alarm2 != null) {
                    hashSet.add(alarm2);
                }
                if (alarm2 != null && alarm2.time < j) {
                    j = alarm2.time;
                    alarm = alarm2;
                }
            }
        }
        return alarm;
    }

    /* JADX WARN: Code duplicated, block: B:78:0x01d6  */
    /* JADX WARN: Code duplicated, block: B:84:0x0205  */
    public static Alarm calculateNextAlert(Context context) {
        String str;
        SharedPreferences sharedPreferences;
        String str2;
        String str3;
        Alarm alarm;
        long jCurrentTimeMillis = System.currentTimeMillis();
        SharedPreferences sharedPreferences2 = FBEUtil.getSharedPreferences(context, AlarmClockFragment.PREFERENCES, 0);
        CopyOnWriteArraySet<Alarm> copyOnWriteArraySet = new CopyOnWriteArraySet();
        Iterator it = Collections.unmodifiableSet(new HashSet(sharedPreferences2.getStringSet(PREF_SNOOZE_IDS, new HashSet()))).iterator();
        while (true) {
            boolean zHasNext = it.hasNext();
            str = TAG;
            if (!zHasNext) {
                break;
            }
            String str4 = (String) it.next();
            Log.i("DC:calculateNextAlert", "snoozedAlarm contains : " + str4);
            try {
                int i = Integer.parseInt(str4);
                if (i != -2 && (alarm = getAlarm(context.getContentResolver(), i)) != null) {
                    if (alarm.type == 2) {
                        ShiftAlarmGroup shiftGroupFromAlarmId = ShiftAlarmDataHelper.getShiftGroupFromAlarmId(alarm.id);
                        alarm.silent = shiftGroupFromAlarmId.silent;
                        alarm.alert = shiftGroupFromAlarmId.alert;
                        alarm.label = shiftGroupFromAlarmId.label;
                        alarm.vibrate = shiftGroupFromAlarmId.vibrate;
                        updateAlarmTimeForSnooze(sharedPreferences2, alarm);
                    }
                    copyOnWriteArraySet.add(alarm);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid alarm ID in snoozed list: " + e);
            }
        }
        Cursor filteredOrSkippedAlarmsCursor = getFilteredOrSkippedAlarmsCursor(context.getContentResolver());
        if (filteredOrSkippedAlarmsCursor != null) {
            try {
                if (filteredOrSkippedAlarmsCursor.moveToFirst()) {
                    do {
                        copyOnWriteArraySet.add(new Alarm(filteredOrSkippedAlarmsCursor));
                    } while (filteredOrSkippedAlarmsCursor.moveToNext());
                }
                filteredOrSkippedAlarmsCursor.close();
            } catch (Throwable th) {
                filteredOrSkippedAlarmsCursor.close();
                throw th;
            }
        }
        int i2 = Integer.MIN_VALUE;
        long j = 0;
        if (BedtimeUtil.isWakeAlarmSupport(context)) {
            Alarm alarm2 = getAlarm(context.getContentResolver(), Integer.MIN_VALUE);
            if (alarm2 != null && alarm2.enabled) {
                copyOnWriteArraySet.add(alarm2);
            } else if (alarm2 != null && !alarm2.enabled && alarm2.skipTime != 0) {
                copyOnWriteArraySet.add(alarm2);
            }
        }
        Alarm nextShiftAlarm = ShiftAlarmAlertHelper.getNextShiftAlarm();
        Log.i(TAG, "shiftAlarm: " + nextShiftAlarm);
        if (nextShiftAlarm != null) {
            if (!copyOnWriteArraySet.contains(nextShiftAlarm)) {
                copyOnWriteArraySet.add(nextShiftAlarm);
            } else {
                Log.i(TAG, "next shift-alert-alarm in snoozes");
                Iterator it2 = copyOnWriteArraySet.iterator();
                while (true) {
                    if (it2.hasNext()) {
                        Alarm alarm3 = (Alarm) it2.next();
                        if (alarm3.id == nextShiftAlarm.id) {
                            sharedPreferences = sharedPreferences2;
                            if (alarm3.time < nextShiftAlarm.time) {
                                break;
                            }
                            disableSnoozeAlert(context, alarm3.id);
                            copyOnWriteArraySet.remove(alarm3);
                            copyOnWriteArraySet.add(nextShiftAlarm);
                            break;
                        }
                    }
                }
            }
            sharedPreferences = sharedPreferences2;
            break;
        }
        sharedPreferences = sharedPreferences2;
        break;
        Alarm alarm4 = null;
        long j2 = Long.MAX_VALUE;
        Alarm alarm5 = null;
        for (Alarm alarm6 : copyOnWriteArraySet) {
            if (alarm6.id == i2) {
                alarm4 = alarm6;
            }
            if (alarm6.time == j) {
                alarm6.time = calculateAlarmTime(context, alarm6);
                str2 = str;
                if (alarm6.time <= alarm6.skipTime) {
                    enableAlarmInternal(context, alarm6, true, false);
                    alarm6.enabled = true;
                    alarm6.skipTime = j;
                    alarm6.time = calculateAlarmTime(context, alarm6);
                }
            } else {
                str2 = str;
            }
            SharedPreferences sharedPreferences3 = sharedPreferences;
            updateAlarmTimeForSnooze(sharedPreferences3, alarm6);
            if (alarm6.time >= jCurrentTimeMillis || (isShutdownAlarmEnabled(context) && SystemClock.elapsedRealtime() <= 180000)) {
                str3 = str2;
                if (alarm6.time < j2) {
                    alarm5 = alarm6;
                    j2 = alarm6.time;
                }
                if (alarm6.skipTime > 0 || alarm6.skipTime >= jCurrentTimeMillis) {
                    j = 0;
                } else {
                    Log.f(str3, "Reopen skipped alarm, id " + alarm6.id);
                    enableAlarmInternal(context, alarm6, true, false);
                    j = 0;
                    alarm6.skipTime = 0L;
                }
            } else if (alarm6.daysOfWeek.isRepeatSet()) {
                clearSnoozePreference(context, sharedPreferences3, alarm6.id, true);
                alarm6.time = calculateAlarmTime(context, alarm6);
                str3 = str2;
                if (alarm6.time < j2) {
                    alarm5 = alarm6;
                    j2 = alarm6.time;
                }
                if (alarm6.skipTime > 0) {
                    j = 0;
                } else {
                    j = 0;
                }
            } else {
                str3 = str2;
                Log.f(str3, "Disabling expired alarm set for " + Util.formatTimeForLog(alarm6.time));
                enableAlarmInternal(context, alarm6, false, false);
            }
            sharedPreferences = sharedPreferences3;
            str = str3;
            i2 = Integer.MIN_VALUE;
        }
        return (alarm4 == null || alarm4.time != alarm5.time) ? alarm5 : alarm4;
    }

    public static void disableExpiredAlarms(Context context, long j) {
        Cursor filteredAlarmsCursor = getFilteredAlarmsCursor(context.getContentResolver());
        if (filteredAlarmsCursor == null) {
            return;
        }
        try {
            if (filteredAlarmsCursor.moveToFirst()) {
                do {
                    Alarm alarm = new Alarm(filteredAlarmsCursor);
                    if (alarm.time != 0 && alarm.time < j) {
                        Log.f(TAG, "Disabling expired alarm set for " + Util.formatTimeForLog(alarm.time));
                        enableAlarmInternal(context, alarm, false, false);
                    }
                } while (filteredAlarmsCursor.moveToNext());
            }
        } finally {
            filteredAlarmsCursor.close();
        }
    }

    public static void setNextAlert(Context context) {
        NotificationUtil.clearAlarmArrivingNotification(context);
        clearAlarmArrivingAlarm(context);
        Alarm alarmCalculateNextAlert = calculateNextAlert(context);
        if (alarmCalculateNextAlert != null) {
            enableAlarmBootInitReceiver(context, true);
            enableAlert(context, alarmCalculateNextAlert, alarmCalculateNextAlert.time);
            setAlarmArrivingNotification(context, alarmCalculateNextAlert);
            saveNextAlarmTime(context, alarmCalculateNextAlert.time);
            SharedPreferences sharedPreferences = FBEUtil.getSharedPreferences(context, AlarmClockFragment.PREFERENCES, 0);
            if (Util.supportShutdownAlarm() && FBEUtil.getDefaultSharedPreferences(context).getBoolean("shutdown_alarm", true) && !hasAlarmBeenSnoozed(sharedPreferences, alarmCalculateNextAlert.id)) {
                ShutdownAlarm.setWakeAlarm(context, alarmCalculateNextAlert.time / 1000);
            }
            RingtoneHelper.handleAlert(alarmCalculateNextAlert);
        } else {
            enableAlarmBootInitReceiver(context, false);
            disableAlert(context);
            saveNextAlarmTime(context, 0L);
            ShutdownAlarm.disableWakeAlarm();
        }
        setAlarmStatusRemind(context, alarmCalculateNextAlert);
    }

    private static void setAlarmStatusRemind(Context context, Alarm alarm) {
        boolean z = FBEUtil.getDefaultSharedPreferences(context).getBoolean("locked_show_alarm_arriving_state", true);
        Log.d(TAG, "shouldShowAlarmAtLocked: " + z + ", alarm: " + alarm);
        try {
            Settings.Secure.putInt(context.getContentResolver(), LOCKED_SCREEN_ALARM_IS_SHOW, z ? 1 : 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!z || alarm == null) {
            try {
                Settings.Secure.putLong(context.getContentResolver(), LOCKED_SCREEN_ALARM_STATUS, 0L);
                return;
            } catch (Exception e2) {
                e2.printStackTrace();
                return;
            }
        }
        try {
            Settings.Secure.putLong(context.getContentResolver(), LOCKED_SCREEN_ALARM_STATUS, alarm.time);
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    private static void enableAlert(Context context, Alarm alarm, long j) {
        Log.f(TAG, "register alarm to AlarmManager: " + alarm.toString());
        Intent intent = new Intent(context, (Class<?>) AlarmService.class);
        intent.setAction(ALARM_ALERT_ACTION);
        putAlarmRawDataToIntent(alarm, intent);
        AlarmUtils.setServiceAlarm(context, j, intent);
        setStatusBarIcon(context, true);
    }

    private static void setAlarmArrivingNotification(final Context context, final Alarm alarm) {
        final SharedPreferences sharedPreferences = FBEUtil.getSharedPreferences(context, AlarmClockFragment.PREFERENCES, 0);
        try {
            Settings.Global.putLong(context.getContentResolver(), NEXT_ALARM_LONG_ID, alarm.id);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.util.AlarmHelper.3
            @Override // java.lang.Runnable
            public void run() {
                Log.f("XiaoAiRingtone alarm: " + alarm + "  title:" + Util.getRingtoneTitle(context, alarm.alert));
                if (!sharedPreferences.getStringSet(XiaoAiRingtoneHelper.XIAOAI_RINGTONE_IDS, new HashSet()).contains(String.valueOf(alarm.id)) && Util.getRingtoneTitle(context, alarm.alert).equals(context.getString(R.string.xiaoai_ringtone_title))) {
                    XiaoAiRingtoneHelper.addXiaoAiRingtoneIds(context, alarm.id);
                }
                AlarmHelper.prepareXiaoAiRingtone(sharedPreferences, alarm, context);
            }
        });
        if (hasAlarmBeenSnoozed(sharedPreferences, alarm.id)) {
            return;
        }
        boolean z = FBEUtil.getDefaultSharedPreferences(context).getBoolean("alarm_arriving_notification", true);
        setMiuiWallpaperManager(Util.getMiuiWallpaperManager());
        if (z) {
            if (alarm.time <= System.currentTimeMillis() + ARRIVING_ALARM_DURATION) {
                NotificationUtil.showAlarmArrivingNotification(context, alarm);
            } else {
                Intent intent = new Intent(ALARM_ARRIVING_ACTION);
                intent.setPackage(context.getPackageName());
                putAlarmRawDataToIntent(alarm, intent);
                AlarmUtils.setAlarm(context, alarm.time - ARRIVING_ALARM_DURATION, intent);
            }
        }
        if (alarm.time <= System.currentTimeMillis() + ARRIVING_ALARM_TRANSPARENT_DURATION) {
            WeatherUtils.updateWeatherBroadcast(alarm, context, true);
            LifePostUtils.executeLifePostDataLoadTask(alarm);
        } else {
            Intent intent2 = new Intent(ALARM_ARRIVING_TRANSPARENT_ACTION);
            intent2.setPackage(context.getPackageName());
            putAlarmRawDataToIntent(alarm, intent2);
            AlarmUtils.setAlarm(context, alarm.time - ARRIVING_ALARM_TRANSPARENT_DURATION, intent2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void prepareXiaoAiRingtone(SharedPreferences sharedPreferences, Alarm alarm, Context context) {
        if (sharedPreferences == null || alarm == null || context == null) {
            return;
        }
        XiaoAiRingtoneHelper.resetEnableValue();
        if (XiaoAiRingtoneHelper.isAvailable()) {
            Set<String> stringSet = sharedPreferences.getStringSet(XiaoAiRingtoneHelper.XIAOAI_RINGTONE_IDS, new HashSet());
            Log.f(XiaoAiRingtoneHelper.TAG, " snoozedIds: " + stringSet + " alarm.id: " + alarm.id);
            if (stringSet.contains(String.valueOf(alarm.id)) || Util.getRingtoneTitle(context, alarm.alert).equals(context.getString(R.string.xiaoai_ringtone_title))) {
                if (alarm.time <= System.currentTimeMillis() + ARRIVING_ALARM_TRANSPARENT_DURATION) {
                    Log.f(XiaoAiRingtoneHelper.TAG, "update xiaoai rt immediately, less than 15 mins");
                    XiaoAiRingtoneHelper.sendBroadCastForUpdate(context, alarm);
                } else if (alarm.time <= System.currentTimeMillis() + 1800000) {
                    long jCurrentTimeMillis = System.currentTimeMillis() + ((long) (Math.random() * ((alarm.time - ARRIVING_ALARM_TRANSPARENT_DURATION) - System.currentTimeMillis())));
                    setSmartRtPreAction(context, alarm, jCurrentTimeMillis);
                    Log.f(XiaoAiRingtoneHelper.TAG, "update xiaoai, less than 30 mins, update time: " + Util.formatTimeForLog(jCurrentTimeMillis));
                } else {
                    long jRandom = (alarm.time - 1800000) + ((long) (Math.random() * 900000.0d));
                    Log.f(XiaoAiRingtoneHelper.TAG, "update xiaoai, more than 30 mins update time : " + Util.formatTimeForLog(jRandom));
                    setSmartRtPreAction(context, alarm, jRandom);
                }
                long j = alarm.time - 60000;
                Log.f(XiaoAiRingtoneHelper.TAG, "query xiaoai at " + Util.formatTimeForLog(j));
                if (System.currentTimeMillis() < j) {
                    Intent intent = new Intent(XIAOAI_RINGTONE_QUERY_ACTION);
                    intent.setPackage(context.getPackageName());
                    putAlarmRawDataToIntent(alarm, intent);
                    AlarmUtils.setAlarm(context, j, intent);
                }
            }
        }
    }

    private static void setSmartRtPreAction(Context context, Alarm alarm, long j) {
        Intent intent = new Intent(ALARM_ARRIVING_TRANSPARENT_ACTION_FOR_XIAOAI);
        intent.setPackage(context.getPackageName());
        putAlarmRawDataToIntent(alarm, intent);
        AlarmUtils.setAlarm(context, j, intent);
    }

    public static void setDatePrepareAlarm(Context context, Alarm alarm, long j) {
        Intent intent = new Intent(ALARM_DATA_PREPARE_ACTION);
        intent.setPackage(context.getPackageName());
        putAlarmRawDataToIntent(alarm, intent);
        AlarmUtils.setAlarm(context, j, intent);
    }

    private static void clearAlarmArrivingAlarm(Context context) {
        Log.f(TAG, "clearAlarmArrivingAlarm");
        AlarmUtils.cancelAlarm(context, ALARM_ARRIVING_ACTION);
        AlarmUtils.cancelAlarm(context, ALARM_ARRIVING_TRANSPARENT_ACTION);
        AlarmUtils.cancelAlarm(context, ALARM_DATA_PREPARE_ACTION);
        AlarmUtils.cancelAlarm(context, ALARM_ARRIVING_TRANSPARENT_ACTION_FOR_XIAOAI);
        AlarmUtils.cancelAlarm(context, XIAOAI_RINGTONE_QUERY_ACTION);
    }

    private static void disableAlert(Context context) {
        Intent intent = new Intent(context, (Class<?>) AlarmService.class);
        intent.setAction(ALARM_ALERT_ACTION);
        AlarmUtils.cancelServiceAlarm(context, intent);
        setStatusBarIcon(context, false);
    }

    public static void saveSnoozeAlert(Context context, int i, long j) {
        Log.f(TAG, "save snooze , id : " + i + " at " + ((Object) DateFormat.format("MM-dd kk:mm", j)));
        SharedPreferences sharedPreferences = FBEUtil.getSharedPreferences(context, AlarmClockFragment.PREFERENCES, 0);
        if (i == -1) {
            clearAllSnoozePreferences(context, sharedPreferences);
        } else {
            Set<String> stringSet = sharedPreferences.getStringSet(PREF_SNOOZE_IDS, new HashSet());
            stringSet.add(Integer.toString(i));
            SharedPreferences.Editor editorEdit = sharedPreferences.edit();
            editorEdit.putStringSet(PREF_SNOOZE_IDS, stringSet);
            editorEdit.putLong(getAlarmPrefSnoozeTimeKey(i), j);
            editorEdit.apply();
            StatHelper.alarmEvent(StatHelper.EVENT_ALARM_SNOOZED_BY_ALL);
        }
        Log.f(TAG, "Snooze alarm, id=" + i + ", time=" + Util.formatTimeForLog(j));
        setNextAlert(context);
    }

    public static String getAlarmPrefSnoozeTimeKey(int i) {
        return getAlarmPrefSnoozeTimeKey(Integer.toString(i));
    }

    private static String getAlarmPrefSnoozeTimeKey(String str) {
        return PREF_SNOOZE_TIME + str;
    }

    public static void cancelSnoozedAlarm(Context context, int i) {
        NotificationUtil.clearAlarmSnoozeNotification(context, i);
        disableSnoozeAlert(context, i);
    }

    public static void disableSnoozeAlert(Context context, int i) {
        disableSnoozeAlert(context, i, true);
    }

    public static void disableSnoozeAlert(Context context, int i, boolean z) {
        SharedPreferences sharedPreferences = FBEUtil.getSharedPreferences(context, AlarmClockFragment.PREFERENCES, 0);
        if (hasAlarmBeenSnoozed(sharedPreferences, i)) {
            clearSnoozePreference(context, sharedPreferences, i, z);
        }
    }

    private static void clearSnoozePreference(Context context, SharedPreferences sharedPreferences, int i, boolean z) {
        String string = Integer.toString(i);
        Set<String> stringSet = sharedPreferences.getStringSet(PREF_SNOOZE_IDS, new HashSet());
        if (stringSet.contains(string)) {
            if (z) {
                BackScreenNotificationUtil.clearAlarmNotification(context);
            }
            NotificationUtil.clearAlarmSnoozeNotification(context, i);
            StatHelper.alarmEvent(StatHelper.EVENT_CANCEL_ALARM_SNOOZED_BY_OTHER);
        }
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        stringSet.remove(string);
        editorEdit.putStringSet(PREF_SNOOZE_IDS, stringSet);
        editorEdit.remove(getAlarmPrefSnoozeTimeKey(string));
        editorEdit.apply();
    }

    private static void clearAllSnoozePreferences(Context context, SharedPreferences sharedPreferences) {
        Log.d(TAG, "clearAllSnoozePreferences");
        Set<String> stringSet = sharedPreferences.getStringSet(PREF_SNOOZE_IDS, new HashSet());
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        for (String str : stringSet) {
            NotificationUtil.clearAlarmSnoozeNotification(context, Integer.parseInt(str));
            editorEdit.remove(getAlarmPrefSnoozeTimeKey(str));
            StatHelper.alarmEvent(StatHelper.EVENT_CANCEL_ALARM_SNOOZED_BY_BOOT_COMPLETED);
        }
        editorEdit.remove(PREF_SNOOZE_IDS);
        editorEdit.apply();
    }

    public static boolean hasAlarmBeenSnoozed(SharedPreferences sharedPreferences, int i) {
        Set<String> stringSet = sharedPreferences.getStringSet(PREF_SNOOZE_IDS, null);
        return stringSet != null && stringSet.contains(Integer.toString(i));
    }

    public static boolean hasEnabledLegalWorkdayAlarm(ContentResolver contentResolver) {
        Cursor cursorQuery = null;
        try {
            boolean zMoveToFirst = false;
            cursorQuery = contentResolver.query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS, "enabled=? and daysofweek=?", new String[]{"1", String.valueOf(128)}, null);
            if (cursorQuery != null && (zMoveToFirst = cursorQuery.moveToFirst())) {
                Log.d("hasEnabledLegalWorkdayAlarm(), and id is " + new Alarm(cursorQuery).id);
            }
            return zMoveToFirst;
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    public static boolean hasEnabledLegalHolidayAlarm(ContentResolver contentResolver) {
        Cursor cursorQuery = null;
        try {
            boolean zMoveToFirst = false;
            cursorQuery = contentResolver.query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS, "enabled=? and daysofweek=?", new String[]{"1", String.valueOf(256)}, null);
            if (cursorQuery != null && (zMoveToFirst = cursorQuery.moveToFirst())) {
                Log.d("hasEnabledLegalHolidayAlarm(), and id is " + new Alarm(cursorQuery).id);
            }
            return zMoveToFirst;
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    private static boolean updateAlarmTimeForSnooze(SharedPreferences sharedPreferences, Alarm alarm) {
        if (alarm == null || !hasAlarmBeenSnoozed(sharedPreferences, alarm.id)) {
            return false;
        }
        alarm.time = sharedPreferences.getLong(getAlarmPrefSnoozeTimeKey(alarm.id), -1L);
        return true;
    }

    public static long getAlarmTimeForSnooze(Context context, Alarm alarm) {
        SharedPreferences sharedPreferences = FBEUtil.getSharedPreferences(context, AlarmClockFragment.PREFERENCES, 0);
        if (!hasAlarmBeenSnoozed(sharedPreferences, alarm.id)) {
            return alarm.time;
        }
        return sharedPreferences.getLong(getAlarmPrefSnoozeTimeKey(alarm.id), -1L);
    }

    private static void setStatusBarIcon(Context context, boolean z) {
        Log.f(TAG, "setStatusBarIcon: " + z);
        Intent intent = new Intent("android.intent.action.ALARM_CHANGED");
        intent.putExtra("alarmSet", z);
        intent.putExtra("alarmSystem", true);
        context.sendBroadcast(intent);
    }

    public static long calculateAlarmTime(Context context, Alarm alarm) {
        return calculateAlarmTime((Calendar) null, context, alarm.hour, alarm.minutes, alarm.daysOfWeek, alarm.skipTime > System.currentTimeMillis()).getTimeInMillis();
    }

    public static List<Long> calculateAlarmTimeWithin(Context context, Alarm alarm, long j, long j2) {
        ArrayList arrayList = new ArrayList();
        if (j >= j2) {
            return arrayList;
        }
        if (alarm.time > 0) {
            if (alarm.time > j && alarm.time < j2) {
                arrayList.add(Long.valueOf(alarm.time));
            }
            return arrayList;
        }
        long timeInMillis = j;
        while (timeInMillis <= j2) {
            long jCalculateAlarmTime = calculateAlarmTime(timeInMillis, context, alarm.hour, alarm.minutes, alarm.daysOfWeek, alarm.skipTime);
            if (jCalculateAlarmTime > j && jCalculateAlarmTime < j2) {
                arrayList.add(Long.valueOf(jCalculateAlarmTime));
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeInMillis);
            calendar.add(6, 1);
            timeInMillis = calendar.getTimeInMillis();
        }
        return arrayList;
    }

    public static Calendar calculateAlarmTime(Context context, int i, int i2, int i3) {
        return calculateAlarmTime(context, i, i2, new Alarm.DaysOfWeek(i3));
    }

    public static Calendar calculateAlarmTime(Context context, int i, int i2, Alarm.DaysOfWeek daysOfWeek) {
        return calculateAlarmTime((Calendar) null, context, i, i2, daysOfWeek, false);
    }

    public static Calendar calculateAlarmTime(Calendar calendar, Context context, int i, int i2, Alarm.DaysOfWeek daysOfWeek) {
        return calculateAlarmTime(calendar, context, i, i2, daysOfWeek, false);
    }

    public static boolean isShutdownAlarmEnabled(Context context) {
        return Util.supportShutdownAlarm() && FBEUtil.getDefaultSharedPreferences(context).getBoolean("shutdown_alarm", true);
    }

    public static Calendar calculateAlarmTime(Calendar calendar, Context context, int i, int i2, Alarm.DaysOfWeek daysOfWeek, boolean z) {
        int nextAlarm;
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        int i3 = calendar.get(11);
        int i4 = calendar.get(12);
        if (i < i3 || (i == i3 && i2 <= i4)) {
            calendar.add(6, 1);
        }
        calendar.set(11, i);
        calendar.set(12, i2);
        calendar.set(13, 0);
        calendar.set(14, 0);
        if (z) {
            nextAlarm = daysOfWeek.getNextAlarmSkipOne(context, calendar);
        } else {
            nextAlarm = daysOfWeek.getNextAlarm(context, calendar);
        }
        if (nextAlarm > 0) {
            calendar.add(7, nextAlarm);
        }
        return calendar;
    }

    public static long calculateAlarmTime(long j, Context context, int i, int i2, Alarm.DaysOfWeek daysOfWeek, long j2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        calendar.set(14, R2.attr.fab_background);
        long timeInMillis = calendar.getTimeInMillis();
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        if ((j2 >= calendar.getTimeInMillis() && j2 <= timeInMillis) || daysOfWeek.getNextAlarm(context, calendar) > 0) {
            return -1L;
        }
        calendar.set(11, i);
        calendar.set(12, i2);
        return calendar.getTimeInMillis();
    }

    public static long formatSnoozeTime(long j) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    private static String showTime(long j) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        return new SimpleDateFormat(TimeUtil.FORMAT_TIME_EN).format(calendar.getTime());
    }

    public static String formatTime(Context context, Calendar calendar) {
        return calendar == null ? "" : (String) DateFormat.format(get24HourMode() ? "HH:mm" : M12, calendar);
    }

    private static String formatDayAndTime(Context context, Calendar calendar) {
        return calendar == null ? "" : (String) DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), DateFormat.is24HourFormat(context) ? DM24 : DM12), calendar);
    }

    static void saveNextAlarmTime(Context context, String str, long j) {
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver != null) {
            try {
                Settings.System.putString(contentResolver, "next_alarm_formatted", str);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        try {
            Settings.Global.putLong(context.getContentResolver(), NEXT_ALARM_LONG_TIME, j);
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
        try {
            Settings.System.putString(context.getContentResolver(), SETTINGS_NEXT_ALARM_TIME, str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Settings.Global.putString(context.getContentResolver(), SETTINGS_NEXT_ALARM_TIME, str);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    static void saveNextAlarmTime(Context context, long j) {
        String dayAndTime;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        if (j == 0) {
            dayAndTime = "";
        } else {
            dayAndTime = formatDayAndTime(context, calendar);
        }
        saveNextAlarmTime(context, dayAndTime, j);
    }

    public static boolean get24HourMode() {
        return is24HourMode;
    }

    public static void reset24HourMode(Context context) {
        if (context != null) {
            is24HourMode = DateFormat.is24HourFormat(context);
        } else {
            is24HourMode = DateFormat.is24HourFormat(DeskClockApp.getAppDEContext());
        }
        Log.d(TAG, "reset24HourMode context:" + context + " is24HourMode ：" + is24HourMode);
    }

    public static CharSequence formatAlarmTime(Context context, Alarm alarm) {
        if (alarm == null) {
            return "";
        }
        return DateFormat.format(get24HourMode() ? "HH:mm" : M12, alarm.time);
    }

    public static CharSequence formatAlarmNotificationTime(Context context, Alarm alarm) {
        return DateFormat.format(get24HourMode() ? "HH:mm" : M12_ALARM, alarm.time);
    }

    public static CharSequence formatTimerDuration(Context context, Alarm alarm) {
        return formatTimerDuration(context, alarm.hour, alarm.minutes, alarm.seconds);
    }

    public static CharSequence formatTimerDuration(Context context, int i, int i2, int i3) {
        StringBuilder sb = new StringBuilder();
        if (i > 0) {
            sb.append(context.getResources().getQuantityString(R.plurals.timer_message_hour, i, Integer.valueOf(i)));
        }
        if (i2 > 0) {
            sb.append(context.getResources().getQuantityString(R.plurals.timer_message_minute, i2, Integer.valueOf(i2)));
        }
        if (i3 > 0 || i + i2 == 0) {
            sb.append(context.getResources().getQuantityString(R.plurals.timer_message_second, i3, Integer.valueOf(i3)));
        }
        return sb.toString();
    }

    public static void snoozeAlarm(Context context, Alarm alarm, int i) {
        NotificationUtil.clearAlarmAlertNotification(context, alarm.id);
        if (Util.isSupportColorfulLight() && ReflectUtil.isSupportBackStrap(context) && ReflectUtil.isStripLightEnable(context)) {
            AlarmColorLightManager.setColorfulLight(context, -1);
        }
        stopAlarmKlaxon(context);
        if (alarm.type == 2) {
            if (ShiftAlarmDataHelper.getShiftGroupFromAlarmId(alarm.id) == null) {
                return;
            }
        } else if (alarm.type == 0) {
            Alarm alarm2 = getAlarm(context.getContentResolver(), alarm.id);
            if (alarm2 == null) {
                return;
            } else {
                AppSearchUtil.getInstance(context).updateAppSearchAlarmSnoozed(alarm2, alarm.daysOfWeek.isRepeatSet());
            }
        }
        if (i == Integer.MIN_VALUE) {
            i = Util.getSnoozeMinutes(context);
        }
        long jCurrentTimeMillis = System.currentTimeMillis() + ((long) (60000 * i));
        formatSnoozeTime(jCurrentTimeMillis);
        saveSnoozeAlert(context, alarm.id, jCurrentTimeMillis);
        BackScreenNotificationUtil.sendAlarmSnoozeNotification(context, alarm, i);
        NotificationUtil.showSnoozeNotification(context, alarm, i);
        String quantityString = context.getResources().getQuantityString(R.plurals.alarm_alert_snooze_set, i, Integer.valueOf(i));
        if (Util.isTinyScreen(context)) {
            Toast.makeText(DeskClockApp.getAppDEContext(), quantityString, 0).show();
        } else {
            Toast.makeText(DeskClockApp.getAppDEContext(), quantityString, 1).show();
        }
    }

    public static void snoozeAlarm(Context context, Alarm alarm) {
        snoozeAlarm(context, alarm, Integer.MIN_VALUE);
    }

    public static void dismissTimer(Context context) {
        NotificationUtil.clearTimerAlertNotification(context, -2);
        BackScreenNotificationUtil.clearTimerNotification(context);
        if (Util.isSupportColorfulLight() && ReflectUtil.isSupportBackStrap(context) && ReflectUtil.isStripLightEnable(context)) {
            AlarmColorLightManager.setColorfulLight(context, -1);
        }
        stopAlarmKlaxon(context);
        AlarmService.setTimerAlarming(false);
        Intent intent = new Intent(TimerService.ACTION_STOP_TIMER);
        intent.setPackage(BuildConfig.APPLICATION_ID);
        context.sendBroadcast(intent);
    }

    public static void dismissAlarm(Context context, Alarm alarm) {
        if (Util.isSupportColorfulLight() && ReflectUtil.isSupportBackStrap(context) && ReflectUtil.isStripLightEnable(context)) {
            AlarmColorLightManager.setColorfulLight(context, -1);
        }
        NotificationUtil.clearAlarmAlertNotification(context, alarm.id);
        BackScreenNotificationUtil.clearAlarmNotification(context);
        stopAlarmKlaxon(context);
        tryDeleteOneshotAlarm(context, alarm);
        if (alarm.type == 2) {
            return;
        }
        if (alarm.deleteAfterUse) {
            AppSearchUtil.getInstance(context).deleteAppSearchAlarm(alarm.id);
        } else if (alarm.daysOfWeek.isRepeatSet()) {
            AppSearchUtil.getInstance(context).updateAppSearchAlarmScheduled(alarm, true);
        } else {
            AppSearchUtil.getInstance(context).updateAppSearchAlarmDismissed(alarm, false);
        }
    }

    public static void stopAlarmKlaxon(Context context) {
        try {
            Intent intent = new Intent(ALARM_ALERT_ACTION);
            intent.setClass(context, AlarmService.class);
            context.stopService(intent);
            Intent intent2 = new Intent(ALARM_STOP_ACTION);
            intent2.setPackage(context.getPackageName());
            context.sendBroadcastAsUser(intent2, ClockCompat.UserHandle_ALL);
        } catch (Exception e) {
            Log.e(TAG, "stopAlarmKlaxon error: " + e);
        }
    }

    public static void snoozeAlarmAsUser(int i, Context context, Alarm alarm) {
        if (Util.isSupportColorfulLight() && ReflectUtil.isSupportBackStrap(context) && ReflectUtil.isStripLightEnable(context)) {
            AlarmColorLightManager.setColorfulLight(context, -1);
        }
        UserHandle currentUserHandle = Util.getCurrentUserHandle(i);
        Intent intent = new Intent(ACTION_ALARM_SNOOZE);
        intent.putExtra(ALARM_INTENT_EXTRA, alarm);
        intent.putExtra(AlarmService.KEY_ALARM_USER_ID, Util.getCurrentUser());
        intent.setPackage(context.getPackageName());
        Log.f(TAG, "snoozeAlarmAsUser sendBroadcastAsUser: " + currentUserHandle);
        context.sendBroadcastAsUser(intent, currentUserHandle);
    }

    public static void notifyToFinishAlertUI(Context context) {
        Log.f(TAG, "sendBroadcast com.android.deskclock.ALERT_UI_DISMISS");
        Intent intent = new Intent(ACTION_ALERT_UI_DISMISS);
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }

    public static void tryDeleteOneshotAlarm(Context context, Alarm alarm) {
        if (alarm.deleteAfterUse) {
            Log.f(TAG, "deleting oneshot alarm, id :" + alarm.id);
            deleteAlarm(context, false, Integer.valueOf(alarm.id));
        }
    }

    public static void putAlarmRawDataToIntent(Alarm alarm, Intent intent) {
        Parcel parcelObtain = Parcel.obtain();
        alarm.writeToParcel(parcelObtain, 0);
        parcelObtain.setDataPosition(0);
        intent.putExtra(ALARM_RAW_DATA, parcelObtain.marshall());
        parcelObtain.recycle();
    }

    public static Alarm parseAlarmFromRawDataIntent(Intent intent) {
        Alarm alarmCreateFromParcel;
        byte[] byteArrayExtra = intent.getByteArrayExtra(ALARM_RAW_DATA);
        if (byteArrayExtra != null) {
            Parcel parcelObtain = Parcel.obtain();
            parcelObtain.unmarshall(byteArrayExtra, 0, byteArrayExtra.length);
            parcelObtain.setDataPosition(0);
            alarmCreateFromParcel = Alarm.CREATOR.createFromParcel(parcelObtain);
            parcelObtain.recycle();
        } else {
            alarmCreateFromParcel = null;
        }
        if (alarmCreateFromParcel == null) {
            Log.e("Failed to parse the alarm from the intent");
        }
        return alarmCreateFromParcel;
    }

    public static void resetWorkdayAlarm(Context context, Set<Integer> set) {
        if (set == null || set.isEmpty()) {
            return;
        }
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put("daysofweek", (Integer) 31);
        Iterator<Integer> it = set.iterator();
        while (it.hasNext()) {
            contentResolver.update(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, it.next().intValue()), contentValues, null, null);
        }
    }

    public static void resetOffdayAlarm(Context context, Set<Integer> set) {
        if (set == null || set.isEmpty()) {
            return;
        }
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put("daysofweek", (Integer) 96);
        Iterator<Integer> it = set.iterator();
        while (it.hasNext()) {
            contentResolver.update(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, it.next().intValue()), contentValues, null, null);
        }
    }

    public static Set<Integer> queryRepeatTypeAlarm(Context context, int i) throws Throwable {
        Cursor cursor = null;
        hashSet = null;
        HashSet hashSet = null;
        try {
            Cursor cursorQuery = context.getContentResolver().query(Alarm.Columns.CONTENT_URI, new String[]{"_id"}, "daysofweek=" + i, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst()) {
                        hashSet = new HashSet();
                        do {
                            hashSet.add(Integer.valueOf(cursorQuery.getInt(0)));
                        } while (cursorQuery.moveToNext());
                    }
                } catch (Throwable th) {
                    th = th;
                    cursor = cursorQuery;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return hashSet;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static void addWakeAlarm(Context context, Alarm alarm) {
        ContentValues contentValuesCreateContentValues = createContentValues(context, alarm);
        if (BedtimeUtil.getWakeAlarm(context) == null) {
            context.getContentResolver().insert(SleepAlarmTable.CONTENT_URI, contentValuesCreateContentValues);
        } else {
            context.getContentResolver().update(SleepAlarmTable.CONTENT_URI, contentValuesCreateContentValues, null, null);
        }
        alarm.id = Integer.MIN_VALUE;
        Log.i("addWakeAlarm " + alarm.toString());
        setNextAlert(context);
    }

    public static void initSleepNotification(Context context) {
        if (!BedtimeUtil.isBedtimeOpen(context) || getAlarm(context.getContentResolver(), Integer.MIN_VALUE) == null) {
            return;
        }
        setSleepNotification(context);
    }

    public static void setSleepNotification(Context context) {
        Alarm alarm;
        AlarmUtils.cancelAlarm(context, SLEEP_ALARM_NOTIFICATION_ACTION);
        NotificationUtil.clearSleepNotification(context);
        if (BedtimeUtil.isBedtimeOpen(context) && (alarm = getAlarm(context.getContentResolver(), Integer.MIN_VALUE)) != null) {
            long sleepNotificationTime = BedtimeUtil.getSleepNotificationTime(context, alarm, false);
            int notificationAdvTime = BedtimeUtil.getNotificationAdvTime(context) != -1 ? BedtimeUtil.getNotificationAdvTime(context) : 0;
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (sleepNotificationTime > jCurrentTimeMillis) {
                Intent intent = new Intent(SLEEP_ALARM_NOTIFICATION_ACTION);
                intent.putExtra("sleep_time", ((long) (notificationAdvTime * 60000)) + sleepNotificationTime);
                intent.setPackage(context.getPackageName());
                AlarmUtils.setAlarm(context, sleepNotificationTime, intent);
                Log.f(TAG, "register alarm notification to AlarmManager at :" + ((Object) DateFormat.format("MM-dd kk:mm:ss", sleepNotificationTime)));
                return;
            }
            if (sleepNotificationTime > jCurrentTimeMillis || sleepNotificationTime + ((long) (notificationAdvTime * 60000)) < jCurrentTimeMillis) {
                String str = HealthDataUtil.SLEEP_RECORD_BEGIN;
                HealthDataUtil.setScheduleState(context, str);
                long sleepNotificationTime2 = BedtimeUtil.getSleepNotificationTime(context, alarm, true);
                Intent intent2 = new Intent(SLEEP_ALARM_NOTIFICATION_ACTION);
                intent2.putExtra("sleep_time", ((long) (notificationAdvTime * 60000)) + sleepNotificationTime2);
                intent2.setPackage(context.getPackageName());
                AlarmUtils.setAlarm(context, sleepNotificationTime2, intent2);
                Log.f(TAG, "register alarm notification to AlarmManager at :" + ((Object) DateFormat.format("MM-dd kk:mm:ss", sleepNotificationTime2)));
                return;
            }
            HealthDataUtil.setScheduleState(context, HealthDataUtil.SLEEP_RECORD_BEGIN);
            if (BedtimeUtil.getNotificationAdvTime(context) != -1) {
                NotificationUtil.showSleepNotification(context);
            }
            setNextSleepNotification(context, alarm);
        }
    }

    public static void setNextSleepNotification(Context context, Alarm alarm) {
        if (alarm == null) {
            return;
        }
        long sleepNotificationTime = BedtimeUtil.getSleepNotificationTime(context, alarm, false);
        if (System.currentTimeMillis() > sleepNotificationTime) {
            sleepNotificationTime = BedtimeUtil.getSleepNotificationTime(context, alarm, true);
        }
        int notificationAdvTime = BedtimeUtil.getNotificationAdvTime(context) != -1 ? BedtimeUtil.getNotificationAdvTime(context) : 0;
        Intent intent = new Intent(SLEEP_ALARM_NOTIFICATION_ACTION);
        intent.putExtra("sleep_time", ((long) (notificationAdvTime * 60000)) + sleepNotificationTime);
        intent.setPackage(context.getPackageName());
        AlarmUtils.setAlarm(context, sleepNotificationTime, intent);
        Log.f(TAG, "register alarm notification to AlarmManager at :" + ((Object) DateFormat.format("MM-dd kk:mm:ss", sleepNotificationTime)));
    }

    public static long setWakeAlarm(Context context, Alarm alarm) {
        Log.f("DC:setWakeAlarm", "new wake alarm: " + alarm.toString());
        context.getContentResolver().update(SleepAlarmTable.CONTENT_URI, createContentValues(context, alarm), null, null);
        long jCalculateAlarmTime = calculateAlarmTime(context, alarm);
        if (alarm.enabled) {
            disableSnoozeAlert(context, alarm.id);
        }
        setNextAlert(context);
        return jCalculateAlarmTime;
    }

    public static void setZenMode(Context context) {
        Log.f(TAG, "setZenMode");
        if (MiuiSdk.isSupportSleep() && FBEUtil.isUserUnlocked(context)) {
            Log.f(TAG, "setZenMode start");
            AlarmUtils.cancelAlarm(context, ACTION_ENTER_ZENMODE);
            if (!BedtimeUtil.getDisturbanceState(context) || !BedtimeUtil.isBedtimeOpen(context)) {
                Log.f(TAG, "ZenMode invalid, exitZenMode ");
                ZenModeUtil.exitZenMode(context);
                return;
            }
            Alarm alarm = getAlarm(context.getContentResolver(), Integer.MIN_VALUE);
            if (alarm == null) {
                ZenModeUtil.exitZenMode(context);
                return;
            }
            alarm.skipTime = 0L;
            alarm.enabled = true;
            long jCalculateAlarmTime = calculateAlarmTime(context, alarm) - ((long) (BedtimeUtil.getSleepDuration(context) * 60000));
            if (jCalculateAlarmTime <= System.currentTimeMillis()) {
                Log.f(TAG, "enterZenMode ");
                ZenModeUtil.enterZenMode(context);
                return;
            }
            Log.f(TAG, "exitZenMode ");
            ZenModeUtil.exitZenMode(context);
            Intent intent = new Intent(ACTION_ENTER_ZENMODE);
            intent.putExtra("sleep_time", jCalculateAlarmTime);
            intent.setPackage(context.getPackageName());
            intent.addFlags(32);
            AlarmUtils.setAlarm(context, jCalculateAlarmTime, intent);
            Log.f(TAG, "register ACTION_ENTER_ZENMODE to AlarmManager at " + ((Object) DateFormat.format("MM-dd kk:mm:ss", jCalculateAlarmTime)));
        }
    }

    public static void registerWakeAlarm(Context context) {
        Alarm wakeAlarm = BedtimeUtil.getWakeAlarm(context);
        if (!BedtimeUtil.isBedtimeOpen(context) || wakeAlarm == null) {
            return;
        }
        if (!wakeAlarm.enabled || (wakeAlarm.enabled && wakeAlarm.skipTime != 0)) {
            AlarmUtils.cancelAlarm(context, ACTION_REACH_WAKE_TIME);
            wakeAlarm.enabled = true;
            wakeAlarm.skipTime = 0L;
            wakeAlarm.time = calculateAlarmTime(context, wakeAlarm);
            Intent intent = new Intent(ACTION_REACH_WAKE_TIME);
            intent.setPackage(context.getPackageName());
            AlarmUtils.setAlarm(context, wakeAlarm.time, intent);
            Log.f(TAG, "register to AlarmManage ACTION_REACH_WAKE_TIME at " + ((Object) DateFormat.format("MM-dd kk:mm", wakeAlarm.time)));
        }
    }

    public static void updateAlarmTime(Context context, int i) {
        Alarm alarm = getAlarm(context.getContentResolver(), i);
        if (alarm != null && alarm.enabled && alarm.daysOfWeek.getCoded() == 0) {
            enableAlarmInternal(context, alarm, true, false);
        }
    }

    public static void handleSPDataToDB(Context context) {
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(context);
        Set<String> stringSet = defaultSharedPreferences.getStringSet(PREF_ONESHOT_ALARMS, new HashSet());
        if (stringSet.isEmpty()) {
            Log.i(TAG, "no need to handle SP data to DB about deleteAfterUes");
            return;
        }
        Log.i(TAG, "handle SP data to DB about deleteAfterUes, " + stringSet.toString());
        ContentResolver contentResolver = context.getContentResolver();
        Iterator<String> it = stringSet.iterator();
        while (it.hasNext()) {
            int iIntValue = Integer.valueOf(it.next()).intValue();
            Alarm alarm = getAlarm(contentResolver, iIntValue);
            if (alarm != null && !alarm.daysOfWeek.isRepeatSet()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("deleteAfterUse", (Integer) 1);
                contentResolver.update(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, iIntValue), contentValues, null, null);
            }
        }
        defaultSharedPreferences.edit().putStringSet(PREF_ONESHOT_ALARMS, null).apply();
    }

    public static void enableAlarmBootInitReceiver(Context context, boolean z) {
        PackageManager packageManager;
        if (context != null) {
            packageManager = context.getPackageManager();
        } else {
            packageManager = DeskClockApp.getAppDEContext().getPackageManager();
        }
        Log.f(TAG, "enableAlarmBootInitReceiver: " + z);
        if (z) {
            packageManager.setComponentEnabledSetting(new ComponentName(context, (Class<?>) AlarmBootInitReceiver.class), 1, 1);
        } else {
            packageManager.setComponentEnabledSetting(new ComponentName(context, (Class<?>) AlarmBootInitReceiver.class), 2, 1);
        }
    }

    public static MiuiWallpaperManager getMiuiWallpaperManager() {
        return mMiuiWallpaperManager;
    }

    public static void setMiuiWallpaperManager(MiuiWallpaperManager miuiWallpaperManager) {
        Log.d(TAG, "setMiuiWallpaperManager: " + miuiWallpaperManager);
        mMiuiWallpaperManager = miuiWallpaperManager;
    }

    public static void releaseMiuiWallpaperManager() {
        if (mMiuiWallpaperManager != null) {
            mMiuiWallpaperManager = null;
        }
    }

    public static long convertToTimeStamp(Integer num, Integer num2, Integer num3, int i, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(num.intValue(), num2.intValue() - 1, num3.intValue(), i, i2, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }
}
