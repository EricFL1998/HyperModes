package kotlin.reflect.jvm.internal.impl;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement;
import kotlin.reflect.jvm.internal.impl.load.java.JvmAbi;
import kotlin.reflect.jvm.internal.impl.load.java.JvmAnnotationNames;
import kotlin.reflect.jvm.internal.impl.load.kotlin.KotlinJvmBinaryClass;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;

/* JADX INFO: compiled from: SpecialJvmAnnotations.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class SpecialJvmAnnotations {
    public static final SpecialJvmAnnotations INSTANCE = new SpecialJvmAnnotations();
    private static final ClassId JAVA_LANG_ANNOTATION_REPEATABLE;
    private static final Set<ClassId> SPECIAL_ANNOTATIONS;

    private SpecialJvmAnnotations() {
    }

    public final Set<ClassId> getSPECIAL_ANNOTATIONS() {
        return SPECIAL_ANNOTATIONS;
    }

    static {
        List listListOf = CollectionsKt.listOf((Object[]) new FqName[]{JvmAnnotationNames.METADATA_FQ_NAME, JvmAnnotationNames.JETBRAINS_NOT_NULL_ANNOTATION, JvmAnnotationNames.JETBRAINS_NULLABLE_ANNOTATION, JvmAnnotationNames.TARGET_ANNOTATION, JvmAnnotationNames.RETENTION_ANNOTATION, JvmAnnotationNames.DOCUMENTED_ANNOTATION});
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        ClassId.Companion companion = ClassId.Companion;
        Iterator it = listListOf.iterator();
        while (it.hasNext()) {
            linkedHashSet.add(companion.topLevel((FqName) it.next()));
        }
        SPECIAL_ANNOTATIONS = linkedHashSet;
        ClassId.Companion companion2 = ClassId.Companion;
        FqName REPEATABLE_ANNOTATION = JvmAnnotationNames.REPEATABLE_ANNOTATION;
        Intrinsics.checkNotNullExpressionValue(REPEATABLE_ANNOTATION, "REPEATABLE_ANNOTATION");
        JAVA_LANG_ANNOTATION_REPEATABLE = companion2.topLevel(REPEATABLE_ANNOTATION);
    }

    public final ClassId getJAVA_LANG_ANNOTATION_REPEATABLE() {
        return JAVA_LANG_ANNOTATION_REPEATABLE;
    }

    public final boolean isAnnotatedWithContainerMetaAnnotation(KotlinJvmBinaryClass klass) {
        Intrinsics.checkNotNullParameter(klass, "klass");
        final Ref.BooleanRef booleanRef = new Ref.BooleanRef();
        klass.loadClassAnnotations(new KotlinJvmBinaryClass.AnnotationVisitor() { // from class: kotlin.reflect.jvm.internal.impl.SpecialJvmAnnotations.isAnnotatedWithContainerMetaAnnotation.1
            @Override // kotlin.reflect.jvm.internal.impl.load.kotlin.KotlinJvmBinaryClass.AnnotationVisitor
            public void visitEnd() {
            }

            @Override // kotlin.reflect.jvm.internal.impl.load.kotlin.KotlinJvmBinaryClass.AnnotationVisitor
            public KotlinJvmBinaryClass.AnnotationArgumentVisitor visitAnnotation(ClassId classId, SourceElement source) {
                Intrinsics.checkNotNullParameter(classId, "classId");
                Intrinsics.checkNotNullParameter(source, "source");
                if (!Intrinsics.areEqual(classId, JvmAbi.INSTANCE.getREPEATABLE_ANNOTATION_CONTAINER_META_ANNOTATION())) {
                    return null;
                }
                booleanRef.element = true;
                return null;
            }
        }, null);
        return booleanRef.element;
    }
}
