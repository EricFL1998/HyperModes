package miuix.core.util;

import android.os.Build;
import android.os.Trace;
import android.util.Log;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes2.dex */
public class MiuixTraceUtils {
    public static final String ANIM_TRACE_TAG = "MIUIX_Widget_Animation";
    private static final String TAG = "MiuixTraceUtils";
    private static final AtomicInteger mTraceCookie = new AtomicInteger(0);
    public static boolean mAnimationTraceEnabled = isAnimationTraceEnabled();

    private static boolean isAnimationTraceEnabled() {
        try {
            return SystemProperties.getBoolean("persist.miuix.animation.trace.enable", false) || SystemProperties.getBoolean("log.tag.miuix.animation.trace.enable", false);
        } catch (Exception e) {
            Log.i(TAG, "can not access property log.tag.miuix.animation.trace.enable | persist.sys.miuix.animation.trace.enable, debug mode disabled", e);
            return false;
        }
    }

    public static int generateUniqueCookie() {
        return mTraceCookie.getAndIncrement();
    }

    public static void beginAsyncTrace(String str, int i) {
        if (!mAnimationTraceEnabled || Build.VERSION.SDK_INT < 29) {
            return;
        }
        Trace.beginAsyncSection(str, i);
    }

    public static void endAsyncTrace(String str, int i) {
        if (!mAnimationTraceEnabled || Build.VERSION.SDK_INT < 29) {
            return;
        }
        Trace.endAsyncSection(str, i);
    }
}
