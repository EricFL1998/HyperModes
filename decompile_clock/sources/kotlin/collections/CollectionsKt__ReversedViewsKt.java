package kotlin.collections;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: compiled from: ReversedViews.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\b\n\u0002\u0010 \n\u0002\b\b\n\u0002\u0010!\n\u0002\b\u0002\u001a\u001d\u0010\u0000\u001a\u00020\u0001*\u0006\u0012\u0002\b\u00030\u00022\u0006\u0010\u0003\u001a\u00020\u0001H\u0002¢\u0006\u0002\b\u0004\u001a\u001d\u0010\u0005\u001a\u00020\u0001*\u0006\u0012\u0002\b\u00030\u00022\u0006\u0010\u0003\u001a\u00020\u0001H\u0002¢\u0006\u0002\b\u0006\u001a\u001d\u0010\u0007\u001a\u00020\u0001*\u0006\u0012\u0002\b\u00030\u00022\u0006\u0010\u0003\u001a\u00020\u0001H\u0002¢\u0006\u0002\b\b\u001a\u001c\u0010\t\u001a\b\u0012\u0004\u0012\u0002H\n0\u0002\"\u0004\b\u0000\u0010\n*\b\u0012\u0004\u0012\u0002H\n0\u0002\u001a#\u0010\t\u001a\b\u0012\u0004\u0012\u0002H\n0\u000b\"\u0004\b\u0000\u0010\n*\b\u0012\u0004\u0012\u0002H\n0\u000bH\u0007¢\u0006\u0002\b\f¨\u0006\r"}, d2 = {"reverseElementIndex", "", "", "index", "reverseElementIndex$CollectionsKt__ReversedViewsKt", "reversePositionIndex", "reversePositionIndex$CollectionsKt__ReversedViewsKt", "reverseIteratorIndex", "reverseIteratorIndex$CollectionsKt__ReversedViewsKt", "asReversed", "T", "", "asReversedMutable", "kotlin-stdlib"}, k = 5, mv = {2, 1, 0}, xi = 49, xs = "kotlin/collections/CollectionsKt")
public class CollectionsKt__ReversedViewsKt extends CollectionsKt__MutableCollectionsKt {
    /* JADX INFO: Access modifiers changed from: private */
    public static final int reverseElementIndex$CollectionsKt__ReversedViewsKt(List<?> list, int i) {
        if (i < 0 || i > CollectionsKt.getLastIndex(list)) {
            throw new IndexOutOfBoundsException("Element index " + i + " must be in range [" + new IntRange(0, CollectionsKt.getLastIndex(list)) + "].");
        }
        return CollectionsKt.getLastIndex(list) - i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final int reversePositionIndex$CollectionsKt__ReversedViewsKt(List<?> list, int i) {
        if (i < 0 || i > list.size()) {
            throw new IndexOutOfBoundsException("Position index " + i + " must be in range [" + new IntRange(0, list.size()) + "].");
        }
        return list.size() - i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final int reverseIteratorIndex$CollectionsKt__ReversedViewsKt(List<?> list, int i) {
        return CollectionsKt.getLastIndex(list) - i;
    }

    public static final <T> List<T> asReversed(List<? extends T> list) {
        Intrinsics.checkNotNullParameter(list, "<this>");
        return new ReversedListReadOnly(list);
    }

    public static final <T> List<T> asReversedMutable(List<T> list) {
        Intrinsics.checkNotNullParameter(list, "<this>");
        return new ReversedList(list);
    }
}
