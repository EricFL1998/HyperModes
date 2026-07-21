package miuix.animation.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: classes2.dex */
public class LogUtils {
    private static final String COMMA = ", ";
    public static final boolean MORE_LOG_ENABLE = false;
    private static final Handler sLogHandler;
    private static volatile int sLogLevel;
    private static final Map<Integer, String> sTag;
    private static final HandlerThread sThread;

    static {
        HandlerThread handlerThread = new HandlerThread("FolmeLogThread");
        sThread = handlerThread;
        sTag = new ConcurrentHashMap();
        handlerThread.start();
        sLogHandler = new Handler(handlerThread.getLooper()) { // from class: miuix.animation.utils.LogUtils.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == 0) {
                    Log.d((String) LogUtils.sTag.get(Integer.valueOf(message.arg1)), "thread log, " + ((String) message.obj));
                }
                message.obj = null;
            }
        };
        sLogLevel = 0;
    }

    public static void logThread(String str, String str2) {
        Message messageObtainMessage = sLogHandler.obtainMessage(0);
        messageObtainMessage.obj = str2;
        messageObtainMessage.arg1 = str.hashCode();
        sTag.put(Integer.valueOf(messageObtainMessage.arg1), str);
        messageObtainMessage.sendToTarget();
    }

    public static void logThread(String str, String str2, Object... objArr) {
        Message messageObtainMessage = sLogHandler.obtainMessage(0);
        if (objArr.length > 0) {
            StringBuilder sb = new StringBuilder(COMMA);
            int length = sb.length();
            for (Object obj : objArr) {
                if (sb.length() > length) {
                    sb.append(COMMA);
                }
                sb.append(obj);
            }
            str2 = str2 + ((Object) sb);
        }
        messageObtainMessage.obj = str2;
        messageObtainMessage.arg1 = str.hashCode();
        sTag.put(Integer.valueOf(messageObtainMessage.arg1), str);
        messageObtainMessage.sendToTarget();
    }

    private LogUtils() {
    }

    public static void getLogEnableInfo() {
        String str = "";
        try {
            String prop = CommonUtils.readProp("log.tag.folme.level");
            if (prop != null) {
                str = prop;
            }
        } catch (Exception e) {
            Log.i(CommonUtils.TAG, "can not access property log.tag.folme.level, no log", e);
        }
        if (sLogLevel > 0) {
            return;
        }
        if (str.equals("D")) {
            sLogLevel = 1;
            return;
        }
        try {
            setLogLevel(Integer.parseInt(str));
        } catch (NumberFormatException unused) {
            sLogLevel = 0;
        }
    }

    public static void setLogLevel(int i) {
        if (i < 0) {
            sLogLevel = 0;
        } else {
            sLogLevel = i;
        }
    }

    public static boolean isLogLevelEnable(int i) {
        return (i & sLogLevel) > 0;
    }

    public static boolean isLogMainEnabled() {
        return isLogLevelEnable(1);
    }

    public static boolean isLogMoreEnable() {
        return isLogLevelEnable(2);
    }

    public static boolean isLogDetailEnable() {
        return isLogLevelEnable(4);
    }

    public static boolean isLogFrameEnable() {
        return isLogLevelEnable(8);
    }

    public static boolean isLogDesignEnable() {
        return isLogLevelEnable(16);
    }

    public static void debug(String str, Object... objArr) {
        if (sLogLevel == 0) {
            return;
        }
        if (objArr.length > 0) {
            StringBuilder sb = new StringBuilder(COMMA);
            int length = sb.length();
            for (Object obj : objArr) {
                if (sb.length() > length) {
                    sb.append(COMMA);
                }
                sb.append(obj);
            }
            Log.i(CommonUtils.TAG, str + ((Object) sb));
            return;
        }
        Log.i(CommonUtils.TAG, str);
    }

    public static String getStackTrace(int i) {
        int iMax = Math.max(i, 0);
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int iMin = Math.min(stackTrace.length, iMax + 4);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printWriter.println("\ntrace:");
        for (int i2 = 3; i2 < iMin; i2++) {
            printWriter.println("\tat " + stackTrace[i2]);
        }
        printWriter.flush();
        return stringWriter.toString();
    }
}
