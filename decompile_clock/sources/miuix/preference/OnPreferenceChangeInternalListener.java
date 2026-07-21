package miuix.preference;

import androidx.preference.Preference;

/* JADX INFO: loaded from: classes3.dex */
interface OnPreferenceChangeInternalListener {
    void notifyPreferenceChangeInternal(Preference preference);

    boolean onPreferenceChangeInternal(Preference preference, Object obj);
}
