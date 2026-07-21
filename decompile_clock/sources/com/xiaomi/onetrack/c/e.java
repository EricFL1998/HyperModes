package com.xiaomi.onetrack.c;

import android.content.Intent;

/* JADX INFO: loaded from: classes2.dex */
class e implements Runnable {
    final /* synthetic */ Intent a;
    final /* synthetic */ d b;

    e(d dVar, Intent intent) {
        this.b = dVar;
        this.a = intent;
    }

    @Override // java.lang.Runnable
    public void run() {
        String action = this.a.getAction();
        if (action.equals("android.intent.action.SCREEN_OFF") || action.equals("android.intent.action.SCREEN_ON")) {
            com.xiaomi.onetrack.util.p.a("EventManager", "screen on/off");
            s.a().a(action.equals("android.intent.action.SCREEN_ON") ? 0 : 2, false);
            com.xiaomi.onetrack.a.c.b.a().b();
        }
    }
}
