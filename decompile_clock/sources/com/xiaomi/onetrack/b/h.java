package com.xiaomi.onetrack.b;

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.onetrack.util.p;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class h {
    private static final String a = "ConfigDbManager";
    private static final int e = 100;
    private g b;
    private ConcurrentHashMap<String, l> c;
    private ConcurrentHashMap<String, Boolean> d;

    /* synthetic */ h(i iVar) {
        this();
    }

    private h() {
        this.c = new ConcurrentHashMap<>();
        this.d = new ConcurrentHashMap<>();
        this.b = new g(com.xiaomi.onetrack.f.a.a());
    }

    private static class a {
        private static final h a = new h(null);

        private a() {
        }
    }

    public static h a() {
        return a.a;
    }

    public void a(ArrayList<l> arrayList) {
        com.xiaomi.onetrack.c.b.a(new i(this, arrayList));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b(ArrayList<l> arrayList) throws Throwable {
        StringBuilder sb;
        SQLiteDatabase sQLiteDatabase = null;
        try {
            try {
                SQLiteDatabase writableDatabase = this.b.getWritableDatabase();
                try {
                    writableDatabase.beginTransaction();
                    for (l lVar : arrayList) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(g.d, lVar.a);
                        contentValues.put("timestamp", Long.valueOf(lVar.c));
                        if (lVar.e != null) {
                            contentValues.put(g.e, lVar.e.toString());
                        }
                        if (lVar.d != null) {
                            contentValues.put(g.f, lVar.d);
                        }
                        if (DatabaseUtils.queryNumEntries(writableDatabase, g.b, "app_id=?", new String[]{lVar.a}) > 0) {
                            p.a(a, "database updated, row: " + writableDatabase.update(g.b, contentValues, "app_id=?", new String[]{lVar.a}));
                        } else {
                            p.a(a, "database inserted, row: " + writableDatabase.insert(g.b, null, contentValues));
                        }
                        this.d.put(lVar.a, true);
                    }
                    writableDatabase.setTransactionSuccessful();
                    if (writableDatabase != null) {
                        try {
                            writableDatabase.endTransaction();
                        } catch (Exception e2) {
                            e = e2;
                            sb = new StringBuilder("Exception while endTransaction:");
                            p.b(a, sb.append(e).toString());
                        }
                    }
                } catch (Exception e3) {
                    e = e3;
                    sQLiteDatabase = writableDatabase;
                    p.b(a, "updateToDb error: ", e);
                    if (sQLiteDatabase != null) {
                        try {
                            sQLiteDatabase.endTransaction();
                        } catch (Exception e4) {
                            e = e4;
                            sb = new StringBuilder("Exception while endTransaction:");
                            p.b(a, sb.append(e).toString());
                        }
                    }
                } catch (Throwable th) {
                    th = th;
                    sQLiteDatabase = writableDatabase;
                    if (sQLiteDatabase != null) {
                        try {
                            sQLiteDatabase.endTransaction();
                        } catch (Exception e5) {
                            p.b(a, "Exception while endTransaction:" + e5);
                        }
                    }
                    throw th;
                }
            } catch (Exception e6) {
                e = e6;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void a(String str) {
        FutureTask futureTask = new FutureTask(new j(this, str), null);
        com.xiaomi.onetrack.c.b.a(futureTask);
        try {
            futureTask.get();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void b(String str) {
        FutureTask futureTask = new FutureTask(new k(this, str));
        com.xiaomi.onetrack.c.b.a(futureTask);
        try {
            l lVar = (l) futureTask.get(5L, TimeUnit.SECONDS);
            if (lVar != null) {
                this.c.put(str, lVar);
                this.d.put(str, false);
            }
        } catch (Exception e2) {
            p.b(a, "getConfig error: " + e2.toString());
        }
    }

    public boolean a(String str, String str2, String str3, boolean z) {
        try {
            JSONObject jSONObjectC = c(str, str2);
            if (jSONObjectC == null) {
                p.a(a, "config not available, use default value");
                return z;
            }
            return jSONObjectC.getBoolean(str3);
        } catch (Exception e2) {
            p.b(a, "getBoolean: " + e2.toString());
            return z;
        }
    }

    public String a(String str, String str2, String str3, String str4) {
        try {
            JSONObject jSONObjectC = c(str, str2);
            if (jSONObjectC == null) {
                p.a(a, "config not available, use default value");
                return str4;
            }
            return jSONObjectC.getString(str3);
        } catch (Exception e2) {
            p.b(a, "getString: " + e2.toString());
            return str4;
        }
    }

    public int a(String str, String str2, String str3, int i) {
        try {
            JSONObject jSONObjectC = c(str, str2);
            if (jSONObjectC == null) {
                p.a(a, "config not available, use default value");
                return i;
            }
            return jSONObjectC.getInt(str3);
        } catch (Exception e2) {
            p.b(a, "getInt: " + e2.toString());
            return i;
        }
    }

    public long a(String str, String str2, String str3, long j) {
        try {
            JSONObject jSONObjectC = c(str, str2);
            if (jSONObjectC == null) {
                p.a(a, "config not available, use default value");
                return j;
            }
            return jSONObjectC.getLong(str3);
        } catch (Exception e2) {
            p.b(a, "getLong: " + e2.toString());
            return j;
        }
    }

    public double a(String str, String str2, String str3, double d) {
        try {
            JSONObject jSONObjectC = c(str, str2);
            if (jSONObjectC == null) {
                p.a(a, "config not available, use default value");
                return d;
            }
            return jSONObjectC.getDouble(str3);
        } catch (Exception e2) {
            p.b(a, "getDouble: " + e2.toString());
            return d;
        }
    }

    private JSONObject c(String str, String str2) {
        JSONArray jSONArrayOptJSONArray;
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            try {
                if (this.c.get(str) == null || (this.d.containsKey(str) && this.d.get(str).booleanValue())) {
                    b(str);
                }
                l lVar = this.c.get(str);
                if (lVar != null && lVar.e != null && (jSONArrayOptJSONArray = lVar.e.optJSONArray("events")) != null) {
                    for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
                        JSONObject jSONObject = jSONArrayOptJSONArray.getJSONObject(i);
                        if (TextUtils.equals(str2, jSONObject.optString("event"))) {
                            if (p.a) {
                                p.a(a, "getEventConfig:" + (jSONObject != null ? jSONObject.toString() : ""));
                            }
                            return jSONObject;
                        }
                    }
                }
            } catch (Exception e2) {
                Log.e(a, "getEventConfig error: " + e2.toString());
            }
        }
        return null;
    }

    public String c(String str) {
        l lVarF = f(str);
        if (lVarF != null && lVarF.e != null) {
            return lVarF.e.optString(com.xiaomi.onetrack.b.a.g);
        }
        return "";
    }

    public String d(String str) {
        l lVarF = f(str);
        if (lVarF != null) {
            return lVarF.d;
        }
        return "";
    }

    public int e(String str) {
        l lVarF = f(str);
        if (lVarF == null || lVarF.e == null) {
            return 0;
        }
        return lVarF.e.optInt("version");
    }

    public boolean a(String str, String str2) {
        try {
            l lVarF = f(str);
            if (lVarF == null || lVarF.e == null || !lVarF.e.has(str2)) {
                return false;
            }
            return lVarF.e.optBoolean(str2);
        } catch (Exception e2) {
            p.b(a, "getAppLevelBoolean" + e2.toString());
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int b(JSONObject jSONObject) {
        try {
            int iOptInt = jSONObject.optInt(com.xiaomi.onetrack.b.a.e, 100);
            if (iOptInt < 0 || iOptInt > 100) {
                return 100;
            }
            return iOptInt;
        } catch (Exception e2) {
            p.a(a, "getCommonSample Exception:" + e2.getMessage());
            return 100;
        }
    }

    public long b(String str, String str2) {
        l lVar;
        if (TextUtils.isEmpty(str)) {
            return 100L;
        }
        try {
            if (this.c.get(str) == null) {
                b(str);
            }
            if (this.c.get(str) != null) {
                int iA = a(str, str2, com.xiaomi.onetrack.b.a.e, -1);
                if (iA != -1 || (lVar = this.c.get(str)) == null) {
                    p.a(a, "will return event sample " + iA);
                    return iA;
                }
                p.a(a, "will return common sample " + lVar.b);
                return lVar.b;
            }
        } catch (Exception e2) {
            p.b(a, "getAppEventSample" + e2.toString());
        }
        p.a(a, "will return def sample");
        return 100L;
    }

    public l f(String str) {
        p.a(a, "getAppConfigData start, appId: " + str);
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            if (this.c.get(str) == null || (this.d.containsKey(str) && this.d.get(str).booleanValue())) {
                b(str);
            }
        } catch (Exception e2) {
            p.b(a, "getConfig error: " + e2.getMessage());
        }
        return this.c.get(str);
    }
}
