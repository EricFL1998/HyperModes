package kotlin.reflect.jvm.internal.impl.types.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.resolve.constants.IntegerLiteralTypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.DefinitelyNotNullType;
import kotlin.reflect.jvm.internal.impl.types.FlexibleTypesKt;
import kotlin.reflect.jvm.internal.impl.types.IntersectionTypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.SpecialTypesKt;
import kotlin.reflect.jvm.internal.impl.types.StubTypeForBuilderInference;
import kotlin.reflect.jvm.internal.impl.types.TypeAttributes;
import kotlin.reflect.jvm.internal.impl.types.UnwrappedType;

/* JADX INFO: compiled from: IntersectionType.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class TypeIntersector {
    public static final TypeIntersector INSTANCE = new TypeIntersector();

    private TypeIntersector() {
    }

    public final SimpleType intersectTypes$descriptors(List<? extends SimpleType> types) {
        Intrinsics.checkNotNullParameter(types, "types");
        types.size();
        ArrayList arrayList = new ArrayList();
        for (SimpleType simpleType : types) {
            if (simpleType.getConstructor() instanceof IntersectionTypeConstructor) {
                Collection<KotlinType> collectionMo1492getSupertypes = simpleType.getConstructor().mo1492getSupertypes();
                Intrinsics.checkNotNullExpressionValue(collectionMo1492getSupertypes, "getSupertypes(...)");
                Collection<KotlinType> collection = collectionMo1492getSupertypes;
                ArrayList arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(collection, 10));
                for (KotlinType kotlinType : collection) {
                    Intrinsics.checkNotNull(kotlinType);
                    SimpleType simpleTypeUpperIfFlexible = FlexibleTypesKt.upperIfFlexible(kotlinType);
                    if (simpleType.isMarkedNullable()) {
                        simpleTypeUpperIfFlexible = simpleTypeUpperIfFlexible.makeNullableAsSpecified(true);
                    }
                    arrayList2.add(simpleTypeUpperIfFlexible);
                }
                arrayList.addAll(arrayList2);
            } else {
                arrayList.add(simpleType);
            }
        }
        ArrayList<SimpleType> arrayList3 = arrayList;
        ResultNullability resultNullabilityCombine = ResultNullability.START;
        Iterator it = arrayList3.iterator();
        while (it.hasNext()) {
            resultNullabilityCombine = resultNullabilityCombine.combine((UnwrappedType) it.next());
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (NewCapturedType newCapturedTypeMakeSimpleTypeDefinitelyNotNullOrNotNull$default : arrayList3) {
            if (resultNullabilityCombine == ResultNullability.NOT_NULL) {
                if (newCapturedTypeMakeSimpleTypeDefinitelyNotNullOrNotNull$default instanceof NewCapturedType) {
                    newCapturedTypeMakeSimpleTypeDefinitelyNotNullOrNotNull$default = SpecialTypesKt.withNotNullProjection((NewCapturedType) newCapturedTypeMakeSimpleTypeDefinitelyNotNullOrNotNull$default);
                }
                newCapturedTypeMakeSimpleTypeDefinitelyNotNullOrNotNull$default = SpecialTypesKt.makeSimpleTypeDefinitelyNotNullOrNotNull$default(newCapturedTypeMakeSimpleTypeDefinitelyNotNullOrNotNull$default, false, 1, null);
            }
            linkedHashSet.add(newCapturedTypeMakeSimpleTypeDefinitelyNotNullOrNotNull$default);
        }
        LinkedHashSet linkedHashSet2 = linkedHashSet;
        List<? extends SimpleType> list = types;
        ArrayList arrayList4 = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        Iterator<T> it2 = list.iterator();
        while (it2.hasNext()) {
            arrayList4.add(((SimpleType) it2.next()).getAttributes());
        }
        Iterator it3 = arrayList4.iterator();
        if (!it3.hasNext()) {
            throw new UnsupportedOperationException("Empty collection can't be reduced.");
        }
        Object next = it3.next();
        while (it3.hasNext()) {
            next = ((TypeAttributes) next).intersect((TypeAttributes) it3.next());
        }
        return intersectTypesWithoutIntersectionType(linkedHashSet2).replaceAttributes((TypeAttributes) next);
    }

    private final SimpleType intersectTypesWithoutIntersectionType(final Set<? extends SimpleType> set) {
        if (set.size() == 1) {
            return (SimpleType) CollectionsKt.single(set);
        }
        new Function0(set) { // from class: kotlin.reflect.jvm.internal.impl.types.checker.TypeIntersector$$Lambda$0
            private final Set arg$0;

            {
                this.arg$0 = set;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return TypeIntersector.intersectTypesWithoutIntersectionType$lambda$6(this.arg$0);
            }
        };
        Set<? extends SimpleType> set2 = set;
        Collection<SimpleType> collectionFilterTypes = filterTypes(set2, new TypeIntersector$intersectTypesWithoutIntersectionType$filteredEqualTypes$1(this));
        collectionFilterTypes.isEmpty();
        SimpleType simpleTypeFindIntersectionType = IntegerLiteralTypeConstructor.Companion.findIntersectionType(collectionFilterTypes);
        if (simpleTypeFindIntersectionType != null) {
            return simpleTypeFindIntersectionType;
        }
        Collection<SimpleType> collectionFilterTypes2 = filterTypes(collectionFilterTypes, new TypeIntersector$intersectTypesWithoutIntersectionType$filteredSuperAndEqualTypes$1(NewKotlinTypeChecker.Companion.getDefault()));
        collectionFilterTypes2.isEmpty();
        return collectionFilterTypes2.size() < 2 ? (SimpleType) CollectionsKt.single(collectionFilterTypes2) : new IntersectionTypeConstructor(set2).createType();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String intersectTypesWithoutIntersectionType$lambda$6(Set inputTypes) {
        Intrinsics.checkNotNullParameter(inputTypes, "$inputTypes");
        return "This collections cannot be empty! input types: " + CollectionsKt.joinToString$default(inputTypes, null, null, null, 0, null, null, 63, null);
    }

    private final Collection<SimpleType> filterTypes(Collection<? extends SimpleType> collection, Function2<? super SimpleType, ? super SimpleType, Boolean> function2) {
        ArrayList arrayList = new ArrayList(collection);
        Iterator it = arrayList.iterator();
        Intrinsics.checkNotNullExpressionValue(it, "iterator(...)");
        while (it.hasNext()) {
            SimpleType simpleType = (SimpleType) it.next();
            ArrayList<SimpleType> arrayList2 = arrayList;
            if (!(arrayList2 instanceof Collection) || !arrayList2.isEmpty()) {
                for (SimpleType simpleType2 : arrayList2) {
                    if (simpleType2 != simpleType) {
                        Intrinsics.checkNotNull(simpleType2);
                        Intrinsics.checkNotNull(simpleType);
                        if (function2.invoke(simpleType2, simpleType).booleanValue()) {
                            it.remove();
                            break;
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean isStrictSupertype(KotlinType kotlinType, KotlinType kotlinType2) {
        NewKotlinTypeCheckerImpl newKotlinTypeCheckerImpl = NewKotlinTypeChecker.Companion.getDefault();
        return newKotlinTypeCheckerImpl.isSubtypeOf(kotlinType, kotlinType2) && !newKotlinTypeCheckerImpl.isSubtypeOf(kotlinType2, kotlinType);
    }

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* JADX INFO: compiled from: IntersectionType.kt */
    private static final class ResultNullability {
        private static final /* synthetic */ EnumEntries $ENTRIES;
        private static final /* synthetic */ ResultNullability[] $VALUES;
        public static final ResultNullability START = new START("START", 0);
        public static final ResultNullability ACCEPT_NULL = new ACCEPT_NULL("ACCEPT_NULL", 1);
        public static final ResultNullability UNKNOWN = new UNKNOWN("UNKNOWN", 2);
        public static final ResultNullability NOT_NULL = new NOT_NULL("NOT_NULL", 3);

        private static final /* synthetic */ ResultNullability[] $values() {
            return new ResultNullability[]{START, ACCEPT_NULL, UNKNOWN, NOT_NULL};
        }

        public /* synthetic */ ResultNullability(String str, int i, DefaultConstructorMarker defaultConstructorMarker) {
            this(str, i);
        }

        public abstract ResultNullability combine(UnwrappedType unwrappedType);

        /* JADX INFO: compiled from: IntersectionType.kt */
        static final class START extends ResultNullability {
            START(String str, int i) {
                super(str, i, null);
            }

            @Override // kotlin.reflect.jvm.internal.impl.types.checker.TypeIntersector.ResultNullability
            public ResultNullability combine(UnwrappedType nextType) {
                Intrinsics.checkNotNullParameter(nextType, "nextType");
                return getResultNullability(nextType);
            }
        }

        private ResultNullability(String str, int i) {
            super(str, i);
        }

        static {
            ResultNullability[] resultNullabilityArr$values = $values();
            $VALUES = resultNullabilityArr$values;
            $ENTRIES = EnumEntriesKt.enumEntries(resultNullabilityArr$values);
        }

        /* JADX INFO: compiled from: IntersectionType.kt */
        static final class ACCEPT_NULL extends ResultNullability {
            ACCEPT_NULL(String str, int i) {
                super(str, i, null);
            }

            @Override // kotlin.reflect.jvm.internal.impl.types.checker.TypeIntersector.ResultNullability
            public ResultNullability combine(UnwrappedType nextType) {
                Intrinsics.checkNotNullParameter(nextType, "nextType");
                return getResultNullability(nextType);
            }
        }

        /* JADX INFO: compiled from: IntersectionType.kt */
        static final class UNKNOWN extends ResultNullability {
            UNKNOWN(String str, int i) {
                super(str, i, null);
            }

            @Override // kotlin.reflect.jvm.internal.impl.types.checker.TypeIntersector.ResultNullability
            public ResultNullability combine(UnwrappedType nextType) {
                Intrinsics.checkNotNullParameter(nextType, "nextType");
                ResultNullability resultNullability = getResultNullability(nextType);
                return resultNullability == ResultNullability.ACCEPT_NULL ? this : resultNullability;
            }
        }

        /* JADX INFO: compiled from: IntersectionType.kt */
        static final class NOT_NULL extends ResultNullability {
            @Override // kotlin.reflect.jvm.internal.impl.types.checker.TypeIntersector.ResultNullability
            public NOT_NULL combine(UnwrappedType nextType) {
                Intrinsics.checkNotNullParameter(nextType, "nextType");
                return this;
            }

            NOT_NULL(String str, int i) {
                super(str, i, null);
            }
        }

        protected final ResultNullability getResultNullability(UnwrappedType unwrappedType) {
            Intrinsics.checkNotNullParameter(unwrappedType, "<this>");
            if (unwrappedType.isMarkedNullable()) {
                return ACCEPT_NULL;
            }
            if ((unwrappedType instanceof DefinitelyNotNullType) && (((DefinitelyNotNullType) unwrappedType).getOriginal() instanceof StubTypeForBuilderInference)) {
                return NOT_NULL;
            }
            if (unwrappedType instanceof StubTypeForBuilderInference) {
                return UNKNOWN;
            }
            return NullabilityChecker.INSTANCE.isSubtypeOfAny(unwrappedType) ? NOT_NULL : UNKNOWN;
        }

        public static ResultNullability valueOf(String str) {
            return (ResultNullability) Enum.valueOf(ResultNullability.class, str);
        }

        public static ResultNullability[] values() {
            return (ResultNullability[]) $VALUES.clone();
        }
    }
}
