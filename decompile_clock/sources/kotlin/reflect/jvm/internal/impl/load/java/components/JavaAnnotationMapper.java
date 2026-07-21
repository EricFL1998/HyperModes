package kotlin.reflect.jvm.internal.impl.load.java.components;

import com.xiaomi.onetrack.api.g;
import java.util.Map;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.AnnotationDescriptor;
import kotlin.reflect.jvm.internal.impl.load.java.JvmAnnotationNames;
import kotlin.reflect.jvm.internal.impl.load.java.lazy.LazyJavaResolverContext;
import kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaAnnotationDescriptor;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotation;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotationOwner;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.Name;

/* JADX INFO: compiled from: JavaAnnotationMapper.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JavaAnnotationMapper {
    private static final Name DEPRECATED_ANNOTATION_MESSAGE;
    public static final JavaAnnotationMapper INSTANCE = new JavaAnnotationMapper();
    private static final Name RETENTION_ANNOTATION_VALUE;
    private static final Name TARGET_ANNOTATION_ALLOWED_TARGETS;
    private static final Map<FqName, FqName> kotlinToJavaNameMap;

    private JavaAnnotationMapper() {
    }

    static {
        Name nameIdentifier = Name.identifier("message");
        Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
        DEPRECATED_ANNOTATION_MESSAGE = nameIdentifier;
        Name nameIdentifier2 = Name.identifier("allowedTargets");
        Intrinsics.checkNotNullExpressionValue(nameIdentifier2, "identifier(...)");
        TARGET_ANNOTATION_ALLOWED_TARGETS = nameIdentifier2;
        Name nameIdentifier3 = Name.identifier(g.p);
        Intrinsics.checkNotNullExpressionValue(nameIdentifier3, "identifier(...)");
        RETENTION_ANNOTATION_VALUE = nameIdentifier3;
        kotlinToJavaNameMap = MapsKt.mapOf(TuplesKt.to(StandardNames.FqNames.target, JvmAnnotationNames.TARGET_ANNOTATION), TuplesKt.to(StandardNames.FqNames.retention, JvmAnnotationNames.RETENTION_ANNOTATION), TuplesKt.to(StandardNames.FqNames.mustBeDocumented, JvmAnnotationNames.DOCUMENTED_ANNOTATION));
    }

    public final Name getDEPRECATED_ANNOTATION_MESSAGE$descriptors_jvm() {
        return DEPRECATED_ANNOTATION_MESSAGE;
    }

    public final Name getTARGET_ANNOTATION_ALLOWED_TARGETS$descriptors_jvm() {
        return TARGET_ANNOTATION_ALLOWED_TARGETS;
    }

    public final Name getRETENTION_ANNOTATION_VALUE$descriptors_jvm() {
        return RETENTION_ANNOTATION_VALUE;
    }

    public static /* synthetic */ AnnotationDescriptor mapOrResolveJavaAnnotation$default(JavaAnnotationMapper javaAnnotationMapper, JavaAnnotation javaAnnotation, LazyJavaResolverContext lazyJavaResolverContext, boolean z, int i, Object obj) {
        if ((i & 4) != 0) {
            z = false;
        }
        return javaAnnotationMapper.mapOrResolveJavaAnnotation(javaAnnotation, lazyJavaResolverContext, z);
    }

    public final AnnotationDescriptor mapOrResolveJavaAnnotation(JavaAnnotation annotation, LazyJavaResolverContext c, boolean z) {
        Intrinsics.checkNotNullParameter(annotation, "annotation");
        Intrinsics.checkNotNullParameter(c, "c");
        ClassId classId = annotation.getClassId();
        ClassId.Companion companion = ClassId.Companion;
        FqName TARGET_ANNOTATION = JvmAnnotationNames.TARGET_ANNOTATION;
        Intrinsics.checkNotNullExpressionValue(TARGET_ANNOTATION, "TARGET_ANNOTATION");
        if (Intrinsics.areEqual(classId, companion.topLevel(TARGET_ANNOTATION))) {
            return new JavaTargetAnnotationDescriptor(annotation, c);
        }
        ClassId.Companion companion2 = ClassId.Companion;
        FqName RETENTION_ANNOTATION = JvmAnnotationNames.RETENTION_ANNOTATION;
        Intrinsics.checkNotNullExpressionValue(RETENTION_ANNOTATION, "RETENTION_ANNOTATION");
        if (Intrinsics.areEqual(classId, companion2.topLevel(RETENTION_ANNOTATION))) {
            return new JavaRetentionAnnotationDescriptor(annotation, c);
        }
        ClassId.Companion companion3 = ClassId.Companion;
        FqName DOCUMENTED_ANNOTATION = JvmAnnotationNames.DOCUMENTED_ANNOTATION;
        Intrinsics.checkNotNullExpressionValue(DOCUMENTED_ANNOTATION, "DOCUMENTED_ANNOTATION");
        if (Intrinsics.areEqual(classId, companion3.topLevel(DOCUMENTED_ANNOTATION))) {
            return new JavaAnnotationDescriptor(c, annotation, StandardNames.FqNames.mustBeDocumented);
        }
        ClassId.Companion companion4 = ClassId.Companion;
        FqName DEPRECATED_ANNOTATION = JvmAnnotationNames.DEPRECATED_ANNOTATION;
        Intrinsics.checkNotNullExpressionValue(DEPRECATED_ANNOTATION, "DEPRECATED_ANNOTATION");
        if (Intrinsics.areEqual(classId, companion4.topLevel(DEPRECATED_ANNOTATION))) {
            return null;
        }
        return new LazyJavaAnnotationDescriptor(c, annotation, z);
    }

    public final AnnotationDescriptor findMappedJavaAnnotation(FqName kotlinName, JavaAnnotationOwner annotationOwner, LazyJavaResolverContext c) {
        JavaAnnotation javaAnnotationFindAnnotation;
        Intrinsics.checkNotNullParameter(kotlinName, "kotlinName");
        Intrinsics.checkNotNullParameter(annotationOwner, "annotationOwner");
        Intrinsics.checkNotNullParameter(c, "c");
        if (Intrinsics.areEqual(kotlinName, StandardNames.FqNames.deprecated)) {
            FqName DEPRECATED_ANNOTATION = JvmAnnotationNames.DEPRECATED_ANNOTATION;
            Intrinsics.checkNotNullExpressionValue(DEPRECATED_ANNOTATION, "DEPRECATED_ANNOTATION");
            JavaAnnotation javaAnnotationFindAnnotation2 = annotationOwner.findAnnotation(DEPRECATED_ANNOTATION);
            if (javaAnnotationFindAnnotation2 != null || annotationOwner.isDeprecatedInJavaDoc()) {
                return new JavaDeprecatedAnnotationDescriptor(javaAnnotationFindAnnotation2, c);
            }
        }
        FqName fqName = kotlinToJavaNameMap.get(kotlinName);
        if (fqName == null || (javaAnnotationFindAnnotation = annotationOwner.findAnnotation(fqName)) == null) {
            return null;
        }
        return mapOrResolveJavaAnnotation$default(INSTANCE, javaAnnotationFindAnnotation, c, false, 4, null);
    }
}
