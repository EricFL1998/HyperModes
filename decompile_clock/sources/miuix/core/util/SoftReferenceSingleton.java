package miuix.core.util;

import java.lang.ref.SoftReference;

/* JADX INFO: loaded from: classes2.dex */
public abstract class SoftReferenceSingleton<T> {
    private SoftReference<T> mInstance = null;

    protected T createInstance() {
        return null;
    }

    protected T createInstance(Object obj) {
        return null;
    }

    protected void updateInstance(T t) {
    }

    protected void updateInstance(T t, Object obj) {
    }

    public final T get() {
        T tCreateInstance;
        synchronized (this) {
            SoftReference<T> softReference = this.mInstance;
            if (softReference == null || (tCreateInstance = softReference.get()) == null) {
                tCreateInstance = createInstance();
                this.mInstance = new SoftReference<>(tCreateInstance);
            } else {
                updateInstance(tCreateInstance);
            }
        }
        return tCreateInstance;
    }

    public final T get(Object obj) {
        T tCreateInstance;
        synchronized (this) {
            SoftReference<T> softReference = this.mInstance;
            if (softReference == null || (tCreateInstance = softReference.get()) == null) {
                tCreateInstance = createInstance(obj);
                this.mInstance = new SoftReference<>(tCreateInstance);
            } else {
                updateInstance(tCreateInstance, obj);
            }
        }
        return tCreateInstance;
    }
}
