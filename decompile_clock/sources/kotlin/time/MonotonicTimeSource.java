package kotlin.time;

import kotlin.Metadata;

/* JADX INFO: compiled from: MonoTimeSource.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\f\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0006\u001a\u00020\u0005H\u0002J\b\u0010\u0007\u001a\u00020\bH\u0016J\u000f\u0010\t\u001a\u00020\nH\u0016¢\u0006\u0004\b\u000b\u0010\fJ\u0015\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\n¢\u0006\u0004\b\u0010\u0010\u0011J\u001d\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\n¢\u0006\u0004\b\u0015\u0010\u0016J\u001d\u0010\u0017\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\n2\u0006\u0010\u0018\u001a\u00020\u000e¢\u0006\u0004\b\u0019\u0010\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001a"}, d2 = {"Lkotlin/time/MonotonicTimeSource;", "Lkotlin/time/TimeSource$WithComparableMarks;", "<init>", "()V", "zero", "", "read", "toString", "", "markNow", "Lkotlin/time/TimeSource$Monotonic$ValueTimeMark;", "markNow-z9LOYto", "()J", "elapsedFrom", "Lkotlin/time/Duration;", "timeMark", "elapsedFrom-6eNON_k", "(J)J", "differenceBetween", "one", "another", "differenceBetween-fRLX17w", "(JJ)J", "adjustReading", "duration", "adjustReading-6QKq23U", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
public final class MonotonicTimeSource implements TimeSource.WithComparableMarks {
    public static final MonotonicTimeSource INSTANCE = new MonotonicTimeSource();
    private static final long zero = System.nanoTime();

    private MonotonicTimeSource() {
    }

    @Override // kotlin.time.TimeSource.WithComparableMarks, kotlin.time.TimeSource
    public /* bridge */ /* synthetic */ ComparableTimeMark markNow() {
        return TimeSource.Monotonic.ValueTimeMark.m1674boximpl(m1668markNowz9LOYto());
    }

    @Override // kotlin.time.TimeSource
    public /* bridge */ /* synthetic */ TimeMark markNow() {
        return TimeSource.Monotonic.ValueTimeMark.m1674boximpl(m1668markNowz9LOYto());
    }

    private final long read() {
        return System.nanoTime() - zero;
    }

    public String toString() {
        return "TimeSource(System.nanoTime())";
    }

    /* JADX INFO: renamed from: markNow-z9LOYto, reason: not valid java name */
    public long m1668markNowz9LOYto() {
        return TimeSource.Monotonic.ValueTimeMark.m1677constructorimpl(read());
    }

    /* JADX INFO: renamed from: elapsedFrom-6eNON_k, reason: not valid java name */
    public final long m1667elapsedFrom6eNON_k(long timeMark) {
        return LongSaturatedMathKt.saturatingDiff(read(), timeMark, DurationUnit.NANOSECONDS);
    }

    /* JADX INFO: renamed from: differenceBetween-fRLX17w, reason: not valid java name */
    public final long m1666differenceBetweenfRLX17w(long one, long another) {
        return LongSaturatedMathKt.saturatingOriginsDiff(one, another, DurationUnit.NANOSECONDS);
    }

    /* JADX INFO: renamed from: adjustReading-6QKq23U, reason: not valid java name */
    public final long m1665adjustReading6QKq23U(long timeMark, long duration) {
        return TimeSource.Monotonic.ValueTimeMark.m1677constructorimpl(LongSaturatedMathKt.m1663saturatingAddNuflL3o(timeMark, DurationUnit.NANOSECONDS, duration));
    }
}
