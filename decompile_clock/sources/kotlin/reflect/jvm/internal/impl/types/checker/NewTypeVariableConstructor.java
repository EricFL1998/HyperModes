package kotlin.reflect.jvm.internal.impl.types.checker;

import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.types.TypeConstructor;

/* JADX INFO: compiled from: utils.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface NewTypeVariableConstructor extends TypeConstructor {
    TypeParameterDescriptor getOriginalTypeParameter();
}
