package com.android.deskclock.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SeekBarPreference;
import com.android.deskclock.R;
import miuix.androidbasewidget.widget.SeekBar;

/* JADX INFO: loaded from: classes.dex */
public class ClockSeekBarPreference extends SeekBarPreference {
    private int mMax;
    private int mMin;
    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public interface OnSeekBarChangeListener {
        void onProgressChanged(int i, boolean z);

        void onStartTrackingTouch();
    }

    public ClockSeekBarPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public ClockSeekBarPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public ClockSeekBarPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ClockSeekBarPreference(Context context) {
        super(context);
    }

    public void setCustomMax(int i) {
        this.mMax = i;
        setMax(i);
    }

    public void setCustomMin(int i) {
        this.mMin = i;
        setMin(0);
    }

    @Override // androidx.preference.SeekBarPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        SeekBar seekBar = (SeekBar) preferenceViewHolder.findViewById(R.id.seekbar);
        int i = this.mMax;
        if (i != 0) {
            seekBar.setDraggableMinPercentProgress(this.mMin / i);
        }
        seekBar.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() { // from class: com.android.deskclock.widget.ClockSeekBarPreference.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(android.widget.SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(android.widget.SeekBar seekBar2, int i2, boolean z) {
                if (ClockSeekBarPreference.this.mOnSeekBarChangeListener != null) {
                    ClockSeekBarPreference.this.mOnSeekBarChangeListener.onProgressChanged(i2, z);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(android.widget.SeekBar seekBar2) {
                if (ClockSeekBarPreference.this.mOnSeekBarChangeListener != null) {
                    ClockSeekBarPreference.this.mOnSeekBarChangeListener.onStartTrackingTouch();
                }
            }
        });
        modifyTalkBackBehavior(preferenceViewHolder);
    }

    private void modifyTalkBackBehavior(PreferenceViewHolder preferenceViewHolder) {
        preferenceViewHolder.itemView.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: com.android.deskclock.widget.ClockSeekBarPreference.2
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                accessibilityNodeInfo.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
                accessibilityNodeInfo.setClickable(false);
            }
        });
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    public void removeOnSeekBarChangeListener() {
        this.mOnSeekBarChangeListener = null;
    }
}
