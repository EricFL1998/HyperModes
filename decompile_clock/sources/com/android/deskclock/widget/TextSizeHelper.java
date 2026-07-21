package com.android.deskclock.widget;

import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
public class TextSizeHelper {
    private Float mMaxTextSize;
    private TextView mTextView;
    private TextPaint mMeasurePaint = new TextPaint();
    private int mWidthConstraint = Integer.MAX_VALUE;
    private int mHeightConstraint = Integer.MAX_VALUE;
    private boolean mIgnoreRequestLayout = false;

    public TextSizeHelper(TextView textView) {
        this.mTextView = textView;
        this.mMaxTextSize = Float.valueOf(textView.getTextSize());
    }

    public void resetMaxTextSize() {
        this.mMaxTextSize = Float.valueOf(this.mTextView.getTextSize());
    }

    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getMode(i) != 0 ? (View.MeasureSpec.getSize(i) - this.mTextView.getCompoundPaddingLeft()) - this.mTextView.getCompoundPaddingRight() : Integer.MAX_VALUE;
        int size2 = View.MeasureSpec.getMode(i2) != 0 ? (View.MeasureSpec.getSize(i2) - this.mTextView.getCompoundPaddingTop()) - this.mTextView.getCompoundPaddingBottom() : Integer.MAX_VALUE;
        if (!this.mTextView.isLayoutRequested() && this.mWidthConstraint == size && this.mHeightConstraint == size2) {
            return;
        }
        this.mWidthConstraint = size;
        this.mHeightConstraint = size2;
        adjustTextSize();
    }

    public void onTextChanged(int i, int i2) {
        if (i != i2) {
            this.mTextView.requestLayout();
        }
    }

    public boolean shouldIgnoreRequestLayout() {
        return this.mIgnoreRequestLayout;
    }

    private void adjustTextSize() {
        String strValueOf = String.valueOf(this.mTextView.getText());
        Float fValueOf = this.mMaxTextSize;
        if (!TextUtils.isEmpty(strValueOf) && (this.mWidthConstraint < Integer.MAX_VALUE || this.mHeightConstraint < Integer.MAX_VALUE)) {
            this.mMeasurePaint.set(this.mTextView.getPaint());
            float fFloatValue = this.mMaxTextSize.floatValue();
            float f = 1.0f;
            while (fFloatValue >= f) {
                float fRound = Math.round((fFloatValue + f) / 2.0f);
                this.mMeasurePaint.setTextSize(fRound);
                float desiredWidth = Layout.getDesiredWidth(strValueOf, this.mMeasurePaint);
                int fontMetricsInt = this.mMeasurePaint.getFontMetricsInt(null);
                if (desiredWidth > this.mWidthConstraint || fontMetricsInt > this.mHeightConstraint) {
                    fFloatValue = fRound - 1.0f;
                } else {
                    fValueOf = Float.valueOf(fRound);
                    f = fRound + 1.0f;
                }
            }
        }
        if (this.mTextView.getTextSize() != fValueOf.floatValue()) {
            this.mIgnoreRequestLayout = true;
            this.mTextView.setTextSize(0, fValueOf.floatValue());
            this.mIgnoreRequestLayout = false;
        }
    }
}
