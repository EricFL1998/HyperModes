package com.android.deskclock.view.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

/* JADX INFO: loaded from: classes.dex */
public class MaskLayerDrawable extends Drawable {
    private Context mContext;
    private Drawable mInnerDrawable;
    private int mScreenHeight;
    private int mScreenWidth;
    private Paint mSrcPaint;
    private Path mSrcPath = new Path();

    public MaskLayerDrawable(Context context, Drawable drawable) {
        this.mInnerDrawable = drawable;
        Paint paint = new Paint(1);
        this.mSrcPaint = paint;
        paint.setColor(-1);
        this.mContext = context;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        this.mScreenWidth = displayMetrics.widthPixels;
        this.mScreenHeight = displayMetrics.heightPixels;
    }

    public void setSrcPath(Path path) {
        this.mSrcPath = path;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        this.mInnerDrawable.setBounds(new Rect(0, 0, this.mScreenWidth, this.mScreenHeight));
        Path path = this.mSrcPath;
        if (path == null || path.isEmpty()) {
            this.mInnerDrawable.draw(canvas);
            return;
        }
        int iSaveLayer = canvas.saveLayer(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), this.mSrcPaint, 31);
        this.mInnerDrawable.draw(canvas);
        this.mSrcPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPath(this.mSrcPath, this.mSrcPaint);
        this.mSrcPaint.setXfermode(null);
        canvas.restoreToCount(iSaveLayer);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mInnerDrawable.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mInnerDrawable.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.mInnerDrawable.getOpacity();
    }
}
