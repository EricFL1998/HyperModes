package kotlin.reflect.jvm.internal.impl.types;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.TypeParameterDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.types.checker.NewCapturedType;
import kotlin.reflect.jvm.internal.impl.types.checker.NewTypeVariableConstructor;
import kotlin.reflect.jvm.internal.impl.types.checker.NullabilityChecker;
import kotlin.reflect.jvm.internal.impl.types.model.DefinitelyNotNullTypeMarker;

/* JADX INFO: compiled from: SpecialTypes.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class DefinitelyNotNullType extends DelegatingSimpleType implements CustomTypeParameter, DefinitelyNotNullTypeMarker {
    public static final Companion Companion = new Companion(null);
    private final SimpleType original;
    private final boolean useCorrectedNullabilityForTypeParameters;

    public /* synthetic */ DefinitelyNotNullType(SimpleType simpleType, boolean z, DefaultConstructorMarker defaultConstructorMarker) {
        this(simpleType, z);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.DelegatingSimpleType, kotlin.reflect.jvm.internal.impl.types.KotlinType
    public boolean isMarkedNullable() {
        return false;
    }

    public final SimpleType getOriginal() {
        return this.original;
    }

    private DefinitelyNotNullType(SimpleType simpleType, boolean z) {
        this.original = simpleType;
        this.useCorrectedNullabilityForTypeParameters = z;
    }

    /* JADX INFO: compiled from: SpecialTypes.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public static /* synthetic */ DefinitelyNotNullType makeDefinitelyNotNull$default(Companion companion, UnwrappedType unwrappedType, boolean z, boolean z2, int i, Object obj) {
            if ((i & 2) != 0) {
                z = false;
            }
            if ((i & 4) != 0) {
                z2 = false;
            }
            return companion.makeDefinitelyNotNull(unwrappedType, z, z2);
        }

        public final DefinitelyNotNullType makeDefinitelyNotNull(UnwrappedType type, boolean z, boolean z2) {
            Intrinsics.checkNotNullParameter(type, "type");
            if (type instanceof DefinitelyNotNullType) {
                return (DefinitelyNotNullType) type;
            }
            DefaultConstructorMarker defaultConstructorMarker = null;
            if (!z2 && !makesSenseToBeDefinitelyNotNull(type, z)) {
                return null;
            }
            if (type instanceof FlexibleType) {
                FlexibleType flexibleType = (FlexibleType) type;
                Intrinsics.areEqual(flexibleType.getLowerBound().getConstructor(), flexibleType.getUpperBound().getConstructor());
            }
            return new DefinitelyNotNullType(FlexibleTypesKt.lowerIfFlexible(type).makeNullableAsSpecified(false), z, defaultConstructorMarker);
        }

        private final boolean makesSenseToBeDefinitelyNotNull(UnwrappedType unwrappedType, boolean z) {
            if (!canHaveUndefinedNullability(unwrappedType)) {
                return false;
            }
            if (unwrappedType instanceof StubTypeForBuilderInference) {
                return TypeUtils.isNullableType(unwrappedType);
            }
            ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = unwrappedType.getConstructor().mo1491getDeclarationDescriptor();
            TypeParameterDescriptorImpl typeParameterDescriptorImpl = classifierDescriptorMo1491getDeclarationDescriptor instanceof TypeParameterDescriptorImpl ? (TypeParameterDescriptorImpl) classifierDescriptorMo1491getDeclarationDescriptor : null;
            if (typeParameterDescriptorImpl != null && !typeParameterDescriptorImpl.isInitialized()) {
                return true;
            }
            if (z && (unwrappedType.getConstructor().mo1491getDeclarationDescriptor() instanceof TypeParameterDescriptor)) {
                return TypeUtils.isNullableType(unwrappedType);
            }
            return !NullabilityChecker.INSTANCE.isSubtypeOfAny(unwrappedType);
        }

        private final boolean canHaveUndefinedNullability(UnwrappedType unwrappedType) {
            return (unwrappedType.getConstructor() instanceof NewTypeVariableConstructor) || (unwrappedType.getConstructor().mo1491getDeclarationDescriptor() instanceof TypeParameterDescriptor) || (unwrappedType instanceof NewCapturedType) || (unwrappedType instanceof StubTypeForBuilderInference);
        }
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.DelegatingSimpleType
    protected SimpleType getDelegate() {
        return this.original;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.CustomTypeParameter
    public boolean isTypeParameter() {
        return (getDelegate().getConstructor() instanceof NewTypeVariableConstructor) || (getDelegate().getConstructor().mo1491getDeclarationDescriptor() instanceof TypeParameterDescriptor);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.CustomTypeParameter
    public KotlinType substitutionResult(KotlinType replacement) {
        Intrinsics.checkNotNullParameter(replacement, "replacement");
        return SpecialTypesKt.makeDefinitelyNotNullOrNotNull(replacement.unwrap(), this.useCorrectedNullabilityForTypeParameters);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.UnwrappedType
    public SimpleType replaceAttributes(TypeAttributes newAttributes) {
        Intrinsics.checkNotNullParameter(newAttributes, "newAttributes");
        return new DefinitelyNotNullType(getDelegate().replaceAttributes(newAttributes), this.useCorrectedNullabilityForTypeParameters);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.UnwrappedType
    public SimpleType makeNullableAsSpecified(boolean z) {
        return z ? getDelegate().makeNullableAsSpecified(z) : this;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.SimpleType
    public String toString() {
        return getDelegate() + " & Any";
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.DelegatingSimpleType
    public DefinitelyNotNullType replaceDelegate(SimpleType delegate) {
        Intrinsics.checkNotNullParameter(delegate, "delegate");
        return new DefinitelyNotNullType(delegate, this.useCorrectedNullabilityForTypeParameters);
    }
}
