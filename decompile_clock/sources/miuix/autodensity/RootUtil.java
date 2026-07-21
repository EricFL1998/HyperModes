package miuix.autodensity;

import android.os.Build;
import java.io.File;

/* JADX INFO: loaded from: classes2.dex */
public class RootUtil {
    private static boolean sDeviceRooted = checkDeviceRooted();

    public static boolean isDeviceRooted() {
        return sDeviceRooted;
    }

    private static boolean checkDeviceRooted() {
        String str = Build.TAGS;
        int i = 0;
        boolean z = true;
        boolean z2 = str != null && str.contains("test-keys");
        if (!z2) {
            String[] strArr = {"/system/bin/su", "/system/xbin/su"};
            while (true) {
                if (i >= 2) {
                    z = z2;
                    break;
                }
                if (new File(strArr[i]).exists()) {
                    break;
                }
                i++;
            }
        } else {
            z = z2;
            break;
        }
        if (z && DebugUtil.isEnableDebug()) {
            DebugUtil.printDensityLog("Current device is rooted");
        }
        return z;
    }
}
