package com.xiaomi.onetrack.api;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.xiaomi.onetrack.OneTrack;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes2.dex */
public class c {
    private static final String a = "BroadcastManager";
    private static String b = "onetrack_broadcast_manager";
    private static volatile c c = null;
    private static final int e = 10;
    private static final int f = 100;
    private static final int g = 101;
    private static volatile boolean h = false;
    private static volatile boolean j = false;
    private Handler d;
    private CopyOnWriteArrayList<j> i = new CopyOnWriteArrayList<>();
    private AtomicBoolean k = new AtomicBoolean(false);
    private boolean l = false;
    private boolean m = false;
    private BroadcastReceiver n = new d(this);
    private BroadcastReceiver o = new e(this);

    public static c a() {
        if (c == null) {
            b();
        }
        return c;
    }

    public static void b() {
        if (c == null) {
            synchronized (c.class) {
                if (c == null) {
                    c = new c();
                }
            }
        }
    }

    private c() {
        try {
            HandlerThread handlerThread = new HandlerThread(b);
            handlerThread.start();
            this.d = new a(this, handlerThread.getLooper(), null);
        } catch (Throwable unused) {
        }
    }

    public void c() {
        this.l = true;
    }

    public void d() {
        this.m = true;
    }

    public void a(j jVar) {
        if (this.i.contains(jVar)) {
            return;
        }
        this.i.add(jVar);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(int i) {
        for (j jVar : this.i) {
            if (i == 100) {
                jVar.a(true);
            } else if (i == 101) {
                jVar.a(false);
            }
        }
    }

    public void e() {
        if (h) {
            return;
        }
        h = true;
        try {
            g();
        } catch (Throwable unused) {
            h = false;
        }
    }

    private void g() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        com.xiaomi.onetrack.f.a.b().registerReceiver(this.n, intentFilter);
        Log.d(com.xiaomi.onetrack.util.p.a(a), "register screen receiver");
    }

    private class a extends Handler {
        /* synthetic */ a(c cVar, Looper looper, d dVar) {
            this(looper);
        }

        private a(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            boolean zA;
            if (message == null) {
                return;
            }
            if (message.what == 100 || message.what == 101) {
                try {
                    c.this.a(message.what);
                } catch (Exception e) {
                    com.xiaomi.onetrack.util.p.a(c.a, "screenReceiver exception: ", e);
                }
            }
            if (message.what == 10) {
                if (c.this.k.get()) {
                    try {
                        if (OneTrack.isRestrictGetNetworkInfo()) {
                            zA = com.xiaomi.onetrack.b.n.c();
                            com.xiaomi.onetrack.b.n.b(!zA);
                        } else {
                            zA = com.xiaomi.onetrack.g.c.a();
                            com.xiaomi.onetrack.b.n.b(zA);
                        }
                        com.xiaomi.onetrack.util.p.a(c.a, "Only one of allowed NetworkInfo :" + OneTrack.isRestrictGetNetworkInfo() + " ,network status changed, isNetworkConnected: " + com.xiaomi.onetrack.b.n.c());
                        if (zA) {
                            if (c.this.l) {
                                com.xiaomi.onetrack.a.c.b.a().a(com.xiaomi.onetrack.b.n.c());
                            }
                            if (c.this.m) {
                                com.xiaomi.onetrack.c.s.a().a(com.xiaomi.onetrack.b.n.c());
                            }
                        }
                    } catch (Throwable th) {
                        com.xiaomi.onetrack.util.p.b(c.a, "MESSAGE_BROADCAST_NET_RECEIVER throwable:" + th.getMessage());
                    }
                }
                c.this.k.set(true);
            }
        }
    }

    public void f() {
        if (j) {
            return;
        }
        j = true;
        boolean zB = com.xiaomi.onetrack.g.c.b();
        com.xiaomi.onetrack.util.p.a(a, "Get network status for the first time, isNetworkConnected: " + zB);
        com.xiaomi.onetrack.b.n.b(zB);
        try {
            h();
        } catch (Throwable unused) {
            j = false;
        }
    }

    private void h() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        com.xiaomi.onetrack.f.a.b().registerReceiver(this.o, intentFilter);
        Log.d(com.xiaomi.onetrack.util.p.a(a), "register net receiver");
    }
}
