package kotlin.reflect.jvm.internal.impl.types;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.types.model.FlexibleTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.model.KotlinTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.model.SimpleTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.model.TypeArgumentMarker;
import kotlin.reflect.jvm.internal.impl.types.model.TypeSystemContext;

/* JADX INFO: compiled from: AbstractStrictEqualityTypeChecker.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class AbstractStrictEqualityTypeChecker {
    public static final AbstractStrictEqualityTypeChecker INSTANCE = new AbstractStrictEqualityTypeChecker();

    private AbstractStrictEqualityTypeChecker() {
    }

    public final boolean strictEqualTypes(TypeSystemContext context, KotlinTypeMarker a, KotlinTypeMarker b) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(a, "a");
        Intrinsics.checkNotNullParameter(b, "b");
        return strictEqualTypesInternal(context, a, b);
    }

    private final boolean strictEqualTypesInternal(TypeSystemContext typeSystemContext, KotlinTypeMarker kotlinTypeMarker, KotlinTypeMarker kotlinTypeMarker2) {
        if (kotlinTypeMarker == kotlinTypeMarker2) {
            return true;
        }
        SimpleTypeMarker simpleTypeMarkerAsSimpleType = typeSystemContext.asSimpleType(kotlinTypeMarker);
        SimpleTypeMarker simpleTypeMarkerAsSimpleType2 = typeSystemContext.asSimpleType(kotlinTypeMarker2);
        if (simpleTypeMarkerAsSimpleType != null && simpleTypeMarkerAsSimpleType2 != null) {
            return strictEqualSimpleTypes(typeSystemContext, simpleTypeMarkerAsSimpleType, simpleTypeMarkerAsSimpleType2);
        }
        FlexibleTypeMarker flexibleTypeMarkerAsFlexibleType = typeSystemContext.asFlexibleType(kotlinTypeMarker);
        FlexibleTypeMarker flexibleTypeMarkerAsFlexibleType2 = typeSystemContext.asFlexibleType(kotlinTypeMarker2);
        if (flexibleTypeMarkerAsFlexibleType == null || flexibleTypeMarkerAsFlexibleType2 == null) {
            return false;
        }
        return strictEqualSimpleTypes(typeSystemContext, typeSystemContext.lowerBound(flexibleTypeMarkerAsFlexibleType), typeSystemContext.lowerBound(flexibleTypeMarkerAsFlexibleType2)) && strictEqualSimpleTypes(typeSystemContext, typeSystemContext.upperBound(flexibleTypeMarkerAsFlexibleType), typeSystemContext.upperBound(flexibleTypeMarkerAsFlexibleType2));
    }

    private final boolean strictEqualSimpleTypes(TypeSystemContext typeSystemContext, SimpleTypeMarker simpleTypeMarker, SimpleTypeMarker simpleTypeMarker2) {
        SimpleTypeMarker simpleTypeMarker3 = simpleTypeMarker;
        SimpleTypeMarker simpleTypeMarker4 = simpleTypeMarker2;
        if (typeSystemContext.argumentsCount(simpleTypeMarker3) == typeSystemContext.argumentsCount(simpleTypeMarker4) && typeSystemContext.isMarkedNullable(simpleTypeMarker) == typeSystemContext.isMarkedNullable(simpleTypeMarker2)) {
            if ((typeSystemContext.asDefinitelyNotNullType(simpleTypeMarker) == null) == (typeSystemContext.asDefinitelyNotNullType(simpleTypeMarker2) == null) && typeSystemContext.areEqualTypeConstructors(typeSystemContext.typeConstructor(simpleTypeMarker), typeSystemContext.typeConstructor(simpleTypeMarker2))) {
                if (typeSystemContext.identicalArguments(simpleTypeMarker, simpleTypeMarker2)) {
                    return true;
                }
                int iArgumentsCount = typeSystemContext.argumentsCount(simpleTypeMarker3);
                for (int i = 0; i < iArgumentsCount; i++) {
                    TypeArgumentMarker argument = typeSystemContext.getArgument(simpleTypeMarker3, i);
                    TypeArgumentMarker argument2 = typeSystemContext.getArgument(simpleTypeMarker4, i);
                    if (typeSystemContext.isStarProjection(argument) != typeSystemContext.isStarProjection(argument2)) {
                        return false;
                    }
                    if (!typeSystemContext.isStarProjection(argument) && (typeSystemContext.getVariance(argument) != typeSystemContext.getVariance(argument2) || !strictEqualTypesInternal(typeSystemContext, typeSystemContext.getType(argument), typeSystemContext.getType(argument2)))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
