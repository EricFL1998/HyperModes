package kotlin.reflect.jvm.internal.impl.util;

import java.util.Arrays;
import java.util.Iterator;
import kotlin.collections.AbstractIterator;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: ArrayMap.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ArrayMapImpl<T> extends ArrayMap<T> {
    public static final Companion Companion = new Companion(null);
    private Object[] data;
    private int size;

    /* JADX INFO: compiled from: ArrayMap.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    private ArrayMapImpl(Object[] objArr, int i) {
        super(null);
        this.data = objArr;
        this.size = i;
    }

    public ArrayMapImpl() {
        this(new Object[20], 0);
    }

    @Override // kotlin.reflect.jvm.internal.impl.util.ArrayMap
    public int getSize() {
        return this.size;
    }

    private final void ensureCapacity(int i) {
        Object[] objArr = this.data;
        if (objArr.length > i) {
            return;
        }
        int length = objArr.length;
        do {
            length *= 2;
        } while (length <= i);
        Object[] objArrCopyOf = Arrays.copyOf(this.data, length);
        Intrinsics.checkNotNullExpressionValue(objArrCopyOf, "copyOf(...)");
        this.data = objArrCopyOf;
    }

    @Override // kotlin.reflect.jvm.internal.impl.util.ArrayMap
    public void set(int i, T value) {
        Intrinsics.checkNotNullParameter(value, "value");
        ensureCapacity(i);
        if (this.data[i] == null) {
            this.size = getSize() + 1;
        }
        this.data[i] = value;
    }

    @Override // kotlin.reflect.jvm.internal.impl.util.ArrayMap
    public T get(int i) {
        return (T) ArraysKt.getOrNull(this.data, i);
    }

    @Override // kotlin.reflect.jvm.internal.impl.util.ArrayMap, java.lang.Iterable
    public Iterator<T> iterator() {
        return new AbstractIterator<T>(this) { // from class: kotlin.reflect.jvm.internal.impl.util.ArrayMapImpl.iterator.1
            private int index = -1;
            final /* synthetic */ ArrayMapImpl<T> this$0;

            {
                this.this$0 = this;
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // kotlin.collections.AbstractIterator
            protected void computeNext() {
                do {
                    int i = this.index + 1;
                    this.index = i;
                    if (i >= ((ArrayMapImpl) this.this$0).data.length) {
                        break;
                    }
                } while (((ArrayMapImpl) this.this$0).data[this.index] == null);
                if (this.index < ((ArrayMapImpl) this.this$0).data.length) {
                    Object obj = ((ArrayMapImpl) this.this$0).data[this.index];
                    Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type T of org.jetbrains.kotlin.util.ArrayMapImpl");
                    setNext(obj);
                    return;
                }
                done();
            }
        };
    }
}
