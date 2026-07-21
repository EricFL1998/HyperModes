package com.miui.support.cardview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import java.lang.reflect.InvocationTargetException;
import miuix.device.DeviceUtils;
import miuix.reflect.ReflectionHelper;
import miuix.smooth.SmoothContainerDrawableForCardView;

/* JADX INFO: loaded from: classes2.dex */
public class CardView extends androidx.cardview.widget.CardView {
    private static final String TAG = "MiuiX.CardView";
    private Path mClipPath;
    private RectF mLayer;
    private int mStrokeColor;
    private int mStrokeWidth;
    private boolean mUseSmooth;

    public CardView(Context context) {
        this(context, null);
    }

    public CardView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.cardViewStyle);
    }

    public CardView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLayer = new RectF();
        this.mClipPath = new Path();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.CardView, i, 0);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.CardView_outlineStyle, -1);
        if (resourceId != -1) {
            TypedArray typedArrayObtainStyledAttributes2 = context.obtainStyledAttributes(resourceId, R.styleable.OutlineProvider);
            String string = typedArrayObtainStyledAttributes2.getString(R.styleable.OutlineProvider_android_name);
            if (!TextUtils.isEmpty(string)) {
                setOutlineProviderFromAttribute(context, string, resourceId);
            }
            typedArrayObtainStyledAttributes2.recycle();
        }
        this.mUseSmooth = typedArrayObtainStyledAttributes.getBoolean(R.styleable.CardView_miuix_useSmooth, true);
        if (checkNeedSmooth()) {
            setSmoothCornerEnable(true);
        }
        setStrokeWidth(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.CardView_miuix_strokeWidth, 0));
        setStrokeColor(typedArrayObtainStyledAttributes.getColor(R.styleable.CardView_miuix_strokeColor, 0));
        typedArrayObtainStyledAttributes.recycle();
        updateBackground();
    }

    private boolean checkNeedSmooth() {
        return !isCommonLiteStrategy() && this.mUseSmooth;
    }

    private boolean isCommonLiteStrategy() {
        return DeviceUtils.isMiuiLiteV2() || DeviceUtils.isLiteV1StockPlus();
    }

    private void setSmoothCornerEnable(boolean z) {
        try {
            ReflectionHelper.invoke(View.class, this, "setSmoothCornerEnabled", new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e(TAG, "setSmoothCornerEnabled failed:" + e.getMessage());
        }
    }

    @Override // androidx.cardview.widget.CardView
    public void setRadius(float f) {
        super.setRadius(f);
        updateBackground();
    }

    private void updateBackground() {
        Drawable originalBackground = getOriginalBackground();
        SmoothContainerDrawableForCardView smoothContainerDrawableForCardView = new SmoothContainerDrawableForCardView();
        smoothContainerDrawableForCardView.setChildDrawable(originalBackground);
        smoothContainerDrawableForCardView.setCornerRadius(getRadius());
        smoothContainerDrawableForCardView.setStrokeWidth(getStrokeWidth());
        smoothContainerDrawableForCardView.setStrokeColor(getStrokeColor());
        setBackground(smoothContainerDrawableForCardView);
    }

    private Drawable getOriginalBackground() {
        Drawable background = getBackground();
        return background instanceof SmoothContainerDrawableForCardView ? ((SmoothContainerDrawableForCardView) background).getChildDrawable() : background;
    }

    public void setStrokeWidth(int i) {
        if (this.mStrokeWidth != i) {
            this.mStrokeWidth = i;
            updateBackground();
        }
    }

    public int getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public void setStrokeColor(int i) {
        if (this.mStrokeColor != i) {
            this.mStrokeColor = i;
            updateBackground();
        }
    }

    public int getStrokeColor() {
        return this.mStrokeColor;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        this.mClipPath.reset();
        this.mLayer.left = getPaddingLeft();
        this.mLayer.top = getPaddingTop();
        this.mLayer.right = getWidth() - getPaddingRight();
        this.mLayer.bottom = getHeight() - getPaddingBottom();
        this.mClipPath.addRoundRect(this.mLayer, getRadius(), getRadius(), Path.Direction.CW);
        canvas.clipPath(this.mClipPath);
        super.onDraw(canvas);
    }

    private void setOutlineProviderFromAttribute(Context context, String str, int i) {
        try {
            Class<? extends U> clsAsSubclass = Class.forName(str).asSubclass(ViewOutlineProvider.class);
            try {
                try {
                    setOutlineProvider((ViewOutlineProvider) clsAsSubclass.getConstructor(Context.class, Integer.TYPE).newInstance(context, Integer.valueOf(i)));
                } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            } catch (IllegalAccessException | NoSuchMethodException unused) {
                setOutlineProvider((ViewOutlineProvider) clsAsSubclass.getConstructor(new Class[0]).newInstance(new Object[0]));
            } catch (InstantiationException e2) {
                e = e2;
                throw new RuntimeException(e);
            } catch (InvocationTargetException e3) {
                e = e3;
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException unused2) {
            throw new NoClassDefFoundError(str);
        }
    }
}
