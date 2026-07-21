package kotlin.uuid;

import kotlin.Metadata;
import kotlin.jvm.internal.ByteCompanionObject;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.HexExtensionsKt;

/* JADX INFO: compiled from: Uuid.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000,\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0001\u001a\u0019\u0010\u0004\u001a\u00020\u0005*\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u0002¢\u0006\u0002\b\b\u001a)\u0010\t\u001a\u00020\n*\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u0007H\u0002¢\u0006\u0002\b\u000e\u001a\u0019\u0010\u000f\u001a\u00020\n*\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0007H\u0002¢\u0006\u0002\b\u0012\u001a!\u0010\u0013\u001a\u00020\n*\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\u0007H\u0002¢\u0006\u0002\b\u0014¨\u0006\u0015"}, d2 = {"uuidFromRandomBytes", "Lkotlin/uuid/Uuid;", "randomBytes", "", "toLong", "", "startIndex", "", "toLong$UuidKt__UuidKt", "formatBytesInto", "", "dst", "dstOffset", "count", "formatBytesInto$UuidKt__UuidKt", "checkHyphenAt", "", "index", "checkHyphenAt$UuidKt__UuidKt", "toByteArray", "toByteArray$UuidKt__UuidKt", "kotlin-stdlib"}, k = 5, mv = {2, 1, 0}, xi = 49, xs = "kotlin/uuid/UuidKt")
class UuidKt__UuidKt extends UuidKt__UuidJVMKt {
    public static final Uuid uuidFromRandomBytes(byte[] randomBytes) {
        Intrinsics.checkNotNullParameter(randomBytes, "randomBytes");
        byte b = (byte) (randomBytes[6] & 15);
        randomBytes[6] = b;
        randomBytes[6] = (byte) (b | 64);
        byte b2 = (byte) (randomBytes[8] & 63);
        randomBytes[8] = b2;
        randomBytes[8] = (byte) (b2 | ByteCompanionObject.MIN_VALUE);
        return Uuid.Companion.fromByteArray(randomBytes);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final long toLong$UuidKt__UuidKt(byte[] bArr, int i) {
        return (((long) bArr[i + 7]) & 255) | ((((long) bArr[i]) & 255) << 56) | ((((long) bArr[i + 1]) & 255) << 48) | ((((long) bArr[i + 2]) & 255) << 40) | ((((long) bArr[i + 3]) & 255) << 32) | ((((long) bArr[i + 4]) & 255) << 24) | ((((long) bArr[i + 5]) & 255) << 16) | ((((long) bArr[i + 6]) & 255) << 8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void formatBytesInto$UuidKt__UuidKt(long j, byte[] bArr, int i, int i2) {
        int i3 = i + (i2 * 2);
        for (int i4 = 0; i4 < i2; i4++) {
            int i5 = HexExtensionsKt.getBYTE_TO_LOWER_CASE_HEX_DIGITS()[(int) (255 & j)];
            bArr[i3 - 1] = (byte) i5;
            i3 -= 2;
            bArr[i3] = (byte) (i5 >> 8);
            j >>= 8;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void checkHyphenAt$UuidKt__UuidKt(String str, int i) {
        if (str.charAt(i) != '-') {
            throw new IllegalArgumentException(("Expected '-' (hyphen) at index " + i + ", but was '" + str.charAt(i) + '\'').toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void toByteArray$UuidKt__UuidKt(long j, byte[] bArr, int i) {
        for (int i2 = 0; i2 < 8; i2++) {
            bArr[i + i2] = (byte) (j >>> ((7 - i2) * 8));
        }
    }
}
