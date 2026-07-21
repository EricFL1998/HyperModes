package kotlin.reflect.jvm.internal.impl.types;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: TypeCapabilities.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class TypeCapabilitiesKt {
    public static final boolean isCustomTypeParameter(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        Object objUnwrap = kotlinType.unwrap();
        CustomTypeParameter customTypeParameter = objUnwrap instanceof CustomTypeParameter ? (CustomTypeParameter) objUnwrap : null;
        if (customTypeParameter != null) {
            return customTypeParameter.isTypeParameter();
        }
        return false;
    }

    public static final CustomTypeParameter getCustomTypeParameter(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        Object objUnwrap = kotlinType.unwrap();
        CustomTypeParameter customTypeParameter = objUnwrap instanceof CustomTypeParameter ? (CustomTypeParameter) objUnwrap : null;
        if (customTypeParameter == null || !customTypeParameter.isTypeParameter()) {
            return null;
        }
        return customTypeParameter;
    }
}
