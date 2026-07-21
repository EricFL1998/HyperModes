package com.xiaomi.onetrack.api;

import android.text.TextUtils;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
class v implements Runnable {
    final /* synthetic */ boolean a;
    final /* synthetic */ m b;

    v(m mVar, boolean z) {
        this.b = mVar;
        this.a = z;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            String strZ = com.xiaomi.onetrack.util.aa.z();
            if (TextUtils.isEmpty(strZ)) {
                return;
            }
            JSONObject jSONObject = new JSONObject(strZ);
            this.b.b.a(g.g, jSONObject.put(h.a, jSONObject.optJSONObject(h.a).put(g.u, this.a)).toString());
            if (com.xiaomi.onetrack.util.p.a) {
                com.xiaomi.onetrack.util.p.a("OneTrackImp", "trackPageEndAuto");
            }
            com.xiaomi.onetrack.util.aa.i("");
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "trackPageEndAuto error:" + e.toString());
        }
    }
}
