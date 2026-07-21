package com.xiaomi.onetrack.api;

import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
class ah implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ m b;

    ah(m mVar, String str) {
        this.b = mVar;
        this.a = str;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            JSONArray jSONArray = new JSONArray(this.a);
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i);
                if (jSONObjectOptJSONObject == null || !jSONObjectOptJSONObject.has(h.b) || !jSONObjectOptJSONObject.has(h.a)) {
                    com.xiaomi.onetrack.util.p.a("OneTrackImp", "h5 json is empty or has no \"H\" or \"B\" ");
                } else {
                    i iVar = new i(jSONObjectOptJSONObject.optJSONObject(h.b));
                    if (!this.b.e(iVar.a())) {
                        this.b.b.a(iVar.a(), h.a(iVar, jSONObjectOptJSONObject.optJSONObject(h.a), this.b.f, this.b.h, this.b.f(iVar.a()), this.b.i, this.b.j));
                    }
                }
            }
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "trackEventFromH5 error: " + e.toString());
        }
    }
}
