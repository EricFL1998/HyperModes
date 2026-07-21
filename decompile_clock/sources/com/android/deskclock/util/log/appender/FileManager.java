package com.android.deskclock.util.log.appender;

import android.os.SystemClock;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import miuix.core.util.IOUtils;

/* JADX INFO: loaded from: classes.dex */
public class FileManager {
    private static final int FILE_CHECK_INTERVAL = 1000;
    private static final String LOG_EXTENSION = ".log";
    private static final int PREPARE_RETRY_INTERVAL = 30000;
    private static final int RETRY_LIMIT = 10;
    private static final String TAG = "FileManager";
    private long mFileCheckStamp;
    protected File mLogFile;
    private long mLogLength;
    protected String mLogName;
    protected String mLogRoot;
    private FileOutputStream mOutputStream;
    private long mPrepareRetryStamp;
    private int mRetryCount;
    private OutputStreamWriter mWriter;

    public FileManager(String str, String str2) {
        this.mLogRoot = str;
        this.mLogName = str2;
    }

    public String getLogRoot() {
        return this.mLogRoot;
    }

    public String getLogName() {
        return this.mLogName;
    }

    public String getLogExtension() {
        return LOG_EXTENSION;
    }

    public File getLogFile() {
        return this.mLogFile;
    }

    public long getLogSize() {
        return this.mLogLength;
    }

    public synchronized void write(String str) {
        OutputStreamWriter outputStreamWriter = this.mWriter;
        if (outputStreamWriter == null) {
            prepareWriter();
        } else if (outputStreamWriter != null) {
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            if (jElapsedRealtime - this.mFileCheckStamp > 1000) {
                this.mFileCheckStamp = jElapsedRealtime;
                if (!this.mLogFile.exists()) {
                    Log.d(TAG, "Repair writer for log file is missing");
                    repairWriter();
                }
            }
        }
        if (this.mWriter == null) {
            Log.e(TAG, "Fail to append log for writer is null");
        } else {
            try {
                doWrite(str);
            } catch (IOException e) {
                Log.w(TAG, "Retry to write log", e);
                repairWriter();
                if (this.mWriter == null) {
                    Log.e(TAG, "Fail to append log for writer is null when retry");
                } else {
                    try {
                        doWrite(str);
                    } catch (IOException e2) {
                        Log.e(TAG, "Fail to append log for writer is null when retry", e2);
                    }
                }
            }
        }
    }

    private void doWrite(String str) throws IOException {
        this.mWriter.write(str);
        this.mWriter.flush();
        this.mLogLength += (long) str.length();
    }

    public synchronized void close() {
        this.mLogFile = null;
        this.mRetryCount = 0;
        this.mOutputStream = null;
        IOUtils.closeQuietly((Writer) this.mWriter);
        this.mWriter = null;
        this.mLogLength = 0L;
    }

    private void prepareWriter() {
        if (this.mRetryCount >= 10) {
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            if (jElapsedRealtime - this.mPrepareRetryStamp <= 30000) {
                return;
            } else {
                this.mPrepareRetryStamp = jElapsedRealtime;
            }
        }
        this.mRetryCount++;
        File fileOnCreateLogFile = onCreateLogFile();
        this.mLogFile = fileOnCreateLogFile;
        if (fileOnCreateLogFile != null) {
            try {
                this.mOutputStream = new FileOutputStream(this.mLogFile, true);
                this.mWriter = new OutputStreamWriter(this.mOutputStream);
                this.mRetryCount = 0;
                this.mLogLength = this.mLogFile.length();
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Fail to create writer: " + this.mLogFile.getPath(), e);
            }
        }
    }

    private void repairWriter() {
        close();
        prepareWriter();
    }

    protected File onCreateLogFile() {
        String strOnBuildLogPath = onBuildLogPath();
        if (strOnBuildLogPath == null) {
            Log.e(TAG, "Fail to build log path");
            return null;
        }
        File file = new File(strOnBuildLogPath);
        File parentFile = file.getParentFile();
        if (parentFile.exists()) {
            if (!parentFile.isDirectory()) {
                Log.e(TAG, "LogDir is not a directory: " + parentFile.getPath());
                return null;
            }
        } else if (!parentFile.mkdirs() && !parentFile.exists()) {
            Log.e(TAG, "Fail to create directory: " + parentFile.getPath());
            return null;
        }
        if (file.exists()) {
            if (!file.isFile()) {
                Log.e(TAG, "LogFile is not a file: " + strOnBuildLogPath);
                return null;
            }
        } else {
            try {
                if (!file.createNewFile() && !file.exists()) {
                    Log.e(TAG, "Fail to create LogFile: " + strOnBuildLogPath);
                    return null;
                }
            } catch (IOException e) {
                Log.e(TAG, "Fail to create LogFile: " + strOnBuildLogPath, e);
                return null;
            }
        }
        return file;
    }

    protected String onBuildLogPath() {
        return this.mLogRoot + "/" + this.mLogName + LOG_EXTENSION;
    }
}
