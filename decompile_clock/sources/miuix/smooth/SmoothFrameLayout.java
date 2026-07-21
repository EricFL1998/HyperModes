package miuix.smooth;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import miuix.smooth.internal.SmoothDrawHelper;

/* JADX INFO: loaded from: classes3.dex */
public class SmoothFrameLayout extends FrameLayout {
    private static final PorterDuffXfermode XFERMODE_DST_OUT = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    private SmoothDrawHelper mHelper;
    private Rect mLayer;
    private RectF mSavedLayer;

    public SmoothFrameLayout(Context context) {
        this(context, null);
    }

    public SmoothFrameLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SmoothFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLayer = new Rect();
        this.mSavedLayer = new RectF();
        this.mHelper = new SmoothDrawHelper();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.MiuixSmoothFrameLayout);
        setCornerRadius(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothFrameLayout_android_radius, 0));
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixSmoothFrameLayout_android_topLeftRadius) || typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixSmoothFrameLayout_android_topRightRadius) || typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixSmoothFrameLayout_android_bottomRightRadius) || typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixSmoothFrameLayout_android_bottomLeftRadius)) {
            float dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothFrameLayout_android_topLeftRadius, 0);
            float dimensionPixelSize2 = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothFrameLayout_android_topRightRadius, 0);
            float dimensionPixelSize3 = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothFrameLayout_android_bottomRightRadius, 0);
            float dimensionPixelSize4 = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothFrameLayout_android_bottomLeftRadius, 0);
            setCornerRadii(new float[]{dimensionPixelSize, dimensionPixelSize, dimensionPixelSize2, dimensionPixelSize2, dimensionPixelSize3, dimensionPixelSize3, dimensionPixelSize4, dimensionPixelSize4});
        }
        setStrokeWidth(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothFrameLayout_miuix_strokeWidth, 0));
        setStrokeColor(typedArrayObtainStyledAttributes.getColor(R.styleable.MiuixSmoothFrameLayout_miuix_strokeColor, 0));
        setLayerType(typedArrayObtainStyledAttributes.getColor(R.styleable.MiuixSmoothFrameLayout_android_layerType, 2), null);
        typedArrayObtainStyledAttributes.recycle();
    }

    public void setStrokeWidth(int i) {
        if (this.mHelper.getStrokeWidth() != i) {
            this.mHelper.setStrokeWidth(i);
            updateBackground();
        }
    }

    public int getStrokeWidth() {
        return this.mHelper.getStrokeWidth();
    }

    public void setStrokeColor(int i) {
        if (this.mHelper.getStrokeColor() != i) {
            this.mHelper.setStrokeColor(i);
            updateBackground();
        }
    }

    public int getStrokeColor() {
        return this.mHelper.getStrokeColor();
    }

    public void setCornerRadii(float[] fArr) {
        this.mHelper.setRadii(fArr);
        if (fArr == null) {
            this.mHelper.setRadius(0.0f);
        }
        updateBackground();
    }

    public float[] getCornerRadii() {
        if (this.mHelper.getRadii() == null) {
            return null;
        }
        return (float[]) this.mHelper.getRadii().clone();
    }

    public void setCornerRadius(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.mHelper.setRadius(f);
        this.mHelper.setRadii(null);
        updateBackground();
    }

    public float getCornerRadius() {
        return this.mHelper.getRadius();
    }

    private void updateBackground() {
        updateBounds();
        invalidateOutline();
        invalidate();
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mLayer.set(0, 0, i, i2);
        this.mSavedLayer.set(0.0f, 0.0f, i, i2);
        updateBounds();
    }

    private void updateBounds() {
        this.mHelper.onBoundsChange(this.mLayer);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        int iSaveLayer = getLayerType() != 2 ? canvas.saveLayer(this.mSavedLayer, null, 31) : -1;
        super.dispatchDraw(canvas);
        this.mHelper.drawMask(canvas, XFERMODE_DST_OUT);
        if (getLayerType() != 2) {
            canvas.restoreToCount(iSaveLayer);
        }
        this.mHelper.drawStroke(canvas);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        int iSaveLayer = getLayerType() != 2 ? canvas.saveLayer(this.mSavedLayer, null, 31) : -1;
        super.draw(canvas);
        this.mHelper.drawMask(canvas, XFERMODE_DST_OUT);
        if (getLayerType() != 2) {
            canvas.restoreToCount(iSaveLayer);
        }
        this.mHelper.drawStroke(canvas);
    }
}
