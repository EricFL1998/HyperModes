package kotlin.reflect.jvm.internal.impl.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.storage.LockBasedStorageManager;
import kotlin.reflect.jvm.internal.impl.storage.MemoizedFunctionToNotNull;
import kotlin.reflect.jvm.internal.impl.types.checker.IntersectionTypeKt;
import kotlin.reflect.jvm.internal.impl.types.error.ErrorType;
import kotlin.reflect.jvm.internal.impl.types.error.ErrorTypeKind;
import kotlin.reflect.jvm.internal.impl.types.error.ErrorUtils;
import kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt;

/* JADX INFO: compiled from: TypeParameterUpperBoundEraser.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class TypeParameterUpperBoundEraser {
    public static final Companion Companion = new Companion(null);
    private final Lazy erroneousErasedBound$delegate;
    private final MemoizedFunctionToNotNull<DataToEraseUpperBound, KotlinType> getErasedUpperBound;
    private final TypeParameterErasureOptions options;
    private final ErasureProjectionComputer projectionComputer;
    private final LockBasedStorageManager storage;

    public TypeParameterUpperBoundEraser(ErasureProjectionComputer projectionComputer, TypeParameterErasureOptions options) {
        Intrinsics.checkNotNullParameter(projectionComputer, "projectionComputer");
        Intrinsics.checkNotNullParameter(options, "options");
        this.projectionComputer = projectionComputer;
        this.options = options;
        LockBasedStorageManager lockBasedStorageManager = new LockBasedStorageManager("Type parameter upper bound erasure results");
        this.storage = lockBasedStorageManager;
        this.erroneousErasedBound$delegate = LazyKt.lazy(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.types.TypeParameterUpperBoundEraser$$Lambda$0
            private final TypeParameterUpperBoundEraser arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return TypeParameterUpperBoundEraser.erroneousErasedBound_delegate$lambda$0(this.arg$0);
            }
        });
        MemoizedFunctionToNotNull<DataToEraseUpperBound, KotlinType> memoizedFunctionToNotNullCreateMemoizedFunction = lockBasedStorageManager.createMemoizedFunction(new Function1(this) { // from class: kotlin.reflect.jvm.internal.impl.types.TypeParameterUpperBoundEraser$$Lambda$1
            private final TypeParameterUpperBoundEraser arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return TypeParameterUpperBoundEraser.getErasedUpperBound$lambda$2(this.arg$0, (TypeParameterUpperBoundEraser.DataToEraseUpperBound) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(memoizedFunctionToNotNullCreateMemoizedFunction, "createMemoizedFunction(...)");
        this.getErasedUpperBound = memoizedFunctionToNotNullCreateMemoizedFunction;
    }

    public /* synthetic */ TypeParameterUpperBoundEraser(ErasureProjectionComputer erasureProjectionComputer, TypeParameterErasureOptions typeParameterErasureOptions, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(erasureProjectionComputer, (i & 2) != 0 ? new TypeParameterErasureOptions(false, false) : typeParameterErasureOptions);
    }

    private final ErrorType getErroneousErasedBound() {
        return (ErrorType) this.erroneousErasedBound$delegate.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final ErrorType erroneousErasedBound_delegate$lambda$0(TypeParameterUpperBoundEraser this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return ErrorUtils.createErrorType(ErrorTypeKind.CANNOT_COMPUTE_ERASED_BOUND, this$0.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: compiled from: TypeParameterUpperBoundEraser.kt */
    static final class DataToEraseUpperBound {
        private final ErasureTypeAttributes typeAttr;
        private final TypeParameterDescriptor typeParameter;

        public String toString() {
            return "DataToEraseUpperBound(typeParameter=" + this.typeParameter + ", typeAttr=" + this.typeAttr + ')';
        }

        public DataToEraseUpperBound(TypeParameterDescriptor typeParameter, ErasureTypeAttributes typeAttr) {
            Intrinsics.checkNotNullParameter(typeParameter, "typeParameter");
            Intrinsics.checkNotNullParameter(typeAttr, "typeAttr");
            this.typeParameter = typeParameter;
            this.typeAttr = typeAttr;
        }

        public final TypeParameterDescriptor getTypeParameter() {
            return this.typeParameter;
        }

        public final ErasureTypeAttributes getTypeAttr() {
            return this.typeAttr;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof DataToEraseUpperBound)) {
                return false;
            }
            DataToEraseUpperBound dataToEraseUpperBound = (DataToEraseUpperBound) obj;
            return Intrinsics.areEqual(dataToEraseUpperBound.typeParameter, this.typeParameter) && Intrinsics.areEqual(dataToEraseUpperBound.typeAttr, this.typeAttr);
        }

        public int hashCode() {
            int iHashCode = this.typeParameter.hashCode();
            return iHashCode + (iHashCode * 31) + this.typeAttr.hashCode();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final KotlinType getErasedUpperBound$lambda$2(TypeParameterUpperBoundEraser this$0, DataToEraseUpperBound dataToEraseUpperBound) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return this$0.getErasedUpperBoundInternal(dataToEraseUpperBound.getTypeParameter(), dataToEraseUpperBound.getTypeAttr());
    }

    public final KotlinType getErasedUpperBound(TypeParameterDescriptor typeParameter, ErasureTypeAttributes typeAttr) {
        Intrinsics.checkNotNullParameter(typeParameter, "typeParameter");
        Intrinsics.checkNotNullParameter(typeAttr, "typeAttr");
        KotlinType kotlinTypeInvoke = this.getErasedUpperBound.invoke(new DataToEraseUpperBound(typeParameter, typeAttr));
        Intrinsics.checkNotNullExpressionValue(kotlinTypeInvoke, "invoke(...)");
        return kotlinTypeInvoke;
    }

    private final KotlinType getDefaultType(ErasureTypeAttributes erasureTypeAttributes) {
        KotlinType kotlinTypeReplaceArgumentsWithStarProjections;
        SimpleType defaultType = erasureTypeAttributes.getDefaultType();
        return (defaultType == null || (kotlinTypeReplaceArgumentsWithStarProjections = TypeUtilsKt.replaceArgumentsWithStarProjections(defaultType)) == null) ? getErroneousErasedBound() : kotlinTypeReplaceArgumentsWithStarProjections;
    }

    private final KotlinType getErasedUpperBoundInternal(TypeParameterDescriptor typeParameterDescriptor, ErasureTypeAttributes erasureTypeAttributes) {
        TypeProjection typeProjectionComputeProjection;
        Set<TypeParameterDescriptor> visitedTypeParameters = erasureTypeAttributes.getVisitedTypeParameters();
        if (visitedTypeParameters != null && visitedTypeParameters.contains(typeParameterDescriptor.getOriginal())) {
            return getDefaultType(erasureTypeAttributes);
        }
        SimpleType defaultType = typeParameterDescriptor.getDefaultType();
        Intrinsics.checkNotNullExpressionValue(defaultType, "getDefaultType(...)");
        Set<TypeParameterDescriptor> setExtractTypeParametersFromUpperBounds = TypeUtilsKt.extractTypeParametersFromUpperBounds(defaultType, visitedTypeParameters);
        LinkedHashMap linkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(setExtractTypeParametersFromUpperBounds, 10)), 16));
        for (TypeParameterDescriptor typeParameterDescriptor2 : setExtractTypeParametersFromUpperBounds) {
            if (visitedTypeParameters == null || !visitedTypeParameters.contains(typeParameterDescriptor2)) {
                typeProjectionComputeProjection = this.projectionComputer.computeProjection(typeParameterDescriptor2, erasureTypeAttributes, this, getErasedUpperBound(typeParameterDescriptor2, erasureTypeAttributes.withNewVisitedTypeParameter(typeParameterDescriptor)));
            } else {
                typeProjectionComputeProjection = TypeUtils.makeStarProjection(typeParameterDescriptor2, erasureTypeAttributes);
                Intrinsics.checkNotNullExpressionValue(typeProjectionComputeProjection, "makeStarProjection(...)");
            }
            Pair pair = TuplesKt.to(typeParameterDescriptor2.getTypeConstructor(), typeProjectionComputeProjection);
            linkedHashMap.put(pair.getFirst(), pair.getSecond());
        }
        TypeSubstitutor typeSubstitutorCreate = TypeSubstitutor.create(TypeConstructorSubstitution.Companion.createByConstructorsMap$default(TypeConstructorSubstitution.Companion, linkedHashMap, false, 2, null));
        Intrinsics.checkNotNullExpressionValue(typeSubstitutorCreate, "create(...)");
        List<KotlinType> upperBounds = typeParameterDescriptor.getUpperBounds();
        Intrinsics.checkNotNullExpressionValue(upperBounds, "getUpperBounds(...)");
        Set<KotlinType> setSubstituteErasedUpperBounds = substituteErasedUpperBounds(typeSubstitutorCreate, upperBounds, erasureTypeAttributes);
        if (!setSubstituteErasedUpperBounds.isEmpty()) {
            if (!this.options.getIntersectUpperBounds()) {
                if (setSubstituteErasedUpperBounds.size() != 1) {
                    throw new IllegalArgumentException("Should only be one computed upper bound if no need to intersect all bounds".toString());
                }
                return (KotlinType) CollectionsKt.single(setSubstituteErasedUpperBounds);
            }
            List list = CollectionsKt.toList(setSubstituteErasedUpperBounds);
            ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
            Iterator it = list.iterator();
            while (it.hasNext()) {
                arrayList.add(((KotlinType) it.next()).unwrap());
            }
            return IntersectionTypeKt.intersectTypes(arrayList);
        }
        return getDefaultType(erasureTypeAttributes);
    }

    private final Set<KotlinType> substituteErasedUpperBounds(TypeSubstitutor typeSubstitutor, List<? extends KotlinType> list, ErasureTypeAttributes erasureTypeAttributes) {
        Set setCreateSetBuilder = SetsKt.createSetBuilder();
        for (KotlinType kotlinType : list) {
            ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = kotlinType.getConstructor().mo1491getDeclarationDescriptor();
            if (classifierDescriptorMo1491getDeclarationDescriptor instanceof ClassDescriptor) {
                setCreateSetBuilder.add(Companion.replaceArgumentsOfUpperBound(kotlinType, typeSubstitutor, erasureTypeAttributes.getVisitedTypeParameters(), this.options.getLeaveNonTypeParameterTypes()));
            } else if (classifierDescriptorMo1491getDeclarationDescriptor instanceof TypeParameterDescriptor) {
                Set<TypeParameterDescriptor> visitedTypeParameters = erasureTypeAttributes.getVisitedTypeParameters();
                if (visitedTypeParameters != null && visitedTypeParameters.contains(classifierDescriptorMo1491getDeclarationDescriptor)) {
                    setCreateSetBuilder.add(getDefaultType(erasureTypeAttributes));
                } else {
                    List<KotlinType> upperBounds = ((TypeParameterDescriptor) classifierDescriptorMo1491getDeclarationDescriptor).getUpperBounds();
                    Intrinsics.checkNotNullExpressionValue(upperBounds, "getUpperBounds(...)");
                    setCreateSetBuilder.addAll(substituteErasedUpperBounds(typeSubstitutor, upperBounds, erasureTypeAttributes));
                }
            }
            if (!this.options.getIntersectUpperBounds()) {
                break;
            }
        }
        return SetsKt.build(setCreateSetBuilder);
    }

    /* JADX INFO: compiled from: TypeParameterUpperBoundEraser.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* JADX WARN: Code duplicated, block: B:29:0x00a9  */
        /* JADX WARN: Code duplicated, block: B:57:0x0143  */
        /* JADX WARN: Code duplicated, block: B:88:0x01e4  */
        public final KotlinType replaceArgumentsOfUpperBound(KotlinType kotlinType, TypeSubstitutor substitutor, Set<? extends TypeParameterDescriptor> set, boolean z) {
            SimpleType simpleTypeFlexibleType;
            KotlinType type;
            KotlinType type2;
            KotlinType type3;
            Intrinsics.checkNotNullParameter(kotlinType, "<this>");
            Intrinsics.checkNotNullParameter(substitutor, "substitutor");
            UnwrappedType unwrappedTypeUnwrap = kotlinType.unwrap();
            if (unwrappedTypeUnwrap instanceof FlexibleType) {
                FlexibleType flexibleType = (FlexibleType) unwrappedTypeUnwrap;
                SimpleType lowerBound = flexibleType.getLowerBound();
                if (!lowerBound.getConstructor().getParameters().isEmpty() && lowerBound.getConstructor().mo1491getDeclarationDescriptor() != null) {
                    List<TypeParameterDescriptor> parameters = lowerBound.getConstructor().getParameters();
                    Intrinsics.checkNotNullExpressionValue(parameters, "getParameters(...)");
                    List<TypeParameterDescriptor> list = parameters;
                    ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
                    for (TypeParameterDescriptor typeParameterDescriptor : list) {
                        StarProjectionImpl starProjectionImpl = (TypeProjection) CollectionsKt.getOrNull(kotlinType.getArguments(), typeParameterDescriptor.getIndex());
                        if (!z || starProjectionImpl == null || (type3 = starProjectionImpl.getType()) == null || TypeUtilsKt.containsTypeParameter(type3)) {
                            boolean z2 = set != null && set.contains(typeParameterDescriptor);
                            if (starProjectionImpl == null || z2) {
                                starProjectionImpl = new StarProjectionImpl(typeParameterDescriptor);
                            } else {
                                TypeSubstitution substitution = substitutor.getSubstitution();
                                KotlinType type4 = starProjectionImpl.getType();
                                Intrinsics.checkNotNullExpressionValue(type4, "getType(...)");
                                if (substitution.mo1496get(type4) == null) {
                                    starProjectionImpl = new StarProjectionImpl(typeParameterDescriptor);
                                }
                            }
                        }
                        arrayList.add(starProjectionImpl);
                    }
                    lowerBound = TypeSubstitutionKt.replace$default(lowerBound, arrayList, null, 2, null);
                }
                SimpleType upperBound = flexibleType.getUpperBound();
                if (!upperBound.getConstructor().getParameters().isEmpty() && upperBound.getConstructor().mo1491getDeclarationDescriptor() != null) {
                    List<TypeParameterDescriptor> parameters2 = upperBound.getConstructor().getParameters();
                    Intrinsics.checkNotNullExpressionValue(parameters2, "getParameters(...)");
                    List<TypeParameterDescriptor> list2 = parameters2;
                    ArrayList arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(list2, 10));
                    for (TypeParameterDescriptor typeParameterDescriptor2 : list2) {
                        StarProjectionImpl starProjectionImpl2 = (TypeProjection) CollectionsKt.getOrNull(kotlinType.getArguments(), typeParameterDescriptor2.getIndex());
                        if (!z || starProjectionImpl2 == null || (type2 = starProjectionImpl2.getType()) == null || TypeUtilsKt.containsTypeParameter(type2)) {
                            boolean z3 = set != null && set.contains(typeParameterDescriptor2);
                            if (starProjectionImpl2 == null || z3) {
                                starProjectionImpl2 = new StarProjectionImpl(typeParameterDescriptor2);
                            } else {
                                TypeSubstitution substitution2 = substitutor.getSubstitution();
                                KotlinType type5 = starProjectionImpl2.getType();
                                Intrinsics.checkNotNullExpressionValue(type5, "getType(...)");
                                if (substitution2.mo1496get(type5) == null) {
                                    starProjectionImpl2 = new StarProjectionImpl(typeParameterDescriptor2);
                                }
                            }
                        }
                        arrayList2.add(starProjectionImpl2);
                    }
                    upperBound = TypeSubstitutionKt.replace$default(upperBound, arrayList2, null, 2, null);
                }
                simpleTypeFlexibleType = KotlinTypeFactory.flexibleType(lowerBound, upperBound);
            } else {
                if (!(unwrappedTypeUnwrap instanceof SimpleType)) {
                    throw new NoWhenBranchMatchedException();
                }
                SimpleType simpleTypeReplace$default = (SimpleType) unwrappedTypeUnwrap;
                if (!simpleTypeReplace$default.getConstructor().getParameters().isEmpty() && simpleTypeReplace$default.getConstructor().mo1491getDeclarationDescriptor() != null) {
                    List<TypeParameterDescriptor> parameters3 = simpleTypeReplace$default.getConstructor().getParameters();
                    Intrinsics.checkNotNullExpressionValue(parameters3, "getParameters(...)");
                    List<TypeParameterDescriptor> list3 = parameters3;
                    ArrayList arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(list3, 10));
                    for (TypeParameterDescriptor typeParameterDescriptor3 : list3) {
                        StarProjectionImpl starProjectionImpl3 = (TypeProjection) CollectionsKt.getOrNull(kotlinType.getArguments(), typeParameterDescriptor3.getIndex());
                        if (!z || starProjectionImpl3 == null || (type = starProjectionImpl3.getType()) == null || TypeUtilsKt.containsTypeParameter(type)) {
                            boolean z4 = set != null && set.contains(typeParameterDescriptor3);
                            if (starProjectionImpl3 == null || z4) {
                                starProjectionImpl3 = new StarProjectionImpl(typeParameterDescriptor3);
                            } else {
                                TypeSubstitution substitution3 = substitutor.getSubstitution();
                                KotlinType type6 = starProjectionImpl3.getType();
                                Intrinsics.checkNotNullExpressionValue(type6, "getType(...)");
                                if (substitution3.mo1496get(type6) == null) {
                                    starProjectionImpl3 = new StarProjectionImpl(typeParameterDescriptor3);
                                }
                            }
                        }
                        arrayList3.add(starProjectionImpl3);
                    }
                    simpleTypeReplace$default = TypeSubstitutionKt.replace$default(simpleTypeReplace$default, arrayList3, null, 2, null);
                }
                simpleTypeFlexibleType = simpleTypeReplace$default;
            }
            KotlinType kotlinTypeSafeSubstitute = substitutor.safeSubstitute(TypeWithEnhancementKt.inheritEnhancement(simpleTypeFlexibleType, unwrappedTypeUnwrap), Variance.OUT_VARIANCE);
            Intrinsics.checkNotNullExpressionValue(kotlinTypeSafeSubstitute, "safeSubstitute(...)");
            return kotlinTypeSafeSubstitute;
        }
    }
}
