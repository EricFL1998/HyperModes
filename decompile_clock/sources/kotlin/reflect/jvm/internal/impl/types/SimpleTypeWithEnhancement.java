package kotlin.reflect.jvm.internal.impl.types;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeRefiner;
import kotlin.reflect.jvm.internal.impl.types.model.KotlinTypeMarker;

/* JADX INFO: compiled from: TypeWithEnhancement.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class SimpleTypeWithEnhancement extends DelegatingSimpleType implements TypeWithEnhancement {
    private final SimpleType delegate;
    private final KotlinType enhancement;

    @Override // kotlin.reflect.jvm.internal.impl.types.DelegatingSimpleType
    protected SimpleType getDelegate() {
        return this.delegate;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeWithEnhancement
    public KotlinType getEnhancement() {
        return this.enhancement;
    }

    public SimpleTypeWithEnhancement(SimpleType delegate, KotlinType enhancement) {
        Intrinsics.checkNotNullParameter(delegate, "delegate");
        Intrinsics.checkNotNullParameter(enhancement, "enhancement");
        this.delegate = delegate;
        this.enhancement = enhancement;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeWithEnhancement
    public SimpleType getOrigin() {
        return getDelegate();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.UnwrappedType
    public SimpleType replaceAttributes(TypeAttributes newAttributes) {
        Intrinsics.checkNotNullParameter(newAttributes, "newAttributes");
        UnwrappedType unwrappedTypeWrapEnhancement = TypeWithEnhancementKt.wrapEnhancement(getOrigin().replaceAttributes(newAttributes), getEnhancement());
        Intrinsics.checkNotNull(unwrappedTypeWrapEnhancement, "null cannot be cast to non-null type org.jetbrains.kotlin.types.SimpleType");
        return (SimpleType) unwrappedTypeWrapEnhancement;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.UnwrappedType
    public SimpleType makeNullableAsSpecified(boolean z) {
        UnwrappedType unwrappedTypeWrapEnhancement = TypeWithEnhancementKt.wrapEnhancement(getOrigin().makeNullableAsSpecified(z), getEnhancement().unwrap().makeNullableAsSpecified(z));
        Intrinsics.checkNotNull(unwrappedTypeWrapEnhancement, "null cannot be cast to non-null type org.jetbrains.kotlin.types.SimpleType");
        return (SimpleType) unwrappedTypeWrapEnhancement;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.DelegatingSimpleType
    public SimpleTypeWithEnhancement replaceDelegate(SimpleType delegate) {
        Intrinsics.checkNotNullParameter(delegate, "delegate");
        return new SimpleTypeWithEnhancement(delegate, getEnhancement());
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.DelegatingSimpleType, kotlin.reflect.jvm.internal.impl.types.UnwrappedType, kotlin.reflect.jvm.internal.impl.types.KotlinType
    public SimpleTypeWithEnhancement refine(KotlinTypeRefiner kotlinTypeRefiner) {
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "kotlinTypeRefiner");
        KotlinType kotlinTypeRefineType = kotlinTypeRefiner.refineType((KotlinTypeMarker) getDelegate());
        Intrinsics.checkNotNull(kotlinTypeRefineType, "null cannot be cast to non-null type org.jetbrains.kotlin.types.SimpleType");
        return new SimpleTypeWithEnhancement((SimpleType) kotlinTypeRefineType, kotlinTypeRefiner.refineType((KotlinTypeMarker) getEnhancement()));
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.SimpleType
    public String toString() {
        return "[@EnhancedForWarnings(" + getEnhancement() + ")] " + getOrigin();
    }
}
