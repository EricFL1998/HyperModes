package kotlin.reflect.jvm.internal.impl.metadata.deserialization;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: versionSpecificBehavior.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class VersionSpecificBehaviorKt {
    public static final boolean isVersionRequirementTableWrittenCorrectly(BinaryVersion version) {
        Intrinsics.checkNotNullParameter(version, "version");
        return isKotlin1Dot4OrLater(version);
    }

    public static final boolean isKotlin1Dot4OrLater(BinaryVersion version) {
        Intrinsics.checkNotNullParameter(version, "version");
        return (version.getMajor() == 1 && version.getMinor() >= 4) || version.getMajor() > 1;
    }
}
