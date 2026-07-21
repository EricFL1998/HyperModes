package com.android.deskclock.util.log.appender;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import com.android.deskclock.util.log.Level;
import com.android.deskclock.util.log.format.Formatter;

/* JADX INFO: loaded from: classes.dex */
public class AsyncFileAppender implements Appender {
    private static final String TAG = "AsyncFileAppender";
    private static final int WRITE_LOG = 1;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private FileManager mFileManager;
    private Formatter mFormatter;

    public AsyncFileAppender() {
        HandlerThread handlerThread = new HandlerThread("AsyncFileAppenderWorker", 10);
        this.mBackgroundThread = handlerThread;
        handlerThread.start();
        this.mBackgroundHandler = new Handler(this.mBackgroundThread.getLooper()) { // from class: com.android.deskclock.util.log.appender.AsyncFileAppender.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                AsyncFileAppender.this.performBackgroundTask(message.what, message.obj);
            }
        };
    }

    @Override // com.android.deskclock.util.log.appender.Appender
    public void setFormatter(Formatter formatter) {
        this.mFormatter = formatter;
    }

    @Override // com.android.deskclock.util.log.appender.Appender
    public Formatter getFormatter() {
        return this.mFormatter;
    }

    public void setFileManager(FileManager fileManager) {
        if (this.mFileManager == fileManager) {
            return;
        }
        close();
        this.mFileManager = fileManager;
    }

    public FileManager getFileManager() {
        return this.mFileManager;
    }

    @Override // com.android.deskclock.util.log.appender.Appender
    public void append(String str, String str2, long j, Level level, String str3, Throwable th) {
        doAppend(str, str2, j, level, str3, th, null);
    }

    @Override // com.android.deskclock.util.log.appender.Appender
    public void append(String str, String str2, long j, Level level, com.android.deskclock.util.log.message.Message message) {
        doAppend(str, str2, j, level, null, null, message);
    }

    private void doAppend(String str, String str2, long j, Level level, String str3, Throwable th, com.android.deskclock.util.log.message.Message message) {
        String str4;
        Formatter formatter = this.mFormatter;
        if (formatter == null) {
            Log.e(TAG, "Fail to append log for formatter is null");
            return;
        }
        if (this.mFileManager == null) {
            Log.e(TAG, "Fail to append log for FileManager is null");
            return;
        }
        if (str3 == null) {
            str4 = formatter.format(str, str2, j, level, message);
        } else {
            str4 = formatter.format(str, str2, j, level, str3, th);
        }
        this.mBackgroundHandler.obtainMessage(1, str4).sendToTarget();
    }

    @Override // com.android.deskclock.util.log.appender.Appender
    public void close() {
        FileManager fileManager = this.mFileManager;
        if (fileManager != null) {
            fileManager.close();
            this.mFileManager = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performBackgroundTask(int i, Object obj) {
        if (i != 1) {
            return;
        }
        FileManager fileManager = this.mFileManager;
        if (fileManager == null) {
            Log.e(TAG, "Fail to append log for FileManager is null");
        } else {
            fileManager.write((String) obj);
        }
    }
}
