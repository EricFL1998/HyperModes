package kotlin.reflect.jvm.internal.impl.types.model;

import java.util.Collection;
import java.util.List;
import kotlin.reflect.jvm.internal.impl.types.TypeCheckerState;

/* JADX INFO: compiled from: TypeSystemContext.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface TypeSystemContext extends TypeSystemOptimizationContext {
    boolean areEqualTypeConstructors(TypeConstructorMarker typeConstructorMarker, TypeConstructorMarker typeConstructorMarker2);

    int argumentsCount(KotlinTypeMarker kotlinTypeMarker);

    TypeArgumentListMarker asArgumentList(SimpleTypeMarker simpleTypeMarker);

    CapturedTypeMarker asCapturedType(SimpleTypeMarker simpleTypeMarker);

    DefinitelyNotNullTypeMarker asDefinitelyNotNullType(SimpleTypeMarker simpleTypeMarker);

    DynamicTypeMarker asDynamicType(FlexibleTypeMarker flexibleTypeMarker);

    FlexibleTypeMarker asFlexibleType(KotlinTypeMarker kotlinTypeMarker);

    SimpleTypeMarker asSimpleType(KotlinTypeMarker kotlinTypeMarker);

    TypeArgumentMarker asTypeArgument(KotlinTypeMarker kotlinTypeMarker);

    SimpleTypeMarker captureFromArguments(SimpleTypeMarker simpleTypeMarker, CaptureStatus captureStatus);

    CaptureStatus captureStatus(CapturedTypeMarker capturedTypeMarker);

    List<SimpleTypeMarker> fastCorrespondingSupertypes(SimpleTypeMarker simpleTypeMarker, TypeConstructorMarker typeConstructorMarker);

    TypeArgumentMarker get(TypeArgumentListMarker typeArgumentListMarker, int i);

    TypeArgumentMarker getArgument(KotlinTypeMarker kotlinTypeMarker, int i);

    TypeArgumentMarker getArgumentOrNull(SimpleTypeMarker simpleTypeMarker, int i);

    List<TypeArgumentMarker> getArguments(KotlinTypeMarker kotlinTypeMarker);

    TypeParameterMarker getParameter(TypeConstructorMarker typeConstructorMarker, int i);

    List<TypeParameterMarker> getParameters(TypeConstructorMarker typeConstructorMarker);

    KotlinTypeMarker getType(TypeArgumentMarker typeArgumentMarker);

    TypeParameterMarker getTypeParameter(TypeVariableTypeConstructorMarker typeVariableTypeConstructorMarker);

    TypeParameterMarker getTypeParameterClassifier(TypeConstructorMarker typeConstructorMarker);

    List<KotlinTypeMarker> getUpperBounds(TypeParameterMarker typeParameterMarker);

    TypeVariance getVariance(TypeArgumentMarker typeArgumentMarker);

    TypeVariance getVariance(TypeParameterMarker typeParameterMarker);

    boolean hasFlexibleNullability(KotlinTypeMarker kotlinTypeMarker);

    boolean hasRecursiveBounds(TypeParameterMarker typeParameterMarker, TypeConstructorMarker typeConstructorMarker);

    KotlinTypeMarker intersectTypes(List<? extends KotlinTypeMarker> list);

    boolean isAnyConstructor(TypeConstructorMarker typeConstructorMarker);

    boolean isCapturedType(KotlinTypeMarker kotlinTypeMarker);

    boolean isClassType(SimpleTypeMarker simpleTypeMarker);

    boolean isClassTypeConstructor(TypeConstructorMarker typeConstructorMarker);

    boolean isCommonFinalClassConstructor(TypeConstructorMarker typeConstructorMarker);

    boolean isDefinitelyNotNullType(KotlinTypeMarker kotlinTypeMarker);

    boolean isDenotable(TypeConstructorMarker typeConstructorMarker);

    boolean isDynamic(KotlinTypeMarker kotlinTypeMarker);

    boolean isError(KotlinTypeMarker kotlinTypeMarker);

    boolean isFlexibleWithDifferentTypeConstructors(KotlinTypeMarker kotlinTypeMarker);

    boolean isIntegerLiteralType(SimpleTypeMarker simpleTypeMarker);

    boolean isIntegerLiteralTypeConstructor(TypeConstructorMarker typeConstructorMarker);

    boolean isIntersection(TypeConstructorMarker typeConstructorMarker);

    boolean isMarkedNullable(KotlinTypeMarker kotlinTypeMarker);

    boolean isMarkedNullable(SimpleTypeMarker simpleTypeMarker);

    boolean isNotNullTypeParameter(KotlinTypeMarker kotlinTypeMarker);

    boolean isNothing(KotlinTypeMarker kotlinTypeMarker);

    boolean isNothingConstructor(TypeConstructorMarker typeConstructorMarker);

    boolean isNullableType(KotlinTypeMarker kotlinTypeMarker);

    boolean isOldCapturedType(CapturedTypeMarker capturedTypeMarker);

    boolean isPrimitiveType(SimpleTypeMarker simpleTypeMarker);

    boolean isProjectionNotNull(CapturedTypeMarker capturedTypeMarker);

    boolean isRawType(KotlinTypeMarker kotlinTypeMarker);

    boolean isSingleClassifierType(SimpleTypeMarker simpleTypeMarker);

    boolean isStarProjection(TypeArgumentMarker typeArgumentMarker);

    boolean isStubType(SimpleTypeMarker simpleTypeMarker);

    boolean isStubTypeForBuilderInference(SimpleTypeMarker simpleTypeMarker);

    boolean isTypeVariableType(KotlinTypeMarker kotlinTypeMarker);

    SimpleTypeMarker lowerBound(FlexibleTypeMarker flexibleTypeMarker);

    SimpleTypeMarker lowerBoundIfFlexible(KotlinTypeMarker kotlinTypeMarker);

    KotlinTypeMarker lowerType(CapturedTypeMarker capturedTypeMarker);

    KotlinTypeMarker makeDefinitelyNotNullOrNotNull(KotlinTypeMarker kotlinTypeMarker);

    SimpleTypeMarker original(DefinitelyNotNullTypeMarker definitelyNotNullTypeMarker);

    SimpleTypeMarker originalIfDefinitelyNotNullable(SimpleTypeMarker simpleTypeMarker);

    int parametersCount(TypeConstructorMarker typeConstructorMarker);

    Collection<KotlinTypeMarker> possibleIntegerTypes(SimpleTypeMarker simpleTypeMarker);

    TypeArgumentMarker projection(CapturedTypeConstructorMarker capturedTypeConstructorMarker);

    int size(TypeArgumentListMarker typeArgumentListMarker);

    TypeCheckerState.SupertypesPolicy substitutionSupertypePolicy(SimpleTypeMarker simpleTypeMarker);

    Collection<KotlinTypeMarker> supertypes(TypeConstructorMarker typeConstructorMarker);

    CapturedTypeConstructorMarker typeConstructor(CapturedTypeMarker capturedTypeMarker);

    TypeConstructorMarker typeConstructor(KotlinTypeMarker kotlinTypeMarker);

    TypeConstructorMarker typeConstructor(SimpleTypeMarker simpleTypeMarker);

    SimpleTypeMarker upperBound(FlexibleTypeMarker flexibleTypeMarker);

    SimpleTypeMarker upperBoundIfFlexible(KotlinTypeMarker kotlinTypeMarker);

    KotlinTypeMarker withNullability(KotlinTypeMarker kotlinTypeMarker, boolean z);

    SimpleTypeMarker withNullability(SimpleTypeMarker simpleTypeMarker, boolean z);
}
