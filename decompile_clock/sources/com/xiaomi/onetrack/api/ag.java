package com.xiaomi.onetrack.api;

import android.text.TextUtils;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
class ag implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ Map b;
    final /* synthetic */ List c;
    final /* synthetic */ m d;

    ag(m mVar, String str, Map map, List list) {
        this.d = mVar;
        this.a = str;
        this.b = map;
        this.c = list;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            if (TextUtils.isEmpty(this.d.f.getAdEventAppId())) {
                com.xiaomi.onetrack.util.p.a("OneTrackImp", "adEventAppId is null,Please configure,event name:" + this.a);
                return;
            }
            if (this.d.d(this.a)) {
                return;
            }
            this.d.b.a(this.a, h.a(this.a, com.xiaomi.onetrack.util.r.a((Map<String, Object>) this.b, true), this.d.f, this.d.h, this.d.f(this.a), this.d.i, com.xiaomi.onetrack.util.r.a(this.c), this.d.j));
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "track map error: " + e.toString());
        }
    }
}
