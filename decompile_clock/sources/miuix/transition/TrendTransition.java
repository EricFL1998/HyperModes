package miuix.transition;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.ViewCompat;
import androidx.transition.MiuixTransitionUtils;
import java.util.Map;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.property.ViewProperty;
import miuix.internal.util.ViewUtils;

/* JADX INFO: loaded from: classes3.dex */
public class TrendTransition extends ChangeBounds {
    private static final String END_TAG = "trendtransition_end";
    private static final String PROPNAME_ALPHA = "android:transition:alpha";
    private static final String START_TAG = "trendtransition_start";
    AnimState invisibleState;
    private AnimConfig mEnterAnimConfig;
    private AnimConfig mExitAnimConfig;
    private int[] mTempLocation;

    public TrendTransition() {
        this.mTempLocation = new int[2];
        this.invisibleState = new AnimState().add(ViewProperty.ALPHA, 0.0d);
    }

    public TrendTransition(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTempLocation = new int[2];
        this.invisibleState = new AnimState().add(ViewProperty.ALPHA, 0.0d);
    }

    @Override // miuix.transition.ChangeBounds
    protected void captureValues(TransitionValues transitionValues) {
        super.captureValues(transitionValues);
        View view = transitionValues.view;
        if (!ViewCompat.isLaidOut(view) && view.getWidth() == 0 && view.getHeight() == 0) {
            return;
        }
        transitionValues.values.put(PROPNAME_ALPHA, Float.valueOf(view.getAlpha()));
    }

    @Override // miuix.transition.ChangeBounds, miuix.transition.MiuixTransition
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // miuix.transition.ChangeBounds, miuix.transition.MiuixTransition
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // miuix.transition.ChangeBounds, miuix.transition.MiuixTransition
    public void createAnimator(final ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
        AnimConfig animConfig;
        AnimConfig animConfig2;
        int i;
        if (transitionValues == null || transitionValues2 == null) {
            return;
        }
        Map<String, Object> map = transitionValues.values;
        Map<String, Object> map2 = transitionValues2.values;
        ViewGroup viewGroup2 = (ViewGroup) map.get("android:transition:parent");
        final ViewGroup viewGroup3 = (ViewGroup) map2.get("android:transition:parent");
        if (viewGroup2 == null || viewGroup3 == null) {
            return;
        }
        final View view = transitionValues2.view;
        final View view2 = transitionValues.view;
        final float alpha = view.getAlpha();
        AnimState animStateAdd = new AnimState().add(ViewProperty.ALPHA, alpha);
        AnimConfig animConfig3 = this.mEnterAnimConfig;
        if (animConfig3 == null) {
            animConfig3 = getAnimConfig();
        }
        AnimConfig animConfig4 = this.mExitAnimConfig;
        if (animConfig4 == null) {
            animConfig4 = getAnimConfig();
        }
        final AnimConfig animConfig5 = new AnimConfig();
        animConfig5.addListeners(new TransitionListener() { // from class: miuix.transition.TrendTransition.1
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj) {
                if (obj == TrendTransition.END_TAG) {
                    if (TrendTransition.this.mNumInstances == 0) {
                        TrendTransition.this.onTransitionStart();
                    }
                    TrendTransition.this.mNumInstances++;
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                if (obj == TrendTransition.END_TAG) {
                    TrendTransition.this.mNumInstances--;
                    if (TrendTransition.this.mNumInstances == 0) {
                        animConfig5.removeListeners(this);
                        TrendTransition.this.onTransitionEnd();
                    }
                }
            }
        });
        if (parentMatches(viewGroup2, viewGroup3)) {
            viewGroup2.removeViewInLayout(view2);
            viewGroup3.getOverlay().add(view2);
            Rect rect = (Rect) transitionValues.values.get("android:transition:bounds");
            Rect rect2 = (Rect) transitionValues2.values.get("android:transition:bounds");
            int i2 = rect.left;
            int i3 = rect2.left;
            int i4 = rect.top;
            int i5 = rect2.top;
            int i6 = rect.right;
            int i7 = rect2.right;
            int i8 = rect.bottom;
            int i9 = rect2.bottom;
            if (i2 == i3 && i4 == i5 && i6 == i7 && i8 == i9) {
                animConfig = animConfig3;
                animConfig2 = animConfig4;
                i = 1;
            } else {
                animConfig = animConfig3;
                ChangeBounds.ViewBounds viewBounds = new ChangeBounds.ViewBounds(view2);
                ChangeBounds.ViewBounds viewBounds2 = new ChangeBounds.ViewBounds(view);
                ViewUtils.setLeftTopRightBottom(view, i2, i4, i6, i8);
                animConfig2 = animConfig4;
                i = 1;
                AnimState animStateAdd2 = new AnimState(START_TAG).add((ViewProperty) this.mLeftProperty, i2, 4).add((ViewProperty) this.mTopProperty, i4, 4).add((ViewProperty) this.mRightProperty, i6, 4).add((ViewProperty) this.mBottomProperty, i8, 4);
                AnimState animStateAdd3 = new AnimState(END_TAG).add((ViewProperty) this.mLeftProperty, i3, 4).add((ViewProperty) this.mTopProperty, i5, 4).add((ViewProperty) this.mRightProperty, i7, 4).add((ViewProperty) this.mBottomProperty, i9, 4);
                animConfig5.addListeners(new TransitionListener() { // from class: miuix.transition.TrendTransition.2
                    @Override // miuix.animation.listener.TransitionListener
                    public void onComplete(Object obj) {
                        if (obj == TrendTransition.END_TAG) {
                            animConfig5.removeListeners(this);
                            viewGroup3.getOverlay().remove(view2);
                        }
                    }
                });
                addTransitionRunner(new MiuixTransition.TransitionRunner(viewBounds, animStateAdd2, animStateAdd3, animConfig2, animConfig5));
                addTransitionRunner(new MiuixTransition.TransitionRunner(viewBounds2, animStateAdd2, animStateAdd3, animConfig, animConfig5));
            }
            View[] viewArr = new View[i];
            viewArr[0] = view;
            Folme.useAt(viewArr).state().setTo(this.invisibleState);
            AnimState animState = this.invisibleState;
            AnimConfig[] animConfigArr = new AnimConfig[i];
            animConfigArr[0] = animConfig;
            addTransitionRunner(new MiuixTransition.TransitionRunner(view, animState, animStateAdd, animConfigArr));
            AnimState animState2 = this.invisibleState;
            AnimConfig[] animConfigArr2 = new AnimConfig[i];
            animConfigArr2[0] = animConfig2;
            addTransitionRunner(new MiuixTransition.TransitionRunner(view2, animStateAdd, animState2, animConfigArr2));
            return;
        }
        AnimConfig animConfig6 = animConfig3;
        AnimConfig animConfig7 = animConfig4;
        viewGroup.getLocationInWindow(this.mTempLocation);
        int iIntValue = ((Integer) transitionValues.values.get("android:transition:windowX")).intValue() - this.mTempLocation[0];
        int iIntValue2 = ((Integer) transitionValues.values.get("android:transition:windowY")).intValue() - this.mTempLocation[1];
        int iIntValue3 = ((Integer) transitionValues2.values.get("android:transition:windowX")).intValue() - this.mTempLocation[0];
        int iIntValue4 = ((Integer) transitionValues2.values.get("android:transition:windowY")).intValue() - this.mTempLocation[1];
        float fFloatValue = ((Float) transitionValues.values.get("android:transition:width")).floatValue();
        float fFloatValue2 = ((Float) transitionValues.values.get("android:transition:height")).floatValue();
        float fFloatValue3 = ((Float) transitionValues2.values.get("android:transition:width")).floatValue();
        float fFloatValue4 = ((Float) transitionValues2.values.get("android:transition:height")).floatValue();
        if (iIntValue == iIntValue3 && iIntValue2 == iIntValue4) {
            return;
        }
        final View viewCopyViewImage = MiuixTransitionUtils.copyViewImage(viewGroup, view2, viewGroup2);
        final View viewCopyViewImage2 = MiuixTransitionUtils.copyViewImage(viewGroup, view, viewGroup3);
        viewGroup.getOverlay().add(viewCopyViewImage);
        viewGroup.getOverlay().add(viewCopyViewImage2);
        view.setAlpha(0.0f);
        viewCopyViewImage.setAlpha(1.0f);
        AnimState animStateAdd4 = new AnimState().add(ViewProperty.X, iIntValue).add(ViewProperty.Y, iIntValue2).add(ViewProperty.WIDTH, fFloatValue).add(ViewProperty.HEIGHT, fFloatValue2);
        AnimState animStateAdd5 = new AnimState(END_TAG).add(ViewProperty.X, iIntValue3).add(ViewProperty.Y, iIntValue4).add(ViewProperty.WIDTH, fFloatValue3).add(ViewProperty.HEIGHT, fFloatValue4);
        animConfig5.addListeners(new TransitionListener() { // from class: miuix.transition.TrendTransition.3
            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                if (obj == TrendTransition.END_TAG) {
                    animConfig5.removeListeners(this);
                    viewGroup.getOverlay().remove(viewCopyViewImage2);
                    viewGroup.getOverlay().remove(viewCopyViewImage);
                    view.setAlpha(alpha);
                }
            }
        });
        Folme.useAt(viewCopyViewImage).state().setTo(animStateAdd4);
        Folme.useAt(viewCopyViewImage2).state().setTo(animStateAdd4).setTo(this.invisibleState);
        addTransitionRunner(new MiuixTransition.TransitionRunner(viewCopyViewImage, animStateAdd4, animStateAdd5, animConfig7, animConfig5));
        addTransitionRunner(new MiuixTransition.TransitionRunner(viewCopyViewImage, animStateAdd, this.invisibleState, animConfig7));
        addTransitionRunner(new MiuixTransition.TransitionRunner(viewCopyViewImage2, animStateAdd4, animStateAdd5, animConfig6, animConfig5));
        addTransitionRunner(new MiuixTransition.TransitionRunner(viewCopyViewImage2, this.invisibleState, animStateAdd, animConfig6));
    }

    public void setEnterAnimConfig(AnimConfig animConfig) {
        this.mEnterAnimConfig = animConfig;
    }

    public void setExitAnimConfig(AnimConfig animConfig) {
        this.mExitAnimConfig = animConfig;
    }
}
