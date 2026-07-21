package com.android.deskclock.util.log.appender;

import android.util.Log;
import com.android.deskclock.util.log.Level;
import com.android.deskclock.util.log.format.Formatter;
import com.android.deskclock.util.log.message.Message;

/* JADX INFO: loaded from: classes.dex */
public class LogcatAppender implements Appender {
    private ThreadLocal<StringBuilder> mThreadCache = new ThreadLocal<StringBuilder>() { // from class: com.android.deskclock.util.log.appender.LogcatAppender.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public StringBuilder initialValue() {
            return new StringBuilder();
        }
    };

    @Override // com.android.deskclock.util.log.appender.Appender
    public void close() {
    }

    @Override // com.android.deskclock.util.log.appender.Appender
    public Formatter getFormatter() {
        return null;
    }

    @Override // com.android.deskclock.util.log.appender.Appender
    public void setFormatter(Formatter formatter) {
    }

    /* JADX INFO: renamed from: com.android.deskclock.util.log.appender.LogcatAppender$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$android$deskclock$util$log$Level;

        static {
            int[] iArr = new int[Level.values().length];
            $SwitchMap$com$android$deskclock$util$log$Level = iArr;
            try {
                iArr[Level.VERBOSE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$deskclock$util$log$Level[Level.DEBUG.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$deskclock$util$log$Level[Level.INFO.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$deskclock$util$log$Level[Level.WARNING.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$deskclock$util$log$Level[Level.ERROR.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$deskclock$util$log$Level[Level.FATAL.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    @Override // com.android.deskclock.util.log.appender.Appender
    public void append(String str, String str2, long j, Level level, String str3, Throwable th) {
        switch (AnonymousClass2.$SwitchMap$com$android$deskclock$util$log$Level[level.ordinal()]) {
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

    @Override // com.android.deskclock.util.log.appender.Appender
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
