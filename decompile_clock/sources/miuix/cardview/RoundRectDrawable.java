package miuix.cardview;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RoundRectShape;
import androidx.core.view.ViewCompat;
import com.miui.support.drawable.DrawableUtils;
import miuix.animation.Folme;
import miuix.animation.FolmeEase;
import miuix.animation.FolmeObject;
import miuix.animation.base.AnimConfig;
import miuix.animation.property.ColorProperty;

/* JADX INFO: loaded from: classes2.dex */
class RoundRectDrawable extends Drawable implements FolmeObject {
    private int mAlpha;
    private ColorStateList mBackground;
    private int mBackgroundColor;
    private final RectF mBoundsF;
    private final Rect mBoundsI;
    private boolean mDrawStrokeOverlay;
    private Folme.ObjectFolmeImpl mFolmeAnimator;
    private boolean mInsetForPadding;
    private boolean mInsetForRadius;
    private boolean mIsStrokeShaderDirty;
    private boolean mIsStrokeShapeDirty;
    private float mPadding;
    private final Paint mPaint;
    private float mRadius;
    private int mStrokeColor;
    private float[] mStrokeGradientColorPositions;
    private int[] mStrokeGradientColors;
    private Paint mStrokePaint;
    private Shader mStrokeShader;
    private RoundRectShape mStrokeShape;
    private float mStrokeWidth;
    private ColorStateList mTint;
    private PorterDuffColorFilter mTintFilter;
    private PorterDuff.Mode mTintMode;
    private static final ColorProperty<RoundRectDrawable> COLOR = new ColorProperty<RoundRectDrawable>("cardBgColor") { // from class: miuix.cardview.RoundRectDrawable.1
        @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
        public void setIntValue(RoundRectDrawable roundRectDrawable, int i) {
            roundRectDrawable.setBackgroundColor(i);
        }

        @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
        public int getIntValue(RoundRectDrawable roundRectDrawable) {
            return roundRectDrawable.getBackgroundColor();
        }
    };
    private static final boolean USE_FOLME = !DrawableUtils.isCommonLiteStrategy();
    private static final AnimConfig NORMAL_CONFIG = new AnimConfig().setEase(FolmeEase.spring(1.0f, 0.35f));
    private static final AnimConfig ACTIVATE_CONFIG = new AnimConfig().setEase(FolmeEase.spring(1.0f, 0.2f));

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    RoundRectDrawable(ColorStateList colorStateList, float f) {
        this(colorStateList, f, 0.0f, 0);
    }

    RoundRectDrawable(ColorStateList colorStateList, float f, float f2, int i) {
        this.mInsetForPadding = false;
        this.mInsetForRadius = true;
        this.mStrokePaint = null;
        this.mTintMode = PorterDuff.Mode.SRC_IN;
        this.mRadius = f;
        this.mPaint = new Paint(5);
        this.mAlpha = 255;
        if (USE_FOLME) {
            this.mFolmeAnimator = Folme.use((FolmeObject) this);
        }
        setBackground(colorStateList);
        this.mBoundsF = new RectF();
        this.mBoundsI = new Rect();
        this.mStrokeWidth = f2;
        this.mStrokeColor = i;
        if (f2 > 0.0f) {
            createStrokePaint();
            this.mIsStrokeShapeDirty = true;
        }
    }

    @Override // miuix.animation.FolmeObject
    public void setFolmeImpl(Folme.ObjectFolmeImpl objectFolmeImpl) {
        this.mFolmeAnimator = objectFolmeImpl;
    }

    @Override // miuix.animation.FolmeObject
    public Folme.ObjectFolmeImpl folme() {
        return this.mFolmeAnimator;
    }

    private void setBackground(ColorStateList colorStateList) {
        if (colorStateList == null) {
            colorStateList = ColorStateList.valueOf(0);
        }
        this.mBackground = colorStateList;
        int colorForState = colorStateList.getColorForState(getState(), this.mBackground.getDefaultColor());
        this.mPaint.setColor(updateColorWithAlpha(colorForState, this.mAlpha));
        Folme.ObjectFolmeImpl objectFolmeImpl = this.mFolmeAnimator;
        if (objectFolmeImpl != null) {
            objectFolmeImpl.setTo(COLOR, Integer.valueOf(colorForState));
        } else {
            setBackgroundColor(colorForState);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBackgroundColor(int i) {
        if (this.mBackgroundColor != i) {
            this.mBackgroundColor = i;
            this.mPaint.setColor(updateColorWithAlpha(i, this.mAlpha));
            invalidateSelf();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getBackgroundColor() {
        return this.mBackgroundColor;
    }

    void setPadding(float f, boolean z, boolean z2) {
        if (f == this.mPadding && this.mInsetForPadding == z && this.mInsetForRadius == z2) {
            return;
        }
        this.mPadding = f;
        this.mInsetForPadding = z;
        this.mInsetForRadius = z2;
        updateBounds(null);
        invalidateSelf();
    }

    float getPadding() {
        return this.mPadding;
    }

    private void createStrokePaint() {
        Paint paint = new Paint(1);
        this.mStrokePaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.mStrokePaint.setColor(this.mStrokeColor);
        this.mStrokePaint.setStrokeWidth(this.mStrokeWidth);
    }

    private void updateStrokeShape() {
        this.mIsStrokeShapeDirty = false;
        float f = this.mRadius;
        float[] fArr = {f, f, f, f, f, f, f, f};
        float f2 = f - this.mStrokeWidth;
        float f3 = this.mStrokeWidth;
        this.mStrokeShape = new RoundRectShape(fArr, new RectF(f3, f3, f3, f3), new float[]{f2, f2, f2, f2, f2, f2, f2, f2});
        Rect bounds = getBounds();
        this.mStrokeShape.resize(bounds.width(), bounds.height());
    }

    public void setStrokeWidth(float f) {
        if (this.mStrokeWidth != f) {
            this.mStrokeWidth = f;
            this.mIsStrokeShapeDirty = true;
            Paint paint = this.mStrokePaint;
            if (paint != null) {
                paint.setStrokeWidth(f);
            } else if (f > 0.0f) {
                createStrokePaint();
            }
            invalidateSelf();
        }
    }

    public float getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public void setStrokeColor(int i) {
        if (this.mStrokeColor != i) {
            this.mStrokeColor = i;
            this.mIsStrokeShapeDirty = true;
            Paint paint = this.mStrokePaint;
            if (paint != null) {
                paint.setColor(i);
            }
            invalidateSelf();
        }
    }

    public int getStrokeColor() {
        return this.mStrokeColor;
    }

    public void setStrokeGradientColors(int[] iArr) {
        this.mStrokeGradientColors = iArr;
        this.mIsStrokeShaderDirty = true;
    }

    public void setStrokeColorGradientPositions(float[] fArr) {
        this.mStrokeGradientColorPositions = fArr;
        this.mIsStrokeShaderDirty = true;
    }

    public void setDrawStrokeOverlay(boolean z) {
        if (this.mDrawStrokeOverlay != z) {
            this.mDrawStrokeOverlay = z;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        boolean z;
        Paint paint = this.mPaint;
        if (this.mTintFilter == null || paint.getColorFilter() != null) {
            z = false;
        } else {
            paint.setColorFilter(this.mTintFilter);
            z = true;
        }
        RectF rectF = this.mBoundsF;
        float f = this.mRadius;
        canvas.drawRoundRect(rectF, f, f, paint);
        if (!this.mDrawStrokeOverlay) {
            drawStroke(canvas);
        }
        if (z) {
            paint.setColorFilter(null);
        }
    }

    public void drawStroke(Canvas canvas) {
        Paint paint = this.mStrokePaint;
        if (paint != null) {
            if (this.mIsStrokeShaderDirty && this.mStrokeGradientColors != null) {
                this.mIsStrokeShaderDirty = false;
                this.mStrokeShader = new LinearGradient(0.0f, 0.0f, 0.0f, getBounds().height(), this.mStrokeGradientColors, this.mStrokeGradientColorPositions, Shader.TileMode.CLAMP);
            }
            if (this.mIsStrokeShapeDirty) {
                updateStrokeShape();
            }
            Shader shader = this.mStrokeShader;
            if (shader != null) {
                paint.setShader(shader);
                paint.setColor(ViewCompat.MEASURED_STATE_MASK);
            } else {
                paint.setColor(this.mStrokeColor);
            }
            RoundRectShape roundRectShape = this.mStrokeShape;
            if (roundRectShape != null) {
                roundRectShape.draw(canvas, this.mStrokePaint);
            }
        }
    }

    private void updateBounds(Rect rect) {
        if (rect == null) {
            rect = getBounds();
        }
        this.mBoundsF.set(rect.left, rect.top, rect.right, rect.bottom);
        this.mBoundsI.set(rect);
        if (this.mInsetForPadding) {
            this.mBoundsI.inset((int) Math.ceil(RoundRectDrawableWithShadow.calculateHorizontalPadding(this.mPadding, this.mRadius, this.mInsetForRadius)), (int) Math.ceil(RoundRectDrawableWithShadow.calculateVerticalPadding(this.mPadding, this.mRadius, this.mInsetForRadius)));
            this.mBoundsF.set(this.mBoundsI);
        }
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        updateBounds(rect);
        RoundRectShape roundRectShape = this.mStrokeShape;
        if (roundRectShape != null) {
            roundRectShape.resize(rect.width(), rect.height());
        }
        this.mIsStrokeShaderDirty = true;
    }

    @Override // android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        outline.setRoundRect(this.mBoundsI, this.mRadius);
    }

    void setRadius(float f) {
        if (f == this.mRadius) {
            return;
        }
        this.mRadius = f;
        updateBounds(null);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        int iMax = Math.max(0, Math.min(i, 255));
        if (this.mAlpha != iMax) {
            this.mAlpha = iMax;
            this.mPaint.setColor(updateColorWithAlpha(this.mBackgroundColor, iMax));
        }
    }

    private int updateColorWithAlpha(int i, int i2) {
        return (i & 16777215) | ((((i >>> 24) * i2) / 255) << 24);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    public float getRadius() {
        return this.mRadius;
    }

    public void setColor(ColorStateList colorStateList) {
        setBackground(colorStateList);
        invalidateSelf();
    }

    public ColorStateList getColor() {
        return this.mBackground;
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintList(ColorStateList colorStateList) {
        this.mTint = colorStateList;
        this.mTintFilter = createTintFilter(colorStateList, this.mTintMode);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintMode(PorterDuff.Mode mode) {
        this.mTintMode = mode;
        this.mTintFilter = createTintFilter(this.mTint, mode);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        PorterDuff.Mode mode;
        ColorStateList colorStateList = this.mBackground;
        int colorForState = colorStateList.getColorForState(iArr, colorStateList.getDefaultColor());
        boolean z = colorForState != this.mPaint.getColor();
        if (z) {
            Folme.ObjectFolmeImpl objectFolmeImpl = this.mFolmeAnimator;
            if (objectFolmeImpl != null) {
                objectFolmeImpl.to(COLOR, Integer.valueOf(colorForState), ACTIVATE_CONFIG);
            } else {
                this.mPaint.setColor(colorForState);
            }
        }
        ColorStateList colorStateList2 = this.mTint;
        if (colorStateList2 == null || (mode = this.mTintMode) == null) {
            return z;
        }
        this.mTintFilter = createTintFilter(colorStateList2, mode);
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        ColorStateList colorStateList;
        ColorStateList colorStateList2 = this.mTint;
        return (colorStateList2 != null && colorStateList2.isStateful()) || ((colorStateList = this.mBackground) != null && colorStateList.isStateful()) || super.isStateful();
    }

    private PorterDuffColorFilter createTintFilter(ColorStateList colorStateList, PorterDuff.Mode mode) {
        if (colorStateList == null || mode == null) {
            return null;
        }
        return new PorterDuffColorFilter(colorStateList.getColorForState(getState(), 0), mode);
    }
}
