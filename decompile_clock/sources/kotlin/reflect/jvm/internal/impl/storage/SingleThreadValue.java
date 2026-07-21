package kotlin.reflect.jvm.internal.impl.storage;

/* JADX INFO: loaded from: classes2.dex */
class SingleThreadValue<T> {
    private final Thread thread = Thread.currentThread();
    private final T value;

    SingleThreadValue(T t) {
        this.value = t;
    }

    public boolean hasValue() {
        return this.thread == Thread.currentThread();
    }

    public T getValue() {
        if (!hasValue()) {
            throw new IllegalStateException("No value in this thread (hasValue should be checked before)");
        }
        return this.value;
    }
}
