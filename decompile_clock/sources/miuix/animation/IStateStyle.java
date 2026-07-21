package miuix.animation;

import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.physics.FactorOperator;
import miuix.animation.property.FloatProperty;
import miuix.animation.utils.EaseManager;

/* JADX INFO: loaded from: classes2.dex */
public interface IStateStyle extends FolmeStyle, IStateContainer {
    IStateStyle add(String str, float f);

    IStateStyle add(String str, float f, long j);

    IStateStyle add(String str, int i);

    IStateStyle add(String str, int i, long j);

    IStateStyle add(FloatProperty floatProperty, float f);

    IStateStyle add(FloatProperty floatProperty, float f, long j);

    IStateStyle add(FloatProperty floatProperty, int i);

    IStateStyle add(FloatProperty floatProperty, int i, long j);

    IStateStyle addInitProperty(String str, float f);

    IStateStyle addInitProperty(String str, int i);

    IStateStyle addInitProperty(FloatProperty floatProperty, float f);

    IStateStyle addInitProperty(FloatProperty floatProperty, int i);

    IStateStyle addListener(TransitionListener transitionListener);

    void cancel(AnimState animState);

    AnimState getCurrentState();

    float getPredictFriction(FloatProperty floatProperty, float f);

    float getPredictValue(FloatProperty floatProperty, float... fArr);

    IStateStyle removeListener(TransitionListener transitionListener);

    IStateStyle set(Object obj);

    IStateStyle setConfig(AnimConfig animConfig, FloatProperty... floatPropertyArr);

    IStateStyle setEase(int i, float... fArr);

    IStateStyle setEase(int i, FactorOperator... factorOperatorArr);

    IStateStyle setEase(FloatProperty floatProperty, int i, float... fArr);

    IStateStyle setEase(EaseManager.EaseStyle easeStyle, FloatProperty... floatPropertyArr);

    IStateStyle setTransitionFlags(long j, FloatProperty... floatPropertyArr);
}
