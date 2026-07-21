package com.android.deskclock.addition.resource;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.util.AlarmThreadPool;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.MiUiUtil;
import com.android.deskclock.util.NotificationUtil;
import com.android.deskclock.util.Util;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ResourceLoadService extends Service {
    public static final int DOWNLOAD = 3;
    public static final int DOWNLOAD_ERROR = 5;
    public static final int DOWNLOAD_SUCCESS = 4;
    public static final String EXTRA_TYPE = "extra_type";
    public static final int IDLE = 0;
    public static final int LOAD = 1;
    public static final int LOAD_DONE = 2;
    private static final int NOTIFICATION_ID = -6;
    private static final String TAG = "DC:ExternalResourceUtils";
    private List<CallbackListener> mCallbackListeners;
    private MiuiResource mMiuiResourceLoader;
    private int mState = 0;

    public interface CallbackListener {
        void onNetLoadFailed();

        void onNetLoadSuccess();

        void onRomLoadFailed();

        void onRomLoadSuccess(int i);
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mCallbackListeners = new ArrayList();
        this.mMiuiResourceLoader = new MiuiResource(new MiuiResource.OnLoadListener() { // from class: com.android.deskclock.addition.resource.ResourceLoadService.1
            @Override // com.android.deskclock.addition.resource.MiuiResource.OnLoadListener
            public void onSuccess(int i, boolean z) {
                if (z) {
                    ResourceLoadService.this.mState = 4;
                    ResourceLoadService.this.onNetLoadSuccess();
                } else {
                    ResourceLoadService.this.mState = 2;
                    ResourceLoadService.this.onRomLoadSuccess(i);
                }
            }

            @Override // com.android.deskclock.addition.resource.MiuiResource.OnLoadListener
            public void onFail(boolean z) {
                if (z) {
                    ResourceLoadService.this.mState = 5;
                    ResourceLoadService.this.onNetLoadFailed();
                } else {
                    ResourceLoadService.this.mState = 2;
                    ResourceLoadService.this.onRomLoadFailed();
                }
            }
        });
        try {
            if (Util.isVersionS()) {
                return;
            }
            MiUiUtil.init(DeskClockApp.getAppContext());
        } catch (Throwable unused) {
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        startForeground((intent.hasExtra(EXTRA_TYPE) && intent.getIntExtra(EXTRA_TYPE, 1) == 3) ? R.string.module_downloading : R.string.update_notification_info);
        return 2;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return new CallbackBinder();
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        MiuiResource miuiResource = this.mMiuiResourceLoader;
        if (miuiResource != null) {
            miuiResource.clear();
            this.mMiuiResourceLoader = null;
        }
    }

    public void registerCallbackListener(CallbackListener callbackListener) {
        this.mCallbackListeners.add(callbackListener);
    }

    public void unregisterCallbackListener(CallbackListener callbackListener) {
        this.mCallbackListeners.remove(callbackListener);
    }

    public int getState() {
        return this.mState;
    }

    public boolean isDownloading() {
        return this.mState == 3;
    }

    public boolean isLoading() {
        return this.mState == 1;
    }

    public void loadNetResource() {
        int i = this.mState;
        if (i != 0 && i != 2 && i != 5) {
            Log.i("DC:ExternalResourceUtils", "load net resource error for state = " + this.mState);
            return;
        }
        Log.i("DC:ExternalResourceUtils", "load net resource");
        this.mState = 3;
        startForeground(R.string.module_downloading);
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.addition.resource.ResourceLoadService.2
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                ResourceLoadService.this.mMiuiResourceLoader.loadNetResource();
            }
        });
    }

    public void loadRomResource(final int i) {
        if (this.mState != 0) {
            Log.i("DC:ExternalResourceUtils", "load rom resource error for state = " + this.mState);
            return;
        }
        Log.i("DC:ExternalResourceUtils", "load rom resource");
        this.mState = 1;
        startForeground(R.string.update_notification_info);
        AlarmThreadPool.poolExecute(new Runnable() { // from class: com.android.deskclock.addition.resource.ResourceLoadService.3
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                ResourceLoadService.this.mMiuiResourceLoader.loadRomResource(i);
            }
        });
    }

    public void onRomLoadSuccess(final int i) {
        Log.i("DC:ExternalResourceUtils", "rom resource load success");
        AlarmThreadPool.runOnUiThread(new Runnable() { // from class: com.android.deskclock.addition.resource.ResourceLoadService.4
            @Override // java.lang.Runnable
            public void run() {
                ExternalResourceUtils.recordRomResourceLoaded();
                for (int i2 = 0; i2 < ResourceLoadService.this.mCallbackListeners.size(); i2++) {
                    ((CallbackListener) ResourceLoadService.this.mCallbackListeners.get(i2)).onRomLoadSuccess(i);
                }
                ResourceLoadService.this.stopForeground();
            }
        });
    }

    public void onRomLoadFailed() {
        Log.i("DC:ExternalResourceUtils", "rom resource load failed");
        AlarmThreadPool.runOnUiThread(new Runnable() { // from class: com.android.deskclock.addition.resource.ResourceLoadService.5
            @Override // java.lang.Runnable
            public void run() {
                ExternalResourceUtils.recordRomResourceLoaded();
                for (int i = 0; i < ResourceLoadService.this.mCallbackListeners.size(); i++) {
                    ((CallbackListener) ResourceLoadService.this.mCallbackListeners.get(i)).onRomLoadFailed();
                }
                ResourceLoadService.this.stopForeground();
            }
        });
    }

    public void onNetLoadSuccess() {
        Log.i("DC:ExternalResourceUtils", "net resource load success");
        AlarmThreadPool.runOnUiThread(new Runnable() { // from class: com.android.deskclock.addition.resource.ResourceLoadService.6
            @Override // java.lang.Runnable
            public void run() {
                for (int i = 0; i < ResourceLoadService.this.mCallbackListeners.size(); i++) {
                    ((CallbackListener) ResourceLoadService.this.mCallbackListeners.get(i)).onNetLoadSuccess();
                }
                ResourceLoadService.this.stopForeground();
            }
        });
    }

    public void onNetLoadFailed() {
        Log.i("DC:ExternalResourceUtils", "net resource load failed");
        AlarmThreadPool.runOnUiThread(new Runnable() { // from class: com.android.deskclock.addition.resource.ResourceLoadService.7
            @Override // java.lang.Runnable
            public void run() {
                for (int i = 0; i < ResourceLoadService.this.mCallbackListeners.size(); i++) {
                    ((CallbackListener) ResourceLoadService.this.mCallbackListeners.get(i)).onNetLoadFailed();
                }
                ResourceLoadService.this.stopForeground();
            }
        });
    }

    private void startForeground(int i) {
        Notification notificationBuildMarkNotification = NotificationUtil.buildMarkNotification(this, getString(i));
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(-6, notificationBuildMarkNotification, 1);
        } else {
            startForeground(-6, notificationBuildMarkNotification);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopForeground() {
        stopForeground(true);
    }

    public class CallbackBinder extends Binder {
        public CallbackBinder() {
        }

        public ResourceLoadService getService() {
            return ResourceLoadService.this;
        }
    }
}
