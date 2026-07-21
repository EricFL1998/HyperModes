package com.xiaomi.onetrack.api;

import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
class o implements Runnable {
    final /* synthetic */ Map a;
    final /* synthetic */ m b;

    o(m mVar, Map map) {
        this.b = mVar;
        this.a = map;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            this.b.b.a(g.d, h.b(new JSONObject(this.a), this.b.f, this.b.h, this.b.f(g.d), this.b.i, this.b.j));
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "userProfileIncrement map error:" + e.toString());
        }
    }
}
