package kotlin.reflect.jvm.internal.impl.descriptors;

import com.xiaomi.onetrack.api.g;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX INFO: compiled from: ClassKind.kt */
/* JADX INFO: loaded from: classes2.dex */
public enum ClassKind {
    CLASS(g.r),
    INTERFACE("interface"),
    ENUM_CLASS("enum class"),
    ENUM_ENTRY(null),
    ANNOTATION_CLASS("annotation class"),
    OBJECT("object");

    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());
    private final String codeRepresentation;

    ClassKind(String str) {
        this.codeRepresentation = str;
    }

    public final boolean isSingleton() {
        return this == OBJECT || this == ENUM_ENTRY;
    }
}
