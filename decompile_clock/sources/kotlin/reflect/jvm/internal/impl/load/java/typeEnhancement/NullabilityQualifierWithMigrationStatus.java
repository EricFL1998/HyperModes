package kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: NullabilityQualifierWithMigrationStatus.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class NullabilityQualifierWithMigrationStatus {
    private final boolean isForWarningOnly;
    private final NullabilityQualifier qualifier;

    public static /* synthetic */ NullabilityQualifierWithMigrationStatus copy$default(NullabilityQualifierWithMigrationStatus nullabilityQualifierWithMigrationStatus, NullabilityQualifier nullabilityQualifier, boolean z, int i, Object obj) {
        if ((i & 1) != 0) {
            nullabilityQualifier = nullabilityQualifierWithMigrationStatus.qualifier;
        }
        if ((i & 2) != 0) {
            z = nullabilityQualifierWithMigrationStatus.isForWarningOnly;
        }
        return nullabilityQualifierWithMigrationStatus.copy(nullabilityQualifier, z);
    }

    public final NullabilityQualifierWithMigrationStatus copy(NullabilityQualifier qualifier, boolean z) {
        Intrinsics.checkNotNullParameter(qualifier, "qualifier");
        return new NullabilityQualifierWithMigrationStatus(qualifier, z);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NullabilityQualifierWithMigrationStatus)) {
            return false;
        }
        NullabilityQualifierWithMigrationStatus nullabilityQualifierWithMigrationStatus = (NullabilityQualifierWithMigrationStatus) obj;
        return this.qualifier == nullabilityQualifierWithMigrationStatus.qualifier && this.isForWarningOnly == nullabilityQualifierWithMigrationStatus.isForWarningOnly;
    }

    public int hashCode() {
        return (this.qualifier.hashCode() * 31) + Boolean.hashCode(this.isForWarningOnly);
    }

    public String toString() {
        return "NullabilityQualifierWithMigrationStatus(qualifier=" + this.qualifier + ", isForWarningOnly=" + this.isForWarningOnly + ')';
    }

    public NullabilityQualifierWithMigrationStatus(NullabilityQualifier qualifier, boolean z) {
        Intrinsics.checkNotNullParameter(qualifier, "qualifier");
        this.qualifier = qualifier;
        this.isForWarningOnly = z;
    }

    public /* synthetic */ NullabilityQualifierWithMigrationStatus(NullabilityQualifier nullabilityQualifier, boolean z, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(nullabilityQualifier, (i & 2) != 0 ? false : z);
    }

    public final NullabilityQualifier getQualifier() {
        return this.qualifier;
    }

    public final boolean isForWarningOnly() {
        return this.isForWarningOnly;
    }
}
