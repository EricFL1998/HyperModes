package com.android.deskclock.settings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.MiuiTheme;
import com.android.deskclock.addition.holiday.HolidayInstance;
import com.android.deskclock.addition.holiday.HolidayUtil;
import com.android.deskclock.addition.ringtone.RingtoneUriCompat;
import com.android.deskclock.addition.ringtone.digital.DigitalTimerRingtoneHelper;
import com.android.deskclock.addition.ringtone.star.WYStarRingtoneHelper;
import com.android.deskclock.addition.ringtone.weather.WeatherRingtoneHelper;
import com.android.deskclock.addition.ringtone.week.WeekRingtoneHelper;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper;
import com.android.deskclock.alarm.lifepost.LifePostSettingActivity;
import com.android.deskclock.settings.pref.ClockListPreference;
import com.android.deskclock.settings.pref.ClockUpdatePreference;
import com.android.deskclock.settings.pref.ClockValuePreference;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.DialogUtil;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.NetworkUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.permission.KoreaPermissionUtil;
import com.android.deskclock.util.permission.PermissionUtil;
import com.android.deskclock.util.permission.SystemPermissionUtil;
import com.android.deskclock.util.permission.UserNoticeUtil;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.util.themeringtone.RingtoneHelper;
import com.android.deskclock.view.SimpleDialogFragment;
import com.android.deskclock.widget.ClockSeekBarPreference;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import miui.settings.splitlib.SplitUtils;
import miuix.appcompat.app.ProgressDialog;
import miuix.preference.DropDownPreference;
import miuix.preference.PreferenceFragment;

/* JADX INFO: loaded from: classes.dex */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    public static final String KEY_ALARM_VOLUME = "alarm_volume";
    public static final String KEY_AUTO_SILENCE = "auto_silence";
    public static final String KEY_DEFAULT_ALARM_ALERT = "default_alarm_alert";
    public static final String KEY_DEFAULT_TIMER_ALERT = "default_timer_alert";
    private static final String KEY_EDIT_SYSTEM_TIME = "edit_system_time";
    public static final String KEY_HOLIDAY_UPDATE = "holiday_update";
    public static final String KEY_MORE_ALERT_SETTINGS = "more_alert_settings";
    public static final String KEY_OTHER_CATEGORY = "alarm_other_settings";
    private static final String KEY_PRIVACY_POLICY = "privacy_policy";
    private static final String KEY_PRIVACY_SETTING = "privacy_settings";
    private static final String KEY_SYSTEM_TIME_CATEGORY = "alarm_time_settings";
    private static final int REQUEST_CTA_PERMISSION_CODE = 7;
    private static final int REQUEST_NETWORK_CODE = 5;
    private static final int REQUEST_NETWORK_CODE_NEW = 6;
    public static final String TAG = "DC:SettingsFragment";
    private static SeekBarVolumizer.VolumeChangeObserver mVolumeChangeObserver;
    private ClockListPreference mAlarmDurationPreference;
    private DropDownPreference mAlarmDurationPreferenceNew;
    private Uri mAlarmRingtone;
    private ClockValuePreference mDefaultAlarmPreference;
    private ClockValuePreference mDefaultTimerPreference;
    private ProgressDialog mHolidayUpdateDialog;
    private boolean mIsXiaoAiOrTimerNeedPermission;
    private long mLastPressedTime;
    private Uri mNeedPermissionUri;
    private Uri mOldAlarmRingtone;
    private Uri mOldTimerRingtone;
    private PreferenceCategory mOtherPreferenceCategory;
    private SettingsActivity mParentActivity;
    private ClockValuePreference mPrivacyPolicyPreference;
    private ClockValuePreference mPrivacySettingsPreference;
    private SeekBarVolumizer mSeekBarVolumizer;
    private Preference mSystemTimePreference;
    private PreferenceCategory mTimeSettingPreferenceCategory;
    private Uri mTimerRingtone;
    private ClockUpdatePreference mValueHolidayPreference;
    private ClockSeekBarPreference mVolumeSeekBarPreference;
    private ActivityResultLauncher<Intent> toCtaLauncher;
    private ActivityResultLauncher<Intent> toRingTonePickerLauncher;
    private ActivityResultLauncher<Intent> toThemeRingtoneLauncher;
    private ActivityResultLauncher<Intent> toTimerPickerLauncher;
    private SimpleDialogFragment mUserNoticeDialog = null;
    private boolean mSupportWeekRingtone = false;
    private boolean mThemeSupportChangeAlertDirectly = false;

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        PreferenceCategory preferenceCategory;
        FBEUtil.setStorageDeviceProtectedForFBE(getPreferenceManager());
        setPreferencesFromResource(R.xml.settings_fragment_miui12, str);
        this.mAlarmDurationPreferenceNew = (DropDownPreference) findPreference("auto_silence");
        this.mAlarmDurationPreferenceNew.setEntries(new String[]{String.format(getString(R.string.auto_silence_entries1), new Object[0]), String.format(getString(R.string.auto_silence_entries2), 1), String.format(getString(R.string.auto_silence_entries3), 5), String.format(getString(R.string.auto_silence_entries4), 10), String.format(getString(R.string.auto_silence_entries5), 15), String.format(getString(R.string.auto_silence_entries6), 20), String.format(getString(R.string.auto_silence_entries7), 25), String.format(getString(R.string.auto_silence_entries8), 30)});
        this.mAlarmDurationPreferenceNew.setEntryValues(R.array.auto_silence_values);
        this.mAlarmDurationPreferenceNew.setValue(FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppContext()).getString("auto_silence", "10"));
        this.mAlarmDurationPreferenceNew.setOnPreferenceChangeListener(this);
        this.mParentActivity = (SettingsActivity) getActivity();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mTimeSettingPreferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(KEY_SYSTEM_TIME_CATEGORY);
        Preference preferenceFindPreference = findPreference(KEY_EDIT_SYSTEM_TIME);
        this.mSystemTimePreference = preferenceFindPreference;
        preferenceFindPreference.setOnPreferenceClickListener(this);
        if (Settings.Global.getInt(getActivity().getContentResolver(), "xiaomi_account_is_child", 0) == 1 && Settings.Global.getInt(getActivity().getContentResolver(), "device_is_guarded", 0) == 1 && (preferenceCategory = this.mTimeSettingPreferenceCategory) != null) {
            preferenceCategory.setVisible(false);
        }
        ClockValuePreference clockValuePreference = (ClockValuePreference) findPreference("default_alarm_alert");
        this.mDefaultAlarmPreference = clockValuePreference;
        clockValuePreference.setOnPreferenceClickListener(this);
        ClockValuePreference clockValuePreference2 = (ClockValuePreference) findPreference("default_timer_alert");
        this.mDefaultTimerPreference = clockValuePreference2;
        clockValuePreference2.setOnPreferenceClickListener(this);
        ClockSeekBarPreference clockSeekBarPreference = (ClockSeekBarPreference) findPreference(KEY_ALARM_VOLUME);
        this.mVolumeSeekBarPreference = clockSeekBarPreference;
        clockSeekBarPreference.setOnPreferenceChangeListener(this);
        this.mSeekBarVolumizer = new SeekBarVolumizer(getContext(), this.mVolumeSeekBarPreference, this);
        findPreference(KEY_MORE_ALERT_SETTINGS).setOnPreferenceClickListener(this);
        this.mOtherPreferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(KEY_OTHER_CATEGORY);
        ClockUpdatePreference clockUpdatePreference = (ClockUpdatePreference) findPreference(KEY_HOLIDAY_UPDATE);
        this.mValueHolidayPreference = clockUpdatePreference;
        clockUpdatePreference.setOnPreferenceClickListener(this);
        ClockValuePreference clockValuePreference3 = (ClockValuePreference) findPreference(KEY_PRIVACY_POLICY);
        this.mPrivacyPolicyPreference = clockValuePreference3;
        clockValuePreference3.setOnPreferenceClickListener(this);
        ClockValuePreference clockValuePreference4 = (ClockValuePreference) findPreference(KEY_PRIVACY_SETTING);
        this.mPrivacySettingsPreference = clockValuePreference4;
        clockValuePreference4.setOnPreferenceClickListener(this);
        getDefaultAlarmRingtone(false);
        setAlarmPrefSummary();
        getDefaultTimerRingtone();
        setTimerPrefSummary();
        this.mSupportWeekRingtone = WeekRingtoneHelper.isRomSupport() && WeatherRingtoneHelper.isRomSupport();
        StatHelper.deskclockEvent(StatHelper.EVENT_SETTINGS_ACTIVE_COUNT);
        if (Util.isInternational() && this.mOtherPreferenceCategory != null) {
            if (!KoreaPermissionUtil.isKoreaRegion()) {
                this.mOtherPreferenceCategory.setVisible(false);
            } else {
                ClockUpdatePreference clockUpdatePreference2 = this.mValueHolidayPreference;
                if (clockUpdatePreference2 != null) {
                    this.mOtherPreferenceCategory.removePreference(clockUpdatePreference2);
                }
                ClockValuePreference clockValuePreference5 = this.mPrivacySettingsPreference;
                if (clockValuePreference5 != null) {
                    clockValuePreference5.setVisible(false);
                }
            }
        } else if (PermissionUtil.isNewPrivacyPolicySupport()) {
            ClockValuePreference clockValuePreference6 = this.mPrivacyPolicyPreference;
            if (clockValuePreference6 != null) {
                clockValuePreference6.setVisible(false);
            }
        } else {
            ClockValuePreference clockValuePreference7 = this.mPrivacySettingsPreference;
            if (clockValuePreference7 != null) {
                clockValuePreference7.setVisible(false);
            }
        }
        this.mThemeSupportChangeAlertDirectly = MiuiTheme.supportChangeAlertDirectly();
    }

    public static class SeekBarVolumizer implements Handler.Callback {
        private static final int CHECK_RINGTONE_PLAYBACK_DELAY_MS = 1000;
        private static final int MSG_SET_STREAM_VOLUME = 0;
        private static final int MSG_START_SAMPLE = 1;
        private static final int MSG_STOP_SAMPLE = 2;
        private static final int NOT_QUIT_HANDLER_THREAD = 2000;
        private AudioManager mAudioManager;
        private Context mContext;
        private Handler mHandler;
        private HandlerThread mHandlerThread;
        private Handler mMainHandler;
        private int mOriginalVolume;
        private WeakReference<SettingsFragment> mReference;
        private volatile Ringtone mRingtone;
        private ClockSeekBarPreference mSeekBarPreference;
        private int mStreamType = 4;
        private int mMinVolume = 0;
        private int mLastProgress = -1;

        public SeekBarVolumizer(Context context, ClockSeekBarPreference clockSeekBarPreference, SettingsFragment settingsFragment) {
            this.mContext = context;
            this.mAudioManager = (AudioManager) context.getSystemService("audio");
            this.mSeekBarPreference = clockSeekBarPreference;
            HandlerThread handlerThread = new HandlerThread("VolumePreference.CallbackHandler");
            this.mHandlerThread = handlerThread;
            handlerThread.start();
            this.mHandler = new Handler(this.mHandlerThread.getLooper(), this);
            this.mMainHandler = new Handler(Looper.getMainLooper());
            initSeekBar(this.mSeekBarPreference);
            this.mReference = new WeakReference<>(settingsFragment);
            if (this.mMainHandler != null) {
                registerVolumeObserver();
            }
        }

        private void initSeekBar(ClockSeekBarPreference clockSeekBarPreference) {
            clockSeekBarPreference.setCustomMax(this.mAudioManager.getStreamMaxVolume(this.mStreamType));
            if (Build.VERSION.SDK_INT >= 28) {
                int streamMinVolume = this.mAudioManager.getStreamMinVolume(this.mStreamType);
                this.mMinVolume = streamMinVolume;
                clockSeekBarPreference.setCustomMin(streamMinVolume);
            }
            int streamVolume = this.mAudioManager.getStreamVolume(this.mStreamType);
            this.mOriginalVolume = streamVolume;
            clockSeekBarPreference.setValue(streamVolume);
            clockSeekBarPreference.setOnSeekBarChangeListener(new ClockSeekBarPreference.OnSeekBarChangeListener() { // from class: com.android.deskclock.settings.SettingsFragment.SeekBarVolumizer.1
                @Override // com.android.deskclock.widget.ClockSeekBarPreference.OnSeekBarChangeListener
                public void onProgressChanged(int i, boolean z) {
                    if (z) {
                        if (i < SeekBarVolumizer.this.mMinVolume) {
                            i = SeekBarVolumizer.this.mMinVolume;
                        }
                        SeekBarVolumizer.this.mLastProgress = i;
                        SeekBarVolumizer.this.mAudioManager.setStreamVolume(SeekBarVolumizer.this.mStreamType, i, 0);
                    }
                }

                @Override // com.android.deskclock.widget.ClockSeekBarPreference.OnSeekBarChangeListener
                public void onStartTrackingTouch() {
                    SeekBarVolumizer.this.postStartSample();
                }
            });
            Executors.newSingleThreadExecutor().execute(new Runnable() { // from class: com.android.deskclock.settings.SettingsFragment$SeekBarVolumizer$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m97x9eecc5e1();
                }
            });
        }

        /* JADX INFO: renamed from: lambda$initSeekBar$0$com-android-deskclock-settings-SettingsFragment$SeekBarVolumizer, reason: not valid java name */
        /* synthetic */ void m97x9eecc5e1() {
            Ringtone ringtone = RingtoneManager.getRingtone(DeskClockApp.getAppDEContext(), Settings.System.DEFAULT_ALARM_ALERT_URI);
            if (ringtone != null) {
                ringtone.setStreamType(this.mStreamType);
            }
            this.mRingtone = ringtone;
        }

        private void registerVolumeObserver() {
            VolumeChangeObserver unused = SettingsFragment.mVolumeChangeObserver = new VolumeChangeObserver(this.mMainHandler);
            this.mContext.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, SettingsFragment.mVolumeChangeObserver);
        }

        private void unregisterVolumeObserver() {
            if (SettingsFragment.mVolumeChangeObserver != null) {
                this.mContext.getContentResolver().unregisterContentObserver(SettingsFragment.mVolumeChangeObserver);
                VolumeChangeObserver unused = SettingsFragment.mVolumeChangeObserver = null;
            }
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                this.mAudioManager.setStreamVolume(this.mStreamType, this.mLastProgress, 0);
            } else if (i == 1) {
                onStartSample();
            } else if (i == 2) {
                onStopSample();
                if (message.arg1 != 2000) {
                    this.mHandlerThread.quitSafely();
                }
            } else {
                Log.e("VolumePreference", "invalid SeekBarVolumizer message: " + message.what);
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void postStartSample() {
            this.mHandler.removeMessages(1);
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(1), isSamplePlaying() ? 1000L : 0L);
        }

        private void onStartSample() {
            WeakReference<SettingsFragment> weakReference;
            SettingsFragment settingsFragment;
            if (isSamplePlaying() || (weakReference = this.mReference) == null || (settingsFragment = weakReference.get()) == null) {
                return;
            }
            settingsFragment.onSampleStarting(this);
            if (this.mRingtone != null) {
                this.mRingtone.play();
            }
        }

        private void postStopSample(boolean z) {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            if (z) {
                Handler handler = this.mHandler;
                handler.sendMessage(handler.obtainMessage(2));
            } else {
                Handler handler2 = this.mHandler;
                handler2.sendMessage(handler2.obtainMessage(2, 2000, 0));
            }
        }

        private void onStopSample() {
            if (this.mRingtone != null) {
                this.mRingtone.stop();
            }
        }

        void postSetVolume(int i) {
            int i2 = this.mMinVolume;
            if (i < i2) {
                i = i2;
            }
            this.mSeekBarPreference.setValue(i);
            this.mLastProgress = i;
            this.mHandler.removeMessages(0);
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(0));
        }

        public boolean isSamplePlaying() {
            return this.mRingtone != null && this.mRingtone.isPlaying();
        }

        public void stopSample(boolean z) {
            postStopSample(z);
        }

        public void release() {
            postStopSample(true);
            unregisterVolumeObserver();
            Handler handler = this.mMainHandler;
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                this.mMainHandler = null;
            }
            ClockSeekBarPreference clockSeekBarPreference = this.mSeekBarPreference;
            if (clockSeekBarPreference != null) {
                clockSeekBarPreference.removeOnSeekBarChangeListener();
            }
        }

        public void revertVolume() {
            this.mAudioManager.setStreamVolume(this.mStreamType, this.mOriginalVolume, 0);
        }

        private class VolumeChangeObserver extends ContentObserver {
            public VolumeChangeObserver(Handler handler) {
                super(handler);
            }

            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                super.onChange(z);
                int streamVolume = SeekBarVolumizer.this.mAudioManager.getStreamVolume(SeekBarVolumizer.this.mStreamType);
                if (streamVolume == SeekBarVolumizer.this.mLastProgress || SeekBarVolumizer.this.mSeekBarPreference == null) {
                    return;
                }
                SeekBarVolumizer.this.mLastProgress = streamVolume;
                SeekBarVolumizer.this.mSeekBarPreference.setValue(SeekBarVolumizer.this.mLastProgress);
            }
        }
    }

    protected void onSampleStarting(SeekBarVolumizer seekBarVolumizer) {
        SeekBarVolumizer seekBarVolumizer2 = this.mSeekBarVolumizer;
        if (seekBarVolumizer2 == null || seekBarVolumizer == seekBarVolumizer2) {
            return;
        }
        seekBarVolumizer2.stopSample(true);
    }

    private void getDefaultAlarmRingtone(boolean z) {
        this.mAlarmRingtone = AlarmRingtoneUtil.getDefaultAlarmRingtone();
        Log.i(TAG, "getDefaultAlarmRingtone: " + this.mAlarmRingtone);
        if (z && !XiaoAiRingtoneHelper.isXiaoAiRingtone(this.mAlarmRingtone)) {
            XiaoAiRingtoneHelper.clearXiaoAiRingtoneIds(this.mParentActivity);
        }
        if (!WYStarRingtoneHelper.isWYStarAlert(this.mAlarmRingtone) || WYStarRingtoneHelper.isSupport()) {
            return;
        }
        Uri weatherRingtoneUri = WeatherRingtoneHelper.getWeatherRingtoneUri();
        this.mAlarmRingtone = weatherRingtoneUri;
        AlarmRingtoneUtil.setDefaultAlarmRingtone(weatherRingtoneUri);
    }

    private void getDefaultTimerRingtone() {
        this.mTimerRingtone = TimerDao.getTimerRingtone();
        Log.d(TAG, "getDefaultTimerRingtone: " + this.mTimerRingtone);
    }

    @Override // miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initFragmentResultLauncher();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        DialogUtil.dismissDialogFragment(this.mUserNoticeDialog);
        this.mUserNoticeDialog = null;
        SeekBarVolumizer seekBarVolumizer = this.mSeekBarVolumizer;
        if (seekBarVolumizer != null) {
            seekBarVolumizer.stopSample(false);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if ("default_alarm_alert".equals(preference.getKey())) {
            if (SystemClock.elapsedRealtime() - this.mLastPressedTime >= 1000) {
                this.mLastPressedTime = SystemClock.elapsedRealtime();
                if (this.mSupportWeekRingtone && !MiuiSdk.isLiteOrMiddleMode()) {
                    jumpToRingtonePicker();
                } else {
                    openRingtonePicker(true);
                }
            }
            StatHelper.alarmEvent(StatHelper.EVENT_CLICK_DEFAULT_RINGTONE);
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.SETTINGS_DEFAULT_RINGTONE_CLICK);
            return true;
        }
        if ("default_timer_alert".equals(preference.getKey())) {
            openRingtonePickerForTimer();
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.SETTINGS_TIMER_RINGTONE_CLICK);
            return false;
        }
        if ("auto_silence".equals(preference.getKey())) {
            StatHelper.alarmEvent(StatHelper.EVENT_CLICK_MAX_ALARM_DURATION);
            return false;
        }
        if (KEY_EDIT_SYSTEM_TIME.equals(preference.getKey())) {
            Intent intent = new Intent("android.settings.DATE_SETTINGS");
            if (Util.isPadOrFoldDeviceFullScreen(this.mParentActivity) && Build.VERSION.SDK_INT == 34) {
                SplitUtils.startSettingsSplitActivity(this.mParentActivity, intent, "");
            } else if (Util.isPadOrFoldDeviceFullScreen(this.mParentActivity) && Build.VERSION.SDK_INT >= 35) {
                intent.addFlags(335544320);
                startActivity(intent);
            } else {
                startActivity(intent);
            }
            return true;
        }
        if (KEY_ALARM_VOLUME.equals(preference.getKey())) {
            StatHelper.alarmEvent(StatHelper.EVENT_CLICK_SETTINGS_VOLUME_COUNT);
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.SETTINGS_VOLUME_CLICK);
            return false;
        }
        if (KEY_PRIVACY_POLICY.equals(preference.getKey())) {
            UserNoticeUtil.gotoPrivacyWebPage(this.mParentActivity);
            return false;
        }
        if (KEY_PRIVACY_SETTING.equals(preference.getKey())) {
            openPrivacySettings();
            return false;
        }
        if (KEY_HOLIDAY_UPDATE.equals(preference.getKey())) {
            if (!UserNoticeUtil.isNetPermissionAgreed()) {
                showNetPermissionDialog();
            } else {
                startUpdateHoliday();
            }
            StatHelper.alarmEvent(StatHelper.EVENT_CLICK_HOLIDAY_UPDATE_SETTINGS);
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.SETTINGS_HOLIDAY_UPDATE_CLICK);
            return false;
        }
        if (!KEY_MORE_ALERT_SETTINGS.equals(preference.getKey())) {
            return false;
        }
        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.SETTINGS_MORE_CLICK);
        openMoreSettings();
        return false;
    }

    private void showNetPermissionDialog() {
        if (SystemPermissionUtil.showPermissionDeclare(this.mParentActivity, 5, 6)) {
            return;
        }
        this.mUserNoticeDialog = UserNoticeUtil.showUserNoticeDialog(this.mParentActivity, Util.isInternational() ? R.string.network_privacy_global : R.string.network_privacy, new UserNoticeUtil.OnNetPermissionListener() { // from class: com.android.deskclock.settings.SettingsFragment.1
            @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
            public void onAccept() {
                SettingsFragment.this.startUpdateHoliday();
                LifePostSettingActivity.updateLifePostSwitchState(DeskClockApp.getAppContext(), true);
            }

            @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
            public void onReject() {
                LifePostSettingActivity.updateLifePostSwitchState(DeskClockApp.getAppContext(), false);
            }
        }, getFragmentManager());
    }

    public void handleActivityResult(int i, int i2, Intent intent) {
        if (i == 5 || i == 6) {
            if (i2 == -3) {
                showCtaPermissionDialog();
                return;
            }
            if (i2 == 1) {
                UserNoticeUtil.setAcceptNetPermission(true);
                StatHelper.init(DeskClockApp.getAppContext());
                OneTrackStatHelper.init(DeskClockApp.getAppContext());
                startUpdateHoliday();
                LifePostSettingActivity.updateLifePostSwitchState(DeskClockApp.getAppContext(), true);
                return;
            }
            if (i2 == 666 || i2 == 0) {
                UserNoticeUtil.setAcceptNetPermission(false);
                UserNoticeUtil.setRemindNetPermission(false);
                LifePostSettingActivity.updateLifePostSwitchState(DeskClockApp.getAppContext(), false);
                return;
            }
            Log.e(SystemPermissionUtil.TAG, "lack of important information");
            return;
        }
        if (i != 7) {
            return;
        }
        if (i2 == -3) {
            showCtaPermissionDialog();
            return;
        }
        if (i2 == 1) {
            UserNoticeUtil.setAcceptNetPermission(true);
            StatHelper.init(DeskClockApp.getAppContext());
            OneTrackStatHelper.init(DeskClockApp.getAppContext());
            if (PermissionUtil.shouldNotAskPermission(this.mParentActivity)) {
                this.mTimerRingtone = this.mOldTimerRingtone;
                setTimerPrefSummary();
                TimerDao.setTimerRingtone(this.mTimerRingtone);
                return;
            }
            return;
        }
        this.mTimerRingtone = this.mOldTimerRingtone;
        setTimerPrefSummary();
        TimerDao.setTimerRingtone(this.mTimerRingtone);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        SeekBarVolumizer seekBarVolumizer;
        if ("auto_silence".equals(preference.getKey())) {
            StatHelper.updateAlarmProperties(StatHelper.PROP_MAX_ALARM_DURATION, Integer.parseInt((String) obj));
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.SETTINGS_MAX_ALERT_DURATION_CLICK);
            return true;
        }
        if (!KEY_ALARM_VOLUME.equals(preference.getKey()) || (seekBarVolumizer = this.mSeekBarVolumizer) == null) {
            return true;
        }
        seekBarVolumizer.postSetVolume(((Integer) obj).intValue());
        this.mSeekBarVolumizer.postStartSample();
        return true;
    }

    private void refresh() {
        Preference preference = this.mSystemTimePreference;
        if (preference != null) {
            preference.setOnPreferenceChangeListener(this);
        }
        DropDownPreference dropDownPreference = this.mAlarmDurationPreferenceNew;
        if (dropDownPreference != null) {
            dropDownPreference.setOnPreferenceChangeListener(this);
        }
        findPreference("default_alarm_alert").setOnPreferenceChangeListener(this);
        findPreference(KEY_PRIVACY_POLICY).setOnPreferenceChangeListener(this);
    }

    private void showUpdateProgressDialog() {
        if (this.mHolidayUpdateDialog == null) {
            this.mHolidayUpdateDialog = new ProgressDialog(this.mParentActivity);
        }
        this.mHolidayUpdateDialog.setMessage(getString(R.string.holiday_update_loading));
        if (this.mHolidayUpdateDialog.isShowing()) {
            return;
        }
        this.mHolidayUpdateDialog.show();
    }

    public void dismissProgressDialog() {
        ProgressDialog progressDialog = this.mHolidayUpdateDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            this.mHolidayUpdateDialog.dismiss();
        }
        this.mHolidayUpdateDialog = null;
    }

    public void handlePermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i != 1) {
            return;
        }
        if (iArr.length <= 0 || iArr[0] != 0) {
            this.mTimerRingtone = this.mOldTimerRingtone;
            setTimerPrefSummary();
            TimerDao.setTimerRingtone(this.mTimerRingtone);
        } else {
            if (Build.VERSION.SDK_INT >= 29 && this.mIsXiaoAiOrTimerNeedPermission) {
                this.mAlarmRingtone = RingtoneUriCompat.saveMediaStore(this.mParentActivity, this.mNeedPermissionUri);
                this.mIsXiaoAiOrTimerNeedPermission = false;
            }
            setAlarmPrefSummary();
        }
    }

    private void initFragmentResultLauncher() {
        this.toThemeRingtoneLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.android.deskclock.settings.SettingsFragment$$ExternalSyntheticLambda0
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                this.f$0.m93x13343d9d((ActivityResult) obj);
            }
        });
        this.toRingTonePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.android.deskclock.settings.SettingsFragment$$ExternalSyntheticLambda1
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                this.f$0.m94x12bdd79e((ActivityResult) obj);
            }
        });
        this.toTimerPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.android.deskclock.settings.SettingsFragment$$ExternalSyntheticLambda2
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                this.f$0.m95x1247719f((ActivityResult) obj);
            }
        });
        this.toCtaLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.android.deskclock.settings.SettingsFragment$$ExternalSyntheticLambda3
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                this.f$0.m96x11d10ba0((ActivityResult) obj);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$initFragmentResultLauncher$0$com-android-deskclock-settings-SettingsFragment, reason: not valid java name */
    /* synthetic */ void m93x13343d9d(ActivityResult activityResult) {
        Uri uriSaveMediaStore;
        int resultCode = activityResult.getResultCode();
        Intent data = activityResult.getData();
        Log.i(TAG, "toThemeRingtoneLauncher resultCode: " + resultCode);
        if ((resultCode == -1 || resultCode == 112) && data != null) {
            if (this.mThemeSupportChangeAlertDirectly) {
                if (RingtoneUriCompat.atLeastU() && (uriSaveMediaStore = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI")) != null && (uriSaveMediaStore.equals(DigitalTimerRingtoneHelper.getRingtoneUri()) || uriSaveMediaStore.equals(XiaoAiRingtoneHelper.getRingtoneUri()))) {
                    this.mIsXiaoAiOrTimerNeedPermission = true;
                    this.mNeedPermissionUri = uriSaveMediaStore;
                    if (Build.VERSION.SDK_INT >= 29) {
                        uriSaveMediaStore = RingtoneUriCompat.saveMediaStore(this.mParentActivity, uriSaveMediaStore);
                        Log.d(TAG, "initFragmentResultLauncher, saveMediaStore: " + uriSaveMediaStore);
                    }
                    this.mAlarmRingtone = uriSaveMediaStore;
                }
                this.mOldAlarmRingtone = this.mAlarmRingtone;
                getDefaultAlarmRingtone(true);
                setAlarmPrefSummary();
                return;
            }
            this.mOldAlarmRingtone = this.mAlarmRingtone;
            this.mAlarmRingtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            setAlarmPrefSummary();
            AlarmRingtoneUtil.setDefaultAlarmRingtone(this.mAlarmRingtone);
            AlarmRingtoneUtil.takePersistableUriPermission(data, this.mAlarmRingtone, this.mParentActivity);
        }
    }

    /* JADX INFO: renamed from: lambda$initFragmentResultLauncher$1$com-android-deskclock-settings-SettingsFragment, reason: not valid java name */
    /* synthetic */ void m94x12bdd79e(ActivityResult activityResult) {
        int resultCode = activityResult.getResultCode();
        Log.i(TAG, "toRingTonePickerLauncher resultCode: " + resultCode);
        if (resultCode == -1) {
            getDefaultAlarmRingtone(true);
            setAlarmPrefSummary();
        }
    }

    /* JADX INFO: renamed from: lambda$initFragmentResultLauncher$2$com-android-deskclock-settings-SettingsFragment, reason: not valid java name */
    /* synthetic */ void m95x1247719f(ActivityResult activityResult) {
        int resultCode = activityResult.getResultCode();
        Intent data = activityResult.getData();
        Log.i(TAG, "toTimerPickerLauncher resultCode: " + resultCode);
        if (resultCode != -1 || data == null) {
            return;
        }
        if (Util.isRingtoneInternal((Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI")) || PermissionUtil.shouldAskReadPermission(this.mParentActivity)) {
            this.mOldTimerRingtone = this.mTimerRingtone;
            Uri uri = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            this.mTimerRingtone = uri;
            AlarmRingtoneUtil.takePersistableUriPermission(data, uri, this.mParentActivity);
            setTimerPrefSummary();
            TimerDao.setTimerRingtone(this.mTimerRingtone);
            return;
        }
        if (PermissionUtil.shouldShowCtaPermission(this.mParentActivity)) {
            showCtaPermissionDialog();
            this.mOldTimerRingtone = this.mTimerRingtone;
            Uri uri2 = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            this.mTimerRingtone = uri2;
            AlarmRingtoneUtil.takePersistableUriPermission(data, uri2, this.mParentActivity);
            setTimerPrefSummary();
            TimerDao.setTimerRingtone(this.mTimerRingtone);
        }
    }

    /* JADX INFO: renamed from: lambda$initFragmentResultLauncher$3$com-android-deskclock-settings-SettingsFragment, reason: not valid java name */
    /* synthetic */ void m96x11d10ba0(ActivityResult activityResult) {
        int resultCode = activityResult.getResultCode();
        Log.i(TAG, "toCtaLauncher resultCode: " + resultCode);
        if (resultCode == -3) {
            showCtaRingTonePermissionDialog();
        }
        if (resultCode == 1) {
            AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.settings.SettingsFragment.2
                @Override // java.lang.Runnable
                public void run() {
                    RingtoneUriCompat.updateConvertAllUri();
                }
            });
            UserNoticeUtil.setAcceptNetPermission(true);
            StatHelper.init(DeskClockApp.getAppContext());
            OneTrackStatHelper.init(DeskClockApp.getAppContext());
            jumpToRingtonePicker();
        }
    }

    private void showCtaPermissionDialog() {
        SystemPermissionUtil.showPermissionDeclare(this.mParentActivity, 7, 7);
    }

    private void setAlarmPrefSummary() {
        if (this.mAlarmRingtone == null) {
            this.mDefaultAlarmPreference.setValue(R.string.silent_alarm_summary);
        } else if (RingtoneManager.getDefaultUri(4).equals(this.mAlarmRingtone)) {
            this.mDefaultAlarmPreference.setValue(R.string.default_alarm_title);
        } else {
            this.mDefaultAlarmPreference.setValue(AlarmRingtoneUtil.getAlarmRingtoneTitle(DeskClockApp.getAppDEContext(), this.mAlarmRingtone));
        }
    }

    private void setTimerPrefSummary() {
        Log.d(TAG, "setTimerPrefSummary mTimerRingtone :" + this.mTimerRingtone);
        if (this.mTimerRingtone == null) {
            this.mDefaultTimerPreference.setValue(R.string.silent_alarm_summary);
        } else {
            this.mDefaultTimerPreference.setValue(AlarmRingtoneUtil.getAlarmRingtoneTitle(DeskClockApp.getAppDEContext(), this.mTimerRingtone));
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        DialogUtil.dismissDialog(this.mHolidayUpdateDialog);
        this.mHolidayUpdateDialog = null;
        DialogUtil.dismissDialogFragment(this.mUserNoticeDialog);
        this.mUserNoticeDialog = null;
        dismissProgressDialog();
        RingtoneHelper.handleTimerAlert(this.mTimerRingtone);
        this.toThemeRingtoneLauncher = null;
        SeekBarVolumizer seekBarVolumizer = this.mSeekBarVolumizer;
        if (seekBarVolumizer != null) {
            seekBarVolumizer.stopSample(true);
            this.mSeekBarVolumizer.release();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startUpdateHoliday() {
        if (NetworkUtil.isNetworkConnected()) {
            new UpdateHolidayAsyncTask(this).execute(new Void[0]);
            showUpdateProgressDialog();
        } else {
            Toast.makeText(DeskClockApp.getAppDEContext(), getString(R.string.holiday_update_fail), 0).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHolidayUpdateResult(boolean z) {
        Resources resources;
        int i;
        ClockUpdatePreference clockUpdatePreference;
        dismissProgressDialog();
        if (z && (clockUpdatePreference = this.mValueHolidayPreference) != null) {
            clockUpdatePreference.setCircleViewVisibility(8);
        }
        if (isAdded()) {
            Context appDEContext = DeskClockApp.getAppDEContext();
            if (z) {
                resources = getResources();
                i = R.string.holiday_update_success;
            } else {
                resources = getResources();
                i = R.string.holiday_update_fail;
            }
            Toast.makeText(appDEContext, resources.getString(i), 0).show();
        }
    }

    private static class UpdateHolidayAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<SettingsFragment> reference;

        public UpdateHolidayAsyncTask(SettingsFragment settingsFragment) {
            this.reference = new WeakReference<>(settingsFragment);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... voidArr) {
            Context appDEContext = DeskClockApp.getAppDEContext();
            if (HolidayUtil.updateHolidayData(appDEContext)) {
                return Boolean.valueOf(HolidayInstance.getInstance(appDEContext).initHolidayData(appDEContext));
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean bool) {
            SettingsFragment settingsFragment = this.reference.get();
            if (settingsFragment != null) {
                settingsFragment.handleHolidayUpdateResult(bool.booleanValue());
            }
        }
    }

    private void openRingtonePicker(final boolean z) {
        if (this.mAlarmRingtone == null) {
            this.mAlarmRingtone = AlarmRingtoneUtil.getDefaultAlarmRingtone();
        }
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.settings.SettingsFragment.3
            @Override // java.lang.Runnable
            public void run() {
                SettingsFragment.this.mAlarmRingtone = AlarmRingtoneUtil.getXiaoAiOrDigitalTimerAlertUri(DeskClockApp.getAppDEContext(), SettingsFragment.this.mAlarmRingtone);
                Intent intentCreateRingtonePickerIntent = z ? MiuiTheme.createRingtonePickerIntent(SettingsFragment.this.mAlarmRingtone, MiuiTheme.createAlarmRingtoneExtras(), SettingsFragment.this.mThemeSupportChangeAlertDirectly, "SettingsFragment") : MiuiTheme.createRingtonePickerIntent(SettingsFragment.this.mAlarmRingtone, null, SettingsFragment.this.mThemeSupportChangeAlertDirectly, "SettingsFragment");
                if (SettingsFragment.this.toThemeRingtoneLauncher != null) {
                    SettingsFragment.this.toThemeRingtoneLauncher.launch(intentCreateRingtonePickerIntent);
                }
                Log.i(SettingsFragment.TAG, "jump to theme ringtone");
            }
        });
    }

    private void openRingtonePickerForTimer() {
        this.toTimerPickerLauncher.launch(MiuiTheme.createRingtonePickerIntent(this.mTimerRingtone, "SettingsFragment"));
    }

    private void jumpToRingtonePicker() {
        if (RingtoneUriCompat.atLeastU() && !Util.isInternational() && !UserNoticeUtil.isNetPermissionAgreed()) {
            showCtaRingTonePermissionDialog();
            return;
        }
        Intent intent = new Intent(this.mParentActivity, (Class<?>) AlarmRingtonePickerActivity.class);
        intent.putExtra(AlarmRingtonePickerActivity.IS_FROM_ALARM, true);
        this.toRingTonePickerLauncher.launch(intent);
    }

    private void showCtaRingTonePermissionDialog() {
        SystemPermissionUtil.showPermissionDeclare(this.mParentActivity, this.toCtaLauncher);
    }

    private void openMoreSettings() {
        startActivity(new Intent(this.mParentActivity, (Class<?>) AlarmSettingsActivity.class));
    }

    private void openPrivacySettings() {
        startActivity(new Intent(this.mParentActivity, (Class<?>) PrivacySettingsActivity.class));
    }
}
