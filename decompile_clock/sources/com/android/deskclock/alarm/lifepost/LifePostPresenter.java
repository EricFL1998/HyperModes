package com.android.deskclock.alarm.lifepost;

import com.android.deskclock.alarm.bedtime.SleepReport;
import com.android.deskclock.alarm.lifepost.model.ILifePostModel;
import com.android.deskclock.alarm.lifepost.model.LifePostModel;

/* JADX INFO: loaded from: classes.dex */
public class LifePostPresenter implements LifePostModel.OnLoadListener {
    private ILifePostModel mLifePostModel = new LifePostModel(this);
    private ILifePostView mLifePostView;
    private int mWakeUpPercentage;
    private long mWakeUpTime;

    public LifePostPresenter(ILifePostView iLifePostView) {
        this.mLifePostView = iLifePostView;
    }

    public void loadAcumulateDay() {
        this.mLifePostModel.loadAcumulateDay();
    }

    public void loadSleepReport() {
        this.mLifePostModel.loadSleepReport();
    }

    public void saveWakeUpDay() {
        this.mLifePostModel.saveWakeUpDay(this.mWakeUpTime, this.mWakeUpPercentage);
    }

    @Override // com.android.deskclock.alarm.lifepost.model.LifePostModel.OnLoadListener
    public void onLoadAcumulateDay(int i) {
        this.mLifePostView.setAcumulateDay(i);
    }

    @Override // com.android.deskclock.alarm.lifepost.model.LifePostModel.OnLoadListener
    public void onLoadSleepReport(SleepReport sleepReport) {
        this.mLifePostView.setSleepReport(sleepReport);
    }

    public void updateWakeUpTime(long j) {
        this.mWakeUpTime = j;
        this.mLifePostView.setWakeUpTime(LifePostUtils.getTimeInHour(j));
        int wakeUpPercentage = LifePostUtils.getWakeUpPercentage(j);
        this.mWakeUpPercentage = wakeUpPercentage;
        this.mLifePostView.setWakeUpPercentage(String.valueOf(wakeUpPercentage));
    }

    public void onDestroy() {
        this.mLifePostModel.onDestroy();
    }
}
