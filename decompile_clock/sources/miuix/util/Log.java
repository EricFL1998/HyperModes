package miuix.util;

import android.content.Context;
import android.util.Patterns;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import miuix.internal.log.Level;
import miuix.internal.log.Logger;
import miuix.internal.log.LoggerFactory;
import miuix.internal.log.message.AbstractMessage;
import miuix.internal.log.message.Message;
import miuix.internal.log.message.MessageFactory;
import miuix.internal.log.message.StringFormattedMessage;

/* JADX INFO: loaded from: classes3.dex */
public class Log {
    protected Log() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    public static Facade getLogcatLogger() {
        return LogcatLoggerInstance.INSTANCE;
    }

    public static Facade getFileLogger(Context context) {
        FileLoggerInstance.init(context);
        return FileLoggerInstance.instance();
    }

    public static Facade getExternalFileLogger(Context context) {
        ExternalFileLoggerInstance.init(context);
        return ExternalFileLoggerInstance.instance();
    }

    public static Facade getAsyncFileLogger(Context context) {
        return AsyncFileLoggerInstance.getInstance(context);
    }

    public static Facade getFullLogger(Context context) {
        FileLoggerInstance.init(context);
        return FullLoggerInstance.INSTANCE;
    }

    public static Facade getExternalFullLogger(Context context) {
        ExternalFileLoggerInstance.init(context);
        return ExternalFullLoggerInstance.INSTANCE;
    }

    private static class FileLoggerInstance {
        private static Facade facade;
        private static volatile FileLoggerInstance singleton;

        private FileLoggerInstance(Context context) {
            facade = new Facade(LoggerFactory.getFileLogger(context));
        }

        static void init(Context context) {
            if (singleton == null) {
                synchronized (FileLoggerInstance.class) {
                    if (singleton == null) {
                        singleton = new FileLoggerInstance(context);
                    }
                }
            }
        }

        static Facade instance() {
            return facade;
        }
    }

    private static class AsyncFileLoggerInstance {
        private static volatile Facade facade;

        private AsyncFileLoggerInstance() {
        }

        static Facade getInstance(Context context) {
            if (facade == null) {
                synchronized (AsyncFileLoggerInstance.class) {
                    if (facade == null) {
                        facade = new Facade(LoggerFactory.getFileLogger(context), true);
                    }
                }
            }
            return facade;
        }
    }

    private static class ExternalFileLoggerInstance {
        private static Facade facade;
        private static volatile ExternalFileLoggerInstance singleton;

        private ExternalFileLoggerInstance(Context context) {
            facade = new Facade(LoggerFactory.getExternalFileLogger(context));
        }

        static void init(Context context) {
            if (singleton == null) {
                synchronized (ExternalFileLoggerInstance.class) {
                    if (singleton == null) {
                        singleton = new ExternalFileLoggerInstance(context);
                    }
                }
            }
        }

        static Facade instance() {
            return facade;
        }
    }

    private static class LogcatLoggerInstance {
        static final Facade INSTANCE = new Facade(LoggerFactory.getLogcatLogger());

        private LogcatLoggerInstance() {
        }
    }

    private static class FullLoggerInstance {
        static final Facade INSTANCE = new FullFacade();

        private FullLoggerInstance() {
        }
    }

    private static class ExternalFullLoggerInstance {
        static final Facade INSTANCE = new ExternalFullFacade();

        private ExternalFullLoggerInstance() {
        }
    }

    public static void v(String str, String str2) {
        android.util.Log.v(str, str2);
    }

    public static void v(String str, String str2, Throwable th) {
        android.util.Log.v(str, str2, th);
    }

    public static void verbose(String str, String str2) {
        LogcatLoggerInstance.INSTANCE.verbose(str, str2);
    }

    public static void verbose(String str, String str2, Throwable th) {
        LogcatLoggerInstance.INSTANCE.verbose(str, str2, th);
    }

    public static void verbose(String str, String str2, Object... objArr) {
        LogcatLoggerInstance.INSTANCE.verbose(str, str2, objArr);
    }

    public static void d(String str, String str2) {
        android.util.Log.d(str, str2);
    }

    public static void d(String str, String str2, Throwable th) {
        android.util.Log.d(str, str2, th);
    }

    public static void debug(String str, String str2) {
        LogcatLoggerInstance.INSTANCE.debug(str, str2);
    }

    public static void debug(String str, String str2, Throwable th) {
        LogcatLoggerInstance.INSTANCE.debug(str, str2, th);
    }

    public static void debug(String str, String str2, Object... objArr) {
        LogcatLoggerInstance.INSTANCE.debug(str, str2, objArr);
    }

    public static void i(String str, String str2) {
        android.util.Log.i(str, str2);
    }

    public static void i(String str, String str2, Throwable th) {
        android.util.Log.i(str, str2, th);
    }

    public static void info(String str, String str2) {
        LogcatLoggerInstance.INSTANCE.info(str, str2);
    }

    public static void info(String str, String str2, Throwable th) {
        LogcatLoggerInstance.INSTANCE.info(str, str2, th);
    }

    public static void info(String str, String str2, Object... objArr) {
        LogcatLoggerInstance.INSTANCE.info(str, str2, objArr);
    }

    public static void w(String str, String str2) {
        android.util.Log.w(str, str2);
    }

    public static void w(String str, String str2, Throwable th) {
        android.util.Log.w(str, str2, th);
    }

    public static void warn(String str, String str2) {
        LogcatLoggerInstance.INSTANCE.warn(str, str2);
    }

    public static void warn(String str, String str2, Throwable th) {
        LogcatLoggerInstance.INSTANCE.warn(str, str2, th);
    }

    public static void warn(String str, String str2, Object... objArr) {
        LogcatLoggerInstance.INSTANCE.warn(str, str2, objArr);
    }

    public static void e(String str, String str2) {
        android.util.Log.e(str, str2);
    }

    public static void e(String str, String str2, Throwable th) {
        android.util.Log.e(str, str2, th);
    }

    public static void error(String str, String str2) {
        LogcatLoggerInstance.INSTANCE.error(str, str2);
    }

    public static void error(String str, String str2, Throwable th) {
        LogcatLoggerInstance.INSTANCE.error(str, str2, th);
    }

    public static void error(String str, String str2, Object... objArr) {
        LogcatLoggerInstance.INSTANCE.error(str, str2, objArr);
    }

    public static void wtf(String str, String str2) {
        android.util.Log.wtf(str, str2);
    }

    public static void wtf(String str, String str2, Throwable th) {
        android.util.Log.wtf(str, str2, th);
    }

    public static void fatal(String str, String str2) {
        LogcatLoggerInstance.INSTANCE.fatal(str, str2);
    }

    public static void fatal(String str, String str2, Throwable th) {
        LogcatLoggerInstance.INSTANCE.fatal(str, str2, th);
    }

    public static void fatal(String str, String str2, Object... objArr) {
        LogcatLoggerInstance.INSTANCE.fatal(str, str2, objArr);
    }

    private static String filterSensitiveMsg(String str) {
        return str != null ? Patterns.IP_ADDRESS.matcher(str).replaceAll("*.*.*.*") : str;
    }

    public static class Facade {
        private static final String TAG = "LogcatFacade";
        private Logger mLogger;
        private boolean mPrintAsync;
        private ExecutorService mSingleThreadExecutor;

        private Facade(Logger logger) {
            this(logger, false);
        }

        private Facade(Logger logger, boolean z) {
            this.mLogger = logger;
            this.mPrintAsync = z;
            if (z) {
                this.mSingleThreadExecutor = Executors.newSingleThreadExecutor();
            }
        }

        public void verbose(String str, String str2) {
            log(Level.VERBOSE, str, str2, null);
        }

        public void verbose(String str, String str2, Throwable th) {
            log(Level.VERBOSE, str, str2, th);
        }

        public void verbose(String str, String str2, Object... objArr) {
            logf(Level.VERBOSE, str, str2, objArr);
        }

        public void debug(String str, String str2) {
            log(Level.DEBUG, str, str2, null);
        }

        public void debug(String str, String str2, Throwable th) {
            log(Level.DEBUG, str, str2, th);
        }

        public void debug(String str, String str2, Object... objArr) {
            logf(Level.DEBUG, str, str2, objArr);
        }

        public void info(String str, String str2) {
            log(Level.INFO, str, str2, null);
        }

        public void info(String str, String str2, Throwable th) {
            log(Level.INFO, str, str2, th);
        }

        public void info(String str, String str2, Object... objArr) {
            logf(Level.INFO, str, str2, objArr);
        }

        public void warn(String str, String str2) {
            log(Level.WARNING, str, str2, null);
        }

        public void warn(String str, String str2, Throwable th) {
            log(Level.WARNING, str, str2, th);
        }

        public void warn(String str, String str2, Object... objArr) {
            logf(Level.WARNING, str, str2, objArr);
        }

        public void error(String str, String str2) {
            log(Level.ERROR, str, str2, null);
        }

        public void error(String str, String str2, Throwable th) {
            log(Level.ERROR, str, str2, th);
        }

        public void error(String str, String str2, Object... objArr) {
            logf(Level.ERROR, str, str2, objArr);
        }

        public void fatal(String str, String str2) {
            log(Level.FATAL, str, str2, null);
        }

        public void fatal(String str, String str2, Throwable th) {
            log(Level.FATAL, str, str2, th);
        }

        public void fatal(String str, String str2, Object... objArr) {
            logf(Level.FATAL, str, str2, objArr);
        }

        private void log(Level level, String str, String str2, Throwable th) {
            doLog(level, str, str2, th, null);
        }

        private void logf(Level level, String str, String str2, Object... objArr) {
            doLog(level, str, null, null, StringFormattedMessage.obtain().setFormat(str2).setParameters(objArr));
        }

        protected void doLog(Level level, String str, String str2, Throwable th, Message message) {
            if (this.mPrintAsync) {
                PrintLogRunnable printLogRunnableObtain = PrintLogRunnable.obtain();
                printLogRunnableObtain.setLogInfo(this.mLogger, level, str, str2, th, message);
                this.mSingleThreadExecutor.execute(printLogRunnableObtain);
                return;
            }
            printLog(level, str, str2, th, message);
        }

        public static class PrintLogRunnable extends AbstractMessage implements Runnable {
            private Level mLevel;
            private Logger mLogger;
            private Message mMessage;
            private String mTag;
            private String mText;
            private Throwable mThrowable;

            @Override // miuix.internal.log.message.AbstractMessage, miuix.internal.log.message.Message
            public void format(Appendable appendable) {
            }

            @Override // miuix.internal.log.message.AbstractMessage, miuix.internal.log.message.Message
            public Throwable getThrowable() {
                return null;
            }

            public static PrintLogRunnable obtain() {
                return (PrintLogRunnable) MessageFactory.obtain(PrintLogRunnable.class);
            }

            void setLogInfo(Logger logger, Level level, String str, String str2, Throwable th, Message message) {
                this.mLogger = logger;
                this.mLevel = level;
                this.mTag = str;
                this.mText = str2;
                this.mThrowable = th;
                this.mMessage = message;
            }

            @Override // java.lang.Runnable
            public void run() {
                Logger logger = this.mLogger;
                if (logger == null) {
                    android.util.Log.e(Facade.TAG, "mLogger is null");
                } else {
                    Message message = this.mMessage;
                    if (message == null) {
                        logger.log(this.mLevel, this.mTag, this.mText, this.mThrowable);
                    } else {
                        logger.log(this.mLevel, this.mTag, message);
                    }
                }
                recycle();
            }

            @Override // miuix.internal.log.message.AbstractMessage
            protected void onRecycle() {
                this.mLogger = null;
                this.mLevel = null;
                this.mTag = null;
                this.mText = null;
                this.mThrowable = null;
                Message message = this.mMessage;
                if (message != null) {
                    message.recycle();
                    this.mMessage = null;
                }
            }
        }

        private void printLog(Level level, String str, String str2, Throwable th, Message message) {
            Logger logger = this.mLogger;
            if (logger == null) {
                android.util.Log.e(TAG, "mLogger is null");
            } else if (message == null) {
                logger.log(level, str, str2, th);
            } else {
                logger.log(level, str, message);
                message.recycle();
            }
        }
    }

    private static class FullFacade extends Facade {
        private FullFacade() {
            super((Logger) null);
        }

        @Override // miuix.util.Log.Facade
        protected void doLog(Level level, String str, String str2, Throwable th, Message message) {
            LogcatLoggerInstance.INSTANCE.doLog(level, str, str2, th, message);
            FileLoggerInstance.instance().doLog(level, str, str2, th, message);
        }
    }

    private static class ExternalFullFacade extends Facade {
        private ExternalFullFacade() {
            super((Logger) null);
        }

        @Override // miuix.util.Log.Facade
        protected void doLog(Level level, String str, String str2, Throwable th, Message message) {
            LogcatLoggerInstance.INSTANCE.doLog(level, str, str2, th, message);
            ExternalFileLoggerInstance.instance().doLog(level, str, str2, th, message);
        }
    }
}
