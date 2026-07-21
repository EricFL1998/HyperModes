package kotlin.reflect.jvm.internal.impl.descriptors;

import kotlin.reflect.jvm.internal.impl.types.TypeSubstitutor;

/* JADX INFO: compiled from: ClassConstructorDescriptor.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface ClassConstructorDescriptor extends ConstructorDescriptor {
    @Override // kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor, kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor, kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor, kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    ClassConstructorDescriptor getOriginal();

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ConstructorDescriptor, kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor, kotlin.reflect.jvm.internal.impl.descriptors.Substitutable
    ClassConstructorDescriptor substitute(TypeSubstitutor typeSubstitutor);
}
