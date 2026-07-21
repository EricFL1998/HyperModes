package kotlin.reflect.jvm.internal.impl.descriptors;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: ModalityUtils.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ModalityUtilsKt {
    public static final boolean isFinalClass(ClassDescriptor classDescriptor) {
        Intrinsics.checkNotNullParameter(classDescriptor, "<this>");
        return classDescriptor.getModality() == Modality.FINAL && classDescriptor.getKind() != ClassKind.ENUM_CLASS;
    }
}
