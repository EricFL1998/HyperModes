package kotlin.reflect.jvm.internal.impl.descriptors;

import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.TypeConstructor;

/* JADX INFO: loaded from: classes2.dex */
public interface ClassifierDescriptor extends DeclarationDescriptorNonRoot {
    SimpleType getDefaultType();

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    ClassifierDescriptor getOriginal();

    TypeConstructor getTypeConstructor();
}
