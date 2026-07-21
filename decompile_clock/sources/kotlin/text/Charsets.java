package kotlin.text;

import java.nio.charset.Charset;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Charsets.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0010\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u000b\u001a\u00020\u00058G¢\u0006\u0006\u001a\u0004\b\f\u0010\rR\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u000f\u001a\u00020\u00058G¢\u0006\u0006\u001a\u0004\b\u0010\u0010\rR\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u0012\u001a\u00020\u00058G¢\u0006\u0006\u001a\u0004\b\u0013\u0010\rR\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0015"}, d2 = {"Lkotlin/text/Charsets;", "", "<init>", "()V", "UTF_8", "Ljava/nio/charset/Charset;", "UTF_16", "UTF_16BE", "UTF_16LE", "US_ASCII", "ISO_8859_1", "UTF_32", "UTF32", "()Ljava/nio/charset/Charset;", "utf_32", "UTF_32LE", "UTF32_LE", "utf_32le", "UTF_32BE", "UTF32_BE", "utf_32be", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
public final class Charsets {
    public static final Charsets INSTANCE = new Charsets();
    public static final Charset ISO_8859_1;
    public static final Charset US_ASCII;
    public static final Charset UTF_16;
    public static final Charset UTF_16BE;
    public static final Charset UTF_16LE;
    public static final Charset UTF_8;
    private static volatile Charset utf_32;
    private static volatile Charset utf_32be;
    private static volatile Charset utf_32le;

    private Charsets() {
    }

    static {
        Charset charsetForName = Charset.forName("UTF-8");
        Intrinsics.checkNotNullExpressionValue(charsetForName, "forName(...)");
        UTF_8 = charsetForName;
        Charset charsetForName2 = Charset.forName("UTF-16");
        Intrinsics.checkNotNullExpressionValue(charsetForName2, "forName(...)");
        UTF_16 = charsetForName2;
        Charset charsetForName3 = Charset.forName("UTF-16BE");
        Intrinsics.checkNotNullExpressionValue(charsetForName3, "forName(...)");
        UTF_16BE = charsetForName3;
        Charset charsetForName4 = Charset.forName("UTF-16LE");
        Intrinsics.checkNotNullExpressionValue(charsetForName4, "forName(...)");
        UTF_16LE = charsetForName4;
        Charset charsetForName5 = Charset.forName("US-ASCII");
        Intrinsics.checkNotNullExpressionValue(charsetForName5, "forName(...)");
        US_ASCII = charsetForName5;
        Charset charsetForName6 = Charset.forName("ISO-8859-1");
        Intrinsics.checkNotNullExpressionValue(charsetForName6, "forName(...)");
        ISO_8859_1 = charsetForName6;
    }

    public final Charset UTF32() {
        Charset charset = utf_32;
        if (charset != null) {
            return charset;
        }
        Charset charsetForName = Charset.forName("UTF-32");
        Intrinsics.checkNotNullExpressionValue(charsetForName, "forName(...)");
        utf_32 = charsetForName;
        return charsetForName;
    }

    public final Charset UTF32_LE() {
        Charset charset = utf_32le;
        if (charset != null) {
            return charset;
        }
        Charset charsetForName = Charset.forName("UTF-32LE");
        Intrinsics.checkNotNullExpressionValue(charsetForName, "forName(...)");
        utf_32le = charsetForName;
        return charsetForName;
    }

    public final Charset UTF32_BE() {
        Charset charset = utf_32be;
        if (charset != null) {
            return charset;
        }
        Charset charsetForName = Charset.forName("UTF-32BE");
        Intrinsics.checkNotNullExpressionValue(charsetForName, "forName(...)");
        utf_32be = charsetForName;
        return charsetForName;
    }
}
