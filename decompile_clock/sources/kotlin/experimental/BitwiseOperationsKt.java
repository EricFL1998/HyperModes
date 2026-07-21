package kotlin.experimental;

import com.android.deskclock.settings.SearchContract;
import kotlin.Metadata;

/* JADX INFO: compiled from: bitwiseOperations.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0005\n\u0002\b\u0004\n\u0002\u0010\n\n\u0000\u001a\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0001H\u0087\f\u001a\u0015\u0010\u0003\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0001H\u0087\f\u001a\u0015\u0010\u0004\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0001H\u0087\f\u001a\r\u0010\u0005\u001a\u00020\u0001*\u00020\u0001H\u0087\b\u001a\u0015\u0010\u0000\u001a\u00020\u0006*\u00020\u00062\u0006\u0010\u0002\u001a\u00020\u0006H\u0087\f\u001a\u0015\u0010\u0003\u001a\u00020\u0006*\u00020\u00062\u0006\u0010\u0002\u001a\u00020\u0006H\u0087\f\u001a\u0015\u0010\u0004\u001a\u00020\u0006*\u00020\u00062\u0006\u0010\u0002\u001a\u00020\u0006H\u0087\f\u001a\r\u0010\u0005\u001a\u00020\u0006*\u00020\u0006H\u0087\b¨\u0006\u0007"}, d2 = {"and", "", SearchContract.SearchResultColumn.COLUMN_OTHER, "or", "xor", "inv", "", "kotlin-stdlib"}, k = 2, mv = {2, 1, 0}, xi = 48)
public final class BitwiseOperationsKt {
    private static final byte and(byte b, byte b2) {
        return (byte) (b & b2);
    }

    private static final short and(short s, short s2) {
        return (short) (s & s2);
    }

    private static final byte inv(byte b) {
        return (byte) (~b);
    }

    private static final short inv(short s) {
        return (short) (~s);
    }

    private static final byte or(byte b, byte b2) {
        return (byte) (b | b2);
    }

    private static final short or(short s, short s2) {
        return (short) (s | s2);
    }

    private static final byte xor(byte b, byte b2) {
        return (byte) (b ^ b2);
    }

    private static final short xor(short s, short s2) {
        return (short) (s ^ s2);
    }
}
