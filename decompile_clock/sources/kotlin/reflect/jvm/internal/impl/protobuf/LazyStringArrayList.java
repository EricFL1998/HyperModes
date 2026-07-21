package kotlin.reflect.jvm.internal.impl.protobuf;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

/* JADX INFO: loaded from: classes2.dex */
public class LazyStringArrayList extends AbstractList<String> implements RandomAccess, LazyStringList {
    public static final LazyStringList EMPTY = new LazyStringArrayList().getUnmodifiableView();
    private final List<Object> list;

    public LazyStringArrayList() {
        this.list = new ArrayList();
    }

    public LazyStringArrayList(LazyStringList lazyStringList) {
        this.list = new ArrayList(lazyStringList.size());
        addAll(lazyStringList);
    }

    @Override // java.util.AbstractList, java.util.List
    public String get(int i) {
        Object obj = this.list.get(i);
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof ByteString) {
            ByteString byteString = (ByteString) obj;
            String stringUtf8 = byteString.toStringUtf8();
            if (byteString.isValidUtf8()) {
                this.list.set(i, stringUtf8);
            }
            return stringUtf8;
        }
        byte[] bArr = (byte[]) obj;
        String stringUtf9 = Internal.toStringUtf8(bArr);
        if (Internal.isValidUtf8(bArr)) {
            this.list.set(i, stringUtf9);
        }
        return stringUtf9;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.list.size();
    }

    @Override // java.util.AbstractList, java.util.List
    public String set(int i, String str) {
        return asString(this.list.set(i, str));
    }

    @Override // java.util.AbstractList, java.util.List
    public void add(int i, String str) {
        this.list.add(i, str);
        this.modCount++;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean addAll(Collection<? extends String> collection) {
        return addAll(size(), collection);
    }

    @Override // java.util.AbstractList, java.util.List
    public boolean addAll(int i, Collection<? extends String> collection) {
        if (collection instanceof LazyStringList) {
            collection = ((LazyStringList) collection).getUnderlyingElements();
        }
        boolean zAddAll = this.list.addAll(i, collection);
        this.modCount++;
        return zAddAll;
    }

    @Override // java.util.AbstractList, java.util.List
    public String remove(int i) {
        Object objRemove = this.list.remove(i);
        this.modCount++;
        return asString(objRemove);
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public void clear() {
        this.list.clear();
        this.modCount++;
    }

    @Override // kotlin.reflect.jvm.internal.impl.protobuf.LazyStringList
    public void add(ByteString byteString) {
        this.list.add(byteString);
        this.modCount++;
    }

    @Override // kotlin.reflect.jvm.internal.impl.protobuf.LazyStringList
    public ByteString getByteString(int i) {
        Object obj = this.list.get(i);
        ByteString byteStringAsByteString = asByteString(obj);
        if (byteStringAsByteString != obj) {
            this.list.set(i, byteStringAsByteString);
        }
        return byteStringAsByteString;
    }

    private static String asString(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof ByteString) {
            return ((ByteString) obj).toStringUtf8();
        }
        return Internal.toStringUtf8((byte[]) obj);
    }

    private static ByteString asByteString(Object obj) {
        if (obj instanceof ByteString) {
            return (ByteString) obj;
        }
        if (obj instanceof String) {
            return ByteString.copyFromUtf8((String) obj);
        }
        return ByteString.copyFrom((byte[]) obj);
    }

    @Override // kotlin.reflect.jvm.internal.impl.protobuf.LazyStringList
    public List<?> getUnderlyingElements() {
        return Collections.unmodifiableList(this.list);
    }

    @Override // kotlin.reflect.jvm.internal.impl.protobuf.LazyStringList
    public LazyStringList getUnmodifiableView() {
        return new UnmodifiableLazyStringList(this);
    }
}
