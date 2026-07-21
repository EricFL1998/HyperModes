package miuix.smooth;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/* JADX INFO: loaded from: classes3.dex */
public class SmoothFrameLayout2 extends FrameLayout {
    private static final String TAG = "SmoothFrameLayout2";
    private boolean mClip;
    private Path mClipPath;
    private RectF mLayer;
    private Paint mPaintSolid;
    private Paint mPaintStroke;
    private float[] mRadii;
    private float mRadius;
    private int mStrokeColor;
    private int mStrokeWidth;
    private float[] mTempRadii;
    private boolean mUseSmooth;

    public SmoothFrameLayout2(Context context) {
        this(context, null);
    }

    public SmoothFrameLayout2(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SmoothFrameLayout2(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLayer = new RectF();
        this.mClipPath = new Path();
        this.mClip = false;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.MiuixSmoothFrameLayout2);
        this.mRadius = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothFrameLayout2_android_radius, 0);
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixSmoothFrameLayout2_android_topLeftRadius) || typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixSmoothFrameLayout2_android_topRightRadius) || typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixSmoothFrameLayout2_android_bottomRightRadius) || typedArrayObtainStyledAttributes.hasValue(R.styleable.MiuixSmoothFrameLayout2_android_bottomLeftRadius)) {
            float dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothFrameLayout2_android_topLeftRadius, 0);
            float dimensionPixelSize2 = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothFrameLayout2_android_topRightRadius, 0);
            float dimensionPixelSize3 = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothFrameLayout2_android_bottomRightRadius, 0);
            float dimensionPixelSize4 = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothFrameLayout2_android_bottomLeftRadius, 0);
            setCornerRadii(new float[]{dimensionPixelSize, dimensionPixelSize, dimensionPixelSize2, dimensionPixelSize2, dimensionPixelSize3, dimensionPixelSize3, dimensionPixelSize4, dimensionPixelSize4});
        }
        this.mStrokeWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.MiuixSmoothFrameLayout2_miuix_strokeWidth, 0);
        this.mStrokeColor = typedArrayObtainStyledAttributes.getColor(R.styleable.MiuixSmoothFrameLayout2_miuix_strokeColor, 0);
        this.mUseSmooth = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MiuixSmoothContainerDrawable2_miuix_useSmooth, true);
        if (SmoothCornerHelper.FORCE_USE_SMOOTH != null) {
            this.mUseSmooth = SmoothCornerHelper.FORCE_USE_SMOOTH.booleanValue();
        }
        if (this.mUseSmooth) {
            setSmoothCornerEnable(true);
        }
        typedArrayObtainStyledAttributes.recycle();
        Paint paint = new Paint();
        this.mPaintSolid = paint;
        paint.setFlags(1);
        Paint paint2 = new Paint();
        this.mPaintStroke = paint2;
        paint2.setFlags(1);
        this.mPaintStroke.setStyle(Paint.Style.STROKE);
        this.mPaintStroke.setStrokeWidth(this.mStrokeWidth);
        this.mPaintStroke.setColor(this.mStrokeColor);
    }

    public void setStrokeWidth(int i) {
        this.mStrokeWidth = i;
        updateBackground();
    }

    public void setUseSmooth(boolean z) {
        this.mUseSmooth = z;
        setSmoothCornerEnable(z);
    }

    public boolean getUseSmooth() {
        return this.mUseSmooth;
    }

    private void setSmoothCornerEnable(boolean z) {
        SmoothCornerHelper.setViewSmoothCornerEnable(this, z);
    }

    public int getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public void setStrokeColor(int i) {
        this.mStrokeColor = i;
        updateBackground();
    }

    public int getStrokeColor() {
        return this.mStrokeColor;
    }

    public void setCornerRadii(float[] fArr) {
        this.mRadii = fArr;
        updateBackground();
    }

    public float[] getCornerRadii() {
        return (float[]) this.mRadii.clone();
    }

    public void setCornerRadius(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.mRadius = f;
        this.mRadii = null;
        updateBackground();
    }

    public float getCornerRadius() {
        return this.mRadius;
    }

    private void updateBackground() {
        invalidateOutline();
        invalidate();
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mLayer.set(0.0f, 0.0f, i, i2);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        int iSave = canvas.save();
        clipRoundRect(canvas);
        this.mClip = true;
        if (this.mStrokeWidth > 0) {
            int iSave2 = canvas.save();
            clipInnerRoundRect(canvas);
            super.draw(canvas);
            canvas.restoreToCount(iSave2);
        } else {
            super.draw(canvas);
        }
        if (this.mStrokeWidth > 0) {
            drawRoundRectStroke(canvas);
        }
        this.mClip = false;
        canvas.restoreToCount(iSave);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        int iSave = canvas.save();
        if (!this.mClip) {
            clipRoundRect(canvas);
        }
        if (this.mStrokeWidth > 0) {
            int iSave2 = canvas.save();
            clipInnerRoundRect(canvas);
            super.dispatchDraw(canvas);
            canvas.restoreToCount(iSave2);
        } else {
            super.dispatchDraw(canvas);
        }
        if (!this.mClip && this.mStrokeWidth > 0) {
            drawRoundRectStroke(canvas);
        }
        canvas.restoreToCount(iSave);
    }

    private void clipRoundRect(Canvas canvas) {
        this.mClipPath.reset();
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
    }

    private void clipInnerRoundRect(Canvas canvas) {
        this.mClipPath.reset();
        float f = this.mStrokeWidth * 0.5f;
        float[] fArr = this.mRadii;
        if (fArr == null) {
            Path path = this.mClipPath;
            float f2 = this.mLayer.left + f;
            float f3 = this.mLayer.top + f;
            float f4 = this.mLayer.right - f;
            float f5 = this.mLayer.bottom - f;
            float f6 = this.mRadius;
            path.addRoundRect(f2, f3, f4, f5, f6 - f, f6 - f, Path.Direction.CW);
        } else {
            float[] fArr2 = (float[]) fArr.clone();
            this.mTempRadii = fArr2;
            float[] fArr3 = this.mRadii;
            fArr2[0] = fArr3[0] - f;
            fArr2[1] = fArr3[1] - f;
            fArr2[2] = fArr3[2] - f;
            fArr2[3] = fArr3[3] - f;
            this.mClipPath.addRoundRect(this.mLayer.left + f, this.mLayer.top + f, this.mLayer.right - f, this.mLayer.bottom - f, this.mTempRadii, Path.Direction.CW);
        }
        canvas.clipPath(this.mClipPath);
    }

    private void drawRoundRectStroke(Canvas canvas) {
        this.mClipPath.reset();
        float f = this.mStrokeWidth * 0.5f;
        float[] fArr = this.mRadii;
        if (fArr == null) {
            Path path = this.mClipPath;
            float f2 = this.mLayer.left + f;
            float f3 = this.mLayer.top + f;
            float f4 = this.mLayer.right - f;
            float f5 = this.mLayer.bottom - f;
            float f6 = this.mRadius;
            path.addRoundRect(f2, f3, f4, f5, f6 + f, f6 + f, Path.Direction.CW);
        } else {
            float[] fArr2 = (float[]) fArr.clone();
            this.mTempRadii = fArr2;
            float[] fArr3 = this.mRadii;
            fArr2[0] = fArr3[0] + f;
            fArr2[1] = fArr3[1] + f;
            fArr2[2] = fArr3[2] + f;
            fArr2[3] = fArr3[3] + f;
            this.mClipPath.addRoundRect(this.mLayer.left + f, this.mLayer.top + f, this.mLayer.right - f, this.mLayer.bottom - f, this.mTempRadii, Path.Direction.CW);
        }
        canvas.drawPath(this.mClipPath, this.mPaintStroke);
    }
}
