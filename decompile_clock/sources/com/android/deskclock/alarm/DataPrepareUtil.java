package com.android.deskclock.alarm;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmDataHelper;
import com.android.deskclock.util.PadAdapterUtil;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class DataPrepareUtil {
    private static String TAG = "DC:DataPrepare";
    private static List<Alarm> mAlarms = null;
    private static boolean mQueryDone = false;
    private static long mQueryTime;
    private static Alarm mWakeAlarm;

    public static void queryAlarm() {
        mQueryDone = false;
        mAlarms = queryNormalAlarms();
        mWakeAlarm = queryWakeAlarm();
        mQueryDone = true;
    }

    public static void setQueryTime() {
        mQueryTime = System.currentTimeMillis();
    }

    public static boolean isQueryDone() {
        Log.d(TAG, "isFinished: " + mQueryDone);
        return mQueryDone;
    }

    public static boolean isValid() {
        long jCurrentTimeMillis = System.currentTimeMillis() - mQueryTime;
        return jCurrentTimeMillis < 3000 && jCurrentTimeMillis > 0;
    }

    public static List<Alarm> getAlarms() {
        return mAlarms;
    }

    public static Alarm getWakeAlarm(Context context) {
        if (!BedtimeUtil.isWakeAlarmSupport(context)) {
            mWakeAlarm = null;
        }
        return mWakeAlarm;
    }

    public static List<Alarm> queryNormalAlarms() {
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = DeskClockApp.getAppDEContext().getContentResolver().query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS, Alarm.Columns.WHERE_NORMAL_ALARM, null, Alarm.Columns.DEFAULT_SORT_ORDER);
        try {
            if (cursorQuery.moveToFirst()) {
                do {
                    arrayList.add(new Alarm(cursorQuery));
                } while (cursorQuery.moveToNext());
            }
            cursorQuery.close();
            arrayList.addAll(ShiftAlarmDataHelper.getShowAlarms());
            return arrayList;
        } catch (Throwable th) {
            cursorQuery.close();
            throw th;
        }
    }

    public static Alarm queryWakeAlarm() {
        Alarm alarm = null;
        if (PadAdapterUtil.IS_PAD) {
            return null;
        }
        Cursor cursorQueryWakeAlarm = BedtimeUtil.queryWakeAlarm(DeskClockApp.getAppDEContext());
        if (cursorQueryWakeAlarm != null) {
            try {
                alarm = cursorQueryWakeAlarm.moveToFirst() ? new Alarm(cursorQueryWakeAlarm) : null;
            } finally {
                cursorQueryWakeAlarm.close();
            }
        }
        return alarm;
    }
}
