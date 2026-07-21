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
public class m {
    private static final String d = "ZTEDeviceIDHelper";
    String a = "com.mdid.msa";
    public final LinkedBlockingQueue<IBinder> b = new LinkedBlockingQueue<>(1);
    ServiceConnection c = new ServiceConnection() { // from class: com.xiaomi.onetrack.util.oaid.helpers.ZTEDeviceIDHelper$1
        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                this.a.b.offer(iBinder, 1L, TimeUnit.SECONDS);
            } catch (Exception e) {
                p.a("ZTEDeviceIDHelper", e.getMessage());
            }
        }
    };

    private void a(String str, Context context) {
        Intent intent = new Intent();
        intent.setClassName(this.a, "com.mdid.msa.service.MsaKlService");
        intent.setAction("com.bun.msa.action.start.service");
        intent.putExtra("com.bun.msa.param.pkgname", str);
        try {
            intent.putExtra("com.bun.msa.param.runinset", true);
            context.startService(intent);
        } catch (Exception e) {
            p.a(d, e.getMessage());
        }
    }

    /* JADX INFO: Removed unreachable split cross block B:38:0x0091 */
    /* JADX WARN: Multi-variable type inference failed */
    public String a(Context context) {
        try {
            context.getPackageManager().getPackageInfo(this.a, 0);
        } catch (Exception e) {
            p.a(d, e.getMessage());
        }
        String packageName = context.getPackageName();
        a(packageName, context);
        Intent intent = new Intent();
        intent.setClassName("com.mdid.msa", "com.mdid.msa.service.MsaIdService");
        intent.setAction("com.bun.msa.action.bindto.service");
        intent.putExtra("com.bun.msa.param.pkgname", packageName);
        boolean zBindService = context.bindService(intent, this.c, 1);
        String strB = "";
        try {
            try {
                if (zBindService) {
                    try {
                        IBinder iBinderPoll = this.b.poll(1L, TimeUnit.SECONDS);
                        if (iBinderPoll != null) {
                            strB = new com.xiaomi.onetrack.util.oaid.a.g.a.C0028a(iBinderPoll).b();
                            context.unbindService(this.c);
                            context = context;
                        } else {
                            try {
                                context.unbindService(this.c);
                            } catch (Exception e2) {
                                p.a(d, e2.getMessage());
                            }
                            return "";
                        }
                    } catch (Exception e3) {
                        p.a(d, e3.getMessage());
                        context.unbindService(this.c);
                        context = context;
                    }
                }
            } catch (Exception e4) {
                String message = e4.getMessage();
                p.a(d, message);
                context = message;
            }
            return strB;
        } catch (Throwable th) {
            try {
                context.unbindService(this.c);
            } catch (Exception e5) {
                p.a(d, e5.getMessage());
            }
            throw th;
        }
    }
}
