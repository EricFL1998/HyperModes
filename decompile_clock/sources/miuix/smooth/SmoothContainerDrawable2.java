package miuix.smooth;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes3.dex */
public class SmoothContainerDrawable2 extends Drawable implements Drawable.Callback {
    private static final String TAG = "SmoothContainerDrawable2";
    private Path mClipPath;
    private ContainerState mContainerState;
    private RectF mLayer;
    private float[] mRadii;
    private float mRadius;
    private int mStrokeColor;
    private Paint mStrokePaint;
    private int mStrokeWidth;
    private float[] mTempRadii;
    private boolean mUseSmooth;

    public SmoothContainerDrawable2() {
        this.mLayer = new RectF();
        this.mClipPath = new Path();
        this.mContainerState = new ContainerState();
        init();
    }

    private void init() {
        Paint paint = new Paint(1);
        this.mStrokePaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.mStrokePaint.setColor(this.mStrokeColor);
        this.mStrokePaint.setStrokeWidth(this.mStrokeWidth);
    }

    private SmoothContainerDrawable2(Resources resources, Resources.Theme theme, ContainerState containerState) {
        this.mLayer = new RectF();
        this.mClipPath = new Path();
        this.mContainerState = new ContainerState(containerState, this, resources, theme);
        this.mStrokeWidth = containerState.mStrokeWidth;
        this.mStrokeColor = containerState.mStrokeColor;
        this.mRadii = containerState.mRadii;
        this.mRadius = containerState.mRadius;
        boolean z = containerState.mUseSmooth;
        this.mUseSmooth = z;
        setSmoothCornerEnable(z);
        init();
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        TypedArray typedArrayObtainAttributes = obtainAttributes(resources, theme, attributeSet, R.styleable.MiuixSmoothContainerDrawable2);
        setCornerRadius(typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothContainerDrawable2_android_radius, 0));
        if (typedArrayObtainAttributes.hasValue(R.styleable.MiuixSmoothContainerDrawable2_android_topLeftRadius) || typedArrayObtainAttributes.hasValue(R.styleable.MiuixSmoothContainerDrawable2_android_topRightRadius) || typedArrayObtainAttributes.hasValue(R.styleable.MiuixSmoothContainerDrawable2_android_bottomRightRadius) || typedArrayObtainAttributes.hasValue(R.styleable.MiuixSmoothContainerDrawable2_android_bottomLeftRadius)) {
            float dimensionPixelSize = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothContainerDrawable2_android_topLeftRadius, 0);
            float dimensionPixelSize2 = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothContainerDrawable2_android_topRightRadius, 0);
            float dimensionPixelSize3 = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothContainerDrawable2_android_bottomRightRadius, 0);
            float dimensionPixelSize4 = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothContainerDrawable2_android_bottomLeftRadius, 0);
            setCornerRadii(new float[]{dimensionPixelSize, dimensionPixelSize, dimensionPixelSize2, dimensionPixelSize2, dimensionPixelSize3, dimensionPixelSize3, dimensionPixelSize4, dimensionPixelSize4});
        }
        setStrokeWidth(typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothContainerDrawable2_miuix_strokeWidth, 0));
        setStrokeColor(typedArrayObtainAttributes.getColor(R.styleable.MiuixSmoothContainerDrawable2_miuix_strokeColor, 0));
        setLayerType(typedArrayObtainAttributes.getInt(R.styleable.MiuixSmoothContainerDrawable2_android_layerType, 0));
        this.mUseSmooth = typedArrayObtainAttributes.getBoolean(R.styleable.MiuixSmoothContainerDrawable2_miuix_useSmooth, true);
        if (SmoothCornerHelper.FORCE_USE_SMOOTH != null) {
            this.mUseSmooth = SmoothCornerHelper.FORCE_USE_SMOOTH.booleanValue();
        }
        if (this.mUseSmooth) {
            setSmoothCornerEnable(true);
        }
        typedArrayObtainAttributes.recycle();
        inflateInnerDrawable(resources, xmlPullParser, attributeSet, theme);
    }

    private void inflateInnerDrawable(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        int next;
        int depth = xmlPullParser.getDepth() + 1;
        while (true) {
            int next2 = xmlPullParser.next();
            if (next2 == 1) {
                return;
            }
            int depth2 = xmlPullParser.getDepth();
            if (depth2 < depth && next2 == 3) {
                return;
            }
            if (next2 == 2 && depth2 <= depth && xmlPullParser.getName().equals("child")) {
                do {
                    next = xmlPullParser.next();
                } while (next == 4);
                if (next != 2) {
                    throw new XmlPullParserException(xmlPullParser.getPositionDescription() + ": <child> tag requires a 'drawable' attribute or child tag defining a drawable");
                }
                ChildDrawableWrapper childDrawableWrapper = new ChildDrawableWrapper();
                childDrawableWrapper.mDrawable = Drawable.createFromXmlInner(resources, xmlPullParser, attributeSet, theme);
                childDrawableWrapper.mDrawable.setCallback(this);
                this.mContainerState.mChildDrawableWrapper = childDrawableWrapper;
                return;
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme theme) {
        super.applyTheme(theme);
        this.mContainerState.mChildDrawableWrapper.mDrawable.applyTheme(theme);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean canApplyTheme() {
        ContainerState containerState = this.mContainerState;
        return (containerState != null && containerState.canApplyTheme()) || super.canApplyTheme();
    }

    public void setChildDrawable(Drawable drawable) {
        if (this.mContainerState != null) {
            ChildDrawableWrapper childDrawableWrapper = new ChildDrawableWrapper();
            childDrawableWrapper.mDrawable = drawable;
            childDrawableWrapper.mDrawable.setCallback(this);
            this.mContainerState.mChildDrawableWrapper = childDrawableWrapper;
        }
    }

    public Drawable getChildDrawable() {
        ContainerState containerState = this.mContainerState;
        if (containerState != null) {
            return containerState.mChildDrawableWrapper.mDrawable;
        }
        return null;
    }

    public void setStrokeWidth(int i) {
        if (this.mContainerState.mStrokeWidth != i) {
            this.mContainerState.mStrokeWidth = i;
            this.mStrokeWidth = i;
            this.mStrokePaint.setStrokeWidth(i);
            invalidateSelf();
        }
    }

    public int getStrokeWidth() {
        return this.mContainerState.mStrokeWidth;
    }

    public void setStrokeColor(int i) {
        if (this.mContainerState.mStrokeColor != i) {
            this.mContainerState.mStrokeColor = i;
            this.mStrokeColor = i;
            this.mStrokePaint.setColor(i);
            invalidateSelf();
        }
    }

    public int getStrokeColor() {
        return this.mContainerState.mStrokeColor;
    }

    public void setCornerRadii(float[] fArr) {
        this.mContainerState.mRadii = fArr;
        this.mRadii = fArr;
        if (fArr == null) {
            this.mContainerState.mRadius = 0.0f;
            this.mRadius = 0.0f;
        }
        invalidateSelf();
    }

    public float[] getCornerRadii() {
        if (this.mContainerState.mRadii == null) {
            return null;
        }
        return (float[]) this.mContainerState.mRadii.clone();
    }

    public void setCornerRadius(float f) {
        if (Float.isNaN(f)) {
            return;
        }
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.mContainerState.mRadius = f;
        this.mContainerState.mRadii = null;
        this.mRadius = f;
        this.mRadii = null;
        invalidateSelf();
    }

    public float getCornerRadius() {
        return this.mContainerState.mRadius;
    }

    public void setLayerType(int i) {
        if (i < 0 || i > 2) {
            throw new IllegalArgumentException("Layer type can only be one of: LAYER_TYPE_NONE, LAYER_TYPE_SOFTWARE or LAYER_TYPE_HARDWARE");
        }
        if (this.mContainerState.mLayerType != i) {
            this.mContainerState.mLayerType = i;
            invalidateSelf();
        }
    }

    public void setUseSmooth(boolean z) {
        this.mUseSmooth = z;
        setSmoothCornerEnable(z);
    }

    public boolean getUseSmooth() {
        return this.mUseSmooth;
    }

    public int getLayerType() {
        return this.mContainerState.mLayerType;
    }

    @Override // android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        outline.setRoundRect(getBoundsInner(), getCornerRadius());
    }

    protected static TypedArray obtainAttributes(Resources resources, Resources.Theme theme, AttributeSet attributeSet, int[] iArr) {
        if (theme == null) {
            return resources.obtainAttributes(attributeSet, iArr);
        }
        return theme.obtainStyledAttributes(attributeSet, iArr, 0, 0);
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        scheduleSelf(runnable, j);
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        unscheduleSelf(runnable);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mContainerState.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mContainerState.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        this.mContainerState.onBoundsChange(rect);
    }

    public final Rect getBoundsInner() {
        return this.mContainerState.getBounds();
    }

    @Override // android.graphics.drawable.Drawable
    public Rect getDirtyBounds() {
        return this.mContainerState.getDirtyBounds();
    }

    @Override // android.graphics.drawable.Drawable
    public void setChangingConfigurations(int i) {
        this.mContainerState.setChangingConfigurations(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean z) {
        this.mContainerState.setDither(z);
    }

    @Override // android.graphics.drawable.Drawable
    public void setFilterBitmap(boolean z) {
        this.mContainerState.setFilterBitmap(z);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect rect) {
        return this.mContainerState.getPadding(rect);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        drawRoundRect(canvas);
    }

    private void drawRoundRect(Canvas canvas) {
        Rect bounds = getBounds();
        int iSave = canvas.save();
        this.mClipPath.reset();
        this.mLayer.left = bounds.left;
        this.mLayer.top = bounds.top;
        this.mLayer.right = bounds.right;
        this.mLayer.bottom = bounds.bottom;
        float[] fArr = this.mRadii;
        if (fArr == null) {
            Path path = this.mClipPath;
            RectF rectF = this.mLayer;
            float f = this.mRadius;
            path.addRoundRect(rectF, f, f, Path.Direction.CW);
        } else {
            this.mClipPath.addRoundRect(this.mLayer, fArr, Path.Direction.CW);
        }
        canvas.clipPath(this.mClipPath);
        int i = this.mStrokeWidth;
        float f2 = i * 0.5f;
        if (i != 0) {
            int iSave2 = canvas.save();
            this.mLayer.inset(f2, f2);
            this.mClipPath.reset();
            float[] fArr2 = this.mRadii;
            if (fArr2 == null) {
                Path path2 = this.mClipPath;
                RectF rectF2 = this.mLayer;
                float f3 = this.mRadius;
                path2.addRoundRect(rectF2, f3 - f2, f3 - f2, Path.Direction.CW);
            } else {
                float[] fArr3 = (float[]) fArr2.clone();
                this.mTempRadii = fArr3;
                float[] fArr4 = this.mRadii;
                fArr3[0] = fArr4[0] - f2;
                fArr3[1] = fArr4[1] - f2;
                fArr3[2] = fArr4[2] - f2;
                fArr3[3] = fArr4[3] - f2;
                this.mClipPath.addRoundRect(this.mLayer, fArr3, Path.Direction.CCW);
            }
            canvas.clipPath(this.mClipPath);
            this.mContainerState.mChildDrawableWrapper.mDrawable.draw(canvas);
            canvas.restoreToCount(iSave2);
            canvas.drawPath(this.mClipPath, this.mStrokePaint);
        } else {
            this.mContainerState.mChildDrawableWrapper.mDrawable.draw(canvas);
        }
        canvas.restoreToCount(iSave);
    }

    private void setSmoothCornerEnable(boolean z) {
        SmoothCornerHelper.setDrawableSmoothCornerEnable(this, z);
        this.mContainerState.mUseSmooth = z;
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mContainerState.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mContainerState.setAlpha(i);
        this.mStrokePaint.setAlpha(i);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mContainerState.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.mContainerState.getOpacity();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.mContainerState;
    }

    @Override // android.graphics.drawable.Drawable
    public void jumpToCurrentState() {
        this.mContainerState.jumpToCurrentState();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        return this.mContainerState.onStateChange(iArr);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mContainerState.isStateful();
    }

    public float[] getmRadii() {
        return this.mRadii;
    }

    public void setmRadii(float[] fArr) {
        this.mRadii = fArr;
    }

    static class ChildDrawableWrapper {
        Drawable mDrawable;

        ChildDrawableWrapper() {
            this.mDrawable = new GradientDrawable();
        }

        ChildDrawableWrapper(ChildDrawableWrapper childDrawableWrapper, SmoothContainerDrawable2 smoothContainerDrawable2, Resources resources, Resources.Theme theme) {
            Drawable drawableNewDrawable;
            Drawable drawable = childDrawableWrapper.mDrawable;
            if (drawable != null) {
                Drawable.ConstantState constantState = drawable.getConstantState();
                if (constantState == null) {
                    drawableNewDrawable = drawable;
                } else if (resources == null) {
                    drawableNewDrawable = constantState.newDrawable();
                } else if (theme == null) {
                    drawableNewDrawable = constantState.newDrawable(resources);
                } else {
                    drawableNewDrawable = constantState.newDrawable(resources, theme);
                }
                drawableNewDrawable.setLayoutDirection(drawable.getLayoutDirection());
                drawableNewDrawable.setBounds(drawable.getBounds());
                drawableNewDrawable.setLevel(drawable.getLevel());
                drawableNewDrawable.setCallback(smoothContainerDrawable2);
            } else {
                drawableNewDrawable = null;
            }
            this.mDrawable = drawableNewDrawable;
        }
    }

    static final class ContainerState extends Drawable.ConstantState {
        ChildDrawableWrapper mChildDrawableWrapper;
        int mLayerType;
        float[] mRadii;
        float mRadius;
        int mStrokeColor;
        int mStrokeWidth;
        boolean mUseSmooth;

        @Override // android.graphics.drawable.Drawable.ConstantState
        public boolean canApplyTheme() {
            return true;
        }

        public ContainerState() {
            this.mLayerType = 0;
            this.mChildDrawableWrapper = new ChildDrawableWrapper();
        }

        public ContainerState(ContainerState containerState, SmoothContainerDrawable2 smoothContainerDrawable2, Resources resources, Resources.Theme theme) {
            this.mLayerType = 0;
            this.mChildDrawableWrapper = new ChildDrawableWrapper(containerState.mChildDrawableWrapper, smoothContainerDrawable2, resources, theme);
            this.mRadius = containerState.mRadius;
            this.mRadii = containerState.mRadii;
            this.mStrokeWidth = containerState.mStrokeWidth;
            this.mStrokeColor = containerState.mStrokeColor;
            this.mLayerType = containerState.mLayerType;
            this.mUseSmooth = containerState.mUseSmooth;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new SmoothContainerDrawable2(null, 0 == true ? 1 : 0, this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new SmoothContainerDrawable2(resources, null, this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources, Resources.Theme theme) {
            return new SmoothContainerDrawable2(resources, theme, this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChildDrawableWrapper.mDrawable.getChangingConfigurations();
        }

        public final boolean isStateful() {
            return this.mChildDrawableWrapper.mDrawable.isStateful();
        }

        public boolean onStateChange(int[] iArr) {
            return isStateful() && this.mChildDrawableWrapper.mDrawable.setState(iArr);
        }

        public int getIntrinsicWidth() {
            return this.mChildDrawableWrapper.mDrawable.getIntrinsicWidth();
        }

        public int getIntrinsicHeight() {
            return this.mChildDrawableWrapper.mDrawable.getIntrinsicHeight();
        }

        public void onBoundsChange(Rect rect) {
            this.mChildDrawableWrapper.mDrawable.setBounds(rect);
        }

        public void jumpToCurrentState() {
            this.mChildDrawableWrapper.mDrawable.jumpToCurrentState();
        }

        public int getOpacity() {
            return this.mChildDrawableWrapper.mDrawable.getOpacity();
        }

        public void setAlpha(int i) {
            this.mChildDrawableWrapper.mDrawable.setAlpha(i);
            this.mChildDrawableWrapper.mDrawable.invalidateSelf();
        }

        public void setColorFilter(ColorFilter colorFilter) {
            this.mChildDrawableWrapper.mDrawable.setColorFilter(colorFilter);
        }

        public Rect getBounds() {
            return this.mChildDrawableWrapper.mDrawable.getBounds();
        }

        public void setBounds(Rect rect) {
            this.mChildDrawableWrapper.mDrawable.setBounds(rect);
        }

        public void setBounds(int i, int i2, int i3, int i4) {
            this.mChildDrawableWrapper.mDrawable.setBounds(i, i2, i3, i4);
        }

        public Rect getDirtyBounds() {
            return this.mChildDrawableWrapper.mDrawable.getDirtyBounds();
        }

        public void setChangingConfigurations(int i) {
            this.mChildDrawableWrapper.mDrawable.setChangingConfigurations(i);
        }

        public void setDither(boolean z) {
            this.mChildDrawableWrapper.mDrawable.setDither(z);
        }

        public void setFilterBitmap(boolean z) {
            this.mChildDrawableWrapper.mDrawable.setFilterBitmap(z);
        }

        public int getAlpha() {
            return this.mChildDrawableWrapper.mDrawable.getAlpha();
        }

        public boolean getPadding(Rect rect) {
            return this.mChildDrawableWrapper.mDrawable.getPadding(rect);
        }
    }
}
