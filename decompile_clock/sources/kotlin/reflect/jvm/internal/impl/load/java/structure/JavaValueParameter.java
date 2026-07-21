package kotlin.reflect.jvm.internal.impl.load.java.structure;

import kotlin.reflect.jvm.internal.impl.name.Name;

/* JADX INFO: compiled from: javaElements.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface JavaValueParameter extends JavaAnnotationOwner {
    Name getName();

    JavaType getType();

    boolean isVararg();
}
