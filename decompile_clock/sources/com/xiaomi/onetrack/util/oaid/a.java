package com.xiaomi.onetrack.util.oaid;

import android.content.Context;
import com.xiaomi.onetrack.util.n;
import com.xiaomi.onetrack.util.oaid.helpers.b;
import com.xiaomi.onetrack.util.oaid.helpers.g;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;
import com.xiaomi.onetrack.util.w;

/* JADX INFO: loaded from: classes2.dex */
public class a {
    private static final String a = "a";
    private static volatile a b;
    private volatile boolean c = false;
    private volatile String d = "";
    private boolean e = false;
    private final int f = 3;
    private volatile int g = 0;

    public static a a() {
        if (b == null) {
            synchronized (a.class) {
                if (b == null) {
                    b = new a();
                }
            }
        }
        return b;
    }

    private boolean b() {
        return this.g >= 3;
    }

    public void a(boolean z) {
        this.e = z;
        p.a(a, "setCloseOaidDependMsaSDK：" + this.e);
    }

    public String a(Context context) {
        String strA;
        synchronized (this.d) {
            if (w.a()) {
                if (p.a) {
                    throw new IllegalStateException("Don't use it on the main thread");
                }
                p.b(a, "getOaid() throw exception : Don't use it on the main thread");
                return "";
            }
            if (this.d != null && !this.d.equals("")) {
                return this.d;
            }
            if (b()) {
                p.a(a, "isNotAllowedGetOaid");
                return this.d;
            }
            if (q.a()) {
                this.d = n.b(context);
                this.g++;
                return this.d;
            }
            if (!this.e && (strA = new g().a(context)) != null && !strA.equals("")) {
                this.d = strA;
                this.g++;
                return strA;
            }
            String strA2 = new b().a(context);
            if (strA2 != null && !strA2.equals("")) {
                this.d = strA2;
                this.g++;
                return strA2;
            }
            this.g++;
            return this.d;
        }
    }
}
