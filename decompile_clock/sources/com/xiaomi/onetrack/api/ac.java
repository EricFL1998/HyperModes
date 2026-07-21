package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes2.dex */
class ac implements Runnable {
    final /* synthetic */ m a;

    ac(m mVar) {
        this.a = mVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.a.b.a(g.i, h.a(this.a.f, this.a.h, this.a.i, this.a.j));
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "cta event error: " + e.toString());
        }
    }
}
