package com.android.deskclock.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.MiuiTheme;
import com.android.deskclock.addition.ringtone.RingtoneUriCompat;
import com.android.deskclock.addition.ringtone.weather.WeatherRingtoneHelper;
import com.android.deskclock.addition.ringtone.week.WeekRingtoneHelper;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper;
import com.android.deskclock.alarm.AlarmController;
import com.android.deskclock.alarm.RepeatAlarmController;
import com.android.deskclock.alarm.SetAlarmController;
import com.android.deskclock.alarm.bedtime.BedtimeSettingsActivity;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.alarm.bedtime.HealthDataUtil;
import com.android.deskclock.alarm.bedtime.MiHomeHelper;
import com.android.deskclock.settings.pref.ClockListPreference;
import com.android.deskclock.settings.pref.ClockRepeatPreference;
import com.android.deskclock.settings.pref.ClockValuePreference;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.AlarmUtils;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.permission.PermissionUtil;
import com.android.deskclock.util.permission.SystemPermissionUtil;
import com.android.deskclock.util.permission.UserNoticeUtil;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.util.themeringtone.RingtoneHelper;
import miuix.bottomsheet.BottomSheetBehavior;
import miuix.bottomsheet.BottomSheetModal;
import miuix.preference.DropDownPreference;
import miuix.preference.PreferenceFragment;

/* JADX INFO: loaded from: classes.dex */
public class BedtimeSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String KEY_BEDTIME_REPEAT_TYPE = "key_bedtime_repeat_type";
    private static final String KEY_NOTIFICATION_ADV_TIME = "notification_adv_time";
    private static final String KEY_OPEN_BEDTIME = "key_open_bedtime";
    private static final String KEY_SLEEP_NO_DISTURBANCE = "key_sleep_no_disturbance";
    private static final String KEY_VIBRATOR = "key_vibrator";
    private static final String KEY_WAKE_ALERT = "key_wake_alert";
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    public static final String PREFERENCES_NAME = "BedtimeAlarm";
    public static final String TAG = "BedtimeSettingsFragment";
    private static long mLastClickTime;
    protected BedtimeSettingsActivity mActivity;
    private Context mAppContext;
    private CheckBoxPreference mBedtimeSwitchPreference;
    private CheckBoxPreference mDisturbancePreference;
    private boolean mIsXiaoAiRingtone;
    private ClockListPreference mNotificationAdvTimePreference;
    private DropDownPreference mNotificationAdvTimePreferenceNew;
    private Uri mOldWakeAlert;
    private Activity mParentActivity;
    public BottomSheetModal mRepeatAlarmBottomSheetModal;
    public RepeatAlarmController mRepeatAlarmController;
    private View mRepeatAlarmView;
    private BottomSheetBehavior mRepeatBottomSheetBehavior;
    private ClockRepeatPreference mRepeatTypePreference;
    private SharedPreferences mSharedPref;
    private boolean mSupportWeekRingtone = false;
    private CheckBoxPreference mVibratorPreference;
    private Alarm mWakeAlarm;
    private Uri mWakeAlert;
    private ClockValuePreference mWakeAlertPreference;
    private MiHomeHelper miHomeHelper;
    private ActivityResultLauncher<Intent> toBedtimeRepeatLauncher;
    private ActivityResultLauncher<Intent> toCTALauncher;
    private ActivityResultLauncher<Intent> toCtaRingToneLauncher;
    private ActivityResultLauncher<Intent> toRingTonePickerLauncher;
    private ActivityResultLauncher<Intent> toThemeRingtoneLauncher;

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        this.mActivity = (BedtimeSettingsActivity) getActivity();
        FBEUtil.setStorageDeviceProtectedForFBE(getPreferenceManager());
        getPreferenceManager().setSharedPreferencesName("BedtimeAlarm");
        setPreferencesFromResource(R.xml.settings_bedtime_fragment_miui12, str);
        this.mNotificationAdvTimePreferenceNew = (DropDownPreference) findPreference(KEY_NOTIFICATION_ADV_TIME);
        this.mNotificationAdvTimePreferenceNew.setEntries(new String[]{String.format(getString(R.string.notification_adv_time_entries1), new Object[0]), String.format(getString(R.string.notification_adv_time_entries2), 15), String.format(getString(R.string.notification_adv_time_entries3), 30), String.format(getString(R.string.notification_adv_time_entries4), 1), String.format(getString(R.string.notification_adv_time_entries5), new Object[0])});
        this.mNotificationAdvTimePreferenceNew.setEntryValues(R.array.notification_adv_time_values);
        this.mNotificationAdvTimePreferenceNew.setOnPreferenceChangeListener(this);
        this.mNotificationAdvTimePreferenceNew.setValue(String.valueOf(BedtimeUtil.getNotificationAdvTime(this.mParentActivity)));
        this.mParentActivity = getActivity();
        this.mAppContext = DeskClockApp.getAppContext();
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("key_open_bedtime");
        this.mBedtimeSwitchPreference = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        ClockValuePreference clockValuePreference = (ClockValuePreference) findPreference(KEY_WAKE_ALERT);
        this.mWakeAlertPreference = clockValuePreference;
        clockValuePreference.setOnPreferenceClickListener(this);
        Alarm wakeAlarm = BedtimeUtil.getWakeAlarm(this.mParentActivity);
        this.mWakeAlarm = wakeAlarm;
        if (wakeAlarm == null) {
            Alarm alarm = new Alarm();
            this.mWakeAlarm = alarm;
            alarm.hour = 6;
            this.mWakeAlarm.minutes = 0;
        }
        this.mWakeAlert = this.mWakeAlarm.alert;
        this.mIsXiaoAiRingtone = XiaoAiRingtoneHelper.isXiaoAiAlarm(DeskClockApp.getAppDEContext(), Integer.MIN_VALUE);
        setAlarmPrefSummary();
        ClockRepeatPreference clockRepeatPreference = (ClockRepeatPreference) findPreference(KEY_BEDTIME_REPEAT_TYPE);
        this.mRepeatTypePreference = clockRepeatPreference;
        clockRepeatPreference.setOnPreferenceChangeListener(this);
        this.mRepeatTypePreference.setOnPreferenceClickListener(this);
        this.mRepeatTypePreference.setPrefValue(this.mWakeAlarm.daysOfWeek.toString(this.mActivity, true));
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference("key_sleep_no_disturbance");
        this.mDisturbancePreference = checkBoxPreference2;
        checkBoxPreference2.setOnPreferenceChangeListener(this);
        this.mDisturbancePreference.setChecked(BedtimeUtil.getDisturbanceState(this.mAppContext));
        CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) findPreference("key_vibrator");
        this.mVibratorPreference = checkBoxPreference3;
        checkBoxPreference3.setOnPreferenceChangeListener(this);
        this.mVibratorPreference.setChecked(this.mWakeAlarm.vibrate);
        this.mSupportWeekRingtone = WeekRingtoneHelper.isRomSupport() && WeatherRingtoneHelper.isRomSupport();
        this.mSharedPref = FBEUtil.getSharedPreferences(getActivity(), "BedtimeAlarm", 0);
    }

    @Override // miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initFragmentResultLauncher();
        if (bundle == null || !bundle.getBoolean(SetAlarmController.IS_SHOW_REPEAT_ALARM_DIALOG)) {
            return;
        }
        showRepeatAlarmDialog(bundle);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mRepeatAlarmBottomSheetModal != null) {
            bundle.putBoolean(SetAlarmController.IS_SHOW_REPEAT_ALARM_DIALOG, true);
            RepeatAlarmController repeatAlarmController = this.mRepeatAlarmController;
            if (repeatAlarmController != null) {
                repeatAlarmController.onSaveInstance(bundle);
            }
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mSharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override // miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mSharedPref.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (KEY_WAKE_ALERT.equals(preference.getKey())) {
            if (!this.mSupportWeekRingtone || MiuiSdk.isLiteOrMiddleMode()) {
                openRingtonePicker();
            } else if (Math.abs(System.currentTimeMillis() - mLastClickTime) > 1000) {
                mLastClickTime = System.currentTimeMillis();
                jumpToRingtonePicker();
            }
            StatHelper.alarmEvent(StatHelper.EVENT_CLICK_DEFAULT_RINGTONE);
            return true;
        }
        if (!KEY_BEDTIME_REPEAT_TYPE.equals(preference.getKey())) {
            return false;
        }
        setDaysOfWeek(this.mWakeAlarm.daysOfWeek);
        showRepeatAlarmDialog(null);
        return false;
    }

    private void showRepeatAlarmDialog(Bundle bundle) {
        this.mRepeatAlarmBottomSheetModal = new BottomSheetModal(this.mActivity);
        View viewInflate = LayoutInflater.from(this.mActivity).inflate(R.layout.activity_alarm_repeat, (ViewGroup) null);
        this.mRepeatAlarmView = viewInflate;
        this.mRepeatAlarmBottomSheetModal.setContentView(viewInflate);
        this.mRepeatAlarmBottomSheetModal.setDragHandleViewEnabled(true);
        RepeatAlarmController repeatAlarmController = new RepeatAlarmController(this.mActivity, this.mRepeatAlarmView, false);
        this.mRepeatAlarmController = repeatAlarmController;
        repeatAlarmController.initData(null, null, false, true);
        this.mRepeatAlarmController.setLastCheckedItem(this.mRepeatTypePreference.getPrefValue());
        this.mRepeatAlarmController.initBundleData(bundle);
        this.mRepeatAlarmController.setBackButtonClickListener(new AlarmController.BackButtonClickListener() { // from class: com.android.deskclock.settings.BedtimeSettingsFragment.1
            @Override // com.android.deskclock.alarm.AlarmController.BackButtonClickListener
            public void onButtonClick() {
                BedtimeSettingsFragment.this.dismissBedtimeRepeatDialog();
            }
        });
        BottomSheetBehavior<FrameLayout> behavior = this.mRepeatAlarmBottomSheetModal.getBehavior();
        this.mRepeatBottomSheetBehavior = behavior;
        behavior.setOnModeChangeListener(new BottomSheetBehavior.OnModeChangeListener() { // from class: com.android.deskclock.settings.BedtimeSettingsFragment.2
            @Override // miuix.bottomsheet.BottomSheetBehavior.OnModeChangeListener
            public void onModeChange(int i, View view) {
                if (BedtimeSettingsFragment.this.mRepeatAlarmController != null) {
                    if (i == 1) {
                        BedtimeSettingsFragment.this.mRepeatAlarmController.setViewLayout(true);
                    } else {
                        BedtimeSettingsFragment.this.mRepeatAlarmController.setViewLayout(false);
                    }
                }
            }
        });
        this.mRepeatBottomSheetBehavior.setDraggable(true);
        this.mRepeatBottomSheetBehavior.setSkipHalfExpanded(true);
        this.mRepeatBottomSheetBehavior.setSkipCollapsed(true);
        this.mRepeatBottomSheetBehavior.setForceFullHeight(true);
        this.mRepeatBottomSheetBehavior.setState(3);
        this.mRepeatAlarmBottomSheetModal.show();
        if (bundle == null || !bundle.getBoolean(RepeatAlarmController.IS_SHOW_SELF_DEFINE_DIALOG)) {
            return;
        }
        this.mRepeatAlarmController.showSelfDefDialog(RepeatAlarmController.mNewDaysOfWeekSwitchScreen.getBooleanArray(), bundle);
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if ("key_open_bedtime".equals(str)) {
            boolean zIsBedtimeOpen = BedtimeUtil.isBedtimeOpen(this.mAppContext);
            BedtimeUtil.setBedtimeOpenState(this.mAppContext, Boolean.valueOf(zIsBedtimeOpen).booleanValue());
            setWakeAlarmState(Boolean.valueOf(zIsBedtimeOpen));
            if (zIsBedtimeOpen) {
                HealthDataUtil.setHealthData(this.mAppContext);
            }
            AlarmHelper.setZenMode(this.mAppContext);
            return;
        }
        if ("key_vibrator".equals(str)) {
            this.mWakeAlarm.vibrate = Boolean.valueOf(BedtimeUtil.getVibratorState(this.mAppContext)).booleanValue();
            AlarmHelper.setWakeAlarm(this.mAppContext, this.mWakeAlarm);
        } else if ("key_sleep_no_disturbance".equals(str)) {
            AlarmHelper.setZenMode(this.mAppContext);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("key_open_bedtime".equals(preference.getKey())) {
            Log.i(TAG, "set bedtime manage state: " + ((Boolean) obj));
            return true;
        }
        if ("key_vibrator".equals(preference.getKey())) {
            Log.i(TAG, "set wake alert vibrator state: " + ((Boolean) obj));
            return true;
        }
        if ("key_sleep_no_disturbance".equals(preference.getKey())) {
            Log.i(TAG, "set disturbance state: " + ((Boolean) obj));
            return true;
        }
        if (KEY_BEDTIME_REPEAT_TYPE.equals(preference.getKey())) {
            this.mWakeAlarm.daysOfWeek = (Alarm.DaysOfWeek) obj;
            AlarmHelper.setWakeAlarm(this.mAppContext, this.mWakeAlarm);
            notifyBedtimeChanged(this.mParentActivity);
            AlarmHelper.setSleepNotification(this.mAppContext);
            AlarmHelper.setZenMode(this.mAppContext);
            AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.settings.BedtimeSettingsFragment.3
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        if (HealthDataUtil.isHealthAppValuable(BedtimeSettingsFragment.this.mAppContext)) {
                            HealthDataUtil.updateRepeatType(BedtimeSettingsFragment.this.mAppContext, BedtimeSettingsFragment.this.mWakeAlarm.daysOfWeek);
                        }
                    } catch (Exception unused) {
                    }
                }
            });
            Log.i(TAG, "set bedtime repeat type: " + this.mWakeAlarm.daysOfWeek.getCoded());
            return true;
        }
        if (!KEY_NOTIFICATION_ADV_TIME.equals(preference.getKey())) {
            return true;
        }
        String str = (String) obj;
        BedtimeUtil.setNotificationAdvTime(this.mAppContext, Integer.parseInt(str));
        AlarmHelper.setSleepNotification(this.mAppContext);
        Log.i(TAG, "set notification adv time: " + str);
        return true;
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mBedtimeSwitchPreference.setChecked(BedtimeUtil.isBedtimeOpen(this.mAppContext));
    }

    private void setAlarmPrefSummary() {
        if (this.mIsXiaoAiRingtone) {
            this.mWakeAlertPreference.setValue(R.string.xiaoai_ringtone_title);
            return;
        }
        if (this.mWakeAlert == null) {
            this.mWakeAlertPreference.setValue(R.string.silent_alarm_summary);
        } else if (RingtoneManager.getDefaultUri(4).equals(this.mWakeAlert)) {
            this.mWakeAlertPreference.setValue(R.string.default_alarm_title);
        } else {
            this.mWakeAlertPreference.setValue(AlarmRingtoneUtil.getAlarmRingtoneTitle(DeskClockApp.getAppDEContext(), this.mWakeAlert));
        }
    }

    public void handleRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i != 1) {
            return;
        }
        if (iArr.length <= 0 || iArr[0] != 0) {
            this.mWakeAlert = this.mOldWakeAlert;
            setAlarmPrefSummary();
            this.mWakeAlarm.alert = this.mWakeAlert;
            AlarmHelper.setWakeAlarm(this.mAppContext, this.mWakeAlarm);
            Log.i(TAG, "set wakeAlarm alert: " + AlarmRingtoneUtil.getAlarmRingtoneTitle(DeskClockApp.getAppDEContext(), this.mWakeAlert));
        }
    }

    private void handleWakeAlarmRepeat() {
        this.mRepeatTypePreference.setPrefValue(RepeatAlarmController.mDaysOfWeek.toString(this.mActivity, true));
        this.mWakeAlarm.daysOfWeek.setDay(RepeatAlarmController.getDays());
        AlarmHelper.setWakeAlarm(this.mAppContext, this.mWakeAlarm);
    }

    private void initFragmentResultLauncher() {
        this.toThemeRingtoneLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.android.deskclock.settings.BedtimeSettingsFragment$$ExternalSyntheticLambda0
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                this.f$0.m88x12c6b137((ActivityResult) obj);
            }
        });
        this.toRingTonePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.android.deskclock.settings.BedtimeSettingsFragment$$ExternalSyntheticLambda1
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                this.f$0.m89x13fd0416((ActivityResult) obj);
            }
        });
        this.toCTALauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.android.deskclock.settings.BedtimeSettingsFragment$$ExternalSyntheticLambda2
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                this.f$0.m90x153356f5((ActivityResult) obj);
            }
        });
        this.toCtaRingToneLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.android.deskclock.settings.BedtimeSettingsFragment$$ExternalSyntheticLambda3
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                this.f$0.m91x1669a9d4((ActivityResult) obj);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$initFragmentResultLauncher$0$com-android-deskclock-settings-BedtimeSettingsFragment, reason: not valid java name */
    /* synthetic */ void m88x12c6b137(ActivityResult activityResult) {
        int resultCode = activityResult.getResultCode();
        Intent data = activityResult.getData();
        Log.i(TAG, "toThemeRingtoneLauncher resultCode: " + resultCode);
        if (resultCode != -1 || data == null) {
            return;
        }
        if (Util.isRingtoneInternal((Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI")) || ((UserNoticeUtil.isCtaAgreed() && PermissionUtil.isPermissionGranted(this.mParentActivity, "android.permission.READ_EXTERNAL_STORAGE")) || (UserNoticeUtil.isCtaAgreed() && PermissionUtil.requestPermissionIfNeeded(this, "android.permission.READ_EXTERNAL_STORAGE")))) {
            this.mOldWakeAlert = this.mWakeAlert;
            Uri uri = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            this.mWakeAlert = uri;
            if (XiaoAiRingtoneHelper.isXiaoAiRingtone(uri)) {
                this.mIsXiaoAiRingtone = true;
                this.mWakeAlert = this.mOldWakeAlert;
                XiaoAiRingtoneHelper.addXiaoAiRingtoneIds(DeskClockApp.getAppDEContext(), Integer.MIN_VALUE);
            } else {
                this.mIsXiaoAiRingtone = false;
                XiaoAiRingtoneHelper.removeXiaoAiRingtoneIds(DeskClockApp.getAppDEContext(), Integer.MIN_VALUE);
            }
            AlarmRingtoneUtil.takePersistableUriPermission(data, this.mWakeAlert, this.mParentActivity);
            setAlarmPrefSummary();
            this.mWakeAlarm.alert = this.mWakeAlert;
            AlarmHelper.setWakeAlarm(this.mAppContext, this.mWakeAlarm);
            Log.i(TAG, "set wakeAlarm alert: " + AlarmRingtoneUtil.getAlarmRingtoneTitle(DeskClockApp.getAppDEContext(), this.mWakeAlert));
            return;
        }
        if (PermissionUtil.isPermissionGranted(this.mParentActivity, "android.permission.READ_EXTERNAL_STORAGE") || PermissionUtil.canPermissionAsk(this.mParentActivity, "android.permission.READ_EXTERNAL_STORAGE")) {
            showCtaPermissionDialog();
            this.mOldWakeAlert = this.mWakeAlert;
            Uri uri2 = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            this.mWakeAlert = uri2;
            AlarmRingtoneUtil.takePersistableUriPermission(data, uri2, this.mParentActivity);
            setAlarmPrefSummary();
            this.mWakeAlarm.alert = this.mWakeAlert;
            AlarmHelper.setWakeAlarm(this.mAppContext, this.mWakeAlarm);
            Log.i(TAG, "set wakeAlarm alert: " + AlarmRingtoneUtil.getAlarmRingtoneTitle(DeskClockApp.getAppDEContext(), this.mWakeAlert));
        }
    }

    /* JADX INFO: renamed from: lambda$initFragmentResultLauncher$1$com-android-deskclock-settings-BedtimeSettingsFragment, reason: not valid java name */
    /* synthetic */ void m89x13fd0416(ActivityResult activityResult) {
        int resultCode = activityResult.getResultCode();
        Intent data = activityResult.getData();
        Log.i(TAG, "toRingTonePickerLauncher resultCode: " + resultCode);
        if (resultCode != -1 || data == null) {
            return;
        }
        Uri uri = this.mWakeAlert;
        Uri uri2 = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
        this.mWakeAlert = uri2;
        if (XiaoAiRingtoneHelper.isXiaoAiRingtone(uri2)) {
            this.mIsXiaoAiRingtone = true;
            this.mWakeAlert = uri;
            XiaoAiRingtoneHelper.addXiaoAiRingtoneIds(DeskClockApp.getAppDEContext(), Integer.MIN_VALUE);
        } else {
            this.mIsXiaoAiRingtone = false;
            XiaoAiRingtoneHelper.removeXiaoAiRingtoneIds(DeskClockApp.getAppDEContext(), Integer.MIN_VALUE);
        }
        AlarmRingtoneUtil.takePersistableUriPermission(data, this.mWakeAlert, this.mParentActivity);
        setAlarmPrefSummary();
        this.mWakeAlarm.alert = this.mWakeAlert;
        AlarmHelper.setWakeAlarm(this.mAppContext, this.mWakeAlarm);
        Log.i(TAG, "set wakeAlarm alert: " + AlarmRingtoneUtil.getAlarmRingtoneTitle(DeskClockApp.getAppDEContext(), this.mWakeAlert));
    }

    /* JADX INFO: renamed from: lambda$initFragmentResultLauncher$2$com-android-deskclock-settings-BedtimeSettingsFragment, reason: not valid java name */
    /* synthetic */ void m90x153356f5(ActivityResult activityResult) {
        int resultCode = activityResult.getResultCode();
        Log.i(TAG, "toCTALauncher resultCode: " + resultCode);
        if (resultCode == 1) {
            UserNoticeUtil.setAcceptNetPermission(true);
            StatHelper.init(DeskClockApp.getAppContext());
            OneTrackStatHelper.init(DeskClockApp.getAppContext());
            if (PermissionUtil.isPermissionGranted(this.mParentActivity, "android.permission.READ_EXTERNAL_STORAGE") || PermissionUtil.requestPermissionIfNeeded(this, "android.permission.READ_EXTERNAL_STORAGE")) {
                return;
            }
            this.mWakeAlert = this.mOldWakeAlert;
            setAlarmPrefSummary();
            this.mWakeAlarm.alert = this.mWakeAlert;
            AlarmHelper.setWakeAlarm(this.mAppContext, this.mWakeAlarm);
            return;
        }
        this.mWakeAlert = this.mOldWakeAlert;
        setAlarmPrefSummary();
        this.mWakeAlarm.alert = this.mWakeAlert;
        AlarmHelper.setWakeAlarm(this.mAppContext, this.mWakeAlarm);
    }

    /* JADX INFO: renamed from: lambda$initFragmentResultLauncher$3$com-android-deskclock-settings-BedtimeSettingsFragment, reason: not valid java name */
    /* synthetic */ void m91x1669a9d4(ActivityResult activityResult) {
        int resultCode = activityResult.getResultCode();
        Log.i(TAG, "toCtaRingToneLauncher resultCode: " + resultCode);
        if (activityResult.getResultCode() == -3) {
            showCtaRingTonePermissionDialog();
            return;
        }
        if (resultCode == 1) {
            AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.settings.BedtimeSettingsFragment.4
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
        SystemPermissionUtil.showPermissionDeclare(this.mParentActivity, this.toCTALauncher);
    }

    private void openRingtonePicker() {
        if (XiaoAiRingtoneHelper.isXiaoAiAlarm(this.mParentActivity, Integer.MIN_VALUE)) {
            this.mWakeAlert = XiaoAiRingtoneHelper.getRingtoneUri();
        }
        this.toThemeRingtoneLauncher.launch(MiuiTheme.createRingtonePickerIntent(this.mWakeAlert, MiuiTheme.createAlarmRingtoneExtrasWithXiaoAi(), TAG));
    }

    private void jumpToRingtonePicker() {
        if (RingtoneUriCompat.atLeastU() && !Util.isInternational() && !UserNoticeUtil.isNetPermissionAgreed()) {
            showCtaRingTonePermissionDialog();
            return;
        }
        Intent intent = new Intent(this.mParentActivity, (Class<?>) AlarmRingtonePickerActivity.class);
        intent.putExtra(AlarmRingtonePickerActivity.IS_SET_MODE, false);
        intent.putExtra(AlarmRingtonePickerActivity.IS_FROM_ALARM, true);
        Uri ringtoneUri = this.mWakeAlert;
        if (XiaoAiRingtoneHelper.isXiaoAiAlarm(this.mParentActivity, Integer.MIN_VALUE)) {
            ringtoneUri = XiaoAiRingtoneHelper.getRingtoneUri();
        }
        intent.putExtra("android.intent.extra.ringtone.PICKED_URI", ringtoneUri);
        intent.putExtra(AlarmRingtonePickerActivity.IS_SUPPORT_XIAO_AI_RINGTONE, true);
        this.toRingTonePickerLauncher.launch(intent);
    }

    private void showCtaRingTonePermissionDialog() {
        SystemPermissionUtil.showPermissionDeclare(this.mParentActivity, this.toCtaRingToneLauncher);
    }

    private void setWakeAlarmState(Boolean bool) {
        if (bool.booleanValue()) {
            this.mWakeAlarm.enabled = true;
        } else {
            this.mWakeAlarm.enabled = false;
        }
        this.mWakeAlarm.skipTime = 0L;
        AlarmHelper.setWakeAlarm(this.mAppContext, this.mWakeAlarm);
        AlarmHelper.setSleepNotification(this.mAppContext);
        Log.f("DC:BedtimeSettingsActivity", "cancel ACTION_REACH_WAKE_TIME");
        AlarmUtils.cancelAlarm(this.mAppContext, AlarmHelper.ACTION_REACH_WAKE_TIME);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        MiHomeHelper miHomeHelper = this.miHomeHelper;
        if (miHomeHelper != null) {
            miHomeHelper.release();
            this.miHomeHelper = null;
        }
        RingtoneHelper.handleWakeAlert(this.mWakeAlert);
    }

    public void notifyBedtimeChanged(Context context) {
        if (this.miHomeHelper == null) {
            this.miHomeHelper = new MiHomeHelper(context);
        }
        this.miHomeHelper.notifyBedtimeChanged();
    }

    private void setDaysOfWeek(Alarm.DaysOfWeek daysOfWeek) {
        RepeatAlarmController.setRepeatDaysOfWeek(daysOfWeek);
    }

    public void handleBedtimeRepeatResult() {
        if (this.mRepeatAlarmController.isSelfDefineDialogShow()) {
            this.mRepeatAlarmController.handleSelfDefineResult();
        } else {
            handleWakeAlarmRepeat();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissBedtimeRepeatDialog() {
        handleBedtimeRepeatResult();
        BottomSheetModal bottomSheetModal = this.mRepeatAlarmBottomSheetModal;
        if (bottomSheetModal != null) {
            bottomSheetModal.dismiss();
            this.mRepeatAlarmBottomSheetModal = null;
        }
    }

    public boolean isBedtimeRepeatAlarmDialogShow() {
        return (this.mRepeatAlarmController == null || this.mRepeatAlarmBottomSheetModal == null) ? false : true;
    }
}
