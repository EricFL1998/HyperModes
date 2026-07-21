package com.xiaomi.onetrack.api;

import com.xiaomi.onetrack.OneTrack;

/* JADX INFO: loaded from: classes2.dex */
class ap implements Runnable {
    final /* synthetic */ ao a;

    ap(ao aoVar) {
        this.a = aoVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (OneTrack.isRestrictGetNetworkInfo()) {
            c.a().f();
        }
    }
}
