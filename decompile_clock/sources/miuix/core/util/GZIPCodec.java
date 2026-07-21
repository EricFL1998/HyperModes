package miuix.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/* JADX INFO: loaded from: classes2.dex */
public final class GZIPCodec {
    protected GZIPCodec() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    public static String getID() {
        return "gzip";
    }

    public static byte[] encode(byte[] bArr) throws Throwable {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gZIPOutputStream = null;
        try {
            GZIPOutputStream gZIPOutputStream2 = new GZIPOutputStream(byteArrayOutputStream);
            try {
                gZIPOutputStream2.write(bArr);
                IOUtils.closeQuietly((OutputStream) gZIPOutputStream2);
                return byteArrayOutputStream.toByteArray();
            } catch (Throwable th) {
                th = th;
                gZIPOutputStream = gZIPOutputStream2;
                IOUtils.closeQuietly((OutputStream) gZIPOutputStream);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static byte[] decode(byte[] bArr) throws Throwable {
        GZIPInputStream gZIPInputStream;
        Throwable th;
        try {
            gZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(bArr));
            try {
                byte[] byteArray = IOUtils.toByteArray(gZIPInputStream);
                IOUtils.closeQuietly((InputStream) gZIPInputStream);
                return byteArray;
            } catch (Throwable th2) {
                th = th2;
                IOUtils.closeQuietly((InputStream) gZIPInputStream);
                throw th;
            }
        } catch (Throwable th3) {
            gZIPInputStream = null;
            th = th3;
        }
    }
}
