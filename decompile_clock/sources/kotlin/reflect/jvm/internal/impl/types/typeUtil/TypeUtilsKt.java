package kotlin.reflect.jvm.internal.impl.types.typeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IndexedValue;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassKind;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptorWithTypeParameters;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeAliasDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.types.AbstractStubType;
import kotlin.reflect.jvm.internal.impl.types.DefinitelyNotNullType;
import kotlin.reflect.jvm.internal.impl.types.FlexibleType;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.KotlinTypeFactory;
import kotlin.reflect.jvm.internal.impl.types.KotlinTypeKt;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.StarProjectionImpl;
import kotlin.reflect.jvm.internal.impl.types.StubTypeForBuilderInference;
import kotlin.reflect.jvm.internal.impl.types.TypeAttributesKt;
import kotlin.reflect.jvm.internal.impl.types.TypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.TypeProjection;
import kotlin.reflect.jvm.internal.impl.types.TypeProjectionImpl;
import kotlin.reflect.jvm.internal.impl.types.TypeSubstitutionKt;
import kotlin.reflect.jvm.internal.impl.types.TypeUtils;
import kotlin.reflect.jvm.internal.impl.types.TypeWithEnhancementKt;
import kotlin.reflect.jvm.internal.impl.types.UnwrappedType;
import kotlin.reflect.jvm.internal.impl.types.Variance;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeChecker;
import kotlin.reflect.jvm.internal.impl.types.error.ErrorType;
import kotlin.reflect.jvm.internal.impl.types.model.TypeVariableTypeConstructorMarker;

/* JADX INFO: compiled from: TypeUtils.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class TypeUtilsKt {
    public static final boolean hasTypeParameterRecursiveBounds(TypeParameterDescriptor typeParameter) {
        Intrinsics.checkNotNullParameter(typeParameter, "typeParameter");
        return hasTypeParameterRecursiveBounds$default(typeParameter, null, null, 6, null);
    }

    public static final KotlinBuiltIns getBuiltIns(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        KotlinBuiltIns builtIns = kotlinType.getConstructor().getBuiltIns();
        Intrinsics.checkNotNullExpressionValue(builtIns, "getBuiltIns(...)");
        return builtIns;
    }

    public static final KotlinType makeNullable(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        KotlinType kotlinTypeMakeNullable = TypeUtils.makeNullable(kotlinType);
        Intrinsics.checkNotNullExpressionValue(kotlinTypeMakeNullable, "makeNullable(...)");
        return kotlinTypeMakeNullable;
    }

    public static final KotlinType makeNotNullable(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        KotlinType kotlinTypeMakeNotNullable = TypeUtils.makeNotNullable(kotlinType);
        Intrinsics.checkNotNullExpressionValue(kotlinTypeMakeNotNullable, "makeNotNullable(...)");
        return kotlinTypeMakeNotNullable;
    }

    public static final boolean isNothing(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        return KotlinBuiltIns.isNothing(kotlinType);
    }

    public static final boolean isBoolean(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        return KotlinBuiltIns.isBoolean(kotlinType);
    }

    public static final boolean isTypeParameter(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        return TypeUtils.isTypeParameter(kotlinType);
    }

    public static final boolean containsTypeParameter(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        return TypeUtils.contains(kotlinType, new Function1() { // from class: kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt$$Lambda$0
            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return TypeUtilsKt.containsTypeParameter$lambda$0((UnwrappedType) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Boolean containsTypeParameter$lambda$0(UnwrappedType unwrappedType) {
        return Boolean.valueOf(TypeUtils.isTypeParameter(unwrappedType));
    }

    public static final boolean isSubtypeOf(KotlinType kotlinType, KotlinType superType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        Intrinsics.checkNotNullParameter(superType, "superType");
        return KotlinTypeChecker.DEFAULT.isSubtypeOf(kotlinType, superType);
    }

    public static final KotlinType replaceAnnotations(KotlinType kotlinType, Annotations newAnnotations) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        Intrinsics.checkNotNullParameter(newAnnotations, "newAnnotations");
        return (kotlinType.getAnnotations().isEmpty() && newAnnotations.isEmpty()) ? kotlinType : kotlinType.unwrap().replaceAttributes(TypeAttributesKt.replaceAnnotations(kotlinType.getAttributes(), newAnnotations));
    }

    public static final TypeProjection createProjection(KotlinType type, Variance projectionKind, TypeParameterDescriptor typeParameterDescriptor) {
        Intrinsics.checkNotNullParameter(type, "type");
        Intrinsics.checkNotNullParameter(projectionKind, "projectionKind");
        if ((typeParameterDescriptor != null ? typeParameterDescriptor.getVariance() : null) == projectionKind) {
            projectionKind = Variance.INVARIANT;
        }
        return new TypeProjectionImpl(projectionKind, type);
    }

    public static final TypeProjection asTypeProjection(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        return new TypeProjectionImpl(kotlinType);
    }

    public static final boolean contains(KotlinType kotlinType, Function1<? super UnwrappedType, Boolean> predicate) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        return TypeUtils.contains(kotlinType, predicate);
    }

    public static final Set<TypeParameterDescriptor> extractTypeParametersFromUpperBounds(KotlinType kotlinType, Set<? extends TypeParameterDescriptor> set) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        extractTypeParametersFromUpperBounds(kotlinType, kotlinType, linkedHashSet, set);
        return linkedHashSet;
    }

    private static final void extractTypeParametersFromUpperBounds(KotlinType kotlinType, KotlinType kotlinType2, Set<TypeParameterDescriptor> set, Set<? extends TypeParameterDescriptor> set2) {
        ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = kotlinType.getConstructor().mo1491getDeclarationDescriptor();
        if (classifierDescriptorMo1491getDeclarationDescriptor instanceof TypeParameterDescriptor) {
            if (!Intrinsics.areEqual(kotlinType.getConstructor(), kotlinType2.getConstructor())) {
                set.add(classifierDescriptorMo1491getDeclarationDescriptor);
                return;
            }
            for (KotlinType kotlinType3 : ((TypeParameterDescriptor) classifierDescriptorMo1491getDeclarationDescriptor).getUpperBounds()) {
                Intrinsics.checkNotNull(kotlinType3);
                extractTypeParametersFromUpperBounds(kotlinType3, kotlinType2, set, set2);
            }
            return;
        }
        ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor2 = kotlinType.getConstructor().mo1491getDeclarationDescriptor();
        ClassifierDescriptorWithTypeParameters classifierDescriptorWithTypeParameters = classifierDescriptorMo1491getDeclarationDescriptor2 instanceof ClassifierDescriptorWithTypeParameters ? (ClassifierDescriptorWithTypeParameters) classifierDescriptorMo1491getDeclarationDescriptor2 : null;
        List<TypeParameterDescriptor> declaredTypeParameters = classifierDescriptorWithTypeParameters != null ? classifierDescriptorWithTypeParameters.getDeclaredTypeParameters() : null;
        int i = 0;
        for (TypeProjection typeProjection : kotlinType.getArguments()) {
            int i2 = i + 1;
            TypeParameterDescriptor typeParameterDescriptor = declaredTypeParameters != null ? (TypeParameterDescriptor) CollectionsKt.getOrNull(declaredTypeParameters, i) : null;
            if ((typeParameterDescriptor == null || set2 == null || !set2.contains(typeParameterDescriptor)) && !typeProjection.isStarProjection() && !CollectionsKt.contains(set, typeProjection.getType().getConstructor().mo1491getDeclarationDescriptor()) && !Intrinsics.areEqual(typeProjection.getType().getConstructor(), kotlinType2.getConstructor())) {
                KotlinType type = typeProjection.getType();
                Intrinsics.checkNotNullExpressionValue(type, "getType(...)");
                extractTypeParametersFromUpperBounds(type, kotlinType2, set, set2);
            }
            i = i2;
        }
    }

    public static /* synthetic */ boolean hasTypeParameterRecursiveBounds$default(TypeParameterDescriptor typeParameterDescriptor, TypeConstructor typeConstructor, Set set, int i, Object obj) {
        if ((i & 2) != 0) {
            typeConstructor = null;
        }
        if ((i & 4) != 0) {
            set = null;
        }
        return hasTypeParameterRecursiveBounds(typeParameterDescriptor, typeConstructor, set);
    }

    public static final boolean hasTypeParameterRecursiveBounds(TypeParameterDescriptor typeParameter, TypeConstructor typeConstructor, Set<? extends TypeParameterDescriptor> set) {
        Intrinsics.checkNotNullParameter(typeParameter, "typeParameter");
        List<KotlinType> upperBounds = typeParameter.getUpperBounds();
        Intrinsics.checkNotNullExpressionValue(upperBounds, "getUpperBounds(...)");
        List<KotlinType> list = upperBounds;
        if ((list instanceof Collection) && list.isEmpty()) {
            return false;
        }
        for (KotlinType kotlinType : list) {
            Intrinsics.checkNotNull(kotlinType);
            if (containsSelfTypeParameter(kotlinType, typeParameter.getDefaultType().getConstructor(), set) && (typeConstructor == null || Intrinsics.areEqual(kotlinType.getConstructor(), typeConstructor))) {
                return true;
            }
        }
        return false;
    }

    private static final boolean containsSelfTypeParameter(KotlinType kotlinType, TypeConstructor typeConstructor, Set<? extends TypeParameterDescriptor> set) {
        boolean zContainsSelfTypeParameter;
        if (Intrinsics.areEqual(kotlinType.getConstructor(), typeConstructor)) {
            return true;
        }
        ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = kotlinType.getConstructor().mo1491getDeclarationDescriptor();
        ClassifierDescriptorWithTypeParameters classifierDescriptorWithTypeParameters = classifierDescriptorMo1491getDeclarationDescriptor instanceof ClassifierDescriptorWithTypeParameters ? (ClassifierDescriptorWithTypeParameters) classifierDescriptorMo1491getDeclarationDescriptor : null;
        List<TypeParameterDescriptor> declaredTypeParameters = classifierDescriptorWithTypeParameters != null ? classifierDescriptorWithTypeParameters.getDeclaredTypeParameters() : null;
        Iterable<IndexedValue> iterableWithIndex = CollectionsKt.withIndex(kotlinType.getArguments());
        if (!(iterableWithIndex instanceof Collection) || !((Collection) iterableWithIndex).isEmpty()) {
            for (IndexedValue indexedValue : iterableWithIndex) {
                int index = indexedValue.getIndex();
                TypeProjection typeProjection = (TypeProjection) indexedValue.component2();
                TypeParameterDescriptor typeParameterDescriptor = declaredTypeParameters != null ? (TypeParameterDescriptor) CollectionsKt.getOrNull(declaredTypeParameters, index) : null;
                if ((typeParameterDescriptor == null || set == null || !set.contains(typeParameterDescriptor)) && !typeProjection.isStarProjection()) {
                    KotlinType type = typeProjection.getType();
                    Intrinsics.checkNotNullExpressionValue(type, "getType(...)");
                    zContainsSelfTypeParameter = containsSelfTypeParameter(type, typeConstructor, set);
                } else {
                    zContainsSelfTypeParameter = false;
                }
                if (zContainsSelfTypeParameter) {
                    return true;
                }
            }
        }
        return false;
    }

    public static final boolean containsTypeAliasParameters(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        return contains(kotlinType, new Function1() { // from class: kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt$$Lambda$2
            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return Boolean.valueOf(TypeUtilsKt.containsTypeAliasParameters$lambda$14((UnwrappedType) obj));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean containsTypeAliasParameters$lambda$14(UnwrappedType it) {
        Intrinsics.checkNotNullParameter(it, "it");
        ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = it.getConstructor().mo1491getDeclarationDescriptor();
        if (classifierDescriptorMo1491getDeclarationDescriptor != null) {
            return isTypeAliasParameter(classifierDescriptorMo1491getDeclarationDescriptor);
        }
        return false;
    }

    public static final boolean isTypeAliasParameter(ClassifierDescriptor classifierDescriptor) {
        Intrinsics.checkNotNullParameter(classifierDescriptor, "<this>");
        return (classifierDescriptor instanceof TypeParameterDescriptor) && (((TypeParameterDescriptor) classifierDescriptor).getContainingDeclaration() instanceof TypeAliasDescriptor);
    }

    public static final boolean requiresTypeAliasExpansion(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        return contains(kotlinType, new Function1() { // from class: kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt$$Lambda$4
            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return Boolean.valueOf(TypeUtilsKt.requiresTypeAliasExpansion$lambda$17((UnwrappedType) obj));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean requiresTypeAliasExpansion$lambda$17(UnwrappedType it) {
        Intrinsics.checkNotNullParameter(it, "it");
        ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = it.getConstructor().mo1491getDeclarationDescriptor();
        if (classifierDescriptorMo1491getDeclarationDescriptor != null) {
            return (classifierDescriptorMo1491getDeclarationDescriptor instanceof TypeAliasDescriptor) || (classifierDescriptorMo1491getDeclarationDescriptor instanceof TypeParameterDescriptor);
        }
        return false;
    }

    public static final KotlinType getRepresentativeUpperBound(TypeParameterDescriptor typeParameterDescriptor) {
        Object obj;
        Intrinsics.checkNotNullParameter(typeParameterDescriptor, "<this>");
        List<KotlinType> upperBounds = typeParameterDescriptor.getUpperBounds();
        Intrinsics.checkNotNullExpressionValue(upperBounds, "getUpperBounds(...)");
        upperBounds.isEmpty();
        List<KotlinType> upperBounds2 = typeParameterDescriptor.getUpperBounds();
        Intrinsics.checkNotNullExpressionValue(upperBounds2, "getUpperBounds(...)");
        Iterator<T> it = upperBounds2.iterator();
        while (true) {
            obj = null;
            if (!it.hasNext()) {
                break;
            }
            Object next = it.next();
            ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = ((KotlinType) next).getConstructor().mo1491getDeclarationDescriptor();
            ClassDescriptor classDescriptor = classifierDescriptorMo1491getDeclarationDescriptor instanceof ClassDescriptor ? (ClassDescriptor) classifierDescriptorMo1491getDeclarationDescriptor : null;
            if (classDescriptor != null && classDescriptor.getKind() != ClassKind.INTERFACE && classDescriptor.getKind() != ClassKind.ANNOTATION_CLASS) {
                obj = next;
                break;
            }
        }
        KotlinType kotlinType = (KotlinType) obj;
        if (kotlinType != null) {
            return kotlinType;
        }
        List<KotlinType> upperBounds3 = typeParameterDescriptor.getUpperBounds();
        Intrinsics.checkNotNullExpressionValue(upperBounds3, "getUpperBounds(...)");
        Object objFirst = CollectionsKt.first((List<? extends Object>) upperBounds3);
        Intrinsics.checkNotNullExpressionValue(objFirst, "first(...)");
        return (KotlinType) objFirst;
    }

    public static final boolean shouldBeUpdated(KotlinType kotlinType) {
        return kotlinType == null || contains(kotlinType, new Function1() { // from class: kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt$$Lambda$5
            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return Boolean.valueOf(TypeUtilsKt.shouldBeUpdated$lambda$24((UnwrappedType) obj));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean shouldBeUpdated$lambda$24(UnwrappedType it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return (it instanceof StubTypeForBuilderInference) || (it.getConstructor() instanceof TypeVariableTypeConstructorMarker) || KotlinTypeKt.isError(it);
    }

    public static final boolean isStubType(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        return (kotlinType instanceof AbstractStubType) || ((kotlinType instanceof DefinitelyNotNullType) && (((DefinitelyNotNullType) kotlinType).getOriginal() instanceof AbstractStubType));
    }

    public static final boolean isStubTypeForBuilderInference(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        return (kotlinType instanceof StubTypeForBuilderInference) || ((kotlinType instanceof DefinitelyNotNullType) && (((DefinitelyNotNullType) kotlinType).getOriginal() instanceof StubTypeForBuilderInference));
    }

    public static final boolean isUnresolvedType(KotlinType type) {
        Intrinsics.checkNotNullParameter(type, "type");
        return (type instanceof ErrorType) && ((ErrorType) type).getKind().isUnresolved();
    }

    public static final KotlinType replaceArgumentsWithStarProjections(KotlinType kotlinType) {
        SimpleType simpleTypeFlexibleType;
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        UnwrappedType unwrappedTypeUnwrap = kotlinType.unwrap();
        if (unwrappedTypeUnwrap instanceof FlexibleType) {
            FlexibleType flexibleType = (FlexibleType) unwrappedTypeUnwrap;
            SimpleType lowerBound = flexibleType.getLowerBound();
            if (!lowerBound.getConstructor().getParameters().isEmpty() && lowerBound.getConstructor().mo1491getDeclarationDescriptor() != null) {
                List<TypeParameterDescriptor> parameters = lowerBound.getConstructor().getParameters();
                Intrinsics.checkNotNullExpressionValue(parameters, "getParameters(...)");
                List<TypeParameterDescriptor> list = parameters;
                ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
                Iterator<T> it = list.iterator();
                while (it.hasNext()) {
                    arrayList.add(new StarProjectionImpl((TypeParameterDescriptor) it.next()));
                }
                lowerBound = TypeSubstitutionKt.replace$default(lowerBound, arrayList, null, 2, null);
            }
            SimpleType upperBound = flexibleType.getUpperBound();
            if (!upperBound.getConstructor().getParameters().isEmpty() && upperBound.getConstructor().mo1491getDeclarationDescriptor() != null) {
                List<TypeParameterDescriptor> parameters2 = upperBound.getConstructor().getParameters();
                Intrinsics.checkNotNullExpressionValue(parameters2, "getParameters(...)");
                List<TypeParameterDescriptor> list2 = parameters2;
                ArrayList arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(list2, 10));
                Iterator<T> it2 = list2.iterator();
                while (it2.hasNext()) {
                    arrayList2.add(new StarProjectionImpl((TypeParameterDescriptor) it2.next()));
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
                Iterator<T> it3 = list3.iterator();
                while (it3.hasNext()) {
                    arrayList3.add(new StarProjectionImpl((TypeParameterDescriptor) it3.next()));
                }
                simpleTypeReplace$default = TypeSubstitutionKt.replace$default(simpleTypeReplace$default, arrayList3, null, 2, null);
            }
            simpleTypeFlexibleType = simpleTypeReplace$default;
        }
        return TypeWithEnhancementKt.inheritEnhancement(simpleTypeFlexibleType, unwrappedTypeUnwrap);
    }
}
