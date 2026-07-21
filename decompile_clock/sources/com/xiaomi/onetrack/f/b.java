package com.xiaomi.onetrack.f;

import android.content.Context;
import android.text.TextUtils;
import com.miui.miwallpaper.MiuiWallpaperManager;
import com.xiaomi.onetrack.BuildConfig;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.api.i;
import com.xiaomi.onetrack.util.DeviceUtil;
import com.xiaomi.onetrack.util.aa;
import com.xiaomi.onetrack.util.ac;
import com.xiaomi.onetrack.util.o;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;
import com.xiaomi.onetrack.util.v;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class b {
    public static final int a = 0;
    public static final int b = 1;
    public static final int c = 2;
    private static final String d = "Event";
    private long e;
    private String f;
    private String g;
    private String h;
    private int i;
    private JSONObject j;
    private long k;

    /* JADX INFO: renamed from: com.xiaomi.onetrack.f.b$b, reason: collision with other inner class name */
    public static class C0023b {
        public static String A = "sdk_mode";
        public static String B = "ot_first_day";
        public static String C = "ot_test_env";
        public static String D = "ot_privacy_policy";
        public static String E = "market_name";
        public static String F = "ot_ad";
        public static String G = "ot_basic_mode";
        public static String H = "ot_ad_monitor";
        public static String I = "ot_hybrid_type";
        public static String a = "event";
        public static String b = "imei";
        public static String c = "oaid";
        public static String d = "sn";
        public static String e = "gaid";
        public static String f = "android_id";
        public static String g = "instance_id";
        public static String h = "mfrs";
        public static String i = "model";
        public static String j = "platform";
        public static String k = "miui";
        public static String l = "build";
        public static String m = "os_ver";
        public static String n = "app_id";
        public static String o = "app_ver";
        public static String p = "pkg";
        public static String q = "channel";
        public static String r = "e_ts";
        public static String s = "tz";
        public static String t = "net";
        public static String u = "region";
        public static String v = "plugin_id";
        public static String w = "sdk_ver";
        public static String x = "uid";
        public static String y = "uid_type";
        public static String z = "sid";
    }

    public b() {
    }

    public long b() {
        return this.e;
    }

    public void a(long j) {
        this.e = j;
    }

    public String c() {
        return this.f;
    }

    public void a(String str) {
        this.f = str;
    }

    public String d() {
        return this.g;
    }

    public void b(String str) {
        this.g = str;
    }

    public String e() {
        return this.h;
    }

    public void c(String str) {
        this.h = str;
    }

    public int f() {
        return this.i;
    }

    public void a(int i) {
        this.i = i;
    }

    public JSONObject g() {
        return this.j;
    }

    public void b(JSONObject jSONObject) {
        this.j = jSONObject;
    }

    public long h() {
        return this.k;
    }

    public void b(long j) {
        this.k = j;
    }

    public boolean i() {
        try {
            JSONObject jSONObject = this.j;
            return (jSONObject == null || !jSONObject.has(com.xiaomi.onetrack.api.h.b) || !this.j.has(com.xiaomi.onetrack.api.h.a) || TextUtils.isEmpty(this.f) || TextUtils.isEmpty(this.g)) ? false : true;
        } catch (Exception e) {
            p.b(d, "check event isValid error, ", e);
            return false;
        }
    }

    private b(a aVar) {
        this.e = aVar.a;
        this.f = aVar.b;
        this.g = aVar.c;
        this.h = aVar.d;
        this.i = aVar.e;
        this.j = aVar.f;
        this.k = aVar.g;
    }

    public static class a {
        private long a;
        private String b;
        private String c;
        private String d;
        private int e;
        private JSONObject f;
        private long g;

        public a a(long j) {
            this.a = this.a;
            return this;
        }

        public a a(String str) {
            this.b = str;
            return this;
        }

        public a b(String str) {
            this.c = str;
            return this;
        }

        public a c(String str) {
            this.d = str;
            return this;
        }

        public a a(int i) {
            this.e = i;
            return this;
        }

        public a a(JSONObject jSONObject) {
            this.f = jSONObject;
            return this;
        }

        public a b(long j) {
            this.g = j;
            return this;
        }

        public b a() {
            return new b(this);
        }
    }

    public static JSONObject a(String str, Configuration configuration, OneTrack.IEventHook iEventHook, v vVar, boolean z, boolean z2) throws JSONException {
        return a(str, configuration, iEventHook, "", vVar, z, z2);
    }

    public static JSONObject a(String str, Configuration configuration, OneTrack.IEventHook iEventHook, String str2, v vVar, boolean z, boolean z2) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        Context contextB = com.xiaomi.onetrack.f.a.b();
        jSONObject.put(C0023b.a, str);
        if (!z) {
            if (q.a() ? q.h() : configuration.isInternational()) {
                if (iEventHook != null && iEventHook.isRecommendEvent(str)) {
                    String strE = DeviceUtil.e(contextB);
                    if (!TextUtils.isEmpty(strE)) {
                        jSONObject.put(C0023b.e, strE);
                    }
                }
            } else {
                jSONObject.put(C0023b.b, DeviceUtil.b(contextB));
                jSONObject.put(C0023b.c, com.xiaomi.onetrack.util.oaid.a.a().a(contextB));
            }
            jSONObject.put(C0023b.g, o.a().b());
            a(jSONObject, configuration, str2);
            a(jSONObject, contextB);
            jSONObject.put(C0023b.z, q.f());
        }
        jSONObject.put(C0023b.h, DeviceUtil.d());
        jSONObject.put(C0023b.i, DeviceUtil.b());
        jSONObject.put(C0023b.j, "Android");
        jSONObject.put(C0023b.k, q.d());
        jSONObject.put(C0023b.l, q.c());
        jSONObject.put(C0023b.m, q.e());
        jSONObject.put(C0023b.o, com.xiaomi.onetrack.f.a.c());
        jSONObject.put(C0023b.r, System.currentTimeMillis());
        jSONObject.put(C0023b.s, q.b());
        jSONObject.put(C0023b.t, com.xiaomi.onetrack.g.c.a(contextB).toString());
        String strI = q.i();
        com.xiaomi.onetrack.b.a.a().d(strI);
        jSONObject.put(C0023b.u, strI);
        jSONObject.put(C0023b.w, BuildConfig.SDK_VERSION);
        if (z2) {
            jSONObject.put(C0023b.n, configuration.getAdEventAppId());
        } else {
            jSONObject.put(C0023b.n, configuration.getAppId());
        }
        jSONObject.put(C0023b.F, z2);
        jSONObject.put(C0023b.p, com.xiaomi.onetrack.f.a.e());
        jSONObject.put(C0023b.q, !TextUtils.isEmpty(configuration.getChannel()) ? configuration.getChannel() : MiuiWallpaperManager.MI_WALLPAPER_TYPE_DEFAULT);
        jSONObject.put(C0023b.A, (configuration.getMode() != null ? configuration.getMode() : OneTrack.Mode.APP).getType());
        jSONObject.put(C0023b.B, ac.d(aa.B()));
        if (p.c) {
            jSONObject.put(C0023b.C, true);
        }
        jSONObject.put(C0023b.D, vVar.a());
        jSONObject.put(C0023b.E, DeviceUtil.c());
        jSONObject.put(C0023b.G, z);
        return jSONObject;
    }

    public static JSONObject a(i iVar, Configuration configuration, OneTrack.IEventHook iEventHook, v vVar, boolean z, boolean z2) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        Context contextB = com.xiaomi.onetrack.f.a.b();
        jSONObject.put(C0023b.a, iVar.a());
        if (!z) {
            if (q.a() ? q.h() : configuration.isInternational()) {
                if (iEventHook != null && iEventHook.isRecommendEvent(iVar.a())) {
                    String strE = DeviceUtil.e(contextB);
                    if (!TextUtils.isEmpty(strE)) {
                        jSONObject.put(C0023b.e, strE);
                    }
                }
            } else {
                jSONObject.put(C0023b.b, DeviceUtil.b(contextB));
                jSONObject.put(C0023b.c, com.xiaomi.onetrack.util.oaid.a.a().a(contextB));
            }
            jSONObject.put(C0023b.g, o.a().b());
            jSONObject.put(C0023b.v, configuration.getPluginId());
            if (!TextUtils.isEmpty(iVar.e()) && !TextUtils.isEmpty(iVar.f())) {
                jSONObject.put(C0023b.x, iVar.e());
                jSONObject.put(C0023b.y, iVar.f());
            }
            jSONObject.put(C0023b.z, q.f());
        }
        jSONObject.put(C0023b.F, z2);
        jSONObject.put(C0023b.h, DeviceUtil.d());
        jSONObject.put(C0023b.i, DeviceUtil.b());
        jSONObject.put(C0023b.j, "Android");
        jSONObject.put(C0023b.k, q.d());
        jSONObject.put(C0023b.l, q.c());
        jSONObject.put(C0023b.m, q.e());
        jSONObject.put(C0023b.o, com.xiaomi.onetrack.f.a.c());
        jSONObject.put(C0023b.r, iVar.b());
        jSONObject.put(C0023b.s, q.b());
        jSONObject.put(C0023b.t, com.xiaomi.onetrack.g.c.a(contextB).toString());
        jSONObject.put(C0023b.u, q.i());
        jSONObject.put(C0023b.w, BuildConfig.SDK_VERSION);
        jSONObject.put(C0023b.n, iVar.c());
        jSONObject.put(C0023b.p, com.xiaomi.onetrack.f.a.e());
        jSONObject.put(C0023b.q, !TextUtils.isEmpty(iVar.d()) ? iVar.d() : MiuiWallpaperManager.MI_WALLPAPER_TYPE_DEFAULT);
        jSONObject.put(C0023b.A, (configuration.getMode() != null ? configuration.getMode() : OneTrack.Mode.APP).getType());
        jSONObject.put(C0023b.B, ac.d(aa.B()));
        if (p.c) {
            jSONObject.put(C0023b.C, true);
        }
        jSONObject.put(C0023b.D, vVar.a());
        jSONObject.put(C0023b.E, DeviceUtil.c());
        jSONObject.put(C0023b.G, z);
        jSONObject.put(C0023b.I, "JS");
        return jSONObject;
    }

    private static void a(JSONObject jSONObject, Context context) throws JSONException {
        String strU = aa.u();
        String strW = aa.w();
        if (TextUtils.isEmpty(strU) || TextUtils.isEmpty(strW)) {
            return;
        }
        jSONObject.put(C0023b.x, strU);
        jSONObject.put(C0023b.y, strW);
    }

    private static void a(JSONObject jSONObject, Configuration configuration, String str) throws JSONException {
        if (!TextUtils.isEmpty(str)) {
            jSONObject.put(C0023b.v, str);
        } else {
            jSONObject.put(C0023b.v, configuration.getPluginId());
        }
    }
}
