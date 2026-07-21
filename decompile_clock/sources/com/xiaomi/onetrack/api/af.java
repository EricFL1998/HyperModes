package com.xiaomi.onetrack.api;

import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
class af implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ Map b;
    final /* synthetic */ m c;

    af(m mVar, String str, Map map) {
        this.c = mVar;
        this.a = str;
        this.b = map;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (this.c.d(this.a)) {
                return;
            }
            this.c.b.a(this.a, h.a(this.a, com.xiaomi.onetrack.util.r.a((Map<String, Object>) this.b, true), this.c.f, this.c.h, this.c.f(this.a), this.c.i, this.c.j));
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "track map error: " + e.toString());
        }
    }
}
