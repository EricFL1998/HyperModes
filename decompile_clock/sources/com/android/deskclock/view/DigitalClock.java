package com.android.deskclock.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.deskclock.R;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.DateFormatUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.TimeUtil;
import com.android.deskclock.util.Util;
import java.lang.ref.WeakReference;
import java.text.DateFormatSymbols;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class DigitalClock extends RelativeLayout {
    private static final String M12 = "hh:mm";
    private static final String M24 = "kk:mm";
    private boolean m24Format;
    private TextView mAmPmDisplay;
    private String mAmString;
    private Calendar mCalendar;
    private TextView mDateDisplay;
    private TextView mDateDisplayWithYear;
    private String mDateFormat;
    private String mDateFormatWithYear;
    private final Handler mHandler;
    private BroadcastReceiver mIntentReceiver;
    private boolean mIsExactToSecond;
    private String mPmString;
    private boolean mShowAmPm;
    private boolean mShowDate;
    private boolean mShowTime;
    private boolean mShowWeek;
    private Runnable mTicker;
    private boolean mTickerStopped;
    private TextView mTimeDisplay;
    private String mTimeFormat;
    private TextView mWeekDisplay;
    private String[] mWeekdayDes;

    public DigitalClock(Context context) {
        this(context, null);
    }

    public DigitalClock(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.DigitalClockStyle);
    }

    public DigitalClock(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.m24Format = true;
        this.mIsExactToSecond = false;
        this.mShowDate = false;
        this.mShowWeek = false;
        this.mShowTime = true;
        this.mShowAmPm = true;
        this.mHandler = new Handler();
        this.mTickerStopped = true;
        TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.DigitalClock, i, 0);
        this.mIsExactToSecond = typedArrayObtainStyledAttributes.getBoolean(3, false);
        this.mShowDate = typedArrayObtainStyledAttributes.getBoolean(1, false);
        this.mShowWeek = typedArrayObtainStyledAttributes.getBoolean(5, false);
        this.mShowTime = typedArrayObtainStyledAttributes.getBoolean(4, true);
        this.mShowAmPm = typedArrayObtainStyledAttributes.getBoolean(0, true);
        typedArrayObtainStyledAttributes.recycle();
        this.mDateFormat = context.getString(R.string.worldcolock_timezone_date);
        this.mDateFormatWithYear = context.getString(R.string.worldcolock_time_date);
        this.mWeekdayDes = new DateFormatSymbols().getWeekdays();
        DateFormatUtil.reset();
        String[] amPmStrings = DateFormatUtil.getAmPmStrings();
        this.mAmString = amPmStrings[0];
        this.mPmString = amPmStrings[1];
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (this.mShowDate) {
            this.mDateDisplay = (TextView) findViewById(R.id.date_display);
            this.mDateDisplayWithYear = (TextView) findViewById(R.id.date_display_with_year);
        }
        if (this.mShowWeek) {
            this.mWeekDisplay = (TextView) findViewById(R.id.week_display);
        }
        if (this.mShowTime) {
            this.mTimeDisplay = (TextView) findViewById(R.id.time_display);
        }
        if (this.mShowAmPm) {
            this.mAmPmDisplay = (TextView) findViewById(R.id.am_pm);
        }
        this.mCalendar = Calendar.getInstance();
        setDateFormat();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mIntentReceiver == null) {
            this.mIntentReceiver = new TimeChangedReceiver(this);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.TIME_TICK");
            intentFilter.addAction("android.intent.action.TIME_SET");
            if (Build.VERSION.SDK_INT >= 34) {
                getContext().registerReceiver(this.mIntentReceiver, intentFilter, 4);
            } else {
                getContext().registerReceiver(this.mIntentReceiver, intentFilter);
            }
        }
        if (this.mIsExactToSecond) {
            if (this.mTicker == null) {
                this.mTicker = new Runnable() { // from class: com.android.deskclock.view.DigitalClock.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (DigitalClock.this.mTickerStopped) {
                            return;
                        }
                        DigitalClock.this.updateTime();
                        DigitalClock.this.mHandler.postAtTime(DigitalClock.this.mTicker, SystemClock.uptimeMillis() + (1000 - (System.currentTimeMillis() % 1000)));
                    }
                };
            }
            this.mTickerStopped = false;
            this.mTicker.run();
        }
        AlarmHelper.reset24HourMode(null);
        setDateFormat();
        updateTime();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mTickerStopped = true;
        if (this.mIntentReceiver != null) {
            getContext().unregisterReceiver(this.mIntentReceiver);
        }
        this.mIntentReceiver = null;
    }

    public void updateTime(Calendar calendar) {
        this.mCalendar = calendar;
        updateTime();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTime() {
        TextView textView;
        TextView textView2;
        String str;
        this.mCalendar.setTimeInMillis(System.currentTimeMillis());
        if (this.mShowDate && this.mDateDisplay != null) {
            this.mDateDisplay.setText(TimeUtil.formatDate(this.mDateFormat, this.mCalendar.getTime(), this.mCalendar.getTimeZone()));
        }
        if (this.mShowDate && this.mDateDisplayWithYear != null) {
            this.mDateDisplayWithYear.setText(TimeUtil.formatDate(this.mDateFormatWithYear, this.mCalendar.getTime(), this.mCalendar.getTimeZone()));
        }
        if (this.mShowWeek && this.mWeekDisplay != null) {
            try {
                str = this.mWeekdayDes[Util.getWeekDay(this.mCalendar)];
            } catch (Exception unused) {
                str = "";
            }
            this.mWeekDisplay.setText(str);
        }
        if (this.mShowTime && (textView2 = this.mTimeDisplay) != null) {
            textView2.setText(DateFormat.format(this.mTimeFormat, this.mCalendar));
        }
        if (!this.mShowAmPm || (textView = this.mAmPmDisplay) == null) {
            return;
        }
        if (!this.m24Format) {
            textView.setVisibility(0);
            this.mAmPmDisplay.setText(this.mCalendar.get(9) == 0 ? this.mAmString : this.mPmString);
        } else {
            textView.setVisibility(8);
        }
    }

    private void setDateFormat() {
        String string;
        boolean z = AlarmHelper.get24HourMode();
        this.m24Format = z;
        String str = z ? "kk:mm" : "hh:mm";
        if (z) {
            string = getContext().getString(R.string.clock_date_time_format_seconds_24);
        } else {
            string = getContext().getString(R.string.clock_date_time_format_seconds_12);
        }
        if (this.mIsExactToSecond) {
            str = string;
        }
        this.mTimeFormat = str;
    }

    private static class TimeChangedReceiver extends BroadcastReceiver {
        private WeakReference<DigitalClock> mClock;

        public TimeChangedReceiver(DigitalClock digitalClock) {
            this.mClock = new WeakReference<>(digitalClock);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.d("DigitalClock onReceive: " + intent.getAction());
            DigitalClock digitalClock = this.mClock.get();
            if (digitalClock != null) {
                digitalClock.updateTime();
            }
        }
    }
}
