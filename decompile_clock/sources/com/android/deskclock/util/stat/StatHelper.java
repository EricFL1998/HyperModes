package com.android.deskclock.util.stat;

import android.content.Context;
import com.android.deskclock.Alarm;
import com.android.deskclock.R;
import com.android.deskclock.util.Log;
import com.android.deskclock.worldclock.WorldClockEditActivity;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class StatHelper {
    private static final String APP_ID = "2882303761517449452";
    private static final String APP_KEY = "5731744975452";
    public static final String BANNER_CLICK = "banner_click";
    public static final String BANNER_SHOW = "banner_show";
    public static final String BANNER_X = "banner_x";
    public static final String CATEGORY_ALARM_COMMON = "category_alarm_common";
    public static final String CATEGORY_ALARM_PLAY = "category_alarm_play";
    public static final String CATEGORY_DESKCLOCK_COMMON = "category_deskclock_common";
    public static final String CATEGORY_LIFE_POST = "category_life_post";
    public static final String CATEGORY_SLEEP_MANAGE = "category_sleep_manage";
    private static final String CHANNEL = "miui";
    public static final String COUNT = "_count";
    public static final String EVENT_ADD_TIMER_CANCEL = "add_timer_cancel";
    public static final String EVENT_ADD_TIMER_CLICK = "add_timer_click";
    public static final String EVENT_ADD_TIMER_SUCCESS = "add_timer_success";
    public static final String EVENT_ADD_WORLD_CLOCK_COUNT = "add_world_clock_count";
    public static final String EVENT_ALARM_ACTIVE_COUNT = "alarm_tab_selected";
    public static final String EVENT_ALARM_ADD = "alarm_add";
    public static final String EVENT_ALARM_BG_GALLERY_VIEW = "life_alarm_bg_gallery_view";
    public static final String EVENT_ALARM_DISMISS_BY_NOTIFICATION = "alarm_dismiss_by_notification";
    public static final String EVENT_ALARM_DISMISS_BY_SWIPE = "alarm_dismiss_by_swipe";
    public static final String EVENT_ALARM_DYNAMIC_ALARM_WEATHER_FAIL = "alarm_dynamic_alarm_weather_fail";
    public static final String EVENT_ALARM_EDIT = "alarm_edit";
    private static final String EVENT_ALARM_FIRES = "alarm_fires";
    public static final String EVENT_ALARM_SNOOZED_BY_ALL = "alarm_snoozed_by_all";
    public static final String EVENT_ALARM_SNOOZED_BY_CLICK = "alarm_snoozed_by_click";
    public static final String EVENT_ALARM_SNOOZED_BY_KEY = "alarm_snoozed_by_key";
    public static final String EVENT_ALARM_SNOOZED_DISMISS_NOTIFICATION = "alarm_snoozed_dismiss_notification";
    public static final String EVENT_ALARM_SNOOZED_NOTIFICATION = "alarm_snoozed_notification";
    public static final String EVENT_ALERT_RINGTONE_URL_FORMAT = "alert_ringtone_url_format";
    public static final String EVENT_CANCEL_ALARM_SNOOZED_BY_BOOT_COMPLETED = "cancel_alarm_snoozed_by_boot_completed";
    public static final String EVENT_CANCEL_ALARM_SNOOZED_BY_OTHER = "cancel_alarm_snoozed_by_other";
    public static final String EVENT_CLCIK_ACUMULATE_DAY = "click_acumulate_day";
    public static final String EVENT_CLICK_ADD_WORLD_CLOCK = "click_add_world_clock";
    public static final String EVENT_CLICK_ALARM_ADD = "alarm_add_click_count";
    public static final String EVENT_CLICK_ALARM_ARRIVING_NOTIFICATION = "click_alarm_arriving_notification";
    public static final String EVENT_CLICK_ALARM_TAB_COUNT = "click_alarm_tab_count";
    public static final String EVENT_CLICK_ALARM_VOICE = "alarm_voice_click_count";
    public static final String EVENT_CLICK_BACKGROUND_SELECT = "click_background_select";
    public static final String EVENT_CLICK_BEAT_PERCENTAGE = "click_beat_percentage";
    public static final String EVENT_CLICK_CLOSE_LIFE_POST = "click_close_life_post";
    public static final String EVENT_CLICK_CLOSE_REPEAT_ALARM = "click_close_repeated_alarm";
    public static final String EVENT_CLICK_CONTINU_STOPWATCH = "click_continue_stopwatch";
    public static final String EVENT_CLICK_DEFAULT_RINGTONE = "click_default_ringtone_settings";
    public static final String EVENT_CLICK_EXTERNAL_RESOURCE_DIALOG_ALARM_POSITIVE_COUNT = "external_resource_dialog_show_alarm_positive_count";
    public static final String EVENT_CLICK_EXTERNAL_RESOURCE_DIALOG_TIMER_POSITIVE_COUNT = "external_resource_dialog_timer_positive_click_count";
    public static final String EVENT_CLICK_HOLIDAY_UPDATE_SETTINGS = "click_holiday_update_settings";
    public static final String EVENT_CLICK_LAP_STOPWATCH = "click_lap_stopwatch";
    public static final String EVENT_CLICK_LIFE_POST_LIST_COUNT = "click_life_post_list_count";
    public static final String EVENT_CLICK_MAX_ALARM_DURATION = "click_max_alarm_duration";
    public static final String EVENT_CLICK_PAUSE_STOPWATCH = "click_pause_stopwatch";
    public static final String EVENT_CLICK_PERMISSION_EXTERNAL_RESOURCE_DIALOG_TIMER_POSITIVE_COUNT = "permission_external_resource_dialog_timer_positive_click_count";
    public static final String EVENT_CLICK_RESET_STOPWATCH = "click_reset_stopwatch";
    public static final String EVENT_CLICK_SETTINGS_VOLUME_COUNT = "click_settings_volume_count";
    public static final String EVENT_CLICK_SET_ALARM_CANCEL = "click_set_alarm_cancel_count";
    public static final String EVENT_CLICK_SET_ALARM_LABEL = "click_alarm_label";
    public static final String EVENT_CLICK_SET_ALARM_PICKER_COUNT = "click_set_alarm_picker_count";
    public static final String EVENT_CLICK_SET_ALARM_REPEAT_TYPE = "click_repeat_type";
    public static final String EVENT_CLICK_SET_ALARM_RINGTONE = "click_ringtone_settings";
    public static final String EVENT_CLICK_SET_ALARM_SAVE = "click_set_alarm_save_count";
    public static final String EVENT_CLICK_SET_ALARM_VIBRATE_COUNT = "click_set_alarm_vibrate_count";
    public static final String EVENT_CLICK_SNOOZE_DURATION_SETTINGS = "click_snooze_duration_settings";
    public static final String EVENT_CLICK_START_STOPWATCH = "click_start_stopwatch";
    public static final String EVENT_CLICK_STOPWATCH_TAB_COUNT = "click_stopwatch_tab_count";
    public static final String EVENT_CLICK_TIMER_CANCEL = "click_timer_cancel";
    public static final String EVENT_CLICK_TIMER_CONTINUE = "click_timer_continue";
    public static final String EVENT_CLICK_TIMER_NOISE_BEACH_COUNT = "click_timer_noise_beach_count";
    public static final String EVENT_CLICK_TIMER_NOISE_FIRE_COUNT = "click_timer_noise_fire_count";
    public static final String EVENT_CLICK_TIMER_NOISE_FOREST_COUNT = "click_timer_noise_forest_count";
    public static final String EVENT_CLICK_TIMER_NOISE_MUTE_COUNT = "click_timer_noise_mute_count";
    public static final String EVENT_CLICK_TIMER_NOISE_RAIN_COUNT = "click_timer_noise_rain_count";
    public static final String EVENT_CLICK_TIMER_NOISE_SUMMER_COUNT = "click_timer_noise_summer_count";
    public static final String EVENT_CLICK_TIMER_PAUSE = "click_timer_pause";
    public static final String EVENT_CLICK_TIMER_START = "click_start_timer";
    public static final String EVENT_CLICK_TIMER_TAB_COUNT = "click_timer_tab_count";
    public static final String EVENT_CLICK_TIME_PICKER_FINISH = "time_picker_finish_click_count";
    public static final String EVENT_CLICK_TIME_PICKER_MORE_SETTING = "time_picker_more_setting_click_count";
    public static final String EVENT_CLICK_VOLUME_BEHAVIOR = "click_volume_button_behavior";
    public static final String EVENT_CLICK_WAKE_UP_TIME = "click_wake_up_time";
    public static final String EVENT_CLICK_WEATHER = "click_weather";
    public static final String EVENT_CLICK_WORLD_CLOCK_ITEM_COUNT = "click_world_clock_item_count";
    public static final String EVENT_CLICK_WORLD_CLOCK_TAB_COUNT = "click_world_clock_tab_count";
    public static final String EVENT_CLOSE_REPEAT_ALARM_FOREVER = "close_repeated_alarm_forever";
    public static final String EVENT_CLOSE_REPEAT_ALARM_ONECE = "close_repeated_alarm_once";
    public static final String EVENT_DELETE_MULTIPLE_ALARMS = "delete_multiple_alarms";
    public static final String EVENT_DELETE_ONE_ALARM = "delete_one_alarm";
    public static final String EVENT_DISMISS_ALARM_ARRIVING_NOTIFICATION = "dismiss_alarm_arriving_notification";
    public static final String EVENT_EDIT_ALARM_CHANGE_RINGTONE = "edit_alarm_change_ringtone";
    public static final String EVENT_EDIT_ALARM_HOUR_PICKER_SLIDE_TIMES = "edit_alarm_hour_picker_slide_times";
    public static final String EVENT_EDIT_ALARM_MIN_PICKER_SLIDE_TIMES = "edit_alarm_min_picker_slide_times";
    public static final String EVENT_EDIT_ALARM_NOT_CHANGE_RINGTONE = "edit_alarm_not_change_ringtone";
    public static final String EVENT_ENTER_PLACE_HOLDER_FRAGMENT = "enter_place_holder_fragment";
    public static final String EVENT_EXIST_TIMER_CLICK = "exist_timer_click";
    public static final String EVENT_EXTERNAL_RESOURCE_DOWNLOAD_COUNT = "external_resource_download_count";
    public static final String EVENT_LIFE_HREF_GALLERY_CLICK = "life_href_gallery_click";
    public static final String EVENT_LIFE_HREF_GALLERY_VIEW = "life_href_gallery_view";
    public static final String EVENT_LIFE_LISTEN_NEWS = "life_post_listen_news";
    public static final String EVENT_LIFE_LISTEN_NEWS_STATE = "life_post_listen_news_state";
    public static final String EVENT_LIFE_POST_ACTIVE_COUNT = "life_post_active_count";
    public static final String EVENT_LIFE_POST_ACTIVE_COUNT_WHEN_RING = "life_post_active_count_ring";
    public static final String EVENT_LIFE_POST_BACKGROUND = "life_post_background";
    public static final String EVENT_LIFE_POST_NEWS_DATA_CATCH_COUNT = "life_post_news_data_catch_count";
    public static final String EVENT_LIFE_POST_NEWS_DATA_FAIL_COUNT = "life_post_news_data_fail_count";
    public static final String EVENT_LIFE_POST_NEWS_FBE_COUNT = "life_post_news_fbe_count";
    public static final String EVENT_LIFE_POST_NEWS_GONE_COUNT = "life_post_news_gone_count";
    public static final String EVENT_LIFE_POST_NEWS_START = "life_post_news_start";
    public static final String EVENT_LIFE_POST_NEWS_VISIBLE = "life_post_news_visible";
    public static final String EVENT_LIFE_POST_SETTINGS = "life_post_settings";
    public static final String EVENT_LIFE_POST_SETTINGS_STATE = "life_post_settings_state";
    public static final String EVENT_LIFE_POST_VISIBLE = "life_post_visible";
    public static final String EVENT_MI_HOME_BIND_RESULT = "clock_mi_home_bind_result";
    public static final String EVENT_MI_WEARABLE_BIND_RESULT = "clock_mi_wearable_bind_result";
    public static final String EVENT_MI_WEARABLE_DISMISS = "clock_mi_wearable_dismiss_alarm";
    public static final String EVENT_MI_WEARABLE_SERVICE_CONNECTED = "clock_mi_wearable_service_connected";
    public static final String EVENT_MI_WEARABLE_SNOOZE = "clock_mi_wearable_snooze_alarm";
    public static final String EVENT_NETWORK_STATE = "enter_network_state";
    public static final String EVENT_NEW_ALARM_DEFAULT_RINGTONE = "new_alarm_default_ringtone";
    public static final String EVENT_NEW_ALARM_EDIT_RINGTONE = "new_alarm_edit_ringtone";
    public static final String EVENT_NEW_ALARM_HOUR_PICKER_SLIDE_TIMES = "new_alarm_hour_picker_slide_times";
    public static final String EVENT_NEW_ALARM_MIN_PICKER_SLIDE_TIMES = "new_alarm_min_picker_slide_times";
    public static final String EVENT_PERMISSION_EXTERNAL_RESOURCE_DIALOG_SHOW_TIMER_COUNT = "permission_external_resource_dialog_show_timer_count";
    public static final String EVENT_RESOURCE_STATE = "enter_resource_state";
    public static final String EVENT_SETTINGS_ACTIVE_COUNT = "settings_active_count";
    public static final String EVENT_SET_ALARM_ACTIVE_COUNT = "set_alarm_ative_count";
    public static final String EVENT_SHOW_ALARM_ARRIVING_NOTIFICATION = "show_alarm_arriving_notification";
    public static final String EVENT_SHOW_DELETE_ALARM_DIALOG = "show_delete_alarm_dialog";
    public static final String EVENT_SLEEP_SETTINGS = "sleep_settings";
    public static final String EVENT_SLIDE_RULER_VIEW_COUNT = "slide_ruler_view_count";
    public static final String EVENT_STOPWATCH_ACTIVE_COUNT = "stopwatch_tab_selected";
    public static final String EVENT_SWITCH_OFF_ALARM = "switch_off_alarm";
    public static final String EVENT_SWITCH_OFF_ALARM_ONESHOT_PREF = "switch_off_alarm_oneshot_pref";
    public static final String EVENT_SWITCH_ON_ALARM = "switch_on_alarm";
    public static final String EVENT_SWITCH_ON_ALARM_ONESHOT_PREF = "switch_on_alarm_oneshot_pref";
    public static final String EVENT_TIMER_ACTIVE_COUNT = "timer_tab_selected";
    public static final String EVENT_TIMER_ALERT = "timer_alert";
    public static final String EVENT_TIMER_ALERT_MUTE = "timer_alert_mute";
    public static final String EVENT_TIMER_QUICK_SETTING_BTN_CLICK = "timer_quick_setting_btn_click";
    public static final String EVENT_TIMEZONE_RULER_VIEW_SLIDE = "timezone_ruler_view_slide";
    public static final String EVENT_TIME_PICKER_OPEN_COUNT = "time_picker_open_count";
    public static final String EVENT_WEATHER_PROPERTY = "weather_property";
    public static final String EVENT_WHITE_NOISE_BTN_CLICK = "white_noise_btn_click";
    public static final String EVENT_WORLD_CLOCK_ACTIVE_COUNT = "world_clock_tab_selected";
    public static final String GUIDE_FINISH = "guide_finish";
    public static final String GUIDE_NO_DISTURB = "guide_no_disturb";
    public static final String GUIDE_REPEAT_TYPE = "guide_repeat_type";
    public static final String GUIDE_SLEEP_TIME = "guide_sleep_time";
    public static final String GUIDE_WAKE_TIME = "guide_wake_time";
    public static final String GUIDE_WELCOME = "guide_welcome";
    public static final String GUIDE_X = "guide_x";
    public static final String HOUR = "_hour";
    private static boolean INITIALIZED = false;
    public static boolean IS_ENABLED = false;
    public static final String KEY_ALARM_ALERT_TIME = "alarm_alert_time";
    public static final String KEY_ALARM_COUNT = "alarm_count";
    public static final String KEY_ALARM_DELAY = "alarm_delay";
    public static final String KEY_ALARM_DELAY_NEW = "alarm_delay_new";
    public static final String KEY_CLOSE_TIME = "close_time";
    private static final String KEY_CONTINUED_DURATION = "continued_duration";
    public static final String KEY_DEFAULT_ALARM_RINGTONE = "default_alarm_ringtone";
    public static final String KEY_DEFAULT_ALARM_RINGTONE_PERCENT = "default_alarm_ringtone_percent";
    public static final String KEY_HOLIDAY_VERSION = "holiday_version";
    public static final String KEY_LIFE_POST_VISIBLE_DURATION = "life_post_visible_duration";
    public static final String KEY_LIFE_POST_WEATHER_PUBLISH_TIME = "life_post_weather_publish_time";
    public static final String KEY_MONITOR_ALERT = "monitor_alert";
    public static final String KEY_MONITOR_ELAPSED_TIME = "monitor_elapsed_time";
    public static final String KEY_MONITOR_SHOW = "monitor_show";
    public static final String KEY_NOTIFICATION_ADVANCE_TIME = "notification_advance_time";
    public static final String KEY_OLD_WEATHER_RINGTONE = "old_weather_ringtone";
    private static final String KEY_ONESHOT = "oneshot";
    public static final String KEY_OPEN_DESKCLOCK = "open_deskclock";
    private static final String KEY_REPEAT_TYPE = "repeat_type";
    private static final String KEY_RINGING_TIME = "ringing_time";
    public static final String KEY_SET_ALARM_RINGTONE = "set_alarm_ringtone";
    public static final String KEY_SET_ALARM_TIME = "set_alarm_time";
    public static final String KEY_SHUT_DOWN_ALARM_DELAY = "shut_down_alarm_delay";
    public static final String KEY_SHUT_DOWN_ALARM_DELAY_NEW = "shut_down_alarm_delay_new";
    public static final String KEY_SHUT_DOWN_ALARM_EXPIRED = "shut_down_alarm_alert_expired";
    public static final String KEY_SHUT_DOWN_ALARM_EXPIRED_NEW = "shut_down_alarm_alert_expired_new";
    public static final String KEY_SKIP_ALARM_FROM_OLD_XIAOAI = "skip_alarm_from_old_xiaoai";
    public static final String KEY_SLEEP_DISTURB = "sleep_disturb";
    public static final String KEY_SLEEP_TIME = "sleep_time";
    public static final String KEY_SNOOZED_ALARM_ALERT_PLAY = "snoozed_alarm_alert_play";
    public static final String KEY_STOP_WATCH_DURATION = "stop_watch_duration";
    public static final String KEY_TIMER_ALARM_ALERT = "timer_alarm_alert";
    public static final String KEY_TIMER_ALARM_TYPE = "timer_alarm_type";
    public static final String KEY_TIMER_DURATION = "timer_duration";
    private static final String KEY_VIBRATE = "vibrate";
    public static final String KEY_VISIBLE_TIME = "visible_time";
    public static final String KEY_WAKE_ALERT_ENABLE = "wake_alert_enable";
    public static final String KEY_WAKE_ALERT_RINGTONE = "wake_alert_ringtone";
    public static final String KEY_WAKE_TIME = "wake_time";
    public static final String KEY_WEATHER_RINGTONE = "weather_ringtone";
    public static final String KEY_WEATHER_RINGTONE_VERSION = "weather_ringtone_version";
    public static final String KEY_WORLDCLOCK_COUNT = "worldclock_count";
    public static final String LIFE_SLEEP_CLICK = "life_sleep_click";
    public static final String LIFE_SLEEP_SHOW = "life_sleep_show";
    public static final String NOTIFICATION_CLICK = "notification_click";
    public static final String NOTIFICATION_SHOW = "notification_show";
    public static final String NUMERIC_PARAMETER = "numeric_parameter";
    public static final String PARAM_MONITOR_ALL_ALARM = "monitor_all_alarm";
    public static final String PARAM_MONITOR_ALL_ALARM_ALERT = "monitor_all_alarm_alert";
    public static final String PARAM_MONITOR_NORMAL_ALARM = "monitor_normal_alarm";
    public static final String PARAM_MONITOR_NORMAL_ALARM_ALERT = "monitor_normal_alarm_alert";
    public static final String PARAM_MONITOR_SHOW_ALL = "monitor_show_all";
    public static final String PARAM_MONITOR_SHOW_ALL_SUCCESS = "monitor_show_all_success";
    public static final String PARAM_MONITOR_SHOW_LOCKED = "monitor_show_locked";
    public static final String PARAM_MONITOR_SHOW_LOCKED_SUCCESS = "monitor_show_locked_success";
    public static final String PARAM_MONITOR_SHOW_UNLOCKED = "monitor_show_unlocked";
    public static final String PARAM_MONITOR_SHOW_UNLOCKED_SUCCESS = "monitor_show_unlocked_success";
    public static final String PARAM_MONITOR_SHUT_DOWN_ALARM = "monitor_shut_down_alarm";
    public static final String PARAM_MONITOR_SHUT_DOWN_ALARM_ALERT = "monitor_shut_down_alarm_alert";
    public static final String PROP_ALARM_ARRIVING = "alarm_arriving_setting";
    public static final String PROP_IS_DYNAMIC_ALARM_DEFAULT = "is_dynamic_alarm_default";
    public static final String PROP_MAX_ALARM_DURATION = "max_alarm_duration";
    public static final String PROP_POWER_OFF_ALARM = "power_off_alarm_setting";
    public static final String PROP_SNOOZE_DURATION = "snooze_duration";
    public static final String PROP_SNOOZE_DURATION_VALUE = "snooze_duration_value";
    public static final String PROP_SNOOZE_REPEAT_COUNT = "snooze_repeat_count";
    public static final String PROP_VOLUME_ASCENDING = "volume_ascending_setting";
    public static final String PROP_VOLUME_BACKGROUND_SELECT = "background_select";
    public static final String PROP_VOLUME_BEHAVIOR = "volume_button_behavior";
    public static final String SLEEP_CHART_CLICK = "sleep_chart_click";
    public static final String SLEEP_CHART_HIDE = "sleep_chart_hide";
    public static final String SLEEP_CHART_SHOW = "sleep_chart_show";
    public static final String SLEEP_CHART_SHOW_NO_DATA = "sleep_chart_show_no_data";
    public static final String SLEEP_CLICK = "sleep_click";
    public static final String SLEEP_CLICK2 = "sleep_click2";
    public static final String SLEEP_HEALTH_CLICK = "sleep_health_click";
    public static final String SLEEP_HEALTH_SHOW = "sleep_health_show";
    public static final String SLEEP_MANAGE_STATE = "sleep_manage_state";
    public static final String SLEEP_RECORD_GO = "sleep_record_go";
    public static final String SLEEP_SHOW = "sleep_show";
    public static final String SLEEP_SLEEP_GO = "sleep_sleep_go";
    public static final String SLEEP_TIME_EDIT_CANCEL = "sleep_time_edit_cancel";
    public static final String SLEEP_TIME_EDIT_CLICK = "sleep_time_edit_click";
    public static final String SLEEP_TIME_EDIT_SUCCESS = "sleep_time_edit_success";
    public static final String STRING_PARAMETER = "string_parameter";
    private static final String TAG = "DC:StatHelper";
    public static final String WAKE_TIME_EDIT_CANCEL = "wake_time_edit_cancel";
    public static final String WAKE_TIME_EDIT_CLICK = "wake_time_edit_click";
    public static final String WAKE_TIME_EDIT_SUCCESS = "wake_time_edit_success";

    public static void alarmEvent(String str) {
    }

    public static void deskclockEvent(String str) {
    }

    public static void init(Context context) {
    }

    public static void recordAlarmAction(Context context, String str, Alarm alarm) {
    }

    public static void recordAlarmFires(Context context, Alarm alarm, long j) {
    }

    public static void recordCountEvent(String str, String str2) {
    }

    public static void recordCountEvent(String str, String str2, Map<String, String> map) {
    }

    public static void recordCountEvent(String str, Map<String, String> map) {
    }

    public static void recordCountEventWithDevice(String str, String str2) {
    }

    public static void recordNumericPropertyEvent(String str, String str2, int i) {
    }

    public static void recordNumericPropertyEvent(String str, String str2, long j) {
    }

    public static void recordPageEnd(Object obj) {
    }

    public static void recordPageStart(Object obj) {
    }

    public static void recordSleepSettings(Context context) {
    }

    public static void trackEvent(String str) {
    }

    public static void trackEvent(String str, String str2) {
    }

    public static void trackEvent(String str, String str2, String str3) {
    }

    public static void updateAlarmProperties(String str, long j) {
    }

    public static void updateAlarmProperties(String str, String str2) {
    }

    public static void recordTabSelected(int i) {
        if (i == 0) {
            deskclockEvent(EVENT_ALARM_ACTIVE_COUNT);
            return;
        }
        if (i == 1) {
            deskclockEvent(EVENT_WORLD_CLOCK_ACTIVE_COUNT);
            return;
        }
        if (i == 2) {
            deskclockEvent(EVENT_STOPWATCH_ACTIVE_COUNT);
        } else if (i == 3) {
            deskclockEvent(EVENT_TIMER_ACTIVE_COUNT);
        } else {
            Log.e("recordTabSelected error, index=" + i);
        }
    }

    private static String getRepeatStatString(Context context, Alarm alarm) {
        int i;
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

    public static Map<String, String> buildLifePostCloseTimeMap(String str) {
        HashMap map = new HashMap();
        map.put(KEY_CLOSE_TIME, str);
        return map;
    }

    public static Map<String, String> buildLifePostTimeMap(Alarm alarm) {
        HashMap map = new HashMap();
        map.put(KEY_VISIBLE_TIME, String.valueOf(alarm.hour));
        return map;
    }

    public static void recordWeatherPublishTimeStringProperty(String str, String str2, long j) {
        String str3;
        if (IS_ENABLED) {
            int iCurrentTimeMillis = (int) ((System.currentTimeMillis() - j) / 60000);
            if (iCurrentTimeMillis <= 30 && iCurrentTimeMillis >= 0) {
                str3 = "[0, 30]";
            } else if (iCurrentTimeMillis <= 60 && iCurrentTimeMillis > 30) {
                str3 = "(30, 60]";
            } else if (iCurrentTimeMillis <= 180 && iCurrentTimeMillis > 60) {
                str3 = "(60, 180]";
            } else if (iCurrentTimeMillis <= 180) {
                str3 = WorldClockEditActivity.LOCAL_CITY_ID;
            } else {
                str3 = ">180";
            }
            trackEvent(str, str2, str3);
        }
    }

    public static Map<String, String> buildSnoozeDurationMap(String str, String str2) {
        HashMap map = new HashMap();
        map.put(PROP_SNOOZE_DURATION_VALUE, str);
        map.put("snooze_repeat_count", str2);
        return map;
    }

    public static void recordTimeStringProperty(String str, String str2, long j) {
        String str3;
        if (IS_ENABLED) {
            int i = (int) (j / 1000);
            if (i <= 5 && i >= 0) {
                str3 = "[0, 5]";
            } else if (i <= 10 && i > 5) {
                str3 = "(5, 10]";
            } else if (i <= 30 && i > 10) {
                str3 = "(10, 30]";
            } else if (i <= 60 && i > 30) {
                str3 = "(30, 60]";
            } else if (i <= 180 && i > 60) {
                str3 = "(60, 180]";
            } else if (i <= 180) {
                str3 = WorldClockEditActivity.LOCAL_CITY_ID;
            } else {
                str3 = ">180";
            }
            trackEvent(str, str2, str3);
        }
    }
}
