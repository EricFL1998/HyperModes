package com.xiaomi.onetrack.c;

/* JADX INFO: loaded from: classes2.dex */
class q implements Runnable {
    final /* synthetic */ boolean a;
    final /* synthetic */ p b;

    q(p pVar, boolean z) {
        this.b = pVar;
        this.a = z;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.b.a();
        try {
            if (this.a) {
                int[] iArr = {0, 1, 2};
                for (int i = 0; i < 3; i++) {
                    int i2 = iArr[i];
                    int iA = com.xiaomi.onetrack.b.n.a(i2);
                    if (!this.b.hasMessages(i2)) {
                        this.b.sendEmptyMessageDelayed(i2, iA);
                    }
                }
            }
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.a("UploadTimer", "netReceiver error: " + e);
        }
    }
}
