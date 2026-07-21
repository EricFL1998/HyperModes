package com.xiaomi.onetrack.util;

import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import miuix.navigation.SplitLayout;

/* JADX INFO: loaded from: classes2.dex */
public class v {
    private static final String a = "custom_open";
    private static final String b = "custom_close";
    private static final String c = "exprience_open";
    private static final String d = "exprience_close";
    private static final String e = "PrivacyManager";
    private static final long k = 900000;
    private OneTrack.IEventHook f;
    private Configuration g;
    private boolean h;
    private boolean i;
    private long j = 0;

    public v(Configuration configuration) {
        this.g = configuration;
        this.h = aa.k(r.a(configuration));
    }

    public boolean a(String str) {
        boolean zB;
        boolean zIsUseCustomPrivacyPolicy = this.g.isUseCustomPrivacyPolicy();
        String str2 = SplitLayout.TAG_OPEN;
        if (zIsUseCustomPrivacyPolicy) {
            if (!this.h) {
                str2 = SplitLayout.TAG_CLOSE;
            }
            p.a(e, "use custom privacy policy, the policy is ".concat(str2));
            zB = this.h;
        } else {
            zB = b();
            if (!zB) {
                str2 = SplitLayout.TAG_CLOSE;
            }
            p.a(e, "use system experience plan, the policy is ".concat(str2));
        }
        if (zB) {
            return zB;
        }
        boolean zB2 = b(str);
        boolean zC = c(str);
        boolean zD = d(str);
        p.a(e, "This event " + str + (zB2 ? " is " : " is not ") + "basic event and " + (zC ? "is" : "is not") + " recommend event and " + (zD ? "is" : "is not") + " custom dau event");
        return zB2 || zC || zD;
    }

    private boolean b(String str) {
        return "onetrack_dau".equals(str) || com.xiaomi.onetrack.api.g.g.equals(str);
    }

    private boolean c(String str) {
        OneTrack.IEventHook iEventHook = this.f;
        return iEventHook != null && iEventHook.isRecommendEvent(str);
    }

    private boolean d(String str) {
        OneTrack.IEventHook iEventHook = this.f;
        return iEventHook != null && iEventHook.isCustomDauEvent(str);
    }

    public void a(OneTrack.IEventHook iEventHook) {
        this.f = iEventHook;
    }

    public void a(boolean z) {
        this.h = z;
    }

    public String a() {
        if (this.g.isUseCustomPrivacyPolicy()) {
            if (this.h) {
                return a;
            }
            return b;
        }
        if (b()) {
            return c;
        }
        return d;
    }

    private boolean b() {
        if (Math.abs(System.currentTimeMillis() - this.j) > 900000) {
            this.j = System.currentTimeMillis();
            this.i = q.a(com.xiaomi.onetrack.f.a.b());
        }
        return this.i;
    }
}
