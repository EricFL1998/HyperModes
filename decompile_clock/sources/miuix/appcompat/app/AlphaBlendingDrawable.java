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
import androidx.core.view.ViewCompat;
import java.io.IOException;
import miuix.animation.styles.AlphaBlendingStateEffect;
import miuix.appcompat.R;
import miuix.internal.util.LiteUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes2.dex */
public class AlphaBlendingDrawable extends Drawable implements AlphaBlendingStateEffect.AlphaObserver {
    private static final boolean USE_FOLME = !LiteUtils.isCommonLiteStrategy();
    private float mActivatedAlpha;
    protected float[] mAllRadii;
    private int mAlpha;
    private float mCheckedAlpha;
    private float mDisabledAlpha;
    private float mFocusedAlpha;
    private float mHoveredAlpha;
    private float mHoveredCheckedAlpha;
    private int mInsetB;
    private int mInsetL;
    private int mInsetR;
    private int mInsetT;
    private int mMinHeight;
    private int mMinWidth;
    private float mNormalAlpha;
    private final Paint mPaint;
    private float mPressedAlpha;
    private int mRadius;
    protected final RectF mRect;
    private final AlphaBlendingState mState;
    private final AlphaBlendingStateEffect mStateEffect;
    private int mTintColor;

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

    public AlphaBlendingDrawable() {
        this.mRect = new RectF();
        this.mAllRadii = new float[8];
        this.mPaint = new Paint();
        this.mAlpha = 255;
        AlphaBlendingStateEffect alphaBlendingStateEffect = new AlphaBlendingStateEffect(this);
        this.mStateEffect = alphaBlendingStateEffect;
        alphaBlendingStateEffect.setEnableAnim(USE_FOLME);
        this.mState = new AlphaBlendingState();
    }

    AlphaBlendingDrawable(AlphaBlendingState alphaBlendingState, Resources resources) {
        this.mRect = new RectF();
        this.mAllRadii = new float[8];
        this.mPaint = new Paint();
        this.mAlpha = 255;
        AlphaBlendingStateEffect alphaBlendingStateEffect = new AlphaBlendingStateEffect(this);
        this.mStateEffect = alphaBlendingStateEffect;
        alphaBlendingStateEffect.setEnableAnim(USE_FOLME);
        this.mTintColor = alphaBlendingState.mTintColor;
        this.mMinHeight = alphaBlendingState.mMinHeight;
        this.mRadius = alphaBlendingState.mRadius;
        this.mNormalAlpha = alphaBlendingState.mNormalAlpha;
        this.mPressedAlpha = alphaBlendingState.mPressedAlpha;
        this.mHoveredAlpha = alphaBlendingState.mHoveredAlpha;
        this.mFocusedAlpha = alphaBlendingState.mFocusedAlpha;
        this.mActivatedAlpha = alphaBlendingState.mActivatedAlpha;
        this.mCheckedAlpha = alphaBlendingState.mCheckedAlpha;
        this.mHoveredCheckedAlpha = alphaBlendingState.mHoveredCheckedAlpha;
        this.mDisabledAlpha = alphaBlendingState.mDisabledAlpha;
        this.mState = new AlphaBlendingState();
        updateLocalState();
        init();
    }

    @Override // miuix.animation.styles.AlphaBlendingStateEffect.AlphaObserver
    public void onAlphaChanged(float f) {
        this.mPaint.setAlpha((int) (((Math.min(Math.max(f, 0.0f), 1.0f) * this.mAlpha) / 255.0f) * 255.0f));
        invalidateSelf();
    }

    public void setRadius(int i) {
        if (this.mRadius == i) {
            return;
        }
        this.mRadius = i;
        this.mState.mRadius = i;
        invalidateSelf();
    }

    public int getTintColor() {
        return this.mTintColor;
    }

    public void setInset(int i, int i2, int i3, int i4) {
        this.mInsetL = i;
        this.mInsetT = i2;
        this.mInsetR = i3;
        this.mInsetB = i4;
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
        this.mTintColor = typedArrayObtainAttributes.getColor(R.styleable.StateTransitionDrawable_miuixDrawableTintColor, ViewCompat.MEASURED_STATE_MASK);
        this.mRadius = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.StateTransitionDrawable_miuixDrawableTintRadius, 0);
        this.mNormalAlpha = typedArrayObtainAttributes.getFloat(R.styleable.StateTransitionDrawable_normalAlpha, 0.0f);
        this.mPressedAlpha = typedArrayObtainAttributes.getFloat(R.styleable.StateTransitionDrawable_pressedAlpha, 0.0f);
        this.mHoveredAlpha = typedArrayObtainAttributes.getFloat(R.styleable.StateTransitionDrawable_hoveredAlpha, 0.0f);
        this.mFocusedAlpha = typedArrayObtainAttributes.getFloat(R.styleable.StateTransitionDrawable_focusedAlpha, this.mHoveredAlpha);
        this.mActivatedAlpha = typedArrayObtainAttributes.getFloat(R.styleable.StateTransitionDrawable_activatedAlpha, 0.0f);
        this.mCheckedAlpha = typedArrayObtainAttributes.getFloat(R.styleable.StateTransitionDrawable_checkedAlpha, 0.0f);
        this.mHoveredCheckedAlpha = typedArrayObtainAttributes.getFloat(R.styleable.StateTransitionDrawable_hoveredCheckedAlpha, 0.0f);
        this.mDisabledAlpha = typedArrayObtainAttributes.getFloat(R.styleable.StateTransitionDrawable_disabledAlpha, 0.0f);
        this.mMinWidth = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.StateTransitionDrawable_android_width, -1);
        this.mMinHeight = typedArrayObtainAttributes.getDimensionPixelSize(R.styleable.StateTransitionDrawable_android_height, -1);
        typedArrayObtainAttributes.recycle();
        int i = this.mRadius;
        this.mAllRadii = new float[]{i, i, i, i, i, i, i, i};
        init();
        updateLocalState();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mState.mMinWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mState.mMinHeight;
    }

    private void init() {
        this.mPaint.setColor(this.mTintColor);
        this.mStateEffect.normalAlpha = this.mNormalAlpha;
        this.mStateEffect.pressedAlpha = this.mPressedAlpha;
        this.mStateEffect.hoveredAlpha = this.mHoveredAlpha;
        this.mStateEffect.focusedAlpha = this.mFocusedAlpha;
        this.mStateEffect.checkedAlpha = this.mCheckedAlpha;
        this.mStateEffect.activatedAlpha = this.mActivatedAlpha;
        this.mStateEffect.hoveredCheckedAlpha = this.mHoveredCheckedAlpha;
        this.mStateEffect.disabledAlpha = this.mDisabledAlpha;
        this.mStateEffect.initStates();
    }

    private void updateLocalState() {
        this.mState.mTintColor = this.mTintColor;
        this.mState.mAlpha = this.mAlpha;
        this.mState.mMinWidth = this.mMinWidth;
        this.mState.mMinHeight = this.mMinHeight;
        this.mState.mRadius = this.mRadius;
        this.mState.mNormalAlpha = this.mNormalAlpha;
        this.mState.mPressedAlpha = this.mPressedAlpha;
        this.mState.mHoveredAlpha = this.mHoveredAlpha;
        this.mState.mFocusedAlpha = this.mFocusedAlpha;
        this.mState.mActivatedAlpha = this.mActivatedAlpha;
        this.mState.mCheckedAlpha = this.mCheckedAlpha;
        this.mState.mHoveredCheckedAlpha = this.mHoveredCheckedAlpha;
        this.mState.mDisabledAlpha = this.mDisabledAlpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void jumpToCurrentState() {
        this.mStateEffect.jumpToCurrentState();
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        this.mRect.set(rect);
        this.mRect.left += this.mInsetL;
        this.mRect.top += this.mInsetT;
        this.mRect.right -= this.mInsetR;
        this.mRect.bottom -= this.mInsetB;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        this.mStateEffect.draw(canvas);
        if (isVisible()) {
            RectF rectF = this.mRect;
            int i = this.mRadius;
            canvas.drawRoundRect(rectF, i, i, this.mPaint);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        if (this.mAlpha != i) {
            this.mAlpha = i;
            this.mState.mAlpha = i;
            this.mPaint.setAlpha((int) (i * this.mStateEffect.getAlphaF()));
            invalidateSelf();
        }
    }

    static final class AlphaBlendingState extends Drawable.ConstantState {
        float mActivatedAlpha;
        int mAlpha;
        float mCheckedAlpha;
        float mDisabledAlpha;
        float mFocusedAlpha;
        float mHoveredAlpha;
        float mHoveredCheckedAlpha;
        int mMinHeight;
        int mMinWidth;
        float mNormalAlpha;
        float mPressedAlpha;
        int mRadius;
        int mTintColor;

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return 0;
        }

        AlphaBlendingState() {
        }

        AlphaBlendingState(AlphaBlendingState alphaBlendingState) {
            this.mTintColor = alphaBlendingState.mTintColor;
            this.mAlpha = alphaBlendingState.mAlpha;
            this.mMinWidth = alphaBlendingState.mMinWidth;
            this.mMinHeight = alphaBlendingState.mMinHeight;
            this.mRadius = alphaBlendingState.mRadius;
            this.mNormalAlpha = alphaBlendingState.mNormalAlpha;
            this.mPressedAlpha = alphaBlendingState.mPressedAlpha;
            this.mHoveredAlpha = alphaBlendingState.mHoveredAlpha;
            this.mFocusedAlpha = alphaBlendingState.mFocusedAlpha;
            this.mActivatedAlpha = alphaBlendingState.mActivatedAlpha;
            this.mCheckedAlpha = alphaBlendingState.mCheckedAlpha;
            this.mHoveredCheckedAlpha = alphaBlendingState.mHoveredCheckedAlpha;
            this.mDisabledAlpha = alphaBlendingState.mDisabledAlpha;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new AlphaBlendingDrawable(new AlphaBlendingState(this), null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new AlphaBlendingDrawable(new AlphaBlendingState(this), resources);
        }
    }
}
