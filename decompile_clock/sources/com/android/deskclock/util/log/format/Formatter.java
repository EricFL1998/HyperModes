package com.android.deskclock.util.log.format;

import com.android.deskclock.util.log.Level;
import com.android.deskclock.util.log.message.Message;

/* JADX INFO: loaded from: classes.dex */
public interface Formatter {
    String format(String str, String str2, long j, Level level, Message message);

    String format(String str, String str2, long j, Level level, String str3, Throwable th);
}
