package kotlin.reflect.jvm.internal.impl.utils;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: exceptionUtils.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ExceptionUtilsKt {
    public static final RuntimeException rethrow(Throwable e) throws Throwable {
        Intrinsics.checkNotNullParameter(e, "e");
        throw e;
    }

    public static final boolean isProcessCanceledException(Throwable th) {
        Intrinsics.checkNotNullParameter(th, "<this>");
        Class<?> superclass = th.getClass();
        while (!Intrinsics.areEqual(superclass.getCanonicalName(), "com.intellij.openapi.progress.ProcessCanceledException")) {
            superclass = superclass.getSuperclass();
            if (superclass == null) {
                return false;
            }
        }
        return true;
    }
}
