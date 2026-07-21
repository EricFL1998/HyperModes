package com.xiaomi.onetrack.api;

import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
class p implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ Number b;
    final /* synthetic */ m c;

    p(m mVar, String str, Number number) {
        this.c = mVar;
        this.a = str;
        this.b = number;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            this.c.b.a(g.d, h.b(new JSONObject().put(this.a, this.b), this.c.f, this.c.h, this.c.f(g.d), this.c.i, this.c.j));
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "userProfileIncrement single error:" + e.toString());
        }
    }
}
