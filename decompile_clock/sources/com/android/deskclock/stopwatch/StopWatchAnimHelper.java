package com.android.deskclock.stopwatch;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;

/* JADX INFO: loaded from: classes.dex */
public class StopWatchAnimHelper {
    private ValueAnimator mDownAnimator;
    private boolean mHasLap;
    private View mHourView;
    private View mPlaceHolderView;
    private TextView mTimeView;
    private ValueAnimator mUpAnimator;
    private int placeHolderViewHeightDiff;
    private int placeHolderViewHeightLarge = (int) DeskClockApp.getAppContext().getResources().getDimension(R.dimen.stopwatch_view_holder_height_large);
    private int placeHolderViewHeightSmall = (int) DeskClockApp.getAppContext().getResources().getDimension(R.dimen.stopwatch_view_holder_height_small);
    private int textSizeLarge = (int) DeskClockApp.getAppContext().getResources().getDimension(R.dimen.stopwatch_time_display_text_size_large);
    private int textSizeSmall = (int) DeskClockApp.getAppContext().getResources().getDimension(R.dimen.stopwatch_time_display_text_size_small);

    public StopWatchAnimHelper(View view, TextView textView, View view2) {
        this.mPlaceHolderView = view;
        this.mTimeView = textView;
        this.mHourView = view2;
    }

    public void resetUI(boolean z) {
        if (z) {
            setPlaceHolderViewHeight(this.placeHolderViewHeightSmall);
            setTimeTextSize(this.textSizeSmall);
        } else {
            setPlaceHolderViewHeight(this.placeHolderViewHeightLarge);
            setTimeTextSize(this.textSizeLarge);
        }
        this.mHasLap = z;
    }

    public void resetOnConfigChanged(int i) {
        this.placeHolderViewHeightLarge = i;
        this.placeHolderViewHeightSmall = (int) DeskClockApp.getAppContext().getResources().getDimension(R.dimen.stopwatch_view_holder_height_small);
        resetUI(this.mHasLap);
    }

    public void anim(boolean z) {
        if (this.mHasLap == z) {
            return;
        }
        this.mHasLap = z;
        ValueAnimator valueAnimator = this.mUpAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.mUpAnimator.cancel();
        }
        ValueAnimator valueAnimator2 = this.mDownAnimator;
        if (valueAnimator2 != null && valueAnimator2.isRunning()) {
            this.mDownAnimator.cancel();
        }
        if (z) {
            this.placeHolderViewHeightDiff = this.placeHolderViewHeightLarge - this.placeHolderViewHeightSmall;
            if (this.mUpAnimator == null) {
                ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.mUpAnimator = valueAnimatorOfFloat;
                valueAnimatorOfFloat.setDuration(200L);
                this.mUpAnimator.setInterpolator(new DecelerateInterpolator(1.5f));
                final int i = this.textSizeLarge - this.textSizeSmall;
                final LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mPlaceHolderView.getLayoutParams();
                this.mUpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.deskclock.stopwatch.StopWatchAnimHelper.1
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator valueAnimator3) {
                        float fFloatValue = ((Float) valueAnimator3.getAnimatedValue()).floatValue();
                        layoutParams.height = (int) (StopWatchAnimHelper.this.placeHolderViewHeightLarge - (StopWatchAnimHelper.this.placeHolderViewHeightDiff * fFloatValue));
                        StopWatchAnimHelper.this.mPlaceHolderView.requestLayout();
                        StopWatchAnimHelper.this.mTimeView.setTextSize(0, StopWatchAnimHelper.this.textSizeLarge - (i * fFloatValue));
                    }
                });
                this.mUpAnimator.addListener(new Animator.AnimatorListener() { // from class: com.android.deskclock.stopwatch.StopWatchAnimHelper.2
                    @Override // android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                    }

                    @Override // android.animation.Animator.AnimatorListener
                    public void onAnimationRepeat(Animator animator) {
                    }

                    @Override // android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override // android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animator) {
                        StopWatchAnimHelper.this.resetUI(true);
                    }
                });
            }
            this.mUpAnimator.start();
            return;
        }
        this.placeHolderViewHeightDiff = this.placeHolderViewHeightLarge - this.placeHolderViewHeightSmall;
        if (this.mDownAnimator == null) {
            ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.mDownAnimator = valueAnimatorOfFloat2;
            valueAnimatorOfFloat2.setDuration(200L);
            this.mDownAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            final int i2 = this.textSizeLarge - this.textSizeSmall;
            final LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mPlaceHolderView.getLayoutParams();
            this.mDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.deskclock.stopwatch.StopWatchAnimHelper.3
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    float fFloatValue = ((Float) valueAnimator3.getAnimatedValue()).floatValue();
                    layoutParams2.height = (int) (StopWatchAnimHelper.this.placeHolderViewHeightSmall + (StopWatchAnimHelper.this.placeHolderViewHeightDiff * fFloatValue));
                    StopWatchAnimHelper.this.mPlaceHolderView.requestLayout();
                    StopWatchAnimHelper.this.mTimeView.setTextSize(0, StopWatchAnimHelper.this.textSizeSmall + (i2 * fFloatValue));
                }
            });
            this.mDownAnimator.addListener(new Animator.AnimatorListener() { // from class: com.android.deskclock.stopwatch.StopWatchAnimHelper.4
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationRepeat(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    StopWatchAnimHelper.this.resetUI(false);
                }
            });
        }
        this.mDownAnimator.start();
    }

    private void setPlaceHolderViewHeight(int i) {
        ((LinearLayout.LayoutParams) this.mPlaceHolderView.getLayoutParams()).height = i;
        this.mPlaceHolderView.requestLayout();
    }

    private void setTimeTextSize(final int i) {
        this.mTimeView.setTextSize(0, i);
        this.mTimeView.post(new Runnable() { // from class: com.android.deskclock.stopwatch.StopWatchAnimHelper.5
            @Override // java.lang.Runnable
            public void run() {
                StopWatchAnimHelper.this.mTimeView.setTextSize(0, i);
            }
        });
    }

    public void releaseAnim() {
        ValueAnimator valueAnimator = this.mUpAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.mUpAnimator.removeAllListeners();
            this.mUpAnimator.removeAllUpdateListeners();
        }
        ValueAnimator valueAnimator2 = this.mDownAnimator;
        if (valueAnimator2 == null || !valueAnimator2.isRunning()) {
            return;
        }
        this.mDownAnimator.cancel();
        this.mDownAnimator.removeAllListeners();
        this.mDownAnimator.removeAllUpdateListeners();
    }
}
