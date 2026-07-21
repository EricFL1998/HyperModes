package com.xiaomi.onetrack.c;

import com.xiaomi.onetrack.api.ar;
import java.util.List;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
class n implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ l b;

    n(l lVar, String str) {
        this.b = lVar;
        this.a = str;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            List<JSONObject> listB = l.b(this.a);
            if (listB != null && listB.size() > 0) {
                for (JSONObject jSONObject : listB) {
                    ar.a().a(this.a, jSONObject.optString("eventName"), jSONObject.optString("data"));
                }
            }
            this.b.a(this.a);
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("SystemImpCacheManager", "trackSystemImpCache event error: " + e.toString());
        }
    }
}
