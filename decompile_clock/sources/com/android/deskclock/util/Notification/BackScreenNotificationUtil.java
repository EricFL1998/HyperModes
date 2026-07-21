package com.android.deskclock.util.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import com.android.deskclock.Alarm;
import com.android.deskclock.R;
import com.android.deskclock.common.BroadcastReceiverActions;
import com.android.deskclock.timer.Timer;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.NotificationUtil;
import com.android.deskclock.util.Util;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class BackScreenNotificationUtil {
    public static final int BACK_SCREEN_ALARM_ID = -10;
    public static final int BACK_SCREEN_TIMER_ID = -9;
    private static final String BUSINESS = "business";
    private static final String CHANNEL_ID_BACK_SCREEN_ALARM = "channel_back_screen_alarm_id";
    private static final String CHANNEL_ID_BACK_SCREEN_TIMER = "channel_back_screen_timer_id";
    private static final String CLOCK_PACKAGE_NAME = "com.android.deskclock";
    public static final String EXTRA_FOCUS_ENABLE_ALERT = "miui.focus.enableAlert";
    private static final String EXTRA_FOCUS_ONLY_BACK_SCREEN = "isOnlyShowOnSubScreen";
    private static final String INDEX = "index";
    private static final String MAML_CONFIG = "maml_config";
    private static final String MIUI_REAR_PARAM = "miui.rear.param";
    private static final String PRIORITY = "priority";
    private static final String SHOW_TIME_TIP = "show_time_tip";
    private static final String SWIPE_OUT_SCREEN_LISTENER = "swipe_out_screen_listener";
    private static final String TAG = "DC:BackScreenNotificationUtil";

    public static void sendTimerRunningNotification(Context context, Timer timer, boolean z, boolean z2) {
        Notification timerRunningNotification = getTimerRunningNotification(context, timer, z);
        if (timerRunningNotification != null) {
            Log.d(TAG, "sendTimerRunningNotification: isRunning is " + z);
            timerRunningNotification.extras.putBoolean(EXTRA_FOCUS_ONLY_BACK_SCREEN, true);
            if (!z2) {
                timerRunningNotification.extras.putBoolean(EXTRA_FOCUS_ENABLE_ALERT, false);
            }
            NotificationUtil.notifyNotification(context, timerRunningNotification, -9);
        }
    }

    public static void sendTimerAlertNotification(Context context) {
        Notification timerAlertNotification = getTimerAlertNotification(context);
        if (timerAlertNotification != null) {
            Log.d(TAG, "sendTimerAlertNotification");
            timerAlertNotification.extras.putBoolean(EXTRA_FOCUS_ONLY_BACK_SCREEN, true);
            NotificationUtil.notifyNotification(context, timerAlertNotification, -9);
        }
    }

    public static void clearTimerNotification(Context context) {
        Log.d(TAG, "clearTimerNotification");
        NotificationUtil.clearNotification(context, -9);
    }

    public static void sendAlarmAlertNotification(Context context, Alarm alarm) {
        Notification alarmAlertNotification = getAlarmAlertNotification(context, alarm);
        if (alarmAlertNotification != null) {
            Log.d(TAG, "sendAlarmAlertNotification");
            alarmAlertNotification.extras.putBoolean(EXTRA_FOCUS_ONLY_BACK_SCREEN, true);
            NotificationUtil.notifyNotification(context, alarmAlertNotification, -10);
        }
    }

    public static void sendAlarmSnoozeNotification(Context context, Alarm alarm, int i) {
        Notification alarmSnoozeNotification = getAlarmSnoozeNotification(context, alarm, i);
        if (alarmSnoozeNotification != null) {
            Log.d(TAG, "sendAlarmSnoozeNotification: snoozeMinutes is " + i);
            alarmSnoozeNotification.extras.putBoolean(EXTRA_FOCUS_ONLY_BACK_SCREEN, true);
            NotificationUtil.notifyNotification(context, alarmSnoozeNotification, -10);
        }
    }

    public static void clearAlarmNotification(Context context) {
        Log.d(TAG, "clearAlarmNotification");
        NotificationUtil.clearNotification(context, -10);
    }

    private static Notification getTimerRunningNotification(Context context, Timer timer, boolean z) {
        if (Util.isIndependentRearDevice() && Util.isSupportRearSmartAssistant()) {
            createTimerChannel(context);
            long time = timer.getTime();
            int i = z ? -1 : -2;
            Bundle bundle = new Bundle();
            String timerRunningNotificationMessage = getTimerRunningNotificationMessage(context, i, time, timer, z);
            Log.d(TAG, "timerRunningBackScreenNotification is " + timerRunningNotificationMessage);
            bundle.putString(MIUI_REAR_PARAM, timerRunningNotificationMessage);
            return new NotificationCompat.Builder(context, CHANNEL_ID_BACK_SCREEN_TIMER).setSmallIcon(R.drawable.stat_notify_alarm).setAutoCancel(false).setPriority(3).addExtras(bundle).build();
        }
        Log.i(TAG, "getTimerRunningNotification: the isIndependentRearDevice is " + Util.isIndependentRearDevice());
        Log.i(TAG, "getTimerRunningNotification: the isSupportRearSmartAssistant is " + Util.isSupportRearSmartAssistant());
        return null;
    }

    private static Notification getTimerAlertNotification(Context context) {
        if (Util.isIndependentRearDevice() && Util.isSupportRearSmartAssistant()) {
            createTimerChannel(context);
            String string = context.getString(R.string.timer_end_island);
            Bundle bundle = new Bundle();
            String timerAlertNotificationMessage = getTimerAlertNotificationMessage(string);
            Log.d(TAG, "timerAlertBackScreenNotification is " + timerAlertNotificationMessage);
            bundle.putString(MIUI_REAR_PARAM, timerAlertNotificationMessage);
            return new NotificationCompat.Builder(context, CHANNEL_ID_BACK_SCREEN_TIMER).setSmallIcon(R.drawable.stat_notify_alarm).setAutoCancel(false).setPriority(3).addExtras(bundle).build();
        }
        Log.i(TAG, "getTimerRunningNotification: the isIndependentRearDevice is " + Util.isIndependentRearDevice());
        Log.i(TAG, "getTimerRunningNotification: the isSupportRearSmartAssistant is " + Util.isSupportRearSmartAssistant());
        return null;
    }

    private static Notification getAlarmAlertNotification(Context context, Alarm alarm) {
        if (Util.isIndependentRearDevice() && Util.isSupportRearSmartAssistant()) {
            createAlarmChannel(context);
            AlarmHelper.reset24HourMode(context);
            Bundle bundle = new Bundle();
            String labelOrDefault = alarm.getLabelOrDefault(context);
            if (labelOrDefault == null || labelOrDefault.isEmpty() || labelOrDefault.equals(context.getString(R.string.default_label))) {
                labelOrDefault = "";
            }
            String alarmAlertNotificationMessage = getAlarmAlertNotificationMessage(labelOrDefault, alarm.time, alarm.id, alarm.daysOfWeek.isRepeatSet());
            Log.d(TAG, "alarmAlertBackScreenNotification is " + alarmAlertNotificationMessage);
            bundle.putString(MIUI_REAR_PARAM, alarmAlertNotificationMessage);
            return new NotificationCompat.Builder(context, CHANNEL_ID_BACK_SCREEN_ALARM).setSmallIcon(R.drawable.stat_notify_alarm).setAutoCancel(false).setPriority(3).addExtras(bundle).build();
        }
        Log.i(TAG, "getAlarmAlertNotification: the isIndependentRearDevice is " + Util.isIndependentRearDevice());
        Log.i(TAG, "getAlarmAlertNotification: the isSupportRearSmartAssistant is " + Util.isSupportRearSmartAssistant());
        return null;
    }

    private static Notification getAlarmSnoozeNotification(Context context, Alarm alarm, int i) {
        if (Util.isIndependentRearDevice() && Util.isSupportRearSmartAssistant()) {
            createAlarmChannel(context);
            Bundle bundle = new Bundle();
            String snoozeAlarmNotificationMessage = getSnoozeAlarmNotificationMessage(context, System.currentTimeMillis() + ((long) (i * 60000)), alarm.id, alarm.daysOfWeek.isRepeatSet());
            Log.d(TAG, "snoozeAlarmBackScreenNotification is " + snoozeAlarmNotificationMessage);
            bundle.putString(MIUI_REAR_PARAM, snoozeAlarmNotificationMessage);
            return new NotificationCompat.Builder(context, CHANNEL_ID_BACK_SCREEN_ALARM).setSmallIcon(R.drawable.stat_notify_alarm).setAutoCancel(false).setPriority(3).addExtras(bundle).build();
        }
        Log.i(TAG, "getAlarmSnoozeNotification: the isIndependentRearDevice is " + Util.isIndependentRearDevice());
        Log.i(TAG, "getAlarmSnoozeNotification: the isSupportRearSmartAssistant is " + Util.isSupportRearSmartAssistant());
        return null;
    }

    private static String getTimerRunningNotificationMessage(Context context, int i, long j, Timer timer, boolean z) {
        String str;
        if (z) {
            str = TimerDao.ACTION_TIMER_PAUSE;
        } else {
            str = TimerDao.ACTION_TIMER_CONTINUE;
        }
        try {
            JSONObject jSONObject = new JSONObject();
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("timer_type", 0).put("maml_type", i).put("timer_rear_content", context.getString(R.string.tiemr_remain_back_screen)).put("timer_when", j).put("timer_system_current", System.currentTimeMillis()).put("timer_remain", timer.getRemain()).put("timer_total", timer.getDuration()).put("miui_rear_action_1", str).put("miui_rear_action_2", TimerDao.ACTION_TIMER_CANCEL).put("miui_rear_package_name", "com.android.deskclock");
            jSONObject.put(BUSINESS, "countdown").put("index", 0).put("priority", 400).put(SWIPE_OUT_SCREEN_LISTENER, true).put(SHOW_TIME_TIP, false).put(MAML_CONFIG, jSONObject2);
            return jSONObject.toString();
        } catch (JSONException e) {
            Log.e(TAG, "getTimerRunningBackScreenNotification error: ", e);
            return null;
        }
    }

    private static String getTimerAlertNotificationMessage(String str) {
        try {
            JSONObject jSONObject = new JSONObject();
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("timer_type", 1).put("timer_alert_title", str).put("miui_rear_close_action", AlarmHelper.ACTION_TIMER_DISMISS).put("miui_rear_package_name", "com.android.deskclock");
            jSONObject.put(BUSINESS, "countdown").put("index", 0).put("priority", 400).put(SWIPE_OUT_SCREEN_LISTENER, true).put(SHOW_TIME_TIP, false).put(MAML_CONFIG, jSONObject2);
            return jSONObject.toString();
        } catch (JSONException e) {
            Log.e(TAG, "getTimerAlertBackScreenNotification error: ", e);
            return null;
        }
    }

    private static String getSnoozeAlarmNotificationMessage(Context context, long j, int i, boolean z) {
        try {
            JSONObject jSONObject = new JSONObject();
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("alarm_type", 1).put("alarm_id", i).put("alarm_is_repeat", z).put("snooze_alarm_time", j).put("snooze_alarm_content", context.getString(R.string.snooze_alarm_back_screen)).put("snooze_rear_action", BroadcastReceiverActions.ACTION_SUB_BACK_SCREEN_ALARM_CANCEL).put("miui_rear_package_name", "com.android.deskclock");
            jSONObject.put(BUSINESS, "alarm").put("index", 0).put("priority", 400).put(SWIPE_OUT_SCREEN_LISTENER, true).put(SHOW_TIME_TIP, false).put(MAML_CONFIG, jSONObject2);
            return jSONObject.toString();
        } catch (JSONException e) {
            Log.e(TAG, "getSnoozeAlarmBackScreenNotification error: ", e);
            return null;
        }
    }

    private static String getAlarmAlertNotificationMessage(String str, long j, int i, boolean z) {
        try {
            JSONObject jSONObject = new JSONObject();
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("alarm_type", 0).put("alarm_id", i).put("alarm_is_repeat", z).put("alarm_time", j).put("alarm_label", str).put("miui_rear_action_1", BroadcastReceiverActions.ACTION_SUB_BACK_SCREEN_ALARM_SNOOZE).put("miui_rear_action_2", BroadcastReceiverActions.ACTION_SUB_BACK_SCREEN_ALARM_DISMISS).put("miui_rear_package_name", "com.android.deskclock");
            jSONObject.put(BUSINESS, "alarm").put("index", 0).put("priority", 400).put(SWIPE_OUT_SCREEN_LISTENER, true).put(SHOW_TIME_TIP, false).put(MAML_CONFIG, jSONObject2);
            return jSONObject.toString();
        } catch (JSONException e) {
            Log.e(TAG, "getAlarmBackScreenNotification error: ", e);
            return null;
        }
    }

    private static void createTimerChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID_BACK_SCREEN_TIMER, "back_screen_timer_channel", 3);
        notificationChannel.setSound(null, null);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private static void createAlarmChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID_BACK_SCREEN_ALARM, "back_screen_alarm_channel", 3);
        notificationChannel.setSound(null, null);
        notificationManager.createNotificationChannel(notificationChannel);
    }
}
