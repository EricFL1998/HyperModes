package kotlin.reflect.jvm.internal.impl.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import kotlin.collections.AbstractMutableSet;
import kotlin.collections.ArraysKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.ArrayIteratorKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import kotlin.jvm.internal.markers.KMutableIterator;

/* JADX INFO: compiled from: SmartSet.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class SmartSet<T> extends AbstractMutableSet<T> {
    public static final Companion Companion = new Companion(null);
    private Object data;
    private int size;

    public /* synthetic */ SmartSet(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    @JvmStatic
    public static final <T> SmartSet<T> create() {
        return Companion.create();
    }

    /* JADX INFO: compiled from: SmartSet.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final <T> SmartSet<T> create() {
            return new SmartSet<>(null);
        }

        @JvmStatic
        public final <T> SmartSet<T> create(Collection<? extends T> set) {
            Intrinsics.checkNotNullParameter(set, "set");
            SmartSet<T> smartSet = new SmartSet<>(null);
            smartSet.addAll(set);
            return smartSet;
        }
    }

    private SmartSet() {
    }

    @Override // kotlin.collections.AbstractMutableSet
    public int getSize() {
        return this.size;
    }

    public void setSize(int i) {
        this.size = i;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
    public Iterator<T> iterator() {
        if (size() == 0) {
            return Collections.emptySet().iterator();
        }
        if (size() == 1) {
            return new SingletonIterator(this.data);
        }
        if (size() < 5) {
            Object obj = this.data;
            Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type kotlin.Array<T of org.jetbrains.kotlin.utils.SmartSet>");
            return new ArrayIterator((Object[]) obj);
        }
        Object obj2 = this.data;
        Intrinsics.checkNotNull(obj2, "null cannot be cast to non-null type kotlin.collections.MutableSet<T of org.jetbrains.kotlin.utils.SmartSet>");
        return TypeIntrinsics.asMutableSet(obj2).iterator();
    }

    @Override // kotlin.collections.AbstractMutableSet, java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean add(T t) {
        Object obj;
        if (size() == 0) {
            this.data = t;
        } else if (size() == 1) {
            if (Intrinsics.areEqual(this.data, t)) {
                return false;
            }
            this.data = new Object[]{this.data, t};
        } else if (size() < 5) {
            Object obj2 = this.data;
            Intrinsics.checkNotNull(obj2, "null cannot be cast to non-null type kotlin.Array<T of org.jetbrains.kotlin.utils.SmartSet>");
            Object[] objArr = (Object[]) obj2;
            if (ArraysKt.contains(objArr, t)) {
                return false;
            }
            if (size() == 4) {
                LinkedHashSet linkedHashSetLinkedSetOf = SetsKt.linkedSetOf(Arrays.copyOf(objArr, objArr.length));
                linkedHashSetLinkedSetOf.add(t);
                obj = linkedHashSetLinkedSetOf;
            } else {
                Object[] objArrCopyOf = Arrays.copyOf(objArr, size() + 1);
                Intrinsics.checkNotNullExpressionValue(objArrCopyOf, "copyOf(...)");
                objArrCopyOf[objArrCopyOf.length - 1] = t;
                obj = objArrCopyOf;
            }
            this.data = obj;
        } else {
            Object obj3 = this.data;
            Intrinsics.checkNotNull(obj3, "null cannot be cast to non-null type kotlin.collections.MutableSet<T of org.jetbrains.kotlin.utils.SmartSet>");
            if (!TypeIntrinsics.asMutableSet(obj3).add(t)) {
                return false;
            }
        }
        setSize(size() + 1);
        return true;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public void clear() {
        this.data = null;
        setSize(0);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean contains(Object obj) {
        if (size() == 0) {
            return false;
        }
        if (size() == 1) {
            return Intrinsics.areEqual(this.data, obj);
        }
        if (size() < 5) {
            Object obj2 = this.data;
            Intrinsics.checkNotNull(obj2, "null cannot be cast to non-null type kotlin.Array<T of org.jetbrains.kotlin.utils.SmartSet>");
            return ArraysKt.contains((Object[]) obj2, obj);
        }
        Object obj3 = this.data;
        Intrinsics.checkNotNull(obj3, "null cannot be cast to non-null type kotlin.collections.Set<T of org.jetbrains.kotlin.utils.SmartSet>");
        return ((Set) obj3).contains(obj);
    }

    /* JADX INFO: compiled from: SmartSet.kt */
    private static final class SingletonIterator<T> implements Iterator<T>, KMutableIterator {
        private final T element;
        private boolean hasNext = true;

        public SingletonIterator(T t) {
            this.element = t;
        }

        @Override // java.util.Iterator
        public T next() {
            if (this.hasNext) {
                this.hasNext = false;
                return this.element;
            }
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.hasNext;
        }

        @Override // java.util.Iterator
        public Void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* JADX INFO: compiled from: SmartSet.kt */
    private static final class ArrayIterator<T> implements Iterator<T>, KMutableIterator {
        private final Iterator<T> arrayIterator;

        public ArrayIterator(T[] array) {
            Intrinsics.checkNotNullParameter(array, "array");
            this.arrayIterator = ArrayIteratorKt.iterator(array);
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.arrayIterator.hasNext();
        }

        @Override // java.util.Iterator
        public T next() {
            return this.arrayIterator.next();
        }

        @Override // java.util.Iterator
        public Void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
