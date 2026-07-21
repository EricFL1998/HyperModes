package kotlin.reflect.jvm.internal.impl.descriptors.runtime.components;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import kotlin.collections.ArraysKt;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.internal.ArrayIteratorKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType;
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames;
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectClassUtilKt;
import kotlin.reflect.jvm.internal.impl.load.kotlin.KotlinJvmBinaryClass;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.name.SpecialNames;
import kotlin.reflect.jvm.internal.impl.resolve.constants.ClassLiteralValue;
import kotlin.reflect.jvm.internal.impl.resolve.jvm.JvmPrimitiveType;

/* JADX INFO: compiled from: ReflectKotlinClass.kt */
/* JADX INFO: loaded from: classes2.dex */
final class ReflectClassStructure {
    public static final ReflectClassStructure INSTANCE = new ReflectClassStructure();

    private ReflectClassStructure() {
    }

    public final void loadClassAnnotations(Class<?> klass, KotlinJvmBinaryClass.AnnotationVisitor visitor) throws InvocationTargetException {
        Intrinsics.checkNotNullParameter(klass, "klass");
        Intrinsics.checkNotNullParameter(visitor, "visitor");
        Iterator it = ArrayIteratorKt.iterator(klass.getDeclaredAnnotations());
        while (it.hasNext()) {
            Annotation annotation = (Annotation) it.next();
            Intrinsics.checkNotNull(annotation);
            processAnnotation(visitor, annotation);
        }
        visitor.visitEnd();
    }

    public final void visitMembers(Class<?> klass, KotlinJvmBinaryClass.MemberVisitor memberVisitor) throws InvocationTargetException {
        Intrinsics.checkNotNullParameter(klass, "klass");
        Intrinsics.checkNotNullParameter(memberVisitor, "memberVisitor");
        loadMethodAnnotations(klass, memberVisitor);
        loadConstructorAnnotations(klass, memberVisitor);
        loadFieldAnnotations(klass, memberVisitor);
    }

    private final void loadMethodAnnotations(Class<?> cls, KotlinJvmBinaryClass.MemberVisitor memberVisitor) throws InvocationTargetException {
        Iterator it = ArrayIteratorKt.iterator(cls.getDeclaredMethods());
        while (it.hasNext()) {
            Method method = (Method) it.next();
            Name nameIdentifier = Name.identifier(method.getName());
            Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
            SignatureSerializer signatureSerializer = SignatureSerializer.INSTANCE;
            Intrinsics.checkNotNull(method);
            KotlinJvmBinaryClass.MethodAnnotationVisitor methodAnnotationVisitorVisitMethod = memberVisitor.visitMethod(nameIdentifier, signatureSerializer.methodDesc(method));
            if (methodAnnotationVisitorVisitMethod != null) {
                Iterator it2 = ArrayIteratorKt.iterator(method.getDeclaredAnnotations());
                while (it2.hasNext()) {
                    Annotation annotation = (Annotation) it2.next();
                    Intrinsics.checkNotNull(annotation);
                    processAnnotation(methodAnnotationVisitorVisitMethod, annotation);
                }
                Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                Intrinsics.checkNotNullExpressionValue(parameterAnnotations, "getParameterAnnotations(...)");
                Annotation[][] annotationArr = parameterAnnotations;
                int length = annotationArr.length;
                for (int i = 0; i < length; i++) {
                    Iterator it3 = ArrayIteratorKt.iterator(annotationArr[i]);
                    while (it3.hasNext()) {
                        Annotation annotation2 = (Annotation) it3.next();
                        Class<?> javaClass = JvmClassMappingKt.getJavaClass(JvmClassMappingKt.getAnnotationClass(annotation2));
                        ClassId classId = ReflectClassUtilKt.getClassId(javaClass);
                        Intrinsics.checkNotNull(annotation2);
                        KotlinJvmBinaryClass.AnnotationArgumentVisitor annotationArgumentVisitorVisitParameterAnnotation = methodAnnotationVisitorVisitMethod.visitParameterAnnotation(i, classId, new ReflectAnnotationSource(annotation2));
                        if (annotationArgumentVisitorVisitParameterAnnotation != null) {
                            INSTANCE.processAnnotationArguments(annotationArgumentVisitorVisitParameterAnnotation, annotation2, javaClass);
                        }
                    }
                }
                methodAnnotationVisitorVisitMethod.visitEnd();
            }
        }
    }

    private final void loadConstructorAnnotations(Class<?> cls, KotlinJvmBinaryClass.MemberVisitor memberVisitor) throws InvocationTargetException {
        Iterator it = ArrayIteratorKt.iterator(cls.getDeclaredConstructors());
        while (it.hasNext()) {
            Constructor<?> constructor = (Constructor) it.next();
            Name name = SpecialNames.INIT;
            SignatureSerializer signatureSerializer = SignatureSerializer.INSTANCE;
            Intrinsics.checkNotNull(constructor);
            KotlinJvmBinaryClass.MethodAnnotationVisitor methodAnnotationVisitorVisitMethod = memberVisitor.visitMethod(name, signatureSerializer.constructorDesc(constructor));
            if (methodAnnotationVisitorVisitMethod != null) {
                Iterator it2 = ArrayIteratorKt.iterator(constructor.getDeclaredAnnotations());
                while (it2.hasNext()) {
                    Annotation annotation = (Annotation) it2.next();
                    Intrinsics.checkNotNull(annotation);
                    processAnnotation(methodAnnotationVisitorVisitMethod, annotation);
                }
                Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
                Intrinsics.checkNotNull(parameterAnnotations);
                Annotation[][] annotationArr = parameterAnnotations;
                if (!(annotationArr.length == 0)) {
                    int length = constructor.getParameterTypes().length - annotationArr.length;
                    int length2 = annotationArr.length;
                    for (int i = 0; i < length2; i++) {
                        Iterator it3 = ArrayIteratorKt.iterator(parameterAnnotations[i]);
                        while (it3.hasNext()) {
                            Annotation annotation2 = (Annotation) it3.next();
                            Class<?> javaClass = JvmClassMappingKt.getJavaClass(JvmClassMappingKt.getAnnotationClass(annotation2));
                            ClassId classId = ReflectClassUtilKt.getClassId(javaClass);
                            Intrinsics.checkNotNull(annotation2);
                            KotlinJvmBinaryClass.AnnotationArgumentVisitor annotationArgumentVisitorVisitParameterAnnotation = methodAnnotationVisitorVisitMethod.visitParameterAnnotation(i + length, classId, new ReflectAnnotationSource(annotation2));
                            if (annotationArgumentVisitorVisitParameterAnnotation != null) {
                                INSTANCE.processAnnotationArguments(annotationArgumentVisitorVisitParameterAnnotation, annotation2, javaClass);
                            }
                        }
                    }
                }
                methodAnnotationVisitorVisitMethod.visitEnd();
            }
        }
    }

    private final void loadFieldAnnotations(Class<?> cls, KotlinJvmBinaryClass.MemberVisitor memberVisitor) throws InvocationTargetException {
        Iterator it = ArrayIteratorKt.iterator(cls.getDeclaredFields());
        while (it.hasNext()) {
            Field field = (Field) it.next();
            Name nameIdentifier = Name.identifier(field.getName());
            Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
            SignatureSerializer signatureSerializer = SignatureSerializer.INSTANCE;
            Intrinsics.checkNotNull(field);
            KotlinJvmBinaryClass.AnnotationVisitor annotationVisitorVisitField = memberVisitor.visitField(nameIdentifier, signatureSerializer.fieldDesc(field), null);
            if (annotationVisitorVisitField != null) {
                Iterator it2 = ArrayIteratorKt.iterator(field.getDeclaredAnnotations());
                while (it2.hasNext()) {
                    Annotation annotation = (Annotation) it2.next();
                    Intrinsics.checkNotNull(annotation);
                    processAnnotation(annotationVisitorVisitField, annotation);
                }
                annotationVisitorVisitField.visitEnd();
            }
        }
    }

    private final void processAnnotation(KotlinJvmBinaryClass.AnnotationVisitor annotationVisitor, Annotation annotation) throws InvocationTargetException {
        Class<?> javaClass = JvmClassMappingKt.getJavaClass(JvmClassMappingKt.getAnnotationClass(annotation));
        KotlinJvmBinaryClass.AnnotationArgumentVisitor annotationArgumentVisitorVisitAnnotation = annotationVisitor.visitAnnotation(ReflectClassUtilKt.getClassId(javaClass), new ReflectAnnotationSource(annotation));
        if (annotationArgumentVisitorVisitAnnotation != null) {
            INSTANCE.processAnnotationArguments(annotationArgumentVisitorVisitAnnotation, annotation, javaClass);
        }
    }

    private final void processAnnotationArguments(KotlinJvmBinaryClass.AnnotationArgumentVisitor annotationArgumentVisitor, Annotation annotation, Class<?> cls) throws InvocationTargetException {
        Iterator it = ArrayIteratorKt.iterator(cls.getDeclaredMethods());
        while (it.hasNext()) {
            Method method = (Method) it.next();
            try {
                Object objInvoke = method.invoke(annotation, new Object[0]);
                Intrinsics.checkNotNull(objInvoke);
                Name nameIdentifier = Name.identifier(method.getName());
                Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
                processAnnotationArgumentValue(annotationArgumentVisitor, nameIdentifier, objInvoke);
            } catch (IllegalAccessException unused) {
            }
        }
        annotationArgumentVisitor.visitEnd();
    }

    private final ClassLiteralValue classLiteralValue(Class<?> cls) {
        int i = 0;
        while (cls.isArray()) {
            i++;
            cls = cls.getComponentType();
        }
        if (cls.isPrimitive()) {
            if (Intrinsics.areEqual(cls, Void.TYPE)) {
                ClassId.Companion companion = ClassId.Companion;
                FqName safe = StandardNames.FqNames.unit.toSafe();
                Intrinsics.checkNotNullExpressionValue(safe, "toSafe(...)");
                return new ClassLiteralValue(companion.topLevel(safe), i);
            }
            PrimitiveType primitiveType = JvmPrimitiveType.get(cls.getName()).getPrimitiveType();
            Intrinsics.checkNotNullExpressionValue(primitiveType, "getPrimitiveType(...)");
            if (i > 0) {
                return new ClassLiteralValue(ClassId.Companion.topLevel(primitiveType.getArrayTypeFqName()), i - 1);
            }
            return new ClassLiteralValue(ClassId.Companion.topLevel(primitiveType.getTypeFqName()), i);
        }
        ClassId classId = ReflectClassUtilKt.getClassId(cls);
        ClassId classIdMapJavaToKotlin = JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(classId.asSingleFqName());
        if (classIdMapJavaToKotlin != null) {
            classId = classIdMapJavaToKotlin;
        }
        return new ClassLiteralValue(classId, i);
    }

    private final void processAnnotationArgumentValue(KotlinJvmBinaryClass.AnnotationArgumentVisitor annotationArgumentVisitor, Name name, Object obj) throws InvocationTargetException {
        Class<?> enclosingClass = obj.getClass();
        if (!Intrinsics.areEqual(enclosingClass, Class.class)) {
            if (ReflectKotlinClassKt.TYPES_ELIGIBLE_FOR_SIMPLE_VISIT.contains(enclosingClass)) {
                annotationArgumentVisitor.visit(name, obj);
                return;
            }
            if (ReflectClassUtilKt.isEnumClassOrSpecializedEnumEntryClass(enclosingClass)) {
                if (!enclosingClass.isEnum()) {
                    enclosingClass = enclosingClass.getEnclosingClass();
                }
                Intrinsics.checkNotNull(enclosingClass);
                ClassId classId = ReflectClassUtilKt.getClassId(enclosingClass);
                Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type kotlin.Enum<*>");
                Name nameIdentifier = Name.identifier(((Enum) obj).name());
                Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
                annotationArgumentVisitor.visitEnum(name, classId, nameIdentifier);
                return;
            }
            if (Annotation.class.isAssignableFrom(enclosingClass)) {
                Class<?>[] interfaces = enclosingClass.getInterfaces();
                Intrinsics.checkNotNullExpressionValue(interfaces, "getInterfaces(...)");
                Class<?> cls = (Class) ArraysKt.single(interfaces);
                Intrinsics.checkNotNull(cls);
                KotlinJvmBinaryClass.AnnotationArgumentVisitor annotationArgumentVisitorVisitAnnotation = annotationArgumentVisitor.visitAnnotation(name, ReflectClassUtilKt.getClassId(cls));
                if (annotationArgumentVisitorVisitAnnotation == null) {
                    return;
                }
                Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type kotlin.Annotation");
                processAnnotationArguments(annotationArgumentVisitorVisitAnnotation, (Annotation) obj, cls);
                return;
            }
            if (enclosingClass.isArray()) {
                KotlinJvmBinaryClass.AnnotationArrayArgumentVisitor annotationArrayArgumentVisitorVisitArray = annotationArgumentVisitor.visitArray(name);
                if (annotationArrayArgumentVisitorVisitArray == null) {
                    return;
                }
                Class<?> componentType = enclosingClass.getComponentType();
                int i = 0;
                if (componentType.isEnum()) {
                    Intrinsics.checkNotNull(componentType);
                    ClassId classId2 = ReflectClassUtilKt.getClassId(componentType);
                    Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type kotlin.Array<*>");
                    Object[] objArr = (Object[]) obj;
                    int length = objArr.length;
                    while (i < length) {
                        Object obj2 = objArr[i];
                        Intrinsics.checkNotNull(obj2, "null cannot be cast to non-null type kotlin.Enum<*>");
                        Name nameIdentifier2 = Name.identifier(((Enum) obj2).name());
                        Intrinsics.checkNotNullExpressionValue(nameIdentifier2, "identifier(...)");
                        annotationArrayArgumentVisitorVisitArray.visitEnum(classId2, nameIdentifier2);
                        i++;
                    }
                } else if (Intrinsics.areEqual(componentType, Class.class)) {
                    Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type kotlin.Array<*>");
                    Object[] objArr2 = (Object[]) obj;
                    int length2 = objArr2.length;
                    while (i < length2) {
                        Object obj3 = objArr2[i];
                        Intrinsics.checkNotNull(obj3, "null cannot be cast to non-null type java.lang.Class<*>");
                        annotationArrayArgumentVisitorVisitArray.visitClassLiteral(classLiteralValue((Class) obj3));
                        i++;
                    }
                } else if (Annotation.class.isAssignableFrom(componentType)) {
                    Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type kotlin.Array<*>");
                    Object[] objArr3 = (Object[]) obj;
                    int length3 = objArr3.length;
                    while (i < length3) {
                        Object obj4 = objArr3[i];
                        Intrinsics.checkNotNull(componentType);
                        KotlinJvmBinaryClass.AnnotationArgumentVisitor annotationArgumentVisitorVisitAnnotation2 = annotationArrayArgumentVisitorVisitArray.visitAnnotation(ReflectClassUtilKt.getClassId(componentType));
                        if (annotationArgumentVisitorVisitAnnotation2 != null) {
                            Intrinsics.checkNotNull(obj4, "null cannot be cast to non-null type kotlin.Annotation");
                            processAnnotationArguments(annotationArgumentVisitorVisitAnnotation2, (Annotation) obj4, componentType);
                        }
                        i++;
                    }
                } else {
                    Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type kotlin.Array<*>");
                    Object[] objArr4 = (Object[]) obj;
                    int length4 = objArr4.length;
                    while (i < length4) {
                        annotationArrayArgumentVisitorVisitArray.visit(objArr4[i]);
                        i++;
                    }
                }
                annotationArrayArgumentVisitorVisitArray.visitEnd();
                return;
            }
            throw new UnsupportedOperationException("Unsupported annotation argument value (" + enclosingClass + "): " + obj);
        }
        Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type java.lang.Class<*>");
        annotationArgumentVisitor.visitClassLiteral(name, classLiteralValue((Class) obj));
    }
}
