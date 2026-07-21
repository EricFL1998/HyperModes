package com.xiaomi.onetrack.c;

/* JADX INFO: loaded from: classes2.dex */
final class j implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ String b;

    j(String str, String str2) {
        this.a = str;
        this.b = str2;
    }

    @Override // java.lang.Runnable
    public void run() {
        com.xiaomi.onetrack.api.c.a().e();
        i.c(this.a, this.b);
    }
}
