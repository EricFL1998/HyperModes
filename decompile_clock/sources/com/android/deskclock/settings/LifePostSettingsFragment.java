package com.android.deskclock.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiMusic;
import com.android.deskclock.addition.weather.WeatherUtils;
import com.android.deskclock.util.DialogUtil;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.permission.SystemPermissionUtil;
import com.android.deskclock.util.permission.UserNoticeUtil;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.view.SimpleDialogFragment;
import miuix.preference.PreferenceFragment;

/* JADX INFO: loaded from: classes.dex */
public class LifePostSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final int BUTTON_ANNI_DURATION = 200;
    public static final String KEY_OPEN_ALARM_LIFE_POST = "key_open_alarm_life_post";
    public static final String KEY_OPEN_ALARM_LIFE_POST_BACKGROUND = "key_open_life_post_background";
    public static final String KEY_OPEN_ALARM_LIFE_POST_NEWS = "key_open_alarm_life_post_news";
    private static final int REQUEST_NETWORK_CODE = 1;
    private static final int REQUEST_NETWORK_CODE_NEW = 2;
    public static final String TAG = "LifePostSettingsFragment";
    private CheckBoxPreference mLifePostPreference;
    private CheckBoxPreference mListenNewsPreference;
    private Activity mParentActivity;
    private PreferenceScreen mPreferenceScreen;
    private SimpleDialogFragment mUserNoticeDialog = null;
    private SimpleDialogFragment mNetPermissionDialog = null;

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        FBEUtil.setStorageDeviceProtectedForFBE(getPreferenceManager());
        this.mParentActivity = getActivity();
        setPreferencesFromResource(R.xml.settings_life_post_fragment_lite, str);
        FBEUtil.getDefaultSharedPreferences(this.mParentActivity).edit().putBoolean("key_open_life_post_background", false).apply();
        this.mPreferenceScreen = getPreferenceScreen();
        this.mLifePostPreference = (CheckBoxPreference) findPreference("key_open_alarm_life_post");
        this.mListenNewsPreference = (CheckBoxPreference) findPreference("key_open_alarm_life_post_news");
        this.mLifePostPreference.setSummary(String.format(getResources().getString(R.string.life_post_setting_open_summary_new), "4:00", "10:00"));
    }

    private void updatePreferenceState() {
        if (MiuiMusic.supportNews()) {
            return;
        }
        this.mListenNewsPreference.setChecked(false);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("key_open_alarm_life_post".equals(preference.getKey())) {
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.LIFE_POST_STATE_CLICK);
            Boolean bool = (Boolean) obj;
            if (bool.booleanValue()) {
                remindFBEToast();
            } else {
                this.mListenNewsPreference.setChecked(false);
            }
            StatHelper.trackEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_LIFE_POST_SETTINGS, bool.booleanValue() ? "ON" : "OFF");
            return true;
        }
        if ("key_open_life_post_background".equals(preference.getKey())) {
            OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.LIFE_POST_BACKGROUND_STATE_CLICK);
            return true;
        }
        if (!"key_open_alarm_life_post_news".equals(preference.getKey())) {
            return true;
        }
        OneTrackStatHelper.trackClickEvent(OneTrackStatHelper.LIFE_POST_NEWS_STATE_CLICK);
        if (!((Boolean) obj).booleanValue()) {
            return true;
        }
        openListenNewsView();
        return true;
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        refresh();
        updatePreferenceState();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        DialogUtil.dismissDialogFragment(this.mUserNoticeDialog);
        this.mUserNoticeDialog = null;
    }

    private void refresh() {
        CheckBoxPreference checkBoxPreference = this.mLifePostPreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(this);
        }
        if (Util.isChinese(DeskClockApp.getAppContext())) {
            this.mPreferenceScreen.addPreference(this.mListenNewsPreference);
            this.mListenNewsPreference.setOnPreferenceChangeListener(this);
            if (UserNoticeUtil.isNetPermissionAgreed()) {
                return;
            }
            this.mListenNewsPreference.setChecked(false);
            return;
        }
        this.mPreferenceScreen.removePreference(this.mListenNewsPreference);
    }

    private void openListenNewsView() {
        if (!MiuiMusic.supportNews()) {
            videoNewsToast();
            closeListenerNewsSlowly();
        } else if (!UserNoticeUtil.isNetPermissionAgreed()) {
            showNetPermissionDialog();
        } else {
            StatHelper.trackEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_LIFE_LISTEN_NEWS, "ON");
        }
    }

    public void handleActivityResult(int i, int i2, Intent intent) {
        if (i == 1 || i == 2) {
            if (i2 == 1) {
                UserNoticeUtil.setAcceptNetPermission(true);
                StatHelper.init(DeskClockApp.getAppContext());
                OneTrackStatHelper.init(DeskClockApp.getAppContext());
                this.mListenNewsPreference.setChecked(true);
                return;
            }
            if (i2 == 666 || i2 == 0) {
                UserNoticeUtil.setAcceptNetPermission(false);
                UserNoticeUtil.setRemindNetPermission(false);
                closeListenerNewsSlowly();
                return;
            }
            Log.e(SystemPermissionUtil.TAG, "lack of important information");
        }
    }

    private void showNetPermissionDialog() {
        if (SystemPermissionUtil.showPermissionDeclare(this.mParentActivity, 1, 2)) {
            return;
        }
        this.mUserNoticeDialog = UserNoticeUtil.showUserNoticeDialog(this.mParentActivity, R.string.listen_news_privacy, new UserNoticeUtil.OnNetPermissionListener() { // from class: com.android.deskclock.settings.LifePostSettingsFragment.1
            @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
            public void onAccept() {
                LifePostSettingsFragment.this.mListenNewsPreference.setChecked(true);
            }

            @Override // com.android.deskclock.util.permission.UserNoticeUtil.OnNetPermissionListener
            public void onReject() {
                LifePostSettingsFragment.this.closeListenerNewsSlowly();
            }
        }, getFragmentManager());
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        DialogUtil.dismissDialogFragment(this.mNetPermissionDialog);
        this.mNetPermissionDialog = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeListenerNewsSlowly() {
        new Handler().postDelayed(new Runnable() { // from class: com.android.deskclock.settings.LifePostSettingsFragment.2
            @Override // java.lang.Runnable
            public void run() {
                LifePostSettingsFragment.this.mListenNewsPreference.setChecked(false);
            }
        }, 200L);
        StatHelper.trackEvent(StatHelper.CATEGORY_LIFE_POST, StatHelper.EVENT_LIFE_LISTEN_NEWS, "OFF");
    }

    private void remindFBEToast() {
        Context appDEContext = DeskClockApp.getAppDEContext();
        if (WeatherUtils.isFBEWeather() || !FBEUtil.isFBEDeviceAndSetedUpScreenLock(appDEContext)) {
            return;
        }
        Toast.makeText(DeskClockApp.getAppDEContext(), R.string.life_post_setting_FBE_remind, 1).show();
    }

    private void videoNewsToast() {
        DeskClockApp.getAppDEContext();
        Toast.makeText(DeskClockApp.getAppDEContext(), R.string.life_post_setting_video_news_remind, 1).show();
    }
}
