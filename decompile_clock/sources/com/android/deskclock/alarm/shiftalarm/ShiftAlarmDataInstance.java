package com.android.deskclock.alarm.shiftalarm;

import android.util.Log;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ShiftAlarmDataInstance {
    static final String TAG = "DC:ShiftAlarmDataInstance";
    private static ShiftAlarmDataInstance sInstance;
    List<ShiftAlarmGroup> mShiftAlarmGroups;

    private ShiftAlarmDataInstance() {
    }

    public static synchronized ShiftAlarmDataInstance getInstance() {
        if (sInstance == null) {
            sInstance = new ShiftAlarmDataInstance();
        }
        return sInstance;
    }

    public List<ShiftAlarmGroup> getShiftAlarmGroups() {
        checkDataValid();
        return this.mShiftAlarmGroups;
    }

    public void resetIfNeed() {
        if (this.mShiftAlarmGroups == null) {
            return;
        }
        this.mShiftAlarmGroups = ShiftAlarmDbHelper.getAllShiftGroups(DeskClockApp.getAppContext());
        Log.i(TAG, "need reset");
        show();
    }

    private void checkDataValid() {
        if (this.mShiftAlarmGroups == null) {
            this.mShiftAlarmGroups = ShiftAlarmDbHelper.getAllShiftGroups(DeskClockApp.getAppContext());
            com.android.deskclock.util.Log.f(TAG, "init all shift alarms");
            show();
        }
    }

    public void addShiftGroup(ShiftAlarmGroup shiftAlarmGroup) {
        checkDataValid();
        if (this.mShiftAlarmGroups.contains(shiftAlarmGroup)) {
            return;
        }
        this.mShiftAlarmGroups.add(shiftAlarmGroup.copy());
    }

    private void show() {
        Iterator<ShiftAlarmGroup> it = this.mShiftAlarmGroups.iterator();
        while (it.hasNext()) {
            it.next().showLog();
        }
    }

    public void updateShiftGroup(ShiftAlarmGroup shiftAlarmGroup) {
        checkDataValid();
        for (ShiftAlarmGroup shiftAlarmGroup2 : this.mShiftAlarmGroups) {
            if (shiftAlarmGroup2.id == shiftAlarmGroup.id) {
                if (shiftAlarmGroup == shiftAlarmGroup2) {
                    return;
                }
                shiftAlarmGroup2.copy(shiftAlarmGroup);
                return;
            }
        }
    }

    public void removeShiftGroup(ShiftAlarmGroup shiftAlarmGroup) {
        checkDataValid();
        if (shiftAlarmGroup != null) {
            this.mShiftAlarmGroups.remove(shiftAlarmGroup);
        }
    }

    public List<Alarm> getShowAlarms() {
        checkDataValid();
        ArrayList arrayList = new ArrayList();
        Iterator<ShiftAlarmGroup> it = this.mShiftAlarmGroups.iterator();
        while (it.hasNext()) {
            Alarm showAlarm = ShiftAlarmAlertHelper.getShowAlarm(it.next());
            if (showAlarm != null) {
                arrayList.add(showAlarm);
            }
        }
        return arrayList;
    }

    public ShiftAlarmGroup getShiftGroupFromAlarmId(int i) {
        checkDataValid();
        for (ShiftAlarmGroup shiftAlarmGroup : this.mShiftAlarmGroups) {
            if (shiftAlarmGroup.alarmIds.contains(Integer.valueOf(i))) {
                return shiftAlarmGroup;
            }
        }
        return null;
    }

    public String getShiftDurationFromAlarmId(int i) {
        checkDataValid();
        ShiftAlarmGroup shiftGroupFromAlarmId = getShiftGroupFromAlarmId(i);
        if (shiftGroupFromAlarmId != null) {
            return DeskClockApp.getAppContext().getString(R.string.shift_alarm_info, DeskClockApp.getAppContext().getResources().getQuantityString(R.plurals.shift_alarm_duration, shiftGroupFromAlarmId.duration, Integer.valueOf(shiftGroupFromAlarmId.duration)), DeskClockApp.getAppContext().getResources().getQuantityString(R.plurals.alarms_count, shiftGroupFromAlarmId.alarmCount, Integer.valueOf(shiftGroupFromAlarmId.alarmCount)));
        }
        return "";
    }

    public int getShiftIndexFromAlarmId(int i) {
        checkDataValid();
        ShiftAlarmGroup shiftGroupFromAlarmId = getShiftGroupFromAlarmId(i);
        if (shiftGroupFromAlarmId == null) {
            return -1;
        }
        for (ShiftAlarm shiftAlarm : shiftGroupFromAlarmId.shiftAlarms) {
            if (shiftAlarm.alarmId == i) {
                return shiftAlarm.index;
            }
        }
        return -1;
    }
}
