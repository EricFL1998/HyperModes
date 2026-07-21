package com.xiaomi.onetrack.b;

import android.text.TextUtils;

/* JADX INFO: loaded from: classes2.dex */
class b implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ a b;

    b(a aVar, String str) {
        this.b = aVar;
        this.a = str;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (TextUtils.isEmpty(this.a)) {
            return;
        }
        this.b.c(this.a);
    }
}
