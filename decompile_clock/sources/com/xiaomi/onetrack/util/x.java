package com.xiaomi.onetrack.util;

import android.content.Context;
import android.text.TextUtils;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class x {
    private static final String A = "/api/v4/detail/config_common";
    private static final String a = "RegionDomainManager";
    private static final String b = "CN";
    private static final String c = "INTL";
    private static final String d = "IN";
    private static final String e = "RU";
    private static final String f = "http://";
    private static final String g = "https://";
    private static String h = "";
    private static String i = "";
    private static String j = "";
    private static String k = "";
    private static String l = "";
    private static String m = "";
    private static String n = "";
    private static String o = "";
    private static final String x = "/track/v4";
    private static final String y = "/track/key_get";
    private static final String z = "/api/v4/detail/config";
    private Context B;
    private static final byte[] p = {116, 114, 97, 99, 107, 105, 110, 103, 46, 109, 105, 117, 105, 46, 99, 111, 109};
    private static final byte[] q = {116, 114, 97, 99, 107, 105, 110, 103, 46, 105, 110, 116, 108, 46, 109, 105, 117, 105, 46, 99, 111, 109};
    private static final byte[] r = {116, 114, 97, 99, 107, 105, 110, 103, 46, 114, 117, 115, 46, 109, 105, 117, 105, 46, 99, 111, 109};
    private static final byte[] s = {116, 114, 97, 99, 107, 105, 110, 103, 46, 105, 110, 100, 105, 97, 46, 109, 105, 117, 105, 46, 99, 111, 109};
    private static final byte[] t = {115, 100, 107, 99, 111, 110, 102, 105, 103, 46, 97, 100, 46, 120, 105, 97, 111, 109, 105, 46, 99, 111, 109};
    private static final byte[] u = {115, 100, 107, 99, 111, 110, 102, 105, 103, 46, 97, 100, 46, 105, 110, 116, 108, 46, 120, 105, 97, 111, 109, 105, 46, 99, 111, 109};
    private static final byte[] v = {115, 100, 107, 99, 111, 110, 102, 105, 103, 46, 97, 100, 46, 105, 110, 100, 105, 97, 46, 120, 105, 97, 111, 109, 105, 46, 99, 111, 109};
    private static final byte[] w = {115, 100, 107, 99, 111, 110, 102, 105, 103, 46, 97, 100, 46, 114, 117, 115, 46, 120, 105, 97, 111, 109, 105, 46, 99, 111, 109};
    private static ConcurrentHashMap<String, String> C = new ConcurrentHashMap<>();

    private x() {
        f();
        C.put(d, k);
        C.put(e, j);
        g();
    }

    private void f() {
        h = a(p);
        i = a(q);
        j = a(r);
        k = a(s);
        l = a(t);
        m = a(u);
        n = a(v);
        o = a(w);
    }

    private String a(byte[] bArr) {
        String str = "";
        try {
            String str2 = new String(bArr, "UTF-8");
            try {
                p.a(a, "transmitToString host:".concat(str2));
                return str2;
            } catch (Exception e2) {
                e = e2;
                str = str2;
                p.b(a, e.getMessage());
                return str;
            }
        } catch (Exception e3) {
            e = e3;
        }
    }

    private static class a {
        private static final x a = new x();

        private a() {
        }
    }

    public static x a() {
        return a.a;
    }

    private void g() {
        try {
            String strH = aa.h();
            if (TextUtils.isEmpty(strH)) {
                return;
            }
            a(new JSONObject(strH));
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    public synchronized void a(JSONObject jSONObject) {
        p.a(a, "updateHostMap:" + jSONObject.toString());
        if (jSONObject == null) {
            return;
        }
        try {
            Iterator<String> itKeys = jSONObject.keys();
            while (itKeys.hasNext()) {
                String next = itKeys.next();
                String strOptString = jSONObject.optString(next);
                if (!TextUtils.isEmpty(next) && !TextUtils.isEmpty(strOptString)) {
                    C.put(next, strOptString);
                }
            }
            aa.b(new JSONObject(C).toString());
        } catch (Exception e2) {
            p.a(a, "updateHostMap: " + e2.toString());
        }
        p.a(a, "merge config:" + new JSONObject(C).toString());
    }

    public String b() {
        try {
            if (TextUtils.isEmpty(aa.l())) {
                com.xiaomi.onetrack.b.e.b();
            }
        } catch (Exception e2) {
            p.a(a, "getTrackingUrl: " + e2.toString());
        }
        return a(h(), i(), x);
    }

    public String c() {
        return a(h(), j(), z);
    }

    public String d() {
        return a(h(), j(), A);
    }

    public String e() {
        return a(h(), i(), y);
    }

    public String a(String str, String str2, String str3) {
        return str + str2 + str3;
    }

    private String h() {
        return g;
    }

    private String i() {
        return a(q.h(), q.i());
    }

    private String a(boolean z2, String str) {
        if (!z2) {
            return h;
        }
        String str2 = C.get(str);
        return TextUtils.isEmpty(str2) ? i : str2;
    }

    private String j() {
        boolean zH = q.h();
        String strI = q.i();
        if (!zH) {
            return l;
        }
        if (TextUtils.equals(strI, d)) {
            return n;
        }
        if (TextUtils.equals(strI, e)) {
            return o;
        }
        return m;
    }
}
