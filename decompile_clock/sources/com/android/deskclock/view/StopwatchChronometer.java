package com.android.deskclock.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.deskclock.R;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.TypefaceFactory;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class StopwatchChronometer extends LinearLayout {
    private static final int HOUR_LIMITATION = 1000;
    private static final int TICK_WHAT = 2;
    private static final long TIME_DELAY_REFRESH = 10;
    private static final long UP_MILLIS_LIMITATION = 3600000000L;
    private long mBase;
    private Handler mHandler;
    private boolean mRunning;
    private boolean mStarted;
    private View mTimeHourView;
    private TextView mTimeView;
    private boolean mVisible;

    public interface OnChronometerTickListener {
        void onChronometerTick(StopwatchChronometer stopwatchChronometer);
    }

    public StopwatchChronometer(Context context) {
        this(context, null);
    }

    public StopwatchChronometer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHandler = new Handler() { // from class: com.android.deskclock.view.StopwatchChronometer.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (StopwatchChronometer.this.mRunning) {
                    StopwatchChronometer.this.updateText(System.currentTimeMillis());
                    sendMessageDelayed(Message.obtain(this, 2), StopwatchChronometer.TIME_DELAY_REFRESH);
                }
            }
        };
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTimeHourView = findViewById(R.id.timer_hour_panel);
        TextView textView = (TextView) findViewById(R.id.time);
        this.mTimeView = textView;
        textView.setTypeface(TypefaceFactory.get(TypefaceFactory.CUSTOM_MONO_DEMIBOLD));
        this.mTimeView.setFontFeatureSettings("tnum");
    }

    public void setBase(long j) {
        this.mBase = j;
        updateText(System.currentTimeMillis());
    }

    public long getBase() {
        return this.mBase;
    }

    public void start() {
        this.mStarted = true;
        updateRunning();
    }

    public void stop() {
        this.mStarted = false;
        updateRunning();
    }

    public void setVisible(boolean z) {
        this.mVisible = z;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mVisible = false;
        updateRunning();
    }

    @Override // android.view.View
    protected void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        this.mVisible = i == 0;
        updateRunning();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void updateText(long j) {
        View view;
        long j2 = j - this.mBase;
        if (j2 >= UP_MILLIS_LIMITATION && (view = this.mTimeHourView) != null) {
            long j3 = (j2 / AlarmHelper.ARRIVING_ALARM_DURATION) / 1000;
            j2 -= j3 * UP_MILLIS_LIMITATION;
            view.setVisibility(0);
            ((TextView) this.mTimeHourView.findViewById(R.id.time_hour)).setText(Util.formatDigits(1000 * j3));
        } else {
            View view2 = this.mTimeHourView;
            if (view2 != null) {
                view2.setVisibility(8);
            }
        }
        this.mTimeView.setText(Util.formatElapsedTime(j2, false));
    }

    private void updateRunning() {
        boolean z = this.mVisible && this.mStarted;
        if (z != this.mRunning) {
            updateText(System.currentTimeMillis());
            long jCurrentTimeMillis = System.currentTimeMillis() - this.mBase;
            if (jCurrentTimeMillis >= UP_MILLIS_LIMITATION && this.mTimeHourView != null) {
                jCurrentTimeMillis -= ((jCurrentTimeMillis / AlarmHelper.ARRIVING_ALARM_DURATION) / 1000) * UP_MILLIS_LIMITATION;
            }
            this.mTimeView.setContentDescription(Util.formatLapItem(getContext(), jCurrentTimeMillis, R.array.time) + (jCurrentTimeMillis % TIME_DELAY_REFRESH >= 5 ? ((jCurrentTimeMillis % 1000) / TIME_DELAY_REFRESH) + 1 : (jCurrentTimeMillis % 1000) / TIME_DELAY_REFRESH));
            if (z) {
                Handler handler = this.mHandler;
                handler.sendMessageDelayed(Message.obtain(handler, 2), TIME_DELAY_REFRESH);
            } else {
                this.mHandler.removeMessages(2);
            }
            this.mRunning = z;
        }
    }

    public void resetTimeViewContentDescription() {
        TextView textView = this.mTimeView;
        if (textView != null) {
            textView.setContentDescription(Util.formatLapItem(getContext(), 0L, R.array.time) + 0);
        }
    }

    public TextView getTimeView() {
        return this.mTimeView;
    }

    public View getTimeHourView() {
        return this.mTimeHourView;
    }
}
