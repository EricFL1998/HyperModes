package miuix.smooth;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import java.io.IOException;
import miuix.smooth.internal.SmoothDrawHelper;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes3.dex */
@Deprecated
public class SmoothGradientDrawable extends GradientDrawable {
    private static final PorterDuffXfermode XFERMODE_DST_OUT = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    private SmoothDrawHelper mHelper;
    private Rect mLayer;
    private GradientDrawable mParentDrawable;
    private RectF mSavedLayer;
    protected SmoothConstantState mSmoothConstantState;

    public SmoothGradientDrawable() {
        this.mHelper = new SmoothDrawHelper();
        this.mSavedLayer = new RectF();
        this.mLayer = new Rect();
        SmoothConstantState smoothConstantState = new SmoothConstantState();
        this.mSmoothConstantState = smoothConstantState;
        smoothConstantState.setConstantState(super.getConstantState());
    }

    public SmoothGradientDrawable(GradientDrawable.Orientation orientation, int[] iArr) {
        super(orientation, iArr);
        this.mHelper = new SmoothDrawHelper();
        this.mSavedLayer = new RectF();
        this.mLayer = new Rect();
        SmoothConstantState smoothConstantState = new SmoothConstantState();
        this.mSmoothConstantState = smoothConstantState;
        smoothConstantState.setConstantState(super.getConstantState());
    }

    private SmoothGradientDrawable(SmoothConstantState smoothConstantState, Resources resources) {
        Drawable drawableNewDrawable;
        this.mHelper = new SmoothDrawHelper();
        this.mSavedLayer = new RectF();
        this.mLayer = new Rect();
        this.mSmoothConstantState = smoothConstantState;
        if (resources == null) {
            drawableNewDrawable = smoothConstantState.mParent.newDrawable();
        } else {
            drawableNewDrawable = smoothConstantState.mParent.newDrawable(resources);
        }
        this.mSmoothConstantState.setConstantState(drawableNewDrawable.getConstantState());
        this.mParentDrawable = (GradientDrawable) drawableNewDrawable;
        this.mHelper.setRadii(smoothConstantState.mRadii);
        this.mHelper.setRadius(smoothConstantState.mRadius);
        this.mHelper.setStrokeWidth(smoothConstantState.mStrokeWidth);
        this.mHelper.setStrokeColor(smoothConstantState.mStrokeColor);
    }

    public void setStrokeWidth(int i) {
        if (this.mSmoothConstantState.mStrokeWidth != i) {
            this.mSmoothConstantState.mStrokeWidth = i;
            this.mHelper.setStrokeWidth(i);
            invalidateSelf();
        }
    }

    public int getStrokeWidth() {
        return this.mSmoothConstantState.mStrokeWidth;
    }

    public void setStrokeColor(int i) {
        if (this.mSmoothConstantState.mStrokeColor != i) {
            this.mSmoothConstantState.mStrokeColor = i;
            this.mHelper.setStrokeColor(i);
            invalidateSelf();
        }
    }

    public int getStrokeColor() {
        return this.mSmoothConstantState.mStrokeColor;
    }

    public void setLayerType(int i) {
        if (i < 0 || i > 2) {
            throw new IllegalArgumentException("Layer type can only be one of: LAYER_TYPE_NONE, LAYER_TYPE_SOFTWARE or LAYER_TYPE_HARDWARE");
        }
        if (this.mSmoothConstantState.mLayerType != i) {
            this.mSmoothConstantState.mLayerType = i;
            invalidateSelf();
        }
    }

    public int getLayerType() {
        return this.mSmoothConstantState.mLayerType;
    }

    @Override // android.graphics.drawable.GradientDrawable
    public void setColor(int i) {
        GradientDrawable gradientDrawable = this.mParentDrawable;
        if (gradientDrawable != null) {
            gradientDrawable.setColor(i);
        } else {
            super.setColor(i);
        }
    }

    @Override // android.graphics.drawable.GradientDrawable
    public void setColor(ColorStateList colorStateList) {
        GradientDrawable gradientDrawable = this.mParentDrawable;
        if (gradientDrawable != null) {
            gradientDrawable.setColor(colorStateList);
        } else {
            super.setColor(colorStateList);
        }
    }

    @Override // android.graphics.drawable.GradientDrawable
    public ColorStateList getColor() {
        if (this.mParentDrawable != null) {
            return this.mParentDrawable.getColor();
        }
        return super.getColor();
    }

    @Override // android.graphics.drawable.GradientDrawable
    public void setColors(int[] iArr, float[] fArr) {
        if (this.mParentDrawable != null && Build.VERSION.SDK_INT >= 29) {
            this.mParentDrawable.setColors(iArr, fArr);
        } else {
            super.setColors(iArr, fArr);
        }
    }

    @Override // android.graphics.drawable.GradientDrawable
    public int[] getColors() {
        if (this.mParentDrawable != null) {
            return this.mParentDrawable.getColors();
        }
        return super.getColors();
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        TypedArray typedArrayObtainAttributes = obtainAttributes(resources, theme, attributeSet, R.styleable.MiuixSmoothGradientDrawable);
        setStrokeWidth(typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothGradientDrawable_miuix_strokeWidth, 0));
        setStrokeColor(typedArrayObtainAttributes.getColor(R.styleable.MiuixSmoothGradientDrawable_miuix_strokeColor, 0));
        setLayerType(typedArrayObtainAttributes.getInt(R.styleable.MiuixSmoothGradientDrawable_android_layerType, 0));
        typedArrayObtainAttributes.recycle();
        super.inflate(resources, xmlPullParser, attributeSet, theme);
    }

    @Override // android.graphics.drawable.GradientDrawable
    public void setCornerRadii(float[] fArr) {
        super.setCornerRadii(fArr);
        this.mSmoothConstantState.mRadii = fArr;
        this.mHelper.setRadii(fArr);
        if (fArr == null) {
            this.mSmoothConstantState.mRadius = 0.0f;
            this.mHelper.setRadius(0.0f);
        }
    }

    @Override // android.graphics.drawable.GradientDrawable
    public void setCornerRadius(float f) {
        if (Float.isNaN(f)) {
            return;
        }
        super.setCornerRadius(f);
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.mSmoothConstantState.mRadius = f;
        this.mSmoothConstantState.mRadii = null;
        this.mHelper.setRadius(f);
        this.mHelper.setRadii(null);
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        GradientDrawable gradientDrawable = this.mParentDrawable;
        if (gradientDrawable != null) {
            gradientDrawable.setAlpha(i);
        } else {
            super.setAlpha(i);
        }
        invalidateSelf();
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public int getAlpha() {
        GradientDrawable gradientDrawable = this.mParentDrawable;
        if (gradientDrawable != null) {
            return gradientDrawable.getAlpha();
        }
        return super.getAlpha();
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme theme) {
        super.applyTheme(theme);
        this.mSmoothConstantState.setConstantState(super.getConstantState());
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public boolean canApplyTheme() {
        SmoothConstantState smoothConstantState = this.mSmoothConstantState;
        return (smoothConstantState != null && smoothConstantState.canApplyTheme()) || super.canApplyTheme();
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        if (Build.VERSION.SDK_INT >= 30) {
            outline.setPath(this.mHelper.getSmoothPath(this.mLayer));
        } else {
            outline.setRoundRect(this.mLayer, this.mHelper.getRadius());
        }
    }

    private TypedArray obtainAttributes(Resources resources, Resources.Theme theme, AttributeSet attributeSet, int[] iArr) {
        if (theme == null) {
            return resources.obtainAttributes(attributeSet, iArr);
        }
        return theme.obtainStyledAttributes(attributeSet, iArr, 0, 0);
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        GradientDrawable gradientDrawable = this.mParentDrawable;
        if (gradientDrawable != null) {
            gradientDrawable.setBounds(rect);
        }
        this.mHelper.onBoundsChange(rect);
        this.mLayer = rect;
        this.mSavedLayer.set(0.0f, 0.0f, rect.width(), rect.height());
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int iSaveLayer = getLayerType() != 2 ? canvas.saveLayer(this.mSavedLayer, null, 31) : -1;
        GradientDrawable gradientDrawable = this.mParentDrawable;
        if (gradientDrawable != null) {
            gradientDrawable.draw(canvas);
        } else {
            super.draw(canvas);
        }
        this.mHelper.drawMask(canvas, XFERMODE_DST_OUT);
        if (getLayerType() != 2) {
            canvas.restoreToCount(iSaveLayer);
        }
        this.mHelper.drawStroke(canvas);
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.mSmoothConstantState;
    }

    protected static class SmoothConstantState extends Drawable.ConstantState {
        int mLayerType;
        Drawable.ConstantState mParent;
        float[] mRadii;
        float mRadius;
        int mStrokeColor;
        int mStrokeWidth;

        @Override // android.graphics.drawable.Drawable.ConstantState
        public boolean canApplyTheme() {
            return true;
        }

        public SmoothConstantState() {
            this.mStrokeWidth = 0;
            this.mStrokeColor = 0;
            this.mLayerType = 0;
        }

        public SmoothConstantState(SmoothConstantState smoothConstantState) {
            this.mStrokeWidth = 0;
            this.mStrokeColor = 0;
            this.mLayerType = 0;
            this.mRadius = smoothConstantState.mRadius;
            this.mRadii = smoothConstantState.mRadii;
            this.mStrokeWidth = smoothConstantState.mStrokeWidth;
            this.mStrokeColor = smoothConstantState.mStrokeColor;
            this.mParent = smoothConstantState.mParent;
            this.mLayerType = smoothConstantState.mLayerType;
        }

        public void setConstantState(Drawable.ConstantState constantState) {
            this.mParent = constantState;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            Resources resources = null;
            byte b = 0;
            if (this.mParent == null) {
                return null;
            }
            return new SmoothGradientDrawable(this, resources);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            if (this.mParent == null) {
                return null;
            }
            return new SmoothGradientDrawable(new SmoothConstantState(this), resources);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mParent.getChangingConfigurations();
        }
    }
}
