package com.xiaomi.onetrack.a;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteBlobTooBigException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.xiaomi.onetrack.b.n;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.z;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes2.dex */
public class a {
    private static final String a = "AdMonitorManager";
    private static final int b = 204800;
    private static final int c = 100;
    private static final int d = 4;
    private static final int e = 300;
    private static final String f = "_id ASC";
    private static final int g = 7;
    private static a h;
    private final C0021a i = new C0021a(com.xiaomi.onetrack.f.a.a());

    public static a a() {
        if (h == null) {
            a(com.xiaomi.onetrack.f.a.a());
        }
        return h;
    }

    public static void a(Context context) {
        if (h == null) {
            synchronized (a.class) {
                if (h == null) {
                    h = new a();
                }
            }
        }
    }

    private a() {
        c();
    }

    public void a(com.xiaomi.onetrack.f.b bVar) {
        try {
            com.xiaomi.onetrack.a.a.a.a(new b(this, bVar));
        } catch (Throwable th) {
            p.a(a, "filterAdMonitor Throwable：" + th.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long a(com.xiaomi.onetrack.a.b.a aVar) {
        synchronized (this.i) {
            try {
                if (!aVar.h()) {
                    p.c(a, "addAdMonitorToDatabase event is inValid, event:" + aVar.a());
                    return -1L;
                }
                SQLiteDatabase writableDatabase = this.i.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put("appid", aVar.e());
                contentValues.put("package", aVar.f());
                contentValues.put("event_name", aVar.a());
                contentValues.put("timestamp", Long.valueOf(aVar.d()));
                contentValues.put(C0021a.g, aVar.c());
                long jInsert = writableDatabase.insert(C0021a.b, null, contentValues);
                p.a(a, "DB-Thread: AdMonitorManager.addAdMonitorToDatabase , row=" + jInsert);
                if (p.a) {
                    p.a(a, "添加后，ad monitor url 中事件个数为 " + e());
                }
                return jInsert;
            } catch (Throwable th) {
                p.a(a, "addAdMonitorToDatabase Throwable：" + th.getMessage());
                return -1L;
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:38:0x00e8 A[Catch: all -> 0x0105, PHI: r10
  0x00e8: PHI (r10v4 android.database.Cursor) = (r10v3 android.database.Cursor), (r10v5 android.database.Cursor) binds: [B:37:0x00e6, B:43:0x00f8] A[DONT_GENERATE, DONT_INLINE], TRY_ENTER, TRY_LEAVE, TryCatch #2 {all -> 0x0105, blocks: (B:25:0x00d2, B:26:0x00d5, B:38:0x00e8, B:45:0x00fb, B:50:0x0101, B:51:0x0104), top: B:55:0x0006 }] */
    /* JADX WARN: Not initialized variable reg: 10, insn: 0x00fe: MOVE (r9 I:??[OBJECT, ARRAY]) = (r10 I:??[OBJECT, ARRAY]), block:B:48:0x00fe */
    public com.xiaomi.onetrack.a.c.a b() {
        Cursor cursor;
        Cursor cursorQuery;
        int i;
        synchronized (this.i) {
            Cursor cursor2 = null;
            try {
                try {
                    try {
                        cursorQuery = this.i.getReadableDatabase().query(C0021a.b, null, null, null, null, null, f);
                        try {
                            int columnIndex = cursorQuery.getColumnIndex("_id");
                            cursorQuery.getColumnIndex("appid");
                            cursorQuery.getColumnIndex("package");
                            cursorQuery.getColumnIndex("event_name");
                            int columnIndex2 = cursorQuery.getColumnIndex("timestamp");
                            int columnIndex3 = cursorQuery.getColumnIndex(C0021a.g);
                            int columnIndex4 = cursorQuery.getColumnIndex(C0021a.i);
                            ArrayList arrayList = new ArrayList();
                            long jCurrentTimeMillis = System.currentTimeMillis();
                            int i2 = 0;
                            while (true) {
                                if (!cursorQuery.moveToNext()) {
                                    i = i2;
                                    break;
                                }
                                int i3 = cursorQuery.getInt(columnIndex4);
                                long j = cursorQuery.getLong(columnIndex2);
                                if (a(jCurrentTimeMillis, j, i3)) {
                                    int i4 = cursorQuery.getInt(columnIndex);
                                    String string = cursorQuery.getString(columnIndex3);
                                    com.xiaomi.onetrack.a.b.a aVar = new com.xiaomi.onetrack.a.b.a();
                                    aVar.b(string);
                                    aVar.a(i4);
                                    aVar.a(j);
                                    aVar.b(i3);
                                    arrayList.add(aVar);
                                    i = i2 + 1;
                                    if (i >= 100) {
                                        break;
                                    }
                                    i2 = i;
                                }
                            }
                            boolean z = true;
                            if (arrayList.size() > 0) {
                                p.a(a, "get ad monitor size :" + arrayList.size());
                                if (cursorQuery.isAfterLast()) {
                                    p.a(a, "cursor isAfterLast");
                                } else {
                                    z = false;
                                }
                            }
                            com.xiaomi.onetrack.a.c.a aVar2 = new com.xiaomi.onetrack.a.c.a(i, arrayList, z);
                            if (cursorQuery != null) {
                                cursorQuery.close();
                            }
                            return aVar2;
                        } catch (SQLiteBlobTooBigException e2) {
                            e = e2;
                            p.b(a, "blob too big ***", e);
                            f();
                            if (cursorQuery != null) {
                                cursorQuery.close();
                            }
                            return null;
                        } catch (Exception e3) {
                            e = e3;
                            p.a(a, "", e);
                            if (cursorQuery != null) {
                                cursorQuery.close();
                            }
                            return null;
                        }
                    } catch (SQLiteBlobTooBigException e4) {
                        e = e4;
                        cursorQuery = null;
                    } catch (Exception e5) {
                        e = e5;
                        cursorQuery = null;
                    } catch (Throwable th) {
                        th = th;
                        if (cursor2 != null) {
                            cursor2.close();
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    throw th2;
                }
            } catch (Throwable th3) {
                th = th3;
                cursor2 = cursor;
            }
        }
    }

    public int a(ArrayList<Integer> arrayList) {
        synchronized (this.i) {
            if (arrayList != null) {
                if (arrayList.size() != 0) {
                    try {
                        SQLiteDatabase writableDatabase = this.i.getWritableDatabase();
                        StringBuilder sb = new StringBuilder(((Long.toString(arrayList.get(0).intValue()).length() + 1) * arrayList.size()) + 16);
                        sb.append("_id").append(" in (");
                        sb.append(arrayList.get(0));
                        int size = arrayList.size();
                        for (int i = 1; i < size; i++) {
                            sb.append(z.b).append(arrayList.get(i));
                        }
                        sb.append(")");
                        int iDelete = writableDatabase.delete(C0021a.b, sb.toString(), null);
                        p.a(a, "*** *** deleted ad monitor count " + iDelete);
                        if (p.a) {
                            p.a(a, "after delete ad monitor record remains=" + e());
                        }
                        return iDelete;
                    } catch (Exception e2) {
                        p.b(a, "e=" + e2);
                        return 0;
                    }
                }
            }
            return 0;
        }
    }

    public void b(ArrayList<Integer> arrayList) {
        String str;
        String str2;
        synchronized (this.i) {
            if (arrayList != null) {
                try {
                    if (arrayList.size() > 0) {
                        SQLiteDatabase writableDatabase = null;
                        try {
                            try {
                                writableDatabase = this.i.getWritableDatabase();
                                writableDatabase.beginTransaction();
                                Iterator<Integer> it = arrayList.iterator();
                                while (it.hasNext()) {
                                    writableDatabase.execSQL(String.format("update %s set %s = %s + 1 where %s = %s", C0021a.b, C0021a.i, C0021a.i, "_id", Integer.valueOf(it.next().intValue())));
                                }
                                writableDatabase.setTransactionSuccessful();
                                if (writableDatabase != null) {
                                    try {
                                        writableDatabase.endTransaction();
                                        writableDatabase.close();
                                    } catch (Exception e2) {
                                        str = a;
                                        str2 = "addAdMonitorsRetryCount endTransaction error: " + e2.getMessage();
                                        p.b(str, str2);
                                    }
                                }
                            } catch (Exception e3) {
                                p.b(a, "addAdMonitorsRetryCount Exception: " + e3.getMessage());
                                if (writableDatabase != null) {
                                    try {
                                        writableDatabase.endTransaction();
                                        writableDatabase.close();
                                    } catch (Exception e4) {
                                        str = a;
                                        str2 = "addAdMonitorsRetryCount endTransaction error: " + e4.getMessage();
                                        p.b(str, str2);
                                    }
                                }
                            }
                        } catch (Throwable th) {
                            if (writableDatabase != null) {
                                try {
                                    writableDatabase.endTransaction();
                                    writableDatabase.close();
                                } catch (Exception e5) {
                                    p.b(a, "addAdMonitorsRetryCount endTransaction error: " + e5.getMessage());
                                }
                            }
                            throw th;
                        }
                    }
                } catch (Throwable th2) {
                    p.a(a, "addAdMonitorsRetryCount Throwable:" + th2.getMessage());
                }
            }
        }
    }

    public void c() {
        com.xiaomi.onetrack.a.a.a.a(new c(this));
    }

    /* JADX WARN: Code duplicated, block: B:29:0x009c A[Catch: all -> 0x00a0, TryCatch #0 {all -> 0x00a0, blocks: (B:12:0x0071, B:25:0x0097, B:29:0x009c, B:30:0x009f, B:24:0x0094), top: B:34:0x000b }] */
    public void d() {
        Cursor cursor;
        synchronized (this.i) {
            try {
                try {
                    SQLiteDatabase writableDatabase = this.i.getWritableDatabase();
                    String[] strArr = {Long.toString(4L)};
                    Cursor cursorQuery = writableDatabase.query(C0021a.b, new String[]{"timestamp"}, "send_count >= ? ", strArr, null, null, f);
                    try {
                        if (cursorQuery.getCount() != 0) {
                            p.a(a, "*** deleted obsolete ad monitor count=" + writableDatabase.delete(C0021a.b, "send_count >= ? ", strArr));
                        }
                        if (p.a) {
                            p.a(a, "after delete obsolete ad monitor record remains=" + e());
                        }
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                    } catch (Exception e2) {
                        e = e2;
                        cursor = cursorQuery;
                        try {
                            p.d(a, "remove obsolete ad monitor failed with " + e);
                            if (cursor != null) {
                                cursor.close();
                            }
                        } catch (Throwable th) {
                            th = th;
                            if (cursor != null) {
                                cursor.close();
                            }
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        cursor = cursorQuery;
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                } catch (Exception e3) {
                    e = e3;
                    cursor = null;
                } catch (Throwable th3) {
                    th = th3;
                    cursor = null;
                }
            } catch (Throwable th4) {
                throw th4;
            }
        }
    }

    public long e() {
        try {
            return DatabaseUtils.queryNumEntries(this.i.getReadableDatabase(), C0021a.b);
        } catch (Exception e2) {
            p.b(a, "getTotalEventsNumberSync failed with " + e2.getMessage());
            return 0L;
        }
    }

    private void f() {
        try {
            this.i.getWritableDatabase().delete(C0021a.b, null, null);
            p.a(a, "delete table monitor");
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public boolean a(long j, long j2, int i) {
        if (i <= 0) {
            return true;
        }
        if (i < 4) {
            return Math.abs(j - j2) >= ((long) n.a(i - 1));
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: com.xiaomi.onetrack.a.a$a, reason: collision with other inner class name */
    static class C0021a extends SQLiteOpenHelper {
        public static final String a = "onetrack_ad";
        public static final String b = "monitor";
        public static final String c = "_id";
        public static final String d = "appid";
        public static final String e = "package";
        public static final String f = "event_name";
        public static final String g = "url";
        public static final String h = "timestamp";
        public static final String i = "send_count";
        private static final int j = 1;
        private static final String k = "CREATE TABLE monitor (_id INTEGER PRIMARY KEY AUTOINCREMENT,appid TEXT,package TEXT,event_name TEXT,url TEXT,send_count INTEGER DEFAULT 0,timestamp INTEGER)";

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i2, int i3) {
        }

        public C0021a(Context context) {
            super(context, a, (SQLiteDatabase.CursorFactory) null, 1);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL(k);
        }
    }
}
