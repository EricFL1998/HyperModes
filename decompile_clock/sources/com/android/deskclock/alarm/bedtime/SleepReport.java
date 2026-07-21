package com.android.deskclock.alarm.bedtime;

import android.text.format.DateFormat;
import com.android.deskclock.alarm.AlarmModel;

/* JADX INFO: loaded from: classes.dex */
public class SleepReport {
    public long beginTime;
    public int duration;
    public long endTime;

    public SleepReport() {
        this.beginTime = Long.MAX_VALUE;
        this.endTime = Long.MIN_VALUE;
        this.duration = 0;
    }

    public SleepReport(long j, long j2, int i) {
        this.beginTime = j;
        this.endTime = j2;
        this.duration = i;
    }

    public String toString() {
        return "SleepReport:" + ((Object) DateFormat.format(AlarmModel.M24, this.beginTime)) + " " + ((Object) DateFormat.format(AlarmModel.M24, this.endTime)) + " " + String.valueOf(((double) ((int) ((((double) this.duration) / 60.0d) * 100.0d))) / 100.0d);
    }
}
