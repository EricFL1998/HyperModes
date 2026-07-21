package com.android.deskclock.worldclock;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.android.deskclock.R;
import java.util.Calendar;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class ClockView extends View {
    private static final int ANIM_DURATION_MILLIS = 300;
    private static final int TIME_ENTER_DAY = 6;
    private static final int TIME_ENTER_NIGHT = 18;
    private ValueAnimator animForHour;
    private ValueAnimator animForMin;
    private int mBackgroundColor;
    private Paint mBackgroundPaint;
    private int mBorderColor;
    private Paint mBorderPaint;
    private int mBorderWidth;
    private Calendar mCalendar;
    private int mCenterColor;
    private Paint mCenterPaint;
    private int mCenterRadius;
    private int mDayBackgroundColor;
    private int mDayBorderColor;
    private int mDayCenterColor;
    private int mDayHourColor;
    private int mDayMinuteColor;
    private int mHeight;
    private int mHourColor;
    private int mHourDegree;
    private int mHourHeight;
    private Paint mHourPaint;
    private int mHourWidth;
    private int mMinuteColor;
    private int mMinuteDegree;
    private int mMinuteHeight;
    private Paint mMinutePaint;
    private int mMinuteWidth;
    private int mNightBackgroundColor;
    private int mNightBorderColor;
    private int mNightCenterColor;
    private int mNightHourColor;
    private int mNightMinuteColor;
    private int mTargetMinsDegree;
    private int mWidth;

    static /* synthetic */ int access$012(ClockView clockView, int i) {
        int i2 = clockView.mMinuteDegree + i;
        clockView.mMinuteDegree = i2;
        return i2;
    }

    static /* synthetic */ int access$020(ClockView clockView, int i) {
        int i2 = clockView.mMinuteDegree - i;
        clockView.mMinuteDegree = i2;
        return i2;
    }

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ClockView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDayBackgroundColor = -1116932;
        this.mDayBorderColor = 167772160;
        this.mDayMinuteColor = -8618884;
        this.mDayHourColor = -10066330;
        this.mDayCenterColor = -7303024;
        this.mNightBackgroundColor = -13487566;
        this.mNightBorderColor = 167772160;
        this.mNightMinuteColor = -1447447;
        this.mNightHourColor = -1;
        this.mNightCenterColor = -7303024;
        this.mBackgroundColor = -13487566;
        this.mBorderColor = 167772160;
        this.mMinuteColor = -1447447;
        this.mHourColor = -1;
        this.mCenterColor = -7303024;
        this.mWidth = 115;
        this.mHeight = 115;
        this.mBorderWidth = 1;
        this.mMinuteWidth = 2;
        this.mMinuteHeight = 46;
        this.mHourWidth = 4;
        this.mHourHeight = 31;
        this.mCenterRadius = 1;
        this.mTargetMinsDegree = -1;
        init(context, attributeSet);
        this.mCalendar = Calendar.getInstance();
        Paint paint = new Paint();
        this.mBackgroundPaint = paint;
        paint.setAntiAlias(true);
        this.mBackgroundPaint.setStyle(Paint.Style.FILL);
        Paint paint2 = new Paint();
        this.mBorderPaint = paint2;
        paint2.setAntiAlias(true);
        this.mBorderPaint.setStrokeWidth(this.mBorderWidth);
        this.mBorderPaint.setStyle(Paint.Style.STROKE);
        Paint paint3 = new Paint();
        this.mMinutePaint = paint3;
        paint3.setAntiAlias(true);
        this.mMinutePaint.setStrokeWidth(this.mMinuteWidth);
        this.mMinutePaint.setStyle(Paint.Style.FILL);
        Paint paint4 = new Paint();
        this.mHourPaint = paint4;
        paint4.setAntiAlias(true);
        this.mHourPaint.setStrokeWidth(this.mHourWidth);
        this.mHourPaint.setStyle(Paint.Style.FILL);
        Paint paint5 = new Paint();
        this.mCenterPaint = paint5;
        paint5.setAntiAlias(true);
        this.mCenterPaint.setStyle(Paint.Style.FILL);
        updateClockViewData(true);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ClockView);
        this.mDayBackgroundColor = typedArrayObtainStyledAttributes.getColor(2, this.mDayBackgroundColor);
        this.mDayBorderColor = typedArrayObtainStyledAttributes.getColor(3, this.mDayBorderColor);
        this.mDayHourColor = typedArrayObtainStyledAttributes.getColor(5, this.mDayHourColor);
        this.mDayMinuteColor = typedArrayObtainStyledAttributes.getColor(6, this.mDayMinuteColor);
        this.mDayCenterColor = typedArrayObtainStyledAttributes.getColor(4, this.mDayCenterColor);
        this.mNightBackgroundColor = typedArrayObtainStyledAttributes.getColor(11, this.mNightBackgroundColor);
        this.mNightBorderColor = typedArrayObtainStyledAttributes.getColor(12, this.mNightBorderColor);
        this.mNightHourColor = typedArrayObtainStyledAttributes.getColor(14, this.mNightHourColor);
        this.mNightMinuteColor = typedArrayObtainStyledAttributes.getColor(15, this.mNightMinuteColor);
        this.mNightCenterColor = typedArrayObtainStyledAttributes.getColor(13, this.mNightCenterColor);
        this.mBorderWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(0, this.mBorderWidth);
        this.mMinuteWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(10, this.mMinuteWidth);
        this.mMinuteHeight = typedArrayObtainStyledAttributes.getDimensionPixelSize(9, this.mMinuteHeight);
        this.mHourWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(8, this.mHourWidth);
        this.mHourHeight = typedArrayObtainStyledAttributes.getDimensionPixelSize(7, this.mHourHeight);
        this.mCenterRadius = typedArrayObtainStyledAttributes.getDimensionPixelSize(1, this.mCenterRadius);
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int iMin = Math.min(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
        this.mWidth = iMin;
        this.mHeight = iMin;
        setMeasuredDimension(iMin, iMin);
    }

    public void updateTime(TimeZone timeZone, long j) {
        this.mCalendar.setTimeZone(timeZone);
        this.mCalendar.setTimeInMillis(j);
        updateClockViewData(true);
        invalidate();
    }

    public void updateTimeAnim(TimeZone timeZone, final long j) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(timeZone);
        calendar.setTimeInMillis(j);
        this.mCalendar.setTimeZone(timeZone);
        final int i7 = (calendar.get(12) * 360) / 60;
        final int i8 = this.mMinuteDegree;
        if (calendar.getTimeInMillis() > this.mCalendar.getTimeInMillis()) {
            if (i7 < i8) {
                i7 += 360;
            }
        } else if (i7 > i8) {
            i8 += 360;
        }
        ValueAnimator valueAnimator = this.animForMin;
        if (valueAnimator == null || !valueAnimator.isRunning() || i7 != (i6 = this.mTargetMinsDegree) || i6 == -1) {
            ValueAnimator valueAnimator2 = this.animForMin;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            this.mTargetMinsDegree = i7;
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.animForMin = valueAnimatorOfFloat;
            valueAnimatorOfFloat.setDuration(300L);
            this.animForMin.setInterpolator(new DecelerateInterpolator(1.5f));
            this.animForMin.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.deskclock.worldclock.ClockView.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    float fFloatValue = ((Float) valueAnimator3.getAnimatedValue()).floatValue();
                    int i9 = i8;
                    ClockView.this.mMinuteDegree = (int) (i9 + ((i7 - i9) * fFloatValue));
                    while (ClockView.this.mMinuteDegree >= 360) {
                        ClockView.access$020(ClockView.this, 360);
                    }
                    while (ClockView.this.mMinuteDegree < 0) {
                        ClockView.access$012(ClockView.this, 360);
                    }
                    ClockView.this.invalidate();
                }
            });
            this.animForMin.start();
        }
        int i9 = calendar.get(11);
        boolean z = i9 >= 18 || i9 < 6;
        final int i10 = this.mBackgroundColor;
        final int i11 = this.mBorderColor;
        final int i12 = this.mMinuteColor;
        final int i13 = this.mHourColor;
        final int i14 = this.mCenterColor;
        if (!z) {
            i = this.mDayBackgroundColor;
            i2 = this.mDayBorderColor;
            i3 = this.mDayMinuteColor;
            i4 = this.mDayHourColor;
            i5 = this.mDayCenterColor;
        } else {
            i = this.mNightBackgroundColor;
            i2 = this.mNightBorderColor;
            i3 = this.mNightMinuteColor;
            i4 = this.mNightHourColor;
            i5 = this.mNightCenterColor;
        }
        final int i15 = i;
        final int i16 = i2;
        final int i17 = i3;
        final int i18 = i4;
        final int i19 = i5;
        final long timeInMillis = this.mCalendar.getTimeInMillis();
        ValueAnimator valueAnimator3 = this.animForHour;
        if (valueAnimator3 != null) {
            valueAnimator3.cancel();
        }
        ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.animForHour = valueAnimatorOfFloat2;
        valueAnimatorOfFloat2.setDuration(300L);
        this.animForHour.setInterpolator(new DecelerateInterpolator(1.5f));
        this.animForHour.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.deskclock.worldclock.ClockView.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator4) {
                float fFloatValue = ((Float) valueAnimator4.getAnimatedValue()).floatValue();
                long j2 = timeInMillis;
                ClockView.this.mCalendar.setTimeInMillis((long) (j2 + ((j - j2) * fFloatValue)));
                ClockView clockView = ClockView.this;
                clockView.mBackgroundColor = clockView.evaluate(fFloatValue, i10, i15).intValue();
                ClockView clockView2 = ClockView.this;
                clockView2.mBorderColor = clockView2.evaluate(fFloatValue, i11, i16).intValue();
                ClockView clockView3 = ClockView.this;
                clockView3.mMinuteColor = clockView3.evaluate(fFloatValue, i12, i17).intValue();
                ClockView clockView4 = ClockView.this;
                clockView4.mHourColor = clockView4.evaluate(fFloatValue, i13, i18).intValue();
                ClockView clockView5 = ClockView.this;
                clockView5.mCenterColor = clockView5.evaluate(fFloatValue, i14, i19).intValue();
                ClockView.this.updateClockViewData(false);
                ClockView.this.invalidate();
            }
        });
        this.animForHour.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateClockViewData(boolean z) {
        if (z) {
            int i = this.mCalendar.get(11);
            if (i >= 18 || i < 6) {
                this.mBackgroundPaint.setColor(this.mNightBackgroundColor);
                this.mBorderPaint.setColor(this.mNightBorderColor);
                this.mMinutePaint.setColor(this.mNightMinuteColor);
                this.mHourPaint.setColor(this.mNightHourColor);
                this.mCenterPaint.setColor(this.mNightCenterColor);
                this.mBackgroundColor = this.mNightBackgroundColor;
                this.mBorderColor = this.mNightBorderColor;
                this.mMinuteColor = this.mNightMinuteColor;
                this.mHourColor = this.mNightHourColor;
                this.mCenterColor = this.mNightCenterColor;
            } else {
                this.mBackgroundPaint.setColor(this.mDayBackgroundColor);
                this.mBorderPaint.setColor(this.mDayBorderColor);
                this.mMinutePaint.setColor(this.mDayMinuteColor);
                this.mHourPaint.setColor(this.mDayHourColor);
                this.mCenterPaint.setColor(this.mDayCenterColor);
                this.mBackgroundColor = this.mDayBackgroundColor;
                this.mBorderColor = this.mDayBorderColor;
                this.mMinuteColor = this.mDayMinuteColor;
                this.mHourColor = this.mDayHourColor;
                this.mCenterColor = this.mDayCenterColor;
            }
        } else {
            this.mBackgroundPaint.setColor(this.mBackgroundColor);
            this.mBorderPaint.setColor(this.mBorderColor);
            this.mMinutePaint.setColor(this.mMinuteColor);
            this.mHourPaint.setColor(this.mHourColor);
            this.mCenterPaint.setColor(this.mCenterColor);
        }
        int i2 = this.mCalendar.get(12);
        int i3 = this.mCalendar.get(10);
        if (z) {
            this.mMinuteDegree = (i2 * 360) / 60;
        }
        int i4 = (i3 * 360) / 12;
        this.mHourDegree = i4;
        this.mHourDegree = i4 + ((i2 * 30) / 60);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i = this.mWidth;
        int i2 = i / 2;
        int i3 = i / 2;
        int i4 = i / 2;
        float f = i2;
        float f2 = i3;
        canvas.drawCircle(f, f2, i4, this.mBackgroundPaint);
        canvas.drawCircle(f, f2, i4 - this.mBorderWidth, this.mBorderPaint);
        canvas.save();
        canvas.rotate(this.mMinuteDegree, f, f2);
        int i5 = this.mMinuteHeight;
        float f3 = i3 - ((i5 * 4) / 5);
        float f4 = (i5 / 5) + i3;
        canvas.drawLine(f, f3, f, f4, this.mMinutePaint);
        canvas.drawCircle(f, f3, this.mMinuteWidth / 2, this.mMinutePaint);
        canvas.drawCircle(f, f4, this.mMinuteWidth / 2, this.mMinutePaint);
        canvas.restore();
        canvas.save();
        canvas.rotate(this.mHourDegree, f, f2);
        int i6 = this.mHourHeight;
        int i7 = i3 - ((i6 * 77) / 100);
        int i8 = i3 + ((i6 * 23) / 100);
        float f5 = i7;
        float f6 = i8;
        canvas.drawLine(f, f5, f, f6, this.mHourPaint);
        canvas.drawCircle(f, f5, this.mHourWidth / 2, this.mHourPaint);
        canvas.drawCircle(f, f6, this.mHourWidth / 2, this.mHourPaint);
        canvas.restore();
        canvas.drawCircle(f, f2, this.mCenterRadius, this.mCenterPaint);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:4:0x0005 A[PHI: r0
  0x0005: PHI (r0v14 float) = (r0v0 float), (r0v1 float) binds: [B:3:0x0003, B:6:0x000b] A[DONT_GENERATE, DONT_INLINE]] */
    public Integer evaluate(float f, int i, int i2) {
        float f2 = 0.0f;
        if (f < 0.0f) {
            f = f2;
        } else {
            f2 = 1.0f;
            if (f >= 1.0f) {
                f = f2;
            }
        }
        double d = f;
        float f3 = d >= 0.5d ? (float) (((double) 0.85f) + (((d - 0.5d) / 0.5d) * ((double) 0.14999998f))) : (float) ((d / 0.5d) * ((double) 0.85f));
        int i3 = (i >> 24) & 255;
        int i4 = (i >> 16) & 255;
        int i5 = (i >> 8) & 255;
        int i6 = i & 255;
        return Integer.valueOf(((i3 + ((int) ((((i2 >> 24) & 255) - i3) * f3))) << 24) | ((i4 + ((int) ((((i2 >> 16) & 255) - i4) * f3))) << 16) | ((i5 + ((int) ((((i2 >> 8) & 255) - i5) * f3))) << 8) | (i6 + ((int) (f3 * ((i2 & 255) - i6)))));
    }
}
