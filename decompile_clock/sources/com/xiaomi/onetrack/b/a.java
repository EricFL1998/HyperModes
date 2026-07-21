package com.xiaomi.onetrack.b;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.worldclock.WorldClockEditActivity;
import com.xiaomi.onetrack.BuildConfig;
import com.xiaomi.onetrack.util.DeviceUtil;
import com.xiaomi.onetrack.util.aa;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;
import com.xiaomi.onetrack.util.x;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class a {
    public static final String a = "disable_log";
    public static final String b = "event";
    public static final String c = "events";
    public static final String d = "level";
    public static final String e = "sample";
    public static final String f = "needIds";
    public static final String g = "bannedParams";
    public static final String h = "version";
    private static final String i = "AppConfigUpdater";
    private static final long j = 172800000;
    private static final String k = "hash";
    private static final String l = "appId";
    private static final String m = "apps";
    private static final String n = "type";
    private static final String o = "status";
    private static final String p = "deleted";
    private static final String q = "Android";
    private static final int s = 0;
    private static final int t = 1;
    private static final int u = 2;
    private static final int v = 100;
    private static final long x = 1800000;
    private static final int z = 0;
    private static AtomicBoolean r = new AtomicBoolean(false);
    private static ConcurrentHashMap<String, Long> w = new ConcurrentHashMap<>();
    private static b y = new b(Looper.getMainLooper(), null);
    private static ConcurrentHashMap<String, Boolean> A = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Boolean> B = new ConcurrentHashMap<>();
    private static String C = "";

    /* synthetic */ a(com.xiaomi.onetrack.b.b bVar) {
        this();
    }

    private a() {
        String strC = aa.C();
        if (!TextUtils.isEmpty(strC)) {
            C = strC;
            return;
        }
        String strI = q.i();
        if (TextUtils.isEmpty(strI)) {
            return;
        }
        C = strI;
        aa.l(strI);
    }

    /* JADX INFO: renamed from: com.xiaomi.onetrack.b.a$a, reason: collision with other inner class name */
    private static class C0022a {
        private static final a a = new a(null);

        private C0022a() {
        }
    }

    public static a a() {
        return C0022a.a;
    }

    public void a(String str) {
        B.put(str, false);
    }

    public void b(String str) {
        com.xiaomi.onetrack.util.i.a(new com.xiaomi.onetrack.b.b(this, str));
    }

    public void c(String str) {
        if (g(str)) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(str);
            b(arrayList);
            return;
        }
        p.a(i, "AppConfigUpdater Does not meet prerequisites for request");
    }

    private static boolean f(String str) {
        ConcurrentHashMap<String, Boolean> concurrentHashMap = B;
        return concurrentHashMap != null && concurrentHashMap.containsKey(str) && B.get(str).booleanValue();
    }

    private boolean g(String str) {
        if (!com.xiaomi.onetrack.g.c.a()) {
            p.a(i, "net is not connected!");
            return false;
        }
        l lVarF = h.a().f(str);
        if (lVarF == null) {
            return true;
        }
        long j2 = lVarF.c;
        return j2 < System.currentTimeMillis() || j2 - System.currentTimeMillis() > j || f(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void b(List<String> list) {
        p.a(i, "pullCloudData start, appIds: " + list.toString());
        if (q.a(i)) {
            return;
        }
        if (r.compareAndSet(false, true)) {
            HashMap map = new HashMap();
            try {
                try {
                    String strI = q.i();
                    if (!TextUtils.isEmpty(strI)) {
                        C = strI;
                        aa.l(strI);
                    }
                    map.put(m.a, com.xiaomi.onetrack.util.oaid.a.a().a(com.xiaomi.onetrack.f.a.b()));
                    map.put(m.b, q.d());
                    map.put(m.c, q.c());
                    map.put(m.d, q.h() ? "1" : WorldClockEditActivity.LOCAL_CITY_ID);
                    map.put(m.e, BuildConfig.SDK_VERSION);
                    map.put(m.m, com.xiaomi.onetrack.f.a.c());
                    map.put(m.f, q.e());
                    map.put(m.g, DeviceUtil.b());
                    map.put(m.h, strI);
                    map.put(m.i, c(list));
                    map.put(m.j, com.xiaomi.onetrack.f.a.e());
                    map.put(m.l, q);
                    map.put(m.n, "1");
                    String strC = x.a().c();
                    p.a(i, "pullData:" + strC);
                    String strB = com.xiaomi.onetrack.g.b.b(strC, map, true);
                    p.a(i, "response:" + strB);
                    a(strB, list);
                } catch (Exception e2) {
                    p.b(i, "pullCloudData error: " + e2.getMessage());
                }
            } finally {
                r.set(false);
            }
        }
    }

    private static String c(List<String> list) {
        JSONArray jSONArray = new JSONArray();
        try {
            JSONObject jSONObject = new JSONObject();
            for (int i2 = 0; i2 < list.size(); i2++) {
                String str = list.get(i2);
                jSONObject.put("appId", str);
                if (f(str)) {
                    jSONObject.put("hash", "");
                } else {
                    jSONObject.put("hash", h.a().d(str));
                }
                jSONArray.put(jSONObject);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return jSONArray.toString();
    }

    public static void a(String str, List<String> list) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.optInt("code") == 0) {
                d(list);
                a(jSONObject.optJSONObject("data").optJSONArray(m), list);
            }
        } catch (Exception e2) {
            p.a(i, "saveAppCloudData: " + e2.toString());
        }
    }

    private static void d(List<String> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (int i2 = 0; i2 < list.size(); i2++) {
            w.put(list.get(i2), Long.valueOf(System.currentTimeMillis() + 1800000));
        }
    }

    private static void a(JSONArray jSONArray, List<String> list) throws JSONException {
        p.a(i, "updateDataToDb start");
        long jCurrentTimeMillis = System.currentTimeMillis() + TimerDao.TIMER_MAX_LENGTH + ((long) new Random().nextInt(86400000));
        if (jSONArray != null && jSONArray.length() > 0) {
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i2);
                String strOptString = jSONObjectOptJSONObject == null ? "" : jSONObjectOptJSONObject.optString("appId");
                p.a(i, "appId: " + strOptString);
                if (!TextUtils.isEmpty(strOptString)) {
                    arrayList.add(strOptString);
                    a(strOptString, jSONObjectOptJSONObject, jCurrentTimeMillis);
                }
            }
            a(list, jCurrentTimeMillis, arrayList);
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                B.put(it.next(), false);
            }
            return;
        }
        a(list, jCurrentTimeMillis);
    }

    private static void a(String str, JSONObject jSONObject, long j2) throws JSONException {
        int iOptInt = jSONObject == null ? 0 : jSONObject.optInt("version");
        int iE = h.a().e(str);
        p.a(i, "local version: " + iE + ", server version: " + iOptInt);
        if (iE > 0 && iOptInt <= iE && !f(str)) {
            a(jSONObject, j2);
            return;
        }
        int iOptInt2 = jSONObject != null ? jSONObject.optInt("type") : -1;
        p.a(i, "type: " + iOptInt2);
        if (iOptInt2 == 0 || iOptInt2 == 1 || f(str)) {
            a(jSONObject, j2, iOptInt);
        } else if (iOptInt2 == 2) {
            b(jSONObject, j2);
        } else {
            p.a(i, "handleData do nothing!");
        }
    }

    private static void a(JSONObject jSONObject, long j2) {
        ArrayList<l> arrayList = new ArrayList<>();
        if (jSONObject != null) {
            l lVar = new l();
            lVar.a = jSONObject.optString("appId");
            lVar.c = j2;
            arrayList.add(lVar);
        }
        if (!arrayList.isEmpty()) {
            h.a().a(arrayList);
        } else {
            p.a(i, "updateMinVersionData no timestamp can be updated!");
        }
    }

    private static void a(JSONObject jSONObject, long j2, int i2) throws JSONException {
        l lVarF;
        ArrayList<l> arrayList = new ArrayList<>();
        if (jSONObject != null) {
            l lVar = new l();
            lVar.d = jSONObject.optString("hash");
            lVar.a = jSONObject.optString("appId");
            lVar.b = b(jSONObject);
            lVar.c = j2;
            if (!jSONObject.has("events") && (lVarF = h.a().f(lVar.a)) != null && lVarF.e != null && lVarF.e.optJSONArray("events") != null) {
                jSONObject.put("events", lVarF.e.optJSONArray("events"));
            }
            lVar.e = jSONObject;
            arrayList.add(lVar);
        }
        if (!arrayList.isEmpty()) {
            h.a().a(arrayList);
        } else {
            p.a(i, "handleFullOrNoNewData no configuration can be updated!");
        }
    }

    private static void b(JSONObject jSONObject, long j2) {
        ArrayList<l> arrayList = new ArrayList<>();
        if (jSONObject == null || !jSONObject.has("events")) {
            p.a(i, "handleIncrementalUpdate config is not change!");
        } else {
            l lVar = new l();
            lVar.d = jSONObject.optString("hash");
            String strOptString = jSONObject.optString("appId");
            lVar.a = strOptString;
            lVar.b = b(jSONObject);
            lVar.c = j2;
            lVar.e = a(strOptString, jSONObject);
            arrayList.add(lVar);
        }
        if (arrayList.isEmpty()) {
            p.a(i, "handleIncrementalUpdate no configuration can be updated!");
        } else {
            h.a().a(arrayList);
        }
    }

    private static int b(JSONObject jSONObject) {
        try {
            int iOptInt = jSONObject.optInt(e, 100);
            if (iOptInt < 0 || iOptInt > 100) {
                return 100;
            }
            return iOptInt;
        } catch (Exception e2) {
            p.a(i, "getCommonSample Exception:" + e2.getMessage());
            return 100;
        }
    }

    private static JSONObject a(String str, JSONObject jSONObject) {
        try {
            l lVarF = h.a().f(str);
            jSONObject.put("events", a(lVarF != null ? lVarF.e.optJSONArray("events") : null, jSONObject.optJSONArray("events")));
            return jSONObject;
        } catch (Exception e2) {
            p.b(i, "mergeConfig: " + e2.toString());
            return null;
        }
    }

    private static JSONArray a(JSONArray jSONArray, JSONArray jSONArray2) {
        int i2 = 0;
        while (jSONArray2 != null) {
            try {
                if (i2 >= jSONArray2.length()) {
                    break;
                }
                JSONObject jSONObjectOptJSONObject = jSONArray2.optJSONObject(i2);
                String strOptString = jSONObjectOptJSONObject.optString("event");
                for (int i3 = 0; jSONArray != null && i3 < jSONArray.length(); i3++) {
                    if (TextUtils.equals(strOptString, jSONArray.optJSONObject(i3).optString("event"))) {
                        jSONArray.remove(i3);
                        break;
                    }
                }
                if (!jSONObjectOptJSONObject.has("status") || (jSONObjectOptJSONObject.has("status") && !TextUtils.equals(jSONObjectOptJSONObject.optString("status"), p))) {
                    if (jSONArray == null) {
                        jSONArray = new JSONArray();
                    }
                    jSONArray.put(jSONObjectOptJSONObject);
                }
                i2++;
            } catch (Exception e2) {
                p.b(i, "mergeEventsElement error:" + e2.toString());
            }
        }
        return jSONArray;
    }

    private static void a(List<String> list, long j2, List<String> list2) {
        try {
            if (list.size() != list2.size()) {
                list.removeAll(list2);
                a(list, j2);
            }
        } catch (Exception e2) {
            p.b(i, "handleInvalidAppIds error:" + e2.toString());
        }
    }

    private static void a(List<String> list, long j2) {
        try {
            ArrayList<l> arrayList = new ArrayList<>();
            for (int i2 = 0; i2 < list.size(); i2++) {
                l lVar = new l();
                lVar.a = list.get(i2);
                lVar.b = 100L;
                lVar.c = j2;
                arrayList.add(lVar);
            }
            h.a().a(arrayList);
        } catch (Exception e2) {
            p.b(i, "handleError" + e2.toString());
        }
    }

    public void a(JSONObject jSONObject) {
        com.xiaomi.onetrack.util.i.a(new c(this, jSONObject));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean h(String str) {
        Long l2 = w.get(str);
        return l2 == null || l2.longValue() - System.currentTimeMillis() < 0 || l2.longValue() - System.currentTimeMillis() > 1800000;
    }

    public void d(String str) {
        if (TextUtils.isEmpty(C) || TextUtils.isEmpty(str) || TextUtils.equals(C, str)) {
            return;
        }
        Iterator<Map.Entry<String, Boolean>> it = B.entrySet().iterator();
        while (it.hasNext()) {
            it.next().setValue(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class b extends Handler {
        /* synthetic */ b(Looper looper, com.xiaomi.onetrack.b.b bVar) {
            this(looper);
        }

        private b(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            p.a(a.i, "ScheduleCloudHandler.handleMessage, msg.what=" + message.what);
            if (message.what == 0) {
                Object obj = message.obj;
                if (obj != null) {
                    try {
                        String str = (String) obj;
                        p.a(a.i, "ScheduleCloudHandler.handleMessage, appId: " + str);
                        com.xiaomi.onetrack.util.i.a(new d(this, str));
                        return;
                    } catch (Exception e) {
                        p.b(a.i, "handleMessage error: " + e.getMessage());
                        return;
                    }
                }
                p.a(a.i, "ScheduleCloudHandler.handleMessage, msg.obj is null");
            }
        }
    }
}
