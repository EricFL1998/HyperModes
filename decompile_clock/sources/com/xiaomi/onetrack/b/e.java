package com.xiaomi.onetrack.b;

import android.text.TextUtils;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.worldclock.WorldClockEditActivity;
import com.xiaomi.onetrack.BuildConfig;
import com.xiaomi.onetrack.util.DeviceUtil;
import com.xiaomi.onetrack.util.aa;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;
import com.xiaomi.onetrack.util.x;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class e {
    public static final String a = "l";
    private static final String b = "CommonConfigUpdater";
    private static final String c = "t";
    private static final String d = "levels";
    private static final String e = "Android";
    private static ConcurrentHashMap<Integer, Integer> f = new ConcurrentHashMap<>();
    private static final long g = 172800000;

    public static void a() {
        com.xiaomi.onetrack.util.i.a(new f());
    }

    public static void b() {
        if (e()) {
            f();
        } else {
            p.a(b, "CommonConfigUpdater Does not meet prerequisites for request");
        }
    }

    private static boolean e() {
        if (!com.xiaomi.onetrack.g.c.a()) {
            p.b(b, "net is not connected!");
            return false;
        }
        if (TextUtils.isEmpty(aa.l())) {
            return true;
        }
        long j = aa.j();
        return j < System.currentTimeMillis() || j - System.currentTimeMillis() > g;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void f() {
        if (q.a(b)) {
            return;
        }
        HashMap map = new HashMap();
        try {
            map.put(m.a, com.xiaomi.onetrack.util.oaid.a.a().a(com.xiaomi.onetrack.f.a.b()));
            map.put(m.b, q.d());
            map.put(m.c, q.c());
            map.put(m.d, q.h() ? "1" : WorldClockEditActivity.LOCAL_CITY_ID);
            map.put(m.e, BuildConfig.SDK_VERSION);
            map.put(m.m, com.xiaomi.onetrack.f.a.c());
            map.put(m.f, q.e());
            map.put(m.g, DeviceUtil.b());
            map.put(m.h, q.i());
            map.put(m.l, e);
            String strD = x.a().d();
            String strB = com.xiaomi.onetrack.g.b.b(strD, map, true);
            p.a(b, "url:" + strD + " response:" + strB);
            a(strB);
        } catch (IOException e2) {
            p.a(b, "requestCloudData: " + e2.toString());
        }
    }

    private static void a(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.optInt("code") == 0) {
                String strOptString = jSONObject.optString(com.xiaomi.onetrack.g.a.e);
                JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("data");
                if (jSONObjectOptJSONObject != null) {
                    JSONObject jSONObjectOptJSONObject2 = jSONObjectOptJSONObject.optJSONObject("regionUrl");
                    if (jSONObjectOptJSONObject2 != null) {
                        x.a().a(jSONObjectOptJSONObject2);
                    }
                    aa.d(jSONObjectOptJSONObject.toString());
                    aa.c(strOptString);
                }
                aa.j(System.currentTimeMillis() + TimerDao.TIMER_MAX_LENGTH + ((long) new Random().nextInt(86400000)));
            }
        } catch (JSONException e2) {
            p.a(b, "saveCommonCloudData: " + e2.toString());
        }
    }

    public static Map<Integer, Integer> c() {
        try {
            if (!f.isEmpty()) {
                return f;
            }
            String strL = aa.l();
            if (!TextUtils.isEmpty(strL)) {
                JSONArray jSONArrayOptJSONArray = new JSONObject(strL).optJSONArray(d);
                for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
                    JSONObject jSONObject = jSONArrayOptJSONArray.getJSONObject(i);
                    int iOptInt = jSONObject.optInt(a);
                    int iOptInt2 = jSONObject.optInt(c);
                    if (iOptInt > 0 && iOptInt2 > 0) {
                        f.put(Integer.valueOf(iOptInt), Integer.valueOf(iOptInt2));
                    }
                }
            }
            return f.isEmpty() ? g() : f;
        } catch (Exception e2) {
            p.a(b, "getLevelIntervalConfig: " + e2.toString());
        }
    }

    private static HashMap<Integer, Integer> g() {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(1, 1000);
        map.put(2, 15000);
        map.put(3, 900000);
        return map;
    }
}
