package com.xiaomi.onetrack.c;

/* JADX INFO: loaded from: classes2.dex */
class f implements Runnable {
    final /* synthetic */ com.xiaomi.onetrack.f.b a;
    final /* synthetic */ c b;

    f(c cVar, com.xiaomi.onetrack.f.b bVar) {
        this.b = cVar;
        this.a = bVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.b.b(this.a);
            com.xiaomi.onetrack.a.a.a().a(this.a);
            com.xiaomi.onetrack.util.p.a("EventManager", "addEvent: " + this.a.e() + "data:" + this.a.g().toString());
            s.a().a(this.a.f(), false);
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("EventManager", "EventManager.addEvent exception: ", e);
        }
    }
}
