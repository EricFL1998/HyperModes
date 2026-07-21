package com.android.deskclock.stopwatch;

/* JADX INFO: loaded from: classes.dex */
public class StopwatchModel {
    private long elapsedTime;
    private boolean isReset;
    private boolean isRunning;
    private long lastElapsedTime;

    public StopwatchModel(long j, boolean z, boolean z2, long j2) {
        this.elapsedTime = j;
        this.isRunning = z;
        this.isReset = z2;
        this.lastElapsedTime = j2;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public boolean isRest() {
        return this.isReset;
    }

    public long getLastElapsedTime() {
        return this.lastElapsedTime;
    }

    public void setElapsedTime(long j) {
        this.elapsedTime = j;
    }

    public void setRunning(boolean z) {
        this.isRunning = z;
    }

    public void setReset(boolean z) {
        this.isReset = z;
    }

    public void setLastElapsedTime(long j) {
        this.lastElapsedTime = j;
    }
}
