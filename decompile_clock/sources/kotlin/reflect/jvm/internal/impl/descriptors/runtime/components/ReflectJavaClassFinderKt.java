package kotlin.reflect.jvm.internal.impl.descriptors.runtime.components;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: ReflectJavaClassFinder.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ReflectJavaClassFinderKt {
    public static final Class<?> tryLoadClass(ClassLoader classLoader, String fqName) {
        Intrinsics.checkNotNullParameter(classLoader, "<this>");
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        try {
            return Class.forName(fqName, false, classLoader);
        } catch (ClassNotFoundException unused) {
            return null;
        }
    }
}
