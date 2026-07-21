package com.android.deskclock.util;

/* JADX INFO: loaded from: classes.dex */
public class ClickUtils {
    private static final int DEFAULT_FAST_CLICK_BUTTON_ID = -1;
    private static final long DEFAULT_FAST_CLICK_THRESHOLD = 300;
    private static int lastButtonId;
    private static long lastClickTime;

    public static boolean isFastClick(int i, long j) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j2 = lastClickTime;
        long j3 = jCurrentTimeMillis - j2;
        if (i == -1 && j2 > 0 && Math.abs(j3) < j) {
            return true;
        }
        if (lastButtonId == i && lastClickTime > 0 && Math.abs(j3) < j) {
            return true;
        }
        lastClickTime = jCurrentTimeMillis;
        lastButtonId = i;
        return false;
    }

    public static boolean isFastClick(int i) {
        return isFastClick(i, 300L);
    }

    public static boolean isFastClick(long j) {
        return isFastClick(-1, j);
    }

    public static boolean isFastClick() {
        return isFastClick(-1, 300L);
    }
}
