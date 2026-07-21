package kotlin.reflect.jvm.internal.impl.load.java;

import java.util.Collection;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.NullabilityQualifier;
import kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement.NullabilityQualifierWithMigrationStatus;

/* JADX INFO: compiled from: AnnotationQualifiersFqNames.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JavaDefaultQualifiers {
    private final boolean definitelyNotNull;
    private final NullabilityQualifierWithMigrationStatus nullabilityQualifier;
    private final Collection<AnnotationQualifierApplicabilityType> qualifierApplicabilityTypes;

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ JavaDefaultQualifiers copy$default(JavaDefaultQualifiers javaDefaultQualifiers, NullabilityQualifierWithMigrationStatus nullabilityQualifierWithMigrationStatus, Collection collection, boolean z, int i, Object obj) {
        if ((i & 1) != 0) {
            nullabilityQualifierWithMigrationStatus = javaDefaultQualifiers.nullabilityQualifier;
        }
        if ((i & 2) != 0) {
            collection = javaDefaultQualifiers.qualifierApplicabilityTypes;
        }
        if ((i & 4) != 0) {
            z = javaDefaultQualifiers.definitelyNotNull;
        }
        return javaDefaultQualifiers.copy(nullabilityQualifierWithMigrationStatus, collection, z);
    }

    public final JavaDefaultQualifiers copy(NullabilityQualifierWithMigrationStatus nullabilityQualifier, Collection<? extends AnnotationQualifierApplicabilityType> qualifierApplicabilityTypes, boolean z) {
        Intrinsics.checkNotNullParameter(nullabilityQualifier, "nullabilityQualifier");
        Intrinsics.checkNotNullParameter(qualifierApplicabilityTypes, "qualifierApplicabilityTypes");
        return new JavaDefaultQualifiers(nullabilityQualifier, qualifierApplicabilityTypes, z);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof JavaDefaultQualifiers)) {
            return false;
        }
        JavaDefaultQualifiers javaDefaultQualifiers = (JavaDefaultQualifiers) obj;
        return Intrinsics.areEqual(this.nullabilityQualifier, javaDefaultQualifiers.nullabilityQualifier) && Intrinsics.areEqual(this.qualifierApplicabilityTypes, javaDefaultQualifiers.qualifierApplicabilityTypes) && this.definitelyNotNull == javaDefaultQualifiers.definitelyNotNull;
    }

    public int hashCode() {
        return (((this.nullabilityQualifier.hashCode() * 31) + this.qualifierApplicabilityTypes.hashCode()) * 31) + Boolean.hashCode(this.definitelyNotNull);
    }

    public String toString() {
        return "JavaDefaultQualifiers(nullabilityQualifier=" + this.nullabilityQualifier + ", qualifierApplicabilityTypes=" + this.qualifierApplicabilityTypes + ", definitelyNotNull=" + this.definitelyNotNull + ')';
    }

    /* JADX WARN: Multi-variable type inference failed */
    public JavaDefaultQualifiers(NullabilityQualifierWithMigrationStatus nullabilityQualifier, Collection<? extends AnnotationQualifierApplicabilityType> qualifierApplicabilityTypes, boolean z) {
        Intrinsics.checkNotNullParameter(nullabilityQualifier, "nullabilityQualifier");
        Intrinsics.checkNotNullParameter(qualifierApplicabilityTypes, "qualifierApplicabilityTypes");
        this.nullabilityQualifier = nullabilityQualifier;
        this.qualifierApplicabilityTypes = qualifierApplicabilityTypes;
        this.definitelyNotNull = z;
    }

    public final NullabilityQualifierWithMigrationStatus getNullabilityQualifier() {
        return this.nullabilityQualifier;
    }

    public final Collection<AnnotationQualifierApplicabilityType> getQualifierApplicabilityTypes() {
        return this.qualifierApplicabilityTypes;
    }

    public /* synthetic */ JavaDefaultQualifiers(NullabilityQualifierWithMigrationStatus nullabilityQualifierWithMigrationStatus, Collection collection, boolean z, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(nullabilityQualifierWithMigrationStatus, collection, (i & 4) != 0 ? nullabilityQualifierWithMigrationStatus.getQualifier() == NullabilityQualifier.NOT_NULL : z);
    }

    public final boolean getDefinitelyNotNull() {
        return this.definitelyNotNull;
    }
}
