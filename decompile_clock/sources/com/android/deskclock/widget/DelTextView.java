package com.android.deskclock.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.view.TypefaceTextView;

/* JADX INFO: loaded from: classes.dex */
public class DelTextView extends TypefaceTextView {
    private static final float SHADOW_RADIUS = 5.45f;
    private static final float X_OFFSET = 0.0f;
    private static final float Y_OFFSET = 5.45f;
    private int mBgColor;
    private int mBgColorPress;
    private int mHeight;
    private Paint mPaintBg;
    private Paint mPaintShadow;
    private int mRadius;
    private int mShadowColor;
    private int mShadowOffsetX;
    private int mShadowOffsetY;
    private int mShadowWidth;
    private int mWidth;

    public DelTextView(Context context) {
        super(context);
        this.mShadowColor = 218103808;
        this.mBgColor = -1;
        this.mBgColorPress = 117440512;
        init();
    }

    public DelTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mShadowColor = 218103808;
        this.mBgColor = -1;
        this.mBgColorPress = 117440512;
        init();
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            if (x < getShadowX() || x > this.mWidth - getShadowX() || y < getShadowY() || y > this.mHeight - getShadowY()) {
                return false;
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    private void init() {
        if (!MiuiSdk.isLiteOrMiddleMode()) {
            MiuiFolme.touch(this);
        }
        this.mShadowColor = getContext().getResources().getColor(R.color.del_view_shadow_color);
        this.mBgColor = getContext().getResources().getColor(R.color.del_view_bg_color);
        this.mBgColorPress = getContext().getResources().getColor(R.color.del_view_bg_color_p);
        float f = getContext().getResources().getDisplayMetrics().density;
        this.mShadowOffsetX = (int) (0.0f * f);
        int i = (int) (f * 5.45f);
        this.mShadowOffsetY = i;
        this.mShadowWidth = i;
        this.mRadius = (int) getContext().getResources().getDimension(R.dimen.common_timer_del_view_radius);
        Paint paint = new Paint(1);
        this.mPaintShadow = paint;
        paint.setStyle(Paint.Style.FILL);
        this.mPaintShadow.setColor(this.mShadowColor);
        this.mPaintShadow.setShadowLayer(this.mShadowWidth, this.mShadowOffsetX, this.mShadowOffsetY, this.mShadowColor);
        Paint paint2 = new Paint(1);
        this.mPaintBg = paint2;
        paint2.setStyle(Paint.Style.FILL);
        setTextColor(getContext().getResources().getColor(R.color.del_text_color));
    }

    private int getShadowX() {
        return this.mShadowWidth + Math.abs(this.mShadowOffsetX);
    }

    private int getShadowY() {
        return this.mShadowWidth + Math.abs(this.mShadowOffsetY);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mWidth = getMeasuredWidth() + (getShadowX() * 2);
        int measuredHeight = getMeasuredHeight() + (getShadowY() * 2);
        this.mHeight = measuredHeight;
        setMeasuredDimension(this.mWidth, measuredHeight);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        float shadowX = getShadowX();
        float shadowY = getShadowY();
        float shadowX2 = this.mWidth - getShadowX();
        float shadowY2 = this.mHeight - getShadowY();
        int i = this.mRadius;
        canvas.drawRoundRect(shadowX, shadowY, shadowX2, shadowY2, i, i, this.mPaintShadow);
        if (isPressed()) {
            this.mPaintBg.setColor(this.mBgColorPress);
        } else {
            this.mPaintBg.setColor(this.mBgColor);
        }
        float shadowX3 = getShadowX();
        float shadowY3 = getShadowY();
        float shadowX4 = this.mWidth - getShadowX();
        float shadowY4 = this.mHeight - getShadowY();
        int i2 = this.mRadius;
        canvas.drawRoundRect(shadowX3, shadowY3, shadowX4, shadowY4, i2, i2, this.mPaintBg);
        canvas.translate(getShadowX(), getShadowY());
        super.onDraw(canvas);
    }
}
