package kotlin.reflect.jvm.internal.impl.load.java;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;

/* JADX INFO: compiled from: ReportLevel.kt */
/* JADX INFO: loaded from: classes2.dex */
public enum ReportLevel {
    IGNORE("ignore"),
    WARN("warn"),
    STRICT("strict");

    private final String description;
    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());
    public static final Companion Companion = new Companion(null);

    ReportLevel(String str) {
        this.description = str;
    }

    public final String getDescription() {
        return this.description;
    }

    /* JADX INFO: compiled from: ReportLevel.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    public final boolean isWarning() {
        return this == WARN;
    }

    public final boolean isIgnore() {
        return this == IGNORE;
    }
}
