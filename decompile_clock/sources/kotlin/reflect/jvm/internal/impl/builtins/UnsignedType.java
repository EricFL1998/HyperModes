package kotlin.reflect.jvm.internal.impl.builtins;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.Name;

/* JADX INFO: compiled from: UnsignedType.kt */
/* JADX INFO: loaded from: classes2.dex */
public enum UnsignedType {
    UBYTE(ClassId.Companion.fromString$default(ClassId.Companion, "kotlin/UByte", false, 2, null)),
    USHORT(ClassId.Companion.fromString$default(ClassId.Companion, "kotlin/UShort", false, 2, null)),
    UINT(ClassId.Companion.fromString$default(ClassId.Companion, "kotlin/UInt", false, 2, null)),
    ULONG(ClassId.Companion.fromString$default(ClassId.Companion, "kotlin/ULong", false, 2, null));

    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());
    private final ClassId arrayClassId;
    private final ClassId classId;
    private final Name typeName;

    UnsignedType(ClassId classId) {
        this.classId = classId;
        Name shortClassName = classId.getShortClassName();
        this.typeName = shortClassName;
        FqName packageFqName = classId.getPackageFqName();
        Name nameIdentifier = Name.identifier(shortClassName.asString() + "Array");
        Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
        this.arrayClassId = new ClassId(packageFqName, nameIdentifier);
    }

    public final ClassId getClassId() {
        return this.classId;
    }

    public final Name getTypeName() {
        return this.typeName;
    }

    public final ClassId getArrayClassId() {
        return this.arrayClassId;
    }
}
