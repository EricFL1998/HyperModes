package com.xiaomi.onetrack.b;

/* JADX INFO: loaded from: classes2.dex */
class j implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ h b;

    j(h hVar, String str) {
        this.b = hVar;
        this.a = str;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.b.b.getWritableDatabase().delete(g.b, "app_id=?", new String[]{this.a});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
