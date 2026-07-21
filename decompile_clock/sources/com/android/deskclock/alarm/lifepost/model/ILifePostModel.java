package com.android.deskclock.alarm.lifepost.model;

/* JADX INFO: loaded from: classes.dex */
public interface ILifePostModel {
    void loadAcumulateDay();

    void loadSleepReport();

    void onDestroy();

    void saveWakeUpDay(long j, int i);
}
