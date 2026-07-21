package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes2.dex */
class aq implements Runnable {
    final /* synthetic */ ao a;

    aq(ao aoVar) {
        this.a = aoVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (com.xiaomi.onetrack.c.i.b()) {
            this.a.b();
        }
    }
}
