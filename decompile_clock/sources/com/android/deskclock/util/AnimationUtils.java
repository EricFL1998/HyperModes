package com.android.deskclock.util;

import android.animation.Animator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.view.FabView;
import miuix.view.animation.SineEaseInOutInterpolator;
import miuix.view.animation.SineEaseOutInterpolator;

/* JADX INFO: loaded from: classes.dex */
public class AnimationUtils {
    public static final float ALPHA_0 = 0.0f;
    public static final float ALPHA_0_2 = 0.2f;
    public static final float ALPHA_1 = 1.0f;
    public static final long ANIMATE_DELAY_DURATION = 100;
    public static final long ANIMATE_IN_DURATION = 300;
    public static final long ANIMATE_OUT_DURATION = 300;
    public static final long TIMER_ANIMATE_IN_DURATION = 300;
    private static SineEaseOutInterpolator mInterpolator = new SineEaseOutInterpolator();
    private static SineEaseOutInterpolator mSineEaseOutInterpolator = new SineEaseOutInterpolator();

    public static class AnimationListenerAdapter implements Animation.AnimationListener {
        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationEnd(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationStart(Animation animation) {
        }
    }

    public static class AnimatorListenerAdapter implements Animator.AnimatorListener {
        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
        }
    }

    public static void animateTranslateOut(View view, float f, Animator.AnimatorListener animatorListener) {
        animateTranslateX(view, 0.0f, f, 1.0f, 0.0f, 300L, 0L, animatorListener);
    }

    public static void animateTranslateIn(View view, float f, long j, Animator.AnimatorListener animatorListener) {
        animateTranslateX(view, f, 0.0f, 0.0f, 1.0f, 300L, j, animatorListener);
    }

    public static void animateTranslateX(View view, float f, float f2, float f3, float f4, long j, long j2, Animator.AnimatorListener animatorListener) {
        if (view != null) {
            view.setTranslationX(f);
            view.setAlpha(f3);
            view.animate().setListener(animatorListener).alpha(f4).translationX(f2).setInterpolator(mInterpolator).setDuration(j).setStartDelay(j2).start();
        }
    }

    public static void animateTranslateY(View view, float f, float f2, long j) {
        animateTranslateY(view, f, f2, j, mInterpolator);
    }

    public static void animateTranslateY(View view, float f, float f2, long j, Interpolator interpolator) {
        if (view != null) {
            view.setTranslationY(f);
            view.animate().translationY(f2).setInterpolator(interpolator).setDuration(j).start();
        }
    }

    public static void animateAlphaIn(View view, long j) {
        animateAlphaIn(view, j, mSineEaseOutInterpolator);
    }

    public static void animateAlphaIn(View view, long j, float f) {
        if (view != null) {
            view.setAlpha(0.0f);
            view.animate().alpha(f).setDuration(j).setInterpolator(mSineEaseOutInterpolator).start();
        }
    }

    public static void animateAlphaIn(View view, long j, long j2) {
        animateAlphaIn(view, j, mSineEaseOutInterpolator, j2);
    }

    public static void animateAlphaIn(View view, long j, Interpolator interpolator, long j2) {
        if (view != null) {
            view.setAlpha(0.0f);
            view.animate().alpha(1.0f).setDuration(j).setInterpolator(interpolator).setStartDelay(j2).start();
        }
    }

    public static void animateAlphaIn(View view, long j, Interpolator interpolator) {
        if (view != null) {
            view.setAlpha(0.0f);
            view.animate().alpha(1.0f).setDuration(j).setInterpolator(interpolator).start();
        }
    }

    public static void animateAlphaIn(View view, Animator.AnimatorListener animatorListener) {
        animateAlpha(view, 0.0f, 1.0f, 300L, 0L, animatorListener);
    }

    public static void animateAlphaIn1(View view, Animator.AnimatorListener animatorListener) {
        animateAlpha(view, 0.0f, 1.0f, 2000L, 0L, animatorListener);
    }

    public static void animateAlphaIn(long j, Animator.AnimatorListener animatorListener, View... viewArr) {
        for (View view : viewArr) {
            animateAlpha(view, 0.0f, 1.0f, 300L, j, animatorListener);
        }
    }

    public static void animateAlphaInWithDuration(View view, long j, Animator.AnimatorListener animatorListener) {
        animateAlpha(view, 0.0f, 1.0f, j, 0L, animatorListener);
    }

    public static void animateAlphaIn(View view, long j, Animator.AnimatorListener animatorListener) {
        animateAlpha(view, 0.0f, 1.0f, 300L, j, animatorListener);
    }

    public static void animateAlphaOut(Animator.AnimatorListener animatorListener, View... viewArr) {
        for (View view : viewArr) {
            animateAlpha(view, 1.0f, 0.0f, 300L, 0L, animatorListener);
        }
    }

    public static void animateAlphaOut(View view, Animator.AnimatorListener animatorListener) {
        animateAlpha(view, 1.0f, 0.0f, 300L, 0L, animatorListener);
    }

    public static void animateAlphaGone(final View view) {
        if (view == null) {
            return;
        }
        animateAlpha(view, 1.0f, 0.0f, 500L, 0L, new Animator.AnimatorListener() { // from class: com.android.deskclock.util.AnimationUtils.1
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                view.setAlpha(1.0f);
                view.setVisibility(8);
            }
        });
    }

    public static void animateAlphaOut1(View view, Animator.AnimatorListener animatorListener) {
        animateAlpha(view, 1.0f, 0.0f, 2000L, 0L, animatorListener);
    }

    public static void animateAlpha(View view, float f, float f2, long j, long j2, Animator.AnimatorListener animatorListener) {
        if (view != null) {
            view.setAlpha(f);
            view.animate().setListener(animatorListener).alpha(f2).setDuration(j).setInterpolator(mInterpolator).setStartDelay(j2).start();
        }
    }

    public static void animateAlphaVisiable(final View view) {
        animateAlpha(view, 0.0f, 1.0f, 500L, 0L, new Animator.AnimatorListener() { // from class: com.android.deskclock.util.AnimationUtils.2
            boolean cancel = false;

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (this.cancel) {
                    return;
                }
                view.setAlpha(1.0f);
                view.setVisibility(0);
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                this.cancel = true;
            }
        });
    }

    public static void resetFabAnimate(View... viewArr) {
        for (View view : viewArr) {
            if (view != null) {
                view.clearAnimation();
                view.setAlpha(1.0f);
                if (view instanceof FabView) {
                    FabView fabView = (FabView) view;
                    fabView.setImageAlpha(255);
                    fabView.resetShadowColor();
                }
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
            }
        }
    }

    public static void showView(View... viewArr) {
        for (View view : viewArr) {
            if (view != null) {
                view.setVisibility(0);
            }
        }
    }

    public static void hideView(View... viewArr) {
        for (View view : viewArr) {
            if (view != null) {
                view.setVisibility(8);
            }
        }
    }

    public static void animateAlpha(View view, float f, float f2, long j, Animation.AnimationListener animationListener) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(f, f2);
        alphaAnimation.setDuration(j);
        alphaAnimation.setAnimationListener(animationListener);
        alphaAnimation.setInterpolator(mInterpolator);
        view.startAnimation(alphaAnimation);
    }

    public static Animation loadAnimation(int i) {
        return android.view.animation.AnimationUtils.loadAnimation(DeskClockApp.getAppDEContext(), i);
    }

    public static void animateAlphaScaleIn(View view, Animator.AnimatorListener animatorListener) {
        animateAlphaScale(view, 0.0f, 1.0f, 0.8f, 1.0f, 60L, animatorListener);
    }

    public static void animateAlphaScale(View view, float f, float f2, float f3, float f4, long j, Animator.AnimatorListener animatorListener) {
        if (view != null) {
            view.setAlpha(f);
            view.setScaleX(f3);
            view.setScaleY(f3);
            view.animate().setListener(animatorListener).alpha(f2).setStartDelay(j).scaleX(f4).scaleY(f4).setDuration(300L).setInterpolator(new SineEaseInOutInterpolator()).start();
        }
    }

    public static void animateScale(View view, float f, float f2, Animator.AnimatorListener animatorListener) {
        if (view != null) {
            view.setScaleX(f);
            view.setScaleY(f);
            view.animate().setListener(animatorListener).scaleX(f2).scaleY(f2).setDuration(300L).setInterpolator(new SineEaseInOutInterpolator()).start();
        }
    }
}
