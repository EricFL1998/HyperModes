package kotlin.reflect.jvm.internal.impl.resolve;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor;

/* JADX INFO: compiled from: OverridingStrategy.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class NonReportingOverrideStrategy extends OverridingStrategy {
    protected abstract void conflict(CallableMemberDescriptor callableMemberDescriptor, CallableMemberDescriptor callableMemberDescriptor2);

    @Override // kotlin.reflect.jvm.internal.impl.resolve.OverridingStrategy
    public void overrideConflict(CallableMemberDescriptor fromSuper, CallableMemberDescriptor fromCurrent) {
        Intrinsics.checkNotNullParameter(fromSuper, "fromSuper");
        Intrinsics.checkNotNullParameter(fromCurrent, "fromCurrent");
        conflict(fromSuper, fromCurrent);
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.OverridingStrategy
    public void inheritanceConflict(CallableMemberDescriptor first, CallableMemberDescriptor second) {
        Intrinsics.checkNotNullParameter(first, "first");
        Intrinsics.checkNotNullParameter(second, "second");
        conflict(first, second);
    }
}
