package com.xiaomi.onetrack.g;

import android.text.TextUtils;
import com.xiaomi.onetrack.d.d;
import com.xiaomi.onetrack.d.f;
import com.xiaomi.onetrack.util.m;
import com.xiaomi.onetrack.util.p;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class b {
    public static final int a = 10000;
    public static final int b = 15000;
    public static final String c = "OT_SID";
    public static final String d = "OT_ts";
    public static final String e = "OT_net";
    public static final String f = "OT_sender";
    public static final String g = "OT_protocol";
    private static String h = "HttpUtil";
    private static final String i = "GET";
    private static final String j = "POST";
    private static final String k = "&";
    private static final String l = "=";
    private static final String m = "UTF-8";
    private static final String n = "miui_sdkconfig_jafej!@#)(*e@!#";
    private static final int o = 3;

    private b() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r0v1 */
    /* JADX WARN: Type inference failed for: r0v2 */
    /* JADX WARN: Type inference failed for: r0v3, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r0v4, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v6 */
    /* JADX WARN: Type inference failed for: r0v9, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r1v1 */
    /* JADX WARN: Type inference failed for: r1v2 */
    /* JADX WARN: Type inference failed for: r1v3, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v5 */
    /* JADX WARN: Type inference failed for: r2v11, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r2v2, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r2v4, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r2v5 */
    /* JADX WARN: Type inference failed for: r2v6 */
    /* JADX WARN: Type inference failed for: r2v7, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r2v8, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r3v2, types: [int] */
    public static String a(String str, byte[] bArr) throws IOException {
        InputStream inputStream;
        ?? outputStream = "sid:";
        String str2 = h;
        ?? Append = new StringBuilder("doPost url=").append(str).append(", len=");
        ?? length = bArr.length;
        ?? string = Append.append(length).toString();
        p.a(str2, string);
        ?? r1 = 0;
        r1 = 0;
        r1 = 0;
        try {
            try {
                string = (HttpURLConnection) new URL(str).openConnection();
                try {
                    string.setConnectTimeout(10000);
                    string.setReadTimeout(15000);
                    string.setDoOutput(true);
                    string.setRequestMethod("POST");
                    string.setRequestProperty("Content-Type", "application/octet-stream");
                    String str3 = f.a().b()[1];
                    string.setRequestProperty(c, str3);
                    string.setRequestProperty(d, Long.toString(System.currentTimeMillis()));
                    string.setRequestProperty(e, c.a(com.xiaomi.onetrack.f.a.b()).toString());
                    string.setRequestProperty(f, com.xiaomi.onetrack.f.a.e());
                    string.setRequestProperty(g, "3.0");
                    p.a(h, "sid:" + str3);
                    outputStream = string.getOutputStream();
                    try {
                        outputStream.write(bArr, 0, bArr.length);
                        outputStream.flush();
                        int responseCode = string.getResponseCode();
                        inputStream = string.getInputStream();
                        try {
                            byte[] bArrB = m.b(inputStream);
                            p.a(h, String.format("HttpUtils POST 上传成功 url: %s, code: %s", str, Integer.valueOf(responseCode)));
                            String str4 = new String(bArrB, m);
                            m.a(inputStream);
                            m.a((OutputStream) outputStream);
                            m.a((HttpURLConnection) string);
                            return str4;
                        } catch (IOException e2) {
                            e = e2;
                            p.b(h, String.format("HttpUtils POST 上传失败, url: %s, error: %s", str, e.getMessage()));
                            m.a(inputStream);
                            m.a((OutputStream) outputStream);
                            m.a((HttpURLConnection) string);
                            return null;
                        }
                    } catch (IOException e3) {
                        e = e3;
                        inputStream = null;
                    } catch (Throwable th) {
                        th = th;
                        m.a((InputStream) r1);
                        m.a((OutputStream) outputStream);
                        m.a((HttpURLConnection) string);
                        throw th;
                    }
                } catch (IOException e4) {
                    e = e4;
                    outputStream = 0;
                    inputStream = null;
                } catch (Throwable th2) {
                    th = th2;
                    outputStream = 0;
                }
            } catch (IOException e5) {
                e = e5;
                outputStream = 0;
                string = 0;
                inputStream = null;
            } catch (Throwable th3) {
                th = th3;
                outputStream = 0;
                string = 0;
            }
        } catch (Throwable th4) {
            th = th4;
            r1 = length;
        }
    }

    public static String a(String str) throws IOException {
        return a(str, null, false);
    }

    public static String a(String str, Map<String, String> map) throws IOException {
        return a(str, map, true);
    }

    public static String a(String str, Map<String, String> map, boolean z) throws IOException {
        return a("GET", str, map, z);
    }

    public static String b(String str, Map<String, String> map) throws IOException {
        return b(str, map, true);
    }

    public static String b(String str, Map<String, String> map, boolean z) throws IOException {
        return a("POST", str, map, z);
    }

    private static String a(String str, String str2, Map<String, String> map, boolean z) throws Throwable {
        String strA;
        OutputStream outputStream;
        HttpURLConnection httpURLConnection;
        InputStream inputStream;
        InputStream inputStream2 = null;
        if (map == null) {
            strA = null;
        } else {
            try {
                strA = a(map, z);
            } catch (Exception e2) {
                e = e2;
                outputStream = null;
                httpURLConnection = null;
                inputStream = null;
                p.b(h, "HttpUtils POST 上传异常", e);
                m.a(inputStream);
                m.a(outputStream);
                m.a(httpURLConnection);
                return null;
            } catch (Throwable th) {
                th = th;
                outputStream = null;
                httpURLConnection = null;
                m.a(inputStream2);
                m.a(outputStream);
                m.a(httpURLConnection);
                throw th;
            }
        }
        httpURLConnection = (HttpURLConnection) new URL((!"GET".equals(str) || strA == null) ? str2 : str2 + "? " + strA).openConnection();
        try {
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(15000);
            try {
                try {
                    if ("GET".equals(str)) {
                        httpURLConnection.setRequestMethod("GET");
                    } else {
                        if ("POST".equals(str) && strA != null) {
                            httpURLConnection.setRequestMethod("POST");
                            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            httpURLConnection.setDoOutput(true);
                            byte[] bytes = strA.getBytes(m);
                            outputStream = httpURLConnection.getOutputStream();
                            try {
                                outputStream.write(bytes, 0, bytes.length);
                                outputStream.flush();
                            } catch (Exception e3) {
                                e = e3;
                                inputStream = null;
                                p.b(h, "HttpUtils POST 上传异常", e);
                                m.a(inputStream);
                                m.a(outputStream);
                                m.a(httpURLConnection);
                                return null;
                            } catch (Throwable th2) {
                                th = th2;
                                m.a(inputStream2);
                                m.a(outputStream);
                                m.a(httpURLConnection);
                                throw th;
                            }
                        }
                        int responseCode = httpURLConnection.getResponseCode();
                        inputStream = httpURLConnection.getInputStream();
                        byte[] bArrB = m.b(inputStream);
                        p.a(h, String.format("HttpUtils POST 上传成功 url: %s, code: %s", str2, Integer.valueOf(responseCode)));
                        String str3 = new String(bArrB, m);
                        m.a(inputStream);
                        m.a(outputStream);
                        m.a(httpURLConnection);
                        return str3;
                    }
                    byte[] bArrB2 = m.b(inputStream);
                    p.a(h, String.format("HttpUtils POST 上传成功 url: %s, code: %s", str2, Integer.valueOf(responseCode)));
                    String str4 = new String(bArrB2, m);
                    m.a(inputStream);
                    m.a(outputStream);
                    m.a(httpURLConnection);
                    return str4;
                } catch (Throwable th3) {
                    th = th3;
                    inputStream2 = inputStream;
                    m.a(inputStream2);
                    m.a(outputStream);
                    m.a(httpURLConnection);
                    throw th;
                }
            } catch (Exception e4) {
                e = e4;
                p.b(h, "HttpUtils POST 上传异常", e);
                m.a(inputStream);
                m.a(outputStream);
                m.a(httpURLConnection);
                return null;
            }
            outputStream = null;
            int responseCode2 = httpURLConnection.getResponseCode();
            inputStream = httpURLConnection.getInputStream();
        } catch (Exception e5) {
            e = e5;
            outputStream = null;
            inputStream = null;
        } catch (Throwable th4) {
            th = th4;
            outputStream = null;
        }
    }

    private static String a(Map<String, String> map, boolean z) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            try {
                if (!TextUtils.isEmpty(entry.getKey())) {
                    if (sb.length() > 0) {
                        sb.append(k);
                    }
                    sb.append(URLEncoder.encode(entry.getKey(), m));
                    sb.append(l);
                    sb.append(URLEncoder.encode(entry.getValue() == null ? "null" : entry.getValue(), m));
                }
            } catch (UnsupportedEncodingException unused) {
                p.b(h, "format params failed");
            }
        }
        if (z) {
            String strA = a(map);
            if (sb.length() > 0) {
                sb.append(k);
            }
            sb.append(URLEncoder.encode("sign", m));
            sb.append(l);
            sb.append(URLEncoder.encode(strA, m));
        }
        return sb.toString();
    }

    public static String a(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        if (map != null) {
            ArrayList<String> arrayList = new ArrayList(map.keySet());
            Collections.sort(arrayList);
            for (String str : arrayList) {
                if (!TextUtils.isEmpty(str)) {
                    sb.append(str);
                    sb.append(map.get(str));
                }
            }
        }
        sb.append(n);
        return d.c(sb.toString());
    }

    public static boolean b(String str) throws Throwable {
        if (TextUtils.isEmpty(str)) {
            p.a(h, "doGetAdMonitor dbUrl is null");
            return true;
        }
        HttpURLConnection httpURLConnection = null;
        int i2 = 0;
        int i3 = 0;
        while (i2 < 3) {
            try {
                try {
                    try {
                        if (i3 / 100 == 3) {
                            i2++;
                            str = httpURLConnection.getHeaderField("Location");
                            p.a(h, "redirect url is:" + str);
                        }
                        HttpURLConnection httpURLConnection2 = (HttpURLConnection) new URL(str).openConnection();
                        try {
                            httpURLConnection2.setInstanceFollowRedirects(false);
                            httpURLConnection2.setRequestMethod("GET");
                            httpURLConnection2.setConnectTimeout(10000);
                            httpURLConnection2.setReadTimeout(15000);
                            int responseCode = httpURLConnection2.getResponseCode();
                            p.a(h, "AdMonitor get 请求url:" + str + "_ResponseCode：" + responseCode);
                            if (responseCode / 100 != 5 && responseCode / 100 != 3) {
                                try {
                                    m.a(httpURLConnection2);
                                } catch (Exception unused) {
                                }
                                return true;
                            }
                            if (responseCode / 100 != 3) {
                                m.a(httpURLConnection2);
                                return false;
                            }
                            i3 = responseCode;
                            httpURLConnection = httpURLConnection2;
                        } catch (ProtocolException e2) {
                            e = e2;
                            httpURLConnection = httpURLConnection2;
                            if (TextUtils.isEmpty(e.getMessage()) && e.getMessage().contains("200 OK")) {
                                p.a(h, "response code is 200, bug status line is invalid.");
                                try {
                                    m.a(httpURLConnection);
                                } catch (Exception unused2) {
                                }
                                return true;
                            }
                            m.a(httpURLConnection);
                        } catch (Exception e3) {
                            e = e3;
                            httpURLConnection = httpURLConnection2;
                            p.b(h, "HttpUtils doGetAdMonitor 上传异常:" + e.getMessage());
                            m.a(httpURLConnection);
                        } catch (Throwable th) {
                            th = th;
                            httpURLConnection = httpURLConnection2;
                            try {
                                m.a(httpURLConnection);
                            } catch (Exception unused3) {
                            }
                            throw th;
                        }
                    } catch (ProtocolException e4) {
                        e = e4;
                    } catch (Exception e5) {
                        e = e5;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Exception unused4) {
            }
        }
        p.a(h, "redirectCount >= 3, return true");
        try {
            m.a(httpURLConnection);
        } catch (Exception unused5) {
        }
        return true;
    }
}
