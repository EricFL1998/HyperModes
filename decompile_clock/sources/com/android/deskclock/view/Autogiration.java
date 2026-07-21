package com.android.deskclock.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/* JADX INFO: loaded from: classes.dex */
public class Autogiration extends View {
    public static final float CIRCLE_DEGREE = 360.0f;
    private static final float ROTATE_DEGREE_OFFSET = 270.0f;
    private Context mContext;
    private float mDegree;
    private long mDuration;
    private int mHeight;
    private float mLastDegree;
    private Drawable mProgress;
    private int mProgressAlpha;
    private boolean mStart;
    private long mStartTime;
    private int mWidth;

    public Autogiration(Context context) {
        super(context);
        this.mDuration = 1000L;
        this.mProgressAlpha = 255;
    }

    public Autogiration(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDuration = 1000L;
        this.mProgressAlpha = 255;
        this.mContext = context;
    }

    public void setProgressRes(int i) {
        Drawable drawable = this.mContext.getResources().getDrawable(i);
        this.mProgress = drawable;
        this.mWidth = drawable.getIntrinsicWidth();
        this.mHeight = this.mProgress.getIntrinsicHeight();
        invalidate();
    }

    public void setDuration(long j) {
        this.mDuration = j;
    }

    public void onStart() {
        this.mStartTime = System.currentTimeMillis();
        this.mStart = true;
        invalidate();
    }

    public void onStop() {
        this.mStart = false;
        this.mLastDegree = this.mDegree;
        invalidate();
    }

    public void onReset() {
        this.mStart = false;
        final ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.mLastDegree, 0.0f);
        valueAnimatorOfFloat.setInterpolator(new AccelerateInterpolator());
        valueAnimatorOfFloat.setDuration(400L);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.deskclock.view.Autogiration.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (valueAnimatorOfFloat.isRunning()) {
                    Autogiration.this.mLastDegree = ((Float) valueAnimatorOfFloat.getAnimatedValue()).floatValue();
                    Autogiration.this.invalidate();
                }
            }
        });
        valueAnimatorOfFloat.start();
        invalidate();
    }

    public void doEnterAnimation() {
        this.mProgressAlpha = 0;
        float f = this.mLastDegree;
        float f2 = f - ROTATE_DEGREE_OFFSET;
        this.mLastDegree = f2;
        final ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(f2, f);
        valueAnimatorOfFloat.setDuration(650L);
        valueAnimatorOfFloat.setInterpolator(new DecelerateInterpolator(1.5f));
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.deskclock.view.Autogiration.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (valueAnimatorOfFloat.isRunning()) {
                    Autogiration.this.mProgressAlpha = (int) (valueAnimator.getAnimatedFraction() * 255.0f);
                    if (!Autogiration.this.mStart) {
                        Autogiration.this.mLastDegree = ((Float) valueAnimatorOfFloat.getAnimatedValue()).floatValue();
                    }
                    Autogiration.this.invalidate();
                }
            }
        });
        valueAnimatorOfFloat.start();
        invalidate();
    }

    public void setDegree(long j) {
        this.mLastDegree = calDegreeFromElapsedTime(j);
        invalidate();
    }

    private float calDegreeFromElapsedTime(long j) {
        long j2 = this.mDuration;
        return ((j % j2) * 360.0f) / j2;
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        int i4;
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size2 = View.MeasureSpec.getSize(i2);
        float f = 1.0f;
        float f2 = (mode == 0 || size >= (i4 = this.mWidth)) ? 1.0f : size / i4;
        if (mode2 != 0 && size2 < (i3 = this.mHeight)) {
            f = size2 / i3;
        }
        float fMin = Math.min(f2, f);
        setMeasuredDimension(resolveSizeAndState((int) (this.mWidth * fMin), i, 0), resolveSizeAndState((int) (this.mHeight * fMin), i2, 0));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        boolean z;
        super.onDraw(canvas);
        long jCurrentTimeMillis = System.currentTimeMillis() - this.mStartTime;
        if (this.mStart) {
            this.mDegree = (this.mLastDegree + calDegreeFromElapsedTime(jCurrentTimeMillis)) % 360.0f;
        } else {
            this.mDegree = this.mLastDegree;
        }
        int right = getRight() - getLeft();
        int bottom = getBottom() - getTop();
        int i = right / 2;
        int i2 = bottom / 2;
        Drawable drawable = this.mProgress;
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        if (right < intrinsicWidth || bottom < intrinsicHeight) {
            float fMin = Math.min(right / intrinsicWidth, bottom / intrinsicHeight);
            canvas.save();
            canvas.scale(fMin, fMin, i, i2);
            z = true;
        } else {
            z = false;
        }
        canvas.save();
        canvas.rotate(this.mDegree, i, i2);
        drawable.setAlpha(this.mProgressAlpha);
        int i3 = intrinsicWidth / 2;
        int i4 = intrinsicHeight / 2;
        drawable.setBounds(i - i3, i2 - i4, i + i3, i2 + i4);
        drawable.draw(canvas);
        canvas.restore();
        if (z) {
            canvas.restore();
        }
        if (this.mStart) {
            invalidate();
        }
    }
}
