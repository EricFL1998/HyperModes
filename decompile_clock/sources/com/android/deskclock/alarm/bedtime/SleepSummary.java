package com.android.deskclock.alarm.bedtime;

/* JADX INFO: loaded from: classes.dex */
public class SleepSummary {
    public int beginTime;
    public long duration;
    public int endTime;

    public SleepSummary() {
        this.beginTime = Integer.MAX_VALUE;
        this.endTime = Integer.MIN_VALUE;
        this.duration = 0L;
    }

    public SleepSummary(int i, int i2, long j) {
        this.beginTime = i;
        this.endTime = i2;
        this.duration = j;
    }
}
