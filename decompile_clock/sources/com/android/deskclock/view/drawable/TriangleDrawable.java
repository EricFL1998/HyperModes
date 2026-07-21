package com.android.deskclock.view.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/* JADX INFO: loaded from: classes.dex */
public class TriangleDrawable extends Drawable {
    private static final int CORNER = 6;
    private int mColor;
    private int mIntrinsicHeight;
    private int mIntrinsicWidth;
    private Paint mPaint;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    public TriangleDrawable(int i, int i2, int i3) {
        this.mColor = i;
        this.mIntrinsicWidth = i2;
        this.mIntrinsicHeight = i3;
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(this.mColor);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setAntiAlias(true);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int i = bounds.left;
        int i2 = bounds.top;
        int i3 = bounds.right;
        int i4 = bounds.bottom;
        int i5 = ((bounds.right - bounds.left) / 2) + i;
        Path path = new Path();
        float f = i + 6;
        float f2 = i2;
        path.moveTo(f, f2);
        path.lineTo(i3 - 6, f2);
        float f3 = i2 + 6;
        path.quadTo(i3, f2, i3 - 3, f3);
        float f4 = i4 - 6;
        path.lineTo(i5 + 3, f4);
        path.quadTo(i5, i4 - 3, i5 - 3, f4);
        path.lineTo(i + 3, f3);
        path.quadTo(i, f2, f, f2);
        path.close();
        canvas.save();
        canvas.drawPath(path, this.mPaint);
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mPaint.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mIntrinsicWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mIntrinsicHeight;
    }
}
