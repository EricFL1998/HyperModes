package com.android.deskclock.util.log;

import com.android.deskclock.DeskClockApp;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.FileUtil;
import com.android.deskclock.util.log.appender.AsyncFileAppender;
import com.android.deskclock.util.log.appender.LogcatAppender;
import com.android.deskclock.util.log.appender.rolling.FileRolloverStrategy;
import com.android.deskclock.util.log.appender.rolling.RollingFileManager;
import com.android.deskclock.util.log.format.SimpleFormatter;
import java.io.File;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class ExLogger {
    private static final String DIRECTORY_LOCKED = "locked";
    private static final String DUMP_DIRECTORY = "dump";
    private static final String LOG_NAME = "android.android.deskclock";
    private static final String LOG_NAME_LOCKED = "android.android.deskclock.locked";
    private static final String TAG = "DC::ExLogger";
    private static final ExLogger instance = new ExLogger();
    private AsyncFileAppender mCEStorageAppender;
    private AsyncFileAppender mDEStorageAppender;
    private Logger mLogger;

    public static ExLogger getInstance() {
        return instance;
    }

    private ExLogger() {
        Logger logger = new Logger(TAG);
        logger.addAppender(new LogcatAppender());
        if (Build.IS_DEBUGGABLE) {
            logger.setLevel(Level.VERBOSE);
        } else {
            logger.setLevel(Level.INFO);
        }
        this.mLogger = logger;
    }

    public void addDEStorageLog() {
        File filesDir;
        if (this.mDEStorageAppender == null && (filesDir = DeskClockApp.getAppDEContext().getFilesDir()) != null) {
            AsyncFileAppender asyncFileAppender = new AsyncFileAppender();
            this.mDEStorageAppender = asyncFileAppender;
            asyncFileAppender.setFormatter(new SimpleFormatter());
            FileRolloverStrategy fileRolloverStrategy = new FileRolloverStrategy();
            fileRolloverStrategy.setMaxBackupIndex(1);
            fileRolloverStrategy.setMaxFileSize(1048576);
            RollingFileManager rollingFileManager = new RollingFileManager(filesDir.getAbsolutePath() + File.separator + DUMP_DIRECTORY, LOG_NAME_LOCKED);
            rollingFileManager.setRolloverStrategy(fileRolloverStrategy);
            this.mDEStorageAppender.setFileManager(rollingFileManager);
            this.mLogger.addAppender(this.mDEStorageAppender);
        }
    }

    public void addCEStorageLog() {
        File externalFilesDir;
        AsyncFileAppender asyncFileAppender = this.mDEStorageAppender;
        if (asyncFileAppender != null) {
            this.mLogger.removeAppender(asyncFileAppender);
            this.mDEStorageAppender = null;
        }
        if (this.mCEStorageAppender == null && (externalFilesDir = DeskClockApp.getAppDEContext().getExternalFilesDir(null)) != null) {
            AsyncFileAppender asyncFileAppender2 = new AsyncFileAppender();
            this.mCEStorageAppender = asyncFileAppender2;
            asyncFileAppender2.setFormatter(new SimpleFormatter());
            FileRolloverStrategy fileRolloverStrategy = new FileRolloverStrategy();
            fileRolloverStrategy.setMaxBackupIndex(4);
            fileRolloverStrategy.setMaxFileSize(1048576);
            RollingFileManager rollingFileManager = new RollingFileManager(externalFilesDir.getAbsolutePath() + File.separator + DUMP_DIRECTORY, LOG_NAME);
            rollingFileManager.setRolloverStrategy(fileRolloverStrategy);
            this.mCEStorageAppender.setFileManager(rollingFileManager);
            this.mLogger.addAppender(this.mCEStorageAppender);
        }
    }

    public void copyCEStorageLogToCE() {
        File externalFilesDir = DeskClockApp.getAppDEContext().getExternalFilesDir(null);
        if (externalFilesDir != null) {
            final String str = (externalFilesDir.getAbsolutePath() + File.separator + DUMP_DIRECTORY) + File.separator + DIRECTORY_LOCKED;
            AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.util.log.ExLogger.1
                @Override // java.lang.Runnable
                public void run() {
                    FileUtil.copyDirectory(DeskClockApp.getAppDEContext().getFilesDir().getAbsolutePath() + File.separator + ExLogger.DUMP_DIRECTORY, str);
                }
            });
        }
    }

    public void i(String str, String str2) {
        doLog(Level.INFO, str, str2);
    }

    public void d(String str, String str2) {
        doLog(Level.DEBUG, str, str2);
    }

    public void e(String str, String str2) {
        doLog(Level.ERROR, str, str2);
    }

    public void e(String str, String str2, Throwable th) {
        doLog(Level.ERROR, str, str2, th);
    }

    private void doLog(Level level, String str, String str2) {
        this.mLogger.log(level, str, str2);
    }

    private void doLog(Level level, String str, String str2, Throwable th) {
        this.mLogger.log(level, str, str2, th);
    }
}
