package com.google.android.material.textfield;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import com.google.android.material.R;
import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.motion.MotionUtils;

/* JADX INFO: loaded from: classes2.dex */
class ClearTextEndIconDelegate extends EndIconDelegate {
    private static final float ANIMATION_SCALE_FROM_VALUE = 0.8f;
    private static final int DEFAULT_ANIMATION_FADE_DURATION = 100;
    private static final int DEFAULT_ANIMATION_SCALE_DURATION = 150;
    private final int animationFadeDuration;
    private final TimeInterpolator animationFadeInterpolator;
    private final int animationScaleDuration;
    private final TimeInterpolator animationScaleInterpolator;
    private EditText editText;
    private AnimatorSet iconInAnim;
    private ValueAnimator iconOutAnim;
    private final View.OnFocusChangeListener onFocusChangeListener;
    private final View.OnClickListener onIconClickListener;

    /* JADX INFO: renamed from: lambda$new$0$com-google-android-material-textfield-ClearTextEndIconDelegate, reason: not valid java name */
    /* synthetic */ void m183xfc81bd94(View view) {
        EditText editText = this.editText;
        if (editText == null) {
            return;
        }
        Editable text = editText.getText();
        if (text != null) {
            text.clear();
        }
        refreshIconState();
    }

    /* JADX INFO: renamed from: lambda$new$1$com-google-android-material-textfield-ClearTextEndIconDelegate, reason: not valid java name */
    /* synthetic */ void m184x7ae2c173(View view, boolean z) {
        animateIcon(shouldBeVisible());
    }

    ClearTextEndIconDelegate(EndCompoundLayout endCompoundLayout) {
        super(endCompoundLayout);
        this.onIconClickListener = new View.OnClickListener() { // from class: com.google.android.material.textfield.ClearTextEndIconDelegate$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m183xfc81bd94(view);
            }
        };
        this.onFocusChangeListener = new View.OnFocusChangeListener() { // from class: com.google.android.material.textfield.ClearTextEndIconDelegate$$ExternalSyntheticLambda1
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z) {
                this.f$0.m184x7ae2c173(view, z);
            }
        };
        this.animationFadeDuration = MotionUtils.resolveThemeDuration(endCompoundLayout.getContext(), R.attr.motionDurationShort3, 100);
        this.animationScaleDuration = MotionUtils.resolveThemeDuration(endCompoundLayout.getContext(), R.attr.motionDurationShort3, 150);
        this.animationFadeInterpolator = MotionUtils.resolveThemeInterpolator(endCompoundLayout.getContext(), R.attr.motionEasingLinearInterpolator, AnimationUtils.LINEAR_INTERPOLATOR);
        this.animationScaleInterpolator = MotionUtils.resolveThemeInterpolator(endCompoundLayout.getContext(), R.attr.motionEasingEmphasizedInterpolator, AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR);
    }

    @Override // com.google.android.material.textfield.EndIconDelegate
    void setUp() {
        initAnimators();
    }

    @Override // com.google.android.material.textfield.EndIconDelegate
    void tearDown() {
        EditText editText = this.editText;
        if (editText != null) {
            editText.post(new Runnable() { // from class: com.google.android.material.textfield.ClearTextEndIconDelegate$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m185x26d8c5f4();
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$tearDown$2$com-google-android-material-textfield-ClearTextEndIconDelegate, reason: not valid java name */
    /* synthetic */ void m185x26d8c5f4() {
        animateIcon(true);
    }

    @Override // com.google.android.material.textfield.EndIconDelegate
    int getIconDrawableResId() {
        return R.drawable.mtrl_ic_cancel;
    }

    @Override // com.google.android.material.textfield.EndIconDelegate
    int getIconContentDescriptionResId() {
        return R.string.clear_text_end_icon_content_description;
    }

    @Override // com.google.android.material.textfield.EndIconDelegate
    void onSuffixVisibilityChanged(boolean z) {
        if (this.endLayout.getSuffixText() == null) {
            return;
        }
        animateIcon(z);
    }

    @Override // com.google.android.material.textfield.EndIconDelegate
    View.OnClickListener getOnIconClickListener() {
        return this.onIconClickListener;
    }

    @Override // com.google.android.material.textfield.EndIconDelegate
    public void onEditTextAttached(EditText editText) {
        this.editText = editText;
        this.textInputLayout.setEndIconVisible(shouldBeVisible());
    }

    @Override // com.google.android.material.textfield.EndIconDelegate
    void afterEditTextChanged(Editable editable) {
        if (this.endLayout.getSuffixText() != null) {
            return;
        }
        animateIcon(shouldBeVisible());
    }

    @Override // com.google.android.material.textfield.EndIconDelegate
    View.OnFocusChangeListener getOnEditTextFocusChangeListener() {
        return this.onFocusChangeListener;
    }

    @Override // com.google.android.material.textfield.EndIconDelegate
    View.OnFocusChangeListener getOnIconViewFocusChangeListener() {
        return this.onFocusChangeListener;
    }

    private void animateIcon(boolean z) {
        boolean z2 = this.endLayout.isEndIconVisible() == z;
        if (z && !this.iconInAnim.isRunning()) {
            this.iconOutAnim.cancel();
            this.iconInAnim.start();
            if (z2) {
                this.iconInAnim.end();
                return;
            }
            return;
        }
        if (z) {
            return;
        }
        this.iconInAnim.cancel();
        this.iconOutAnim.start();
        if (z2) {
            this.iconOutAnim.end();
        }
    }

    private void initAnimators() {
        ValueAnimator scaleAnimator = getScaleAnimator();
        ValueAnimator alphaAnimator = getAlphaAnimator(0.0f, 1.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        this.iconInAnim = animatorSet;
        animatorSet.playTogether(scaleAnimator, alphaAnimator);
        this.iconInAnim.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.textfield.ClearTextEndIconDelegate.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                ClearTextEndIconDelegate.this.endLayout.setEndIconVisible(true);
            }
        });
        ValueAnimator alphaAnimator2 = getAlphaAnimator(1.0f, 0.0f);
        this.iconOutAnim = alphaAnimator2;
        alphaAnimator2.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.textfield.ClearTextEndIconDelegate.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ClearTextEndIconDelegate.this.endLayout.setEndIconVisible(false);
            }
        });
    }

    private ValueAnimator getAlphaAnimator(float... fArr) {
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(fArr);
        valueAnimatorOfFloat.setInterpolator(this.animationFadeInterpolator);
        valueAnimatorOfFloat.setDuration(this.animationFadeDuration);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.google.android.material.textfield.ClearTextEndIconDelegate$$ExternalSyntheticLambda2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.m181xa5c23ba8(valueAnimator);
            }
        });
        return valueAnimatorOfFloat;
    }

    /* JADX INFO: renamed from: lambda$getAlphaAnimator$3$com-google-android-material-textfield-ClearTextEndIconDelegate, reason: not valid java name */
    /* synthetic */ void m181xa5c23ba8(ValueAnimator valueAnimator) {
        this.endIconView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    private ValueAnimator getScaleAnimator() {
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(ANIMATION_SCALE_FROM_VALUE, 1.0f);
        valueAnimatorOfFloat.setInterpolator(this.animationScaleInterpolator);
        valueAnimatorOfFloat.setDuration(this.animationScaleDuration);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.google.android.material.textfield.ClearTextEndIconDelegate$$ExternalSyntheticLambda4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.m182x3819711b(valueAnimator);
            }
        });
        return valueAnimatorOfFloat;
    }

    /* JADX INFO: renamed from: lambda$getScaleAnimator$4$com-google-android-material-textfield-ClearTextEndIconDelegate, reason: not valid java name */
    /* synthetic */ void m182x3819711b(ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.endIconView.setScaleX(fFloatValue);
        this.endIconView.setScaleY(fFloatValue);
    }

    private boolean shouldBeVisible() {
        EditText editText = this.editText;
        return editText != null && (editText.hasFocus() || this.endIconView.hasFocus()) && this.editText.getText().length() > 0;
    }
}
