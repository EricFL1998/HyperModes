package com.android.deskclock.alarm.shiftalarm;

import android.util.Log;
import com.android.deskclock.Alarm;
import com.android.deskclock.timer.TimerDao;
import java.util.Calendar;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class ShiftAlarmAlertHelper {
    static String TAG = "ShiftAlarmAlertHelper";

    public static Alarm getShowAlarm(ShiftAlarmGroup shiftAlarmGroup) {
        Log.i(TAG, "getShowAlarm id: " + shiftAlarmGroup.id);
        Alarm alarm = new Alarm();
        alarm.alert = shiftAlarmGroup.alert;
        alarm.silent = shiftAlarmGroup.silent;
        alarm.seconds = 0;
        alarm.vibrate = shiftAlarmGroup.vibrate;
        alarm.label = shiftAlarmGroup.label;
        alarm.daysOfWeek = new Alarm.DaysOfWeek(512);
        alarm.enabled = shiftAlarmGroup.enable;
        alarm.type = 2;
        alarm.deleteAfterUse = false;
        int currentIndex = getCurrentIndex(shiftAlarmGroup.startTime, shiftAlarmGroup.duration);
        if (currentIndex == 0) {
            Log.i(TAG, "shift-alarm not started");
            for (ShiftAlarm shiftAlarm : shiftAlarmGroup.shiftAlarms) {
                if (shiftAlarm.enable) {
                    alarm.skipTime = 0L;
                    alarm.id = shiftAlarm.alarmId;
                    alarm.minutes = shiftAlarm.minutes;
                    alarm.hour = shiftAlarm.hour;
                    alarm.time = calculateUnStartedAlertTime(shiftAlarmGroup).getTimeInMillis();
                    break;
                }
            }
            return alarm;
        }
        if (!shiftAlarmGroup.enable && shiftAlarmGroup.skipTime > 0 && shiftAlarmGroup.skipTime < System.currentTimeMillis()) {
            Log.i(TAG, "skipTime skipped, reset");
            shiftAlarmGroup.skipTime = 0L;
            shiftAlarmGroup.skipIndex = -1;
            shiftAlarmGroup.enable = true;
            ShiftAlarmDataHelper.saveShiftAlarmGroup(shiftAlarmGroup);
        }
        if (!shiftAlarmGroup.enable && shiftAlarmGroup.skipTime > 0 && shiftAlarmGroup.skipTime > System.currentTimeMillis() && shiftAlarmGroup.shiftAlarms != null && shiftAlarmGroup.skipIndex > 0 && shiftAlarmGroup.skipIndex <= shiftAlarmGroup.shiftAlarms.size()) {
            alarm.skipTime = shiftAlarmGroup.skipTime;
            alarm.id = shiftAlarmGroup.shiftAlarms.get(shiftAlarmGroup.skipIndex - 1).alarmId;
            alarm.minutes = shiftAlarmGroup.shiftAlarms.get(shiftAlarmGroup.skipIndex - 1).minutes;
            alarm.hour = shiftAlarmGroup.shiftAlarms.get(shiftAlarmGroup.skipIndex - 1).hour;
            return alarm;
        }
        Log.i(TAG, "getShowAlarm，today index:" + currentIndex);
        if (currentIndex > shiftAlarmGroup.shiftAlarms.size() || shiftAlarmGroup.duration > shiftAlarmGroup.shiftAlarms.size()) {
            return null;
        }
        int i = currentIndex - 1;
        int i2 = isTodayTimePassed(shiftAlarmGroup.shiftAlarms.get(i).hour, shiftAlarmGroup.shiftAlarms.get(i).minutes) ? (1 % shiftAlarmGroup.duration) + currentIndex : currentIndex;
        Log.d(TAG, "query show day from:" + i2);
        for (int i3 = i2 - 1; i3 < (shiftAlarmGroup.duration + i2) - 1; i3++) {
            int i4 = i3 % shiftAlarmGroup.duration;
            ShiftAlarm shiftAlarm2 = shiftAlarmGroup.shiftAlarms.get(i4);
            Log.i(TAG, "query day index:" + shiftAlarm2.index);
            if (!shiftAlarm2.enable) {
                Log.i(TAG, "reset skip");
            } else {
                alarm.skipTime = 0L;
                alarm.id = shiftAlarmGroup.shiftAlarms.get(i4).alarmId;
                alarm.minutes = shiftAlarmGroup.shiftAlarms.get(i4).minutes;
                alarm.hour = shiftAlarmGroup.shiftAlarms.get(i4).hour;
                alarm.time = calculateAlertTime(alarm.hour, alarm.minutes, currentIndex, shiftAlarmGroup.shiftAlarms.get(i4).index, shiftAlarmGroup.duration).getTimeInMillis();
                Log.i(TAG, "to show sshift alarm index:" + shiftAlarm2.index);
                break;
            }
        }
        return alarm;
    }

    private static boolean isTodayTimePassed(int i, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int i3 = calendar.get(11);
        boolean z = i3 > i || (i3 == i && calendar.get(12) >= i2);
        Log.i(TAG, "isTodayTimePassed: " + z);
        return z;
    }

    private static Alarm getAlertAlarmFromGroup(ShiftAlarmGroup shiftAlarmGroup) {
        Log.i(TAG, "getAlertAlarmFromGroup, id:" + shiftAlarmGroup.id);
        ShiftAlarm shiftAlarm = null;
        if (!shiftAlarmGroup.enable && shiftAlarmGroup.skipTime == 0) {
            Log.i(TAG, "shift alarm is close");
            return null;
        }
        if (shiftAlarmGroup.skipTime > 0 && shiftAlarmGroup.skipTime < System.currentTimeMillis()) {
            Log.i(TAG, "skipTime已过期");
            shiftAlarmGroup.skipTime = 0L;
            shiftAlarmGroup.enable = true;
            shiftAlarmGroup.skipIndex = -1;
            ShiftAlarmDataHelper.saveShiftAlarmGroup(shiftAlarmGroup);
        }
        Alarm alarm = new Alarm();
        alarm.alert = shiftAlarmGroup.alert;
        alarm.silent = shiftAlarmGroup.silent;
        alarm.deleteAfterUse = false;
        alarm.seconds = 0;
        alarm.vibrate = shiftAlarmGroup.vibrate;
        alarm.label = shiftAlarmGroup.label;
        alarm.enabled = shiftAlarmGroup.enable;
        alarm.daysOfWeek = new Alarm.DaysOfWeek(512);
        alarm.time = 0L;
        alarm.skipTime = 0L;
        alarm.type = 2;
        int currentIndex = getCurrentIndex(shiftAlarmGroup.startTime, shiftAlarmGroup.duration);
        Log.i(TAG, "todayIndex: " + currentIndex);
        if (currentIndex == 0) {
            Log.i(TAG, "shift-alarm not started");
            for (ShiftAlarm shiftAlarm2 : shiftAlarmGroup.shiftAlarms) {
                if (shiftAlarm2.enable) {
                    alarm.id = shiftAlarm2.alarmId;
                    alarm.minutes = shiftAlarm2.minutes;
                    alarm.hour = shiftAlarm2.hour;
                    alarm.time = calculateUnStartedAlertTime(shiftAlarmGroup).getTimeInMillis();
                    break;
                }
            }
            return alarm;
        }
        if (currentIndex > shiftAlarmGroup.shiftAlarms.size() || shiftAlarmGroup.duration > shiftAlarmGroup.shiftAlarms.size()) {
            return null;
        }
        int i = currentIndex - 1;
        int i2 = isTodayTimePassed(shiftAlarmGroup.shiftAlarms.get(i).hour, shiftAlarmGroup.shiftAlarms.get(i).minutes) ? (1 % shiftAlarmGroup.duration) + currentIndex : currentIndex;
        Log.i(TAG, "query alert alarm from:" + i2);
        int i3 = i2 - 1;
        for (int i4 = 1; i3 < (shiftAlarmGroup.duration + i2) - i4; i4 = 1) {
            int i5 = i3 % shiftAlarmGroup.duration;
            ShiftAlarm shiftAlarm3 = shiftAlarmGroup.shiftAlarms.get(i5);
            Log.i(TAG, "query alert alarm, dayIndex:" + shiftAlarm3.index);
            if (!shiftAlarm3.enable && shiftAlarmGroup.skipIndex != shiftAlarm3.index) {
                Log.i(TAG, "reset skip");
            } else {
                alarm.id = shiftAlarmGroup.shiftAlarms.get(i5).alarmId;
                alarm.minutes = shiftAlarmGroup.shiftAlarms.get(i5).minutes;
                alarm.hour = shiftAlarmGroup.shiftAlarms.get(i5).hour;
                alarm.time = calculateAlertTime(alarm.hour, alarm.minutes, currentIndex, shiftAlarmGroup.shiftAlarms.get(i5).index, shiftAlarmGroup.duration).getTimeInMillis();
                Log.i(TAG, "find next alert , day index:" + shiftAlarm3.index + ", hour:" + alarm.hour + " min:" + alarm.minutes + " alarm:" + alarm);
                if (shiftAlarmGroup.skipTime <= 0 || shiftAlarm != null) {
                    break;
                }
                if (shiftAlarmGroup.skipTime != alarm.time || shiftAlarmGroup.skipIndex != shiftAlarm3.index) {
                    shiftAlarmGroup.skipTime = 0L;
                    shiftAlarmGroup.enable = true;
                    shiftAlarmGroup.skipIndex = -1;
                    ShiftAlarmDataHelper.saveShiftAlarmGroup(shiftAlarmGroup);
                    break;
                }
                alarm.time = 0L;
                alarm.id = -1;
                shiftAlarm = shiftAlarm3;
            }
            i3++;
            i2 = i2;
        }
        if (alarm.time == 0 && shiftAlarm != null) {
            alarm.minutes = shiftAlarm.minutes;
            alarm.hour = shiftAlarm.hour;
            alarm.time = calculateAlarmTime(alarm.hour, alarm.minutes, currentIndex, shiftAlarm.index, shiftAlarmGroup.duration, true).getTimeInMillis();
            alarm.id = shiftAlarm.alarmId;
        }
        return alarm;
    }

    public static Alarm getNextShiftAlarm() {
        Iterator<ShiftAlarmGroup> it = ShiftAlarmDataHelper.getShiftAlarmGroups().iterator();
        Alarm alarm = null;
        while (it.hasNext()) {
            Alarm alertAlarmFromGroup = getAlertAlarmFromGroup(it.next());
            if (alertAlarmFromGroup != null && (alarm == null || alertAlarmFromGroup.time < alarm.time)) {
                alarm = alertAlarmFromGroup;
            }
        }
        return alarm;
    }

    public static long getAlertTime(ShiftAlarmGroup shiftAlarmGroup) {
        Alarm alertAlarmFromGroup = getAlertAlarmFromGroup(shiftAlarmGroup);
        if (alertAlarmFromGroup != null) {
            return alertAlarmFromGroup.time;
        }
        return 0L;
    }

    public static int getCurrentIndex(long j, int i) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis < j) {
            return 0;
        }
        return (((int) ((jCurrentTimeMillis - j) / TimerDao.TIMER_MAX_LENGTH)) % i) + 1;
    }

    public static Calendar calculateUnStartedAlertTime(ShiftAlarmGroup shiftAlarmGroup) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(shiftAlarmGroup.startTime);
        for (ShiftAlarm shiftAlarm : shiftAlarmGroup.shiftAlarms) {
            if (shiftAlarm.enable) {
                calendar.set(11, shiftAlarm.hour);
                calendar.set(12, shiftAlarm.minutes);
                calendar.set(13, 0);
                calendar.set(14, 0);
                calendar.add(5, shiftAlarm.index - 1);
                break;
            }
        }
        return calendar;
    }

    public static Calendar calculateAlertTime(int i, int i2, int i3, int i4, int i5) {
        return calculateAlarmTime(i, i2, i3, i4, i5, false);
    }

    public static Calendar calculateAlarmTime(int i, int i2, int i3, int i4, int i5, boolean z) {
        int i6;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int i7 = calendar.get(11);
        int i8 = calendar.get(12);
        if (i3 == i4) {
            i6 = (i < i7 || (i == i7 && i2 <= i8)) ? i5 : 0;
        } else {
            i6 = i4 - i3;
            if (i6 < 0) {
                i6 += i5;
            }
        }
        calendar.set(11, i);
        calendar.set(12, i2);
        calendar.set(13, 0);
        calendar.set(14, 0);
        if (z) {
            i6 += i5;
        }
        if (i6 > 0) {
            calendar.add(7, i6);
        }
        return calendar;
    }
}
