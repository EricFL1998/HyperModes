package com.android.deskclock.alarm.bedtime;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.deskclock.AdditionUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.xiaomi.smarthome.binder.ICallback;
import com.xiaomi.smarthome.binder.IClockService;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class MiHomeHelper {
    private static String TAG = "DC:MiHomeHelper";
    private boolean mBindSuccess;
    private ICallback.Stub mCallbackImpl = new CallbackImpl();
    private Context mContext;
    private IClockService mIClockService;
    private ServiceConnection mServiceConnection;

    public MiHomeHelper(Context context) {
        this.mContext = context;
    }

    private void bindService() {
        Log.i(TAG, "bindService");
        this.mServiceConnection = new ServiceConnection() { // from class: com.android.deskclock.alarm.bedtime.MiHomeHelper.1
            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i(MiHomeHelper.TAG, "onServiceConnected");
                MiHomeHelper.this.mIClockService = IClockService.Stub.asInterface(iBinder);
                new ClockServiceThread(MiHomeHelper.this).start();
            }
        };
        try {
            this.mBindSuccess = this.mContext.bindService(new Intent().setClassName("com.xiaomi.smarthome", "com.xiaomi.smarthome.ClockService"), this.mServiceConnection, 1);
            Log.f(TAG, "bindService result: " + this.mBindSuccess);
            StatHelper.trackEvent(StatHelper.EVENT_MI_HOME_BIND_RESULT, String.valueOf(this.mBindSuccess));
            OneTrackStatHelper.trackBoolEvent(this.mBindSuccess, OneTrackStatHelper.XIAO_HOME_BIND_RESULT);
        } catch (Exception e) {
            Log.e(TAG, "bindService error : " + e.getMessage());
        }
    }

    public void notifyBedtimeChanged() {
        Log.i(TAG, "notifyBedtimeChanged");
        if (!AdditionUtil.isMiHomeSupport()) {
            Log.i(TAG, "not support");
        } else if (this.mIClockService == null) {
            bindService();
        } else {
            new ClockServiceThread(this).start();
        }
    }

    public void release() {
        Log.i(TAG, "release");
        ServiceConnection serviceConnection = this.mServiceConnection;
        if (serviceConnection == null || !this.mBindSuccess) {
            return;
        }
        this.mContext.unbindService(serviceConnection);
        this.mServiceConnection = null;
        this.mIClockService = null;
    }

    private static class CallbackImpl extends ICallback.Stub {
        private CallbackImpl() {
        }

        @Override // com.xiaomi.smarthome.binder.ICallback
        public void onSuccess(String str) throws RemoteException {
            Log.i(MiHomeHelper.TAG, "Callback onSuccess");
        }

        @Override // com.xiaomi.smarthome.binder.ICallback
        public void onFailure(int i, String str) throws RemoteException {
            Log.i(MiHomeHelper.TAG, "Callback onFailure");
        }
    }

    public static class ClockServiceThread extends Thread {
        private WeakReference<MiHomeHelper> mReference;

        public ClockServiceThread(MiHomeHelper miHomeHelper) {
            this.mReference = new WeakReference<>(miHomeHelper);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            WeakReference<MiHomeHelper> weakReference = this.mReference;
            MiHomeHelper miHomeHelper = weakReference != null ? weakReference.get() : null;
            if (miHomeHelper != null) {
                miHomeHelper.notifySleepChange();
            }
        }
    }

    public void notifySleepChange() {
        try {
            if (this.mCallbackImpl == null || this.mIClockService == null) {
                return;
            }
            Log.f(TAG, "notifySleepChange");
            this.mIClockService.notifySleepChange(this.mCallbackImpl);
        } catch (RemoteException e) {
            Log.f(TAG, "notifySleepChange error: " + e);
            e.printStackTrace();
        }
    }
}
