package kotlin.reflect.jvm.internal.impl.types;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: KotlinTypeFactory.kt */
/* JADX INFO: loaded from: classes2.dex */
final class SimpleTypeWithAttributes extends DelegatingSimpleTypeImpl {
    private final TypeAttributes attributes;

    @Override // kotlin.reflect.jvm.internal.impl.types.DelegatingSimpleType, kotlin.reflect.jvm.internal.impl.types.KotlinType
    public TypeAttributes getAttributes() {
        return this.attributes;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public SimpleTypeWithAttributes(SimpleType delegate, TypeAttributes attributes) {
        super(delegate);
        Intrinsics.checkNotNullParameter(delegate, "delegate");
        Intrinsics.checkNotNullParameter(attributes, "attributes");
        this.attributes = attributes;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.DelegatingSimpleType
    public SimpleTypeWithAttributes replaceDelegate(SimpleType delegate) {
        Intrinsics.checkNotNullParameter(delegate, "delegate");
        return new SimpleTypeWithAttributes(delegate, getAttributes());
    }
}
