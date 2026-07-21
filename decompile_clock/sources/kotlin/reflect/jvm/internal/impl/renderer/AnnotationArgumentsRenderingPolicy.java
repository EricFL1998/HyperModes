package kotlin.reflect.jvm.internal.impl.renderer;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;

/* JADX INFO: compiled from: DescriptorRenderer.kt */
/* JADX INFO: loaded from: classes2.dex */
public enum AnnotationArgumentsRenderingPolicy {
    NO_ARGUMENTS(false, false, 3, null),
    UNLESS_EMPTY(true, false, 2, null),
    ALWAYS_PARENTHESIZED(true, true);

    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());
    private final boolean includeAnnotationArguments;
    private final boolean includeEmptyAnnotationArguments;

    AnnotationArgumentsRenderingPolicy(boolean z, boolean z2) {
        this.includeAnnotationArguments = z;
        this.includeEmptyAnnotationArguments = z2;
    }

    /* synthetic */ AnnotationArgumentsRenderingPolicy(boolean z, boolean z2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? false : z, (i & 2) != 0 ? false : z2);
    }

    public final boolean getIncludeAnnotationArguments() {
        return this.includeAnnotationArguments;
    }

    public final boolean getIncludeEmptyAnnotationArguments() {
        return this.includeEmptyAnnotationArguments;
    }
}
