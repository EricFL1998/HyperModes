package com.android.deskclock.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiFolme;
import com.android.deskclock.addition.MiuiSdk;

/* JADX INFO: loaded from: classes.dex */
public class TimerButton extends ImageButton {
    private static final float SHADOW_RADIUS = 5.45f;
    private static final String TAG = "TimerButton";
    private static final float X_OFFSET = 0.0f;
    private static final float Y_OFFSET = 5.45f;
    private int imageResourceId;
    private Drawable mBackgroundDrawable;
    private Drawable mBackgroundPressedDrawable;
    private int mFabHeightSize;
    private int mFabWidthSize;
    private int mHeight;
    private Drawable mIconDrawable;
    private int mRectRadius;
    private int mShadowColor;
    private int mShadowOffsetX;
    private int mShadowOffsetY;
    private int mShadowWidth;
    private boolean mTransparentMode;
    private int mWidth;
    private int originalShadowColor;

    public TimerButton(Context context) {
        this(context, null);
    }

    public TimerButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TimerButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShadowColor = 436207616;
        this.mTransparentMode = false;
        this.imageResourceId = -1;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ClockFloatingActionButton, i, 0);
        int color = typedArrayObtainStyledAttributes.getColor(5, this.mShadowColor);
        this.mShadowColor = color;
        this.originalShadowColor = color;
        this.mBackgroundDrawable = typedArrayObtainStyledAttributes.getDrawable(0);
        this.mBackgroundPressedDrawable = typedArrayObtainStyledAttributes.getDrawable(2);
        this.mTransparentMode = typedArrayObtainStyledAttributes.getBoolean(9, false);
        this.mRectRadius = typedArrayObtainStyledAttributes.getDimensionPixelSize(4, 0);
        typedArrayObtainStyledAttributes.recycle();
        float f = getContext().getResources().getDisplayMetrics().density;
        this.mShadowOffsetX = (int) (0.0f * f);
        int i2 = (int) (f * 5.45f);
        this.mShadowOffsetY = i2;
        this.mShadowWidth = i2;
        if (MiuiSdk.isLiteOrMiddleMode()) {
            return;
        }
        MiuiFolme.touch(this);
    }

    public void setFabBackground(int i) {
        Drawable drawable = getResources().getDrawable(i);
        if (this.mBackgroundDrawable != drawable) {
            this.mBackgroundDrawable = drawable;
            updateBackground();
        }
    }

    @Override // android.widget.ImageView
    public void setImageDrawable(Drawable drawable) {
        if (this.mIconDrawable != drawable) {
            this.mIconDrawable = drawable;
            updateBackground();
        }
    }

    @Override // android.widget.ImageView
    public void setImageResource(int i) {
        try {
            this.imageResourceId = i;
            Drawable drawable = getResources().getDrawable(i);
            if (this.mIconDrawable != drawable) {
                this.mIconDrawable = drawable;
                updateBackground();
            }
        } catch (Exception e) {
            Log.e(TAG, "setImageResource: " + e);
            Log.e(TAG, "setImageResource: resId: " + i);
        }
    }

    public int getImageResourceId() {
        return this.imageResourceId;
    }

    public void setShadowColor(int i) {
        if (this.mShadowColor != i) {
            this.mShadowColor = i;
            updateBackground();
        }
    }

    public void resetShadowColor() {
        setShadowColor(this.originalShadowColor);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onMeasure(int i, int i2) {
        this.mFabWidthSize = View.MeasureSpec.getSize(i);
        this.mFabHeightSize = View.MeasureSpec.getSize(i2);
        this.mWidth = View.MeasureSpec.getSize(i) + (getShadowX() * 2);
        int size = View.MeasureSpec.getSize(i2) + (getShadowY() * 2);
        this.mHeight = size;
        setMeasuredDimension(this.mWidth, size);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        updateBackground();
    }

    public int getShadowX() {
        return this.mShadowWidth + Math.abs(this.mShadowOffsetX);
    }

    public int getShadowY() {
        return this.mShadowWidth + Math.abs(this.mShadowOffsetY);
    }

    private void updateBackground() {
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{createShadowDrawable(), createFillDrawable(), getIconDrawable()});
        int iAbs = this.mShadowWidth + Math.abs(this.mShadowOffsetX);
        int iAbs2 = this.mShadowWidth + Math.abs(this.mShadowOffsetY);
        int iMax = getIconDrawable() != null ? Math.max(getIconDrawable().getIntrinsicWidth(), getIconDrawable().getIntrinsicHeight()) : -1;
        int i = (this.mFabWidthSize - (iMax > 0 ? iMax : 0)) / 2;
        int i2 = this.mFabHeightSize;
        int i3 = iMax > 0 ? iMax : 0;
        int i4 = iAbs + i;
        int i5 = iAbs2 + ((i2 - i3) / 2);
        layerDrawable.setLayerInset(2, i4, i5, i4, i5);
        setBackground(layerDrawable);
    }

    private Drawable getIconDrawable() {
        Drawable drawable = this.mIconDrawable;
        return drawable != null ? drawable : new ColorDrawable(0);
    }

    private Drawable createShadowDrawable() {
        return new ShadowDrawable();
    }

    private Drawable createFillDrawable() {
        Drawable drawable = this.mBackgroundDrawable;
        if (drawable != null) {
            if (this.mBackgroundPressedDrawable == null) {
                this.mBackgroundPressedDrawable = drawable;
            }
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new BackgroundDrawable(this.mBackgroundPressedDrawable));
            stateListDrawable.addState(new int[0], new BackgroundDrawable(this.mBackgroundDrawable));
            return stateListDrawable;
        }
        return new ColorDrawable(0);
    }

    public void setBackground(int i, int i2) {
        if (i == 0) {
            this.mBackgroundDrawable = null;
        } else {
            Drawable drawable = getResources().getDrawable(i);
            if (this.mBackgroundDrawable != drawable) {
                this.mBackgroundDrawable = drawable;
            }
        }
        if (i2 == 0) {
            this.mBackgroundPressedDrawable = null;
        } else {
            Drawable drawable2 = getResources().getDrawable(i2);
            if (this.mBackgroundPressedDrawable != drawable2) {
                this.mBackgroundPressedDrawable = drawable2;
            }
        }
        updateBackground();
    }

    public void setmShadowColor(int i) {
        this.mShadowColor = i;
    }

    @Override // android.widget.ImageView
    public void setImageAlpha(int i) {
        Drawable drawable = this.mIconDrawable;
        if (drawable != null) {
            drawable.setAlpha(i);
            updateBackground();
        }
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

    private class BackgroundDrawable extends Drawable {
        private Drawable bgDrawable;
        private int circleInsetHorizontal;
        private int circleInsetVertical;

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return 0;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        private BackgroundDrawable(Drawable drawable) {
            this.circleInsetHorizontal = TimerButton.this.mShadowWidth + Math.abs(TimerButton.this.mShadowOffsetX);
            this.circleInsetVertical = TimerButton.this.mShadowWidth + Math.abs(TimerButton.this.mShadowOffsetY);
            this.bgDrawable = drawable;
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            this.bgDrawable.setBounds(this.circleInsetHorizontal, this.circleInsetVertical, TimerButton.this.mWidth - this.circleInsetHorizontal, TimerButton.this.mHeight - this.circleInsetVertical);
            this.bgDrawable.draw(canvas);
        }
    }

    private class ShadowDrawable extends Drawable {
        private Paint mPaint;
        PaintFlagsDrawFilter mPaintFlagsDrawFilter;
        private float mRadius;

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return 0;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        private ShadowDrawable() {
            this.mPaint = new Paint(1);
            this.mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, 3);
            this.mPaint.setStyle(Paint.Style.FILL);
            this.mPaint.setColor(TimerButton.this.mShadowColor);
            this.mPaint.setShadowLayer(TimerButton.this.mShadowWidth, TimerButton.this.mShadowOffsetX, TimerButton.this.mShadowOffsetY, TimerButton.this.mShadowColor);
            this.mRadius = TimerButton.this.mFabWidthSize / 2.0f;
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            if (TimerButton.this.mTransparentMode) {
                Bitmap bitmapCreateBitmap = Bitmap.createBitmap(TimerButton.this.mWidth, TimerButton.this.mHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas2 = new Canvas(bitmapCreateBitmap);
                canvas2.setDrawFilter(this.mPaintFlagsDrawFilter);
                canvas2.drawRoundRect(TimerButton.this.getShadowX(), TimerButton.this.getShadowY(), TimerButton.this.mWidth - TimerButton.this.getShadowX(), TimerButton.this.mHeight - TimerButton.this.getShadowY(), 19.0f, 19.0f, this.mPaint);
                Bitmap bitmapCreateBitmap2 = Bitmap.createBitmap(TimerButton.this.mWidth, TimerButton.this.mHeight, Bitmap.Config.ARGB_8888);
                new Canvas(bitmapCreateBitmap2).setDrawFilter(this.mPaintFlagsDrawFilter);
                canvas2.drawRoundRect(TimerButton.this.getShadowX(), TimerButton.this.getShadowY(), TimerButton.this.mWidth - TimerButton.this.getShadowX(), TimerButton.this.mHeight - TimerButton.this.getShadowY(), 19.0f, 19.0f, new Paint());
                Bitmap bitmapCreateBitmap3 = Bitmap.createBitmap(TimerButton.this.getWidth(), TimerButton.this.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas3 = new Canvas(bitmapCreateBitmap3);
                Paint paint = new Paint();
                canvas3.drawBitmap(bitmapCreateBitmap, 0.0f, 0.0f, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                canvas3.drawBitmap(bitmapCreateBitmap2, 0.0f, 0.0f, paint);
                canvas.setDrawFilter(this.mPaintFlagsDrawFilter);
                canvas.drawBitmap(bitmapCreateBitmap3, 0.0f, 0.0f, new Paint());
                this.mPaint.setXfermode(null);
                return;
            }
            canvas.setDrawFilter(this.mPaintFlagsDrawFilter);
            canvas.drawRoundRect(TimerButton.this.getShadowX(), TimerButton.this.getShadowY(), TimerButton.this.mWidth - TimerButton.this.getShadowX(), TimerButton.this.mHeight - TimerButton.this.getShadowY(), TimerButton.this.mRectRadius, TimerButton.this.mRectRadius, this.mPaint);
        }
    }
}
