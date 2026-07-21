package miuix.popupwidget.widget;

import android.R;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.StateSet;
import androidx.core.view.ViewCompat;
import java.io.IOException;
import miuix.animation.Folme;
import miuix.animation.FolmeObject;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.utils.EaseManager;
import miuix.device.DeviceUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes3.dex */
public class PressEffectDrawable extends Drawable implements FolmeObject {
    private static final AnimConfig ACTIVATE_ENTER_CONFIG;
    private static final AnimConfig ACTIVATE_EXIT_CONFIG;
    private static final String ALPHA_F = "alphaF";
    private static final AnimConfig HOVER_ENTER_CONFIG;
    private static final AnimConfig HOVER_EXIT_CONFIG;
    private static final AnimConfig PRESS_ENTER_CONFIG;
    private static final AnimConfig PRESS_EXIT_CONFIG;
    private static final String TAG = "StateTransitionDrawable";
    private static final boolean USE_FOLME;
    private boolean mActivated;
    private float mActivatedAlpha;
    private AnimState mActivatedState;
    private Folme.ObjectFolmeImpl mFolmeAnimator;
    private boolean mHovered;
    private float mHoveredActivatedAlpha;
    private AnimState mHoveredActivatedState;
    private float mHoveredAlpha;
    private AnimState mHoveredState;
    private int mInsetB;
    private int mInsetL;
    private int mInsetR;
    private int mInsetT;
    private float mNormalAlpha;
    private AnimState mNormalState;
    private boolean mPressed;
    private float mPressedAlpha;
    private AnimState mPressedState;
    private int mTintColor;
    private static final int[] STATE_PRESSED = {R.attr.state_pressed};
    private static final int[] STATE_DRAG_HOVERED = {R.attr.state_drag_hovered};
    private static final int[] STATE_SELECTED = {R.attr.state_selected};
    private static final int[] STATE_HOVERED_ACTIVATED = {R.attr.state_hovered, R.attr.state_activated};
    private static final int[] STATE_HOVERED = {R.attr.state_hovered};
    private static final int[] STATE_ACTIVATED = {R.attr.state_activated};
    private final RectF mRect = new RectF();
    private final Paint mPaint = new Paint();
    private PressEffectState mState = new PressEffectState();

    @Override // miuix.animation.FolmeObject
    public Folme.ObjectFolmeImpl folme() {
        return null;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override // miuix.animation.FolmeObject
    public void setFolmeImpl(Folme.ObjectFolmeImpl objectFolmeImpl) {
    }

    static {
        boolean z = (DeviceUtils.isMiuiLiteV2() || DeviceUtils.isLiteV1StockPlus() || DeviceUtils.isMiuiMiddle()) ? false : true;
        USE_FOLME = z;
        if (z) {
            HOVER_ENTER_CONFIG = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.99f, 0.6f));
            HOVER_EXIT_CONFIG = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.9f, 0.2f));
            AnimConfig ease = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.99f, 0.25f));
            PRESS_ENTER_CONFIG = ease;
            AnimConfig ease2 = new AnimConfig().setEase(EaseManager.getStyle(-2, 0.99f, 0.35f));
            PRESS_EXIT_CONFIG = ease2;
            ACTIVATE_ENTER_CONFIG = ease;
            ACTIVATE_EXIT_CONFIG = ease2;
            return;
        }
        HOVER_ENTER_CONFIG = null;
        HOVER_EXIT_CONFIG = null;
        PRESS_ENTER_CONFIG = null;
        PRESS_EXIT_CONFIG = null;
        ACTIVATE_ENTER_CONFIG = null;
        ACTIVATE_EXIT_CONFIG = null;
    }

    public PressEffectDrawable() {
    }

    PressEffectDrawable(PressEffectState pressEffectState, Resources resources) {
        this.mTintColor = pressEffectState.mTintColor;
        this.mNormalAlpha = pressEffectState.mNormalAlpha;
        this.mPressedAlpha = pressEffectState.mPressedAlpha;
        this.mHoveredAlpha = pressEffectState.mHoveredAlpha;
        this.mActivatedAlpha = pressEffectState.mActivatedAlpha;
        this.mHoveredActivatedAlpha = pressEffectState.mHoveredActivatedAlpha;
        updateLocalState();
        init();
    }

    public void setAlphaF(float f) {
        this.mPaint.setAlpha((int) (f * 255.0f));
        invalidateSelf();
    }

    public float getAlphaF() {
        return this.mPaint.getAlpha() / 255.0f;
    }

    public void setInset(int i, int i2, int i3, int i4) {
        this.mInsetL = i;
        this.mInsetT = i2;
        this.mInsetR = i3;
        this.mInsetB = i4;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        if (StateSet.stateSetMatches(STATE_PRESSED, iArr) || StateSet.stateSetMatches(STATE_DRAG_HOVERED, iArr) || StateSet.stateSetMatches(STATE_SELECTED, iArr)) {
            return toPressedState();
        }
        if (StateSet.stateSetMatches(STATE_HOVERED_ACTIVATED, iArr)) {
            return toHoveredActivatedState();
        }
        if (StateSet.stateSetMatches(STATE_HOVERED, iArr)) {
            return toHoveredState();
        }
        if (StateSet.stateSetMatches(STATE_ACTIVATED, iArr)) {
            return toActivatedState();
        }
        return toNormalState();
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
            typedArrayObtainAttributes = theme.obtainStyledAttributes(attributeSet, miuix.popupwidget.R.styleable.StateTransitionDrawable, 0, 0);
        } else {
            typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, miuix.popupwidget.R.styleable.StateTransitionDrawable);
        }
        this.mTintColor = typedArrayObtainAttributes.getColor(miuix.popupwidget.R.styleable.StateTransitionDrawable_miuixDrawableTintColor, ViewCompat.MEASURED_STATE_MASK);
        this.mNormalAlpha = typedArrayObtainAttributes.getFloat(miuix.popupwidget.R.styleable.StateTransitionDrawable_normalAlpha, 0.0f);
        this.mPressedAlpha = typedArrayObtainAttributes.getFloat(miuix.popupwidget.R.styleable.StateTransitionDrawable_pressedAlpha, 0.0f);
        this.mHoveredAlpha = typedArrayObtainAttributes.getFloat(miuix.popupwidget.R.styleable.StateTransitionDrawable_hoveredAlpha, 0.0f);
        this.mActivatedAlpha = typedArrayObtainAttributes.getFloat(miuix.popupwidget.R.styleable.StateTransitionDrawable_activatedAlpha, 0.0f);
        this.mHoveredActivatedAlpha = typedArrayObtainAttributes.getFloat(miuix.popupwidget.R.styleable.StateTransitionDrawable_hoveredActivatedAlpha, 0.0f);
        typedArrayObtainAttributes.recycle();
        init();
        updateLocalState();
    }

    private void init() {
        this.mPaint.setColor(this.mTintColor);
        this.mPaint.setAlpha(0);
        if (USE_FOLME) {
            this.mNormalState = new AnimState().add(ALPHA_F, this.mNormalAlpha);
            this.mPressedState = new AnimState().add(ALPHA_F, this.mPressedAlpha);
            this.mHoveredState = new AnimState().add(ALPHA_F, this.mHoveredAlpha);
            this.mActivatedState = new AnimState().add(ALPHA_F, this.mActivatedAlpha);
            this.mHoveredActivatedState = new AnimState().add(ALPHA_F, this.mHoveredActivatedAlpha);
            return;
        }
        setAlphaF(this.mNormalAlpha);
    }

    private void updateLocalState() {
        this.mState.mTintColor = this.mTintColor;
        this.mState.mNormalAlpha = this.mNormalAlpha;
        this.mState.mPressedAlpha = this.mPressedAlpha;
        this.mState.mHoveredAlpha = this.mHoveredAlpha;
        this.mState.mActivatedAlpha = this.mActivatedAlpha;
        this.mState.mHoveredActivatedAlpha = this.mHoveredActivatedAlpha;
    }

    private boolean toPressedState() {
        if (this.mPressed) {
            return false;
        }
        if (isAnimEnabled()) {
            this.mFolmeAnimator.to(this.mPressedState, PRESS_ENTER_CONFIG);
        } else {
            setAlphaF(this.mPressedAlpha);
        }
        this.mPressed = true;
        this.mHovered = false;
        this.mActivated = false;
        return true;
    }

    private boolean toHoveredActivatedState() {
        if (this.mPressed) {
            this.mPressed = false;
            this.mHovered = true;
            this.mActivated = true;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mHoveredActivatedState, PRESS_EXIT_CONFIG);
            } else {
                setAlphaF(this.mHoveredActivatedAlpha);
            }
            return true;
        }
        boolean z = this.mHovered;
        if (z && this.mActivated) {
            return false;
        }
        if (z) {
            this.mActivated = true;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mHoveredActivatedState, ACTIVATE_ENTER_CONFIG);
            } else {
                setAlphaF(this.mHoveredActivatedAlpha);
            }
            return true;
        }
        if (this.mActivated) {
            this.mHovered = true;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mHoveredActivatedState, HOVER_ENTER_CONFIG);
            } else {
                setAlphaF(this.mHoveredActivatedAlpha);
            }
            return true;
        }
        this.mActivated = true;
        this.mHovered = true;
        if (isAnimEnabled()) {
            this.mFolmeAnimator.to(this.mHoveredActivatedState, HOVER_ENTER_CONFIG);
        } else {
            setAlphaF(this.mHoveredActivatedAlpha);
        }
        return true;
    }

    private boolean toHoveredState() {
        if (this.mPressed) {
            this.mPressed = false;
            this.mHovered = true;
            this.mActivated = false;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mHoveredState, PRESS_EXIT_CONFIG);
            } else {
                setAlphaF(this.mHoveredAlpha);
            }
            return true;
        }
        if (this.mHovered) {
            if (!this.mActivated) {
                return false;
            }
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mHoveredState, HOVER_EXIT_CONFIG);
            } else {
                setAlphaF(this.mHoveredAlpha);
            }
            return true;
        }
        this.mHovered = true;
        this.mActivated = false;
        if (isAnimEnabled()) {
            this.mFolmeAnimator.to(this.mHoveredState, HOVER_ENTER_CONFIG);
        } else {
            setAlphaF(this.mHoveredAlpha);
        }
        return true;
    }

    private boolean toActivatedState() {
        if (this.mPressed) {
            this.mPressed = false;
            this.mHovered = false;
            this.mActivated = true;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mActivatedState, PRESS_EXIT_CONFIG);
            } else {
                setAlphaF(this.mActivatedAlpha);
            }
            return true;
        }
        if (this.mHovered) {
            this.mHovered = false;
            this.mActivated = true;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mActivatedState, HOVER_EXIT_CONFIG);
            } else {
                setAlphaF(this.mActivatedAlpha);
            }
            return true;
        }
        if (this.mActivated) {
            return false;
        }
        this.mActivated = true;
        if (isAnimEnabled()) {
            this.mFolmeAnimator.to(this.mActivatedState, ACTIVATE_ENTER_CONFIG);
        } else {
            setAlphaF(this.mActivatedAlpha);
        }
        return true;
    }

    private boolean toNormalState() {
        if (this.mPressed) {
            this.mPressed = false;
            this.mHovered = false;
            this.mActivated = false;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mNormalState, PRESS_EXIT_CONFIG);
            } else {
                setAlphaF(this.mNormalAlpha);
            }
            return true;
        }
        if (this.mHovered) {
            this.mHovered = false;
            this.mActivated = false;
            if (isAnimEnabled()) {
                this.mFolmeAnimator.to(this.mNormalState, HOVER_EXIT_CONFIG);
            } else {
                setAlphaF(this.mNormalAlpha);
            }
            return true;
        }
        if (!this.mActivated) {
            return false;
        }
        this.mActivated = false;
        if (isAnimEnabled()) {
            this.mFolmeAnimator.to(this.mNormalState, ACTIVATE_EXIT_CONFIG);
        } else {
            setAlphaF(this.mNormalAlpha);
        }
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public void jumpToCurrentState() {
        if (isAnimEnabled()) {
            Folme.ObjectFolmeImpl objectFolmeImpl = this.mFolmeAnimator;
            objectFolmeImpl.setTo(objectFolmeImpl.state().getCurrentState());
        }
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
        if (USE_FOLME && this.mFolmeAnimator == null) {
            this.mFolmeAnimator = Folme.use((FolmeObject) this);
        }
        if (isVisible()) {
            canvas.drawRect(this.mRect, this.mPaint);
        }
    }

    public boolean isAnimEnabled() {
        return USE_FOLME && this.mFolmeAnimator != null;
    }

    static final class PressEffectState extends Drawable.ConstantState {
        float mActivatedAlpha;
        float mHoveredActivatedAlpha;
        float mHoveredAlpha;
        float mNormalAlpha;
        float mPressedAlpha;
        int mRadius;
        int mTintColor;

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return 0;
        }

        PressEffectState() {
        }

        PressEffectState(PressEffectState pressEffectState) {
            this.mTintColor = pressEffectState.mTintColor;
            this.mRadius = pressEffectState.mRadius;
            this.mNormalAlpha = pressEffectState.mNormalAlpha;
            this.mPressedAlpha = pressEffectState.mPressedAlpha;
            this.mHoveredAlpha = pressEffectState.mHoveredAlpha;
            this.mActivatedAlpha = pressEffectState.mActivatedAlpha;
            this.mHoveredActivatedAlpha = pressEffectState.mHoveredActivatedAlpha;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new PressEffectDrawable(new PressEffectState(this), null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new PressEffectDrawable(new PressEffectState(this), resources);
        }
    }
}
