package com.android.deskclock.util.log;

import com.android.deskclock.util.log.appender.Appender;
import com.android.deskclock.util.log.message.Message;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: classes.dex */
public class Logger {
    public static final int MAX_LOG_LENGTH = 8192;
    private static final String TAG = "Logger";
    private String mName;
    private Level mLevel = Level.VERBOSE;
    private CopyOnWriteArrayList<Appender> mAppenders = new CopyOnWriteArrayList<>();

    public Logger(String str) {
        this.mName = str;
    }

    public void setLevel(Level level) {
        this.mLevel = level;
    }

    public Level getLevel() {
        return this.mLevel;
    }

    public void addAppender(Appender appender) {
        if (appender == null) {
            throw new IllegalArgumentException("Appender not allowed to be null");
        }
        this.mAppenders.addIfAbsent(appender);
    }

    public void removeAppender(Appender appender) {
        if (appender == null) {
            throw new IllegalArgumentException("The appender must not be null.");
        }
        appender.close();
        this.mAppenders.remove(appender);
    }

    public int getAppenderCount() {
        return this.mAppenders.size();
    }

    public Appender getAppenderAt(int i) {
        return this.mAppenders.get(i);
    }

    public void log(Level level, String str, String str2) {
        doLog(level, str, str2, null, null);
    }

    public void log(Level level, String str, String str2, Throwable th) {
        doLog(level, str, str2, th, null);
    }

    public void log(Level level, String str, Message message) {
        doLog(level, str, null, null, message);
    }

    private void doLog(Level level, String str, String str2, Throwable th, Message message) {
        if (level.compareTo(this.mLevel) >= 0) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            for (Appender appender : this.mAppenders) {
                if (str2 == null) {
                    appender.append(this.mName, str, jCurrentTimeMillis, level, message);
                } else {
                    appender.append(this.mName, str, jCurrentTimeMillis, level, str2, th);
                }
            }
        }
    }

    public void shutdown() {
        Iterator<Appender> it = this.mAppenders.iterator();
        while (it.hasNext()) {
            it.next().close();
        }
        this.mAppenders.clear();
    }
}
