package miuix.transition;

import android.content.Context;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.ViewCompat;
import java.util.Map;
import miuix.animation.Folme;
import miuix.animation.controller.AnimState;
import miuix.animation.property.ViewProperty;
import miuix.animation.property.ViewPropertyExt;

/* JADX INFO: loaded from: classes3.dex */
public class SharedElementTransition extends ChangeBounds {
    private static final String END_TRG = "sharedelement_end";
    private static final String PROPNAME_TRANSLATION_X = "android:transition:translationX";
    private static final String PROPNAME_TRANSLATION_Y = "android:transition:translationY";
    private static final String PROPNAME_TRANSLATION_Z = "android:transition:translationZ";
    static Map<String, ViewProperty> sViewPropertyMap;
    private static final String PROPNAME_Z = "android:transition:z";
    private static final String PROPNAME_SCALE_X = "android:transition:scaleX";
    private static final String PROPNAME_SCALE_Y = "android:transition:scaleY";
    private static final String PROPNAME_ROTATION = "android:transition:rotation";
    private static final String PROPNAME_ROTATION_X = "android:transition:rotationX";
    private static final String PROPNAME_ROTATION_Y = "android:transition:rotationY";
    private static final String PROPNAME_SCROLL_X = "android:transition:scrollX";
    private static final String PROPNAME_SCROLL_Y = "android:transition:scrollY";
    private static final String PROPNAME_ALPHA = "android:transition:alpha";
    private static final String PROPNAME_BACKGROUND = "android:transition:background";
    private static final String PROPNAME_FOREGROUND = "android:transition:foreground";
    private static final String[] sTransitionProperties = {"android:transition:bounds", "android:transition:parent", "android:transition:windowX", "android:transition:windowY", PROPNAME_Z, PROPNAME_SCALE_X, PROPNAME_SCALE_Y, PROPNAME_ROTATION, PROPNAME_ROTATION_X, PROPNAME_ROTATION_Y, PROPNAME_SCROLL_X, PROPNAME_SCROLL_Y, PROPNAME_ALPHA, PROPNAME_BACKGROUND, PROPNAME_FOREGROUND};

    static {
        ArrayMap arrayMap = new ArrayMap();
        sViewPropertyMap = arrayMap;
        arrayMap.put(PROPNAME_Z, ViewProperty.Z);
        sViewPropertyMap.put(PROPNAME_TRANSLATION_X, ViewProperty.TRANSLATION_X);
        sViewPropertyMap.put(PROPNAME_TRANSLATION_Y, ViewProperty.TRANSLATION_Y);
        sViewPropertyMap.put(PROPNAME_TRANSLATION_Z, ViewProperty.TRANSLATION_Z);
        sViewPropertyMap.put(PROPNAME_SCALE_X, ViewProperty.SCALE_X);
        sViewPropertyMap.put(PROPNAME_SCALE_Y, ViewProperty.SCALE_Y);
        sViewPropertyMap.put(PROPNAME_ROTATION, ViewProperty.ROTATION);
        sViewPropertyMap.put(PROPNAME_ROTATION_X, ViewProperty.ROTATION_X);
        sViewPropertyMap.put(PROPNAME_ROTATION_Y, ViewProperty.ROTATION_Y);
        sViewPropertyMap.put(PROPNAME_SCROLL_X, ViewProperty.SCROLL_X);
        sViewPropertyMap.put(PROPNAME_SCROLL_Y, ViewProperty.SCROLL_Y);
        sViewPropertyMap.put(PROPNAME_ALPHA, ViewProperty.ALPHA);
        sViewPropertyMap.put(PROPNAME_BACKGROUND, ViewPropertyExt.BACKGROUND);
        sViewPropertyMap.put(PROPNAME_FOREGROUND, ViewPropertyExt.FOREGROUND);
    }

    public SharedElementTransition() {
    }

    public SharedElementTransition(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // miuix.transition.ChangeBounds, miuix.transition.MiuixTransition
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    @Override // miuix.transition.ChangeBounds
    protected void captureValues(TransitionValues transitionValues) {
        super.captureValues(transitionValues);
        View view = transitionValues.view;
        if (!ViewCompat.isLaidOut(view) && view.getWidth() == 0 && view.getHeight() == 0) {
            return;
        }
        for (Map.Entry<String, ViewProperty> entry : sViewPropertyMap.entrySet()) {
            if (entry.getKey().equals(PROPNAME_BACKGROUND)) {
                transitionValues.values.put(entry.getKey(), Integer.valueOf(ViewPropertyExt.BACKGROUND.getIntValue(view)));
            } else if (entry.getKey().equals(PROPNAME_FOREGROUND)) {
                transitionValues.values.put(entry.getKey(), Integer.valueOf(ViewPropertyExt.FOREGROUND.getIntValue(view)));
            } else {
                transitionValues.values.put(entry.getKey(), Float.valueOf(entry.getValue().getValue(view)));
            }
        }
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
    public void createAnimator(ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
        if (transitionValues == null || transitionValues2 == null) {
            return;
        }
        View view = transitionValues2.view;
        AnimState diffAnimState = getDiffAnimState(transitionValues, transitionValues2);
        diffAnimState.setTag(END_TRG);
        if (!diffAnimState.isEmpty()) {
            addTransitionRunner(new MiuixTransition.TransitionRunner(Folme.useAt(view).state(), (Object) null, (Object) END_TRG, getAnimConfig()));
        }
        super.createAnimator(viewGroup, transitionValues, transitionValues2);
    }

    protected AnimState getDiffAnimState(TransitionValues transitionValues, TransitionValues transitionValues2) {
        if (transitionValues == null || transitionValues2 == null) {
            return null;
        }
        AnimState animState = new AnimState();
        for (String str : sViewPropertyMap.keySet()) {
            if (str.equals(PROPNAME_BACKGROUND) || str.equals(PROPNAME_FOREGROUND)) {
                int iIntValue = ((Integer) transitionValues.values.get(str)).intValue();
                int iIntValue2 = ((Integer) transitionValues2.values.get(str)).intValue();
                if (iIntValue != iIntValue2) {
                    animState.add(sViewPropertyMap.get(str), iIntValue2);
                }
            } else {
                float fFloatValue = ((Float) transitionValues.values.get(str)).floatValue();
                float fFloatValue2 = ((Float) transitionValues2.values.get(str)).floatValue();
                if (diff(fFloatValue, fFloatValue2)) {
                    animState.add(sViewPropertyMap.get(str), fFloatValue2);
                }
            }
        }
        return animState;
    }
}
