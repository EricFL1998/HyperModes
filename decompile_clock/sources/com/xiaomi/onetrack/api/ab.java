package com.xiaomi.onetrack.api;

import com.xiaomi.onetrack.ServiceQualityEvent;

/* JADX INFO: loaded from: classes2.dex */
class ab implements Runnable {
    final /* synthetic */ ServiceQualityEvent a;
    final /* synthetic */ m b;

    ab(m mVar, ServiceQualityEvent serviceQualityEvent) {
        this.b = mVar;
        this.a = serviceQualityEvent;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.b.b.a(g.e, h.a(this.a, this.b.f, this.b.h, this.b.i, this.b.j));
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "trackNetAvailableEvent error: " + e.toString());
        }
    }
}
