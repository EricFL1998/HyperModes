package kotlin.reflect.jvm.internal.impl.types;

import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: TypeWithEnhancement.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class TypeWithEnhancementKt {
    /* JADX WARN: Multi-variable type inference failed */
    public static final KotlinType getEnhancement(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        if (kotlinType instanceof TypeWithEnhancement) {
            return ((TypeWithEnhancement) kotlinType).getEnhancement();
        }
        return null;
    }

    public static final UnwrappedType inheritEnhancement(UnwrappedType unwrappedType, KotlinType origin, Function1<? super KotlinType, ? extends KotlinType> transform) {
        Intrinsics.checkNotNullParameter(unwrappedType, "<this>");
        Intrinsics.checkNotNullParameter(origin, "origin");
        Intrinsics.checkNotNullParameter(transform, "transform");
        KotlinType enhancement = getEnhancement(origin);
        return wrapEnhancement(unwrappedType, enhancement != null ? transform.invoke(enhancement) : null);
    }

    public static final UnwrappedType inheritEnhancement(UnwrappedType unwrappedType, KotlinType origin) {
        Intrinsics.checkNotNullParameter(unwrappedType, "<this>");
        Intrinsics.checkNotNullParameter(origin, "origin");
        return wrapEnhancement(unwrappedType, getEnhancement(origin));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static final UnwrappedType wrapEnhancement(UnwrappedType unwrappedType, KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(unwrappedType, "<this>");
        if (unwrappedType instanceof TypeWithEnhancement) {
            return wrapEnhancement(((TypeWithEnhancement) unwrappedType).getOrigin(), kotlinType);
        }
        if (kotlinType == null || Intrinsics.areEqual(kotlinType, unwrappedType)) {
            return unwrappedType;
        }
        if (unwrappedType instanceof SimpleType) {
            return new SimpleTypeWithEnhancement((SimpleType) unwrappedType, kotlinType);
        }
        if (unwrappedType instanceof FlexibleType) {
            return new FlexibleTypeWithEnhancement((FlexibleType) unwrappedType, kotlinType);
        }
        throw new NoWhenBranchMatchedException();
    }
}
