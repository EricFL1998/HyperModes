package miuix.slidingwidget.widget;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CompoundButton;
import androidx.appcompat.widget.ViewUtils;
import miuix.animation.physics.DynamicAnimation;
import miuix.animation.physics.SpringAnimation;
import miuix.animation.property.FloatProperty;
import miuix.smooth.SmoothContainerDrawable2;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: loaded from: classes3.dex */
public class SlidingButtonHelper {
    private static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
    public static final int FULL_ALPHA = 255;
    private boolean mAnimChecked;
    private Drawable mBarOn;
    private int mCornerRadius;
    private int mHeight;
    private int mLastX;
    private int mMarginHorizontal;
    private int mMarginVertical;
    private SpringAnimation mMarkedAlphaHideAnim;
    private SpringAnimation mMarkedAlphaShowAnim;
    private Drawable mMaskCheckedSlideBar;
    private float mMaskCheckedSlideBarAlpha;
    private Drawable mMaskUnCheckedPressedSlideBar;
    private Drawable mMaskUnCheckedSlideBar;
    private int mMaxTranslateOffset;
    private CompoundButton.OnCheckedChangeListener mOnPerformCheckedChangeListener;
    private int mOriginalTouchPointX;
    private StateListDrawable mSlideBar;
    private int mSliderHeight;
    private SpringAnimation mSliderMoveAnim;
    private boolean mSliderMoved;
    private Drawable mSliderOff;
    private int mSliderOffset;
    private Drawable mSliderOn;
    private int mSliderOnAlpha;
    private int mSliderPaddingH;
    private int mSliderPositionEnd;
    private int mSliderPositionStart;
    private SpringAnimation mSliderPressedAnim;
    private SpringAnimation mSliderUnPressedAnim;
    private int mSliderWidth;
    private int mSlidingBarColor;
    private int mTapThreshold;
    private boolean mTracking;
    private CompoundButton mView;
    private int mWidth;
    private final Rect mTmpRect = new Rect();
    private boolean mIsSliderEdgeReached = false;
    private final FloatProperty<CompoundButton> mSliderOffsetProperty = new FloatProperty<CompoundButton>("SliderOffset") { // from class: miuix.slidingwidget.widget.SlidingButtonHelper.1
        @Override // miuix.animation.property.FloatProperty
        public float getValue(CompoundButton compoundButton) {
            return SlidingButtonHelper.this.getSliderOffset();
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(CompoundButton compoundButton, float f) {
            SlidingButtonHelper.this.setSliderOffset((int) f);
        }
    };
    private float mSliderScale = 1.0f;
    private boolean mParamCached = false;
    private int mSliderOffsetTemp = -1;
    private int mSliderOnAlphaTemp = -1;
    private boolean mAnimCheckedTemp = false;
    private float mMaskCheckedSlideBarAlphaTemp = -1.0f;
    private final FloatProperty<CompoundButton> mSliderScaleFloatProperty = new FloatProperty<CompoundButton>("SliderScale") { // from class: miuix.slidingwidget.widget.SlidingButtonHelper.2
        @Override // miuix.animation.property.FloatProperty
        public float getValue(CompoundButton compoundButton) {
            return SlidingButtonHelper.this.mSliderScale;
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(CompoundButton compoundButton, float f) {
            SlidingButtonHelper.this.mSliderScale = f;
        }
    };
    private final DynamicAnimation.OnAnimationUpdateListener mInvalidateUpdateListener = new DynamicAnimation.OnAnimationUpdateListener() { // from class: miuix.slidingwidget.widget.SlidingButtonHelper$$ExternalSyntheticLambda0
        @Override // miuix.animation.physics.DynamicAnimation.OnAnimationUpdateListener
        public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
            this.f$0.m1940lambda$new$0$miuixslidingwidgetwidgetSlidingButtonHelper(dynamicAnimation, f, f2);
        }
    };
    private final FloatProperty<CompoundButton> mMaskCheckedSlideBarAlphaProperty = new FloatProperty<CompoundButton>("MaskCheckedSlideBarAlpha") { // from class: miuix.slidingwidget.widget.SlidingButtonHelper.3
        @Override // miuix.animation.property.FloatProperty
        public float getValue(CompoundButton compoundButton) {
            return SlidingButtonHelper.this.mMaskCheckedSlideBarAlpha;
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(CompoundButton compoundButton, float f) {
            SlidingButtonHelper.this.mMaskCheckedSlideBarAlpha = f;
        }
    };
    private float mExtraAlpha = 1.0f;
    private float[] mTranslateDist = {0.0f, 0.0f};

    /* JADX INFO: renamed from: lambda$new$0$miuix-slidingwidget-widget-SlidingButtonHelper, reason: not valid java name */
    /* synthetic */ void m1940lambda$new$0$miuixslidingwidgetwidgetSlidingButtonHelper(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.mView.invalidate();
    }

    public SlidingButtonHelper(CompoundButton compoundButton) {
        this.mMaskCheckedSlideBarAlpha = 1.0f;
        this.mView = compoundButton;
        this.mAnimChecked = compoundButton.isChecked();
        if (this.mView.isChecked()) {
            return;
        }
        this.mMaskCheckedSlideBarAlpha = 0.0f;
    }

    public void setSliderDrawState() {
        if (getSliderOn() != null) {
            this.mSliderOn.setState(this.mView.getDrawableState());
            this.mSliderOff.setState(this.mView.getDrawableState());
            this.mSlideBar.setState(this.mView.getDrawableState());
            this.mMaskCheckedSlideBar.setState(this.mView.getDrawableState());
            this.mMaskUnCheckedSlideBar.setState(this.mView.getDrawableState());
        }
    }

    public int getMeasuredWidth() {
        return this.mWidth;
    }

    public int getMeasuredHeight() {
        return this.mHeight;
    }

    public Drawable getSliderOn() {
        return this.mSliderOn;
    }

    public Drawable getBarOn() {
        return this.mBarOn;
    }

    public StateListDrawable getSlideBar() {
        return this.mSlideBar;
    }

    public void initResource(Context context, TypedArray typedArray) {
        this.mCornerRadius = this.mView.getResources().getDimensionPixelSize(miuix.slidingwidget.R.dimen.miuix_appcompat_sliding_button_frame_corner_radius);
        this.mMarginVertical = this.mView.getResources().getDimensionPixelSize(miuix.slidingwidget.R.dimen.miuix_appcompat_sliding_button_mask_vertical_padding);
        this.mMarginHorizontal = this.mView.getResources().getDimensionPixelSize(miuix.slidingwidget.R.dimen.miuix_appcompat_sliding_button_mask_horizontal_padding);
        this.mView.setDrawingCacheEnabled(false);
        this.mTapThreshold = ViewConfiguration.get(context).getScaledTouchSlop() / 2;
        this.mSliderOn = typedArray.getDrawable(miuix.slidingwidget.R.styleable.SlidingButton_sliderOn);
        this.mSliderOff = typedArray.getDrawable(miuix.slidingwidget.R.styleable.SlidingButton_sliderOff);
        this.mView.setBackground(typedArray.getDrawable(miuix.slidingwidget.R.styleable.SlidingButton_android_background));
        Color.parseColor("#FF3482FF");
        this.mSlidingBarColor = typedArray.getColor(miuix.slidingwidget.R.styleable.SlidingButton_slidingBarColor, context.getColor(miuix.slidingwidget.R.color.miuix_appcompat_sliding_button_bar_on_light));
        int dimensionPixelSize = this.mView.getResources().getDimensionPixelSize(miuix.slidingwidget.R.dimen.miuix_appcompat_sliding_button_frame_vertical_padding);
        int dimensionPixelSize2 = this.mView.getResources().getDimensionPixelSize(miuix.slidingwidget.R.dimen.miuix_appcompat_sliding_button_frame_horizontal_padding);
        int dimensionPixelSize3 = this.mView.getResources().getDimensionPixelSize(miuix.slidingwidget.R.dimen.miuix_appcompat_sliding_button_height);
        this.mWidth = (dimensionPixelSize2 * 2) + this.mView.getResources().getDimensionPixelSize(miuix.slidingwidget.R.dimen.miuix_appcompat_sliding_button_width);
        this.mHeight = (dimensionPixelSize * 2) + dimensionPixelSize3;
        int dimensionPixelSize4 = this.mView.getResources().getDimensionPixelSize(miuix.slidingwidget.R.dimen.miuix_appcompat_sliding_button_slider_size);
        int dimensionPixelSize5 = this.mView.getResources().getDimensionPixelSize(miuix.slidingwidget.R.dimen.miuix_appcompat_sliding_button_slider_horizontal_padding);
        this.mSliderPaddingH = dimensionPixelSize5;
        this.mSliderWidth = dimensionPixelSize4;
        this.mSliderHeight = dimensionPixelSize4;
        this.mSliderPositionStart = 0;
        this.mSliderPositionEnd = (this.mWidth - dimensionPixelSize4) - (dimensionPixelSize5 * 2);
        this.mSliderOffset = 0;
        TypedValue typedValue = new TypedValue();
        typedArray.getValue(miuix.slidingwidget.R.styleable.SlidingButton_barOff, typedValue);
        TypedValue typedValue2 = new TypedValue();
        typedArray.getValue(miuix.slidingwidget.R.styleable.SlidingButton_barOn, typedValue2);
        Drawable drawable = typedArray.getDrawable(miuix.slidingwidget.R.styleable.SlidingButton_barOff);
        this.mBarOn = typedArray.getDrawable(miuix.slidingwidget.R.styleable.SlidingButton_barOn);
        Drawable drawable2 = (typedValue.type == typedValue2.type && typedValue.data == typedValue2.data && typedValue.resourceId == typedValue2.resourceId) ? drawable : this.mBarOn;
        if (drawable2 != null && drawable != null) {
            drawable2.setTint(this.mSlidingBarColor);
            initMaskedSlideBar(createMaskDrawable(drawable2), createMaskDrawable(drawable), createMaskDrawable(drawable2));
            this.mSlideBar = createMaskedSlideBar();
        }
        setSliderDrawState();
        if (this.mView.isChecked()) {
            setSliderOffset(this.mSliderPositionEnd);
        }
        this.mMaxTranslateOffset = this.mView.getResources().getDimensionPixelOffset(miuix.slidingwidget.R.dimen.miuix_appcompat_sliding_button_slider_max_offset);
    }

    public int getSliderOffset() {
        return this.mSliderOffset;
    }

    public void setSliderOffset(int i) {
        this.mSliderOffset = i;
        this.mView.invalidate();
    }

    public int getSliderOnAlpha() {
        return this.mSliderOnAlpha;
    }

    public void setSliderOnAlpha(int i) {
        this.mSliderOnAlpha = i;
        this.mView.invalidate();
    }

    public void setChecked(boolean z) {
        saveCurrentParams();
        this.mAnimChecked = z;
        this.mSliderOffset = z ? this.mSliderPositionEnd : this.mSliderPositionStart;
        this.mSliderOnAlpha = z ? 255 : 0;
        this.mMaskCheckedSlideBarAlpha = z ? 1.0f : 0.0f;
        SpringAnimation springAnimation = this.mSliderMoveAnim;
        if (springAnimation != null && springAnimation.isRunning()) {
            this.mSliderMoveAnim.cancel();
        }
        if (this.mMarkedAlphaHideAnim.isRunning()) {
            this.mMarkedAlphaHideAnim.cancel();
        }
        if (this.mMarkedAlphaShowAnim.isRunning()) {
            this.mMarkedAlphaShowAnim.cancel();
        }
        this.mView.invalidate();
    }

    private void saveCurrentParams() {
        this.mSliderOffsetTemp = this.mSliderOffset;
        this.mSliderOnAlphaTemp = this.mSliderOnAlpha;
        this.mMaskCheckedSlideBarAlphaTemp = this.mMaskCheckedSlideBarAlpha;
        this.mAnimCheckedTemp = this.mAnimChecked;
        this.mParamCached = true;
    }

    private void popSavedParams() {
        if (this.mParamCached) {
            this.mSliderOffset = this.mSliderOffsetTemp;
            this.mSliderOnAlpha = this.mSliderOnAlphaTemp;
            this.mMaskCheckedSlideBarAlpha = this.mMaskCheckedSlideBarAlphaTemp;
            this.mAnimChecked = this.mAnimCheckedTemp;
            this.mParamCached = false;
            this.mSliderOffsetTemp = -1;
            this.mSliderOnAlphaTemp = -1;
            this.mMaskCheckedSlideBarAlphaTemp = -1.0f;
        }
    }

    private void startCheckedChangeAnimInternal(boolean z) {
        SpringAnimation springAnimation = this.mSliderMoveAnim;
        if (springAnimation == null || !springAnimation.isRunning()) {
            boolean z2 = this.mAnimChecked;
            this.mSliderOffset = z2 ? this.mSliderPositionEnd : this.mSliderPositionStart;
            this.mSliderOnAlpha = z2 ? 255 : 0;
        }
        popSavedParams();
        setCheckedInner(z);
    }

    private void setCheckedInner(boolean z) {
        if (this.mAnimChecked) {
            if (this.mMarkedAlphaHideAnim.isRunning()) {
                this.mMarkedAlphaHideAnim.cancel();
            }
            if (!this.mMarkedAlphaShowAnim.isRunning() && !z) {
                this.mMaskCheckedSlideBarAlpha = 1.0f;
            }
        }
        if (this.mAnimChecked) {
            return;
        }
        if (this.mMarkedAlphaShowAnim.isRunning()) {
            this.mMarkedAlphaShowAnim.cancel();
        }
        if (this.mMarkedAlphaHideAnim.isRunning() || !z) {
            return;
        }
        this.mMaskCheckedSlideBarAlpha = 0.0f;
    }

    public void setAlpha(float f) {
        this.mExtraAlpha = f;
    }

    public float getAlpha() {
        return this.mExtraAlpha;
    }

    public void initAnim() {
        SpringAnimation springAnimation = new SpringAnimation(this.mView, this.mSliderScaleFloatProperty, 1.127f);
        this.mSliderPressedAnim = springAnimation;
        springAnimation.getSpring().setStiffness(986.96f);
        this.mSliderPressedAnim.getSpring().setDampingRatio(0.6f);
        this.mSliderPressedAnim.setMinimumVisibleChange(0.002f);
        this.mSliderPressedAnim.addUpdateListener(this.mInvalidateUpdateListener);
        SpringAnimation springAnimation2 = new SpringAnimation(this.mView, this.mSliderScaleFloatProperty, 1.0f);
        this.mSliderUnPressedAnim = springAnimation2;
        springAnimation2.getSpring().setStiffness(986.96f);
        this.mSliderUnPressedAnim.getSpring().setDampingRatio(0.6f);
        this.mSliderUnPressedAnim.setMinimumVisibleChange(0.002f);
        this.mSliderUnPressedAnim.addUpdateListener(this.mInvalidateUpdateListener);
        SpringAnimation springAnimation3 = new SpringAnimation(this.mView, this.mMaskCheckedSlideBarAlphaProperty, 1.0f);
        this.mMarkedAlphaShowAnim = springAnimation3;
        springAnimation3.getSpring().setStiffness(438.64f);
        this.mMarkedAlphaShowAnim.getSpring().setDampingRatio(0.99f);
        this.mMarkedAlphaShowAnim.setMinimumVisibleChange(0.00390625f);
        this.mMarkedAlphaShowAnim.addUpdateListener(this.mInvalidateUpdateListener);
        SpringAnimation springAnimation4 = new SpringAnimation(this.mView, this.mMaskCheckedSlideBarAlphaProperty, 0.0f);
        this.mMarkedAlphaHideAnim = springAnimation4;
        springAnimation4.getSpring().setStiffness(986.96f);
        this.mMarkedAlphaHideAnim.getSpring().setDampingRatio(0.99f);
        this.mMarkedAlphaHideAnim.setMinimumVisibleChange(0.00390625f);
        this.mMarkedAlphaHideAnim.addUpdateListener(this.mInvalidateUpdateListener);
    }

    public void onDraw(Canvas canvas) {
        setSliderDrawState();
        onDrawSlideBar(canvas);
        boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(this.mView);
        int i = this.mSliderPaddingH;
        if (zIsLayoutRtl) {
            i = -i;
        }
        int i2 = zIsLayoutRtl ? (this.mWidth - this.mSliderOffset) - this.mSliderWidth : this.mSliderOffset;
        float[] fArr = this.mTranslateDist;
        float f = fArr[0];
        int i3 = i2 + i + ((int) f);
        int i4 = (zIsLayoutRtl ? this.mWidth - this.mSliderOffset : this.mSliderWidth + this.mSliderOffset) + i + ((int) f);
        int i5 = this.mHeight;
        int i6 = this.mSliderHeight;
        int i7 = ((i5 - i6) / 2) + ((int) fArr[1]);
        int i8 = i6 + i7;
        scaleCanvasStart(canvas, (i4 + i3) / 2, (i8 + i7) / 2);
        if (this.mAnimChecked) {
            this.mSliderOn.setBounds(i3, i7, i4, i8);
            this.mSliderOn.draw(canvas);
        } else {
            this.mSliderOff.setBounds(i3, i7, i4, i8);
            this.mSliderOff.draw(canvas);
        }
        scaleCanvasEnd(canvas);
    }

    public void jumpDrawablesToCurrentState() {
        StateListDrawable stateListDrawable = this.mSlideBar;
        if (stateListDrawable != null) {
            stateListDrawable.jumpToCurrentState();
        }
    }

    public boolean verifyDrawable(Drawable drawable) {
        return drawable == this.mSlideBar;
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        Rect rect = this.mTmpRect;
        boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(this.mView);
        rect.set(zIsLayoutRtl ? (this.mWidth - this.mSliderOffset) - this.mSliderWidth : this.mSliderOffset, 0, zIsLayoutRtl ? this.mWidth - this.mSliderOffset : this.mSliderOffset + this.mSliderWidth, this.mHeight);
        boolean z = true;
        if (action == 0) {
            if (rect.contains(x, y)) {
                this.mTracking = true;
                this.mView.setPressed(true);
                onPressedInner();
                int i = this.mSliderOffset;
                if (i > this.mSliderPositionStart && i < this.mSliderPositionEnd) {
                    z = false;
                }
                this.mIsSliderEdgeReached = z;
            } else {
                this.mTracking = false;
            }
            this.mLastX = x;
            this.mOriginalTouchPointX = x;
            this.mSliderMoved = false;
            return;
        }
        if (action == 1) {
            this.mView.playSoundEffect(0);
            onUnPressedInner();
            if (!this.mTracking || !this.mSliderMoved) {
                animateToggle();
            } else {
                z = this.mSliderOffset >= this.mSliderPositionEnd / 2;
                this.mAnimChecked = z;
                animateToState(z);
                if (rect.contains(x, y)) {
                    HapticCompat.performHapticFeedback(this.mView, HapticFeedbackConstants.MIUI_ZAXIS_SWITCH, HapticFeedbackConstants.MIUI_SWITCH);
                }
            }
            this.mTracking = false;
            this.mSliderMoved = false;
            this.mView.setPressed(false);
            return;
        }
        if (action == 2) {
            if (this.mTracking) {
                moveSlider(x - this.mLastX);
                this.mLastX = x;
                if (Math.abs(x - this.mOriginalTouchPointX) >= this.mTapThreshold) {
                    this.mSliderMoved = true;
                    this.mView.getParent().requestDisallowInterceptTouchEvent(true);
                    return;
                }
                return;
            }
            return;
        }
        if (action != 3) {
            return;
        }
        onUnPressedInner();
        if (this.mTracking) {
            z = this.mSliderOffset >= this.mSliderPositionEnd / 2;
            this.mAnimChecked = z;
            animateToState(z);
        }
        this.mTracking = false;
        this.mSliderMoved = false;
        this.mView.setPressed(false);
    }

    public void onHoverEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 7) {
            this.mTranslateDist = actualTranslateDist(this.mView, motionEvent);
            this.mView.invalidate();
            return;
        }
        if (actionMasked == 9) {
            if (this.mSliderUnPressedAnim.isRunning()) {
                this.mSliderUnPressedAnim.cancel();
            }
            this.mSliderPressedAnim.start();
        } else {
            if (actionMasked != 10) {
                return;
            }
            float[] fArr = this.mTranslateDist;
            fArr[0] = 0.0f;
            fArr[1] = 0.0f;
            if (this.mSliderPressedAnim.isRunning()) {
                this.mSliderPressedAnim.cancel();
            }
            this.mSliderUnPressedAnim.start();
        }
    }

    private float[] actualTranslateDist(View view, MotionEvent motionEvent) {
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        float width = iArr[0] + (view.getWidth() * 0.5f);
        float height = iArr[1] + (view.getHeight() * 0.5f);
        float width2 = view.getWidth() == 0 ? 0.0f : (rawX - width) / view.getWidth();
        float height2 = view.getHeight() != 0 ? (rawY - height) / view.getHeight() : 0.0f;
        float fMax = Math.max(-1.0f, Math.min(1.0f, width2));
        float fMax2 = Math.max(-1.0f, Math.min(1.0f, height2));
        int i = this.mMaxTranslateOffset;
        return new float[]{fMax * i, fMax2 * i};
    }

    private void onPressedInner() {
        if (this.mSliderUnPressedAnim.isRunning()) {
            this.mSliderUnPressedAnim.cancel();
        }
        if (this.mSliderPressedAnim.isRunning()) {
            return;
        }
        this.mSliderPressedAnim.start();
    }

    private void onUnPressedInner() {
        if (this.mSliderPressedAnim.isRunning()) {
            this.mSliderPressedAnim.cancel();
        }
        if (this.mSliderUnPressedAnim.isRunning()) {
            return;
        }
        this.mSliderUnPressedAnim.start();
    }

    private void scaleCanvasEnd(Canvas canvas) {
        canvas.restore();
    }

    private void scaleCanvasStart(Canvas canvas, int i, int i2) {
        canvas.save();
        float f = this.mSliderScale;
        canvas.scale(f, f, i, i2);
    }

    private void onDrawSlideBar(Canvas canvas) {
        int i = (int) ((1.0f - this.mMaskCheckedSlideBarAlpha) * 255.0f);
        if (i > 0) {
            this.mMaskUnCheckedSlideBar.setAlpha(i);
            this.mMaskUnCheckedSlideBar.draw(canvas);
        }
        int i2 = (int) (this.mMaskCheckedSlideBarAlpha * 255.0f);
        if (i2 > 0) {
            this.mMaskCheckedSlideBar.setAlpha(i2);
            this.mMaskCheckedSlideBar.draw(canvas);
        }
    }

    private void moveSlider(int i) {
        if (ViewUtils.isLayoutRtl(this.mView)) {
            i = -i;
        }
        int i2 = this.mSliderOffset + i;
        this.mSliderOffset = i2;
        int i3 = this.mSliderPositionStart;
        if (i2 < i3) {
            this.mSliderOffset = i3;
        } else {
            int i4 = this.mSliderPositionEnd;
            if (i2 > i4) {
                this.mSliderOffset = i4;
            }
        }
        int i5 = this.mSliderOffset;
        boolean z = i5 == i3 || i5 == this.mSliderPositionEnd;
        if (z && !this.mIsSliderEdgeReached) {
            HapticCompat.performHapticFeedback(this.mView, HapticFeedbackConstants.MIUI_ZAXIS_SWITCH, HapticFeedbackConstants.MIUI_SWITCH);
        }
        this.mIsSliderEdgeReached = z;
        setSliderOffset(this.mSliderOffset);
    }

    private void animateToState(boolean z, int i, final Runnable runnable) {
        SpringAnimation springAnimation = this.mSliderMoveAnim;
        if (springAnimation != null && springAnimation.isRunning()) {
            this.mSliderMoveAnim.cancel();
        }
        if (z != this.mView.isChecked()) {
            return;
        }
        SpringAnimation springAnimation2 = new SpringAnimation(this.mView, this.mSliderOffsetProperty, i);
        this.mSliderMoveAnim = springAnimation2;
        springAnimation2.getSpring().setStiffness(986.96f);
        this.mSliderMoveAnim.getSpring().setDampingRatio(0.7f);
        this.mSliderMoveAnim.addUpdateListener(this.mInvalidateUpdateListener);
        this.mSliderMoveAnim.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: miuix.slidingwidget.widget.SlidingButtonHelper.4
            @Override // miuix.animation.physics.DynamicAnimation.OnAnimationEndListener
            public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                runnable.run();
            }
        });
        this.mSliderMoveAnim.start();
        if (z) {
            if (!this.mMarkedAlphaShowAnim.isRunning()) {
                this.mMarkedAlphaShowAnim.start();
            }
            if (this.mMarkedAlphaHideAnim.isRunning()) {
                this.mMarkedAlphaHideAnim.cancel();
                return;
            }
            return;
        }
        if (!this.mMarkedAlphaHideAnim.isRunning()) {
            this.mMarkedAlphaHideAnim.start();
        }
        if (this.mMarkedAlphaShowAnim.isRunning()) {
            this.mMarkedAlphaShowAnim.cancel();
        }
    }

    public void setOnPerformCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.mOnPerformCheckedChangeListener = onCheckedChangeListener;
    }

    public void notifyCheckedChangeListener() {
        if (this.mOnPerformCheckedChangeListener != null) {
            this.mOnPerformCheckedChangeListener.onCheckedChanged(this.mView, this.mView.isChecked());
        }
    }

    private void animateToggle() {
        animateToState(!this.mView.isChecked());
        HapticCompat.performHapticFeedback(this.mView, HapticFeedbackConstants.MIUI_ZAXIS_SWITCH, HapticFeedbackConstants.MIUI_SWITCH);
    }

    private void animateToState(boolean z) {
        if (z != this.mView.isChecked()) {
            this.mView.setChecked(z);
            startCheckedChangeAnimInternal(z);
            notifyCheckedChangeListener();
        }
        animateToState(z, z ? this.mSliderPositionEnd : this.mSliderPositionStart, new Runnable() { // from class: miuix.slidingwidget.widget.SlidingButtonHelper.5
            @Override // java.lang.Runnable
            public void run() {
                SlidingButtonHelper slidingButtonHelper = SlidingButtonHelper.this;
                slidingButtonHelper.mAnimChecked = slidingButtonHelper.mSliderOffset >= SlidingButtonHelper.this.mSliderPositionEnd;
            }
        });
    }

    public void setParentClipChildren() {
        ViewParent parent = this.mView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).setClipChildren(false);
        }
    }

    private StateListDrawable createMaskedSlideBar() {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.setBounds(0, 0, this.mWidth, this.mHeight);
        stateListDrawable.setCallback(this.mView);
        return stateListDrawable;
    }

    private void initMaskedSlideBar(Drawable drawable, Drawable drawable2, Drawable drawable3) {
        this.mMaskCheckedSlideBar = drawable;
        this.mMaskUnCheckedSlideBar = drawable2;
        this.mMaskUnCheckedPressedSlideBar = drawable3;
    }

    private Drawable createMaskDrawable(Drawable drawable) {
        SmoothContainerDrawable2 smoothContainerDrawable2 = new SmoothContainerDrawable2();
        smoothContainerDrawable2.setLayerType(this.mView.getLayerType());
        smoothContainerDrawable2.setCornerRadius(this.mCornerRadius);
        smoothContainerDrawable2.setChildDrawable(drawable);
        int i = this.mMarginHorizontal;
        int i2 = this.mMarginVertical;
        smoothContainerDrawable2.setBounds(new Rect(i, i2, this.mWidth - i, this.mHeight - i2));
        return smoothContainerDrawable2;
    }

    public void setLayerType(int i) {
        Drawable drawable = this.mMaskCheckedSlideBar;
        if (drawable instanceof SmoothContainerDrawable2) {
            ((SmoothContainerDrawable2) drawable).setLayerType(i);
        }
        Drawable drawable2 = this.mMaskUnCheckedSlideBar;
        if (drawable2 instanceof SmoothContainerDrawable2) {
            ((SmoothContainerDrawable2) drawable2).setLayerType(i);
        }
        Drawable drawable3 = this.mMaskUnCheckedPressedSlideBar;
        if (drawable3 instanceof SmoothContainerDrawable2) {
            ((SmoothContainerDrawable2) drawable3).setLayerType(i);
        }
    }
}
