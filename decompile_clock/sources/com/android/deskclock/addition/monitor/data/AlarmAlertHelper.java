package com.android.deskclock.addition.monitor.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.android.deskclock.util.PrefUtil;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AlarmAlertHelper {
    private static final String TAG = "DC:AlarmAlertHelper";

    public static List<AlarmAlert> getAlarmAlert(Context context) {
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = context.getContentResolver().query(AlarmAlertTable.CONTENT_URI, AlarmAlertTable.PROJECTION, null, null, null);
                if (cursorQuery != null) {
                    cursorQuery.moveToFirst();
                    while (!cursorQuery.isAfterLast()) {
                        arrayList.add(createFromCursor(cursorQuery));
                        cursorQuery.moveToNext();
                    }
                }
            } catch (Exception e) {
                PrefUtil.setMonitorStatus(2);
                Log.e(TAG, "getAlarmAlert error: ", e);
            }
            return arrayList;
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    public static boolean alertExistAtTime(Context context, long j) {
        Cursor cursorQuery = null;
        boolean z = false;
        try {
            try {
                cursorQuery = context.getContentResolver().query(AlarmAlertTable.CONTENT_URI, null, "alarm_time=\"" + j + "\"", null, null);
                if (cursorQuery != null && cursorQuery.getCount() > 0) {
                    z = true;
                }
            } catch (Exception e) {
                PrefUtil.setMonitorStatus(2);
                Log.e(TAG, "hasAlertRecordAtTime error: ", e);
            }
            return z;
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    public static int insertOrUpdateAlarmAlert(Context context, AlarmAlert alarmAlert) {
        int iInsertAlarmAlert;
        if (alarmAlert == null) {
            return -1;
        }
        try {
            long alarmTime = alarmAlert.getAlarmTime();
            if (alertExistAtTime(context, alarmTime)) {
                long playTime = alarmAlert.getPlayTime();
                boolean zIsScreenLocked = alarmAlert.isScreenLocked();
                ContentValues contentValues = new ContentValues();
                contentValues.put(AlarmAlertTable.Columns.PLAY_TIME, Long.valueOf(playTime));
                contentValues.put(AlarmAlertTable.Columns.SCREEN_LOCKED, Boolean.valueOf(zIsScreenLocked));
                iInsertAlarmAlert = updateAlarmAlert(context, contentValues, alarmTime);
            } else {
                iInsertAlarmAlert = insertAlarmAlert(context, alarmAlert);
            }
            return iInsertAlarmAlert;
        } catch (Exception e) {
            PrefUtil.setMonitorStatus(2);
            Log.e(TAG, "insertOrUpdateAlarmAlert error: ", e);
            return -1;
        }
    }

    public static int insertOrUpdateAlarmShow(Context context, AlarmAlert alarmAlert) {
        int iInsertAlarmAlert;
        if (alarmAlert == null) {
            return -1;
        }
        try {
            long alarmTime = alarmAlert.getAlarmTime();
            if (alertExistAtTime(context, alarmTime)) {
                long showTime = alarmAlert.getShowTime();
                ContentValues contentValues = new ContentValues();
                contentValues.put(AlarmAlertTable.Columns.SHOW_TIME, Long.valueOf(showTime));
                iInsertAlarmAlert = updateAlarmAlert(context, contentValues, alarmTime);
            } else {
                iInsertAlarmAlert = insertAlarmAlert(context, alarmAlert);
            }
            return iInsertAlarmAlert;
        } catch (Exception e) {
            PrefUtil.setMonitorStatus(2);
            Log.e(TAG, "insertOrUpdateAlarmShow error: ", e);
            return -1;
        }
    }

    private static int insertAlarmAlert(Context context, AlarmAlert alarmAlert) {
        if (alarmAlert == null) {
            return -1;
        }
        try {
            alarmAlert.setId(-1);
            Uri uriInsert = context.getContentResolver().insert(AlarmAlertTable.CONTENT_URI, transferToContentValues(alarmAlert));
            if (uriInsert != null) {
                return (int) ContentUris.parseId(uriInsert);
            }
            return -1;
        } catch (Exception e) {
            PrefUtil.setMonitorStatus(2);
            Log.e(TAG, "insertAlarmAlert error: ", e);
            return -1;
        }
    }

    private static int updateAlarmAlert(Context context, ContentValues contentValues, long j) {
        if (contentValues == null) {
            return -1;
        }
        try {
            return context.getContentResolver().update(AlarmAlertTable.CONTENT_URI, contentValues, "alarm_time = ?", new String[]{String.valueOf(j)});
        } catch (Exception e) {
            PrefUtil.setMonitorStatus(2);
            Log.e(TAG, "updateAlarmAlert error: ", e);
            return -1;
        }
    }

    public static void deleteAlarmAlert(Context context) {
        try {
            context.getContentResolver().delete(AlarmAlertTable.CONTENT_URI, null, null);
        } catch (Exception e) {
            PrefUtil.setMonitorStatus(2);
            Log.e(TAG, "deleteAlarmAlert error: ", e);
        }
    }

    private static AlarmAlert createFromCursor(Cursor cursor) {
        AlarmAlert alarmAlert = new AlarmAlert();
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex("_id");
            if (columnIndex != -1) {
                alarmAlert.setId(cursor.getInt(columnIndex));
            }
            int columnIndex2 = cursor.getColumnIndex("alarm_id");
            if (columnIndex2 != -1) {
                alarmAlert.setAlarmId(cursor.getInt(columnIndex2));
            }
            int columnIndex3 = cursor.getColumnIndex("alarm_time");
            if (columnIndex3 != -1) {
                alarmAlert.setAlarmTime(cursor.getLong(columnIndex3));
            }
            int columnIndex4 = cursor.getColumnIndex(AlarmAlertTable.Columns.NOTIFY_TIME);
            if (columnIndex4 != -1) {
                alarmAlert.setNotifyTime(cursor.getLong(columnIndex4));
            }
            int columnIndex5 = cursor.getColumnIndex(AlarmAlertTable.Columns.PLAY_TIME);
            if (columnIndex5 != -1) {
                alarmAlert.setPlayTime(cursor.getLong(columnIndex5));
            }
            int columnIndex6 = cursor.getColumnIndex(AlarmAlertTable.Columns.SHOW_TIME);
            if (columnIndex6 != -1) {
                alarmAlert.setShowTime(cursor.getLong(columnIndex6));
            }
            int columnIndex7 = cursor.getColumnIndex(AlarmAlertTable.Columns.VOLUME);
            if (columnIndex7 != -1) {
                alarmAlert.setVolume(cursor.getInt(columnIndex7));
            }
            int columnIndex8 = cursor.getColumnIndex(AlarmAlertTable.Columns.SCREEN_LOCKED);
            if (columnIndex8 != -1) {
                alarmAlert.setScreenLocked(cursor.getInt(columnIndex8) == 1);
            }
        }
        return alarmAlert;
    }

    private static ContentValues transferToContentValues(AlarmAlert alarmAlert) {
        ContentValues contentValues = new ContentValues();
        if (alarmAlert != null) {
            if (alarmAlert.getId() != -1) {
                contentValues.put("_id", Integer.valueOf(alarmAlert.getId()));
            }
            contentValues.put("alarm_id", Integer.valueOf(alarmAlert.getAlarmId()));
            contentValues.put("alarm_time", Long.valueOf(alarmAlert.getAlarmTime()));
            contentValues.put(AlarmAlertTable.Columns.NOTIFY_TIME, Long.valueOf(alarmAlert.getNotifyTime()));
            contentValues.put(AlarmAlertTable.Columns.PLAY_TIME, Long.valueOf(alarmAlert.getPlayTime()));
            contentValues.put(AlarmAlertTable.Columns.SHOW_TIME, Long.valueOf(alarmAlert.getShowTime()));
            contentValues.put(AlarmAlertTable.Columns.VOLUME, Integer.valueOf(alarmAlert.getVolume()));
            contentValues.put(AlarmAlertTable.Columns.SCREEN_LOCKED, Integer.valueOf(alarmAlert.isScreenLocked() ? 1 : 0));
        }
        return contentValues;
    }
}
