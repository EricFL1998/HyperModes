package com.android.deskclock.settings.pref;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.deskclock.R;
import miuix.core.util.MiuixUIUtils;

/* JADX INFO: loaded from: classes.dex */
public class ClockValuePreference extends Preference {
    private IAccessibilityDelegate mAccessibilityDelegate;
    private CharSequence mValue;
    private int mValueRes;

    public interface IAccessibilityDelegate {
        void setAccessibilityDelegate(View view);
    }

    public ClockValuePreference(Context context) {
        this(context, null);
    }

    public ClockValuePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ClockValuePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        setLayoutResource(R.layout.preference_value_list);
    }

    public void setValue(String str) {
        boolean z = !TextUtils.equals(this.mValue, str);
        if (z) {
            this.mValueRes = 0;
            this.mValue = str;
            if (z) {
                notifyChanged();
            }
        }
    }

    public void setValue(int i) {
        setValue(getContext().getString(i));
        this.mValueRes = i;
    }

    public int getValueRes() {
        return this.mValueRes;
    }

    public CharSequence getValue() {
        return this.mValue;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        IAccessibilityDelegate iAccessibilityDelegate = this.mAccessibilityDelegate;
        if (iAccessibilityDelegate != null) {
            iAccessibilityDelegate.setAccessibilityDelegate(preferenceViewHolder.itemView);
        }
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(R.id.value_right);
        if (textView != null) {
            CharSequence value = getValue();
            if (!TextUtils.isEmpty(value)) {
                textView.setText(value);
                textView.setVisibility(0);
            } else {
                textView.setVisibility(8);
            }
        }
        if (MiuixUIUtils.getFontLevel(getContext()) == 2) {
            RelativeLayout relativeLayout = (RelativeLayout) preferenceViewHolder.itemView.findViewById(R.id.titleVIew);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
            layoutParams.addRule(3, android.R.id.summary);
            textView.setLayoutParams(layoutParams);
            textView.setMaxWidth(Integer.MAX_VALUE);
            textView.setGravity(GravityCompat.START);
            if (textView.getParent() != null) {
                ((ViewGroup) textView.getParent()).removeView(textView);
            }
            relativeLayout.addView(textView);
        }
    }

    public void setAccessibilityDelegate(IAccessibilityDelegate iAccessibilityDelegate) {
        this.mAccessibilityDelegate = iAccessibilityDelegate;
    }

    public void release() {
        this.mAccessibilityDelegate = null;
    }
}
