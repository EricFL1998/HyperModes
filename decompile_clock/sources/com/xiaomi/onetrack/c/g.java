package com.xiaomi.onetrack.c;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Calendar;

/* JADX INFO: loaded from: classes2.dex */
class g implements Runnable {
    final /* synthetic */ c a;

    g(c cVar) {
        this.a = cVar;
    }

    /* JADX WARN: Code duplicated, block: B:31:0x00e1 A[Catch: all -> 0x00e5, TryCatch #2 {all -> 0x00e5, blocks: (B:14:0x00b6, B:27:0x00dc, B:31:0x00e1, B:32:0x00e4, B:26:0x00d9), top: B:36:0x000f }] */
    @Override // java.lang.Runnable
    public void run() {
        Cursor cursor;
        synchronized (this.a.i) {
            try {
                try {
                    SQLiteDatabase writableDatabase = this.a.i.getWritableDatabase();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(6, calendar.get(6) - 7);
                    calendar.set(11, 0);
                    calendar.set(12, 0);
                    calendar.set(13, 0);
                    String[] strArr = {Long.toString(calendar.getTimeInMillis())};
                    Cursor cursorQuery = writableDatabase.query("events", new String[]{"timestamp"}, "timestamp < ? ", strArr, null, null, "timestamp ASC");
                    try {
                        if (cursorQuery.getCount() != 0) {
                            com.xiaomi.onetrack.util.p.a("EventManager", "*** deleted obsolete item count=" + writableDatabase.delete("events", "timestamp < ? ", strArr));
                        }
                        long jC = c.a().c();
                        com.xiaomi.onetrack.b.n.a(jC == 0);
                        com.xiaomi.onetrack.util.p.a("EventManager", "after delete obsolete record remains=" + jC);
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                    } catch (Exception e) {
                        e = e;
                        cursor = cursorQuery;
                        try {
                            com.xiaomi.onetrack.util.p.d("EventManager", "remove obsolete events failed with " + e);
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
                } catch (Exception e2) {
                    e = e2;
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
}
