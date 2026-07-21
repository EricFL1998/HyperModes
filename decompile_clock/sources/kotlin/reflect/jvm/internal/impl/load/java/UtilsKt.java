package kotlin.reflect.jvm.internal.impl.load.java;

import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibility;
import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.Visibility;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.AnnotationDescriptor;
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.JavaMethodDescriptor;
import kotlin.reflect.jvm.internal.impl.load.java.lazy.LazyJavaAnnotations;
import kotlin.reflect.jvm.internal.impl.load.java.lazy.LazyJavaResolverContext;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaWildcardType;
import kotlin.reflect.jvm.internal.impl.name.FqName;

/* JADX INFO: compiled from: utils.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class UtilsKt {
    public static final DescriptorVisibility toDescriptorVisibility(Visibility visibility) {
        Intrinsics.checkNotNullParameter(visibility, "<this>");
        DescriptorVisibility descriptorVisibility = JavaDescriptorVisibilities.toDescriptorVisibility(visibility);
        Intrinsics.checkNotNullExpressionValue(descriptorVisibility, "toDescriptorVisibility(...)");
        return descriptorVisibility;
    }

    public static final boolean isJspecifyEnabledInStrictMode(JavaTypeEnhancementState javaTypeEnhancementState) {
        Intrinsics.checkNotNullParameter(javaTypeEnhancementState, "javaTypeEnhancementState");
        return javaTypeEnhancementState.getGetReportLevelForAnnotation().invoke(JavaNullabilityAnnotationSettingsKt.getJSPECIFY_ANNOTATIONS_PACKAGE()) == ReportLevel.STRICT;
    }

    public static final boolean hasErasedValueParameters(CallableMemberDescriptor memberDescriptor) {
        Intrinsics.checkNotNullParameter(memberDescriptor, "memberDescriptor");
        return (memberDescriptor instanceof FunctionDescriptor) && Intrinsics.areEqual(memberDescriptor.getUserData(JavaMethodDescriptor.HAS_ERASED_VALUE_PARAMETERS), (Object) true);
    }

    public static final AnnotationDescriptor extractNullabilityAnnotationOnBoundedWildcard(LazyJavaResolverContext c, JavaWildcardType wildcardType) {
        AnnotationDescriptor next;
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(wildcardType, "wildcardType");
        if (wildcardType.getBound() == null) {
            throw new IllegalArgumentException("Nullability annotations on unbounded wildcards aren't supported".toString());
        }
        Iterator<AnnotationDescriptor> it = new LazyJavaAnnotations(c, wildcardType, false, 4, null).iterator();
        while (it.hasNext()) {
            next = it.next();
            AnnotationDescriptor annotationDescriptor = next;
            for (FqName fqName : JavaNullabilityAnnotationSettingsKt.getRXJAVA3_ANNOTATIONS()) {
                if (Intrinsics.areEqual(annotationDescriptor.getFqName(), fqName)) {
                    return next;
                }
            }
        }
        next = null;
        return next;
    }
}
