package com.android.deskclock.alarm.alert;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import com.android.deskclock.AdditionUtil;
import com.android.deskclock.Alarm;
import com.android.deskclock.AlarmAlertWakeLock;
import com.android.deskclock.BuildConfig;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.monitor.MonitorHelper;
import com.android.deskclock.addition.monitor.MonitorJobScheduler;
import com.android.deskclock.addition.monitor.NotificationMonitor;
import com.android.deskclock.addition.other.MiWearableHelper;
import com.android.deskclock.alarm.AlarmClockFragment;
import com.android.deskclock.alarm.AlarmColorLightManager;
import com.android.deskclock.alarm.ReflectUtil;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarm;
import com.android.deskclock.appaf.AppSearchUtil;
import com.android.deskclock.common.BroadcastReceiverActions;
import com.android.deskclock.compat.ClockCompat;
import com.android.deskclock.console.IAlarmConsoleV1Callback;
import com.android.deskclock.settings.AlarmSettingsActivity;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmUtils;
import com.android.deskclock.util.CompatUtil;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Notification.BackScreenNotificationUtil;
import com.android.deskclock.util.NotificationUtil;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.ShutdownAlarm;
import com.android.deskclock.util.TimeUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import miuix.android.content.MiuiIntent;

/* JADX INFO: loaded from: classes.dex */
public class AlarmService extends Service {
    private static final String DEFAULT_ALARM_TIMEOUT = "10";
    public static final String KEY_ALARM_USER_ID = "alarm_user_id";
    private static final int MSG_ALERT_TIMEOUT = 1000;
    private static final int STALE_WINDOW = 1800000;
    private static final String TAG = "DC:AlarmService";
    private static int mAlarmUserId = 0;
    private static Alarm mCurrentAlarm = null;
    private static int mCurrentUserId = 0;
    private static boolean mMiWearableExist = false;
    private static MiWearableHelper mMiWearableHelper = null;
    private static boolean mTimerAlarming = false;
    private static boolean sCreated = false;
    private IAlarmConsoleV1Callback mAlarmConsoleV1Callback;
    private AlarmKlaxon mAlarmKlaxon;
    private int mCurrentCallState;
    private SharedPreferences mDefaultPreferences;
    private BroadcastReceiver mKlaxonReceiver;
    private NotificationMonitor mNotificationMonitor;
    private int mOrientation;
    private int mScreenWidthDp;
    private boolean mIsAlarmAscending = true;
    private int mAlertTimeout = 10;
    boolean isSameSpace = true;
    private Handler mHandler = new Handler(new Handler.Callback() { // from class: com.android.deskclock.alarm.alert.AlarmService.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (message.what != 1000) {
                return false;
            }
            AlarmService.this.handleAlertTimeout((Alarm) message.obj);
            return false;
        }
    });

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean created() {
        return sCreated;
    }

    @Override // android.app.Service
    public void onCreate() {
        Log.f(TAG, "onCreate()");
        sCreated = true;
        AlarmAlertWakeLock.acquireCpuWakeLock(this);
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppContext());
        this.mDefaultPreferences = defaultSharedPreferences;
        this.mIsAlarmAscending = defaultSharedPreferences.getBoolean("alarm_ascending_mode", true);
        Log.i(TAG, "support alarm volume ascend: " + this.mIsAlarmAscending);
        try {
            this.mAlertTimeout = Integer.parseInt(this.mDefaultPreferences.getString("auto_silence", "10"));
        } catch (Exception unused) {
            this.mAlertTimeout = 10;
        }
        Log.i(TAG, "alert timeout: " + this.mAlertTimeout + "min");
        this.mAlarmKlaxon = new AlarmKlaxon();
        this.mKlaxonReceiver = new KlaxonReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_BACKGROUND");
        intentFilter.addAction(AlarmHelper.ALARM_STOP_ACTION);
        intentFilter.addAction(AlarmHelper.ACTION_ALARM_DISMISS);
        intentFilter.addAction(AlarmHelper.ACTION_TIMER_DISMISS);
        intentFilter.addAction(AlarmHelper.FUNCTION_TIMER_DISMISS);
        intentFilter.addAction(AlarmHelper.ACTION_ALARM_SNOOZE_FROM_ADDITIONS);
        intentFilter.addAction(AlarmHelper.ACTION_ALARM_DISMISS_FROM_ADDITIONS);
        intentFilter.addAction(AlarmHelper.ACTION_ALARM_SNOOZE);
        intentFilter.addAction(MiuiIntent.ACTION_KEYCODE_EXTERNAL);
        intentFilter.addAction(BroadcastReceiverActions.ACTION_SUB_BACK_SCREEN_ALARM_DISMISS);
        intentFilter.addAction(BroadcastReceiverActions.ACTION_SUB_BACK_SCREEN_ALARM_SNOOZE);
        intentFilter.addAction(BroadcastReceiverActions.ACTION_SUB_BACK_SCREEN_ALARM_CANCEL);
        intentFilter.addAction(BroadcastReceiverActions.ACTION_SUB_BACK_SCREEN);
        intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        intentFilter.setPriority(1000);
        if (Build.VERSION.SDK_INT >= 34) {
            registerReceiver(this.mKlaxonReceiver, intentFilter, 2);
        } else {
            registerReceiver(this.mKlaxonReceiver, intentFilter);
        }
        NotificationMonitor notificationMonitor = new NotificationMonitor();
        this.mNotificationMonitor = notificationMonitor;
        notificationMonitor.registerSystemService(this, new ComponentName(getPackageName(), getClass().getCanonicalName()));
        this.mScreenWidthDp = getResources().getConfiguration().screenWidthDp;
        this.mOrientation = getResources().getConfiguration().orientation;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        Alarm currentAlarm;
        Log.f(TAG, "onStartCommand triggered");
        if (intent == null || intent.getAction() == null) {
            Log.f(TAG, "onStartCommand stopped: intent or intent action is null");
            handleInvalidData();
            return 2;
        }
        String action = intent.getAction();
        if (Util.atLeastU()) {
            checkAlarmUserSpace(intent);
        }
        Log.f(TAG, "action: " + action);
        if (AlarmHelper.ALARM_ALERT_ACTION.equals(action)) {
            Alarm alarmFromRawDataIntent = AlarmHelper.parseAlarmFromRawDataIntent(intent);
            if (alarmFromRawDataIntent != null) {
                Log.f(TAG, "coming alarm: " + alarmFromRawDataIntent.toString());
                handleAlarm(alarmFromRawDataIntent, this.isSameSpace);
            } else {
                Log.f(TAG, "onStartCommand stopped: alarm is null");
                handleInvalidData();
            }
        } else if (TimerDao.TIMER_ALERT_ACTION.equals(action)) {
            Log.f(TAG, "coming timer");
            if (isAlarmAlert() && (currentAlarm = getCurrentAlarm()) != null) {
                AlarmHelper.tryDeleteOneshotAlarm(DeskClockApp.getAppDEContext(), currentAlarm);
            }
            Alarm alarm = new Alarm();
            alarm.id = -2;
            alarm.vibrate = false;
            alarm.alert = TimerDao.getTimerRingtone();
            if (intent.hasExtra(AlarmHelper.ACTION_TIMER_NAME)) {
                alarm.label = intent.getStringExtra(AlarmHelper.ACTION_TIMER_NAME);
            }
            handleTimer(alarm);
        } else {
            Log.f(TAG, "onStartCommand stopped: not alarm/timer alert action, ignore");
            handleInvalidData();
        }
        return 2;
    }

    private void checkAlarmUserSpace(Intent intent) {
        mAlarmUserId = Util.getUserId(this);
        mCurrentUserId = Util.getCurrentUser();
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(this);
        int intExtra = intent.getIntExtra(KEY_ALARM_USER_ID, mCurrentUserId);
        Log.d(TAG, "alarmUserIdFromExtra: " + intExtra);
        Log.d(TAG, "Util.getUserId(this): " + mAlarmUserId + " Util.getCurrentUser(): " + mCurrentUserId);
        if (intExtra != mCurrentUserId) {
            defaultSharedPreferences.edit().putInt(KEY_ALARM_USER_ID, intExtra).apply();
            defaultSharedPreferences.edit().putInt(AlarmSettingsActivity.KEY_ALARM_APACE_SNOOZE, intent.getIntExtra(AlarmSettingsActivity.KEY_ALARM_APACE_SNOOZE, Integer.MIN_VALUE)).apply();
            this.isSameSpace = false;
        }
        if (mAlarmUserId != mCurrentUserId) {
            defaultSharedPreferences.edit().putInt(KEY_ALARM_USER_ID, mAlarmUserId).apply();
            defaultSharedPreferences.edit().putInt(AlarmSettingsActivity.KEY_ALARM_APACE_SNOOZE, Util.getSnoozeMinutes(this)).apply();
            intent.putExtra(KEY_ALARM_USER_ID, mAlarmUserId);
            intent.putExtra(AlarmSettingsActivity.KEY_ALARM_APACE_SNOOZE, Util.getSnoozeMinutes(this));
            Log.f(TAG, "componentName：" + startForegroundServiceAsUser(this, intent, Util.getCurrentUserHandle(mCurrentUserId)));
        }
    }

    private boolean isAlarmAlert() {
        try {
            int i = Settings.Global.getInt(DeskClockApp.getAppDEContext().getContentResolver(), AlarmHelper.ALARM_ALERT_STATUS);
            Alarm currentAlarm = getCurrentAlarm();
            Log.d(TAG, "onChange status :" + i);
            return i == 1 && currentAlarm != null;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void handleInvalidData() {
        startForeground(-6, NotificationUtil.buildMarkNotification(this, getString(R.string.update_notification_info)));
        dismissMiWearable();
        stopSelf();
    }

    private void handleAlarm(Alarm alarm, boolean z) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis > alarm.time + AlarmHelper.UPDATE_WEATHER_DURATION) {
            Log.f(TAG, "trigger alarm 30 minutes overtime, ignore");
            handleInvalidData();
            return;
        }
        if (AlarmHelper.UPDATE_WEATHER_DURATION + jCurrentTimeMillis < alarm.time) {
            Log.f(TAG, "trigger alarm 30 minutes before, ignore");
            handleInvalidData();
            return;
        }
        Log.f(TAG, "handleAlarm: " + alarm);
        sendBackScreenNotification(alarm);
        showForegroundNotification(alarm);
        if (alarm.type == 0) {
            AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).updateAppSearchAlarmFiring(alarm, true);
        }
        if (Util.isSupportColorfulLight() && ReflectUtil.isSupportBackStrap(this) && ReflectUtil.isStripLightEnable(this)) {
            AlarmColorLightManager.setColorfulLight(this, 4);
        }
        mCurrentAlarm = alarm;
        if (z && !Util.stopAlertForBackScreenDemo(DeskClockApp.getAppDEContext())) {
            Log.d("play mCurrentAlarm: " + mCurrentAlarm);
            play(mCurrentAlarm);
        }
        PrefUtil.setRecentAlarmAlertTime(jCurrentTimeMillis);
        recordAlarmTime(alarm.id, alarm.time, jCurrentTimeMillis, ((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode());
        AlarmHelper.disableSnoozeAlert(this, alarm.id, false);
        if (!alarm.daysOfWeek.isRepeatSet() && alarm.type == 0) {
            AlarmHelper.enableAlarm(this, alarm.id, false);
        } else {
            DeskClockApp.getAppContext().getContentResolver().notifyChange(ShiftAlarm.Columns.CONTENT_URI, null);
            AlarmHelper.setNextAlert(this);
        }
        if (alarm.id == Integer.MIN_VALUE) {
            BedtimeUtil.doInWakeTime(this);
        }
        mMiWearableExist = AdditionUtil.isMiWearableSupport();
        Log.f(TAG, "mi wearable exist: " + mMiWearableExist);
        if (mMiWearableExist) {
            bindMiWearableService();
        }
    }

    private void handleTimer(Alarm alarm) {
        sendBackScreenNotification(alarm);
        showForegroundNotification(alarm);
        AppSearchUtil.getInstance(DeskClockApp.getAppDEContext()).updateAppSearchTimerToExpired(TimerDao.getTimer(DeskClockApp.getAppDEContext()));
        if (Util.isSupportColorfulLight() && ReflectUtil.isSupportBackStrap(DeskClockApp.getAppDEContext()) && ReflectUtil.isStripLightEnable(DeskClockApp.getAppDEContext())) {
            AlarmColorLightManager.setColorfulLight(DeskClockApp.getAppDEContext(), 4);
        }
        setTimerAlarming(true);
        mCurrentAlarm = alarm;
        if (!Util.stopAlertForBackScreenDemo(DeskClockApp.getAppDEContext())) {
            play(mCurrentAlarm);
        }
        Intent intent = new Intent();
        intent.setPackage(BuildConfig.APPLICATION_ID);
        intent.setAction("action.timer_off");
        sendBroadcast(intent);
        StatHelper.alarmEvent("timer_alert");
        dismissMiWearable();
    }

    public static void setTimerAlarming(boolean z) {
        mTimerAlarming = z;
    }

    public static boolean getTimerAlarming() {
        return mTimerAlarming;
    }

    private void showForegroundNotification(Alarm alarm) {
        Notification alarmAlertNotification;
        Context contextCreateDeviceProtectedStorageContext;
        try {
            sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        } catch (Throwable th) {
            th.printStackTrace();
        }
        if (alarm.id == -2) {
            Log.f(TAG, "require timer screen");
            alarmAlertNotification = NotificationUtil.getTimerAlertNotification(this, alarm);
        } else {
            Log.f(TAG, "require alarm screen");
            AlarmUtils.alarmAlertStatus = true;
            AlarmUtils.alarmRingForXiaoAi = true;
            try {
                Settings.Global.putInt(getContentResolver(), AlarmHelper.ALARM_ALERT_STATUS, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            alarmAlertNotification = NotificationUtil.getAlarmAlertNotification(this, alarm);
        }
        Log.d(TAG, "getAlertNotification: " + alarmAlertNotification);
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(alarm.id, alarmAlertNotification, 1024);
        }
        boolean zInKeyguardRestrictedInputMode = ((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode();
        if (FBEUtil.isUserLocked(this)) {
            Log.f(TAG, " isUserLocked ");
            contextCreateDeviceProtectedStorageContext = FBEUtil.createDeviceProtectedStorageContext(this);
        } else {
            contextCreateDeviceProtectedStorageContext = this;
        }
        boolean zIsContextUserForeground = isContextUserForeground();
        Log.f(TAG, "showForegroundNotification: isLocked= " + zInKeyguardRestrictedInputMode + ", foreground=" + zIsContextUserForeground);
        if (zInKeyguardRestrictedInputMode || !zIsContextUserForeground) {
            Log.f(TAG, "extra Activity request for: context= " + contextCreateDeviceProtectedStorageContext);
            startActivity(contextCreateDeviceProtectedStorageContext, alarm);
        }
    }

    private void sendBackScreenNotification(Alarm alarm) {
        if (alarm.id == -2) {
            Log.f(TAG, "send timer back screen notification");
            if (isAlarmAlert()) {
                BackScreenNotificationUtil.clearAlarmNotification(this);
            }
            BackScreenNotificationUtil.sendTimerAlertNotification(this);
            return;
        }
        Log.f(TAG, "send alarm back screen notification");
        BackScreenNotificationUtil.sendAlarmAlertNotification(this, alarm);
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.deskclock.alarm.alert.AlarmService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m82xdef692c8();
            }
        }, 250L);
    }

    /* JADX INFO: renamed from: lambda$sendBackScreenNotification$0$com-android-deskclock-alarm-alert-AlarmService, reason: not valid java name */
    /* synthetic */ void m82xdef692c8() {
        if (mTimerAlarming) {
            BackScreenNotificationUtil.clearTimerNotification(this);
        }
    }

    private boolean isContextUserForeground() {
        int userId = CompatUtil.getUserId(this);
        int currentUser = CompatUtil.getCurrentUser();
        return userId == Integer.MIN_VALUE || currentUser == Integer.MIN_VALUE || userId == currentUser;
    }

    /* JADX WARN: Type inference failed for: r4v2, types: [com.android.deskclock.alarm.alert.AlarmService$2] */
    private void startActivity(final Context context, Alarm alarm) {
        final Intent intent = new Intent(this, (Class<?>) AlarmAlertFullScreenActivity.class);
        intent.putExtra(AlarmHelper.ALARM_INTENT_EXTRA, alarm);
        intent.setFlags(268697600);
        new AsyncTask<Void, Void, Boolean>() { // from class: com.android.deskclock.alarm.alert.AlarmService.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Boolean doInBackground(Void... voidArr) {
                return Boolean.valueOf(CompatUtil.startActivityAsUser(context, intent, ClockCompat.UserHandle_CURRENT));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Boolean bool) {
                if (bool.booleanValue()) {
                    return;
                }
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    Log.e(AlarmService.TAG, "Start activity failed", e);
                }
            }
        }.execute(new Void[0]);
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.f(TAG, "AlarmService onDestroy start");
        sCreated = false;
        setTimerAlarming(false);
        AlarmUtils.alarmAlertStatus = false;
        try {
            Settings.Global.putInt(getContentResolver(), AlarmHelper.ALARM_ALERT_STATUS, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        Alarm alarm = mCurrentAlarm;
        if (alarm != null && alarm.id != -2) {
            Alarm alarm2 = mCurrentAlarm;
            StatHelper.recordAlarmFires(this, alarm2, (jCurrentTimeMillis - alarm2.time) / 1000);
            Alarm alarm3 = mCurrentAlarm;
            OneTrackStatHelper.trackAlarmFiresEvent(this, alarm3, (jCurrentTimeMillis - alarm3.time) / 1000);
        }
        unregisterReceiver(this.mKlaxonReceiver);
        stopForeground(true);
        stop();
        release();
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        NotificationMonitor notificationMonitor = this.mNotificationMonitor;
        if (notificationMonitor != null) {
            notificationMonitor.unregisterAsSystemService();
        }
        AlarmAlertWakeLock.releaseCpuLock();
        MiWearableHelper miWearableHelper = mMiWearableHelper;
        if (miWearableHelper != null) {
            miWearableHelper.release();
            mMiWearableHelper = null;
        }
        mCurrentAlarm = null;
        AlarmHelper.releaseMiuiWallpaperManager();
        Log.f(TAG, "AlarmService onDestroy finish in " + (System.currentTimeMillis() - jCurrentTimeMillis) + "ms");
    }

    private void play(Alarm alarm) {
        Log.f(TAG, "start AlarmService#play");
        this.mAlarmKlaxon.start(this, alarm);
        registerTimeoutHandler(alarm);
    }

    public void stop() {
        Log.f(TAG, "AlarmService#stop start");
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeMessages(1000);
        }
        this.mAlarmKlaxon.stop(this);
        sendBroadcast(new Intent(AlarmHelper.ALARM_DONE_ACTION));
        Log.f(TAG, "AlarmService#stop done");
    }

    public void release() {
        this.mAlarmKlaxon.release(this);
    }

    private void registerTimeoutHandler(Alarm alarm) {
        this.mHandler.removeMessages(1000);
        if (this.mAlertTimeout != -1) {
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(1000, alarm), this.mAlertTimeout * 60000);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAlertTimeout(Alarm alarm) {
        Log.f(TAG, "dismiss/snooze alarm/timer with timeout");
        Context applicationContext = getApplicationContext();
        if (alarm.id == -2) {
            AlarmHelper.dismissTimer(applicationContext);
        } else {
            int i = Integer.parseInt(AlarmSettingsActivity.getRemindSnoozeRepeatCount());
            if (i > 0) {
                AlarmHelper.snoozeAlarm(applicationContext, alarm);
                notifyMiWearable(false, alarm);
                AlarmSettingsActivity.updateSnoozeRepeatCountRemind(String.valueOf(i - 1));
            } else {
                AlarmHelper.dismissAlarm(applicationContext, alarm);
                notifyMiWearable(true, alarm);
                AlarmSettingsActivity.resetSnoozeRepeatCountRemind();
            }
        }
        AlarmHelper.notifyToFinishAlertUI(applicationContext);
    }

    private static class KlaxonReceiver extends BroadcastReceiver {
        private WeakReference<AlarmService> mReference;

        public KlaxonReceiver(AlarmService alarmService) {
            this.mReference = new WeakReference<>(alarmService);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            WeakReference<AlarmService> weakReference;
            Alarm alarm = intent.hasExtra(AlarmHelper.ALARM_INTENT_EXTRA) ? (Alarm) intent.getParcelableExtra(AlarmHelper.ALARM_INTENT_EXTRA) : null;
            if (intent.hasExtra(MiWearableHelper.CONSOLE_ALARM_ID_INTENT_EXTRA)) {
                int intExtra = intent.getIntExtra(MiWearableHelper.CONSOLE_ALARM_ID_INTENT_EXTRA, -1);
                Alarm currentAlarm = AlarmService.getCurrentAlarm();
                if (currentAlarm != null && intExtra == currentAlarm.id) {
                    alarm = currentAlarm;
                }
            }
            String action = intent.getAction();
            SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(context);
            if (Util.atLeastU()) {
                int intExtra2 = intent.getIntExtra(AlarmService.KEY_ALARM_USER_ID, Integer.MIN_VALUE);
                Log.d(AlarmService.TAG, "alarmUserIdFromExtra: " + intExtra2);
                if (intExtra2 != Integer.MIN_VALUE) {
                    defaultSharedPreferences.edit().putInt(AlarmService.KEY_ALARM_USER_ID, intExtra2).apply();
                }
            }
            Log.f(AlarmService.TAG, "AlarmService receive: " + action + "  getUserId(context): " + Util.getUserId(context) + "  actionAlarm: " + alarm);
            if ("android.intent.action.USER_BACKGROUND".equals(action)) {
                AlarmHelper.stopAlarmKlaxon(context);
                AlarmHelper.notifyToFinishAlertUI(context);
                return;
            }
            if (AlarmHelper.ALARM_STOP_ACTION.equals(action)) {
                AlarmHelper.stopAlarmKlaxon(context);
                return;
            }
            if (AlarmHelper.ACTION_ALARM_DISMISS.equals(action)) {
                WeakReference<AlarmService> weakReference2 = this.mReference;
                if (weakReference2 == null || weakReference2.get() == null) {
                    return;
                }
                this.mReference.get().handleAlarmDismiss(context, alarm);
                return;
            }
            if (AlarmHelper.ACTION_TIMER_DISMISS.equals(action)) {
                Log.f(AlarmService.TAG, "dismiss timer from Notification");
                AlarmHelper.dismissTimer(context);
                AlarmHelper.notifyToFinishAlertUI(context);
                return;
            }
            if (AlarmHelper.FUNCTION_TIMER_DISMISS.equals(action)) {
                Log.f(AlarmService.TAG, "dismiss timer from Gemini");
                AlarmHelper.dismissTimer(context);
                AlarmHelper.notifyToFinishAlertUI(context);
                return;
            }
            if (AlarmHelper.ACTION_ALARM_SNOOZE_FROM_ADDITIONS.equals(action)) {
                if (alarm != null) {
                    Log.f(AlarmService.TAG, "snooze alarm from MiWearable");
                    try {
                        AlarmHelper.snoozeAlarm(context, alarm);
                        AlarmHelper.notifyToFinishAlertUI(context);
                        return;
                    } catch (Exception e) {
                        Log.e(AlarmService.TAG, "handle alarm_snooze_additions error : " + e.getMessage());
                        return;
                    }
                }
                return;
            }
            if (AlarmHelper.ACTION_ALARM_DISMISS_FROM_ADDITIONS.equals(action)) {
                if (alarm == null || alarm.id == -2) {
                    return;
                }
                Log.f(AlarmService.TAG, "dismiss alarm from MiWearable");
                try {
                    AlarmHelper.dismissAlarm(context, alarm);
                    AlarmSettingsActivity.resetSnoozeRepeatCountRemind();
                    AlarmHelper.notifyToFinishAlertUI(context);
                    return;
                } catch (Exception e2) {
                    Log.e(AlarmService.TAG, "handle alarm_dismiss_additions error : " + e2.getMessage());
                    return;
                }
            }
            if (AlarmHelper.ACTION_ALARM_SNOOZE.equals(action)) {
                if (alarm != null) {
                    AlarmService.snoozeAlarmByIntent(context, intent, alarm, defaultSharedPreferences);
                    return;
                }
                return;
            }
            if (action.equals(MiuiIntent.ACTION_KEYCODE_EXTERNAL)) {
                Log.f(AlarmService.TAG, "power or volume key pressed");
                if (AlarmService.mCurrentAlarm == null || (weakReference = this.mReference) == null || weakReference.get() == null) {
                    return;
                }
                this.mReference.get().snoozeOrDismissAlarmByKey(context, AlarmService.mCurrentAlarm);
                return;
            }
            if ("android.intent.action.LOCALE_CHANGED".equals(action)) {
                if (AlarmService.mCurrentAlarm == null || AlarmService.mCurrentAlarm.id == -2) {
                    return;
                }
                NotificationUtil.showAlarmAlertNotification(context, AlarmService.mCurrentAlarm);
                return;
            }
            if (BroadcastReceiverActions.ACTION_SUB_BACK_SCREEN_ALARM_DISMISS.equals(action)) {
                try {
                    int intExtra3 = intent.getIntExtra(MonitorJobScheduler.ALARM_ID, -1);
                    Log.f(AlarmService.TAG, "action is action.alarm_dismiss_sub_back_screen\nalarmId is " + intExtra3);
                    Alarm currentAlarm2 = AlarmService.getCurrentAlarm();
                    if (currentAlarm2 != null && intExtra3 == currentAlarm2.id) {
                        WeakReference<AlarmService> weakReference3 = this.mReference;
                        if (weakReference3 != null && weakReference3.get() != null) {
                            this.mReference.get().handleAlarmDismiss(context, currentAlarm2);
                        }
                    } else {
                        Log.i(AlarmService.TAG, "the id is not match");
                    }
                    return;
                } catch (Exception e3) {
                    Log.e(AlarmService.TAG, "handle alarm_dismiss_additions error : " + e3);
                    return;
                }
            }
            if (BroadcastReceiverActions.ACTION_SUB_BACK_SCREEN_ALARM_SNOOZE.equals(action)) {
                try {
                    int intExtra4 = intent.getIntExtra(MonitorJobScheduler.ALARM_ID, -1);
                    Log.f(AlarmService.TAG, "action is action.alarm_snooze_sub_back_screen\nalarmId is " + intExtra4);
                    Alarm currentAlarm3 = AlarmService.getCurrentAlarm();
                    if (currentAlarm3 != null && intExtra4 == currentAlarm3.id) {
                        AlarmService.snoozeAlarmByIntent(context, intent, currentAlarm3, defaultSharedPreferences);
                    } else {
                        Log.i(AlarmService.TAG, "the id is not match");
                    }
                    return;
                } catch (Exception e4) {
                    Log.e(AlarmService.TAG, "handle alarm_snooze_additions error : " + e4.getMessage());
                    return;
                }
            }
            if (BroadcastReceiverActions.ACTION_SUB_BACK_SCREEN.equals(action)) {
                Log.f(AlarmService.TAG, "dismiss timer or alarm from ACTION_SUB_BACK_SCREEN");
                try {
                    ArrayList<Bundle> parcelableArrayListExtra = intent.getParcelableArrayListExtra("notificationInfos");
                    if (parcelableArrayListExtra != null && !parcelableArrayListExtra.isEmpty()) {
                        for (Bundle bundle : parcelableArrayListExtra) {
                            int i = bundle.getInt("clockType", -1);
                            Log.d(AlarmService.TAG, "the alertType is " + i);
                            if (i == 0) {
                                int i2 = bundle.getInt(MonitorJobScheduler.ALARM_ID, -1);
                                Log.d(AlarmService.TAG, "cancel alarm by sub back screen id is " + i2);
                                Alarm currentAlarm4 = AlarmService.getCurrentAlarm();
                                if (i2 != -1 && currentAlarm4 != null && i2 == currentAlarm4.id) {
                                    Log.d(AlarmService.TAG, "cancel alarm by sub back screen success !");
                                    WeakReference<AlarmService> weakReference4 = this.mReference;
                                    if (weakReference4 != null && weakReference4.get() != null) {
                                        this.mReference.get().handleAlarmDismiss(context, currentAlarm4);
                                    }
                                }
                            } else if (i == 1) {
                                AlarmHelper.dismissTimer(context);
                                AlarmHelper.notifyToFinishAlertUI(context);
                            }
                        }
                        return;
                    }
                    Log.d(AlarmService.TAG, "notificationInfos is empty");
                } catch (Exception e5) {
                    Log.e(AlarmService.TAG, "handle ACTION_SUB_BACK_SCREEN error : " + e5.getMessage());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void snoozeAlarmByIntent(Context context, Intent intent, Alarm alarm, SharedPreferences sharedPreferences) {
        if (Util.atLeastU()) {
            int currentUser = Util.getCurrentUser();
            mCurrentUserId = currentUser;
            mAlarmUserId = sharedPreferences.getInt(KEY_ALARM_USER_ID, currentUser);
            Log.f(TAG, "update userId from sp mAlarmUserId : " + mAlarmUserId + "   mCurrentUserId: " + mCurrentUserId);
            if (mAlarmUserId != mCurrentUserId) {
                sharedPreferences.edit().putInt(KEY_ALARM_USER_ID, mCurrentUserId).apply();
                AlarmHelper.snoozeAlarmAsUser(mAlarmUserId, context, alarm);
                context.stopService(intent);
                return;
            } else {
                try {
                    AlarmHelper.snoozeAlarm(context, alarm);
                    AlarmHelper.notifyToFinishAlertUI(context);
                    notifyMiWearable(false, alarm);
                    return;
                } catch (Exception e) {
                    Log.e(TAG, "handle alarm_snooze error : " + e);
                    return;
                }
            }
        }
        try {
            AlarmHelper.snoozeAlarm(context, alarm);
            AlarmHelper.notifyToFinishAlertUI(context);
            notifyMiWearable(false, alarm);
        } catch (Exception e2) {
            Log.e(TAG, "handle alarm_snooze error : " + e2.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void snoozeOrDismissAlarmByKey(Context context, Alarm alarm) {
        String string = FBEUtil.getDefaultSharedPreferences(context).getString("volume_button_setting", "1");
        int i = (alarm.id != -2 || Integer.parseInt(string) == 0) ? Integer.parseInt(string) : 2;
        if (i == 1) {
            try {
                Log.f(TAG, "snooze with physical key pressed");
                AlarmHelper.snoozeAlarm(context, alarm);
                AlarmHelper.notifyToFinishAlertUI(context);
                notifyMiWearable(false, alarm);
                StatHelper.alarmEvent(StatHelper.EVENT_ALARM_SNOOZED_BY_KEY);
                OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_SNOOZED_BY_KEY_CLICK);
                return;
            } catch (Exception e) {
                Log.e(TAG, "snooze with physical key pressed error : " + e.getMessage());
                return;
            }
        }
        if (i != 2) {
            return;
        }
        try {
            Log.f(TAG, "dismiss with physical key pressed");
            if (alarm.id == -2) {
                Intent intent = new Intent(AlarmHelper.ACTION_KEY_DISMISS_TIMER_TO_XIAOAI);
                intent.setPackage("com.miui.voiceassist");
                sendBroadcast(intent);
                Log.f(TAG, "send broadcast dismiss timer to xiaoai");
                AlarmHelper.dismissTimer(context);
                AlarmHelper.notifyToFinishAlertUI(context);
            } else {
                Intent intent2 = new Intent(AlarmHelper.ACTION_KEY_DISMISS_ALARM_TO_XIAOAI);
                intent2.setPackage("com.miui.voiceassist");
                sendBroadcast(intent2);
                Log.f(TAG, "send broadcast dismiss alarm to xiaoai");
                handleAlarmDismiss(context, alarm);
            }
        } catch (Exception e2) {
            Log.e(TAG, "dismiss with physical key pressed error : " + e2.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAlarmDismiss(final Context context, final Alarm alarm) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.android.deskclock.alarm.alert.AlarmService.3
                @Override // java.lang.Runnable
                public void run() {
                    Log.f(AlarmService.TAG, "dismiss alarm from Notification:" + alarm);
                    Alarm alarm2 = alarm;
                    if (alarm2 == null || alarm2.id == -2) {
                        return;
                    }
                    AlarmHelper.dismissAlarm(context, alarm);
                    AlarmSettingsActivity.resetSnoozeRepeatCountRemind();
                    AlarmHelper.notifyToFinishAlertUI(context);
                    AlarmService.notifyMiWearable(true, alarm);
                }
            });
        }
    }

    private void recordAlarmTime(int i, long j, long j2, boolean z) {
        StatHelper.trackEvent(StatHelper.KEY_ALARM_ALERT_TIME, TimeUtil.getFormatTime(j2, "HH:mm"));
        OneTrackStatHelper.trackNumEvent(j2, OneTrackStatHelper.ALERT_TIME);
        if (AlarmHelper.hasAlarmBeenSnoozed(FBEUtil.getSharedPreferences(this, AlarmClockFragment.PREFERENCES, 0), i)) {
            StatHelper.alarmEvent(StatHelper.KEY_SNOOZED_ALARM_ALERT_PLAY);
            OneTrackStatHelper.trackTriggerEvent(OneTrackStatHelper.ALARM_SNOOZED_ALERT);
        }
        reportAlarmAlertTimely(j, j2);
        MonitorHelper.alert(i, j, j2, z);
    }

    private void reportAlarmAlertTimely(long j, long j2) {
        long j3 = j2 - j;
        long bootTime = PrefUtil.getBootTime();
        if (bootTime > 0) {
            if (ShutdownAlarm.getShutdownAlarmClockOffset(DeskClockApp.getAppDEContext()) == 300) {
                boolean z = j < bootTime + 600000;
                StatHelper.recordTimeStringProperty(StatHelper.CATEGORY_ALARM_PLAY, z ? StatHelper.KEY_SHUT_DOWN_ALARM_DELAY_NEW : StatHelper.KEY_ALARM_DELAY_NEW, j3);
                OneTrackStatHelper.trackNumEvent(j3, z ? OneTrackStatHelper.ALERT_SHUTDOWN_ALARM_DELAY : OneTrackStatHelper.ALERT_DELAY);
                return;
            }
            StatHelper.recordTimeStringProperty(StatHelper.CATEGORY_ALARM_PLAY, j < bootTime + 300000 ? StatHelper.KEY_SHUT_DOWN_ALARM_DELAY : StatHelper.KEY_ALARM_DELAY, j3);
        }
    }

    private void bindMiWearableService() {
        Log.f(TAG, "bindMiWearableService");
        dismissMiWearable();
        if (mMiWearableHelper == null) {
            mMiWearableHelper = new MiWearableHelper(this);
            IAlarmConsoleV1Callback.Stub stub = new IAlarmConsoleV1Callback.Stub() { // from class: com.android.deskclock.alarm.alert.AlarmService.4
                @Override // com.android.deskclock.console.IAlarmConsoleV1Callback
                public void snooze(int i) throws RemoteException {
                    Log.f(AlarmService.TAG, "snooze alarm from MiWearabel");
                    Intent intent = new Intent(AlarmHelper.ACTION_ALARM_SNOOZE_FROM_ADDITIONS);
                    intent.putExtra(MiWearableHelper.CONSOLE_ALARM_ID_INTENT_EXTRA, i);
                    intent.setPackage(BuildConfig.APPLICATION_ID);
                    AlarmService.this.sendBroadcast(intent);
                    StatHelper.alarmEvent(StatHelper.EVENT_MI_WEARABLE_SNOOZE);
                    OneTrackStatHelper.trackTriggerEvent(OneTrackStatHelper.XIAOMI_WEARABLE_SNOOZE);
                }

                @Override // com.android.deskclock.console.IAlarmConsoleV1Callback
                public void dismiss(int i) throws RemoteException {
                    Log.f(AlarmService.TAG, "dismiss alarm from MiWearabel");
                    Intent intent = new Intent(AlarmHelper.ACTION_ALARM_DISMISS_FROM_ADDITIONS);
                    intent.setPackage(BuildConfig.APPLICATION_ID);
                    intent.putExtra(MiWearableHelper.CONSOLE_ALARM_ID_INTENT_EXTRA, i);
                    AlarmService.this.sendBroadcast(intent);
                    StatHelper.alarmEvent(StatHelper.EVENT_MI_WEARABLE_DISMISS);
                    OneTrackStatHelper.trackTriggerEvent(OneTrackStatHelper.XIAOMI_WEARABLE_DISMISS);
                }
            };
            this.mAlarmConsoleV1Callback = stub;
            mMiWearableHelper.setCallBack(stub);
        }
        Alarm alarm = mCurrentAlarm;
        if (alarm != null) {
            mMiWearableHelper.notifyMiWearableAlert(alarm.id, mCurrentAlarm.time, mCurrentAlarm.label);
        }
    }

    public static Alarm getCurrentAlarm() {
        return mCurrentAlarm;
    }

    public static void notifyMiWearable(boolean z, Alarm alarm) {
        MiWearableHelper miWearableHelper;
        Log.f(TAG, "notifyMiWearable, mMiWearableExist: " + mMiWearableExist + " alarm: " + alarm);
        if (!mMiWearableExist || (miWearableHelper = mMiWearableHelper) == null || alarm == null) {
            return;
        }
        if (!z) {
            mMiWearableHelper.notifyMiWearable(false, alarm.id, System.currentTimeMillis() + ((long) (Util.getSnoozeMinutes(DeskClockApp.getAppDEContext()) * 60000)), alarm.label);
        } else {
            miWearableHelper.notifyMiWearable(true, alarm.id, alarm.time, alarm.label);
        }
    }

    private void dismissMiWearable() {
        MiWearableHelper miWearableHelper;
        if (!mMiWearableExist || (miWearableHelper = mMiWearableHelper) == null) {
            return;
        }
        miWearableHelper.directlyDismiss();
    }

    public ComponentName startForegroundServiceAsUser(ContextWrapper contextWrapper, Intent intent, UserHandle userHandle) {
        try {
            Method method = ContextWrapper.class.getMethod("startForegroundServiceAsUser", Intent.class, UserHandle.class);
            method.setAccessible(true);
            return (ComponentName) method.invoke(contextWrapper, intent, userHandle);
        } catch (Exception e) {
            Log.d(TAG, "startForegroundServiceAsUser Exception:" + e);
            return null;
        }
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Log.f(TAG, "onConfigurationChanged ----------------------------------");
        if (Settings.System.getInt(getContentResolver(), "lock_screen_after_fold_screen", 1) == 1 && configuration.screenWidthDp < this.mScreenWidthDp && configuration.screenWidthDp < 600 && Util.isFoldDeviceByType(this) && !Util.isInInternalScreen(this) && configuration.orientation == this.mOrientation) {
            Log.f(TAG, "lock screen");
            Alarm alarm = mCurrentAlarm;
            if (alarm != null) {
                snoozeOrDismissAlarmByKey(this, alarm);
                return;
            }
        }
        this.mScreenWidthDp = configuration.screenWidthDp;
        this.mOrientation = configuration.orientation;
    }
}
