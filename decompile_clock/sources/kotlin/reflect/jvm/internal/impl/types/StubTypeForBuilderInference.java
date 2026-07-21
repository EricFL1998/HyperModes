package kotlin.reflect.jvm.internal.impl.types;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope;
import kotlin.reflect.jvm.internal.impl.types.checker.NewTypeVariableConstructor;
import kotlin.reflect.jvm.internal.impl.types.model.StubTypeMarker;

/* JADX INFO: compiled from: StubTypes.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class StubTypeForBuilderInference extends AbstractStubType implements StubTypeMarker {
    private final TypeConstructor constructor;
    private final MemberScope memberScope;

    @Override // kotlin.reflect.jvm.internal.impl.types.KotlinType
    public TypeConstructor getConstructor() {
        return this.constructor;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public StubTypeForBuilderInference(NewTypeVariableConstructor originalTypeVariable, boolean z, TypeConstructor constructor) {
        super(originalTypeVariable, z);
        Intrinsics.checkNotNullParameter(originalTypeVariable, "originalTypeVariable");
        Intrinsics.checkNotNullParameter(constructor, "constructor");
        this.constructor = constructor;
        this.memberScope = originalTypeVariable.getBuiltIns().getAnyType().getMemberScope();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.AbstractStubType
    public AbstractStubType materialize(boolean z) {
        return new StubTypeForBuilderInference(getOriginalTypeVariable(), z, getConstructor());
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.AbstractStubType, kotlin.reflect.jvm.internal.impl.types.KotlinType
    public MemberScope getMemberScope() {
        return this.memberScope;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.SimpleType
    public String toString() {
        return "Stub (BI): " + getOriginalTypeVariable() + (isMarkedNullable() ? "?" : "");
    }
}
