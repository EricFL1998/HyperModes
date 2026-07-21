package com.android.deskclock.worldclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import com.android.deskclock.R;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class LocalClockView extends View {
    private static String TAG = "DC:LocalClockView";
    private static final int TIME_ENTER_DAY = 6;
    private static final int TIME_ENTER_NIGHT = 18;
    private int[] highLightColors1;
    private int[] highLightColors2;
    private Calendar mCalender;
    private Paint mCenterPointerPaint;
    private int mCenterX;
    private int mCenterY;
    private float mClockRadius;
    private float mHWidth;
    private int mHeight;
    private Paint mHighLightPaint;
    private float mHour;
    private Paint mHourPointerPaint;
    private float mMWidth;
    private Paint mMinPointerPaint;
    private float mMinute;
    private float mSTailWidth;
    private float mSWidth;
    private float mSecond;
    private Paint mSedPointerPaint;
    private boolean mSunRiseMode;
    private int mWidth;

    public LocalClockView(Context context) {
        super(context);
        this.mSunRiseMode = true;
    }

    public LocalClockView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSunRiseMode = true;
        init(context, attributeSet);
        initPaint();
    }

    public void setTime(Calendar calendar) {
        if (calendar == null) {
            this.mCalender = Calendar.getInstance();
        } else {
            this.mCalender = calendar;
        }
        int i = this.mCalender.get(10);
        int i2 = this.mCalender.get(12);
        int i3 = this.mCalender.get(13);
        if (i >= 24 || i < 0 || i2 >= 60 || i2 < 0 || i3 >= 60 || i3 < 0) {
            Log.d(TAG, "setTime is error");
            return;
        }
        if (i >= 12) {
            this.mHour = (((i + ((i2 * 1.0f) / 60.0f)) + ((i3 * 1.0f) / 3600.0f)) - 12.0f) * 30.0f;
        } else {
            this.mHour = (i + ((i2 * 1.0f) / 60.0f) + ((i3 * 1.0f) / 3600.0f)) * 30.0f;
        }
        float f = i2;
        float f2 = i3;
        this.mMinute = (f + ((1.0f * f2) / 60.0f)) * 6.0f;
        this.mSecond = f2 * 6.0f;
        int i4 = this.mCalender.get(11);
        if (i4 >= 18 || i4 < 6) {
            this.mSunRiseMode = false;
        } else {
            this.mSunRiseMode = true;
        }
    }

    public void updateClockView(Calendar calendar) {
        setTime(calendar);
        if (this.mSecond == 360.0f) {
            this.mSecond = 0.0f;
        }
        if (this.mMinute == 360.0f) {
            this.mMinute = 0.0f;
        }
        if (this.mHour == 360.0f) {
            this.mHour = 0.0f;
        }
        this.mMinute += 0.1f;
        this.mHour += 0.008333334f;
        postInvalidate();
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.Clock);
        this.mHWidth = typedArrayObtainStyledAttributes.getDimension(3, 5.4f);
        this.mMWidth = typedArrayObtainStyledAttributes.getDimension(4, SizeUtils.dp2px(context, 4.4f));
        this.mSWidth = typedArrayObtainStyledAttributes.getDimension(6, SizeUtils.dp2px(context, 10.0f));
        this.mSTailWidth = typedArrayObtainStyledAttributes.getDimension(5, SizeUtils.dp2px(context, 4.0f));
        typedArrayObtainStyledAttributes.recycle();
    }

    private void initPaint() {
        Paint paint = new Paint();
        this.mHourPointerPaint = paint;
        paint.setAntiAlias(true);
        this.mHourPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mHourPointerPaint.setStrokeCap(Paint.Cap.ROUND);
        Paint paint2 = new Paint();
        this.mMinPointerPaint = paint2;
        paint2.setAntiAlias(true);
        this.mMinPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mMinPointerPaint.setStrokeCap(Paint.Cap.ROUND);
        Paint paint3 = new Paint();
        this.mSedPointerPaint = paint3;
        paint3.setAntiAlias(true);
        this.mSedPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mSedPointerPaint.setStrokeCap(Paint.Cap.ROUND);
        Paint paint4 = new Paint();
        this.mHighLightPaint = paint4;
        paint4.setAntiAlias(true);
        this.mHighLightPaint.setStyle(Paint.Style.STROKE);
        Paint paint5 = new Paint();
        this.mCenterPointerPaint = paint5;
        paint5.setAntiAlias(true);
        this.mCenterPointerPaint.setStyle(Paint.Style.FILL);
        this.mCenterPointerPaint.setStrokeCap(Paint.Cap.ROUND);
        this.highLightColors1 = new int[]{getContext().getResources().getColor(R.color.clock_hour_hight_light_1), getContext().getResources().getColor(R.color.clock_hour_hight_light_2), getContext().getResources().getColor(R.color.clock_hour_hight_light_2), getContext().getResources().getColor(R.color.clock_hour_hight_light_1)};
        this.highLightColors2 = new int[]{0, getContext().getResources().getColor(R.color.clock_view_highlight_pointer_9), getContext().getResources().getColor(R.color.clock_view_highlight_pointer_53), -1};
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(getMeasureSize(true, i), getMeasureSize(false, i2));
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mWidth = i;
        this.mHeight = i2;
        this.mClockRadius = getResources().getDimension(R.dimen.clock_view_radius);
        if (Util.isFoldDevice(getContext()) && Util.isWideMode(getContext())) {
            this.mClockRadius = getResources().getDimension(R.dimen.clock_view_radius_fold);
        }
        this.mCenterX = i / 2;
        this.mCenterY = (int) (((this.mClockRadius * 2.0f) + 30.0f) / 2.0f);
    }

    private int getMeasureSize(boolean z, int i) {
        int size = View.MeasureSpec.getSize(i);
        int mode = View.MeasureSpec.getMode(i);
        if (mode == Integer.MIN_VALUE) {
            if (z) {
                return Math.min(size, this.mWidth);
            }
            return Math.min(size, this.mHeight);
        }
        if (mode != 0) {
            if (mode != 1073741824) {
                return 0;
            }
            return size;
        }
        if (z) {
            return getSuggestedMinimumWidth();
        }
        return getSuggestedMinimumHeight();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(this.mCenterX, this.mCenterY);
        drawPointer(canvas);
    }

    private void drawPointer(Canvas canvas) {
        int color = getResources().getColor(R.color.clock_hour_color);
        int color2 = getResources().getColor(R.color.hour_pointer_color);
        if (!this.mSunRiseMode) {
            color = getResources().getColor(R.color.clock_hour_color_sunset);
            color2 = getResources().getColor(R.color.hour_pointer_color_sunset);
        }
        int i = color;
        int i2 = color2;
        canvas.save();
        canvas.rotate(this.mHour, 0.0f, 0.0f);
        this.mHourPointerPaint.setColor(i2);
        this.mHourPointerPaint.setStrokeWidth(this.mHWidth);
        this.mHourPointerPaint.setShadowLayer(10.0f, ((float) (((double) SizeUtils.dp2px(getContext(), 4.0f)) * Math.sin(Math.toRadians(this.mHour)))) - SizeUtils.dp2px(getContext(), 1.0f), (float) (((double) SizeUtils.dp2px(getContext(), 4.0f)) * Math.cos(Math.toRadians(this.mHour))), getResources().getColor(R.color.clock_hour_inner_shadow));
        canvas.drawLine(0.0f, 0.0f, 0.0f, -((float) (((double) this.mClockRadius) * 0.6d)), this.mHourPointerPaint);
        this.mHourPointerPaint.setShadowLayer(10.0f, ((float) (((double) SizeUtils.dp2px(getContext(), 4.0f)) * Math.sin(Math.toRadians(this.mHour)))) + SizeUtils.dp2px(getContext(), 1.0f), (float) (((double) SizeUtils.dp2px(getContext(), 4.0f)) * Math.cos(Math.toRadians(this.mHour))), getResources().getColor(R.color.clock_hour_inner_shadow));
        canvas.drawLine(0.0f, 0.0f, 0.0f, -((float) (((double) this.mClockRadius) * 0.6d)), this.mHourPointerPaint);
        canvas.restore();
        canvas.save();
        canvas.rotate(this.mMinute, 0.0f, 0.0f);
        this.mMinPointerPaint.setColor(i2);
        this.mMinPointerPaint.setStrokeWidth(this.mMWidth);
        this.mMinPointerPaint.setShadowLayer(10.0f, ((float) (((double) SizeUtils.dp2px(getContext(), 4.0f)) * Math.sin(Math.toRadians(this.mMinute)))) - SizeUtils.dp2px(getContext(), 1.0f), (float) (((double) SizeUtils.dp2px(getContext(), 4.0f)) * Math.cos(Math.toRadians(this.mMinute))), getResources().getColor(R.color.clock_hour_inner_shadow));
        canvas.drawLine(0.0f, 0.0f, 0.0f, -((float) (((double) this.mClockRadius) * 0.93d)), this.mMinPointerPaint);
        this.mMinPointerPaint.setShadowLayer(10.0f, ((float) (((double) SizeUtils.dp2px(getContext(), 4.0f)) * Math.sin(Math.toRadians(this.mMinute)))) + SizeUtils.dp2px(getContext(), 1.0f), (float) (((double) SizeUtils.dp2px(getContext(), 4.0f)) * Math.cos(Math.toRadians(this.mMinute))), getResources().getColor(R.color.clock_hour_inner_shadow));
        canvas.drawLine(0.0f, 0.0f, 0.0f, -((float) (((double) this.mClockRadius) * 0.93d)), this.mMinPointerPaint);
        canvas.restore();
        if (this.mSunRiseMode) {
            drawHighLinePointer(canvas, this.mHour, 4.0f, (float) (((double) this.mClockRadius) * 0.6d), this.mHWidth);
            drawHighLinePointer(canvas, this.mMinute, 2.7f, (float) (((double) this.mClockRadius) * 0.93d), this.mMWidth);
        }
        this.mCenterPointerPaint.setColor(i2);
        canvas.drawCircle(0.0f, 0.0f, SizeUtils.dp2px(getContext(), 5.0f), this.mCenterPointerPaint);
        this.mCenterPointerPaint.setShadowLayer(10.0f, 0.0f, SizeUtils.dp2px(getContext(), 4.0f), getResources().getColor(R.color.clock_hour_inner_shadow));
        if (this.mSunRiseMode) {
            this.mCenterPointerPaint.setColor(getContext().getColor(R.color.clockview_second_pointer_color));
            this.mSedPointerPaint.setColor(getContext().getResources().getColor(R.color.clockview_second_pointer_color));
        } else {
            this.mCenterPointerPaint.setColor(getContext().getColor(R.color.clockview_second_pointer_color_sunset));
            this.mSedPointerPaint.setColor(getContext().getResources().getColor(R.color.clockview_second_pointer_color_sunset));
        }
        canvas.drawCircle(0.0f, 0.0f, SizeUtils.dp2px(getContext(), 4.0f), this.mCenterPointerPaint);
        canvas.save();
        this.mSedPointerPaint.setStrokeWidth(this.mSWidth);
        canvas.rotate(this.mSecond, 0.0f, 0.0f);
        this.mSedPointerPaint.setShadowLayer(10.0f, ((float) (((double) SizeUtils.dp2px(getContext(), 4.0f)) * Math.sin(Math.toRadians(this.mSecond)))) - SizeUtils.dp2px(getContext(), 1.0f), (float) (((double) SizeUtils.dp2px(getContext(), 3.0f)) * Math.cos(Math.toRadians(this.mSecond))), this.mSunRiseMode ? getResources().getColor(R.color.clockview_second_inner_shadow) : getResources().getColor(R.color.clockview_second_inner_shadow_sunset));
        canvas.drawLine(0.0f, 40.0f, 0.0f, -((float) (((double) this.mClockRadius) * 0.94d)), this.mSedPointerPaint);
        this.mSedPointerPaint.setStrokeWidth(this.mSTailWidth);
        canvas.drawLine(0.0f, 35.0f, 0.0f, 85.0f, this.mSedPointerPaint);
        this.mSedPointerPaint.setStrokeWidth(this.mSWidth);
        this.mSedPointerPaint.setShadowLayer(10.0f, ((float) (((double) SizeUtils.dp2px(getContext(), 4.0f)) * Math.sin(Math.toRadians(this.mSecond)))) + SizeUtils.dp2px(getContext(), 1.0f), (float) (((double) SizeUtils.dp2px(getContext(), 3.0f)) * Math.cos(Math.toRadians(this.mSecond))), this.mSunRiseMode ? getResources().getColor(R.color.clockview_second_inner_shadow) : getResources().getColor(R.color.clockview_second_inner_shadow_sunset));
        canvas.drawLine(0.0f, 40.0f, 0.0f, -((float) (((double) this.mClockRadius) * 0.94d)), this.mSedPointerPaint);
        this.mSedPointerPaint.setStrokeWidth(this.mSTailWidth);
        canvas.drawLine(0.0f, 35.0f, 0.0f, 85.0f, this.mSedPointerPaint);
        canvas.restore();
        this.mCenterPointerPaint.setColor(i);
        canvas.drawCircle(0.0f, 0.0f, SizeUtils.dp2px(getContext(), 2.0f), this.mCenterPointerPaint);
    }

    private void drawHighLinePointer(Canvas canvas, float f, float f2, float f3, float f4) {
        canvas.save();
        canvas.rotate(f, 0.0f, 0.0f);
        if (f > 10.0f && f <= 180.0f) {
            this.mHighLightPaint.setStrokeWidth(1.5f);
            float f5 = -f2;
            float f6 = -f3;
            float f7 = f6 * 0.1f;
            float f8 = f6 * 0.9f;
            this.mHighLightPaint.setShader(new LinearGradient(f5, f7, f5, f8, this.highLightColors1, new float[]{0.0f, 0.3f, 0.6f, 1.0f}, Shader.TileMode.CLAMP));
            canvas.drawLine(f5, f7, f5, f8, this.mHighLightPaint);
        } else if (f > 180.0f && f < 350.0f) {
            this.mHighLightPaint.setStrokeWidth(1.5f);
            float f9 = -f3;
            float f10 = f9 * 0.1f;
            float f11 = f9 * 0.9f;
            this.mHighLightPaint.setShader(new LinearGradient(f2, f10, f2, f11, this.highLightColors1, new float[]{0.0f, 0.3f, 0.6f, 1.0f}, Shader.TileMode.CLAMP));
            canvas.drawLine(f2, f10, f2, f11, this.mHighLightPaint);
        } else {
            float f12 = f4 / 3.0f;
            this.mHighLightPaint.setStrokeWidth(f4 / 6.0f);
            float f13 = -f3;
            float f14 = f13 + ((3.0f * f12) / 2.0f);
            float f15 = f13 - (f12 / 2.0f);
            this.mHighLightPaint.setShader(new LinearGradient(0.0f, f14, 0.0f, f15, this.highLightColors2, new float[]{0.0f, 0.5f, 0.6f, 1.0f}, Shader.TileMode.CLAMP));
            canvas.drawArc(-f12, f14, f12, f15, 0.0f, -360.0f, false, this.mHighLightPaint);
        }
        canvas.restore();
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
