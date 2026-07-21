package kotlin.reflect.jvm.internal.impl.load.java.structure;

/* JADX INFO: compiled from: javaTypes.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface JavaWildcardType extends JavaType {
    JavaType getBound();

    boolean isExtends();
}
