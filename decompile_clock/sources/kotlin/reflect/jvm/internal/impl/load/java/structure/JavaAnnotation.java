package kotlin.reflect.jvm.internal.impl.load.java.structure;

import java.util.Collection;
import kotlin.reflect.jvm.internal.impl.name.ClassId;

/* JADX INFO: compiled from: javaElements.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface JavaAnnotation extends JavaElement {
    Collection<JavaAnnotationArgument> getArguments();

    ClassId getClassId();

    boolean isFreshlySupportedTypeUseAnnotation();

    boolean isIdeExternalAnnotation();

    JavaClass resolve();
}
