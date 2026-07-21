package com.android.deskclock.alarm.alert;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.deskclock.Alarm;
import com.android.deskclock.BuildConfig;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.R2;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.weather.WeatherUtils;
import com.android.deskclock.alarm.AlarmColorLightManager;
import com.android.deskclock.alarm.ReflectUtil;
import com.android.deskclock.alarm.bedtime.HealthDataUtil;
import com.android.deskclock.compat.ClockCompat;
import com.android.deskclock.timer.AlertTimerFragment;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Notification.BackScreenNotificationUtil;
import com.android.deskclock.util.NotificationUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.miui.miwallpaper.MiuiWallpaperManager;
import java.lang.ref.WeakReference;
import java.util.Objects;
import miuix.appcompat.app.AppCompatActivity;
import miuix.core.util.HyperMaterialUtils;
import miuix.core.util.MaterialConfig;
import miuix.core.util.MiuiBlurUtils;
import miuix.core.util.MiuixUIUtils;
import miuix.theme.token.hypermaterial.Overlay;

/* JADX INFO: loaded from: classes.dex */
public class AlarmAlertFullScreenActivity extends AppCompatActivity implements AlertScreenController.AlertScreenListener {
    private static final String ACTION_SET_FPS = "com.miui.powerkeeper.SET_ACTIVITY_FPS";
    private static final String ALARM_FRAGMENT_TAG = "alarm_fragment_tag";
    private static final String CIRCLE_FLASH_ACTION = "com.huaqin.circleflash.ACTION_CIRCLE_FLASH";
    private static final String CIRCLE_FLASH_CLASS = "com.huaqin.circleflash.CircleFlashService";
    private static final String CIRCLE_FLASH_PACKAGE = "com.huaqin.circleflash";
    private static final String IS_SHOW_LIFE_POST = "is_show_life_post";
    public static final String POWER_PACKAGE_NAME = "com.miui.powerkeeper";
    private static final long SCREEN_ON_DELAY = 500;
    private static final String TAG = "DC:AlarmAlertFullScreenActivity";
    private static final String TIMER_FRAGMENT_TAG = "timer_fragment_tag";
    protected Alarm mAlarm;
    private ViewGroup mAlarmViewRoot;
    private DisplayManager mDisplayManager;
    private Handler mHandler;
    private boolean mIsAlarmDismissed;
    private boolean mNotifyPowerStateFalse;
    private int mPhysicalKeyBehavior;
    private int mRepeatCount;
    private int mRepeatCountReminder;
    private ViewGroup mRoot;
    private int mScreenWidthDp;
    private SetBgAsyncTask mSetBgAsyncTask;
    private SharedPreferences mSharedPreferences;
    private boolean mShouldNotifyPowerState;
    private boolean mShowLifePost;
    private boolean mShowWhenLocked;
    protected Alarm mTimerAlarm;
    private ViewGroup mTimerViewRoot;
    private boolean mHasTurnOnScreen = false;
    private boolean isLocked = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.deskclock.alarm.alert.AlarmAlertFullScreenActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.f(AlarmAlertFullScreenActivity.TAG, "AlarmAlertFullScreenActivity receive: " + action);
            if (AlarmHelper.ACTION_ALERT_UI_DISMISS.equals(action)) {
                if (AlarmAlertFullScreenActivity.this.mHandler != null) {
                    AlarmAlertFullScreenActivity.this.mHandler.post(new Runnable() { // from class: com.android.deskclock.alarm.alert.AlarmAlertFullScreenActivity.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            AlarmAlertFullScreenActivity.this.finish();
                        }
                    });
                }
            } else if (AlarmHelper.ALARM_DISMISS_ACTION.equals(action)) {
                if (AlarmAlertFullScreenActivity.this.mAlarm != null) {
                    AlarmAlertFullScreenActivity.this.dismiss(false, true);
                }
            } else {
                if (!AlarmHelper.ALARM_SNOOZE_ACTION.equals(action) || AlarmAlertFullScreenActivity.this.mAlarm == null || AlarmAlertFullScreenActivity.this.mAlarm.id == -2) {
                    return;
                }
                AlarmAlertFullScreenActivity.this.snooze(true);
            }
        }
    };
    private DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener() { // from class: com.android.deskclock.alarm.alert.AlarmAlertFullScreenActivity.2
        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int i) {
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int i) {
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int i) {
            if (i == 0) {
                AlarmAlertFullScreenActivity alarmAlertFullScreenActivity = AlarmAlertFullScreenActivity.this;
                alarmAlertFullScreenActivity.resetRuyiScreenPadding(alarmAlertFullScreenActivity.mAlarm != null);
            }
        }
    };

    private boolean handleKeyEvent(int i) {
        return i == 24 || i == 25 || i == 27 || i == 80 || i == 164;
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
    }

    private void snoozeOrDismissAlarmByKey() {
        if (this.mIsAlarmDismissed) {
            Log.i(TAG, "alarm has been dismissed, no need to handle again");
            return;
        }
        if (this.mAlarm.id == -2) {
            this.mPhysicalKeyBehavior = 2;
        }
        int i = this.mPhysicalKeyBehavior;
        if (i == 1) {
            Log.f(TAG, "snooze with physical key pressed");
            snooze(true);
            StatHelper.alarmEvent(StatHelper.EVENT_ALARM_SNOOZED_BY_KEY);
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_SNOOZED_BY_KEY_CLICK);
            return;
        }
        if (i != 2) {
            return;
        }
        Log.f(TAG, "dismiss with physical key pressed");
        Alarm alarm = this.mAlarm;
        if (alarm != null && alarm.id == Integer.MIN_VALUE) {
            HealthDataUtil.stopSleepRecord(this);
        }
        AlarmService.notifyMiWearable(true, this.mAlarm);
        dismiss(false, true);
    }

    private void updateSnoozeRepeatCountReminder() {
        SharedPreferences.Editor editorEdit = this.mSharedPreferences.edit();
        editorEdit.putString("snooze_repeat_count_remainder", String.valueOf(this.mRepeatCountReminder));
        editorEdit.apply();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        AlarmHelper.setMiuiWallpaperManager(Util.getMiuiWallpaperManager());
        Log.f(TAG, "getCurrentAlarm ：" + AlarmService.getCurrentAlarm());
        if (Util.isDeviceRuyiOrBixi() && AlarmService.getCurrentAlarm() == null) {
            Log.f(TAG, "getCurrentAlarm is null, finish");
            finish();
        }
        if (!Util.isDeviceCetus()) {
            resetOrientation();
        }
        Log.f(TAG, "AlarmAlertFullScreenActivity onCreate start");
        setVolumeControlStream(4);
        setContentView(R.layout.alarm_alert_fullscreen);
        this.mRoot = (ViewGroup) findViewById(R.id.root);
        this.mShowLifePost = false;
        this.mAlarmViewRoot = (ViewGroup) findViewById(R.id.alarm_alert_fullscreen);
        this.mTimerViewRoot = (ViewGroup) findViewById(R.id.timer_alert_fullscreen);
        this.mPhysicalKeyBehavior = Integer.parseInt(FBEUtil.getDefaultSharedPreferences(this).getString("volume_button_setting", "1"));
        this.mHandler = new Handler();
        getWindow().addFlags(4718721);
        if (Build.VERSION.SDK_INT >= 28) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.layoutInDisplayCutoutMode = 3;
            getWindow().setAttributes(attributes);
        }
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService("keyguard");
        this.mDisplayManager = (DisplayManager) getSystemService("display");
        this.isLocked = keyguardManager.inKeyguardRestrictedInputMode();
        setAlarmAlertFullScreenView(getIntent(), true);
        this.mSharedPreferences = FBEUtil.getDefaultSharedPreferences(this);
        getRepeatCount();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AlarmHelper.ACTION_ALERT_UI_DISMISS);
        intentFilter.addAction(AlarmHelper.ALARM_SNOOZE_ACTION);
        intentFilter.addAction(AlarmHelper.ALARM_DISMISS_ACTION);
        intentFilter.setPriority(1000);
        if (Build.VERSION.SDK_INT >= 34) {
            registerReceiver(this.mReceiver, intentFilter, 2);
        } else {
            registerReceiver(this.mReceiver, intentFilter);
        }
        this.mShouldNotifyPowerState = true;
        getWindow().getDecorView().postDelayed(new TurnOnRunnable(this), SCREEN_ON_DELAY);
        if (this.mShowWhenLocked) {
            getWindow().getDecorView().postDelayed(new NotifyPowerStateRunnable(this), 1000L);
        }
        this.mScreenWidthDp = getResources().getConfiguration().screenWidthDp;
        if (Util.isTinyScreen(this)) {
            initOrientationEventListener();
        }
        Log.f(TAG, "AlarmAlertFullScreenActivity onCreate end");
    }

    static class NotifyPowerStateRunnable implements Runnable {
        WeakReference<AlarmAlertFullScreenActivity> mReference;

        public NotifyPowerStateRunnable(AlarmAlertFullScreenActivity alarmAlertFullScreenActivity) {
            this.mReference = new WeakReference<>(alarmAlertFullScreenActivity);
        }

        @Override // java.lang.Runnable
        public void run() {
            WeakReference<AlarmAlertFullScreenActivity> weakReference = this.mReference;
            AlarmAlertFullScreenActivity alarmAlertFullScreenActivity = weakReference == null ? null : weakReference.get();
            if (alarmAlertFullScreenActivity == null || !alarmAlertFullScreenActivity.mShouldNotifyPowerState) {
                return;
            }
            alarmAlertFullScreenActivity.notifyPowerState(alarmAlertFullScreenActivity, true);
        }
    }

    private void initOrientationEventListener() {
        this.mDisplayManager.registerDisplayListener(this.mDisplayListener, this.mHandler);
    }

    private void getRepeatCount() {
        try {
            this.mRepeatCountReminder = Integer.parseInt((String) Objects.requireNonNull(this.mSharedPreferences.getString("snooze_repeat_count_remainder", "3")));
            this.mRepeatCount = Integer.parseInt((String) Objects.requireNonNull(this.mSharedPreferences.getString("snooze_repeat_count", "3")));
        } catch (Exception e) {
            Log.e(TAG, "getRepeatCount error: " + e);
        }
    }

    private void setAlarmAlertFullScreenView(Intent intent, boolean z) {
        this.mShowWhenLocked = ((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode();
        Alarm alarm = (Alarm) intent.getParcelableExtra(AlarmHelper.ALARM_INTENT_EXTRA);
        if (alarm.id == -2) {
            if (this.mAlarm != null) {
                NotificationUtil.clearAlarmAlertNotification(DeskClockApp.getAppDEContext(), this.mAlarm.id);
                this.mAlarm = null;
            }
            this.mTimerAlarm = alarm;
            setupTimerScreen(alarm, false);
            this.mTimerViewRoot.setVisibility(0);
            this.mAlarmViewRoot.setVisibility(8);
            if (isSupportHyperMaterial()) {
                applyMaskMaterial(this.mAlarmViewRoot, false);
                applyMaskMaterial(this.mTimerViewRoot, true);
            }
            if (Util.isTinyScreen(this)) {
                setFlipLayoutPadding(this.mTimerViewRoot);
            }
        } else {
            if (this.mTimerAlarm != null) {
                NotificationUtil.clearTimerAlertNotification(DeskClockApp.getAppDEContext(), this.mTimerAlarm.id);
                this.mTimerAlarm = null;
            }
            Alarm alarm2 = this.mAlarm;
            if (alarm2 != null && alarm2.id != alarm.id) {
                NotificationUtil.clearAlarmAlertNotification(DeskClockApp.getAppDEContext(), this.mAlarm.id);
                this.mAlarm = null;
            }
            this.mAlarm = alarm;
            setupAlarmScreen(alarm, false);
            this.mTimerViewRoot.setVisibility(8);
            this.mAlarmViewRoot.setVisibility(0);
            if (isSupportHyperMaterial()) {
                applyMaskMaterial(this.mTimerViewRoot, false);
                applyMaskMaterial(this.mAlarmViewRoot, true);
            }
            if (Util.isTinyScreen(this)) {
                setFlipLayoutPadding(this.mAlarmViewRoot);
            }
        }
        if (!z || MiuiSdk.isLiteOrMiddleMode()) {
            return;
        }
        setBackground();
    }

    /* JADX WARN: Code duplicated, block: B:16:0x005c  */
    private void setFlipLayoutPadding(ViewGroup viewGroup) {
        int dimension;
        int dimension2;
        if (Build.VERSION.SDK_INT >= 29) {
            DisplayCutout displayCutout = Build.VERSION.SDK_INT >= 30 ? ((WindowManager) getSystemService("window")).getCurrentWindowMetrics().getWindowInsets().getDisplayCutout() : null;
            if (displayCutout != null) {
                dimension2 = displayCutout.getBoundingRectLeft().width();
                dimension = displayCutout.getBoundingRectRight().width();
            } else if (Util.getRotationMode(this) == 0) {
                dimension = (int) getResources().getDimension(R.dimen.alert_screen_tiny_margin_start);
                dimension2 = 0;
            } else if (Util.getRotationMode(this) == 2) {
                dimension2 = (int) getResources().getDimension(R.dimen.alert_screen_tiny_margin_start);
                dimension = 0;
            } else {
                dimension = 0;
                dimension2 = 0;
            }
        } else {
            dimension = 0;
            dimension2 = 0;
        }
        viewGroup.setPadding(dimension2, 0, dimension, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetRuyiScreenPadding(boolean z) {
        ViewGroup viewGroup;
        ViewGroup viewGroup2;
        if (!Util.isTinyScreen(this)) {
            resetFullScreenPadding(z);
            return;
        }
        if (z && (viewGroup2 = this.mAlarmViewRoot) != null) {
            setFlipLayoutPadding(viewGroup2);
        } else {
            if (z || (viewGroup = this.mTimerViewRoot) == null) {
                return;
            }
            setFlipLayoutPadding(viewGroup);
        }
    }

    private void resetFullScreenPadding(boolean z) {
        ViewGroup viewGroup;
        ViewGroup viewGroup2;
        if (z && (viewGroup2 = this.mAlarmViewRoot) != null) {
            viewGroup2.setPadding(0, 0, 0, 0);
        } else {
            if (z || (viewGroup = this.mTimerViewRoot) == null) {
                return;
            }
            viewGroup.setPadding(0, 0, 0, 0);
        }
    }

    private static class SetBgAsyncTask extends AsyncTask<Void, Void, Drawable> {
        private WeakReference<AlarmAlertFullScreenActivity> activityRef;
        private boolean isSupportHyperMaterial;

        public SetBgAsyncTask(AlarmAlertFullScreenActivity alarmAlertFullScreenActivity, boolean z) {
            this.activityRef = new WeakReference<>(alarmAlertFullScreenActivity);
            this.isSupportHyperMaterial = z;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Drawable doInBackground(Void... voidArr) {
            Bitmap lockWallpaper;
            Bitmap gaussianBitmap;
            WeakReference<AlarmAlertFullScreenActivity> weakReference = this.activityRef;
            AlarmAlertFullScreenActivity alarmAlertFullScreenActivity = weakReference == null ? null : weakReference.get();
            if (alarmAlertFullScreenActivity != null && !alarmAlertFullScreenActivity.isFinishing()) {
                try {
                    MiuiWallpaperManager miuiWallpaperManager = AlarmHelper.getMiuiWallpaperManager();
                    if (Build.VERSION.SDK_INT >= 33 && miuiWallpaperManager != null) {
                        if (Util.isTinyScreen(alarmAlertFullScreenActivity) || (Util.isFoldDevice(alarmAlertFullScreenActivity) && !Util.isInInternalScreen(alarmAlertFullScreenActivity))) {
                            lockWallpaper = miuiWallpaperManager.getMiuiWallpaperPreview(8);
                        } else {
                            lockWallpaper = miuiWallpaperManager.getMiuiWallpaperPreview(2);
                        }
                    } else {
                        lockWallpaper = Util.getLockWallpaper(alarmAlertFullScreenActivity);
                    }
                    if (lockWallpaper == null) {
                        return null;
                    }
                    try {
                        if (this.isSupportHyperMaterial) {
                            return new BitmapDrawable(alarmAlertFullScreenActivity.getResources(), lockWallpaper);
                        }
                        gaussianBitmap = Util.setGaussianBitmap(lockWallpaper, alarmAlertFullScreenActivity);
                        try {
                            lockWallpaper.recycle();
                            if (gaussianBitmap != null) {
                                return new BitmapDrawable(alarmAlertFullScreenActivity.getResources(), gaussianBitmap);
                            }
                            return null;
                        } catch (Exception e) {
                            e = e;
                            Log.e(AlarmAlertFullScreenActivity.TAG, "Error loading background", e);
                            if (lockWallpaper != null && !lockWallpaper.isRecycled()) {
                                lockWallpaper.recycle();
                            }
                            if (gaussianBitmap != null && !gaussianBitmap.isRecycled()) {
                                lockWallpaper.recycle();
                            }
                            return null;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        gaussianBitmap = null;
                    }
                } catch (Exception e3) {
                    e = e3;
                    lockWallpaper = null;
                    gaussianBitmap = null;
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Drawable drawable) {
            WeakReference<AlarmAlertFullScreenActivity> weakReference = this.activityRef;
            AlarmAlertFullScreenActivity alarmAlertFullScreenActivity = weakReference == null ? null : weakReference.get();
            if (alarmAlertFullScreenActivity == null || alarmAlertFullScreenActivity.isFinishing() || alarmAlertFullScreenActivity.mRoot == null || drawable == null) {
                return;
            }
            if (!alarmAlertFullScreenActivity.isLocked || (alarmAlertFullScreenActivity.isLocked && !alarmAlertFullScreenActivity.mHasTurnOnScreen)) {
                alarmAlertFullScreenActivity.mRoot.setBackground(drawable);
            }
        }
    }

    private void setBackground() {
        cancelSetBgAsyncTask();
        SetBgAsyncTask setBgAsyncTask = new SetBgAsyncTask(this, isSupportHyperMaterial());
        this.mSetBgAsyncTask = setBgAsyncTask;
        setBgAsyncTask.execute(new Void[0]);
    }

    private void cancelSetBgAsyncTask() {
        SetBgAsyncTask setBgAsyncTask = this.mSetBgAsyncTask;
        if (setBgAsyncTask != null) {
            setBgAsyncTask.cancel(true);
            this.mSetBgAsyncTask = null;
        }
    }

    private Bundle newFragmentArgs(Alarm alarm, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("alarm", alarm);
        return bundle;
    }

    private void setupAlarmScreen(Alarm alarm, boolean z) {
        AlertAlarmFragment alertAlarmFragment = (AlertAlarmFragment) getSupportFragmentManager().findFragmentByTag(ALARM_FRAGMENT_TAG);
        Bundle bundleNewFragmentArgs = newFragmentArgs(alarm, z);
        if (alertAlarmFragment == null) {
            alertAlarmFragment = new AlertAlarmFragment();
            alertAlarmFragment.setArguments(bundleNewFragmentArgs);
        } else {
            alertAlarmFragment.getArguments().putAll(bundleNewFragmentArgs);
            if (alertAlarmFragment.getView() != null) {
                alertAlarmFragment.initView();
            }
        }
        FragmentTransaction fragmentTransactionBeginTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction.replace(R.id.alarm_alert_fullscreen, alertAlarmFragment, ALARM_FRAGMENT_TAG);
        fragmentTransactionBeginTransaction.commitAllowingStateLoss();
    }

    private void setupTimerScreen(Alarm alarm, boolean z) {
        AlertTimerFragment alertTimerFragment = (AlertTimerFragment) getSupportFragmentManager().findFragmentByTag(TIMER_FRAGMENT_TAG);
        Bundle bundleNewFragmentArgs = newFragmentArgs(alarm, z);
        if (alertTimerFragment == null) {
            alertTimerFragment = new AlertTimerFragment();
            alertTimerFragment.setArguments(bundleNewFragmentArgs);
        } else {
            alertTimerFragment.getArguments().putAll(bundleNewFragmentArgs);
            if (alertTimerFragment.getView() != null) {
                alertTimerFragment.initView();
            }
        }
        FragmentTransaction fragmentTransactionBeginTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction.replace(R.id.timer_alert_fullscreen, alertTimerFragment, TIMER_FRAGMENT_TAG);
        fragmentTransactionBeginTransaction.commitAllowingStateLoss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void snooze(boolean z) {
        if (this.mAlarm == null) {
            return;
        }
        if (Util.atLeastU()) {
            int i = FBEUtil.getDefaultSharedPreferences(this).getInt(AlarmService.KEY_ALARM_USER_ID, Util.getCurrentUser());
            Log.f(TAG, "snooze alarmSpace: " + i + " Util.getCurrentUser(): " + Util.getCurrentUser());
            if (i != Util.getCurrentUser()) {
                AlarmHelper.snoozeAlarmAsUser(i, this, this.mAlarm);
                finish();
            }
        }
        if (z) {
            this.mRepeatCountReminder--;
            updateSnoozeRepeatCountReminder();
        }
        WeatherUtils.updateWeatherPublishTimeFlag(this, false);
        if (Util.atLeastU()) {
            int snoozeMinutes = Util.getAlarmSpaceSnoozeMinutes(this) == Integer.MIN_VALUE ? Util.getSnoozeMinutes(this) : Util.getAlarmSpaceSnoozeMinutes(this);
            Log.f(TAG, "snooze min: " + snoozeMinutes);
            AlarmHelper.snoozeAlarm(this, this.mAlarm, snoozeMinutes);
        } else {
            AlarmHelper.snoozeAlarm(this, this.mAlarm);
        }
        AlarmService.notifyMiWearable(false, this.mAlarm);
        this.mAlarm = null;
        if (this.mShowWhenLocked) {
            try {
                ClockCompat.goToSleep(this, SystemClock.uptimeMillis());
            } catch (Exception e) {
                Log.e(TAG, "goToSleep error: " + e);
            }
        }
        getWindow().getDecorView().postDelayed(new DelayFinishRunnable(this), 200L);
    }

    static class DelayFinishRunnable implements Runnable {
        WeakReference<AlarmAlertFullScreenActivity> mReference;

        public DelayFinishRunnable(AlarmAlertFullScreenActivity alarmAlertFullScreenActivity) {
            this.mReference = new WeakReference<>(alarmAlertFullScreenActivity);
        }

        @Override // java.lang.Runnable
        public void run() {
            WeakReference<AlarmAlertFullScreenActivity> weakReference = this.mReference;
            AlarmAlertFullScreenActivity alarmAlertFullScreenActivity = weakReference == null ? null : weakReference.get();
            if (alarmAlertFullScreenActivity != null) {
                alarmAlertFullScreenActivity.finish();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismiss(boolean z, boolean z2) {
        dismiss(z, false, z2);
    }

    @Override // miuix.appcompat.app.AppCompatActivity, android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        if (bundle != null && bundle.containsKey(IS_SHOW_LIFE_POST) && bundle.getBoolean(IS_SHOW_LIFE_POST)) {
            Fragment fragmentFindFragmentByTag = getSupportFragmentManager().findFragmentByTag(ALARM_FRAGMENT_TAG);
            if (fragmentFindFragmentByTag instanceof AlertAlarmFragment) {
                ((AlertAlarmFragment) fragmentFindFragmentByTag).showLifePost();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(IS_SHOW_LIFE_POST, this.mShowLifePost);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismiss(boolean z, boolean z2, boolean z3) {
        Alarm alarm;
        Log.f(TAG, z ? "Alarm killed" : "Alarm dismissed");
        this.mShowLifePost = z2;
        if (z3) {
            this.mRepeatCountReminder = this.mRepeatCount;
            updateSnoozeRepeatCountReminder();
        }
        WeatherUtils.updateWeatherPublishTimeFlag(this, false);
        if (!z && (alarm = this.mAlarm) != null) {
            NotificationUtil.clearAlarmAlertNotification(this, alarm.id);
            BackScreenNotificationUtil.clearAlarmNotification(this);
            AlarmHelper.stopAlarmKlaxon(this);
            AlarmHelper.tryDeleteOneshotAlarm(this, this.mAlarm);
        }
        if (Util.isSupportColorfulLight() && ReflectUtil.isSupportBackStrap(DeskClockApp.getAppDEContext()) && ReflectUtil.isStripLightEnable(DeskClockApp.getAppDEContext())) {
            AlarmColorLightManager.setColorfulLight(DeskClockApp.getAppDEContext(), -1);
        }
        this.mAlarm = null;
        if (!z && z2) {
            clearKeepScreenOn();
        } else {
            finish();
        }
    }

    private void dismissTimer() {
        AlarmHelper.dismissTimer(this);
        this.mTimerAlarm = null;
        finish();
    }

    private void clearKeepScreenOn() {
        getWindow().clearFlags(128);
    }

    private void setKeepScreenOn() {
        getWindow().addFlags(128);
    }

    private void toggleScreenButtonState(boolean z) {
        try {
            Settings.Secure.putInt(getContentResolver(), ClockCompat.MiuiSettings_SCREEN_BUTTONS_STATE, z ? 1 : 0);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.f(TAG, "AlarmAlertFullScreenActivity onNewIntent");
        if (!((PowerManager) getSystemService("power")).isScreenOn()) {
            Log.f(TAG, "isScreenOn: false");
            finish();
            startActivity(intent);
        } else {
            Log.f(TAG, "isScreenOn: true");
            setIntent(intent);
            this.mIsAlarmDismissed = false;
            setKeepScreenOn();
            this.mShowLifePost = false;
            setAlarmAlertFullScreenView(intent, false);
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        Log.f(TAG, "AlarmAlertFullScreenActivity onWindowFocusChanged");
        super.onWindowFocusChanged(z);
        toggleScreenButtonState(z);
        Log.f(TAG, "AlarmAlertFullScreenActivity onWindowFocusChanged toggleScreenButtonState() end");
        try {
            ClockCompat.enableStatusBar(this, !z);
        } catch (Exception e) {
            Log.e(TAG, "enableStatusBar error " + e);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        Log.f(TAG, "AlarmAlertFullScreenActivity onResume");
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(R2.dimen.m3_searchbar_margin_vertical);
        if (this.mShowWhenLocked) {
            return;
        }
        notifyPowerState(this, true);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        Log.f(TAG, "AlarmAlertFullScreenActivity  onPause");
        notifyPowerState(this, false);
        this.mNotifyPowerStateFalse = true;
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        Log.f(TAG, "AlarmAlertFullScreenActivity#onDestroy start");
        startCircleFlashService(0);
        DisplayManager.DisplayListener displayListener = this.mDisplayListener;
        if (displayListener != null) {
            this.mDisplayManager.unregisterDisplayListener(displayListener);
            this.mDisplayListener = null;
        }
        try {
            BroadcastReceiver broadcastReceiver = this.mReceiver;
            if (broadcastReceiver != null) {
                unregisterReceiver(broadcastReceiver);
                this.mReceiver = null;
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "onDestroy: " + e);
        }
        toggleScreenButtonState(false);
        this.mShouldNotifyPowerState = false;
        if (!this.mNotifyPowerStateFalse) {
            notifyPowerState(this, false);
        }
        try {
            ClockCompat.enableStatusBar(this, true);
        } catch (Exception e2) {
            Log.e(TAG, "enableStatusBar error " + e2);
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            this.mHandler = null;
        }
        AlarmHelper.releaseMiuiWallpaperManager();
        SetBgAsyncTask setBgAsyncTask = this.mSetBgAsyncTask;
        if (setBgAsyncTask == null || setBgAsyncTask.isCancelled()) {
            return;
        }
        this.mSetBgAsyncTask.cancel(true);
        this.mSetBgAsyncTask = null;
    }

    private void snoozeOrDismissAlarm(final boolean z, final boolean z2) {
        runOnUiThread(new Runnable() { // from class: com.android.deskclock.alarm.alert.AlarmAlertFullScreenActivity.3
            @Override // java.lang.Runnable
            public void run() {
                if (z) {
                    AlarmAlertFullScreenActivity.this.snooze(true);
                } else {
                    AlarmAlertFullScreenActivity.this.dismiss(false, z2, true);
                }
            }
        });
    }

    @Override // com.android.deskclock.alarm.alert.AlertScreenController.AlertScreenListener
    public void onSnooze() {
        Log.f(TAG, "User manually snoozed");
        snoozeOrDismissAlarm(true, false);
        StatHelper.alarmEvent(StatHelper.EVENT_ALARM_SNOOZED_BY_CLICK);
        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.ALARM_SNOOZED_BY_CLICK);
    }

    @Override // com.android.deskclock.alarm.alert.AlertScreenController.AlertScreenListener
    public void onDismiss(boolean z) {
        Log.f(TAG, "User manually dismissed, showLifePost:" + z);
        if (this.mTimerViewRoot.getVisibility() == 0) {
            dismissTimer();
            return;
        }
        this.mIsAlarmDismissed = true;
        Alarm alarm = this.mAlarm;
        if (alarm != null) {
            AlarmService.notifyMiWearable(true, alarm);
        }
        snoozeOrDismissAlarm(false, z);
        StatHelper.alarmEvent(StatHelper.EVENT_ALARM_DISMISS_BY_SWIPE);
        OneTrackStatHelper.trackTriggerEvent(OneTrackStatHelper.ALARM_ALERT_DISMISS_BY_SWIPE);
    }

    @Override // miuix.appcompat.app.AppCompatActivity, android.app.Activity
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private static class TurnOnRunnable implements Runnable {
        private WeakReference<AlarmAlertFullScreenActivity> mReference;

        public TurnOnRunnable(AlarmAlertFullScreenActivity alarmAlertFullScreenActivity) {
            this.mReference = new WeakReference<>(alarmAlertFullScreenActivity);
        }

        @Override // java.lang.Runnable
        public void run() {
            WeakReference<AlarmAlertFullScreenActivity> weakReference = this.mReference;
            (weakReference == null ? null : weakReference.get()).setTurnOnScreen();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTurnOnScreen() {
        this.mHasTurnOnScreen = true;
        if (isFinishing()) {
            return;
        }
        turnOnScreen();
        startCircleFlashService(1);
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Log.d(TAG, "onConfigurationChanged------------------------------");
        if (Util.isPcMode(this)) {
            return;
        }
        if (!Util.isDeviceCetus()) {
            resetOrientation();
        }
        Log.d(TAG, "onConfigurationChanged isTinyScreen ： " + Util.isTinyScreen(this));
        if (Util.isDeviceRuyiOrBixi() && Util.isTinyScreen(this)) {
            initOrientationEventListener();
        }
        if (Settings.System.getInt(getContentResolver(), "lock_screen_after_fold_screen", 1) == 1 && configuration.screenWidthDp < this.mScreenWidthDp && configuration.screenWidthDp < 600) {
            Log.f(TAG, "lock screen");
            if (this.mAlarm != null) {
                snoozeOrDismissAlarmByKey();
                Log.d(TAG, "onConfigurationChanged mPhysicalKeyBehavior : " + this.mPhysicalKeyBehavior);
                if (this.mPhysicalKeyBehavior != 0 || !Util.isDeviceRuyiOrBixi()) {
                    return;
                }
            }
        }
        this.mScreenWidthDp = configuration.screenWidthDp;
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        AlertAlarmFragment alertAlarmFragment = (AlertAlarmFragment) supportFragmentManager.findFragmentByTag(ALARM_FRAGMENT_TAG);
        AlertTimerFragment alertTimerFragment = (AlertTimerFragment) supportFragmentManager.findFragmentByTag(TIMER_FRAGMENT_TAG);
        if (alertAlarmFragment != null) {
            if (!this.mShowLifePost) {
                alertAlarmFragment.initView();
            }
            alertAlarmFragment.resetLifePostLayout();
            resetRuyiScreenPadding(true);
        }
        if (alertTimerFragment != null) {
            alertTimerFragment.initView();
            resetRuyiScreenPadding(false);
        }
    }

    private void turnOnScreen() {
        Log.f(TAG, "turn on screen");
        PowerManager.WakeLock wakeLockNewWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(268435466, Log.TAG);
        wakeLockNewWakeLock.acquire();
        wakeLockNewWakeLock.release();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyPowerState(Context context, boolean z) {
        Log.f(TAG, "notifyPowerState, isEnter: " + z);
        Intent intent = new Intent();
        intent.setPackage("com.miui.powerkeeper");
        intent.setAction(ACTION_SET_FPS);
        intent.putExtra("package_name", BuildConfig.APPLICATION_ID);
        intent.putExtra("isEnter", z);
        context.sendBroadcast(intent);
    }

    protected void resetOrientation() {
        if (Util.isTinyScreen(this)) {
            Log.d(TAG, "resetOrientation  SCREEN_ORIENTATION_USER_PORTRAIT");
            setRequestedOrientation(12);
        } else if (getResources().getBoolean(R.bool.large_mode)) {
            setRequestedOrientation(-1);
        } else {
            setRequestedOrientation(1);
        }
    }

    private void startCircleFlashService(int i) {
        if (!Util.checkApkExist(CIRCLE_FLASH_PACKAGE)) {
            Log.d(TAG, "CIRCLE_FLASH_PACKAGE not Exist,return");
            return;
        }
        Log.d(TAG, "startCircleFlashService state: " + i);
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(CIRCLE_FLASH_PACKAGE, CIRCLE_FLASH_CLASS));
            intent.putExtra("circle_flash_state", i);
            intent.putExtra("circle_flash_app_name", BuildConfig.APPLICATION_ID);
            startService(intent);
        } catch (Exception e) {
            Log.e(TAG, "startCircleFlashService " + e);
        }
    }

    private void applyMaskMaterial(View view, boolean z) {
        if (!z) {
            MiuiBlurUtils.clearBackgroundBlur(view);
            Drawable background = view.getBackground();
            if (background != null) {
                background.setAlpha(255);
                return;
            }
            return;
        }
        MaterialConfig materialConfigCreate = MaterialConfig.create(Overlay.Thick_Light);
        if (materialConfigCreate == null || materialConfigCreate.getBlurConfig() == null) {
            return;
        }
        Drawable background2 = view.getBackground();
        if (background2 != null) {
            background2.setAlpha(0);
        }
        MiuiBlurUtils.setBackgroundBlur(view, MiuixUIUtils.dp2px(view.getResources().getDisplayMetrics().density, materialConfigCreate.getBlurConfig().blurRadius), false);
        MaterialConfig.ColorBlendConfig colorBlendConfig = materialConfigCreate.getColorBlendConfig();
        if (colorBlendConfig != null) {
            MiuiBlurUtils.setBackgroundBlendConfig(view, colorBlendConfig.blendColors, colorBlendConfig.blendModes);
        }
    }

    private boolean isSupportHyperMaterial() {
        return HyperMaterialUtils.isEnable() && HyperMaterialUtils.isFeatureEnable(this);
    }
}
