package miuix.androidbasewidget.internal.view;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import miuix.androidbasewidget.R;
import miuix.animation.physics.DynamicAnimation;
import miuix.animation.physics.SpringAnimation;
import miuix.animation.property.FloatProperty;

/* JADX INFO: loaded from: classes2.dex */
public class SeekBaThumbShapeDrawable extends SeekBarGradientDrawable {
    private static final int FULL_ALPHA = 255;
    private static final String TAG = "SeekBaThumbShape";
    private static Drawable mShadowDrawable;
    private DynamicAnimation.OnAnimationUpdateListener mInvalidateUpdateListener;
    private SpringAnimation mPressedScaleAnim;
    private SpringAnimation mPressedShadowShowAnim;
    private float mScale;
    private FloatProperty<SeekBaThumbShapeDrawable> mScaleFloatProperty;
    private float mShadowAlpha;
    private FloatProperty<SeekBaThumbShapeDrawable> mShadowAlphaProperty;
    private SpringAnimation mUnPressedScaleAnim;
    private SpringAnimation mUnPressedShadowHideAnim;

    public float getShadowAlpha() {
        return this.mShadowAlpha;
    }

    public void setShadowAlpha(float f) {
        this.mShadowAlpha = f;
    }

    /* JADX INFO: renamed from: lambda$new$0$miuix-androidbasewidget-internal-view-SeekBaThumbShapeDrawable, reason: not valid java name */
    /* synthetic */ void m1775x60f5349b(DynamicAnimation dynamicAnimation, float f, float f2) {
        invalidateSelf();
    }

    public float getScale() {
        return this.mScale;
    }

    public void setScale(float f) {
        this.mScale = f;
    }

    public SeekBaThumbShapeDrawable() {
        this.mScale = 1.0f;
        this.mShadowAlpha = 0.0f;
        this.mShadowAlphaProperty = new FloatProperty<SeekBaThumbShapeDrawable>("ShadowAlpha") { // from class: miuix.androidbasewidget.internal.view.SeekBaThumbShapeDrawable.1
            @Override // miuix.animation.property.FloatProperty
            public float getValue(SeekBaThumbShapeDrawable seekBaThumbShapeDrawable) {
                return seekBaThumbShapeDrawable.getShadowAlpha();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(SeekBaThumbShapeDrawable seekBaThumbShapeDrawable, float f) {
                if (f > 1.0f) {
                    f = 1.0f;
                }
                if (f < 0.0f) {
                    f = 0.0f;
                }
                seekBaThumbShapeDrawable.setShadowAlpha(f);
            }
        };
        this.mInvalidateUpdateListener = new DynamicAnimation.OnAnimationUpdateListener() { // from class: miuix.androidbasewidget.internal.view.SeekBaThumbShapeDrawable$$ExternalSyntheticLambda0
            @Override // miuix.animation.physics.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                this.f$0.m1775x60f5349b(dynamicAnimation, f, f2);
            }
        };
        this.mScaleFloatProperty = new FloatProperty<SeekBaThumbShapeDrawable>("Scale") { // from class: miuix.androidbasewidget.internal.view.SeekBaThumbShapeDrawable.2
            @Override // miuix.animation.property.FloatProperty
            public float getValue(SeekBaThumbShapeDrawable seekBaThumbShapeDrawable) {
                return seekBaThumbShapeDrawable.getScale();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(SeekBaThumbShapeDrawable seekBaThumbShapeDrawable, float f) {
                seekBaThumbShapeDrawable.setScale(f);
            }
        };
        initAnim();
    }

    public SeekBaThumbShapeDrawable(Resources resources, Resources.Theme theme, SeekBarGradientDrawable.SeekBarGradientState seekBarGradientState) {
        super(resources, theme, seekBarGradientState);
        this.mScale = 1.0f;
        this.mShadowAlpha = 0.0f;
        this.mShadowAlphaProperty = new FloatProperty<SeekBaThumbShapeDrawable>("ShadowAlpha") { // from class: miuix.androidbasewidget.internal.view.SeekBaThumbShapeDrawable.1
            @Override // miuix.animation.property.FloatProperty
            public float getValue(SeekBaThumbShapeDrawable seekBaThumbShapeDrawable) {
                return seekBaThumbShapeDrawable.getShadowAlpha();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(SeekBaThumbShapeDrawable seekBaThumbShapeDrawable, float f) {
                if (f > 1.0f) {
                    f = 1.0f;
                }
                if (f < 0.0f) {
                    f = 0.0f;
                }
                seekBaThumbShapeDrawable.setShadowAlpha(f);
            }
        };
        this.mInvalidateUpdateListener = new DynamicAnimation.OnAnimationUpdateListener() { // from class: miuix.androidbasewidget.internal.view.SeekBaThumbShapeDrawable$$ExternalSyntheticLambda0
            @Override // miuix.animation.physics.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                this.f$0.m1775x60f5349b(dynamicAnimation, f, f2);
            }
        };
        this.mScaleFloatProperty = new FloatProperty<SeekBaThumbShapeDrawable>("Scale") { // from class: miuix.androidbasewidget.internal.view.SeekBaThumbShapeDrawable.2
            @Override // miuix.animation.property.FloatProperty
            public float getValue(SeekBaThumbShapeDrawable seekBaThumbShapeDrawable) {
                return seekBaThumbShapeDrawable.getScale();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(SeekBaThumbShapeDrawable seekBaThumbShapeDrawable, float f) {
                seekBaThumbShapeDrawable.setScale(f);
            }
        };
        initAnim();
        if (resources == null || mShadowDrawable != null) {
            return;
        }
        mShadowDrawable = resources.getDrawable(R.drawable.miuix_appcompat_sliding_btn_slider_shadow);
    }

    @Override // miuix.androidbasewidget.internal.view.SeekBarGradientDrawable
    protected SeekBarGradientDrawable.SeekBarGradientState newSeekBarGradientState() {
        return new SeekBaThumbShapeDrawableState();
    }

    private void initAnim() {
        SpringAnimation springAnimation = new SpringAnimation(this, this.mScaleFloatProperty, 3.19f);
        this.mPressedScaleAnim = springAnimation;
        springAnimation.getSpring().setStiffness(986.96f);
        this.mPressedScaleAnim.getSpring().setDampingRatio(0.7f);
        this.mPressedScaleAnim.setMinimumVisibleChange(0.002f);
        this.mPressedScaleAnim.addUpdateListener(this.mInvalidateUpdateListener);
        SpringAnimation springAnimation2 = new SpringAnimation(this, this.mScaleFloatProperty, 1.0f);
        this.mUnPressedScaleAnim = springAnimation2;
        springAnimation2.getSpring().setStiffness(986.96f);
        this.mUnPressedScaleAnim.getSpring().setDampingRatio(0.8f);
        this.mUnPressedScaleAnim.setMinimumVisibleChange(0.002f);
        this.mUnPressedScaleAnim.addUpdateListener(this.mInvalidateUpdateListener);
        SpringAnimation springAnimation3 = new SpringAnimation(this, this.mShadowAlphaProperty, 1.0f);
        this.mPressedShadowShowAnim = springAnimation3;
        springAnimation3.getSpring().setStiffness(986.96f);
        this.mPressedShadowShowAnim.getSpring().setDampingRatio(0.99f);
        this.mPressedShadowShowAnim.setMinimumVisibleChange(0.00390625f);
        this.mPressedShadowShowAnim.addUpdateListener(this.mInvalidateUpdateListener);
        SpringAnimation springAnimation4 = new SpringAnimation(this, this.mShadowAlphaProperty, 0.0f);
        this.mUnPressedShadowHideAnim = springAnimation4;
        springAnimation4.getSpring().setStiffness(986.96f);
        this.mUnPressedShadowHideAnim.getSpring().setDampingRatio(0.99f);
        this.mUnPressedShadowHideAnim.setMinimumVisibleChange(0.00390625f);
        this.mUnPressedShadowHideAnim.addUpdateListener(this.mInvalidateUpdateListener);
    }

    @Override // miuix.androidbasewidget.internal.view.SeekBarGradientDrawable
    protected void startPressedAnim() {
        if (this.mUnPressedScaleAnim.isRunning()) {
            this.mUnPressedScaleAnim.cancel();
        }
        if (!this.mPressedScaleAnim.isRunning()) {
            this.mPressedScaleAnim.start();
        }
        if (this.mUnPressedShadowHideAnim.isRunning()) {
            this.mUnPressedShadowHideAnim.cancel();
        }
        if (this.mPressedShadowShowAnim.isRunning()) {
            return;
        }
        this.mPressedShadowShowAnim.start();
    }

    @Override // miuix.androidbasewidget.internal.view.SeekBarGradientDrawable
    protected void startUnPressedAnim() {
        if (this.mPressedScaleAnim.isRunning()) {
            this.mPressedScaleAnim.cancel();
        }
        if (!this.mUnPressedScaleAnim.isRunning()) {
            this.mUnPressedScaleAnim.start();
        }
        if (this.mPressedShadowShowAnim.isRunning()) {
            this.mPressedShadowShowAnim.cancel();
        }
        if (this.mUnPressedShadowHideAnim.isRunning()) {
            return;
        }
        this.mUnPressedShadowHideAnim.start();
    }

    protected static class SeekBaThumbShapeDrawableState extends SeekBarGradientDrawable.SeekBarGradientState {
        protected SeekBaThumbShapeDrawableState() {
        }

        @Override // miuix.androidbasewidget.internal.view.SeekBarGradientDrawable.SeekBarGradientState
        protected Drawable newSeekBarGradientDrawable(Resources resources, Resources.Theme theme, SeekBarGradientDrawable.SeekBarGradientState seekBarGradientState) {
            return new SeekBaThumbShapeDrawable(resources, theme, seekBarGradientState);
        }
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int i = (bounds.right + bounds.left) / 2;
        int i2 = (bounds.top + bounds.bottom) / 2;
        drawShadow(canvas);
        canvas.save();
        float f = this.mScale;
        canvas.scale(f, f, i, i2);
        super.draw(canvas);
        canvas.restore();
    }

    private void drawShadow(Canvas canvas) {
        Rect bounds = getBounds();
        Drawable drawable = mShadowDrawable;
        if (drawable != null) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = mShadowDrawable.getIntrinsicHeight();
            int intrinsicWidth2 = intrinsicWidth - getIntrinsicWidth();
            int i = intrinsicWidth2 / 2;
            int intrinsicHeight2 = (intrinsicHeight - getIntrinsicHeight()) / 2;
            mShadowDrawable.setBounds(bounds.left - i, bounds.top - intrinsicHeight2, bounds.right + i, bounds.bottom + intrinsicHeight2);
            mShadowDrawable.setAlpha((int) (this.mShadowAlpha * 255.0f));
            mShadowDrawable.draw(canvas);
        }
    }
}
