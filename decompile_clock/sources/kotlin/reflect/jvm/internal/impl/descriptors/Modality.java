package kotlin.reflect.jvm.internal.impl.descriptors;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;

/* JADX INFO: compiled from: Modality.kt */
/* JADX INFO: loaded from: classes2.dex */
public enum Modality {
    FINAL,
    SEALED,
    OPEN,
    ABSTRACT;

    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());
    public static final Companion Companion = new Companion(null);

    /* JADX INFO: compiled from: Modality.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final Modality convertFromFlags(boolean z, boolean z2, boolean z3) {
            if (z) {
                return Modality.SEALED;
            }
            if (z2) {
                return Modality.ABSTRACT;
            }
            if (z3) {
                return Modality.OPEN;
            }
            return Modality.FINAL;
        }
    }
}
