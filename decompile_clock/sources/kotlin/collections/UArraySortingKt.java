package kotlin.collections;

import kotlin.Metadata;
import kotlin.UByteArray;
import kotlin.UIntArray;
import kotlin.ULongArray;
import kotlin.UShort;
import kotlin.UShortArray;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: UArraySorting.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u00000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\f\u001a'\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0004\b\u0006\u0010\u0007\u001a'\u0010\b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0004\b\n\u0010\u000b\u001a'\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\f2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0004\b\r\u0010\u000e\u001a'\u0010\b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\f2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0004\b\u000f\u0010\u0010\u001a'\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00112\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0004\b\u0012\u0010\u0013\u001a'\u0010\b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00112\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0004\b\u0014\u0010\u0015\u001a'\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00162\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0004\b\u0017\u0010\u0018\u001a'\u0010\b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00162\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0004\b\u0019\u0010\u001a\u001a'\u0010\u001b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001¢\u0006\u0004\b\u001e\u0010\u000b\u001a'\u0010\u001b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\f2\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001¢\u0006\u0004\b\u001f\u0010\u0010\u001a'\u0010\u001b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00112\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001¢\u0006\u0004\b \u0010\u0015\u001a'\u0010\u001b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00162\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001¢\u0006\u0004\b!\u0010\u001a¨\u0006\""}, d2 = {"partition", "", "array", "Lkotlin/UByteArray;", "left", "right", "partition-4UcCI2c", "([BII)I", "quickSort", "", "quickSort-4UcCI2c", "([BII)V", "Lkotlin/UShortArray;", "partition-Aa5vz7o", "([SII)I", "quickSort-Aa5vz7o", "([SII)V", "Lkotlin/UIntArray;", "partition-oBK06Vg", "([III)I", "quickSort-oBK06Vg", "([III)V", "Lkotlin/ULongArray;", "partition--nroSd4", "([JII)I", "quickSort--nroSd4", "([JII)V", "sortArray", "fromIndex", "toIndex", "sortArray-4UcCI2c", "sortArray-Aa5vz7o", "sortArray-oBK06Vg", "sortArray--nroSd4", "kotlin-stdlib"}, k = 2, mv = {2, 1, 0}, xi = 48)
public final class UArraySortingKt {
    /* JADX INFO: renamed from: partition-4UcCI2c, reason: not valid java name */
    private static final int m664partition4UcCI2c(byte[] bArr, int i, int i2) {
        int i3;
        byte bM280getw2LRezQ = UByteArray.m280getw2LRezQ(bArr, (i + i2) / 2);
        while (i <= i2) {
            while (true) {
                i3 = bM280getw2LRezQ & 255;
                if (Intrinsics.compare(UByteArray.m280getw2LRezQ(bArr, i) & 255, i3) >= 0) {
                    break;
                }
                i++;
            }
            while (Intrinsics.compare(UByteArray.m280getw2LRezQ(bArr, i2) & 255, i3) > 0) {
                i2--;
            }
            if (i <= i2) {
                byte bM280getw2LRezQ2 = UByteArray.m280getw2LRezQ(bArr, i);
                UByteArray.m285setVurrAj0(bArr, i, UByteArray.m280getw2LRezQ(bArr, i2));
                UByteArray.m285setVurrAj0(bArr, i2, bM280getw2LRezQ2);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: quickSort-4UcCI2c, reason: not valid java name */
    private static final void m668quickSort4UcCI2c(byte[] bArr, int i, int i2) {
        int iM664partition4UcCI2c = m664partition4UcCI2c(bArr, i, i2);
        int i3 = iM664partition4UcCI2c - 1;
        if (i < i3) {
            m668quickSort4UcCI2c(bArr, i, i3);
        }
        if (iM664partition4UcCI2c < i2) {
            m668quickSort4UcCI2c(bArr, iM664partition4UcCI2c, i2);
        }
    }

    /* JADX INFO: renamed from: partition-Aa5vz7o, reason: not valid java name */
    private static final int m665partitionAa5vz7o(short[] sArr, int i, int i2) {
        int i3;
        short sM543getMh2AYeg = UShortArray.m543getMh2AYeg(sArr, (i + i2) / 2);
        while (i <= i2) {
            while (true) {
                int iM543getMh2AYeg = UShortArray.m543getMh2AYeg(sArr, i) & UShort.MAX_VALUE;
                i3 = sM543getMh2AYeg & UShort.MAX_VALUE;
                if (Intrinsics.compare(iM543getMh2AYeg, i3) >= 0) {
                    break;
                }
                i++;
            }
            while (Intrinsics.compare(UShortArray.m543getMh2AYeg(sArr, i2) & UShort.MAX_VALUE, i3) > 0) {
                i2--;
            }
            if (i <= i2) {
                short sM543getMh2AYeg2 = UShortArray.m543getMh2AYeg(sArr, i);
                UShortArray.m548set01HTLdE(sArr, i, UShortArray.m543getMh2AYeg(sArr, i2));
                UShortArray.m548set01HTLdE(sArr, i2, sM543getMh2AYeg2);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: quickSort-Aa5vz7o, reason: not valid java name */
    private static final void m669quickSortAa5vz7o(short[] sArr, int i, int i2) {
        int iM665partitionAa5vz7o = m665partitionAa5vz7o(sArr, i, i2);
        int i3 = iM665partitionAa5vz7o - 1;
        if (i < i3) {
            m669quickSortAa5vz7o(sArr, i, i3);
        }
        if (iM665partitionAa5vz7o < i2) {
            m669quickSortAa5vz7o(sArr, iM665partitionAa5vz7o, i2);
        }
    }

    /* JADX INFO: renamed from: partition-oBK06Vg, reason: not valid java name */
    private static final int m666partitionoBK06Vg(int[] iArr, int i, int i2) {
        int iM359getpVg5ArA = UIntArray.m359getpVg5ArA(iArr, (i + i2) / 2);
        while (i <= i2) {
            while (Integer.compareUnsigned(UIntArray.m359getpVg5ArA(iArr, i), iM359getpVg5ArA) < 0) {
                i++;
            }
            while (Integer.compareUnsigned(UIntArray.m359getpVg5ArA(iArr, i2), iM359getpVg5ArA) > 0) {
                i2--;
            }
            if (i <= i2) {
                int iM359getpVg5ArA2 = UIntArray.m359getpVg5ArA(iArr, i);
                UIntArray.m364setVXSXFK8(iArr, i, UIntArray.m359getpVg5ArA(iArr, i2));
                UIntArray.m364setVXSXFK8(iArr, i2, iM359getpVg5ArA2);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: quickSort-oBK06Vg, reason: not valid java name */
    private static final void m670quickSortoBK06Vg(int[] iArr, int i, int i2) {
        int iM666partitionoBK06Vg = m666partitionoBK06Vg(iArr, i, i2);
        int i3 = iM666partitionoBK06Vg - 1;
        if (i < i3) {
            m670quickSortoBK06Vg(iArr, i, i3);
        }
        if (iM666partitionoBK06Vg < i2) {
            m670quickSortoBK06Vg(iArr, iM666partitionoBK06Vg, i2);
        }
    }

    /* JADX INFO: renamed from: partition--nroSd4, reason: not valid java name */
    private static final int m663partitionnroSd4(long[] jArr, int i, int i2) {
        long jM438getsVKNKU = ULongArray.m438getsVKNKU(jArr, (i + i2) / 2);
        while (i <= i2) {
            while (Long.compareUnsigned(ULongArray.m438getsVKNKU(jArr, i), jM438getsVKNKU) < 0) {
                i++;
            }
            while (Long.compareUnsigned(ULongArray.m438getsVKNKU(jArr, i2), jM438getsVKNKU) > 0) {
                i2--;
            }
            if (i <= i2) {
                long jM438getsVKNKU2 = ULongArray.m438getsVKNKU(jArr, i);
                ULongArray.m443setk8EXiF4(jArr, i, ULongArray.m438getsVKNKU(jArr, i2));
                ULongArray.m443setk8EXiF4(jArr, i2, jM438getsVKNKU2);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: quickSort--nroSd4, reason: not valid java name */
    private static final void m667quickSortnroSd4(long[] jArr, int i, int i2) {
        int iM663partitionnroSd4 = m663partitionnroSd4(jArr, i, i2);
        int i3 = iM663partitionnroSd4 - 1;
        if (i < i3) {
            m667quickSortnroSd4(jArr, i, i3);
        }
        if (iM663partitionnroSd4 < i2) {
            m667quickSortnroSd4(jArr, iM663partitionnroSd4, i2);
        }
    }

    /* JADX INFO: renamed from: sortArray-4UcCI2c, reason: not valid java name */
    public static final void m672sortArray4UcCI2c(byte[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m668quickSort4UcCI2c(array, i, i2 - 1);
    }

    /* JADX INFO: renamed from: sortArray-Aa5vz7o, reason: not valid java name */
    public static final void m673sortArrayAa5vz7o(short[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m669quickSortAa5vz7o(array, i, i2 - 1);
    }

    /* JADX INFO: renamed from: sortArray-oBK06Vg, reason: not valid java name */
    public static final void m674sortArrayoBK06Vg(int[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m670quickSortoBK06Vg(array, i, i2 - 1);
    }

    /* JADX INFO: renamed from: sortArray--nroSd4, reason: not valid java name */
    public static final void m671sortArraynroSd4(long[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m667quickSortnroSd4(array, i, i2 - 1);
    }
}
