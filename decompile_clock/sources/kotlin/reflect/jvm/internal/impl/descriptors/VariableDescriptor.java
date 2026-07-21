package kotlin.reflect.jvm.internal.impl.descriptors;

import kotlin.reflect.jvm.internal.impl.resolve.constants.ConstantValue;
import kotlin.reflect.jvm.internal.impl.types.TypeSubstitutor;

/* JADX INFO: loaded from: classes2.dex */
public interface VariableDescriptor extends ValueDescriptor {
    /* JADX INFO: renamed from: getCompileTimeInitializer */
    ConstantValue<?> mo1487getCompileTimeInitializer();

    boolean isConst();

    boolean isLateInit();

    boolean isVar();

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.Substitutable
    VariableDescriptor substitute(TypeSubstitutor typeSubstitutor);
}
