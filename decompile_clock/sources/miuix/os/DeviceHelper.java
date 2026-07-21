package miuix.os;

import android.content.Context;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import miuix.core.util.EnvStateManager;
import miuix.core.util.SystemProperties;
import miuix.core.util.WindowUtils;

/* JADX INFO: loaded from: classes3.dex */
public class DeviceHelper {
    public static final int SUB_BUILTIN_DISPLAY = SystemProperties.getInt("persist.sys.secondary_builtin_display_id", -1);
    private static final String TAG = "Miuix.DeviceHelper";

    public static int detectType(Context context) {
        if (Build.IS_FOLD_INSIDE) {
            return 3;
        }
        if (Build.IS_FLIP) {
            return 4;
        }
        if (Build.IS_FOLD_OUTSIDE) {
            return 5;
        }
        if (Build.IS_TABLET) {
            return 2;
        }
        if (Build.IS_AUTOMOTIVE) {
            return 11;
        }
        return Build.IS_WEAR ? 10 : 1;
    }

    public static boolean isFoldable() {
        return Build.IS_FOLDABLE;
    }

    public static boolean isTablet() {
        return Build.IS_TABLET;
    }

    public static boolean hasIndependentRearDisplay() {
        return Build.IS_INDEPENDENT_REAR;
    }

    public static boolean isInRearDisplay(Context context) {
        if (context == null || !hasIndependentRearDisplay() || android.os.Build.VERSION.SDK_INT < 30) {
            return false;
        }
        try {
            return context.getDisplay().getDisplayId() == SUB_BUILTIN_DISPLAY;
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean isInRearDisplay(Display display) {
        return display != null && hasIndependentRearDisplay() && display.getDisplayId() == SUB_BUILTIN_DISPLAY;
    }

    public static boolean isCarWithScreen(Context context, Display display) {
        if (display == null) {
            if (android.os.Build.VERSION.SDK_INT >= 30) {
                try {
                    display = context.getDisplay();
                } catch (Exception unused) {
                }
            }
            if (display == null) {
                try {
                    display = ((DisplayManager) context.getSystemService("display")).getDisplay(0);
                } catch (Exception unused2) {
                }
            }
        }
        if (display == null || display.getName() == null) {
            return false;
        }
        return display.getName().toLowerCase().contains("carlink") || display.getName().toLowerCase().contains("ucar");
    }

    public static boolean isXiaomiSynergy(Context context) {
        try {
            return Settings.Secure.getInt(context.getContentResolver(), "synergy_mode", 0) == 1;
        } catch (Exception e) {
            Log.w(TAG, "isXiaomiSynergy warning!! context cannot get synergy_mode: " + e);
            return false;
        }
    }

    public static boolean isWideScreen(Context context) {
        return ((float) EnvStateManager.getScreenShortEdge(context)) > context.getResources().getDisplayMetrics().density * 600.0f;
    }

    public static boolean isTinyScreen(Context context) {
        int screenType = WindowUtils.getScreenType(context.getResources().getConfiguration());
        if (detectType(context) == 4) {
            return screenType == 1;
        }
        Point screenSize = WindowUtils.getScreenSize(context);
        return ((int) (((float) Math.max(screenSize.x, screenSize.y)) / context.getResources().getDisplayMetrics().density)) <= 640;
    }

    @Deprecated
    public static boolean isExternalScreen(Context context) {
        if (Build.IS_FLIP) {
            return isTinyScreen(context);
        }
        return Build.IS_FOLDABLE && !isWideScreen(context);
    }

    @Deprecated
    public static boolean isInternalScreen(Context context) {
        if (Build.IS_FLIP) {
            return !isTinyScreen(context);
        }
        return Build.IS_FOLDABLE && isWideScreen(context);
    }
}
