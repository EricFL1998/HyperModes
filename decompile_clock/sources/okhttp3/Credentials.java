package okhttp3;

import java.nio.charset.Charset;
import miuix.appcompat.app.floatingactivity.multiapp.MethodCodeHelper;
import okhttp3.internal.Util;
import okio.ByteString;

/* JADX INFO: loaded from: classes3.dex */
public final class Credentials {
    private Credentials() {
    }

    public static String basic(String str, String str2) {
        return basic(str, str2, Util.ISO_8859_1);
    }

    public static String basic(String str, String str2, Charset charset) {
        return "Basic " + ByteString.encodeString(str + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + str2, charset).base64();
    }
}
