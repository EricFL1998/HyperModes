package com.android.deskclock.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.DateFormatUtil;
import com.android.deskclock.util.Log;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class FormatTimeViewForAlarmItem extends RelativeLayout {
    private static final String M12 = "hh:mm";
    private static final String M24 = "kk:mm";
    private boolean m24Format;
    private TextView mAmPmDisplay;
    private Handler mAsyncHandler;
    private Calendar mCalendar;
    private Handler mHandler;
    private TextView mTimeDisplay;

    public FormatTimeViewForAlarmItem(Context context) {
        this(context, null);
    }

    public FormatTimeViewForAlarmItem(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.DigitalClockStyle);
    }

    public FormatTimeViewForAlarmItem(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.m24Format = true;
    }

    public void initAlarmItemHandler() {
        HandlerThread handlerThread = new HandlerThread("FormatTimeViewForAlarmItem");
        handlerThread.start();
        this.mAsyncHandler = new Handler(handlerThread.getLooper()) { // from class: com.android.deskclock.view.FormatTimeViewForAlarmItem.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                super.handleMessage(message);
                String str = (String) DateFormat.format(FormatTimeViewForAlarmItem.this.m24Format ? "kk:mm" : "hh:mm", FormatTimeViewForAlarmItem.this.mCalendar);
                Message messageObtainMessage = FormatTimeViewForAlarmItem.this.mHandler.obtainMessage(0);
                messageObtainMessage.obj = str;
                FormatTimeViewForAlarmItem.this.mHandler.removeMessages(0);
                FormatTimeViewForAlarmItem.this.mHandler.sendMessage(messageObtainMessage);
            }
        };
        this.mHandler = new Handler() { // from class: com.android.deskclock.view.FormatTimeViewForAlarmItem.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (FormatTimeViewForAlarmItem.this.mTimeDisplay == null || message.obj == null) {
                    return;
                }
                FormatTimeViewForAlarmItem.this.mTimeDisplay.setText(message.obj.toString());
            }
        };
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTimeDisplay = (TextView) findViewById(R.id.time_display);
        this.mAmPmDisplay = (TextView) findViewById(R.id.am_pm);
        this.mCalendar = Calendar.getInstance();
        setDateFormat();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateTime();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void dismiss() {
        Log.d("FormatTimeViewForAlarmItem dismiss mHandler: " + this.mHandler + "  mAsyncHandler: " + this.mAsyncHandler);
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        Handler handler2 = this.mAsyncHandler;
        if (handler2 != null) {
            handler2.removeCallbacksAndMessages(null);
        }
    }

    public void updateTime(Calendar calendar) {
        this.mCalendar = calendar;
        updateTime();
    }

    private void updateTime() {
        if (this.mTimeDisplay == null || this.mAmPmDisplay == null) {
            return;
        }
        Handler handler = this.mAsyncHandler;
        if (handler != null) {
            handler.removeMessages(0);
            Handler handler2 = this.mAsyncHandler;
            handler2.sendMessage(handler2.obtainMessage(0));
        }
        if (!this.m24Format) {
            this.mAmPmDisplay.setVisibility(0);
            DateFormatUtil.reset();
            String[] amPmStrings = DateFormatUtil.getAmPmStrings();
            this.mAmPmDisplay.setText(this.mCalendar.get(9) == 0 ? amPmStrings[0] : amPmStrings[1]);
            return;
        }
        this.mAmPmDisplay.setVisibility(8);
    }

    private void setDateFormat() {
        this.m24Format = AlarmHelper.get24HourMode();
    }

    public void setTextColor(int i) {
        TextView textView = this.mTimeDisplay;
        if (textView == null || this.mAmPmDisplay == null) {
            return;
        }
        textView.setTextColor(i);
        this.mAmPmDisplay.setTextColor(i);
    }

    public void setTypeface(Typeface typeface, Typeface typeface2) {
        TextView textView = this.mTimeDisplay;
        if (textView == null || this.mAmPmDisplay == null) {
            return;
        }
        MiuiFont.setFont(textView, typeface);
        MiuiFont.setFont(this.mAmPmDisplay, typeface2);
    }

    public void resetTimeFormat() {
        setDateFormat();
        updateTime();
    }

    public boolean is24Format() {
        return this.m24Format;
    }
}
