package com.xiaomi.onetrack.api;

import android.os.Process;
import android.util.Log;
import com.xiaomi.onetrack.CrashAnalysis;
import java.io.File;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public class k implements Thread.UncaughtExceptionHandler {
    private static final String a = "OneTrackExceptionHandler";
    private static final String c = "tombstone";
    private static final String d = ".java.xcrash";
    private static final String e = "backtrace feature id:\n\t";
    private static final String f = "error reason:\n\t";
    private static final long h = 2;
    private Thread.UncaughtExceptionHandler b;
    private final Date g = new Date();
    private int i = 50;
    private int j = 50;
    private int k = 200;
    private boolean l = true;
    private boolean m = true;

    public void a() {
        Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (defaultUncaughtExceptionHandler instanceof k) {
            return;
        }
        this.b = defaultUncaughtExceptionHandler;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable th) {
        Log.d(com.xiaomi.onetrack.util.p.a(a), "crash happened->stacktrace: " + th.getStackTrace());
        FutureTask futureTask = new FutureTask(new l(this, thread, th), null);
        com.xiaomi.onetrack.util.i.a(futureTask);
        try {
            futureTask.get(2L, TimeUnit.SECONDS);
        } catch (Exception e2) {
            Log.e(com.xiaomi.onetrack.util.p.a(a), "handleException error :" + e2.getMessage());
        }
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = this.b;
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: Removed unreachable split cross block B:81:0x0145 */
    /* JADX WARN: Code duplicated, block: B:31:0x00c5 A[Catch: all -> 0x011d, Exception -> 0x0121, TryCatch #11 {Exception -> 0x0121, all -> 0x011d, blocks: (B:29:0x008b, B:31:0x00c5, B:32:0x00cc, B:34:0x00d0, B:36:0x00d4, B:39:0x00e7, B:41:0x00eb, B:42:0x00f6, B:44:0x00fa, B:45:0x0105, B:38:0x00d8), top: B:87:0x008b }] */
    /* JADX WARN: Code duplicated, block: B:38:0x00d8 A[Catch: all -> 0x011d, Exception -> 0x0121, TryCatch #11 {Exception -> 0x0121, all -> 0x011d, blocks: (B:29:0x008b, B:31:0x00c5, B:32:0x00cc, B:34:0x00d0, B:36:0x00d4, B:39:0x00e7, B:41:0x00eb, B:42:0x00f6, B:44:0x00fa, B:45:0x0105, B:38:0x00d8), top: B:87:0x008b }] */
    /* JADX WARN: Code duplicated, block: B:41:0x00eb A[Catch: all -> 0x011d, Exception -> 0x0121, TryCatch #11 {Exception -> 0x0121, all -> 0x011d, blocks: (B:29:0x008b, B:31:0x00c5, B:32:0x00cc, B:34:0x00d0, B:36:0x00d4, B:39:0x00e7, B:41:0x00eb, B:42:0x00f6, B:44:0x00fa, B:45:0x0105, B:38:0x00d8), top: B:87:0x008b }] */
    /* JADX WARN: Code duplicated, block: B:44:0x00fa A[Catch: all -> 0x011d, Exception -> 0x0121, TryCatch #11 {Exception -> 0x0121, all -> 0x011d, blocks: (B:29:0x008b, B:31:0x00c5, B:32:0x00cc, B:34:0x00d0, B:36:0x00d4, B:39:0x00e7, B:41:0x00eb, B:42:0x00f6, B:44:0x00fa, B:45:0x0105, B:38:0x00d8), top: B:87:0x008b }] */
    /* JADX WARN: Code duplicated, block: B:67:0x0145 A[ORIG_RETURN, RETURN] */
    /* JADX WARN: Code duplicated, block: B:75:0x0084 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    public void a(Thread thread, Throwable th) throws Throwable {
        File file;
        String string;
        String strA;
        Throwable th2;
        RandomAccessFile randomAccessFile;
        RandomAccessFile randomAccessFile2;
        int i;
        Date date = new Date();
        Object obj = null;
        RandomAccessFile randomAccessFile3 = null;
        obj = null;
        try {
            file = new File(String.format(Locale.US, "%s/%s_%020d_%s__%s%s", com.xiaomi.onetrack.util.k.a(), c, Long.valueOf(this.g.getTime() * 1000), com.xiaomi.onetrack.util.b.a(com.xiaomi.onetrack.f.a.b()), com.xiaomi.onetrack.f.a.e(), d));
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (Exception e2) {
                e = e2;
                com.xiaomi.onetrack.util.p.b(a, "JavaCrashHandler createLogFile failed", e);
            }
        } catch (Exception e3) {
            e = e3;
            file = null;
        }
        try {
            StringWriter stringWriter = new StringWriter();
            try {
                th.printStackTrace(new PrintWriter(stringWriter));
                string = stringWriter.toString();
                try {
                    strA = a(date, thread, string);
                } catch (Exception e4) {
                    e = e4;
                    com.xiaomi.onetrack.util.p.b(a, "JavaCrashHandler getEmergency failed", e);
                    strA = null;
                }
            } catch (Exception e5) {
                e = e5;
                string = null;
                com.xiaomi.onetrack.util.p.b(a, "JavaCrashHandler getEmergency failed", e);
                strA = null;
                try {
                    if (file != null) {
                        try {
                            randomAccessFile2 = new RandomAccessFile(file, "rws");
                            try {
                                String strCalculateJavaDigest = CrashAnalysis.calculateJavaDigest(string);
                                randomAccessFile2.write((e + strCalculateJavaDigest + "\n\n").getBytes("UTF-8"));
                                randomAccessFile2.write((f + th.toString() + "\n\n").getBytes("UTF-8"));
                                if (strA != null) {
                                    randomAccessFile2.write(strA.getBytes("UTF-8"));
                                }
                                i = this.k;
                                if (i <= 0) {
                                    randomAccessFile2.write(com.xiaomi.onetrack.util.b.a(i, this.i, this.j).getBytes("UTF-8"));
                                } else {
                                    randomAccessFile2.write(com.xiaomi.onetrack.util.b.a(i, this.i, this.j).getBytes("UTF-8"));
                                }
                                if (this.l) {
                                    randomAccessFile2.write(com.xiaomi.onetrack.util.b.f().getBytes("UTF-8"));
                                }
                                if (this.m) {
                                    randomAccessFile2.write(com.xiaomi.onetrack.util.b.e().getBytes("UTF-8"));
                                }
                                randomAccessFile2.write(com.xiaomi.onetrack.util.b.d().getBytes("UTF-8"));
                                randomAccessFile2.write("foreground:\nyes\n\n".getBytes("UTF-8"));
                                randomAccessFile2.close();
                                obj = strCalculateJavaDigest;
                            } catch (Exception e6) {
                                e = e6;
                                randomAccessFile3 = randomAccessFile2;
                                com.xiaomi.onetrack.util.p.b(a, "JavaCrashHandler write log file failed", e);
                                obj = randomAccessFile3;
                                if (randomAccessFile3 != null) {
                                    randomAccessFile3.close();
                                    obj = randomAccessFile3;
                                }
                            } catch (Throwable th3) {
                                th2 = th3;
                                randomAccessFile = randomAccessFile2;
                                if (randomAccessFile == 0) {
                                    throw th2;
                                }
                                try {
                                    randomAccessFile.close();
                                    throw th2;
                                } catch (Exception e7) {
                                    com.xiaomi.onetrack.util.p.b(a, "JavaCrashHandler close RandomAccessFile failed", e7);
                                    throw th2;
                                }
                            }
                        } catch (Exception e8) {
                            e = e8;
                        }
                    }
                } catch (Exception e9) {
                    com.xiaomi.onetrack.util.p.b(a, "JavaCrashHandler close RandomAccessFile failed", e9);
                }
            }
        } catch (Exception e10) {
            e = e10;
        }
        try {
            if (file != null) {
                randomAccessFile2 = new RandomAccessFile(file, "rws");
                String strCalculateJavaDigest2 = CrashAnalysis.calculateJavaDigest(string);
                randomAccessFile2.write((e + strCalculateJavaDigest2 + "\n\n").getBytes("UTF-8"));
                randomAccessFile2.write((f + th.toString() + "\n\n").getBytes("UTF-8"));
                if (strA != null) {
                    randomAccessFile2.write(strA.getBytes("UTF-8"));
                }
                i = this.k;
                if (i <= 0 || this.i > 0 || this.j > 0) {
                    randomAccessFile2.write(com.xiaomi.onetrack.util.b.a(i, this.i, this.j).getBytes("UTF-8"));
                }
                if (this.l) {
                    randomAccessFile2.write(com.xiaomi.onetrack.util.b.f().getBytes("UTF-8"));
                }
                if (this.m) {
                    randomAccessFile2.write(com.xiaomi.onetrack.util.b.e().getBytes("UTF-8"));
                }
                randomAccessFile2.write(com.xiaomi.onetrack.util.b.d().getBytes("UTF-8"));
                randomAccessFile2.write("foreground:\nyes\n\n".getBytes("UTF-8"));
                randomAccessFile2.close();
                obj = strCalculateJavaDigest2;
            }
        } catch (Throwable th4) {
            th2 = th4;
            randomAccessFile = obj;
        }
    }

    private String a(Date date, Thread thread, String str) {
        return com.xiaomi.onetrack.util.b.a(this.g, date, "java", com.xiaomi.onetrack.f.a.e(), com.xiaomi.onetrack.util.b.a(com.xiaomi.onetrack.f.a.b())) + "pid: " + Process.myPid() + ", tid: " + Process.myTid() + ", name: " + thread.getName() + "  >>> " + com.xiaomi.onetrack.f.a.e() + " <<<\n\njava stacktrace:\n" + str + "\n";
    }
}
