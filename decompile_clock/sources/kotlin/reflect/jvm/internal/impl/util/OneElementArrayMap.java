package kotlin.reflect.jvm.internal.impl.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;

/* JADX INFO: compiled from: ArrayMap.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class OneElementArrayMap<T> extends ArrayMap<T> {
    private final int index;
    private final T value;

    @Override // kotlin.reflect.jvm.internal.impl.util.ArrayMap
    public int getSize() {
        return 1;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public OneElementArrayMap(T value, int i) {
        super(null);
        Intrinsics.checkNotNullParameter(value, "value");
        this.value = value;
        this.index = i;
    }

    public final int getIndex() {
        return this.index;
    }

    public final T getValue() {
        return this.value;
    }

    @Override // kotlin.reflect.jvm.internal.impl.util.ArrayMap
    public void set(int i, T value) {
        Intrinsics.checkNotNullParameter(value, "value");
        throw new IllegalStateException();
    }

    @Override // kotlin.reflect.jvm.internal.impl.util.ArrayMap
    public T get(int i) {
        if (i == this.index) {
            return this.value;
        }
        return null;
    }

    /* JADX INFO: renamed from: kotlin.reflect.jvm.internal.impl.util.OneElementArrayMap$iterator$1, reason: invalid class name */
    /* JADX INFO: compiled from: ArrayMap.kt */
    public static final class AnonymousClass1 implements Iterator<T>, KMappedMarker {
        private boolean notVisited = true;
        final /* synthetic */ OneElementArrayMap<T> this$0;

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
        }

        AnonymousClass1(OneElementArrayMap<T> oneElementArrayMap) {
            this.this$0 = oneElementArrayMap;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.notVisited;
        }

        @Override // java.util.Iterator
        public T next() {
            if (this.notVisited) {
                this.notVisited = false;
                return this.this$0.getValue();
            }
            throw new NoSuchElementException();
        }
    }

    @Override // kotlin.reflect.jvm.internal.impl.util.ArrayMap, java.lang.Iterable
    public Iterator<T> iterator() {
        return new AnonymousClass1(this);
    }
}
