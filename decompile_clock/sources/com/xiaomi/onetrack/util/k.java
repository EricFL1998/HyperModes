package com.xiaomi.onetrack.util;

import android.text.TextUtils;
import android.util.LruCache;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/* JADX INFO: loaded from: classes2.dex */
public class k {
    private static final String a = "FileUtil";
    private static final String b = "onetrack";
    private static final String c = "tombstone";
    private static LruCache<String, a> d = new l(1048576);

    /* JADX INFO: Access modifiers changed from: private */
    static class a {
        String a;

        private a() {
        }

        /* synthetic */ a(l lVar) {
            this();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v2, types: [com.xiaomi.onetrack.util.l] */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v4, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r1v5, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r1v7 */
    /* JADX WARN: Type inference failed for: r1v8 */
    public static void a(String str, String str2) throws Throwable {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return;
        }
        ?? r1 = 0;
        r1 = 0;
        try {
            try {
                a aVar = new a(r1);
                aVar.a = str2;
                d.put(str, aVar);
                String strB = b();
                File file = new File(strB);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File file2 = new File(strB, str);
                if (!file2.exists()) {
                    file2.createNewFile();
                }
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file2), 1024);
                try {
                    bufferedWriter.write(str2);
                    bufferedWriter.flush();
                    m.a(bufferedWriter);
                } catch (Exception e) {
                    r1 = bufferedWriter;
                    e = e;
                    p.c(a, "put error:" + e.toString());
                    m.a((Closeable) r1);
                } catch (Throwable th) {
                    r1 = bufferedWriter;
                    th = th;
                    m.a((Closeable) r1);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v11 */
    /* JADX WARN: Type inference failed for: r0v4, types: [com.xiaomi.onetrack.util.l] */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v6, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r0v7, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r0v8 */
    /* JADX WARN: Type inference failed for: r0v9 */
    public static String a(String str) throws Throwable {
        BufferedReader bufferedReader;
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        a aVar = d.get(str);
        if (aVar != null) {
            return aVar.a;
        }
        ?? r0 = 0;
        r0 = 0;
        try {
            try {
                File file = new File(b(), str);
                StringBuilder sb = new StringBuilder();
                if (file.exists()) {
                    bufferedReader = new BufferedReader(new FileReader(file));
                    while (true) {
                        try {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            sb.append(line);
                        } catch (Exception e) {
                            e = e;
                            r0 = bufferedReader;
                            p.c(a, "get error:" + e.toString());
                            m.a((Closeable) r0);
                            return "";
                        } catch (Throwable th) {
                            th = th;
                            r0 = bufferedReader;
                            m.a((Closeable) r0);
                            throw th;
                        }
                    }
                } else {
                    bufferedReader = null;
                }
                String string = sb.toString();
                a aVar2 = new a(r0);
                aVar2.a = string;
                d.put(str, aVar2);
                m.a(bufferedReader);
                return string;
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    private static String b() {
        return c("onetrack");
    }

    public static String a() {
        return c(c);
    }

    private static String c(String str) {
        String str2 = com.xiaomi.onetrack.f.a.a().getFilesDir().getAbsolutePath() + File.separator + str;
        File file = new File(str2);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str2;
    }

    public static void b(String str) {
        try {
            if (TextUtils.isEmpty(str)) {
                return;
            }
            d.remove(str);
            File file = new File(b(), str);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        } catch (Exception e) {
            p.c(a, "clear error:" + e.toString());
        }
    }

    /* JADX WARN: Not initialized variable reg: 2, insn: 0x0072: MOVE (r0 I:??[OBJECT, ARRAY]) = (r2 I:??[OBJECT, ARRAY]), block:B:30:0x0072 */
    public static String a(String str, int i) throws Throwable {
        BufferedReader bufferedReader;
        Closeable closeable;
        Closeable closeable2 = null;
        try {
            try {
                File file = new File(str);
                StringBuilder sb = new StringBuilder();
                if (file.exists()) {
                    bufferedReader = new BufferedReader(new FileReader(file));
                    do {
                        try {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            sb.append(line).append("\n");
                        } catch (Exception e) {
                            e = e;
                            p.c(a, "get error:" + e.toString());
                            m.a(bufferedReader);
                            return null;
                        }
                    } while (sb.length() <= i);
                } else {
                    bufferedReader = null;
                }
                if (sb.length() > i) {
                    String strSubstring = sb.substring(0, i - 1);
                    m.a(bufferedReader);
                    return strSubstring;
                }
                String string = sb.toString();
                m.a(bufferedReader);
                return string;
            } catch (Exception e2) {
                e = e2;
                bufferedReader = null;
            } catch (Throwable th) {
                th = th;
                m.a(closeable2);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            closeable2 = closeable;
            m.a(closeable2);
            throw th;
        }
    }

    public static void a(File file) {
        try {
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        } catch (Exception e) {
            p.c(a, "failed to remove file: " + file.getName() + z.b + e.toString());
        }
    }
}
