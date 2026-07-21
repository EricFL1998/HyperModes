package com.xiaomi.onetrack.api;

import android.text.TextUtils;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
class z implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ m b;

    z(m mVar, String str) {
        this.b = mVar;
        this.a = str;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            String strA = com.xiaomi.onetrack.util.k.a(com.xiaomi.onetrack.util.r.a(this.b.f));
            if (TextUtils.isEmpty(strA)) {
                return;
            }
            JSONObject jSONObject = new JSONObject(strA);
            jSONObject.remove(this.a);
            com.xiaomi.onetrack.util.k.a(com.xiaomi.onetrack.util.r.a(this.b.f), jSONObject.toString());
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "removeCommonProperty error:" + e.toString());
        }
    }
}
