package com.xiaomi.onetrack.util.oaid.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.xiaomi.onetrack.util.p;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public class k {
    private static final String c = "SamsungDeviceIDHelper";
    public final LinkedBlockingQueue<IBinder> a = new LinkedBlockingQueue<>(1);
    ServiceConnection b = new ServiceConnection() { // from class: com.xiaomi.onetrack.util.oaid.helpers.SamsungDeviceIDHelper$1
        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                this.a.a.offer(iBinder, 1L, TimeUnit.SECONDS);
            } catch (Exception e) {
                p.a("SamsungDeviceIDHelper", e.getMessage());
            }
        }
    };

    public String a(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.samsung.android.deviceidservice", "com.samsung.android.deviceidservice.DeviceIdService");
        String strA = "";
        if (context.bindService(intent, this.b, 1)) {
            try {
                try {
                    try {
                        IBinder iBinderPoll = this.a.poll(1L, TimeUnit.SECONDS);
                        if (iBinderPoll != null) {
                            strA = new com.xiaomi.onetrack.util.oaid.a.f.a(iBinderPoll).a();
                            context.unbindService(this.b);
                        } else {
                            try {
                                context.unbindService(this.b);
                            } catch (Exception e) {
                                p.a(c, e.getMessage());
                            }
                            return "";
                        }
                    } catch (Throwable th) {
                        try {
                            context.unbindService(this.b);
                        } catch (Exception e2) {
                            p.a(c, e2.getMessage());
                        }
                        throw th;
                    }
                } catch (Exception e3) {
                    p.a(c, e3.getMessage());
                }
            } catch (Exception e4) {
                p.a(c, e4.getMessage());
                context.unbindService(this.b);
            }
        }
        return strA;
    }
}
