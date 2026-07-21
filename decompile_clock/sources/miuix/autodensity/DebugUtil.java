package miuix.autodensity;

import android.text.TextUtils;
import android.util.Log;
import com.android.deskclock.worldclock.WorldClockEditActivity;
import miuix.core.util.SystemProperties;

/* JADX INFO: loaded from: classes2.dex */
public class DebugUtil {
    static final String TAG = "AutoDensity";
    private static String sAutoDensityDebug;
    private static volatile float sDebugAutoDensityScale;
    private static int sDebugLogLevel;
    public static boolean sForceEnableDebug;

    public static void initAutoDensityDebugEnable() {
        String str;
        try {
            str = SystemProperties.get("log.tag.autodensity.debug.enable");
            sAutoDensityDebug = str;
            if (str == null) {
                str = WorldClockEditActivity.LOCAL_CITY_ID;
            }
        } catch (Exception e) {
            Log.i(TAG, "can not access property log.tag.autodensity.enable, undebugable", e);
            str = "";
        }
        Log.d(TAG, "autodensity debugEnable = " + str);
        try {
            sDebugAutoDensityScale = Float.parseFloat(str);
        } catch (NumberFormatException unused) {
            sDebugAutoDensityScale = 0.0f;
        }
        try {
            sDebugLogLevel = Integer.parseInt(SystemProperties.get("persist.miuix.dpi.log"));
        } catch (Exception unused2) {
            sDebugLogLevel = 0;
        }
    }

    public static boolean isDisableAutoDensityInDebugMode() {
        return sDebugAutoDensityScale < 0.0f;
    }

    public static float getAutoDensityScaleInDebugMode() {
        return sDebugAutoDensityScale;
    }

    public static boolean isEnableDebug() {
        return sForceEnableDebug || sDebugLogLevel > 0 || (sDebugAutoDensityScale >= 0.0f && !TextUtils.isEmpty(sAutoDensityDebug));
    }

    public static void printDensityLog(String str) {
        if (isEnableDebug()) {
            Log.d(TAG, str);
        }
    }
}
