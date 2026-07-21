package androidx.core.app;

import android.os.Bundle;
import android.os.IBinder;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class BundleCompat {
    private BundleCompat() {
    }

    public static IBinder getBinder(Bundle bundle, String str) {
        return bundle.getBinder(str);
    }

    public static void putBinder(Bundle bundle, String str, IBinder iBinder) {
        bundle.putBinder(str, iBinder);
    }
}
