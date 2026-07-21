package com.xiaomi.onetrack.api;

import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class i {
    private static final String a = "H5DataModel";
    private String b;
    private long c;
    private String d;
    private String e;
    private String f;
    private String g;

    public static class a {
        public static String a = "event";
        public static String b = "session_id";
        public static String c = "instance_id";
        public static String d = "platform";
        public static String e = "e_ts";
        public static String f = "tz";
        public static String g = "sdk_ver";
        public static String h = "app_id";
        public static String i = "channel";
        public static String j = "uid";
        public static String k = "uid_type";
    }

    public i(JSONObject jSONObject) {
        this.b = a(jSONObject, a.a);
        try {
            this.c = Long.parseLong(a(jSONObject, a.e));
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b(a, "e_ts parse error: " + e.getMessage());
        }
        this.d = a(jSONObject, a.h);
        this.e = a(jSONObject, a.i);
        this.f = a(jSONObject, a.j);
        this.g = a(jSONObject, a.k);
    }

    private String a(JSONObject jSONObject, String str) {
        Object objOpt = jSONObject.opt(str);
        if (objOpt == null) {
            return "";
        }
        return String.valueOf(objOpt);
    }

    public String a() {
        return this.b;
    }

    public long b() {
        return this.c;
    }

    public String c() {
        return this.d;
    }

    public String d() {
        return this.e;
    }

    public String e() {
        return this.f;
    }

    public String f() {
        return this.g;
    }

    public String toString() {
        return "H5DataModel{eventName='" + this.b + "', e_ts=" + this.c + ", appId='" + this.d + "', channel='" + this.e + "', uid='" + this.f + "', uidType='" + this.g + "'}";
    }
}
