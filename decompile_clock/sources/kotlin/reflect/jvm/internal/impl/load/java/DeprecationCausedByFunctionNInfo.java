package kotlin.reflect.jvm.internal.impl.load.java;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.resolve.deprecation.DeprecationLevelValue;
import kotlin.reflect.jvm.internal.impl.resolve.deprecation.DescriptorBasedDeprecationInfo;

/* JADX INFO: compiled from: utils.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class DeprecationCausedByFunctionNInfo extends DescriptorBasedDeprecationInfo {
    private final DeclarationDescriptor target;

    public DeprecationCausedByFunctionNInfo(DeclarationDescriptor target) {
        Intrinsics.checkNotNullParameter(target, "target");
        this.target = target;
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.deprecation.DeprecationInfo
    public DeprecationLevelValue getDeprecationLevel() {
        return DeprecationLevelValue.ERROR;
    }
}
