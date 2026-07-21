package com.android.deskclock.util;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/* JADX INFO: loaded from: classes.dex */
public class AlarmThreadPool {
    private static volatile Executor sPool;
    private static Handler sUiHandler = new Handler(Looper.getMainLooper());

    private static final Executor getExecutor() {
        if (sPool != null) {
            return sPool;
        }
        synchronized (AlarmThreadPool.class) {
            if (sPool == null) {
                sPool = Executors.newCachedThreadPool();
            }
        }
        return sPool;
    }

    public static void poolExecute(Runnable runnable) {
        getExecutor().execute(runnable);
    }

    public static void runOnUiThread(Runnable runnable) {
        sUiHandler.post(runnable);
    }

    public static void runOnUiThreadDelayed(Runnable runnable, long j) {
        sUiHandler.postDelayed(runnable, j);
    }
}
