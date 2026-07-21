package kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: utfEncoding.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class UtfEncodingKt {
    public static final byte[] stringsToBytes(String[] strings) {
        Intrinsics.checkNotNullParameter(strings, "strings");
        int length = 0;
        for (String str : strings) {
            length += str.length();
        }
        byte[] bArr = new byte[length];
        int i = 0;
        for (String str2 : strings) {
            int length2 = str2.length();
            int i2 = 0;
            while (i2 < length2) {
                bArr[i] = (byte) str2.charAt(i2);
                i2++;
                i++;
            }
        }
        return bArr;
    }
}
