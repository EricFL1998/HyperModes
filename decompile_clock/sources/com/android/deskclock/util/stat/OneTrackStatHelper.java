package com.android.deskclock.util.stat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.text.TextUtils;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.holiday.HolidayUtil;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.settings.AlarmSettingsFragment;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.permission.UserNoticeUtil;
import com.android.deskclock.worldclock.WorldClock;
import com.xiaomi.onetrack.api.g;
import java.util.HashMap;
import java.util.Map;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class OneTrackStatHelper {
    public static final String ALARM_ADD = "";
    public static final String ALARM_ALERT_DISMISS_BY_SWIPE = "479.3.1.1.11790";
    public static final String ALARM_ARRIVING_NOTIFICATION_CLICK = "479.3.0.1.11784";
    public static final String ALARM_ARRIVING_NOTIFICATION_DISMISS_CLICK = "479.3.0.1.11785";
    public static final String ALARM_CLOSE_REPEAT = "479.1.4.1.10844";
    public static final String ALARM_EDIT = "";
    public static final String ALARM_EDIT_CANCEL_CLICK = "479.1.5.1.10816";
    public static final String ALARM_EDIT_CHANGE_RINGTONE = "479.1.5.1.11746";
    public static final String ALARM_EDIT_LABEL_CLICK = "479.1.5.1.10811";
    public static final String ALARM_EDIT_ONESHOT_CLICK = "479.1.5.1.10817";
    public static final String ALARM_EDIT_REPEAT_CLICK = "479.1.5.1.10812";
    public static final String ALARM_EDIT_RINGTONE_CLICK = "479.1.5.1.10813";
    public static final String ALARM_EDIT_SAVE_CLICK = "479.1.5.1.10815";
    public static final String ALARM_EDIT_VIBRATE_CLICK = "479.1.5.1.10814";
    public static final String ALARM_FAB_ADD_CLICK = "479.1.2.1.10413";
    public static final String ALARM_FAB_VOICE_ADD_CLICK = "479.1.2.1.10810";
    public static final String ALARM_ITEM_BUTTON_VALUE = "479.1.0.1.11615";
    public static final String ALARM_ITEM_COUNT = "";
    public static final String ALARM_ITEM_DELETE_COUNT = "479.1.4.1.10845";
    public static final String ALARM_ITEM_DIALOG_DONE_CLICK = "479.1.4.1.10809";
    public static final String ALARM_ITEM_DIALOG_MORE_CLICK = "479.1.4.1.10808";
    public static final String ALARM_NEW_EDIT_RINGTONE = "479.1.5.1.11745";
    public static final String ALARM_PAGE_TAB_CLICK = "479.1.0.1.10807";
    public static final String ALARM_PAGE_VIEW = "479.1.0.1.10563";
    public static final String ALARM_SAVE_ALERT_TIME = "";
    public static final String ALARM_SKIP_FOR_OLD_XIAOAI = "479.1.5.1.10846";
    public static final String ALARM_SNOOZED_ALERT = "479.3.0.1.11805";
    public static final String ALARM_SNOOZED_BY_CLICK = "479.3.1.1.11786";
    public static final String ALARM_SNOOZED_BY_KEY_CLICK = "479.3.1.1.11787";
    public static final String ALARM_SNOOZED_DISMISS_FROM_NOTIFICATION_CLICK = "479.3.1.1.11788";
    public static final String ALARM_SNOOZED_NOTIFICATION_SHOW = "479.3.0.1.11789";
    public static final String ALERT_DELAY = "479.3.0.1.11780";
    public static final String ALERT_FIRES = "";
    public static final String ALERT_MONITOR_ALERT = "479.3.0.1.19216";
    public static final String ALERT_MONITOR_ELAPSED_TIME = "479.3.0.1.11779";
    public static final String ALERT_MONITOR_SHOW = "";
    public static final String ALERT_SHUTDOWN_ALARM_DELAY = "479.3.0.1.11781";
    public static final String ALERT_SHUTDOWN_ALARM_EXPIRED = "479.3.0.1.11782";
    public static final String ALERT_TIME = "479.3.0.1.11783";
    public static final String BEDTIME_BANNER_CLICK = "479.8.0.1.11628";
    public static final String BEDTIME_BANNER_SHOW = "479.8.0.1.11627";
    public static final String BEDTIME_BANNER_X = "479.8.0.1.11629";
    public static final String BEDTIME_ENTER_GUIDE_PAGE = "479.8.0.1.11630";
    public static final String BEDTIME_GUIDE_CANCEL = "479.8.0.1.11635";
    public static final String BEDTIME_GUIDE_FINISH = "479.8.0.1.11632";
    public static final String BEDTIME_ITEM_CLICK = "479.8.0.1.11748";
    public static final String BEDTIME_LIFE_POST_ITEM_CLICK = "479.8.0.1.11760";
    public static final String BEDTIME_LIFE_POST_ITEM_SHOW = "479.8.0.1.11759";
    public static final String BEDTIME_NOTIFICATION_CLICK = "479.8.0.1.11758";
    public static final String BEDTIME_NOTIFICATION_SHOW = "479.8.0.1.11757";
    public static final String BEDTIME_RECORD_TO_OPEN_CLICK = "479.8.0.1.11756";
    public static final String BEDTIME_SLEEP_CHART_CLICK_VALUE = "479.8.0.1.11753";
    public static final String BEDTIME_SLEEP_CHART_HIDE = "479.8.0.1.11761";
    public static final String BEDTIME_SLEEP_CHART_SHOW_WITH_DATA = "479.8.0.1.11762";
    public static final String BEDTIME_SLEEP_HEALTH_CLICK = "479.8.0.1.11754";
    public static final String BEDTIME_SLEEP_TO_OPEN_CLICK = "479.8.0.1.11755";
    public static final String BEDTIME_TIME_EDIT_DIALOG = "479.8.0.1.11751";
    public static final String BEDTIME_TIME_EDIT_SUCCESS = "479.8.0.1.11752";
    public static final String BOOLEAN_PARAMETER = "bool_value";
    public static final String CLOCK_ADD_TIMEZONE = "479.2.0.1.11621";
    public static final String CLOCK_FAB_ADD_CLICK = "479.2.1.1.10414";
    public static final String CLOCK_ITEM_CLICK = "479.2.0.1.10820";
    public static final String CLOCK_ITEM_COUNT = "";
    public static final String CLOCK_ITEM_SLIDE_RULER = "479.2.0.1.11622";
    public static final String CLOCK_PAGE_TAB_CLICK = "479.2.0.1.10819";
    public static final String CLOCK_PAGE_VIEW = "479.2.0.1.10818";
    public static final String DYNAMIC_ALARM_WEATHER_STATE = "479.10.0.1.11806";
    public static final String EDIT_ALARM_HOUR_PICKER_SLIDE_COUNT = "479.1.5.1.11815";
    public static final String EDIT_ALARM_MIN_PICKER_SLIDE_COUNT = "479.1.5.1.11817";
    private static final String EVENT_ALARM_FIRES = "alarm_fires";
    private static final String EVENT_ALARM_SAVE = "alarm_save";
    private static final String EVENT_BEDTIME_SETTINGS = "bedtime_settings";
    private static final String EVENT_BOOL_STATISTICS = "bool_statistics";
    private static final String EVENT_CLICK = "click";
    public static final String EVENT_ENTER_PLACE_HOLDER_FRAGMENT = "";
    private static final String EVENT_NUM_STATISTICS = "num_statistics";
    private static final String EVENT_OTHER_SETTINGS = "other_settings";
    public static final String EVENT_RESOURCE_STATE = "";
    public static final String EVENT_SLEEP_SETTINGS = "";
    private static final String EVENT_STATE_SETTINGS = "state_settings";
    private static final String EVENT_STR_STATISTICS = "string_statistics";
    private static final String EVENT_TIMER_FINISH = "timer_finish";
    private static final String EVENT_TRIGGER = "trigger";
    public static final String EVENT_UPDATE_WEATHER = "update_weather";
    private static final String EVENT_VIEW = "view";
    public static final String EXTERNAL_RESOURCE_DOWNLOAD_COUNT = "";
    public static final String EXTERNAL_RESOURCE_DOWNLOAD_DIALOG_AGREE_CLICK = "";
    public static final String HOLIDAY_VERSION = "";
    public static boolean IS_ENABLED = false;
    public static final String KEY_MONITOR_ALERT = "alert_statistics";
    public static final String KEY_OPEN_DESKCLOCK = "";
    public static final String KEY_TIMER_ALARM_ALERT = "timer_alert";
    public static final String KEY_TIMER_ALARM_TYPE = "timer_type";
    public static final String KEY_TIMER_DURATION = "timer_duration";
    public static final String LIFE_POST_BACKGROUND_STATE = "";
    public static final String LIFE_POST_BACKGROUND_STATE_CLICK = "479.9.1.1.11777";
    public static final String LIFE_POST_GALLERY_BG_VIEW = "479.3.1.1.11802";
    public static final String LIFE_POST_HREF_GALLERY_CLICK = "479.3.1.1.11804";
    public static final String LIFE_POST_HREF_GALLERY_VIEW = "479.3.1.1.11803";
    public static final String LIFE_POST_NEWS_DOWNLOAD_RESULT = "479.3.1.1.11799";
    public static final String LIFE_POST_NEWS_FBE_GONE = "479.3.1.1.11801";
    public static final String LIFE_POST_NEWS_GONE = "479.3.1.1.11800";
    public static final String LIFE_POST_NEWS_ITEM_CLICK_VALUE = "479.3.1.1.11798";
    public static final String LIFE_POST_NEWS_STATE = "";
    public static final String LIFE_POST_NEWS_STATE_CLICK = "479.9.1.1.11778";
    public static final String LIFE_POST_NEWS_VISIBLE = "479.3.1.1.11797";
    public static final String LIFE_POST_PAGE_CLOSE = "479.3.1.1.11793";
    public static final String LIFE_POST_PAGE_VISIBLE = "479.3.1.1.11791";
    public static final String LIFE_POST_PAGE_VISIBLE_DURATION = "479.3.1.1.11792";
    public static final String LIFE_POST_STATE = "";
    public static final String LIFE_POST_STATE_CLICK = "479.9.1.1.11776";
    public static final String LIFE_POST_WEATHER_CLICK = "479.3.1.1.11794";
    public static final String LIFE_POST_WEATHER_INVALID = "479.3.1.1.11795";
    public static final String LIFE_POST_WEATHER_PUBLISH_TIME_DIFF = "479.3.1.1.11796";
    public static final String MENU_BEDTIME_CLICK = "479.8.0.1.11749";
    public static final String MENU_LIFE_POST_CLICK = "479.9.0.1.11775";
    public static final String MENU_SETTINGS_CLICK = "479.9.0.1.11763";
    public static final String NETWORK_PERMISSION_STATE = "";
    public static final String NEW_ALARM_HOUR_PICKER_SLIDE_COUNT = "479.1.5.1.11814";
    public static final String NEW_ALARM_MIN_PICKER_SLIDE_COUNT = "479.1.5.1.11816";
    public static final String NUMERIC_PARAMETER = "num_value";
    private static final String PARAM_ALARM_ITEM_COUNT = "alarm_count";
    private static final String PARAM_ALERT_ASCENDING = "alert_ascending";
    private static final String PARAM_ALERT_RINGTONE = "alert_ringtone";
    private static final String PARAM_ARRIVING_NOTIFY = "arriving_notify";
    private static final String PARAM_AUTO_SILENCE = "auto_silence";
    private static final String PARAM_BEDTIME_STATE = "bedtime_state";
    private static final String PARAM_CLOCK_ITEM_COUNT = "clock_count";
    private static final String PARAM_DEFAULT_RINGTONE = "default_ringtone";
    private static final String PARAM_DEFAULT_VIBRATE = "default_vibrate";
    private static final String PARAM_DURATION = "duration";
    private static final String PARAM_HOLIDAY_VERSION = "holiday_version";
    private static final String PARAM_IS_DEFAULT_DEFAULT = "is_default_ringtone";
    private static final String PARAM_LIFE_POST_BG_STATE = "life_post_bg_state";
    private static final String PARAM_LIFE_POST_NEWS_STATE = "life_post_news_state";
    private static final String PARAM_LIFE_POST_STATE = "life_post_state";
    public static final String PARAM_MONITOR_ALL_ALARM = "all_alarm_count";
    public static final String PARAM_MONITOR_ALL_ALARM_ALERT = "all_alarm_alert_count";
    public static final String PARAM_MONITOR_NORMAL_ALARM = "normal_alarm_count";
    public static final String PARAM_MONITOR_NORMAL_ALARM_ALERT = "normal_alarm_alert_count";
    public static final String PARAM_MONITOR_SHUT_DOWN_ALARM = "shut_down_alarm_count";
    public static final String PARAM_MONITOR_SHUT_DOWN_ALARM_ALERT = "shut_down_alarm_alert_count";
    private static final String PARAM_NETWORK_PERMISSION = "network_permission";
    private static final String PARAM_NOTIFICATION_ADVANCE_TIME = "notification_advance_time";
    private static final String PARAM_ONESHOT = "oneshot";
    private static final String PARAM_REPEAT_TYPE = "repeat_type";
    private static final String PARAM_RINGING_TIME = "alert_time";
    private static final String PARAM_RINGTONE = "ringtone";
    private static final String PARAM_SHUTDOWN_ALARM = "shutdown_alarm";
    private static final String PARAM_SLEEP_DISTURB = "sleep_disturb";
    private static final String PARAM_SLEEP_TIME = "sleep_time";
    private static final String PARAM_SNOOZE_COUNT = "snooze_count";
    private static final String PARAM_SNOOZE_INTERNAL = "snooze_internal";
    private static final String PARAM_TIMER_RINGTONE = "timer_ringtone";
    public static final String PARAM_UPDATE_WEATHER_NOW = "update_weather_now";
    public static final String PARAM_UPDATE_WEATHER_TIME = "update_weather_time";
    public static final String PARAM_UPDATE_WEATHER_TYPE = "update_weather_type";
    private static final String PARAM_VIBRATE = "vibrate";
    private static final String PARAM_VOLUME = "alert_volume";
    private static final String PARAM_WAKE_ALERT_ENABLE = "wake_alert_enable";
    private static final String PARAM_WAKE_TIME = "wake_time";
    public static final String RINGTONE_URL_FORMAT = "";
    public static final String SETTINGS_ALARM_ARRING_NOTIFICATION_STATE_CHANGE = "479.9.0.1.11767";
    public static final String SETTINGS_DEFAULT_RINGTONE = "";
    public static final String SETTINGS_DEFAULT_RINGTONE_CLICK = "479.9.0.1.11765";
    public static final String SETTINGS_HOLIDAY_UPDATE_CLICK = "479.9.0.1.11772";
    public static final String SETTINGS_MAX_ALERT_DURATION_CLICK = "479.9.0.1.11769";
    public static final String SETTINGS_MORE_CLICK = "479.9.0.1.11774";
    public static final String SETTINGS_POWER_BTN_CLICK = "479.9.0.1.11770";
    public static final String SETTINGS_SHUTDOWN_ALARM_STATE_CHANGE = "479.9.0.1.11764";
    public static final String SETTINGS_SNOOZE_CLICK = "479.9.0.1.11768";
    public static final String SETTINGS_TIMER_RINGTONE_CLICK = "479.9.0.1.11773";
    public static final String SETTINGS_VOLUME_ASCENDING_CHANGE = "479.9.0.1.11766";
    public static final String SETTINGS_VOLUME_CLICK = "479.9.0.1.11771";
    public static final String SLEEP_MANAGE_STATE = "";
    public static final String STOPWATCH_DURATION = "479.7.0.1.11626";
    public static final String STOPWATCH_FAB_CONTINUE = "479.7.1.1.10841";
    public static final String STOPWATCH_FAB_LAP = "479.7.1.1.10843";
    public static final String STOPWATCH_FAB_PAUSE = "479.7.1.1.10842";
    public static final String STOPWATCH_FAB_RESET = "479.7.1.1.10840";
    public static final String STOPWATCH_FAB_START = "479.7.1.1.10839";
    public static final String STOPWATCH_PAGE_TAB_CLICK = "479.7.0.1.10838";
    public static final String STOPWATCH_PAGE_VIEW = "479.7.0.1.10837";
    public static final String STRING_PARAMETER = "string_value";
    public static final String TIMER_ADD_TIMER_CLICK = "479.6.3.1.10835";
    public static final String TIMER_ADD_TIMER_SUCCESS = "479.6.3.1.10836";
    public static final String TIMER_ALERT_MUTE_STATE_CHANGE = "479.6.0.1.11625";
    public static final String TIMER_EXIST_TIMER_CLICK_VALUE = "479.6.3.1.11624";
    public static final String TIMER_EXTERNAL_RESOURCE_DIALOG_AGREE_CLICK = "";
    public static final String TIMER_EXTERNAL_RESOURCE_DIALOG_SHOW = "";
    public static final String TIMER_FAB_CANCEL = "479.6.1.1.10824";
    public static final String TIMER_FAB_CONTINUE = "479.6.1.1.10826";
    public static final String TIMER_FAB_PAUSE = "479.6.1.1.10825";
    public static final String TIMER_FAB_QUICK_SETTING = "479.6.1.1.10828";
    public static final String TIMER_FAB_START = "479.6.1.1.10823";
    public static final String TIMER_FAB_WHITE_NOISE = "479.6.1.1.10827";
    public static final String TIMER_FINISH = "";
    public static final String TIMER_NOISE_BEACH_CLICK = "479.6.2.1.10831";
    public static final String TIMER_NOISE_FIRE_CLICK = "479.6.2.1.10833";
    public static final String TIMER_NOISE_FOREST_CLICK = "479.6.2.1.10832";
    public static final String TIMER_NOISE_MUTE_CLICK = "479.6.2.1.10834";
    public static final String TIMER_NOISE_RAIN_CLICK = "479.6.2.1.10829";
    public static final String TIMER_NOISE_SUMMER_CLICK = "479.6.2.1.10830";
    public static final String TIMER_PAGE_TAB_CLICK = "479.6.0.1.10821";
    public static final String TIMER_PAGE_VIEW = "479.6.0.1.10822";
    public static final String TIMER_PERMISSION_EXTERNAL_RESOURCE_DIALOG_AGREE_CLICK = "";
    private static final String TIP_ALARM_FIRES = "479.3.0.1.10985";
    private static final String TIP_ALARM_SAVE = "479.1.5.1.10984";
    private static final String TIP_BEDTIME_SETTINGS = "479.8.0.1.11824";
    private static final String TIP_OTHER_SETTINGS = "479.0.0.0.11823";
    private static final String TIP_STATE_SETTINGS = "479.9.0.1.11825";
    private static final String TIP_TIMER_FINISH = "479.6.0.1.11747";
    public static final String TIP_UPDATE_WEATHER = "479.0.0.0.25099";
    public static final String UPDATE_FOR_LIFE_POST = "update_weather_for_life_post";
    public static final String UPDATE_FOR_WEATHER_RINGTONE = "update_for_weather_ringtone";
    public static final String WEATHER_RINGTONE_VERSION = "479.10.0.1.11807";
    public static final String XIAOMI_WEARABLE_BIND_RESULT = "479.4.0.1.11819";
    public static final String XIAOMI_WEARABLE_DISMISS = "479.4.0.1.11821";
    public static final String XIAOMI_WEARABLE_SERVICE_CONNECTED = "479.4.0.1.11818";
    public static final String XIAOMI_WEARABLE_SNOOZE = "479.4.0.1.11820";
    public static final String XIAO_HOME_BIND_RESULT = "479.4.0.1.11822";
    private static volatile IOneTrackStat sInstance;

    public static IOneTrackStat getInstance() {
        if (sInstance == null) {
            synchronized (StatHelper.class) {
                sInstance = new OneTrackStatImp();
            }
        }
        return sInstance;
    }

    public static void init(Context context) {
        boolean z = UserNoticeUtil.isNetPermissionAgreed() && !Util.isInternational() && Build.checkRegion("CN") && !ActivityManager.isUserAMonkey();
        IS_ENABLED = z;
        if (z) {
            try {
                getInstance().init(context);
            } catch (Exception e) {
                Log.e("OneTrack init error", e);
            }
            Log.i("OneTrack has been initialized");
        }
    }

    public static void trackClickEvent(String str) {
        if (!IS_ENABLED || TextUtils.isEmpty(str)) {
            return;
        }
        try {
            getInstance().track(EVENT_CLICK, str);
        } catch (Exception e) {
            Log.e("OneTrack trackClickEvent error", e);
        }
    }

    public static void trackViewEvent(String str) {
        if (!IS_ENABLED || TextUtils.isEmpty(str)) {
            return;
        }
        try {
            getInstance().track("view", str);
        } catch (Exception e) {
            Log.e("OneTrack trackViewEvent error", e);
        }
    }

    public static void trackTriggerEvent(String str) {
        if (!IS_ENABLED || TextUtils.isEmpty(str)) {
            return;
        }
        try {
            getInstance().track(EVENT_TRIGGER, str);
        } catch (Exception e) {
            Log.e("OneTrack trackTriggerEvent error", e);
        }
    }

    public static void trackNumEvent(long j, String str) {
        if (!IS_ENABLED || TextUtils.isEmpty(str)) {
            return;
        }
        try {
            HashMap map = new HashMap();
            map.put(NUMERIC_PARAMETER, Long.valueOf(j));
            getInstance().track(EVENT_NUM_STATISTICS, map, str);
        } catch (Exception e) {
            Log.e("OneTrack trackNumEvent error", e);
        }
    }

    public static void trackBoolEvent(boolean z, String str) {
        if (!IS_ENABLED || TextUtils.isEmpty(str)) {
            return;
        }
        try {
            HashMap map = new HashMap();
            map.put(BOOLEAN_PARAMETER, Boolean.valueOf(z));
            getInstance().track(EVENT_BOOL_STATISTICS, map, str);
        } catch (Exception e) {
            Log.e("OneTrack trackBoolEvent error", e);
        }
    }

    public static void trackStringEvent(String str, String str2) {
        if (!IS_ENABLED || TextUtils.isEmpty(str2)) {
            return;
        }
        try {
            HashMap map = new HashMap();
            map.put(STRING_PARAMETER, str);
            getInstance().track(EVENT_STR_STATISTICS, map, str2);
        } catch (Exception e) {
            Log.e("OneTrack trackStringEvent error", e);
        }
    }

    public static void trackMultiParamEvent(String str, Map<String, Object> map, String str2) {
        if (!IS_ENABLED || TextUtils.isEmpty(str2)) {
            return;
        }
        try {
            getInstance().track(str, map, str2);
        } catch (Exception e) {
            Log.e("OneTrack trackMultiParamEvent error", e);
        }
    }

    public static void trackAlarmFiresEvent(Context context, Alarm alarm, long j) {
        if (!IS_ENABLED || alarm == null || context == null) {
            return;
        }
        try {
            HashMap map = new HashMap();
            map.put("repeat_type", getRepeatStatString(context, alarm));
            map.put(PARAM_RINGING_TIME, String.valueOf(alarm.hour));
            map.put("duration", String.valueOf(j));
            map.put(PARAM_ALERT_RINGTONE, AlarmRingtoneUtil.getAlarmRingtoneTitle(context, alarm.alert, alarm.id));
            getInstance().track(EVENT_ALARM_FIRES, map, TIP_ALARM_FIRES);
        } catch (Exception e) {
            Log.e("OneTrack trackAlarmFiresEvent error", e);
        }
    }

    public static void recordTabView(int i) {
        if (i == 0) {
            trackViewEvent(ALARM_PAGE_VIEW);
            return;
        }
        if (i == 1) {
            trackViewEvent(CLOCK_PAGE_VIEW);
            return;
        }
        if (i == 2) {
            trackViewEvent(STOPWATCH_PAGE_VIEW);
        } else if (i == 3) {
            trackViewEvent(TIMER_PAGE_VIEW);
        } else {
            Log.e("recordTabSelected error, index=" + i);
        }
    }

    public static void recordTabClick(int i) {
        if (i == 0) {
            trackClickEvent(ALARM_PAGE_TAB_CLICK);
            return;
        }
        if (i == 1) {
            trackClickEvent(CLOCK_PAGE_TAB_CLICK);
            return;
        }
        if (i == 2) {
            trackClickEvent(STOPWATCH_PAGE_TAB_CLICK);
        } else if (i == 3) {
            trackClickEvent(TIMER_PAGE_TAB_CLICK);
        } else {
            Log.e("recordTabSelected error, index=" + i);
        }
    }

    public static void recordAlarmAction(Context context, Alarm alarm) {
        if (!IS_ENABLED || alarm == null) {
            return;
        }
        try {
            getInstance().track(EVENT_ALARM_SAVE, buildAlarmActionMap(context, alarm), null);
        } catch (Exception e) {
            Log.e("OneTrack recordAlarmAction error", e);
        }
    }

    private static Map<String, Object> buildAlarmActionMap(Context context, Alarm alarm) {
        HashMap map = new HashMap();
        if (alarm != null) {
            try {
                map.put("repeat_type", getRepeatStatString(context, alarm));
                map.put(PARAM_RINGING_TIME, Integer.valueOf((alarm.hour * 60) + alarm.minutes));
                map.put("vibrate", Boolean.valueOf(alarm.vibrate));
                map.put(PARAM_ONESHOT, Boolean.valueOf(alarm.deleteAfterUse));
                map.put(PARAM_IS_DEFAULT_DEFAULT, Boolean.valueOf(RingtoneManager.getDefaultUri(4).equals(alarm.alert)));
                map.put(PARAM_RINGTONE, AlarmRingtoneUtil.getAlarmRingtoneTitle(context, alarm.alert));
                map.put(g.ac, TIP_ALARM_SAVE);
            } catch (Exception e) {
                Log.e("OneTrack buildAlarmActionMap error", e);
            }
        }
        return map;
    }

    public static void recordTimerFinishAction(Context context) {
        if (IS_ENABLED) {
            try {
                getInstance().track(EVENT_TIMER_FINISH, buildTimerActionMap(context), TIP_TIMER_FINISH);
            } catch (Exception e) {
                Log.e("OneTrack recordAlarmAction error", e);
            }
        }
    }

    public static Map<String, Object> buildTimerActionMap(Context context) {
        HashMap map = new HashMap();
        try {
            SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(context);
            long j = defaultSharedPreferences.getLong("duration", 0L);
            int i = defaultSharedPreferences.getInt("timer_type", 0);
            boolean z = defaultSharedPreferences.getBoolean(TimerDao.KEY_MUTE, false);
            map.put("timer_duration", Long.toString(j / 1000));
            map.put("timer_type", Integer.toString(i));
            map.put("timer_alert", Boolean.toString(z));
        } catch (Exception e) {
            Log.e("OneTrack buildTimerActionMap error", e);
        }
        return map;
    }

    private static String getRepeatStatString(Context context, Alarm alarm) {
        int i;
        if (alarm == null) {
            return "";
        }
        int coded = alarm.daysOfWeek.getCoded();
        if (coded == 0) {
            i = R.string.stat_repeat_none;
        } else if (coded == 31) {
            i = R.string.stat_repeat_weekdays;
        } else if (coded == 96) {
            i = R.string.stat_repeat_weekends;
        } else if (coded == 256) {
            i = R.string.stat_repeat_legal_off_day;
        } else if (coded != 127) {
            i = coded != 128 ? R.string.stat_repeat_self_define : R.string.stat_repeat_legal_workday;
        } else {
            i = R.string.stat_repeat_everyday;
        }
        return context.getString(i);
    }

    public static Map<String, Object> trans(Map<String, String> map) {
        HashMap map2 = new HashMap();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                map2.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            Log.e("OneTrack trans error", e);
        }
        return map2;
    }

    public static void recordAllSettings(Context context) {
        recordBoolSettings(context);
        HashMap map = new HashMap();
        try {
            int netPermission = PrefUtil.getNetPermission();
            if (netPermission == -1) {
                map.put("holiday_version", "");
            } else if (netPermission == 1) {
                map.put("holiday_version", String.valueOf(HolidayUtil.getRemoteVersion(context)));
            }
            try {
                map.put(PARAM_DEFAULT_RINGTONE, AlarmRingtoneUtil.getAlarmRingtoneTitle(context, AlarmRingtoneUtil.getDefaultAlarmRingtone()));
            } catch (Exception e) {
                Log.d("statistics default alarm ringtone failed: " + e.getMessage());
            }
            SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(context);
            map.put(PARAM_TIMER_RINGTONE, AlarmRingtoneUtil.getAlarmRingtoneTitle(context, TimerDao.getTimerRingtone()));
            map.put(PARAM_SNOOZE_INTERNAL, defaultSharedPreferences.getString("snooze_duration", "10"));
            map.put(PARAM_SNOOZE_COUNT, defaultSharedPreferences.getString("snooze_repeat_count", "3"));
            map.put("auto_silence", defaultSharedPreferences.getString("auto_silence", "10"));
            map.put(PARAM_VOLUME, defaultSharedPreferences.getString("volume_button_setting", "1"));
            Cursor cursorQuery = DeskClockApp.getAppDEContext().getContentResolver().query(WorldClock.CONTENT_URI, WorldClock.PROJECTION, "cityid_new is not null", null, null);
            if (cursorQuery != null) {
                map.put(PARAM_CLOCK_ITEM_COUNT, Integer.valueOf(cursorQuery.getCount()));
            }
            Cursor cursorQuery2 = DeskClockApp.getAppDEContext().getContentResolver().query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS, null, null, null);
            if (cursorQuery2 != null) {
                map.put("alarm_count", Integer.valueOf(cursorQuery2.getCount()));
            }
        } catch (Exception e2) {
            Log.e("OneTrack recordAllSettings error", e2);
        }
        getInstance().track(EVENT_OTHER_SETTINGS, map, TIP_OTHER_SETTINGS);
    }

    public static void recordBoolSettings(Context context) {
        HashMap map = new HashMap();
        try {
            map.put(PARAM_NETWORK_PERMISSION, Boolean.valueOf(PrefUtil.getNetPermission() == 1));
            if (MiuiSdk.isSupportSleep()) {
                boolean zIsBedtimeOpen = BedtimeUtil.isBedtimeOpen(context);
                map.put("bedtime_state", Boolean.valueOf(zIsBedtimeOpen));
                if (zIsBedtimeOpen) {
                    getInstance().track(EVENT_BEDTIME_SETTINGS, buildSleepSettingsMap(context), TIP_BEDTIME_SETTINGS);
                }
            }
            SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(context);
            map.put("shutdown_alarm", Boolean.valueOf(defaultSharedPreferences.getBoolean("shutdown_alarm", true)));
            map.put("life_post_state", Boolean.valueOf(defaultSharedPreferences.getBoolean("key_open_alarm_life_post", true)));
            map.put(PARAM_LIFE_POST_BG_STATE, Boolean.valueOf(defaultSharedPreferences.getBoolean("key_open_life_post_background", false)));
            map.put(PARAM_LIFE_POST_NEWS_STATE, Boolean.valueOf(defaultSharedPreferences.getBoolean("key_open_alarm_life_post_news", true)));
            map.put(PARAM_ALERT_ASCENDING, Boolean.valueOf(defaultSharedPreferences.getBoolean("alarm_ascending_mode", true)));
            map.put(PARAM_ARRIVING_NOTIFY, Boolean.valueOf(defaultSharedPreferences.getBoolean("alarm_arriving_notification", true)));
            map.put(PARAM_DEFAULT_VIBRATE, Boolean.valueOf(defaultSharedPreferences.getBoolean(AlarmSettingsFragment.KEY_ALERT_VIBRATE, true)));
        } catch (Exception e) {
            Log.e("OneTrack recordBoolSettings error", e);
        }
        getInstance().track(EVENT_STATE_SETTINGS, map, TIP_STATE_SETTINGS);
    }

    private static Map<String, Object> buildSleepSettingsMap(Context context) {
        HashMap map = new HashMap();
        try {
            Alarm wakeAlarm = BedtimeUtil.getWakeAlarm(context);
            map.put("sleep_time", Integer.valueOf((BedtimeUtil.getSleepAlarmHour(context) * 60) + BedtimeUtil.getSleepAlarmMin(context)));
            map.put("wake_time", Integer.valueOf((wakeAlarm.hour * 60) + wakeAlarm.minutes));
            if (RingtoneManager.getDefaultUri(4).equals(wakeAlarm.alert)) {
                map.put(PARAM_IS_DEFAULT_DEFAULT, true);
            } else {
                map.put(PARAM_IS_DEFAULT_DEFAULT, false);
            }
            wakeAlarm.enabled = true;
            map.put("wake_alert_enable", "ON");
            map.put("notification_advance_time", Integer.valueOf(BedtimeUtil.getNotificationAdvTime(context)));
            map.put("sleep_disturb", Boolean.valueOf(BedtimeUtil.getDisturbanceState(context)));
            map.put("repeat_type", getRepeatStatString(context, wakeAlarm));
        } catch (Exception e) {
            Log.e("OneTrack buildSleepSettingsMap error", e);
        }
        return map;
    }
}
