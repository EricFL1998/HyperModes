package kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities;
import kotlin.reflect.jvm.internal.impl.descriptors.Visibility;
import kotlin.reflect.jvm.internal.impl.descriptors.java.JavaVisibilities;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotation;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaMember;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaValueParameter;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.name.SpecialNames;

/* JADX INFO: compiled from: ReflectJavaMember.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class ReflectJavaMember extends ReflectJavaElement implements ReflectJavaAnnotationOwner, ReflectJavaModifierListOwner, JavaMember {
    public abstract Member getMember();

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotationOwner
    public boolean isDeprecatedInJavaDoc() {
        return false;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotationOwner
    public /* bridge */ /* synthetic */ JavaAnnotation findAnnotation(FqName fqName) {
        return findAnnotation(fqName);
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotationOwner
    public /* bridge */ /* synthetic */ Collection getAnnotations() {
        return getAnnotations();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectJavaAnnotationOwner, kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotationOwner
    public List<ReflectJavaAnnotation> getAnnotations() {
        Annotation[] declaredAnnotations;
        List<ReflectJavaAnnotation> annotations;
        AnnotatedElement element = getElement();
        return (element == null || (declaredAnnotations = element.getDeclaredAnnotations()) == null || (annotations = ReflectJavaAnnotationOwnerKt.getAnnotations(declaredAnnotations)) == null) ? CollectionsKt.emptyList() : annotations;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaModifierListOwner
    public boolean isAbstract() {
        return Modifier.isAbstract(getModifiers());
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectJavaAnnotationOwner, kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotationOwner
    public ReflectJavaAnnotation findAnnotation(FqName fqName) {
        Annotation[] declaredAnnotations;
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        AnnotatedElement element = getElement();
        if (element == null || (declaredAnnotations = element.getDeclaredAnnotations()) == null) {
            return null;
        }
        return ReflectJavaAnnotationOwnerKt.findAnnotation(declaredAnnotations, fqName);
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaModifierListOwner
    public boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectJavaAnnotationOwner
    public AnnotatedElement getElement() {
        Member member = getMember();
        Intrinsics.checkNotNull(member, "null cannot be cast to non-null type java.lang.reflect.AnnotatedElement");
        return (AnnotatedElement) member;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectJavaModifierListOwner
    public int getModifiers() {
        return getMember().getModifiers();
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaModifierListOwner
    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaNamedElement
    public Name getName() {
        Name nameIdentifier;
        String name = getMember().getName();
        return (name == null || (nameIdentifier = Name.identifier(name)) == null) ? SpecialNames.NO_NAME_PROVIDED : nameIdentifier;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaModifierListOwner
    public Visibility getVisibility() {
        int modifiers = getModifiers();
        if (Modifier.isPublic(modifiers)) {
            return Visibilities.Public.INSTANCE;
        }
        if (Modifier.isPrivate(modifiers)) {
            return Visibilities.Private.INSTANCE;
        }
        if (Modifier.isProtected(modifiers)) {
            return Modifier.isStatic(modifiers) ? JavaVisibilities.ProtectedStaticVisibility.INSTANCE : JavaVisibilities.ProtectedAndPackage.INSTANCE;
        }
        return JavaVisibilities.PackageVisibility.INSTANCE;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaMember
    public ReflectJavaClass getContainingClass() {
        Class<?> declaringClass = getMember().getDeclaringClass();
        Intrinsics.checkNotNullExpressionValue(declaringClass, "getDeclaringClass(...)");
        return new ReflectJavaClass(declaringClass);
    }

    protected final List<JavaValueParameter> getValueParameters(Type[] parameterTypes, Annotation[][] parameterAnnotations, boolean z) throws IllegalAccessException, InvocationTargetException {
        String str;
        Intrinsics.checkNotNullParameter(parameterTypes, "parameterTypes");
        Intrinsics.checkNotNullParameter(parameterAnnotations, "parameterAnnotations");
        ArrayList arrayList = new ArrayList(parameterTypes.length);
        List<String> listLoadParameterNames = Java8ParameterNamesLoader.INSTANCE.loadParameterNames(getMember());
        int size = listLoadParameterNames != null ? listLoadParameterNames.size() - parameterTypes.length : 0;
        int length = parameterTypes.length;
        int i = 0;
        while (i < length) {
            ReflectJavaType reflectJavaTypeCreate = ReflectJavaType.Factory.create(parameterTypes[i]);
            if (listLoadParameterNames != null) {
                str = (String) CollectionsKt.getOrNull(listLoadParameterNames, i + size);
                if (str == null) {
                    throw new IllegalStateException(("No parameter with index " + i + '+' + size + " (name=" + getName() + " type=" + reflectJavaTypeCreate + ") in " + this).toString());
                }
            } else {
                str = null;
            }
            arrayList.add(new ReflectJavaValueParameter(reflectJavaTypeCreate, parameterAnnotations[i], str, z && i == ArraysKt.getLastIndex(parameterTypes)));
            i++;
        }
        return arrayList;
    }

    public boolean equals(Object obj) {
        return (obj instanceof ReflectJavaMember) && Intrinsics.areEqual(getMember(), ((ReflectJavaMember) obj).getMember());
    }

    public int hashCode() {
        return getMember().hashCode();
    }

    public String toString() {
        return getClass().getName() + ": " + getMember();
    }
}
