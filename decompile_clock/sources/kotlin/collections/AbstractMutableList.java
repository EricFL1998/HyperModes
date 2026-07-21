package kotlin.collections;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.markers.KMutableList;

/* JADX INFO: compiled from: AbstractMutableList.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\b'\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003B\t\b\u0004¢\u0006\u0004\b\u0004\u0010\u0005J\u001e\u0010\u0006\u001a\u00028\u00002\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00028\u0000H¦\u0002¢\u0006\u0002\u0010\nJ\u0015\u0010\u000b\u001a\u00028\u00002\u0006\u0010\u0007\u001a\u00020\bH&¢\u0006\u0002\u0010\fJ\u001d\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00028\u0000H&¢\u0006\u0002\u0010\u000f¨\u0006\u0010"}, d2 = {"Lkotlin/collections/AbstractMutableList;", "E", "", "Ljava/util/AbstractList;", "<init>", "()V", "set", "index", "", "element", "(ILjava/lang/Object;)Ljava/lang/Object;", "removeAt", "(I)Ljava/lang/Object;", "add", "", "(ILjava/lang/Object;)V", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
public abstract class AbstractMutableList<E> extends java.util.AbstractList<E> implements List<E>, KMutableList {
    @Override // java.util.AbstractList, java.util.List
    public abstract void add(int index, E element);

    public abstract int getSize();

    public abstract E removeAt(int index);

    @Override // java.util.AbstractList, java.util.List
    public abstract E set(int index, E element);

    @Override // java.util.AbstractList, java.util.List
    public final /* bridge */ E remove(int i) {
        return removeAt(i);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final /* bridge */ int size() {
        return getSize();
    }

    protected AbstractMutableList() {
    }
}
