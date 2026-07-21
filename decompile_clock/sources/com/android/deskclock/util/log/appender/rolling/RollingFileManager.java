package com.android.deskclock.util.log.appender.rolling;

import com.android.deskclock.util.log.appender.FileManager;

/* JADX INFO: loaded from: classes.dex */
public class RollingFileManager extends FileManager {
    private String mLogPath;
    private RolloverStrategy mRolloverStrategy;

    public RollingFileManager(String str, String str2) {
        super(str, str2);
    }

    public synchronized void setRolloverStrategy(RolloverStrategy rolloverStrategy) {
        this.mRolloverStrategy = rolloverStrategy;
    }

    public RolloverStrategy getRolloverStrategy() {
        return this.mRolloverStrategy;
    }

    @Override // com.android.deskclock.util.log.appender.FileManager
    protected String onBuildLogPath() {
        String str = this.mLogPath;
        return str == null ? super.onBuildLogPath() : str;
    }

    @Override // com.android.deskclock.util.log.appender.FileManager
    public synchronized void write(String str) {
        checkRollover();
        super.write(str);
    }

    private void checkRollover() {
        RolloverStrategy rolloverStrategy;
        if (this.mLogFile == null || (rolloverStrategy = this.mRolloverStrategy) == null) {
            return;
        }
        String strRollover = rolloverStrategy.rollover(this);
        this.mLogPath = strRollover;
        if (strRollover != null) {
            close();
        }
    }
}
