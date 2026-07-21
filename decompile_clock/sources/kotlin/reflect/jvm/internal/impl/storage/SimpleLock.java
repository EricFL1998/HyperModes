package kotlin.reflect.jvm.internal.impl.storage;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/* JADX INFO: compiled from: locks.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface SimpleLock {
    public static final Companion Companion = Companion.$$INSTANCE;

    void lock();

    void unlock();

    /* JADX INFO: compiled from: locks.kt */
    public static final class Companion {
        static final /* synthetic */ Companion $$INSTANCE = new Companion();

        private Companion() {
        }

        public final DefaultSimpleLock simpleLock(Runnable runnable, Function1<? super InterruptedException, Unit> function1) {
            if (runnable != null && function1 != null) {
                return new CancellableSimpleLock(runnable, function1);
            }
            return new DefaultSimpleLock(null, 1, null);
        }
    }
}
