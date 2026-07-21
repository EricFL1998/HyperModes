package com.xiaomi.onetrack.api;

import com.xiaomi.onetrack.OneTrack;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
class al implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ OneTrack.UserIdType b;
    final /* synthetic */ boolean c;
    final /* synthetic */ Map d;
    final /* synthetic */ m e;

    al(m mVar, String str, OneTrack.UserIdType userIdType, boolean z, Map map) {
        this.e = mVar;
        this.a = str;
        this.b = userIdType;
        this.c = z;
        this.d = map;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            com.xiaomi.onetrack.util.aa.g(this.a);
            com.xiaomi.onetrack.util.aa.h(this.b.getUserIdType());
            if (this.c) {
                return;
            }
            JSONObject jSONObjectA = com.xiaomi.onetrack.util.r.a((Map<String, Object>) this.d, false);
            if (!this.e.j) {
                jSONObjectA.put("uid", this.a);
                jSONObjectA.put("uid_type", this.b.getUserIdType());
            }
            this.e.b.a("ot_login", h.c(jSONObjectA, this.e.f, this.e.h, this.e.f("ot_login"), this.e.i, this.e.j));
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "login error:" + e.toString());
        }
    }
}
