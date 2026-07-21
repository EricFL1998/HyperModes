package kotlin.reflect.jvm.internal.impl.metadata.deserialization;

/* JADX INFO: compiled from: NameResolver.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface NameResolver {
    String getQualifiedClassName(int i);

    String getString(int i);

    boolean isLocalClassName(int i);
}
