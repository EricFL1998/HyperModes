package com.xiaomi.onetrack.util;

import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Process;
import android.system.Os;
import android.text.TextUtils;
import androidx.core.os.EnvironmentCompat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class b {
    static final String a = "2.0.9";
    static final String b = "OneTrack 2.0.9";
    static final String c = "CrashUtil";
    public static final String d = "*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***";
    public static final String e = "--- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---";
    public static final String f = "+++ +++ +++ +++ +++ +++ +++ +++ +++ +++ +++ +++ +++ +++ +++ +++";
    static final String g = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String h = "java";
    static final String i = "tombstone";
    static final String j = ".java.xcrash";
    private static final String k = "%21s %8s\n";
    private static final String l = "%21s %8s %21s %8s\n";
    private static final String[] m = {"/data/local/su", "/data/local/bin/su", "/data/local/xbin/su", "/system/xbin/su", "/system/bin/su", "/system/bin/.ext/su", "/system/bin/failsafe/su", "/system/sd/xbin/su", "/system/usr/we-need-root/su", "/sbin/su", "/su/bin/su"};

    private b() {
    }

    public static String a(Context context, int i2) throws Throwable {
        BufferedReader bufferedReader;
        BufferedReader bufferedReader2 = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/" + i2 + "/cmdline"));
            try {
                String line = bufferedReader.readLine();
                if (!TextUtils.isEmpty(line)) {
                    String strTrim = line.trim();
                    if (!TextUtils.isEmpty(strTrim)) {
                        try {
                            bufferedReader.close();
                        } catch (Exception unused) {
                        }
                        return strTrim;
                    }
                }
            } catch (Exception unused2) {
                if (bufferedReader != null) {
                }
                return null;
            } catch (Throwable th) {
                th = th;
                bufferedReader2 = bufferedReader;
                if (bufferedReader2 != null) {
                    try {
                        bufferedReader2.close();
                    } catch (Exception unused3) {
                    }
                }
                throw th;
            }
        } catch (Exception unused4) {
            bufferedReader = null;
        } catch (Throwable th2) {
            th = th2;
        }
        try {
            bufferedReader.close();
        } catch (Exception unused5) {
        }
        return null;
    }

    static boolean a() {
        try {
            for (String str : m) {
                if (new File(str).exists()) {
                    return true;
                }
            }
        } catch (Exception unused) {
        }
        return false;
    }

    static String b() {
        return TextUtils.join(z.b, Build.SUPPORTED_ABIS);
    }

    public static String a(Context context) {
        String str;
        try {
            str = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception unused) {
            str = null;
        }
        return TextUtils.isEmpty(str) ? EnvironmentCompat.MEDIA_UNKNOWN : str;
    }

    static String c() {
        StringBuilder sb = new StringBuilder(" Process Summary (From: android.os.Debug.MemoryInfo)\n");
        sb.append(String.format(Locale.US, k, "", "Pss(KB)"));
        sb.append(String.format(Locale.US, k, "", "------"));
        try {
            Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
            Debug.getMemoryInfo(memoryInfo);
            sb.append(String.format(Locale.US, k, "Java Heap:", memoryInfo.getMemoryStat("summary.java-heap")));
            sb.append(String.format(Locale.US, k, "Native Heap:", memoryInfo.getMemoryStat("summary.native-heap")));
            sb.append(String.format(Locale.US, k, "Code:", memoryInfo.getMemoryStat("summary.code")));
            sb.append(String.format(Locale.US, k, "Stack:", memoryInfo.getMemoryStat("summary.stack")));
            sb.append(String.format(Locale.US, k, "Graphics:", memoryInfo.getMemoryStat("summary.graphics")));
            sb.append(String.format(Locale.US, k, "Private Other:", memoryInfo.getMemoryStat("summary.private-other")));
            sb.append(String.format(Locale.US, k, "System:", memoryInfo.getMemoryStat("summary.system")));
            sb.append(String.format(Locale.US, l, "TOTAL:", memoryInfo.getMemoryStat("summary.total-pss"), "TOTAL SWAP:", memoryInfo.getMemoryStat("summary.total-swap")));
        } catch (Exception e2) {
            p.b(c, "CrashUtil getProcessMemoryInfo failed", e2);
        }
        return sb.toString();
    }

    private static String b(String str) {
        return a(str, 0);
    }

    private static String a(String str, int i2) throws Throwable {
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            try {
                try {
                    BufferedReader bufferedReader2 = new BufferedReader(new FileReader(str));
                    int i3 = 0;
                    while (true) {
                        try {
                            String line = bufferedReader2.readLine();
                            if (line == null) {
                                break;
                            }
                            String strTrim = line.trim();
                            if (strTrim.length() > 0) {
                                i3++;
                                if (i2 == 0 || i3 <= i2) {
                                    sb.append("  ").append(strTrim).append("\n");
                                }
                            }
                        } catch (Exception e2) {
                            e = e2;
                            bufferedReader = bufferedReader2;
                            p.c(c, "CrashUtil getInfo(" + str + ") failed", e);
                            if (bufferedReader != null) {
                                bufferedReader.close();
                            }
                            return sb.toString();
                        } catch (Throwable th) {
                            th = th;
                            bufferedReader = bufferedReader2;
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (Exception unused) {
                                }
                            }
                            throw th;
                        }
                    }
                    if (i2 > 0 && i3 > i2) {
                        sb.append("  ......\n").append("  (number of records: ").append(i3).append(")\n");
                    }
                    bufferedReader2.close();
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Exception e3) {
                e = e3;
            }
        } catch (Exception unused2) {
        }
        return sb.toString();
    }

    public static String a(Date date, Date date2, String str, String str2, String str3) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(g, Locale.US);
        return "*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***\nTombstone maker: 'OneTrack 2.0.9'\nCrash type: '" + str + "'\nStart time: '" + simpleDateFormat.format(date) + "'\nCrash time: '" + simpleDateFormat.format(date2) + "'\nApp ID: '" + str2 + "'\nApp version: '" + str3 + "'\nRooted: '" + (a() ? "Yes" : "No") + "'\nAPI level: '" + Build.VERSION.SDK_INT + "'\nOS version: '" + Build.VERSION.RELEASE + "'\nABI list: '" + b() + "'\nManufacturer: '" + Build.MANUFACTURER + "'\nBrand: '" + Build.BRAND + "'\nModel: '" + Build.MODEL + "'\nBuild fingerprint: '" + Build.FINGERPRINT + "'\n";
    }

    public static String d() {
        return "memory info:\n System Summary (From: /proc/meminfo)\n" + b("/proc/meminfo") + "-\n Process Status (From: /proc/PID/status)\n" + b("/proc/self/status") + "-\n Process Limits (From: /proc/PID/limits)\n" + b("/proc/self/limits") + "-\n" + c() + "\n";
    }

    public static String e() {
        if (Build.VERSION.SDK_INT >= 29) {
            return "network info:\nNot supported on Android Q (API level 29) and later.\n\n";
        }
        return "network info:\n TCP over IPv4 (From: /proc/PID/net/tcp)\n" + a("/proc/self/net/tcp", 1024) + "-\n TCP over IPv6 (From: /proc/PID/net/tcp6)\n" + a("/proc/self/net/tcp6", 1024) + "-\n UDP over IPv4 (From: /proc/PID/net/udp)\n" + a("/proc/self/net/udp", 1024) + "-\n UDP over IPv6 (From: /proc/PID/net/udp6)\n" + a("/proc/self/net/udp6", 1024) + "-\n ICMP in IPv4 (From: /proc/PID/net/icmp)\n" + a("/proc/self/net/icmp", 256) + "-\n ICMP in IPv6 (From: /proc/PID/net/icmp6)\n" + a("/proc/self/net/icmp6", 256) + "-\n UNIX domain (From: /proc/PID/net/unix)\n" + a("/proc/self/net/unix", 256) + "\n";
    }

    public static String f() {
        String str;
        StringBuilder sb = new StringBuilder("open files:\n");
        try {
            File[] fileArrListFiles = new File("/proc/self/fd").listFiles(new c());
            if (fileArrListFiles != null) {
                int i2 = 0;
                for (File file : fileArrListFiles) {
                    try {
                        str = Os.readlink(file.getAbsolutePath());
                    } catch (Exception unused) {
                        str = null;
                    }
                    sb.append("    fd ").append(file.getName()).append(": ").append(TextUtils.isEmpty(str) ? "???" : str.trim()).append('\n');
                    i2++;
                    if (i2 > 1024) {
                        break;
                    }
                }
                if (fileArrListFiles.length > 1024) {
                    sb.append("    ......\n");
                }
                sb.append("    (number of FDs: ").append(fileArrListFiles.length).append(")\n");
            }
        } catch (Exception unused2) {
        }
        sb.append('\n');
        return sb.toString();
    }

    public static String a(int i2, int i3, int i4) throws Throwable {
        int iMyPid = Process.myPid();
        StringBuilder sb = new StringBuilder();
        sb.append("logcat:\n");
        if (i2 > 0) {
            a(iMyPid, sb, "main", i2, 'D');
        }
        if (i3 > 0) {
            a(iMyPid, sb, "system", i3, 'W');
        }
        if (i4 > 0) {
            a(iMyPid, sb, "events", i3, 'I');
        }
        sb.append("\n");
        return sb.toString();
    }

    /* JADX WARN: Code duplicated, block: B:29:0x00c8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v5, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r6v6 */
    /* JADX WARN: Type inference failed for: r6v8, types: [java.io.BufferedReader] */
    private static void a(int i2, StringBuilder sb, String str, int i3, char c2) throws Throwable {
        Throwable th;
        BufferedReader bufferedReader;
        Exception e2;
        String string = Integer.toString(i2);
        String str2 = " " + string + " ";
        ArrayList arrayList = new ArrayList();
        arrayList.add("/system/bin/logcat");
        arrayList.add("-b");
        arrayList.add(str);
        arrayList.add("-d");
        arrayList.add("-v");
        arrayList.add("threadtime");
        arrayList.add("-t");
        arrayList.add(Integer.toString(i3));
        arrayList.add("--pid");
        arrayList.add(string);
        arrayList.add("*:" + c2);
        Object[] array = arrayList.toArray();
        ?? Append = sb.append("--------- tail end of log ");
        Append.append(str);
        sb.append(" (").append(TextUtils.join(" ", array)).append(")\n");
        try {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(new ProcessBuilder(new String[0]).command(arrayList).start().getInputStream()));
                while (true) {
                    try {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        } else {
                            sb.append(line).append("\n");
                        }
                    } catch (Exception e3) {
                        e2 = e3;
                        p.b(c, "CrashUtil run logcat command failed", e2);
                        if (bufferedReader == null) {
                            return;
                        }
                        try {
                            bufferedReader.close();
                        } catch (IOException unused) {
                            return;
                        }
                    }
                }
            } catch (Exception e4) {
                bufferedReader = null;
                e2 = e4;
            } catch (Throwable th2) {
                Append = 0;
                th = th2;
                if (Append != 0) {
                    try {
                        Append.close();
                    } catch (IOException unused2) {
                    }
                }
                throw th;
            }
            bufferedReader.close();
        } catch (Throwable th3) {
            th = th3;
            if (Append != 0) {
                Append.close();
            }
            throw th;
        }
    }

    public static long a(String str) {
        try {
            return new SimpleDateFormat(g, Locale.US).parse(str).getTime();
        } catch (ParseException e2) {
            e2.printStackTrace();
            return 0L;
        }
    }
}
