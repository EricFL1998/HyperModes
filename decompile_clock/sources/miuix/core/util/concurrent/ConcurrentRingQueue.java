package miuix.core.util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes2.dex */
public class ConcurrentRingQueue<T> implements Queue<T> {
    private volatile int mAdditional;
    private final boolean mAllowExtendCapacity;
    private final boolean mAutoReleaseCapacity;
    private int mCapacity;
    private volatile Node<T> mReadCursor;
    private volatile Node<T> mWriteCursor;
    private final AtomicInteger mReadLock = new AtomicInteger(0);
    private final AtomicInteger mWriteLock = new AtomicInteger(0);

    private static class Node<T> {
        T element;
        Node<T> next;

        private Node() {
        }
    }

    public ConcurrentRingQueue(int i, boolean z, boolean z2) {
        this.mCapacity = i;
        this.mAllowExtendCapacity = z;
        this.mAutoReleaseCapacity = z2;
        this.mReadCursor = new Node<>();
        this.mWriteCursor = this.mReadCursor;
        Node<T> node = this.mReadCursor;
        for (int i2 = 0; i2 < i; i2++) {
            node.next = new Node<>();
            node = node.next;
        }
        node.next = this.mReadCursor;
    }

    @Override // miuix.core.util.concurrent.Queue
    public boolean put(T t) {
        if (t == null) {
            return false;
        }
        while (true) {
            if (this.mWriteLock.get() == 0 && this.mWriteLock.compareAndSet(0, -1)) {
                break;
            }
            Thread.yield();
        }
        Node<T> node = this.mReadCursor;
        Node<T> node2 = this.mWriteCursor;
        int i = this.mAdditional;
        boolean z = true;
        if (node2.next != node) {
            node2.element = t;
            if (node2.next.next != node && this.mAutoReleaseCapacity && i > 0) {
                node2.next = node2.next.next;
                this.mAdditional = i - 1;
            }
            this.mWriteCursor = node2.next;
        } else if (this.mAllowExtendCapacity || i < 0) {
            node2.next = new Node<>();
            node2.next.next = node;
            node2.element = t;
            this.mAdditional = i + 1;
            this.mWriteCursor = node2.next;
        } else {
            z = false;
        }
        this.mWriteLock.set(0);
        return z;
    }

    @Override // miuix.core.util.concurrent.Queue
    public T get() {
        while (true) {
            if (this.mReadLock.get() == 0 && this.mReadLock.compareAndSet(0, -1)) {
                break;
            }
            Thread.yield();
        }
        Node<T> node = this.mReadCursor;
        Node<T> node2 = this.mWriteCursor;
        T t = null;
        while (t == null && node != node2) {
            t = node.element;
            node.element = null;
            node = node.next;
            node2 = this.mWriteCursor;
        }
        if (t != null) {
            this.mReadCursor = node;
        }
        this.mReadLock.set(0);
        return t;
    }

    @Override // miuix.core.util.concurrent.Queue
    public boolean remove(T t) {
        boolean z;
        if (t == null) {
            return false;
        }
        while (true) {
            if (this.mReadLock.get() == 0 && this.mReadLock.compareAndSet(0, -1)) {
                break;
            }
            Thread.yield();
        }
        for (Node<T> node = this.mReadCursor; node != this.mWriteCursor; node = node.next) {
            if (t.equals(node.element)) {
                node.element = null;
                z = true;
                this.mReadLock.set(0);
                return z;
            }
        }
        z = false;
        this.mReadLock.set(0);
        return z;
    }

    @Override // miuix.core.util.concurrent.Queue
    public int remove(Queue.Predicate<T> predicate) {
        if (predicate == null) {
            return 0;
        }
        while (true) {
            if (this.mReadLock.get() != 0 || !this.mReadLock.compareAndSet(0, -1)) {
                Thread.yield();
            } else {
                try {
                    break;
                } finally {
                    this.mReadLock.set(0);
                }
            }
        }
        int i = 0;
        for (Node<T> node = this.mReadCursor; node != this.mWriteCursor; node = node.next) {
            if (predicate.apply(node.element)) {
                node.element = null;
                i++;
            }
        }
        return i;
    }

    @Override // miuix.core.util.concurrent.Queue
    public int clear() {
        while (true) {
            if (this.mReadLock.get() == 0 && this.mReadLock.compareAndSet(0, -1)) {
                break;
            }
            Thread.yield();
        }
        Node<T> node = this.mReadCursor;
        int i = 0;
        while (node != this.mWriteCursor) {
            node.element = null;
            i++;
            node = node.next;
        }
        this.mReadCursor = node;
        this.mReadLock.set(0);
        return i;
    }

    @Override // miuix.core.util.concurrent.Queue
    public boolean isEmpty() {
        return this.mWriteCursor == this.mReadCursor;
    }

    @Override // miuix.core.util.concurrent.Queue
    public int getCapacity() {
        int i = this.mAdditional;
        int i2 = this.mCapacity;
        return i > 0 ? i2 + i : i2;
    }

    public void increaseCapacity(int i) {
        if (this.mAllowExtendCapacity || i <= 0) {
            return;
        }
        while (true) {
            if (this.mWriteLock.get() != 0 || !this.mWriteLock.compareAndSet(0, -1)) {
                Thread.yield();
            } else {
                this.mAdditional = -i;
                this.mCapacity += i;
                this.mWriteLock.set(0);
                return;
            }
        }
    }

    public void decreaseCapacity(int i) {
        if (!this.mAutoReleaseCapacity || i <= 0) {
            return;
        }
        while (true) {
            if (this.mWriteLock.get() != 0 || !this.mWriteLock.compareAndSet(0, -1)) {
                Thread.yield();
            } else {
                this.mCapacity -= i;
                this.mAdditional = i;
                this.mWriteLock.set(0);
                return;
            }
        }
    }
}
