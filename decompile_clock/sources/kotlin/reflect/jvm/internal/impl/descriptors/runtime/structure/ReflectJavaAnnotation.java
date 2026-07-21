package kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotation;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotationArgument;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.Name;

/* JADX INFO: compiled from: ReflectJavaAnnotation.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ReflectJavaAnnotation extends ReflectJavaElement implements JavaAnnotation {
    private final Annotation annotation;

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotation
    public boolean isFreshlySupportedTypeUseAnnotation() {
        return false;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotation
    public boolean isIdeExternalAnnotation() {
        return false;
    }

    public ReflectJavaAnnotation(Annotation annotation) {
        Intrinsics.checkNotNullParameter(annotation, "annotation");
        this.annotation = annotation;
    }

    public final Annotation getAnnotation() {
        return this.annotation;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotation
    public Collection<JavaAnnotationArgument> getArguments() throws IllegalAccessException, InvocationTargetException {
        Method[] declaredMethods = JvmClassMappingKt.getJavaClass(JvmClassMappingKt.getAnnotationClass(this.annotation)).getDeclaredMethods();
        Intrinsics.checkNotNullExpressionValue(declaredMethods, "getDeclaredMethods(...)");
        Method[] methodArr = declaredMethods;
        ArrayList arrayList = new ArrayList(methodArr.length);
        for (Method method : methodArr) {
            ReflectJavaAnnotationArgument.Factory factory = ReflectJavaAnnotationArgument.Factory;
            Object objInvoke = method.invoke(this.annotation, new Object[0]);
            Intrinsics.checkNotNullExpressionValue(objInvoke, "invoke(...)");
            arrayList.add(factory.create(objInvoke, Name.identifier(method.getName())));
        }
        return arrayList;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotation
    public ClassId getClassId() {
        return ReflectClassUtilKt.getClassId(JvmClassMappingKt.getJavaClass(JvmClassMappingKt.getAnnotationClass(this.annotation)));
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotation
    public ReflectJavaClass resolve() {
        return new ReflectJavaClass(JvmClassMappingKt.getJavaClass(JvmClassMappingKt.getAnnotationClass(this.annotation)));
    }

    public boolean equals(Object obj) {
        return (obj instanceof ReflectJavaAnnotation) && this.annotation == ((ReflectJavaAnnotation) obj).annotation;
    }

    public int hashCode() {
        return System.identityHashCode(this.annotation);
    }

    public String toString() {
        return getClass().getName() + ": " + this.annotation;
    }
}
