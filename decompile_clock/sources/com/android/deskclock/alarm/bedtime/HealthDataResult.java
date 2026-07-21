package com.android.deskclock.alarm.bedtime;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class HealthDataResult {
    public int lowLimitTime;
    public List<SleepRecord> sleepRecordList = new ArrayList();
    public List<SleepSummary> sleepSummarytList = new ArrayList();
    public int thresholdMins;
    public int upLimitTime;

    public HealthDataResult(Context context) {
        for (int i = 0; i < 7; i++) {
            this.sleepSummarytList.add(new SleepSummary());
        }
        int thresholdMins = BedtimeUtil.getThresholdMins(context);
        this.thresholdMins = thresholdMins;
        this.lowLimitTime = thresholdMins - 1440;
        this.upLimitTime = thresholdMins;
    }
}
