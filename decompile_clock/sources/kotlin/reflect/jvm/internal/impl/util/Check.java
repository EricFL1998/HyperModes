package kotlin.reflect.jvm.internal.impl.util;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor;

/* JADX INFO: compiled from: modifierChecks.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface Check {
    boolean check(FunctionDescriptor functionDescriptor);

    String getDescription();

    String invoke(FunctionDescriptor functionDescriptor);

    /* JADX INFO: compiled from: modifierChecks.kt */
    public static final class DefaultImpls {
        public static String invoke(Check check, FunctionDescriptor functionDescriptor) {
            Intrinsics.checkNotNullParameter(functionDescriptor, "functionDescriptor");
            if (check.check(functionDescriptor)) {
                return null;
            }
            return check.getDescription();
        }
    }
}
