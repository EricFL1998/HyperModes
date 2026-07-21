package miuix.mimotion;

import android.util.Log;
import com.xiaomi.mimotion.MimotionUtils;
import miuix.core.util.SystemProperties;
import miuix.internal.util.ReflectUtil;

/* JADX INFO: loaded from: classes2.dex */
public class MiMotionHelper {
    private static boolean DEBUG = false;
    private static final boolean SUPPORT_MI_MOTION;
    public static final String SYSTEM_PROPERTY_MIMOTION_DEBUG = "persist.mimotion.debug";
    private static final String TAG = "MiMotionHelper";
    private static MiMotionHelper sInstance;
    private static boolean sIsMimotionUtilsAvailable;

    static {
        boolean z = Boolean.parseBoolean(SystemProperties.get("ro.display.mimotion", "false"));
        SUPPORT_MI_MOTION = z;
        if (z) {
            DEBUG = Boolean.parseBoolean(SystemProperties.get(SYSTEM_PROPERTY_MIMOTION_DEBUG, "false"));
            checkMimotionUtilsAvailable();
        }
    }

    private static void checkMimotionUtilsAvailable() {
        if (ReflectUtil.getClass("com.xiaomi.mimotion.MimotionUtils") == null) {
            return;
        }
        sIsMimotionUtilsAvailable = true;
    }

    private MiMotionHelper() {
    }

    public static boolean isSupportMiMotion() {
        return SUPPORT_MI_MOTION;
    }

    public static MiMotionHelper getInstance() {
        if (sInstance == null) {
            sInstance = new MiMotionHelper();
        }
        return sInstance;
    }

    public boolean isEnabled() {
        if (!sIsMimotionUtilsAvailable) {
            return false;
        }
        boolean zIsEnabled = MimotionUtils.isEnabled();
        if (DEBUG) {
            Log.i(TAG, "MimotionHelper isEnabled: " + zIsEnabled);
        }
        return zIsEnabled;
    }

    public static void setEnable(boolean z) {
        if (sIsMimotionUtilsAvailable) {
            MimotionUtils.setEnable(z);
        }
    }

    public boolean setPreferredRefreshRate(Object obj, int i) {
        if (!sIsMimotionUtilsAvailable) {
            return false;
        }
        if (DEBUG) {
            Log.i(TAG, "setPreferredRefreshRate: " + i);
        }
        return MimotionUtils.setPreferredRefreshRate(obj, i);
    }
}
