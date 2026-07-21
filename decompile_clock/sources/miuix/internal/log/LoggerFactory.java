package miuix.internal.log;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import miuix.internal.log.appender.FileAppender;
import miuix.internal.log.appender.LogcatAppender;
import miuix.internal.log.appender.rolling.FileRolloverStrategy;
import miuix.internal.log.appender.rolling.RollingFileManager;
import miuix.internal.log.format.SimpleFormatter;
import miuix.internal.log.util.Config;
import miuix.os.Build;

/* JADX INFO: loaded from: classes2.dex */
public class LoggerFactory {
    private static final int MAX_BACK_UP = 20;
    private static final String MAX_BACK_UP_KEY = "maxBackup";
    private static final int MAX_FILE_MB_SIZE = 10;
    private static final String MAX_FILE_MB_SIZE_KEY = "maxFileMbSize";
    private static final String TAG = "LoggerFactory";

    public static Logger getLogcatLogger() {
        Logger logger = new Logger(Config.LOG_NAME);
        logger.addAppender(new LogcatAppender());
        if (Build.IS_DEBUGGABLE) {
            logger.setLevel(Level.VERBOSE);
        } else {
            logger.setLevel(Level.INFO);
        }
        return logger;
    }

    public static Logger getFileLogger(Context context) {
        return getFileLogger(context, Config.getDefaultCacheLogDir(context), Config.LOG_NAME);
    }

    public static Logger getExternalFileLogger(Context context) {
        return getFileLogger(context, Config.getDefaultExternalLogDir(context), Config.LOG_NAME);
    }

    public static Logger getFileLogger(Context context, String str, String str2) {
        Bundle bundle;
        Logger logger = new Logger(str2);
        FileAppender fileAppender = new FileAppender();
        fileAppender.setFormatter(new SimpleFormatter());
        FileRolloverStrategy fileRolloverStrategy = new FileRolloverStrategy();
        try {
            bundle = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
            bundle = null;
        }
        fileRolloverStrategy.setMaxBackupIndex(obtainMaxBackup(bundle) - 1);
        fileRolloverStrategy.setMaxFileSize(obtainMaxFileSize(bundle));
        RollingFileManager rollingFileManager = new RollingFileManager(str, str2);
        rollingFileManager.setRolloverStrategy(fileRolloverStrategy);
        fileAppender.setFileManager(rollingFileManager);
        logger.addAppender(fileAppender);
        if (Build.IS_DEBUGGABLE) {
            logger.setLevel(Level.VERBOSE);
        } else {
            logger.setLevel(Level.INFO);
        }
        return logger;
    }

    private static int obtainMaxFileSize(Bundle bundle) {
        if (bundle == null || !bundle.containsKey(MAX_FILE_MB_SIZE_KEY)) {
            return 1048576;
        }
        Object obj = bundle.get(MAX_FILE_MB_SIZE_KEY);
        if (obj instanceof Integer) {
            Integer num = (Integer) obj;
            if (num.intValue() <= 10) {
                return 1048576 * num.intValue();
            }
        }
        Log.e(TAG, "Log config error:maxFileMbSize must be int type and smaller than 10");
        return 1048576;
    }

    private static int obtainMaxBackup(Bundle bundle) {
        if (bundle != null && bundle.containsKey(MAX_BACK_UP_KEY)) {
            Object obj = bundle.get(MAX_BACK_UP_KEY);
            if (obj instanceof Integer) {
                Integer num = (Integer) obj;
                if (num.intValue() < 20) {
                    return num.intValue();
                }
            }
            Log.e(TAG, "Log config error:maxBackup must be int type and smaller than 20");
        }
        return 4;
    }
}
