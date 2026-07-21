package com.xiaomi.onetrack.f;

import android.content.Context;

/* JADX INFO: loaded from: classes2.dex */
class h implements Runnable {
    final /* synthetic */ Context a;
    final /* synthetic */ g b;

    h(g gVar, Context context) {
        this.b = gVar;
        this.a = context;
    }

    @Override // java.lang.Runnable
    public void run() {
        com.xiaomi.onetrack.c.a.a();
        com.xiaomi.onetrack.api.c.a().d();
        com.xiaomi.onetrack.api.c.a().f();
        com.xiaomi.onetrack.c.c.a(this.a);
    }
}
