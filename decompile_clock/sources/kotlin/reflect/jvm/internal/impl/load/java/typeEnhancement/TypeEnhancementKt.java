package kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement;

import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMapper;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.CompositeAnnotations;
import kotlin.reflect.jvm.internal.impl.load.java.JvmAnnotationNames;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.checker.SimpleClassicTypeSystemContext;

/* JADX INFO: compiled from: typeEnhancement.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class TypeEnhancementKt {
    private static final EnhancedTypeAnnotations ENHANCED_MUTABILITY_ANNOTATIONS;
    private static final Annotations ENHANCED_NULLABILITY_ANNOTATIONS;

    /* JADX INFO: compiled from: typeEnhancement.kt */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[NullabilityQualifier.values().length];
            try {
                iArr[NullabilityQualifier.NULLABLE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[NullabilityQualifier.NOT_NULL.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public static final boolean hasEnhancedNullability(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        return TypeEnhancementUtilsKt.hasEnhancedNullability(SimpleClassicTypeSystemContext.INSTANCE, kotlinType);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Annotations compositeAnnotationsOrSingle(List<? extends Annotations> list) {
        int size = list.size();
        if (size == 0) {
            throw new IllegalStateException("At least one Annotations object expected".toString());
        }
        if (size == 1) {
            return (Annotations) CollectionsKt.single((List) list);
        }
        return new CompositeAnnotations((List<? extends Annotations>) CollectionsKt.toList(list));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final ClassifierDescriptor enhanceMutability(ClassifierDescriptor classifierDescriptor, JavaTypeQualifiers javaTypeQualifiers, TypeComponentPosition typeComponentPosition) {
        JavaToKotlinClassMapper javaToKotlinClassMapper = JavaToKotlinClassMapper.INSTANCE;
        if (!TypeComponentPositionKt.shouldEnhance(typeComponentPosition) || !(classifierDescriptor instanceof ClassDescriptor)) {
            return null;
        }
        if (javaTypeQualifiers.getMutability() == MutabilityQualifier.READ_ONLY && typeComponentPosition == TypeComponentPosition.FLEXIBLE_LOWER) {
            ClassDescriptor classDescriptor = (ClassDescriptor) classifierDescriptor;
            if (javaToKotlinClassMapper.isMutable(classDescriptor)) {
                return javaToKotlinClassMapper.convertMutableToReadOnly(classDescriptor);
            }
        }
        if (javaTypeQualifiers.getMutability() != MutabilityQualifier.MUTABLE || typeComponentPosition != TypeComponentPosition.FLEXIBLE_UPPER) {
            return null;
        }
        ClassDescriptor classDescriptor2 = (ClassDescriptor) classifierDescriptor;
        if (javaToKotlinClassMapper.isReadOnly(classDescriptor2)) {
            return javaToKotlinClassMapper.convertReadOnlyToMutable(classDescriptor2);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Boolean getEnhancedNullability(JavaTypeQualifiers javaTypeQualifiers, TypeComponentPosition typeComponentPosition) {
        if (!TypeComponentPositionKt.shouldEnhance(typeComponentPosition)) {
            return null;
        }
        NullabilityQualifier nullability = javaTypeQualifiers.getNullability();
        int i = nullability == null ? -1 : WhenMappings.$EnumSwitchMapping$0[nullability.ordinal()];
        if (i != 1) {
            return i != 2 ? null : false;
        }
        return true;
    }

    static {
        FqName ENHANCED_NULLABILITY_ANNOTATION = JvmAnnotationNames.ENHANCED_NULLABILITY_ANNOTATION;
        Intrinsics.checkNotNullExpressionValue(ENHANCED_NULLABILITY_ANNOTATION, "ENHANCED_NULLABILITY_ANNOTATION");
        ENHANCED_NULLABILITY_ANNOTATIONS = new EnhancedTypeAnnotations(ENHANCED_NULLABILITY_ANNOTATION);
        FqName ENHANCED_MUTABILITY_ANNOTATION = JvmAnnotationNames.ENHANCED_MUTABILITY_ANNOTATION;
        Intrinsics.checkNotNullExpressionValue(ENHANCED_MUTABILITY_ANNOTATION, "ENHANCED_MUTABILITY_ANNOTATION");
        ENHANCED_MUTABILITY_ANNOTATIONS = new EnhancedTypeAnnotations(ENHANCED_MUTABILITY_ANNOTATION);
    }

    public static final Annotations getENHANCED_NULLABILITY_ANNOTATIONS() {
        return ENHANCED_NULLABILITY_ANNOTATIONS;
    }
}
