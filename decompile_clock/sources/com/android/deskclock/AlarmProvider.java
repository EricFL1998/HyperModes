package com.android.deskclock;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.core.app.NotificationCompat;
import com.android.deskclock.addition.monitor.MonitorHelper;
import com.android.deskclock.addition.monitor.data.AlarmAlertTable;
import com.android.deskclock.addition.monitor.data.AlarmBackupTable;
import com.android.deskclock.addition.monitor.data.AlarmModifyTable;
import com.android.deskclock.addition.ringtone.RingtoneUriCompat;
import com.android.deskclock.addition.ringtone.star.WYStarRingtoneHelper;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper;
import com.android.deskclock.alarm.AlarmClockFragment;
import com.android.deskclock.alarm.alert.AlarmService;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.alarm.bedtime.SleepAlarmTable;
import com.android.deskclock.alarm.lifepost.model.GalleryTable;
import com.android.deskclock.alarm.lifepost.model.NewsTable;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmTable;
import com.android.deskclock.appaf.AppSearchUtil;
import com.android.deskclock.settings.AlarmSettingsFragment;
import com.android.deskclock.stopwatch.Stopwatch;
import com.android.deskclock.timer.CommonTimerTable;
import com.android.deskclock.timer.CommonTimerTableNew;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.timer.TimerHistoryTable;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.AlarmUtils;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.ResidentCityUtils;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.permission.UserNoticeUtil;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.worldclock.WorldClock;
import com.xiaomi.onetrack.util.z;
import java.util.HashSet;

/* JADX INFO: loaded from: classes.dex */
public class AlarmProvider extends ContentProvider {
    private static final int ALARMS = 1;
    private static final int ALARMS_ASSIST = 24;
    private static final int ALARMS_ID = 2;
    private static final int ALARMS_ID_ASSIST = 25;
    private static final int ALARMS_ID_VOICE_ASSIST = 19;
    private static final int ALARMS_VOICE_ASSIST = 18;
    private static final int ALARM_ALERT = 13;
    private static final int ALARM_ALERT_ID = 14;
    private static final int ALARM_BACKUP = 11;
    private static final int ALARM_BACKUP_ID = 12;
    private static final String ALARM_ID = "alarm_id";
    private static final int ALARM_MODIFY = 15;
    private static final int ALARM_MODIFY_ID = 16;
    private static final String AUTHORITY = "com.android.deskclock";
    private static final int COMMON_TIMERS = 21;
    private static final int COMMON_TIMERS_ID = 22;
    private static final int COMMON_TIMERS_ID_NEW = 29;
    private static final int COMMON_TIMERS_NEW = 28;
    private static final int GALLERY = 9;
    private static final int GALLERY_ID = 10;
    protected static final String KEY_IS_SMART_RINGTONE = "is_smart_ringtone";
    private static final String METHOD_ALARM_RINGING_STATUS = "alarmRingingStatus";
    private static final String METHOD_CHECK_NET_PERMISSION = "checkNetPermission";
    private static final String METHOD_CLOSE_ALARM_ALERT = "closeAlarmAlert";
    private static final String METHOD_CLOSE_SNOOZE_ALARM_BY_ID = "closeSnoozeAlarmById";
    private static final String METHOD_DEFAULT_ALARM_ALERT = "defaultAlarmAlert";
    private static final String METHOD_QUERY_SNOOZE_ID = "querySnoozeId";
    private static final String METHOD_SNOOZE_ALARM_ALERT = "snoozeAlarmAlert";
    private static final String METHOD_SNOOZE_ALERT_TIME = "snoozeAlertTime";
    private static final String METHOD_UPDATE_ALARM_RINGTONE = "updateAlarmRingtone";
    private static final int NEWS = 7;
    private static final int NEWS_ID = 8;
    private static final int RESIDENTCITY = 27;
    private static final int SHIFT_ALARMS = 34;
    private static final int SLEEP_ALARMS = 17;
    private static final int SLEEP_ALARMS_DB = 23;
    private static final String SNOOZE_ID = "snooze_id";
    private static final int STOPWATCH = 5;
    private static final int STOPWATCH_ID = 6;
    private static final String TAG = "DC:AlarmProvider";
    private static final String TAG_ALARM_ASSIST = "DC:AlarmProvider:AlarmAssist";
    private static final String TAG_VOICE_ASSIST = "DC:AlarmProvider:VoiceAssist";
    private static final int TIMER_COUNT_DOWN = 20;
    private static final int TIMER_HISTORY = 32;
    private static final int TIMER_HISTORY_ID = 33;
    private static final int TIMEZONE_SEARCH_CITI_NEW = 31;
    public static final String VOICE_ASSIST_PKG = "com.miui.voiceassist";
    private static final int WORLDCLOCK_CITIES = 26;
    private static final int WORLDCLOCK_CITIES_NEW_V1 = 30;
    private static final int WORLD_CLOCKS = 3;
    private static final int WORLD_CLOCKS_ID = 4;
    private static final UriMatcher sURLMatcher;
    private AdditionDatabaseHelper mAdditionalOpenHelper;
    private AlarmDatabaseHelper mOpenHelper;

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sURLMatcher = uriMatcher;
        uriMatcher.addURI("com.android.deskclock", "alarm", 1);
        uriMatcher.addURI("com.android.deskclock", "alarm/#", 2);
        uriMatcher.addURI("com.android.deskclock", "worldclock", 3);
        uriMatcher.addURI("com.android.deskclock", "worldclock/#", 4);
        uriMatcher.addURI("com.android.deskclock", NotificationCompat.CATEGORY_STOPWATCH, 5);
        uriMatcher.addURI("com.android.deskclock", "stopwatch/#", 6);
        uriMatcher.addURI("com.android.deskclock", NewsTable.TABLE_NAME, 7);
        uriMatcher.addURI("com.android.deskclock", "news/#", 8);
        uriMatcher.addURI("com.android.deskclock", "gallery", 9);
        uriMatcher.addURI("com.android.deskclock", "gallery/#", 10);
        uriMatcher.addURI("com.android.deskclock", AlarmBackupTable.TABLE_NAME, 11);
        uriMatcher.addURI("com.android.deskclock", "alarm_backup/#", 12);
        uriMatcher.addURI("com.android.deskclock", AlarmAlertTable.TABLE_NAME, 13);
        uriMatcher.addURI("com.android.deskclock", "alarm_alert/#", 14);
        uriMatcher.addURI("com.android.deskclock", AlarmModifyTable.TABLE_NAME, 15);
        uriMatcher.addURI("com.android.deskclock", "alarm_modify/#", 16);
        uriMatcher.addURI("com.android.deskclock", SleepAlarmTable.TABLE_NAME, 17);
        uriMatcher.addURI("com.android.deskclock", "sleep_alarms_db", 23);
        uriMatcher.addURI("com.android.deskclock", "alarm/voiceassist", 18);
        uriMatcher.addURI("com.android.deskclock", "alarm/#/voiceassist", 19);
        uriMatcher.addURI("com.android.deskclock", "timercountdown", 20);
        uriMatcher.addURI("com.android.deskclock", CommonTimerTable.TABLE_NAME, 21);
        uriMatcher.addURI("com.android.deskclock", "common_timers/#", 22);
        uriMatcher.addURI("com.android.deskclock", "alarm/alarmassist", 24);
        uriMatcher.addURI("com.android.deskclock", "alarm/#/alarmassist", 25);
        uriMatcher.addURI("com.android.deskclock", "worldclock_cities", 26);
        uriMatcher.addURI("com.android.deskclock", "residentcity", 27);
        uriMatcher.addURI("com.android.deskclock", CommonTimerTableNew.TABLE_NAME, 28);
        uriMatcher.addURI("com.android.deskclock", "common_timers_new/#", 29);
        uriMatcher.addURI("com.android.deskclock", "worldclock_cities_new_v1", 30);
        uriMatcher.addURI("com.android.deskclock", "timezone_search_city_new", 31);
        uriMatcher.addURI("com.android.deskclock", TimerHistoryTable.TABLE_NAME, 32);
        uriMatcher.addURI("com.android.deskclock", "timer_history/#", 33);
        uriMatcher.addURI("com.android.deskclock", ShiftAlarmTable.TABLE_NAME, 34);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        this.mOpenHelper = new AlarmDatabaseHelper(FBEUtil.createDeviceProtectedStorageContext(getContext()));
        this.mAdditionalOpenHelper = new AdditionDatabaseHelper(FBEUtil.createDeviceProtectedStorageContext(getContext()));
        return true;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        switch (sURLMatcher.match(uri)) {
            case 1:
                return "vnd.android.cursor.dir/alarms";
            case 2:
                return "vnd.android.cursor.item/alarms";
            case 3:
                return "vnd.android.cursor.dir/worldclocks";
            case 4:
                return "vnd.android.cursor.item/worldclocks";
            case 5:
                return "vnd.android.cursor.dir/stopwatchs";
            case 6:
                return "vnd.android.cursor.item/stopwatchs";
            case 7:
                return "vnd.android.cursor.dir/news";
            case 8:
                return "vnd.android.cursor.item/news";
            case 9:
                return "vnd.android.cursor.dir/gallery";
            case 10:
                return "vnd.android.cursor.item/gallery";
            case 11:
                return "vnd.android.cursor.dir/alarm_backup";
            case 12:
                return "vnd.android.cursor.item/alarm_backup";
            case 13:
                return "vnd.android.cursor.dir/alarm_alert";
            case 14:
                return "vnd.android.cursor.item/alarm_alert";
            case 15:
                return "vnd.android.cursor.dir/alarm_modify";
            case 16:
                return "vnd.android.cursor.item/alarm_modify";
            case 17:
                return "vnd.android.cursor.dir/sleep_alarms";
            case 18:
                return "vnd.android.cursor.dir/alarm/voiceassist";
            case 19:
                return "vnd.android.cursor.item/alarm/voiceassist";
            case 20:
                return "vnd.android.cursor.item/timercountdown";
            case 21:
                return "vnd.android.cursor.dir/common_timers";
            case 22:
                return "vnd.android.cursor.item/common_timers";
            case 23:
                return "vnd.android.cursor.dir/sleep_alarms_db";
            case 24:
                return "vnd.android.cursor.dir/alarm/alarmassist";
            case 25:
                return "vnd.android.cursor.item/alarm/alarmassist";
            case 26:
                return "vnd.android.cursor.dir/worldclock_cities";
            case 27:
                return "vnd.android.cursor.dir/residentcity";
            case 28:
                return "vnd.android.cursor.dir/common_timers_new";
            case 29:
                return "vnd.android.cursor.item/common_timers_new";
            case 30:
                return "vnd.android.cursor.dir/worldclock_cities_new_v1";
            case 31:
                return "vnd.android.cursor.dir/timezone_search_city_new";
            case 32:
                return "vnd.android.cursor.dir/timer_history";
            case 33:
                return "vnd.android.cursor.item/timer_history";
            case 34:
                return "vnd.android.cursor.dir/shift_alarms";
            default:
                throw new IllegalArgumentException("Unknown URL");
        }
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        switch (sURLMatcher.match(uri)) {
            case 1:
                sQLiteQueryBuilder.setTables("alarms");
                break;
            case 2:
                sQLiteQueryBuilder.setTables("alarms");
                sQLiteQueryBuilder.appendWhere("_id=");
                sQLiteQueryBuilder.appendWhere(uri.getPathSegments().get(1));
                break;
            case 3:
                sQLiteQueryBuilder.setTables("worldclocks");
                break;
            case 4:
                sQLiteQueryBuilder.setTables("worldclocks");
                sQLiteQueryBuilder.appendWhere("_id=");
                sQLiteQueryBuilder.appendWhere(uri.getPathSegments().get(1));
                break;
            case 5:
                sQLiteQueryBuilder.setTables("stopwatchs");
                break;
            case 6:
                sQLiteQueryBuilder.setTables("stopwatchs");
                sQLiteQueryBuilder.appendWhere("_id=");
                sQLiteQueryBuilder.appendWhere(uri.getPathSegments().get(1));
                break;
            case 7:
                sQLiteQueryBuilder.setTables(NewsTable.TABLE_NAME);
                break;
            case 8:
                sQLiteQueryBuilder.setTables(NewsTable.TABLE_NAME);
                sQLiteQueryBuilder.appendWhere("_id=");
                sQLiteQueryBuilder.appendWhere(uri.getPathSegments().get(1));
                break;
            case 9:
                sQLiteQueryBuilder.setTables("gallery");
                break;
            case 10:
                sQLiteQueryBuilder.setTables("gallery");
                sQLiteQueryBuilder.appendWhere("_id=");
                sQLiteQueryBuilder.appendWhere(uri.getPathSegments().get(1));
                break;
            case 11:
                sQLiteQueryBuilder.setTables(AlarmBackupTable.TABLE_NAME);
                break;
            case 12:
                sQLiteQueryBuilder.setTables(AlarmBackupTable.TABLE_NAME);
                sQLiteQueryBuilder.appendWhere("_id=");
                sQLiteQueryBuilder.appendWhere(uri.getPathSegments().get(1));
                break;
            case 13:
                sQLiteQueryBuilder.setTables(AlarmAlertTable.TABLE_NAME);
                break;
            case 14:
                sQLiteQueryBuilder.setTables(AlarmAlertTable.TABLE_NAME);
                sQLiteQueryBuilder.appendWhere("_id=");
                sQLiteQueryBuilder.appendWhere(uri.getPathSegments().get(1));
                break;
            case 15:
                sQLiteQueryBuilder.setTables(AlarmModifyTable.TABLE_NAME);
                break;
            case 16:
                sQLiteQueryBuilder.setTables(AlarmModifyTable.TABLE_NAME);
                sQLiteQueryBuilder.appendWhere("_id=");
                sQLiteQueryBuilder.appendWhere(uri.getPathSegments().get(1));
                break;
            case 17:
                return BedtimeUtil.queryWakeAlarm(getContext());
            case 18:
                Log.f(TAG_VOICE_ASSIST, "query alarms from xiaoai");
                sQLiteQueryBuilder.setTables("alarms");
                sQLiteQueryBuilder.appendWhere(Alarm.Columns.WHERE_NORMAL_ALARM);
                break;
            case 19:
                String str3 = uri.getPathSegments().get(1);
                Log.f(TAG_VOICE_ASSIST, "query alarm from xiaoai, id=" + str3);
                sQLiteQueryBuilder.setTables("alarms");
                sQLiteQueryBuilder.appendWhere("_id=");
                sQLiteQueryBuilder.appendWhere(str3);
                sQLiteQueryBuilder.appendWhere(" AND type=0");
                break;
            case 20:
                Log.f(TAG, "query timer form:" + getCallingPackage());
                return TimerDao.queryTimer(getContext());
            case 21:
                sQLiteQueryBuilder.setTables(CommonTimerTable.TABLE_NAME);
                break;
            case 22:
                sQLiteQueryBuilder.setTables(CommonTimerTable.TABLE_NAME);
                sQLiteQueryBuilder.appendWhere("_id=");
                sQLiteQueryBuilder.appendWhere(uri.getPathSegments().get(1));
                break;
            case 23:
                sQLiteQueryBuilder.setTables(SleepAlarmTable.TABLE_NAME);
                break;
            case 24:
                Log.i(TAG_ALARM_ASSIST, "query alarms from :" + getCallingPackage());
                sQLiteQueryBuilder.setTables("alarms");
                sQLiteQueryBuilder.appendWhere(Alarm.Columns.WHERE_NORMAL_ALARM);
                break;
            case 25:
                Log.i(TAG_ALARM_ASSIST, "query alarms from :" + getCallingPackage());
                sQLiteQueryBuilder.setTables("alarms");
                sQLiteQueryBuilder.appendWhere("_id=");
                sQLiteQueryBuilder.appendWhere(uri.getPathSegments().get(1));
                break;
            case 26:
                if (!Util.isInternational() && !UserNoticeUtil.isNetPermissionAgreed() && UserNoticeUtil.canRemindNetPermission()) {
                    return null;
                }
                Log.d(TAG, "query worldclock cities from: " + getCallingPackage());
                SQLiteDatabase readableDatabase = this.mOpenHelper.getReadableDatabase();
                SQLiteQueryBuilder sQLiteQueryBuilder2 = new SQLiteQueryBuilder();
                sQLiteQueryBuilder2.setTables("worldclocks");
                return AdditionUtil.getWorldClockCities(sQLiteQueryBuilder2.query(readableDatabase, strArr, str, strArr2, null, null, str2), DeskClockApp.getAppDEContext(), 3);
            case 27:
                Log.d(TAG, "query isSupportResidentCity from: " + getCallingPackage());
                MatrixCursor matrixCursor = new MatrixCursor(AdditionUtil.RESIDENT_PROJECTION);
                Object[] objArr = {Integer.valueOf(ResidentCityUtils.isResidentCity() ? 1 : 0)};
                Log.d(TAG, "isSupportResidentCity: " + objArr[0]);
                matrixCursor.addRow(objArr);
                return matrixCursor;
            case 28:
                SQLiteDatabase readableDatabase2 = this.mAdditionalOpenHelper.getReadableDatabase();
                sQLiteQueryBuilder.setTables(CommonTimerTableNew.TABLE_NAME);
                Cursor cursorQuery = sQLiteQueryBuilder.query(readableDatabase2, strArr, str, strArr2, null, null, str2);
                if (cursorQuery == null) {
                    Log.v("Common timers query failed");
                } else {
                    cursorQuery.setNotificationUri(getContext().getContentResolver(), uri);
                }
                return cursorQuery;
            case 29:
            case 33:
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
            case 30:
                if (!Util.isInternational() && !UserNoticeUtil.isNetPermissionAgreed() && UserNoticeUtil.canRemindNetPermission()) {
                    return null;
                }
                Log.d(TAG, "query world cities from: " + getCallingPackage());
                SQLiteDatabase readableDatabase3 = this.mOpenHelper.getReadableDatabase();
                SQLiteQueryBuilder sQLiteQueryBuilder3 = new SQLiteQueryBuilder();
                sQLiteQueryBuilder3.setTables("worldclocks");
                return AdditionUtil.getWorldClockCities(sQLiteQueryBuilder3.query(readableDatabase3, strArr, str, strArr2, null, null, str2), DeskClockApp.getAppDEContext(), 4);
            case 31:
                return AdditionUtil.getAddWorldClockCity(DeskClockApp.getAppContext(), uri);
            case 32:
                SQLiteDatabase readableDatabase4 = this.mAdditionalOpenHelper.getReadableDatabase();
                sQLiteQueryBuilder.setTables(TimerHistoryTable.TABLE_NAME);
                Cursor cursorQuery2 = sQLiteQueryBuilder.query(readableDatabase4, strArr, str, strArr2, null, null, str2);
                if (cursorQuery2 == null) {
                    Log.v("timer history query failed");
                } else {
                    cursorQuery2.setNotificationUri(getContext().getContentResolver(), uri);
                }
                return cursorQuery2;
            case 34:
                sQLiteQueryBuilder.setTables(ShiftAlarmTable.TABLE_NAME);
                break;
        }
        Cursor cursorQuery3 = sQLiteQueryBuilder.query(this.mOpenHelper.getReadableDatabase(), strArr, str, strArr2, null, null, str2);
        if (cursorQuery3 == null) {
            Log.v("AlarmHelper.query: failed");
        } else {
            cursorQuery3.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursorQuery3;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        int iUpdate;
        long j;
        long j2;
        long j3;
        long j4;
        int iMatch = sURLMatcher.match(uri);
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        if (iMatch == 1) {
            Log.i(TAG, "update all alarms: " + contentValues.toString());
            WYStarRingtoneHelper.updateWYStarAlertToDefault(contentValues);
            iUpdate = writableDatabase.update("alarms", contentValues, null, null);
            MonitorHelper.modify(7, System.currentTimeMillis(), -1, contentValues);
        } else {
            if (iMatch == 2) {
                Log.f(TAG, "update alarm");
                j2 = Long.parseLong(uri.getPathSegments().get(1));
                WYStarRingtoneHelper.updateWYStarAlertToDefault(contentValues);
                iUpdate = writableDatabase.update("alarms", contentValues, "_id=" + j2, null);
                int i = (int) j2;
                MonitorHelper.modify(7, System.currentTimeMillis(), i, contentValues);
                try {
                    if ("com.miui.voiceassist".equals(getCallingPackage()) && contentValues.containsKey("enabled") && contentValues.containsKey("daysofweek")) {
                        boolean zBooleanValue = contentValues.getAsBoolean("enabled").booleanValue();
                        int iIntValue = contentValues.getAsInteger("daysofweek").intValue();
                        if (!zBooleanValue && iIntValue != 0) {
                            Log.i(TAG_VOICE_ASSIST, "skip repeat alarm from old xiaoai: " + contentValues.toString());
                            StatHelper.trackEvent(StatHelper.KEY_SKIP_ALARM_FROM_OLD_XIAOAI);
                            OneTrackStatHelper.trackTriggerEvent(OneTrackStatHelper.ALARM_SKIP_FOR_OLD_XIAOAI);
                            clearIdentity();
                            AlarmHelper.skipAlarmForOnce(getContext(), i);
                        }
                    }
                } catch (Exception unused) {
                }
            } else if (iMatch == 4) {
                j2 = Long.parseLong(uri.getPathSegments().get(1));
                iUpdate = writableDatabase.update("worldclocks", contentValues, "_id=" + j2, null);
            } else if (iMatch != 22) {
                iUpdate = 0;
                if (iMatch == 25) {
                    Log.i(TAG_ALARM_ASSIST, "update alarms from :" + getCallingPackage() + z.b + contentValues.toString());
                    j2 = Long.parseLong(uri.getPathSegments().get(1));
                    WYStarRingtoneHelper.updateWYStarAlertToDefault(contentValues);
                    try {
                        boolean zBooleanValue2 = contentValues.getAsBoolean("enabled").booleanValue();
                        int iIntValue2 = contentValues.getAsInteger("daysofweek").intValue();
                        int iIntValue3 = contentValues.getAsInteger("hour").intValue();
                        int iIntValue4 = contentValues.getAsInteger("minutes").intValue();
                        if (iIntValue2 == 0 && zBooleanValue2) {
                            contentValues.put("skiptime", (Integer) 0);
                            contentValues.put("alarmtime", Long.valueOf(AlarmHelper.calculateAlarmTime(getContext(), iIntValue3, iIntValue4, iIntValue2).getTimeInMillis()));
                        } else if (iIntValue2 != 0 && zBooleanValue2) {
                            contentValues.put("alarmtime", (Integer) 0);
                            contentValues.put("skiptime", (Integer) 0);
                        } else if (iIntValue2 != 0 && !zBooleanValue2) {
                            contentValues.put("alarmtime", (Integer) 0);
                            contentValues.put("skiptime", Long.valueOf(AlarmHelper.calculateAlarmTime(getContext(), iIntValue3, iIntValue4, iIntValue2).getTimeInMillis()));
                        }
                        AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).updateAppSearchAlarmScheduled(AlarmHelper.getAlarm(DeskClockApp.getAppDEContext().getContentResolver(), (int) j2), zBooleanValue2);
                    } catch (Exception e) {
                        Log.e(TAG_ALARM_ASSIST, "alarm assist update error: " + e.getMessage());
                    }
                    boolean z = contentValues.containsKey("deleteAfterUse") && contentValues.getAsBoolean("deleteAfterUse").booleanValue() && contentValues.containsKey("daysofweek") && contentValues.getAsInteger("daysofweek").intValue() == 0;
                    Boolean[] boolArrIsSmartRingtone = isSmartRingtone(contentValues);
                    contentValues.put("deleteAfterUse", Boolean.valueOf(z));
                    int iUpdate2 = writableDatabase.update("alarms", contentValues, "_id=" + j2, null);
                    clearIdentity();
                    int i2 = (int) j2;
                    MonitorHelper.modify(7, System.currentTimeMillis(), i2, contentValues);
                    Log.d(TAG, "update alarm from xiaoai, isXiaoAiRingtone: " + boolArrIsSmartRingtone[0]);
                    if (boolArrIsSmartRingtone[0].booleanValue()) {
                        XiaoAiRingtoneHelper.addXiaoAiRingtoneIds(getContext(), i2);
                    } else {
                        if (boolArrIsSmartRingtone[1].booleanValue()) {
                            android.util.Log.d(TAG, "update alarm from xiaoai, not sure alarm ");
                            XiaoAiRingtoneHelper.preHandleNotSureAlarm(i2);
                        }
                        AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).updateAppSearchAlarmScheduled(AlarmHelper.getAlarm(DeskClockApp.getAppDEContext().getContentResolver(), i2), true);
                    }
                    AlarmHelper.setNextAlert(getContext());
                    iUpdate = iUpdate2;
                } else {
                    if (iMatch != 29) {
                        if (iMatch == 33) {
                            j3 = Long.parseLong(uri.getPathSegments().get(1));
                            iUpdate = writableDatabase.update(TimerHistoryTable.TABLE_NAME, contentValues, "_id=" + j3, null);
                        } else if (iMatch == 16) {
                            j3 = Long.parseLong(uri.getPathSegments().get(1));
                            iUpdate = writableDatabase.update(AlarmModifyTable.TABLE_NAME, contentValues, "_id=" + j3, null);
                        } else if (iMatch == 17) {
                            clearIdentity();
                            WYStarRingtoneHelper.updateWYStarAlertToDefault(contentValues);
                            iUpdate = BedtimeUtil.updateWakeAlarm(getContext(), contentValues);
                        } else if (iMatch == 19) {
                            String str2 = uri.getPathSegments().get(1);
                            long j5 = Long.parseLong(str2);
                            WYStarRingtoneHelper.updateWYStarAlertToDefault(contentValues);
                            Boolean[] boolArrIsSmartRingtone2 = isSmartRingtone(contentValues);
                            int iUpdate3 = writableDatabase.update("alarms", contentValues, "_id=" + j5, null);
                            Log.f(TAG_VOICE_ASSIST, "update alarm from xiaoai:" + contentValues.toString() + ", id:" + str2 + " count:" + iUpdate3 + " isXiaoAiRingtone: " + boolArrIsSmartRingtone2[0]);
                            clearIdentity();
                            int i3 = (int) j5;
                            MonitorHelper.modify(7, System.currentTimeMillis(), i3, contentValues);
                            if (boolArrIsSmartRingtone2[0].booleanValue()) {
                                XiaoAiRingtoneHelper.addXiaoAiRingtoneIds(getContext(), i3);
                            } else if (boolArrIsSmartRingtone2[1].booleanValue()) {
                                Log.d(TAG, "update alarm from xiaoai, not sure alarm ");
                                XiaoAiRingtoneHelper.preHandleNotSureAlarm(i3);
                            }
                            AlarmHelper.updateAlarmTime(getContext(), i3);
                            AlarmHelper.setNextAlert(getContext());
                            iUpdate = iUpdate3;
                            j = j5;
                        } else if (iMatch != 20) {
                            switch (iMatch) {
                                case 6:
                                    j3 = Long.parseLong(uri.getPathSegments().get(1));
                                    iUpdate = writableDatabase.update("stopwatchs", contentValues, "_id=" + j3, null);
                                    break;
                                case 7:
                                    j4 = Long.parseLong(uri.getPathSegments().get(1));
                                    iUpdate = writableDatabase.update(NewsTable.TABLE_NAME, contentValues, "_id=" + j4, null);
                                    j = j4;
                                    break;
                                case 8:
                                    j4 = Long.parseLong(uri.getPathSegments().get(1));
                                    iUpdate = writableDatabase.update(NewsTable.TABLE_NAME, contentValues, "_id=" + j4, null);
                                    j = j4;
                                    break;
                                case 9:
                                    j3 = Long.parseLong(uri.getPathSegments().get(1));
                                    iUpdate = writableDatabase.update("gallery", contentValues, "_id=" + j3, null);
                                    break;
                                case 10:
                                    j3 = Long.parseLong(uri.getPathSegments().get(1));
                                    iUpdate = writableDatabase.update("gallery", contentValues, "_id=" + j3, null);
                                    break;
                                default:
                                    switch (iMatch) {
                                        case 12:
                                            j3 = Long.parseLong(uri.getPathSegments().get(1));
                                            iUpdate = writableDatabase.update(AlarmBackupTable.TABLE_NAME, contentValues, "_id=" + j3, null);
                                            break;
                                        case 13:
                                            iUpdate = writableDatabase.update(AlarmAlertTable.TABLE_NAME, contentValues, str, strArr);
                                            break;
                                        case 14:
                                            j3 = Long.parseLong(uri.getPathSegments().get(1));
                                            iUpdate = writableDatabase.update(AlarmAlertTable.TABLE_NAME, contentValues, "_id=" + j3, null);
                                            break;
                                        default:
                                            throw new UnsupportedOperationException("Cannot update URL: " + uri);
                                    }
                                    break;
                            }
                        } else {
                            Log.f(TAG, "update timer form:" + getCallingPackage() + " values: " + contentValues);
                            clearIdentity();
                            TimerDao.updateTimer(getContext(), contentValues);
                        }
                        j = j3;
                    } else {
                        j2 = Long.parseLong(uri.getPathSegments().get(1));
                        iUpdate = this.mAdditionalOpenHelper.getWritableDatabase().update(CommonTimerTableNew.TABLE_NAME, contentValues, "_id=" + j2, null);
                    }
                    Log.v("*** notifyChange() rowId: " + j + " url " + uri);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return iUpdate;
                }
            } else {
                j2 = Long.parseLong(uri.getPathSegments().get(1));
                iUpdate = writableDatabase.update(CommonTimerTable.TABLE_NAME, contentValues, "_id=" + j2, null);
            }
            j = j2;
            Log.v("*** notifyChange() rowId: " + j + " url " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
            return iUpdate;
        }
        j = 0;
        Log.v("*** notifyChange() rowId: " + j + " url " + uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return iUpdate;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) throws Throwable {
        Uri uriCommonInsert;
        switch (sURLMatcher.match(uri)) {
            case 1:
                Log.d("AlarmProvider insert Alarm");
                WYStarRingtoneHelper.updateWYStarAlertToDefault(contentValues);
                contentValues.put("deleteAfterUse", Boolean.valueOf(contentValues.containsKey("deleteAfterUse") && contentValues.getAsBoolean("deleteAfterUse").booleanValue() && contentValues.containsKey("daysofweek") && contentValues.getAsInteger("daysofweek").intValue() == 0));
                uriCommonInsert = this.mOpenHelper.commonInsert(contentValues);
                MonitorHelper.modify(5, System.currentTimeMillis(), (int) Long.parseLong(uriCommonInsert.getLastPathSegment()), contentValues);
                break;
            case 3:
                long jInsert = this.mOpenHelper.getWritableDatabase().insert("worldclocks", null, contentValues);
                if (jInsert < 0) {
                    throw new SQLException("Failed to insert row");
                }
                uriCommonInsert = ContentUris.withAppendedId(WorldClock.CONTENT_URI, jInsert);
                break;
                break;
            case 5:
                long jInsert2 = this.mOpenHelper.getWritableDatabase().insert("stopwatchs", null, contentValues);
                if (jInsert2 < 0) {
                    throw new SQLException("Failed to insert row");
                }
                uriCommonInsert = ContentUris.withAppendedId(Stopwatch.CONTENT_URI, jInsert2);
                break;
                break;
            case 7:
                long jInsert3 = this.mOpenHelper.getWritableDatabase().insert(NewsTable.TABLE_NAME, null, contentValues);
                if (jInsert3 < 0) {
                    throw new SQLException("Failed to insert row");
                }
                uriCommonInsert = ContentUris.withAppendedId(NewsTable.CONTENT_URI, jInsert3);
                break;
                break;
            case 9:
                long jInsert4 = this.mOpenHelper.getWritableDatabase().insert("gallery", null, contentValues);
                if (jInsert4 < 0) {
                    throw new SQLException("Failed to insert row");
                }
                uriCommonInsert = ContentUris.withAppendedId(GalleryTable.CONTENT_URI, jInsert4);
                break;
                break;
            case 11:
                long jInsert5 = this.mOpenHelper.getWritableDatabase().insert(AlarmBackupTable.TABLE_NAME, null, contentValues);
                if (jInsert5 < 0) {
                    throw new SQLException("Failed to insert row");
                }
                uriCommonInsert = ContentUris.withAppendedId(AlarmBackupTable.CONTENT_URI, jInsert5);
                break;
                break;
            case 13:
                long jInsert6 = this.mOpenHelper.getWritableDatabase().insert(AlarmAlertTable.TABLE_NAME, null, contentValues);
                if (jInsert6 < 0) {
                    throw new SQLException("Failed to insert row");
                }
                uriCommonInsert = ContentUris.withAppendedId(AlarmAlertTable.CONTENT_URI, jInsert6);
                break;
                break;
            case 15:
                long jInsert7 = this.mOpenHelper.getWritableDatabase().insert(AlarmModifyTable.TABLE_NAME, null, contentValues);
                if (jInsert7 < 0) {
                    throw new SQLException("Failed to insert row");
                }
                uriCommonInsert = ContentUris.withAppendedId(AlarmModifyTable.CONTENT_URI, jInsert7);
                break;
                break;
            case 17:
                Log.i("insert wake alarm to SP");
                clearIdentity();
                contentValues.put("_id", (Integer) Integer.MIN_VALUE);
                BedtimeUtil.updateWakeAlarm(getContext(), contentValues);
                uriCommonInsert = ContentUris.withAppendedId(SleepAlarmTable.CONTENT_URI, -2147483648L);
                break;
            case 18:
                Log.f(TAG_VOICE_ASSIST, "insert alarm from xiaoai: " + contentValues.toString());
                WYStarRingtoneHelper.updateWYStarAlertToDefault(contentValues);
                Boolean[] boolArrIsSmartRingtone = isSmartRingtone(contentValues);
                Log.d(TAG, "insert alarm from xiaoai, isXiaoAiRingtone: " + boolArrIsSmartRingtone[0] + " not sure alarm: " + boolArrIsSmartRingtone[1]);
                contentValues.put("deleteAfterUse", Boolean.valueOf(contentValues.containsKey("deleteAfterUse") && contentValues.getAsBoolean("deleteAfterUse").booleanValue() && contentValues.containsKey("daysofweek") && contentValues.getAsInteger("daysofweek").intValue() == 0));
                contentValues.put("vibrate", Boolean.valueOf(AlarmSettingsFragment.getVibrateState()));
                Uri uriCommonInsert2 = this.mOpenHelper.commonInsert(contentValues);
                long j = Long.parseLong(uriCommonInsert2.getLastPathSegment());
                Log.f(TAG_VOICE_ASSIST, "insert id:" + j + " values:" + contentValues);
                clearIdentity();
                int i = (int) j;
                MonitorHelper.modify(5, System.currentTimeMillis(), i, contentValues);
                if (boolArrIsSmartRingtone[0].booleanValue()) {
                    XiaoAiRingtoneHelper.addXiaoAiRingtoneIds(getContext(), i);
                } else if (boolArrIsSmartRingtone[1].booleanValue()) {
                    Log.f(TAG_VOICE_ASSIST, "insert alarm from xiaoai, not sure alarm ");
                    XiaoAiRingtoneHelper.preHandleNotSureAlarm(i);
                }
                AlarmHelper.setNextAlert(getContext());
                uriCommonInsert = uriCommonInsert2;
                break;
            case 20:
                Log.f(TAG, "insert timer form:" + getCallingPackage() + " initialValues: " + contentValues);
                TimerDao.handleXiaoAiTimer(getContext(), contentValues);
                uriCommonInsert = TimerDao.insertTimer(getContext(), contentValues);
                break;
            case 21:
                long jInsert8 = this.mOpenHelper.getWritableDatabase().insert(CommonTimerTable.TABLE_NAME, null, contentValues);
                if (jInsert8 < 0) {
                    throw new SQLException("Failed to insert row");
                }
                uriCommonInsert = ContentUris.withAppendedId(CommonTimerTable.CONTENT_URI, jInsert8);
                break;
                break;
            case 24:
                Log.f(TAG_ALARM_ASSIST, "insert alarm from : " + getCallingPackage() + ", " + contentValues.toString());
                if (contentValues.containsKey("daysofweek") && contentValues.getAsInteger("daysofweek").equals(0)) {
                    try {
                        contentValues.put("alarmtime", Long.valueOf(AlarmHelper.calculateAlarmTime(getContext(), contentValues.getAsInteger("hour").intValue(), contentValues.getAsInteger("minutes").intValue(), contentValues.getAsInteger("daysofweek").intValue()).getTimeInMillis()));
                        contentValues.put("skiptime", (Integer) 0);
                    } catch (Exception e) {
                        Log.e(TAG_ALARM_ASSIST, "alarm assist insert error: " + e.getMessage());
                    }
                }
                WYStarRingtoneHelper.updateWYStarAlertToDefault(contentValues);
                contentValues.put("deleteAfterUse", Boolean.valueOf(contentValues.containsKey("deleteAfterUse") && contentValues.getAsBoolean("deleteAfterUse").booleanValue() && contentValues.containsKey("daysofweek") && contentValues.getAsInteger("daysofweek").intValue() == 0));
                uriCommonInsert = this.mOpenHelper.commonInsert(contentValues);
                clearIdentity();
                long j2 = Long.parseLong(uriCommonInsert.getLastPathSegment());
                MonitorHelper.modify(5, System.currentTimeMillis(), (int) j2, contentValues);
                Log.f(TAG_ALARM_ASSIST, "insert id:" + j2 + " values: " + contentValues);
                AlarmHelper.setNextAlert(getContext());
                break;
            case 28:
                long jInsert9 = this.mAdditionalOpenHelper.getWritableDatabase().insert(CommonTimerTableNew.TABLE_NAME, null, contentValues);
                if (jInsert9 < 0) {
                    throw new SQLException("Failed to insert row");
                }
                uriCommonInsert = ContentUris.withAppendedId(CommonTimerTableNew.CONTENT_URI, jInsert9);
                break;
                break;
            case 32:
                long jInsert10 = this.mAdditionalOpenHelper.getWritableDatabase().insert(TimerHistoryTable.TABLE_NAME, null, contentValues);
                if (jInsert10 < 0) {
                    throw new SQLException("Failed to insert row");
                }
                uriCommonInsert = ContentUris.withAppendedId(TimerHistoryTable.CONTENT_URI, jInsert10);
                break;
                break;
            default:
                throw new IllegalArgumentException("Cannot insert into URL: " + uri);
        }
        if (uriCommonInsert != null) {
            getContext().getContentResolver().notifyChange(uriCommonInsert, null);
        }
        return uriCommonInsert;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        ContentObserver contentObserver;
        int iDelete;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        String str8;
        String str9;
        String str10;
        int iDelete2;
        String str11;
        String str12;
        String str13;
        String str14;
        String str15;
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        switch (sURLMatcher.match(uri)) {
            case 1:
                contentObserver = null;
                Log.d("AlarmProvider delete Alarm");
                iDelete = writableDatabase.delete("alarms", str, strArr);
                MonitorHelper.modify(6, System.currentTimeMillis(), -1, null);
                XiaoAiRingtoneHelper.handleAlarmChange();
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 2:
                String str16 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(str)) {
                    str2 = "_id=" + str16;
                } else {
                    str2 = "_id=" + str16 + " AND (" + str + ")";
                }
                int iDelete3 = writableDatabase.delete("alarms", str2, strArr);
                contentObserver = null;
                MonitorHelper.modify(6, System.currentTimeMillis(), (int) Long.parseLong(str16), null);
                XiaoAiRingtoneHelper.handleAlarmChange();
                iDelete = iDelete3;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 3:
                iDelete = writableDatabase.delete("worldclocks", str, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 4:
                String str17 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(str)) {
                    str3 = "_id=" + str17;
                } else {
                    str3 = "_id=" + str17 + " AND (" + str + ")";
                }
                iDelete = writableDatabase.delete("worldclocks", str3, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 5:
                iDelete = writableDatabase.delete("stopwatchs", str, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 6:
                String str18 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(str)) {
                    str4 = "_id=" + str18;
                } else {
                    str4 = "_id=" + str18 + " AND (" + str + ")";
                }
                iDelete = writableDatabase.delete("stopwatchs", str4, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 7:
                iDelete = writableDatabase.delete(NewsTable.TABLE_NAME, str, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 8:
                String str19 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(str)) {
                    str5 = "_id=" + str19;
                } else {
                    str5 = "_id=" + str19 + " AND (" + str + ")";
                }
                iDelete = writableDatabase.delete(NewsTable.TABLE_NAME, str5, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 9:
                iDelete = writableDatabase.delete("gallery", str, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 10:
                String str20 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(str)) {
                    str6 = "_id=" + str20;
                } else {
                    str6 = "_id=" + str20 + " AND (" + str + ")";
                }
                iDelete = writableDatabase.delete("gallery", str6, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 11:
                iDelete = writableDatabase.delete(AlarmBackupTable.TABLE_NAME, str, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 12:
                String str21 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(str)) {
                    str7 = "_id=" + str21;
                } else {
                    str7 = "_id=" + str21 + " AND (" + str + ")";
                }
                iDelete = writableDatabase.delete(AlarmBackupTable.TABLE_NAME, str7, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 13:
                iDelete = writableDatabase.delete(AlarmAlertTable.TABLE_NAME, str, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 14:
                String str22 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(str)) {
                    str8 = "_id=" + str22;
                } else {
                    str8 = "_id=" + str22 + " AND (" + str + ")";
                }
                iDelete = writableDatabase.delete(AlarmAlertTable.TABLE_NAME, str8, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 15:
                iDelete = writableDatabase.delete(AlarmModifyTable.TABLE_NAME, str, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 16:
                String str23 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(str)) {
                    str9 = "_id=" + str23;
                } else {
                    str9 = "_id=" + str23 + " AND (" + str + ")";
                }
                iDelete = writableDatabase.delete(AlarmModifyTable.TABLE_NAME, str9, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 17:
                Log.d("AlarmProvider delete sleep Alarm");
                iDelete = BedtimeUtil.resetWakeAlarm(getContext());
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 18:
            case 23:
            case 26:
            case 27:
            case 28:
            case 30:
            case 31:
            default:
                throw new IllegalArgumentException("Cannot delete from URL: " + uri);
            case 19:
                String str24 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(str)) {
                    str10 = "_id=" + str24;
                } else {
                    str10 = "_id=" + str24 + " AND (" + str + ")";
                }
                iDelete2 = writableDatabase.delete("alarms", str10, strArr);
                Log.f(TAG_VOICE_ASSIST, "delete alarm from xiaoai, where:" + str10 + " count:" + iDelete2);
                AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).deleteAppSearchAlarm(Integer.parseInt(str24));
                clearIdentity();
                MonitorHelper.modify(6, System.currentTimeMillis(), (int) Long.parseLong(str24), null);
                if (iDelete2 == 1) {
                    Log.d(TAG, "delete alarm from xiaoai: " + Integer.valueOf(str24));
                    XiaoAiRingtoneHelper.removeXiaoAiRingtoneIds(getContext(), Integer.valueOf(str24).intValue());
                } else {
                    Log.d(TAG, "delete alarm from xiaoai");
                    XiaoAiRingtoneHelper.handleAlarmChange();
                }
                AlarmHelper.setNextAlert(getContext());
                iDelete = iDelete2;
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 20:
                Log.f(TAG, "delete timer form:" + getCallingPackage());
                iDelete = TimerDao.deleteTimer(getContext());
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 21:
                iDelete = writableDatabase.delete(CommonTimerTable.TABLE_NAME, str, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 22:
                String str25 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(str)) {
                    str11 = "_id=" + str25;
                } else {
                    str11 = "_id=" + str25 + " AND (" + str + ")";
                }
                iDelete = writableDatabase.delete(CommonTimerTable.TABLE_NAME, str11, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 24:
                Log.f(TAG_ALARM_ASSIST, "delete all alarm from : " + getCallingPackage());
                if (TextUtils.isEmpty(str)) {
                    str12 = Alarm.Columns.WHERE_NORMAL_ALARM;
                } else {
                    str12 = "type=0 AND (" + str + ")";
                }
                iDelete = writableDatabase.delete("alarms", str12, strArr);
                clearIdentity();
                MonitorHelper.modify(6, System.currentTimeMillis(), -1, null);
                XiaoAiRingtoneHelper.handleAlarmChange();
                AlarmHelper.setNextAlert(getContext());
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 25:
                Log.f(TAG_ALARM_ASSIST, "delete alarm from : " + getCallingPackage());
                String str26 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(str)) {
                    str13 = "_id=" + str26;
                } else {
                    str13 = "_id=" + str26 + " AND (" + str + ")";
                }
                iDelete2 = writableDatabase.delete("alarms", str13, strArr);
                Log.f(TAG_ALARM_ASSIST, "delete alarm, where" + str13 + " count: " + iDelete2);
                clearIdentity();
                AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).deleteAppSearchAlarm(Integer.parseInt(str26));
                MonitorHelper.modify(6, System.currentTimeMillis(), (int) Long.parseLong(str26), null);
                if (iDelete2 == 1) {
                    Log.d(TAG, "delete alarm from xiaoai: " + Integer.valueOf(str26));
                    XiaoAiRingtoneHelper.removeXiaoAiRingtoneIds(getContext(), Integer.valueOf(str26).intValue());
                } else {
                    Log.d(TAG, "delete alarm from xiaoai");
                    XiaoAiRingtoneHelper.handleAlarmChange();
                }
                AlarmHelper.setNextAlert(getContext());
                iDelete = iDelete2;
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 29:
                String str27 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(str)) {
                    str14 = "_id=" + str27;
                } else {
                    str14 = "_id=" + str27 + " AND (" + str + ")";
                }
                iDelete = this.mAdditionalOpenHelper.getWritableDatabase().delete(CommonTimerTableNew.TABLE_NAME, str14, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 32:
                iDelete = this.mAdditionalOpenHelper.getWritableDatabase().delete(TimerHistoryTable.TABLE_NAME, str, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
            case 33:
                String str28 = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(str)) {
                    str15 = "_id=" + str28;
                } else {
                    str15 = "_id=" + str28 + " AND (" + str + ")";
                }
                iDelete = this.mAdditionalOpenHelper.getWritableDatabase().delete(TimerHistoryTable.TABLE_NAME, str15, strArr);
                contentObserver = null;
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                return iDelete;
        }
    }

    @Override // android.content.ContentProvider
    public Bundle call(String str, String str2, Bundle bundle) {
        Alarm currentAlarm;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            if (METHOD_DEFAULT_ALARM_ALERT.equals(str)) {
                Bundle bundle2 = new Bundle();
                bundle2.putString(str, AlarmRingtoneUtil.getAlarmRingtoneTitle(getContext(), AlarmRingtoneUtil.getDefaultAlarmRingtone(getContext())));
                return bundle2;
            }
            if (METHOD_CHECK_NET_PERMISSION.equals(str)) {
                Bundle bundle3 = new Bundle();
                bundle3.putBoolean(str, UserNoticeUtil.isNetPermissionAgreed());
                return bundle3;
            }
            if (METHOD_ALARM_RINGING_STATUS.equals(str)) {
                Bundle bundle4 = new Bundle();
                boolean z = AlarmUtils.alarmRingForXiaoAi;
                if (z && (currentAlarm = AlarmService.getCurrentAlarm()) != null) {
                    bundle4.putInt("currentAlarmId", currentAlarm.id);
                }
                bundle4.putBoolean(str, z);
                return bundle4;
            }
            if (METHOD_CLOSE_ALARM_ALERT.equals(str)) {
                Log.f(TAG, "dismiss alarm from XiaoAi");
                Intent intent = new Intent(AlarmHelper.ACTION_ALARM_DISMISS);
                intent.putExtra(AlarmHelper.ALARM_INTENT_EXTRA, AlarmService.getCurrentAlarm());
                intent.setPackage(getContext().getPackageName());
                getContext().sendBroadcast(intent);
            } else if (METHOD_SNOOZE_ALARM_ALERT.equals(str)) {
                Log.f(TAG, "snooze alarm from XiaoAi");
                Intent intent2 = new Intent(AlarmHelper.ACTION_ALARM_SNOOZE);
                intent2.putExtra(AlarmHelper.ALARM_INTENT_EXTRA, AlarmService.getCurrentAlarm());
                intent2.setPackage(getContext().getPackageName());
                getContext().sendBroadcast(intent2);
            } else {
                if (METHOD_UPDATE_ALARM_RINGTONE.equals(str)) {
                    Log.f(TAG, "update alarm ringtone from XiaoAi");
                    Uri uriSaveMediaStore = Build.VERSION.SDK_INT >= 29 ? RingtoneUriCompat.saveMediaStore(getContext(), (Uri) bundle.getParcelable("alert")) : null;
                    Bundle bundle5 = new Bundle();
                    bundle5.putParcelable(str, uriSaveMediaStore);
                    Log.f(TAG, "update alarm ringtone from XiaoAi ringtone : " + uriSaveMediaStore);
                    return bundle5;
                }
                if (METHOD_QUERY_SNOOZE_ID.equals(str)) {
                    Bundle bundle6 = new Bundle();
                    bundle6.putString(str, FBEUtil.getSharedPreferences(getContext(), AlarmClockFragment.PREFERENCES, 0).getStringSet(AlarmHelper.PREF_SNOOZE_IDS, new HashSet()).toString());
                    return bundle6;
                }
                if (METHOD_CLOSE_SNOOZE_ALARM_BY_ID.equals(str)) {
                    if (bundle != null && bundle.containsKey("alarm_id")) {
                        int i = bundle.getInt("alarm_id");
                        Log.d(TAG, "close snooze alarm by id from XiaoAi: " + i);
                        Alarm alarmById = getAlarmById(i);
                        Intent intent3 = new Intent(AlarmHelper.ACTION_SNOOZE_CANCEL);
                        intent3.putExtra(AlarmHelper.ALARM_INTENT_EXTRA, alarmById);
                        intent3.setPackage(getContext().getPackageName());
                        getContext().sendBroadcast(intent3);
                    }
                } else if (METHOD_SNOOZE_ALERT_TIME.equals(str)) {
                    Bundle bundle7 = new Bundle();
                    if (bundle != null && bundle.containsKey(SNOOZE_ID)) {
                        bundle7.putLong(str, FBEUtil.getSharedPreferences(getContext(), AlarmClockFragment.PREFERENCES, 0).getLong(AlarmHelper.getAlarmPrefSnoozeTimeKey(bundle.getInt(SNOOZE_ID)), -1L));
                        return bundle7;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            Log.e("AlarmProvider call error: " + e.getMessage());
        }
    }

    private Alarm getAlarmById(int i) {
        Cursor cursorQuery;
        SQLiteDatabase readableDatabase = this.mOpenHelper.getReadableDatabase();
        if (i == Integer.MIN_VALUE) {
            cursorQuery = BedtimeUtil.queryWakeAlarm(DeskClockApp.getAppDEContext());
        } else {
            cursorQuery = readableDatabase.query("alarms", null, "_id=" + i, null, null, null, null);
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

    public static void clearIdentity() {
        if (Util.isVersionS()) {
            Binder.clearCallingIdentity();
        }
    }

    protected static Boolean[] isSmartRingtone(ContentValues contentValues) {
        String asString;
        if (contentValues.containsKey(KEY_IS_SMART_RINGTONE)) {
            asString = contentValues.getAsString(KEY_IS_SMART_RINGTONE);
            contentValues.remove(KEY_IS_SMART_RINGTONE);
        } else {
            asString = null;
        }
        if ("true".equals(asString)) {
            return new Boolean[]{true, false};
        }
        if ("notSure".equals(asString)) {
            return new Boolean[]{false, true};
        }
        return new Boolean[]{false, false};
    }
}
