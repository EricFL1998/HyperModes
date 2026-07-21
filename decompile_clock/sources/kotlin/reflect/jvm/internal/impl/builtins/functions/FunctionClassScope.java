package kotlin.reflect.jvm.internal.impl.builtins.functions;

import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.GivenFunctionsMemberScope;
import kotlin.reflect.jvm.internal.impl.storage.StorageManager;

/* JADX INFO: compiled from: FunctionClassScope.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class FunctionClassScope extends GivenFunctionsMemberScope {
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public FunctionClassScope(StorageManager storageManager, FunctionClassDescriptor containingClass) {
        super(storageManager, containingClass);
        Intrinsics.checkNotNullParameter(storageManager, "storageManager");
        Intrinsics.checkNotNullParameter(containingClass, "containingClass");
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.GivenFunctionsMemberScope
    protected List<FunctionDescriptor> computeDeclaredFunctions() {
        ClassDescriptor containingClass = getContainingClass();
        Intrinsics.checkNotNull(containingClass, "null cannot be cast to non-null type org.jetbrains.kotlin.builtins.functions.FunctionClassDescriptor");
        FunctionTypeKind functionTypeKind = ((FunctionClassDescriptor) containingClass).getFunctionTypeKind();
        if (Intrinsics.areEqual(functionTypeKind, FunctionTypeKind.Function.INSTANCE)) {
            return CollectionsKt.listOf(FunctionInvokeDescriptor.Factory.create((FunctionClassDescriptor) getContainingClass(), false));
        }
        return Intrinsics.areEqual(functionTypeKind, FunctionTypeKind.SuspendFunction.INSTANCE) ? CollectionsKt.listOf(FunctionInvokeDescriptor.Factory.create((FunctionClassDescriptor) getContainingClass(), true)) : CollectionsKt.emptyList();
    }
}
