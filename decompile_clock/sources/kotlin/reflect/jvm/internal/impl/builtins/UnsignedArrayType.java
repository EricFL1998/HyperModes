package kotlin.reflect.jvm.internal.impl.builtins;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.Name;

/* JADX INFO: compiled from: UnsignedType.kt */
/* JADX INFO: loaded from: classes2.dex */
public enum UnsignedArrayType {
    UBYTEARRAY(ClassId.Companion.fromString$default(ClassId.Companion, "kotlin/UByteArray", false, 2, null)),
    USHORTARRAY(ClassId.Companion.fromString$default(ClassId.Companion, "kotlin/UShortArray", false, 2, null)),
    UINTARRAY(ClassId.Companion.fromString$default(ClassId.Companion, "kotlin/UIntArray", false, 2, null)),
    ULONGARRAY(ClassId.Companion.fromString$default(ClassId.Companion, "kotlin/ULongArray", false, 2, null));

    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());
    private final ClassId classId;
    private final Name typeName;

    UnsignedArrayType(ClassId classId) {
        this.classId = classId;
        this.typeName = classId.getShortClassName();
    }

    public final Name getTypeName() {
        return this.typeName;
    }
}
