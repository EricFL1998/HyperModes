package com.android.deskclock.util;

import android.app.Activity;
import com.android.deskclock.addition.MiuiSdk;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class FastStartUtil {
    public static void notifyTakeSnapshotQs(Activity activity) {
        if (MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode()) {
            try {
                Method declaredMethod = Activity.class.getDeclaredMethod("notifyTakeSnapshotQs", new Class[0]);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(activity, new Object[0]);
            } catch (Exception e) {
                Log.d(Log.TAG, "error in notifyTakeSnapshotQs " + e);
            }
        }
    }

    public static void notifyRemoveSnapshotQs(Activity activity) {
        if (MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode()) {
            try {
                Method declaredMethod = Activity.class.getDeclaredMethod("notifyRemoveSnapshotQs", new Class[0]);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(activity, new Object[0]);
            } catch (Exception e) {
                Log.d(Log.TAG, "error in notifyRemoveSnapshotQs " + e);
            }
        }
    }
}
