package com.android.deskclock.addition.monitor.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.android.deskclock.Alarm;
import com.android.deskclock.AlarmDatabaseHelper;
import com.android.deskclock.util.FBEUtil;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AlarmBackupHelper {
    private static String BACKUP_ALARM_SQL = "insert into alarm_backup (_id, hour, minutes, daysofweek, alarmtime, enabled, vibrate, message, alert, type, deleteAfterUse, skiptime) select _id, hour, minutes, daysofweek, alarmtime, enabled, vibrate, message, alert, type, deleteAfterUse, skiptime from alarms";
    private static final String TAG = "DC:AlarmAlertHelper";

    /* JADX WARN: Code duplicated, block: B:17:0x003b A[DONT_GENERATE, PHI: r1
  0x003b: PHI (r1v3 android.database.Cursor) = (r1v2 android.database.Cursor), (r1v4 android.database.Cursor) binds: [B:16:0x0039, B:10:0x002c] A[DONT_GENERATE, DONT_INLINE]] */
    public static List<Alarm> getAlarmBackup(Context context) {
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = context.getContentResolver().query(AlarmBackupTable.CONTENT_URI, AlarmBackupTable.PROJECTION, null, null, null);
                if (cursorQuery != null) {
                    cursorQuery.moveToFirst();
                    while (!cursorQuery.isAfterLast()) {
                        arrayList.add(new Alarm(cursorQuery));
                        cursorQuery.moveToNext();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "getAlarmBackup error: ", e);
            }
            return arrayList;
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    public static void backupAlarm(Context context) {
        new AlarmDatabaseHelper(FBEUtil.createDeviceProtectedStorageContext(context)).getWritableDatabase().execSQL(BACKUP_ALARM_SQL);
    }

    public static void deleteAlarmBackup(Context context) {
        try {
            context.getContentResolver().delete(AlarmBackupTable.CONTENT_URI, null, null);
        } catch (Exception e) {
            Log.e(TAG, "deleteAlarmBackup error: ", e);
        }
    }

    /* JADX WARN: Code duplicated, block: B:17:0x003b A[DONT_GENERATE, PHI: r1
  0x003b: PHI (r1v3 android.database.Cursor) = (r1v2 android.database.Cursor), (r1v4 android.database.Cursor) binds: [B:16:0x0039, B:10:0x002c] A[DONT_GENERATE, DONT_INLINE]] */
    public static List<Alarm> getCurrAlarm(Context context) {
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = context.getContentResolver().query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS, null, null, null);
                if (cursorQuery != null) {
                    cursorQuery.moveToFirst();
                    while (!cursorQuery.isAfterLast()) {
                        arrayList.add(new Alarm(cursorQuery));
                        cursorQuery.moveToNext();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "getAlarmBackup error: ", e);
            }
            return arrayList;
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }
}
