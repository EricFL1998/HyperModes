package com.android.deskclock.appaf;

import android.content.Context;
import com.android.deskclock.Alarm;
import com.android.deskclock.timer.Timer;

/* JADX INFO: loaded from: classes.dex */
public class AppSearchUtil {
    public static final String TAG = "DC:AppSearchUtil";
    private static volatile AppSearchUtil mAppSearchUtil;

    public void addAppSearchAlarmScheduled(Alarm alarm, boolean z) {
    }

    public void addAppSearchTimer(Timer timer) {
    }

    public void deleteAppSearchAlarm(int i) {
    }

    public void deleteAppSearchTimer(int i) {
    }

    public void insertDefaultAlarmsToAppSearch() {
    }

    public void updateAppSearchAlarmDismissed(Alarm alarm, boolean z) {
    }

    public void updateAppSearchAlarmFiring(Alarm alarm, boolean z) {
    }

    public void updateAppSearchAlarmMissed(Alarm alarm, boolean z) {
    }

    public void updateAppSearchAlarmScheduled(Alarm alarm, boolean z) {
    }

    public void updateAppSearchAlarmSnoozed(Alarm alarm, boolean z) {
    }

    public void updateAppSearchTimerToContinue(Timer timer) {
    }

    public void updateAppSearchTimerToExpired(Timer timer) {
    }

    public void updateAppSearchTimerToPause(Timer timer) {
    }

    private AppSearchUtil(Context context) {
    }

    public static synchronized AppSearchUtil getInstance(Context context) {
        if (mAppSearchUtil == null) {
            synchronized (AppSearchUtil.class) {
                if (mAppSearchUtil == null) {
                    mAppSearchUtil = new AppSearchUtil(context);
                }
            }
        }
        return mAppSearchUtil;
    }
}
