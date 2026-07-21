package com.android.deskclock.settings.pref;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.deskclock.R;
import miuix.appcompat.app.AlertDialog;
import miuix.core.util.MiuixUIUtils;
import miuix.preference.PreferenceFragment;

/* JADX INFO: loaded from: classes.dex */
public class ClockRepeatPreference extends Preference {
    public static final int ALARM_TYPE_EVERY_DAY = 1;
    public static final int ALARM_TYPE_LEGAL_WORKDAY = 2;
    public static final int ALARM_TYPE_MONDAY_TO_FRIDAY = 4;
    public static final int ALARM_TYPE_SELF_DEFINE = 5;
    private String mDefaultRepeatText;
    private PreferenceFragment mFragment;
    private AlertDialog mWeekDialog;
    private TextView valueView;

    public ClockRepeatPreference(Context context) {
        this(context, null);
    }

    public ClockRepeatPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ClockRepeatPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        setLayoutResource(R.layout.preference_value_list);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(R.id.value_right);
        this.valueView = textView;
        String str = this.mDefaultRepeatText;
        if (str != null) {
            textView.setText(str);
        }
        if (MiuixUIUtils.getFontLevel(getContext()) == 2) {
            RelativeLayout relativeLayout = (RelativeLayout) preferenceViewHolder.itemView.findViewById(R.id.titleVIew);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
            layoutParams.addRule(3, android.R.id.summary);
            this.valueView.setLayoutParams(layoutParams);
            this.valueView.setMaxWidth(Integer.MAX_VALUE);
            this.valueView.setGravity(GravityCompat.START);
            if (this.valueView.getParent() != null) {
                ((ViewGroup) this.valueView.getParent()).removeView(this.valueView);
            }
            relativeLayout.addView(this.valueView);
        }
    }

    public void setPrefValue(String str) {
        this.mDefaultRepeatText = str;
        TextView textView = this.valueView;
        if (textView == null) {
            return;
        }
        textView.setText(str);
    }

    public String getPrefValue() {
        TextView textView = this.valueView;
        if (textView != null) {
            return (String) textView.getText();
        }
        return null;
    }
}
