package com.android.deskclock.timer;

import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Message;
import android.text.TextUtils;
import com.android.deskclock.AlarmClockExtras;
import com.android.deskclock.AsyncHandler;
import com.android.deskclock.BuildConfig;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.addition.ringtone.digital.DigitalTimerRingtoneHelper;
import com.android.deskclock.alarm.alert.AlarmService;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmUtils;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class TimerDao {
    public static final String ACTION_TIMER_CANCEL = "action.timer_cancel";
    public static final String ACTION_TIMER_CANCEL_TO_CONTINUE = "com.miui.voiceassist.action.timer_continue";
    public static final String ACTION_TIMER_CANCEL_TO_XIAOAI = "com.miui.voiceassist.action.timer_cancel";
    public static final String ACTION_TIMER_CONTINUE = "action.timer_continue";
    public static final String ACTION_TIMER_OFF = "action.timer_off";
    public static final String ACTION_TIMER_PAUSE = "action.timer_pause";
    public static final String ACTION_TIMER_PAUSE_TO_XIAOAI = "com.miui.voiceassist.action.timer_pause";
    public static final String DEFAULT_ALERT_VALUE = "default_timer_alert";
    public static final String FUNCTION_TIMER_CANCEL = "function.timer_cancel";
    public static final String KEY_CHANGE_TIMER_SILENCE_TO_DEFAULT = "change_timer_silence_to_default";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_GUIDE = "first_enter_remind";
    public static final String KEY_KEEP_SCREEN = "KeepScreen";
    public static final String KEY_MUTE = "alert_mute";
    public static final String KEY_TIMER_ALERT = "default_timer_alert";
    public static final String KEY_TYPE = "timer_type";
    private static final int MSG_TIMER_HANDLER = Integer.MAX_VALUE;
    public static final String PROVIDER_TIMER_CANCEL = "provider.timer_cancel";
    public static final String PROVIDER_TIMER_PAUSE = "provider.timer_pause";
    public static final String PROVIDER_TIMER_RESUME = "provider.timer_resume";
    public static final int REGISTER_ALARM_MANAGER_TIMER = 0;
    public static final int REGISTER_HANDLER_TIMER = 1;
    public static final int REGISTER_TIMER_FAIL = -1;
    private static final String TAG = "DC:TimerDao";
    public static final String TIMER_ALERT_ACTION = "com.android.deskclock.TIMER_ALERT";
    private static final long TIMER_DEFAULT_LENGTH = 300000;
    public static final long TIMER_MAX_LENGTH = 86400000;
    public static final long TIMER_MIN_LENGTH = 1000;
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/timercountdown");
    public static final String KEY_STATE = "timestate";
    public static final String KEY_END_TIME = "endtime";
    public static final String KEY_REMAIN_TIME = "timerremained";
    public static final String[] PROJECTION = {KEY_STATE, KEY_END_TIME, "duration", KEY_REMAIN_TIME};

    public static int registerTimerToAlarmManager(Context context, Timer timer) {
        Log.d(TAG, "register timer");
        unRegisterTimerOffToAlarmManager(context);
        long time = timer.getTime();
        Intent intent = new Intent(context, (Class<?>) AlarmService.class);
        intent.setAction(TIMER_ALERT_ACTION);
        if (timer.getLabel() != null) {
            intent.putExtra(AlarmHelper.ACTION_TIMER_NAME, timer.getLabel());
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (time == 0 || time <= jCurrentTimeMillis) {
            return -1;
        }
        long j = time - jCurrentTimeMillis;
        if (j < 5000) {
            triggerDelay(context, j, intent);
            return 1;
        }
        Log.f(TAG, "register timer to AlarmManager: " + timer.toString());
        AlarmUtils.setServiceAlarm(context, time, intent);
        return 0;
    }

    public static void registerTimerOffToAlarmManager(Context context, long j) {
        Intent intent = new Intent("action.timer_off");
        intent.setPackage(context.getPackageName());
        AlarmUtils.setAlarm(context, j, intent);
    }

    public static void unRegisterTimerOffToAlarmManager(Context context) {
        AlarmUtils.cancelAlarm(context, "action.timer_off");
    }

    public static void unregisterTimerToAlarmManager(Context context) {
        Log.d(TAG, "unregister timer");
        Intent intent = new Intent(context, (Class<?>) AlarmService.class);
        intent.setAction(TIMER_ALERT_ACTION);
        AlarmUtils.cancelServiceAlarm(context, intent);
        AsyncHandler.removeCallbacks(Integer.MAX_VALUE);
    }

    private static void triggerDelay(Context context, long j, Intent intent) {
        final PendingIntent foregroundService = PendingIntent.getForegroundService(context, 0, intent, 201326592);
        Message messageObtain = AsyncHandler.obtain(new Runnable() { // from class: com.android.deskclock.timer.TimerDao.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    foregroundService.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        });
        messageObtain.what = Integer.MAX_VALUE;
        AsyncHandler.sendMessageDelayed(messageObtain, j);
    }

    public static Cursor queryTimer(Context context) {
        Log.i(TAG, "query Timer form ContentProvider");
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(context);
        MatrixCursor matrixCursor = new MatrixCursor(PROJECTION);
        matrixCursor.addRow(new Object[]{Integer.valueOf(defaultSharedPreferences.getInt(KEY_STATE, 0)), Long.valueOf(defaultSharedPreferences.getLong(KEY_END_TIME, 0L)), Long.valueOf(defaultSharedPreferences.getLong("duration", 0L)), Long.valueOf(defaultSharedPreferences.getLong(KEY_REMAIN_TIME, 0L))});
        return matrixCursor;
    }

    public static int updateTimer(Context context, ContentValues contentValues) {
        Log.i(TAG, "update Timer form ContentProvider: " + contentValues.toString());
        int iIntValue = 0;
        for (String str : contentValues.keySet()) {
            if (KEY_STATE.equals(str)) {
                iIntValue = ((Integer) contentValues.get(str)).intValue();
            }
        }
        Intent intent = new Intent();
        intent.setPackage(BuildConfig.APPLICATION_ID);
        Timer timer = getTimer(context);
        if (timer != null && ((timer.getState() == 0 || timer.getState() == 3) && iIntValue == 1)) {
            iIntValue = 0;
        }
        if (iIntValue == 0) {
            intent.setAction(PROVIDER_TIMER_CANCEL);
            context.sendBroadcast(intent);
        } else if (iIntValue == 1) {
            intent.setAction(PROVIDER_TIMER_RESUME);
            context.sendBroadcast(intent);
        } else if (iIntValue == 2) {
            intent.setAction(PROVIDER_TIMER_PAUSE);
            context.sendBroadcast(intent);
        }
        return 0;
    }

    public static Uri insertTimer(Context context, ContentValues contentValues) {
        Log.i(TAG, "insert Timer form ContentProvider: " + contentValues.toString());
        long jLongValue = 0;
        for (String str : contentValues.keySet()) {
            if ("duration".equals(str)) {
                jLongValue = Long.valueOf(contentValues.get(str).toString()).longValue();
            }
        }
        if (jLongValue > 1000 && jLongValue < TIMER_MAX_LENGTH) {
            Intent intent = new Intent(context, (Class<?>) TimerService.class);
            intent.putExtra(AlarmClockExtras.TIMER_INTENT_EXTRA, jLongValue);
            intent.putExtra(Util.IS_START_TIMER, true);
            context.startForegroundService(intent);
        } else {
            Log.e(TAG, "timer duration id out of range");
        }
        return ContentUris.withAppendedId(CONTENT_URI, 0L);
    }

    public static void handleXiaoAiTimer(Context context, ContentValues contentValues) {
        long jLongValue = 0;
        for (String str : contentValues.keySet()) {
            if ("duration".equals(str)) {
                jLongValue = Long.valueOf(contentValues.get(str).toString()).longValue();
            }
        }
        if (jLongValue <= 1000 || jLongValue >= TIMER_MAX_LENGTH) {
            return;
        }
        FBEUtil.getDefaultSharedPreferences(context).edit().putInt(KEY_STATE, 1).putLong(KEY_END_TIME, System.currentTimeMillis() + jLongValue).putLong("duration", jLongValue).putLong(KEY_REMAIN_TIME, jLongValue).apply();
    }

    public static int deleteTimer(Context context) {
        Log.i(TAG, "delete Timer form ContentProvider");
        Intent intent = new Intent();
        intent.setPackage(BuildConfig.APPLICATION_ID);
        intent.setAction(PROVIDER_TIMER_CANCEL);
        context.sendBroadcast(intent);
        return 0;
    }

    public static Timer getTimer(Context context) {
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(context);
        int i = defaultSharedPreferences.getInt(KEY_STATE, 0);
        long j = 0;
        long j2 = defaultSharedPreferences.getLong(KEY_END_TIME, 0L);
        long j3 = defaultSharedPreferences.getLong(KEY_REMAIN_TIME, TIMER_DEFAULT_LENGTH);
        if ((i != 1 && i != 3) || j2 >= System.currentTimeMillis()) {
            j = j3;
        } else if (AlarmService.getTimerAlarming()) {
            i = 3;
        } else {
            i = 0;
            j = j3;
        }
        long j4 = defaultSharedPreferences.getLong("duration", TIMER_DEFAULT_LENGTH);
        int i2 = defaultSharedPreferences.getInt("timer_type", 0);
        boolean z = defaultSharedPreferences.getBoolean(KEY_MUTE, false);
        boolean z2 = defaultSharedPreferences.getBoolean(KEY_KEEP_SCREEN, true);
        Timer timer = new Timer();
        timer.setDuration(j4);
        timer.setRemain(j);
        timer.setTime(j2);
        timer.setType(i2);
        timer.setState(i);
        timer.setSilent(z);
        timer.setBright(z2);
        return timer;
    }

    public static void saveTimer(Context context, Timer timer) {
        SharedPreferences.Editor editorEdit = FBEUtil.getDefaultSharedPreferences(context).edit();
        editorEdit.putInt(KEY_STATE, timer.getState());
        editorEdit.putLong(KEY_END_TIME, timer.getTime());
        editorEdit.putLong("duration", timer.getDuration());
        editorEdit.putLong(KEY_REMAIN_TIME, timer.getRemain());
        editorEdit.putInt("timer_type", timer.getType());
        editorEdit.putBoolean(KEY_MUTE, timer.isSilent());
        editorEdit.putBoolean(KEY_KEEP_SCREEN, timer.isBright());
        editorEdit.apply();
    }

    public static void updateTimerState(Context context, int i) {
        SharedPreferences.Editor editorEdit = FBEUtil.getDefaultSharedPreferences(context).edit();
        editorEdit.putInt(KEY_STATE, i);
        editorEdit.apply();
    }

    public static void updateTimerType(Context context, int i) {
        SharedPreferences.Editor editorEdit = FBEUtil.getDefaultSharedPreferences(context).edit();
        editorEdit.putInt("timer_type", i);
        editorEdit.apply();
    }

    public static void updateRemainTime(Context context, long j) {
        SharedPreferences.Editor editorEdit = FBEUtil.getDefaultSharedPreferences(context).edit();
        editorEdit.putLong(KEY_REMAIN_TIME, j);
        editorEdit.apply();
    }

    public static void updateTimerSilent(Context context, boolean z) {
        SharedPreferences.Editor editorEdit = FBEUtil.getDefaultSharedPreferences(context).edit();
        editorEdit.putBoolean(KEY_MUTE, z);
        editorEdit.apply();
    }

    public static void updateTimerBright(Context context, boolean z) {
        SharedPreferences.Editor editorEdit = FBEUtil.getDefaultSharedPreferences(context).edit();
        editorEdit.putBoolean(KEY_KEEP_SCREEN, z);
        editorEdit.apply();
    }

    public static void setTimerRingtone(Uri uri) {
        SharedPreferences.Editor editorEdit = FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).edit();
        editorEdit.putString("default_timer_alert", uri == null ? "" : uri.toString());
        editorEdit.apply();
    }

    public static long getTimerDuration() {
        return FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).getLong("duration", 0L);
    }

    public static Uri getTimerRingtone() {
        Uri ringtoneUri;
        Context appDEContext = DeskClockApp.getAppDEContext();
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(appDEContext);
        String string = defaultSharedPreferences.getString("default_timer_alert", "default_timer_alert");
        Log.d(TAG, "getTimerRingtone value:" + string);
        boolean z = defaultSharedPreferences.getBoolean(KEY_CHANGE_TIMER_SILENCE_TO_DEFAULT, false);
        try {
            if ("default_timer_alert".equals(string)) {
                ringtoneUri = DigitalTimerRingtoneHelper.getRingtoneUri();
            } else if (TextUtils.isEmpty(string)) {
                Log.d(TAG, "Util.isDataRestored(context):" + Util.isDataRestored(appDEContext) + "  Util.isRestored(context):" + Util.isRestored(appDEContext) + "  !changeTimer:" + (!z));
                if ((Util.isDataRestored(appDEContext) || Util.isRestored(appDEContext)) && !z) {
                    ringtoneUri = DigitalTimerRingtoneHelper.getRingtoneUri();
                    SharedPreferences.Editor editorEdit = defaultSharedPreferences.edit();
                    editorEdit.putBoolean(KEY_CHANGE_TIMER_SILENCE_TO_DEFAULT, true);
                    editorEdit.putString("default_timer_alert", "default_timer_alert");
                    editorEdit.apply();
                } else {
                    ringtoneUri = null;
                }
            } else {
                ringtoneUri = Uri.parse(string);
            }
        } catch (Exception e) {
            Log.e(TAG, "getTimerRingtone error, use default", e);
            ringtoneUri = DigitalTimerRingtoneHelper.getRingtoneUri();
        }
        Log.i(TAG, "getTimerRingtone=" + ringtoneUri);
        return ringtoneUri;
    }
}
