package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes2.dex */
class aa implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ m b;

    aa(m mVar, String str) {
        this.b = mVar;
        this.a = str;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            com.xiaomi.onetrack.util.o.a().a(this.a);
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "setInstanceId error: " + e.toString());
        }
    }
}
