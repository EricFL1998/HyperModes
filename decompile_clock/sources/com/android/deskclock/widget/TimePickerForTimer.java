package com.android.deskclock.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.TimeUtil;
import com.android.deskclock.util.Util;
import java.util.Locale;
import miuix.pickerwidget.date.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class TimePickerForTimer extends FrameLayout {
    private static final boolean DEFAULT_ENABLED_STATE = true;
    public static final int DEFAULT_HOUR_VALUE = 0;
    public static final int DEFAULT_MINUTE_VALUE = 5;
    public static final int DEFAULT_SECOND_VALUE = 0;
    private static final OnTimeChangedListener NO_OP_CHANGE_LISTENER = new OnTimeChangedListener() { // from class: com.android.deskclock.widget.TimePickerForTimer.1
        @Override // com.android.deskclock.widget.TimePickerForTimer.OnTimeChangedListener
        public void onTimeChanged(TimePickerForTimer timePickerForTimer, int i, int i2, int i3) {
        }
    };
    private Locale mCurrentLocale;
    private final NumberPicker mHourSpinner;
    private boolean mIsEnabled;
    private final NumberPicker mMinuteSpinner;
    private OnTimeChangedListener mOnTimeChangedListener;
    private OnTimerScrollListener mOnTimerScrollListener;
    private int mPaddingBottom;
    private int mPaddingTop;
    private final NumberPicker mSecondSpinner;
    private Calendar mTempCalendar;

    public interface OnTimeChangedListener {
        void onTimeChanged(TimePickerForTimer timePickerForTimer, int i, int i2, int i3);
    }

    public interface OnTimerScrollListener {
        void onScrollStateChange(int i);
    }

    public TimePickerForTimer(Context context) {
        this(context, null);
    }

    public TimePickerForTimer(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TimePickerForTimer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsEnabled = true;
        this.mPaddingTop = 0;
        this.mPaddingBottom = 0;
        TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.SetAlarmNumberPicker, i, 0);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(0, R.layout.time_picker);
        this.mPaddingTop = typedArrayObtainStyledAttributes.getDimensionPixelSize(2, 0);
        this.mPaddingBottom = typedArrayObtainStyledAttributes.getDimensionPixelSize(1, 0);
        typedArrayObtainStyledAttributes.recycle();
        setCurrentLocale(Locale.getDefault());
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(resourceId, (ViewGroup) this, true);
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.hour);
        this.mHourSpinner = numberPicker;
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(23);
        numberPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: com.android.deskclock.widget.TimePickerForTimer.2
            @Override // com.android.deskclock.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker2, int i2, int i3) {
                TimePickerForTimer.this.onTimeChanged();
            }
        });
        numberPicker.setOnScrollListener(new NumberPicker.OnScrollListener() { // from class: com.android.deskclock.widget.TimePickerForTimer.3
            @Override // com.android.deskclock.widget.NumberPicker.OnScrollListener
            public void onScrollStateChange(NumberPicker numberPicker2, int i2) {
                TimePickerForTimer.this.onTimeScrollChanged(i2);
            }
        });
        numberPicker.setPadding(0, this.mPaddingTop, 0, this.mPaddingBottom);
        NumberPicker numberPicker2 = (NumberPicker) findViewById(R.id.minute);
        this.mMinuteSpinner = numberPicker2;
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(59);
        numberPicker2.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: com.android.deskclock.widget.TimePickerForTimer.4
            @Override // com.android.deskclock.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker3, int i2, int i3) {
                TimePickerForTimer.this.onTimeChanged();
            }
        });
        numberPicker2.setOnScrollListener(new NumberPicker.OnScrollListener() { // from class: com.android.deskclock.widget.TimePickerForTimer.5
            @Override // com.android.deskclock.widget.NumberPicker.OnScrollListener
            public void onScrollStateChange(NumberPicker numberPicker3, int i2) {
                TimePickerForTimer.this.onTimeScrollChanged(i2);
            }
        });
        numberPicker2.setPadding(0, this.mPaddingTop, 0, this.mPaddingBottom);
        NumberPicker numberPicker3 = (NumberPicker) findViewById(R.id.second);
        this.mSecondSpinner = numberPicker3;
        numberPicker3.setMinValue(0);
        numberPicker3.setMaxValue(59);
        numberPicker3.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        numberPicker3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: com.android.deskclock.widget.TimePickerForTimer.6
            @Override // com.android.deskclock.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker4, int i2, int i3) {
                TimePickerForTimer.this.onTimeChanged();
            }
        });
        numberPicker3.setOnScrollListener(new NumberPicker.OnScrollListener() { // from class: com.android.deskclock.widget.TimePickerForTimer.7
            @Override // com.android.deskclock.widget.NumberPicker.OnScrollListener
            public void onScrollStateChange(NumberPicker numberPicker4, int i2) {
                TimePickerForTimer.this.onTimeScrollChanged(i2);
            }
        });
        numberPicker3.setPadding(0, this.mPaddingTop, 0, this.mPaddingBottom);
        if (MiuiSdk.isSupportMiUiFont()) {
            numberPicker.setTypeface(MiuiFont.MI_TYPE_MONO_DEMIBOLD, MiuiFont.MI_TYPE_MONO_DEMIBOLD);
            numberPicker2.setTypeface(MiuiFont.MI_TYPE_MONO_DEMIBOLD, MiuiFont.MI_TYPE_MONO_DEMIBOLD);
            numberPicker3.setTypeface(MiuiFont.MI_TYPE_MONO_DEMIBOLD, MiuiFont.MI_TYPE_MONO_DEMIBOLD);
        }
        if (Util.isTinyScreen(context)) {
            ImageView imageView = (ImageView) findViewById(R.id.number_picker_split_line1);
            ImageView imageView2 = (ImageView) findViewById(R.id.number_picker_split_line2);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) imageView2.getLayoutParams();
            layoutParams.width = (int) context.getResources().getDimension(R.dimen.number_picker_split_line_width_ting);
            imageView.setLayoutParams(layoutParams);
            layoutParams2.width = (int) context.getResources().getDimension(R.dimen.number_picker_split_line_width_ting);
            imageView2.setLayoutParams(layoutParams2);
        }
        setOnTimeChangedListener(NO_OP_CHANGE_LISTENER);
        setCurrentHour(0);
        setCurrentMinute(5);
        setCurrentSecond(0);
        if (!isEnabled()) {
            setEnabled(false);
        }
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
    }

    public void setSelectorIndicesCount(int i) {
        this.mHourSpinner.setSelectorIndicesCount(i);
        this.mMinuteSpinner.setSelectorIndicesCount(i);
        this.mSecondSpinner.setSelectorIndicesCount(i);
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        if (this.mIsEnabled == z) {
            return;
        }
        super.setEnabled(z);
        this.mMinuteSpinner.setEnabled(z);
        this.mHourSpinner.setEnabled(z);
        this.mSecondSpinner.setEnabled(z);
        this.mIsEnabled = z;
    }

    @Override // android.view.View
    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        setCurrentLocale(configuration.locale);
    }

    private void setCurrentLocale(Locale locale) {
        if (locale.equals(this.mCurrentLocale)) {
            return;
        }
        this.mCurrentLocale = locale;
        if (this.mTempCalendar == null) {
            this.mTempCalendar = new Calendar();
        }
    }

    private static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.android.deskclock.widget.TimePickerForTimer.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        private final int mHour;
        private final int mMinute;
        private final int mSecond;

        private SavedState(Parcelable parcelable, int i, int i2, int i3) {
            super(parcelable);
            this.mHour = i;
            this.mMinute = i2;
            this.mSecond = i3;
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.mHour = parcel.readInt();
            this.mMinute = parcel.readInt();
            this.mSecond = parcel.readInt();
        }

        public int getHour() {
            return this.mHour;
        }

        public int getMinute() {
            return this.mMinute;
        }

        public int getSecond() {
            return this.mSecond;
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mHour);
            parcel.writeInt(this.mMinute);
            parcel.writeInt(this.mSecond);
        }
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), getCurrentHour().intValue(), getCurrentMinute().intValue(), getCurrentSecond().intValue());
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        setCurrentHour(Integer.valueOf(savedState.getHour()));
        setCurrentMinute(Integer.valueOf(savedState.getMinute()));
        setCurrentSecond(Integer.valueOf(savedState.getSecond()));
    }

    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        this.mOnTimeChangedListener = onTimeChangedListener;
    }

    public void setOnTimerScrollListener(OnTimerScrollListener onTimerScrollListener) {
        this.mOnTimerScrollListener = onTimerScrollListener;
    }

    public Integer getCurrentHour() {
        return Integer.valueOf(this.mHourSpinner.getValue());
    }

    public void setCurrentHour(Integer num) {
        if (num == null || num.equals(getCurrentHour())) {
            Log.i("TimePickerForTimer setCurrentHour(), the currentHour is " + num);
        } else {
            this.mHourSpinner.setValue(num.intValue());
            onTimeChanged();
        }
    }

    public void setCurrentHourWithAnim(Integer num) {
        if (num == null || num.equals(getCurrentHour())) {
            Log.i("TimePickerForTimer setCurrentHour(), the currentHour is " + num);
        } else {
            this.mHourSpinner.setValueWithAnim(num.intValue(), "hour");
            onTimeChanged();
        }
    }

    public Integer getCurrentMinute() {
        return Integer.valueOf(this.mMinuteSpinner.getValue());
    }

    public void setCurrentMinute(Integer num) {
        if (num.equals(getCurrentMinute())) {
            Log.i("TimePickerForTimer setCurrentMinute(), the currentMinute is " + num);
        } else {
            this.mMinuteSpinner.setValue(num.intValue());
            onTimeChanged();
        }
    }

    public void setCurrentMinuteWithAnim(Integer num) {
        if (num.equals(getCurrentMinute())) {
            Log.i("TimePickerForTimer setCurrentMinute(), the currentMinute is " + num);
        } else {
            this.mMinuteSpinner.setValueWithAnim(num.intValue(), "minute");
            onTimeChanged();
        }
    }

    public Integer getCurrentSecond() {
        return Integer.valueOf(this.mSecondSpinner.getValue());
    }

    public void setCurrentSecond(Integer num) {
        if (num.equals(getCurrentSecond())) {
            Log.i("TimePickerForTimer setCurrentSecond(), the currentSecond is " + num);
        } else {
            this.mSecondSpinner.setValue(num.intValue());
            onTimeChanged();
        }
    }

    public void setCurrentSecondWithAnim(Integer num) {
        if (num.equals(getCurrentSecond())) {
            Log.i("TimePickerForTimer setCurrentSecond(), the currentSecond is " + num);
        } else {
            this.mSecondSpinner.setValueWithAnim(num.intValue(), "second");
            onTimeChanged();
        }
    }

    @Override // android.view.View
    public int getBaseline() {
        return this.mHourSpinner.getBaseline();
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        onPopulateAccessibilityEvent(accessibilityEvent);
        return true;
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onPopulateAccessibilityEvent(accessibilityEvent);
        this.mTempCalendar.set(18, getCurrentHour().intValue());
        this.mTempCalendar.set(20, getCurrentMinute().intValue());
        this.mTempCalendar.set(21, getCurrentSecond().intValue());
        accessibilityEvent.getText().add(TimeUtil.getFormatTime(this.mTempCalendar.getTimeInMillis(), TimeUtil.FORMAT_DAY_EN));
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(TimePickerForTimer.class.getName());
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(TimePickerForTimer.class.getName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeChanged() {
        sendAccessibilityEvent(4);
        OnTimeChangedListener onTimeChangedListener = this.mOnTimeChangedListener;
        if (onTimeChangedListener != null) {
            onTimeChangedListener.onTimeChanged(this, getCurrentHour().intValue(), getCurrentMinute().intValue(), getCurrentSecond().intValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeScrollChanged(int i) {
        OnTimerScrollListener onTimerScrollListener = this.mOnTimerScrollListener;
        if (onTimerScrollListener != null) {
            onTimerScrollListener.onScrollStateChange(i);
        }
    }

    public void stopScroll() {
        this.mHourSpinner.stopScroll();
        this.mMinuteSpinner.stopScroll();
        this.mSecondSpinner.stopScroll();
    }

    public void setTextSize(int i, int i2) {
        this.mHourSpinner.setTextSize(i, i2);
        this.mMinuteSpinner.setTextSize(i, i2);
        this.mSecondSpinner.setTextSize(i, i2);
        invalidate();
        requestLayout();
    }
}
