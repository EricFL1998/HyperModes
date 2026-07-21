package com.android.deskclock.util;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.android.deskclock.DeskClockApp;

/* JADX INFO: loaded from: classes.dex */
public class NetworkUtil {
    private static final String TAG = "NetworkUtil ";

    public enum NetState {
        WIFI,
        MN2G,
        MN3G,
        MN4G,
        NONE
    }

    public static int getActiveNetworkType() {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) DeskClockApp.getAppDEContext().getSystemService("connectivity")).getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.getType();
            }
            return -1;
        } catch (SecurityException e) {
            Log.e(TAG + e.toString());
            return -1;
        }
    }

    public static boolean isNetworkConnected() {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) DeskClockApp.getAppDEContext().getSystemService("connectivity")).getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (SecurityException e) {
            Log.e(TAG + e.toString());
            return false;
        }
    }

    public static boolean isWifiConnected() {
        return getNetState() == NetState.WIFI;
    }

    public static NetState getNetState() {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) DeskClockApp.getAppDEContext().getSystemService("connectivity")).getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
                if (activeNetworkInfo.getType() == 1) {
                    return NetState.WIFI;
                }
                return NetState.NONE;
            }
            return NetState.NONE;
        } catch (Exception e) {
            Log.e(TAG + e.getMessage());
        }
    }
}
