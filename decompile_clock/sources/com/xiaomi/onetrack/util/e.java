package com.xiaomi.onetrack.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/* JADX INFO: loaded from: classes2.dex */
class e extends Handler {
    final /* synthetic */ d a;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    e(d dVar, Looper looper) {
        super(looper);
        this.a = dVar;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (message.what != 100) {
            return;
        }
        this.a.c(message.getData().getString("hint"));
    }
}
