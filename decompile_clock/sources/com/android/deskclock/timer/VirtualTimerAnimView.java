package com.android.deskclock.timer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.SimpleNumberFormatter;
import com.android.deskclock.util.TimeUtil;
import com.android.deskclock.util.TypefaceFactory;
import com.android.deskclock.util.Util;
import miuix.view.animation.CubicEaseOutInterpolator;

/* JADX INFO: loaded from: classes.dex */
public class VirtualTimerAnimView extends View {
    private static final String COLON = ":";
    private static final long DURATION_COMPRESS = 300;
    private static final long DURATION_EXPAND = 300;
    private String TAG;
    private Animator mAnim;
    private int mColonAlpha;
    private Paint mColonPaint;
    private float mColonWidth;
    private int mHeight;
    private String mHour;
    private float mHourInitCenterX;
    private boolean mIsRTL;
    private float mLeftColonCenterX;
    private String mMinute;
    private float mMinuteCenterX;
    private float mOffset;
    private float mRightColonCenterX;
    private String mSecond;
    private float mSecondInitCenterX;
    private float mTextBaseLine;
    private float mTextCenter;
    private int mTextColor;
    private Paint mTextPaint;
    private int mTextSize;
    private float mTextWidth;
    private float mTotalOffset;
    private int mWidth;
    private int parentTop;
    private int selfTop;
    private float yAllOffset;
    private float yOffset;

    public VirtualTimerAnimView(Context context) {
        this(context, null);
    }

    public VirtualTimerAnimView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VirtualTimerAnimView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.TAG = "VirtualTimerAnimView";
        this.mTextSize = 25;
        this.mTextColor = -452984832;
        this.mHour = "00";
        this.mMinute = "00";
        this.mSecond = "00";
        this.mColonAlpha = 0;
        this.mOffset = 0.0f;
        TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.NumberPicker, i, 0);
        if (Util.isTinyScreen(context)) {
            this.mTextSize = (int) getResources().getDimension(R.dimen.timer_number_picker_tiny_text_size);
        } else {
            this.mTextSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(8, this.mTextSize);
        }
        this.mTextColor = typedArrayObtainStyledAttributes.getColor(0, this.mTextColor);
        typedArrayObtainStyledAttributes.recycle();
        Paint paint = new Paint();
        this.mTextPaint = paint;
        paint.setAntiAlias(true);
        this.mTextPaint.setTextAlign(Paint.Align.CENTER);
        this.mTextPaint.setTextSize(this.mTextSize);
        this.mTextPaint.setColor(this.mTextColor);
        MiuiFont.setPaintFont(this.mTextPaint, MiuiSdk.isSupportMiUiFont() ? MiuiFont.MI_TYPE_MONO_DEMIBOLD : TypefaceFactory.get(TypefaceFactory.MI_TYPE_2019_60));
        Paint.FontMetricsInt fontMetricsInt = this.mTextPaint.getFontMetricsInt();
        this.mTextCenter = (fontMetricsInt.descent + fontMetricsInt.ascent) / 2.0f;
        this.mTextWidth = this.mTextPaint.measureText(formatNumber(0));
        Paint paint2 = new Paint();
        this.mColonPaint = paint2;
        paint2.setAntiAlias(true);
        this.mColonPaint.setTextAlign(Paint.Align.CENTER);
        this.mColonPaint.setTextSize(this.mTextSize);
        this.mColonPaint.setColor(this.mTextColor);
        MiuiFont.setPaintFont(this.mColonPaint, MiuiSdk.isSupportMiUiFont() ? MiuiFont.MI_TYPE_MONO_DEMIBOLD : TypefaceFactory.get(TypefaceFactory.MI_TYPE_2019_60));
        this.mColonPaint.setAlpha(this.mColonAlpha);
        this.mColonWidth = this.mColonPaint.measureText(":");
        this.mIsRTL = Util.isRtl();
    }

    public void setSize(int i) {
        this.mTextSize = i;
        this.mTextPaint.setTextSize(i);
        this.mColonPaint.setTextSize(this.mTextSize);
        Paint.FontMetricsInt fontMetricsInt = this.mTextPaint.getFontMetricsInt();
        this.mTextCenter = (fontMetricsInt.descent + fontMetricsInt.ascent) / 2.0f;
        this.mTextWidth = this.mTextPaint.measureText(formatNumber(0));
        this.mColonWidth = this.mColonPaint.measureText(":");
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mWidth = View.MeasureSpec.getSize(i);
        int size = View.MeasureSpec.getSize(i2);
        this.mHeight = size;
        setMeasuredDimension(this.mWidth, size);
        this.mTextBaseLine = ((this.mHeight / 2) - this.mTextCenter) - 1.0f;
        this.mHourInitCenterX = this.mWidth / 6.0f;
        if (!PadAdapterUtil.IS_PAD) {
            this.mMinuteCenterX = ((this.mWidth / 6.0f) * 3.0f) - 0.8f;
        } else {
            this.mMinuteCenterX = (this.mWidth / 6.0f) * 3.0f;
        }
        this.mSecondInitCenterX = (this.mWidth / 6.0f) * 5.0f;
        float f = this.mMinuteCenterX;
        float f2 = this.mTextWidth;
        float f3 = this.mColonWidth;
        float f4 = (f - (f2 / 2.0f)) - (f3 / 2.0f);
        this.mLeftColonCenterX = f4;
        this.mRightColonCenterX = f + (f2 / 2.0f) + (f3 / 2.0f);
        this.mTotalOffset = ((f4 - (f3 / 2.0f)) - (f2 / 2.0f)) - this.mHourInitCenterX;
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        ViewParent parent = getParent();
        if (parent instanceof ViewGroup) {
            this.parentTop = ((ViewGroup) parent).getTop();
        }
        this.selfTop = i2;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float f = this.mIsRTL ? (this.mHourInitCenterX + this.mOffset) - 8.5f : this.mHourInitCenterX + this.mOffset;
        float f2 = this.mTextBaseLine + this.yOffset;
        canvas.drawText(this.mHour, f, f2, this.mTextPaint);
        canvas.drawText(":", this.mLeftColonCenterX, f2, this.mColonPaint);
        canvas.drawText(this.mMinute, this.mMinuteCenterX, f2, this.mTextPaint);
        canvas.drawText(":", this.mRightColonCenterX, f2, this.mColonPaint);
        canvas.drawText(this.mSecond, this.mIsRTL ? (this.mSecondInitCenterX - this.mOffset) + 8.5f : this.mSecondInitCenterX - this.mOffset, f2, this.mTextPaint);
    }

    public void setDuration(long j) {
        setDuration(TimeUtil.getHourFromDuration(j), TimeUtil.getMinuteFromDuration(j), TimeUtil.getSecondFromDuration(j));
    }

    public void setDuration(int i, int i2, int i3) {
        this.mHour = formatNumber(i);
        this.mMinute = formatNumber(i2);
        this.mSecond = formatNumber(i3);
        invalidate();
    }

    private String formatNumber(int i) {
        return SimpleNumberFormatter.format(2, i);
    }

    public void expand(float f) {
        Paint paint = this.mTextPaint;
        if (paint != null) {
            this.yAllOffset = (((f - paint.getFontMetricsInt().ascent) - this.mTextBaseLine) - this.selfTop) - this.parentTop;
        }
        Animator animator = this.mAnim;
        if (animator != null) {
            animator.cancel();
        }
        Animator animatorCreateExpandAnimator = createExpandAnimator();
        this.mAnim = animatorCreateExpandAnimator;
        animatorCreateExpandAnimator.start();
    }

    public void compress(float f) {
        Paint paint = this.mTextPaint;
        if (paint != null) {
            this.yAllOffset = (((f - paint.getFontMetricsInt().ascent) - this.mTextBaseLine) - this.selfTop) - this.parentTop;
        }
        Animator animator = this.mAnim;
        if (animator != null) {
            animator.cancel();
        }
        Animator animatorCreateCompressAnimator = createCompressAnimator();
        this.mAnim = animatorCreateCompressAnimator;
        animatorCreateCompressAnimator.start();
    }

    private Animator createExpandAnimator() {
        ValueAnimator valueAnimatorOfInt = ValueAnimator.ofInt(100, 0);
        valueAnimatorOfInt.setInterpolator(new CubicEaseOutInterpolator());
        valueAnimatorOfInt.setDuration(300L);
        valueAnimatorOfInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.deskclock.timer.VirtualTimerAnimView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int iIntValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                VirtualTimerAnimView.this.mColonAlpha = (iIntValue * 255) / 100;
                VirtualTimerAnimView.this.mColonPaint.setAlpha(VirtualTimerAnimView.this.mColonAlpha);
                VirtualTimerAnimView virtualTimerAnimView = VirtualTimerAnimView.this;
                float f = iIntValue;
                virtualTimerAnimView.mOffset = (virtualTimerAnimView.mTotalOffset * f) / 100.0f;
                VirtualTimerAnimView virtualTimerAnimView2 = VirtualTimerAnimView.this;
                virtualTimerAnimView2.yOffset = (f * virtualTimerAnimView2.yAllOffset) / 100.0f;
                VirtualTimerAnimView.this.invalidate();
            }
        });
        valueAnimatorOfInt.addListener(new Animator.AnimatorListener() { // from class: com.android.deskclock.timer.VirtualTimerAnimView.2
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                VirtualTimerAnimView.this.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.deskclock.timer.VirtualTimerAnimView.2.1
                    @Override // android.view.View.OnTouchListener
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                VirtualTimerAnimView.this.setOnTouchListener(null);
            }
        });
        return valueAnimatorOfInt;
    }

    private Animator createCompressAnimator() {
        ValueAnimator valueAnimatorOfInt = ValueAnimator.ofInt(0, 100);
        valueAnimatorOfInt.setInterpolator(new CubicEaseOutInterpolator());
        valueAnimatorOfInt.setDuration(300L);
        valueAnimatorOfInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.deskclock.timer.VirtualTimerAnimView.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int iIntValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                VirtualTimerAnimView.this.mColonAlpha = (iIntValue * 255) / 100;
                VirtualTimerAnimView.this.mColonPaint.setAlpha(VirtualTimerAnimView.this.mColonAlpha);
                VirtualTimerAnimView virtualTimerAnimView = VirtualTimerAnimView.this;
                float f = iIntValue;
                virtualTimerAnimView.mOffset = (virtualTimerAnimView.mTotalOffset * f) / 100.0f;
                VirtualTimerAnimView virtualTimerAnimView2 = VirtualTimerAnimView.this;
                virtualTimerAnimView2.yOffset = (f * virtualTimerAnimView2.yAllOffset) / 100.0f;
                VirtualTimerAnimView.this.invalidate();
            }
        });
        return valueAnimatorOfInt;
    }

    public boolean isRunning() {
        Animator animator = this.mAnim;
        if (animator == null) {
            return false;
        }
        return animator.isRunning();
    }
}
