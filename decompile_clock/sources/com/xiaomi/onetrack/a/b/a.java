package com.xiaomi.onetrack.a.b;

import android.text.TextUtils;
import com.xiaomi.onetrack.util.p;

/* JADX INFO: loaded from: classes2.dex */
public class a {
    public static final int a = 0;
    public static final int b = 1;
    public static final int c = 2;
    private static final String d = "AdMonitor";
    private int e;
    private String f;
    private long g;
    private String h = "";
    private String i = "";
    private String j = "";
    private int k = 0;

    public String a() {
        return this.j;
    }

    public void a(String str) {
        this.j = str;
    }

    public int b() {
        return this.e;
    }

    public void a(int i) {
        this.e = i;
    }

    public String c() {
        return this.f;
    }

    public void b(String str) {
        this.f = str;
    }

    public long d() {
        return this.g;
    }

    public void a(long j) {
        this.g = j;
    }

    public String e() {
        return this.h;
    }

    public void c(String str) {
        this.h = str;
    }

    public String f() {
        return this.i;
    }

    public void d(String str) {
        this.i = str;
    }

    public int g() {
        return this.k;
    }

    public void b(int i) {
        this.k = i;
    }

    public boolean h() {
        try {
            return (TextUtils.isEmpty(this.f) || TextUtils.isEmpty(this.h) || TextUtils.isEmpty(this.i)) ? false : true;
        } catch (Exception e) {
            p.a(d, "check AdMonitor isValid error:" + e.getMessage());
            return false;
        }
    }
}
