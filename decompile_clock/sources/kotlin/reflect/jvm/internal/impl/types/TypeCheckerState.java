package kotlin.reflect.jvm.internal.impl.types;

import java.util.ArrayDeque;
import java.util.Set;
import kotlin.Unit;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.types.model.CapturedTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.model.KotlinTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.model.SimpleTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.model.TypeSystemContext;
import kotlin.reflect.jvm.internal.impl.utils.SmartSet;

/* JADX INFO: compiled from: AbstractTypeChecker.kt */
/* JADX INFO: loaded from: classes2.dex */
public class TypeCheckerState {
    private final boolean allowedTypeVariable;
    private int argumentsDepth;
    private final boolean isErrorTypeEqualsToAnything;
    private final boolean isStubTypeEqualsToAnything;
    private final AbstractTypePreparator kotlinTypePreparator;
    private final AbstractTypeRefiner kotlinTypeRefiner;
    private ArrayDeque<SimpleTypeMarker> supertypesDeque;
    private boolean supertypesLocked;
    private Set<SimpleTypeMarker> supertypesSet;
    private final TypeSystemContext typeSystemContext;

    /* JADX INFO: compiled from: AbstractTypeChecker.kt */
    public enum LowerCapturedTypePolicy {
        CHECK_ONLY_LOWER,
        CHECK_SUBTYPE_AND_LOWER,
        SKIP_LOWER;

        private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());
    }

    public Boolean addSubtypeConstraint(KotlinTypeMarker subType, KotlinTypeMarker superType, boolean z) {
        Intrinsics.checkNotNullParameter(subType, "subType");
        Intrinsics.checkNotNullParameter(superType, "superType");
        return null;
    }

    public boolean customIsSubtypeOf(KotlinTypeMarker subType, KotlinTypeMarker superType) {
        Intrinsics.checkNotNullParameter(subType, "subType");
        Intrinsics.checkNotNullParameter(superType, "superType");
        return true;
    }

    public TypeCheckerState(boolean z, boolean z2, boolean z3, TypeSystemContext typeSystemContext, AbstractTypePreparator kotlinTypePreparator, AbstractTypeRefiner kotlinTypeRefiner) {
        Intrinsics.checkNotNullParameter(typeSystemContext, "typeSystemContext");
        Intrinsics.checkNotNullParameter(kotlinTypePreparator, "kotlinTypePreparator");
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "kotlinTypeRefiner");
        this.isErrorTypeEqualsToAnything = z;
        this.isStubTypeEqualsToAnything = z2;
        this.allowedTypeVariable = z3;
        this.typeSystemContext = typeSystemContext;
        this.kotlinTypePreparator = kotlinTypePreparator;
        this.kotlinTypeRefiner = kotlinTypeRefiner;
    }

    public final boolean isErrorTypeEqualsToAnything() {
        return this.isErrorTypeEqualsToAnything;
    }

    public final boolean isStubTypeEqualsToAnything() {
        return this.isStubTypeEqualsToAnything;
    }

    public final TypeSystemContext getTypeSystemContext() {
        return this.typeSystemContext;
    }

    public final KotlinTypeMarker refineType(KotlinTypeMarker type) {
        Intrinsics.checkNotNullParameter(type, "type");
        return this.kotlinTypeRefiner.refineType(type);
    }

    public final KotlinTypeMarker prepareType(KotlinTypeMarker type) {
        Intrinsics.checkNotNullParameter(type, "type");
        return this.kotlinTypePreparator.prepareType(type);
    }

    public LowerCapturedTypePolicy getLowerCapturedTypePolicy(SimpleTypeMarker subType, CapturedTypeMarker superType) {
        Intrinsics.checkNotNullParameter(subType, "subType");
        Intrinsics.checkNotNullParameter(superType, "superType");
        return LowerCapturedTypePolicy.CHECK_SUBTYPE_AND_LOWER;
    }

    public static /* synthetic */ Boolean addSubtypeConstraint$default(TypeCheckerState typeCheckerState, KotlinTypeMarker kotlinTypeMarker, KotlinTypeMarker kotlinTypeMarker2, boolean z, int i, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: addSubtypeConstraint");
        }
        if ((i & 4) != 0) {
            z = false;
        }
        return typeCheckerState.addSubtypeConstraint(kotlinTypeMarker, kotlinTypeMarker2, z);
    }

    public boolean runForkingPoint(Function1<? super ForkPointContext, Unit> block) {
        Intrinsics.checkNotNullParameter(block, "block");
        ForkPointContext.Default r0 = new ForkPointContext.Default();
        block.invoke(r0);
        return r0.getResult();
    }

    /* JADX INFO: compiled from: AbstractTypeChecker.kt */
    public interface ForkPointContext {
        void fork(Function0<Boolean> function0);

        /* JADX INFO: compiled from: AbstractTypeChecker.kt */
        public static final class Default implements ForkPointContext {
            private boolean result;

            public final boolean getResult() {
                return this.result;
            }

            @Override // kotlin.reflect.jvm.internal.impl.types.TypeCheckerState.ForkPointContext
            public void fork(Function0<Boolean> block) {
                Intrinsics.checkNotNullParameter(block, "block");
                if (this.result) {
                    return;
                }
                this.result = block.invoke().booleanValue();
            }
        }
    }

    public final ArrayDeque<SimpleTypeMarker> getSupertypesDeque() {
        return this.supertypesDeque;
    }

    public final Set<SimpleTypeMarker> getSupertypesSet() {
        return this.supertypesSet;
    }

    public final void initialize() {
        this.supertypesLocked = true;
        if (this.supertypesDeque == null) {
            this.supertypesDeque = new ArrayDeque<>(4);
        }
        if (this.supertypesSet == null) {
            this.supertypesSet = SmartSet.Companion.create();
        }
    }

    public final void clear() {
        ArrayDeque<SimpleTypeMarker> arrayDeque = this.supertypesDeque;
        Intrinsics.checkNotNull(arrayDeque);
        arrayDeque.clear();
        Set<SimpleTypeMarker> set = this.supertypesSet;
        Intrinsics.checkNotNull(set);
        set.clear();
        this.supertypesLocked = false;
    }

    /* JADX INFO: compiled from: AbstractTypeChecker.kt */
    public static abstract class SupertypesPolicy {
        public /* synthetic */ SupertypesPolicy(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* JADX INFO: renamed from: transformType */
        public abstract SimpleTypeMarker mo1495transformType(TypeCheckerState typeCheckerState, KotlinTypeMarker kotlinTypeMarker);

        private SupertypesPolicy() {
        }

        /* JADX INFO: compiled from: AbstractTypeChecker.kt */
        public static final class None extends SupertypesPolicy {
            public static final None INSTANCE = new None();

            private None() {
                super(null);
            }

            @Override // kotlin.reflect.jvm.internal.impl.types.TypeCheckerState.SupertypesPolicy
            /* JADX INFO: renamed from: transformType, reason: collision with other method in class */
            public /* bridge */ /* synthetic */ SimpleTypeMarker mo1495transformType(TypeCheckerState typeCheckerState, KotlinTypeMarker kotlinTypeMarker) {
                return (SimpleTypeMarker) transformType(typeCheckerState, kotlinTypeMarker);
            }

            public Void transformType(TypeCheckerState state, KotlinTypeMarker type) {
                Intrinsics.checkNotNullParameter(state, "state");
                Intrinsics.checkNotNullParameter(type, "type");
                throw new UnsupportedOperationException("Should not be called");
            }
        }

        /* JADX INFO: compiled from: AbstractTypeChecker.kt */
        public static final class UpperIfFlexible extends SupertypesPolicy {
            public static final UpperIfFlexible INSTANCE = new UpperIfFlexible();

            private UpperIfFlexible() {
                super(null);
            }

            @Override // kotlin.reflect.jvm.internal.impl.types.TypeCheckerState.SupertypesPolicy
            /* JADX INFO: renamed from: transformType */
            public SimpleTypeMarker mo1495transformType(TypeCheckerState state, KotlinTypeMarker type) {
                Intrinsics.checkNotNullParameter(state, "state");
                Intrinsics.checkNotNullParameter(type, "type");
                return state.getTypeSystemContext().upperBoundIfFlexible(type);
            }
        }

        /* JADX INFO: compiled from: AbstractTypeChecker.kt */
        public static final class LowerIfFlexible extends SupertypesPolicy {
            public static final LowerIfFlexible INSTANCE = new LowerIfFlexible();

            private LowerIfFlexible() {
                super(null);
            }

            @Override // kotlin.reflect.jvm.internal.impl.types.TypeCheckerState.SupertypesPolicy
            /* JADX INFO: renamed from: transformType */
            public SimpleTypeMarker mo1495transformType(TypeCheckerState state, KotlinTypeMarker type) {
                Intrinsics.checkNotNullParameter(state, "state");
                Intrinsics.checkNotNullParameter(type, "type");
                return state.getTypeSystemContext().lowerBoundIfFlexible(type);
            }
        }

        /* JADX INFO: compiled from: AbstractTypeChecker.kt */
        public static abstract class DoCustomTransform extends SupertypesPolicy {
            public DoCustomTransform() {
                super(null);
            }
        }
    }

    public final boolean isAllowedTypeVariable(KotlinTypeMarker type) {
        Intrinsics.checkNotNullParameter(type, "type");
        return this.allowedTypeVariable && this.typeSystemContext.isTypeVariableType(type);
    }
}
