package com.xiaomi.onetrack.api;

import android.content.Context;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.ServiceQualityEvent;
import com.xiaomi.onetrack.util.DeviceUtil;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class h {
    public static final String a = "B";
    public static final String b = "H";
    private static final String c = "EventDataBuilder";

    public static String a(String str, JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, com.xiaomi.onetrack.util.v vVar, boolean z) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(b, com.xiaomi.onetrack.f.b.a(str, configuration, iEventHook, vVar, z, false));
        jSONObject3.put(a, com.xiaomi.onetrack.util.r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }

    public static String a(i iVar, JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, com.xiaomi.onetrack.util.v vVar, boolean z) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(b, com.xiaomi.onetrack.f.b.a(iVar, configuration, iEventHook, vVar, z, false));
        jSONObject3.put(a, com.xiaomi.onetrack.util.r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }

    public static String a(String str, String str2, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject, boolean z, com.xiaomi.onetrack.util.v vVar, boolean z2) throws JSONException {
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put(b, com.xiaomi.onetrack.f.b.a(str2, configuration, iEventHook, vVar, z2, false));
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(g.r, str);
        jSONObject3.put("type", 1);
        jSONObject3.put(g.t, z);
        jSONObject2.put(a, com.xiaomi.onetrack.util.r.a(jSONObject3, jSONObject));
        return jSONObject2.toString();
    }

    public static String a(String str, String str2, long j, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject, com.xiaomi.onetrack.util.v vVar, boolean z) throws JSONException {
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put(b, com.xiaomi.onetrack.f.b.a(str2, configuration, iEventHook, vVar, z, false));
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(g.r, str);
        jSONObject3.put("type", 2);
        jSONObject3.put("duration", j);
        jSONObject2.put(a, com.xiaomi.onetrack.util.r.a(jSONObject3, jSONObject));
        return jSONObject2.toString();
    }

    public static String a(String str, String str2, String str3, String str4, String str5, long j, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject, com.xiaomi.onetrack.util.v vVar, boolean z) throws JSONException {
        JSONObject jSONObject2 = new JSONObject();
        JSONObject jSONObjectA = com.xiaomi.onetrack.f.b.a("onetrack_bug_report", configuration, iEventHook, vVar, z, false);
        if (str5 != null) {
            jSONObjectA.put(com.xiaomi.onetrack.f.b.C0023b.o, str5);
        }
        jSONObject2.put(b, jSONObjectA);
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put("exception", str);
        jSONObject3.put("type", str3);
        jSONObject3.put("message", str2);
        jSONObject3.put(g.n, str4);
        jSONObject3.put(g.o, j);
        jSONObject2.put(a, com.xiaomi.onetrack.util.r.a(jSONObject3, jSONObject));
        return jSONObject2.toString();
    }

    public static String a(Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject, com.xiaomi.onetrack.util.v vVar, boolean z) throws JSONException {
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put(b, com.xiaomi.onetrack.f.b.a("onetrack_dau", configuration, iEventHook, vVar, z, false));
        JSONObject jSONObject3 = new JSONObject();
        Context contextB = com.xiaomi.onetrack.f.a.b();
        boolean zS = com.xiaomi.onetrack.util.aa.s();
        if (zS) {
            com.xiaomi.onetrack.util.aa.c(false);
        }
        jSONObject3.put(g.x, zS);
        if (!(com.xiaomi.onetrack.util.q.a() ? com.xiaomi.onetrack.util.q.h() : configuration.isInternational())) {
            if (configuration.isIMEIEnable()) {
                jSONObject3.put(g.y, DeviceUtil.f(contextB));
            }
            if (configuration.isIMSIEnable()) {
                jSONObject3.put(g.z, DeviceUtil.i(contextB));
            }
        }
        jSONObject3.put(g.C, f.a(configuration));
        jSONObject2.put(a, com.xiaomi.onetrack.util.r.a(jSONObject3, jSONObject));
        return jSONObject2.toString();
    }

    public static String a(JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, com.xiaomi.onetrack.util.v vVar, boolean z) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(b, com.xiaomi.onetrack.f.b.a(g.c, configuration, iEventHook, vVar, z, false));
        jSONObject3.put(a, com.xiaomi.onetrack.util.r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }

    public static String b(JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, com.xiaomi.onetrack.util.v vVar, boolean z) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(b, com.xiaomi.onetrack.f.b.a(g.d, configuration, iEventHook, vVar, z, false));
        jSONObject3.put(a, com.xiaomi.onetrack.util.r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }

    public static String a(String str, String str2, JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, com.xiaomi.onetrack.util.v vVar, boolean z) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(b, com.xiaomi.onetrack.f.b.a(str2, configuration, iEventHook, str, vVar, z, false));
        jSONObject3.put(a, com.xiaomi.onetrack.util.r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }

    public static String a(Configuration configuration, OneTrack.IEventHook iEventHook, com.xiaomi.onetrack.util.v vVar, boolean z) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(b, com.xiaomi.onetrack.f.b.a(g.i, configuration, iEventHook, vVar, z, false));
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put(g.p, com.xiaomi.onetrack.c.i.b());
        jSONObject.put(a, jSONObject2);
        return jSONObject.toString();
    }

    public static String c(JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, com.xiaomi.onetrack.util.v vVar, boolean z) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(b, com.xiaomi.onetrack.f.b.a("ot_login", configuration, iEventHook, vVar, z, false));
        jSONObject3.put(a, com.xiaomi.onetrack.util.r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }

    public static String d(JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, com.xiaomi.onetrack.util.v vVar, boolean z) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(b, com.xiaomi.onetrack.f.b.a("ot_logout", configuration, iEventHook, vVar, z, false));
        jSONObject3.put(a, com.xiaomi.onetrack.util.r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }

    public static String a(ServiceQualityEvent serviceQualityEvent, Configuration configuration, OneTrack.IEventHook iEventHook, com.xiaomi.onetrack.util.v vVar, boolean z) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(b, com.xiaomi.onetrack.f.b.a(g.e, configuration, iEventHook, vVar, z, false));
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put(g.D, serviceQualityEvent.getScheme());
        jSONObject2.put(g.E, serviceQualityEvent.getHost());
        jSONObject2.put(g.F, serviceQualityEvent.getPort());
        jSONObject2.put(g.G, serviceQualityEvent.getPath());
        jSONObject2.put(g.H, serviceQualityEvent.getIps());
        jSONObject2.put(g.I, serviceQualityEvent.getResponseCode());
        jSONObject2.put("status", serviceQualityEvent.getStatusCode());
        jSONObject2.put("exception", serviceQualityEvent.getExceptionTag());
        jSONObject2.put(g.L, serviceQualityEvent.getResultType());
        jSONObject2.put(g.M, serviceQualityEvent.getRetryCount());
        jSONObject2.put(g.N, serviceQualityEvent.getRequestTimestamp());
        jSONObject2.put(g.O, serviceQualityEvent.getRequestNetType());
        jSONObject2.put(g.P, serviceQualityEvent.getDnsLookupTime());
        jSONObject2.put(g.Q, serviceQualityEvent.getTcpConnectTime());
        jSONObject2.put(g.S, serviceQualityEvent.getHandshakeTime());
        jSONObject2.put(g.T, serviceQualityEvent.getReceiveFirstByteTime());
        jSONObject2.put(g.U, serviceQualityEvent.getReceiveAllByteTime());
        jSONObject2.put(g.R, serviceQualityEvent.getRequestDataSendTime());
        jSONObject2.put("duration", serviceQualityEvent.getDuration());
        jSONObject2.put(g.W, serviceQualityEvent.getNetSdkVersion());
        Map<String, Object> extraParams = serviceQualityEvent.getExtraParams();
        if (extraParams != null && extraParams.size() > 0) {
            for (Map.Entry<String, Object> entry : extraParams.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (com.xiaomi.onetrack.util.r.b(value)) {
                    jSONObject2.put(key, value);
                }
            }
        }
        jSONObject.put(a, jSONObject2);
        return jSONObject.toString();
    }

    public static String a(long j, String str, long j2, long j3, Configuration configuration, OneTrack.IEventHook iEventHook, com.xiaomi.onetrack.util.v vVar, boolean z) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(b, com.xiaomi.onetrack.f.b.a(g.j, configuration, iEventHook, vVar, z, false));
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put(g.X, j);
        jSONObject2.put(g.Y, str);
        jSONObject2.put(g.Z, j2);
        jSONObject2.put(g.aa, j3);
        jSONObject.put(a, jSONObject2);
        return jSONObject.toString();
    }

    public static String a(String str, JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, com.xiaomi.onetrack.util.v vVar, JSONArray jSONArray, boolean z) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        JSONObject jSONObjectA = com.xiaomi.onetrack.f.b.a(str, configuration, iEventHook, vVar, z, true);
        if (jSONArray != null && jSONArray.length() > 0) {
            jSONObjectA.put(com.xiaomi.onetrack.f.b.C0023b.H, jSONArray);
        }
        jSONObject3.put(b, jSONObjectA);
        jSONObject3.put(a, com.xiaomi.onetrack.util.r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }
}
