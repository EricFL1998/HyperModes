package miuix.appcompat.internal.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import miuix.appcompat.R;

/* JADX INFO: loaded from: classes2.dex */
public class ColorTransitionTextView extends TextView {
    private boolean hasTransitedColor;
    private int mAnimateColor;
    private ValueAnimator mAnimator;
    private int mNormalColor;
    private ColorStateList mOriginColor;
    private int mTransitedColor;

    public ColorTransitionTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.hasTransitedColor = false;
        setupColors();
    }

    private void setupColors() {
        ColorStateList textColors = getTextColors();
        this.mOriginColor = textColors;
        this.mNormalColor = textColors.getColorForState(ENABLED_STATE_SET, getResources().getColor(R.color.miuix_appcompat_action_bar_title_text_color_light));
        int colorForState = this.mOriginColor.getColorForState(ENABLED_SELECTED_STATE_SET, this.mNormalColor);
        this.mTransitedColor = colorForState;
        if (this.mNormalColor != colorForState) {
            this.hasTransitedColor = true;
        }
    }

    @Override // android.widget.TextView
    public void setTextColor(ColorStateList colorStateList) {
        super.setTextColor(colorStateList);
        setupColors();
    }

    @Override // android.widget.TextView, android.view.View
    public void setSelected(boolean z) {
        super.setSelected(false);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        ValueAnimator valueAnimator;
        if (!this.hasTransitedColor || (valueAnimator = this.mAnimator) == null || !valueAnimator.isRunning()) {
            super.onDraw(canvas);
        } else {
            setTextColor(this.mAnimateColor);
            super.onDraw(canvas);
        }
    }

    public void startColorTransition(final boolean z, boolean z2) {
        if (!z2) {
            if (z) {
                setTextColor(this.mTransitedColor);
            } else {
                setTextColor(this.mNormalColor);
            }
            invalidate();
            return;
        }
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator == null) {
            this.mAnimator = new ValueAnimator();
        } else {
            valueAnimator.cancel();
        }
        if (z) {
            this.mAnimator.setIntValues(getCurrentTextColor(), this.mTransitedColor);
        } else {
            this.mAnimator.setIntValues(getCurrentTextColor(), this.mNormalColor);
        }
        this.mAnimator.setDuration(50L);
        this.mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.mAnimator.setEvaluator(new ArgbEvaluator());
        this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.appcompat.internal.view.ColorTransitionTextView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ColorTransitionTextView.this.mAnimateColor = ((Integer) valueAnimator2.getAnimatedValue()).intValue();
                ColorTransitionTextView.this.invalidate();
            }
        });
        this.mAnimator.addListener(new AnimatorListenerAdapter() { // from class: miuix.appcompat.internal.view.ColorTransitionTextView.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (z) {
                    ColorTransitionTextView colorTransitionTextView = ColorTransitionTextView.this;
                    colorTransitionTextView.setTextColor(colorTransitionTextView.mTransitedColor);
                } else {
                    ColorTransitionTextView colorTransitionTextView2 = ColorTransitionTextView.this;
                    colorTransitionTextView2.setTextColor(colorTransitionTextView2.mNormalColor);
                }
            }
        });
        this.mAnimator.start();
    }
}
