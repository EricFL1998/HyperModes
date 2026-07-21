package miuix.appcompat.app;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import java.io.IOException;
import miuix.animation.styles.ColorStateEffect;
import miuix.appcompat.R;
import miuix.internal.util.LiteUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes2.dex */
public class ColorStateDrawable extends Drawable implements ColorStateEffect.ColorObserver {
    private static final boolean USE_FOLME = !LiteUtils.isCommonLiteStrategy();
    protected float[] mAllRadii;
    private int mAlpha;
    private final Paint mPaint;
    protected final RectF mRect;
    private final ColorState mState;
    private final ColorStateEffect mStateEffect;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public ColorStateDrawable() {
        this.mRect = new RectF();
        this.mAllRadii = new float[8];
        this.mAlpha = 255;
        this.mPaint = new Paint();
        ColorStateEffect colorStateEffect = new ColorStateEffect(this);
        this.mStateEffect = colorStateEffect;
        colorStateEffect.setEnableAnim(USE_FOLME);
        this.mState = new ColorState();
    }

    ColorStateDrawable(ColorState colorState, Resources resources) {
        this.mRect = new RectF();
        this.mAllRadii = new float[8];
        this.mAlpha = 255;
        this.mPaint = new Paint();
        ColorStateEffect colorStateEffect = new ColorStateEffect(this);
        this.mStateEffect = colorStateEffect;
        colorStateEffect.setEnableAnim(USE_FOLME);
        this.mState = new ColorState(colorState);
        init();
    }

    @Override // miuix.animation.styles.ColorStateEffect.ColorObserver
    public void onColorChanged(int i) {
        this.mPaint.setColor(updateColorWithAlpha(i, this.mAlpha));
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        int iMax = Math.max(0, Math.min(i, 255));
        if (this.mAlpha != iMax) {
            this.mAlpha = iMax;
            this.mPaint.setColor(updateColorWithAlpha(this.mStateEffect.getStateColor(), this.mAlpha));
        }
    }

    private int updateColorWithAlpha(int i, int i2) {
        return (i & 16777215) | ((((i >>> 24) * i2) / 255) << 24);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mState.mIntrinsicWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mState.mIntrinsicHeight;
    }

    public void setRadius(int i) {
        if (this.mState.mRadius == i) {
            return;
        }
        this.mState.mRadius = i;
        invalidateSelf();
    }

    public void setInset(int i, int i2, int i3, int i4) {
        this.mState.mInsetL = i;
        this.mState.mInsetT = i2;
        this.mState.mInsetR = i3;
        this.mState.mInsetB = i4;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        return this.mStateEffect.onStateChange(iArr);
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.mState;
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        TypedArray typedArrayObtainAttributes;
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        if (theme != null) {
            typedArrayObtainAttributes = theme.obtainStyledAttributes(attributeSet, R.styleable.StateTransitionDrawable, 0, 0);
        } else {
            typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.StateTransitionDrawable);
        }
        this.mState.mRadius = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.StateTransitionDrawable_miuixDrawableTintRadius, 0);
        this.mState.mIntrinsicWidth = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.StateTransitionDrawable_android_width, -1);
        this.mState.mIntrinsicHeight = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.StateTransitionDrawable_android_height, -1);
        this.mState.mNormalColor = typedArrayObtainAttributes.getColor(R.styleable.StateTransitionDrawable_normalColor, 0);
        this.mState.mPressedColor = typedArrayObtainAttributes.getColor(R.styleable.StateTransitionDrawable_pressedColor, this.mState.mNormalColor);
        this.mState.mHoveredColor = typedArrayObtainAttributes.getColor(R.styleable.StateTransitionDrawable_hoveredColor, this.mState.mNormalColor);
        this.mState.mFocusedColor = typedArrayObtainAttributes.getColor(R.styleable.StateTransitionDrawable_focusedColor, this.mState.mHoveredColor);
        this.mState.mActivatedColor = typedArrayObtainAttributes.getColor(R.styleable.StateTransitionDrawable_activatedColor, this.mState.mNormalColor);
        this.mState.mCheckedColor = typedArrayObtainAttributes.getColor(R.styleable.StateTransitionDrawable_checkedColor, this.mState.mNormalColor);
        this.mState.mHoveredCheckedColor = typedArrayObtainAttributes.getColor(R.styleable.StateTransitionDrawable_hoveredCheckedColor, this.mState.mNormalColor);
        this.mState.mDisabledColor = typedArrayObtainAttributes.getColor(R.styleable.StateTransitionDrawable_disabledColor, this.mState.mNormalColor);
        typedArrayObtainAttributes.recycle();
        this.mAllRadii = new float[]{this.mState.mRadius, this.mState.mRadius, this.mState.mRadius, this.mState.mRadius, this.mState.mRadius, this.mState.mRadius, this.mState.mRadius, this.mState.mRadius};
        init();
    }

    private void init() {
        this.mPaint.setColor(this.mState.mNormalColor);
        this.mStateEffect.normalColor = this.mState.mNormalColor;
        this.mStateEffect.pressedColor = this.mState.mPressedColor;
        this.mStateEffect.hoveredColor = this.mState.mHoveredColor;
        this.mStateEffect.focusedColor = this.mState.mFocusedColor;
        this.mStateEffect.checkedColor = this.mState.mCheckedColor;
        this.mStateEffect.activatedColor = this.mState.mActivatedColor;
        this.mStateEffect.hoveredCheckedColor = this.mState.mHoveredCheckedColor;
        this.mStateEffect.disabledColor = this.mState.mDisabledColor;
        this.mStateEffect.initStates();
    }

    @Override // android.graphics.drawable.Drawable
    public void jumpToCurrentState() {
        this.mStateEffect.jumpToCurrentState();
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        this.mRect.set(rect);
        this.mRect.left += this.mState.mInsetL;
        this.mRect.top += this.mState.mInsetT;
        this.mRect.right -= this.mState.mInsetR;
        this.mRect.bottom -= this.mState.mInsetB;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        this.mStateEffect.draw(canvas);
        if (isVisible()) {
            canvas.drawRoundRect(this.mRect, this.mState.mRadius, this.mState.mRadius, this.mPaint);
        }
    }

    public int getDefaultColor() {
        return this.mState.mNormalColor;
    }

    static final class ColorState extends Drawable.ConstantState {
        int mActivatedColor;
        int mCheckedColor;
        int mDisabledColor;
        int mFocusedColor;
        int mHoveredCheckedColor;
        int mHoveredColor;
        int mInsetB;
        int mInsetL;
        int mInsetR;
        int mInsetT;
        int mIntrinsicHeight;
        int mIntrinsicWidth;
        int mNormalColor;
        int mPressedColor;
        int mRadius;

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return 0;
        }

        ColorState() {
        }

        ColorState(ColorState colorState) {
            this.mRadius = colorState.mRadius;
            this.mIntrinsicWidth = colorState.mIntrinsicWidth;
            this.mIntrinsicHeight = colorState.mIntrinsicHeight;
            this.mInsetL = colorState.mInsetL;
            this.mInsetT = colorState.mInsetT;
            this.mInsetR = colorState.mInsetR;
            this.mInsetB = colorState.mInsetB;
            this.mNormalColor = colorState.mNormalColor;
            this.mPressedColor = colorState.mPressedColor;
            this.mHoveredColor = colorState.mHoveredColor;
            this.mFocusedColor = colorState.mFocusedColor;
            this.mActivatedColor = colorState.mActivatedColor;
            this.mCheckedColor = colorState.mCheckedColor;
            this.mHoveredCheckedColor = colorState.mHoveredCheckedColor;
            this.mDisabledColor = colorState.mDisabledColor;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new ColorStateDrawable(new ColorState(this), null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new ColorStateDrawable(new ColorState(this), resources);
        }
    }
}
