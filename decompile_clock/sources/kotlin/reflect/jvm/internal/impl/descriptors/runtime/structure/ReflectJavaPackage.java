package kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure;

import java.util.Collection;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotation;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaClass;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaPackage;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.Name;

/* JADX INFO: compiled from: ReflectJavaPackage.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ReflectJavaPackage extends ReflectJavaElement implements JavaPackage {
    private final FqName fqName;

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotationOwner
    public JavaAnnotation findAnnotation(FqName fqName) {
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        return null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotationOwner
    public boolean isDeprecatedInJavaDoc() {
        return false;
    }

    public ReflectJavaPackage(FqName fqName) {
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        this.fqName = fqName;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaPackage
    public FqName getFqName() {
        return this.fqName;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaPackage
    public Collection<JavaClass> getClasses(Function1<? super Name, Boolean> nameFilter) {
        Intrinsics.checkNotNullParameter(nameFilter, "nameFilter");
        return CollectionsKt.emptyList();
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaPackage
    public Collection<JavaPackage> getSubPackages() {
        return CollectionsKt.emptyList();
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotationOwner
    public List<JavaAnnotation> getAnnotations() {
        return CollectionsKt.emptyList();
    }

    public boolean equals(Object obj) {
        return (obj instanceof ReflectJavaPackage) && Intrinsics.areEqual(getFqName(), ((ReflectJavaPackage) obj).getFqName());
    }

    public int hashCode() {
        return getFqName().hashCode();
    }

    public String toString() {
        return getClass().getName() + ": " + getFqName();
    }
}
