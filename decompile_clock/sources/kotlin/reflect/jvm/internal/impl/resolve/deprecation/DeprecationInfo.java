package kotlin.reflect.jvm.internal.impl.resolve.deprecation;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: DeprecationInfo.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class DeprecationInfo implements Comparable<DeprecationInfo> {
    public abstract DeprecationLevelValue getDeprecationLevel();

    public abstract boolean getPropagatesToOverrides();

    @Override // java.lang.Comparable
    public int compareTo(DeprecationInfo other) {
        Intrinsics.checkNotNullParameter(other, "other");
        int iCompareTo = getDeprecationLevel().compareTo(other.getDeprecationLevel());
        if (iCompareTo == 0 && !getPropagatesToOverrides() && other.getPropagatesToOverrides()) {
            return 1;
        }
        return iCompareTo;
    }
}
