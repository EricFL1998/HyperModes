package androidx.core.provider;

import android.os.Handler;
import android.os.Looper;

/* JADX INFO: loaded from: classes.dex */
class CalleeHandler {
    private CalleeHandler() {
    }

    static Handler create() {
        if (Looper.myLooper() == null) {
            return new Handler(Looper.getMainLooper());
        }
        return new Handler();
    }
}
