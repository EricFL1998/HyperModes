package miuix.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* JADX INFO: loaded from: classes3.dex */
public class DigestUtils {
    public static final String ALGORITHM_MD5 = "MD5";
    public static final String ALGORITHM_SHA_1 = "SHA-1";
    private static final int BUFFER_SIZE = 4096;

    protected DigestUtils() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    public static byte[] get(CharSequence charSequence, String str) {
        return get(charSequence.toString().getBytes(), str);
    }

    public static byte[] get(byte[] bArr, String str) {
        try {
            return get(new ByteArrayInputStream(bArr), str);
        } catch (IOException e) {
            throw new RuntimeException("IO exception happend in ByteArrayInputStream", e);
        }
    }

    public static byte[] get(InputStream inputStream, String str) throws IOException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(str);
            byte[] bArr = new byte[4096];
            while (true) {
                int i = inputStream.read(bArr);
                if (i > 0) {
                    messageDigest.update(bArr, 0, i);
                } else {
                    return messageDigest.digest();
                }
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        }
    }
}
