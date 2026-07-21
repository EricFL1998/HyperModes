package com.android.deskclock.view.pref;

import android.content.Context;
import android.preference.ListPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.deskclock.R;

/* JADX INFO: loaded from: classes.dex */
public class AlarmListPreference extends ListPreference {
    public AlarmListPreference(Context context) {
        this(context, null);
    }

    public AlarmListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.preference.Preference
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView textView = (TextView) view.findViewById(R.id.value_right);
        if (textView != null) {
            CharSequence entry = getEntry();
            if (!TextUtils.isEmpty(entry)) {
                textView.setText(entry);
                textView.setVisibility(0);
            } else {
                textView.setVisibility(8);
            }
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.arrow_right);
        TextView textView2 = (TextView) view.findViewById(android.R.id.title);
        TextView textView3 = (TextView) view.findViewById(android.R.id.summary);
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

    @Override // android.preference.Preference
    protected View onCreateView(ViewGroup viewGroup) {
        super.onCreateView(viewGroup);
        return LayoutInflater.from(getContext()).inflate(R.layout.preference_value_list, viewGroup, false);
    }
}
