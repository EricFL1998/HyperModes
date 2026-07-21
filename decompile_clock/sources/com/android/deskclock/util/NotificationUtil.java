package com.android.deskclock.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;
import com.android.deskclock.Alarm;
import com.android.deskclock.AlarmReceiver;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.R;
import com.android.deskclock.alarm.alert.AlarmAlertFullScreenActivity;
import com.android.deskclock.alarm.bedtime.BedtimeManageActivity;
import com.android.deskclock.compat.ClockCompat;
import com.android.deskclock.stopwatch.StopWatchService;
import com.android.deskclock.stopwatch.Stopwatch;
import com.android.deskclock.timer.Timer;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.xiaomi.onetrack.api.a;
import java.util.Calendar;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class NotificationUtil {
    private static final int ACTION_INFO_TYPE = 1;
    public static final int ALARM_DATA_INVALID_ID = -6;
    private static final int ANIM_ICON_INFO_TYPE_SHADER = 2;
    private static final int ANIM_ICON_INFO_TYPE_STATIC = 0;
    private static final int ANIM_ICON_INFO_TYPE_VIDEO = 1;
    public static final int ARRIVING_ALARM_NOTIFICATION_ID = -3;
    private static final String CHANNEL_ID_ALARM = "channel_id_deskclock_alarm";
    private static final String CHANNEL_ID_APP = "channel_id_deskclock_alarm";
    private static final String CHANNEL_ID_SLEEP = "channel_id_deskclock_sleep";
    private static final String CHANNEL_ID_SNOOZE = "channel_id_deskclock_snooze";
    private static final String CHANNEL_ID_STOPWATCH = "channel_id_deskclock_stopwatch";
    private static final String CHANNEL_ID_TIMER = "channel_id_deskclock_timer";
    public static final String CTS_START_TIMER_TEST_TITLE = "Start Timer Test";
    private static final String GROUP_ID_SNOOZE = "group_id_deskclock_snooze";
    private static final int IMAGE_TEXT_INFO_LEFT_TYPE = 1;
    private static final int ISLAND_PROPERTY_INFO_DISPLAY = 1;
    private static final int ISLAND_PROPERTY_OPERATION = 2;
    private static final int ISLAND_PROPERTY_SHORT_TIME_ATTENTION = 0;
    private static final int PROTOCOL_VERSION = 1;
    public static final int RECOMMEND_UPDATE_ID = -5;
    public static final int SLEEP_ARRIVING_ALARM_NOTIFICATION_ID = -7;
    private static final int SLEEP_NOTIFICATION_DISPLAY_TIME = 600000;
    public static final String STATUS_BAR_CONTENT_REMOTE = "miui.focus.rvBar";
    public static final String STATUS_BAR_CONTENT_REMOTE_NIGHT = "miui.focus.rvBarNight";
    public static final int STOPWATCH_NOTIFICATION_ID = -8;
    private static final String TAG = "DC:Notification";
    public static final int TIMER_ALARM_RUNNING_ID = -4;
    private static final String TIMER_TIME_FORMAT = "%02d:%02d";
    private static final String TIMER_TIME_FORMAT_HOUR = "%02d:%02d:%02d";
    public static final int TIMER_TYPE_PAUSE = -2;
    public static final int TIMER_TYPE_START = -1;
    private static final int TWELVE_HOURS_IN_SECONDS = 43200;

    public static Notification getAlarmAlertNotification(Context context, Alarm alarm) {
        PendingIntent pendingIntent;
        Notification notificationBuildNotificationWithAlarmAlert;
        createAlarmChannel(context);
        AlarmHelper.reset24HourMode(context);
        String labelOrDefault = alarm.getLabelOrDefault(context);
        String labelOrDefault2 = alarm.getLabelOrDefault(context);
        String string = context.getString(R.string.alarm_alert_now, AlarmHelper.formatAlarmTime(context, alarm));
        String str = (String) AlarmHelper.formatAlarmNotificationTime(context, alarm);
        Intent intent = new Intent(context, (Class<?>) AlarmAlertFullScreenActivity.class);
        intent.putExtra(AlarmHelper.ALARM_INTENT_EXTRA, alarm);
        intent.setFlags(268697600);
        PendingIntent activityAsUser = getActivityAsUser(context, alarm.id, intent);
        String string2 = context.getResources().getString(R.string.close_alarm_new);
        Intent intent2 = new Intent(AlarmHelper.ACTION_ALARM_DISMISS);
        intent2.putExtra(AlarmHelper.ALARM_INTENT_EXTRA, alarm);
        intent2.setPackage(context.getPackageName());
        intent2.addFlags(268435456);
        PendingIntent broadcast = getBroadcast(context, alarm.id, intent2);
        String string3 = context.getResources().getString(R.string.alarm_alert_snooze_button);
        Intent intent3 = new Intent(AlarmHelper.ACTION_ALARM_SNOOZE);
        intent3.putExtra(AlarmHelper.ALARM_INTENT_EXTRA, alarm);
        intent3.setPackage(context.getPackageName());
        intent3.addFlags(268435456);
        PendingIntent broadcast2 = getBroadcast(context, alarm.id, intent3);
        if (Build.VERSION.SDK_INT > 29) {
            pendingIntent = activityAsUser;
            notificationBuildNotificationWithAlarmAlert = buildNotificationWithAlarmAlert(context, "channel_id_deskclock_alarm", string, str, null, labelOrDefault2, activityAsUser, string2, broadcast, string3, broadcast2, true, alarm.time);
        } else {
            pendingIntent = activityAsUser;
            notificationBuildNotificationWithAlarmAlert = buildNotificationWithAlarmAlert(context, "channel_id_deskclock_alarm", string, str, labelOrDefault, labelOrDefault2, pendingIntent, string2, broadcast, string3, broadcast2, true, alarm.time);
        }
        notificationBuildNotificationWithAlarmAlert.flags |= 1;
        notificationBuildNotificationWithAlarmAlert.defaults |= 4;
        notificationBuildNotificationWithAlarmAlert.fullScreenIntent = pendingIntent;
        clearAlarmAlertNotification(context, alarm.id);
        return notificationBuildNotificationWithAlarmAlert;
    }

    public static void showSnoozeNotification(Context context, Alarm alarm, int i) {
        String string;
        PendingIntent broadcast;
        createSnoozeChannel(context);
        Calendar calendar = Calendar.getInstance();
        long j = 60000 * i;
        calendar.setTimeInMillis(System.currentTimeMillis() + j);
        String string2 = context.getString(R.string.alarm_notify_snooze_label, AlarmHelper.formatTime(context, calendar));
        String string3 = context.getString(R.string.snooze_alarm_alert, AlarmHelper.formatTime(context, calendar));
        String time = AlarmHelper.formatTime(context, calendar);
        long jCurrentTimeMillis = System.currentTimeMillis() + j;
        String str = (String) AlarmHelper.formatAlarmTime(context, AlarmHelper.calculateSnoozeAlarmNextAlert(context));
        String time2 = AlarmHelper.formatTime(context, calendar);
        String labelOrDefault = alarm.getLabelOrDefault(context);
        boolean z = Settings.System.getInt(context.getContentResolver(), "notification_focus_protocol", 0) > 0;
        Log.d("DC:NotificationUtil", "hasFocusFeature: " + z);
        android.util.Log.d("DC:NotificationUtil", "hasFocusPermission: " + hasFocusPermission(context));
        Intent intent = new Intent(context, (Class<?>) AlarmReceiver.class);
        intent.setAction(AlarmHelper.ACTION_SNOOZE_CANCEL);
        intent.putExtra(AlarmHelper.ALARM_INTENT_EXTRA, alarm);
        intent.addFlags(268435456);
        if (z && hasFocusPermission(context)) {
            string = context.getString(R.string.click_to_view_alarm);
            Intent intent2 = new Intent(context, (Class<?>) DeskClockTabActivity.class);
            intent2.putExtra(Util.NAVIGATION_TAB, 0);
            broadcast = getActivity(context, alarm.id, intent2);
        } else {
            string = context.getString(R.string.click_to_cancel_alarm);
            broadcast = getBroadcast(context, alarm.id, intent);
        }
        Notification notificationBuildNotificationWithSnooze = buildNotificationWithSnooze(context, CHANNEL_ID_SNOOZE, jCurrentTimeMillis, str, time2, string2, string3, string, broadcast, labelOrDefault, getBroadcast(context, alarm.id, intent), true, System.currentTimeMillis(), time);
        setEnableFloat(notificationBuildNotificationWithSnooze, false);
        setEnableKeyguard(notificationBuildNotificationWithSnooze, false);
        Log.d("showSnoozeNotification");
        notifyNotification(context, notificationBuildNotificationWithSnooze, alarm.id);
        StatHelper.alarmEvent(StatHelper.EVENT_ALARM_SNOOZED_NOTIFICATION);
        OneTrackStatHelper.trackTriggerEvent(OneTrackStatHelper.ALARM_SNOOZED_NOTIFICATION_SHOW);
    }

    private static boolean hasFocusPermission(Context context) {
        try {
            Uri uri = Uri.parse("content://miui.statusbar.notification.public");
            Bundle bundle = new Bundle();
            bundle.putString("package", context.getPackageName());
            return context.getContentResolver().call(uri, "canShowFocus", (String) null, bundle).getBoolean("canShowFocus", false);
        } catch (Exception e) {
            Log.e("DC:NotificationUtil", e);
            return false;
        }
    }

    public static Notification getAlarmArrivingNotification(Context context, Alarm alarm) {
        Notification notificationBuildNotificationWithAlarmArriving;
        createAlarmChannel(context);
        AlarmHelper.reset24HourMode(context);
        CharSequence alarmTime = AlarmHelper.formatAlarmTime(context, alarm);
        String str = String.format(context.getResources().getString(R.string.upcoming_alarm), alarmTime);
        String str2 = String.format(context.getResources().getString(R.string.upcoming_alarm_new), alarmTime);
        CharSequence text = context.getText(R.string.upcoming_alarm_hint);
        String labelOrDefault = alarm.getLabelOrDefault(context);
        if (labelOrDefault == null || labelOrDefault.equals(context.getString(R.string.default_label))) {
            labelOrDefault = context.getString(R.string.alarm_list_title);
        }
        String string = context.getResources().getString(R.string.close_alarm);
        Intent intent = new Intent(context, (Class<?>) AlarmReceiver.class);
        intent.setAction(AlarmHelper.ACTION_ALARM_CANCEL);
        intent.putExtra(AlarmHelper.ALARM_INTENT_EXTRA, alarm);
        PendingIntent broadcast = getBroadcast(context, alarm.id, intent);
        Intent intent2 = new Intent(context, (Class<?>) DeskClockTabActivity.class);
        intent2.putExtra(Util.NAVIGATION_TAB, 0);
        intent2.putExtra(Util.INTENT_FROM, StatHelper.EVENT_CLICK_ALARM_ARRIVING_NOTIFICATION);
        PendingIntent activity = getActivity(context, alarm.id, intent2);
        if (Build.VERSION.SDK_INT > 29) {
            notificationBuildNotificationWithAlarmArriving = buildNotificationWithAlarmArriving(context, "channel_id_deskclock_alarm", str2, labelOrDefault, activity, string, broadcast, false, alarm.time);
        } else {
            notificationBuildNotificationWithAlarmArriving = buildNotificationWithAlarmArriving(context, "channel_id_deskclock_alarm", str, text, activity, string, broadcast, false, alarm.time);
        }
        setEnableFloat(notificationBuildNotificationWithAlarmArriving, false);
        return notificationBuildNotificationWithAlarmArriving;
    }

    public static void showAlarmArrivingNotification(Context context, Alarm alarm) {
        Log.f("DC:NotificationUtil", "showAlarmArrivingNotification");
        notifyNotification(context, getAlarmArrivingNotification(context, alarm), -3);
        StatHelper.alarmEvent(StatHelper.EVENT_SHOW_ALARM_ARRIVING_NOTIFICATION);
    }

    public static Notification getTimerAlertNotification(Context context, Alarm alarm) {
        Notification notificationBuildNotificationWithTimerAlert;
        AlarmHelper.setMiuiWallpaperManager(Util.getMiuiWallpaperManager());
        createTimerChannel(context);
        CharSequence timerDuration = Util.formatTimerDuration(context);
        String string = context.getString(R.string.timer_start_alarm);
        String timerLabelOrDefault = alarm.getTimerLabelOrDefault(context);
        Intent intent = new Intent(context, (Class<?>) AlarmAlertFullScreenActivity.class);
        intent.putExtra(AlarmHelper.ALARM_INTENT_EXTRA, alarm);
        intent.setFlags(268697600);
        PendingIntent activityAsUser = getActivityAsUser(context, alarm.id, intent);
        String string2 = context.getString(R.string.timer_got_it_new);
        Intent intent2 = new Intent(AlarmHelper.ACTION_TIMER_DISMISS);
        intent2.setPackage(context.getPackageName());
        intent2.addFlags(268435456);
        PendingIntent broadcast = getBroadcast(context, alarm.id, intent2);
        if (Build.VERSION.SDK_INT > 29) {
            notificationBuildNotificationWithTimerAlert = buildNotificationWithTimerAlert(context, CHANNEL_ID_TIMER, string, timerDuration, activityAsUser, string2, broadcast, true, System.currentTimeMillis());
        } else {
            notificationBuildNotificationWithTimerAlert = buildNotificationWithTimerAlert(context, CHANNEL_ID_TIMER, string, timerLabelOrDefault, activityAsUser, string2, broadcast, true, System.currentTimeMillis());
        }
        notificationBuildNotificationWithTimerAlert.flags |= 1;
        notificationBuildNotificationWithTimerAlert.defaults |= 4;
        notificationBuildNotificationWithTimerAlert.fullScreenIntent = activityAsUser;
        clearTimerAlertNotification(context, alarm.id);
        return notificationBuildNotificationWithTimerAlert;
    }

    public static Notification getTimerRunningNotification(Context context, Timer timer, boolean z, boolean z2) {
        String string;
        String string2;
        RemoteViews remoteViews;
        RemoteViews remoteViews2;
        String time;
        String string3;
        createTimerChannel(context);
        int id = timer.getId();
        long time2 = timer.getTime();
        try {
            boolean zIsToday = Util.isToday(time2);
            Date date = new Date(time2);
            if (zIsToday) {
                string3 = context.getString(R.string.timer_end_time_format_today);
            } else {
                string3 = context.getString(R.string.timer_end_time_format);
            }
            String date2 = TimeUtil.formatDate(string3, date, null);
            if (z) {
                string = String.format(context.getResources().getString(R.string.timer_end_time), date2);
            } else {
                string = context.getString(R.string.timer_is_paused);
            }
        } catch (Exception e) {
            Log.e("showTimerRunningNotification String.format is crashed", e);
            string = "";
        }
        String str = string;
        if (z) {
            string2 = context.getResources().getString(R.string.timer_running);
        } else {
            string2 = context.getResources().getString(R.string.timer_can_continue_in_timer);
        }
        String str2 = string2;
        Intent intent = new Intent(context, (Class<?>) DeskClockTabActivity.class);
        intent.putExtra(Util.NAVIGATION_TAB, 3);
        PendingIntent activity = getActivity(context, id, intent);
        CharSequence text = context.getText(R.string.timer_cancel_hint);
        Intent intent2 = new Intent(TimerDao.ACTION_TIMER_CANCEL);
        intent2.setPackage(context.getPackageName());
        intent2.addFlags(268435456);
        PendingIntent broadcast = getBroadcast(context, id, intent2);
        Intent intent3 = new Intent(TimerDao.ACTION_TIMER_PAUSE);
        intent3.setPackage(context.getPackageName());
        intent3.addFlags(268435456);
        PendingIntent broadcast2 = getBroadcast(context, id, intent3);
        Intent intent4 = new Intent(TimerDao.ACTION_TIMER_CONTINUE);
        intent4.setPackage(context.getPackageName());
        intent4.addFlags(268435456);
        Notification notificationBuildNotificationWithMiuiFocusAction = buildNotificationWithMiuiFocusAction(context, CHANNEL_ID_TIMER, str, str2, text, activity, broadcast, broadcast2, getBroadcast(context, id, intent4), true, timer, z, z2);
        long jCurrentTimeMillis = (time2 - System.currentTimeMillis()) + 900;
        Log.d(TAG, "time: " + jCurrentTimeMillis);
        if (z) {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.timer_status_bar_layout);
            remoteViews2 = new RemoteViews(context.getPackageName(), R.layout.timer_status_bar_layout_night);
            Log.d(TAG, "getTimerRunningNotification: ");
            remoteViews.setChronometerCountDown(R.id.timer_display, true);
            remoteViews2.setChronometerCountDown(R.id.timer_display, true);
            remoteViews.setChronometer(R.id.timer_display, SystemClock.elapsedRealtime() + jCurrentTimeMillis, TIMER_TIME_FORMAT_HOUR, true);
            remoteViews2.setChronometer(R.id.timer_display, SystemClock.elapsedRealtime() + jCurrentTimeMillis, TIMER_TIME_FORMAT_HOUR, true);
        } else {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.timer_status_bar_layout_pause);
            remoteViews2 = new RemoteViews(context.getPackageName(), R.layout.timer_status_bar_layout_night_pause);
            long j = jCurrentTimeMillis / AlarmHelper.ARRIVING_ALARM_DURATION;
            long j2 = (jCurrentTimeMillis % AlarmHelper.ARRIVING_ALARM_DURATION) / 60000;
            long j3 = (jCurrentTimeMillis % 60000) / 1000;
            if (j == 0) {
                time = Util.formatTime(TIMER_TIME_FORMAT, Long.valueOf(j2), Long.valueOf(j3));
            } else {
                time = Util.formatTime(TIMER_TIME_FORMAT_HOUR, Long.valueOf(j), Long.valueOf(j2), Long.valueOf(j3));
            }
            Log.d(TAG, "statusTimerDisplay: " + time);
            remoteViews.setTextViewText(R.id.timer_display, time);
            remoteViews2.setTextViewText(R.id.timer_display, time);
            remoteViews.setContentDescription(R.id.timer_display, Util.formatTimerDuration(DeskClockApp.getAppDEContext(), jCurrentTimeMillis, R.array.time));
            remoteViews2.setContentDescription(R.id.timer_display, Util.formatTimerDuration(DeskClockApp.getAppDEContext(), jCurrentTimeMillis, R.array.time));
        }
        notificationBuildNotificationWithMiuiFocusAction.extras.putParcelable(STATUS_BAR_CONTENT_REMOTE, remoteViews);
        notificationBuildNotificationWithMiuiFocusAction.extras.putParcelable(STATUS_BAR_CONTENT_REMOTE_NIGHT, remoteViews2);
        notificationBuildNotificationWithMiuiFocusAction.icon = R.drawable.timer_notifi_icon;
        setEnableFloat(notificationBuildNotificationWithMiuiFocusAction, false);
        return notificationBuildNotificationWithMiuiFocusAction;
    }

    /* JADX WARN: Code duplicated, block: B:13:0x00a5  */
    /* JADX WARN: Code duplicated, block: B:62:0x040b  */
    /* JADX WARN: Code duplicated, block: B:63:0x042e  */
    private static Notification buildNotificationWithMiuiFocusAction(Context context, String str, CharSequence charSequence, String str2, CharSequence charSequence2, PendingIntent pendingIntent, PendingIntent pendingIntent2, PendingIntent pendingIntent3, PendingIntent pendingIntent4, boolean z, Timer timer, boolean z2, boolean z3) {
        String str3;
        String string;
        String string2;
        String str4;
        int i;
        boolean z4;
        String str5;
        String str6;
        String str7;
        Notification.Action actionBuild;
        Bundle bundle = new Bundle();
        bundle.putBoolean(ClockCompat.MiuiNotification_EXTRA_SHOW_ACTION, true);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, str);
        builder.setContentTitle(charSequence);
        builder.setContentText(str2);
        builder.addAction(new NotificationCompat.Action.Builder(0, charSequence2, pendingIntent2).build());
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.stat_notify_alarm);
        builder.setOngoing(z);
        builder.setAutoCancel(false);
        builder.setPriority(4);
        builder.setWhen(z2 ? timer.getTime() : System.currentTimeMillis());
        builder.setShowWhen(false);
        builder.setUsesChronometer(z2);
        builder.setChronometerCountDown(z2);
        if (z2) {
            String string3 = context.getResources().getString(R.string.timer_alert_running);
            String string4 = context.getResources().getString(R.string.timer_running_notification);
            str3 = string3;
            String string5 = context.getResources().getString(R.string.aod_timer_running);
            String label = timer.getLabel();
            string = string5;
            if (label != null) {
                string2 = CTS_START_TIMER_TEST_TITLE;
                if (!label.equals(CTS_START_TIMER_TEST_TITLE)) {
                    string2 = context.getResources().getString(R.string.timer_running_island);
                }
            } else {
                string2 = context.getResources().getString(R.string.timer_running_island);
            }
            str4 = string4;
            i = -1;
            z4 = true;
        } else {
            String string6 = context.getResources().getString(R.string.timer_alert_pause);
            String string7 = context.getResources().getString(R.string.timer_pause_notification);
            str3 = string6;
            string = context.getResources().getString(R.string.aod_timer_pause);
            string2 = context.getResources().getString(R.string.timer_pause_island);
            str4 = string7;
            i = -2;
            z4 = false;
        }
        String str8 = str3;
        String str9 = string;
        long time = timer.getTime() + 900;
        Log.d(TAG, "buildNotificationWithMiuiFocusAction: " + time);
        String str10 = string2;
        Log.d(TAG, "timerAlertRunning2: " + str4);
        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        JSONObject jSONObject3 = new JSONObject();
        JSONObject jSONObject4 = jSONObject;
        JSONObject jSONObject5 = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        JSONObject jSONObject6 = new JSONObject();
        JSONObject jSONObject7 = new JSONObject();
        JSONObject jSONObject8 = new JSONObject();
        JSONObject jSONObject9 = new JSONObject();
        JSONObject jSONObject10 = new JSONObject();
        JSONObject jSONObject11 = new JSONObject();
        JSONObject jSONObject12 = new JSONObject();
        JSONObject jSONObject13 = new JSONObject();
        JSONObject jSONObject14 = new JSONObject();
        JSONObject jSONObject15 = new JSONObject();
        JSONObject jSONObject16 = new JSONObject();
        JSONObject jSONObject17 = new JSONObject();
        boolean z5 = z4;
        String str11 = "miui.focus.action_2";
        try {
            if (z3) {
                try {
                    jSONObject5.put("timerType", i).put("timerWhen", time).put("timerSystemCurrent", System.currentTimeMillis()).put("timerTotal", timer.getDuration());
                } catch (JSONException e) {
                    e = e;
                    str5 = "miui.focus.pic_timer";
                    str6 = "miui.focus.pic_ticker";
                    str7 = "miui.focus.action_1";
                    Log.e("JSONException", e);
                    bundle.putString("miui.focus.param", jSONObject4.toString());
                    Bundle bundle2 = new Bundle();
                    if (z2) {
                        actionBuild = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_pause_icon), context.getString(R.string.timer_pause_timer), pendingIntent3).build();
                        actionBuild.getExtras().putString("icon_name", "action_pause_timer");
                    } else {
                        actionBuild = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_start_icon), context.getString(R.string.timer_continue_timer), pendingIntent4).build();
                        actionBuild.getExtras().putString("icon_name", "action_restart_timer");
                    }
                    bundle2.putParcelable(str7, actionBuild);
                    Notification.Action actionBuild2 = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_stop_icon), context.getString(R.string.timer_cancel_timer), pendingIntent2).build();
                    actionBuild2.getExtras().putString("icon_name", "action_close");
                    bundle2.putParcelable(str11, actionBuild2);
                    bundle.putBundle("miui.focus.actions", bundle2);
                    Bundle bundle3 = new Bundle();
                    bundle3.putParcelable(str5, Icon.createWithResource(context, R.drawable.timer_notification_icon));
                    bundle3.putParcelable(str6, Icon.createWithResource(context, R.drawable.clock_icon));
                    bundle.putBundle("miui.focus.pics", bundle3);
                    builder.addExtras(bundle);
                    return builder.build();
                }
            } else {
                jSONObject5.put("timerType", i).put("timerWhen", time).put("timerSystemCurrent", System.currentTimeMillis());
            }
            try {
                jSONObject3.put("timerInfo", jSONObject5).put("subContent", str4).put("colorSubContent", "#3482FF").put("colorSubContentDark", "#277AF7").put("picFunction", "miui.focus.pic_timer");
                str5 = "miui.focus.pic_timer";
                jSONObject14.put("type", 2).put("pic", "hourglass").put("autoplay", z5);
                jSONObject11.put("type", 1).put("picInfo", jSONObject14);
                jSONObject12.put("timerInfo", jSONObject5);
                jSONObject9.put("imageTextInfoLeft", jSONObject11).put("sameWidthDigitInfo", jSONObject12);
                jSONObject15.put("type", 2).put("pic", "hourglass").put("autoplay", z5);
                jSONObject10.put("picInfo", jSONObject15);
                jSONObject17.put("progress", (int) ((timer.getRemain() / timer.getDuration()) * 100.0f)).put("colorProgress", "#3482FF").put("colorProgressDark", "#4788FF").put("colorProgressEnd", "#1A000000").put("colorProgressEndDark", "#29FFFFFF").put("isAutoProgress", true).put("isCCW", true);
                JSONObject jSONObjectPut = jSONObject6.put("type", 1).put("progressInfo", jSONObject17);
                str7 = "miui.focus.action_1";
                try {
                    jSONObjectPut.put(a.a, str7);
                    try {
                        jSONObject7.put(a.a, str11);
                        jSONArray.put(jSONObject6).put(jSONObject7);
                        str11 = str11;
                        jSONObject8.put("islandProperty", 1).put("islandOrder", true).put("expandedTime", 60).put("bigIslandArea", jSONObject9).put("smallIslandArea", jSONObject10).put("islandTimeout", TWELVE_HOURS_IN_SECONDS);
                        jSONObject13.put("type", 2).put("src", "hourglass_big").put("autoplay", z5);
                        jSONObject16.put("timerInfo", jSONObject5).put("content", str10).put("colorContent", "#99000000").put("colorContentDark", "#80FFFFFF").put("animIconInfo", jSONObject13);
                        str6 = "miui.focus.pic_ticker";
                        try {
                            jSONObject2.put("protocol", 1).put("updatable", true).put("enableFloat", false).put("aodTitle", str9).put("aodPic", str6).put("highlightInfo", jSONObject3).put("actions", jSONArray).put("param_island", jSONObject8).put("animTextInfo", jSONObject16).put("islandFirstFloat", true).put("business", "countdown");
                            jSONObject4 = jSONObject4;
                            try {
                                jSONObject4.put("protocol", 1);
                                jSONObject4.put("scene", "timer");
                                context = context;
                                try {
                                    jSONObject4.put("ticker", context.getString(R.string.channel_name_timer));
                                    jSONObject4.put("content", str8);
                                    jSONObject4.put("timerType", i);
                                    jSONObject4.put("timerWhen", time);
                                    jSONObject4.put("timerSystemCurrent", System.currentTimeMillis());
                                    jSONObject4.put("timerTotal", timer.getDuration());
                                    jSONObject4.put("enableFloat", false);
                                    jSONObject4.put("updatable", true);
                                    jSONObject4.put("param_v2", jSONObject2);
                                } catch (JSONException e2) {
                                    e = e2;
                                    Log.e("JSONException", e);
                                }
                            } catch (JSONException e3) {
                                e = e3;
                                context = context;
                            }
                        } catch (JSONException e4) {
                            e = e4;
                            context = context;
                            jSONObject4 = jSONObject4;
                        }
                    } catch (JSONException e5) {
                        e = e5;
                        str11 = str11;
                        str6 = "miui.focus.pic_ticker";
                        Log.e("JSONException", e);
                        bundle.putString("miui.focus.param", jSONObject4.toString());
                        Bundle bundle4 = new Bundle();
                        if (z2) {
                            actionBuild = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_pause_icon), context.getString(R.string.timer_pause_timer), pendingIntent3).build();
                            actionBuild.getExtras().putString("icon_name", "action_pause_timer");
                        } else {
                            actionBuild = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_start_icon), context.getString(R.string.timer_continue_timer), pendingIntent4).build();
                            actionBuild.getExtras().putString("icon_name", "action_restart_timer");
                        }
                        bundle4.putParcelable(str7, actionBuild);
                        Notification.Action actionBuild3 = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_stop_icon), context.getString(R.string.timer_cancel_timer), pendingIntent2).build();
                        actionBuild3.getExtras().putString("icon_name", "action_close");
                        bundle4.putParcelable(str11, actionBuild3);
                        bundle.putBundle("miui.focus.actions", bundle4);
                        Bundle bundle5 = new Bundle();
                        bundle5.putParcelable(str5, Icon.createWithResource(context, R.drawable.timer_notification_icon));
                        bundle5.putParcelable(str6, Icon.createWithResource(context, R.drawable.clock_icon));
                        bundle.putBundle("miui.focus.pics", bundle5);
                        builder.addExtras(bundle);
                        return builder.build();
                    }
                } catch (JSONException e6) {
                    e = e6;
                }
            } catch (JSONException e7) {
                e = e7;
                str5 = "miui.focus.pic_timer";
                str6 = "miui.focus.pic_ticker";
                str7 = "miui.focus.action_1";
                Log.e("JSONException", e);
                bundle.putString("miui.focus.param", jSONObject4.toString());
                Bundle bundle6 = new Bundle();
                if (z2) {
                    actionBuild = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_pause_icon), context.getString(R.string.timer_pause_timer), pendingIntent3).build();
                    actionBuild.getExtras().putString("icon_name", "action_pause_timer");
                } else {
                    actionBuild = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_start_icon), context.getString(R.string.timer_continue_timer), pendingIntent4).build();
                    actionBuild.getExtras().putString("icon_name", "action_restart_timer");
                }
                bundle6.putParcelable(str7, actionBuild);
                Notification.Action actionBuild4 = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_stop_icon), context.getString(R.string.timer_cancel_timer), pendingIntent2).build();
                actionBuild4.getExtras().putString("icon_name", "action_close");
                bundle6.putParcelable(str11, actionBuild4);
                bundle.putBundle("miui.focus.actions", bundle6);
                Bundle bundle7 = new Bundle();
                bundle7.putParcelable(str5, Icon.createWithResource(context, R.drawable.timer_notification_icon));
                bundle7.putParcelable(str6, Icon.createWithResource(context, R.drawable.clock_icon));
                bundle.putBundle("miui.focus.pics", bundle7);
                builder.addExtras(bundle);
                return builder.build();
            }
        } catch (JSONException e8) {
            e = e8;
        }
        bundle.putString("miui.focus.param", jSONObject4.toString());
        Bundle bundle8 = new Bundle();
        if (z2) {
            actionBuild = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_pause_icon), context.getString(R.string.timer_pause_timer), pendingIntent3).build();
            actionBuild.getExtras().putString("icon_name", "action_pause_timer");
        } else {
            actionBuild = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_start_icon), context.getString(R.string.timer_continue_timer), pendingIntent4).build();
            actionBuild.getExtras().putString("icon_name", "action_restart_timer");
        }
        bundle8.putParcelable(str7, actionBuild);
        Notification.Action actionBuild5 = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_stop_icon), context.getString(R.string.timer_cancel_timer), pendingIntent2).build();
        actionBuild5.getExtras().putString("icon_name", "action_close");
        bundle8.putParcelable(str11, actionBuild5);
        bundle.putBundle("miui.focus.actions", bundle8);
        Bundle bundle9 = new Bundle();
        bundle9.putParcelable(str5, Icon.createWithResource(context, R.drawable.timer_notification_icon));
        bundle9.putParcelable(str6, Icon.createWithResource(context, R.drawable.clock_icon));
        bundle.putBundle("miui.focus.pics", bundle9);
        builder.addExtras(bundle);
        return builder.build();
    }

    public static void showTimerRunningNotification(Context context, Timer timer, boolean z, boolean z2) {
        notifyNotification(context, getTimerRunningNotification(context, timer, z, z2), -4);
    }

    public static void showAlarmAlertNotification(Context context, Alarm alarm) {
        notifyNotification(context, getAlarmAlertNotification(context, alarm), alarm.id);
    }

    public static Notification buildMarkNotification(Context context, String str) {
        createAppChannel(context);
        Notification notificationBuildNotification = buildNotification(context, "channel_id_deskclock_alarm", context.getString(R.string.app_label), str, null, false, System.currentTimeMillis(), false);
        setEnableFloat(notificationBuildNotification, false);
        setEnableKeyguard(notificationBuildNotification, false);
        return notificationBuildNotification;
    }

    public static void clearAlarmArrivingNotification(Context context) {
        Log.f("DC:NotificationUtil", "clearAlarmArrivingNotification");
        clearNotification(context, -3);
    }

    public static void clearAlarmAlertNotification(Context context, int i) {
        clearNotification(context, i, Util.getUserHandle());
    }

    public static void clearAlarmSnoozeNotification(Context context, int i) {
        clearNotification(context, i);
    }

    public static void clearTimerRunningNotification(Context context) {
        clearNotification(context, -4);
    }

    public static void clearStopWatchNotification(Context context) {
        clearNotification(context, -8);
    }

    public static void clearTimerAlertNotification(Context context, int i) {
        clearNotification(context, i, Util.getUserHandle());
    }

    public static void clearNotification(Context context, int i) {
        if (context != null) {
            try {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
                if (notificationManager != null) {
                    notificationManager.cancel(i);
                }
            } catch (Exception e) {
                Log.e(TAG, "clearNotification error: " + e);
            }
        }
    }

    public static void clearNotification(Context context, int i, UserHandle userHandle) {
        Log.d(TAG, "clearNotification");
        try {
            ClockCompat.cancelNotification((NotificationManager) context.getSystemService("notification"), null, i, userHandle);
        } catch (Exception e) {
            Log.e(TAG, "clearNotification error: " + e);
        }
    }

    public static void notifyNotification(Context context, Notification notification, int i) {
        ((NotificationManager) context.getSystemService("notification")).notify(i, notification);
    }

    private static PendingIntent getBroadcast(Context context, int i, Intent intent) {
        return PendingIntent.getBroadcast(context, i, intent, 201326592);
    }

    private static PendingIntent getActivity(Context context, int i, Intent intent) {
        return PendingIntent.getActivity(context, i, intent, 201326592);
    }

    private static PendingIntent getActivityAsUser(Context context, int i, Intent intent) {
        try {
            return ClockCompat.getActivityPendingIntent(context, i, intent, 201326592, null, ClockCompat.UserHandle_CURRENT);
        } catch (Exception e) {
            Log.e("DC:NotificationUtil", "getActivityAsUser error: " + e);
            return PendingIntent.getActivity(context, i, intent, 201326592);
        }
    }

    private static void setEnableFloat(Notification notification, boolean z) {
        try {
            Object obj = notification.getClass().getDeclaredField("extraNotification").get(notification);
            obj.getClass().getDeclaredMethod("setEnableFloat", Boolean.TYPE).invoke(obj, Boolean.valueOf(z));
        } catch (Exception unused) {
        }
    }

    private static void setEnableKeyguard(Notification notification, boolean z) {
        try {
            Object obj = notification.getClass().getDeclaredField("extraNotification").get(notification);
            obj.getClass().getDeclaredMethod("setEnableKeyguard", Boolean.TYPE).invoke(obj, Boolean.valueOf(z));
        } catch (Exception unused) {
        }
    }

    private static Notification buildNotification(Context context, String str, CharSequence charSequence, CharSequence charSequence2, PendingIntent pendingIntent, Boolean bool, long j, boolean z) {
        return new NotificationCompat.Builder(context, str).setContentTitle(charSequence).setContentText(charSequence2).setContentIntent(pendingIntent).setSmallIcon(R.drawable.stat_notify_alarm).setOngoing(bool.booleanValue()).setAutoCancel(z).setPriority(4).setWhen(j).setShowWhen(true).build();
    }

    private static Notification buildNotificationWithAlarmAlert(Context context, String str, CharSequence charSequence, String str2, CharSequence charSequence2, CharSequence charSequence3, PendingIntent pendingIntent, CharSequence charSequence4, PendingIntent pendingIntent2, CharSequence charSequence5, PendingIntent pendingIntent3, Boolean bool, long j) {
        String string;
        String string2;
        Context context2;
        String str3;
        String str4;
        String str5;
        String str6;
        Bundle bundle = new Bundle();
        bundle.putBoolean(ClockCompat.MiuiNotification_EXTRA_SHOW_ACTION, true);
        bundle.putBoolean("miui.expandableOnKeyguard", true);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, str);
        builder.setContentTitle(charSequence);
        builder.setContentText(charSequence2);
        builder.addAction(new NotificationCompat.Action.Builder(0, charSequence4, pendingIntent2).build());
        builder.addAction(new NotificationCompat.Action.Builder(0, charSequence5, pendingIntent3).build());
        builder.setContentIntent(pendingIntent);
        builder.setDeleteIntent(pendingIntent2);
        builder.setSmallIcon(R.drawable.stat_notify_alarm);
        builder.setOngoing(bool.booleanValue());
        builder.setAutoCancel(false);
        builder.setPriority(4);
        builder.setWhen(j);
        builder.setShowWhen(false);
        if (charSequence3 == null || charSequence3.length() == 0 || charSequence3.equals(context.getString(R.string.default_label))) {
            string = context.getString(R.string.alarm_alert);
            string2 = context.getString(R.string.alarm_list_title);
        } else {
            string = (String) charSequence3;
            string2 = string;
        }
        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        JSONObject jSONObject3 = new JSONObject();
        JSONObject jSONObject4 = new JSONObject();
        JSONObject jSONObject5 = new JSONObject();
        JSONObject jSONObject6 = jSONObject;
        JSONArray jSONArray = new JSONArray();
        JSONObject jSONObject7 = new JSONObject();
        JSONObject jSONObject8 = new JSONObject();
        JSONObject jSONObject9 = new JSONObject();
        JSONObject jSONObject10 = new JSONObject();
        JSONObject jSONObject11 = new JSONObject();
        JSONObject jSONObject12 = new JSONObject();
        String str7 = string2;
        JSONObject jSONObject13 = new JSONObject();
        JSONObject jSONObject14 = new JSONObject();
        JSONObject jSONObject15 = new JSONObject();
        try {
            String str8 = string;
            jSONObject3.put("type", 1).put("title", str2).put("subContent", string).put("colorSubContent", "#3482FF").put("colorSubContentDark", "#277AF7").put("picFunction", "miui.focus.pic_alarm");
            jSONObject12.put("type", 2).put("pic", "alarmBig").put("autoplay", "true");
            jSONObject10.put("type", 1).put("picInfo", jSONObject12);
            jSONObject11.put("digit", str2).put("highlightColor", "#FFFFFF");
            jSONObject8.put("imageTextInfoLeft", jSONObject10).put("sameWidthDigitInfo", jSONObject11);
            jSONObject13.put("type", 2).put("pic", "alarmSmall").put("autoplay", "true");
            jSONObject9.put("picInfo", jSONObject13);
            jSONObject15.put("type", 2).put("src", "alarmBig").put("autoplay", "true");
            jSONObject14.put("title", str2).put("content", str7).put("colorTitle", "#000000").put("colorTitleDark", "#FFFFFF").put("colorContent", "#99000000").put("colorContentDark", "#80FFFFFF").put("animIconInfo", jSONObject15);
            jSONObject7.put("islandProperty", 2).put("islandOrder", true).put("expandedTime", 60).put("bigIslandArea", jSONObject8).put("smallIslandArea", jSONObject9);
            str6 = "miui.focus.action_1";
            try {
                jSONObject4.put(a.a, str6);
                str5 = "miui.focus.action_2";
                try {
                    jSONObject5.put(a.a, str5);
                    jSONArray.put(jSONObject4).put(jSONObject5);
                    str3 = "miui.focus.pic_alarm";
                    context2 = context;
                    try {
                        str4 = "miui.focus.pic_ticker";
                        try {
                            jSONObject2.put("protocol", 1).put("enableFloat", true).put("ticker", context2.getString(R.string.status_bar_alarm_alert)).put("highlightInfo", jSONObject3).put("tickerPic", str4).put("updatable", true).put("actions", jSONArray).put("param_island", jSONObject7).put("animTextInfo", jSONObject14).put("business", "alarm");
                            jSONObject6 = jSONObject6;
                            try {
                                jSONObject6.put("protocol", 1);
                                jSONObject6.put("scene", "templateBaseScene");
                                jSONObject6.put("ticker", context2.getString(R.string.alarm_list_title));
                                jSONObject6.put("title", str8);
                                jSONObject6.put("content", str2);
                                jSONObject6.put("enableFloat", true);
                                jSONObject6.put("updatable", true);
                                jSONObject6.put("param_v2", jSONObject2);
                            } catch (JSONException e) {
                                e = e;
                                Log.e("JSONException: ", e);
                            }
                        } catch (JSONException e2) {
                            e = e2;
                            jSONObject6 = jSONObject6;
                        }
                    } catch (JSONException e3) {
                        e = e3;
                        str4 = "miui.focus.pic_ticker";
                        Log.e("JSONException: ", e);
                        bundle.putString("miui.focus.param", jSONObject6.toString());
                        Bundle bundle2 = new Bundle();
                        Notification.Action actionBuild = new Notification.Action.Builder(Icon.createWithResource(context2, R.drawable.fab_stop_icon), context2.getString(R.string.snooze_duration), pendingIntent3).build();
                        actionBuild.getExtras().putString("icon_name", "action_later");
                        bundle2.putParcelable(str6, actionBuild);
                        Notification.Action actionBuild2 = new Notification.Action.Builder(Icon.createWithResource(context2, R.drawable.fab_stop_icon), context2.getString(R.string.close_alarm), pendingIntent2).build();
                        actionBuild2.getExtras().putString("icon_name", "action_end");
                        bundle2.putParcelable(str5, actionBuild2);
                        bundle.putBundle("miui.focus.actions", bundle2);
                        Bundle bundle3 = new Bundle();
                        bundle3.putParcelable(str3, Icon.createWithResource(context2, R.drawable.alarm_notification_icon));
                        bundle3.putParcelable(str4, Icon.createWithResource(context2, R.drawable.clock_icon));
                        bundle.putBundle("miui.focus.pics", bundle3);
                        builder.addExtras(bundle);
                        return builder.build();
                    }
                } catch (JSONException e4) {
                    e = e4;
                    context2 = context;
                    str3 = "miui.focus.pic_alarm";
                }
            } catch (JSONException e5) {
                e = e5;
                context2 = context;
                jSONObject6 = jSONObject6;
                str3 = "miui.focus.pic_alarm";
                str4 = "miui.focus.pic_ticker";
                str5 = "miui.focus.action_2";
            }
        } catch (JSONException e6) {
            e = e6;
            context2 = context;
            jSONObject6 = jSONObject6;
            str3 = "miui.focus.pic_alarm";
            str4 = "miui.focus.pic_ticker";
            str5 = "miui.focus.action_2";
            str6 = "miui.focus.action_1";
        }
        bundle.putString("miui.focus.param", jSONObject6.toString());
        Bundle bundle4 = new Bundle();
        Notification.Action actionBuild3 = new Notification.Action.Builder(Icon.createWithResource(context2, R.drawable.fab_stop_icon), context2.getString(R.string.snooze_duration), pendingIntent3).build();
        actionBuild3.getExtras().putString("icon_name", "action_later");
        bundle4.putParcelable(str6, actionBuild3);
        Notification.Action actionBuild4 = new Notification.Action.Builder(Icon.createWithResource(context2, R.drawable.fab_stop_icon), context2.getString(R.string.close_alarm), pendingIntent2).build();
        actionBuild4.getExtras().putString("icon_name", "action_end");
        bundle4.putParcelable(str5, actionBuild4);
        bundle.putBundle("miui.focus.actions", bundle4);
        Bundle bundle5 = new Bundle();
        bundle5.putParcelable(str3, Icon.createWithResource(context2, R.drawable.alarm_notification_icon));
        bundle5.putParcelable(str4, Icon.createWithResource(context2, R.drawable.clock_icon));
        bundle.putBundle("miui.focus.pics", bundle5);
        builder.addExtras(bundle);
        return builder.build();
    }

    private static Notification buildNotificationWithAlarmArriving(Context context, String str, CharSequence charSequence, CharSequence charSequence2, PendingIntent pendingIntent, CharSequence charSequence3, PendingIntent pendingIntent2, Boolean bool, long j) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ClockCompat.MiuiNotification_EXTRA_SHOW_ACTION, true);
        bundle.putBoolean("miui.expandableOnKeyguard", true);
        return new NotificationCompat.Builder(context, str).setContentTitle(charSequence).setContentText(charSequence2).addAction(new NotificationCompat.Action.Builder(0, charSequence3, pendingIntent2).build()).setContentIntent(pendingIntent).setSmallIcon(R.drawable.stat_notify_alarm).setOngoing(bool.booleanValue()).setAutoCancel(false).setPriority(4).setWhen(j).setShowWhen(false).addExtras(bundle).build();
    }

    private static Notification buildNotificationWithTimerAlert(Context context, String str, CharSequence charSequence, CharSequence charSequence2, PendingIntent pendingIntent, CharSequence charSequence3, PendingIntent pendingIntent2, Boolean bool, long j) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ClockCompat.MiuiNotification_EXTRA_SHOW_ACTION, true);
        bundle.putBoolean("miui.expandableOnKeyguard", true);
        NotificationCompat.Builder builderAddExtras = new NotificationCompat.Builder(context, str).setContentTitle(charSequence).setContentText(charSequence2).addAction(new NotificationCompat.Action.Builder(0, charSequence3, pendingIntent2).build()).setContentIntent(pendingIntent).setDeleteIntent(pendingIntent2).setSmallIcon(R.drawable.stat_notify_alarm).setOngoing(bool.booleanValue()).setAutoCancel(false).setPriority(4).setWhen(j).setShowWhen(false).addExtras(bundle);
        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        JSONObject jSONObject3 = new JSONObject();
        JSONObject jSONObject4 = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        JSONObject jSONObject5 = new JSONObject();
        JSONObject jSONObject6 = new JSONObject();
        JSONObject jSONObject7 = new JSONObject();
        JSONObject jSONObject8 = new JSONObject();
        JSONObject jSONObject9 = new JSONObject();
        JSONObject jSONObject10 = new JSONObject();
        JSONObject jSONObject11 = new JSONObject();
        JSONObject jSONObject12 = new JSONObject();
        JSONObject jSONObject13 = new JSONObject();
        try {
            jSONObject4.put(a.a, "miui.focus.action_1");
            jSONArray.put(jSONObject4);
            jSONObject3.put("title", "00:00").put("subContent", charSequence).put("colorSubContent", "#3482FF").put("colorSubContentDark", "#277AF7").put("picFunction", "miui.focus.pic_timer");
            jSONObject11.put("type", 2).put("pic", "hourglass");
            jSONObject8.put("type", 1).put("picInfo", jSONObject11);
            jSONObject9.put("title", DeskClockApp.getAppDEContext().getResources().getString(R.string.timer_end_island));
            jSONObject6.put("imageTextInfoLeft", jSONObject8).put("textInfo", jSONObject9);
            jSONObject12.put("type", 2).put("pic", "hourglass");
            jSONObject7.put("picInfo", jSONObject12);
            jSONObject5.put("islandProperty", 1).put("islandOrder", true).put("expandedTime", 60).put("bigIslandArea", jSONObject6).put("smallIslandArea", jSONObject7);
            jSONObject10.put("type", 0).put("src", "hourglass_big");
            jSONObject13.put("title", DeskClockApp.getAppDEContext().getResources().getString(R.string.timer_end_island)).put("colorTitle", "#000000").put("colorTitleDark", "#FFFFFF").put("animIconInfo", jSONObject10);
            jSONObject2.put("protocol", 1).put("updatable", true).put("enableFloat", true).put("highlightInfo", jSONObject3).put("actions", jSONArray).put("param_island", jSONObject5).put("animTextInfo", jSONObject13).put("islandFirstFloat", true).put("business", "countdown");
            jSONObject.put("param_v2", jSONObject2);
            bundle.putString("miui.focus.param", jSONObject.toString());
            Bundle bundle2 = new Bundle();
            Notification.Action actionBuild = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_stop_icon), charSequence3, pendingIntent2).build();
            actionBuild.getExtras().putString("icon_name", "action_close");
            bundle2.putParcelable("miui.focus.action_1", actionBuild);
            bundle.putBundle("miui.focus.actions", bundle2);
            Bundle bundle3 = new Bundle();
            bundle3.putParcelable("miui.focus.pic_timer", Icon.createWithResource(context, R.drawable.timer_notification_icon));
            bundle.putBundle("miui.focus.pics", bundle3);
            builderAddExtras.addExtras(bundle);
            return builderAddExtras.build();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static Notification buildNotificationWithSnooze(Context context, String str, long j, String str2, String str3, CharSequence charSequence, String str4, CharSequence charSequence2, PendingIntent pendingIntent, String str5, PendingIntent pendingIntent2, Boolean bool, long j2, String str6) {
        String str7;
        Bundle bundle;
        Bundle bundle2 = new Bundle();
        bundle2.putBoolean(ClockCompat.MiuiNotification_EXTRA_SHOW_ACTION, true);
        bundle2.putBoolean("miui.expandableOnKeyguard", true);
        NotificationCompat.Builder showWhen = new NotificationCompat.Builder(context, str).setContentTitle(charSequence).setContentText(charSequence2).setContentIntent(pendingIntent).setSmallIcon(R.drawable.stat_notify_alarm).setOngoing(bool.booleanValue()).setAutoCancel(false).setPriority(4).setWhen(j2).setShowWhen(false);
        String string = (str5 == null || str5.equals(context.getString(R.string.default_label))) ? context.getString(R.string.alarm_list_title) : str5;
        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        JSONObject jSONObject3 = new JSONObject();
        JSONObject jSONObject4 = new JSONObject();
        JSONObject jSONObject5 = new JSONObject();
        String str8 = string;
        JSONArray jSONArray = new JSONArray();
        JSONObject jSONObject6 = new JSONObject();
        JSONObject jSONObject7 = new JSONObject();
        JSONObject jSONObject8 = new JSONObject();
        JSONObject jSONObject9 = new JSONObject();
        JSONObject jSONObject10 = new JSONObject();
        JSONObject jSONObject11 = new JSONObject();
        JSONObject jSONObject12 = new JSONObject();
        JSONObject jSONObject13 = new JSONObject();
        JSONObject jSONObject14 = new JSONObject();
        try {
            jSONObject4.put("timerType", -1).put("timerWhen", j).put("timerSystemCurrent", System.currentTimeMillis());
            jSONObject3.put("timerInfo", jSONObject4).put("subContent", str4).put("colorSubContent", "#3482FF").put("colorSubContentDark", "#277AF7").put("picFunction", "miui.focus.pic_notification");
            jSONObject5.put(a.a, "miui.focus.action_2");
            jSONArray.put(jSONObject5);
            jSONObject12.put("type", 2).put("pic", "alarmBig");
            jSONObject9.put("type", 1).put("picInfo", jSONObject12);
            jSONObject10.put("digit", str4).put("highlightColor", "#FFFFFF");
            jSONObject7.put("imageTextInfoLeft", jSONObject9).put("sameWidthDigitInfo", jSONObject10);
            jSONObject13.put("type", 2).put("pic", "alarmSmall");
            jSONObject8.put("picInfo", jSONObject13);
            jSONObject6.put("islandProperty", 2).put("islandOrder", true).put("bigIslandArea", jSONObject7).put("smallIslandArea", jSONObject8);
            jSONObject11.put("type", 2).put("src", "alarmBig");
            jSONObject14.put("title", str6).put("content", DeskClockApp.getAppDEContext().getResources().getString(R.string.alarm_snooze_island)).put("colorTitle", "#000000").put("colorTitleDark", "#FFFFFF").put("colorContent", "#99000000").put("colorContentDark", "#80FFFFFF").put("animIconInfo", jSONObject11);
            str7 = "miui.focus.pic_aod";
            try {
                jSONObject2.put("protocol", 1).put("aodTitle", str3).put("aodPic", str7).put("ticker", str3).put("tickerPic", str7).put("highlightInfo", jSONObject3).put("islandFirstFloat", false).put("enableFloat", false).put("updatable", true).put("param_island", jSONObject6).put("animTextInfo", jSONObject14).put("actions", jSONArray).put("business", "alarm");
                jSONObject.put("protocol", 1);
                jSONObject.put("scene", "templateBaseScene");
                jSONObject.put("title", str4);
                jSONObject.put("content", str8);
                jSONObject.put("updatable", true);
                jSONObject.put("param_v2", jSONObject2);
                bundle = bundle2;
                try {
                    bundle.putString("miui.focus.param", jSONObject.toString());
                } catch (JSONException e) {
                    e = e;
                    Log.e("JSONException", e);
                }
            } catch (JSONException e2) {
                e = e2;
                bundle = bundle2;
                Log.e("JSONException", e);
                Bundle bundle3 = new Bundle();
                Notification.Action actionBuild = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_stop_icon), context.getString(R.string.click_to_cancel_alarm), pendingIntent2).build();
                actionBuild.getExtras().putString("icon_name", "action_end");
                bundle3.putParcelable("miui.focus.action_1", actionBuild);
                Notification.Action actionBuild2 = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_stop_icon), context.getString(R.string.click_to_cancel_alarm), pendingIntent2).build();
                actionBuild2.getExtras().putString("icon_name", "action_close");
                bundle3.putParcelable("miui.focus.action_2", actionBuild2);
                bundle.putBundle("miui.focus.actions", bundle3);
                Bundle bundle4 = new Bundle();
                bundle4.putParcelable("miui.focus.pic_notification", Icon.createWithResource(context, R.drawable.alarm_notification_icon));
                bundle4.putParcelable(str7, Icon.createWithResource(context, R.drawable.clock_icon));
                bundle.putBundle("miui.focus.pics", bundle4);
                showWhen.addExtras(bundle);
                return showWhen.build();
            }
        } catch (JSONException e3) {
            e = e3;
            str7 = "miui.focus.pic_aod";
        }
        Bundle bundle5 = new Bundle();
        Notification.Action actionBuild3 = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_stop_icon), context.getString(R.string.click_to_cancel_alarm), pendingIntent2).build();
        actionBuild3.getExtras().putString("icon_name", "action_end");
        bundle5.putParcelable("miui.focus.action_1", actionBuild3);
        Notification.Action actionBuild4 = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_stop_icon), context.getString(R.string.click_to_cancel_alarm), pendingIntent2).build();
        actionBuild4.getExtras().putString("icon_name", "action_close");
        bundle5.putParcelable("miui.focus.action_2", actionBuild4);
        bundle.putBundle("miui.focus.actions", bundle5);
        Bundle bundle6 = new Bundle();
        bundle6.putParcelable("miui.focus.pic_notification", Icon.createWithResource(context, R.drawable.alarm_notification_icon));
        bundle6.putParcelable(str7, Icon.createWithResource(context, R.drawable.clock_icon));
        bundle.putBundle("miui.focus.pics", bundle6);
        showWhen.addExtras(bundle);
        return showWhen.build();
    }

    private static void createAppChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        NotificationChannel notificationChannel = new NotificationChannel("channel_id_deskclock_alarm", context.getResources().getString(R.string.channel_name_alarm), 4);
        notificationChannel.setSound(null, null);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private static void createAlarmChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        NotificationChannel notificationChannel = new NotificationChannel("channel_id_deskclock_alarm", context.getResources().getString(R.string.channel_name_alarm), 4);
        notificationChannel.setSound(null, null);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private static void createSnoozeChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        String string = context.getResources().getString(R.string.channel_name_alarm);
        notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(GROUP_ID_SNOOZE, string));
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID_SNOOZE, string, 4);
        notificationChannel.setGroup(GROUP_ID_SNOOZE);
        notificationChannel.setSound(null, null);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private static void createTimerChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID_TIMER, context.getResources().getString(R.string.channel_name_timer), 4);
        notificationChannel.setSound(null, null);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private static void createSleepChannel(Context context) {
        ((NotificationManager) context.getSystemService("notification")).createNotificationChannel(new NotificationChannel(CHANNEL_ID_SLEEP, context.getResources().getString(R.string.channel_name_sleep), 4));
    }

    private static void createStopWatchChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID_STOPWATCH, context.getResources().getString(R.string.channel_name_stopwatch), 4);
        notificationChannel.setSound(null, null);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    public static void showSleepNotification(Context context) {
        Log.f("DC:NotificationUtil", "show sleep notification");
        notifyNotification(context, getSleepNotification(context), -7);
        StatHelper.recordCountEvent(StatHelper.CATEGORY_SLEEP_MANAGE, StatHelper.NOTIFICATION_SHOW);
        OneTrackStatHelper.trackTriggerEvent(OneTrackStatHelper.BEDTIME_NOTIFICATION_SHOW);
    }

    public static Notification getSleepNotification(Context context) {
        createSleepChannel(context);
        String string = context.getResources().getString(R.string.sleep_notification_title);
        String string2 = context.getResources().getString(R.string.sleep_notification_desc);
        Intent intent = new Intent(context, (Class<?>) BedtimeManageActivity.class);
        intent.putExtra("notification_click", true);
        Notification notificationBuildNotification = buildNotification(context, CHANNEL_ID_SLEEP, string, string2, getActivity(context, AlarmHelper.SLEEP_ALARM_ID, intent), false, System.currentTimeMillis(), true);
        setEnableFloat(notificationBuildNotification, true);
        setFloatTime(notificationBuildNotification, SLEEP_NOTIFICATION_DISPLAY_TIME);
        setEnableKeyguard(notificationBuildNotification, true);
        return notificationBuildNotification;
    }

    public static Notification getStopWatchNotification(Context context, String str) {
        createStopWatchChannel(context);
        String string = context.getResources().getString(R.string.stopwatch_running_notification);
        Intent intent = new Intent(context, (Class<?>) DeskClockTabActivity.class);
        intent.putExtra(Util.NAVIGATION_TAB, 2);
        Notification notificationBuildNotification = buildNotification(context, CHANNEL_ID_STOPWATCH, string, str, getActivity(context, -8, intent), false, System.currentTimeMillis(), true);
        setEnableFloat(notificationBuildNotification, true);
        setEnableKeyguard(notificationBuildNotification, true);
        return notificationBuildNotification;
    }

    public static void clearSleepNotification(Context context) {
        clearNotification(context, -7);
    }

    public static void setFloatTime(Notification notification, int i) {
        try {
            Object obj = notification.getClass().getDeclaredField("extraNotification").get(notification);
            obj.getClass().getDeclaredMethod("setFloatTime", Integer.TYPE).invoke(obj, Integer.valueOf(i));
        } catch (Exception unused) {
        }
    }

    public static void handleStopWatchNotification() {
        long jCurrentTimeMillis;
        Context appDEContext = DeskClockApp.getAppDEContext();
        Log.f("DC:NotificationUtil", "handle StopWatch Notification");
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(appDEContext);
        int i = 0;
        if (defaultSharedPreferences.getBoolean(Util.STOPWATCH_STATE_RUNNING_PREF, false)) {
            jCurrentTimeMillis = System.currentTimeMillis() - defaultSharedPreferences.getLong(Util.STOPWATCH_BASE_TIME_PREF, System.currentTimeMillis());
        } else {
            jCurrentTimeMillis = defaultSharedPreferences.getLong(Util.STOPWATCH_ELAPSED_TIME_PREF, 0L);
        }
        if (jCurrentTimeMillis < TimerDao.TIMER_MAX_LENGTH) {
            return;
        }
        long j = (int) (jCurrentTimeMillis / 60000);
        if (j >= Stopwatch.ELAPSED_TIME_THIRTY_DAY) {
            if (Stopwatch.isNotificationShowed(appDEContext, Stopwatch.KEY_SHOWED_NOTIFICATION_THIRTY_DAY)) {
                return;
            } else {
                Stopwatch.setKeyValue(appDEContext, Stopwatch.KEY_SHOWED_NOTIFICATION_THIRTY_DAY, true);
            }
        } else if (j >= Stopwatch.ELAPSED_TIME_SEVEN_DAY) {
            if (Stopwatch.isNotificationShowed(appDEContext, Stopwatch.KEY_SHOWED_NOTIFICATION_SEVEN_DAY)) {
                return;
            }
            Stopwatch.setKeyValue(appDEContext, Stopwatch.KEY_SHOWED_NOTIFICATION_SEVEN_DAY, true);
            i = 1;
        } else if (j < Stopwatch.ELAPSED_TIME_ONE_DAY) {
            i = -1;
        } else {
            if (Stopwatch.isNotificationShowed(appDEContext, Stopwatch.KEY_SHOWED_NOTIFICATION_ONE_DAY)) {
                return;
            }
            Stopwatch.setKeyValue(appDEContext, Stopwatch.KEY_SHOWED_NOTIFICATION_ONE_DAY, true);
            i = 2;
        }
        if (i != -1) {
            showStopWatchNotification(appDEContext, i);
        }
    }

    public static void showStopWatchNotification(Context context, int i) {
        String quantityString;
        if (context == null) {
            return;
        }
        if (i == 0) {
            quantityString = context.getResources().getQuantityString(R.plurals.stopwatch_running_notification_day, 30, 30);
        } else if (i == 1) {
            quantityString = context.getResources().getQuantityString(R.plurals.stopwatch_running_notification_day, 7, 7);
        } else {
            quantityString = i != 2 ? null : context.getResources().getQuantityString(R.plurals.stopwatch_running_notification_hour, 24, 24);
        }
        if (quantityString != null) {
            notifyNotification(context, getStopWatchNotification(context, quantityString), -8);
        }
    }

    public static Notification getStopwatchRunningNotification(Context context, boolean z, int i, long j) {
        createStopWatchChannel(context);
        String string = DeskClockApp.getAppDEContext().getResources().getString(z ? R.string.stopwatch_notification_nomal_title_running : R.string.stopwatch_notification_nomal_title_pause);
        Intent intent = new Intent(context, (Class<?>) DeskClockTabActivity.class);
        intent.putExtra(Util.NAVIGATION_TAB, 2);
        PendingIntent activity = getActivity(context, -8, intent);
        CharSequence text = context.getText(R.string.timer_cancel_hint);
        Notification notificationBuildStopwatchNotification = buildStopwatchNotification(context, CHANNEL_ID_STOPWATCH, string, "", activity, buildPendingIntent(context, StopWatchService.ACTION_STOPWATCH_RESET, -8), buildPendingIntent(context, StopWatchService.ACTION_STOPWATCH_LAP, -8), buildPendingIntent(context, StopWatchService.ACTION_STOPWATCH_PAUSE, -8), buildPendingIntent(context, StopWatchService.ACTION_STOPWATCH_CONTINUE, -8), true, j, z, i, text);
        setEnableFloat(notificationBuildStopwatchNotification, true);
        setEnableKeyguard(notificationBuildStopwatchNotification, true);
        return notificationBuildStopwatchNotification;
    }

    private static PendingIntent buildPendingIntent(Context context, String str, int i) {
        Intent intent = new Intent(str);
        intent.setPackage(context.getPackageName());
        intent.addFlags(268435456);
        return getBroadcast(context, i, intent);
    }

    /* JADX WARN: Code duplicated, block: B:55:0x0377  */
    /* JADX WARN: Code duplicated, block: B:56:0x039a  */
    /* JADX WARN: Code duplicated, block: B:59:0x03c3  */
    /* JADX WARN: Code duplicated, block: B:60:0x03e6  */
    private static Notification buildStopwatchNotification(Context context, String str, CharSequence charSequence, String str2, PendingIntent pendingIntent, PendingIntent pendingIntent2, PendingIntent pendingIntent3, PendingIntent pendingIntent4, PendingIntent pendingIntent5, boolean z, long j, boolean z2, int i, CharSequence charSequence2) {
        String string;
        String str3;
        String string2;
        CharSequence string3;
        int i2;
        String str4;
        String str5;
        Notification.Action actionBuild;
        Notification.Action actionBuild2;
        String str6 = "miui.focus.pic_ticker";
        String str7 = "miui.focus.action_1";
        Bundle bundle = new Bundle();
        bundle.putBoolean(ClockCompat.MiuiNotification_EXTRA_SHOW_ACTION, true);
        if (i > 0) {
            String string4 = DeskClockApp.getAppDEContext().getResources().getString(R.string.stopwatch_running_format, Integer.valueOf(i));
            if (z2) {
                string2 = DeskClockApp.getAppDEContext().getResources().getString(R.string.stopwatch_running_format_nomal, Integer.valueOf(i));
            } else {
                string2 = DeskClockApp.getAppDEContext().getResources().getString(R.string.stopwatch_pause_format_nomal, Integer.valueOf(i));
            }
            str3 = string4;
        } else {
            String string5 = DeskClockApp.getAppDEContext().getResources().getString(R.string.stopwatch_title);
            if (z2) {
                string = " ";
            } else {
                string = DeskClockApp.getAppDEContext().getResources().getString(R.string.stopwatch_notification_nomal_label);
            }
            String str8 = string;
            str3 = string5;
            string2 = str8;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, str);
        NotificationCompat.Builder ongoing = builder.setContentTitle(charSequence).setContentText(string2).setContentIntent(pendingIntent).setSmallIcon(R.drawable.stat_notify_alarm).setOngoing(z);
        boolean z3 = false;
        ongoing.setAutoCancel(false).addAction(new NotificationCompat.Action.Builder(0, charSequence2, pendingIntent2).build()).setPriority(4).setWhen(System.currentTimeMillis() - j).setShowWhen(false).setUsesChronometer(true).setChronometerCountDown(false);
        if (z2) {
            string3 = charSequence;
            z3 = true;
            i2 = 1;
        } else {
            string3 = DeskClockApp.getAppDEContext().getResources().getString(R.string.stopwatch_notification_pause_aod);
            i2 = 2;
        }
        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        JSONObject jSONObject3 = new JSONObject();
        JSONObject jSONObject4 = new JSONObject();
        JSONObject jSONObject5 = jSONObject;
        JSONArray jSONArray = new JSONArray();
        CharSequence charSequence3 = string3;
        JSONObject jSONObject6 = new JSONObject();
        JSONObject jSONObject7 = new JSONObject();
        JSONObject jSONObject8 = new JSONObject();
        JSONObject jSONObject9 = new JSONObject();
        JSONObject jSONObject10 = new JSONObject();
        JSONObject jSONObject11 = new JSONObject();
        JSONObject jSONObject12 = new JSONObject();
        JSONObject jSONObject13 = new JSONObject();
        JSONObject jSONObject14 = new JSONObject();
        JSONObject jSONObject15 = new JSONObject();
        JSONObject jSONObject16 = new JSONObject();
        try {
            int i3 = i2;
            jSONObject4.put("timerType", i2).put("timerWhen", System.currentTimeMillis() - j).put("timerSystemCurrent", System.currentTimeMillis());
            jSONObject3.put("timerInfo", jSONObject4).put("subContent", str3).put("colorSubContent", "#3482FF").put("colorSubContentDark", "#277AF7").put("picFunction", "miui.focus.pic_timer");
            str4 = "miui.focus.pic_timer";
            try {
                jSONObject14.put("type", 2).put("pic", "stopwatch_big").put("autoplay", z3);
                jSONObject11.put("type", 1).put("picInfo", jSONObject14);
                jSONObject12.put("timerInfo", jSONObject4);
                jSONObject9.put("imageTextInfoLeft", jSONObject11).put("sameWidthDigitInfo", jSONObject12);
                jSONObject15.put("type", 2).put("pic", NotificationCompat.CATEGORY_STOPWATCH).put("autoplay", z3);
                jSONObject10.put("picInfo", jSONObject15);
                try {
                    jSONObject6.put(a.a, str7);
                    str7 = str7;
                    try {
                        jSONObject7.put(a.a, "miui.focus.action_2");
                        jSONArray.put(jSONObject6).put(jSONObject7);
                        str5 = "miui.focus.action_2";
                        try {
                            jSONObject8.put("islandProperty", 1).put("islandOrder", true).put("expandedTime", 5).put("bigIslandArea", jSONObject9).put("smallIslandArea", jSONObject10).put("islandTimeout", TWELVE_HOURS_IN_SECONDS);
                            jSONObject13.put("type", 2).put("src", "stopwatch_big").put("autoplay", z3);
                            jSONObject16.put("timerInfo", jSONObject4).put("content", str3).put("colorContent", "#99000000").put("colorContentDark", "#80FFFFFF").put("animIconInfo", jSONObject13);
                            str6 = str6;
                            try {
                                jSONObject2.put("protocol", 1).put("updatable", true).put("enableFloat", false).put("aodTitle", charSequence3).put("aodPic", str6).put("highlightInfo", jSONObject3).put("actions", jSONArray).put("param_island", jSONObject8).put("animTextInfo", jSONObject16).put("business", NotificationCompat.CATEGORY_STOPWATCH);
                                jSONObject5 = jSONObject5;
                                try {
                                    jSONObject5.put("protocol", 1);
                                    jSONObject5.put("scene", "stopWatch");
                                    context = context;
                                    try {
                                        jSONObject5.put("ticker", context.getString(R.string.stopwatch_title));
                                        jSONObject5.put("content", str3);
                                        jSONObject5.put("timerType", i3);
                                        jSONObject5.put("timerWhen", j);
                                        jSONObject5.put("timerSystemCurrent", 0);
                                        jSONObject5.put("enableFloat", false);
                                        jSONObject5.put("updatable", true);
                                        jSONObject5.put("param_v2", jSONObject2);
                                    } catch (JSONException e) {
                                        e = e;
                                        Log.e("JSONException", e);
                                    }
                                } catch (JSONException e2) {
                                    e = e2;
                                    context = context;
                                }
                            } catch (JSONException e3) {
                                e = e3;
                                context = context;
                                jSONObject5 = jSONObject5;
                            }
                        } catch (JSONException e4) {
                            e = e4;
                            context = context;
                            jSONObject5 = jSONObject5;
                            str6 = str6;
                        }
                    } catch (JSONException e5) {
                        e = e5;
                        context = context;
                        jSONObject5 = jSONObject5;
                        str6 = str6;
                        str5 = "miui.focus.action_2";
                    }
                } catch (JSONException e6) {
                    e = e6;
                    str7 = str7;
                    str5 = "miui.focus.action_2";
                    Log.e("JSONException", e);
                    bundle.putString("miui.focus.param", jSONObject5.toString());
                    Bundle bundle2 = new Bundle();
                    if (z2) {
                        actionBuild = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_lap_icon), context.getString(R.string.lap), pendingIntent3).build();
                        actionBuild.getExtras().putString("icon_name", "action_mark_timer");
                    } else {
                        actionBuild = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_stop_icon), context.getString(R.string.reset), pendingIntent2).build();
                        actionBuild.getExtras().putString("icon_name", "action_end");
                    }
                    bundle2.putParcelable(str7, actionBuild);
                    if (z2) {
                        actionBuild2 = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_pause_icon), context.getString(R.string.pause), pendingIntent4).build();
                        actionBuild2.getExtras().putString("icon_name", "action_pause");
                    } else {
                        actionBuild2 = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_start_icon), context.getString(R.string.timer_continue), pendingIntent5).build();
                        actionBuild2.getExtras().putString("icon_name", "action_restart");
                    }
                    bundle2.putParcelable(str5, actionBuild2);
                    bundle.putBundle("miui.focus.actions", bundle2);
                    Bundle bundle3 = new Bundle();
                    bundle3.putParcelable(str4, Icon.createWithResource(context, R.drawable.stopwatch_notification_icon));
                    bundle3.putParcelable(str6, Icon.createWithResource(context, R.drawable.clock_icon));
                    bundle.putBundle("miui.focus.pics", bundle3);
                    builder.addExtras(bundle);
                    return builder.build();
                }
            } catch (JSONException e7) {
                e = e7;
            }
        } catch (JSONException e8) {
            e = e8;
            str4 = "miui.focus.pic_timer";
        }
        bundle.putString("miui.focus.param", jSONObject5.toString());
        Bundle bundle4 = new Bundle();
        if (z2) {
            actionBuild = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_lap_icon), context.getString(R.string.lap), pendingIntent3).build();
            actionBuild.getExtras().putString("icon_name", "action_mark_timer");
        } else {
            actionBuild = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_stop_icon), context.getString(R.string.reset), pendingIntent2).build();
            actionBuild.getExtras().putString("icon_name", "action_end");
        }
        bundle4.putParcelable(str7, actionBuild);
        if (z2) {
            actionBuild2 = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_pause_icon), context.getString(R.string.pause), pendingIntent4).build();
            actionBuild2.getExtras().putString("icon_name", "action_pause");
        } else {
            actionBuild2 = new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.fab_start_icon), context.getString(R.string.timer_continue), pendingIntent5).build();
            actionBuild2.getExtras().putString("icon_name", "action_restart");
        }
        bundle4.putParcelable(str5, actionBuild2);
        bundle.putBundle("miui.focus.actions", bundle4);
        Bundle bundle5 = new Bundle();
        bundle5.putParcelable(str4, Icon.createWithResource(context, R.drawable.stopwatch_notification_icon));
        bundle5.putParcelable(str6, Icon.createWithResource(context, R.drawable.clock_icon));
        bundle.putBundle("miui.focus.pics", bundle5);
        builder.addExtras(bundle);
        return builder.build();
    }

    public static void showStopWatchRunningNotification(Context context, boolean z, int i, long j) {
        notifyNotification(context, getStopwatchRunningNotification(context, z, i, j), -8);
    }
}
