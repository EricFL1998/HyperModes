package com.xiaomi.onetrack.util.oaid.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public class e {
    public final LinkedBlockingQueue<IBinder> a = new LinkedBlockingQueue<>(1);
    ServiceConnection b = new ServiceConnection() { // from class: com.xiaomi.onetrack.util.oaid.helpers.LenovoDeviceIDHelper$1
        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                this.a.a.offer(iBinder, 1L, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public String a(Context context) {
        context.getPackageName();
        Intent intent = new Intent();
        intent.setClassName("com.zui.deviceidservice", "com.zui.deviceidservice.DeviceidService");
        String strA = "";
        if (context.bindService(intent, this.b, 1)) {
            try {
                try {
                    IBinder iBinderPoll = this.a.poll(1L, TimeUnit.SECONDS);
                    if (iBinderPoll == null) {
                        return "";
                    }
                    strA = new com.xiaomi.onetrack.util.oaid.a.c.a.C0025a(iBinderPoll).a();
                } finally {
                    try {
                        context.unbindService(this.b);
                    } catch (Exception unused) {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return strA;
    }
}
