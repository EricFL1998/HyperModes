package com.xiaomi.onetrack.c;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class l {
    public static final String a = "eventName";
    public static final String b = "data";
    private static final String c = "SystemImpCacheManager";
    private static final String d = "systemimp_cache";
    private static String e = "systemimp_cache_%s";
    private static volatile l f = null;
    private static String g = "system_imp_cache_manager";
    private Handler h;

    public static l a() {
        if (f == null) {
            b();
        }
        return f;
    }

    public static void b() {
        if (f == null) {
            synchronized (l.class) {
                if (f == null) {
                    f = new l();
                }
            }
        }
    }

    private l() {
        try {
            HandlerThread handlerThread = new HandlerThread(g);
            handlerThread.start();
            this.h = new Handler(handlerThread.getLooper());
        } catch (Throwable th) {
            com.xiaomi.onetrack.util.p.b(c, "SystemImpCacheManager init Throwable: " + th.getMessage());
        }
    }

    private static String c() {
        return com.xiaomi.onetrack.f.a.a().getFilesDir().getAbsolutePath() + File.separator + d;
    }

    public void a(String str, String str2, String str3) {
        if (this.h == null || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return;
        }
        this.h.post(new m(this, str, str2, str3));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void b(String str, String str2, String str3) {
        BufferedWriter bufferedWriter;
        FileWriter fileWriter = null;
        try {
            File file = new File(c(), String.format(e, str));
            if (!file.exists()) {
                if (file.getParentFile().exists()) {
                    file.createNewFile();
                } else {
                    new File(file.getParentFile().getAbsolutePath()).mkdirs();
                    file.createNewFile();
                }
            }
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("eventName", str2);
            jSONObject.put("data", str3);
            byte[] bArrA = c.a(jSONObject.toString());
            FileWriter fileWriter2 = new FileWriter(file, true);
            try {
                bufferedWriter = new BufferedWriter(fileWriter2);
                try {
                    bufferedWriter.write(com.xiaomi.onetrack.d.c.a(bArrA));
                    bufferedWriter.newLine();
                    com.xiaomi.onetrack.util.m.a(bufferedWriter);
                    com.xiaomi.onetrack.util.m.a(fileWriter2);
                } catch (Throwable th) {
                    th = th;
                    fileWriter = fileWriter2;
                    try {
                        com.xiaomi.onetrack.util.p.b(c, "systemimp doSaveData error: " + th.getMessage());
                        com.xiaomi.onetrack.util.m.a(bufferedWriter);
                        com.xiaomi.onetrack.util.m.a(fileWriter);
                    } catch (Throwable th2) {
                        com.xiaomi.onetrack.util.m.a(bufferedWriter);
                        com.xiaomi.onetrack.util.m.a(fileWriter);
                        throw th2;
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                bufferedWriter = null;
            }
        } catch (Throwable th4) {
            th = th4;
            bufferedWriter = null;
        }
    }

    public synchronized void a(String str) {
        try {
            File file = new File(c());
            if (file.exists() && file.isDirectory()) {
                String str2 = String.format(e, str);
                File[] fileArrListFiles = file.listFiles();
                for (int i = 0; i < fileArrListFiles.length; i++) {
                    if (fileArrListFiles[i].isFile() && fileArrListFiles[i].getName().equalsIgnoreCase(str2)) {
                        fileArrListFiles[i].delete();
                        break;
                    }
                }
            }
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(c, "systemimp removeObsoleteEvent error: " + e2.toString());
        }
    }

    /* JADX WARN: Not initialized variable reg: 2, insn: 0x008e: MOVE (r1 I:??[OBJECT, ARRAY]) = (r2 I:??[OBJECT, ARRAY]), block:B:38:0x008e */
    public static synchronized List<JSONObject> b(String str) {
        BufferedReader bufferedReader;
        FileReader fileReader;
        Exception e2;
        ArrayList arrayList;
        Closeable closeable;
        Closeable closeable2 = null;
        try {
            try {
                try {
                    try {
                        File file = new File(c(), String.format(e, str));
                        if (!file.exists()) {
                            com.xiaomi.onetrack.util.m.a((Closeable) null);
                            com.xiaomi.onetrack.util.m.a((Closeable) null);
                            return null;
                        }
                        arrayList = new ArrayList();
                        try {
                            fileReader = new FileReader(file);
                            try {
                                bufferedReader = new BufferedReader(fileReader);
                                while (true) {
                                    try {
                                        String line = bufferedReader.readLine();
                                        if (line == null) {
                                            break;
                                        }
                                        arrayList.add(new JSONObject(c.a(com.xiaomi.onetrack.d.c.a(line))));
                                    } catch (Exception e3) {
                                        e2 = e3;
                                        com.xiaomi.onetrack.util.p.b(c, "cta getCacheData error: " + e2.toString());
                                        e2.printStackTrace();
                                        com.xiaomi.onetrack.util.m.a(bufferedReader);
                                    }
                                }
                                com.xiaomi.onetrack.util.m.a(bufferedReader);
                            } catch (Exception e4) {
                                bufferedReader = null;
                                e2 = e4;
                            } catch (Throwable th) {
                                th = th;
                            }
                        } catch (Exception e5) {
                            fileReader = null;
                            e2 = e5;
                            bufferedReader = null;
                        }
                        com.xiaomi.onetrack.util.m.a(fileReader);
                        return arrayList;
                    } catch (Exception e6) {
                        bufferedReader = null;
                        fileReader = null;
                        e2 = e6;
                        arrayList = null;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    closeable2 = closeable;
                }
            } catch (Throwable th3) {
                th = th3;
                fileReader = null;
            }
            com.xiaomi.onetrack.util.m.a(closeable2);
            com.xiaomi.onetrack.util.m.a(fileReader);
            throw th;
        } catch (Throwable th4) {
            throw th4;
        }
    }

    public synchronized void c(String str) {
        if (this.h != null && !TextUtils.isEmpty(str)) {
            this.h.post(new n(this, str));
        }
    }
}
