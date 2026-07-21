package com.xiaomi.onetrack.c;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* JADX INFO: loaded from: classes2.dex */
final class d extends BroadcastReceiver {
    d() {
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        a.a(new e(this, intent));
    }
}
