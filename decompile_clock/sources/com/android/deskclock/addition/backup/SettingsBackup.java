package com.android.deskclock.addition.backup;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.text.TextUtils;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.ringtone.digital.DigitalTimerRingtoneHelper;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.alarm.bedtime.ZenModeUtil;
import com.android.deskclock.settings.AlarmSettingsFragment;
import com.android.deskclock.settings.SettingsActivity;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.permission.UserNoticeUtil;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class SettingsBackup {
    private boolean alarmLouder;
    private boolean arrivingNotify;
    private String autoSilenceTime;
    private boolean lifePost;
    private boolean lifePostBackground;
    private boolean lifePostNews;
    private boolean lifePostState;
    private boolean lockedShowAlarm;
    private AudioManager mAudioManager;
    private int ringVolume;
    private boolean shutdownAlarm;
    private String snoozeCount;
    private String snoozeInterval;
    private String timerAlert;
    private String volumeBehavior;
    private String xiaoaiAlarmIds;
    private boolean hasSetBedtime = false;
    private boolean isBedtimeOpen = false;
    private boolean isManageGuideShowed = false;
    private int sleepAlarmHour = 22;
    private int sleepAlarmMin = 0;
    private boolean bedtimeDndState = true;
    private String bedtimeDndStartTime = "";
    private String bedtimeDndEndTime = "";
    private String bedtimeDndId = "";
    private int bedtiemNotifyAdvTime = 15;
    private boolean isShowBanner = true;
    private int wakeAlarmHour = 6;
    private int wakeAlarmMins = 0;
    private int wakeAlarmDaysOfWeek = 127;
    private long wakeAlarmAlertTime = 0;
    private int wakeAlarmEnable = 0;
    private int wakeAlarmVibrate = 1;
    private String wakeAlarmAlert = "";
    private long wakeAlarmSkipTime = 0;
    private boolean vibrateDefault = true;

    private void initAudioManager(Context context) {
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) context.getSystemService("audio");
        }
    }

    public boolean isShutdownAlarm() {
        return this.shutdownAlarm;
    }

    public void setShutdownAlarm(boolean z) {
        this.shutdownAlarm = z;
    }

    public boolean isAlarmLouder() {
        return this.alarmLouder;
    }

    public void setAlarmLouder(boolean z) {
        this.alarmLouder = z;
    }

    public boolean isArrivingNotify() {
        return this.arrivingNotify;
    }

    public void setArrivingNotify(boolean z) {
        this.arrivingNotify = z;
    }

    public String getSnoozeInterval() {
        return this.snoozeInterval;
    }

    public void setSnoozeInterval(String str) {
        this.snoozeInterval = str;
    }

    public String getSnoozeCount() {
        return this.snoozeCount;
    }

    public void setSnoozeCount(String str) {
        this.snoozeCount = str;
    }

    public String getAutoSilenceTime() {
        return this.autoSilenceTime;
    }

    public void setAutoSilenceTime(String str) {
        this.autoSilenceTime = str;
    }

    public String getTimerAlert() {
        return this.timerAlert;
    }

    public void setTimerAlert(String str) {
        this.timerAlert = str;
    }

    public String getVolumeBehavior() {
        return this.volumeBehavior;
    }

    public void setVolumeBehavior(String str) {
        this.volumeBehavior = str;
    }

    public boolean isLifePost() {
        return this.lifePost;
    }

    public void setLifePost(boolean z) {
        this.lifePost = z;
    }

    public boolean isLifePostBackground() {
        return this.lifePostBackground;
    }

    public void setLifePostBackground(boolean z) {
        this.lifePostBackground = z;
    }

    public boolean isLifePostNews() {
        return this.lifePostNews;
    }

    public void setLifePostNews(boolean z) {
        this.lifePostNews = z;
    }

    public void loadFromPhone(Context context) {
        Log.d("DC:SettingsBackup loadFromPhone start");
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(context);
        this.shutdownAlarm = defaultSharedPreferences.getBoolean("shutdown_alarm", true);
        this.alarmLouder = defaultSharedPreferences.getBoolean("alarm_ascending_mode", true);
        this.arrivingNotify = defaultSharedPreferences.getBoolean("alarm_arriving_notification", true);
        this.snoozeInterval = defaultSharedPreferences.getString("snooze_duration", "10");
        this.snoozeCount = defaultSharedPreferences.getString("snooze_repeat_count", "3");
        this.autoSilenceTime = defaultSharedPreferences.getString("auto_silence", "10");
        this.timerAlert = defaultSharedPreferences.getString("default_timer_alert", String.valueOf(DigitalTimerRingtoneHelper.getRingtoneUri()));
        this.volumeBehavior = defaultSharedPreferences.getString("volume_button_setting", "1");
        this.lifePost = defaultSharedPreferences.getBoolean("key_open_alarm_life_post", true);
        this.lockedShowAlarm = defaultSharedPreferences.getBoolean("locked_show_alarm_arriving_state", true);
        if (!MiuiSdk.canDeleteForLiteMode()) {
            this.lifePostBackground = defaultSharedPreferences.getBoolean("key_open_life_post_background", false);
        }
        this.lifePostNews = defaultSharedPreferences.getBoolean("key_open_alarm_life_post_news", true);
        this.lifePostState = defaultSharedPreferences.getBoolean(AlarmSettingsFragment.KEY_LIFE_POST_STATE, true);
        initAudioManager(context);
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            this.ringVolume = audioManager.getStreamVolume(4);
        }
        if (!Util.isInternational()) {
            SharedPreferences sharedPreferences = FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0);
            this.hasSetBedtime = sharedPreferences.getBoolean(BedtimeUtil.KEY_BEDTIME_ALARM_COMPLETED, false);
            this.isManageGuideShowed = sharedPreferences.getBoolean(BedtimeUtil.KEY_MANAGE_GUIDE_SHOWED, false);
            this.isBedtimeOpen = sharedPreferences.getBoolean(BedtimeUtil.KEY_OPEN_BEDTIME, false);
            this.sleepAlarmHour = sharedPreferences.getInt(BedtimeUtil.KEY_SLEEP_ALARM_HOUR, 22);
            this.sleepAlarmMin = sharedPreferences.getInt(BedtimeUtil.KEY_SLEEP_ALARM_MIN, 0);
            this.bedtimeDndState = sharedPreferences.getBoolean(BedtimeUtil.KEY_SLEEP_NO_DISTURBANCE, true);
            this.isShowBanner = sharedPreferences.getBoolean(BedtimeUtil.KEY_SHOW_BEDTIME_BANNER, false);
            this.bedtiemNotifyAdvTime = sharedPreferences.getInt(BedtimeUtil.KEY_NOTIFICATION_ADV_TIME, 15);
            this.bedtimeDndStartTime = defaultSharedPreferences.getString(ZenModeUtil.KEY_ZEN_RULE_START_TIME, "");
            this.bedtimeDndEndTime = defaultSharedPreferences.getString(ZenModeUtil.KEY_ZEN_RULE_END_TIME, "");
            this.bedtimeDndId = defaultSharedPreferences.getString(ZenModeUtil.KEY_ZEN_RULE_ID, "");
            this.wakeAlarmHour = sharedPreferences.getInt(BedtimeUtil.SP_WAKE_ALARM_HOUR, 6);
            this.wakeAlarmMins = sharedPreferences.getInt(BedtimeUtil.SP_WAKE_ALARM_MINUTES, 0);
            this.wakeAlarmDaysOfWeek = sharedPreferences.getInt(BedtimeUtil.SP_WAKE_ALARM_DAYS_OF_WEEK, 127);
            this.wakeAlarmAlertTime = sharedPreferences.getLong(BedtimeUtil.SP_WAKE_ALARM_ALARM_TIME, 0L);
            this.wakeAlarmEnable = sharedPreferences.getInt(BedtimeUtil.SP_WAKE_ALARM_ENABLE, 0);
            this.wakeAlarmVibrate = sharedPreferences.getInt(BedtimeUtil.SP_WAKE_ALARM_VIBRATE, 1);
            this.wakeAlarmAlert = sharedPreferences.getString(BedtimeUtil.SP_WAKE_ALARM_ALERT, "");
            this.wakeAlarmSkipTime = sharedPreferences.getLong(BedtimeUtil.SP_WAKE_ALARM_SKIP_TIME, 0L);
        }
        this.vibrateDefault = defaultSharedPreferences.getBoolean(AlarmSettingsFragment.KEY_ALERT_VIBRATE, true);
        Log.d("DC:SettingsBackup loadFromPhone end");
    }

    public void loadFromPhoneGoogle(Context context) {
        Log.d("DC:SettingsBackup loadFromPhoneGoogle start");
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(context);
        this.shutdownAlarm = defaultSharedPreferences.getBoolean("shutdown_alarm", true);
        this.alarmLouder = defaultSharedPreferences.getBoolean("alarm_ascending_mode", true);
        this.arrivingNotify = defaultSharedPreferences.getBoolean("alarm_arriving_notification", true);
        this.snoozeInterval = defaultSharedPreferences.getString("snooze_duration", "10");
        this.snoozeCount = defaultSharedPreferences.getString("snooze_repeat_count", "3");
        this.autoSilenceTime = defaultSharedPreferences.getString("auto_silence", "10");
        this.volumeBehavior = defaultSharedPreferences.getString("volume_button_setting", "1");
        this.vibrateDefault = defaultSharedPreferences.getBoolean(AlarmSettingsFragment.KEY_ALERT_VIBRATE, true);
        this.lockedShowAlarm = defaultSharedPreferences.getBoolean("locked_show_alarm_arriving_state", true);
        initAudioManager(context);
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            this.ringVolume = audioManager.getStreamVolume(4);
        }
    }

    public void restoreToPhoneGoogle(Context context) {
        Log.d("DC:SettingsBackup restoreToPhoneGoogle start");
        if (!Util.supportShutdownAlarm() && this.shutdownAlarm) {
            this.shutdownAlarm = false;
        }
        if (TextUtils.isEmpty(this.snoozeInterval)) {
            this.snoozeInterval = "10";
        }
        if (TextUtils.isEmpty(this.snoozeCount)) {
            this.snoozeCount = "3";
        }
        if (TextUtils.isEmpty(this.autoSilenceTime)) {
            this.autoSilenceTime = "10";
        }
        if (TextUtils.isEmpty(this.volumeBehavior)) {
            this.volumeBehavior = "1";
        }
        FBEUtil.getDefaultSharedPreferences(context).edit().putBoolean("shutdown_alarm", this.shutdownAlarm).putBoolean("alarm_ascending_mode", this.alarmLouder).putBoolean("alarm_arriving_notification", this.arrivingNotify).putString("snooze_duration", this.snoozeInterval).putString("snooze_repeat_count", this.snoozeCount).putString("auto_silence", this.autoSilenceTime).putString("volume_button_setting", this.volumeBehavior).putBoolean(AlarmSettingsFragment.KEY_ALERT_VIBRATE, this.vibrateDefault).putBoolean("locked_show_alarm_arriving_state", this.lockedShowAlarm).apply();
        if (this.ringVolume != -1) {
            initAudioManager(context);
            AudioManager audioManager = this.mAudioManager;
            if (audioManager != null) {
                int streamMaxVolume = audioManager.getStreamMaxVolume(4);
                int streamMinVolume = Build.VERSION.SDK_INT >= 28 ? this.mAudioManager.getStreamMinVolume(4) : 0;
                int i = this.ringVolume;
                if (i > streamMaxVolume || i < streamMinVolume) {
                    return;
                }
                this.mAudioManager.setStreamVolume(4, i, 0);
            }
        }
    }

    public void restoreToPhone(Context context) {
        Log.d("DC:SettingsBackup restoreToPhone start");
        if (!Util.supportShutdownAlarm() && this.shutdownAlarm) {
            this.shutdownAlarm = false;
        }
        if (TextUtils.isEmpty(this.snoozeInterval)) {
            this.snoozeInterval = "10";
        }
        if (TextUtils.isEmpty(this.snoozeCount)) {
            this.snoozeCount = "3";
        }
        if (TextUtils.isEmpty(this.autoSilenceTime)) {
            this.autoSilenceTime = "10";
        }
        if (TextUtils.isEmpty(this.timerAlert)) {
            this.timerAlert = String.valueOf(DigitalTimerRingtoneHelper.getRingtoneUri());
        }
        if (TextUtils.isEmpty(this.volumeBehavior)) {
            this.volumeBehavior = "1";
        }
        if (!UserNoticeUtil.isNetPermissionAgreed(context) && this.lifePostNews) {
            this.lifePostNews = false;
        }
        if (Util.isInternational()) {
            this.lifePost = false;
            this.lifePostBackground = false;
            this.lifePostNews = false;
        }
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(context);
        defaultSharedPreferences.edit().putBoolean(SettingsActivity.KEY_IS_SET_BACKGROUND_EVER, true).putBoolean("shutdown_alarm", this.shutdownAlarm).putBoolean("alarm_ascending_mode", this.alarmLouder).putBoolean("alarm_arriving_notification", this.arrivingNotify).putString("snooze_duration", this.snoozeInterval).putString("snooze_repeat_count", this.snoozeCount).putString("snooze_repeat_count_remainder", this.snoozeCount).putString("auto_silence", this.autoSilenceTime).putString("volume_button_setting", this.volumeBehavior).putBoolean("key_open_alarm_life_post", this.lifePost).putBoolean("key_open_alarm_life_post_news", this.lifePostNews).putBoolean(AlarmSettingsFragment.KEY_ALERT_VIBRATE, this.vibrateDefault).putBoolean(AlarmSettingsFragment.KEY_LIFE_POST_STATE, this.lifePostState).putString("default_timer_alert", this.timerAlert).putBoolean("locked_show_alarm_arriving_state", this.lockedShowAlarm).apply();
        Log.d("DC:SettingsBackup restoreToPhone putString timerAlert :" + this.timerAlert);
        if (!MiuiSdk.canDeleteForLiteMode()) {
            defaultSharedPreferences.edit().putBoolean("key_open_life_post_background", this.lifePostBackground).apply();
        }
        if (this.ringVolume != -1) {
            initAudioManager(context);
            AudioManager audioManager = this.mAudioManager;
            if (audioManager != null) {
                int streamMaxVolume = audioManager.getStreamMaxVolume(4);
                int streamMinVolume = Build.VERSION.SDK_INT >= 28 ? this.mAudioManager.getStreamMinVolume(4) : 0;
                int i = this.ringVolume;
                if (i <= streamMaxVolume && i >= streamMinVolume) {
                    this.mAudioManager.setStreamVolume(4, i, 0);
                }
            }
        }
        if (!Util.isInternational()) {
            SharedPreferences.Editor editorPutInt = FBEUtil.getSharedPreferences(context, "BedtimeAlarm", 0).edit().putBoolean(BedtimeUtil.KEY_BEDTIME_ALARM_COMPLETED, this.hasSetBedtime).putBoolean(BedtimeUtil.KEY_SHOW_BEDTIME_BANNER, this.isShowBanner).putBoolean(BedtimeUtil.KEY_MANAGE_GUIDE_SHOWED, this.isManageGuideShowed).putInt(BedtimeUtil.KEY_SLEEP_ALARM_HOUR, this.sleepAlarmHour).putInt(BedtimeUtil.KEY_SLEEP_ALARM_MIN, this.sleepAlarmMin).putBoolean(BedtimeUtil.KEY_SLEEP_NO_DISTURBANCE, this.bedtimeDndState).putInt(BedtimeUtil.KEY_NOTIFICATION_ADV_TIME, this.bedtiemNotifyAdvTime).putBoolean(BedtimeUtil.KEY_OPEN_BEDTIME, this.isBedtimeOpen).putInt(BedtimeUtil.SP_WAKE_ALARM_HOUR, this.wakeAlarmHour).putInt(BedtimeUtil.SP_WAKE_ALARM_MINUTES, this.wakeAlarmMins).putInt(BedtimeUtil.SP_WAKE_ALARM_DAYS_OF_WEEK, this.wakeAlarmDaysOfWeek).putLong(BedtimeUtil.SP_WAKE_ALARM_ALARM_TIME, this.wakeAlarmAlertTime).putInt(BedtimeUtil.SP_WAKE_ALARM_ENABLE, this.wakeAlarmEnable).putInt(BedtimeUtil.SP_WAKE_ALARM_VIBRATE, this.wakeAlarmVibrate);
            String str = this.wakeAlarmAlert;
            if (str == null) {
                str = "";
            }
            editorPutInt.putString(BedtimeUtil.SP_WAKE_ALARM_ALERT, str).putLong(BedtimeUtil.SP_WAKE_ALARM_SKIP_TIME, this.wakeAlarmSkipTime).apply();
            SharedPreferences.Editor editorEdit = defaultSharedPreferences.edit();
            String str2 = this.bedtimeDndId;
            if (str2 == null) {
                str2 = "";
            }
            SharedPreferences.Editor editorPutString = editorEdit.putString(ZenModeUtil.KEY_ZEN_RULE_ID, str2);
            String str3 = this.bedtimeDndStartTime;
            if (str3 == null) {
                str3 = "";
            }
            SharedPreferences.Editor editorPutString2 = editorPutString.putString(ZenModeUtil.KEY_ZEN_RULE_START_TIME, str3);
            String str4 = this.bedtimeDndEndTime;
            editorPutString2.putString(ZenModeUtil.KEY_ZEN_RULE_END_TIME, str4 != null ? str4 : "").apply();
        }
        Util.hasRestored(context);
        Log.d("DC:SettingsBackup restoreToPhone end");
    }

    public JSONObject getSettingsObject() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("alarmLouder", this.alarmLouder);
            jSONObject.put("arrivingNotify", this.arrivingNotify);
            jSONObject.put("autoSilenceTime", this.autoSilenceTime);
            jSONObject.put("bedtiemNotifyAdvTime", this.bedtiemNotifyAdvTime);
            jSONObject.put("bedtimeDndEndTime", this.bedtimeDndEndTime);
            jSONObject.put("bedtimeDndId", this.bedtimeDndId);
            jSONObject.put("bedtimeDndStartTime", this.bedtimeDndStartTime);
            jSONObject.put("bedtimeDndState", this.bedtimeDndState);
            jSONObject.put("hasSetBedtime", this.hasSetBedtime);
            jSONObject.put("isBedtimeOpen", this.isBedtimeOpen);
            jSONObject.put("isManageGuideShowed", this.isManageGuideShowed);
            jSONObject.put("isShowBanner", this.isShowBanner);
            jSONObject.put("lifePost", this.lifePost);
            jSONObject.put("lifePostBackground", this.lifePostBackground);
            jSONObject.put("lifePostNews", this.lifePostNews);
            jSONObject.put("shutdownAlarm", this.shutdownAlarm);
            jSONObject.put("sleepAlarmHour", this.sleepAlarmHour);
            jSONObject.put("sleepAlarmMin", this.sleepAlarmMin);
            jSONObject.put("snoozeCount", this.snoozeCount);
            jSONObject.put("snoozeInterval", this.snoozeInterval);
            jSONObject.put("vibrateDefault", this.vibrateDefault);
            jSONObject.put("volumeBehavior", this.volumeBehavior);
            jSONObject.put("wakeAlarmAlert", this.wakeAlarmAlert);
            jSONObject.put("wakeAlarmAlertTime", this.wakeAlarmAlertTime);
            jSONObject.put("wakeAlarmDaysOfWeek", this.wakeAlarmDaysOfWeek);
            jSONObject.put("wakeAlarmEnable", this.wakeAlarmEnable);
            jSONObject.put("wakeAlarmHour", this.wakeAlarmHour);
            jSONObject.put("wakeAlarmMins", this.wakeAlarmMins);
            jSONObject.put("wakeAlarmSkipTime", this.wakeAlarmSkipTime);
            jSONObject.put("wakeAlarmVibrate", this.wakeAlarmVibrate);
            jSONObject.put("lifePostState", this.lifePostState);
            jSONObject.put("lockedShowAlarm", this.lockedShowAlarm);
            jSONObject.put("ringVolume", this.ringVolume);
            String str = this.timerAlert;
            if (str != null) {
                jSONObject.put("timerAlert", str);
            }
        } catch (Exception e) {
            Log.e("DC:SettingsBackup", "getSettingsObject error: " + e.getMessage());
        }
        return jSONObject;
    }

    public void setData(JSONObject jSONObject, Context context) {
        try {
            this.alarmLouder = jSONObject.getBoolean("alarmLouder");
            this.arrivingNotify = jSONObject.getBoolean("arrivingNotify");
            this.autoSilenceTime = jSONObject.getString("autoSilenceTime");
            this.bedtiemNotifyAdvTime = jSONObject.getInt("bedtiemNotifyAdvTime");
            this.bedtimeDndEndTime = jSONObject.getString("bedtimeDndEndTime");
            this.bedtimeDndId = jSONObject.getString("bedtimeDndId");
            this.bedtimeDndStartTime = jSONObject.getString("bedtimeDndStartTime");
            this.bedtimeDndState = jSONObject.getBoolean("bedtimeDndState");
            this.hasSetBedtime = jSONObject.getBoolean("hasSetBedtime");
            this.isBedtimeOpen = jSONObject.getBoolean("isBedtimeOpen");
            this.isManageGuideShowed = jSONObject.getBoolean("isManageGuideShowed");
            this.isShowBanner = jSONObject.getBoolean("isShowBanner");
            this.lifePost = jSONObject.getBoolean("lifePost");
            this.lifePostBackground = jSONObject.getBoolean("lifePostBackground");
            this.lifePostNews = jSONObject.getBoolean("lifePostNews");
            this.shutdownAlarm = jSONObject.getBoolean("shutdownAlarm");
            this.sleepAlarmHour = jSONObject.getInt("sleepAlarmHour");
            this.sleepAlarmMin = jSONObject.getInt("sleepAlarmMin");
            this.snoozeCount = jSONObject.getString("snoozeCount");
            this.snoozeInterval = jSONObject.getString("snoozeInterval");
            this.vibrateDefault = jSONObject.getBoolean("vibrateDefault");
            this.volumeBehavior = jSONObject.getString("volumeBehavior");
            this.wakeAlarmAlert = jSONObject.getString("wakeAlarmAlert");
            this.wakeAlarmAlertTime = jSONObject.getLong("wakeAlarmAlertTime");
            this.wakeAlarmDaysOfWeek = jSONObject.getInt("wakeAlarmDaysOfWeek");
            this.wakeAlarmEnable = jSONObject.getInt("wakeAlarmEnable");
            this.wakeAlarmHour = jSONObject.getInt("wakeAlarmHour");
            this.wakeAlarmMins = jSONObject.getInt("wakeAlarmMins");
            this.wakeAlarmSkipTime = jSONObject.getLong("wakeAlarmSkipTime");
            this.wakeAlarmVibrate = jSONObject.getInt("wakeAlarmVibrate");
            this.lifePostState = jSONObject.getBoolean("lifePostState");
            if (!jSONObject.isNull("timerAlert")) {
                this.timerAlert = jSONObject.getString("timerAlert");
            }
            if (!jSONObject.isNull("lockedShowAlarm")) {
                this.lockedShowAlarm = jSONObject.getBoolean("lockedShowAlarm");
            } else {
                this.lockedShowAlarm = FBEUtil.getDefaultSharedPreferences(context).getBoolean("locked_show_alarm_arriving_state", true);
            }
            if (!jSONObject.isNull("ringVolume")) {
                this.ringVolume = jSONObject.getInt("ringVolume");
            } else {
                this.ringVolume = -1;
            }
        } catch (Exception e) {
            Log.e("DC:SettingsBackup", "setData error: " + e.getMessage());
        }
    }

    public void setDataGoogle(JSONObject jSONObject) {
        try {
            this.alarmLouder = jSONObject.getBoolean("alarmLouder");
            this.arrivingNotify = jSONObject.getBoolean("arrivingNotify");
            this.autoSilenceTime = jSONObject.getString("autoSilenceTime");
            this.shutdownAlarm = jSONObject.getBoolean("shutdownAlarm");
            this.snoozeCount = jSONObject.getString("snoozeCount");
            this.snoozeInterval = jSONObject.getString("snoozeInterval");
            this.vibrateDefault = jSONObject.getBoolean("vibrateDefault");
            this.volumeBehavior = jSONObject.getString("volumeBehavior");
            if (!jSONObject.isNull("lockedShowAlarm")) {
                this.lockedShowAlarm = jSONObject.getBoolean("lockedShowAlarm");
            } else {
                this.lockedShowAlarm = true;
            }
            if (!jSONObject.isNull("ringVolume")) {
                this.ringVolume = jSONObject.getInt("ringVolume");
            } else {
                this.ringVolume = 0;
            }
        } catch (JSONException e) {
            Log.e("DC:SettingsBackup", "setDataGoogle error: " + e.getMessage());
        }
    }
}
