package kotlin.reflect.jvm.internal.impl.incremental.components;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: LookupTracker.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface LookupTracker {
    boolean getRequiresPosition();

    void record(String str, Position position, String str2, ScopeKind scopeKind, String str3);

    /* JADX INFO: compiled from: LookupTracker.kt */
    public static final class DO_NOTHING implements LookupTracker {
        public static final DO_NOTHING INSTANCE = new DO_NOTHING();

        @Override // kotlin.reflect.jvm.internal.impl.incremental.components.LookupTracker
        public boolean getRequiresPosition() {
            return false;
        }

        @Override // kotlin.reflect.jvm.internal.impl.incremental.components.LookupTracker
        public void record(String filePath, Position position, String scopeFqName, ScopeKind scopeKind, String name) {
            Intrinsics.checkNotNullParameter(filePath, "filePath");
            Intrinsics.checkNotNullParameter(position, "position");
            Intrinsics.checkNotNullParameter(scopeFqName, "scopeFqName");
            Intrinsics.checkNotNullParameter(scopeKind, "scopeKind");
            Intrinsics.checkNotNullParameter(name, "name");
        }

        private DO_NOTHING() {
        }
    }
}
