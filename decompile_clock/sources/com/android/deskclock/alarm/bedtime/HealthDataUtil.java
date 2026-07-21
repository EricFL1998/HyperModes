package com.android.deskclock.alarm.bedtime;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.android.deskclock.Alarm;
import com.android.deskclock.R2;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.StatHelper;
import com.xiaomi.onetrack.util.ac;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class HealthDataUtil {
    public static final String DOWNLOAD_URI = "mimarket://details/detailmini?id=com.mi.health";
    private static final String EXTRAS_FORCE_STOP = "force_stop";
    public static final String HEALTH_DATA_CODE = "code";
    private static final String HEALTH_PACKAGE_NAME = "com.mi.health";
    public static final String HEALTH_PROVIDER_AUTHORITIES = "content://com.mi.health.provider.main";
    private static final int MINUTE_IN_MILLISECOND = 60000;
    private static final int NOT_INITIALIZATION_CODE = 2000;
    private static final int QUERY_VALID_CODE = 0;
    public static final String RECORD_SETTING_URI = "com.mi.health://localhost/d?action=deskclock&origin=deskclock";
    private static final String REPEAT = "repeat";
    public static final String SLEEP_CONFIG_PATH = "/sleep/config";
    private static final String SLEEP_HOUR = "sleep_hour";
    public static final String SLEEP_LAUNCH_PATH = "/sleep/homepage";
    private static final String SLEEP_MIN = "sleep_min";
    public static final String SLEEP_PATH = "/sleep";
    public static final String SLEEP_RECORD_BEGIN = "schedule_begin";
    public static final String SLEEP_RECORD_END = "schedule_end";
    public static final String SLEEP_RECORD_PATH = "/sleep/record";
    public static final String SLEEP_REPORT_PATH = "/sleep/report";
    public static final String SLEEP_SCHEDULE_PATH = "/sleep/schedule";
    private static final int STATE_IN_SLEEP = 8;
    private static final String TAG = "DC:HealthDataUtil";
    private static final String TRACE_ENABLE = "trace_enable";
    private static final int VERSION_CODE_SUPPORT_INTENT = 20203;
    private static final String WAKE_HOUR = "wake_hour";
    private static final String WAKE_MIN = "wake_min";

    public static boolean isHealthAppValuable(Context context) {
        Cursor cursor = null;
        try {
            try {
                Cursor cursorQuery = context.getContentResolver().query(Uri.parse("content://com.mi.health.provider.main/sleep"), null, null, null, null);
                if (cursorQuery != null) {
                    Log.i(TAG, "getHealthAppValuable, code:  " + cursorQuery.getExtras().getInt("code"));
                }
                if (cursorQuery == null || cursorQuery.getExtras().getInt("code") != 0) {
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    return false;
                }
                if (cursorQuery == null) {
                    return true;
                }
                cursorQuery.close();
                return true;
            } catch (Exception e) {
                com.android.deskclock.util.Log.e(TAG, "isHealthAppValuable error, " + e.getMessage());
                if (0 != 0) {
                    cursor.close();
                }
                return false;
            }
        } catch (Throwable th) {
            if (0 != 0) {
                cursor.close();
            }
            throw th;
        }
    }

    public static int updateSleepSchedule(Context context, int i, int i2) {
        try {
            ContentValues scheduleValues = getScheduleValues(context);
            scheduleValues.put("sleep_hour", Integer.valueOf(i));
            scheduleValues.put(SLEEP_MIN, Integer.valueOf(i2));
            return context.getContentResolver().update(Uri.parse("content://com.mi.health.provider.main/sleep/schedule"), scheduleValues, null, null);
        } catch (IllegalStateException e) {
            com.android.deskclock.util.Log.e(TAG, "updateSleepSchedule error, " + e.getMessage());
            try {
                try {
                    return Integer.valueOf(e.getMessage()).intValue();
                } catch (Exception e2) {
                    com.android.deskclock.util.Log.e(TAG, "updateSleepSchedule error, " + e2.getMessage());
                    return -1;
                }
            } catch (Throwable unused) {
                return -1;
            }
        } catch (Exception e3) {
            com.android.deskclock.util.Log.e(TAG, "updateSleepSchedule error, " + e3.getMessage());
            return -1;
        }
    }

    public static int updateWakeSchedule(Context context, int i, int i2) {
        try {
            ContentValues scheduleValues = getScheduleValues(context);
            scheduleValues.put("wake_hour", Integer.valueOf(i));
            scheduleValues.put(WAKE_MIN, Integer.valueOf(i2));
            return context.getContentResolver().update(Uri.parse("content://com.mi.health.provider.main/sleep/schedule"), scheduleValues, null, null);
        } catch (IllegalStateException e) {
            com.android.deskclock.util.Log.e(TAG, "updateWakeSchedule error, " + e.getMessage());
            try {
                try {
                    return Integer.valueOf(e.getMessage()).intValue();
                } catch (Exception e2) {
                    com.android.deskclock.util.Log.e(TAG, "updateWakeSchedule error, " + e2.getMessage());
                    return -1;
                }
            } catch (Throwable unused) {
                return -1;
            }
        } catch (Exception e3) {
            com.android.deskclock.util.Log.e(TAG, "updateWakeSchedule error, " + e3.getMessage());
            return -1;
        }
    }

    public static int updateRepeatType(Context context, Alarm.DaysOfWeek daysOfWeek) {
        try {
            ContentValues scheduleValues = getScheduleValues(context);
            scheduleValues.put(REPEAT, Integer.valueOf(getRepeatType(daysOfWeek)));
            return context.getContentResolver().update(Uri.parse("content://com.mi.health.provider.main/sleep/schedule"), scheduleValues, null, null);
        } catch (IllegalStateException e) {
            com.android.deskclock.util.Log.e(TAG, "updateRepeatType error, " + e.getMessage());
            try {
                try {
                    return Integer.valueOf(e.getMessage()).intValue();
                } catch (Exception e2) {
                    com.android.deskclock.util.Log.e(TAG, "updateRepeatType error, " + e2.getMessage());
                    return -1;
                }
            } catch (Throwable unused) {
                return -1;
            }
        } catch (Exception e3) {
            com.android.deskclock.util.Log.e(TAG, "updateRepeatType error, " + e3.getMessage());
            return -1;
        }
    }

    public static int updateRepeatType(Context context, int i) {
        try {
            ContentValues scheduleValues = getScheduleValues(context);
            scheduleValues.put(REPEAT, Integer.valueOf(i));
            return context.getContentResolver().update(Uri.parse("content://com.mi.health.provider.main/sleep/schedule"), scheduleValues, null, null);
        } catch (IllegalStateException e) {
            com.android.deskclock.util.Log.e(TAG, "updateRepeatType error, " + e.getMessage());
            try {
                try {
                    return Integer.valueOf(e.getMessage()).intValue();
                } catch (Exception e2) {
                    com.android.deskclock.util.Log.e(TAG, "updateRepeatType error, " + e2.getMessage());
                    return -1;
                }
            } catch (Throwable unused) {
                return -1;
            }
        } catch (Exception e3) {
            com.android.deskclock.util.Log.e(TAG, "updateRepeatType error, " + e3.getMessage());
            return -1;
        }
    }

    public static int updateSchedule(Context context, int i, int i2, int i3, int i4, Alarm.DaysOfWeek daysOfWeek) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("sleep_hour", Integer.valueOf(i));
        contentValues.put(SLEEP_MIN, Integer.valueOf(i2));
        contentValues.put("wake_hour", Integer.valueOf(i3));
        contentValues.put(WAKE_MIN, Integer.valueOf(i4));
        contentValues.put(REPEAT, Integer.valueOf(getRepeatType(daysOfWeek)));
        try {
            return context.getContentResolver().update(Uri.parse("content://com.mi.health.provider.main/sleep/schedule"), contentValues, null, null);
        } catch (IllegalStateException e) {
            com.android.deskclock.util.Log.e(TAG, "updateSchedule error, " + e.getMessage());
            try {
                try {
                    return Integer.valueOf(e.getMessage()).intValue();
                } catch (Exception e2) {
                    com.android.deskclock.util.Log.e(TAG, "updateSchedule error, " + e2.getMessage());
                    return -1;
                }
            } catch (Throwable unused) {
                return -1;
            }
        } catch (Exception e3) {
            com.android.deskclock.util.Log.e(TAG, "updateSchedule error, " + e3.getMessage());
            return -1;
        }
    }

    public static int updateSchedule(Context context, ContentValues contentValues) {
        try {
            return context.getContentResolver().update(Uri.parse("content://com.mi.health.provider.main/sleep/schedule"), contentValues, null, null);
        } catch (IllegalStateException e) {
            com.android.deskclock.util.Log.e(TAG, "updateSchedule error, " + e.getMessage());
            try {
                try {
                    return Integer.valueOf(e.getMessage()).intValue();
                } catch (Exception e2) {
                    com.android.deskclock.util.Log.e(TAG, "updateSchedule error, " + e2.getMessage());
                    return -1;
                }
            } catch (Throwable unused) {
                return -1;
            }
        } catch (Exception e3) {
            com.android.deskclock.util.Log.e(TAG, "updateSchedule error, " + e3.getMessage());
            return -1;
        }
    }

    public static ContentValues getScheduleValues(Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("sleep_hour", Integer.valueOf(BedtimeUtil.getSleepAlarmHour(context)));
        contentValues.put(SLEEP_MIN, Integer.valueOf(BedtimeUtil.getSleepAlarmMin(context)));
        contentValues.put("wake_hour", Integer.valueOf(BedtimeUtil.getWakeAlarmHour(context)));
        contentValues.put(WAKE_MIN, Integer.valueOf(BedtimeUtil.getWakeAlarmMin(context)));
        contentValues.put(REPEAT, Integer.valueOf(getRepeatType(new Alarm.DaysOfWeek(BedtimeUtil.getRepeatType(context)))));
        return contentValues;
    }

    public static void setHealthData(final Context context) {
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.HealthDataUtil.1
            @Override // java.lang.Runnable
            public void run() {
                if (HealthDataUtil.isHealthAppValuable(context)) {
                    Context context2 = context;
                    HealthDataUtil.updateSchedule(context2, HealthDataUtil.getScheduleValues(context2));
                }
            }
        });
    }

    public static int getRepeatType(Alarm.DaysOfWeek daysOfWeek) {
        if (daysOfWeek == null || (daysOfWeek.getCoded() & 127) == 0) {
            return -1;
        }
        return daysOfWeek.getCoded();
    }

    /* JADX WARN: Code duplicated, block: B:31:0x00d1  */
    /* JADX WARN: Code duplicated, block: B:51:0x0128  */
    public static SleepReport querySleepReport(Context context, long j, boolean z) throws Throwable {
        Cursor cursorQuery;
        SleepReport sleepReport;
        Cursor cursor = null;
        sleepReport = null;
        sleepReport = null;
        sleepReport = null;
        sleepReport = null;
        sleepReport = null;
        SleepReport sleepReport2 = null;
        if (!MiuiSdk.isSupportSleep() || !queryRecordState(context) || !BedtimeUtil.isBedtimeOpen(context)) {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRAS_FORCE_STOP, true);
        try {
            try {
                context.getContentResolver().call(Uri.parse("content://com.mi.health.provider.main/sleep"), "content://com.mi.health.provider.main/sleep#schedule_end", (String) null, bundle);
            } catch (Exception e) {
                com.android.deskclock.util.Log.e(TAG, "stopSleepRecord error, " + e.getMessage());
            }
        } catch (Exception unused) {
            initHealthApp(context);
            context.getContentResolver().call(Uri.parse("content://com.mi.health.provider.main/sleep"), "content://com.mi.health.provider.main/sleep#schedule_end", (String) null, bundle);
        }
        try {
            if (j == 0) {
                cursorQuery = context.getContentResolver().query(Uri.parse("content://com.mi.health.provider.main/sleep/report"), null, null, null, null);
            } else {
                cursorQuery = context.getContentResolver().query(Uri.parse("content://com.mi.health.provider.main/sleep/report"), null, "date_time = ?", new String[]{String.valueOf(j)}, null);
            }
            if (cursorQuery != null) {
                try {
                    try {
                        if (cursorQuery.getExtras().getInt("code") != 0) {
                            if (cursorQuery != null && cursorQuery.getExtras().getInt("code") == 2000) {
                                initHealthApp(context);
                            }
                        } else if (cursorQuery.moveToFirst()) {
                            do {
                                sleepReport = new SleepReport(cursorQuery.getLong(cursorQuery.getColumnIndex("sleep_time")), cursorQuery.getLong(cursorQuery.getColumnIndex(StatHelper.KEY_WAKE_TIME)), (int) (cursorQuery.getLong(cursorQuery.getColumnIndex("duration")) / 60000));
                            } while (cursorQuery.moveToNext());
                            sleepReport2 = sleepReport;
                        }
                    } catch (Throwable th) {
                        th = th;
                        cursor = cursorQuery;
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                } catch (Exception e2) {
                    e = e2;
                    com.android.deskclock.util.Log.e(TAG, "querySleepReport error, " + e.getMessage());
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    return sleepReport2;
                }
            } else if (cursorQuery != null) {
                initHealthApp(context);
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            if (sleepReport2 != null) {
                com.android.deskclock.util.Log.i(TAG, "get SleepReport " + sleepReport2.toString());
            }
            return sleepReport2;
        } catch (Exception e3) {
            e = e3;
            cursorQuery = null;
        } catch (Throwable th2) {
            th = th2;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public static Cursor querySleepRecord(Context context, long j, long j2) {
        try {
            Cursor cursorQuery = context.getContentResolver().query(Uri.parse("content://com.mi.health.provider.main/sleep/record"), null, "date_time >= ? and date_time <= ?", new String[]{String.valueOf(j), String.valueOf(j2)}, null);
            if (cursorQuery != null && cursorQuery.getExtras().getInt("code") == 2000) {
                initHealthApp(context);
            }
            return cursorQuery;
        } catch (Exception e) {
            com.android.deskclock.util.Log.e(TAG, "querySleepReport error, " + e.getMessage());
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v10 */
    /* JADX WARN: Type inference failed for: r0v13 */
    /* JADX WARN: Type inference failed for: r0v2 */
    /* JADX WARN: Type inference failed for: r4v0 */
    /* JADX WARN: Type inference failed for: r4v1 */
    /* JADX WARN: Type inference failed for: r4v10 */
    /* JADX WARN: Type inference failed for: r4v13 */
    /* JADX WARN: Type inference failed for: r4v14 */
    /* JADX WARN: Type inference failed for: r4v15 */
    /* JADX WARN: Type inference failed for: r4v16 */
    /* JADX WARN: Type inference failed for: r4v17 */
    /* JADX WARN: Type inference failed for: r4v2, types: [android.database.Cursor] */
    /* JADX WARN: Type inference failed for: r4v3, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r4v5 */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v7 */
    /* JADX WARN: Type inference failed for: r4v9 */
    public static void jumpToHealthData(Context context) throws Throwable {
        Exception exc;
        ?? r0;
        ?? string = 0;
        string = 0;
        string = 0;
        string = 0;
        string = 0;
        Cursor cursor = null;
        try {
            try {
                Cursor cursorQuery = context.getContentResolver().query(Uri.parse("content://com.mi.health.provider.main/sleep/homepage"), null, null, null, null);
                if (cursorQuery != null) {
                    try {
                        if (cursorQuery.getExtras().getInt("code") == 2000) {
                            initHealthApp(context);
                            cursorQuery.close();
                            cursorQuery = context.getContentResolver().query(Uri.parse("content://com.mi.health.provider.main/sleep/homepage"), null, null, null, null);
                        }
                    } catch (Exception e) {
                        Cursor cursor2 = cursorQuery;
                        exc = e;
                        r0 = string;
                        cursor = cursor2;
                        com.android.deskclock.util.Log.e(TAG, "jumpToHealthData error, " + exc.getMessage());
                        if (cursor != null) {
                            cursor.close();
                        }
                        string = r0;
                    } catch (Throwable th) {
                        th = th;
                        string = cursorQuery;
                        if (string != 0) {
                            string.close();
                        }
                        throw th;
                    }
                }
                if (cursorQuery != null && cursorQuery.getExtras().getInt("code") == 0) {
                    while (cursorQuery.moveToNext()) {
                        string = cursorQuery.getString(0);
                    }
                }
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            } catch (Exception e2) {
                exc = e2;
                r0 = 0;
            }
            if (string != 0) {
                try {
                    Intent uri = Intent.parseUri(string, 0);
                    uri.setFlags(268435456);
                    context.startActivity(uri);
                } catch (Exception e3) {
                    com.android.deskclock.util.Log.e(TAG, "jumpToHealthData error, " + e3.getMessage());
                }
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static final boolean isSupportSettingsIntent(Context context) {
        return Util.getPackageCode(HEALTH_PACKAGE_NAME) > VERSION_CODE_SUPPORT_INTENT;
    }

    public static final void jumpToHealthSettings(Context context) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(RECORD_SETTING_URI));
            intent.setFlags(268435456);
            context.startActivity(intent);
        } catch (Exception e) {
            com.android.deskclock.util.Log.e(TAG, "jumpToHealthSettings error, " + e.getMessage());
        }
    }

    public static final void downloadHealthApp(Context context) {
        try {
            Intent uri = Intent.parseUri(DOWNLOAD_URI, 0);
            uri.setFlags(268435456);
            context.startActivity(uri);
        } catch (Exception e) {
            com.android.deskclock.util.Log.e(TAG, "downloadHealthApp error, " + e.getMessage());
        }
    }

    public static int getIndex(long j) {
        long jCurrentTimeMillis = System.currentTimeMillis() - j;
        if (jCurrentTimeMillis >= 0) {
            return (int) (jCurrentTimeMillis / TimerDao.TIMER_MAX_LENGTH);
        }
        return ((int) (jCurrentTimeMillis / TimerDao.TIMER_MAX_LENGTH)) - 1;
    }

    public static void initHealthApp(Context context) {
        try {
            context.getContentResolver().update(Uri.parse("content://com.mi.health.provider.main/sleep/schedule"), getScheduleValues(context), null, null);
        } catch (Exception e) {
            com.android.deskclock.util.Log.e(TAG, "initHealthApp failed: " + e.getMessage());
        }
    }

    public static void setScheduleState(final Context context, final String str) {
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.HealthDataUtil.2
            @Override // java.lang.Runnable
            public void run() {
                String str2 = "content://com.mi.health.provider.main/sleep#" + str;
                try {
                    try {
                        if (HealthDataUtil.queryRecordState(context) && BedtimeUtil.isBedtimeOpen(context)) {
                            context.getContentResolver().call(Uri.parse("content://com.mi.health.provider.main/sleep"), str2, (String) null, (Bundle) null);
                        }
                    } catch (Exception unused) {
                        HealthDataUtil.initHealthApp(context);
                        context.getContentResolver().call(Uri.parse("content://com.mi.health.provider.main/sleep"), str2, (String) null, (Bundle) null);
                    }
                } catch (Exception e) {
                    com.android.deskclock.util.Log.e(HealthDataUtil.TAG, "setScheduleState error, " + e.getMessage());
                }
            }
        });
    }

    public static void stopSleepRecord(final Context context) {
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.alarm.bedtime.HealthDataUtil.3
            @Override // java.lang.Runnable
            public void run() {
                if (HealthDataUtil.queryRecordState(context) && BedtimeUtil.isBedtimeOpen(context)) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(HealthDataUtil.EXTRAS_FORCE_STOP, true);
                    try {
                        try {
                            context.getContentResolver().call(Uri.parse("content://com.mi.health.provider.main/sleep"), "content://com.mi.health.provider.main/sleep#schedule_end", (String) null, bundle);
                        } catch (Exception e) {
                            com.android.deskclock.util.Log.e(HealthDataUtil.TAG, "stopSleepRecord error, " + e.getMessage());
                        }
                    } catch (Exception unused) {
                        HealthDataUtil.initHealthApp(context);
                        context.getContentResolver().call(Uri.parse("content://com.mi.health.provider.main/sleep"), "content://com.mi.health.provider.main/sleep#schedule_end", (String) null, bundle);
                    }
                }
            }
        });
    }

    public static HealthDataResult queryHealthData(Context context) {
        if (context == null || !isHealthAppValuable(context) || !queryRecordState(context) || !BedtimeUtil.isBedtimeOpen(context)) {
            return null;
        }
        HealthDataResult healthDataResult = new HealthDataResult(context);
        long morningTimeStamp = BedtimeUtil.getMorningTimeStamp();
        Cursor cursorQuerySleepRecord = querySleepRecord(context, morningTimeStamp - ac.a, morningTimeStamp);
        if (cursorQuerySleepRecord != null && cursorQuerySleepRecord.getExtras().getInt("code") == 0 && cursorQuerySleepRecord.moveToFirst()) {
            do {
                int i = cursorQuerySleepRecord.getInt(cursorQuerySleepRecord.getColumnIndex("stage"));
                long j = cursorQuerySleepRecord.getLong(cursorQuerySleepRecord.getColumnIndex("begin_time"));
                long j2 = cursorQuerySleepRecord.getLong(cursorQuerySleepRecord.getColumnIndex("end_time"));
                com.android.deskclock.util.Log.i("DC:QueryHealthRecord", "get sleep record from health app, stage: " + i + "  begin_time: " + Util.formatTimeForLog(j) + "  end_time: " + Util.formatTimeForLog(j2));
                if (j != j2) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(j2);
                    calendar.set(10, 0);
                    calendar.set(12, 0);
                    calendar.set(13, 0);
                    calendar.set(9, 0);
                    int index = getIndex(calendar.getTimeInMillis());
                    long timeInMillis = ((long) (healthDataResult.thresholdMins * 60000)) + calendar.getTimeInMillis();
                    long timeInMillis2 = (calendar.getTimeInMillis() + ((long) (healthDataResult.thresholdMins * 60000))) - TimerDao.TIMER_MAX_LENGTH;
                    if (j2 < timeInMillis2) {
                        addSleepRecord(healthDataResult, j, j2, i, index + 1);
                    } else if (j > timeInMillis) {
                        addSleepRecord(healthDataResult, j, j2, i, index - 1);
                    } else if (j < timeInMillis2) {
                        addSleepRecord(healthDataResult, j, timeInMillis2, i, index + 1);
                        addSleepRecord(healthDataResult, timeInMillis2, j2, i, index);
                    } else if (timeInMillis >= j2 && j >= timeInMillis2) {
                        addSleepRecord(healthDataResult, j, j2, i, index);
                    } else {
                        addSleepRecord(healthDataResult, j, timeInMillis, i, index);
                        addSleepRecord(healthDataResult, timeInMillis, j2, i, index - 1);
                    }
                }
            } while (cursorQuerySleepRecord.moveToNext());
        }
        if (cursorQuerySleepRecord != null) {
            cursorQuerySleepRecord.close();
        }
        healthDataResult.upLimitTime = BedtimeUtil.getTopMins(context, healthDataResult.upLimitTime);
        healthDataResult.lowLimitTime = BedtimeUtil.getBottomMins(context, healthDataResult.lowLimitTime);
        return healthDataResult;
    }

    private static void addSleepRecord(HealthDataResult healthDataResult, long j, long j2, int i, int i2) {
        if (i2 < 0 || i2 >= 7) {
            return;
        }
        int minsOfDay = BedtimeUtil.getMinsOfDay(j);
        int minsOfDay2 = BedtimeUtil.getMinsOfDay(j2);
        if (minsOfDay > minsOfDay2) {
            minsOfDay -= 1440;
        }
        if (minsOfDay < healthDataResult.thresholdMins - 1440) {
            minsOfDay += R2.attr.mSpecialWidth;
            minsOfDay2 += R2.attr.mSpecialWidth;
        }
        if (minsOfDay2 > healthDataResult.thresholdMins) {
            minsOfDay -= 1440;
            minsOfDay2 -= 1440;
        }
        if (healthDataResult.upLimitTime > minsOfDay) {
            healthDataResult.upLimitTime = minsOfDay;
        }
        if (healthDataResult.lowLimitTime < minsOfDay2) {
            healthDataResult.lowLimitTime = minsOfDay2;
        }
        healthDataResult.sleepRecordList.add(new SleepRecord(minsOfDay, minsOfDay2, i, i2));
        if (i == 8) {
            SleepSummary sleepSummary = healthDataResult.sleepSummarytList.get(i2);
            if (sleepSummary.beginTime > minsOfDay) {
                sleepSummary.beginTime = minsOfDay;
            }
            if (sleepSummary.endTime < minsOfDay2) {
                sleepSummary.endTime = minsOfDay2;
            }
            sleepSummary.duration += j2 - j;
        }
    }

    /* JADX WARN: Code duplicated, block: B:14:0x004f  */
    public static boolean queryRecordState(Context context) throws Throwable {
        Cursor cursorQuery = null;
        try {
            try {
                Cursor cursorQuery2 = context.getContentResolver().query(Uri.parse("content://com.mi.health.provider.main/sleep/config"), new String[]{TRACE_ENABLE}, null, null, null);
                if (cursorQuery2 != null) {
                    try {
                        if (cursorQuery2.getExtras().getInt("code") == 2000) {
                            initHealthApp(context);
                            cursorQuery2.close();
                            cursorQuery = context.getContentResolver().query(Uri.parse("content://com.mi.health.provider.main/sleep/config"), new String[]{TRACE_ENABLE}, null, null, null);
                        } else {
                            cursorQuery = cursorQuery2;
                        }
                    } catch (Exception e) {
                        e = e;
                        cursorQuery = cursorQuery2;
                    } catch (Throwable th) {
                        th = th;
                        cursorQuery = cursorQuery2;
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        throw th;
                    }
                } else {
                    cursorQuery = cursorQuery2;
                }
                if (cursorQuery == null || cursorQuery.getExtras().getInt("code") != 0 || !cursorQuery.moveToNext()) {
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    return false;
                }
                boolean z = cursorQuery.getInt(cursorQuery.getColumnIndex(TRACE_ENABLE)) == 1;
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                return z;
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
        com.android.deskclock.util.Log.f(TAG, "queryRecordState exception, " + e.getMessage());
        if (cursorQuery != null) {
            cursorQuery.close();
        }
        return false;
    }
}
