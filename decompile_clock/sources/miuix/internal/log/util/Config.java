package miuix.internal.log.util;

import android.content.Context;
import android.os.Process;
import android.util.Log;
import java.io.File;
import miuix.os.Environment;
import miuix.os.ProcessUtils;

/* JADX INFO: loaded from: classes2.dex */
public class Config {
    private static final String DEBUG_LOG_DIR = "/debug_log";
    private static final String DUMP_SUB_DIR = "/dump";
    public static String LOG_NAME = ProcessUtils.getProcessNameByPid(Process.myPid());
    private static final String RELATIVE_LOG_DIR = "/debug_log/";
    private static final String TAG = "Config";

    public static String getDefaultCacheLogDir(Context context) {
        return getApplicationCacheLogDir(context);
    }

    public static String getDefaultExternalLogDir(Context context) {
        File externalCacheDir = context.getExternalCacheDir();
        if (externalCacheDir != null) {
            return externalCacheDir.getPath() + DEBUG_LOG_DIR;
        }
        Log.e(TAG, "Fail to getExternalCacheDir");
        return null;
    }

    public static String getDefaultSdcardLogDir(Context context) {
        String packageName = context.getPackageName();
        File externalStorageMiuiDirectory = Environment.getExternalStorageMiuiDirectory();
        if (externalStorageMiuiDirectory != null) {
            return externalStorageMiuiDirectory.getPath() + RELATIVE_LOG_DIR + packageName + DUMP_SUB_DIR;
        }
        Log.e(TAG, "Fail to getExternalStorageMiuiDirectory");
        return null;
    }

    private static String getApplicationCacheLogDir(Context context) {
        File cacheDir = context.getCacheDir();
        if (cacheDir != null) {
            return cacheDir.getPath() + RELATIVE_LOG_DIR;
        }
        Log.e(TAG, "Fail to getCacheDir");
        return null;
    }
}
