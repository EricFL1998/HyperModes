package androidx.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.res.TypedArrayUtils;

/* JADX INFO: loaded from: classes.dex */
public class Fade extends Visibility {
    public static final int IN = 1;
    private static final String LOG_TAG = "Fade";
    public static final int OUT = 2;
    private static final String PROPNAME_TRANSITION_ALPHA = "android:fade:transitionAlpha";

    @Override // androidx.transition.Transition
    public boolean isSeekingSupported() {
        return true;
    }

    public Fade(int i) {
        setMode(i);
    }

    public Fade() {
    }

    public Fade(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, Styleable.FADE);
        setMode(TypedArrayUtils.getNamedInt(typedArrayObtainStyledAttributes, (XmlResourceParser) attributeSet, "fadingMode", 0, getMode()));
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // androidx.transition.Visibility, androidx.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        Float fValueOf = (Float) transitionValues.view.getTag(R.id.transition_pause_alpha);
        if (fValueOf == null) {
            if (transitionValues.view.getVisibility() == 0) {
                fValueOf = Float.valueOf(ViewUtils.getTransitionAlpha(transitionValues.view));
            } else {
                fValueOf = Float.valueOf(0.0f);
            }
        }
        transitionValues.values.put(PROPNAME_TRANSITION_ALPHA, fValueOf);
    }

    private Animator createAnimation(View view, float f, float f2) {
        if (f == f2) {
            return null;
        }
        ViewUtils.setTransitionAlpha(view, f);
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(view, ViewUtils.TRANSITION_ALPHA, f2);
        FadeAnimatorListener fadeAnimatorListener = new FadeAnimatorListener(view);
        objectAnimatorOfFloat.addListener(fadeAnimatorListener);
        getRootTransition().addListener(fadeAnimatorListener);
        return objectAnimatorOfFloat;
    }

    @Override // androidx.transition.Visibility
    public Animator onAppear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
        ViewUtils.saveNonTransitionAlpha(view);
        return createAnimation(view, getStartAlpha(transitionValues, 0.0f), 1.0f);
    }

    @Override // androidx.transition.Visibility
    public Animator onDisappear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
        ViewUtils.saveNonTransitionAlpha(view);
        Animator animatorCreateAnimation = createAnimation(view, getStartAlpha(transitionValues, 1.0f), 0.0f);
        if (animatorCreateAnimation == null) {
            ViewUtils.setTransitionAlpha(view, getStartAlpha(transitionValues2, 1.0f));
        }
        return animatorCreateAnimation;
    }

    private static float getStartAlpha(TransitionValues transitionValues, float f) {
        Float f2;
        return (transitionValues == null || (f2 = (Float) transitionValues.values.get(PROPNAME_TRANSITION_ALPHA)) == null) ? f : f2.floatValue();
    }

    private static class FadeAnimatorListener extends AnimatorListenerAdapter implements Transition.TransitionListener {
        private boolean mLayerTypeChanged = false;
        private final View mView;

        @Override // androidx.transition.Transition.TransitionListener
        public void onTransitionCancel(Transition transition) {
        }

        @Override // androidx.transition.Transition.TransitionListener
        public void onTransitionEnd(Transition transition) {
        }

        @Override // androidx.transition.Transition.TransitionListener
        public void onTransitionStart(Transition transition) {
        }

        @Override // androidx.transition.Transition.TransitionListener
        public void onTransitionStart(Transition transition, boolean z) {
        }

        FadeAnimatorListener(View view) {
            this.mView = view;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            if (this.mView.hasOverlappingRendering() && this.mView.getLayerType() == 0) {
                this.mLayerTypeChanged = true;
                this.mView.setLayerType(2, null);
            }
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            onAnimationEnd(animator, false);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator, boolean z) {
            if (this.mLayerTypeChanged) {
                this.mView.setLayerType(0, null);
            }
            if (z) {
                return;
            }
            ViewUtils.setTransitionAlpha(this.mView, 1.0f);
            ViewUtils.clearNonTransitionAlpha(this.mView);
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
            ViewUtils.setTransitionAlpha(this.mView, 1.0f);
        }

        @Override // androidx.transition.Transition.TransitionListener
        public void onTransitionPause(Transition transition) {
            this.mView.setTag(R.id.transition_pause_alpha, Float.valueOf(this.mView.getVisibility() == 0 ? ViewUtils.getTransitionAlpha(this.mView) : 0.0f));
        }

        @Override // androidx.transition.Transition.TransitionListener
        public void onTransitionResume(Transition transition) {
            this.mView.setTag(R.id.transition_pause_alpha, null);
        }
    }
}
