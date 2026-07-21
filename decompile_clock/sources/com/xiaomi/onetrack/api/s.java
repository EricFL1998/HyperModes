package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes2.dex */
class s implements Runnable {
    final /* synthetic */ m a;

    s(m mVar) {
        this.a = mVar;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (com.xiaomi.onetrack.util.ac.d(com.xiaomi.onetrack.util.aa.t())) {
                return;
            }
            com.xiaomi.onetrack.util.aa.m(System.currentTimeMillis());
            this.a.b.a("onetrack_dau", h.a(this.a.f, this.a.h, this.a.f("onetrack_dau"), this.a.i, this.a.j));
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "trackDau error  e:" + e.toString());
        }
    }
}
