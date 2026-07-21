package miuix.pickerwidget.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.util.Locale;
import miuix.pickerwidget.R;
import miuix.pickerwidget.date.Calendar;
import miuix.pickerwidget.date.CalendarFormatSymbols;
import miuix.pickerwidget.date.DateUtils;
import miuix.pickerwidget.internal.widget.ProperPaddingViewGroup;

/* JADX INFO: loaded from: classes3.dex */
public class TimePicker extends FrameLayout {
    private static final boolean DEFAULT_ENABLED_STATE = true;
    private static final int HOURS_IN_HALF_DAY = 12;
    private static final OnTimeChangedListener NO_OP_CHANGE_LISTENER = new OnTimeChangedListener() { // from class: miuix.pickerwidget.widget.TimePicker.1
        @Override // miuix.pickerwidget.widget.TimePicker.OnTimeChangedListener
        public void onTimeChanged(TimePicker timePicker, int i, int i2) {
        }
    };
    private final Button mAmPmButton;
    private final NumberPicker mAmPmSpinner;
    private Locale mCurrentLocale;
    private final NumberPicker mHourSpinner;
    private boolean mIs24HourView;
    private boolean mIsAm;
    private boolean mIsEnabled;
    private final NumberPicker mMinuteSpinner;
    private OnTimeChangedListener mOnTimeChangedListener;
    private ProperPaddingViewGroup mProperPaddingViewGroup;
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
        setCurrentLocale(Locale.getDefault());
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.miuix_appcompat_time_picker, (ViewGroup) this, true);
        this.mProperPaddingViewGroup = (ProperPaddingViewGroup) findViewById(R.id.properPaddingViewGroup);
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.hour);
        this.mHourSpinner = numberPicker;
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: miuix.pickerwidget.widget.TimePicker.2
            @Override // miuix.pickerwidget.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker2, int i2, int i3) {
                if (!TimePicker.this.is24HourView() && ((i2 == 11 && i3 == 12) || (i2 == 12 && i3 == 11))) {
                    TimePicker timePicker = TimePicker.this;
                    timePicker.mIsAm = !timePicker.mIsAm;
                    TimePicker.this.updateAmPmControl();
                }
                TimePicker.this.onTimeChanged();
            }
        });
        ((EditText) numberPicker.findViewById(R.id.number_picker_input)).setImeOptions(5);
        NumberPicker numberPicker2 = (NumberPicker) findViewById(R.id.minute);
        this.mMinuteSpinner = numberPicker2;
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(59);
        numberPicker2.setOnLongPressUpdateInterval(100L);
        numberPicker2.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: miuix.pickerwidget.widget.TimePicker.3
            @Override // miuix.pickerwidget.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker3, int i2, int i3) {
                TimePicker.this.onTimeChanged();
            }
        });
        ((EditText) numberPicker2.findViewById(R.id.number_picker_input)).setImeOptions(5);
        View viewFindViewById = findViewById(R.id.amPm);
        if (viewFindViewById instanceof Button) {
            this.mAmPmSpinner = null;
            Button button = (Button) viewFindViewById;
            this.mAmPmButton = button;
            button.setOnClickListener(new View.OnClickListener() { // from class: miuix.pickerwidget.widget.TimePicker.4
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    view.requestFocus();
                    TimePicker timePicker = TimePicker.this;
                    timePicker.mIsAm = !timePicker.mIsAm;
                    TimePicker.this.updateAmPmControl();
                    TimePicker.this.onTimeChanged();
                }
            });
        } else {
            this.mAmPmButton = null;
            NumberPicker numberPicker3 = (NumberPicker) viewFindViewById;
            this.mAmPmSpinner = numberPicker3;
            numberPicker3.setMinValue(0);
            numberPicker3.setMaxValue(1);
            numberPicker3.setDisplayedValues(CalendarFormatSymbols.getOrCreate(getContext()).getAmPms());
            numberPicker3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: miuix.pickerwidget.widget.TimePicker.5
                @Override // miuix.pickerwidget.widget.NumberPicker.OnValueChangeListener
                public void onValueChange(NumberPicker numberPicker4, int i2, int i3) {
                    numberPicker4.requestFocus();
                    TimePicker timePicker = TimePicker.this;
                    timePicker.mIsAm = !timePicker.mIsAm;
                    TimePicker.this.updateAmPmControl();
                    TimePicker.this.onTimeChanged();
                }
            });
            ((EditText) numberPicker3.findViewById(R.id.number_picker_input)).setImeOptions(6);
        }
        if (isAmPmAtStart()) {
            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.timePickerLayout);
            viewGroup.removeView(viewFindViewById);
            viewGroup.addView(viewFindViewById, 0);
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

    private boolean isAmPmAtStart() {
        return getContext().getString(R.string.fmt_time_12hour_pm).startsWith("a");
    }

    public void setFixedContentHorizontalPadding(int i, int i2) {
        this.mProperPaddingViewGroup.setFixedContentHorizontalPadding(i, i2);
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        if (this.mIsEnabled == z) {
            return;
        }
        super.setEnabled(z);
        this.mMinuteSpinner.setEnabled(z);
        this.mHourSpinner.setEnabled(z);
        NumberPicker numberPicker = this.mAmPmSpinner;
        if (numberPicker != null) {
            numberPicker.setEnabled(z);
        } else {
            this.mAmPmButton.setEnabled(z);
        }
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
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: miuix.pickerwidget.widget.TimePicker.SavedState.1
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

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

    public void setTextColorHighlight(int i) {
        this.mHourSpinner.setTextColorHighlight(i);
        this.mMinuteSpinner.setTextColorHighlight(i);
        this.mAmPmSpinner.setTextColorHighlight(i);
    }

    public void setLabelTextColor(int i) {
        this.mHourSpinner.setLabelTextColor(i);
        this.mMinuteSpinner.setLabelTextColor(i);
        this.mAmPmSpinner.setLabelTextColor(i);
    }

    public void setCurrentHour(Integer num) {
        if (num == null || num.equals(getCurrentHour())) {
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

    public void set24HourView(Boolean bool) {
        if (this.mIs24HourView == bool.booleanValue()) {
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
            return;
        }
        this.mMinuteSpinner.setValue(num.intValue());
        onTimeChanged();
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
            this.mHourSpinner.setFormatter(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAmPmControl() {
        if (is24HourView()) {
            NumberPicker numberPicker = this.mAmPmSpinner;
            if (numberPicker != null) {
                numberPicker.setVisibility(8);
            } else {
                this.mAmPmButton.setVisibility(8);
            }
        } else {
            int i = !this.mIsAm ? 1 : 0;
            NumberPicker numberPicker2 = this.mAmPmSpinner;
            if (numberPicker2 != null) {
                numberPicker2.setValue(i);
                this.mAmPmSpinner.setVisibility(0);
            } else {
                this.mAmPmButton.setText(CalendarFormatSymbols.getOrCreate(getContext()).getAmPms()[i]);
                this.mAmPmButton.setVisibility(0);
            }
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
}
