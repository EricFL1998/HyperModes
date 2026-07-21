package com.android.deskclock.settings;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import androidx.preference.Preference;
import com.android.deskclock.BuildConfig;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.settings.pref.ClockValuePreference;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.permission.PermissionUtil;
import miuix.preference.PreferenceFragment;

/* JADX INFO: loaded from: classes.dex */
public class PermissionDescFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private static final String KAY_READ_EXTERNAL_PERMISSION = "key_read_external_permission";
    private static final String KEY_FORCE_START_PERMISSION = "key_force_start_permission";
    public static final String TAG = "PermissionDescFragment";
    private ClockValuePreference mForceStartPermissionPreference;
    private ClockValuePreference mReadStoragePermissionPreference;

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        FBEUtil.setStorageDeviceProtectedForFBE(getPreferenceManager());
        setPreferencesFromResource(R.xml.permission_desc_fragment, str);
        ClockValuePreference clockValuePreference = (ClockValuePreference) findPreference(KAY_READ_EXTERNAL_PERMISSION);
        this.mReadStoragePermissionPreference = clockValuePreference;
        clockValuePreference.setOnPreferenceClickListener(this);
        ClockValuePreference clockValuePreference2 = (ClockValuePreference) findPreference(KEY_FORCE_START_PERMISSION);
        this.mForceStartPermissionPreference = clockValuePreference2;
        if (clockValuePreference2 != null) {
            clockValuePreference2.setAccessibilityDelegate(new ClockValuePreference.IAccessibilityDelegate() { // from class: com.android.deskclock.settings.PermissionDescFragment.1
                @Override // com.android.deskclock.settings.pref.ClockValuePreference.IAccessibilityDelegate
                public void setAccessibilityDelegate(View view) {
                    if (view != null) {
                        try {
                            AccessibilityManager accessibilityManager = (AccessibilityManager) view.getContext().getSystemService("accessibility");
                            if (accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled()) {
                                view.setClickable(false);
                            }
                        } catch (Exception e) {
                            Log.e(PermissionDescFragment.TAG, "setAccessibilityDelegate error: " + e);
                        }
                    }
                }
            });
        }
        resetPermission();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        resetPermission();
    }

    private void resetPermission() {
        if (PermissionUtil.isPermissionGranted(DeskClockApp.getAppDEContext(), "android.permission.READ_EXTERNAL_STORAGE") || PermissionUtil.isPermissionGranted(DeskClockApp.getAppDEContext(), "android.permission.READ_MEDIA_AUDIO")) {
            this.mReadStoragePermissionPreference.setValue(R.string.permission_granted);
        } else {
            this.mReadStoragePermissionPreference.setValue(R.string.permission_rejected);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (!KAY_READ_EXTERNAL_PERMISSION.equals(preference.getKey())) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 33) {
            if (PermissionUtil.canPermissionAsk(DeskClockApp.getAppDEContext(), "android.permission.READ_MEDIA_AUDIO")) {
                PermissionUtil.requestPermissionIfNeeded(getActivity(), "android.permission.READ_MEDIA_AUDIO");
                return false;
            }
            openPermissionManagerPage();
            return false;
        }
        if (PermissionUtil.canPermissionAsk(DeskClockApp.getAppDEContext(), "android.permission.READ_EXTERNAL_STORAGE")) {
            PermissionUtil.requestPermissionIfNeeded(getActivity(), "android.permission.READ_EXTERNAL_STORAGE");
            return false;
        }
        openPermissionManagerPage();
        return false;
    }

    @Override // androidx.fragment.app.Fragment
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 1) {
            return;
        }
        resetPermission();
    }

    private void openPermissionManagerPage() {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", BuildConfig.APPLICATION_ID);
        startActivity(intent);
    }

    @Override // miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        ClockValuePreference clockValuePreference = this.mReadStoragePermissionPreference;
        if (clockValuePreference != null) {
            clockValuePreference.setOnPreferenceClickListener(null);
        }
        ClockValuePreference clockValuePreference2 = this.mForceStartPermissionPreference;
        if (clockValuePreference2 != null) {
            clockValuePreference2.release();
        }
    }
}
