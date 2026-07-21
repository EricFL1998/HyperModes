package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes2.dex */
class t implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ boolean b;
    final /* synthetic */ m c;

    t(m mVar, String str, boolean z) {
        this.c = mVar;
        this.a = str;
        this.b = z;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (this.c.f.isAutoTrackActivityAction()) {
                this.c.b.a(g.g, h.a(this.a, g.g, this.c.f, this.c.h, this.c.f(g.g), this.b, this.c.i, this.c.j));
                if (com.xiaomi.onetrack.util.p.a) {
                    com.xiaomi.onetrack.util.p.a("OneTrackImp", "trackPageStartAuto");
                    return;
                }
                return;
            }
            com.xiaomi.onetrack.util.p.a("OneTrackImp", "config.autoTrackActivityAction is false, ignore onetrack_pa resume event");
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "auto trackPageStartAuto error: " + e.toString());
        }
    }
}
