package com.xiaomi.onetrack.api;

import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
class aj implements Runnable {
    final /* synthetic */ Map a;
    final /* synthetic */ m b;

    aj(m mVar, Map map) {
        this.b = mVar;
        this.a = map;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            this.b.b.a(g.c, h.a(com.xiaomi.onetrack.util.r.a((Map<String, Object>) this.a, false), this.b.f, this.b.h, this.b.f(g.c), this.b.i, this.b.j));
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "setUserProfile map error:" + e.toString());
        }
    }
}
