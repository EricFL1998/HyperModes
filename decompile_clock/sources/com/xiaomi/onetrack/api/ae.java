package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes2.dex */
class ae implements Runnable {
    final /* synthetic */ boolean a;
    final /* synthetic */ m b;

    ae(m mVar, boolean z) {
        this.b = mVar;
        this.a = z;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.b.i.a(this.a);
        com.xiaomi.onetrack.util.aa.a(com.xiaomi.onetrack.util.r.a(this.b.f), this.a);
    }
}
