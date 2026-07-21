package kotlin.reflect.jvm.internal.impl.types.typesApproximation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.renderer.ClassifierNamePolicy;
import kotlin.reflect.jvm.internal.impl.renderer.DescriptorRendererOptions;
import kotlin.reflect.jvm.internal.impl.resolve.calls.inference.CapturedTypeConstructor;
import kotlin.reflect.jvm.internal.impl.resolve.calls.inference.CapturedTypeConstructorKt;
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.DescriptorUtilsKt;
import kotlin.reflect.jvm.internal.impl.types.FlexibleTypesKt;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.KotlinTypeFactory;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.TypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.TypeConstructorSubstitution;
import kotlin.reflect.jvm.internal.impl.types.TypeProjection;
import kotlin.reflect.jvm.internal.impl.types.TypeProjectionImpl;
import kotlin.reflect.jvm.internal.impl.types.TypeSubstitutionKt;
import kotlin.reflect.jvm.internal.impl.types.TypeSubstitutor;
import kotlin.reflect.jvm.internal.impl.types.TypeUtils;
import kotlin.reflect.jvm.internal.impl.types.TypeWithEnhancementKt;
import kotlin.reflect.jvm.internal.impl.types.UnwrappedType;
import kotlin.reflect.jvm.internal.impl.types.Variance;
import kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt;

/* JADX INFO: compiled from: CapturedTypeApproximation.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class CapturedTypeApproximationKt {

    /* JADX INFO: compiled from: CapturedTypeApproximation.kt */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[Variance.values().length];
            try {
                iArr[Variance.INVARIANT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[Variance.IN_VARIANCE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[Variance.OUT_VARIANCE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    private static final TypeProjection toTypeProjection(TypeArgument typeArgument) {
        typeArgument.isConsistent();
        if (Intrinsics.areEqual(typeArgument.getInProjection(), typeArgument.getOutProjection()) || typeArgument.getTypeParameter().getVariance() == Variance.IN_VARIANCE) {
            return new TypeProjectionImpl(typeArgument.getInProjection());
        }
        if (!KotlinBuiltIns.isNothing(typeArgument.getInProjection()) || typeArgument.getTypeParameter().getVariance() == Variance.IN_VARIANCE) {
            return KotlinBuiltIns.isNullableAny(typeArgument.getOutProjection()) ? new TypeProjectionImpl(toTypeProjection$removeProjectionIfRedundant(typeArgument, Variance.IN_VARIANCE), typeArgument.getInProjection()) : new TypeProjectionImpl(toTypeProjection$removeProjectionIfRedundant(typeArgument, Variance.OUT_VARIANCE), typeArgument.getOutProjection());
        }
        return new TypeProjectionImpl(toTypeProjection$removeProjectionIfRedundant(typeArgument, Variance.OUT_VARIANCE), typeArgument.getOutProjection());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit toTypeProjection$lambda$1$lambda$0(DescriptorRendererOptions withOptions) {
        Intrinsics.checkNotNullParameter(withOptions, "$this$withOptions");
        withOptions.setClassifierNamePolicy(ClassifierNamePolicy.FULLY_QUALIFIED.INSTANCE);
        return Unit.INSTANCE;
    }

    private static final Variance toTypeProjection$removeProjectionIfRedundant(TypeArgument typeArgument, Variance variance) {
        return variance == typeArgument.getTypeParameter().getVariance() ? Variance.INVARIANT : variance;
    }

    private static final TypeArgument toTypeArgument(TypeProjection typeProjection, TypeParameterDescriptor typeParameterDescriptor) {
        int i = WhenMappings.$EnumSwitchMapping$0[TypeSubstitutor.combine(typeParameterDescriptor.getVariance(), typeProjection).ordinal()];
        if (i == 1) {
            KotlinType type = typeProjection.getType();
            Intrinsics.checkNotNullExpressionValue(type, "getType(...)");
            KotlinType type2 = typeProjection.getType();
            Intrinsics.checkNotNullExpressionValue(type2, "getType(...)");
            return new TypeArgument(typeParameterDescriptor, type, type2);
        }
        if (i == 2) {
            KotlinType type3 = typeProjection.getType();
            Intrinsics.checkNotNullExpressionValue(type3, "getType(...)");
            SimpleType nullableAnyType = DescriptorUtilsKt.getBuiltIns(typeParameterDescriptor).getNullableAnyType();
            Intrinsics.checkNotNullExpressionValue(nullableAnyType, "getNullableAnyType(...)");
            return new TypeArgument(typeParameterDescriptor, type3, nullableAnyType);
        }
        if (i != 3) {
            throw new NoWhenBranchMatchedException();
        }
        SimpleType nothingType = DescriptorUtilsKt.getBuiltIns(typeParameterDescriptor).getNothingType();
        Intrinsics.checkNotNullExpressionValue(nothingType, "getNothingType(...)");
        KotlinType type4 = typeProjection.getType();
        Intrinsics.checkNotNullExpressionValue(type4, "getType(...)");
        return new TypeArgument(typeParameterDescriptor, nothingType, type4);
    }

    public static final TypeProjection approximateCapturedTypesIfNecessary(TypeProjection typeProjection, boolean z) {
        if (typeProjection == null) {
            return null;
        }
        if (typeProjection.isStarProjection()) {
            return typeProjection;
        }
        KotlinType type = typeProjection.getType();
        Intrinsics.checkNotNullExpressionValue(type, "getType(...)");
        if (!TypeUtils.contains(type, new Function1() { // from class: kotlin.reflect.jvm.internal.impl.types.typesApproximation.CapturedTypeApproximationKt$$Lambda$1
            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return CapturedTypeApproximationKt.approximateCapturedTypesIfNecessary$lambda$2((UnwrappedType) obj);
            }
        })) {
            return typeProjection;
        }
        Variance projectionKind = typeProjection.getProjectionKind();
        Intrinsics.checkNotNullExpressionValue(projectionKind, "getProjectionKind(...)");
        if (projectionKind == Variance.OUT_VARIANCE) {
            return new TypeProjectionImpl(projectionKind, approximateCapturedTypes(type).getUpper());
        }
        if (z) {
            return new TypeProjectionImpl(projectionKind, approximateCapturedTypes(type).getLower());
        }
        return substituteCapturedTypesWithProjections(typeProjection);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Boolean approximateCapturedTypesIfNecessary$lambda$2(UnwrappedType unwrappedType) {
        Intrinsics.checkNotNull(unwrappedType);
        return Boolean.valueOf(CapturedTypeConstructorKt.isCaptured(unwrappedType));
    }

    private static final TypeProjection substituteCapturedTypesWithProjections(TypeProjection typeProjection) {
        TypeSubstitutor typeSubstitutorCreate = TypeSubstitutor.create(new TypeConstructorSubstitution() { // from class: kotlin.reflect.jvm.internal.impl.types.typesApproximation.CapturedTypeApproximationKt$substituteCapturedTypesWithProjections$typeSubstitutor$1
            @Override // kotlin.reflect.jvm.internal.impl.types.TypeConstructorSubstitution
            public TypeProjection get(TypeConstructor key) {
                Intrinsics.checkNotNullParameter(key, "key");
                CapturedTypeConstructor capturedTypeConstructor = key instanceof CapturedTypeConstructor ? (CapturedTypeConstructor) key : null;
                if (capturedTypeConstructor == null) {
                    return null;
                }
                if (capturedTypeConstructor.getProjection().isStarProjection()) {
                    return new TypeProjectionImpl(Variance.OUT_VARIANCE, capturedTypeConstructor.getProjection().getType());
                }
                return capturedTypeConstructor.getProjection();
            }
        });
        Intrinsics.checkNotNullExpressionValue(typeSubstitutorCreate, "create(...)");
        return typeSubstitutorCreate.substituteWithoutApproximation(typeProjection);
    }

    public static final ApproximationBounds<KotlinType> approximateCapturedTypes(KotlinType type) {
        SimpleType simpleTypeReplaceTypeArguments;
        Intrinsics.checkNotNullParameter(type, "type");
        if (FlexibleTypesKt.isFlexible(type)) {
            ApproximationBounds<KotlinType> approximationBoundsApproximateCapturedTypes = approximateCapturedTypes(FlexibleTypesKt.lowerIfFlexible(type));
            ApproximationBounds<KotlinType> approximationBoundsApproximateCapturedTypes2 = approximateCapturedTypes(FlexibleTypesKt.upperIfFlexible(type));
            return new ApproximationBounds<>(TypeWithEnhancementKt.inheritEnhancement(KotlinTypeFactory.flexibleType(FlexibleTypesKt.lowerIfFlexible(approximationBoundsApproximateCapturedTypes.getLower()), FlexibleTypesKt.upperIfFlexible(approximationBoundsApproximateCapturedTypes2.getLower())), type), TypeWithEnhancementKt.inheritEnhancement(KotlinTypeFactory.flexibleType(FlexibleTypesKt.lowerIfFlexible(approximationBoundsApproximateCapturedTypes.getUpper()), FlexibleTypesKt.upperIfFlexible(approximationBoundsApproximateCapturedTypes2.getUpper())), type));
        }
        TypeConstructor constructor = type.getConstructor();
        if (CapturedTypeConstructorKt.isCaptured(type)) {
            Intrinsics.checkNotNull(constructor, "null cannot be cast to non-null type org.jetbrains.kotlin.resolve.calls.inference.CapturedTypeConstructor");
            TypeProjection projection = ((CapturedTypeConstructor) constructor).getProjection();
            KotlinType type2 = projection.getType();
            Intrinsics.checkNotNullExpressionValue(type2, "getType(...)");
            KotlinType kotlinTypeApproximateCapturedTypes$makeNullableIfNeeded = approximateCapturedTypes$makeNullableIfNeeded(type2, type);
            int i = WhenMappings.$EnumSwitchMapping$0[projection.getProjectionKind().ordinal()];
            if (i == 2) {
                return new ApproximationBounds<>(kotlinTypeApproximateCapturedTypes$makeNullableIfNeeded, TypeUtilsKt.getBuiltIns(type).getNullableAnyType());
            }
            if (i == 3) {
                SimpleType nothingType = TypeUtilsKt.getBuiltIns(type).getNothingType();
                Intrinsics.checkNotNullExpressionValue(nothingType, "getNothingType(...)");
                return new ApproximationBounds<>(approximateCapturedTypes$makeNullableIfNeeded(nothingType, type), kotlinTypeApproximateCapturedTypes$makeNullableIfNeeded);
            }
            throw new AssertionError("Only nontrivial projections should have been captured, not: " + projection);
        }
        if (type.getArguments().isEmpty() || type.getArguments().size() != constructor.getParameters().size()) {
            return new ApproximationBounds<>(type, type);
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        List<TypeProjection> arguments = type.getArguments();
        List<TypeParameterDescriptor> parameters = constructor.getParameters();
        Intrinsics.checkNotNullExpressionValue(parameters, "getParameters(...)");
        for (Pair pair : CollectionsKt.zip(arguments, parameters)) {
            TypeProjection typeProjection = (TypeProjection) pair.component1();
            TypeParameterDescriptor typeParameterDescriptor = (TypeParameterDescriptor) pair.component2();
            Intrinsics.checkNotNull(typeParameterDescriptor);
            TypeArgument typeArgument = toTypeArgument(typeProjection, typeParameterDescriptor);
            if (typeProjection.isStarProjection()) {
                arrayList.add(typeArgument);
                arrayList2.add(typeArgument);
            } else {
                ApproximationBounds<TypeArgument> approximationBoundsApproximateProjection = approximateProjection(typeArgument);
                TypeArgument typeArgumentComponent1 = approximationBoundsApproximateProjection.component1();
                TypeArgument typeArgumentComponent2 = approximationBoundsApproximateProjection.component2();
                arrayList.add(typeArgumentComponent1);
                arrayList2.add(typeArgumentComponent2);
            }
        }
        ArrayList arrayList3 = arrayList;
        boolean z = false;
        if (!(arrayList3 instanceof Collection) || !arrayList3.isEmpty()) {
            Iterator it = arrayList3.iterator();
            while (it.hasNext()) {
                if (!((TypeArgument) it.next()).isConsistent()) {
                    z = true;
                    break;
                }
            }
        }
        if (z) {
            SimpleType nothingType2 = TypeUtilsKt.getBuiltIns(type).getNothingType();
            Intrinsics.checkNotNullExpressionValue(nothingType2, "getNothingType(...)");
            simpleTypeReplaceTypeArguments = nothingType2;
        } else {
            simpleTypeReplaceTypeArguments = replaceTypeArguments(type, arrayList);
        }
        return new ApproximationBounds<>(simpleTypeReplaceTypeArguments, replaceTypeArguments(type, arrayList2));
    }

    private static final KotlinType approximateCapturedTypes$makeNullableIfNeeded(KotlinType kotlinType, KotlinType kotlinType2) {
        KotlinType kotlinTypeMakeNullableIfNeeded = TypeUtils.makeNullableIfNeeded(kotlinType, kotlinType2.isMarkedNullable());
        Intrinsics.checkNotNullExpressionValue(kotlinTypeMakeNullableIfNeeded, "makeNullableIfNeeded(...)");
        return kotlinTypeMakeNullableIfNeeded;
    }

    private static final KotlinType replaceTypeArguments(KotlinType kotlinType, List<TypeArgument> list) {
        kotlinType.getArguments().size();
        list.size();
        List<TypeArgument> list2 = list;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list2, 10));
        Iterator<T> it = list2.iterator();
        while (it.hasNext()) {
            arrayList.add(toTypeProjection((TypeArgument) it.next()));
        }
        return TypeSubstitutionKt.replace$default(kotlinType, arrayList, null, null, 6, null);
    }

    private static final ApproximationBounds<TypeArgument> approximateProjection(TypeArgument typeArgument) {
        ApproximationBounds<KotlinType> approximationBoundsApproximateCapturedTypes = approximateCapturedTypes(typeArgument.getInProjection());
        KotlinType kotlinTypeComponent1 = approximationBoundsApproximateCapturedTypes.component1();
        KotlinType kotlinTypeComponent2 = approximationBoundsApproximateCapturedTypes.component2();
        ApproximationBounds<KotlinType> approximationBoundsApproximateCapturedTypes2 = approximateCapturedTypes(typeArgument.getOutProjection());
        return new ApproximationBounds<>(new TypeArgument(typeArgument.getTypeParameter(), kotlinTypeComponent2, approximationBoundsApproximateCapturedTypes2.component1()), new TypeArgument(typeArgument.getTypeParameter(), kotlinTypeComponent1, approximationBoundsApproximateCapturedTypes2.component2()));
    }
}
