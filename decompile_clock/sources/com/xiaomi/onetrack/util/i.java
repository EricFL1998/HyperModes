package com.xiaomi.onetrack.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public class i {
    private static ThreadPoolExecutor a;
    private static int b = Runtime.getRuntime().availableProcessors() + 1;

    static {
        int i = b;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(i, i, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        a = threadPoolExecutor;
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }

    public static void a(Runnable runnable) {
        try {
            a.execute(runnable);
        } catch (Throwable unused) {
        }
    }
}
