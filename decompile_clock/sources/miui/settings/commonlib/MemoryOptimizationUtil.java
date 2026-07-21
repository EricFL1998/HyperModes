package miui.settings.commonlib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.android.settings.services.IMemoryOptimizationInterface;

/* JADX INFO: loaded from: classes2.dex */
public class MemoryOptimizationUtil {
    public static final String CONTROLLER_PKG = "com.android.htmlviewer";
    private static final long DEFAULT_KILL_DELAY_TIME = 30000;
    public static final String DELAY_TIME_KEY = "delay_time";
    public static final String OPTIMIZATION_ACTIION = "miui.intent.action.MEMORY_OPTIMIZATION";
    public static final String OPTIMIZATION_INIT_ACTIION = "miui.intent.action.MEMORY_OPTIMIZATION_INIT";
    public static final String OPTIMIZED_PKG_KEY = "optimized_package";
    public static final String RESTART_APP_STUB_ACTIION = "miui.intent.action.RESTART_APP_STUB";
    public static final String RESTART_PROCESS_KEY = "restart_process";
    public static final String TAG = "MemoryOptimizationService";
    private IMemoryOptimizationInterface proxy;
    private ServiceConnection serviceConnection = new ServiceConnection() { // from class: miui.settings.commonlib.MemoryOptimizationUtil.1
        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MemoryOptimizationUtil.this.proxy = IMemoryOptimizationInterface.Stub.asInterface(iBinder);
        }
    };

    public static void sendMemoryOptimizationMsg(Context context, String str, boolean z, long j, boolean z2) {
        if (context == null) {
            Log.i(TAG, "context is null");
            return;
        }
        Intent intent = new Intent(OPTIMIZATION_ACTIION);
        if (z2) {
            intent = new Intent(OPTIMIZATION_INIT_ACTIION);
        }
        intent.setPackage(str);
        intent.putExtra(OPTIMIZED_PKG_KEY, context.getPackageName());
        intent.putExtra(RESTART_PROCESS_KEY, z);
        intent.putExtra(DELAY_TIME_KEY, j);
        try {
            context.startService(intent);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public static void sendMemoryOptimizationMsg(Context context, long j) {
        sendMemoryOptimizationMsg(context, CONTROLLER_PKG, true, j, false);
    }

    public static void sendMemoryOptimizationMsg(Context context) {
        sendMemoryOptimizationMsg(context, CONTROLLER_PKG, true, DEFAULT_KILL_DELAY_TIME, false);
    }

    public static void initMemoryOptimizationService(Context context) {
        sendMemoryOptimizationMsg(context, CONTROLLER_PKG, false, DEFAULT_KILL_DELAY_TIME, true);
    }

    public void bindMemoryOptimizationService(Context context) {
        Intent intent = new Intent(OPTIMIZATION_INIT_ACTIION);
        intent.setPackage(CONTROLLER_PKG);
        try {
            context.bindService(intent, this.serviceConnection, 1);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void startMemoryOptimization(Context context) {
        startMemoryOptimization(context, CONTROLLER_PKG, true, DEFAULT_KILL_DELAY_TIME);
    }

    public void startMemoryOptimization(Context context, String str, boolean z, long j) {
        if (this.proxy == null) {
            Log.e(TAG, "MemoryOptimization proxy is null");
        }
        Intent intent = new Intent(OPTIMIZATION_ACTIION);
        intent.setPackage(str);
        intent.putExtra(OPTIMIZED_PKG_KEY, context.getPackageName());
        intent.putExtra(RESTART_PROCESS_KEY, z);
        intent.putExtra(DELAY_TIME_KEY, j);
        try {
            this.proxy.startMemoryOptimization(intent);
            context.unbindService(this.serviceConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
