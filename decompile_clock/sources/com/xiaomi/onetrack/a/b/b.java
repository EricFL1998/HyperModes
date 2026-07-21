package com.xiaomi.onetrack.a.b;

import android.text.TextUtils;
import com.xiaomi.onetrack.api.h;
import com.xiaomi.onetrack.util.p;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class b extends com.xiaomi.onetrack.f.b {
    private static final String d = "OTAdEvent";
    private List<a> e = null;

    public b(String str, String str2, String str3, String str4) {
        try {
            a(str);
            c(str3);
            b(str2);
            b(System.currentTimeMillis());
            JSONObject jSONObject = new JSONObject(str4);
            JSONObject jSONObject2 = jSONObject.getJSONObject(h.b);
            b(jSONObject);
            a(com.xiaomi.onetrack.b.h.a().a(str, str3, "level", 0));
            a(jSONObject2);
            c(jSONObject2);
        } catch (Exception e) {
            p.b(d, "CustomEvent error:" + e.toString());
        }
    }

    public List<a> a() {
        return this.e;
    }

    public void a(JSONObject jSONObject) {
        if (jSONObject == null) {
            return;
        }
        try {
            JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray(com.xiaomi.onetrack.f.b.C0023b.H);
            if (jSONArrayOptJSONArray == null || jSONArrayOptJSONArray.length() <= 0) {
                return;
            }
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
                String strOptString = jSONArrayOptJSONArray.optString(i);
                if (!TextUtils.isEmpty(strOptString)) {
                    if (strOptString.contains("api.ad.xiaomi.com") && !strOptString.contains("_sn_")) {
                        strOptString = strOptString.contains("?") ? strOptString + "&_sn_=" + UUID.randomUUID().toString() : strOptString + "?_sn_=" + UUID.randomUUID().toString();
                    }
                    a aVar = new a();
                    aVar.c(c());
                    aVar.a(e());
                    aVar.a(h());
                    aVar.d(d());
                    aVar.b(strOptString);
                    arrayList.add(aVar);
                }
            }
            this.e = arrayList;
        } catch (Throwable th) {
            p.a(d, "parseAdMonitor Throwable:" + th.getMessage());
        }
    }

    private void c(JSONObject jSONObject) {
        if (jSONObject != null && jSONObject.has(com.xiaomi.onetrack.f.b.C0023b.H)) {
            jSONObject.remove(com.xiaomi.onetrack.f.b.C0023b.H);
        }
    }
}
