package com.android.deskclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.util.Log;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.holiday.HolidayHelper;
import com.android.deskclock.alarm.DataPrepareUtil;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.alarm.bedtime.HealthDataUtil;
import com.android.deskclock.alarm.bedtime.ZenModeUtil;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.CityZoneHelper;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.log.ExLogger;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.util.themeringtone.RingtoneHelper;
import miuix.app.Application;
import miuix.autodensity.AutoDensityConfig;
import miuix.autodensity.IDensity;
import miuix.device.DeviceUtils;

/* JADX INFO: loaded from: classes.dex */
public class DeskClockApp extends Application implements IDensity {
    private static String TAG = "DC:DeskClockApp";
    private static long sAlarmJobPeriodic = 86400000;
    private static volatile Context sContext = null;
    private static volatile Context sDEContext = null;
    private static long sUpdateHolidayJobPeriodic = 604800000;
    private static long sUpdateRingtoneJobPeriodic = 86400000;

    @Override // miuix.autodensity.IDensity
    public boolean shouldAdaptAutoDensity() {
        return true;
    }

    @Override // android.content.ContextWrapper
    protected void attachBaseContext(Context context) {
        Log.d(TAG, "attachBaseContext");
        super.attachBaseContext(context);
        DeviceUtils.initPerf(context);
        sContext = this;
        sDEContext = FBEUtil.createDeviceProtectedStorageContext(sContext);
    }

    @Override // miuix.app.Application, android.app.Application
    public void onCreate() {
        super.onCreate();
        AutoDensityConfig.init(this);
        com.android.deskclock.util.Log.i(TAG, "onCreate start");
        sContext = this;
        sDEContext = FBEUtil.createDeviceProtectedStorageContext(sContext);
        if (FBEUtil.isUserUnlocked(sContext)) {
            AsyncTask.execute(new Runnable() { // from class: com.android.deskclock.DeskClockApp.1
                @Override // java.lang.Runnable
                public void run() {
                    com.android.deskclock.util.Log.d("Move the DataBaseFile and SharedPrefFile, When app attachBaseContext()");
                    FBEUtil.moveData(DeskClockApp.sContext, DeskClockApp.getAppDEContext());
                }
            });
        }
        Util.init(sContext);
        if (!Util.isInternational()) {
            HolidayHelper.init();
        } else {
            AlarmHelper.setNextAlert(sContext);
        }
        if (!FBEUtil.isUserLocked(sContext)) {
            StatHelper.init(sContext);
            OneTrackStatHelper.init(sContext);
            ExLogger.getInstance().addCEStorageLog();
        } else {
            ExLogger.getInstance().addDEStorageLog();
        }
        if (!Util.isInternational()) {
            JobUtil.startJobScheduler(sContext, JobUtil.JOB_SCHEDULE_ALERT_ID, sAlarmJobPeriodic);
            JobUtil.startJobScheduler(sContext, JobUtil.JOB_SCHEDULE_UPDATE_HOLIDAY_ID, sUpdateHolidayJobPeriodic);
        }
        JobUtil.startJobScheduler(sContext, JobUtil.JOB_SCHEDULE_UPDATE_RINGTONE_ID, sUpdateRingtoneJobPeriodic);
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.DeskClockApp.2
            @Override // java.lang.Runnable
            public void run() {
                SharedPreferences sharedPreferences;
                try {
                    if (BedtimeUtil.isWakeAlarmSupport(DeskClockApp.sDEContext) && (sharedPreferences = FBEUtil.getSharedPreferences(DeskClockApp.sDEContext, "BedtimeAlarm", 0)) != null && BedtimeUtil.getWakeDaysOfWeek(sharedPreferences) == 512) {
                        sharedPreferences.edit().putInt(BedtimeUtil.SP_WAKE_ALARM_DAYS_OF_WEEK, 127).apply();
                        if (HealthDataUtil.isHealthAppValuable(DeskClockApp.sContext)) {
                            HealthDataUtil.updateRepeatType(DeskClockApp.sContext, 127);
                        }
                    }
                } catch (Exception e) {
                    com.android.deskclock.util.Log.e(DeskClockApp.TAG, "change bedtime shift data error: " + e);
                }
            }
        });
        DataPrepareUtil.setQueryTime();
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.DeskClockApp.3
            @Override // java.lang.Runnable
            public void run() {
                if (MiuiSdk.isSupportSleep()) {
                    DeskClockApp.this.handleSleepRelatedTasks();
                }
                DeskClockApp.this.handleAlarmRelatedTasks();
            }
        });
        com.android.deskclock.util.Log.i(TAG, "onCreate end");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAlarmRelatedTasks() {
        DataPrepareUtil.queryAlarm();
        CityZoneHelper.init();
        AlarmHelper.handleSPDataToDB(sDEContext);
        RingtoneHelper.handleDefaultRingtone();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSleepRelatedTasks() {
        if (sContext != null && "".equals(ZenModeUtil.getLocalZenRuleId(sContext))) {
            ZenModeUtil.clearZenMode(sContext);
        }
        if (BedtimeUtil.hasTransferWakeAlarmToSp(sDEContext)) {
            return;
        }
        BedtimeUtil.transferWakeAlarmToSp(sDEContext);
    }

    @Override // miuix.app.Application, android.app.Application, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        com.android.deskclock.util.Log.d(TAG, "DeskClockApp onConfigurationChanged: ");
    }

    public static Context getAppDEContext() {
        return sDEContext;
    }

    public static Context getAppContext() {
        return sContext;
    }
}
