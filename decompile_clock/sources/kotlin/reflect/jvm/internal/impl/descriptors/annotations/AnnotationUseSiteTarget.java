package kotlin.reflect.jvm.internal.impl.descriptors.annotations;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.reflect.jvm.internal.impl.util.capitalizeDecapitalize.CapitalizeDecapitalizeKt;

/* JADX INFO: compiled from: AnnotationUseSiteTarget.kt */
/* JADX INFO: loaded from: classes2.dex */
public enum AnnotationUseSiteTarget {
    FIELD(null, 1, null),
    FILE(null, 1, null),
    PROPERTY(null, 1, null),
    PROPERTY_GETTER("get"),
    PROPERTY_SETTER("set"),
    RECEIVER(null, 1, null),
    CONSTRUCTOR_PARAMETER("param"),
    SETTER_PARAMETER("setparam"),
    PROPERTY_DELEGATE_FIELD("delegate");

    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());
    private final String renderName;

    AnnotationUseSiteTarget(String str) {
        this.renderName = str == null ? CapitalizeDecapitalizeKt.toLowerCaseAsciiOnly(name()) : str;
    }

    /* synthetic */ AnnotationUseSiteTarget(String str, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : str);
    }

    public final String getRenderName() {
        return this.renderName;
    }
}
