package kotlin.reflect.jvm.internal.impl.load.java.lazy.types;

import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.types.TypeUsage;

/* JADX INFO: compiled from: JavaTypeAttributes.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JavaTypeAttributesKt {
    public static /* synthetic */ JavaTypeAttributes toAttributes$default(TypeUsage typeUsage, boolean z, boolean z2, TypeParameterDescriptor typeParameterDescriptor, int i, Object obj) {
        if ((i & 1) != 0) {
            z = false;
        }
        if ((i & 2) != 0) {
            z2 = false;
        }
        if ((i & 4) != 0) {
            typeParameterDescriptor = null;
        }
        return toAttributes(typeUsage, z, z2, typeParameterDescriptor);
    }

    public static final JavaTypeAttributes toAttributes(TypeUsage typeUsage, boolean z, boolean z2, TypeParameterDescriptor typeParameterDescriptor) {
        Intrinsics.checkNotNullParameter(typeUsage, "<this>");
        return new JavaTypeAttributes(typeUsage, null, z2, z, typeParameterDescriptor != null ? SetsKt.setOf(typeParameterDescriptor) : null, null, 34, null);
    }
}
