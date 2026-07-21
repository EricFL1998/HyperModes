package miuix.appcompat.internal.app.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;
import miuix.appcompat.R;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes2.dex */
public class ScrollingTabTextView extends TextView {
    private ValueAnimator mAnimator;
    private int mClipPosition;
    private boolean mLeftToRight;
    private int mNormalColor;
    private ColorStateList mOriginColor;
    private int mSelectedColor;

    public ScrollingTabTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setupColors();
    }

    private void setupColors() {
        ColorStateList textColors = getTextColors();
        this.mOriginColor = textColors;
        this.mNormalColor = textColors.getColorForState(ENABLED_STATE_SET, getResources().getColor(R.color.miuix_appcompat_action_bar_tab_text_color_normal_light));
        this.mSelectedColor = this.mOriginColor.getColorForState(ENABLED_SELECTED_STATE_SET, getResources().getColor(R.color.miuix_appcompat_action_bar_tab_text_color_selected_light));
    }

    @Override // android.widget.TextView
    public void setTextColor(ColorStateList colorStateList) {
        super.setTextColor(colorStateList);
        setupColors();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        int scrollX;
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator == null || !valueAnimator.isRunning()) {
            super.onDraw(canvas);
            return;
        }
        if ((this.mLeftToRight && !isSelected()) || (!this.mLeftToRight && isSelected())) {
            i = this.mNormalColor;
        } else {
            i = this.mSelectedColor;
        }
        setTextColor(i);
        boolean zIsLayoutRtl = ViewUtils.isLayoutRtl(this);
        int scrollX2 = this.mClipPosition;
        int height = getHeight();
        if (zIsLayoutRtl) {
            scrollX = getScrollX();
            scrollX2 += getScrollX();
        } else {
            scrollX = 0;
        }
        canvas.save();
        canvas.clipRect(scrollX, 0, scrollX2, height);
        super.onDraw(canvas);
        canvas.restore();
        int i2 = this.mNormalColor;
        if (i == i2) {
            i = this.mSelectedColor;
        } else if (i == this.mSelectedColor) {
            i = i2;
        }
        setTextColor(i);
        int scrollX3 = this.mClipPosition;
        int width = getWidth();
        if (zIsLayoutRtl) {
            scrollX3 += getScrollX();
            width += getScrollX();
        }
        canvas.save();
        canvas.clipRect(scrollX3, 0, width, height);
        super.onDraw(canvas);
        canvas.restore();
        setTextColor(this.mOriginColor);
    }

    public void startScrollAnimation(boolean z) {
        int width;
        ValueAnimator valueAnimator = this.mAnimator;
        if (valueAnimator == null) {
            this.mAnimator = new ValueAnimator();
        } else {
            valueAnimator.cancel();
        }
        this.mLeftToRight = z;
        int width2 = 0;
        if (z) {
            width = getWidth();
        } else {
            width2 = getWidth();
            width = 0;
        }
        this.mAnimator.setIntValues(width2, width);
        this.mAnimator.setDuration(200L);
        this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.appcompat.internal.app.widget.ScrollingTabTextView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ScrollingTabTextView.this.mClipPosition = ((Integer) valueAnimator2.getAnimatedValue()).intValue();
                ScrollingTabTextView.this.invalidate();
            }
        });
        this.mAnimator.addListener(new AnimatorListenerAdapter() { // from class: miuix.appcompat.internal.app.widget.ScrollingTabTextView.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ScrollingTabTextView scrollingTabTextView = ScrollingTabTextView.this;
                scrollingTabTextView.mClipPosition = scrollingTabTextView.getWidth();
            }
        });
        this.mAnimator.start();
    }
}
