package com.xiaomi.onetrack.api;

import android.text.TextUtils;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
class w implements Runnable {
    final /* synthetic */ Map a;
    final /* synthetic */ m b;

    w(m mVar, Map map) {
        this.b = mVar;
        this.a = map;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            JSONObject jSONObjectA = com.xiaomi.onetrack.util.r.a((Map<String, Object>) this.a, true);
            String strA = com.xiaomi.onetrack.util.k.a(com.xiaomi.onetrack.util.r.a(this.b.f));
            com.xiaomi.onetrack.util.k.a(com.xiaomi.onetrack.util.r.a(this.b.f), com.xiaomi.onetrack.util.r.a(jSONObjectA, !TextUtils.isEmpty(strA) ? new JSONObject(strA) : null).toString());
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", " " + e.toString());
        }
    }
}
