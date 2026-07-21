package kotlin.reflect.jvm.internal.impl.types;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.renderer.DescriptorRenderer;
import kotlin.reflect.jvm.internal.impl.renderer.DescriptorRendererOptions;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeRefiner;
import kotlin.reflect.jvm.internal.impl.types.model.KotlinTypeMarker;

/* JADX INFO: compiled from: TypeWithEnhancement.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class FlexibleTypeWithEnhancement extends FlexibleType implements TypeWithEnhancement {
    private final KotlinType enhancement;
    private final FlexibleType origin;

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeWithEnhancement
    public FlexibleType getOrigin() {
        return this.origin;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeWithEnhancement
    public KotlinType getEnhancement() {
        return this.enhancement;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public FlexibleTypeWithEnhancement(FlexibleType origin, KotlinType enhancement) {
        super(origin.getLowerBound(), origin.getUpperBound());
        Intrinsics.checkNotNullParameter(origin, "origin");
        Intrinsics.checkNotNullParameter(enhancement, "enhancement");
        this.origin = origin;
        this.enhancement = enhancement;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.UnwrappedType
    public UnwrappedType replaceAttributes(TypeAttributes newAttributes) {
        Intrinsics.checkNotNullParameter(newAttributes, "newAttributes");
        return TypeWithEnhancementKt.wrapEnhancement(getOrigin().replaceAttributes(newAttributes), getEnhancement());
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.UnwrappedType
    public UnwrappedType makeNullableAsSpecified(boolean z) {
        return TypeWithEnhancementKt.wrapEnhancement(getOrigin().makeNullableAsSpecified(z), getEnhancement().unwrap().makeNullableAsSpecified(z));
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.FlexibleType
    public String render(DescriptorRenderer renderer, DescriptorRendererOptions options) {
        Intrinsics.checkNotNullParameter(renderer, "renderer");
        Intrinsics.checkNotNullParameter(options, "options");
        if (options.getEnhancedTypes()) {
            return renderer.renderType(getEnhancement());
        }
        return getOrigin().render(renderer, options);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.FlexibleType
    public SimpleType getDelegate() {
        return getOrigin().getDelegate();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.UnwrappedType, kotlin.reflect.jvm.internal.impl.types.KotlinType
    public FlexibleTypeWithEnhancement refine(KotlinTypeRefiner kotlinTypeRefiner) {
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "kotlinTypeRefiner");
        KotlinType kotlinTypeRefineType = kotlinTypeRefiner.refineType((KotlinTypeMarker) getOrigin());
        Intrinsics.checkNotNull(kotlinTypeRefineType, "null cannot be cast to non-null type org.jetbrains.kotlin.types.FlexibleType");
        return new FlexibleTypeWithEnhancement((FlexibleType) kotlinTypeRefineType, kotlinTypeRefiner.refineType((KotlinTypeMarker) getEnhancement()));
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.FlexibleType
    public String toString() {
        return "[@EnhancedForWarnings(" + getEnhancement() + ")] " + getOrigin();
    }
}
