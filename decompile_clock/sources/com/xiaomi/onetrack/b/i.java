package com.xiaomi.onetrack.b;

import com.xiaomi.onetrack.util.p;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
class i implements Runnable {
    final /* synthetic */ ArrayList a;
    final /* synthetic */ h b;

    i(h hVar, ArrayList arrayList) {
        this.b = hVar;
        this.a = arrayList;
    }

    @Override // java.lang.Runnable
    public void run() throws Throwable {
        if (p.a) {
            p.a("ConfigDbManager", "update: " + this.a);
        }
        this.b.b((ArrayList<l>) this.a);
    }
}
