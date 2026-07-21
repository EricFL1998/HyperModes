package com.xiaomi.onetrack.api;

import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
class q implements Runnable {
    final /* synthetic */ boolean a;
    final /* synthetic */ Map b;
    final /* synthetic */ m c;

    q(m mVar, boolean z, Map map) {
        this.c = mVar;
        this.a = z;
        this.b = map;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (!this.a) {
                JSONObject jSONObjectA = com.xiaomi.onetrack.util.r.a((Map<String, Object>) this.b, false);
                if (!this.c.j) {
                    String strU = com.xiaomi.onetrack.util.aa.u();
                    String strW = com.xiaomi.onetrack.util.aa.w();
                    jSONObjectA.put("uid", strU);
                    jSONObjectA.put("uid_type", strW);
                }
                this.c.b.a("ot_logout", h.d(jSONObjectA, this.c.f, this.c.h, this.c.f("ot_logout"), this.c.i, this.c.j));
            }
            com.xiaomi.onetrack.util.aa.v();
            com.xiaomi.onetrack.util.aa.x();
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "logout error:" + e.toString());
        }
    }
}
