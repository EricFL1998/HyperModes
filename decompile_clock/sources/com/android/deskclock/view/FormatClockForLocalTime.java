package com.android.deskclock.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.CityZoneHelper;
import com.android.deskclock.util.DateFormatUtil;
import com.android.deskclock.util.TimeUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.widget.LimitSizeTextView;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import miuix.flexible.view.HyperCellLayout;
import miuix.pickerwidget.date.DateUtils;

/* JADX INFO: loaded from: classes.dex */
public class FormatClockForLocalTime extends HyperCellLayout {
    private static final String M12 = "hh:mm";
    private static final String M24 = "kk:mm";
    private TextView mAmPmDisplay;
    private boolean mAttached;
    private Calendar mCalendar;
    private TextView mDateDisplay;
    private TextView mDateDisplayWithYear;
    private long mDisplayTimeMillis;
    private TimeZone mDisplayTimeZone;
    private boolean mIsExactToSecond;
    private TextView mLocalTimeDesc;
    private boolean mShowAmPm;
    private boolean mShowDate;
    private boolean mShowDetail;
    private boolean mShowTime;
    private boolean mShowWeek;
    private LimitSizeTextView mTimeDisplay;
    private LinearLayout mTimeTotal;
    private LinearLayout mTimeTotalDesc;
    private TextView mWeekDisplay;

    private void updateDateFormat() {
    }

    public FormatClockForLocalTime(Context context) {
        this(context, null);
    }

    public FormatClockForLocalTime(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.DigitalClockStyle);
    }

    public FormatClockForLocalTime(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsExactToSecond = false;
        this.mShowDate = false;
        this.mShowWeek = false;
        this.mShowTime = true;
        this.mShowAmPm = true;
        this.mShowDetail = false;
        TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.DigitalClock, i, 0);
        this.mIsExactToSecond = typedArrayObtainStyledAttributes.getBoolean(3, false);
        this.mShowDate = typedArrayObtainStyledAttributes.getBoolean(1, false);
        this.mShowWeek = typedArrayObtainStyledAttributes.getBoolean(5, false);
        this.mShowTime = typedArrayObtainStyledAttributes.getBoolean(4, true);
        this.mShowAmPm = typedArrayObtainStyledAttributes.getBoolean(0, true);
        this.mShowDetail = typedArrayObtainStyledAttributes.getBoolean(2, false);
        typedArrayObtainStyledAttributes.recycle();
        this.mCalendar = Calendar.getInstance();
        updateDateFormat();
    }

    public LimitSizeTextView getmTimeDisplay() {
        return this.mTimeDisplay;
    }

    @Override // miuix.flexible.view.HyperCellLayout, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTimeTotalDesc = (LinearLayout) findViewById(R.id.time_total_desc);
        if (this.mShowDate) {
            this.mDateDisplay = (TextView) findViewById(R.id.date_display);
            this.mDateDisplayWithYear = (TextView) findViewById(R.id.date_display_with_year);
        }
        if (this.mShowWeek) {
            this.mWeekDisplay = (TextView) findViewById(R.id.week_display);
        }
        if (this.mShowTime) {
            this.mTimeDisplay = (LimitSizeTextView) findViewById(R.id.time_display);
        }
        if (this.mShowAmPm) {
            this.mAmPmDisplay = (TextView) findViewById(R.id.am_pm);
        }
        if (this.mLocalTimeDesc == null) {
            this.mLocalTimeDesc = (TextView) findViewById(R.id.time_desc_local_only);
        }
        if (this.mTimeTotal == null) {
            this.mTimeTotal = (LinearLayout) findViewById(R.id.time_total);
        }
    }

    public void updateTime(long j) {
        this.mDisplayTimeMillis = j;
        this.mCalendar.setTimeInMillis(j);
        updateView();
    }

    public void updateTime(TimeZone timeZone, long j) {
        this.mDisplayTimeZone = timeZone;
        this.mDisplayTimeMillis = j;
        this.mCalendar.setTimeZone(timeZone);
        this.mCalendar.setTimeInMillis(this.mDisplayTimeMillis);
        updateView();
    }

    public Calendar getDisplayCalendar() {
        return this.mCalendar;
    }

    private void updateView() {
        TextView textView;
        String str;
        String[] weekdays = new DateFormatSymbols().getWeekdays();
        boolean z = AlarmHelper.get24HourMode();
        if (this.mShowDate && this.mDateDisplay != null) {
            String date = TimeUtil.formatDate(DeskClockApp.getAppContext().getString(R.string.worldcolock_timezone_date), this.mCalendar.getTime(), this.mDisplayTimeZone);
            if (!date.equals(this.mDateDisplay.getText())) {
                this.mDateDisplay.setText(date);
            }
        }
        if (this.mShowDate && this.mDateDisplayWithYear != null) {
            String date2 = TimeUtil.formatDate(DeskClockApp.getAppContext().getString(R.string.worldcolock_time_date_new), this.mCalendar.getTime(), this.mDisplayTimeZone);
            if (!date2.equals(this.mDateDisplayWithYear.getText())) {
                this.mDateDisplayWithYear.setText(date2);
            }
            this.mDateDisplayWithYear.setPadding(6, 0, 0, 0);
        }
        if (this.mShowWeek && this.mWeekDisplay != null) {
            try {
                str = weekdays[Util.getWeekDay(this.mCalendar)];
            } catch (Exception unused) {
                str = "";
            }
            if (!str.equals(this.mWeekDisplay.getText())) {
                this.mWeekDisplay.setText(str);
            }
        }
        if (this.mShowTime && this.mTimeDisplay != null) {
            String str2 = z ? "kk:mm" : "hh:mm";
            String string = getContext().getString(z ? R.string.clock_date_time_format_seconds_24 : R.string.clock_date_time_format_seconds_12);
            if (this.mIsExactToSecond) {
                str2 = string;
            }
            this.mTimeDisplay.setText(DateFormat.format(str2, this.mCalendar));
        }
        if (!this.mShowAmPm || (textView = this.mAmPmDisplay) == null) {
            return;
        }
        if (!z) {
            String language = Locale.getDefault().getLanguage();
            if (language.equals("zh") || language.equals("ja") || language.equals("ko") || language.equals("fa") || language.equals("ar")) {
                this.mAmPmDisplay.setVisibility(0);
            } else {
                this.mAmPmDisplay.setVisibility(0);
                this.mTimeTotal.setLayoutDirection(1);
            }
            DateFormatUtil.reset();
            String[] amPmStrings = DateFormatUtil.getAmPmStrings();
            if (this.mShowDetail && CityZoneHelper.isChineseLocale()) {
                try {
                    this.mAmPmDisplay.setText(DateUtils.formatDateTime(DeskClockApp.getAppDEContext(), this.mCalendar.getTimeInMillis(), (z ? 32 : 16) | 12, this.mCalendar.getTimeZone()).substring(0, 2));
                    return;
                } catch (Exception unused2) {
                    this.mAmPmDisplay.setText(this.mCalendar.get(9) == 0 ? amPmStrings[0] : amPmStrings[1]);
                    return;
                }
            }
            this.mAmPmDisplay.setText(this.mCalendar.get(9) == 0 ? amPmStrings[0] : amPmStrings[1]);
            return;
        }
        textView.setVisibility(8);
    }

    public void setTextColor(int i) {
        LimitSizeTextView limitSizeTextView;
        if (!this.mShowTime || (limitSizeTextView = this.mTimeDisplay) == null) {
            return;
        }
        limitSizeTextView.setTextColor(i);
    }

    @Override // miuix.flexible.view.HyperCellLayout, android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mAttached) {
            return;
        }
        this.mAttached = true;
    }

    @Override // miuix.flexible.view.HyperCellLayout, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mAttached) {
            this.mAttached = false;
        }
    }

    public void resetTimeFormat() {
        updateDateFormat();
        updateView();
    }
}
