package com.xiaomi.onetrack.c;

import java.util.List;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
final class k implements Runnable {
    final /* synthetic */ com.xiaomi.onetrack.api.j a;

    k(com.xiaomi.onetrack.api.j jVar) {
        this.a = jVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            List<JSONObject> listC = i.c();
            if (listC != null && listC.size() > 0) {
                for (JSONObject jSONObject : listC) {
                    this.a.a(jSONObject.optString("eventName"), jSONObject.optString("data"));
                }
            }
            i.c(true);
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("NetworkAccessManager", "cta event error: " + e.toString());
        }
        boolean unused = i.l = false;
    }
}
