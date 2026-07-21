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
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import java.util.Locale;
import miuix.pickerwidget.date.Calendar;
import miuix.pickerwidget.date.CalendarFormatSymbols;
import miuix.pickerwidget.date.DateUtils;

/* JADX INFO: loaded from: classes.dex */
public class TimePicker extends FrameLayout {
    private static final boolean DEFAULT_ENABLED_STATE = true;
    private static final int HOURS_IN_HALF_DAY = 12;
    private static final OnTimeChangedListener NO_OP_CHANGE_LISTENER = new OnTimeChangedListener() { // from class: com.android.deskclock.widget.TimePicker.1
        @Override // com.android.deskclock.widget.TimePicker.OnTimeChangedListener
        public void onTimeChanged(TimePicker timePicker, int i, int i2) {
        }
    };
    private final NumberPicker mAmPmSpinner;
    private Locale mCurrentLocale;
    private final NumberPicker mHourSpinner;
    private boolean mIs24HourView;
    private boolean mIsAm;
    private boolean mIsEnabled;
    private final NumberPicker mMinuteSpinner;
    private OnTimeChangedListener mOnTimeChangedListener;
    private Calendar mTempCalendar;

    public interface OnTimeChangedListener {
        void onTimeChanged(TimePicker timePicker, int i, int i2);
    }

    public TimePicker(Context context) {
        this(context, null);
    }

    public TimePicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TimePicker(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsEnabled = true;
        TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.SetAlarmNumberPicker, i, 0);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(0, R.layout.time_picker);
        typedArrayObtainStyledAttributes.recycle();
        setCurrentLocale(Locale.getDefault());
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(resourceId, (ViewGroup) this, true);
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.amPm);
        this.mAmPmSpinner = numberPicker;
        numberPicker.setHapticMesh(268435461);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(1);
        numberPicker.setDisplayedValues(CalendarFormatSymbols.getOrCreate(context).getAmPms());
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: com.android.deskclock.widget.TimePicker.2
            @Override // com.android.deskclock.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker2, int i2, int i3) {
                numberPicker2.requestFocus();
                TimePicker timePicker = TimePicker.this;
                timePicker.mIsAm = !timePicker.mIsAm;
                TimePicker.this.updateAmPmControl();
                TimePicker.this.onTimeChanged();
            }
        });
        numberPicker.setTypeface(MiuiFont.MI_PRO_REGULAR, MiuiFont.MI_TYPE_MONO_SEMIBOLD);
        if (numberPicker != null && Util.isTinyScreen(context)) {
            numberPicker.setTextSize((int) context.getResources().getDimension(R.dimen.set_alarm_numberpicker_am_pm_highlight), (int) context.getResources().getDimension(R.dimen.set_alarm_numberpicker_am_pm_hint));
        }
        NumberPicker numberPicker2 = (NumberPicker) findViewById(R.id.hour);
        this.mHourSpinner = numberPicker2;
        numberPicker2.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        numberPicker2.setTypeface(MiuiFont.MI_PRO_REGULAR, MiuiFont.MI_TYPE_MONO_SEMIBOLD);
        if (numberPicker2 != null && Util.isTinyScreen(context)) {
            numberPicker2.setTextSize((int) context.getResources().getDimension(R.dimen.set_alarm_numberpicker_hour_highlight), (int) context.getResources().getDimension(R.dimen.set_alarm_numberpicker_hour_hint));
        }
        numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: com.android.deskclock.widget.TimePicker.3
            @Override // com.android.deskclock.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker3, int i2, int i3) {
                if (!TimePicker.this.is24HourView() && ((i2 == 11 && i3 == 12) || (i2 == 12 && i3 == 11))) {
                    TimePicker timePicker = TimePicker.this;
                    timePicker.mIsAm = !timePicker.mIsAm;
                    TimePicker.this.updateAmPmControl();
                }
                TimePicker.this.onTimeChanged();
            }
        });
        NumberPicker numberPicker3 = (NumberPicker) findViewById(R.id.minute);
        this.mMinuteSpinner = numberPicker3;
        numberPicker3.setMinValue(0);
        numberPicker3.setMaxValue(59);
        numberPicker3.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        if (numberPicker3 != null && Util.isTinyScreen(context)) {
            numberPicker3.setTextSize((int) context.getResources().getDimension(R.dimen.set_alarm_numberpicker_hour_highlight), (int) context.getResources().getDimension(R.dimen.set_alarm_numberpicker_hour_hint));
        }
        numberPicker3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: com.android.deskclock.widget.TimePicker.4
            @Override // com.android.deskclock.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker4, int i2, int i3) {
                TimePicker.this.onTimeChanged();
            }
        });
        if (MiuiSdk.isSupportMiUiFont()) {
            numberPicker3.setTypeface(MiuiFont.MI_TYPE_MONO_DEMIBOLD, MiuiFont.MI_TYPE_MONO_SEMIBOLD);
            numberPicker2.setTypeface(MiuiFont.MI_TYPE_MONO_DEMIBOLD, MiuiFont.MI_TYPE_MONO_SEMIBOLD);
            numberPicker.setTypeface(MiuiFont.MI_PRO_DEMIBOLD, MiuiFont.MI_PRO_DEMIBOLD);
        }
        updateHourControl();
        updateAmPmControl();
        setOnTimeChangedListener(NO_OP_CHANGE_LISTENER);
        setCurrentHour(Integer.valueOf(this.mTempCalendar.get(18)));
        setCurrentMinute(Integer.valueOf(this.mTempCalendar.get(20)));
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
        this.mAmPmSpinner.setSelectorIndicesCount(i);
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        if (this.mIsEnabled == z) {
            return;
        }
        super.setEnabled(z);
        this.mMinuteSpinner.setEnabled(z);
        this.mHourSpinner.setEnabled(z);
        this.mAmPmSpinner.setEnabled(z);
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
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.android.deskclock.widget.TimePicker.SavedState.1
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

        private SavedState(Parcelable parcelable, int i, int i2) {
            super(parcelable);
            this.mHour = i;
            this.mMinute = i2;
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.mHour = parcel.readInt();
            this.mMinute = parcel.readInt();
        }

        public int getHour() {
            return this.mHour;
        }

        public int getMinute() {
            return this.mMinute;
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mHour);
            parcel.writeInt(this.mMinute);
        }
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), getCurrentHour().intValue(), getCurrentMinute().intValue());
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        setCurrentHour(Integer.valueOf(savedState.getHour()));
        setCurrentMinute(Integer.valueOf(savedState.getMinute()));
    }

    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        this.mOnTimeChangedListener = onTimeChangedListener;
    }

    public Integer getCurrentHour() {
        int value = this.mHourSpinner.getValue();
        if (is24HourView()) {
            return Integer.valueOf(value);
        }
        if (this.mIsAm) {
            return Integer.valueOf(value % 12);
        }
        return Integer.valueOf((value % 12) + 12);
    }

    public void setCurrentHour(Integer num) {
        if (num == null || num.equals(getCurrentHour())) {
            Log.i("TimePicker setCurrentHour(), the currentHour is " + num);
            return;
        }
        if (!is24HourView()) {
            if (num.intValue() >= 12) {
                this.mIsAm = false;
                if (num.intValue() > 12) {
                    num = Integer.valueOf(num.intValue() - 12);
                }
            } else {
                this.mIsAm = true;
                if (num.intValue() == 0) {
                    num = 12;
                }
            }
            updateAmPmControl();
        }
        this.mHourSpinner.setValue(num.intValue());
        onTimeChanged();
    }

    public void setIs24HourView(Boolean bool) {
        if (this.mIs24HourView == bool.booleanValue()) {
            Log.i("TimePicker setIs24HourView(), the is24HourView is " + bool);
            return;
        }
        this.mIs24HourView = bool.booleanValue();
        int iIntValue = getCurrentHour().intValue();
        updateHourControl();
        setCurrentHour(Integer.valueOf(iIntValue));
        updateAmPmControl();
    }

    public boolean is24HourView() {
        return this.mIs24HourView;
    }

    public Integer getCurrentMinute() {
        return Integer.valueOf(this.mMinuteSpinner.getValue());
    }

    public void setCurrentMinute(Integer num) {
        if (num.equals(getCurrentMinute())) {
            Log.i("TimePicker setCurrentMinute(), the currentMinute is " + num);
        } else {
            this.mMinuteSpinner.setValue(num.intValue());
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
        int i = this.mIs24HourView ? 44 : 28;
        this.mTempCalendar.set(18, getCurrentHour().intValue());
        this.mTempCalendar.set(20, getCurrentMinute().intValue());
        accessibilityEvent.getText().add(DateUtils.formatDateTime(getContext(), this.mTempCalendar.getTimeInMillis(), i));
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(TimePicker.class.getName());
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(TimePicker.class.getName());
    }

    private void updateHourControl() {
        if (is24HourView()) {
            this.mHourSpinner.setMinValue(0);
            this.mHourSpinner.setMaxValue(23);
            this.mHourSpinner.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        } else {
            this.mHourSpinner.setMinValue(1);
            this.mHourSpinner.setMaxValue(12);
            this.mHourSpinner.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAmPmControl() {
        if (is24HourView()) {
            this.mAmPmSpinner.setVisibility(8);
        } else {
            this.mAmPmSpinner.setValue(!this.mIsAm ? 1 : 0);
            this.mAmPmSpinner.setVisibility(0);
        }
        sendAccessibilityEvent(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeChanged() {
        sendAccessibilityEvent(4);
        OnTimeChangedListener onTimeChangedListener = this.mOnTimeChangedListener;
        if (onTimeChangedListener != null) {
            onTimeChangedListener.onTimeChanged(this, getCurrentHour().intValue(), getCurrentMinute().intValue());
        }
    }

    private void trySetContentDescription(View view, int i, int i2) {
        View viewFindViewById = view.findViewById(i);
        if (viewFindViewById != null) {
            viewFindViewById.setContentDescription(getContext().getString(i2));
        }
    }

    public int getHourSlideTimes() {
        NumberPicker numberPicker = this.mHourSpinner;
        if (numberPicker != null) {
            return numberPicker.getSlideTimes();
        }
        return 0;
    }

    public int getMinSlideTimes() {
        NumberPicker numberPicker = this.mMinuteSpinner;
        if (numberPicker != null) {
            return numberPicker.getSlideTimes();
        }
        return 0;
    }

    public void stopScroll() {
        NumberPicker numberPicker = this.mAmPmSpinner;
        if (numberPicker != null) {
            numberPicker.stopScroll();
        }
        this.mHourSpinner.stopScroll();
        this.mMinuteSpinner.stopScroll();
    }
}
