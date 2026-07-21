package com.android.deskclock.alarm.lifepost;

import android.view.View;
import com.android.deskclock.alarm.bedtime.SleepReport;

/* JADX INFO: loaded from: classes.dex */
public interface ILifePostView {
    void setAcumulateDay(int i);

    void setSleepReport(SleepReport sleepReport);

    void setWakeUpPercentage(String str);

    void setWakeUpTime(CharSequence charSequence);

    void setWeatherClickListener(View view, View.OnClickListener onClickListener);
}
