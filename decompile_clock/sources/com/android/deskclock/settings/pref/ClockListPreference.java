package com.android.deskclock.settings.pref;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceViewHolder;
import com.android.deskclock.R;

/* JADX INFO: loaded from: classes.dex */
public class ClockListPreference extends ListPreference {
    public ClockListPreference(Context context) {
        this(context, null);
    }

    public ClockListPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ClockListPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        setLayoutResource(R.layout.preference_value_list);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(R.id.value_right);
        if (textView != null) {
            CharSequence entry = getEntry();
            if (!TextUtils.isEmpty(entry)) {
                textView.setText(entry);
                textView.setVisibility(0);
            } else {
                textView.setVisibility(8);
            }
        }
    }
}
