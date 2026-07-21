package com.android.deskclock.alarm.bedtime;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import com.android.deskclock.Alarm;
import com.android.deskclock.R2;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class BedtimeUtil {
    public static final String ACTION_ENABLE_WAKE_ALARM = "android.intent.action.ENABLE_WAKE_ALARM";
    public static final int BANNER_SHOW_COUNT_UPPER_LIMIT = 10;
    public static final int DEFAULT_SLEEP_HOUR = 22;
    public static final int DEFAULT_SLEEP_MIN = 0;
    public static final int DEFAULT_SLEEP_REPEAT = 127;
    public static final int DEFAULT_WAKE_HOUR = 6;
    public static final int DEFAULT_WAKE_MIN = 0;
    public static final String KEY_BEDTIME_ALARM_COMPLETED = "key_bedtime_alarm_completed";
    public static final String KEY_GUIDE_SETTINGS_INDEX = "key_guide_settings_index";
    public static final String KEY_MANAGE_GUIDE_SHOWED = "key_manage_guide_showed";
    public static final String KEY_NOTIFICATION_ADV_TIME = "key_notification_adv_time";
    public static final String KEY_OPEN_BEDTIME = "key_open_bedtime";
    public static final String KEY_SHOW_BEDTIME_BANNER = "key_show_bedtime_banner";
    public static final String KEY_SHOW_BEDTIME_BANNER_COUNT = "key_show_bedtime_banner_count";
    public static final String KEY_SLEEP_ALARM_HOUR = "key_sleep_alarm_hour";
    public static final String KEY_SLEEP_ALARM_MIN = "key_sleep_alarm_min";
    public static final String KEY_SLEEP_NO_DISTURBANCE = "key_sleep_no_disturbance";
    public static final String KEY_SLEEP_REPEAT_TYPE = "key_sleep_repeat_type";
    public static final String KEY_VIBRATOR = "key_vibrator";
    public static final String KEY_WAKE_ALARM_HOUR = "key_wake_alarm_hour";
    public static final String KEY_WAKE_ALARM_MIN = "key_wake_alarm_min";
    public static final String PAGE_INDEX = "page_index";
    public static final String PREFERENCES = "BedtimeAlarm";
    public static final String SP_WAKE_ALARM_ALARM_TIME = "sp_wake_alarm_alert_time";
    public static final String SP_WAKE_ALARM_ALERT = "sp_wake_alarm_alert";
    public static final String SP_WAKE_ALARM_DAYS_OF_WEEK = "sp_wake_alarm_days_of_week";
    public static final String SP_WAKE_ALARM_ENABLE = "sp_wake_alarm_enable";
    public static final String SP_WAKE_ALARM_HOUR = "sp_wake_alarm_hour";
    public static final String SP_WAKE_ALARM_MINUTES = "sp_wake_alarm_mins";
    public static final String SP_WAKE_ALARM_SKIP_TIME = "sp_wake_alarm_skip_time";
    public static final String SP_WAKE_ALARM_VIBRATE = "sp_wake_alarm_vibrate";
    public static final String TRANSFER_WAKE_ALARM_TO_SP = "transfer_wake_alarm_to_sp";
    public static final String KEY_BEDTIME_STATE = "bedtime_state";
    public static final String KEY_SLEEP_HOUR = "sleep_hour";
    public static final String KEY_SLEEP_MINUTE = "sleep_minute";
    public static final String KEY_WAKE_HOUR = "wake_hour";
    public static final String KEY_WAKE_MINUTE = "wake_minute";
    public static final String KEY_REPEAT_TYPE = "repeat_type";
    public static final String[] PROJECTION = {KEY_BEDTIME_STATE, KEY_SLEEP_HOUR, KEY_SLEEP_MINUTE, KEY_WAKE_HOUR, KEY_WAKE_MINUTE, KEY_REPEAT_TYPE};
    public static final String[] WAKE_ALARM_PROJECTION = {"_id", "hour", "minutes", "daysofweek", "alarmtime", "enabled", "vibrate", "message", "alert", "type", "deleteAfterUse", "skiptime"};

    public static int getSleepDuration(int i, int i2, int i3, int i4) {
        int i5 = (i3 * 60) + i4;
        int i6 = (i * 60) + i2;
        return i5 < i6 ? (1440 - i6) + i5 : i5 - i6;
    }

    public static boolean showBedTimeBanner(Context context) {
        return FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).getBoolean(KEY_SHOW_BEDTIME_BANNER, true);
    }

    public static void setBannerVisiable(Context context, boolean z) {
        FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit().putBoolean(KEY_SHOW_BEDTIME_BANNER, z).apply();
    }

    public static boolean bedTimeAlarmCompleted(Context context) {
        return FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).getBoolean(KEY_BEDTIME_ALARM_COMPLETED, false);
    }

    public static void setBedTimeCompleted(Context context, boolean z) {
        FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit().putBoolean(KEY_BEDTIME_ALARM_COMPLETED, z).apply();
    }

    public static boolean isBedtimeOpen(Context context) {
        return FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).getBoolean(KEY_OPEN_BEDTIME, false);
    }

    public static void setBedtimeOpenState(Context context, boolean z) {
        FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit().putBoolean(KEY_OPEN_BEDTIME, z).apply();
    }

    public static boolean getDisturbanceState(Context context) {
        return FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).getBoolean(KEY_SLEEP_NO_DISTURBANCE, true);
    }

    public static void setDisturbanceState(Context context, boolean z) {
        FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit().putBoolean(KEY_SLEEP_NO_DISTURBANCE, z).apply();
    }

    public static boolean getVibratorState(Context context) {
        return FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).getBoolean(KEY_VIBRATOR, true);
    }

    public static void setVibratorState(Context context, boolean z) {
        FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit().putBoolean(KEY_VIBRATOR, z).apply();
    }

    public static int getNotificationAdvTime(Context context) {
        return FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).getInt(KEY_NOTIFICATION_ADV_TIME, 15);
    }

    public static void setNotificationAdvTime(Context context, int i) {
        FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit().putInt(KEY_NOTIFICATION_ADV_TIME, i).apply();
    }

    public static boolean getManageGuideShowed(Context context) {
        return FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).getBoolean(KEY_MANAGE_GUIDE_SHOWED, false);
    }

    public static void setManageGuideShowed(Context context, boolean z) {
        FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit().putBoolean(KEY_MANAGE_GUIDE_SHOWED, z).apply();
    }

    public static void saveSleepAlarm(Context context, Alarm alarm) {
        SharedPreferences.Editor editorEdit = FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit();
        editorEdit.putInt(KEY_SLEEP_ALARM_HOUR, alarm.hour);
        editorEdit.putInt(KEY_SLEEP_ALARM_MIN, alarm.minutes);
        editorEdit.apply();
    }

    public static int getSleepAlarmHour(Context context) {
        return FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).getInt(KEY_SLEEP_ALARM_HOUR, 22);
    }

    public static int getSleepAlarmMin(Context context) {
        return FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).getInt(KEY_SLEEP_ALARM_MIN, 0);
    }

    public static Alarm getWakeAlarm(Context context) {
        return AlarmHelper.getAlarm(context.getContentResolver(), Integer.MIN_VALUE);
    }

    public static int getWakeAlarmHour(Context context) {
        Alarm wakeAlarm = getWakeAlarm(context);
        if (wakeAlarm != null) {
            return wakeAlarm.hour;
        }
        return 6;
    }

    public static int getWakeAlarmMin(Context context) {
        Alarm wakeAlarm = getWakeAlarm(context);
        if (wakeAlarm != null) {
            return wakeAlarm.minutes;
        }
        return 0;
    }

    public static int getRepeatType(Context context) {
        Alarm wakeAlarm = getWakeAlarm(context);
        if (wakeAlarm != null) {
            return wakeAlarm.daysOfWeek.getCoded();
        }
        return 127;
    }

    public static int getSleepDuration(Context context) {
        int wakeTimeMins = getWakeTimeMins(context);
        int sleepTimeMins = getSleepTimeMins(context);
        return wakeTimeMins < sleepTimeMins ? (1440 - sleepTimeMins) + wakeTimeMins : wakeTimeMins - sleepTimeMins;
    }

    public static long getMorningTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 0);
        calendar.set(13, 0);
        calendar.set(12, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    public static int getMinsOfDay(long j) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        return (calendar.get(11) * 60) + calendar.get(12);
    }

    public static int getTopMins(Context context, int i) {
        int sleepTimeMins = getSleepTimeMins(context);
        if (sleepTimeMins < i) {
            return ((sleepTimeMins / 60) - (sleepTimeMins % 60 != 0 ? 1 : 0)) * 60;
        }
        return ((i / 60) - (i % 60 != 0 ? 1 : 0)) * 60;
    }

    public static int getBottomMins(Context context, int i) {
        int wakeAlarmHour = (getWakeAlarmHour(context) * 60) + getWakeAlarmMin(context);
        if (wakeAlarmHour > i) {
            return ((wakeAlarmHour / 60) + (wakeAlarmHour % 60 != 0 ? 1 : 0)) * 60;
        }
        return ((i / 60) + (i % 60 != 0 ? 1 : 0)) * 60;
    }

    public static int getThresholdMins(Context context) {
        int sleepAlarmHour = (getSleepAlarmHour(context) * 60) + getSleepAlarmMin(context);
        int wakeAlarmHour = (getWakeAlarmHour(context) * 60) + getWakeAlarmMin(context);
        return ((1440 - (sleepAlarmHour > wakeAlarmHour ? (wakeAlarmHour + R2.attr.mSpecialWidth) - sleepAlarmHour : wakeAlarmHour - sleepAlarmHour)) / 2) + wakeAlarmHour;
    }

    public static int getSleepTimeMins(Context context) {
        int sleepAlarmHour = (getSleepAlarmHour(context) * 60) + getSleepAlarmMin(context);
        return sleepAlarmHour > getWakeTimeMins(context) ? sleepAlarmHour - 1440 : sleepAlarmHour;
    }

    public static int getWakeTimeMins(Context context) {
        return (getWakeAlarmHour(context) * 60) + getWakeAlarmMin(context);
    }

    public static long getSleepNotificationTime(Context context, Alarm alarm, boolean z) {
        return (AlarmHelper.calculateAlarmTime((Calendar) null, context, alarm.hour, alarm.minutes, alarm.daysOfWeek, z).getTimeInMillis() - ((long) (getSleepDuration(context) * 60000))) - ((long) ((getNotificationAdvTime(context) == -1 ? 0 : getNotificationAdvTime(context)) * 60000));
    }

    public static int getGuideSettingsIndex(Context context) {
        return FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).getInt(KEY_GUIDE_SETTINGS_INDEX, -1);
    }

    public static void setGuideSettingsIndex(Context context, int i) {
        FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit().putInt(KEY_GUIDE_SETTINGS_INDEX, i).apply();
    }

    public static void saveTempWakeAlarm(Context context, Alarm alarm) {
        SharedPreferences.Editor editorEdit = FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit();
        editorEdit.putInt(KEY_WAKE_ALARM_HOUR, alarm.hour);
        editorEdit.putInt(KEY_WAKE_ALARM_MIN, alarm.minutes);
        editorEdit.putInt(KEY_SLEEP_REPEAT_TYPE, alarm.daysOfWeek.getCoded());
        editorEdit.apply();
    }

    public static int getTempWakeHour(Context context) {
        return FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).getInt(KEY_WAKE_ALARM_HOUR, 6);
    }

    public static int getTempWakeMin(Context context) {
        return FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).getInt(KEY_WAKE_ALARM_MIN, 0);
    }

    public static int getTempWakeRepeat(Context context) {
        return FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).getInt(KEY_SLEEP_REPEAT_TYPE, 127);
    }

    public static void doInWakeTime(Context context) {
        Log.f("DC:BedtimeUtil", "do in wake time");
        AlarmHelper.setSleepNotification(context);
        AlarmHelper.setZenMode(context);
    }

    public static Cursor queryBedtime(Context context) {
        Log.i("DC:BedtimeUtil", "query Timer form ContentProvider");
        MatrixCursor matrixCursor = new MatrixCursor(PROJECTION);
        matrixCursor.addRow(new Object[]{Integer.valueOf(bedTimeAlarmCompleted(context) ? 1 : 0), Integer.valueOf(getSleepAlarmHour(context)), Integer.valueOf(getSleepAlarmMin(context)), Integer.valueOf(getWakeAlarmHour(context)), Integer.valueOf(getWakeAlarmMin(context)), Integer.valueOf(HealthDataUtil.getRepeatType(new Alarm.DaysOfWeek(getRepeatType(context))))});
        return matrixCursor;
    }

    public static Cursor queryBedtimeForAddition(Context context) {
        Log.i("DC:BedtimeUtil", "query Bedtime form ContentProvider");
        if (Util.isInternational()) {
            return null;
        }
        MatrixCursor matrixCursor = new MatrixCursor(PROJECTION);
        int i = 0;
        SharedPreferences sharedPreferences = FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0);
        if (bedTimeAlarmCompleted(context) && isBedtimeOpen(context)) {
            i = 1;
        }
        matrixCursor.addRow(new Object[]{Integer.valueOf(i), Integer.valueOf(getSleepAlarmHour(context)), Integer.valueOf(getSleepAlarmMin(context)), Integer.valueOf(getWakeAlarmHour(sharedPreferences)), Integer.valueOf(getWakeAlarmMins(sharedPreferences)), Integer.valueOf(HealthDataUtil.getRepeatType(new Alarm.DaysOfWeek(getWakeDaysOfWeek(sharedPreferences))))});
        return matrixCursor;
    }

    public static Cursor queryWakeAlarm(Context context) {
        Log.i("DC:BedtimeUtil", "query WakeAlarm form ContentProvider");
        MatrixCursor matrixCursor = new MatrixCursor(WAKE_ALARM_PROJECTION);
        if (!bedTimeAlarmCompleted(context)) {
            return matrixCursor;
        }
        SharedPreferences sharedPreferences = FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0);
        matrixCursor.addRow(new Object[]{Integer.MIN_VALUE, Integer.valueOf(getWakeAlarmHour(sharedPreferences)), Integer.valueOf(getWakeAlarmMins(sharedPreferences)), Integer.valueOf(getWakeDaysOfWeek(sharedPreferences)), Long.valueOf(getWakeAlarmAlertTime(sharedPreferences)), Integer.valueOf(getWakeAlarmEnable(sharedPreferences)), Integer.valueOf(getWakeAlarmVibrate(sharedPreferences)), "", getWakeAlarmAlert(sharedPreferences), 0, 0, getWakeAlarmSkipTime(sharedPreferences)});
        return matrixCursor;
    }

    public static int getWakeAlarmHour(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(SP_WAKE_ALARM_HOUR, 6);
    }

    public static int getWakeAlarmMins(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(SP_WAKE_ALARM_MINUTES, 0);
    }

    public static int getWakeDaysOfWeek(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(SP_WAKE_ALARM_DAYS_OF_WEEK, 127);
    }

    public static long getWakeAlarmAlertTime(SharedPreferences sharedPreferences) {
        return sharedPreferences.getLong(SP_WAKE_ALARM_ALARM_TIME, 0L);
    }

    public static int getWakeAlarmEnable(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(SP_WAKE_ALARM_ENABLE, 0);
    }

    public static void setWakeAlarmVibrate(Context context, int i) {
        FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit().putInt(SP_WAKE_ALARM_VIBRATE, i).apply();
    }

    public static int getWakeAlarmVibrate(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(SP_WAKE_ALARM_VIBRATE, 1);
    }

    public static String getWakeAlarmAlert(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(SP_WAKE_ALARM_ALERT, "");
    }

    public static Long getWakeAlarmSkipTime(SharedPreferences sharedPreferences) {
        return Long.valueOf(sharedPreferences.getLong(SP_WAKE_ALARM_SKIP_TIME, 0L));
    }

    public static int updateWakeAlarm(Context context, ContentValues contentValues) {
        Log.i("DC:BedtimeUtil", "update WakeAlarm form ContentProvider");
        if (contentValues == null) {
            return 0;
        }
        SharedPreferences.Editor editorEdit = FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit();
        for (String str : contentValues.keySet()) {
            if ("alert".equals(str)) {
                editorEdit.putString(SP_WAKE_ALARM_ALERT, contentValues.getAsString(str)).apply();
            } else if ("enabled".equals(str)) {
                editorEdit.putInt(SP_WAKE_ALARM_ENABLE, contentValues.getAsInteger(str).intValue()).apply();
            } else if ("hour".equals(str)) {
                editorEdit.putInt(SP_WAKE_ALARM_HOUR, contentValues.getAsInteger(str).intValue()).apply();
            } else if ("minutes".equals(str)) {
                editorEdit.putInt(SP_WAKE_ALARM_MINUTES, contentValues.getAsInteger(str).intValue()).apply();
            } else if ("alarmtime".equals(str)) {
                editorEdit.putLong(SP_WAKE_ALARM_ALARM_TIME, contentValues.getAsLong(str).longValue()).apply();
            } else if ("daysofweek".equals(str)) {
                editorEdit.putInt(SP_WAKE_ALARM_DAYS_OF_WEEK, contentValues.getAsInteger(str).intValue()).apply();
            } else if ("vibrate".equals(str)) {
                editorEdit.putInt(SP_WAKE_ALARM_VIBRATE, contentValues.getAsBoolean(str).booleanValue() ? 1 : 0).apply();
            } else if ("skiptime".equals(str)) {
                editorEdit.putLong(SP_WAKE_ALARM_SKIP_TIME, contentValues.getAsLong(str).longValue()).apply();
            }
        }
        return contentValues.size();
    }

    public static int resetWakeAlarm(Context context) {
        Log.i("DC:BedtimeUtil", "delete wake alarm form ContentProvider");
        return 0;
    }

    public static void transferWakeAlarmToSp(Context context) {
        try {
            Cursor cursorQuery = context.getContentResolver().query(SleepAlarmTable.CONTENT_URI_DB, Alarm.Columns.ALARM_QUERY_COLUMNS, null, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst()) {
                        FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit().putInt(SP_WAKE_ALARM_ENABLE, cursorQuery.getInt(5)).putInt(SP_WAKE_ALARM_HOUR, cursorQuery.getInt(1)).putInt(SP_WAKE_ALARM_MINUTES, cursorQuery.getInt(2)).putInt(SP_WAKE_ALARM_DAYS_OF_WEEK, cursorQuery.getInt(3)).putLong(SP_WAKE_ALARM_ALARM_TIME, cursorQuery.getLong(4)).putInt(SP_WAKE_ALARM_VIBRATE, cursorQuery.getInt(6)).putString(SP_WAKE_ALARM_ALERT, cursorQuery.getString(8)).putLong(SP_WAKE_ALARM_SKIP_TIME, cursorQuery.getLong(11)).apply();
                    }
                    setTransferState(context, true);
                } finally {
                    cursorQuery.close();
                }
            }
        } catch (Exception e) {
            Log.e("DC:BedtimeUtil", "transWakeAlarm error: " + e.getMessage());
        }
    }

    public static boolean hasTransferWakeAlarmToSp(Context context) {
        return FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).getBoolean(TRANSFER_WAKE_ALARM_TO_SP, false);
    }

    public static void setTransferState(Context context, boolean z) {
        FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit().putBoolean(TRANSFER_WAKE_ALARM_TO_SP, z).apply();
    }

    public static int getBannerShowCount(Context context) {
        return FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).getInt(KEY_SHOW_BEDTIME_BANNER_COUNT, 0);
    }

    public static void addBannerShowCount(Context context) {
        FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit().putInt(KEY_SHOW_BEDTIME_BANNER_COUNT, getBannerShowCount(context) + 1).apply();
    }

    public static boolean isWakeAlarmSupport(Context context) {
        return MiuiSdk.isSupportSleep() && bedTimeAlarmCompleted(context) && isBedtimeOpen(context);
    }

    public static Cursor queryWakeAlarmForAddition(Context context) {
        Log.i("DC:BedtimeUtil", "query WakeAlarm form AdditionProvider");
        if (Util.isInternational()) {
            return null;
        }
        MatrixCursor matrixCursor = new MatrixCursor(WAKE_ALARM_PROJECTION);
        if (bedTimeAlarmCompleted(context) && isBedtimeOpen(context)) {
            SharedPreferences sharedPreferences = FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0);
            matrixCursor.addRow(new Object[]{Integer.MIN_VALUE, Integer.valueOf(getWakeAlarmHour(sharedPreferences)), Integer.valueOf(getWakeAlarmMins(sharedPreferences)), Integer.valueOf(getWakeDaysOfWeek(sharedPreferences)), Long.valueOf(getWakeAlarmAlertTime(sharedPreferences)), Integer.valueOf(getWakeAlarmEnable(sharedPreferences)), Integer.valueOf(getWakeAlarmVibrate(sharedPreferences)), "", getWakeAlarmAlert(sharedPreferences), 0, 0, getWakeAlarmSkipTime(sharedPreferences)});
        }
        return matrixCursor;
    }
}
