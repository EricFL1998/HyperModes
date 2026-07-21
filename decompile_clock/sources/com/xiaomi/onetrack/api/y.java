package com.xiaomi.onetrack.api;

import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
class y implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ Map b;
    final /* synthetic */ String c;
    final /* synthetic */ m d;

    y(m mVar, String str, Map map, String str2) {
        this.d = mVar;
        this.a = str;
        this.b = map;
        this.c = str2;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (this.d.d(this.a)) {
                return;
            }
            this.d.b.a(this.a, h.a(this.c, this.a, com.xiaomi.onetrack.util.r.a((Map<String, Object>) this.b, true), this.d.f, this.d.h, this.d.f(this.a), this.d.i, this.d.j));
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "track json error:" + e.toString());
        }
    }
}
