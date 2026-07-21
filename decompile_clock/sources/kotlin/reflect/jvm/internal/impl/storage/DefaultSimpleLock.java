package kotlin.reflect.jvm.internal.impl.storage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: locks.kt */
/* JADX INFO: loaded from: classes2.dex */
public class DefaultSimpleLock implements SimpleLock {
    private final Lock lock;

    public DefaultSimpleLock() {
        this(null, 1, 0 == true ? 1 : 0);
    }

    public DefaultSimpleLock(Lock lock) {
        Intrinsics.checkNotNullParameter(lock, "lock");
        this.lock = lock;
    }

    public /* synthetic */ DefaultSimpleLock(ReentrantLock reentrantLock, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? new ReentrantLock() : reentrantLock);
    }

    protected final Lock getLock() {
        return this.lock;
    }

    @Override // kotlin.reflect.jvm.internal.impl.storage.SimpleLock
    public void lock() {
        this.lock.lock();
    }

    @Override // kotlin.reflect.jvm.internal.impl.storage.SimpleLock
    public void unlock() {
        this.lock.unlock();
    }
}
