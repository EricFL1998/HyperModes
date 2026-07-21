package kotlin.ranges;

import com.android.deskclock.settings.SearchContract;
import java.lang.Comparable;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Ranges.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000f\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0012\u0018\u0000*\u000e\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003B\u0017\u0012\u0006\u0010\u0004\u001a\u00028\u0000\u0012\u0006\u0010\u0005\u001a\u00028\u0000¢\u0006\u0004\b\u0006\u0010\u0007J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0096\u0002J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010\u0012\u001a\u00020\u0013H\u0016R\u0016\u0010\u0004\u001a\u00028\u0000X\u0096\u0004¢\u0006\n\n\u0002\u0010\n\u001a\u0004\b\b\u0010\tR\u0016\u0010\u0005\u001a\u00028\u0000X\u0096\u0004¢\u0006\n\n\u0002\u0010\n\u001a\u0004\b\u000b\u0010\t¨\u0006\u0014"}, d2 = {"Lkotlin/ranges/ComparableOpenEndRange;", "T", "", "Lkotlin/ranges/OpenEndRange;", "start", "endExclusive", "<init>", "(Ljava/lang/Comparable;Ljava/lang/Comparable;)V", "getStart", "()Ljava/lang/Comparable;", "Ljava/lang/Comparable;", "getEndExclusive", "equals", "", SearchContract.SearchResultColumn.COLUMN_OTHER, "", "hashCode", "", "toString", "", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
class ComparableOpenEndRange<T extends Comparable<? super T>> implements OpenEndRange<T> {
    private final T endExclusive;
    private final T start;

    public ComparableOpenEndRange(T start, T endExclusive) {
        Intrinsics.checkNotNullParameter(start, "start");
        Intrinsics.checkNotNullParameter(endExclusive, "endExclusive");
        this.start = start;
        this.endExclusive = endExclusive;
    }

    @Override // kotlin.ranges.OpenEndRange
    public boolean contains(T t) {
        return OpenEndRange.DefaultImpls.contains(this, t);
    }

    @Override // kotlin.ranges.OpenEndRange
    public boolean isEmpty() {
        return OpenEndRange.DefaultImpls.isEmpty(this);
    }

    @Override // kotlin.ranges.OpenEndRange
    public T getStart() {
        return this.start;
    }

    @Override // kotlin.ranges.OpenEndRange
    public T getEndExclusive() {
        return this.endExclusive;
    }

    public boolean equals(Object other) {
        if (other instanceof ComparableOpenEndRange) {
            if (!isEmpty() || !((ComparableOpenEndRange) other).isEmpty()) {
                ComparableOpenEndRange comparableOpenEndRange = (ComparableOpenEndRange) other;
                if (!Intrinsics.areEqual(getStart(), comparableOpenEndRange.getStart()) || !Intrinsics.areEqual(getEndExclusive(), comparableOpenEndRange.getEndExclusive())) {
                }
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (isEmpty()) {
            return -1;
        }
        return (getStart().hashCode() * 31) + getEndExclusive().hashCode();
    }

    public String toString() {
        return getStart() + "..<" + getEndExclusive();
    }
}
