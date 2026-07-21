package com.android.deskclock.addition.monitor.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AlarmModifyHelper {
    private static final String TAG = "DC:AlarmModifyHelper";

    /* JADX WARN: Code duplicated, block: B:17:0x003a A[DONT_GENERATE, PHI: r1
  0x003a: PHI (r1v3 android.database.Cursor) = (r1v2 android.database.Cursor), (r1v4 android.database.Cursor) binds: [B:16:0x0038, B:10:0x002b] A[DONT_GENERATE, DONT_INLINE]] */
    public static List<AlarmModify> getAlarmModify(Context context) {
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = context.getContentResolver().query(AlarmModifyTable.CONTENT_URI, AlarmModifyTable.PROJECTION, null, null, null);
                if (cursorQuery != null) {
                    cursorQuery.moveToFirst();
                    while (!cursorQuery.isAfterLast()) {
                        arrayList.add(createFromCursor(cursorQuery));
                        cursorQuery.moveToNext();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "getAlarmModify error: ", e);
            }
            return arrayList;
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    public static int insertAlarmModify(Context context, AlarmModify alarmModify) {
        if (alarmModify == null) {
            return -1;
        }
        try {
            if (alarmModify.getId() != -1) {
                alarmModify.setId(-1);
            }
            Uri uriInsert = context.getContentResolver().insert(AlarmModifyTable.CONTENT_URI, transferToContentValues(alarmModify));
            if (uriInsert != null) {
                return (int) ContentUris.parseId(uriInsert);
            }
            return -1;
        } catch (Exception e) {
            Log.e(TAG, "insertAlarmModify error: ", e);
            return -1;
        }
    }

    public static void deleteAlarmModify(Context context) {
        try {
            context.getContentResolver().delete(AlarmModifyTable.CONTENT_URI, null, null);
        } catch (Exception e) {
            Log.e(TAG, "deleteAlarmModify error: ", e);
        }
    }

    private static AlarmModify createFromCursor(Cursor cursor) {
        AlarmModify alarmModify = new AlarmModify();
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex("_id");
            if (columnIndex != -1) {
                alarmModify.setId(cursor.getInt(columnIndex));
            }
            int columnIndex2 = cursor.getColumnIndex("alarm_id");
            if (columnIndex2 != -1) {
                alarmModify.setAlarmId(cursor.getInt(columnIndex2));
            }
            int columnIndex3 = cursor.getColumnIndex("type");
            if (columnIndex3 != -1) {
                alarmModify.setType(cursor.getInt(columnIndex3));
            }
            int columnIndex4 = cursor.getColumnIndex("time");
            if (columnIndex4 != -1) {
                alarmModify.setTime(cursor.getLong(columnIndex4));
            }
            int columnIndex5 = cursor.getColumnIndex("content");
            if (columnIndex5 != -1) {
                alarmModify.setContent(cursor.getString(columnIndex5));
            }
        }
        return alarmModify;
    }

    private static ContentValues transferToContentValues(AlarmModify alarmModify) {
        ContentValues contentValues = new ContentValues();
        if (alarmModify != null) {
            if (alarmModify.getId() != -1) {
                contentValues.put("_id", Integer.valueOf(alarmModify.getId()));
            }
            contentValues.put("alarm_id", Integer.valueOf(alarmModify.getAlarmId()));
            contentValues.put("type", Integer.valueOf(alarmModify.getType()));
            contentValues.put("time", Long.valueOf(alarmModify.getTime()));
            contentValues.put("content", alarmModify.getContent());
        }
        return contentValues;
    }
}
