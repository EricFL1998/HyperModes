package com.android.deskclock.addition;

import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import miui.os.Build;
import miuix.device.DeviceUtils;

/* JADX INFO: loaded from: classes.dex */
public class MiuiSdk {
    private static final boolean CAN_DELETE_FOR_LITE_MODE;
    private static final boolean IS_LITE_MODE;
    private static final boolean IS_LITE_V1_STOCK_MODE;
    private static final boolean IS_MIUI_MIDDLE;
    private static final boolean IS_SUPER_LITE_MODE;
    private static final boolean IS_SUPPORT_SLEEP;
    private static final int MIUI15 = 150;
    private static String TAG = "DC:MiuiSdk";

    public static boolean isSupportFolmeAnim() {
        return true;
    }

    public static boolean isSupportFontAnim() {
        return false;
    }

    static {
        boolean zIsMiuiLiteRom = DeviceUtils.isMiuiLiteRom();
        IS_LITE_MODE = zIsMiuiLiteRom;
        boolean z = true;
        CAN_DELETE_FOR_LITE_MODE = zIsMiuiLiteRom && !(DeviceUtils.isLiteV1Stock() && DeviceUtils.isLiteV1StockPlus());
        IS_SUPPORT_SLEEP = Util.isApplicationInMainSpace();
        if (!DeviceUtils.isMiuiLiteV2() && !Util.isDeviceYunluo()) {
            z = false;
        }
        IS_SUPER_LITE_MODE = z;
        boolean zIsLiteV1StockPlus = DeviceUtils.isLiteV1StockPlus();
        IS_LITE_V1_STOCK_MODE = zIsLiteV1StockPlus;
        IS_MIUI_MIDDLE = DeviceUtils.isMiuiMiddle();
        Log.d("IS_LITE_V1_STOCK_MODE: " + zIsLiteV1StockPlus + "  IS_LITE_MODE: " + zIsMiuiLiteRom + "  IS_SUPER_LITE_MODE: " + z);
    }

    public static boolean isMiui15() {
        Log.i(TAG, "getMiuiVersion():" + getMiuiVersion());
        return getMiuiVersion() >= 150;
    }

    public static int getMiuiVersion() throws Throwable {
        String systemProperty = getSystemProperty("ro.miui.ui.version.name");
        if (systemProperty == null) {
            return -1;
        }
        try {
            if ("V12.5".equals(systemProperty)) {
                return 12;
            }
            return Integer.parseInt(systemProperty.substring(1));
        } catch (Exception unused) {
            Log.e(TAG, "get miui version code error, version : " + systemProperty);
            return -1;
        }
    }

    /* JADX WARN: Code duplicated, block: B:36:0x0067 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Not initialized variable reg: 4, insn: 0x0064: MOVE (r3 I:??[OBJECT, ARRAY]) = (r4 I:??[OBJECT, ARRAY]), block:B:24:0x0064 */
    public static String getSystemProperty(String str) throws Throwable {
        BufferedReader bufferedReader;
        BufferedReader bufferedReader2;
        BufferedReader bufferedReader3 = null;
        try {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("getprop " + str).getInputStream()), 1024);
                try {
                    String line = bufferedReader.readLine();
                    bufferedReader.close();
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Exception while closing InputStream", e);
                    }
                    return line;
                } catch (IOException e2) {
                    e = e2;
                    Log.e(TAG, "Unable to read sysprop " + str, e);
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e3) {
                            Log.e(TAG, "Exception while closing InputStream", e3);
                        }
                    }
                    return null;
                }
            } catch (IOException e4) {
                e = e4;
                bufferedReader = null;
            } catch (Throwable th) {
                th = th;
                if (bufferedReader3 != null) {
                    try {
                        bufferedReader3.close();
                    } catch (IOException e5) {
                        Log.e(TAG, "Exception while closing InputStream", e5);
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            bufferedReader3 = bufferedReader2;
            if (bufferedReader3 != null) {
                bufferedReader3.close();
            }
            throw th;
        }
    }

    public static boolean isSupportMiUiFont() {
        return !Build.IS_INTERNATIONAL_BUILD;
    }

    public static boolean isSupportSleep() {
        String str = TAG;
        StringBuilder sb = new StringBuilder("isSupportSleep: ");
        boolean z = IS_SUPPORT_SLEEP;
        Log.d(str, sb.append(z).toString());
        return z;
    }

    public static boolean isLiteOrMiddleMode() {
        return IS_LITE_MODE || isMiuiMiddle();
    }

    public static boolean isLiteMode() {
        return IS_LITE_MODE;
    }

    public static boolean canDeleteForLiteMode() {
        return CAN_DELETE_FOR_LITE_MODE;
    }

    public static boolean canDeleteForLiteOrMiuiMiddleMode() {
        return CAN_DELETE_FOR_LITE_MODE || isMiuiMiddle();
    }

    public static boolean isSuperLiteMode() {
        return IS_SUPER_LITE_MODE;
    }

    public static boolean isLiteV1StockMode() {
        return IS_LITE_V1_STOCK_MODE;
    }

    public static boolean isMiuiMiddle() {
        return IS_MIUI_MIDDLE;
    }
}
