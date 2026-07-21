package com.android.deskclock.alarm.shiftalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.android.deskclock.AlarmClockExtras;
import com.android.deskclock.AlarmDatabaseHelper;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.FBEUtil;
import com.xiaomi.onetrack.util.z;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.animation.internal.FolmeCore;

/* JADX INFO: loaded from: classes.dex */
public class ShiftAlarmDbHelper {
    private static final String TAG = "DC:ShiftAlarmDbHelper";
    private static ShiftAlarmDbHelper sInstance;
    AlarmDatabaseHelper dbHelper = new AlarmDatabaseHelper(FBEUtil.createDeviceProtectedStorageContext(DeskClockApp.getAppContext()));

    private ShiftAlarmDbHelper() {
    }

    public static synchronized ShiftAlarmDbHelper getInstance() {
        if (sInstance == null) {
            sInstance = new ShiftAlarmDbHelper();
        }
        return sInstance;
    }

    public static List<ShiftAlarmGroup> getAllShiftGroups(Context context) {
        Cursor cursorQuery = context.getContentResolver().query(ShiftAlarm.Columns.CONTENT_URI, null, "type = ? ", new String[]{String.valueOf(1)}, null);
        if (cursorQuery != null) {
            return ShiftAlarmGroup.fromCursor(cursorQuery);
        }
        return new ArrayList();
    }

    public void insertShiftAlarmGroup(ShiftAlarmGroup shiftAlarmGroup) {
        long jNanoTime = System.nanoTime();
        SQLiteDatabase writableDatabase = this.dbHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            try {
                ContentValues contentValuesBuildShiftGroupValues = buildShiftGroupValues(shiftAlarmGroup);
                shiftAlarmGroup.groupId = generateId(jNanoTime);
                contentValuesBuildShiftGroupValues.put(ShiftAlarm.Columns.GROUP_ID, Long.valueOf(shiftAlarmGroup.groupId));
                long jInsert = writableDatabase.insert(ShiftAlarmTable.TABLE_NAME, "message", contentValuesBuildShiftGroupValues);
                if (jInsert < 0) {
                    Log.e(TAG, "shift alarms, Failed to insert row");
                    return;
                }
                Log.d(TAG, "insert shift group row:" + jInsert);
                shiftAlarmGroup.id = (int) jInsert;
                Iterator<ShiftAlarm> it = shiftAlarmGroup.shiftAlarms.iterator();
                while (it.hasNext()) {
                    insertNewShiftAlarm(it.next(), writableDatabase, shiftAlarmGroup.groupId);
                }
                shiftAlarmGroup.resetAlarmIds();
                writableDatabase.setTransactionSuccessful();
            } catch (Exception unused) {
                Log.e(TAG, "shift alarms, Failed to insert row");
            }
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void deleteShiftAlarmGroup(ShiftAlarmGroup shiftAlarmGroup) {
        SQLiteDatabase writableDatabase = this.dbHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            try {
                writableDatabase.delete(ShiftAlarmTable.TABLE_NAME, "_id = ?", new String[]{String.valueOf(shiftAlarmGroup.id)});
                writableDatabase.delete(ShiftAlarmTable.TABLE_NAME, "groupid = ?", new String[]{String.valueOf(shiftAlarmGroup.groupId)});
                StringBuilder sb = new StringBuilder();
                sb.append("_id in (");
                Iterator<Integer> it = shiftAlarmGroup.alarmIds.iterator();
                while (it.hasNext()) {
                    sb.append(it.next()).append(z.b);
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append(")");
                writableDatabase.delete("alarms", sb.toString(), null);
                writableDatabase.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e(TAG, "Failed to delete shift alarm group", e);
            }
        } finally {
            writableDatabase.endTransaction();
        }
    }

    private static void insertNewShiftAlarm(ShiftAlarm shiftAlarm, SQLiteDatabase sQLiteDatabase, long j) {
        shiftAlarm.groupId = j;
        long jInsert = sQLiteDatabase.insert("alarms", "message", buildAlarmValues(shiftAlarm.hour, shiftAlarm.minutes));
        shiftAlarm.alarmId = (int) jInsert;
        Log.d(TAG, "insertNewAlarm rowId: " + jInsert);
        long jInsert2 = sQLiteDatabase.insert(ShiftAlarmTable.TABLE_NAME, "message", buildShiftAlarmValues(shiftAlarm));
        Log.d(TAG, "insertNewShiftAlarm rowId: " + jInsert2);
        shiftAlarm.id = (int) jInsert2;
    }

    public void updateShiftAlarmGroup(ShiftAlarmGroup shiftAlarmGroup) {
        SQLiteDatabase writableDatabase = this.dbHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        Log.i(TAG, "updateShiftAlarmGroup");
        try {
            try {
                writableDatabase.update(ShiftAlarmTable.TABLE_NAME, buildShiftGroupValues(shiftAlarmGroup), "_id = ?", new String[]{String.valueOf(shiftAlarmGroup.id)});
                StringBuilder sb = null;
                for (ShiftAlarm shiftAlarm : shiftAlarmGroup.shiftAlarms) {
                    if (shiftAlarm.id >= 0) {
                        writableDatabase.update("alarms", buildAlarmValues(shiftAlarm.hour, shiftAlarm.minutes), "_id = ?", new String[]{String.valueOf(shiftAlarm.alarmId)});
                        writableDatabase.update(ShiftAlarmTable.TABLE_NAME, buildShiftAlarmValues(shiftAlarm), "_id = ?", new String[]{String.valueOf(shiftAlarm.id)});
                    } else {
                        insertNewShiftAlarm(shiftAlarm, writableDatabase, shiftAlarmGroup.groupId);
                    }
                    if (sb == null) {
                        sb = new StringBuilder();
                        sb.append(shiftAlarm.id);
                    } else {
                        sb.append(z.b).append(shiftAlarm.id);
                    }
                }
                String str = "_id NOT IN (" + sb.toString() + ") AND " + ShiftAlarm.Columns.GROUP_ID + "= ? AND type = ?";
                Cursor cursorQuery = writableDatabase.query(ShiftAlarmTable.TABLE_NAME, new String[]{ShiftAlarm.Columns.ALARM_ID}, str, new String[]{String.valueOf(shiftAlarmGroup.groupId), String.valueOf(0)}, null, null, null);
                ArrayList arrayList = new ArrayList();
                try {
                    if (cursorQuery.moveToFirst()) {
                        do {
                            arrayList.add(Integer.valueOf(cursorQuery.getInt(0)));
                        } while (cursorQuery.moveToNext());
                    }
                    cursorQuery.close();
                    arrayList.toArray();
                    if (!arrayList.isEmpty()) {
                        Iterator it = arrayList.iterator();
                        while (it.hasNext()) {
                            deleteAlarmInDb(DeskClockApp.getAppContext(), writableDatabase, ((Integer) it.next()).intValue());
                        }
                    }
                    writableDatabase.delete(ShiftAlarmTable.TABLE_NAME, str, new String[]{String.valueOf(shiftAlarmGroup.groupId), String.valueOf(0)});
                    shiftAlarmGroup.resetAlarmIds();
                    writableDatabase.setTransactionSuccessful();
                    writableDatabase.endTransaction();
                } catch (Throwable th) {
                    cursorQuery.close();
                    throw th;
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to update shift alarm group", e);
            }
        } catch (Throwable th2) {
            writableDatabase.endTransaction();
            throw th2;
        }
    }

    public static void deleteAlarmInDb(Context context, SQLiteDatabase sQLiteDatabase, int i) {
        if (i != -1) {
            AlarmHelper.disableSnoozeAlert(context, i);
            sQLiteDatabase.delete("alarms", "_id = ? AND type = ?", new String[]{String.valueOf(i), String.valueOf(2)});
        }
    }

    protected static void handleRestoreShiftAlarms(SQLiteDatabase sQLiteDatabase) {
        try {
            try {
                sQLiteDatabase.beginTransaction();
                Cursor cursorQuery = sQLiteDatabase.query(ShiftAlarmTable.TABLE_NAME, new String[]{"_id", "hour", "minutes"}, "type=?", new String[]{String.valueOf(0)}, null, null, null);
                if (cursorQuery != null) {
                    try {
                        if (cursorQuery.moveToFirst()) {
                            do {
                                int i = cursorQuery.getInt(0);
                                int iInsert = (int) sQLiteDatabase.insert("alarms", "message", buildAlarmValues(cursorQuery.getInt(1), cursorQuery.getInt(2)));
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(ShiftAlarm.Columns.ALARM_ID, Integer.valueOf(iInsert));
                                sQLiteDatabase.update(ShiftAlarmTable.TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(i)});
                            } while (cursorQuery.moveToNext());
                        }
                    } catch (Throwable th) {
                        try {
                            cursorQuery.close();
                        } catch (Exception unused) {
                        }
                        throw th;
                    }
                }
                try {
                    cursorQuery.close();
                } catch (Exception unused2) {
                }
                sQLiteDatabase.setTransactionSuccessful();
            } catch (Exception e) {
                com.android.deskclock.util.Log.f(TAG, "checkRestore error:" + e.getMessage());
            }
            sQLiteDatabase.endTransaction();
        } catch (Throwable th2) {
            sQLiteDatabase.endTransaction();
            throw th2;
        }
    }

    private static ContentValues buildAlarmValues(int i, int i2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", (Integer) 2);
        contentValues.put("minutes", Integer.valueOf(i2));
        contentValues.put("hour", Integer.valueOf(i));
        return contentValues;
    }

    private static ContentValues buildShiftGroupValues(ShiftAlarmGroup shiftAlarmGroup) {
        ContentValues contentValues = new ContentValues();
        boolean z = true;
        contentValues.put("type", (Integer) 1);
        contentValues.put(ShiftAlarm.Columns.START_TIME, Long.valueOf(shiftAlarmGroup.startTime));
        if (shiftAlarmGroup.alert != null && !AlarmClockExtras.NO_RINGTONE.equals(shiftAlarmGroup.alert.toString())) {
            z = false;
        }
        contentValues.put("alert", z ? "silent" : shiftAlarmGroup.alert.toString());
        contentValues.put("duration", Integer.valueOf(shiftAlarmGroup.duration));
        contentValues.put(ShiftAlarm.Columns.ENABLE, Boolean.valueOf(shiftAlarmGroup.enable));
        contentValues.put("vibrate", Boolean.valueOf(shiftAlarmGroup.vibrate));
        contentValues.put("message", shiftAlarmGroup.label);
        contentValues.put("skiptime", Long.valueOf(shiftAlarmGroup.skipTime));
        contentValues.put(ShiftAlarm.Columns.SKIP_INDEX, Integer.valueOf(shiftAlarmGroup.skipIndex));
        contentValues.put(ShiftAlarm.Columns.ALARM_COUNT, Integer.valueOf(shiftAlarmGroup.alarmCount));
        return contentValues;
    }

    private static ContentValues buildShiftAlarmValues(ShiftAlarm shiftAlarm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", (Integer) 0);
        contentValues.put(ShiftAlarm.Columns.SHIFT_INDEX, Integer.valueOf(shiftAlarm.index));
        contentValues.put(ShiftAlarm.Columns.ENABLE, Boolean.valueOf(shiftAlarm.enable));
        contentValues.put(ShiftAlarm.Columns.GROUP_ID, Long.valueOf(shiftAlarm.groupId));
        contentValues.put("skiptime", Long.valueOf(shiftAlarm.skipTime));
        contentValues.put(ShiftAlarm.Columns.ALARM_ID, Integer.valueOf(shiftAlarm.alarmId));
        contentValues.put("hour", Integer.valueOf(shiftAlarm.hour));
        contentValues.put("minutes", Integer.valueOf(shiftAlarm.minutes));
        return contentValues;
    }

    private long generateId(long j) {
        return (System.currentTimeMillis() * FolmeCore.NANOS_TO_MS) + ((System.nanoTime() - j) % FolmeCore.NANOS_TO_MS);
    }
}
