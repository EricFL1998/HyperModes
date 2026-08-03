package com.xiaomi.gnss.polaris.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.RequiresApi;
import com.xiaomi.gnss.polaris.IPolarisService;
import com.xiaomi.gnss.polaris.geofence.BuildConfig;
import com.xiaomi.gnss.polaris.geofence.IMiGeoManagerService;
import com.xiaomi.gnss.polaris.sdk.exception.PolarisException;
import com.xiaomi.gnss.polaris.sdk.geofence.PolarisGeofenceService;
import com.xiaomi.gnss.polaris.sdk.utils.PLog;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes3.dex */
public class PolarisManager implements IPolarisManager {
    private static ExecutorService mExecutorService = Executors.newCachedThreadPool();
    private static PolarisManager managerInstance = null;
    private static IPolarisService sPolarisService;
    private Context context;
    private IBinder.DeathRecipient deathRecipient;
    private IMiGeoManagerService iMiGeoManagerService;
    private PolarisGeofenceService polarisGeofenceService;
    private final PolarisServiceConnection serviceConnection;
    private final String TAG = "PolarisManager";
    private final String POLARIS_SDK_VERSION = BuildConfig.SDK_VERSION;
    private final String SERVICE_COMPONENT_PKG = "com.xiaomi.gnss.polaris";
    private final String SERVICE_COMPONENT_CLS = "com.xiaomi.gnss.polaris.PolarisService";
    private final int MAX_RECONNECT_TIME = 5;

    private class PolarisDeathRecipient implements IBinder.DeathRecipient {
        private PolarisDeathRecipient() {
        }

        @Override // android.os.IBinder.DeathRecipient
        @RequiresApi(api = 29)
        public void binderDied() {
            PLog.i("PolarisManager", "binder died");
            if (PolarisManager.sPolarisService != null) {
                PolarisManager.sPolarisService.asBinder().unlinkToDeath(PolarisManager.this.deathRecipient, 0);
                new Handler(PolarisManager.this.context.getMainLooper()).postDelayed(new Runnable() { // from class: com.xiaomi.gnss.polaris.sdk.PolarisManager.PolarisDeathRecipient.1
                    @Override // java.lang.Runnable
                    public void run() {
                        PLog.i("PolarisManager", "reconnect service while binder died");
                        try {
                            PolarisManager.this.reconnectPolarisService();
                        } catch (RemoteException | PolarisException | NullPointerException e10) {
                            e10.printStackTrace();
                        }
                    }
                }, 1000L);
            }
        }
    }

    private class PolarisServiceConnection implements ServiceConnection {
        private ServiceConnectCallback successCb;

        private PolarisServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PLog.i("PolarisManager", "connect polarisService success");
            IPolarisService unused = PolarisManager.sPolarisService = IPolarisService.Stub.asInterface(iBinder);
            if (iBinder != null) {
                try {
                    iBinder.linkToDeath(PolarisManager.this.deathRecipient, 0);
                } catch (RemoteException e10) {
                    e10.printStackTrace();
                }
            }
            ServiceConnectCallback serviceConnectCallback = this.successCb;
            if (serviceConnectCallback != null) {
                serviceConnectCallback.callback();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            PLog.i("PolarisManager", "polarisService disconnect, componentName:" + componentName);
        }

        protected void setConnectSuccessCb(ServiceConnectCallback serviceConnectCallback) {
            this.successCb = serviceConnectCallback;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    interface ServiceConnectCallback {
        void callback();
    }

    public enum ServiceType {
        Geofence
    }

    private PolarisManager(Context context) {
        this.serviceConnection = new PolarisServiceConnection();
        this.deathRecipient = new PolarisDeathRecipient();
        this.context = context;
    }

    private void checkPolarisServiceConnect() throws PolarisException {
        IPolarisService iPolarisService = sPolarisService;
        if (iPolarisService == null || !iPolarisService.asBinder().isBinderAlive()) {
            throw new PolarisException("polaris service not connect");
        }
    }

    private boolean getBooleanSystemProperties(String str, boolean z10) {
        try {
            return ((Boolean) Class.forName("android.os.SystemProperties").getDeclaredMethod("getBoolean", String.class, Boolean.TYPE).invoke(null, str, Boolean.valueOf(z10))).booleanValue();
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e10) {
            e10.printStackTrace();
            return z10;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized PolarisGeofenceService getGeofenceService() throws PolarisException {
        checkPolarisServiceConnect();
        PolarisGeofenceService polarisGeofenceService = this.polarisGeofenceService;
        if (polarisGeofenceService != null) {
            return polarisGeofenceService;
        }
        if (this.iMiGeoManagerService == null) {
            try {
                this.iMiGeoManagerService = sPolarisService.getGeoManagerService();
            } catch (RemoteException | SecurityException e10) {
                e10.printStackTrace();
                Log.e("PolarisManager", "can not get the IMiGeoManagerService : " + Log.getStackTraceString(e10));
                return null;
            }
        }
        PolarisGeofenceServiceImpl polarisGeofenceServiceImpl = PolarisGeofenceServiceImpl.getInstance(this.context, this.iMiGeoManagerService);
        this.polarisGeofenceService = polarisGeofenceServiceImpl;
        return polarisGeofenceServiceImpl;
    }

    public static synchronized PolarisManager getInstance(Context context) {
        try {
            if (managerInstance == null) {
                managerInstance = new PolarisManager(context);
            }
            if (mExecutorService == null) {
                mExecutorService = Executors.newCachedThreadPool();
            }
        } catch (Throwable th) {
            throw th;
        }
        return managerInstance;
    }

    private boolean isSupportGeofenceService() {
        return Build.VERSION.SDK_INT >= 29 && getBooleanSystemProperties("persist.sys.gps.fence", false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @RequiresApi(api = 29)
    public void reconnectPolarisService() throws PolarisException, RemoteException {
        sPolarisService = null;
        this.iMiGeoManagerService = null;
        this.polarisGeofenceService = null;
        PLog.i("PolarisManager", "try to reconnect polarisService");
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.xiaomi.gnss.polaris", "com.xiaomi.gnss.polaris.PolarisService"));
        this.context.startService(intent);
        PLog.v("PolarisManager", "reconnecting polaris service async");
        this.serviceConnection.setConnectSuccessCb(new ServiceConnectCallback() { // from class: com.xiaomi.gnss.polaris.sdk.PolarisManager.1
            @Override // com.xiaomi.gnss.polaris.sdk.PolarisManager.ServiceConnectCallback
            public void callback() {
                PLog.i("PolarisManager", "reconnect success");
                try {
                    PolarisManager.this.iMiGeoManagerService = PolarisManager.sPolarisService.getGeoManagerService();
                    PolarisManager polarisManager = PolarisManager.this;
                    polarisManager.polarisGeofenceService = polarisManager.getGeofenceService();
                } catch (RemoteException | PolarisException | NullPointerException | SecurityException e10) {
                    PLog.e("PolarisManager", Log.getStackTraceString(e10));
                }
                PolarisManager.this.serviceConnection.setConnectSuccessCb(null);
            }
        });
        this.context.bindService(intent, 1, mExecutorService, this.serviceConnection);
    }

    @Override // com.xiaomi.gnss.polaris.sdk.IPolarisManager
    @RequiresApi(api = 29)
    public synchronized void connectPolarisServiceSync() throws PolarisException {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.xiaomi.gnss.polaris", "com.xiaomi.gnss.polaris.PolarisService"));
        this.context.startService(intent);
        if (sPolarisService == null) {
            PLog.v("PolarisManager", "connecting polaris service sync");
            final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            this.serviceConnection.setConnectSuccessCb(new ServiceConnectCallback() { // from class: com.xiaomi.gnss.polaris.sdk.a
                @Override // com.xiaomi.gnss.polaris.sdk.PolarisManager.ServiceConnectCallback
                public final void callback() {
                    atomicBoolean.set(true);
                }
            });
            boolean zBindService = this.context.bindService(intent, 1, mExecutorService, this.serviceConnection);
            for (int i10 = 0; i10 < 50; i10++) {
                Log.d("PolarisManager", "wait connect time " + i10 + ", bindResult: " + zBindService + ", currentService: " + sPolarisService);
                if (atomicBoolean.get()) {
                    PLog.i("PolarisManager", "connect polaris service sync success at attempt " + i10);
                    return;
                }
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException unused) {
                }
            }
            throw new PolarisException("connect polaris service timeout after 10s. bindResult=" + zBindService);
        }
    }

    @Override // com.xiaomi.gnss.polaris.sdk.IPolarisManager
    public String getPolarisSdkVersion() {
        return BuildConfig.SDK_VERSION;
    }

    @Override // com.xiaomi.gnss.polaris.sdk.IPolarisManager
    public String getPolarisServerVersion() throws PolarisException {
        checkPolarisServiceConnect();
        try {
            return sPolarisService.getPolarisVersion();
        } catch (RemoteException e10) {
            e10.printStackTrace();
            Log.e("PolarisManager", "fail in getPolarisServerVersion() : " + Log.getStackTraceString(e10));
            return null;
        }
    }

    @Override // com.xiaomi.gnss.polaris.sdk.IPolarisManager
    public IChildService getSubService(ServiceType serviceType) throws PolarisException {
        checkPolarisServiceConnect();
        if (serviceType != ServiceType.Geofence) {
            return null;
        }
        try {
            return getGeofenceService();
        } catch (PolarisException e10) {
            e10.printStackTrace();
            return null;
        }
    }

    @Override // com.xiaomi.gnss.polaris.sdk.IPolarisManager
    public boolean isPolarisSupport() {
        return Build.VERSION.SDK_INT >= 30 && getBooleanSystemProperties("persist.sys.gps.polaris", false);
    }

    @Override // com.xiaomi.gnss.polaris.sdk.IPolarisManager
    public boolean isSubServiceSupport(ServiceType serviceType) {
        if (serviceType == ServiceType.Geofence) {
            return isSupportGeofenceService();
        }
        return false;
    }
}
