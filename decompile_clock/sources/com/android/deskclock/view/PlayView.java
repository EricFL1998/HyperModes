package com.android.deskclock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class PlayView extends View {
    private int mAxisX1;
    private int mAxisX2;
    private int mAxisX3;
    private float mBottom;
    private int mDelta1;
    private int mDelta2;
    private int mDelta3;
    private int mHeight;
    private boolean mLoop;
    private Paint mPaint;
    private int mRadius;
    private float mTop1;
    private float mTop2;
    private float mTop3;
    private int mWidth;

    public PlayView(Context context) {
        this(context, null);
    }

    public PlayView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PlayView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDelta1 = -1;
        this.mDelta2 = -1;
        this.mDelta3 = -1;
        this.mLoop = false;
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(-1);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setAntiAlias(true);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mWidth = View.MeasureSpec.getSize(i);
        int size = View.MeasureSpec.getSize(i2);
        this.mHeight = size;
        setMeasuredDimension(this.mWidth, size);
        int i3 = this.mWidth;
        int i4 = i3 / 14;
        this.mRadius = i4;
        int i5 = i3 / 7;
        this.mAxisX1 = i4;
        this.mAxisX2 = ((i3 / 7) * 3) + i4;
        this.mAxisX3 = ((i3 / 7) * 6) + i4;
        int i6 = this.mHeight;
        this.mTop1 = (i6 * 0.5f) - i4;
        this.mTop2 = (i6 * 0.7f) - i4;
        this.mTop3 = (i6 * 0.3f) - i4;
        this.mBottom = i6 - i4;
        this.mPaint.setStrokeWidth(i4 * 2);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int i = this.mAxisX1;
        canvas.drawLine(i, this.mTop1, i, this.mBottom, this.mPaint);
        canvas.drawCircle(this.mAxisX1, this.mTop1, this.mRadius, this.mPaint);
        canvas.drawCircle(this.mAxisX1, this.mBottom, this.mRadius, this.mPaint);
        int i2 = this.mAxisX2;
        canvas.drawLine(i2, this.mTop2, i2, this.mBottom, this.mPaint);
        canvas.drawCircle(this.mAxisX2, this.mTop2, this.mRadius, this.mPaint);
        canvas.drawCircle(this.mAxisX2, this.mBottom, this.mRadius, this.mPaint);
        int i3 = this.mAxisX3;
        canvas.drawLine(i3, this.mTop3, i3, this.mBottom, this.mPaint);
        canvas.drawCircle(this.mAxisX3, this.mTop3, this.mRadius, this.mPaint);
        canvas.drawCircle(this.mAxisX3, this.mBottom, this.mRadius, this.mPaint);
        if (this.mLoop) {
            loop();
        }
    }

    private void loop() {
        float f = this.mTop1 + this.mDelta1;
        this.mTop1 = f;
        float f2 = this.mTop2 + this.mDelta2;
        this.mTop2 = f2;
        float f3 = this.mTop3 + this.mDelta3;
        this.mTop3 = f3;
        int i = this.mRadius;
        if (f <= i) {
            this.mDelta1 = 1;
        } else if (f >= this.mBottom) {
            this.mDelta1 = -1;
        }
        if (f2 <= i) {
            this.mDelta2 = 1;
        } else if (f2 >= this.mBottom) {
            this.mDelta2 = -1;
        }
        if (f3 <= i) {
            this.mDelta3 = 1;
        } else if (f3 >= this.mBottom) {
            this.mDelta3 = -1;
        }
        invalidate();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mLoop = true;
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mLoop = false;
    }
}
