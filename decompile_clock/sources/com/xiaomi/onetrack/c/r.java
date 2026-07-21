package com.xiaomi.onetrack.c;

/* JADX INFO: loaded from: classes2.dex */
class r implements Runnable {
    final /* synthetic */ p a;

    r(p pVar) {
        this.a = pVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (this.a.i.get()) {
            com.xiaomi.onetrack.b.e.b();
        }
        this.a.i.set(true);
    }
}
