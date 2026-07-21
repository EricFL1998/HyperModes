package kotlin.reflect.jvm.internal.impl.types;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.types.model.ArgumentList;
import kotlin.reflect.jvm.internal.impl.types.model.CaptureStatus;
import kotlin.reflect.jvm.internal.impl.types.model.CapturedTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.model.DefinitelyNotNullTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.model.IntersectionTypeConstructorMarker;
import kotlin.reflect.jvm.internal.impl.types.model.KotlinTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.model.SimpleTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.model.TypeArgumentListMarker;
import kotlin.reflect.jvm.internal.impl.types.model.TypeArgumentMarker;
import kotlin.reflect.jvm.internal.impl.types.model.TypeConstructorMarker;
import kotlin.reflect.jvm.internal.impl.types.model.TypeParameterMarker;
import kotlin.reflect.jvm.internal.impl.types.model.TypeSystemContext;
import kotlin.reflect.jvm.internal.impl.types.model.TypeSystemInferenceExtensionContext;
import kotlin.reflect.jvm.internal.impl.types.model.TypeVariableTypeConstructorMarker;
import kotlin.reflect.jvm.internal.impl.types.model.TypeVariance;
import kotlin.reflect.jvm.internal.impl.utils.SmartList;

/* JADX INFO: compiled from: AbstractTypeChecker.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class AbstractTypeChecker {
    public static final AbstractTypeChecker INSTANCE = new AbstractTypeChecker();
    public static boolean RUN_SLOW_ASSERTIONS;

    /* JADX INFO: compiled from: AbstractTypeChecker.kt */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;

        static {
            int[] iArr = new int[TypeVariance.values().length];
            try {
                iArr[TypeVariance.INV.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[TypeVariance.OUT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[TypeVariance.IN.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            $EnumSwitchMapping$0 = iArr;
            int[] iArr2 = new int[TypeCheckerState.LowerCapturedTypePolicy.values().length];
            try {
                iArr2[TypeCheckerState.LowerCapturedTypePolicy.CHECK_ONLY_LOWER.ordinal()] = 1;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                iArr2[TypeCheckerState.LowerCapturedTypePolicy.CHECK_SUBTYPE_AND_LOWER.ordinal()] = 2;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                iArr2[TypeCheckerState.LowerCapturedTypePolicy.SKIP_LOWER.ordinal()] = 3;
            } catch (NoSuchFieldError unused6) {
            }
            $EnumSwitchMapping$1 = iArr2;
        }
    }

    public final boolean isSubtypeOf(TypeCheckerState state, KotlinTypeMarker subType, KotlinTypeMarker superType) {
        Intrinsics.checkNotNullParameter(state, "state");
        Intrinsics.checkNotNullParameter(subType, "subType");
        Intrinsics.checkNotNullParameter(superType, "superType");
        return isSubtypeOf$default(this, state, subType, superType, false, 8, null);
    }

    private AbstractTypeChecker() {
    }

    public static /* synthetic */ boolean isSubtypeOf$default(AbstractTypeChecker abstractTypeChecker, TypeCheckerState typeCheckerState, KotlinTypeMarker kotlinTypeMarker, KotlinTypeMarker kotlinTypeMarker2, boolean z, int i, Object obj) {
        if ((i & 8) != 0) {
            z = false;
        }
        return abstractTypeChecker.isSubtypeOf(typeCheckerState, kotlinTypeMarker, kotlinTypeMarker2, z);
    }

    public final boolean isSubtypeOf(TypeCheckerState state, KotlinTypeMarker subType, KotlinTypeMarker superType, boolean z) {
        Intrinsics.checkNotNullParameter(state, "state");
        Intrinsics.checkNotNullParameter(subType, "subType");
        Intrinsics.checkNotNullParameter(superType, "superType");
        if (subType == superType) {
            return true;
        }
        if (state.customIsSubtypeOf(subType, superType)) {
            return completeIsSubTypeOf(state, subType, superType, z);
        }
        return false;
    }

    public final boolean equalTypes(TypeCheckerState state, KotlinTypeMarker a, KotlinTypeMarker b) {
        Intrinsics.checkNotNullParameter(state, "state");
        Intrinsics.checkNotNullParameter(a, "a");
        Intrinsics.checkNotNullParameter(b, "b");
        TypeSystemContext typeSystemContext = state.getTypeSystemContext();
        if (a == b) {
            return true;
        }
        AbstractTypeChecker abstractTypeChecker = INSTANCE;
        if (abstractTypeChecker.isCommonDenotableType(typeSystemContext, a) && abstractTypeChecker.isCommonDenotableType(typeSystemContext, b)) {
            KotlinTypeMarker kotlinTypeMarkerPrepareType = state.prepareType(state.refineType(a));
            KotlinTypeMarker kotlinTypeMarkerPrepareType2 = state.prepareType(state.refineType(b));
            SimpleTypeMarker simpleTypeMarkerLowerBoundIfFlexible = typeSystemContext.lowerBoundIfFlexible(kotlinTypeMarkerPrepareType);
            if (!typeSystemContext.areEqualTypeConstructors(typeSystemContext.typeConstructor(kotlinTypeMarkerPrepareType), typeSystemContext.typeConstructor(kotlinTypeMarkerPrepareType2))) {
                return false;
            }
            if (typeSystemContext.argumentsCount(simpleTypeMarkerLowerBoundIfFlexible) == 0) {
                return typeSystemContext.hasFlexibleNullability(kotlinTypeMarkerPrepareType) || typeSystemContext.hasFlexibleNullability(kotlinTypeMarkerPrepareType2) || typeSystemContext.isMarkedNullable(simpleTypeMarkerLowerBoundIfFlexible) == typeSystemContext.isMarkedNullable(typeSystemContext.lowerBoundIfFlexible(kotlinTypeMarkerPrepareType2));
            }
        }
        return isSubtypeOf$default(abstractTypeChecker, state, a, b, false, 8, null) && isSubtypeOf$default(abstractTypeChecker, state, b, a, false, 8, null);
    }

    private final boolean completeIsSubTypeOf(TypeCheckerState typeCheckerState, KotlinTypeMarker kotlinTypeMarker, KotlinTypeMarker kotlinTypeMarker2, boolean z) {
        TypeSystemContext typeSystemContext = typeCheckerState.getTypeSystemContext();
        KotlinTypeMarker kotlinTypeMarkerPrepareType = typeCheckerState.prepareType(typeCheckerState.refineType(kotlinTypeMarker));
        KotlinTypeMarker kotlinTypeMarkerPrepareType2 = typeCheckerState.prepareType(typeCheckerState.refineType(kotlinTypeMarker2));
        AbstractTypeChecker abstractTypeChecker = INSTANCE;
        Boolean boolCheckSubtypeForSpecialCases = abstractTypeChecker.checkSubtypeForSpecialCases(typeCheckerState, typeSystemContext.lowerBoundIfFlexible(kotlinTypeMarkerPrepareType), typeSystemContext.upperBoundIfFlexible(kotlinTypeMarkerPrepareType2));
        if (boolCheckSubtypeForSpecialCases != null) {
            boolean zBooleanValue = boolCheckSubtypeForSpecialCases.booleanValue();
            typeCheckerState.addSubtypeConstraint(kotlinTypeMarkerPrepareType, kotlinTypeMarkerPrepareType2, z);
            return zBooleanValue;
        }
        Boolean boolAddSubtypeConstraint = typeCheckerState.addSubtypeConstraint(kotlinTypeMarkerPrepareType, kotlinTypeMarkerPrepareType2, z);
        return boolAddSubtypeConstraint != null ? boolAddSubtypeConstraint.booleanValue() : abstractTypeChecker.isSubtypeOfForSingleClassifierType(typeCheckerState, typeSystemContext.lowerBoundIfFlexible(kotlinTypeMarkerPrepareType), typeSystemContext.upperBoundIfFlexible(kotlinTypeMarkerPrepareType2));
    }

    private final Boolean checkSubtypeForIntegerLiteralType(TypeCheckerState typeCheckerState, SimpleTypeMarker simpleTypeMarker, SimpleTypeMarker simpleTypeMarker2) {
        TypeSystemContext typeSystemContext = typeCheckerState.getTypeSystemContext();
        if (!typeSystemContext.isIntegerLiteralType(simpleTypeMarker) && !typeSystemContext.isIntegerLiteralType(simpleTypeMarker2)) {
            return null;
        }
        if (checkSubtypeForIntegerLiteralType$lambda$7$isIntegerLiteralTypeOrCapturedOne(typeSystemContext, simpleTypeMarker) && checkSubtypeForIntegerLiteralType$lambda$7$isIntegerLiteralTypeOrCapturedOne(typeSystemContext, simpleTypeMarker2)) {
            return true;
        }
        if (typeSystemContext.isIntegerLiteralType(simpleTypeMarker)) {
            if (checkSubtypeForIntegerLiteralType$lambda$7$isTypeInIntegerLiteralType(typeSystemContext, typeCheckerState, simpleTypeMarker, simpleTypeMarker2, false)) {
                return true;
            }
        } else if (typeSystemContext.isIntegerLiteralType(simpleTypeMarker2) && (checkSubtypeForIntegerLiteralType$lambda$7$isIntegerLiteralTypeInIntersectionComponents(typeSystemContext, simpleTypeMarker) || checkSubtypeForIntegerLiteralType$lambda$7$isTypeInIntegerLiteralType(typeSystemContext, typeCheckerState, simpleTypeMarker2, simpleTypeMarker, true))) {
            return true;
        }
        return null;
    }

    private static final boolean checkSubtypeForIntegerLiteralType$lambda$7$isTypeInIntegerLiteralType(TypeSystemContext typeSystemContext, TypeCheckerState typeCheckerState, SimpleTypeMarker simpleTypeMarker, SimpleTypeMarker simpleTypeMarker2, boolean z) {
        Collection<KotlinTypeMarker> collectionPossibleIntegerTypes = typeSystemContext.possibleIntegerTypes(simpleTypeMarker);
        if ((collectionPossibleIntegerTypes instanceof Collection) && collectionPossibleIntegerTypes.isEmpty()) {
            return false;
        }
        for (KotlinTypeMarker kotlinTypeMarker : collectionPossibleIntegerTypes) {
            if (Intrinsics.areEqual(typeSystemContext.typeConstructor(kotlinTypeMarker), typeSystemContext.typeConstructor(simpleTypeMarker2)) || (z && isSubtypeOf$default(INSTANCE, typeCheckerState, simpleTypeMarker2, kotlinTypeMarker, false, 8, null))) {
                return true;
            }
        }
        return false;
    }

    private static final boolean checkSubtypeForIntegerLiteralType$lambda$7$isIntegerLiteralTypeInIntersectionComponents(TypeSystemContext typeSystemContext, SimpleTypeMarker simpleTypeMarker) {
        TypeConstructorMarker typeConstructorMarkerTypeConstructor = typeSystemContext.typeConstructor(simpleTypeMarker);
        if (typeConstructorMarkerTypeConstructor instanceof IntersectionTypeConstructorMarker) {
            Collection<KotlinTypeMarker> collectionSupertypes = typeSystemContext.supertypes(typeConstructorMarkerTypeConstructor);
            if (!(collectionSupertypes instanceof Collection) || !collectionSupertypes.isEmpty()) {
                Iterator<T> it = collectionSupertypes.iterator();
                while (it.hasNext()) {
                    SimpleTypeMarker simpleTypeMarkerAsSimpleType = typeSystemContext.asSimpleType((KotlinTypeMarker) it.next());
                    if (simpleTypeMarkerAsSimpleType != null && typeSystemContext.isIntegerLiteralType(simpleTypeMarkerAsSimpleType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static final boolean checkSubtypeForIntegerLiteralType$lambda$7$isCapturedIntegerLiteralType(TypeSystemContext typeSystemContext, SimpleTypeMarker simpleTypeMarker) {
        if (!(simpleTypeMarker instanceof CapturedTypeMarker)) {
            return false;
        }
        TypeArgumentMarker typeArgumentMarkerProjection = typeSystemContext.projection(typeSystemContext.typeConstructor((CapturedTypeMarker) simpleTypeMarker));
        return !typeSystemContext.isStarProjection(typeArgumentMarkerProjection) && typeSystemContext.isIntegerLiteralType(typeSystemContext.upperBoundIfFlexible(typeSystemContext.getType(typeArgumentMarkerProjection)));
    }

    private static final boolean checkSubtypeForIntegerLiteralType$lambda$7$isIntegerLiteralTypeOrCapturedOne(TypeSystemContext typeSystemContext, SimpleTypeMarker simpleTypeMarker) {
        return typeSystemContext.isIntegerLiteralType(simpleTypeMarker) || checkSubtypeForIntegerLiteralType$lambda$7$isCapturedIntegerLiteralType(typeSystemContext, simpleTypeMarker);
    }

    private final boolean hasNothingSupertype(TypeCheckerState typeCheckerState, SimpleTypeMarker simpleTypeMarker) {
        TypeCheckerState.SupertypesPolicy.LowerIfFlexible lowerIfFlexible;
        TypeSystemContext typeSystemContext = typeCheckerState.getTypeSystemContext();
        TypeConstructorMarker typeConstructorMarkerTypeConstructor = typeSystemContext.typeConstructor(simpleTypeMarker);
        if (typeSystemContext.isClassTypeConstructor(typeConstructorMarkerTypeConstructor)) {
            return typeSystemContext.isNothingConstructor(typeConstructorMarkerTypeConstructor);
        }
        if (typeSystemContext.isNothingConstructor(typeSystemContext.typeConstructor(simpleTypeMarker))) {
            return true;
        }
        typeCheckerState.initialize();
        ArrayDeque<SimpleTypeMarker> supertypesDeque = typeCheckerState.getSupertypesDeque();
        Intrinsics.checkNotNull(supertypesDeque);
        Set<SimpleTypeMarker> supertypesSet = typeCheckerState.getSupertypesSet();
        Intrinsics.checkNotNull(supertypesSet);
        supertypesDeque.push(simpleTypeMarker);
        while (!supertypesDeque.isEmpty()) {
            if (supertypesSet.size() > 1000) {
                throw new IllegalStateException(("Too many supertypes for type: " + simpleTypeMarker + ". Supertypes = " + CollectionsKt.joinToString$default(supertypesSet, null, null, null, 0, null, null, 63, null)).toString());
            }
            SimpleTypeMarker simpleTypeMarkerPop = supertypesDeque.pop();
            Intrinsics.checkNotNull(simpleTypeMarkerPop);
            if (supertypesSet.add(simpleTypeMarkerPop)) {
                if (typeSystemContext.isClassType(simpleTypeMarkerPop)) {
                    lowerIfFlexible = TypeCheckerState.SupertypesPolicy.None.INSTANCE;
                } else {
                    lowerIfFlexible = TypeCheckerState.SupertypesPolicy.LowerIfFlexible.INSTANCE;
                }
                if (!(!Intrinsics.areEqual(lowerIfFlexible, TypeCheckerState.SupertypesPolicy.None.INSTANCE))) {
                    lowerIfFlexible = null;
                }
                if (lowerIfFlexible == null) {
                    continue;
                } else {
                    TypeSystemContext typeSystemContext2 = typeCheckerState.getTypeSystemContext();
                    Iterator<KotlinTypeMarker> it = typeSystemContext2.supertypes(typeSystemContext2.typeConstructor(simpleTypeMarkerPop)).iterator();
                    while (it.hasNext()) {
                        SimpleTypeMarker simpleTypeMarkerMo1495transformType = lowerIfFlexible.mo1495transformType(typeCheckerState, it.next());
                        if (typeSystemContext.isNothingConstructor(typeSystemContext.typeConstructor(simpleTypeMarkerMo1495transformType))) {
                            typeCheckerState.clear();
                            return true;
                        }
                        supertypesDeque.add(simpleTypeMarkerMo1495transformType);
                    }
                }
            }
        }
        typeCheckerState.clear();
        return false;
    }

    /* JADX WARN: Code duplicated, block: B:105:0x00fc A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:44:0x00d0  */
    /* JADX WARN: Code duplicated, block: B:47:0x00e7  */
    /* JADX WARN: Code duplicated, block: B:50:0x00fb  */
    private final boolean isSubtypeOfForSingleClassifierType(final TypeCheckerState typeCheckerState, SimpleTypeMarker simpleTypeMarker, final SimpleTypeMarker simpleTypeMarker2) {
        ArrayList arrayList;
        final ArrayList linkedHashSet;
        SimpleTypeMarker simpleTypeMarkerAsSimpleType;
        KotlinTypeMarker type;
        final TypeSystemContext typeSystemContext = typeCheckerState.getTypeSystemContext();
        if (RUN_SLOW_ASSERTIONS) {
            if (!typeSystemContext.isSingleClassifierType(simpleTypeMarker) && !typeSystemContext.isIntersection(typeSystemContext.typeConstructor(simpleTypeMarker))) {
                typeCheckerState.isAllowedTypeVariable(simpleTypeMarker);
            }
            if (!typeSystemContext.isSingleClassifierType(simpleTypeMarker2)) {
                typeCheckerState.isAllowedTypeVariable(simpleTypeMarker2);
            }
        }
        boolean z = false;
        if (!AbstractNullabilityChecker.INSTANCE.isPossibleSubtype(typeCheckerState, simpleTypeMarker, simpleTypeMarker2)) {
            return false;
        }
        AbstractTypeChecker abstractTypeChecker = INSTANCE;
        SimpleTypeMarker simpleTypeMarker3 = simpleTypeMarker;
        SimpleTypeMarker simpleTypeMarker4 = simpleTypeMarker2;
        Boolean boolCheckSubtypeForIntegerLiteralType = abstractTypeChecker.checkSubtypeForIntegerLiteralType(typeCheckerState, typeSystemContext.lowerBoundIfFlexible(simpleTypeMarker3), typeSystemContext.upperBoundIfFlexible(simpleTypeMarker4));
        if (boolCheckSubtypeForIntegerLiteralType != null) {
            boolean zBooleanValue = boolCheckSubtypeForIntegerLiteralType.booleanValue();
            TypeCheckerState.addSubtypeConstraint$default(typeCheckerState, simpleTypeMarker3, simpleTypeMarker4, false, 4, null);
            return zBooleanValue;
        }
        TypeConstructorMarker typeConstructorMarkerTypeConstructor = typeSystemContext.typeConstructor(simpleTypeMarker2);
        boolean z2 = true;
        if ((typeSystemContext.areEqualTypeConstructors(typeSystemContext.typeConstructor(simpleTypeMarker), typeConstructorMarkerTypeConstructor) && typeSystemContext.parametersCount(typeConstructorMarkerTypeConstructor) == 0) || typeSystemContext.isAnyConstructor(typeSystemContext.typeConstructor(simpleTypeMarker2))) {
            return true;
        }
        List<SimpleTypeMarker> listFindCorrespondingSupertypes = abstractTypeChecker.findCorrespondingSupertypes(typeCheckerState, simpleTypeMarker, typeConstructorMarkerTypeConstructor);
        int i = 10;
        if (listFindCorrespondingSupertypes.size() > 1) {
            TypeSystemContext typeSystemContext2 = typeCheckerState.getTypeSystemContext();
            TypeSystemInferenceExtensionContext typeSystemInferenceExtensionContext = typeSystemContext2 instanceof TypeSystemInferenceExtensionContext ? (TypeSystemInferenceExtensionContext) typeSystemContext2 : null;
            if (typeSystemInferenceExtensionContext == null || !typeSystemInferenceExtensionContext.isK2()) {
                List<SimpleTypeMarker> list = listFindCorrespondingSupertypes;
                arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
                for (SimpleTypeMarker simpleTypeMarker5 : list) {
                    simpleTypeMarkerAsSimpleType = typeSystemContext.asSimpleType(typeCheckerState.prepareType(simpleTypeMarker5));
                    if (simpleTypeMarkerAsSimpleType == null) {
                        simpleTypeMarker5 = simpleTypeMarkerAsSimpleType;
                    }
                    arrayList.add(simpleTypeMarker5);
                }
                linkedHashSet = arrayList;
            } else {
                linkedHashSet = new LinkedHashSet();
                for (SimpleTypeMarker simpleTypeMarker6 : listFindCorrespondingSupertypes) {
                    SimpleTypeMarker simpleTypeMarkerAsSimpleType2 = typeSystemContext.asSimpleType(typeCheckerState.prepareType(simpleTypeMarker6));
                    if (simpleTypeMarkerAsSimpleType2 != null) {
                        simpleTypeMarker6 = simpleTypeMarkerAsSimpleType2;
                    }
                    linkedHashSet.add(simpleTypeMarker6);
                }
            }
        } else {
            List<SimpleTypeMarker> list2 = listFindCorrespondingSupertypes;
            arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list2, 10));
            while (r4.hasNext()) {
                simpleTypeMarkerAsSimpleType = typeSystemContext.asSimpleType(typeCheckerState.prepareType(simpleTypeMarker5));
                if (simpleTypeMarkerAsSimpleType == null) {
                    simpleTypeMarker5 = simpleTypeMarkerAsSimpleType;
                }
                arrayList.add(simpleTypeMarker5);
            }
            linkedHashSet = arrayList;
        }
        int size = linkedHashSet.size();
        if (size == 0) {
            return INSTANCE.hasNothingSupertype(typeCheckerState, simpleTypeMarker);
        }
        if (size == 1) {
            return INSTANCE.isSubtypeForSameConstructor(typeCheckerState, typeSystemContext.asArgumentList((SimpleTypeMarker) CollectionsKt.first(linkedHashSet)), simpleTypeMarker2);
        }
        ArgumentList argumentList = new ArgumentList(typeSystemContext.parametersCount(typeConstructorMarkerTypeConstructor));
        int iParametersCount = typeSystemContext.parametersCount(typeConstructorMarkerTypeConstructor);
        int i2 = 0;
        boolean z3 = false;
        while (i2 < iParametersCount) {
            z3 = (z3 || typeSystemContext.getVariance(typeSystemContext.getParameter(typeConstructorMarkerTypeConstructor, i2)) != TypeVariance.OUT) ? z2 : z;
            if (!z3) {
                Collection<SimpleTypeMarker> collection = linkedHashSet;
                ArrayList arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(collection, i));
                for (SimpleTypeMarker simpleTypeMarker7 : collection) {
                    TypeArgumentMarker argumentOrNull = typeSystemContext.getArgumentOrNull(simpleTypeMarker7, i2);
                    if (argumentOrNull != null) {
                        if (typeSystemContext.getVariance(argumentOrNull) != TypeVariance.INV) {
                            argumentOrNull = null;
                        }
                        if (argumentOrNull != null && (type = typeSystemContext.getType(argumentOrNull)) != null) {
                            arrayList2.add(type);
                        }
                    }
                    throw new IllegalStateException(("Incorrect type: " + simpleTypeMarker7 + ", subType: " + simpleTypeMarker + ", superType: " + simpleTypeMarker2).toString());
                }
                argumentList.add(typeSystemContext.asTypeArgument(typeSystemContext.intersectTypes(arrayList2)));
            }
            i2++;
            z = false;
            z2 = true;
            i = 10;
        }
        if (z3 || !INSTANCE.isSubtypeForSameConstructor(typeCheckerState, argumentList, simpleTypeMarker2)) {
            return typeCheckerState.runForkingPoint(new Function1(linkedHashSet, typeCheckerState, typeSystemContext, simpleTypeMarker2) { // from class: kotlin.reflect.jvm.internal.impl.types.AbstractTypeChecker$$Lambda$0
                private final Collection arg$0;
                private final TypeCheckerState arg$1;
                private final TypeSystemContext arg$2;
                private final SimpleTypeMarker arg$3;

                {
                    this.arg$0 = linkedHashSet;
                    this.arg$1 = typeCheckerState;
                    this.arg$2 = typeSystemContext;
                    this.arg$3 = simpleTypeMarker2;
                }

                @Override // kotlin.jvm.functions.Function1
                public Object invoke(Object obj) {
                    return AbstractTypeChecker.isSubtypeOfForSingleClassifierType$lambda$21$lambda$20(this.arg$0, this.arg$1, this.arg$2, this.arg$3, (TypeCheckerState.ForkPointContext) obj);
                }
            });
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit isSubtypeOfForSingleClassifierType$lambda$21$lambda$20(Collection supertypesWithSameConstructor, final TypeCheckerState state, final TypeSystemContext this_with, final SimpleTypeMarker superType, TypeCheckerState.ForkPointContext runForkingPoint) {
        Intrinsics.checkNotNullParameter(supertypesWithSameConstructor, "$supertypesWithSameConstructor");
        Intrinsics.checkNotNullParameter(state, "$state");
        Intrinsics.checkNotNullParameter(this_with, "$this_with");
        Intrinsics.checkNotNullParameter(superType, "$superType");
        Intrinsics.checkNotNullParameter(runForkingPoint, "$this$runForkingPoint");
        Iterator it = supertypesWithSameConstructor.iterator();
        while (it.hasNext()) {
            final SimpleTypeMarker simpleTypeMarker = (SimpleTypeMarker) it.next();
            runForkingPoint.fork(new Function0(state, this_with, simpleTypeMarker, superType) { // from class: kotlin.reflect.jvm.internal.impl.types.AbstractTypeChecker$$Lambda$1
                private final TypeCheckerState arg$0;
                private final TypeSystemContext arg$1;
                private final SimpleTypeMarker arg$2;
                private final SimpleTypeMarker arg$3;

                {
                    this.arg$0 = state;
                    this.arg$1 = this_with;
                    this.arg$2 = simpleTypeMarker;
                    this.arg$3 = superType;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return Boolean.valueOf(AbstractTypeChecker.isSubtypeOfForSingleClassifierType$lambda$21$lambda$20$lambda$19(this.arg$0, this.arg$1, this.arg$2, this.arg$3));
                }
            });
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean isSubtypeOfForSingleClassifierType$lambda$21$lambda$20$lambda$19(TypeCheckerState state, TypeSystemContext this_with, SimpleTypeMarker subTypeArguments, SimpleTypeMarker superType) {
        Intrinsics.checkNotNullParameter(state, "$state");
        Intrinsics.checkNotNullParameter(this_with, "$this_with");
        Intrinsics.checkNotNullParameter(subTypeArguments, "$subTypeArguments");
        Intrinsics.checkNotNullParameter(superType, "$superType");
        return INSTANCE.isSubtypeForSameConstructor(state, this_with.asArgumentList(subTypeArguments), superType);
    }

    private final boolean isTypeVariableAgainstStarProjectionForSelfType(TypeSystemContext typeSystemContext, KotlinTypeMarker kotlinTypeMarker, KotlinTypeMarker kotlinTypeMarker2, TypeConstructorMarker typeConstructorMarker) {
        TypeParameterMarker typeParameter;
        SimpleTypeMarker simpleTypeMarkerAsSimpleType = typeSystemContext.asSimpleType(kotlinTypeMarker);
        if (!(simpleTypeMarkerAsSimpleType instanceof CapturedTypeMarker)) {
            return false;
        }
        CapturedTypeMarker capturedTypeMarker = (CapturedTypeMarker) simpleTypeMarkerAsSimpleType;
        if (typeSystemContext.isOldCapturedType(capturedTypeMarker) || !typeSystemContext.isStarProjection(typeSystemContext.projection(typeSystemContext.typeConstructor(capturedTypeMarker))) || typeSystemContext.captureStatus(capturedTypeMarker) != CaptureStatus.FOR_SUBTYPING) {
            return false;
        }
        TypeConstructorMarker typeConstructorMarkerTypeConstructor = typeSystemContext.typeConstructor(kotlinTypeMarker2);
        TypeVariableTypeConstructorMarker typeVariableTypeConstructorMarker = typeConstructorMarkerTypeConstructor instanceof TypeVariableTypeConstructorMarker ? (TypeVariableTypeConstructorMarker) typeConstructorMarkerTypeConstructor : null;
        return (typeVariableTypeConstructorMarker == null || (typeParameter = typeSystemContext.getTypeParameter(typeVariableTypeConstructorMarker)) == null || !typeSystemContext.hasRecursiveBounds(typeParameter, typeConstructorMarker)) ? false : true;
    }

    public final boolean isSubtypeForSameConstructor(TypeCheckerState typeCheckerState, TypeArgumentListMarker capturedSubArguments, SimpleTypeMarker superType) {
        boolean zEqualTypes;
        Intrinsics.checkNotNullParameter(typeCheckerState, "<this>");
        Intrinsics.checkNotNullParameter(capturedSubArguments, "capturedSubArguments");
        Intrinsics.checkNotNullParameter(superType, "superType");
        TypeSystemContext typeSystemContext = typeCheckerState.getTypeSystemContext();
        TypeConstructorMarker typeConstructorMarkerTypeConstructor = typeSystemContext.typeConstructor(superType);
        int size = typeSystemContext.size(capturedSubArguments);
        int iParametersCount = typeSystemContext.parametersCount(typeConstructorMarkerTypeConstructor);
        if (size == iParametersCount) {
            SimpleTypeMarker simpleTypeMarker = superType;
            if (size == typeSystemContext.argumentsCount(simpleTypeMarker)) {
                for (int i = 0; i < iParametersCount; i++) {
                    TypeArgumentMarker argument = typeSystemContext.getArgument(simpleTypeMarker, i);
                    if (!typeSystemContext.isStarProjection(argument)) {
                        KotlinTypeMarker type = typeSystemContext.getType(argument);
                        TypeArgumentMarker typeArgumentMarker = typeSystemContext.get(capturedSubArguments, i);
                        typeSystemContext.getVariance(typeArgumentMarker);
                        TypeVariance typeVariance = TypeVariance.INV;
                        KotlinTypeMarker type2 = typeSystemContext.getType(typeArgumentMarker);
                        AbstractTypeChecker abstractTypeChecker = INSTANCE;
                        TypeVariance typeVarianceEffectiveVariance = abstractTypeChecker.effectiveVariance(typeSystemContext.getVariance(typeSystemContext.getParameter(typeConstructorMarkerTypeConstructor, i)), typeSystemContext.getVariance(argument));
                        if (typeVarianceEffectiveVariance == null) {
                            return typeCheckerState.isErrorTypeEqualsToAnything();
                        }
                        if (typeVarianceEffectiveVariance != TypeVariance.INV || (!abstractTypeChecker.isTypeVariableAgainstStarProjectionForSelfType(typeSystemContext, type2, type, typeConstructorMarkerTypeConstructor) && !abstractTypeChecker.isTypeVariableAgainstStarProjectionForSelfType(typeSystemContext, type, type2, typeConstructorMarkerTypeConstructor))) {
                            if (typeCheckerState.argumentsDepth <= 100) {
                                typeCheckerState.argumentsDepth++;
                                int i2 = WhenMappings.$EnumSwitchMapping$0[typeVarianceEffectiveVariance.ordinal()];
                                if (i2 == 1) {
                                    zEqualTypes = abstractTypeChecker.equalTypes(typeCheckerState, type2, type);
                                } else if (i2 == 2) {
                                    zEqualTypes = isSubtypeOf$default(abstractTypeChecker, typeCheckerState, type2, type, false, 8, null);
                                } else {
                                    if (i2 != 3) {
                                        throw new NoWhenBranchMatchedException();
                                    }
                                    zEqualTypes = isSubtypeOf$default(abstractTypeChecker, typeCheckerState, type, type2, false, 8, null);
                                }
                                typeCheckerState.argumentsDepth--;
                                if (!zEqualTypes) {
                                    return false;
                                }
                            } else {
                                throw new IllegalStateException(("Arguments depth is too high. Some related argument: " + type2).toString());
                            }
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private final boolean isCommonDenotableType(TypeSystemContext typeSystemContext, KotlinTypeMarker kotlinTypeMarker) {
        return (!typeSystemContext.isDenotable(typeSystemContext.typeConstructor(kotlinTypeMarker)) || typeSystemContext.isDynamic(kotlinTypeMarker) || typeSystemContext.isDefinitelyNotNullType(kotlinTypeMarker) || typeSystemContext.isNotNullTypeParameter(kotlinTypeMarker) || typeSystemContext.isFlexibleWithDifferentTypeConstructors(kotlinTypeMarker)) ? false : true;
    }

    public final TypeVariance effectiveVariance(TypeVariance declared, TypeVariance useSite) {
        Intrinsics.checkNotNullParameter(declared, "declared");
        Intrinsics.checkNotNullParameter(useSite, "useSite");
        if (declared == TypeVariance.INV) {
            return useSite;
        }
        if (useSite == TypeVariance.INV || declared == useSite) {
            return declared;
        }
        return null;
    }

    private final boolean isStubTypeSubtypeOfAnother(TypeSystemContext typeSystemContext, SimpleTypeMarker simpleTypeMarker, SimpleTypeMarker simpleTypeMarker2) {
        SimpleTypeMarker simpleTypeMarkerOriginal;
        SimpleTypeMarker simpleTypeMarkerOriginal2;
        DefinitelyNotNullTypeMarker definitelyNotNullTypeMarkerAsDefinitelyNotNullType = typeSystemContext.asDefinitelyNotNullType(simpleTypeMarker);
        if (definitelyNotNullTypeMarkerAsDefinitelyNotNullType == null || (simpleTypeMarkerOriginal = typeSystemContext.original(definitelyNotNullTypeMarkerAsDefinitelyNotNullType)) == null) {
            simpleTypeMarkerOriginal = simpleTypeMarker;
        }
        DefinitelyNotNullTypeMarker definitelyNotNullTypeMarkerAsDefinitelyNotNullType2 = typeSystemContext.asDefinitelyNotNullType(simpleTypeMarker2);
        if (definitelyNotNullTypeMarkerAsDefinitelyNotNullType2 == null || (simpleTypeMarkerOriginal2 = typeSystemContext.original(definitelyNotNullTypeMarkerAsDefinitelyNotNullType2)) == null) {
            simpleTypeMarkerOriginal2 = simpleTypeMarker2;
        }
        if (typeSystemContext.typeConstructor(simpleTypeMarkerOriginal) != typeSystemContext.typeConstructor(simpleTypeMarkerOriginal2)) {
            return false;
        }
        if (typeSystemContext.isDefinitelyNotNullType(simpleTypeMarker) || !typeSystemContext.isDefinitelyNotNullType(simpleTypeMarker2)) {
            return !typeSystemContext.isMarkedNullable(simpleTypeMarker) || typeSystemContext.isMarkedNullable(simpleTypeMarker2);
        }
        return false;
    }

    /* JADX WARN: Code duplicated, block: B:84:0x0145  */
    /* JADX WARN: Code duplicated, block: B:86:0x0151  */
    private final Boolean checkSubtypeForSpecialCases(TypeCheckerState typeCheckerState, SimpleTypeMarker simpleTypeMarker, SimpleTypeMarker simpleTypeMarker2) {
        SimpleTypeMarker simpleTypeMarkerOriginal;
        TypeParameterMarker typeParameterForArgumentInBaseIfItEqualToTarget;
        TypeSystemContext typeSystemContext = typeCheckerState.getTypeSystemContext();
        SimpleTypeMarker simpleTypeMarker3 = simpleTypeMarker;
        boolean z = false;
        if (!typeSystemContext.isError(simpleTypeMarker3)) {
            SimpleTypeMarker simpleTypeMarker4 = simpleTypeMarker2;
            if (!typeSystemContext.isError(simpleTypeMarker4)) {
                if (typeSystemContext.isStubTypeForBuilderInference(simpleTypeMarker) && typeSystemContext.isStubTypeForBuilderInference(simpleTypeMarker2)) {
                    return Boolean.valueOf(INSTANCE.isStubTypeSubtypeOfAnother(typeSystemContext, simpleTypeMarker, simpleTypeMarker2) || typeCheckerState.isStubTypeEqualsToAnything());
                }
                if (typeSystemContext.isStubType(simpleTypeMarker) || typeSystemContext.isStubType(simpleTypeMarker2)) {
                    return Boolean.valueOf(typeCheckerState.isStubTypeEqualsToAnything());
                }
                DefinitelyNotNullTypeMarker definitelyNotNullTypeMarkerAsDefinitelyNotNullType = typeSystemContext.asDefinitelyNotNullType(simpleTypeMarker2);
                if (definitelyNotNullTypeMarkerAsDefinitelyNotNullType == null || (simpleTypeMarkerOriginal = typeSystemContext.original(definitelyNotNullTypeMarkerAsDefinitelyNotNullType)) == null) {
                    simpleTypeMarkerOriginal = simpleTypeMarker2;
                }
                CapturedTypeMarker capturedTypeMarkerAsCapturedType = typeSystemContext.asCapturedType(simpleTypeMarkerOriginal);
                KotlinTypeMarker kotlinTypeMarkerLowerType = capturedTypeMarkerAsCapturedType != null ? typeSystemContext.lowerType(capturedTypeMarkerAsCapturedType) : null;
                if (capturedTypeMarkerAsCapturedType != null && kotlinTypeMarkerLowerType != null) {
                    if (typeSystemContext.isMarkedNullable(simpleTypeMarker2)) {
                        kotlinTypeMarkerLowerType = typeSystemContext.withNullability(kotlinTypeMarkerLowerType, true);
                    } else if (typeSystemContext.isDefinitelyNotNullType(simpleTypeMarker4)) {
                        kotlinTypeMarkerLowerType = typeSystemContext.makeDefinitelyNotNullOrNotNull(kotlinTypeMarkerLowerType);
                    }
                    KotlinTypeMarker kotlinTypeMarker = kotlinTypeMarkerLowerType;
                    int i = WhenMappings.$EnumSwitchMapping$1[typeCheckerState.getLowerCapturedTypePolicy(simpleTypeMarker, capturedTypeMarkerAsCapturedType).ordinal()];
                    if (i == 1) {
                        return Boolean.valueOf(isSubtypeOf$default(INSTANCE, typeCheckerState, simpleTypeMarker3, kotlinTypeMarker, false, 8, null));
                    }
                    if (i != 2) {
                        if (i != 3) {
                            throw new NoWhenBranchMatchedException();
                        }
                    } else if (isSubtypeOf$default(INSTANCE, typeCheckerState, simpleTypeMarker3, kotlinTypeMarker, false, 8, null)) {
                        return true;
                    }
                }
                TypeConstructorMarker typeConstructorMarkerTypeConstructor = typeSystemContext.typeConstructor(simpleTypeMarker2);
                if (typeSystemContext.isIntersection(typeConstructorMarkerTypeConstructor)) {
                    typeSystemContext.isMarkedNullable(simpleTypeMarker2);
                    Collection<KotlinTypeMarker> collectionSupertypes = typeSystemContext.supertypes(typeConstructorMarkerTypeConstructor);
                    if ((collectionSupertypes instanceof Collection) && collectionSupertypes.isEmpty()) {
                        z = true;
                    } else {
                        Iterator<T> it = collectionSupertypes.iterator();
                        while (it.hasNext()) {
                            if (!isSubtypeOf$default(INSTANCE, typeCheckerState, simpleTypeMarker3, (KotlinTypeMarker) it.next(), false, 8, null)) {
                            }
                        }
                        z = true;
                    }
                    return Boolean.valueOf(z);
                }
                TypeConstructorMarker typeConstructorMarkerTypeConstructor2 = typeSystemContext.typeConstructor(simpleTypeMarker);
                if (!(simpleTypeMarker instanceof CapturedTypeMarker)) {
                    if (typeSystemContext.isIntersection(typeConstructorMarkerTypeConstructor2)) {
                        Collection<KotlinTypeMarker> collectionSupertypes2 = typeSystemContext.supertypes(typeConstructorMarkerTypeConstructor2);
                        if ((collectionSupertypes2 instanceof Collection) && collectionSupertypes2.isEmpty()) {
                            typeParameterForArgumentInBaseIfItEqualToTarget = INSTANCE.getTypeParameterForArgumentInBaseIfItEqualToTarget(typeCheckerState.getTypeSystemContext(), simpleTypeMarker4, simpleTypeMarker3);
                            if (typeParameterForArgumentInBaseIfItEqualToTarget == null) {
                            }
                        } else {
                            Iterator<T> it2 = collectionSupertypes2.iterator();
                            while (it2.hasNext()) {
                                if (!(((KotlinTypeMarker) it2.next()) instanceof CapturedTypeMarker)) {
                                }
                            }
                            typeParameterForArgumentInBaseIfItEqualToTarget = INSTANCE.getTypeParameterForArgumentInBaseIfItEqualToTarget(typeCheckerState.getTypeSystemContext(), simpleTypeMarker4, simpleTypeMarker3);
                            if (typeParameterForArgumentInBaseIfItEqualToTarget == null) {
                            }
                        }
                    }
                } else {
                    typeParameterForArgumentInBaseIfItEqualToTarget = INSTANCE.getTypeParameterForArgumentInBaseIfItEqualToTarget(typeCheckerState.getTypeSystemContext(), simpleTypeMarker4, simpleTypeMarker3);
                    if (typeParameterForArgumentInBaseIfItEqualToTarget == null && typeSystemContext.hasRecursiveBounds(typeParameterForArgumentInBaseIfItEqualToTarget, typeSystemContext.typeConstructor(simpleTypeMarker2))) {
                        return true;
                    }
                }
                return null;
            }
        }
        if (typeCheckerState.isErrorTypeEqualsToAnything()) {
            return true;
        }
        if (!typeSystemContext.isMarkedNullable(simpleTypeMarker) || typeSystemContext.isMarkedNullable(simpleTypeMarker2)) {
            return Boolean.valueOf(AbstractStrictEqualityTypeChecker.INSTANCE.strictEqualTypes(typeSystemContext, typeSystemContext.withNullability(simpleTypeMarker, false), typeSystemContext.withNullability(simpleTypeMarker2, false)));
        }
        return false;
    }

    private final TypeParameterMarker getTypeParameterForArgumentInBaseIfItEqualToTarget(TypeSystemContext typeSystemContext, KotlinTypeMarker kotlinTypeMarker, KotlinTypeMarker kotlinTypeMarker2) {
        KotlinTypeMarker type;
        int iArgumentsCount = typeSystemContext.argumentsCount(kotlinTypeMarker);
        int i = 0;
        while (true) {
            if (i >= iArgumentsCount) {
                return null;
            }
            TypeArgumentMarker argument = typeSystemContext.getArgument(kotlinTypeMarker, i);
            TypeArgumentMarker typeArgumentMarker = typeSystemContext.isStarProjection(argument) ^ true ? argument : null;
            if (typeArgumentMarker != null && (type = typeSystemContext.getType(typeArgumentMarker)) != null) {
                boolean z = typeSystemContext.isCapturedType(typeSystemContext.originalIfDefinitelyNotNullable(typeSystemContext.lowerBoundIfFlexible(type))) && typeSystemContext.isCapturedType(typeSystemContext.originalIfDefinitelyNotNullable(typeSystemContext.lowerBoundIfFlexible(kotlinTypeMarker2)));
                if (Intrinsics.areEqual(type, kotlinTypeMarker2) || (z && Intrinsics.areEqual(typeSystemContext.typeConstructor(type), typeSystemContext.typeConstructor(kotlinTypeMarker2)))) {
                    return typeSystemContext.getParameter(typeSystemContext.typeConstructor(kotlinTypeMarker), i);
                }
                TypeParameterMarker typeParameterForArgumentInBaseIfItEqualToTarget = getTypeParameterForArgumentInBaseIfItEqualToTarget(typeSystemContext, type, kotlinTypeMarker2);
                if (typeParameterForArgumentInBaseIfItEqualToTarget != null) {
                    return typeParameterForArgumentInBaseIfItEqualToTarget;
                }
            }
            i++;
        }
    }

    private final List<SimpleTypeMarker> collectAllSupertypesWithGivenTypeConstructor(TypeCheckerState typeCheckerState, SimpleTypeMarker simpleTypeMarker, TypeConstructorMarker typeConstructorMarker) {
        TypeCheckerState.SupertypesPolicy.LowerIfFlexible lowerIfFlexibleSubstitutionSupertypePolicy;
        SimpleTypeMarker simpleTypeMarker2 = simpleTypeMarker;
        TypeSystemContext typeSystemContext = typeCheckerState.getTypeSystemContext();
        List<SimpleTypeMarker> listFastCorrespondingSupertypes = typeSystemContext.fastCorrespondingSupertypes(simpleTypeMarker2, typeConstructorMarker);
        if (listFastCorrespondingSupertypes != null) {
            return listFastCorrespondingSupertypes;
        }
        if (!typeSystemContext.isClassTypeConstructor(typeConstructorMarker) && typeSystemContext.isClassType(simpleTypeMarker2)) {
            return CollectionsKt.emptyList();
        }
        if (typeSystemContext.isCommonFinalClassConstructor(typeConstructorMarker)) {
            if (typeSystemContext.areEqualTypeConstructors(typeSystemContext.typeConstructor(simpleTypeMarker2), typeConstructorMarker)) {
                SimpleTypeMarker simpleTypeMarkerCaptureFromArguments = typeSystemContext.captureFromArguments(simpleTypeMarker2, CaptureStatus.FOR_SUBTYPING);
                if (simpleTypeMarkerCaptureFromArguments != null) {
                    simpleTypeMarker2 = simpleTypeMarkerCaptureFromArguments;
                }
                return CollectionsKt.listOf(simpleTypeMarker2);
            }
            return CollectionsKt.emptyList();
        }
        SmartList smartList = new SmartList();
        typeCheckerState.initialize();
        ArrayDeque<SimpleTypeMarker> supertypesDeque = typeCheckerState.getSupertypesDeque();
        Intrinsics.checkNotNull(supertypesDeque);
        Set<SimpleTypeMarker> supertypesSet = typeCheckerState.getSupertypesSet();
        Intrinsics.checkNotNull(supertypesSet);
        supertypesDeque.push(simpleTypeMarker2);
        while (!supertypesDeque.isEmpty()) {
            if (supertypesSet.size() > 1000) {
                throw new IllegalStateException(("Too many supertypes for type: " + simpleTypeMarker2 + ". Supertypes = " + CollectionsKt.joinToString$default(supertypesSet, null, null, null, 0, null, null, 63, null)).toString());
            }
            SimpleTypeMarker simpleTypeMarkerPop = supertypesDeque.pop();
            Intrinsics.checkNotNull(simpleTypeMarkerPop);
            if (supertypesSet.add(simpleTypeMarkerPop)) {
                SimpleTypeMarker simpleTypeMarkerCaptureFromArguments2 = typeSystemContext.captureFromArguments(simpleTypeMarkerPop, CaptureStatus.FOR_SUBTYPING);
                if (simpleTypeMarkerCaptureFromArguments2 == null) {
                    simpleTypeMarkerCaptureFromArguments2 = simpleTypeMarkerPop;
                }
                if (typeSystemContext.areEqualTypeConstructors(typeSystemContext.typeConstructor(simpleTypeMarkerCaptureFromArguments2), typeConstructorMarker)) {
                    smartList.add(simpleTypeMarkerCaptureFromArguments2);
                    lowerIfFlexibleSubstitutionSupertypePolicy = TypeCheckerState.SupertypesPolicy.None.INSTANCE;
                } else if (typeSystemContext.argumentsCount(simpleTypeMarkerCaptureFromArguments2) == 0) {
                    lowerIfFlexibleSubstitutionSupertypePolicy = TypeCheckerState.SupertypesPolicy.LowerIfFlexible.INSTANCE;
                } else {
                    lowerIfFlexibleSubstitutionSupertypePolicy = typeCheckerState.getTypeSystemContext().substitutionSupertypePolicy(simpleTypeMarkerCaptureFromArguments2);
                }
                if (!(!Intrinsics.areEqual(lowerIfFlexibleSubstitutionSupertypePolicy, TypeCheckerState.SupertypesPolicy.None.INSTANCE))) {
                    lowerIfFlexibleSubstitutionSupertypePolicy = null;
                }
                if (lowerIfFlexibleSubstitutionSupertypePolicy != null) {
                    TypeSystemContext typeSystemContext2 = typeCheckerState.getTypeSystemContext();
                    Iterator<KotlinTypeMarker> it = typeSystemContext2.supertypes(typeSystemContext2.typeConstructor(simpleTypeMarkerPop)).iterator();
                    while (it.hasNext()) {
                        supertypesDeque.add(lowerIfFlexibleSubstitutionSupertypePolicy.mo1495transformType(typeCheckerState, it.next()));
                    }
                }
            }
        }
        typeCheckerState.clear();
        return smartList;
    }

    private final List<SimpleTypeMarker> collectAndFilter(TypeCheckerState typeCheckerState, SimpleTypeMarker simpleTypeMarker, TypeConstructorMarker typeConstructorMarker) {
        return selectOnlyPureKotlinSupertypes(typeCheckerState, collectAllSupertypesWithGivenTypeConstructor(typeCheckerState, simpleTypeMarker, typeConstructorMarker));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final List<SimpleTypeMarker> selectOnlyPureKotlinSupertypes(TypeCheckerState typeCheckerState, List<? extends SimpleTypeMarker> list) {
        TypeSystemContext typeSystemContext = typeCheckerState.getTypeSystemContext();
        if (list.size() < 2) {
            return list;
        }
        ArrayList arrayList = new ArrayList();
        for (Object obj : list) {
            TypeArgumentListMarker typeArgumentListMarkerAsArgumentList = typeSystemContext.asArgumentList((SimpleTypeMarker) obj);
            int size = typeSystemContext.size(typeArgumentListMarkerAsArgumentList);
            int i = 0;
            while (true) {
                if (i < size) {
                    if (typeSystemContext.asFlexibleType(typeSystemContext.getType(typeSystemContext.get(typeArgumentListMarkerAsArgumentList, i))) != null) {
                        break;
                    }
                    i++;
                } else {
                    arrayList.add(obj);
                    break;
                }
            }
        }
        ArrayList arrayList2 = arrayList;
        return arrayList2.isEmpty() ^ true ? arrayList2 : list;
    }

    public final List<SimpleTypeMarker> findCorrespondingSupertypes(TypeCheckerState state, SimpleTypeMarker subType, TypeConstructorMarker superConstructor) {
        TypeCheckerState.SupertypesPolicy.LowerIfFlexible lowerIfFlexible;
        Intrinsics.checkNotNullParameter(state, "state");
        Intrinsics.checkNotNullParameter(subType, "subType");
        Intrinsics.checkNotNullParameter(superConstructor, "superConstructor");
        TypeSystemContext typeSystemContext = state.getTypeSystemContext();
        if (typeSystemContext.isClassType(subType)) {
            return INSTANCE.collectAndFilter(state, subType, superConstructor);
        }
        if (!typeSystemContext.isClassTypeConstructor(superConstructor) && !typeSystemContext.isIntegerLiteralTypeConstructor(superConstructor)) {
            return INSTANCE.collectAllSupertypesWithGivenTypeConstructor(state, subType, superConstructor);
        }
        SmartList<SimpleTypeMarker> smartList = new SmartList();
        state.initialize();
        ArrayDeque<SimpleTypeMarker> supertypesDeque = state.getSupertypesDeque();
        Intrinsics.checkNotNull(supertypesDeque);
        Set<SimpleTypeMarker> supertypesSet = state.getSupertypesSet();
        Intrinsics.checkNotNull(supertypesSet);
        supertypesDeque.push(subType);
        while (!supertypesDeque.isEmpty()) {
            if (supertypesSet.size() > 1000) {
                throw new IllegalStateException(("Too many supertypes for type: " + subType + ". Supertypes = " + CollectionsKt.joinToString$default(supertypesSet, null, null, null, 0, null, null, 63, null)).toString());
            }
            SimpleTypeMarker simpleTypeMarkerPop = supertypesDeque.pop();
            Intrinsics.checkNotNull(simpleTypeMarkerPop);
            if (supertypesSet.add(simpleTypeMarkerPop)) {
                if (typeSystemContext.isClassType(simpleTypeMarkerPop)) {
                    smartList.add(simpleTypeMarkerPop);
                    lowerIfFlexible = TypeCheckerState.SupertypesPolicy.None.INSTANCE;
                } else {
                    lowerIfFlexible = TypeCheckerState.SupertypesPolicy.LowerIfFlexible.INSTANCE;
                }
                if (!(!Intrinsics.areEqual(lowerIfFlexible, TypeCheckerState.SupertypesPolicy.None.INSTANCE))) {
                    lowerIfFlexible = null;
                }
                if (lowerIfFlexible != null) {
                    TypeSystemContext typeSystemContext2 = state.getTypeSystemContext();
                    Iterator<KotlinTypeMarker> it = typeSystemContext2.supertypes(typeSystemContext2.typeConstructor(simpleTypeMarkerPop)).iterator();
                    while (it.hasNext()) {
                        supertypesDeque.add(lowerIfFlexible.mo1495transformType(state, it.next()));
                    }
                }
            }
        }
        state.clear();
        ArrayList arrayList = new ArrayList();
        for (SimpleTypeMarker simpleTypeMarker : smartList) {
            AbstractTypeChecker abstractTypeChecker = INSTANCE;
            Intrinsics.checkNotNull(simpleTypeMarker);
            CollectionsKt.addAll(arrayList, abstractTypeChecker.collectAndFilter(state, simpleTypeMarker, superConstructor));
        }
        return arrayList;
    }
}
