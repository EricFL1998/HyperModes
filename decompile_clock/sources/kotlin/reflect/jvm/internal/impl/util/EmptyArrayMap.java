package kotlin.reflect.jvm.internal.impl.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;

/* JADX INFO: compiled from: ArrayMap.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class EmptyArrayMap extends ArrayMap {
    public static final EmptyArrayMap INSTANCE = new EmptyArrayMap();

    @Override // kotlin.reflect.jvm.internal.impl.util.ArrayMap
    public Void get(int i) {
        return null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.util.ArrayMap
    public int getSize() {
        return 0;
    }

    private EmptyArrayMap() {
        super(null);
    }

    @Override // kotlin.reflect.jvm.internal.impl.util.ArrayMap
    public void set(int i, Void value) {
        Intrinsics.checkNotNullParameter(value, "value");
        throw new IllegalStateException();
    }

    /* JADX INFO: renamed from: kotlin.reflect.jvm.internal.impl.util.EmptyArrayMap$iterator$1, reason: invalid class name */
    /* JADX INFO: compiled from: ArrayMap.kt */
    public static final class AnonymousClass1 implements Iterator, KMappedMarker {
        @Override // java.util.Iterator
        public boolean hasNext() {
            return false;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
        }

        AnonymousClass1() {
        }

        @Override // java.util.Iterator
        public Void next() {
            throw new NoSuchElementException();
        }
    }

    @Override // kotlin.reflect.jvm.internal.impl.util.ArrayMap, java.lang.Iterable
    public Iterator iterator() {
        return new AnonymousClass1();
    }
}
