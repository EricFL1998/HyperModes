package com.airbnb.lottie.animation.keyframe;

import com.airbnb.lottie.utils.MiscUtils;
import com.airbnb.lottie.value.Keyframe;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class IntegerKeyframeAnimation extends KeyframeAnimation<Integer> {
    @Override // com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation
    /* bridge */ /* synthetic */ Object getValue(Keyframe keyframe, float f) {
        return getValue((Keyframe<Integer>) keyframe, f);
    }

    public IntegerKeyframeAnimation(List<Keyframe<Integer>> list) {
        super(list);
    }

    @Override // com.airbnb.lottie.animation.keyframe.BaseKeyframeAnimation
    Integer getValue(Keyframe<Integer> keyframe, float f) {
        return Integer.valueOf(getIntValue(keyframe, f));
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    int getIntValue(Keyframe<Integer> keyframe, float f) {
        Integer num;
        if (keyframe.startValue == null || keyframe.endValue == null) {
            throw new IllegalStateException("Missing values for keyframe.");
        }
        if (this.valueCallback != null && (num = (Integer) this.valueCallback.getValueInternal(keyframe.startFrame, keyframe.endFrame.floatValue(), keyframe.startValue, keyframe.endValue, f, getLinearCurrentKeyframeProgress(), getProgress())) != null) {
            return num.intValue();
        }
        return MiscUtils.lerp(keyframe.getStartValueInt(), keyframe.getEndValueInt(), f);
    }

    public int getIntValue() {
        return getIntValue(getCurrentKeyframe(), getInterpolatedCurrentKeyframeProgress());
    }
}
