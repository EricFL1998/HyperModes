package kotlin.time;

import kotlin.Metadata;

/* JADX INFO: compiled from: TimeSource.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u000f\u0010\u0002\u001a\u00020\u0003H&¢\u0006\u0004\b\u0004\u0010\u0005J\u0018\u0010\u0006\u001a\u00020\u00002\u0006\u0010\u0007\u001a\u00020\u0003H\u0096\u0002¢\u0006\u0004\b\b\u0010\tJ\u0018\u0010\n\u001a\u00020\u00002\u0006\u0010\u0007\u001a\u00020\u0003H\u0096\u0002¢\u0006\u0004\b\u000b\u0010\tJ\b\u0010\f\u001a\u00020\rH\u0016J\b\u0010\u000e\u001a\u00020\rH\u0016¨\u0006\u000f"}, d2 = {"Lkotlin/time/TimeMark;", "", "elapsedNow", "Lkotlin/time/Duration;", "elapsedNow-UwyO8pc", "()J", "plus", "duration", "plus-LRDsOJo", "(J)Lkotlin/time/TimeMark;", "minus", "minus-LRDsOJo", "hasPassedNow", "", "hasNotPassedNow", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
public interface TimeMark {
    /* JADX INFO: renamed from: elapsedNow-UwyO8pc */
    long mo1554elapsedNowUwyO8pc();

    boolean hasNotPassedNow();

    boolean hasPassedNow();

    /* JADX INFO: renamed from: minus-LRDsOJo */
    TimeMark mo1555minusLRDsOJo(long duration);

    /* JADX INFO: renamed from: plus-LRDsOJo */
    TimeMark mo1557plusLRDsOJo(long duration);

    /* JADX INFO: compiled from: TimeSource.kt */
    @Metadata(k = 3, mv = {2, 1, 0}, xi = 48)
    public static final class DefaultImpls {
        /* JADX INFO: renamed from: plus-LRDsOJo, reason: not valid java name */
        public static TimeMark m1672plusLRDsOJo(TimeMark timeMark, long j) {
            return new AdjustedTimeMark(timeMark, j, null);
        }

        /* JADX INFO: renamed from: minus-LRDsOJo, reason: not valid java name */
        public static TimeMark m1671minusLRDsOJo(TimeMark timeMark, long j) {
            return timeMark.mo1557plusLRDsOJo(Duration.m1608unaryMinusUwyO8pc(j));
        }

        public static boolean hasPassedNow(TimeMark timeMark) {
            return !Duration.m1590isNegativeimpl(timeMark.mo1554elapsedNowUwyO8pc());
        }

        public static boolean hasNotPassedNow(TimeMark timeMark) {
            return Duration.m1590isNegativeimpl(timeMark.mo1554elapsedNowUwyO8pc());
        }
    }
}
