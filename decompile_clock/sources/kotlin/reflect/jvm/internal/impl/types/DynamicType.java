package kotlin.reflect.jvm.internal.impl.types;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns;
import kotlin.reflect.jvm.internal.impl.renderer.DescriptorRenderer;
import kotlin.reflect.jvm.internal.impl.renderer.DescriptorRendererOptions;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeRefiner;
import kotlin.reflect.jvm.internal.impl.types.model.DynamicTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt;

/* JADX INFO: compiled from: dynamicTypes.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class DynamicType extends FlexibleType implements DynamicTypeMarker {
    private final TypeAttributes attributes;

    @Override // kotlin.reflect.jvm.internal.impl.types.FlexibleType, kotlin.reflect.jvm.internal.impl.types.KotlinType
    public boolean isMarkedNullable() {
        return false;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.UnwrappedType
    public DynamicType makeNullableAsSpecified(boolean z) {
        return this;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.UnwrappedType, kotlin.reflect.jvm.internal.impl.types.KotlinType
    public DynamicType refine(KotlinTypeRefiner kotlinTypeRefiner) {
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "kotlinTypeRefiner");
        return this;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.FlexibleType, kotlin.reflect.jvm.internal.impl.types.KotlinType
    public TypeAttributes getAttributes() {
        return this.attributes;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public DynamicType(KotlinBuiltIns builtIns, TypeAttributes attributes) {
        Intrinsics.checkNotNullParameter(builtIns, "builtIns");
        Intrinsics.checkNotNullParameter(attributes, "attributes");
        SimpleType nothingType = builtIns.getNothingType();
        Intrinsics.checkNotNullExpressionValue(nothingType, "getNothingType(...)");
        SimpleType nullableAnyType = builtIns.getNullableAnyType();
        Intrinsics.checkNotNullExpressionValue(nullableAnyType, "getNullableAnyType(...)");
        super(nothingType, nullableAnyType);
        this.attributes = attributes;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.FlexibleType
    public SimpleType getDelegate() {
        return getUpperBound();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.UnwrappedType
    public DynamicType replaceAttributes(TypeAttributes newAttributes) {
        Intrinsics.checkNotNullParameter(newAttributes, "newAttributes");
        return new DynamicType(TypeUtilsKt.getBuiltIns(getDelegate()), newAttributes);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.FlexibleType
    public String render(DescriptorRenderer renderer, DescriptorRendererOptions options) {
        Intrinsics.checkNotNullParameter(renderer, "renderer");
        Intrinsics.checkNotNullParameter(options, "options");
        return "dynamic";
    }
}
