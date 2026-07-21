package miuix.autodensity;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

/* JADX INFO: loaded from: classes2.dex */
public class AutoDensityPolicy {
    public static final float ACCESSIBILITY_ZOOM_BIG = 1.05f;
    public static final float ACCESSIBILITY_ZOOM_SMALL;
    public static final float STANDARD_DPI = 440.0f;
    public static final float STANDARD_PPI = 386.0f;
    public static final float STANDARD_SCALE = 1.1398964f;

    public static float calcPhoneRearScale(float f) {
        return 0.94f;
    }

    static {
        ACCESSIBILITY_ZOOM_SMALL = TextUtils.equals(Build.DEVICE, "zizhan") ? 0.85f : 0.8f;
    }

    public static float calcPhoneScale(float f) {
        return Math.min(1.0f, f / 2.8f);
    }

    public static float calcPadScale(float f) {
        return Math.max(1.0f, Math.min((f / 9.3f) * 1.06f, 1.15f));
    }

    /* JADX WARN: Code duplicated, block: B:10:0x001c  */
    /* JADX WARN: Code duplicated, block: B:23:0x0043  */
    public static double getDeviceScale(Context context, float f, float f2, boolean z) {
        float fCalcPhoneScale;
        double d;
        if (SkuScale.hasSkuScale()) {
            fCalcPhoneScale = SkuScale.getSkuScale(context);
        } else {
            if (miuix.os.Build.IS_FOLDABLE) {
                if ("cetus".contentEquals(Build.DEVICE)) {
                    d = 1.0d;
                } else {
                    fCalcPhoneScale = calcPhoneScale(f2);
                }
            } else if (miuix.os.Build.IS_TABLET) {
                fCalcPhoneScale = calcPadScale(f);
            } else if (miuix.os.Build.IS_AUTOMOTIVE) {
                d = 1.0d;
            } else if (z) {
                fCalcPhoneScale = calcPhoneRearScale(f);
            } else {
                fCalcPhoneScale = calcPhoneScale(f2);
            }
            if (DebugUtil.isEnableDebug()) {
                DebugUtil.printDensityLog("getDeviceScale " + d);
            }
            return d;
        }
        d = fCalcPhoneScale;
        if (DebugUtil.isEnableDebug()) {
            DebugUtil.printDensityLog("getDeviceScale " + d);
        }
        return d;
    }
}
