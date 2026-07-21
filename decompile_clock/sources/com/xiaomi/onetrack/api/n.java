package com.xiaomi.onetrack.api;

import android.text.TextUtils;

/* JADX INFO: loaded from: classes2.dex */
class n implements Runnable {
    final /* synthetic */ m a;

    n(m mVar) {
        this.a = mVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            c.a().a(this.a.b);
            com.xiaomi.onetrack.b.a.a().a(this.a.f.getAppId());
            if (com.xiaomi.onetrack.util.z.b(this.a.f.getAdEventAppId())) {
                c.a().c();
                com.xiaomi.onetrack.b.a.a().a(this.a.f.getAdEventAppId());
            }
            if (com.xiaomi.onetrack.util.aa.B() == 0) {
                com.xiaomi.onetrack.util.aa.n(System.currentTimeMillis());
            }
            if (!TextUtils.isEmpty(this.a.f.getInstanceId())) {
                com.xiaomi.onetrack.util.o.a().a(this.a.f.getInstanceId());
            }
            this.a.k();
            com.xiaomi.onetrack.util.d.a();
            com.xiaomi.onetrack.c.i.c(false);
        } catch (Throwable th) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "init WorkerExecutor execute throwable:" + th.getMessage());
        }
    }
}
