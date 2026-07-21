package miuix.flexible.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import androidx.core.view.GravityCompat;
import java.util.HashMap;
import java.util.Map;
import miuix.core.util.MiuixUIUtils;
import miuix.flexible.R;
import miuix.flexible.view.HyperCellLayout;

/* JADX INFO: loaded from: classes2.dex */
public class HyperCellAnimationHelper {
    protected static final Map<View, Animator> ANIMATOR_CACHE = new HashMap();
    public static final int DEFAULT_ANIMATION_DURATION = 350;

    public interface AnimationListener {
        void onAnimationCancel(View view);

        void onAnimationEnd(View view);

        void onAnimationStart(View view);

        void onAnimationUpdate(View view, float f);
    }

    private HyperCellAnimationHelper() {
    }

    public static void startAnimation(View view) {
        startAnimation(view, 8);
    }

    public static void startAnimation(View view, int i) {
        startAnimation(view, i, GravityCompat.START);
    }

    public static void startAnimation(View view, int i, int i2) {
        HyperCellLayout.LayoutParams layoutParams = (HyperCellLayout.LayoutParams) view.getLayoutParams();
        layoutParams.setAnimating(true);
        if (i == 8) {
            autoConfigAnimation(view);
        } else {
            layoutParams.setAnimSpec(i);
            layoutParams.setAnimationGravity(i2);
        }
    }

    public static void updateAnimation(View view, float f) {
        HyperCellLayout.LayoutParams layoutParams = (HyperCellLayout.LayoutParams) view.getLayoutParams();
        if (layoutParams.isAnimating()) {
            layoutParams.setAnimationProgress(f);
            if ((layoutParams.getAnimSpec() & 4) > 0) {
                view.setAlpha(f);
            }
            view.requestLayout();
        }
    }

    public static void stopAnimation(View view) {
        HyperCellLayout.LayoutParams layoutParams = (HyperCellLayout.LayoutParams) view.getLayoutParams();
        if (layoutParams.isAnimating()) {
            layoutParams.setAnimating(false);
            view.requestLayout();
        }
    }

    public static void startShowHideAnimation(View view, boolean z) {
        startShowHideAnimation(view, z, null);
    }

    public static void startShowHideAnimation(View view, boolean z, AnimationListener animationListener) {
        startShowHideAnimation(view, z, 350, 8, GravityCompat.START, animationListener);
    }

    public static void startShowHideAnimation(View view, boolean z, int i, int i2, int i3) {
        startShowHideAnimation(view, z, i, i2, i3, null);
    }

    public static void startShowHideAnimation(final View view, final boolean z, int i, final int i2, final int i3, final AnimationListener animationListener) {
        ValueAnimator valueAnimatorOfFloat;
        Map<View, Animator> map = ANIMATOR_CACHE;
        Animator animator = map.get(view);
        if (animator != null) {
            animator.cancel();
            map.remove(view);
        }
        stopAnimation(view);
        if (i2 == 0 || i == 0) {
            view.setVisibility(z ? 0 : 8);
            return;
        }
        if (z) {
            valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        } else {
            valueAnimatorOfFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
        }
        valueAnimatorOfFloat.setDuration(i);
        valueAnimatorOfFloat.setInterpolator(new DecelerateInterpolator());
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.flexible.animation.HyperCellAnimationHelper$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                HyperCellAnimationHelper.lambda$startShowHideAnimation$0(view, animationListener, valueAnimator);
            }
        });
        valueAnimatorOfFloat.addListener(new Animator.AnimatorListener() { // from class: miuix.flexible.animation.HyperCellAnimationHelper.1
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator2) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator2) {
                if (z) {
                    view.setVisibility(0);
                }
                HyperCellAnimationHelper.startAnimation(view, i2, i3);
                AnimationListener animationListener2 = animationListener;
                if (animationListener2 != null) {
                    animationListener2.onAnimationStart(view);
                }
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator2) {
                if (!z) {
                    view.setVisibility(8);
                }
                HyperCellAnimationHelper.stopAnimation(view);
                HyperCellAnimationHelper.ANIMATOR_CACHE.remove(view);
                AnimationListener animationListener2 = animationListener;
                if (animationListener2 != null) {
                    animationListener2.onAnimationEnd(view);
                }
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator2) {
                HyperCellAnimationHelper.stopAnimation(view);
                HyperCellAnimationHelper.ANIMATOR_CACHE.remove(view);
                AnimationListener animationListener2 = animationListener;
                if (animationListener2 != null) {
                    animationListener2.onAnimationCancel(view);
                }
            }
        });
        map.put(view, valueAnimatorOfFloat);
        valueAnimatorOfFloat.start();
    }

    static /* synthetic */ void lambda$startShowHideAnimation$0(View view, AnimationListener animationListener, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateAnimation(view, fFloatValue);
        if (animationListener != null) {
            animationListener.onAnimationUpdate(view, fFloatValue);
        }
    }

    private static void autoConfigAnimation(View view) {
        HyperCellLayout.LayoutParams layoutParams = (HyperCellLayout.LayoutParams) view.getLayoutParams();
        if (layoutParams == null) {
            return;
        }
        if (layoutParams.getAreaId() == R.id.area_head) {
            layoutParams.setAnimationGravity(GravityCompat.END);
            layoutParams.setAnimSpec(5);
            return;
        }
        if (layoutParams.getAreaId() == R.id.area_end) {
            layoutParams.setAnimationGravity(GravityCompat.START);
            layoutParams.setAnimSpec(5);
            return;
        }
        if (layoutParams.getAreaId() == R.id.area_title_comment || layoutParams.getAreaId() == R.id.area_comment || layoutParams.getAreaId() == R.id.area_text_button) {
            if (MiuixUIUtils.getFontLevel(view.getContext()) == 2) {
                layoutParams.setAnimationGravity(48);
                layoutParams.setAnimSpec(6);
                return;
            } else {
                layoutParams.setAnimationGravity(GravityCompat.START);
                layoutParams.setAnimSpec(5);
                return;
            }
        }
        layoutParams.setAnimationGravity(GravityCompat.START);
        layoutParams.setAnimSpec(7);
    }

    public static void addView(HyperCellLayout hyperCellLayout, View view, HyperCellLayout.LayoutParams layoutParams) {
        hyperCellLayout.addView(view, layoutParams);
        startShowHideAnimation(view, true);
    }

    public static void removeView(final HyperCellLayout hyperCellLayout, View view) {
        startShowHideAnimation(view, false, new AnimationListener() { // from class: miuix.flexible.animation.HyperCellAnimationHelper.2
            @Override // miuix.flexible.animation.HyperCellAnimationHelper.AnimationListener
            public void onAnimationCancel(View view2) {
            }

            @Override // miuix.flexible.animation.HyperCellAnimationHelper.AnimationListener
            public void onAnimationStart(View view2) {
            }

            @Override // miuix.flexible.animation.HyperCellAnimationHelper.AnimationListener
            public void onAnimationUpdate(View view2, float f) {
            }

            @Override // miuix.flexible.animation.HyperCellAnimationHelper.AnimationListener
            public void onAnimationEnd(View view2) {
                hyperCellLayout.removeView(view2);
            }
        });
    }
}
