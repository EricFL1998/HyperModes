package kotlin.reflect.jvm.internal.impl.load.kotlin;

import kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType;

/* JADX INFO: compiled from: typeSignatureMapping.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface JvmTypeFactory<T> {
    T boxType(T t);

    T createFromString(String str);

    T createObjectType(String str);

    T createPrimitiveType(PrimitiveType primitiveType);

    T getJavaLangClassType();

    String toString(T t);
}
