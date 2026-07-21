package com.xiaomi.onetrack.c;

/* JADX INFO: loaded from: classes2.dex */
class m implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ String b;
    final /* synthetic */ String c;
    final /* synthetic */ l d;

    m(l lVar, String str, String str2, String str3) {
        this.d = lVar;
        this.a = str;
        this.b = str2;
        this.c = str3;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.d.b(this.a, this.b, this.c);
    }
}
