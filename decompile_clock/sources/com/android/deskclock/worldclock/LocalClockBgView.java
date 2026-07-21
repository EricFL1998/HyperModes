package com.android.deskclock.worldclock;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFont;
import com.android.deskclock.util.Util;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class LocalClockBgView extends View {
    private static String TAG = "DC:LocalClockView";
    private static final int TIME_ENTER_DAY = 6;
    private static final int TIME_ENTER_NIGHT = 18;
    private Bitmap mBgLocalClock;
    private Bitmap mBgLocalClockSunset;
    private Paint mBorderPaint;
    private Calendar mCalender;
    private int mCenterX;
    private int mCenterY;
    private Paint mCirclePaint;
    private float mClockRadius;
    private float mClockRingWidth;
    private float mDefaultLength;
    private float mDefaultWidth;
    private int mHeight;
    private Paint mNumPaint;
    private Rect mRect;
    private Paint mScalePaint;
    private Paint mShadowPaint;
    private float mSpecialLength;
    private float mSpecialWidth;
    private boolean mSunRiseMode;
    private int mWidth;

    public LocalClockBgView(Context context) {
        super(context);
        this.mSunRiseMode = true;
    }

    public LocalClockBgView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSunRiseMode = true;
        init(context, attributeSet);
        initBgLocalClock();
        initPaint();
    }

    private void initBgLocalClock() {
        Resources resources = getResources();
        float f = this.mClockRadius;
        this.mBgLocalClock = decodeSampledBitmapFromResource(resources, R.drawable.clockbg, (int) (f * 2.0f), (int) (f * 2.0f));
        Resources resources2 = getResources();
        float f2 = this.mClockRadius;
        this.mBgLocalClockSunset = decodeSampledBitmapFromResource(resources2, R.drawable.clockbg_sunset, (int) (f2 * 2.0f), (int) (f2 * 2.0f));
    }

    private Bitmap decodeSampledBitmapFromResource(Resources resources, int i, int i2, int i3) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        miuix.graphics.BitmapFactory.decodeResource(resources, i, options);
        options.inSampleSize = Util.calculateInSampleSize(options, i2, i3);
        options.inJustDecodeBounds = false;
        return miuix.graphics.BitmapFactory.decodeResource(resources, i, options);
    }

    public void setTime(Calendar calendar) {
        if (calendar == null) {
            this.mCalender = Calendar.getInstance();
        } else {
            this.mCalender = calendar;
        }
        int i = this.mCalender.get(11);
        if (i >= 18 || i < 6) {
            this.mSunRiseMode = false;
        } else {
            this.mSunRiseMode = true;
        }
    }

    public void updateClockBgView(Calendar calendar) {
        setTime(calendar);
        postInvalidate();
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.Clock);
        boolean z = false;
        this.mClockRingWidth = typedArrayObtainStyledAttributes.getDimension(0, SizeUtils.dp2px(context, 4.0f));
        this.mDefaultWidth = typedArrayObtainStyledAttributes.getDimension(2, SizeUtils.dp2px(context, 50.0f));
        this.mDefaultLength = typedArrayObtainStyledAttributes.getDimension(1, context.getResources().getDimension(R.dimen.world_clock_default_width));
        this.mSpecialWidth = typedArrayObtainStyledAttributes.getDimension(8, context.getResources().getDimension(R.dimen.world_clock_special_width));
        this.mSpecialLength = typedArrayObtainStyledAttributes.getDimension(7, SizeUtils.dp2px(context, 30.0f));
        if (this.mCalender == null) {
            Calendar calendar = Calendar.getInstance();
            this.mCalender = calendar;
            int i = calendar.get(11);
            if (i < 18 && i >= 6) {
                z = true;
            }
            this.mSunRiseMode = z;
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    private void initPaint() {
        Paint paint = new Paint();
        this.mCirclePaint = paint;
        paint.setAntiAlias(true);
        this.mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        Paint paint2 = new Paint();
        this.mShadowPaint = paint2;
        paint2.setAntiAlias(true);
        Paint paint3 = new Paint();
        this.mBorderPaint = paint3;
        paint3.setAntiAlias(true);
        this.mBorderPaint.setStyle(Paint.Style.STROKE);
        Paint paint4 = new Paint();
        this.mScalePaint = paint4;
        paint4.setAntiAlias(true);
        this.mScalePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mScalePaint.setStrokeCap(Paint.Cap.ROUND);
        Paint paint5 = new Paint();
        this.mNumPaint = paint5;
        paint5.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mNumPaint.setTypeface(MiuiFont.MI_TYPE_MONO_BOLD);
        this.mNumPaint.setTextSize(getResources().getDimension(R.dimen.clock_view_textSize));
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
        float f = this.mClockRadius;
        this.mRect = new Rect(-((int) f), -((int) f), (int) f, (int) f);
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
        drawCircle(canvas);
        drawScale(canvas);
        drawNums(canvas);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Bitmap bitmap = this.mBgLocalClock;
        if (bitmap != null && !bitmap.isRecycled()) {
            this.mBgLocalClock.recycle();
        }
        Bitmap bitmap2 = this.mBgLocalClockSunset;
        if (bitmap2 == null || bitmap2.isRecycled()) {
            return;
        }
        this.mBgLocalClockSunset.recycle();
    }

    private void drawCircle(Canvas canvas) {
        int color = getResources().getColor(R.color.clock_drop_shadow_1);
        int color2 = getResources().getColor(R.color.clock_drop_shadow_2);
        if (!this.mSunRiseMode) {
            color = getResources().getColor(R.color.clock_drop_shadow_sunset_1);
            color2 = getResources().getColor(R.color.clock_drop_shadow_sunset_2);
        }
        this.mShadowPaint.setStyle(Paint.Style.FILL);
        this.mShadowPaint.setShadowLayer(SizeUtils.dp2px(getContext(), Util.isPadOrientationLand(getContext()) ? 8.0f : 36.0f), 0.0f, SizeUtils.dp2px(getContext(), 26.0f), color2);
        canvas.drawCircle(0.0f, 0.0f, this.mClockRadius - SizeUtils.dp2px(getContext(), 1.0f), this.mShadowPaint);
        this.mShadowPaint.setShadowLayer(SizeUtils.dp2px(getContext(), Util.isPadOrientationLand(getContext()) ? 8.0f : 22.0f), 0.0f, SizeUtils.dp2px(getContext(), 12.0f), color);
        canvas.drawCircle(0.0f, 0.0f, this.mClockRadius - SizeUtils.dp2px(getContext(), 1.0f), this.mShadowPaint);
        canvas.drawBitmap(this.mSunRiseMode ? this.mBgLocalClock : this.mBgLocalClockSunset, (Rect) null, this.mRect, this.mCirclePaint);
    }

    private void drawScale(Canvas canvas) {
        int color = getResources().getColor(R.color.clock_scale_color);
        int color2 = getResources().getColor(R.color.clock_scale_normal_color);
        if (!this.mSunRiseMode) {
            color = getResources().getColor(R.color.clock_scale_color_sunset);
            color2 = getResources().getColor(R.color.clock_scale_normal_color_sunset);
        }
        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) {
                this.mScalePaint.setStrokeWidth(this.mSpecialWidth);
                this.mScalePaint.setColor(color);
                if (Util.isDeviceN85OrN85X()) {
                    float f = this.mClockRadius;
                    canvas.drawLine(0.0f, (-f) + (this.mClockRingWidth / 2.0f) + 6.0f, 0.0f, (-f) + this.mDefaultLength + 8.0f, this.mScalePaint);
                } else {
                    float f2 = this.mClockRadius;
                    canvas.drawLine(0.0f, (-f2) + (this.mClockRingWidth / 2.0f) + 15.0f, 0.0f, (-f2) + this.mDefaultLength + 17.0f, this.mScalePaint);
                }
            } else {
                this.mScalePaint.setStrokeWidth(this.mDefaultWidth);
                this.mScalePaint.setColor(color2);
                if (Util.isDeviceN85OrN85X()) {
                    float f3 = this.mClockRadius;
                    canvas.drawLine(0.0f, (-f3) + (this.mClockRingWidth / 2.0f) + 6.0f, 0.0f, (-f3) + this.mDefaultLength + 8.0f, this.mScalePaint);
                } else {
                    float f4 = this.mClockRadius;
                    canvas.drawLine(0.0f, (-f4) + (this.mClockRingWidth / 2.0f) + 15.0f, 0.0f, (-f4) + this.mDefaultLength + 17.0f, this.mScalePaint);
                }
            }
            canvas.rotate(6.0f);
        }
    }

    private void drawNums(Canvas canvas) {
        int color = getResources().getColor(R.color.clock_number_color);
        if (!this.mSunRiseMode) {
            color = getResources().getColor(R.color.clock_number_color_sunset);
        }
        for (int i = 0; i < 12; i++) {
            canvas.save();
            Rect rect = new Rect();
            canvas.translate(0.0f, (-this.mClockRadius) + this.mSpecialLength + this.mDefaultLength + this.mClockRingWidth + SizeUtils.dp2px(getContext(), 3.5f));
            if (i == 0) {
                this.mNumPaint.getTextBounds("12", 0, 2, rect);
                this.mNumPaint.setColor(color);
                canvas.drawText("12", (-rect.width()) / 2, (rect.height() / 2) + 5, this.mNumPaint);
            } else {
                String str = i + "";
                this.mNumPaint.getTextBounds(str, 0, str.length(), rect);
                this.mNumPaint.setColor(color);
                canvas.rotate((-i) * 30);
                if (i == 6) {
                    canvas.drawText(str, (-rect.width()) / 2, (rect.height() / 2) - 2, this.mNumPaint);
                } else {
                    canvas.drawText(str, (-rect.width()) / 2, rect.height() / 2, this.mNumPaint);
                }
            }
            canvas.restore();
            canvas.rotate(30.0f);
        }
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
