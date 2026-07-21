package kotlin.reflect.jvm.internal.impl.descriptors;

import kotlin.reflect.jvm.internal.impl.types.SimpleType;

/* JADX INFO: compiled from: TypeAliasDescriptor.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface TypeAliasDescriptor extends ClassifierDescriptorWithTypeParameters {
    ClassDescriptor getClassDescriptor();

    SimpleType getExpandedType();

    SimpleType getUnderlyingType();
}
