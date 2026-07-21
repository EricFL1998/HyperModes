package kotlin.reflect.jvm.internal.impl.util;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor;

/* JADX INFO: compiled from: modifierChecks.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class AbstractModifierChecks {
    public abstract List<Checks> getChecks$descriptors();

    public final CheckResult check(FunctionDescriptor functionDescriptor) {
        Intrinsics.checkNotNullParameter(functionDescriptor, "functionDescriptor");
        for (Checks checks : getChecks$descriptors()) {
            if (checks.isApplicable(functionDescriptor)) {
                return checks.checkAll(functionDescriptor);
            }
        }
        return CheckResult.IllegalFunctionName.INSTANCE;
    }
}
