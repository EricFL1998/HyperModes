package kotlin.sequences;

import java.util.Iterator;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.UInt;
import kotlin.ULong;
import kotlin.UShort;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: _USequences.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000$\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0019\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00010\u0002H\u0007¢\u0006\u0004\b\u0003\u0010\u0004\u001a\u0019\u0010\u0000\u001a\u00020\u0005*\b\u0012\u0004\u0012\u00020\u00050\u0002H\u0007¢\u0006\u0004\b\u0006\u0010\u0007\u001a\u0019\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\b0\u0002H\u0007¢\u0006\u0004\b\t\u0010\u0004\u001a\u0019\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\n0\u0002H\u0007¢\u0006\u0004\b\u000b\u0010\u0004¨\u0006\f"}, d2 = {"sum", "Lkotlin/UInt;", "Lkotlin/sequences/Sequence;", "sumOfUInt", "(Lkotlin/sequences/Sequence;)I", "Lkotlin/ULong;", "sumOfULong", "(Lkotlin/sequences/Sequence;)J", "Lkotlin/UByte;", "sumOfUByte", "Lkotlin/UShort;", "sumOfUShort", "kotlin-stdlib"}, k = 5, mv = {2, 1, 0}, xi = 49, xs = "kotlin/sequences/USequencesKt")
class USequencesKt___USequencesKt {
    public static final int sumOfUInt(Sequence<UInt> sequence) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        Iterator<UInt> it = sequence.iterator();
        int iM299constructorimpl = 0;
        while (it.hasNext()) {
            iM299constructorimpl = UInt.m299constructorimpl(iM299constructorimpl + it.next().getData());
        }
        return iM299constructorimpl;
    }

    public static final long sumOfULong(Sequence<ULong> sequence) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        Iterator<ULong> it = sequence.iterator();
        long jM378constructorimpl = 0;
        while (it.hasNext()) {
            jM378constructorimpl = ULong.m378constructorimpl(jM378constructorimpl + it.next().getData());
        }
        return jM378constructorimpl;
    }

    public static final int sumOfUByte(Sequence<UByte> sequence) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        Iterator<UByte> it = sequence.iterator();
        int iM299constructorimpl = 0;
        while (it.hasNext()) {
            iM299constructorimpl = UInt.m299constructorimpl(iM299constructorimpl + UInt.m299constructorimpl(it.next().getData() & 255));
        }
        return iM299constructorimpl;
    }

    public static final int sumOfUShort(Sequence<UShort> sequence) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        Iterator<UShort> it = sequence.iterator();
        int iM299constructorimpl = 0;
        while (it.hasNext()) {
            iM299constructorimpl = UInt.m299constructorimpl(iM299constructorimpl + UInt.m299constructorimpl(it.next().getData() & UShort.MAX_VALUE));
        }
        return iM299constructorimpl;
    }
}
