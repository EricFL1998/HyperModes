package com.xiaomi.onetrack.e;

import com.xiaomi.onetrack.b.h;
import com.xiaomi.onetrack.util.p;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class a extends com.xiaomi.onetrack.f.b {
    private static final String d = "CustomEvent";

    public a(String str, String str2, String str3, String str4) {
        try {
            a(str);
            c(str3);
            b(str2);
            b(System.currentTimeMillis());
            b(new JSONObject(str4));
            a(h.a().a(str, str3, "level", 1));
        } catch (Exception e) {
            p.b(d, "CustomEvent error:" + e.toString());
        }
    }
}
