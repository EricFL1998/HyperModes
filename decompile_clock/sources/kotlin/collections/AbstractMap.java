package kotlin.collections;

import com.android.deskclock.settings.SearchContract;
import com.xiaomi.onetrack.api.g;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;

/* JADX INFO: compiled from: AbstractMap.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010&\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\"\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u001e\n\u0002\b\u0007\b'\u0018\u0000 **\u0004\b\u0000\u0010\u0001*\u0006\b\u0001\u0010\u0002 \u00012\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00020\u0003:\u0001*B\t\b\u0004¢\u0006\u0004\b\u0004\u0010\u0005J\u0015\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\tJ\u0015\u0010\n\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00028\u0001H\u0016¢\u0006\u0002\u0010\tJ\u001f\u0010\f\u001a\u00020\u00072\u0010\u0010\r\u001a\f\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0018\u00010\u000eH\u0000¢\u0006\u0002\b\u000fJ\u0013\u0010\u0010\u001a\u00020\u00072\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0096\u0002J\u0018\u0010\u0013\u001a\u0004\u0018\u00018\u00012\u0006\u0010\b\u001a\u00028\u0000H\u0096\u0002¢\u0006\u0002\u0010\u0014J\b\u0010\u0015\u001a\u00020\u0016H\u0016J\b\u0010\u0017\u001a\u00020\u0007H\u0016J\b\u0010 \u001a\u00020!H\u0016J\u001c\u0010 \u001a\u00020!2\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u000eH\u0002J\u0012\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010\u0012H\u0002J#\u0010(\u001a\u0010\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0018\u00010\u000e2\u0006\u0010\b\u001a\u00028\u0000H\u0002¢\u0006\u0002\u0010)R\u0014\u0010\u0018\u001a\u00020\u00168VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0019\u0010\u001aR\u001a\u0010\u001b\u001a\b\u0012\u0004\u0012\u00028\u00000\u001c8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u001d\u0010\u001eR\u0016\u0010\u001f\u001a\n\u0012\u0004\u0012\u00028\u0000\u0018\u00010\u001cX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010#\u001a\b\u0012\u0004\u0012\u00028\u00010$8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b%\u0010&R\u0016\u0010'\u001a\n\u0012\u0004\u0012\u00028\u0001\u0018\u00010$X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006+"}, d2 = {"Lkotlin/collections/AbstractMap;", "K", "V", "", "<init>", "()V", "containsKey", "", "key", "(Ljava/lang/Object;)Z", "containsValue", g.p, "containsEntry", "entry", "", "containsEntry$kotlin_stdlib", "equals", SearchContract.SearchResultColumn.COLUMN_OTHER, "", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", "hashCode", "", "isEmpty", "size", "getSize", "()I", "keys", "", "getKeys", "()Ljava/util/Set;", "_keys", "toString", "", "o", "values", "", "getValues", "()Ljava/util/Collection;", "_values", "implFindEntry", "(Ljava/lang/Object;)Ljava/util/Map$Entry;", "Companion", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
public abstract class AbstractMap<K, V> implements Map<K, V>, KMappedMarker {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private volatile Set<? extends K> _keys;
    private volatile Collection<? extends V> _values;

    @Override // java.util.Map
    public void clear() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    public abstract Set getEntries();

    @Override // java.util.Map
    public V put(K k, V v) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    @Override // java.util.Map
    public V remove(Object obj) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    @Override // java.util.Map
    public final /* bridge */ Set<Map.Entry<K, V>> entrySet() {
        return getEntries();
    }

    @Override // java.util.Map
    public final /* bridge */ Set<K> keySet() {
        return getKeys();
    }

    @Override // java.util.Map
    public final /* bridge */ int size() {
        return getSize();
    }

    @Override // java.util.Map
    public final /* bridge */ Collection<V> values() {
        return getValues();
    }

    protected AbstractMap() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return implFindEntry(key) != null;
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        Set<Map.Entry<K, V>> setEntrySet = entrySet();
        if ((setEntrySet instanceof Collection) && setEntrySet.isEmpty()) {
            return false;
        }
        Iterator<T> it = setEntrySet.iterator();
        while (it.hasNext()) {
            if (Intrinsics.areEqual(((Map.Entry) it.next()).getValue(), value)) {
                return true;
            }
        }
        return false;
    }

    public final boolean containsEntry$kotlin_stdlib(Map.Entry<?, ?> entry) {
        if (entry == null) {
            return false;
        }
        Object key = entry.getKey();
        Object value = entry.getValue();
        AbstractMap<K, V> abstractMap = this;
        Intrinsics.checkNotNull(abstractMap, "null cannot be cast to non-null type kotlin.collections.Map<K of kotlin.collections.MapsKt__MapsKt.get, V of kotlin.collections.MapsKt__MapsKt.get>");
        V v = abstractMap.get(key);
        if (!Intrinsics.areEqual(value, v)) {
            return false;
        }
        if (v != null) {
            return true;
        }
        Intrinsics.checkNotNull(abstractMap, "null cannot be cast to non-null type kotlin.collections.Map<K of kotlin.collections.MapsKt__MapsKt.containsKey, *>");
        return abstractMap.containsKey(key);
    }

    @Override // java.util.Map
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Map)) {
            return false;
        }
        Map map = (Map) other;
        if (size() != map.size()) {
            return false;
        }
        Set<Map.Entry<K, V>> setEntrySet = map.entrySet();
        if ((setEntrySet instanceof Collection) && setEntrySet.isEmpty()) {
            return true;
        }
        Iterator<T> it = setEntrySet.iterator();
        while (it.hasNext()) {
            if (!containsEntry$kotlin_stdlib((Map.Entry) it.next())) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    public V get(Object key) {
        Map.Entry<K, V> entryImplFindEntry = implFindEntry(key);
        if (entryImplFindEntry != null) {
            return entryImplFindEntry.getValue();
        }
        return null;
    }

    @Override // java.util.Map
    public int hashCode() {
        return entrySet().hashCode();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return size() == 0;
    }

    public int getSize() {
        return entrySet().size();
    }

    public Set<K> getKeys() {
        if (this._keys == null) {
            this._keys = new AbstractSet<K>(this) { // from class: kotlin.collections.AbstractMap$keys$1
                final /* synthetic */ AbstractMap<K, V> this$0;

                /* JADX WARN: Multi-variable type inference failed */
                {
                    this.this$0 = this;
                }

                @Override // kotlin.collections.AbstractCollection, java.util.Collection
                public boolean contains(Object element) {
                    return this.this$0.containsKey(element);
                }

                @Override // kotlin.collections.AbstractSet, kotlin.collections.AbstractCollection, java.util.Collection, java.lang.Iterable
                public Iterator<K> iterator() {
                    return new AbstractMap$keys$1$iterator$1(this.this$0.entrySet().iterator());
                }

                @Override // kotlin.collections.AbstractCollection
                public int getSize() {
                    return this.this$0.size();
                }
            };
        }
        Set<? extends K> set = this._keys;
        Intrinsics.checkNotNull(set);
        return set;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CharSequence toString$lambda$2(AbstractMap abstractMap, Map.Entry it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return abstractMap.toString(it);
    }

    public String toString() {
        return CollectionsKt.joinToString$default(entrySet(), ", ", "{", "}", 0, null, new Function1() { // from class: kotlin.collections.AbstractMap$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return AbstractMap.toString$lambda$2(this.f$0, (Map.Entry) obj);
            }
        }, 24, null);
    }

    private final String toString(Map.Entry<? extends K, ? extends V> entry) {
        return toString(entry.getKey()) + '=' + toString(entry.getValue());
    }

    private final String toString(Object o) {
        return o == this ? "(this Map)" : String.valueOf(o);
    }

    public Collection<V> getValues() {
        if (this._values == null) {
            this._values = new AbstractCollection<V>(this) { // from class: kotlin.collections.AbstractMap.values.1
                final /* synthetic */ AbstractMap<K, V> this$0;

                /* JADX WARN: Multi-variable type inference failed */
                {
                    this.this$0 = this;
                }

                @Override // kotlin.collections.AbstractCollection, java.util.Collection
                public boolean contains(Object element) {
                    return this.this$0.containsValue(element);
                }

                @Override // kotlin.collections.AbstractCollection, java.util.Collection, java.lang.Iterable
                public Iterator<V> iterator() {
                    return new AbstractMap$values$1$iterator$1(this.this$0.entrySet().iterator());
                }

                @Override // kotlin.collections.AbstractCollection
                public int getSize() {
                    return this.this$0.size();
                }
            };
        }
        Collection<? extends V> collection = this._values;
        Intrinsics.checkNotNull(collection);
        return collection;
    }

    private final Map.Entry<K, V> implFindEntry(K key) {
        Object next;
        Iterator<T> it = entrySet().iterator();
        while (it.hasNext()) {
            next = it.next();
            if (Intrinsics.areEqual(((Map.Entry) next).getKey(), key)) {
                return (Map.Entry) next;
            }
        }
        next = null;
        return (Map.Entry) next;
    }

    /* JADX INFO: compiled from: AbstractMap.kt */
    @Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010&\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0080\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001d\u0010\u0004\u001a\u00020\u00052\u000e\u0010\u0006\u001a\n\u0012\u0002\b\u0003\u0012\u0002\b\u00030\u0007H\u0000¢\u0006\u0002\b\bJ\u001d\u0010\t\u001a\u00020\n2\u000e\u0010\u0006\u001a\n\u0012\u0002\b\u0003\u0012\u0002\b\u00030\u0007H\u0000¢\u0006\u0002\b\u000bJ'\u0010\f\u001a\u00020\r2\u000e\u0010\u0006\u001a\n\u0012\u0002\b\u0003\u0012\u0002\b\u00030\u00072\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u0000¢\u0006\u0002\b\u000f¨\u0006\u0010"}, d2 = {"Lkotlin/collections/AbstractMap$Companion;", "", "<init>", "()V", "entryHashCode", "", "e", "", "entryHashCode$kotlin_stdlib", "entryToString", "", "entryToString$kotlin_stdlib", "entryEquals", "", SearchContract.SearchResultColumn.COLUMN_OTHER, "entryEquals$kotlin_stdlib", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final int entryHashCode$kotlin_stdlib(Map.Entry<?, ?> e) {
            Intrinsics.checkNotNullParameter(e, "e");
            Object key = e.getKey();
            int iHashCode = key != null ? key.hashCode() : 0;
            Object value = e.getValue();
            return iHashCode ^ (value != null ? value.hashCode() : 0);
        }

        public final String entryToString$kotlin_stdlib(Map.Entry<?, ?> e) {
            Intrinsics.checkNotNullParameter(e, "e");
            return new StringBuilder().append(e.getKey()).append('=').append(e.getValue()).toString();
        }

        public final boolean entryEquals$kotlin_stdlib(Map.Entry<?, ?> e, Object other) {
            Intrinsics.checkNotNullParameter(e, "e");
            if (!(other instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry) other;
            return Intrinsics.areEqual(e.getKey(), entry.getKey()) && Intrinsics.areEqual(e.getValue(), entry.getValue());
        }
    }
}
