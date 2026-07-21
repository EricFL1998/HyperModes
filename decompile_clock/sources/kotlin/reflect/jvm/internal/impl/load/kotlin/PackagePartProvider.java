package kotlin.reflect.jvm.internal.impl.load.kotlin;

import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: PackagePartProvider.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface PackagePartProvider {
    List<String> findPackageParts(String str);

    /* JADX INFO: compiled from: PackagePartProvider.kt */
    public static final class Empty implements PackagePartProvider {
        public static final Empty INSTANCE = new Empty();

        private Empty() {
        }

        @Override // kotlin.reflect.jvm.internal.impl.load.kotlin.PackagePartProvider
        public List<String> findPackageParts(String packageFqName) {
            Intrinsics.checkNotNullParameter(packageFqName, "packageFqName");
            return CollectionsKt.emptyList();
        }
    }
}
