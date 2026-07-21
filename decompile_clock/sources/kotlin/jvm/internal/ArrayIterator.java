package kotlin.jvm.internal;

import java.util.Iterator;
import java.util.NoSuchElementException;
import kotlin.Metadata;
import kotlin.jvm.internal.markers.KMappedMarker;

/* JADX INFO: compiled from: ArrayIterator.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010(\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0002\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\u0015\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00000\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\f\u001a\u00020\rH\u0096\u0002J\u000e\u0010\u000e\u001a\u00028\u0000H\u0096\u0002¢\u0006\u0002\u0010\u000fR\u0019\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00000\u0004¢\u0006\n\n\u0002\u0010\t\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lkotlin/jvm/internal/ArrayIterator;", "T", "", "array", "", "<init>", "([Ljava/lang/Object;)V", "getArray", "()[Ljava/lang/Object;", "[Ljava/lang/Object;", "index", "", "hasNext", "", "next", "()Ljava/lang/Object;", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
final class ArrayIterator<T> implements Iterator<T>, KMappedMarker {
    private final T[] array;
    private int index;

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    public ArrayIterator(T[] array) {
        Intrinsics.checkNotNullParameter(array, "array");
        this.array = array;
    }

    public final T[] getArray() {
        return this.array;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.index < this.array.length;
    }

    @Override // java.util.Iterator
    public T next() {
        try {
            T[] tArr = this.array;
            int i = this.index;
            this.index = i + 1;
            return tArr[i];
        } catch (ArrayIndexOutOfBoundsException e) {
            this.index--;
            throw new NoSuchElementException(e.getMessage());
        }
    }
}
