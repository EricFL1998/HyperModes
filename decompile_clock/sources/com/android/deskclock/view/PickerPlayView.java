package com.android.deskclock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class PickerPlayView extends View {
    private float mAxisX1;
    private float mAxisX2;
    private float mAxisX3;
    private float mAxisX4;
    private float mAxisX5;
    private float mBottom;
    private int mDelta1;
    private int mDelta2;
    private int mDelta3;
    private int mDelta4;
    private int mDelta5;
    private int mHeight;
    private boolean mLoop;
    private Paint mPaint;
    private float mRadius;
    private float mTop1;
    private float mTop2;
    private float mTop3;
    private float mTop4;
    private float mTop5;
    private int mWidth;

    public PickerPlayView(Context context) {
        this(context, null);
    }

    public PickerPlayView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PickerPlayView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDelta1 = -1;
        this.mDelta2 = -1;
        this.mDelta3 = -1;
        this.mDelta4 = -1;
        this.mDelta5 = -1;
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
        float f = (float) (((double) i3) / 18.0d);
        this.mRadius = f;
        this.mAxisX1 = (float) (((((double) i3) / 9.0d) * 0.0d) + ((double) f));
        this.mAxisX2 = (float) (((((double) i3) / 9.0d) * 2.0d) + ((double) f));
        this.mAxisX3 = (float) (((((double) i3) / 9.0d) * 4.0d) + ((double) f));
        this.mAxisX4 = (float) (((((double) i3) / 9.0d) * 6.0d) + ((double) f));
        this.mAxisX5 = (float) (((((double) i3) / 9.0d) * 8.0d) + ((double) f));
        int i4 = this.mHeight;
        this.mTop1 = (i4 * 0.5f) - f;
        this.mTop2 = (i4 * 0.7f) - f;
        this.mTop3 = (i4 * 0.3f) - f;
        this.mTop4 = (i4 * 0.1f) - f;
        this.mTop5 = (i4 * 0.9f) - f;
        this.mBottom = i4 - f;
        this.mPaint.setStrokeWidth(f * 2.0f);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float f = this.mAxisX1;
        canvas.drawLine(f, this.mTop1, f, this.mBottom, this.mPaint);
        canvas.drawCircle(this.mAxisX1, this.mTop1, this.mRadius, this.mPaint);
        canvas.drawCircle(this.mAxisX1, this.mBottom, this.mRadius, this.mPaint);
        float f2 = this.mAxisX2;
        canvas.drawLine(f2, this.mTop2, f2, this.mBottom, this.mPaint);
        canvas.drawCircle(this.mAxisX2, this.mTop2, this.mRadius, this.mPaint);
        canvas.drawCircle(this.mAxisX2, this.mBottom, this.mRadius, this.mPaint);
        float f3 = this.mAxisX3;
        canvas.drawLine(f3, this.mTop3, f3, this.mBottom, this.mPaint);
        canvas.drawCircle(this.mAxisX3, this.mTop3, this.mRadius, this.mPaint);
        canvas.drawCircle(this.mAxisX3, this.mBottom, this.mRadius, this.mPaint);
        float f4 = this.mAxisX4;
        canvas.drawLine(f4, this.mTop4, f4, this.mBottom, this.mPaint);
        canvas.drawCircle(this.mAxisX4, this.mTop4, this.mRadius, this.mPaint);
        canvas.drawCircle(this.mAxisX4, this.mBottom, this.mRadius, this.mPaint);
        float f5 = this.mAxisX5;
        canvas.drawLine(f5, this.mTop5, f5, this.mBottom, this.mPaint);
        canvas.drawCircle(this.mAxisX5, this.mTop5, this.mRadius, this.mPaint);
        canvas.drawCircle(this.mAxisX5, this.mBottom, this.mRadius, this.mPaint);
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
        float f4 = this.mTop4 + this.mDelta4;
        this.mTop4 = f4;
        float f5 = this.mTop5 + this.mDelta5;
        this.mTop5 = f5;
        float f6 = this.mRadius;
        if (f <= f6) {
            this.mDelta1 = 1;
        } else if (f >= this.mBottom) {
            this.mDelta1 = -1;
        }
        if (f2 <= f6) {
            this.mDelta2 = 1;
        } else if (f2 >= this.mBottom) {
            this.mDelta2 = -1;
        }
        if (f3 <= f6) {
            this.mDelta3 = 1;
        } else if (f3 >= this.mBottom) {
            this.mDelta3 = -1;
        }
        if (f4 <= f6) {
            this.mDelta4 = 1;
        } else if (f4 >= this.mBottom) {
            this.mDelta4 = -1;
        }
        if (f5 <= f6) {
            this.mDelta5 = 1;
        } else if (f5 >= this.mBottom) {
            this.mDelta5 = -1;
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
