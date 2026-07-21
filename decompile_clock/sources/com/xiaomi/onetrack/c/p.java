package com.xiaomi.onetrack.c;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes2.dex */
public class p extends Handler {
    private static final String a = "UploadTimer";
    private static final int b = 5000;
    private static final int c = 15000;
    private static final int d = 1200000;
    private final int e;
    private final int f;
    private final int g;
    private int h;
    private AtomicBoolean i;

    public p(Looper looper) {
        super(looper);
        this.e = 1000;
        this.f = 10000;
        this.g = d;
        this.h = 10000;
        this.i = new AtomicBoolean(false);
    }

    public void a(int i, boolean z) {
        if (hasMessages(1000)) {
            com.xiaomi.onetrack.util.p.a(a, "in retry mode, return, prio=" + i);
            return;
        }
        if (z) {
            removeMessages(i);
        }
        if (hasMessages(i)) {
            return;
        }
        long jA = z ? 0L : com.xiaomi.onetrack.b.n.a(i);
        com.xiaomi.onetrack.util.p.a(a, "will check prio=" + i + ", delay=" + jA);
        a(i, jA);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        super.handleMessage(message);
        if (!com.xiaomi.onetrack.b.n.a() || !com.xiaomi.onetrack.g.c.a() || com.xiaomi.onetrack.b.n.b()) {
            com.xiaomi.onetrack.util.p.a(a, "不用处理消息, available=" + com.xiaomi.onetrack.b.n.a() + ", 是否有网=" + com.xiaomi.onetrack.g.c.a() + ", 数据库是否为空=" + com.xiaomi.onetrack.b.n.b());
            return;
        }
        if (message.what == 1000) {
            b();
            return;
        }
        int i = message.what;
        boolean zA = s.a().a(i);
        com.xiaomi.onetrack.util.p.a(a, "handleCheckUpload ret=" + zA + ", prio=" + i);
        if (zA) {
            return;
        }
        com.xiaomi.onetrack.util.p.a(a, "handleCheckUpload failed, will check if need to send retry msg");
        if (hasMessages(1000)) {
            return;
        }
        sendEmptyMessageDelayed(1000, this.h);
        com.xiaomi.onetrack.util.p.a(a, "fire retry timer after " + this.h);
    }

    private void b() {
        if (!s.a().a(2)) {
            removeMessages(1000);
            int i = this.h * 2;
            this.h = i;
            if (i > d) {
                this.h = d;
            }
            com.xiaomi.onetrack.util.p.a(a, "will restart retry msg after " + this.h);
            sendEmptyMessageDelayed(1000, this.h);
            return;
        }
        this.h = 10000;
        com.xiaomi.onetrack.util.p.a(a, "retry success");
    }

    private void a(int i, long j) {
        removeMessages(i);
        com.xiaomi.onetrack.util.p.a(a, "will post msg, prio=" + i + ", delay=" + j);
        sendEmptyMessageDelayed(i, j);
    }

    public void a(boolean z) {
        a.a(new q(this, z));
    }

    public void a() {
        com.xiaomi.onetrack.util.i.a(new r(this));
    }
}
