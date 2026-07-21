package com.android.deskclock.timer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.R;
import com.android.deskclock.util.PadAdapterUtil;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class TimerProgressBgView extends FrameLayout {
    private static String TAG = "DC:TimerProgressBgView";
    private LinearGradient gradient;
    private DeskClockTabActivity mActivity;
    private float mBgRadius;
    private Paint mBottomCirclePaint;
    private float mCircleWidth;
    private Paint mShadowPaint;

    public TimerProgressBgView(Context context) {
        this(context, null);
    }

    public TimerProgressBgView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TimerProgressBgView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet);
        initPaint();
        setWillNotDraw(false);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.TimerProgress);
        this.mCircleWidth = typedArrayObtainStyledAttributes.getDimension(0, SizeUtils.dp2px(context, 5.0f));
        this.mBgRadius = typedArrayObtainStyledAttributes.getDimension(1, SizeUtils.dp2px(context, 152.0f));
        typedArrayObtainStyledAttributes.recycle();
    }

    private void initPaint() {
        Paint paint = new Paint();
        this.mBottomCirclePaint = paint;
        paint.setAntiAlias(true);
        this.mBottomCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mBottomCirclePaint.setStrokeWidth(1.0f);
        this.mBottomCirclePaint.setDither(true);
        this.mShadowPaint = new Paint();
    }

    public void setProgressBgRadius(float f) {
        this.mBgRadius = f;
        requestLayout();
        invalidate();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            View childAt = getChildAt(i3);
            int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (this.mBgRadius * 2.0f), BasicMeasure.EXACTLY);
            childAt.measure(iMakeMeasureSpec, iMakeMeasureSpec);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas, getWidth() / 2, this.mBgRadius);
    }

    private void drawCircle(Canvas canvas, int i, float f) {
        float f2 = i;
        float paddingTop = getPaddingTop() + f;
        if (!Util.isNightMode(getContext())) {
            this.mShadowPaint.setStyle(Paint.Style.FILL);
            if (!PadAdapterUtil.IS_PAD && !Util.isTinyScreen(getContext())) {
                this.mShadowPaint.setShadowLayer(SizeUtils.dp2px(getContext(), 60.0f), 0.0f, SizeUtils.dp2px(getContext(), 50.0f), getResources().getColor(R.color.timer_drop_shadow_5));
            } else if (Util.isPadOrientationLand(getContext())) {
                this.mShadowPaint.setShadowLayer(SizeUtils.dp2px(getContext(), 50.0f), 0.0f, SizeUtils.dp2px(getContext(), 20.0f), getResources().getColor(R.color.timer_drop_shadow_5));
            } else if (Util.isTinyScreen(getContext())) {
                this.mShadowPaint.setShadowLayer(SizeUtils.dp2px(getContext(), 23.0f), 0.0f, SizeUtils.dp2px(getContext(), 20.0f), getResources().getColor(R.color.timer_drop_shadow_5));
            } else {
                this.mShadowPaint.setShadowLayer(SizeUtils.dp2px(getContext(), 60.0f), 0.0f, SizeUtils.dp2px(getContext(), 40.0f), getResources().getColor(R.color.timer_drop_shadow_5));
            }
            canvas.drawCircle(f2, paddingTop, f, this.mShadowPaint);
            if (Util.isTinyScreen(getContext())) {
                this.mShadowPaint.setShadowLayer(SizeUtils.dp2px(getContext(), 20.0f), 0.0f, SizeUtils.dp2px(getContext(), 10.0f), getResources().getColor(R.color.timer_drop_shadow_5));
            } else {
                this.mShadowPaint.setShadowLayer(SizeUtils.dp2px(getContext(), 34.0f), 0.0f, SizeUtils.dp2px(getContext(), 30.0f), getResources().getColor(R.color.timer_drop_shadow_4));
            }
            canvas.drawCircle(f2, paddingTop, f, this.mShadowPaint);
        }
        LinearGradient linearGradient = new LinearGradient(0.0f, paddingTop - f, 0.0f, paddingTop + f, getResources().getColor(R.color.timer_linear_from), getResources().getColor(R.color.timer_linear_to), Shader.TileMode.CLAMP);
        this.gradient = linearGradient;
        this.mBottomCirclePaint.setShader(linearGradient);
        canvas.drawCircle(f2, paddingTop, f, this.mBottomCirclePaint);
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    public void initContext(DeskClockTabActivity deskClockTabActivity) {
        this.mActivity = deskClockTabActivity;
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
