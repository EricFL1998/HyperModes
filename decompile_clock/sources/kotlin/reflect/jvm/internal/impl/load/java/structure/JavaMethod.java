package kotlin.reflect.jvm.internal.impl.load.java.structure;

import java.util.List;

/* JADX INFO: compiled from: javaElements.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface JavaMethod extends JavaMember, JavaTypeParameterListOwner {
    JavaAnnotationArgument getAnnotationParameterDefaultValue();

    boolean getHasAnnotationParameterDefaultValue();

    JavaType getReturnType();

    List<JavaValueParameter> getValueParameters();
}
