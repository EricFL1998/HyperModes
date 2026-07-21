package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes2.dex */
class an implements Runnable {
    final /* synthetic */ int a;
    final /* synthetic */ am b;

    an(am amVar, int i) {
        this.b = amVar;
        this.a = i;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (com.xiaomi.onetrack.c.i.b() && this.a == 2) {
            com.xiaomi.onetrack.c.s.a().a(0, true);
            com.xiaomi.onetrack.c.s.a().a(1, true);
            com.xiaomi.onetrack.a.c.b.a().b();
        }
    }
}
