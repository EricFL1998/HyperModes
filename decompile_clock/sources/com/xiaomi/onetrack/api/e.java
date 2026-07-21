package com.xiaomi.onetrack.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

/* JADX INFO: loaded from: classes2.dex */
class e extends BroadcastReceiver {
    final /* synthetic */ c a;

    e(c cVar) {
        this.a = cVar;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (this.a.d != null) {
            Intent intent2 = new Intent();
            intent2.putExtras(intent);
            Message messageObtain = Message.obtain();
            messageObtain.what = 10;
            messageObtain.obj = intent2;
            this.a.d.sendMessage(messageObtain);
            com.xiaomi.onetrack.util.p.a("BroadcastManager", "netReceiver");
        }
    }
}
