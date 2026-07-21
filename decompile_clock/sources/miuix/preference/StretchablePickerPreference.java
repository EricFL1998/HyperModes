package miuix.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.preference.PreferenceViewHolder;
import com.android.deskclock.R2;
import miuix.pickerwidget.date.Calendar;
import miuix.pickerwidget.date.DateUtils;
import miuix.pickerwidget.widget.DateTimePicker;
import miuix.slidingwidget.widget.SlidingButton;

/* JADX INFO: loaded from: classes3.dex */
public class StretchablePickerPreference extends StretchableWidgetPreference {
    private Calendar mCalendar;
    private Context mContext;
    private boolean mIsLunar;
    private CharSequence mLunar;
    private DateTimePicker.LunarFormatter mLunarFormatter;
    private int mMinuteInterval;
    private OnTimeChangeListener mOnTimeChangeListener;
    private boolean mShowLunar;
    private long mTime;

    public interface OnTimeChangeListener {
        long onDateTimeChanged(long j);
    }

    public StretchablePickerPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Calendar calendar = new Calendar();
        this.mCalendar = calendar;
        this.mTime = calendar.getTimeInMillis();
        this.mContext = context;
        this.mLunarFormatter = new DateTimePicker.LunarFormatter(context);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.StretchablePickerPreference, i, 0);
        this.mShowLunar = typedArrayObtainStyledAttributes.getBoolean(R.styleable.StretchablePickerPreference_show_lunar, false);
        typedArrayObtainStyledAttributes.recycle();
    }

    public StretchablePickerPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.stretchablePickerPreferenceStyle);
    }

    public StretchablePickerPreference(Context context) {
        this(context, null);
    }

    @Override // miuix.preference.StretchableWidgetPreference, miuix.preference.BasePreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        boolean z;
        View view = preferenceViewHolder.itemView;
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.lunar_layout);
        final DateTimePicker dateTimePicker = (DateTimePicker) view.findViewById(R.id.datetime_picker);
        final SlidingButton slidingButton = (SlidingButton) view.findViewById(R.id.lunar_button);
        final TextView textView = (TextView) view.findViewById(R.id.lunar_text);
        if (!this.mShowLunar) {
            frameLayout.setVisibility(8);
        } else if (textView != null) {
            CharSequence lunarText = getLunarText();
            if (TextUtils.isEmpty(lunarText)) {
                z = false;
            } else {
                textView.setText(lunarText);
                z = true;
            }
            frameLayout.setFocusable(z);
            slidingButton.setFocusable(!z);
            if (z) {
                frameLayout.setOnClickListener(new View.OnClickListener() { // from class: miuix.preference.StretchablePickerPreference$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        this.f$0.m1929x58c21229(slidingButton, dateTimePicker, view2);
                    }
                });
                if (isAccessibilityEnabled()) {
                    textView.setImportantForAccessibility(2);
                    slidingButton.setImportantForAccessibility(2);
                    ViewCompat.setAccessibilityDelegate(frameLayout, new AccessibilityDelegateCompat() { // from class: miuix.preference.StretchablePickerPreference.1
                        @Override // androidx.core.view.AccessibilityDelegateCompat
                        public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                            super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfoCompat);
                            accessibilityNodeInfoCompat.setCheckable(true);
                            accessibilityNodeInfoCompat.setClassName(Switch.class.getName());
                            accessibilityNodeInfoCompat.setChecked(slidingButton.isChecked());
                            accessibilityNodeInfoCompat.setContentDescription(textView.getText());
                        }
                    });
                }
            } else {
                frameLayout.setOnClickListener(null);
            }
        }
        dateTimePicker.setMinuteInterval(getMinuteInterval());
        this.mTime = dateTimePicker.getTimeInMillis();
        super.onBindViewHolder(preferenceViewHolder);
        changeTimeState(slidingButton, dateTimePicker);
        showTime(this.mIsLunar, dateTimePicker.getTimeInMillis());
        updateTime(dateTimePicker);
    }

    /* JADX INFO: renamed from: lambda$onBindViewHolder$0$miuix-preference-StretchablePickerPreference, reason: not valid java name */
    /* synthetic */ void m1929x58c21229(SlidingButton slidingButton, DateTimePicker dateTimePicker, View view) {
        boolean z = !slidingButton.isChecked();
        slidingButton.setChecked(z);
        setTimePickerLunarMode(dateTimePicker, z);
    }

    public void setMinuteInterval(int i) {
        if (i != this.mMinuteInterval) {
            this.mMinuteInterval = i;
            notifyChanged();
        }
    }

    private int getMinuteInterval() {
        return this.mMinuteInterval;
    }

    public void setLunarText(String str) {
        if (TextUtils.equals(str, this.mLunar)) {
            return;
        }
        this.mLunar = str;
        notifyChanged();
    }

    private CharSequence getLunarText() {
        return this.mLunar;
    }

    private void updateTime(DateTimePicker dateTimePicker) {
        dateTimePicker.setOnTimeChangedListener(new DateTimePicker.OnDateTimeChangedListener() { // from class: miuix.preference.StretchablePickerPreference.2
            @Override // miuix.pickerwidget.widget.DateTimePicker.OnDateTimeChangedListener
            public void onDateTimeChanged(DateTimePicker dateTimePicker2, long j) {
                StretchablePickerPreference.this.mCalendar.setTimeInMillis(j);
                StretchablePickerPreference stretchablePickerPreference = StretchablePickerPreference.this;
                stretchablePickerPreference.showTime(stretchablePickerPreference.mIsLunar, j);
                StretchablePickerPreference.this.mTime = j;
                if (StretchablePickerPreference.this.mOnTimeChangeListener != null) {
                    StretchablePickerPreference.this.mOnTimeChangeListener.onDateTimeChanged(StretchablePickerPreference.this.mTime);
                }
                StretchablePickerPreference.this.notifyChanged();
            }
        });
    }

    public void setSlidingListener(OnTimeChangeListener onTimeChangeListener) {
        this.mOnTimeChangeListener = onTimeChangeListener;
    }

    private void showSolarTime(long j) {
        setDetailMsgText(formatSolarTime(j));
    }

    private String formatSolarTime(long j) {
        return DateUtils.formatDateTime(this.mContext, j, R2.attr.editTextSearchStyle);
    }

    private void changeTimeState(SlidingButton slidingButton, final DateTimePicker dateTimePicker) {
        slidingButton.setOnPerformCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: miuix.preference.StretchablePickerPreference.3
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                StretchablePickerPreference.this.setTimePickerLunarMode(dateTimePicker, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTimePickerLunarMode(DateTimePicker dateTimePicker, boolean z) {
        dateTimePicker.setLunarMode(z);
        showTime(z, dateTimePicker.getTimeInMillis());
        this.mIsLunar = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showTime(boolean z, long j) {
        if (z) {
            showLunarTime(j);
        } else {
            showSolarTime(j);
        }
    }

    public void showLunarTime(long j) {
        setDetailMsgText(formatLunarTime(j, this.mContext));
    }

    private String formatLunarTime(long j, Context context) {
        return this.mLunarFormatter.formatDay(this.mCalendar.get(1), this.mCalendar.get(5), this.mCalendar.get(9)) + " " + DateUtils.formatDateTime(context, j, 12);
    }

    public long getTime() {
        return this.mTime;
    }
}
