package com.android.deskclock.alarm;

import android.content.Context;
import android.os.IBinder;
import com.android.deskclock.util.CompatUtil;
import com.android.deskclock.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class AlarmColorLightManager {
    private static final String TAG = "DC:AlarmColorLightManager";

    public static void setColorfulLight(Context context, int i) {
        try {
            Object objCallStaticObjectMethod = ReflectUtil.callStaticObjectMethod(Class.forName("miui.lights.ILightsManager$Stub"), "asInterface", new Class[]{IBinder.class}, (IBinder) ReflectUtil.callObjectMethod(ReflectUtil.callStaticObjectMethod(Class.forName("android.app.INotificationManager$Stub"), "asInterface", new Class[]{IBinder.class}, (IBinder) ReflectUtil.callStaticObjectMethod(Class.forName("android.os.ServiceManager"), "getService", new Class[]{String.class}, "notification")), IBinder.class, "getColorLightManager", null, new Object[0]));
            if (objCallStaticObjectMethod != null) {
                ReflectUtil.callObjectMethod(objCallStaticObjectMethod, "setColorfulLight", new Class[]{String.class, Integer.TYPE, Integer.TYPE}, context.getPackageName(), Integer.valueOf(i), CompatUtil.getMyUserId());
                Log.i(TAG, "mode " + i + " setColorfulLight");
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}
