package com.android.deskclock;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.SparseArray;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.monitor.data.AlarmAlertTable;
import com.android.deskclock.addition.monitor.data.AlarmBackupTable;
import com.android.deskclock.addition.monitor.data.AlarmModifyTable;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.alarm.bedtime.SleepAlarmTable;
import com.android.deskclock.alarm.lifepost.model.GalleryTable;
import com.android.deskclock.alarm.lifepost.model.NewsTable;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmTable;
import com.android.deskclock.appaf.AppSearchUtil;
import com.android.deskclock.timer.CommonTimerTable;
import com.android.deskclock.util.CityZoneHelper;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class AlarmDatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_ALARMS = "CREATE TABLE alarms (_id INTEGER PRIMARY KEY,hour INTEGER, minutes INTEGER, daysofweek INTEGER, alarmtime INTEGER, enabled INTEGER, vibrate INTEGER, message TEXT, alert TEXT, type INTEGER DEFAULT 0, deleteAfterUse INTEGER DEFAULT 0, skiptime INTEGER DEFAULT 0, external_id INTEGER DEFAULT null);";
    private static final String CREATE_TABLE_STOPWATCHS = "CREATE TABLE stopwatchs (_id INTEGER PRIMARY KEY,total_elapsed LONG,lap_elapsed LONG);";
    private static final String CREATE_TABLE_TIMERS = "CREATE TABLE timers (_id INTEGER PRIMARY KEY,duration LONG,label TEXT);";
    private static final String CREATE_TABLE_WORLDCLOCKS = "CREATE TABLE worldclocks (_id INTEGER PRIMARY KEY,cityid_new TEXT);";
    private static final String DATABASE_NAME = "alarms.db";
    private static final int DATABASE_VERSION = 27;
    private static final String DEFAULT_ALARM_1 = "(6, 00, 127, 0, 0, 1, '', '', 0, null);";
    private static final String DEFAULT_ALARM_2 = "(7, 00, 31, 0, 0, 1, '', '', 0, null);";
    private static final String DEFAULT_ALARM_3 = "(8, 00, 96, 0, 0, 0, '', '', 0, null);";
    private static final String INSERT_ALARM_SQL = "INSERT INTO alarms (hour, minutes, daysofweek, alarmtime, enabled, vibrate,  message, alert) VALUES ";
    private static final String INSERT_TIMER_SQL = "INSERT INTO timers (duration, label) VALUES ";
    private static final String INSERT_WORLDCLOCK_SQL = "INSERT INTO worldclocks (cityid_new) VALUES ";
    private static final String TAG = "DC:AlarmDatabaseHelper";
    private Context mContext;
    private int mNewVersion;

    public AlarmDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 27);
        Log.e("DATABASE_NAME: alarms.dbDATABASE_VERSION: 27");
        this.mContext = context;
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        Log.f(TAG, "AlarmDatabaseHelper onCreate");
        sQLiteDatabase.execSQL(CREATE_TABLE_WORLDCLOCKS);
        sQLiteDatabase.execSQL(CREATE_TABLE_STOPWATCHS);
        sQLiteDatabase.execSQL(CREATE_TABLE_ALARMS);
        insertDefaultWorldClock(sQLiteDatabase);
        insertDefaultAlarms(sQLiteDatabase);
        insertDefaultAlarmsToAppSearch();
        if (this.mNewVersion <= 14) {
            sQLiteDatabase.execSQL(CREATE_TABLE_TIMERS);
            insertDefaultTimers(sQLiteDatabase);
        }
        sQLiteDatabase.execSQL(NewsTable.TABLE_CREATE_SQL);
        sQLiteDatabase.execSQL(GalleryTable.TABLE_CREATE_SQL);
        sQLiteDatabase.execSQL(AlarmBackupTable.TABLE_CREATE_SQL);
        sQLiteDatabase.execSQL(AlarmAlertTable.TABLE_CREATE_SQL);
        sQLiteDatabase.execSQL(AlarmModifyTable.TABLE_CREATE_SQL);
        sQLiteDatabase.execSQL(SleepAlarmTable.TABLE_CREATE_SQL);
        sQLiteDatabase.execSQL(CommonTimerTable.TABLE_CREATE_SQL);
        sQLiteDatabase.execSQL(ShiftAlarmTable.TABLE_CREATE_SQL);
        insertDefaultCommonTimers(sQLiteDatabase);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        int i3;
        Log.f(TAG, "Upgrading alarms database from version " + i + " to " + i2);
        try {
            if (i < 5) {
                sQLiteDatabase.execSQL("DROP TABLE IF EXISTS alarms");
                onCreate(sQLiteDatabase);
                return;
            }
            if (i == 5) {
                sQLiteDatabase.execSQL("INSERT INTO alarms (hour, minutes, daysofweek, alarmtime, enabled, vibrate,  message, alert) VALUES (7, 00, 31, 0, 0, 1, '" + this.mContext.getString(R.string.workday_alarm_clock) + "', '');");
                sQLiteDatabase.execSQL("INSERT INTO alarms (hour, minutes, daysofweek, alarmtime, enabled, vibrate,  message, alert) VALUES (8, 00, 31, 0, 0, 1, '" + this.mContext.getString(R.string.workday_alarm_clock) + "', '');");
                i3 = i + 1;
            } else {
                i3 = i;
            }
            if (i3 == 6) {
                i3++;
            }
            if (i3 == 7) {
                sQLiteDatabase.execSQL("ALTER TABLE alarms ADD COLUMN type INTEGER DEFAULT 0;");
                sQLiteDatabase.execSQL(CREATE_TABLE_WORLDCLOCKS);
                sQLiteDatabase.execSQL(CREATE_TABLE_STOPWATCHS);
                i3++;
            }
            if (i3 == 8) {
                updateToVersion9(sQLiteDatabase);
                i3++;
            }
            if (i3 == 9) {
                sQLiteDatabase.execSQL("ALTER TABLE alarms ADD COLUMN deleteAfterUse INTEGER DEFAULT 0;");
                i3++;
            }
            if (i3 == 10) {
                sQLiteDatabase.execSQL(CREATE_TABLE_TIMERS);
                insertDefaultTimers(sQLiteDatabase);
                i3++;
            }
            if (i3 == 11) {
                updateWorldClockToVersion12(sQLiteDatabase);
                i3++;
            }
            if (i3 <= 12) {
                sQLiteDatabase.execSQL("ALTER TABLE alarms ADD COLUMN skiptime INTEGER DEFAULT 0;");
                i3++;
            }
            if (i3 <= 13) {
                Cursor cursorRawQuery = null;
                try {
                    cursorRawQuery = sQLiteDatabase.rawQuery("PRAGMA table_info(alarms);", null);
                    boolean zEquals = false;
                    while (cursorRawQuery.moveToNext()) {
                        zEquals = "skiptime".equals(cursorRawQuery.getString(1));
                        if (zEquals) {
                            Log.d("skiptime found");
                            break;
                        }
                    }
                    if (cursorRawQuery != null) {
                        cursorRawQuery.close();
                    }
                    if (!zEquals) {
                        Log.d("skiptime not found");
                        sQLiteDatabase.execSQL("ALTER TABLE alarms ADD COLUMN skiptime INTEGER DEFAULT 0;");
                    }
                    i3++;
                } catch (Throwable th) {
                    if (cursorRawQuery != null) {
                        cursorRawQuery.close();
                    }
                    throw th;
                }
            }
            if (i3 <= 14) {
                Log.d("AlarmDatabaseHelper oldVersion < 14 and add the oldVersion = " + i3);
                sQLiteDatabase.execSQL("DROP TABLE IF EXISTS timers");
                i3++;
            }
            if (i3 == 15) {
                i3++;
            }
            if (i3 == 16) {
                i3++;
            }
            if (i3 == 17) {
                sQLiteDatabase.execSQL(GalleryTable.TABLE_CREATE_SQL);
                i3++;
            }
            if (i3 == 18) {
                sQLiteDatabase.execSQL(AlarmBackupTable.TABLE_CREATE_SQL);
                sQLiteDatabase.execSQL(AlarmAlertTable.TABLE_CREATE_SQL);
                sQLiteDatabase.execSQL(AlarmModifyTable.TABLE_CREATE_SQL);
                i3++;
            }
            if (i3 == 19) {
                sQLiteDatabase.execSQL("DROP TABLE IF EXISTS alarmplayprobabilitys");
                sQLiteDatabase.execSQL("DROP TABLE IF EXISTS advertisement");
                i3++;
            }
            if (i3 == 20) {
                sQLiteDatabase.execSQL(SleepAlarmTable.TABLE_CREATE_SQL);
                i3++;
            }
            if (i3 == 21) {
                sQLiteDatabase.execSQL("DROP TABLE IF EXISTS listennews");
                sQLiteDatabase.execSQL("DROP TABLE IF EXISTS listennewsdatas");
                sQLiteDatabase.execSQL("DROP TABLE IF EXISTS listennewscandidates");
                sQLiteDatabase.execSQL(NewsTable.TABLE_CREATE_SQL);
                if (i >= 19) {
                    sQLiteDatabase.execSQL("ALTER TABLE alarm_alert ADD COLUMN screen_locked INTEGER DEFAULT 1;");
                }
                i3++;
            }
            if (i3 == 22) {
                sQLiteDatabase.execSQL(CommonTimerTable.TABLE_CREATE_SQL);
                insertDefaultCommonTimers(sQLiteDatabase);
                i3++;
            }
            if (i3 == 23) {
                Log.d("Upgrade alarm database to version 24");
                i3++;
            }
            if (i3 == 24) {
                Log.d("Upgrade alarm database to version 25");
                try {
                    sQLiteDatabase.execSQL("ALTER TABLE alarms ADD COLUMN external_id INTEGER DEFAULT null;");
                } catch (SQLiteException e) {
                    Log.e("ALTER TABLE alarms ADD COLUMN external_id error: " + e);
                }
                i3++;
            }
            if (i3 == 25) {
                Log.d("Upgrade alarm database to version 26");
                try {
                    sQLiteDatabase.execSQL("ALTER TABLE alarms ADD COLUMN external_id INTEGER DEFAULT null;");
                } catch (Exception unused) {
                    Log.e("ALTER TABLE alarms ADD COLUMN external_id error");
                }
                i3++;
            }
            if (i3 == 26) {
                Log.d("Upgrade alarm database to version 27");
                sQLiteDatabase.execSQL(ShiftAlarmTable.TABLE_CREATE_SQL);
                i3++;
            }
            if (i3 != i2) {
                Log.e("Upgrade alarm database to version " + i2 + " fails");
            }
        } catch (Exception e2) {
            Log.e("Upgrade exception is " + e2);
            resetDatabase(sQLiteDatabase);
        }
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        Log.f(TAG, "AlarmDatabaseHelper onDowngrade: " + i + " to " + i2);
        this.mNewVersion = i2;
        resetDatabase(sQLiteDatabase);
    }

    private void resetDatabase(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS alarms");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS worldclocks");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS stopwatchs");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS timers");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS listennews");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS listennewsdatas");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS listennewscandidates");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS alarmplayprobabilitys");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS advertisement");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS news");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS gallery");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS alarm_backup");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS alarm_alert");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS alarm_modify");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS sleep_alarms");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS common_timers");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS shift_alarms");
        onCreate(sQLiteDatabase);
    }

    private void updateToVersion9(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS worldclocks;");
        sQLiteDatabase.execSQL(CREATE_TABLE_WORLDCLOCKS);
        insertDefaultWorldClock(sQLiteDatabase);
    }

    private void updateWorldClockToVersion12(SQLiteDatabase sQLiteDatabase) {
        SparseArray<String> oldIdConverter = CityZoneHelper.getOldIdConverter(this.mContext);
        sQLiteDatabase.execSQL("ALTER TABLE worldclocks ADD COLUMN cityid_new TEXT;");
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        sQLiteQueryBuilder.setTables("worldclocks");
        Cursor cursorQuery = sQLiteQueryBuilder.query(sQLiteDatabase, new String[]{"cityid"}, null, null, null, null, null);
        if (cursorQuery != null) {
            ArrayList arrayList = new ArrayList();
            while (cursorQuery.moveToNext()) {
                try {
                    try {
                        int i = cursorQuery.getInt(0);
                        String str = oldIdConverter.get(i, "");
                        Log.i("old cityid " + i + " new city id " + str);
                        if (!TextUtils.isEmpty(str) && !arrayList.contains(str)) {
                            arrayList.add(str);
                        }
                    } catch (Exception e) {
                        Log.e("city id convert fail", e);
                    }
                } catch (Throwable th) {
                    cursorQuery.close();
                    throw th;
                }
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                sQLiteDatabase.execSQL("INSERT INTO worldclocks (cityid_new) VALUES ('" + ((String) it.next()) + "');");
            }
            cursorQuery.close();
        }
    }

    private void insertDefaultWorldClock(SQLiteDatabase sQLiteDatabase) {
        if (Util.isInternational()) {
            return;
        }
        sQLiteDatabase.execSQL("INSERT INTO worldclocks (cityid_new) VALUES ('C177');");
        if (MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode()) {
            return;
        }
        sQLiteDatabase.execSQL("INSERT INTO worldclocks (cityid_new) VALUES ('C78');");
        sQLiteDatabase.execSQL("INSERT INTO worldclocks (cityid_new) VALUES ('C186');");
    }

    private void insertDefaultAlarms(SQLiteDatabase sQLiteDatabase) {
        Log.i("Inserting default alarms");
        sQLiteDatabase.execSQL("INSERT INTO alarms  (hour, minutes, daysofweek, alarmtime, enabled, vibrate, message, alert, deleteAfterUse, external_id) VALUES (6, 00, 127, 0, 0, 1, '', '', 0, null);");
        if (MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode()) {
            return;
        }
        sQLiteDatabase.execSQL("INSERT INTO alarms  (hour, minutes, daysofweek, alarmtime, enabled, vibrate, message, alert, deleteAfterUse, external_id) VALUES (7, 00, 31, 0, 0, 1, '', '', 0, null);");
        sQLiteDatabase.execSQL("INSERT INTO alarms  (hour, minutes, daysofweek, alarmtime, enabled, vibrate, message, alert, deleteAfterUse, external_id) VALUES (8, 00, 96, 0, 0, 0, '', '', 0, null);");
    }

    private void insertDefaultAlarmsToAppSearch() {
        AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).insertDefaultAlarmsToAppSearch();
    }

    private void insertDefaultTimers(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("INSERT INTO timers (duration, label) VALUES (300000, '');");
        sQLiteDatabase.execSQL("INSERT INTO timers (duration, label) VALUES (180000, '');");
        sQLiteDatabase.execSQL("INSERT INTO timers (duration, label) VALUES (60000,  '');");
    }

    Uri commonInsert(ContentValues contentValues) throws Throwable {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        long jInsert = writableDatabase.insert("alarms", "message", contentValues);
        if (jInsert < 0) {
            throw new SQLException("Failed to insert row and the db version is 27");
        }
        Log.v("Added alarm rowId = " + jInsert);
        Alarm alarmQueryAlarmById = queryAlarmById(writableDatabase, (int) jInsert);
        if (alarmQueryAlarmById != null) {
            Log.d("commonInsert query alarm is " + alarmQueryAlarmById.toString());
            AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).addAppSearchAlarmScheduled(alarmQueryAlarmById, true);
        } else {
            Log.d("commonInsert query alarm is null!");
        }
        return ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, jInsert);
    }

    private void insertDefaultCommonTimers(SQLiteDatabase sQLiteDatabase) {
        Log.i("Inserting default common timers");
        sQLiteDatabase.execSQL("INSERT INTO common_timers (minutes) VALUES (5)");
        sQLiteDatabase.execSQL("INSERT INTO common_timers (minutes) VALUES (10)");
        sQLiteDatabase.execSQL("INSERT INTO common_timers (minutes) VALUES (15)");
    }

    /* JADX WARN: Code duplicated, block: B:28:0x006e  */
    private Alarm queryAlarmById(SQLiteDatabase sQLiteDatabase, int i) throws Throwable {
        Cursor cursor;
        Cursor cursorQuery;
        Cursor cursor2 = null;
        alarm = null;
        alarm = null;
        alarm = null;
        Alarm alarm = null;
        try {
            if (i == Integer.MIN_VALUE) {
                cursorQuery = BedtimeUtil.queryWakeAlarm(DeskClockApp.getAppDEContext());
            } else {
                SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
                sQLiteQueryBuilder.setTables("alarms");
                sQLiteQueryBuilder.appendWhere("_id=");
                sQLiteQueryBuilder.appendWhere(String.valueOf(i));
                cursorQuery = sQLiteQueryBuilder.query(sQLiteDatabase, Alarm.Columns.ALARM_QUERY_COLUMNS, null, null, null, null, null);
            }
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst()) {
                        alarm = new Alarm(cursorQuery);
                    }
                } catch (Exception e) {
                    cursor = cursorQuery;
                    e = e;
                    try {
                        Log.e("queryAlarmById Error: " + e);
                        if (cursor != null) {
                            cursor.close();
                        }
                    } catch (Throwable th) {
                        th = th;
                        cursor2 = cursor;
                        if (cursor2 != null) {
                            cursor2.close();
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    cursor2 = cursorQuery;
                    th = th2;
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                    throw th;
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        } catch (Exception e2) {
            e = e2;
            cursor = null;
        } catch (Throwable th3) {
            th = th3;
        }
        return alarm;
    }
}
