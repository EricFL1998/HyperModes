package com.xiaomi.onetrack.a.c;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.xiaomi.onetrack.b.n;
import com.xiaomi.onetrack.util.p;

/* JADX INFO: loaded from: classes2.dex */
public class b {
    private static final String a = "AdMonitorUploadTimer";
    private static volatile b b;
    private a c;

    private b() {
        HandlerThread handlerThread = new HandlerThread("onetrack_ad_monitor_uploader");
        handlerThread.start();
        this.c = new a(handlerThread.getLooper());
    }

    public static b a() {
        if (b == null) {
            synchronized (b.class) {
                if (b == null) {
                    b = new b();
                }
            }
        }
        return b;
    }

    public void b() {
        this.c.a(0);
    }

    public void a(boolean z) {
        a aVar;
        if (!z || (aVar = this.c) == null) {
            return;
        }
        aVar.a(0, 1000L);
    }

    private static final class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            p.a(b.a, "AdMonitorUploadTimer.handleMessage, msg.what=" + message.what);
            c.a();
        }

        public void a(int i) {
            if (!hasMessages(i)) {
                long jA = n.a(i);
                p.a(b.a, "will check prio=" + i + ", delay=" + jA);
                a(i, jA);
                return;
            }
            p.a(b.a, "has message\u3000prio=" + i);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void a(int i, long j) {
            removeMessages(i);
            p.a(b.a, "will post msg, prio=" + i + ", delay=" + j);
            sendEmptyMessageDelayed(i, j);
        }
    }
}
