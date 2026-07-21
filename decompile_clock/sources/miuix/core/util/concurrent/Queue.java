package miuix.core.util.concurrent;

/* JADX INFO: loaded from: classes2.dex */
public interface Queue<T> {

    public interface Predicate<T> {
        boolean apply(T t);
    }

    int clear();

    T get();

    int getCapacity();

    boolean isEmpty();

    boolean put(T t);

    int remove(Predicate<T> predicate);

    boolean remove(T t);
}
