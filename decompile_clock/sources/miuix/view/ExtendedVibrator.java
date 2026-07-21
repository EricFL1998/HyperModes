package miuix.view;

import android.util.Log;
import android.view.View;
import miuix.HapticLog;

/* JADX INFO: loaded from: classes3.dex */
class ExtendedVibrator implements HapticFeedbackProvider {
    private static final String TAG = "ExtendedVibrator";

    private ExtendedVibrator() {
    }

    @Override // miuix.view.HapticFeedbackProvider
    public boolean performHapticFeedback(View view, int i) {
        if (i != HapticFeedbackConstants.MIUI_VIRTUAL_RELEASE) {
            return false;
        }
        HapticLog.printTrace("performHapticFeedback: " + i);
        return view.performHapticFeedback(2);
    }

    static {
        initialize();
    }

    private static void initialize() {
        if (PlatformConstants.VERSION < 0) {
            Log.w(TAG, "MiuiHapticFeedbackConstants not found.");
        } else {
            HapticCompat.registerProvider(new ExtendedVibrator());
            Log.i(TAG, "setup ExtendedVibrator success.");
        }
    }
}
