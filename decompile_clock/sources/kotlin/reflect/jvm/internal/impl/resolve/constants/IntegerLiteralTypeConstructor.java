package kotlin.reflect.jvm.internal.impl.resolve.constants;

import com.xiaomi.onetrack.util.z;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.KotlinTypeFactory;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.TypeAttributes;
import kotlin.reflect.jvm.internal.impl.types.TypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.TypeProjectionImpl;
import kotlin.reflect.jvm.internal.impl.types.TypeSubstitutionKt;
import kotlin.reflect.jvm.internal.impl.types.Variance;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeRefiner;

/* JADX INFO: compiled from: IntegerLiteralTypeConstructor.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class IntegerLiteralTypeConstructor implements TypeConstructor {
    public static final Companion Companion = new Companion(null);
    private final ModuleDescriptor module;
    private final Set<KotlinType> possibleTypes;
    private final Lazy supertypes$delegate;
    private final SimpleType type;
    private final long value;

    public /* synthetic */ IntegerLiteralTypeConstructor(long j, ModuleDescriptor moduleDescriptor, Set set, DefaultConstructorMarker defaultConstructorMarker) {
        this(j, moduleDescriptor, set);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeConstructor
    /* JADX INFO: renamed from: getDeclarationDescriptor */
    public ClassifierDescriptor mo1491getDeclarationDescriptor() {
        return null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeConstructor
    public boolean isDenotable() {
        return false;
    }

    /* JADX INFO: compiled from: IntegerLiteralTypeConstructor.kt */
    public static final class Companion {

        /* JADX INFO: compiled from: IntegerLiteralTypeConstructor.kt */
        private enum Mode {
            COMMON_SUPER_TYPE,
            INTERSECTION_TYPE;

            private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());
        }

        /* JADX INFO: compiled from: IntegerLiteralTypeConstructor.kt */
        public /* synthetic */ class WhenMappings {
            public static final /* synthetic */ int[] $EnumSwitchMapping$0;

            static {
                int[] iArr = new int[Mode.values().length];
                try {
                    iArr[Mode.COMMON_SUPER_TYPE.ordinal()] = 1;
                } catch (NoSuchFieldError unused) {
                }
                try {
                    iArr[Mode.INTERSECTION_TYPE.ordinal()] = 2;
                } catch (NoSuchFieldError unused2) {
                }
                $EnumSwitchMapping$0 = iArr;
            }
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final SimpleType findIntersectionType(Collection<? extends SimpleType> types) {
            Intrinsics.checkNotNullParameter(types, "types");
            return findCommonSuperTypeOrIntersectionType(types, Mode.INTERSECTION_TYPE);
        }

        private final SimpleType findCommonSuperTypeOrIntersectionType(Collection<? extends SimpleType> collection, Mode mode) {
            if (collection.isEmpty()) {
                return null;
            }
            Iterator<T> it = collection.iterator();
            if (!it.hasNext()) {
                throw new UnsupportedOperationException("Empty collection can't be reduced.");
            }
            Object next = it.next();
            while (it.hasNext()) {
                SimpleType simpleType = (SimpleType) it.next();
                next = IntegerLiteralTypeConstructor.Companion.fold((SimpleType) next, simpleType, mode);
            }
            return (SimpleType) next;
        }

        private final SimpleType fold(SimpleType simpleType, SimpleType simpleType2, Mode mode) {
            if (simpleType == null || simpleType2 == null) {
                return null;
            }
            TypeConstructor constructor = simpleType.getConstructor();
            TypeConstructor constructor2 = simpleType2.getConstructor();
            boolean z = constructor instanceof IntegerLiteralTypeConstructor;
            if (z && (constructor2 instanceof IntegerLiteralTypeConstructor)) {
                return fold((IntegerLiteralTypeConstructor) constructor, (IntegerLiteralTypeConstructor) constructor2, mode);
            }
            if (z) {
                return fold((IntegerLiteralTypeConstructor) constructor, simpleType2);
            }
            if (constructor2 instanceof IntegerLiteralTypeConstructor) {
                return fold((IntegerLiteralTypeConstructor) constructor2, simpleType);
            }
            return null;
        }

        private final SimpleType fold(IntegerLiteralTypeConstructor integerLiteralTypeConstructor, IntegerLiteralTypeConstructor integerLiteralTypeConstructor2, Mode mode) {
            Set setIntersect;
            int i = WhenMappings.$EnumSwitchMapping$0[mode.ordinal()];
            if (i == 1) {
                setIntersect = CollectionsKt.intersect(integerLiteralTypeConstructor.getPossibleTypes(), integerLiteralTypeConstructor2.getPossibleTypes());
            } else {
                if (i != 2) {
                    throw new NoWhenBranchMatchedException();
                }
                setIntersect = CollectionsKt.union(integerLiteralTypeConstructor.getPossibleTypes(), integerLiteralTypeConstructor2.getPossibleTypes());
            }
            return KotlinTypeFactory.integerLiteralType(TypeAttributes.Companion.getEmpty(), new IntegerLiteralTypeConstructor(integerLiteralTypeConstructor.value, integerLiteralTypeConstructor.module, setIntersect, null), false);
        }

        private final SimpleType fold(IntegerLiteralTypeConstructor integerLiteralTypeConstructor, SimpleType simpleType) {
            if (integerLiteralTypeConstructor.getPossibleTypes().contains(simpleType)) {
                return simpleType;
            }
            return null;
        }
    }

    public final Set<KotlinType> getPossibleTypes() {
        return this.possibleTypes;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private IntegerLiteralTypeConstructor(long j, ModuleDescriptor moduleDescriptor, Set<? extends KotlinType> set) {
        this.type = KotlinTypeFactory.integerLiteralType(TypeAttributes.Companion.getEmpty(), this, false);
        this.supertypes$delegate = LazyKt.lazy(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.resolve.constants.IntegerLiteralTypeConstructor$$Lambda$0
            private final IntegerLiteralTypeConstructor arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return IntegerLiteralTypeConstructor.supertypes_delegate$lambda$2(this.arg$0);
            }
        });
        this.value = j;
        this.module = moduleDescriptor;
        this.possibleTypes = set;
    }

    private final boolean isContainsOnlyUnsignedTypes() {
        Collection<KotlinType> allSignedLiteralTypes = PrimitiveTypeUtilKt.getAllSignedLiteralTypes(this.module);
        if ((allSignedLiteralTypes instanceof Collection) && allSignedLiteralTypes.isEmpty()) {
            return true;
        }
        Iterator<T> it = allSignedLiteralTypes.iterator();
        while (it.hasNext()) {
            if (!(!this.possibleTypes.contains((KotlinType) it.next()))) {
                return false;
            }
        }
        return true;
    }

    private final List<KotlinType> getSupertypes() {
        return (List) this.supertypes$delegate.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final List supertypes_delegate$lambda$2(IntegerLiteralTypeConstructor this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        SimpleType defaultType = this$0.getBuiltIns().getComparable().getDefaultType();
        Intrinsics.checkNotNullExpressionValue(defaultType, "getDefaultType(...)");
        List listMutableListOf = CollectionsKt.mutableListOf(TypeSubstitutionKt.replace$default(defaultType, CollectionsKt.listOf(new TypeProjectionImpl(Variance.IN_VARIANCE, this$0.type)), null, 2, null));
        if (!this$0.isContainsOnlyUnsignedTypes()) {
            listMutableListOf.add(this$0.getBuiltIns().getNumberType());
        }
        return listMutableListOf;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeConstructor
    public List<TypeParameterDescriptor> getParameters() {
        return CollectionsKt.emptyList();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeConstructor
    /* JADX INFO: renamed from: getSupertypes, reason: collision with other method in class */
    public Collection<KotlinType> mo1492getSupertypes() {
        return getSupertypes();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeConstructor
    public KotlinBuiltIns getBuiltIns() {
        return this.module.getBuiltIns();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeConstructor
    public TypeConstructor refine(KotlinTypeRefiner kotlinTypeRefiner) {
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "kotlinTypeRefiner");
        return this;
    }

    public String toString() {
        return "IntegerLiteralType" + valueToString();
    }

    private final String valueToString() {
        return "[" + CollectionsKt.joinToString$default(this.possibleTypes, z.b, null, null, 0, null, new Function1() { // from class: kotlin.reflect.jvm.internal.impl.resolve.constants.IntegerLiteralTypeConstructor$$Lambda$1
            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return IntegerLiteralTypeConstructor.valueToString$lambda$4((KotlinType) obj);
            }
        }, 30, null) + ']';
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CharSequence valueToString$lambda$4(KotlinType it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return it.toString();
    }
}
