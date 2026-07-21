package com.android.deskclock.alarm.lifepost;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.alarm.lifepost.okhttp.ListenNewsUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.NotificationUtil;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.SleepModeUtil;

/* JADX INFO: loaded from: classes.dex */
public class RecommendIntentService extends IntentService {
    private static final String TAG = "DC:RecommendHelper";

    public RecommendIntentService() {
        super(TAG);
    }

    @Override // android.app.IntentService, android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        Notification notificationBuildMarkNotification = NotificationUtil.buildMarkNotification(this, getString(R.string.update_notification_info));
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(-5, notificationBuildMarkNotification, 1);
        } else {
            startForeground(-5, notificationBuildMarkNotification);
        }
        return super.onStartCommand(intent, i, i2);
    }

    @Override // android.app.IntentService, android.app.Service
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    @Override // android.app.IntentService
    protected void onHandleIntent(Intent intent) {
        Context appDEContext = DeskClockApp.getAppDEContext();
        if (SleepModeUtil.inSleepMode(appDEContext)) {
            Log.i(TAG, "recommend update cancel for sleep mode");
            return;
        }
        if (!LifePostUtils.isNewsSupport(appDEContext)) {
            Log.i(TAG, "not support fm news");
        } else if (PrefUtil.getNewsUpdateTime() < LifePostUtils.startTimeToday()) {
            Log.i(TAG, "update fm news");
            ListenNewsUtil.getListenNewsFromUrl(appDEContext);
        } else {
            Log.i(TAG, "fm news has updated today");
        }
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
