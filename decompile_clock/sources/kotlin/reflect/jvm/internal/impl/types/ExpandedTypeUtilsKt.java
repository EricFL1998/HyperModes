package kotlin.reflect.jvm.internal.impl.types;

import java.util.HashSet;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.types.model.KotlinTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.model.SimpleTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.model.TypeConstructorMarker;
import kotlin.reflect.jvm.internal.impl.types.model.TypeParameterMarker;

/* JADX INFO: compiled from: expandedTypeUtils.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ExpandedTypeUtilsKt {
    public static final KotlinTypeMarker computeExpandedTypeForInlineClass(TypeSystemCommonBackendContext typeSystemCommonBackendContext, KotlinTypeMarker inlineClassType) {
        Intrinsics.checkNotNullParameter(typeSystemCommonBackendContext, "<this>");
        Intrinsics.checkNotNullParameter(inlineClassType, "inlineClassType");
        return computeExpandedTypeInner(typeSystemCommonBackendContext, inlineClassType, new HashSet());
    }

    private static final KotlinTypeMarker computeExpandedTypeInner(TypeSystemCommonBackendContext typeSystemCommonBackendContext, KotlinTypeMarker kotlinTypeMarker, HashSet<TypeConstructorMarker> hashSet) {
        KotlinTypeMarker kotlinTypeMarkerComputeExpandedTypeInner;
        KotlinTypeMarker kotlinTypeMarkerMakeNullable;
        TypeConstructorMarker typeConstructorMarkerTypeConstructor = typeSystemCommonBackendContext.typeConstructor(kotlinTypeMarker);
        if (!hashSet.add(typeConstructorMarkerTypeConstructor)) {
            return null;
        }
        TypeParameterMarker typeParameterClassifier = typeSystemCommonBackendContext.getTypeParameterClassifier(typeConstructorMarkerTypeConstructor);
        if (typeParameterClassifier != null) {
            KotlinTypeMarker representativeUpperBound = typeSystemCommonBackendContext.getRepresentativeUpperBound(typeParameterClassifier);
            kotlinTypeMarkerComputeExpandedTypeInner = computeExpandedTypeInner(typeSystemCommonBackendContext, representativeUpperBound, hashSet);
            if (kotlinTypeMarkerComputeExpandedTypeInner == null) {
                return null;
            }
            boolean z = typeSystemCommonBackendContext.isInlineClass(typeSystemCommonBackendContext.typeConstructor(representativeUpperBound)) || ((representativeUpperBound instanceof SimpleTypeMarker) && typeSystemCommonBackendContext.isPrimitiveType((SimpleTypeMarker) representativeUpperBound));
            if ((kotlinTypeMarkerComputeExpandedTypeInner instanceof SimpleTypeMarker) && typeSystemCommonBackendContext.isPrimitiveType((SimpleTypeMarker) kotlinTypeMarkerComputeExpandedTypeInner) && typeSystemCommonBackendContext.isNullableType(kotlinTypeMarker) && z) {
                kotlinTypeMarkerMakeNullable = typeSystemCommonBackendContext.makeNullable(representativeUpperBound);
            } else if (!typeSystemCommonBackendContext.isNullableType(kotlinTypeMarkerComputeExpandedTypeInner) && typeSystemCommonBackendContext.isMarkedNullable(kotlinTypeMarker)) {
                kotlinTypeMarkerMakeNullable = typeSystemCommonBackendContext.makeNullable(kotlinTypeMarkerComputeExpandedTypeInner);
            }
            return kotlinTypeMarkerMakeNullable;
        }
        if (!typeSystemCommonBackendContext.isInlineClass(typeConstructorMarkerTypeConstructor)) {
            return kotlinTypeMarker;
        }
        KotlinTypeMarker unsubstitutedUnderlyingType = typeSystemCommonBackendContext.getUnsubstitutedUnderlyingType(kotlinTypeMarker);
        if (unsubstitutedUnderlyingType == null || (kotlinTypeMarkerComputeExpandedTypeInner = computeExpandedTypeInner(typeSystemCommonBackendContext, unsubstitutedUnderlyingType, hashSet)) == null) {
            return null;
        }
        if (typeSystemCommonBackendContext.isNullableType(kotlinTypeMarker)) {
            if (typeSystemCommonBackendContext.isNullableType(kotlinTypeMarkerComputeExpandedTypeInner)) {
                return kotlinTypeMarker;
            }
            return ((kotlinTypeMarkerComputeExpandedTypeInner instanceof SimpleTypeMarker) && typeSystemCommonBackendContext.isPrimitiveType((SimpleTypeMarker) kotlinTypeMarkerComputeExpandedTypeInner)) ? kotlinTypeMarker : typeSystemCommonBackendContext.makeNullable(kotlinTypeMarkerComputeExpandedTypeInner);
        }
        return kotlinTypeMarkerComputeExpandedTypeInner;
    }
}
