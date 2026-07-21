package miuix.miuixbasewidget.widget;

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
import android.graphics.drawable.shapes.OvalShape;
import androidx.core.view.ViewCompat;
import miuix.animation.Folme;
import miuix.animation.FolmeEase;
import miuix.animation.FolmeObject;
import miuix.animation.base.AnimConfig;
import miuix.animation.property.ColorProperty;
import miuix.device.DeviceUtils;

/* JADX INFO: loaded from: classes2.dex */
class OvalDrawable extends Drawable implements FolmeObject {
    private static final ColorProperty<OvalDrawable> COLOR = new ColorProperty<OvalDrawable>("ovalBgColor") { // from class: miuix.miuixbasewidget.widget.OvalDrawable.1
        @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
        public void setIntValue(OvalDrawable ovalDrawable, int i) {
            ovalDrawable.setBackgroundColor(i);
        }

        @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
        public int getIntValue(OvalDrawable ovalDrawable) {
            return ovalDrawable.getBackgroundColor();
        }
    };
    private static final double COS_45;
    private static final float SHADOW_MULTIPLIER = 1.5f;
    private static final boolean USE_FOLME;
    private int mAlpha;
    private ColorStateList mBackground;
    private int mBackgroundColor;
    private final RectF mBoundsF;
    private final Rect mBoundsI;
    private boolean mDrawStroke;
    private Folme.ObjectFolmeImpl mFolmeAnimator;
    private boolean mInsetForPadding;
    private boolean mInsetForRadius;
    private boolean mIsStrokeShaderDirty;
    private boolean mIsStrokeShapeDirty;
    private float mPadding;
    private final Paint mPaint;
    private int mStrokeColor;
    private float[] mStrokeGradientColorPositions;
    private int[] mStrokeGradientColors;
    private Paint mStrokePaint;
    private Shader mStrokeShader;
    private OvalShape mStrokeShape;
    private int mStrokeWidth;
    private ColorStateList mTint;
    private PorterDuffColorFilter mTintFilter;
    private PorterDuff.Mode mTintMode;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    static {
        USE_FOLME = (DeviceUtils.isMiuiLiteV2() || DeviceUtils.isLiteV1StockPlus() || DeviceUtils.isMiuiMiddle()) ? false : true;
        COS_45 = Math.cos(Math.toRadians(45.0d));
    }

    OvalDrawable(ColorStateList colorStateList) {
        this(colorStateList, 0, 0);
    }

    OvalDrawable(ColorStateList colorStateList, int i, int i2) {
        this.mAlpha = 255;
        this.mInsetForPadding = false;
        this.mInsetForRadius = true;
        this.mDrawStroke = true;
        this.mStrokePaint = null;
        this.mTintMode = PorterDuff.Mode.SRC_IN;
        this.mPaint = new Paint(5);
        if (USE_FOLME) {
            this.mFolmeAnimator = Folme.use((FolmeObject) this);
        }
        this.mAlpha = 255;
        setBackground(colorStateList);
        this.mBoundsF = new RectF();
        this.mBoundsI = new Rect();
        this.mStrokeWidth = i;
        this.mStrokeColor = i2;
        if (i > 0) {
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
        this.mStrokeShape = new OvalShape();
        Rect bounds = getBounds();
        this.mStrokeShape.resize(bounds.width(), bounds.height());
    }

    public void enableDrawStroke(boolean z) {
        this.mDrawStroke = z;
    }

    public void setStrokeWidth(int i) {
        if (this.mStrokeWidth != i) {
            this.mStrokeWidth = i;
            this.mIsStrokeShapeDirty = true;
            Paint paint = this.mStrokePaint;
            if (paint != null) {
                paint.setStrokeWidth(i);
            } else if (i > 0) {
                createStrokePaint();
            }
            invalidateSelf();
        }
    }

    public int getStrokeWidth() {
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

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        boolean z;
        Paint paint = this.mPaint;
        Paint paint2 = this.mStrokePaint;
        if (this.mTintFilter == null || paint.getColorFilter() != null) {
            z = false;
        } else {
            paint.setColorFilter(this.mTintFilter);
            z = true;
        }
        canvas.drawOval(this.mBoundsF, paint);
        if (paint2 != null && this.mDrawStroke) {
            if (this.mIsStrokeShaderDirty && this.mStrokeGradientColors != null) {
                this.mIsStrokeShaderDirty = false;
                this.mStrokeShader = new LinearGradient(0.0f, 0.0f, 0.0f, getBounds().height(), this.mStrokeGradientColors, this.mStrokeGradientColorPositions, Shader.TileMode.CLAMP);
            }
            if (this.mIsStrokeShapeDirty) {
                updateStrokeShape();
            }
            Shader shader = this.mStrokeShader;
            if (shader != null) {
                paint2.setShader(shader);
                paint2.setColor(ViewCompat.MEASURED_STATE_MASK);
            } else {
                paint2.setColor(this.mStrokeColor);
            }
            OvalShape ovalShape = this.mStrokeShape;
            if (ovalShape != null) {
                ovalShape.draw(canvas, this.mStrokePaint);
            }
        }
        if (z) {
            paint.setColorFilter(null);
        }
    }

    private void updateBounds(Rect rect) {
        if (rect == null) {
            rect = getBounds();
        }
        this.mBoundsF.set(rect.left, rect.top, rect.right, rect.bottom);
        this.mBoundsI.set(rect);
        if (this.mInsetForPadding) {
            this.mBoundsI.inset((int) Math.ceil(calculateHorizontalPadding(this.mPadding, rect.width() / 2.0f, this.mInsetForRadius)), (int) Math.ceil(calculateVerticalPadding(this.mPadding, rect.height() / 2.0f, this.mInsetForRadius)));
            this.mBoundsF.set(this.mBoundsI);
        }
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        updateBounds(rect);
        OvalShape ovalShape = this.mStrokeShape;
        if (ovalShape != null) {
            ovalShape.resize(rect.width(), rect.height());
        }
        this.mIsStrokeShaderDirty = true;
    }

    @Override // android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        outline.setOval(this.mBoundsI);
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
                objectFolmeImpl.to(COLOR, Integer.valueOf(colorForState), new AnimConfig().setEase(FolmeEase.spring(1.0f, 0.2f)));
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

    static float calculateVerticalPadding(float f, float f2, boolean z) {
        return z ? (float) (((double) (f * 1.5f)) + ((1.0d - COS_45) * ((double) f2))) : f * 1.5f;
    }

    static float calculateHorizontalPadding(float f, float f2, boolean z) {
        return z ? (float) (((double) f) + ((1.0d - COS_45) * ((double) f2))) : f;
    }
}
