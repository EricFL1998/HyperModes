package com.android.deskclock.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.util.Util;
import miuix.core.util.MiuixUIUtils;

/* JADX INFO: loaded from: classes.dex */
public class CommonTimerTextView extends View {
    private int mHeight;
    private int mLabelPadding;
    private int mNumColor;
    private int mNumPadding;
    private Paint mNumPaint;
    private int mNumTextSize;
    private String mSecond;
    private int mWidth;
    private int offset1;
    private float start1;

    public CommonTimerTextView(Context context) {
        super(context);
        this.mSecond = "00";
        init();
    }

    public CommonTimerTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSecond = "00";
        init();
    }

    public CommonTimerTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSecond = "00";
        init();
    }

    private void init() {
        this.mLabelPadding = (int) getResources().getDimension(R.dimen.common_timer_item_padding_1);
        this.mNumPadding = (int) getResources().getDimension(R.dimen.common_timer_item_padding_2);
        this.mNumTextSize = (int) getContext().getResources().getDimension(R.dimen.common_timer_item_num_text_size);
        if (MiuixUIUtils.getFontLevel(getContext()) == 2) {
            this.mNumTextSize = (int) getContext().getResources().getDimension(R.dimen.common_timer_item_num_text_size_large);
        }
        this.mNumColor = getContext().getColor(R.color.common_timer_item_num_color);
        Paint paint = new Paint();
        this.mNumPaint = paint;
        paint.setStyle(Paint.Style.FILL);
        this.mNumPaint.setColor(this.mNumColor);
        this.mNumPaint.setTextSize(this.mNumTextSize);
        this.mNumPaint.setAntiAlias(true);
        this.mNumPaint.setTextAlign(Paint.Align.CENTER);
        MiuiFont.setPaintFont(this.mNumPaint, MiuiFont.MI_TYPE_MONO_BOLD);
    }

    public void setTextColor(int i) {
        int color = getContext().getColor(i);
        this.mNumColor = color;
        this.mNumPaint.setColor(color);
        invalidate();
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        this.mWidth = 0;
        if (!TextUtils.isEmpty(this.mSecond)) {
            Paint paint = this.mNumPaint;
            String str = this.mSecond;
            float fMeasureText = paint.measureText(str, 0, str.length());
            this.mWidth = (int) (this.mWidth + this.mLabelPadding + fMeasureText);
            this.start1 = fMeasureText / 2.0f;
        }
        Paint.FontMetricsInt fontMetricsInt = this.mNumPaint.getFontMetricsInt();
        this.mHeight = fontMetricsInt.bottom - fontMetricsInt.top;
        this.offset1 = (fontMetricsInt.ascent + fontMetricsInt.descent) / 2;
        setMeasuredDimension(this.mWidth, this.mHeight);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (TextUtils.isEmpty(this.mSecond)) {
            return;
        }
        canvas.drawText(this.mSecond, this.start1, (this.mHeight / 2) - this.offset1, this.mNumPaint);
    }

    public void setValue(int i) {
        this.mSecond = Util.formatTimeWithSPAN(i * 1000).toString();
        invalidate();
    }
}
