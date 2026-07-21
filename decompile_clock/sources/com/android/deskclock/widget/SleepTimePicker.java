package com.android.deskclock.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
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
import java.util.Locale;
import miuix.pickerwidget.date.Calendar;
import miuix.pickerwidget.date.CalendarFormatSymbols;
import miuix.pickerwidget.date.DateUtils;

/* JADX INFO: loaded from: classes.dex */
public class SleepTimePicker extends FrameLayout {
    private static final boolean DEFAULT_ENABLED_STATE = true;
    private static final int HOURS_IN_HALF_DAY = 12;
    private static final int MAX_VALUE = 11;
    private static final int MIN_VALUE = 0;
    private static final OnTimeChangedListener NO_OP_CHANGE_LISTENER = new OnTimeChangedListener() { // from class: com.android.deskclock.widget.SleepTimePicker.1
        @Override // com.android.deskclock.widget.SleepTimePicker.OnTimeChangedListener
        public void onTimeChanged(SleepTimePicker sleepTimePicker, int i, int i2) {
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
    private int mTextColorHighlight;
    private int mTextColorHint;

    public interface OnTimeChangedListener {
        void onTimeChanged(SleepTimePicker sleepTimePicker, int i, int i2);
    }

    public SleepTimePicker(Context context) {
        this(context, null);
    }

    public SleepTimePicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SleepTimePicker(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTextColorHighlight = -452984832;
        this.mTextColorHint = 201326592;
        this.mIsEnabled = true;
        TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.SetAlarmNumberPicker, i, 0);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(0, R.layout.time_picker);
        this.mTextColorHighlight = typedArrayObtainStyledAttributes.getColor(4, getResources().getColor(R.color.numberpicker_highlight_text));
        this.mTextColorHint = typedArrayObtainStyledAttributes.getColor(5, getResources().getColor(R.color.numberpicker_hint_text));
        typedArrayObtainStyledAttributes.recycle();
        setCurrentLocale(Locale.getDefault());
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(resourceId, (ViewGroup) this, true);
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.hour);
        this.mHourSpinner = numberPicker;
        numberPicker.setTypeface(MiuiFont.MI_PRO_REGULAR, MiuiFont.MI_PRO_MEDIUM);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: com.android.deskclock.widget.SleepTimePicker.2
            @Override // com.android.deskclock.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker2, int i2, int i3) {
                if (!SleepTimePicker.this.is24HourView() && ((i2 == 11 && i3 == 12) || (i2 == 12 && i3 == 11))) {
                    SleepTimePicker sleepTimePicker = SleepTimePicker.this;
                    sleepTimePicker.mIsAm = !sleepTimePicker.mIsAm;
                    SleepTimePicker.this.updateAmPmControl();
                }
                SleepTimePicker.this.onTimeChanged();
            }
        });
        numberPicker.setColor(this.mTextColorHighlight, this.mTextColorHint);
        NumberPicker numberPicker2 = (NumberPicker) findViewById(R.id.minute);
        this.mMinuteSpinner = numberPicker2;
        numberPicker2.setTypeface(MiuiFont.MI_PRO_REGULAR, MiuiFont.MI_PRO_MEDIUM);
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(11);
        numberPicker2.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        numberPicker2.setDisplayedValues(new String[]{String.format(getResources().getString(R.string.minute_spinner_zero), 0, 0), String.format(getResources().getString(R.string.minute_spinner_five), 0, 5), String.format(getResources().getString(R.string.minute_spinner_ten), 10), String.format(getResources().getString(R.string.minute_spinner_fifteen), 15), String.format(getResources().getString(R.string.minute_spinner_twenty), 20), String.format(getResources().getString(R.string.minute_spinner_twenty_five), 25), String.format(getResources().getString(R.string.minute_spinner_thirty), 30), String.format(getResources().getString(R.string.minute_spinner_thirty_five), 35), String.format(getResources().getString(R.string.minute_spinner_forty), 40), String.format(getResources().getString(R.string.minute_spinner_forty_five), 45), String.format(getResources().getString(R.string.minute_spinner_fifty), 50), String.format(getResources().getString(R.string.minute_spinner_fifty_five), 55)});
        numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: com.android.deskclock.widget.SleepTimePicker.3
            @Override // com.android.deskclock.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker3, int i2, int i3) {
                SleepTimePicker.this.onTimeChanged();
            }
        });
        numberPicker2.setColor(this.mTextColorHighlight, this.mTextColorHint);
        NumberPicker numberPicker3 = (NumberPicker) findViewById(R.id.amPm);
        this.mAmPmSpinner = numberPicker3;
        numberPicker3.setHapticMesh(268435461);
        numberPicker3.setMinValue(0);
        numberPicker3.setMaxValue(1);
        numberPicker3.setDisplayedValues(CalendarFormatSymbols.getOrCreate(context).getAmPms());
        numberPicker3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: com.android.deskclock.widget.SleepTimePicker.4
            @Override // com.android.deskclock.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker4, int i2, int i3) {
                numberPicker4.requestFocus();
                SleepTimePicker sleepTimePicker = SleepTimePicker.this;
                sleepTimePicker.mIsAm = !sleepTimePicker.mIsAm;
                SleepTimePicker.this.updateAmPmControl();
                SleepTimePicker.this.onTimeChanged();
            }
        });
        numberPicker3.setTypeface(Typeface.DEFAULT, Typeface.DEFAULT_BOLD);
        if (MiuiSdk.isSupportFontAnim()) {
            numberPicker.setTypeface(MiuiFont.MI_TYPE_MONO_REGULAR, MiuiFont.MI_TYPE_MONO_DEMIBOLD);
            numberPicker2.setTypeface(MiuiFont.MI_TYPE_MONO_REGULAR, MiuiFont.MI_TYPE_MONO_DEMIBOLD);
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
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.android.deskclock.widget.SleepTimePicker.SavedState.1
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
        setCurrentMinute(Integer.valueOf(savedState.getMinute() / 5));
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
            Log.i("TimePicker setCurrentMinute(), the currentMinute is " + (num.intValue() * 5));
        } else {
            if (num.intValue() < 0 || num.intValue() > 11) {
                return;
            }
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
        this.mTempCalendar.set(20, getCurrentMinute().intValue() * 5);
        accessibilityEvent.getText().add(DateUtils.formatDateTime(getContext(), this.mTempCalendar.getTimeInMillis(), i));
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(SleepTimePicker.class.getName());
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(SleepTimePicker.class.getName());
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

    public void stopScroll() {
        NumberPicker numberPicker = this.mAmPmSpinner;
        if (numberPicker != null) {
            numberPicker.stopScroll();
        }
        this.mHourSpinner.stopScroll();
        this.mMinuteSpinner.stopScroll();
    }
}
