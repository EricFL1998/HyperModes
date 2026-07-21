package kotlin.reflect.jvm.internal.impl.types;

import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.renderer.DescriptorRenderer;
import kotlin.reflect.jvm.internal.impl.renderer.DescriptorRendererOptions;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeChecker;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeRefiner;
import kotlin.reflect.jvm.internal.impl.types.model.KotlinTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt;

/* JADX INFO: compiled from: flexibleTypes.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class FlexibleTypeImpl extends FlexibleType implements CustomTypeParameter {
    public static final Companion Companion = new Companion(null);
    public static boolean RUN_SLOW_ASSERTIONS;
    private boolean assertionsDone;

    /* JADX INFO: compiled from: flexibleTypes.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public FlexibleTypeImpl(SimpleType lowerBound, SimpleType upperBound) {
        super(lowerBound, upperBound);
        Intrinsics.checkNotNullParameter(lowerBound, "lowerBound");
        Intrinsics.checkNotNullParameter(upperBound, "upperBound");
    }

    private final void runAssertions() {
        if (!RUN_SLOW_ASSERTIONS || this.assertionsDone) {
            return;
        }
        this.assertionsDone = true;
        FlexibleTypesKt.isFlexible(getLowerBound());
        FlexibleTypesKt.isFlexible(getUpperBound());
        Intrinsics.areEqual(getLowerBound(), getUpperBound());
        KotlinTypeChecker.DEFAULT.isSubtypeOf(getLowerBound(), getUpperBound());
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.FlexibleType
    public SimpleType getDelegate() {
        runAssertions();
        return getLowerBound();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.CustomTypeParameter
    public boolean isTypeParameter() {
        return (getLowerBound().getConstructor().mo1491getDeclarationDescriptor() instanceof TypeParameterDescriptor) && Intrinsics.areEqual(getLowerBound().getConstructor(), getUpperBound().getConstructor());
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.CustomTypeParameter
    public KotlinType substitutionResult(KotlinType replacement) {
        UnwrappedType unwrappedTypeFlexibleType;
        Intrinsics.checkNotNullParameter(replacement, "replacement");
        UnwrappedType unwrappedTypeUnwrap = replacement.unwrap();
        if (unwrappedTypeUnwrap instanceof FlexibleType) {
            unwrappedTypeFlexibleType = unwrappedTypeUnwrap;
        } else {
            if (!(unwrappedTypeUnwrap instanceof SimpleType)) {
                throw new NoWhenBranchMatchedException();
            }
            SimpleType simpleType = (SimpleType) unwrappedTypeUnwrap;
            unwrappedTypeFlexibleType = KotlinTypeFactory.flexibleType(simpleType, simpleType.makeNullableAsSpecified(true));
        }
        return TypeWithEnhancementKt.inheritEnhancement(unwrappedTypeFlexibleType, unwrappedTypeUnwrap);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.UnwrappedType
    public UnwrappedType replaceAttributes(TypeAttributes newAttributes) {
        Intrinsics.checkNotNullParameter(newAttributes, "newAttributes");
        return KotlinTypeFactory.flexibleType(getLowerBound().replaceAttributes(newAttributes), getUpperBound().replaceAttributes(newAttributes));
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.FlexibleType
    public String render(DescriptorRenderer renderer, DescriptorRendererOptions options) {
        Intrinsics.checkNotNullParameter(renderer, "renderer");
        Intrinsics.checkNotNullParameter(options, "options");
        if (options.getDebugMode()) {
            return "(" + renderer.renderType(getLowerBound()) + ".." + renderer.renderType(getUpperBound()) + ')';
        }
        return renderer.renderFlexibleType(renderer.renderType(getLowerBound()), renderer.renderType(getUpperBound()), TypeUtilsKt.getBuiltIns(this));
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.FlexibleType
    public String toString() {
        return "(" + getLowerBound() + ".." + getUpperBound() + ')';
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.UnwrappedType
    public UnwrappedType makeNullableAsSpecified(boolean z) {
        return KotlinTypeFactory.flexibleType(getLowerBound().makeNullableAsSpecified(z), getUpperBound().makeNullableAsSpecified(z));
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.UnwrappedType, kotlin.reflect.jvm.internal.impl.types.KotlinType
    public FlexibleType refine(KotlinTypeRefiner kotlinTypeRefiner) {
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "kotlinTypeRefiner");
        KotlinType kotlinTypeRefineType = kotlinTypeRefiner.refineType((KotlinTypeMarker) getLowerBound());
        Intrinsics.checkNotNull(kotlinTypeRefineType, "null cannot be cast to non-null type org.jetbrains.kotlin.types.SimpleType");
        KotlinType kotlinTypeRefineType2 = kotlinTypeRefiner.refineType((KotlinTypeMarker) getUpperBound());
        Intrinsics.checkNotNull(kotlinTypeRefineType2, "null cannot be cast to non-null type org.jetbrains.kotlin.types.SimpleType");
        return new FlexibleTypeImpl((SimpleType) kotlinTypeRefineType, (SimpleType) kotlinTypeRefineType2);
    }
}
