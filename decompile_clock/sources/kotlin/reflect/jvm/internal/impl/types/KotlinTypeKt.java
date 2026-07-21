package kotlin.reflect.jvm.internal.impl.types;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.types.error.ErrorType;

/* JADX INFO: compiled from: KotlinType.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class KotlinTypeKt {
    public static final boolean isNullable(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        return TypeUtils.isNullableType(kotlinType);
    }

    public static final boolean isError(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        UnwrappedType unwrappedTypeUnwrap = kotlinType.unwrap();
        return (unwrappedTypeUnwrap instanceof ErrorType) || ((unwrappedTypeUnwrap instanceof FlexibleType) && (((FlexibleType) unwrappedTypeUnwrap).getDelegate() instanceof ErrorType));
    }
}
