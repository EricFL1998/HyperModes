package kotlin.reflect.jvm.internal.impl.types;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: KotlinTypeFactory.kt */
/* JADX INFO: loaded from: classes2.dex */
final class NullableSimpleType extends DelegatingSimpleTypeImpl {
    @Override // kotlin.reflect.jvm.internal.impl.types.DelegatingSimpleType, kotlin.reflect.jvm.internal.impl.types.KotlinType
    public boolean isMarkedNullable() {
        return true;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public NullableSimpleType(SimpleType delegate) {
        super(delegate);
        Intrinsics.checkNotNullParameter(delegate, "delegate");
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.DelegatingSimpleType
    public NullableSimpleType replaceDelegate(SimpleType delegate) {
        Intrinsics.checkNotNullParameter(delegate, "delegate");
        return new NullableSimpleType(delegate);
    }
}
