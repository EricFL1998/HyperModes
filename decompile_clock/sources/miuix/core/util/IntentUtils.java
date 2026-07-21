package miuix.core.util;

import android.content.Intent;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import miuix.reflect.ReflectionHelper;

/* JADX INFO: loaded from: classes2.dex */
public class IntentUtils {
    public static boolean isIntentFromSettingsSplit(Intent intent) {
        return (getMiuiFlags(intent) & 16) != 0;
    }

    public static int getMiuiFlags(Intent intent) {
        if (intent == null) {
            return 0;
        }
        try {
            return ((Integer) ReflectionHelper.invokeObject(Intent.class, intent, "getMiuiFlags", new Class[0], new Object[0])).intValue();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            Log.e("IntentUtils", " getMiuiFlags error: " + e);
            return 0;
        }
    }
}
