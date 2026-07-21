package kotlin.reflect.jvm.internal.impl.load.java;

import java.util.List;
import java.util.Map;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.name.FqName;

/* JADX INFO: compiled from: Jsr305Settings.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class Jsr305Settings {
    private final Lazy description$delegate;
    private final ReportLevel globalLevel;
    private final boolean isDisabled;
    private final ReportLevel migrationLevel;
    private final Map<FqName, ReportLevel> userDefinedLevelForSpecificAnnotation;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Jsr305Settings)) {
            return false;
        }
        Jsr305Settings jsr305Settings = (Jsr305Settings) obj;
        return this.globalLevel == jsr305Settings.globalLevel && this.migrationLevel == jsr305Settings.migrationLevel && Intrinsics.areEqual(this.userDefinedLevelForSpecificAnnotation, jsr305Settings.userDefinedLevelForSpecificAnnotation);
    }

    public int hashCode() {
        int iHashCode = this.globalLevel.hashCode() * 31;
        ReportLevel reportLevel = this.migrationLevel;
        return ((iHashCode + (reportLevel == null ? 0 : reportLevel.hashCode())) * 31) + this.userDefinedLevelForSpecificAnnotation.hashCode();
    }

    public String toString() {
        return "Jsr305Settings(globalLevel=" + this.globalLevel + ", migrationLevel=" + this.migrationLevel + ", userDefinedLevelForSpecificAnnotation=" + this.userDefinedLevelForSpecificAnnotation + ')';
    }

    /* JADX WARN: Multi-variable type inference failed */
    public Jsr305Settings(ReportLevel globalLevel, ReportLevel reportLevel, Map<FqName, ? extends ReportLevel> userDefinedLevelForSpecificAnnotation) {
        Intrinsics.checkNotNullParameter(globalLevel, "globalLevel");
        Intrinsics.checkNotNullParameter(userDefinedLevelForSpecificAnnotation, "userDefinedLevelForSpecificAnnotation");
        this.globalLevel = globalLevel;
        this.migrationLevel = reportLevel;
        this.userDefinedLevelForSpecificAnnotation = userDefinedLevelForSpecificAnnotation;
        this.description$delegate = LazyKt.lazy(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.load.java.Jsr305Settings$$Lambda$0
            private final Jsr305Settings arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return Jsr305Settings.description_delegate$lambda$3(this.arg$0);
            }
        });
        this.isDisabled = globalLevel == ReportLevel.IGNORE && reportLevel == ReportLevel.IGNORE && userDefinedLevelForSpecificAnnotation.isEmpty();
    }

    public final ReportLevel getGlobalLevel() {
        return this.globalLevel;
    }

    public final ReportLevel getMigrationLevel() {
        return this.migrationLevel;
    }

    public /* synthetic */ Jsr305Settings(ReportLevel reportLevel, ReportLevel reportLevel2, Map map, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(reportLevel, (i & 2) != 0 ? null : reportLevel2, (i & 4) != 0 ? MapsKt.emptyMap() : map);
    }

    public final Map<FqName, ReportLevel> getUserDefinedLevelForSpecificAnnotation() {
        return this.userDefinedLevelForSpecificAnnotation;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String[] description_delegate$lambda$3(Jsr305Settings this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        List listCreateListBuilder = CollectionsKt.createListBuilder();
        listCreateListBuilder.add(this$0.globalLevel.getDescription());
        ReportLevel reportLevel = this$0.migrationLevel;
        if (reportLevel != null) {
            listCreateListBuilder.add("under-migration:" + reportLevel.getDescription());
        }
        for (Map.Entry<FqName, ReportLevel> entry : this$0.userDefinedLevelForSpecificAnnotation.entrySet()) {
            listCreateListBuilder.add("@" + entry.getKey() + ':' + entry.getValue().getDescription());
        }
        return (String[]) CollectionsKt.build(listCreateListBuilder).toArray(new String[0]);
    }

    public final boolean isDisabled() {
        return this.isDisabled;
    }
}
