package com.android.deskclock.alarm.bedtime;

/* JADX INFO: loaded from: classes.dex */
public class SleepRecord {
    public int index;
    public int sleepTime;
    public int stage;
    public int wakeupTime;

    public SleepRecord(int i, int i2, int i3, int i4) {
        this.sleepTime = i;
        this.wakeupTime = i2;
        this.stage = i3;
        this.index = i4;
    }
}
