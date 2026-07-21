package com.xiaomi.onetrack.api;

/* JADX INFO: loaded from: classes2.dex */
class ai implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ String b;
    final /* synthetic */ String c;
    final /* synthetic */ String d;
    final /* synthetic */ String e;
    final /* synthetic */ long f;
    final /* synthetic */ m g;

    ai(m mVar, String str, String str2, String str3, String str4, String str5, long j) {
        this.g = mVar;
        this.a = str;
        this.b = str2;
        this.c = str3;
        this.d = str4;
        this.e = str5;
        this.f = j;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        try {
            this.g.b.a("onetrack_bug_report", h.a(this.a, this.b, this.c, this.d, this.e, this.f, this.g.f, this.g.h, this.g.f("onetrack_bug_report"), this.g.i, this.g.j));
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("OneTrackImp", "trackException error: " + e.toString());
        }
    }
}
