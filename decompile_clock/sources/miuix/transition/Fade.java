package miuix.transition;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import miuix.animation.Folme;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.property.ViewProperty;

/* JADX INFO: loaded from: classes3.dex */
public class Fade extends Visibility {
    protected static final String FADE_IN_END_TAG = "fade_in_end";
    protected static final String FADE_OUT_END_TAG = "fade_out_end";
    public static final int IN = 1;
    public static final int OUT = 2;
    protected static final String PROPNAME_TRANSITION_ALPHA = "android:fade:transitionAlpha";
    AnimState invisibleState;
    AnimState visibleState;

    public Fade(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.visibleState = new AnimState(FADE_IN_END_TAG).add(ViewProperty.AUTO_ALPHA, 1.0d);
        this.invisibleState = new AnimState(FADE_OUT_END_TAG).add(ViewProperty.AUTO_ALPHA, 0.0d);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.Fade);
        setMode(typedArrayObtainStyledAttributes.getInt(R.styleable.Fade_fadingMode, getMode()));
        typedArrayObtainStyledAttributes.recycle();
    }

    public Fade(int i) {
        this.visibleState = new AnimState(FADE_IN_END_TAG).add(ViewProperty.AUTO_ALPHA, 1.0d);
        this.invisibleState = new AnimState(FADE_OUT_END_TAG).add(ViewProperty.AUTO_ALPHA, 0.0d);
        setMode(i);
    }

    @Override // miuix.transition.Visibility, miuix.transition.MiuixTransition
    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        transitionValues.values.put(PROPNAME_TRANSITION_ALPHA, Float.valueOf(transitionValues.view.getAlpha()));
    }

    @Override // miuix.transition.Visibility, miuix.transition.MiuixTransition
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        transitionValues.values.put(PROPNAME_TRANSITION_ALPHA, Float.valueOf(transitionValues.view.getAlpha()));
    }

    @Override // miuix.transition.Visibility
    public void onAppear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
        float startAlpha = getStartAlpha(transitionValues, 0.0f);
        AnimState animStateAdd = new AnimState().add(ViewProperty.ALPHA, startAlpha != 1.0f ? startAlpha : 0.0f);
        final AnimConfig animConfig = new AnimConfig();
        animConfig.addListeners(new TransitionListener() { // from class: miuix.transition.Fade.1
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj) {
                if (obj == Fade.FADE_IN_END_TAG) {
                    if (Fade.this.mNumInstances == 0) {
                        Fade.this.onTransitionStart();
                    }
                    Fade.this.mNumInstances++;
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                if (obj == Fade.FADE_IN_END_TAG) {
                    Fade.this.mNumInstances--;
                    if (Fade.this.mNumInstances == 0) {
                        animConfig.removeListeners(this);
                        Fade.this.onTransitionEnd();
                    }
                }
            }
        });
        IStateStyle iStateStyleState = Folme.useAt(view).state();
        iStateStyleState.setTo(animStateAdd);
        addTransitionRunner(new MiuixTransition.TransitionRunner(iStateStyleState, (Object) animStateAdd, (Object) this.visibleState, getAnimConfig(), animConfig));
    }

    @Override // miuix.transition.Visibility
    public void onDisappear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2, TransitionListener transitionListener) {
        AnimState animStateAdd = new AnimState().add(ViewProperty.AUTO_ALPHA, getStartAlpha(transitionValues, 1.0f));
        final AnimConfig animConfig = new AnimConfig();
        animConfig.addListeners(new TransitionListener() { // from class: miuix.transition.Fade.2
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj) {
                if (obj == Fade.FADE_OUT_END_TAG) {
                    if (Fade.this.mNumInstances == 0) {
                        Fade.this.onTransitionStart();
                    }
                    Fade.this.mNumInstances++;
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                if (obj == Fade.FADE_OUT_END_TAG) {
                    Fade.this.mNumInstances--;
                    if (Fade.this.mNumInstances == 0) {
                        animConfig.removeListeners(this);
                        Fade.this.onTransitionEnd();
                    }
                }
            }
        });
        IStateStyle iStateStyleState = Folme.useAt(view).state();
        iStateStyleState.setTo(animStateAdd);
        addTransitionRunner(new MiuixTransition.TransitionRunner(iStateStyleState, (Object) animStateAdd, (Object) this.invisibleState, getAnimConfig(), animConfig));
    }

    private static float getStartAlpha(TransitionValues transitionValues, float f) {
        Float f2;
        return (transitionValues == null || (f2 = (Float) transitionValues.values.get(PROPNAME_TRANSITION_ALPHA)) == null) ? f : f2.floatValue();
    }
}
