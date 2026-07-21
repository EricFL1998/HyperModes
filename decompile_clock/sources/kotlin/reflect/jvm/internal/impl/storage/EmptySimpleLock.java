package kotlin.reflect.jvm.internal.impl.storage;

/* JADX INFO: compiled from: locks.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class EmptySimpleLock implements SimpleLock {
    public static final EmptySimpleLock INSTANCE = new EmptySimpleLock();

    @Override // kotlin.reflect.jvm.internal.impl.storage.SimpleLock
    public void lock() {
    }

    @Override // kotlin.reflect.jvm.internal.impl.storage.SimpleLock
    public void unlock() {
    }

    private EmptySimpleLock() {
    }
}
