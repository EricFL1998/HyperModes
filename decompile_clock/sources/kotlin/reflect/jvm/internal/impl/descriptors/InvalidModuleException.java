package kotlin.reflect.jvm.internal.impl.descriptors;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: InvalidModuleException.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class InvalidModuleException extends IllegalStateException {
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public InvalidModuleException(String message) {
        super(message);
        Intrinsics.checkNotNullParameter(message, "message");
    }
}
