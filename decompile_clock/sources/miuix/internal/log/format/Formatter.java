package miuix.internal.log.format;

import miuix.internal.log.Level;
import miuix.internal.log.message.Message;

/* JADX INFO: loaded from: classes2.dex */
public interface Formatter {
    String format(String str, String str2, long j, Level level, String str3, Throwable th);

    String format(String str, String str2, long j, Level level, Message message);
}
