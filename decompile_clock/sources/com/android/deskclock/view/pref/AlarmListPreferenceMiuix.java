package com.android.deskclock.view.pref;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceViewHolder;
import com.android.deskclock.R;

/* JADX INFO: loaded from: classes.dex */
public class AlarmListPreferenceMiuix extends ListPreference {
    public AlarmListPreferenceMiuix(Context context) {
        this(context, null);
    }

    public AlarmListPreferenceMiuix(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
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
        ImageView imageView = (ImageView) preferenceViewHolder.itemView.findViewById(R.id.arrow_right);
        TextView textView2 = (TextView) preferenceViewHolder.itemView.findViewById(android.R.id.title);
        TextView textView3 = (TextView) preferenceViewHolder.itemView.findViewById(android.R.id.summary);
        try {
            if (!isEnabled()) {
                textView.setEnabled(false);
                textView2.setEnabled(false);
                textView3.setEnabled(false);
                imageView.setEnabled(false);
            } else {
                textView.setEnabled(true);
                textView2.setEnabled(true);
                textView3.setEnabled(true);
                imageView.setEnabled(true);
            }
        } catch (Exception unused) {
        }
    }
}
