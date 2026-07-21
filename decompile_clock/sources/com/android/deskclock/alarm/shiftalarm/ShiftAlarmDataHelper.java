package com.android.deskclock.alarm.shiftalarm;

import android.database.sqlite.SQLiteDatabase;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.Log;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ShiftAlarmDataHelper {
    static String TAG = "DC:ShiftAlarmDataHelper";

    public static List<Alarm> getShowAlarms() {
        return ShiftAlarmDataInstance.getInstance().getShowAlarms();
    }

    public static void resetCache() {
        ShiftAlarmDataInstance.getInstance().resetIfNeed();
    }

    public static List<ShiftAlarmGroup> getShiftAlarmGroups() {
        return ShiftAlarmDataInstance.getInstance().getShiftAlarmGroups();
    }

    public static ShiftAlarmGroup getShiftGroupFromAlarmId(int i) {
        ShiftAlarmGroup shiftGroupFromAlarmId = ShiftAlarmDataInstance.getInstance().getShiftGroupFromAlarmId(i);
        if (shiftGroupFromAlarmId != null) {
            return shiftGroupFromAlarmId.copy();
        }
        Log.f(TAG, "getShiftGroupFromAlarmId error, alarmId:" + i);
        return ShiftAlarmGroup.getDefault();
    }

    public static String getShiftDurationFromAlarmId(int i) {
        return ShiftAlarmDataInstance.getInstance().getShiftDurationFromAlarmId(i);
    }

    public static int getShiftIndexFromAlarmId(int i) {
        return ShiftAlarmDataInstance.getInstance().getShiftIndexFromAlarmId(i);
    }

    public static void enableShiftAlarm(int i, boolean z, long j) {
        ShiftAlarmGroup shiftGroupFromAlarmId = getShiftGroupFromAlarmId(i);
        if (!z) {
            if (j == 0) {
                shiftGroupFromAlarmId.enable = false;
                shiftGroupFromAlarmId.skipTime = 0L;
                shiftGroupFromAlarmId.skipIndex = 0;
            } else {
                shiftGroupFromAlarmId.enable = false;
                shiftGroupFromAlarmId.skipTime = j;
                for (ShiftAlarm shiftAlarm : shiftGroupFromAlarmId.shiftAlarms) {
                    if (shiftAlarm.alarmId == i) {
                        shiftGroupFromAlarmId.skipIndex = shiftAlarm.index;
                        break;
                    }
                }
            }
        } else {
            shiftGroupFromAlarmId.enable = true;
            shiftGroupFromAlarmId.skipTime = 0L;
            shiftGroupFromAlarmId.skipIndex = 0;
        }
        disableSnooze(shiftGroupFromAlarmId);
        saveShiftAlarmGroup(shiftGroupFromAlarmId);
    }

    public static boolean isShiftAlarm(int i) {
        return ShiftAlarmDataInstance.getInstance().getShiftGroupFromAlarmId(i) != null;
    }

    public static void deleteShiftAlarmByAlarmId(int i) {
        deleteShiftAlarmGroup(ShiftAlarmDataInstance.getInstance().getShiftGroupFromAlarmId(i));
    }

    public static void deleteShiftAlarmGroup(ShiftAlarmGroup shiftAlarmGroup) {
        if (shiftAlarmGroup == null) {
            return;
        }
        Log.f(TAG, "deleteShiftAlarmGroup id: " + shiftAlarmGroup.id);
        shiftAlarmGroup.showLog();
        ShiftAlarmDataInstance.getInstance().removeShiftGroup(shiftAlarmGroup);
        Iterator<Integer> it = shiftAlarmGroup.alarmIds.iterator();
        while (it.hasNext()) {
            AlarmHelper.disableSnoozeAlert(DeskClockApp.getAppContext(), it.next().intValue());
        }
        ShiftAlarmDbHelper.getInstance().deleteShiftAlarmGroup(shiftAlarmGroup);
        DeskClockApp.getAppContext().getContentResolver().notifyChange(ShiftAlarm.Columns.CONTENT_URI, null);
        AlarmHelper.setNextAlert(DeskClockApp.getAppContext());
    }

    public static void saveShiftAlarmGroup(ShiftAlarmGroup shiftAlarmGroup) {
        Log.f(TAG, "saveShiftAlarmGroup");
        shiftAlarmGroup.showLog();
        if (shiftAlarmGroup.id < 0) {
            ShiftAlarmDbHelper.getInstance().insertShiftAlarmGroup(shiftAlarmGroup);
            ShiftAlarmDataInstance.getInstance().addShiftGroup(shiftAlarmGroup);
        } else {
            ShiftAlarmDbHelper.getInstance().updateShiftAlarmGroup(shiftAlarmGroup);
            ShiftAlarmDataInstance.getInstance().updateShiftGroup(shiftAlarmGroup);
        }
        DeskClockApp.getAppContext().getContentResolver().notifyChange(ShiftAlarm.Columns.CONTENT_URI, null);
        AlarmHelper.setNextAlert(DeskClockApp.getAppContext());
        Log.f(TAG, "after save");
        Iterator<ShiftAlarmGroup> it = ShiftAlarmDataInstance.getInstance().getShiftAlarmGroups().iterator();
        while (it.hasNext()) {
            it.next().showLog();
        }
    }

    public static void disableSnooze(ShiftAlarmGroup shiftAlarmGroup) {
        if (shiftAlarmGroup == null) {
            return;
        }
        Iterator<ShiftAlarm> it = shiftAlarmGroup.shiftAlarms.iterator();
        while (it.hasNext()) {
            AlarmHelper.disableSnoozeAlert(DeskClockApp.getAppContext(), it.next().alarmId);
        }
    }

    public static void handleRestoreShiftAlarms(SQLiteDatabase sQLiteDatabase) {
        ShiftAlarmDbHelper.handleRestoreShiftAlarms(sQLiteDatabase);
    }

    public static void handlerXiaoAiRingtoneForShiftAlarm(ShiftAlarmGroup shiftAlarmGroup, boolean z) {
        if (z) {
            for (ShiftAlarm shiftAlarm : shiftAlarmGroup.shiftAlarms) {
                if (shiftAlarm.alarmId > 0) {
                    XiaoAiRingtoneHelper.addXiaoAiRingtoneIds(DeskClockApp.getAppDEContext(), shiftAlarm.alarmId);
                }
            }
            return;
        }
        for (ShiftAlarm shiftAlarm2 : shiftAlarmGroup.shiftAlarms) {
            if (shiftAlarm2.alarmId > 0) {
                XiaoAiRingtoneHelper.removeXiaoAiRingtoneIds(DeskClockApp.getAppDEContext(), shiftAlarm2.alarmId);
            }
        }
    }
}
