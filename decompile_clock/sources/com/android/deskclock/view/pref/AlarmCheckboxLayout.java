package com.android.deskclock.view.pref;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.util.FBEUtil;
import miuix.slidingwidget.widget.SlidingButton;

/* JADX INFO: loaded from: classes.dex */
public class AlarmCheckboxLayout extends LinearLayout {
    private OnPreferenceChangeListener mChangeListener;
    private boolean mChecked;
    private boolean mDefaultValue;
    private String mKey;
    private LinearLayout mRootView;
    private String mSummary;
    private TextView mSummaryTv;
    private String mTitle;
    private TextView mTitleTv;
    private SlidingButton mToggleCb;

    public interface OnPreferenceChangeListener {
        void onPreferenceChange(String str, boolean z);
    }

    public AlarmCheckboxLayout(Context context) {
        this(context, null);
    }

    public AlarmCheckboxLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AlarmCheckboxLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mChecked = false;
        LayoutInflater.from(context).inflate(R.layout.preference_checkbox, (ViewGroup) this, true);
        this.mRootView = (LinearLayout) findViewById(R.id.root);
        this.mTitleTv = (TextView) findViewById(R.id.title);
        TextView textView = (TextView) findViewById(R.id.summary);
        this.mSummaryTv = textView;
        textView.setText(String.format(getResources().getString(R.string.life_post_setting_open_summary_new), "4:00", "10:00"));
        this.mToggleCb = (SlidingButton) findViewById(R.id.toggle);
        if (attributeSet != null) {
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.AlarmCheckboxLayout);
            this.mTitle = typedArrayObtainStyledAttributes.getString(0);
            this.mSummary = typedArrayObtainStyledAttributes.getString(2);
            this.mKey = typedArrayObtainStyledAttributes.getString(1);
            this.mDefaultValue = typedArrayObtainStyledAttributes.getBoolean(3, false);
            typedArrayObtainStyledAttributes.recycle();
        }
        this.mChecked = getToggleValue(this.mKey, this.mDefaultValue);
        this.mTitleTv.setText(this.mTitle);
        String str = this.mSummary;
        if (str == null || str == "") {
            this.mSummaryTv.setVisibility(8);
        } else {
            this.mSummaryTv.setText(str);
        }
        this.mToggleCb.setChecked(this.mChecked);
        this.mRootView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.view.pref.AlarmCheckboxLayout.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AlarmCheckboxLayout alarmCheckboxLayout = AlarmCheckboxLayout.this;
                alarmCheckboxLayout.mChecked = !alarmCheckboxLayout.isChecked();
                AlarmCheckboxLayout.this.mToggleCb.setChecked(AlarmCheckboxLayout.this.mChecked);
                AlarmCheckboxLayout.this.notifyChanged();
            }
        });
        this.mToggleCb.setOnPerformCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.deskclock.view.pref.AlarmCheckboxLayout.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                AlarmCheckboxLayout.this.mChecked = z;
                AlarmCheckboxLayout.this.notifyChanged();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyChanged() {
        setToggleValue(this.mKey, this.mChecked);
        OnPreferenceChangeListener onPreferenceChangeListener = this.mChangeListener;
        if (onPreferenceChangeListener != null) {
            onPreferenceChangeListener.onPreferenceChange(this.mKey, this.mChecked);
        }
    }

    public void setOnPreferenceChangeListener(OnPreferenceChangeListener onPreferenceChangeListener) {
        this.mChangeListener = onPreferenceChangeListener;
    }

    private boolean getToggleValue(String str, boolean z) {
        return FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppContext()).getBoolean(str, z);
    }

    private void setToggleValue(String str, boolean z) {
        FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppContext()).edit().putBoolean(str, z).apply();
    }

    public boolean isChecked() {
        return this.mChecked;
    }

    public void setChecked(boolean z) {
        SlidingButton slidingButton = this.mToggleCb;
        if (slidingButton == null || z == this.mChecked) {
            return;
        }
        this.mChecked = z;
        slidingButton.setChecked(z);
        notifyChanged();
    }

    public void setAvailable(boolean z) {
        this.mToggleCb.setEnabled(z);
        this.mTitleTv.setEnabled(z);
        this.mSummaryTv.setEnabled(z);
        if (z) {
            this.mRootView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.view.pref.AlarmCheckboxLayout.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    AlarmCheckboxLayout alarmCheckboxLayout = AlarmCheckboxLayout.this;
                    alarmCheckboxLayout.mChecked = !alarmCheckboxLayout.isChecked();
                    AlarmCheckboxLayout.this.mToggleCb.setChecked(AlarmCheckboxLayout.this.mChecked);
                    AlarmCheckboxLayout.this.notifyChanged();
                }
            });
        } else {
            this.mRootView.setOnClickListener(null);
        }
    }
}
