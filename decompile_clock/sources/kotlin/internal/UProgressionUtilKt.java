package kotlin.internal;

import kotlin.Metadata;
import kotlin.UInt;
import kotlin.ULong;

/* JADX INFO: compiled from: UProgressionUtil.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\u001a'\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00012\u0006\u0010\u0004\u001a\u00020\u0001H\u0002¢\u0006\u0004\b\u0005\u0010\u0006\u001a'\u0010\u0000\u001a\u00020\u00072\u0006\u0010\u0002\u001a\u00020\u00072\u0006\u0010\u0003\u001a\u00020\u00072\u0006\u0010\u0004\u001a\u00020\u0007H\u0002¢\u0006\u0004\b\b\u0010\t\u001a'\u0010\n\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000eH\u0001¢\u0006\u0004\b\u000f\u0010\u0006\u001a'\u0010\n\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u0010H\u0001¢\u0006\u0004\b\u0011\u0010\t¨\u0006\u0012"}, d2 = {"differenceModulo", "Lkotlin/UInt;", "a", "b", "c", "differenceModulo-WZ9TVnA", "(III)I", "Lkotlin/ULong;", "differenceModulo-sambcqE", "(JJJ)J", "getProgressionLastElement", "start", "end", "step", "", "getProgressionLastElement-Nkh28Cs", "", "getProgressionLastElement-7ftBX0g", "kotlin-stdlib"}, k = 2, mv = {2, 1, 0}, xi = 48)
public final class UProgressionUtilKt {
    /* JADX INFO: renamed from: differenceModulo-WZ9TVnA, reason: not valid java name */
    private static final int m1416differenceModuloWZ9TVnA(int i, int i2, int i3) {
        int iRemainderUnsigned = Integer.remainderUnsigned(i, i3);
        int iRemainderUnsigned2 = Integer.remainderUnsigned(i2, i3);
        int iCompareUnsigned = Integer.compareUnsigned(iRemainderUnsigned, iRemainderUnsigned2);
        int iM299constructorimpl = UInt.m299constructorimpl(iRemainderUnsigned - iRemainderUnsigned2);
        return iCompareUnsigned >= 0 ? iM299constructorimpl : UInt.m299constructorimpl(iM299constructorimpl + i3);
    }

    /* JADX INFO: renamed from: differenceModulo-sambcqE, reason: not valid java name */
    private static final long m1417differenceModulosambcqE(long j, long j2, long j3) {
        long jRemainderUnsigned = Long.remainderUnsigned(j, j3);
        long jRemainderUnsigned2 = Long.remainderUnsigned(j2, j3);
        int iCompareUnsigned = Long.compareUnsigned(jRemainderUnsigned, jRemainderUnsigned2);
        long jM378constructorimpl = ULong.m378constructorimpl(jRemainderUnsigned - jRemainderUnsigned2);
        return iCompareUnsigned >= 0 ? jM378constructorimpl : ULong.m378constructorimpl(jM378constructorimpl + j3);
    }

    /* JADX INFO: renamed from: getProgressionLastElement-Nkh28Cs, reason: not valid java name */
    public static final int m1419getProgressionLastElementNkh28Cs(int i, int i2, int i3) {
        if (i3 > 0) {
            return Integer.compareUnsigned(i, i2) >= 0 ? i2 : UInt.m299constructorimpl(i2 - m1416differenceModuloWZ9TVnA(i2, i, UInt.m299constructorimpl(i3)));
        }
        if (i3 < 0) {
            return Integer.compareUnsigned(i, i2) <= 0 ? i2 : UInt.m299constructorimpl(i2 + m1416differenceModuloWZ9TVnA(i, i2, UInt.m299constructorimpl(-i3)));
        }
        throw new IllegalArgumentException("Step is zero.");
    }

    /* JADX INFO: renamed from: getProgressionLastElement-7ftBX0g, reason: not valid java name */
    public static final long m1418getProgressionLastElement7ftBX0g(long j, long j2, long j3) {
        if (j3 > 0) {
            return Long.compareUnsigned(j, j2) >= 0 ? j2 : ULong.m378constructorimpl(j2 - m1417differenceModulosambcqE(j2, j, ULong.m378constructorimpl(j3)));
        }
        if (j3 < 0) {
            return Long.compareUnsigned(j, j2) <= 0 ? j2 : ULong.m378constructorimpl(j2 + m1417differenceModulosambcqE(j, j2, ULong.m378constructorimpl(-j3)));
        }
        throw new IllegalArgumentException("Step is zero.");
    }
}
