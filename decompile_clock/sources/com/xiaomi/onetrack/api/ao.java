package com.xiaomi.onetrack.api;

import android.os.Process;
import android.text.TextUtils;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class ao implements ar.a, j {
    private static final String a = "OneTrackSystemImp";
    private static final int b = 102400;
    private static final int c = 512000;
    private static final int d = 2;
    private final ConcurrentHashMap<String, String> e = new ConcurrentHashMap<>();
    private Configuration f;
    private ar g;
    private com.xiaomi.onetrack.util.v h;

    public ao(Configuration configuration, com.xiaomi.onetrack.util.v vVar) {
        this.f = configuration;
        this.h = vVar;
        ar arVarA = ar.a();
        this.g = arVarA;
        arVarA.a(this);
        com.xiaomi.onetrack.util.i.a(new ap(this));
    }

    @Override // com.xiaomi.onetrack.api.j
    public void a(String str, String str2) {
        boolean zA = a(str2);
        com.xiaomi.onetrack.util.v vVar = this.h;
        if (vVar != null && !vVar.a(str) && !zA) {
            com.xiaomi.onetrack.util.p.a(a, "The privacy policy is not permitted, and the event is not basic or recommend event or custom dau event, skip it.");
            return;
        }
        if (a(str, str2, zA)) {
            if (!com.xiaomi.onetrack.c.i.b()) {
                if (!g.i.equalsIgnoreCase(str)) {
                    com.xiaomi.onetrack.c.i.a(str, str2);
                    return;
                }
            } else {
                com.xiaomi.onetrack.c.i.a(this);
            }
            if (com.xiaomi.onetrack.util.p.a) {
                com.xiaomi.onetrack.util.p.a(a, "track name:" + str + " data :" + str2 + " tid" + Process.myTid());
            }
            if (this.g.a(str, str2, this.f, zA)) {
                return;
            }
            String appId = this.f.getAppId();
            if (zA) {
                appId = this.f.getAdEventAppId();
            }
            com.xiaomi.onetrack.c.l.a().a(appId, str, str2);
            if (com.xiaomi.onetrack.util.p.a) {
                com.xiaomi.onetrack.util.p.a(a, "track mIOneTrackService is null! SystemImpCacheManager cache data:" + str2);
            }
        }
    }

    private boolean a(String str, String str2, boolean z) {
        if (OneTrack.isDisable()) {
            return false;
        }
        if (str != null && str.equals("onetrack_bug_report")) {
            return true;
        }
        if (z) {
            if (str2 != null && str2.length() > c) {
                com.xiaomi.onetrack.util.p.a(a, "ad Event size exceed limitation!");
                return false;
            }
        } else if (str2 != null && str2.length() * 2 > b) {
            com.xiaomi.onetrack.util.p.a(a, "Event size exceed limitation!");
            return false;
        }
        return true;
    }

    @Override // com.xiaomi.onetrack.api.j
    public void a(int i) {
        this.g.a(i);
    }

    @Override // com.xiaomi.onetrack.api.j
    public void a(boolean z) {
        com.xiaomi.onetrack.c.i.a(this);
    }

    @Override // com.xiaomi.onetrack.api.ar.a
    public void a() {
        com.xiaomi.onetrack.util.i.a(new aq(this));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b() {
        try {
            com.xiaomi.onetrack.c.l.a().c(this.f.getAppId());
            if (TextUtils.isEmpty(this.f.getAdEventAppId())) {
                return;
            }
            com.xiaomi.onetrack.c.l.a().c(this.f.getAdEventAppId());
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.a(a, "trackCachedEvents: " + e.toString());
        }
    }

    private boolean a(String str) {
        try {
            return new JSONObject(str).optJSONObject(h.b).optBoolean(com.xiaomi.onetrack.f.b.C0023b.F, false);
        } catch (Throwable unused) {
            com.xiaomi.onetrack.util.p.a(a, "");
            return false;
        }
    }
}
