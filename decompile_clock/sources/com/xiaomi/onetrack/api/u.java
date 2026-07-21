package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes2.dex */
class u implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ long b;
    final /* synthetic */ m c;

    u(m mVar, String str, long j) {
        this.c = mVar;
        this.a = str;
        this.b = j;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (this.c.f.isAutoTrackActivityAction()) {
                com.xiaomi.onetrack.util.aa.i(h.a(this.a, g.g, this.b, this.c.f, this.c.h, this.c.f(g.g), this.c.i, this.c.j));
            } else {
                com.xiaomi.onetrack.util.p.a("OneTrackImp", "config.autoTrackActivityAction is false, ignore onetrack_pa pause event");
            }
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "savePageEndData error:" + e.toString());
        }
    }
}
