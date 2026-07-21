package com.xiaomi.onetrack.api;

import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
class ak implements Runnable {
    final /* synthetic */ Object a;
    final /* synthetic */ String b;
    final /* synthetic */ m c;

    ak(m mVar, Object obj, String str) {
        this.c = mVar;
        this.a = obj;
        this.b = str;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (com.xiaomi.onetrack.util.r.b(this.a)) {
                this.c.b.a(g.c, h.a(new JSONObject().put(this.b, this.a), this.c.f, this.c.h, this.c.f(g.c), this.c.i, this.c.j));
                return;
            }
            com.xiaomi.onetrack.util.r.a("OneTrackImp", this.b);
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "setUserProfile single error:" + e.toString());
        }
    }
}
