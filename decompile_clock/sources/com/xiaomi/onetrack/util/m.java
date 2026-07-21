package com.xiaomi.onetrack.util;

import android.util.Log;
import com.android.deskclock.worldclock.WorldClockEditActivity;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/* JADX INFO: loaded from: classes2.dex */
public class m {
    public static final int a = -1;
    private static final String b = "onetrack";
    private static final int c = 4096;

    public static void a(HttpURLConnection httpURLConnection) {
        if (httpURLConnection != null) {
            try {
                httpURLConnection.disconnect();
            } catch (Exception e) {
                Log.e("onetrack", "close e", e);
            }
        }
    }

    public static void a(InputStream inputStream) {
        a((Closeable) inputStream);
    }

    public static void a(OutputStream outputStream) {
        a((Closeable) outputStream);
    }

    public static void a(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                Log.e("onetrack", "closeQuietly e", e);
            }
        }
    }

    public static byte[] b(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        a(inputStream, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static long a(InputStream inputStream, OutputStream outputStream) throws IOException {
        return a(inputStream, outputStream, 4096);
    }

    public static long a(InputStream inputStream, OutputStream outputStream, int i) throws IOException {
        byte[] bArr = new byte[i];
        long j = 0;
        while (true) {
            int i2 = inputStream.read(bArr);
            if (-1 == i2) {
                return j;
            }
            outputStream.write(bArr, 0, i2);
            j += (long) i2;
        }
    }

    public static String a(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b2 : bArr) {
            String hexString = Integer.toHexString(b2 & 255);
            while (hexString.length() < 2) {
                hexString = WorldClockEditActivity.LOCAL_CITY_ID + hexString;
            }
            sb.append(hexString);
        }
        return sb.toString();
    }

    public static void b(Closeable closeable) {
        if (closeable == null || !(closeable instanceof Closeable)) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            Log.e("onetrack", "closeSafely e", e);
        }
    }
}
