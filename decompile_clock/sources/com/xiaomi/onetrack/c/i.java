package com.xiaomi.onetrack.c;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class i {
    public static final long a = 0;
    public static final long b = 1;
    public static final long c = 2;
    public static final String d = "eventName";
    public static final String e = "data";
    private static final String f = "NetworkAccessManager";
    private static final String g = "networkAccess";
    private static String h = "onetrack_netaccess_%s";
    private static SimpleDateFormat i = new SimpleDateFormat("yyyyMMdd");
    private static boolean j = false;
    private static volatile boolean k = true;
    private static volatile boolean l = false;

    public static boolean a() {
        return j;
    }

    public static void a(boolean z) {
        j = z;
    }

    public static boolean b() {
        return !new File(com.xiaomi.onetrack.f.a.a().getFilesDir(), ".ot_net_disallowed").exists();
    }

    public static void b(boolean z) {
        File file = new File(com.xiaomi.onetrack.f.a.a().getFilesDir(), ".ot_net_allowed");
        File file2 = new File(com.xiaomi.onetrack.f.a.a().getFilesDir(), ".ot_net_disallowed");
        try {
            if (z) {
                file.createNewFile();
                if (file2.exists()) {
                    file2.delete();
                }
            } else {
                file2.createNewFile();
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (IOException e2) {
            com.xiaomi.onetrack.util.p.b(f, "setNetworkAccessStateEnabled: " + z + "failed ", e2);
        }
    }

    private static String e() {
        return com.xiaomi.onetrack.f.a.a().getFilesDir().getAbsolutePath() + File.separator + g;
    }

    public static void a(String str, String str2) {
        com.xiaomi.onetrack.util.i.a(new j(str, str2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void c(String str, String str2) {
        FileWriter fileWriter;
        File file = new File(e(), String.format(h, i.format(new Date())));
        BufferedWriter bufferedWriter = null;
        try {
            if (!file.exists()) {
                if (file.getParentFile().exists()) {
                    file.createNewFile();
                } else {
                    new File(file.getParentFile().getAbsolutePath()).mkdirs();
                    file.createNewFile();
                }
            }
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("eventName", str);
            jSONObject.put("data", str2);
            byte[] bArrA = c.a(jSONObject.toString());
            fileWriter = new FileWriter(file, true);
            try {
                try {
                    BufferedWriter bufferedWriter2 = new BufferedWriter(fileWriter);
                    try {
                        bufferedWriter2.write(com.xiaomi.onetrack.d.c.a(bArrA));
                        bufferedWriter2.newLine();
                        k = true;
                        com.xiaomi.onetrack.util.m.a(bufferedWriter2);
                    } catch (Exception e2) {
                        e = e2;
                        bufferedWriter = bufferedWriter2;
                        com.xiaomi.onetrack.util.p.b(f, "cta doSaveData error: " + e.toString());
                        e.printStackTrace();
                        com.xiaomi.onetrack.util.m.a(bufferedWriter);
                    } catch (Throwable th) {
                        th = th;
                        bufferedWriter = bufferedWriter2;
                        com.xiaomi.onetrack.util.m.a(bufferedWriter);
                        com.xiaomi.onetrack.util.m.a(fileWriter);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Exception e3) {
                e = e3;
            }
        } catch (Exception e4) {
            e = e4;
            fileWriter = null;
        } catch (Throwable th3) {
            th = th3;
            fileWriter = null;
        }
        com.xiaomi.onetrack.util.m.a(fileWriter);
    }

    public static synchronized void c(boolean z) {
        try {
            File file = new File(e());
            if (file.exists() && file.isDirectory()) {
                String str = String.format(h, i.format(new Date()));
                File[] fileArrListFiles = file.listFiles();
                for (int i2 = 0; i2 < fileArrListFiles.length; i2++) {
                    if (fileArrListFiles[i2].isFile() && (z || !fileArrListFiles[i2].getName().equalsIgnoreCase(str))) {
                        fileArrListFiles[i2].delete();
                    }
                }
                if (file.listFiles().length == 0) {
                    k = false;
                }
                return;
            }
            k = false;
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(f, "cta removeObsoleteEvent error: " + e2.toString());
            e2.printStackTrace();
        }
    }

    public static synchronized List<JSONObject> c() {
        FileReader fileReader;
        Exception e2;
        BufferedReader bufferedReader;
        File file = new File(e(), String.format(h, i.format(new Date())));
        BufferedReader bufferedReader2 = null;
        if (!file.exists()) {
            return null;
        }
        List<JSONObject> arrayList = new ArrayList<>();
        try {
            fileReader = new FileReader(file);
            try {
                bufferedReader = new BufferedReader(fileReader);
                while (true) {
                    try {
                        try {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            arrayList.add(new JSONObject(c.a(com.xiaomi.onetrack.d.c.a(line))));
                        } catch (Throwable th) {
                            th = th;
                            bufferedReader2 = bufferedReader;
                            com.xiaomi.onetrack.util.m.a(bufferedReader2);
                            com.xiaomi.onetrack.util.m.a(fileReader);
                            throw th;
                        }
                    } catch (Exception e3) {
                        e2 = e3;
                        com.xiaomi.onetrack.util.p.b(f, "cta getCacheData error: " + e2.toString());
                        e2.printStackTrace();
                        com.xiaomi.onetrack.util.m.a(bufferedReader);
                    }
                }
                com.xiaomi.onetrack.util.m.a(bufferedReader);
            } catch (Exception e4) {
                e2 = e4;
                bufferedReader = null;
            } catch (Throwable th2) {
                th = th2;
                com.xiaomi.onetrack.util.m.a(bufferedReader2);
                com.xiaomi.onetrack.util.m.a(fileReader);
                throw th;
            }
        } catch (Exception e5) {
            fileReader = null;
            e2 = e5;
            bufferedReader = null;
        } catch (Throwable th3) {
            th = th3;
            fileReader = null;
        }
        com.xiaomi.onetrack.util.m.a(fileReader);
        if (arrayList.size() > 200) {
            arrayList = arrayList.subList(arrayList.size() - 200, arrayList.size());
        }
        if (arrayList.size() > 0) {
            k = true;
        }
        return arrayList;
    }

    public static boolean d() {
        return !l && k;
    }

    public static synchronized void a(com.xiaomi.onetrack.api.j jVar) {
        if (d()) {
            if (jVar != null && b()) {
                l = true;
                com.xiaomi.onetrack.util.i.a(new k(jVar));
            }
        }
    }
}
