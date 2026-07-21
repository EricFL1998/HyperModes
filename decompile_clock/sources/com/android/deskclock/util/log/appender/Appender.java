package com.android.deskclock.util.log.appender;

import com.android.deskclock.util.log.Level;
import com.android.deskclock.util.log.format.Formatter;
import com.android.deskclock.util.log.message.Message;

/* JADX INFO: loaded from: classes.dex */
public interface Appender {
    void append(String str, String str2, long j, Level level, Message message);

    void append(String str, String str2, long j, Level level, String str3, Throwable th);

    void close();

    Formatter getFormatter();

    void setFormatter(Formatter formatter);
}
