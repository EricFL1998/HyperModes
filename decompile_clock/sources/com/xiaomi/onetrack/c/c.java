package com.xiaomi.onetrack.c;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteBlobTooBigException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.xiaomi.onetrack.util.aa;
import com.xiaomi.onetrack.util.z;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class c {
    private static final String a = "EventManager";
    private static final boolean b = false;
    private static final int c = 204800;
    private static final int d = 307200;
    private static final int e = 300;
    private static final String f = "priority ASC, _id ASC";
    private static final int g = 7;
    private static c h;
    private static BroadcastReceiver j = new d();
    private a i;

    public static c a() {
        if (h == null) {
            a(com.xiaomi.onetrack.f.a.b());
        }
        return h;
    }

    public static void a(Context context) {
        if (h == null) {
            synchronized (c.class) {
                if (h == null) {
                    h = new c();
                }
            }
        }
    }

    private c() {
        Context contextA = com.xiaomi.onetrack.f.a.a();
        this.i = new a(contextA);
        b();
        b(contextA);
    }

    private static void b(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        context.registerReceiver(j, intentFilter);
    }

    public synchronized void a(com.xiaomi.onetrack.f.b bVar) {
        b.a(new f(this, bVar));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b(com.xiaomi.onetrack.f.b bVar) {
        synchronized (this.i) {
            if (!bVar.i()) {
                com.xiaomi.onetrack.util.p.c(a, "addEventToDatabase event is inValid, event:" + bVar.e());
                return;
            }
            SQLiteDatabase writableDatabase = this.i.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("appid", bVar.c());
            contentValues.put("package", bVar.d());
            contentValues.put("event_name", bVar.e());
            contentValues.put(a.g, Integer.valueOf(bVar.f()));
            contentValues.put("timestamp", Long.valueOf(System.currentTimeMillis()));
            byte[] bArrA = a(bVar.g().toString());
            if (bArrA.length > c) {
                com.xiaomi.onetrack.util.p.b(a, "Too large data, discard ***");
                return;
            }
            contentValues.put("data", bArrA);
            long jInsert = writableDatabase.insert("events", null, contentValues);
            com.xiaomi.onetrack.util.p.a(a, "DB-Thread: EventManager.addEventToDatabase , row=" + jInsert);
            if (jInsert != -1) {
                if (com.xiaomi.onetrack.util.p.a) {
                    com.xiaomi.onetrack.util.p.a(a, "添加后，DB 中事件个数为 " + c());
                }
                long jCurrentTimeMillis = System.currentTimeMillis();
                if (com.xiaomi.onetrack.util.a.c.equals(bVar.e())) {
                    aa.a(jCurrentTimeMillis);
                }
                com.xiaomi.onetrack.b.n.a(false);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:41:0x0104  */
    /* JADX WARN: Code duplicated, block: B:55:0x011a A[PHI: r5
  0x011a: PHI (r5v5 android.database.Cursor) = (r5v3 android.database.Cursor), (r5v4 android.database.Cursor), (r5v7 android.database.Cursor) binds: [B:59:0x0128, B:54:0x0118, B:43:0x0108] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:65:0x0130  */
    /* JADX WARN: Not initialized variable reg: 5, insn: 0x012d: MOVE (r3 I:??[OBJECT, ARRAY]) = (r5 I:??[OBJECT, ARRAY]), block:B:63:0x012d */
    public h a(int i) {
        Cursor cursorQuery;
        Cursor cursor;
        boolean z;
        boolean z2;
        Cursor cursor2 = null;
        try {
            try {
                cursorQuery = this.i.getReadableDatabase().query("events", null, null, null, null, null, f);
                try {
                    try {
                        int columnIndex = cursorQuery.getColumnIndex("_id");
                        int columnIndex2 = cursorQuery.getColumnIndex("appid");
                        cursorQuery.getColumnIndex("package");
                        cursorQuery.getColumnIndex("event_name");
                        int columnIndex3 = cursorQuery.getColumnIndex(a.g);
                        int columnIndex4 = cursorQuery.getColumnIndex("data");
                        int columnIndex5 = cursorQuery.getColumnIndex("timestamp");
                        JSONArray jSONArray = new JSONArray();
                        ArrayList arrayList = new ArrayList();
                        boolean z3 = true;
                        int i2 = 0;
                        int i3 = 0;
                        while (cursorQuery.moveToNext()) {
                            long j2 = cursorQuery.getLong(columnIndex);
                            cursorQuery.getLong(columnIndex5);
                            cursorQuery.getString(columnIndex2);
                            byte[] blob = cursorQuery.getBlob(columnIndex4);
                            String strA = blob != null ? a(blob) : null;
                            if (z3) {
                                int i4 = cursorQuery.getInt(columnIndex3);
                                if (i4 > i) {
                                    com.xiaomi.onetrack.util.p.a(a, "No records of priority[" + i + "], first record priority=" + i4);
                                    if (cursorQuery != null) {
                                        cursorQuery.close();
                                    }
                                    return null;
                                }
                                z2 = false;
                            } else {
                                z2 = z3;
                            }
                            try {
                                jSONArray.put(new JSONObject(strA));
                                arrayList.add(Long.valueOf(j2));
                                i2++;
                            } catch (Exception e2) {
                                com.xiaomi.onetrack.util.p.b(a, "*** error ***", e2);
                            }
                            int length = i3 + strA.length();
                            if (length >= d) {
                                com.xiaomi.onetrack.util.p.a(a, "reached max len: " + length);
                                break;
                            }
                            i3 = length;
                            z3 = z2;
                        }
                        if (arrayList.size() <= 0) {
                            if (cursorQuery != null) {
                                cursorQuery.close();
                            }
                            return null;
                        }
                        if (!cursorQuery.isAfterLast()) {
                            if (cursorQuery.getInt(columnIndex3) <= i) {
                                z = false;
                            }
                            h hVar = new h(jSONArray, i2, arrayList, z);
                            if (cursorQuery != null) {
                                cursorQuery.close();
                            }
                            return hVar;
                        }
                        com.xiaomi.onetrack.util.p.a(a, "cursor isAfterLast");
                        z = true;
                        h hVar2 = new h(jSONArray, i2, arrayList, z);
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        return hVar2;
                    } catch (SQLiteBlobTooBigException e3) {
                        e = e3;
                        com.xiaomi.onetrack.util.p.b(a, "blob too big ***", e);
                        d();
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                    }
                } catch (Exception e4) {
                    e = e4;
                    com.xiaomi.onetrack.util.p.a(a, "", e);
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                }
            } catch (SQLiteBlobTooBigException e5) {
                e = e5;
                cursorQuery = null;
            } catch (Exception e6) {
                e = e6;
                cursorQuery = null;
            } catch (Throwable th) {
                th = th;
                if (cursor2 != null) {
                    cursor2.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            cursor2 = cursor;
            if (cursor2 != null) {
                cursor2.close();
            }
            throw th;
        }
    }

    public int a(ArrayList<Long> arrayList) {
        synchronized (this.i) {
            if (arrayList != null) {
                if (arrayList.size() != 0) {
                    try {
                        SQLiteDatabase writableDatabase = this.i.getWritableDatabase();
                        boolean z = true;
                        StringBuilder sb = new StringBuilder(((Long.toString(arrayList.get(0).longValue()).length() + 1) * arrayList.size()) + 16);
                        sb.append("_id").append(" in (");
                        sb.append(arrayList.get(0));
                        int size = arrayList.size();
                        for (int i = 1; i < size; i++) {
                            sb.append(z.b).append(arrayList.get(i));
                        }
                        sb.append(")");
                        int iDelete = writableDatabase.delete("events", sb.toString(), null);
                        com.xiaomi.onetrack.util.p.a(a, "deleted events count " + iDelete);
                        long jC = a().c();
                        if (jC != 0) {
                            z = false;
                        }
                        com.xiaomi.onetrack.b.n.a(z);
                        com.xiaomi.onetrack.util.p.a(a, "after delete DB record remains=" + jC);
                        return iDelete;
                    } catch (Exception e2) {
                        com.xiaomi.onetrack.util.p.b(a, "e=" + e2);
                        return 0;
                    }
                }
            }
            return 0;
        }
    }

    public void b() {
        b.a(new g(this));
    }

    public long c() {
        try {
            return DatabaseUtils.queryNumEntries(this.i.getReadableDatabase(), "events");
        } catch (Exception e2) {
            com.xiaomi.onetrack.util.p.b(a, "getTotalEventsNumberSync failed with " + e2.getMessage());
            return 0L;
        }
    }

    public static byte[] a(String str) {
        return com.xiaomi.onetrack.d.a.a(str.getBytes(), com.xiaomi.onetrack.d.d.a(com.xiaomi.onetrack.d.c.a(), true).getBytes());
    }

    public static String a(byte[] bArr) {
        return new String(com.xiaomi.onetrack.d.a.b(bArr, com.xiaomi.onetrack.d.d.a(com.xiaomi.onetrack.d.c.a(), true).getBytes()));
    }

    private void d() {
        try {
            this.i.getWritableDatabase().delete("events", null, null);
            com.xiaomi.onetrack.util.p.a(a, "delete table events");
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class a extends SQLiteOpenHelper {
        public static final String a = "onetrack";
        public static final String b = "events";
        public static final String c = "_id";
        public static final String d = "appid";
        public static final String e = "package";
        public static final String f = "event_name";
        public static final String g = "priority";
        public static final String h = "data";
        public static final String i = "timestamp";
        private static final int j = 1;
        private static final String k = "CREATE TABLE events (_id INTEGER PRIMARY KEY AUTOINCREMENT,appid TEXT,package TEXT,event_name TEXT,priority INTEGER,data BLOB,timestamp INTEGER)";

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        }

        public a(Context context) {
            super(context, a, (SQLiteDatabase.CursorFactory) null, 1);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL(k);
        }
    }
}
