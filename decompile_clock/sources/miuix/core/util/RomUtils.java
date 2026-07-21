package miuix.core.util;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import java.lang.reflect.InvocationTargetException;
import miuix.os.Build;

/* JADX INFO: loaded from: classes2.dex */
public class RomUtils {
    private static int HYPER_OS_VERSION_CODE = -1;
    private static Boolean IS_ANDROID_NATIVE_ROM = null;
    private static Boolean IS_GOOGLE_ANDROID_ROM = null;
    private static int MIUI_VERSION_CODE = -1;
    private static final String PROP_EMUI_VERSION_CODE = "ro.build.version.emui";
    private static final String PROP_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String PROP_OPPO_VERSION_CODE = "ro.build.version.opporom";
    private static final String PROP_OS_VERSION_CODE = "ro.mi.os.version.code";
    private static final String PROP_SMARTISAN_VERSION_CODE = "ro.smartisan.version";
    private static final String PROP_VIVO_VERSION_CODE = "ro.vivo.os.version";

    private RomUtils() {
    }

    public static boolean isInternationalBuild() {
        return Build.IS_INTERNATIONAL_BUILD;
    }

    public static boolean isXiaomiSystem() {
        return (TextUtils.isEmpty(SystemProperties.get(PROP_OS_VERSION_CODE, "")) && TextUtils.isEmpty(SystemProperties.get(PROP_MIUI_VERSION_CODE, ""))) ? false : true;
    }

    public static boolean isGoogleSystem() {
        if (IS_GOOGLE_ANDROID_ROM == null) {
            IS_GOOGLE_ANDROID_ROM = Boolean.valueOf("Google".equals(android.os.Build.MANUFACTURER));
        }
        return IS_GOOGLE_ANDROID_ROM.booleanValue();
    }

    public static int getHyperOsVersion() {
        if (HYPER_OS_VERSION_CODE == -1) {
            HYPER_OS_VERSION_CODE = getHyperOsVersionNoCache();
        }
        return HYPER_OS_VERSION_CODE;
    }

    public static int getHyperOsVersionNoCache() {
        return SystemProperties.getInt(PROP_OS_VERSION_CODE, 0);
    }

    public static boolean isHyperOsRom() {
        return getHyperOsVersion() > 0;
    }

    public static int getMiuiVersion() {
        if (MIUI_VERSION_CODE == -1) {
            MIUI_VERSION_CODE = getMiuiVersionNoCache();
        }
        return MIUI_VERSION_CODE;
    }

    public static int getMiuiVersionNoCache() {
        return SystemProperties.getInt(PROP_MIUI_VERSION_CODE, 0);
    }

    public static boolean isMiuiXSdkSupported() {
        return getMiuiVersion() >= 8;
    }

    public static boolean isMiuiXISdkSupported() {
        return getMiuiVersion() >= 9;
    }

    public static boolean isMiuiXIISdkSupported() {
        return getMiuiVersion() >= 10;
    }

    public static boolean isMiuiXIIV2SdkSupported() {
        return getMiuiVersion() >= 11;
    }

    public static boolean isMiuiXIIISdkSupported() {
        return getMiuiVersion() >= 13;
    }

    public static boolean isMiuiXIVSdkSupported() {
        return getMiuiVersion() >= 14;
    }

    public static boolean isMiuiXVSdkSupported() {
        return getMiuiVersion() >= 15;
    }

    public static boolean isMiuiRom() {
        return getMiuiVersion() > 0;
    }

    public static boolean isMiproFontSupported() {
        if (!isInternationalBuild() || getMiuiVersion() >= 14) {
            return isMiuiXISdkSupported();
        }
        return false;
    }

    public static boolean isAndroidNative(Context context) {
        if (IS_ANDROID_NATIVE_ROM == null) {
            try {
                IS_ANDROID_NATIVE_ROM = Boolean.valueOf(((Boolean) Class.forName("android.app.ActivityManager").getDeclaredMethod("isNativeAndroidStatic", new Class[0]).invoke((ActivityManager) context.getApplicationContext().getSystemService("activity"), new Object[0])).booleanValue());
            } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                IS_ANDROID_NATIVE_ROM = false;
                e.printStackTrace();
            }
        }
        return IS_ANDROID_NATIVE_ROM.booleanValue();
    }
}
