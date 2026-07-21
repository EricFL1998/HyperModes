package com.android.deskclock.worldclock;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
public class TimezoneDateView extends TextView {
    private String mDate;
    private String mTimeGap;

    public TimezoneDateView(Context context) {
        super(context);
    }

    public TimezoneDateView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TimezoneDateView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public TimezoneDateView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public void setText(String str, String str2) {
        if (TextUtils.equals(this.mDate, str) && TextUtils.equals(this.mTimeGap, str2)) {
            return;
        }
        this.mDate = str;
        this.mTimeGap = str2;
        setText(getTextInOneLine());
        requestLayout();
    }

    private String getTextInOneLine() {
        return this.mDate + " " + this.mTimeGap;
    }

    private String getTextInTwoLines() {
        return this.mDate + "\n" + this.mTimeGap;
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (TextUtils.equals(getText(), getTextInTwoLines()) || getMeasuredWidth() >= ((int) getPaint().measureText(getTextInOneLine()))) {
            return;
        }
        setText(getTextInTwoLines());
    }
}
