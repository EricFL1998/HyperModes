package miuix.autodensity;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import miuix.core.util.EnvStateManager;
import miuix.core.util.SystemProperties;
import miuix.os.Build;
import miuix.os.DeviceHelper;

/* JADX INFO: loaded from: classes2.dex */
public class SkuScale {
    private static final String PRIMARY_PPI;
    private static final String PRIMARY_SCALE;
    private static final String SECONDARY_PPI;
    private static final String SECONDARY_SCALE;
    private static int sPrimaryPPIValue;
    private static float sPrimaryScaleValue;
    private static int sSecondaryPPIValue;
    private static float sSecondaryScaleValue;

    static {
        String str = SystemProperties.get("ro.miui.density.primaryscale", null);
        PRIMARY_SCALE = str;
        String str2 = SystemProperties.get("ro.miui.density.secondaryscale", null);
        SECONDARY_SCALE = str2;
        sPrimaryScaleValue = 0.0f;
        sSecondaryScaleValue = 0.0f;
        String str3 = SystemProperties.get("ro.miui.density.primaryppi", null);
        PRIMARY_PPI = str3;
        String str4 = SystemProperties.get("ro.miui.density.secondaryppi", null);
        SECONDARY_PPI = str4;
        sPrimaryPPIValue = 0;
        sSecondaryPPIValue = 0;
        if (!TextUtils.isEmpty(str)) {
            sPrimaryScaleValue = parseSkuScale(str);
        }
        if (!TextUtils.isEmpty(str2)) {
            sSecondaryScaleValue = parseSkuScale(str2);
        }
        if (!TextUtils.isEmpty(str3)) {
            sPrimaryPPIValue = parseSkuPPI(str3);
        }
        if (!TextUtils.isEmpty(str4)) {
            sSecondaryPPIValue = parseSkuPPI(str4);
        }
        if (sSecondaryScaleValue == 0.0f) {
            sSecondaryScaleValue = sPrimaryScaleValue;
        }
    }

    private static float parseSkuScale(String str) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            Log.w("AutoDensity", "catch error: sku scale is not a number", e);
            return 0.0f;
        }
    }

    private static int parseSkuPPI(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            Log.w("AutoDensity", "catch error: sku scale is not a number", e);
            return 0;
        }
    }

    static boolean hasSkuScale() {
        return (sPrimaryScaleValue == 0.0f && sSecondaryScaleValue == 0.0f) ? false : true;
    }

    static boolean hasSkuPPI() {
        return (sPrimaryPPIValue == 0 && sSecondaryPPIValue == 0) ? false : true;
    }

    static float getSkuScale(Context context) {
        float f = sPrimaryScaleValue;
        if ((Build.IS_FOLD_INSIDE || Build.IS_FOLD_OUTSIDE) && EnvStateManager.getScreenShortEdge(context) > 640) {
            f = sSecondaryScaleValue;
        }
        return (Build.IS_FLIP && DeviceHelper.isTinyScreen(context)) ? sSecondaryScaleValue : f;
    }

    static int getSkuPPI(Context context, boolean z) {
        if (z) {
            return sPrimaryPPIValue;
        }
        return sSecondaryPPIValue;
    }
}
