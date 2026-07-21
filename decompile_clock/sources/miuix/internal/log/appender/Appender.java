package miuix.internal.log.appender;

import miuix.internal.log.Level;
import miuix.internal.log.format.Formatter;
import miuix.internal.log.message.Message;

/* JADX INFO: loaded from: classes2.dex */
public interface Appender {
    void append(String str, String str2, long j, Level level, String str3, Throwable th);

    void append(String str, String str2, long j, Level level, Message message);

    void close();

    Formatter getFormatter();

    void setFormatter(Formatter formatter);
}
