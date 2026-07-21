package com.android.deskclock.timer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.addition.MiuiSdk;

/* JADX INFO: loaded from: classes.dex */
public class DefinedShadowLayout extends LinearLayout {
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

    public DefinedShadowLayout(Context context) {
        super(context);
        this.mShadowColor = 218103808;
        this.mBgColor = -1;
        this.mBgColorPress = 117440512;
        init();
    }

    public DefinedShadowLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mShadowColor = 218103808;
        this.mBgColor = -1;
        this.mBgColorPress = 117440512;
        init();
    }

    public DefinedShadowLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShadowColor = 218103808;
        this.mBgColor = -1;
        this.mBgColorPress = 117440512;
        init();
    }

    public DefinedShadowLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mShadowColor = 218103808;
        this.mBgColor = -1;
        this.mBgColorPress = 117440512;
        init();
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
        paint2.setColor(this.mBgColor);
        this.mPaintBg.setStyle(Paint.Style.FILL);
        setWillNotDraw(false);
    }

    private int getShadowX() {
        return this.mShadowWidth + Math.abs(this.mShadowOffsetX);
    }

    private int getShadowY() {
        return this.mShadowWidth + Math.abs(this.mShadowOffsetY);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mWidth = getMeasuredWidth() + (getShadowX() * 2);
        int measuredHeight = getMeasuredHeight() + (getShadowY() * 2);
        this.mHeight = measuredHeight;
        setMeasuredDimension(this.mWidth, measuredHeight);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        float shadowX = getShadowX();
        float shadowY = getShadowY();
        float shadowX2 = this.mWidth - getShadowX();
        float shadowY2 = this.mHeight - getShadowY();
        int i = this.mRadius;
        canvas.drawRoundRect(shadowX, shadowY, shadowX2, shadowY2, i, i, this.mPaintShadow);
        float shadowX3 = getShadowX();
        float shadowY3 = getShadowY();
        float shadowX4 = this.mWidth - getShadowX();
        float shadowY4 = this.mHeight - getShadowY();
        int i2 = this.mRadius;
        canvas.drawRoundRect(shadowX3, shadowY3, shadowX4, shadowY4, i2, i2, this.mPaintBg);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            childAt.layout(((int) childAt.getX()) + getShadowX(), childAt.getTop() + getShadowY(), ((int) childAt.getX()) + getShadowX() + childAt.getWidth(), childAt.getTop() + getShadowY() + childAt.getHeight());
        }
    }
}
