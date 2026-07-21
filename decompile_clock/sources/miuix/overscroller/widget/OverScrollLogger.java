package miuix.overscroller.widget;

import android.util.Log;
import java.util.Locale;

/* JADX INFO: loaded from: classes3.dex */
class OverScrollLogger {
    private static final String TAG = "OverScroll";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final boolean VERBOSE = Log.isLoggable(TAG, 2);

    OverScrollLogger() {
    }

    public static void debug(String str) {
        if (DEBUG) {
            Log.d(TAG, str);
        }
    }

    public static void debug(String str, Object... objArr) {
        if (DEBUG) {
            Log.d(TAG, String.format(Locale.US, str, objArr));
        }
    }

    public static void verbose(String str) {
        if (VERBOSE) {
            Log.v(TAG, str);
        }
    }

    public static void verbose(String str, Object... objArr) {
        if (VERBOSE) {
            Log.v(TAG, String.format(Locale.US, str, objArr));
        }
    }
}
