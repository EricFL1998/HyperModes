package miuix.internal.log.appender;

import android.util.Log;
import miuix.internal.log.Level;
import miuix.internal.log.format.Formatter;
import miuix.internal.log.message.Message;

/* JADX INFO: loaded from: classes2.dex */
public class LogcatAppender implements Appender {
    private ThreadLocal<StringBuilder> mThreadCache = new ThreadLocal<StringBuilder>() { // from class: miuix.internal.log.appender.LogcatAppender.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public StringBuilder initialValue() {
            return new StringBuilder();
        }
    };

    @Override // miuix.internal.log.appender.Appender
    public void close() {
    }

    @Override // miuix.internal.log.appender.Appender
    public Formatter getFormatter() {
        return null;
    }

    @Override // miuix.internal.log.appender.Appender
    public void setFormatter(Formatter formatter) {
    }

    /* JADX INFO: renamed from: miuix.internal.log.appender.LogcatAppender$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$miuix$internal$log$Level;

        static {
            int[] iArr = new int[Level.values().length];
            $SwitchMap$miuix$internal$log$Level = iArr;
            try {
                iArr[Level.VERBOSE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$miuix$internal$log$Level[Level.DEBUG.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$miuix$internal$log$Level[Level.INFO.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$miuix$internal$log$Level[Level.WARNING.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$miuix$internal$log$Level[Level.ERROR.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$miuix$internal$log$Level[Level.FATAL.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    @Override // miuix.internal.log.appender.Appender
    public void append(String str, String str2, long j, Level level, String str3, Throwable th) {
        switch (AnonymousClass2.$SwitchMap$miuix$internal$log$Level[level.ordinal()]) {
            case 1:
                if (th == null) {
                    Log.v(str2, str3);
                } else {
                    Log.v(str2, str3, th);
                }
                break;
            case 2:
                if (th == null) {
                    Log.d(str2, str3);
                } else {
                    Log.d(str2, str3, th);
                }
                break;
            case 3:
                if (th == null) {
                    Log.i(str2, str3);
                } else {
                    Log.i(str2, str3, th);
                }
                break;
            case 4:
                if (th == null) {
                    Log.w(str2, str3);
                } else {
                    Log.w(str2, str3, th);
                }
                break;
            case 5:
                if (th == null) {
                    Log.e(str2, str3);
                } else {
                    Log.e(str2, str3, th);
                }
                break;
            case 6:
                if (th == null) {
                    Log.wtf(str2, str3);
                } else {
                    Log.wtf(str2, str3, th);
                }
                break;
        }
    }

    @Override // miuix.internal.log.appender.Appender
    public void append(String str, String str2, long j, Level level, Message message) {
        StringBuilder sb = this.mThreadCache.get();
        sb.setLength(0);
        message.format(sb);
        append(str, str2, j, level, sb.toString(), message.getThrowable());
        if (sb.length() > 8192) {
            sb.setLength(8192);
            sb.trimToSize();
        }
    }
}
