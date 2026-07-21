package kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: TypeComponentPosition.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class TypeComponentPositionKt {
    public static final boolean shouldEnhance(TypeComponentPosition typeComponentPosition) {
        Intrinsics.checkNotNullParameter(typeComponentPosition, "<this>");
        return typeComponentPosition != TypeComponentPosition.INFLEXIBLE;
    }
}
