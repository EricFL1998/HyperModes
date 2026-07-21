package kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap;
import kotlin.reflect.jvm.internal.impl.load.java.AbstractAnnotationTypeQualifierResolver;
import kotlin.reflect.jvm.internal.impl.load.java.AnnotationQualifierApplicabilityType;
import kotlin.reflect.jvm.internal.impl.load.java.JavaDefaultQualifiers;
import kotlin.reflect.jvm.internal.impl.load.java.JavaTypeQualifiersByElementType;
import kotlin.reflect.jvm.internal.impl.name.FqNameUnsafe;
import kotlin.reflect.jvm.internal.impl.types.model.KotlinTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.model.TypeArgumentMarker;
import kotlin.reflect.jvm.internal.impl.types.model.TypeConstructorMarker;
import kotlin.reflect.jvm.internal.impl.types.model.TypeParameterMarker;
import kotlin.reflect.jvm.internal.impl.types.model.TypeSystemContext;
import kotlin.reflect.jvm.internal.impl.types.model.TypeVariance;

/* JADX INFO: compiled from: AbstractSignatureParts.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class AbstractSignatureParts<TAnnotation> {
    public abstract boolean forceWarning(TAnnotation tannotation, KotlinTypeMarker kotlinTypeMarker);

    public abstract AbstractAnnotationTypeQualifierResolver<TAnnotation> getAnnotationTypeQualifierResolver();

    public abstract Iterable<TAnnotation> getAnnotations(KotlinTypeMarker kotlinTypeMarker);

    public abstract Iterable<TAnnotation> getContainerAnnotations();

    public abstract AnnotationQualifierApplicabilityType getContainerApplicabilityType();

    public abstract JavaTypeQualifiersByElementType getContainerDefaultTypeQualifiers();

    public abstract boolean getContainerIsVarargParameter();

    protected abstract NullabilityQualifierWithMigrationStatus getDefaultNullability(NullabilityQualifierWithMigrationStatus nullabilityQualifierWithMigrationStatus, JavaDefaultQualifiers javaDefaultQualifiers);

    public abstract boolean getEnableImprovementsInStrictMode();

    public abstract KotlinTypeMarker getEnhancedForWarnings(KotlinTypeMarker kotlinTypeMarker);

    public boolean getForceOnlyHeadTypeConstructor() {
        return false;
    }

    public abstract FqNameUnsafe getFqNameUnsafe(KotlinTypeMarker kotlinTypeMarker);

    public abstract boolean getSkipRawTypeArguments();

    public abstract TypeSystemContext getTypeSystem();

    public abstract boolean isArrayOrPrimitiveArray(KotlinTypeMarker kotlinTypeMarker);

    public abstract boolean isCovariant();

    public abstract boolean isEqual(KotlinTypeMarker kotlinTypeMarker, KotlinTypeMarker kotlinTypeMarker2);

    public abstract boolean isFromJava(TypeParameterMarker typeParameterMarker);

    public boolean isNotNullTypeParameterCompat(KotlinTypeMarker kotlinTypeMarker) {
        Intrinsics.checkNotNullParameter(kotlinTypeMarker, "<this>");
        return false;
    }

    private final NullabilityQualifier getNullabilityQualifier(KotlinTypeMarker kotlinTypeMarker) {
        TypeSystemContext typeSystem = getTypeSystem();
        if (typeSystem.isMarkedNullable(typeSystem.lowerBoundIfFlexible(kotlinTypeMarker))) {
            return NullabilityQualifier.NULLABLE;
        }
        if (typeSystem.isMarkedNullable(typeSystem.upperBoundIfFlexible(kotlinTypeMarker))) {
            return null;
        }
        return NullabilityQualifier.NOT_NULL;
    }

    private final JavaTypeQualifiers extractQualifiers(KotlinTypeMarker kotlinTypeMarker) {
        NullabilityQualifier nullabilityQualifier;
        NullabilityQualifier nullabilityQualifier2 = getNullabilityQualifier(kotlinTypeMarker);
        MutabilityQualifier mutabilityQualifier = null;
        if (nullabilityQualifier2 == null) {
            KotlinTypeMarker enhancedForWarnings = getEnhancedForWarnings(kotlinTypeMarker);
            nullabilityQualifier = enhancedForWarnings != null ? getNullabilityQualifier(enhancedForWarnings) : null;
        } else {
            nullabilityQualifier = nullabilityQualifier2;
        }
        TypeSystemContext typeSystem = getTypeSystem();
        if (JavaToKotlinClassMap.INSTANCE.isReadOnly(getFqNameUnsafe(typeSystem.lowerBoundIfFlexible(kotlinTypeMarker)))) {
            mutabilityQualifier = MutabilityQualifier.READ_ONLY;
        } else if (JavaToKotlinClassMap.INSTANCE.isMutable(getFqNameUnsafe(typeSystem.upperBoundIfFlexible(kotlinTypeMarker)))) {
            mutabilityQualifier = MutabilityQualifier.MUTABLE;
        }
        return new JavaTypeQualifiers(nullabilityQualifier, mutabilityQualifier, getTypeSystem().isDefinitelyNotNullType(kotlinTypeMarker) || isNotNullTypeParameterCompat(kotlinTypeMarker), nullabilityQualifier != nullabilityQualifier2);
    }

    private final JavaTypeQualifiers extractQualifiersFromAnnotations(final TypeAndDefaultQualifiers typeAndDefaultQualifiers) {
        List listEmptyList;
        AnnotationQualifierApplicabilityType containerApplicabilityType;
        NullabilityQualifierWithMigrationStatus boundsNullability;
        KotlinTypeMarker type;
        TypeConstructorMarker typeConstructorMarkerTypeConstructor;
        if (typeAndDefaultQualifiers.getType() == null) {
            TypeSystemContext typeSystem = getTypeSystem();
            TypeParameterMarker typeParameterForArgument = typeAndDefaultQualifiers.getTypeParameterForArgument();
            if ((typeParameterForArgument != null ? typeSystem.getVariance(typeParameterForArgument) : null) == TypeVariance.IN) {
                return JavaTypeQualifiers.Companion.getNONE();
            }
        }
        boolean z = false;
        boolean z2 = typeAndDefaultQualifiers.getTypeParameterForArgument() == null;
        KotlinTypeMarker type2 = typeAndDefaultQualifiers.getType();
        if (type2 == null || (listEmptyList = getAnnotations(type2)) == null) {
            listEmptyList = CollectionsKt.emptyList();
        }
        TypeSystemContext typeSystem2 = getTypeSystem();
        KotlinTypeMarker type3 = typeAndDefaultQualifiers.getType();
        TypeParameterMarker typeParameterClassifier = (type3 == null || (typeConstructorMarkerTypeConstructor = typeSystem2.typeConstructor(type3)) == null) ? null : typeSystem2.getTypeParameterClassifier(typeConstructorMarkerTypeConstructor);
        boolean z3 = getContainerApplicabilityType() == AnnotationQualifierApplicabilityType.TYPE_PARAMETER_BOUNDS;
        if (z2) {
            if (!z3 && getEnableImprovementsInStrictMode() && (type = typeAndDefaultQualifiers.getType()) != null && isArrayOrPrimitiveArray(type)) {
                Iterable<TAnnotation> containerAnnotations = getContainerAnnotations();
                ArrayList arrayList = new ArrayList();
                for (TAnnotation tannotation : containerAnnotations) {
                    if (!getAnnotationTypeQualifierResolver().isTypeUseAnnotation(tannotation)) {
                        arrayList.add(tannotation);
                    }
                }
                listEmptyList = CollectionsKt.plus((Collection) arrayList, (Iterable) listEmptyList);
            } else {
                listEmptyList = CollectionsKt.plus((Iterable) getContainerAnnotations(), (Iterable) listEmptyList);
            }
        }
        MutabilityQualifier mutabilityQualifierExtractMutability = getAnnotationTypeQualifierResolver().extractMutability(listEmptyList);
        NullabilityQualifierWithMigrationStatus nullabilityQualifierWithMigrationStatusExtractNullability = getAnnotationTypeQualifierResolver().extractNullability((Iterable) listEmptyList, (Function1) new Function1(this, typeAndDefaultQualifiers) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.AbstractSignatureParts$$Lambda$0
            private final AbstractSignatureParts arg$0;
            private final AbstractSignatureParts.TypeAndDefaultQualifiers arg$1;

            {
                this.arg$0 = this;
                this.arg$1 = typeAndDefaultQualifiers;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return Boolean.valueOf(AbstractSignatureParts.extractQualifiersFromAnnotations$lambda$6(this.arg$0, this.arg$1, obj));
            }
        });
        if (nullabilityQualifierWithMigrationStatusExtractNullability != null) {
            NullabilityQualifier qualifier = nullabilityQualifierWithMigrationStatusExtractNullability.getQualifier();
            if (nullabilityQualifierWithMigrationStatusExtractNullability.getQualifier() == NullabilityQualifier.NOT_NULL && typeParameterClassifier != null) {
                z = true;
            }
            return new JavaTypeQualifiers(qualifier, mutabilityQualifierExtractMutability, z, nullabilityQualifierWithMigrationStatusExtractNullability.isForWarningOnly());
        }
        if (z2 || z3) {
            containerApplicabilityType = getContainerApplicabilityType();
        } else {
            containerApplicabilityType = AnnotationQualifierApplicabilityType.TYPE_USE;
        }
        JavaTypeQualifiersByElementType defaultQualifiers = typeAndDefaultQualifiers.getDefaultQualifiers();
        JavaDefaultQualifiers javaDefaultQualifiers = defaultQualifiers != null ? defaultQualifiers.get(containerApplicabilityType) : null;
        NullabilityQualifierWithMigrationStatus boundsNullability2 = typeParameterClassifier != null ? getBoundsNullability(typeParameterClassifier) : null;
        NullabilityQualifierWithMigrationStatus defaultNullability = getDefaultNullability(boundsNullability2, javaDefaultQualifiers);
        boolean z4 = (boundsNullability2 != null ? boundsNullability2.getQualifier() : null) == NullabilityQualifier.NOT_NULL || !(typeParameterClassifier == null || javaDefaultQualifiers == null || !javaDefaultQualifiers.getDefinitelyNotNull());
        TypeParameterMarker typeParameterForArgument2 = typeAndDefaultQualifiers.getTypeParameterForArgument();
        if (typeParameterForArgument2 == null || (boundsNullability = getBoundsNullability(typeParameterForArgument2)) == null) {
            boundsNullability = null;
        } else if (boundsNullability.getQualifier() == NullabilityQualifier.NULLABLE) {
            boundsNullability = NullabilityQualifierWithMigrationStatus.copy$default(boundsNullability, NullabilityQualifier.FORCE_FLEXIBILITY, false, 2, null);
        }
        NullabilityQualifierWithMigrationStatus nullabilityQualifierWithMigrationStatusMostSpecific = mostSpecific(boundsNullability, defaultNullability);
        NullabilityQualifier qualifier2 = nullabilityQualifierWithMigrationStatusMostSpecific != null ? nullabilityQualifierWithMigrationStatusMostSpecific.getQualifier() : null;
        if (nullabilityQualifierWithMigrationStatusMostSpecific != null && nullabilityQualifierWithMigrationStatusMostSpecific.isForWarningOnly()) {
            z = true;
        }
        return new JavaTypeQualifiers(qualifier2, mutabilityQualifierExtractMutability, z4, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean extractQualifiersFromAnnotations$lambda$6(AbstractSignatureParts this$0, TypeAndDefaultQualifiers this_extractQualifiersFromAnnotations, Object extractNullability) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(this_extractQualifiersFromAnnotations, "$this_extractQualifiersFromAnnotations");
        Intrinsics.checkNotNullParameter(extractNullability, "$this$extractNullability");
        return this$0.forceWarning(extractNullability, this_extractQualifiersFromAnnotations.getType());
    }

    private final NullabilityQualifierWithMigrationStatus mostSpecific(NullabilityQualifierWithMigrationStatus nullabilityQualifierWithMigrationStatus, NullabilityQualifierWithMigrationStatus nullabilityQualifierWithMigrationStatus2) {
        if (nullabilityQualifierWithMigrationStatus == null) {
            return nullabilityQualifierWithMigrationStatus2;
        }
        if (nullabilityQualifierWithMigrationStatus2 == null) {
            return nullabilityQualifierWithMigrationStatus;
        }
        if (nullabilityQualifierWithMigrationStatus.isForWarningOnly() && !nullabilityQualifierWithMigrationStatus2.isForWarningOnly()) {
            return nullabilityQualifierWithMigrationStatus2;
        }
        if (nullabilityQualifierWithMigrationStatus.isForWarningOnly() || !nullabilityQualifierWithMigrationStatus2.isForWarningOnly()) {
            return (nullabilityQualifierWithMigrationStatus.getQualifier().compareTo(nullabilityQualifierWithMigrationStatus2.getQualifier()) >= 0 && nullabilityQualifierWithMigrationStatus.getQualifier().compareTo(nullabilityQualifierWithMigrationStatus2.getQualifier()) > 0) ? nullabilityQualifierWithMigrationStatus : nullabilityQualifierWithMigrationStatus2;
        }
        return nullabilityQualifierWithMigrationStatus;
    }

    /* JADX WARN: Code duplicated, block: B:46:0x00b3  */
    /* JADX WARN: Code duplicated, block: B:49:0x00bd  */
    /* JADX WARN: Code duplicated, block: B:55:0x00d2  */
    /* JADX WARN: Code duplicated, block: B:56:0x00d4  */
    /* JADX WARN: Code duplicated, block: B:71:0x00cc A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:72:0x00c9 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:73:? A[LOOP:3: B:47:0x00b7->B:73:?, LOOP_END, SYNTHETIC] */
    private final NullabilityQualifierWithMigrationStatus getBoundsNullability(TypeParameterMarker typeParameterMarker) {
        ArrayList arrayList;
        List<KotlinTypeMarker> list;
        Iterator<T> it;
        NullabilityQualifier nullabilityQualifier;
        boolean z;
        TypeSystemContext typeSystem = getTypeSystem();
        if (!isFromJava(typeParameterMarker)) {
            return null;
        }
        List<KotlinTypeMarker> upperBounds = typeSystem.getUpperBounds(typeParameterMarker);
        List<KotlinTypeMarker> list2 = upperBounds;
        boolean z2 = list2 instanceof Collection;
        if (!z2 || !list2.isEmpty()) {
            Iterator<T> it2 = list2.iterator();
            while (it2.hasNext()) {
                if (!typeSystem.isError((KotlinTypeMarker) it2.next())) {
                    if (!z2 || !list2.isEmpty()) {
                        Iterator<T> it3 = list2.iterator();
                        while (true) {
                            if (it3.hasNext()) {
                                if (getNullabilityQualifier((KotlinTypeMarker) it3.next()) != null) {
                                    arrayList = upperBounds;
                                }
                            }
                            list = arrayList;
                            if (!(list instanceof Collection) && list.isEmpty()) {
                                nullabilityQualifier = NullabilityQualifier.NULLABLE;
                                break;
                            }
                            it = list.iterator();
                            while (true) {
                                if (!it.hasNext()) {
                                    nullabilityQualifier = NullabilityQualifier.NULLABLE;
                                    break;
                                }
                                if (!typeSystem.isNullableType((KotlinTypeMarker) it.next())) {
                                    nullabilityQualifier = NullabilityQualifier.NOT_NULL;
                                    break;
                                }
                            }
                            if (arrayList != upperBounds) {
                                z = true;
                            } else {
                                z = false;
                            }
                            return new NullabilityQualifierWithMigrationStatus(nullabilityQualifier, z);
                        }
                    }
                    if (!z2 || !list2.isEmpty()) {
                        Iterator<T> it4 = list2.iterator();
                        while (it4.hasNext()) {
                            if (getEnhancedForWarnings((KotlinTypeMarker) it4.next()) != null) {
                                ArrayList arrayList2 = new ArrayList();
                                Iterator<T> it5 = list2.iterator();
                                while (it5.hasNext()) {
                                    KotlinTypeMarker enhancedForWarnings = getEnhancedForWarnings((KotlinTypeMarker) it5.next());
                                    if (enhancedForWarnings != null) {
                                        arrayList2.add(enhancedForWarnings);
                                    }
                                }
                                arrayList = arrayList2;
                                list = arrayList;
                                if (!(list instanceof Collection)) {
                                    it = list.iterator();
                                    while (true) {
                                        if (!it.hasNext()) {
                                            nullabilityQualifier = NullabilityQualifier.NULLABLE;
                                            break;
                                        }
                                        if (!typeSystem.isNullableType((KotlinTypeMarker) it.next())) {
                                            nullabilityQualifier = NullabilityQualifier.NOT_NULL;
                                            break;
                                        }
                                    }
                                } else {
                                    it = list.iterator();
                                    while (true) {
                                        if (!it.hasNext()) {
                                            nullabilityQualifier = NullabilityQualifier.NULLABLE;
                                            break;
                                        }
                                        if (!typeSystem.isNullableType((KotlinTypeMarker) it.next())) {
                                            nullabilityQualifier = NullabilityQualifier.NOT_NULL;
                                            break;
                                        }
                                    }
                                }
                                if (arrayList != upperBounds) {
                                    z = true;
                                } else {
                                    z = false;
                                }
                                return new NullabilityQualifierWithMigrationStatus(nullabilityQualifier, z);
                            }
                        }
                        break;
                    }
                    break;
                }
            }
        }
        return null;
    }

    public final Function1<Integer, JavaTypeQualifiers> computeIndexedQualifiers(KotlinTypeMarker kotlinTypeMarker, Iterable<? extends KotlinTypeMarker> overrides, final TypeEnhancementInfo typeEnhancementInfo, boolean z) {
        int size;
        KotlinTypeMarker type;
        Intrinsics.checkNotNullParameter(kotlinTypeMarker, "<this>");
        Intrinsics.checkNotNullParameter(overrides, "overrides");
        List<TypeAndDefaultQualifiers> indexed = toIndexed(kotlinTypeMarker);
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(overrides, 10));
        Iterator<? extends KotlinTypeMarker> it = overrides.iterator();
        while (it.hasNext()) {
            arrayList.add(toIndexed(it.next()));
        }
        ArrayList arrayList2 = arrayList;
        if (getForceOnlyHeadTypeConstructor()) {
            size = 1;
        } else {
            if (isCovariant() && (!(overrides instanceof Collection) || !((Collection) overrides).isEmpty())) {
                Iterator<? extends KotlinTypeMarker> it2 = overrides.iterator();
                while (true) {
                    if (it2.hasNext()) {
                        if (!isEqual(kotlinTypeMarker, it2.next())) {
                            size = 1;
                        }
                    }
                }
            }
            size = indexed.size();
        }
        final JavaTypeQualifiers[] javaTypeQualifiersArr = new JavaTypeQualifiers[size];
        int i = 0;
        while (i < size) {
            JavaTypeQualifiers javaTypeQualifiersExtractQualifiersFromAnnotations = extractQualifiersFromAnnotations(indexed.get(i));
            ArrayList arrayList3 = new ArrayList();
            Iterator it3 = arrayList2.iterator();
            while (it3.hasNext()) {
                TypeAndDefaultQualifiers typeAndDefaultQualifiers = (TypeAndDefaultQualifiers) CollectionsKt.getOrNull((List) it3.next(), i);
                JavaTypeQualifiers javaTypeQualifiersExtractQualifiers = (typeAndDefaultQualifiers == null || (type = typeAndDefaultQualifiers.getType()) == null) ? null : extractQualifiers(type);
                if (javaTypeQualifiersExtractQualifiers != null) {
                    arrayList3.add(javaTypeQualifiersExtractQualifiers);
                }
            }
            javaTypeQualifiersArr[i] = TypeEnhancementUtilsKt.computeQualifiersForOverride(javaTypeQualifiersExtractQualifiersFromAnnotations, arrayList3, i == 0 && isCovariant(), i == 0 && getContainerIsVarargParameter(), z);
            i++;
        }
        return new Function1(typeEnhancementInfo, javaTypeQualifiersArr) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.AbstractSignatureParts$$Lambda$1
            private final TypeEnhancementInfo arg$0;
            private final JavaTypeQualifiers[] arg$1;

            {
                this.arg$0 = typeEnhancementInfo;
                this.arg$1 = javaTypeQualifiersArr;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return AbstractSignatureParts.computeIndexedQualifiers$lambda$18(this.arg$0, this.arg$1, ((Number) obj).intValue());
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final JavaTypeQualifiers computeIndexedQualifiers$lambda$18(TypeEnhancementInfo typeEnhancementInfo, JavaTypeQualifiers[] computedResult, int i) {
        Map<Integer, JavaTypeQualifiers> map;
        JavaTypeQualifiers javaTypeQualifiers;
        Intrinsics.checkNotNullParameter(computedResult, "$computedResult");
        if (typeEnhancementInfo == null || (map = typeEnhancementInfo.getMap()) == null || (javaTypeQualifiers = map.get(Integer.valueOf(i))) == null) {
            return (i < 0 || i >= computedResult.length) ? JavaTypeQualifiers.Companion.getNONE() : computedResult[i];
        }
        return javaTypeQualifiers;
    }

    private final <T> void flattenTree(T t, List<T> list, Function1<? super T, ? extends Iterable<? extends T>> function1) {
        list.add(t);
        Iterable<? extends T> iterableInvoke = function1.invoke(t);
        if (iterableInvoke != null) {
            Iterator<? extends T> it = iterableInvoke.iterator();
            while (it.hasNext()) {
                flattenTree(it.next(), list, function1);
            }
        }
    }

    private final <T> List<T> flattenTree(T t, Function1<? super T, ? extends Iterable<? extends T>> function1) {
        ArrayList arrayList = new ArrayList(1);
        flattenTree(t, arrayList, function1);
        return arrayList;
    }

    private final JavaTypeQualifiersByElementType extractAndMergeDefaultQualifiers(KotlinTypeMarker kotlinTypeMarker, JavaTypeQualifiersByElementType javaTypeQualifiersByElementType) {
        return getAnnotationTypeQualifierResolver().extractAndMergeDefaultQualifiers(javaTypeQualifiersByElementType, getAnnotations(kotlinTypeMarker));
    }

    private final List<TypeAndDefaultQualifiers> toIndexed(KotlinTypeMarker kotlinTypeMarker) {
        final TypeSystemContext typeSystem = getTypeSystem();
        return flattenTree(new TypeAndDefaultQualifiers(kotlinTypeMarker, extractAndMergeDefaultQualifiers(kotlinTypeMarker, getContainerDefaultTypeQualifiers()), null), new Function1(this, typeSystem) { // from class: kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.AbstractSignatureParts$$Lambda$2
            private final AbstractSignatureParts arg$0;
            private final TypeSystemContext arg$1;

            {
                this.arg$0 = this;
                this.arg$1 = typeSystem;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return AbstractSignatureParts.toIndexed$lambda$23$lambda$22(this.arg$0, this.arg$1, (AbstractSignatureParts.TypeAndDefaultQualifiers) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Iterable toIndexed$lambda$23$lambda$22(AbstractSignatureParts this$0, TypeSystemContext this_with, TypeAndDefaultQualifiers it) {
        TypeConstructorMarker typeConstructorMarkerTypeConstructor;
        List<TypeParameterMarker> parameters;
        TypeAndDefaultQualifiers typeAndDefaultQualifiers;
        KotlinTypeMarker type;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(this_with, "$this_with");
        Intrinsics.checkNotNullParameter(it, "it");
        ArrayList arrayList = null;
        if (this$0.getSkipRawTypeArguments() && (type = it.getType()) != null && this_with.isRawType(type)) {
            return null;
        }
        KotlinTypeMarker type2 = it.getType();
        if (type2 != null && (typeConstructorMarkerTypeConstructor = this_with.typeConstructor(type2)) != null && (parameters = this_with.getParameters(typeConstructorMarkerTypeConstructor)) != null) {
            List<TypeParameterMarker> list = parameters;
            List<TypeArgumentMarker> arguments = this_with.getArguments(it.getType());
            Iterator<T> it2 = list.iterator();
            Iterator<T> it3 = arguments.iterator();
            ArrayList arrayList2 = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(list, 10), CollectionsKt.collectionSizeOrDefault(arguments, 10)));
            while (it2.hasNext() && it3.hasNext()) {
                Object next = it2.next();
                TypeArgumentMarker typeArgumentMarker = (TypeArgumentMarker) it3.next();
                TypeParameterMarker typeParameterMarker = (TypeParameterMarker) next;
                if (this_with.isStarProjection(typeArgumentMarker)) {
                    typeAndDefaultQualifiers = new TypeAndDefaultQualifiers(null, it.getDefaultQualifiers(), typeParameterMarker);
                } else {
                    KotlinTypeMarker type3 = this_with.getType(typeArgumentMarker);
                    typeAndDefaultQualifiers = new TypeAndDefaultQualifiers(type3, this$0.extractAndMergeDefaultQualifiers(type3, it.getDefaultQualifiers()), typeParameterMarker);
                }
                arrayList2.add(typeAndDefaultQualifiers);
            }
            arrayList = arrayList2;
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: compiled from: AbstractSignatureParts.kt */
    static final class TypeAndDefaultQualifiers {
        private final JavaTypeQualifiersByElementType defaultQualifiers;
        private final KotlinTypeMarker type;
        private final TypeParameterMarker typeParameterForArgument;

        public TypeAndDefaultQualifiers(KotlinTypeMarker kotlinTypeMarker, JavaTypeQualifiersByElementType javaTypeQualifiersByElementType, TypeParameterMarker typeParameterMarker) {
            this.type = kotlinTypeMarker;
            this.defaultQualifiers = javaTypeQualifiersByElementType;
            this.typeParameterForArgument = typeParameterMarker;
        }

        public final KotlinTypeMarker getType() {
            return this.type;
        }

        public final JavaTypeQualifiersByElementType getDefaultQualifiers() {
            return this.defaultQualifiers;
        }

        public final TypeParameterMarker getTypeParameterForArgument() {
            return this.typeParameterForArgument;
        }
    }
}
