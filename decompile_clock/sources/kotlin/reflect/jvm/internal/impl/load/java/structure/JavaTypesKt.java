package kotlin.reflect.jvm.internal.impl.load.java.structure;

/* JADX INFO: compiled from: javaTypes.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JavaTypesKt {
    public static final boolean isSuperWildcard(JavaType javaType) {
        JavaWildcardType javaWildcardType = javaType instanceof JavaWildcardType ? (JavaWildcardType) javaType : null;
        return (javaWildcardType == null || javaWildcardType.getBound() == null || javaWildcardType.isExtends()) ? false : true;
    }
}
