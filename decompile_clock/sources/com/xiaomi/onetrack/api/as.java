package com.xiaomi.onetrack.api;

import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class as {
    public static final String a = "name";
    public static final String b = "gender";
    public static final String c = "birthday";
    public static final String d = "phone";
    public static final String e = "job";
    public static final String f = "hobby";
    public static final String g = "region";
    public static final String h = "province";
    public static final String i = "city";
    private String j;
    private String k;
    private String l;
    private String m;
    private String n;
    private String o;
    private String p;
    private String q;
    private String r;

    private as() {
    }

    public String a() {
        return this.j;
    }

    public String b() {
        return this.k;
    }

    public String c() {
        return this.l;
    }

    public String d() {
        return this.m;
    }

    public String e() {
        return this.n;
    }

    public String f() {
        return this.o;
    }

    public String g() {
        return this.p;
    }

    public String h() {
        return this.q;
    }

    public String i() {
        return this.r;
    }

    public JSONObject j() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(a, this.j);
            jSONObject.put(b, this.k);
            jSONObject.put(c, this.l);
            jSONObject.put(d, this.m);
            jSONObject.put(e, this.n);
            jSONObject.put(f, this.o);
            jSONObject.put(g, this.p);
            jSONObject.put(h, this.q);
            jSONObject.put(i, this.r);
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        return jSONObject;
    }

    public String toString() {
        return j().toString();
    }

    public static final class a {
        private String a;
        private String b;
        private String c;
        private String d;
        private String e;
        private String f;
        private String g;
        private String h;
        private String i;

        public a a(String str) {
            this.a = str;
            return this;
        }

        public a b(String str) {
            this.b = str;
            return this;
        }

        public a c(String str) {
            this.c = str;
            return this;
        }

        public a d(String str) {
            this.d = str;
            return this;
        }

        public a e(String str) {
            this.e = str;
            return this;
        }

        public a f(String str) {
            this.f = str;
            return this;
        }

        public a g(String str) {
            this.g = str;
            return this;
        }

        public a h(String str) {
            this.h = str;
            return this;
        }

        public a i(String str) {
            this.i = str;
            return this;
        }

        public as a() {
            as asVar = new as();
            asVar.o = this.f;
            asVar.n = this.e;
            asVar.r = this.i;
            asVar.m = this.d;
            asVar.q = this.h;
            asVar.l = this.c;
            asVar.j = this.a;
            asVar.p = this.g;
            asVar.k = this.b;
            return asVar;
        }
    }
}
