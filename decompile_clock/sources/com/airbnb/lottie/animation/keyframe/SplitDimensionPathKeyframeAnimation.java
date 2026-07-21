package com.airbnb.lottie.animation.keyframe;

import android.graphics.PointF;
import com.airbnb.lottie.value.Keyframe;
import com.airbnb.lottie.value.LottieValueCallback;
import java.util.Collections;

/* JADX INFO: loaded from: classes.dex */
public class SplitDimensionPathKeyframeAnimation extends BaseKeyframeAnimation<PointF, PointF> {
    private final PointF point;
    private final PointF pointWithCallbackValues;
    private final BaseKeyframeAnimation<Float, Float> xAnimation;
    protected LottieValueCallback<Float> xValueCallback;
    private final BaseKeyframeAnimation<Float, Float> yAnimation;
    protected LottieValueCallback<Float> yValueCallback;

    public SplitDimensionPathKeyframeAnimation(BaseKeyframeAnimation<Float, Float> baseKeyframeAnimation, BaseKeyframeAnimation<Float, Float> baseKeyframeAnimation2) {
        super(Collections.emptyList());
        this.point = new PointF();
        this.pointWithCallbackValues = new PointF();
        this.xAnimation = baseKeyframeAnimation;
        this.yAnimation = baseKeyframeAnimation2;
        setProgress(getProgress());
    }

    public void setXValueCallback(LottieValueCallback<Float> lottieValueCallback) {
        LottieValueCallback<Float> lottieValueCallback2 = this.xValueCallback;
        if (lottieValueCallback2 != null) {
            lottieValueCallback2.setAnimation(null);
        }
        this.xValueCallback = lottieValueCallback;
        if (lottieValueCallback != null) {
            lottieValueCallback.setAnimation(this);
        }
    }

    public void setYValueCallback(LottieValueCallback<Float> lottieValueCallback) {
        LottieValueCallback<Float> lottieValueCallback2 = this.yValueCallback;
        if (lottieValueCallback2 != null) {
            lottieValueCallback2.setAnimation(null);
        }
        this.yValueCallback = lottieValueCallback;
        if (lottieValueCallback != null) {
            lottieValueCallback.setAnimation(this);
        }
    }

    @Override // com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation
    public void setProgress(float f) {
        this.xAnimation.setProgress(f);
        this.yAnimation.setProgress(f);
        this.point.set(this.xAnimation.getValue().floatValue(), this.yAnimation.getValue().floatValue());
        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).onValueChanged();
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation
    public PointF getValue() {
        return getValue((Keyframe<PointF>) null, 0.0f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation
    public PointF getValue(Keyframe<PointF> keyframe, float f) {
        Float valueInternal;
        Keyframe<Float> currentKeyframe;
        Keyframe<Float> currentKeyframe2;
        Float valueInternal2 = null;
        if (this.xValueCallback == null || (currentKeyframe2 = this.xAnimation.getCurrentKeyframe()) == null) {
            valueInternal = null;
        } else {
            float interpolatedCurrentKeyframeProgress = this.xAnimation.getInterpolatedCurrentKeyframeProgress();
            Float f2 = currentKeyframe2.endFrame;
            valueInternal = this.xValueCallback.getValueInternal(currentKeyframe2.startFrame, f2 == null ? currentKeyframe2.startFrame : f2.floatValue(), currentKeyframe2.startValue, currentKeyframe2.endValue, f, f, interpolatedCurrentKeyframeProgress);
        }
        if (this.yValueCallback != null && (currentKeyframe = this.yAnimation.getCurrentKeyframe()) != null) {
            float interpolatedCurrentKeyframeProgress2 = this.yAnimation.getInterpolatedCurrentKeyframeProgress();
            Float f3 = currentKeyframe.endFrame;
            valueInternal2 = this.yValueCallback.getValueInternal(currentKeyframe.startFrame, f3 == null ? currentKeyframe.startFrame : f3.floatValue(), currentKeyframe.startValue, currentKeyframe.endValue, f, f, interpolatedCurrentKeyframeProgress2);
        }
        if (valueInternal == null) {
            this.pointWithCallbackValues.set(this.point.x, 0.0f);
        } else {
            this.pointWithCallbackValues.set(valueInternal.floatValue(), 0.0f);
        }
        if (valueInternal2 == null) {
            PointF pointF = this.pointWithCallbackValues;
            pointF.set(pointF.x, this.point.y);
        } else {
            PointF pointF2 = this.pointWithCallbackValues;
            pointF2.set(pointF2.x, valueInternal2.floatValue());
        }
        return this.pointWithCallbackValues;
    }
}
