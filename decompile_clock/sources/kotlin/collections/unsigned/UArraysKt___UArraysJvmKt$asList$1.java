package kotlin.collections.unsigned;

import java.util.RandomAccess;
import kotlin.Metadata;
import kotlin.UInt;
import kotlin.UIntArray;
import kotlin.collections.AbstractList;
import kotlin.collections.ArraysKt;

/* JADX INFO: compiled from: _UArraysJvm.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000'\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\r*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u00012\u00060\u0003j\u0002`\u0004J\b\u0010\t\u001a\u00020\nH\u0016J\u0018\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\u0002H\u0096\u0002¢\u0006\u0004\b\r\u0010\u000eJ\u0018\u0010\u000f\u001a\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u0006H\u0096\u0002¢\u0006\u0004\b\u0011\u0010\u0012J\u0017\u0010\u0013\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\u0002H\u0016¢\u0006\u0004\b\u0014\u0010\u0012J\u0017\u0010\u0015\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\u0002H\u0016¢\u0006\u0004\b\u0016\u0010\u0012R\u0014\u0010\u0005\u001a\u00020\u00068VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0007\u0010\b¨\u0006\u0017"}, d2 = {"kotlin/collections/unsigned/UArraysKt___UArraysJvmKt$asList$1", "Lkotlin/collections/AbstractList;", "Lkotlin/UInt;", "Ljava/util/RandomAccess;", "Lkotlin/collections/RandomAccess;", "size", "", "getSize", "()I", "isEmpty", "", "contains", "element", "contains-WZ4Q5Ns", "(I)Z", "get", "index", "get-pVg5ArA", "(I)I", "indexOf", "indexOf-WZ4Q5Ns", "lastIndexOf", "lastIndexOf-WZ4Q5Ns", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
public final class UArraysKt___UArraysJvmKt$asList$1 extends AbstractList<UInt> implements RandomAccess {
    final /* synthetic */ int[] $this_asList;

    UArraysKt___UArraysJvmKt$asList$1(int[] iArr) {
        this.$this_asList = iArr;
    }

    @Override // kotlin.collections.AbstractCollection, java.util.Collection
    public final /* bridge */ boolean contains(Object obj) {
        if (obj instanceof UInt) {
            return m716containsWZ4Q5Ns(((UInt) obj).getData());
        }
        return false;
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    public /* bridge */ /* synthetic */ Object get(int i) {
        return UInt.m293boximpl(m717getpVg5ArA(i));
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    public final /* bridge */ int indexOf(Object obj) {
        if (obj instanceof UInt) {
            return m718indexOfWZ4Q5Ns(((UInt) obj).getData());
        }
        return -1;
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    public final /* bridge */ int lastIndexOf(Object obj) {
        if (obj instanceof UInt) {
            return m719lastIndexOfWZ4Q5Ns(((UInt) obj).getData());
        }
        return -1;
    }

    @Override // kotlin.collections.AbstractList, kotlin.collections.AbstractCollection
    public int getSize() {
        return UIntArray.m360getSizeimpl(this.$this_asList);
    }

    @Override // kotlin.collections.AbstractCollection, java.util.Collection
    public boolean isEmpty() {
        return UIntArray.m362isEmptyimpl(this.$this_asList);
    }

    /* JADX INFO: renamed from: contains-WZ4Q5Ns, reason: not valid java name */
    public boolean m716containsWZ4Q5Ns(int element) {
        return UIntArray.m355containsWZ4Q5Ns(this.$this_asList, element);
    }

    /* JADX INFO: renamed from: get-pVg5ArA, reason: not valid java name */
    public int m717getpVg5ArA(int index) {
        return UIntArray.m359getpVg5ArA(this.$this_asList, index);
    }

    /* JADX INFO: renamed from: indexOf-WZ4Q5Ns, reason: not valid java name */
    public int m718indexOfWZ4Q5Ns(int element) {
        return ArraysKt.indexOf(this.$this_asList, element);
    }

    /* JADX INFO: renamed from: lastIndexOf-WZ4Q5Ns, reason: not valid java name */
    public int m719lastIndexOfWZ4Q5Ns(int element) {
        return ArraysKt.lastIndexOf(this.$this_asList, element);
    }
}
