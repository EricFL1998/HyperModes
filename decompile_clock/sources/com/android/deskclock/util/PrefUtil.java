package com.android.deskclock.util;

import android.content.Context;
import com.android.deskclock.DeskClockApp;

/* JADX INFO: loaded from: classes.dex */
public class PrefUtil extends PrefImpl {
    private static final String BOOT_TIME = "pref_boot_time";
    public static final long DEFAULT_BOOT_TIME = -1;
    public static final long DEFAULT_SHUT_DOWN_TIME = -1;
    private static final String INTERNET_PERMISSION = "internet_permission";
    private static final String KEY_ACCEPT_NET_PERMISSION = "key_accept_net_permission";
    public static final String KEY_ALARM_MONITOR_VERSION = "alarm_monitor_version";
    private static final String KEY_GALLERY_UPDATE_TIME = "key_gallery_update_time";
    private static final String KEY_LIFE_POST_CANCEL_TIME = "key_life_post_cancel_time";
    private static final String KEY_MIUI_RESOURCE_VERSION = "miui_resource_version";
    private static final String KEY_MONITOR_STATUS = "monitor_status";
    private static final String KEY_NEWS_UPDATE_TIME = "key_news_update_time";
    private static final String KEY_RINGTONE_MODIFY_TO_WEATHER = "is_modify_default_ringtone";
    private static final String KEY_RINGTONE_MODIFY_TO_WEEK = "is_modify_default_ringtone_to_week";
    private static final String KEY_TIME_SET_ABSOLUTE = "time_set_absolute";
    private static final String KEY_TIME_SET_RELATIVE = "time_set_relative";
    private static final String KEY_UPDATE_DIRECTLY = "update_directly";
    private static final String KEY_WEATHER_RINGTONE_INTRODUCE = "weather_ringtone_introduce";
    private static final String KEY_WEEK_RINGTONE_INTRODUCE = "week_ringtone_introduce";
    private static final String KEY_WEEK_RINGTONE_RECOMMEND_IN_CLOCK = "week_ringtone_recommend_in_clock";
    private static final String KEY_WEEK_RINGTONE_RECOMMEND_IN_EDIT = "week_ringtone_recommend_in_edit";
    public static final int NET_PERMISSION_AGREE = 1;
    public static final int NET_PERMISSION_IDLE = 0;
    public static final int NET_PERMISSION_REJECT = -1;
    public static final String PREF_LAST_ALERT_TIMESTAMP = "last_alert_timestamp";
    private static final String SHUT_DOWN_TIME = "pref_shut_down_time";
    public static final int VALUE_MONITOR_ERROR = 2;
    public static final int VALUE_MONITOR_IDLE = 0;
    public static final int VALUE_MONITOR_PREPARE = 1;
    public static final long VALUE_UPDATE_TIME = -1;

    public static void setShutDownTime(long j) {
        putLong(DeskClockApp.getAppDEContext(), SHUT_DOWN_TIME, j);
    }

    public static long getShutDownTime() {
        return getLong(DeskClockApp.getAppDEContext(), SHUT_DOWN_TIME, -1L);
    }

    public static void setBootTime(long j) {
        putLong(DeskClockApp.getAppDEContext(), BOOT_TIME, j);
    }

    public static long getBootTime() {
        return getLong(DeskClockApp.getAppDEContext(), BOOT_TIME, -1L);
    }

    public static void setOldNetPermission(boolean z) {
        putBoolean(DeskClockApp.getAppDEContext(), KEY_ACCEPT_NET_PERMISSION, z);
    }

    public static boolean getOldNetPermission() {
        return getBoolean(DeskClockApp.getAppDEContext(), KEY_ACCEPT_NET_PERMISSION, false);
    }

    public static void setNetPermission(boolean z) {
        putInt(DeskClockApp.getAppDEContext(), INTERNET_PERMISSION, z ? 1 : -1);
    }

    public static int getNetPermission() {
        return getInt(DeskClockApp.getAppDEContext(), INTERNET_PERMISSION, 0);
    }

    public static int getNetPermission(Context context) {
        return getInt(context, INTERNET_PERMISSION, 0);
    }

    public static void setMonitorStatus(int i) {
        putInt(DeskClockApp.getAppDEContext(), KEY_MONITOR_STATUS, i);
    }

    public static int getMonitorStatus() {
        return getInt(DeskClockApp.getAppDEContext(), KEY_MONITOR_STATUS, 0);
    }

    public static void setRelativeTimeSet(long j) {
        putLong(DeskClockApp.getAppDEContext(), KEY_TIME_SET_RELATIVE, j);
    }

    public static long getRelativeTimeSet() {
        return getLong(DeskClockApp.getAppDEContext(), KEY_TIME_SET_RELATIVE, 0L);
    }

    public static void setAbsoluteTimeSet(long j) {
        putLong(DeskClockApp.getAppDEContext(), KEY_TIME_SET_ABSOLUTE, j);
    }

    public static long getAbsoluteTimeSet() {
        return getLong(DeskClockApp.getAppDEContext(), KEY_TIME_SET_ABSOLUTE, 0L);
    }

    public static void setLifePostCloseTime(long j) {
        putLong(DeskClockApp.getAppDEContext(), KEY_LIFE_POST_CANCEL_TIME, j);
    }

    public static long getLifePostCloseTime() {
        return getLong(DeskClockApp.getAppDEContext(), KEY_LIFE_POST_CANCEL_TIME, -1L);
    }

    public static void setNewsUpdateTime(long j) {
        putLong(DeskClockApp.getAppDEContext(), KEY_NEWS_UPDATE_TIME, j);
    }

    public static long getNewsUpdateTime() {
        return getLong(DeskClockApp.getAppDEContext(), KEY_NEWS_UPDATE_TIME, -1L);
    }

    public static void setGalleryUpdateTime(long j) {
        putLong(DeskClockApp.getAppDEContext(), KEY_GALLERY_UPDATE_TIME, j);
    }

    public static long getGalleryUpdateTime() {
        return getLong(DeskClockApp.getAppDEContext(), KEY_GALLERY_UPDATE_TIME, -1L);
    }

    public static void setMiuiResourceVersion(int i) {
        putInt(DeskClockApp.getAppDEContext(), KEY_MIUI_RESOURCE_VERSION, i);
    }

    public static int getMiuiResourceVersion() {
        return getInt(DeskClockApp.getAppDEContext(), KEY_MIUI_RESOURCE_VERSION, 0);
    }

    public static void setRingtoneModifiedToWeather(boolean z) {
        putBoolean(DeskClockApp.getAppDEContext(), KEY_RINGTONE_MODIFY_TO_WEATHER, z);
    }

    public static boolean hasRingtoneModifiedToWeather() {
        return getBoolean(DeskClockApp.getAppDEContext(), KEY_RINGTONE_MODIFY_TO_WEATHER, false);
    }

    public static void setRingtoneModifiedToWeek(boolean z) {
        putBoolean(DeskClockApp.getAppDEContext(), KEY_RINGTONE_MODIFY_TO_WEEK, z);
    }

    public static boolean hasRingtoneModifiedToWeek() {
        return getBoolean(DeskClockApp.getAppDEContext(), KEY_RINGTONE_MODIFY_TO_WEEK, false);
    }

    public static void setUpdateDirectly(boolean z) {
        putBoolean(DeskClockApp.getAppDEContext(), KEY_UPDATE_DIRECTLY, z);
    }

    public static boolean isUpdateDirectly() {
        return getBoolean(DeskClockApp.getAppDEContext(), KEY_UPDATE_DIRECTLY, false);
    }

    public static void setWeatherRingtoneIntroduce(boolean z) {
        putBoolean(DeskClockApp.getAppDEContext(), KEY_WEATHER_RINGTONE_INTRODUCE, z);
    }

    public static boolean isWeatherRingtoneIntroduce() {
        return getBoolean(DeskClockApp.getAppDEContext(), KEY_WEATHER_RINGTONE_INTRODUCE, false);
    }

    public static void setWeekRingtoneIntroduce(boolean z) {
        putBoolean(DeskClockApp.getAppDEContext(), KEY_WEEK_RINGTONE_INTRODUCE, z);
    }

    public static boolean isWeekRingtoneIntroduce() {
        return getBoolean(DeskClockApp.getAppDEContext(), KEY_WEEK_RINGTONE_INTRODUCE, false);
    }

    public static void setRecentAlarmAlertTime(long j) {
        putLong(DeskClockApp.getAppDEContext(), PREF_LAST_ALERT_TIMESTAMP, j);
    }

    public static long getRecentAlarmAlertTime() {
        return getLong(DeskClockApp.getAppDEContext(), PREF_LAST_ALERT_TIMESTAMP, 0L);
    }

    public static void setMonitorVersion(int i) {
        putInt(DeskClockApp.getAppDEContext(), KEY_ALARM_MONITOR_VERSION, i);
    }

    public static int getMonitorVersion(int i) {
        return getInt(DeskClockApp.getAppDEContext(), KEY_ALARM_MONITOR_VERSION, i);
    }

    public static void setWeekRingtoneRecommendInClock(boolean z) {
        putBoolean(DeskClockApp.getAppDEContext(), KEY_WEEK_RINGTONE_RECOMMEND_IN_CLOCK, z);
    }

    public static boolean isWeekRingtoneRecommendInClock() {
        return getBoolean(DeskClockApp.getAppDEContext(), KEY_WEEK_RINGTONE_RECOMMEND_IN_CLOCK, false);
    }

    public static void setWeekRingtoneRecommendInEdit(boolean z) {
        putBoolean(DeskClockApp.getAppDEContext(), KEY_WEEK_RINGTONE_RECOMMEND_IN_EDIT, z);
    }

    public static boolean isWeekRingtoneRecommendInEdit() {
        return getBoolean(DeskClockApp.getAppDEContext(), KEY_WEEK_RINGTONE_RECOMMEND_IN_EDIT, false);
    }
}
