package com.android.deskclock.addition.monitor;

import android.content.ComponentName;
import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import com.android.deskclock.util.Log;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class NotificationMonitor {
    private static final String PKG = "com.android.deskclock";
    private static final String TAG = "DC:NotificationMonitor";
    private final NotificationListenerService mNotificationService = new NotificationListenerService() { // from class: com.android.deskclock.addition.monitor.NotificationMonitor.1
        @Override // android.service.notification.NotificationListenerService
        public void onListenerConnected() {
        }

        @Override // android.service.notification.NotificationListenerService
        public void onListenerDisconnected() {
        }

        @Override // android.service.notification.NotificationListenerService
        public void onNotificationRemoved(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap, int i) {
        }

        @Override // android.service.notification.NotificationListenerService
        public void onNotificationPosted(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) {
            int id;
            Log.i(NotificationMonitor.TAG, "onNotificationPosted pkg:" + statusBarNotification.getPackageName() + ", id:" + statusBarNotification.getId() + ", when:" + statusBarNotification.getNotification().when);
            if (!"com.android.deskclock".equals(statusBarNotification.getPackageName()) || (id = statusBarNotification.getId()) <= 0) {
                return;
            }
            MonitorHelper.show(id, statusBarNotification.getNotification().when, System.currentTimeMillis());
        }
    };

    public void registerSystemService(Context context, ComponentName componentName) {
        try {
            findMethod("registerAsSystemService", Context.class, ComponentName.class, Integer.TYPE).invoke(this.mNotificationService, context.getApplicationContext(), componentName, -1);
            Log.d(TAG, "registerAsSystemService success.");
        } catch (Exception e) {
            Log.e(TAG, "registerAsSystemService failed", e);
        }
    }

    public void unregisterAsSystemService() {
        try {
            findMethod("unregisterAsSystemService", new Class[0]).invoke(this.mNotificationService, new Object[0]);
            Log.d(TAG, "unregisterAsSystemService success.");
        } catch (Exception e) {
            Log.e(TAG, "unregisterAsSystemService failed" + e.getMessage());
        }
    }

    private Method findMethod(String str, Class... clsArr) throws NoSuchMethodException {
        Method declaredMethod = NotificationListenerService.class.getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }
}
