package com.android.deskclock;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.holiday.HolidayInstance;
import com.android.deskclock.addition.holiday.HolidayUtil;
import com.android.deskclock.addition.monitor.MonitorHelper;
import com.android.deskclock.addition.ringtone.star.WYStarRingtoneHelper;
import com.android.deskclock.addition.ringtone.weather.WeatherRingtoneHelper;
import com.android.deskclock.addition.ringtone.week.WeekRingtoneHelper;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.NetworkUtil;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.permission.UserNoticeUtil;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.util.themeringtone.RingtoneHelper;

/* JADX INFO: loaded from: classes.dex */
public class JobSchedulerService extends JobService {
    private static final int MESSAGE_ID = 1;
    private Handler mJobHandler = new Handler(new Handler.Callback() { // from class: com.android.deskclock.JobSchedulerService.4
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            JobSchedulerService.this.jobFinished((JobParameters) message.obj, false);
            return true;
        }
    });

    @Override // android.app.job.JobService
    public boolean onStartJob(final JobParameters jobParameters) {
        final Context appDEContext = DeskClockApp.getAppDEContext();
        if (jobParameters.getJobId() == JobUtil.JOB_SCHEDULE_ALERT_ID) {
            AsyncTask.execute(new Runnable() { // from class: com.android.deskclock.JobSchedulerService.1
                @Override // java.lang.Runnable
                public void run() {
                    MonitorHelper.report();
                    JobSchedulerService.this.addStatistics(appDEContext);
                    JobSchedulerService.this.mJobHandler.sendMessage(Message.obtain(JobSchedulerService.this.mJobHandler, 1, jobParameters));
                }
            });
        } else if (jobParameters.getJobId() == JobUtil.JOB_SCHEDULE_UPDATE_HOLIDAY_ID) {
            Log.i("JobSchedulerService(), onStartJob update holiday!");
            if (!UserNoticeUtil.isNetPermissionAgreed() || !NetworkUtil.isNetworkConnected()) {
                Log.d("JobSchedulerService(), onStartJob update holiday is return!");
                Handler handler = this.mJobHandler;
                handler.sendMessage(Message.obtain(handler, 1, jobParameters));
                return true;
            }
            AsyncTask.execute(new Runnable() { // from class: com.android.deskclock.JobSchedulerService.2
                @Override // java.lang.Runnable
                public void run() throws Throwable {
                    if (HolidayUtil.updateHolidayData(appDEContext)) {
                        HolidayInstance.getInstance(appDEContext).initHolidayData(appDEContext);
                    }
                    JobSchedulerService.this.mJobHandler.sendMessage(Message.obtain(JobSchedulerService.this.mJobHandler, 1, jobParameters));
                }
            });
        } else if (jobParameters.getJobId() == JobUtil.JOB_SCHEDULE_UPDATE_RINGTONE_ID) {
            AsyncTask.execute(new Runnable() { // from class: com.android.deskclock.JobSchedulerService.3
                @Override // java.lang.Runnable
                public void run() {
                    RingtoneHelper.updateRingtone();
                    JobSchedulerService.this.mJobHandler.sendMessage(Message.obtain(JobSchedulerService.this.mJobHandler, 1, jobParameters));
                }
            });
        }
        return true;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        if (jobParameters.getJobId() != JobUtil.JOB_SCHEDULE_ALERT_ID) {
            return false;
        }
        this.mJobHandler.removeMessages(1);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addStatistics(Context context) {
        int netPermission = PrefUtil.getNetPermission();
        if (netPermission == -1) {
            StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.EVENT_NETWORK_STATE, "OFF");
        } else if (netPermission == 1) {
            StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.EVENT_NETWORK_STATE, "ON");
            StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.KEY_HOLIDAY_VERSION, String.valueOf(HolidayUtil.getRemoteVersion(context)));
        }
        StatHelper.trackEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_LIFE_POST_SETTINGS_STATE, FBEUtil.getDefaultSharedPreferences(context).getBoolean("key_open_alarm_life_post", true) ? "ON" : "OFF");
        StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.EVENT_LIFE_POST_BACKGROUND, FBEUtil.getDefaultSharedPreferences(context).getBoolean("key_open_life_post_background", false) ? "ON" : "OFF");
        StatHelper.trackEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_LIFE_LISTEN_NEWS_STATE, FBEUtil.getDefaultSharedPreferences(context).getBoolean("key_open_alarm_life_post_news", true) ? "ON" : "OFF");
        try {
            Uri defaultAlarmRingtone = AlarmRingtoneUtil.getDefaultAlarmRingtone();
            if (WeatherRingtoneHelper.isWeatherRingtone(defaultAlarmRingtone)) {
                StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.KEY_DEFAULT_ALARM_RINGTONE_PERCENT, "WEATHER");
            } else if (WeekRingtoneHelper.isWeekRingtone(defaultAlarmRingtone)) {
                StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.KEY_DEFAULT_ALARM_RINGTONE_PERCENT, "WEEK");
            } else if (WYStarRingtoneHelper.isWYStarAlert(defaultAlarmRingtone)) {
                StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.KEY_DEFAULT_ALARM_RINGTONE_PERCENT, "STAR");
            } else {
                StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.KEY_DEFAULT_ALARM_RINGTONE_PERCENT, "OTHER");
            }
            StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.KEY_DEFAULT_ALARM_RINGTONE, AlarmRingtoneUtil.getAlarmRingtoneTitle(context, defaultAlarmRingtone));
        } catch (Exception e) {
            Log.d("statistics default alarm ringtone failed: " + e.getMessage());
        }
        if (MiuiSdk.isSupportSleep()) {
            boolean zIsBedtimeOpen = BedtimeUtil.isBedtimeOpen(context);
            StatHelper.trackEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.SLEEP_MANAGE_STATE, zIsBedtimeOpen ? "ON" : "OFF");
            if (zIsBedtimeOpen) {
                StatHelper.recordSleepSettings(context);
            }
        }
        OneTrackStatHelper.recordAllSettings(context);
    }
}
