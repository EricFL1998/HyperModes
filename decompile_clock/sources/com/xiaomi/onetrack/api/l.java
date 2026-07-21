package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes2.dex */
class l implements Runnable {
    final /* synthetic */ Thread a;
    final /* synthetic */ Throwable b;
    final /* synthetic */ k c;

    l(k kVar, Thread thread, Throwable th) {
        this.c = kVar;
        this.a = thread;
        this.b = th;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        this.c.a(this.a, this.b);
    }
}
