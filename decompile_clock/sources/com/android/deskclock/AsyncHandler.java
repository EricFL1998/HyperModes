package com.android.deskclock;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

/* JADX INFO: loaded from: classes.dex */
public final class AsyncHandler {
    private static final Handler sHandler;
    private static final HandlerThread sHandlerThread;

    static {
        HandlerThread handlerThread = new HandlerThread("AsyncHandler");
        sHandlerThread = handlerThread;
        handlerThread.start();
        sHandler = new Handler(handlerThread.getLooper());
    }

    public static void post(Runnable runnable) {
        sHandler.post(runnable);
    }

    public static void postDelayed(Runnable runnable, long j) {
        sHandler.postDelayed(runnable, j);
    }

    public static Message obtain(Runnable runnable) {
        return Message.obtain(sHandler, runnable);
    }

    public static void sendMessageDelayed(Message message, long j) {
        sHandler.sendMessageDelayed(message, j);
    }

    public static void removeCallbacks(int i) {
        sHandler.removeMessages(i);
    }

    private AsyncHandler() {
    }
}
