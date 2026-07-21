package com.android.deskclock;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.NotificationUtil;

/* JADX INFO: loaded from: classes.dex */
public class KeepLiveService extends Service {
    public static final int KEEP_SERVICE_RUN_TIME = 180000;
    public static final int MSG_KEEP_SERVICE_RUN_TIME = 100;
    private Handler mStopServiceHandler;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.d("KeepLiveService onCreate invoke");
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.d("KeepLiveService onStartCommand invoke");
        if (intent == null) {
            Log.d("KeepLiveService intent is null");
            return 2;
        }
        Alarm alarm = (Alarm) intent.getParcelableExtra(AlarmHelper.ALARM_INTENT_EXTRA);
        NotificationUtil.clearAlarmArrivingNotification(this);
        Notification alarmArrivingNotification = NotificationUtil.getAlarmArrivingNotification(this, alarm);
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(-3, alarmArrivingNotification, 1024);
        } else {
            startForeground(-3, alarmArrivingNotification);
        }
        Log.f("DC:NotificationUtil", "KeepLiveService startForeground, showAlarmArrivingNotification");
        Handler handler = new Handler() { // from class: com.android.deskclock.KeepLiveService.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                Log.d("stopServiceHandler will stop the service");
                KeepLiveService.this.stopSelf();
            }
        };
        this.mStopServiceHandler = handler;
        handler.sendEmptyMessageDelayed(100, alarm.time - System.currentTimeMillis());
        return super.onStartCommand(intent, i, i2);
    }

    @Override // android.app.Service
    public void onDestroy() {
        stopForeground(true);
        if (this.mStopServiceHandler != null) {
            Log.d("KeepLiveService onDestroy invoke");
            this.mStopServiceHandler.removeMessages(100);
        }
        super.onDestroy();
    }
}
