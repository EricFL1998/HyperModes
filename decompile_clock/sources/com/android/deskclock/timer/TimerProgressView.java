package com.android.deskclock.timer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.R;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class TimerProgressView extends View {
    public static final int CIRCLE_STATE_PAUSE = 2;
    public static final int CIRCLE_STATE_RUN = 1;
    public static final int CIRCLE_STATE_STOP = 0;
    public static final int CIRCLE_STATE_TIME_OFF = 3;
    private static String TAG = "DC:TimerProgressView";
    private ValueAnimator animator;
    private int interval;
    private long lastTime;
    private OnFinishListener listener;
    private DeskClockTabActivity mActivity;
    private Paint mCircleGonePaint;
    private Paint mCirclePaint;
    private float mCircleWidth;
    private float mCurrentDegree;
    private float mCurrentValue;
    private int mDragCircleState;
    private Paint mPointPaint;
    private long mRemainedValue;
    private long mTotalValue;
    private Paint mWhitePointPaint;
    private PaintFlagsDrawFilter paintFlagsDrawFilter;

    public interface OnFinishListener {
        void onFinish();
    }

    public TimerProgressView(Context context) {
        this(context, null);
    }

    public TimerProgressView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDragCircleState = 0;
        this.lastTime = 0L;
        this.interval = 30;
        init(context, attributeSet);
        initPaint();
    }

    public TimerProgressView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDragCircleState = 0;
        this.lastTime = 0L;
        this.interval = 30;
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.TimerProgress);
        this.mCircleWidth = typedArrayObtainStyledAttributes.getDimension(0, SizeUtils.dp2px(context, 4.0f));
        if (Util.isTinyScreen(getContext())) {
            this.mCircleWidth = SizeUtils.dp2px(context, 3.0f);
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    private void initPaint() {
        Paint paint = new Paint();
        this.mCirclePaint = paint;
        paint.setAntiAlias(true);
        this.mCirclePaint.setStyle(Paint.Style.STROKE);
        this.mCirclePaint.setStrokeWidth(this.mCircleWidth);
        Paint paint2 = new Paint();
        this.mCircleGonePaint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.mCircleGonePaint.setStrokeWidth(this.mCircleWidth);
        Paint paint3 = new Paint();
        this.mPointPaint = paint3;
        paint3.setAntiAlias(true);
        this.mPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        Paint paint4 = new Paint();
        this.mWhitePointPaint = paint4;
        paint4.setAntiAlias(true);
        this.mWhitePointPaint.setStyle(Paint.Style.STROKE);
        this.mWhitePointPaint.setStrokeWidth(SizeUtils.dp2px(getContext(), 4.0f));
        this.paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, 3);
    }

    public void setTotalValue(long j) {
        this.mTotalValue = j;
    }

    public void setCurrentValueValue(long j) {
        this.mCurrentValue = j;
    }

    public int getState() {
        return this.mDragCircleState;
    }

    public void setState(int i, long j, long j2) {
        this.mDragCircleState = i;
        this.mTotalValue = j2;
        this.mRemainedValue = j;
        this.mCurrentValue = j2 - j;
        if (i == 0) {
            release();
            return;
        }
        if (i == 1) {
            setDuration(j, new OnFinishListener() { // from class: com.android.deskclock.timer.TimerProgressView.1
                @Override // com.android.deskclock.timer.TimerProgressView.OnFinishListener
                public void onFinish() {
                    TimerProgressView.this.mCurrentDegree = 360.0f;
                    TimerProgressView.this.mTotalValue = 0L;
                }
            });
            return;
        }
        if (i == 2) {
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.pause();
                return;
            }
            return;
        }
        if (i != 3) {
            return;
        }
        if (j2 != 0) {
            this.mCurrentValue = j2;
            invalidate();
        }
        release();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        drawCircle(canvas);
    }

    private void drawCircle(Canvas canvas) {
        int iDp2px;
        float dimensionPixelOffset = this.mCircleWidth + getResources().getDimensionPixelOffset(R.dimen.timer_progress_circle_border_width);
        float width = getWidth() - dimensionPixelOffset;
        float height = getHeight() - dimensionPixelOffset;
        canvas.setDrawFilter(this.paintFlagsDrawFilter);
        this.mCirclePaint.setColor(getResources().getColor(R.color.timer_remain_circle_color));
        RectF rectF = new RectF(dimensionPixelOffset, dimensionPixelOffset, width, height);
        float f = (this.mCurrentValue * 360.0f) / this.mTotalValue;
        this.mCurrentDegree = f;
        if (f >= 357.0f) {
            canvas.drawArc(rectF, (-93.0f) - f, 0.0f, false, this.mCirclePaint);
        } else if (f >= 3.0f && f < 357.0f) {
            canvas.drawArc(rectF, (-93.0f) - f, (-357.0f) + f, false, this.mCirclePaint);
        } else {
            canvas.drawArc(rectF, (-93.0f) - f, -354.0f, false, this.mCirclePaint);
        }
        RectF rectF2 = new RectF(dimensionPixelOffset, dimensionPixelOffset, width, height);
        this.mCircleGonePaint.setColor(getResources().getColor(R.color.timer_past_circle_color));
        float f2 = (this.mCurrentValue * 360.0f) / this.mTotalValue;
        this.mCurrentDegree = f2;
        if (f2 >= 3.0f) {
            canvas.drawArc(rectF2, -90.0f, (-f2) + 3.0f, false, this.mCircleGonePaint);
        }
        this.mPointPaint.setColor(getContext().getResources().getColor(R.color.timer_process_pointer_color));
        float f3 = (width + dimensionPixelOffset) / 2.0f;
        canvas.rotate(-this.mCurrentDegree, f3, (height + dimensionPixelOffset) / 2.0f);
        if (PadAdapterUtil.IS_PAD && !this.mActivity.isInMultiWindowMode()) {
            iDp2px = SizeUtils.dp2px(getContext(), 4.0f);
        } else if (Util.isTinyScreen(getContext())) {
            iDp2px = SizeUtils.dp2px(getContext(), 2.65f);
        } else {
            iDp2px = SizeUtils.dp2px(getContext(), 3.5f);
        }
        canvas.drawCircle(f3, dimensionPixelOffset, iDp2px, this.mPointPaint);
    }

    public void setDuration(long j, OnFinishListener onFinishListener) {
        this.listener = onFinishListener;
        if (this.animator != null) {
            Log.d(TAG, "animator != null");
            this.animator.cancel();
        }
        Log.d(TAG, "animator = null duration :" + j + "  mTotalValue :" + this.mTotalValue);
        long j2 = this.mTotalValue;
        if (j != j2) {
            this.animator = ValueAnimator.ofFloat(0.0f, j);
        } else {
            this.animator = ValueAnimator.ofFloat(0.0f, j2);
        }
        final float f = this.mCurrentValue;
        this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.deskclock.timer.TimerProgressView.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                long jCurrentTimeMillis = System.currentTimeMillis();
                if (jCurrentTimeMillis - TimerProgressView.this.lastTime < TimerProgressView.this.interval) {
                    return;
                }
                TimerProgressView.this.lastTime = jCurrentTimeMillis;
                TimerProgressView.this.mCurrentValue = ((Float) valueAnimator.getAnimatedValue()).floatValue() + f;
                TimerProgressView.this.invalidate();
                if ((TimerProgressView.this.mTotalValue == TimerProgressView.this.mCurrentValue || TimerProgressView.this.mDragCircleState == 3 || TimerProgressView.this.mDragCircleState == 0) && TimerProgressView.this.listener != null) {
                    TimerProgressView.this.listener.onFinish();
                }
            }
        });
        this.animator.setInterpolator(new LinearInterpolator());
        this.animator.setDuration(j);
        this.animator.start();
    }

    public void release() {
        this.mCurrentDegree = 360.0f;
        this.mTotalValue = 0L;
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.end();
            this.animator.removeAllUpdateListeners();
            this.animator = null;
        }
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    public void initContext(DeskClockTabActivity deskClockTabActivity) {
        this.mActivity = deskClockTabActivity;
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.listener = onFinishListener;
    }

    public static class SizeUtils {
        public static int dp2px(Context context, float f) {
            return (int) (((double) (f * context.getResources().getDisplayMetrics().density)) + 0.5d);
        }

        static int px2dp(Context context, float f) {
            return (int) (((double) (f / context.getResources().getDisplayMetrics().density)) + 0.5d);
        }
    }
}
