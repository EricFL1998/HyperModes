package kotlin.reflect.jvm.internal.impl.load.java;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX INFO: compiled from: AnnotationQualifierApplicabilityType.kt */
/* JADX INFO: loaded from: classes2.dex */
public enum AnnotationQualifierApplicabilityType {
    METHOD_RETURN_TYPE("METHOD"),
    VALUE_PARAMETER("PARAMETER"),
    FIELD("FIELD"),
    TYPE_USE("TYPE_USE"),
    TYPE_PARAMETER_BOUNDS("TYPE_USE"),
    TYPE_PARAMETER("TYPE_PARAMETER");

    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());
    private final String javaTarget;

    AnnotationQualifierApplicabilityType(String str) {
        this.javaTarget = str;
    }

    public final String getJavaTarget() {
        return this.javaTarget;
    }
}
