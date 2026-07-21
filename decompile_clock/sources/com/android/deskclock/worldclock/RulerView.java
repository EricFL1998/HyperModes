package com.android.deskclock.worldclock;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import java.util.Collection;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.utils.EaseManager;
import miuix.springback.view.SpringBackLayout;

/* JADX INFO: loaded from: classes.dex */
public class RulerView extends View {
    private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 300;
    private static final int SIZE_UNSPECIFIED = -1;
    private final Scroller mAdjustScroller;
    private int mBackgroundColor;
    private Paint mBackgroundPaint;
    private float mCurrentOffset;
    private float mHeight;
    private float mIndicatorHeight;
    private int mInitValue;
    private float mLeftGap;
    private float mLineGap;
    private Paint mLinePaint;
    private int mNormalLineColor;
    private int mNormalTextColor;
    private float mRightGap;
    private float mRulerHeight;
    private int mShadowColor;
    private int mStrokeColor;
    private Paint mStrokePaint;
    private Paint mTextPaint;
    private float mTextSize;
    private int mThumbColor;
    private Paint mThumbPaint;
    private float mThumbWidth;
    private int mValue;
    private OnValueChangeListener mValueChangeListener;
    private float mWidth;
    ValueAnimator releaseAnim;
    private float widthOffset;

    public interface OnValueChangeListener {
        void onChangeStart();

        void onChangeStop();

        void onReleaseStop();

        void onReleaseValueChanged(RulerView rulerView, int i, int i2);

        void onValueChanged(RulerView rulerView, int i, int i2);
    }

    private void drawStroke(Canvas canvas) {
    }

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RulerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mNormalLineColor = 419430400;
        this.mNormalTextColor = 1275068416;
        this.mThumbColor = -13335809;
        this.mBackgroundColor = -986896;
        this.mShadowColor = 251658240;
        this.mStrokeColor = 520093696;
        this.mTextSize = 32.0f;
        this.mRulerHeight = 80.0f;
        this.mThumbWidth = 68.0f;
        float f = 80.0f / 2.0f;
        this.mLeftGap = f;
        this.mRightGap = f;
        this.mIndicatorHeight = 20.0f;
        this.mInitValue = -1;
        this.mNormalLineColor = context.getColor(R.color.ruler_view_normal_line_color);
        this.mNormalTextColor = context.getColor(R.color.ruler_view_normal_text_color);
        this.mThumbColor = context.getColor(R.color.ruler_view_thumb_color);
        this.mBackgroundColor = context.getColor(R.color.ruler_view_bg_color);
        this.mShadowColor = context.getColor(R.color.ruler_view_shadow_color);
        this.mStrokeColor = context.getColor(R.color.ruler_view_stroke_color);
        this.widthOffset = SizeUtils.dp2px(getContext(), 10.0f);
        this.mTextSize = context.getResources().getDimension(R.dimen.timezone_item_text_size);
        this.mRulerHeight = context.getResources().getDimension(R.dimen.timezone_item_ruler_height);
        this.mThumbWidth = context.getResources().getDimension(R.dimen.timezone_item_ruler_thumb_width);
        this.mIndicatorHeight = context.getResources().getDimension(R.dimen.timezone_item_ruler_indicator_height);
        float f2 = this.mRulerHeight;
        float f3 = (f2 / 2.0f) + this.widthOffset;
        this.mLeftGap = f3;
        this.mRightGap = f3;
        this.mHeight = f2;
        this.mWidth = -1.0f;
        Paint paint = new Paint();
        this.mTextPaint = paint;
        paint.setAntiAlias(true);
        this.mTextPaint.setTextAlign(Paint.Align.CENTER);
        this.mTextPaint.setTextSize(this.mTextSize);
        this.mTextPaint.setColor(this.mNormalTextColor);
        this.mTextPaint.setTypeface(MiuiFont.MI_PRO_DEMIBOLD);
        Paint paint2 = new Paint();
        this.mBackgroundPaint = paint2;
        paint2.setAntiAlias(true);
        this.mBackgroundPaint.setColor(this.mBackgroundColor);
        Paint paint3 = new Paint();
        this.mStrokePaint = paint3;
        paint3.setColor(this.mStrokeColor);
        this.mStrokePaint.setStyle(Paint.Style.STROKE);
        this.mStrokePaint.setStrokeWidth(0.5f);
        Paint paint4 = new Paint();
        this.mLinePaint = paint4;
        paint4.setAntiAlias(true);
        this.mLinePaint.setColor(this.mNormalLineColor);
        this.mLinePaint.setStrokeWidth(SizeUtils.dp2px(getContext(), 1.4f));
        Paint paint5 = new Paint();
        this.mThumbPaint = paint5;
        paint5.setAntiAlias(true);
        this.mThumbPaint.setColor(this.mThumbColor);
        this.mAdjustScroller = new Scroller(getContext(), new DecelerateInterpolator(2.5f));
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        setMeasuredDimension(resolveSizeAndStateRespectingMinSize((int) this.mWidth, getMeasuredWidth(), i), resolveSizeAndStateRespectingMinSize((int) this.mHeight, getMeasuredHeight(), i2));
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (z) {
            initializeSelectorWheel();
        }
    }

    private void initializeSelectorWheel() {
        float right = getRight() - getLeft();
        float f = this.mLeftGap;
        float f2 = ((right - f) - this.mRightGap) / 24.0f;
        this.mLineGap = f2;
        this.mCurrentOffset = (this.mInitValue * f2) + f;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(0.0f, SizeUtils.dp2px(getContext(), 20.0f));
        drawBackground(canvas);
        drawRuler(canvas);
        drawThumb(canvas);
        canvas.restore();
    }

    public void requestDisallowParentInterceptTouchEvent(boolean z) {
        ViewParent parent = getParent();
        parent.requestDisallowInterceptTouchEvent(z);
        while (parent != null) {
            if (parent instanceof SpringBackLayout) {
                ((SpringBackLayout) parent).internalRequestDisallowInterceptTouchEvent(z);
            }
            parent = parent.getParent();
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            ValueAnimator valueAnimator = this.releaseAnim;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            StatHelper.deskclockEvent(StatHelper.EVENT_SLIDE_RULER_VIEW_COUNT);
            OneTrackStatHelper.trackTriggerEvent(OneTrackStatHelper.CLOCK_ITEM_SLIDE_RULER);
            requestDisallowParentInterceptTouchEvent(true);
            if (!this.mAdjustScroller.isFinished()) {
                this.mAdjustScroller.forceFinished(true);
            }
            this.mCurrentOffset = checkedPosition(motionEvent.getX());
            invalidate();
            OnValueChangeListener onValueChangeListener = this.mValueChangeListener;
            if (onValueChangeListener != null) {
                onValueChangeListener.onChangeStart();
            }
            int i = (int) ((this.mCurrentOffset - this.mLeftGap) / this.mLineGap);
            int i2 = this.mValue;
            if (i != i2) {
                float f = i2;
                this.mValue = i;
                OnValueChangeListener onValueChangeListener2 = this.mValueChangeListener;
                if (onValueChangeListener2 != null) {
                    onValueChangeListener2.onValueChanged(this, (int) f, i);
                }
            }
        } else if (action == 1) {
            StatHelper.deskclockEvent(StatHelper.EVENT_TIMEZONE_RULER_VIEW_SLIDE);
            OnValueChangeListener onValueChangeListener3 = this.mValueChangeListener;
            if (onValueChangeListener3 != null) {
                onValueChangeListener3.onChangeStop();
            }
            releaseThumb();
        } else if (action == 2) {
            this.mCurrentOffset = checkedPosition(motionEvent.getX());
            invalidate();
            int i3 = (int) ((this.mCurrentOffset - this.mLeftGap) / this.mLineGap);
            int i4 = this.mValue;
            if (i3 != i4) {
                float f2 = i4;
                this.mValue = i3;
                OnValueChangeListener onValueChangeListener4 = this.mValueChangeListener;
                if (onValueChangeListener4 != null) {
                    onValueChangeListener4.onValueChanged(this, (int) f2, i3);
                }
            }
        }
        return true;
    }

    public void cancelInteraction() {
        ValueAnimator valueAnimator = this.releaseAnim;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (!this.mAdjustScroller.isFinished()) {
            this.mAdjustScroller.forceFinished(true);
        }
        OnValueChangeListener onValueChangeListener = this.mValueChangeListener;
        if (onValueChangeListener != null) {
            onValueChangeListener.onChangeStop();
            this.mValueChangeListener.onReleaseStop();
        }
        requestDisallowParentInterceptTouchEvent(false);
    }

    private void releaseThumb() {
        float f = this.mInitValue;
        float f2 = this.mLineGap;
        final float f3 = (f * f2) + this.mLeftGap;
        if (((int) f3) != ((int) this.mCurrentOffset) && f2 != 0.0f) {
            ValueAnimator valueAnimator = this.releaseAnim;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.mCurrentOffset, f3);
            this.releaseAnim = valueAnimatorOfFloat;
            valueAnimatorOfFloat.setDuration(300L);
            this.releaseAnim.setInterpolator(new DecelerateInterpolator(1.5f));
            this.releaseAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.deskclock.worldclock.RulerView.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    RulerView.this.mCurrentOffset = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                    RulerView.this.invalidate();
                    int i = (int) ((RulerView.this.mCurrentOffset - RulerView.this.mLeftGap) / RulerView.this.mLineGap);
                    if (i != RulerView.this.mValue) {
                        float f4 = RulerView.this.mValue;
                        RulerView.this.mValue = i;
                        if (RulerView.this.mValueChangeListener != null) {
                            OnValueChangeListener onValueChangeListener = RulerView.this.mValueChangeListener;
                            RulerView rulerView = RulerView.this;
                            onValueChangeListener.onReleaseValueChanged(rulerView, (int) f4, rulerView.mValue);
                        }
                    }
                }
            });
            this.releaseAnim.addListener(new Animator.AnimatorListener() { // from class: com.android.deskclock.worldclock.RulerView.2
                boolean isCancel = false;

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationRepeat(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (this.isCancel) {
                        return;
                    }
                    if (RulerView.this.mValueChangeListener != null) {
                        RulerView.this.mValueChangeListener.onReleaseStop();
                    }
                    RulerView.this.mCurrentOffset = f3;
                    RulerView.this.invalidate();
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    this.isCancel = true;
                }
            });
            this.releaseAnim.start();
            return;
        }
        OnValueChangeListener onValueChangeListener = this.mValueChangeListener;
        if (onValueChangeListener != null) {
            onValueChangeListener.onReleaseStop();
        }
        this.mCurrentOffset = f3;
        invalidate();
    }

    public void moveThumb(int i) {
        if (this.mInitValue != -1) {
            this.mInitValue = i;
            final float f = (i * this.mLineGap) + this.mLeftGap;
            Folme.useValue("moveThumb").setTo("rulerViewSlider", Float.valueOf(this.mCurrentOffset)).to("rulerViewSlider", Float.valueOf(f), new AnimConfig().setEase(EaseManager.getStyle(-2, 0.9f, 0.35f)).addListeners(new TransitionListener() { // from class: com.android.deskclock.worldclock.RulerView.3
                boolean isCancel = false;

                @Override // miuix.animation.listener.TransitionListener
                public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                    super.onUpdate(obj, collection);
                    RulerView.this.mCurrentOffset = UpdateInfo.findByName(collection, "rulerViewSlider").getFloatValue();
                    RulerView.this.invalidate();
                }

                @Override // miuix.animation.listener.TransitionListener
                public void onComplete(Object obj) {
                    if (this.isCancel) {
                        return;
                    }
                    RulerView.this.mCurrentOffset = f;
                    RulerView.this.invalidate();
                }

                @Override // miuix.animation.listener.TransitionListener
                public void onCancel(Object obj) {
                    this.isCancel = true;
                }
            }));
        }
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mAdjustScroller.isFinished()) {
            return;
        }
        this.mAdjustScroller.computeScrollOffset();
        this.mCurrentOffset = this.mAdjustScroller.getCurrX();
        invalidate();
    }

    public void setValue(int i) {
        this.mValue = i;
        int i2 = this.mInitValue;
        if (i2 != -1 && i2 != i) {
            this.mInitValue = i;
            this.mCurrentOffset = (i * this.mLineGap) + this.mLeftGap;
            invalidate();
            return;
        }
        this.mInitValue = i;
    }

    public int getValue() {
        return this.mValue;
    }

    private void drawBackground(Canvas canvas) {
        float f = this.mRulerHeight / 2.0f;
        this.mBackgroundPaint.setColor(this.mBackgroundColor);
        this.mBackgroundPaint.setShadowLayer(16.0f, 0.0f, 8.0f, this.mShadowColor);
        canvas.drawRoundRect(new RectF(this.widthOffset, 0.0f, getWidth() - this.widthOffset, this.mHeight), f, f, this.mBackgroundPaint);
        canvas.drawRoundRect(new RectF(this.widthOffset, 1.0f, getWidth() - this.widthOffset, this.mHeight - 1.0f), f, f, this.mStrokePaint);
    }

    private void drawRuler(Canvas canvas) {
        float f = this.mRulerHeight;
        float f2 = f + 0.0f;
        float f3 = (f - this.mIndicatorHeight) / 2.0f;
        float f4 = this.mLeftGap;
        int i = 0;
        for (int i2 = 0; i2 <= 24; i2++) {
            float f5 = f4 + (this.mLineGap * i2);
            if (i % 6 == 0) {
                Paint.FontMetricsInt fontMetricsInt = this.mTextPaint.getFontMetricsInt();
                canvas.drawText(formatNumber(i2), f5, (((this.mRulerHeight - fontMetricsInt.descent) - fontMetricsInt.ascent) - 2.0f) / 2.0f, this.mTextPaint);
            } else {
                this.mLinePaint.setStrokeCap(Paint.Cap.ROUND);
                canvas.drawLine(f5, 0.0f + f3, f5, f2 - f3, this.mLinePaint);
            }
            i++;
        }
    }

    private void drawThumb(Canvas canvas) {
        float f = this.mRulerHeight + 0.0f;
        float f2 = (int) this.mCurrentOffset;
        float f3 = (f + 0.0f) / 2.0f;
        float f4 = this.mThumbWidth / 2.0f;
        this.mThumbPaint.setColor(this.mThumbColor);
        canvas.drawCircle(f2, f3, f4, this.mThumbPaint);
        this.mThumbPaint.setColor(-1);
        canvas.drawCircle(f2, f3, f4 / 2.78f, this.mThumbPaint);
    }

    private float checkedPosition(float f) {
        float f2 = this.mLeftGap;
        if (f < f2) {
            f = f2;
        }
        return f > ((float) getWidth()) - this.mRightGap ? getWidth() - this.mRightGap : f;
    }

    private int resolveSizeAndStateRespectingMinSize(int i, int i2, int i3) {
        return i != -1 ? resolveSizeAndState(Math.max(i, i2), i3, 0) : i2;
    }

    private String formatNumber(int i) {
        if (i < 10) {
            return WorldClockEditActivity.LOCAL_CITY_ID + String.valueOf(i);
        }
        return String.valueOf(i);
    }

    public void setOnSeekBarChangeListener(OnValueChangeListener onValueChangeListener) {
        this.mValueChangeListener = onValueChangeListener;
    }

    private static class SizeUtils {
        private SizeUtils() {
        }

        static int dp2px(Context context, float f) {
            return (int) (((double) (f * context.getResources().getDisplayMetrics().density)) + 0.5d);
        }

        static int px2dp(Context context, float f) {
            return (int) (((double) (f / context.getResources().getDisplayMetrics().density)) + 0.5d);
        }
    }
}
