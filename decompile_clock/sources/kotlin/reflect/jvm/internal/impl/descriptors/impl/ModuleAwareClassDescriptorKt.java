package kotlin.reflect.jvm.internal.impl.descriptors.impl;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope;
import kotlin.reflect.jvm.internal.impl.types.TypeSubstitution;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeRefiner;

/* JADX INFO: compiled from: ModuleAwareClassDescriptor.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ModuleAwareClassDescriptorKt {
    public static final MemberScope getRefinedUnsubstitutedMemberScopeIfPossible(ClassDescriptor classDescriptor, KotlinTypeRefiner kotlinTypeRefiner) {
        Intrinsics.checkNotNullParameter(classDescriptor, "<this>");
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "kotlinTypeRefiner");
        return ModuleAwareClassDescriptor.Companion.getRefinedUnsubstitutedMemberScopeIfPossible$descriptors(classDescriptor, kotlinTypeRefiner);
    }

    public static final MemberScope getRefinedMemberScopeIfPossible(ClassDescriptor classDescriptor, TypeSubstitution typeSubstitution, KotlinTypeRefiner kotlinTypeRefiner) {
        Intrinsics.checkNotNullParameter(classDescriptor, "<this>");
        Intrinsics.checkNotNullParameter(typeSubstitution, "typeSubstitution");
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "kotlinTypeRefiner");
        return ModuleAwareClassDescriptor.Companion.getRefinedMemberScopeIfPossible$descriptors(classDescriptor, typeSubstitution, kotlinTypeRefiner);
    }
}
