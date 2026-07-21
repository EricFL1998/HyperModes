package com.android.deskclock.settings;

import android.content.Intent;
import android.os.Bundle;
import androidx.preference.Preference;
import com.android.deskclock.R;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.permission.UserNoticeUtil;
import miuix.preference.PreferenceFragment;

/* JADX INFO: loaded from: classes.dex */
public class PrivacySettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private static final String PERMISSION_DESC = "permission_desc";
    private static final String QUERY_PRIVACY_POLICY = "query_privacy_policy";
    public static final String TAG = "PrivacySettingsFragment";
    private Preference mPermissionDesc;
    private Preference mQueryPrivacyPolicy;

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        FBEUtil.setStorageDeviceProtectedForFBE(getPreferenceManager());
        setPreferencesFromResource(R.xml.privacy_settings_fragment, str);
        this.mQueryPrivacyPolicy = findPreference(QUERY_PRIVACY_POLICY);
        this.mPermissionDesc = findPreference(PERMISSION_DESC);
        this.mQueryPrivacyPolicy.setOnPreferenceClickListener(this);
        this.mPermissionDesc.setOnPreferenceClickListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (getActivity() == null) {
            return false;
        }
        if (QUERY_PRIVACY_POLICY.equals(preference.getKey())) {
            UserNoticeUtil.gotoPrivacyWebPage(getActivity());
        } else if (PERMISSION_DESC.equals(preference.getKey())) {
            startActivity(new Intent(getActivity(), (Class<?>) PermissionDescActivity.class));
        }
        return false;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        Preference preference = this.mQueryPrivacyPolicy;
        if (preference != null) {
            preference.setOnPreferenceClickListener(null);
        }
        Preference preference2 = this.mPermissionDesc;
        if (preference2 != null) {
            preference2.setOnPreferenceClickListener(null);
        }
    }
}
