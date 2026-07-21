package com.android.deskclock.settings;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.monitor.MonitorHelper;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.ShutdownAlarm;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import miuix.preference.DropDownPreference;
import miuix.preference.PreferenceFragment;

/* JADX INFO: loaded from: classes.dex */
public class AlarmSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    public static final String DEFAULT_SNOOZE_DURATION = "10";
    public static final String DEFAULT_SNOOZE_REPEAT_COUNT = "3";
    public static final String DEFAULT_VOLUME_BEHAVIOR = "1";
    public static final String KEY_ALARM_ARRIVING_NOTIFICATION = "alarm_arriving_notification";
    public static final String KEY_ALARM_ASCENDING = "alarm_ascending_mode";
    public static final String KEY_ALARM_SNOOZE_COUNT = "snooze_repeat_count";
    public static final String KEY_ALARM_SNOOZE_DURATION = "snooze_duration";
    public static final String KEY_ALERT_VIBRATE = "alert_vibrate_state";
    public static final String KEY_LIFE_POST_STATE = "life_post_state";
    public static final String KEY_LOCKED_SHOW_ALARM_ARRIVING = "locked_show_alarm_arriving_state";
    public static final String KEY_MORE_SETTING = "settings";
    public static final String KEY_SHUTDOWN_ALARM = "shutdown_alarm";
    public static final String KEY_SNOOZE_REPEAT_COUNT_REMAINDER = "snooze_repeat_count_remainder";
    public static final String KEY_VOLUME_BEHAVIOR = "volume_button_setting";
    public static final String TAG = "AlarmSettingsFragment";
    private CheckBoxPreference mAlarmArrivingPreference;
    private CheckBoxPreference mAlarmAscendingPreference;
    private DropDownPreference mAlarmSnoozeCountPreference;
    private DropDownPreference mAlarmSnoozeIntervalPreference;
    private CheckBoxPreference mAlertVibratePreference;
    private boolean mDialogFlag = false;
    private CheckBoxPreference mLifePostPreference;
    private CheckBoxPreference mLockedShowAlarmArrivingPreference;
    private Activity mParentActivity;
    private PreferenceCategory mPreferenceCategory;
    private PreferenceScreen mPreferenceScreen;
    private SharedPreferences mSharedPreferences;
    private CheckBoxPreference mShutdownAlarmPreference;
    private DropDownPreference mVolumeBtnSettingPreference;

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        FBEUtil.setStorageDeviceProtectedForFBE(getPreferenceManager());
        setPreferencesFromResource(R.xml.settings_alarm_fragment_miui12, str);
        DropDownPreference dropDownPreference = (DropDownPreference) findPreference("volume_button_setting");
        this.mVolumeBtnSettingPreference = dropDownPreference;
        dropDownPreference.setEntries(R.array.volume_button_setting_entries);
        this.mVolumeBtnSettingPreference.setEntryValues(new String[]{String.format(getString(R.string.volume_button_setting_values1), 0), String.format(getString(R.string.volume_button_setting_values2), 1), String.format(getString(R.string.volume_button_setting_values3), 2)});
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppContext());
        this.mSharedPreferences = defaultSharedPreferences;
        this.mVolumeBtnSettingPreference.setValue(defaultSharedPreferences.getString("volume_button_setting", "1"));
        this.mVolumeBtnSettingPreference.setOnPreferenceChangeListener(this);
        this.mAlarmSnoozeIntervalPreference = (DropDownPreference) findPreference("snooze_duration");
        this.mAlarmSnoozeIntervalPreference.setEntries(new String[]{String.format(getString(R.string.snooze_interval_entries_one), 1), String.format(getString(R.string.snooze_interval_entries_five), 5), String.format(getString(R.string.snooze_interval_entries_ten), 10), String.format(getString(R.string.snooze_interval_entries_fifteen), 15), String.format(getString(R.string.snooze_interval_entries_twenty), 20), String.format(getString(R.string.snooze_interval_entries_twenty_five), 25), String.format(getString(R.string.snooze_interval_entries_thirty), 30)});
        this.mAlarmSnoozeIntervalPreference.setEntryValues(new String[]{String.format(getString(R.string.snooze_interval_values_one), 1), String.format(getString(R.string.snooze_interval_values_five), 5), String.format(getString(R.string.snooze_interval_values_ten), 10), String.format(getString(R.string.snooze_interval_values_fifteen), 15), String.format(getString(R.string.snooze_interval_values_twenty), 20), String.format(getString(R.string.snooze_interval_values_twenty_five), 25), String.format(getString(R.string.snooze_interval_values_thirty), 30)});
        this.mAlarmSnoozeIntervalPreference.setValue(this.mSharedPreferences.getString("snooze_duration", "10"));
        this.mAlarmSnoozeIntervalPreference.setOnPreferenceChangeListener(this);
        this.mAlarmSnoozeCountPreference = (DropDownPreference) findPreference("snooze_repeat_count");
        this.mAlarmSnoozeCountPreference.setEntries(new String[]{String.format(getString(R.string.snooze_count_entries_one), 1), String.format(getString(R.string.snooze_count_entries_two), 2), String.format(getString(R.string.snooze_count_entries_three), 3), String.format(getString(R.string.snooze_count_entries_five), 5), String.format(getString(R.string.snooze_count_entries_ten), 10)});
        this.mAlarmSnoozeCountPreference.setEntryValues(new String[]{String.format(getString(R.string.snooze_count_values_one), 1), String.format(getString(R.string.snooze_count_values_two), 2), String.format(getString(R.string.snooze_count_values_three), 3), String.format(getString(R.string.snooze_count_values_five), 5), String.format(getString(R.string.snooze_count_values_ten), 10)});
        this.mAlarmSnoozeCountPreference.setValue(this.mSharedPreferences.getString("snooze_repeat_count", "3"));
        this.mAlarmSnoozeCountPreference.setOnPreferenceChangeListener(this);
        this.mParentActivity = getActivity();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mPreferenceScreen = preferenceScreen;
        this.mPreferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(KEY_MORE_SETTING);
        this.mShutdownAlarmPreference = (CheckBoxPreference) findPreference("shutdown_alarm");
        this.mAlarmAscendingPreference = (CheckBoxPreference) findPreference("alarm_ascending_mode");
        this.mAlarmArrivingPreference = (CheckBoxPreference) findPreference("alarm_arriving_notification");
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("locked_show_alarm_arriving_state");
        this.mLockedShowAlarmArrivingPreference = checkBoxPreference;
        checkBoxPreference.setSummary(String.format(getString(R.string.locked_show_arriving_alarm_summary), 12));
        this.mLifePostPreference = (CheckBoxPreference) findPreference(KEY_LIFE_POST_STATE);
        if (!MiuiSdk.isMiui15()) {
            if (this.mLifePostPreference != null && Util.isInternational()) {
                this.mLifePostPreference.setVisible(false);
            }
        } else {
            this.mPreferenceCategory.removePreference(this.mLifePostPreference);
        }
        this.mAlertVibratePreference = (CheckBoxPreference) findPreference(KEY_ALERT_VIBRATE);
        setShutDownAlarmSummary(this.mSharedPreferences.getBoolean("shutdown_alarm", true));
        StatHelper.deskclockEvent(StatHelper.EVENT_SETTINGS_ACTIVE_COUNT);
        if (this.mShutdownAlarmPreference != null) {
            if (Util.supportShutdownAlarm()) {
                this.mPreferenceCategory.addPreference(this.mShutdownAlarmPreference);
                this.mShutdownAlarmPreference.setOnPreferenceChangeListener(this);
            } else {
                this.mPreferenceCategory.removePreference(this.mShutdownAlarmPreference);
            }
        }
        if (Util.isSupportVibrator(this.mParentActivity)) {
            return;
        }
        this.mPreferenceCategory.removePreference(this.mAlertVibratePreference);
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if ("volume_button_setting".equals(preference.getKey())) {
            StatHelper.alarmEvent(StatHelper.EVENT_CLICK_VOLUME_BEHAVIOR);
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.SETTINGS_POWER_BTN_CLICK);
            return false;
        }
        if ("snooze_duration".equals(preference.getKey())) {
            StatHelper.alarmEvent(StatHelper.EVENT_CLICK_SNOOZE_DURATION_SETTINGS);
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.SETTINGS_SNOOZE_CLICK);
            return false;
        }
        if (!"snooze_repeat_count".equals(preference.getKey())) {
            return false;
        }
        StatHelper.alarmEvent(StatHelper.EVENT_CLICK_SNOOZE_DURATION_SETTINGS);
        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.SETTINGS_SNOOZE_CLICK);
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("alarm_ascending_mode".equals(preference.getKey())) {
            Boolean bool = (Boolean) obj;
            StatHelper.updateAlarmProperties(StatHelper.PROP_VOLUME_ASCENDING, bool.booleanValue() ? "ON" : "OFF");
            OneTrackStatHelper.trackBoolEvent(bool.booleanValue(), OneTrackStatHelper.SETTINGS_VOLUME_ASCENDING_CHANGE);
        } else if ("alarm_arriving_notification".equals(preference.getKey())) {
            Boolean bool2 = (Boolean) obj;
            StatHelper.updateAlarmProperties(StatHelper.PROP_ALARM_ARRIVING, bool2.booleanValue() ? "ON" : "OFF");
            OneTrackStatHelper.trackBoolEvent(bool2.booleanValue(), OneTrackStatHelper.SETTINGS_ALARM_ARRING_NOTIFICATION_STATE_CHANGE);
            this.mSharedPreferences.edit().putBoolean("alarm_arriving_notification", bool2.booleanValue()).apply();
            AlarmHelper.setNextAlert(DeskClockApp.getAppContext());
        } else if ("shutdown_alarm".equals(preference.getKey())) {
            Boolean bool3 = (Boolean) obj;
            this.mSharedPreferences.edit().putBoolean("shutdown_alarm", bool3.booleanValue()).apply();
            if (bool3.booleanValue()) {
                Alarm alarmCalculateNextAlert = AlarmHelper.calculateNextAlert(DeskClockApp.getAppContext());
                if (alarmCalculateNextAlert != null) {
                    ShutdownAlarm.setWakeAlarm(DeskClockApp.getAppContext(), alarmCalculateNextAlert.time / 1000);
                }
            } else {
                ShutdownAlarm.disableWakeAlarm();
            }
            setShutDownAlarmSummary(bool3.booleanValue());
            StatHelper.updateAlarmProperties(StatHelper.PROP_POWER_OFF_ALARM, bool3.booleanValue() ? "ON" : "OFF");
            OneTrackStatHelper.trackBoolEvent(bool3.booleanValue(), OneTrackStatHelper.SETTINGS_SHUTDOWN_ALARM_STATE_CHANGE);
            MonitorHelper.setShutDownAlarmState();
        } else if (KEY_ALERT_VIBRATE.equals(preference.getKey())) {
            Boolean bool4 = (Boolean) obj;
            this.mSharedPreferences.edit().putBoolean(KEY_ALERT_VIBRATE, bool4.booleanValue()).apply();
            if (this.mParentActivity != null) {
                ContentValues contentValues = new ContentValues(2);
                contentValues.put("vibrate", Integer.valueOf(bool4.booleanValue() ? 1 : 0));
                this.mParentActivity.getContentResolver().update(Alarm.Columns.CONTENT_URI, contentValues, null, null);
                BedtimeUtil.setWakeAlarmVibrate(this.mParentActivity, bool4.booleanValue() ? 1 : 0);
                AlarmHelper.setNextAlert(this.mParentActivity);
            }
        } else if ("volume_button_setting".equals(preference.getKey())) {
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.SETTINGS_POWER_BTN_CLICK);
        } else if ("snooze_repeat_count".equals(preference.getKey())) {
            updateSnoozeRepeatCountRemind((String) obj);
        } else if ("locked_show_alarm_arriving_state".equals(preference.getKey())) {
            Boolean bool5 = (Boolean) obj;
            this.mSharedPreferences.edit().putBoolean("locked_show_alarm_arriving_state", bool5.booleanValue()).apply();
            try {
                Settings.Secure.putInt(this.mParentActivity.getContentResolver(), AlarmHelper.LOCKED_SCREEN_ALARM_IS_SHOW, ((Boolean) obj).booleanValue() ? 1 : 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Alarm alarmCalculateNextAlert2 = AlarmHelper.calculateNextAlert(DeskClockApp.getAppContext());
            if (bool5.booleanValue() && alarmCalculateNextAlert2 != null) {
                try {
                    Settings.Secure.putLong(this.mParentActivity.getContentResolver(), AlarmHelper.LOCKED_SCREEN_ALARM_STATUS, alarmCalculateNextAlert2.time);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } else {
                try {
                    Settings.Secure.putLong(this.mParentActivity.getContentResolver(), AlarmHelper.LOCKED_SCREEN_ALARM_STATUS, 0L);
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
        }
        return true;
    }

    private void refresh() {
        CheckBoxPreference checkBoxPreference = this.mAlarmAscendingPreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(this);
        }
        CheckBoxPreference checkBoxPreference2 = this.mAlarmArrivingPreference;
        if (checkBoxPreference2 != null) {
            checkBoxPreference2.setOnPreferenceChangeListener(this);
        }
        CheckBoxPreference checkBoxPreference3 = this.mAlertVibratePreference;
        if (checkBoxPreference3 != null) {
            checkBoxPreference3.setOnPreferenceChangeListener(this);
        }
        CheckBoxPreference checkBoxPreference4 = this.mLockedShowAlarmArrivingPreference;
        if (checkBoxPreference4 != null) {
            checkBoxPreference4.setOnPreferenceChangeListener(this);
        }
    }

    private void setShutDownAlarmSummary(boolean z) {
        if (z) {
            this.mShutdownAlarmPreference.setSummary(String.format(getString(R.string.shutdown_alarm_alert_enable_summary_new), 10));
        } else {
            this.mShutdownAlarmPreference.setSummary(R.string.shutdown_alarm_disable_summary);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }

    public static void popSnoozeSetToast(String str) {
        DeskClockApp.getAppDEContext();
        if (TextUtils.isEmpty(str)) {
            return;
        }
        Toast.makeText(DeskClockApp.getAppDEContext(), str, 1).show();
    }

    public static void updateSnoozeRepeatCountRemind(String str) {
        FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).edit().putString("snooze_repeat_count_remainder", str).apply();
    }

    public static boolean getVibrateState() {
        return FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppContext()).getBoolean(KEY_ALERT_VIBRATE, true);
    }
}
