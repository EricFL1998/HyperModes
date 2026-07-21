package kotlin.reflect.jvm.internal.impl.util;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: modifierChecks.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class CheckResult {
    private final boolean isSuccess;

    public /* synthetic */ CheckResult(boolean z, DefaultConstructorMarker defaultConstructorMarker) {
        this(z);
    }

    /* JADX INFO: compiled from: modifierChecks.kt */
    public static final class IllegalSignature extends CheckResult {
        private final String error;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public IllegalSignature(String error) {
            super(false, null);
            Intrinsics.checkNotNullParameter(error, "error");
            this.error = error;
        }
    }

    private CheckResult(boolean z) {
        this.isSuccess = z;
    }

    public final boolean isSuccess() {
        return this.isSuccess;
    }

    /* JADX INFO: compiled from: modifierChecks.kt */
    public static final class IllegalFunctionName extends CheckResult {
        public static final IllegalFunctionName INSTANCE = new IllegalFunctionName();

        private IllegalFunctionName() {
            super(false, null);
        }
    }

    /* JADX INFO: compiled from: modifierChecks.kt */
    public static final class SuccessCheck extends CheckResult {
        public static final SuccessCheck INSTANCE = new SuccessCheck();

        private SuccessCheck() {
            super(true, null);
        }
    }
}
