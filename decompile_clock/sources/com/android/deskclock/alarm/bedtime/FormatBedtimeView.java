package com.android.deskclock.alarm.bedtime;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class FormatBedtimeView extends FrameLayout {
    private static final String M24 = "kk:mm";
    private TextView mTimeDisplay;

    public FormatBedtimeView(Context context) {
        this(context, null);
    }

    public FormatBedtimeView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.DigitalClockStyle);
    }

    public FormatBedtimeView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTimeDisplay = (TextView) findViewById(R.id.time_display);
        updateTime(Calendar.getInstance());
    }

    public void updateTime(Calendar calendar) {
        TextView textView = this.mTimeDisplay;
        if (textView == null) {
            return;
        }
        textView.setText(DateFormat.format("kk:mm", calendar));
    }

    public void setTextColor(int i) {
        TextView textView = this.mTimeDisplay;
        if (textView == null) {
            return;
        }
        textView.setTextColor(i);
    }

    public void setTypeface(Typeface typeface) {
        TextView textView = this.mTimeDisplay;
        if (textView == null) {
            return;
        }
        MiuiFont.setFont(textView, typeface);
    }
}
