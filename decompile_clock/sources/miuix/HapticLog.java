package miuix;

import android.util.Log;
import miuix.core.util.SystemProperties;

/* JADX INFO: loaded from: classes2.dex */
public class HapticLog {
    private static final String TAG = "MiuixHaptic";
    private static final boolean sLogHapticEnabled = SystemProperties.getBoolean("log.miuix.haptic.enabled", false);

    private HapticLog() {
        throw new IllegalStateException("Cannot create instance of utility class.");
    }

    public static void printTrace(String str) {
        if (sLogHapticEnabled) {
            Log.d(TAG, str, new Throwable());
        }
    }
}
