package com.xiaomi.onetrack.a;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.xiaomi.onetrack.util.p;
import java.util.Calendar;

/* JADX INFO: loaded from: classes2.dex */
class c implements Runnable {
    final /* synthetic */ a a;

    c(a aVar) {
        this.a = aVar;
    }

    /* JADX WARN: Code duplicated, block: B:29:0x00ce A[Catch: all -> 0x00d2, TryCatch #1 {all -> 0x00d2, blocks: (B:12:0x00a3, B:25:0x00c9, B:29:0x00ce, B:30:0x00d1, B:24:0x00c6), top: B:34:0x000f }] */
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
                    Cursor cursorQuery = writableDatabase.query(a.C0021a.b, new String[]{"timestamp"}, "timestamp < ? ", strArr, null, null, "timestamp ASC");
                    try {
                        if (cursorQuery.getCount() != 0) {
                            p.a("AdMonitorManager", "*** deleted obsolete ad monitor count=" + writableDatabase.delete(a.C0021a.b, "timestamp < ? ", strArr));
                        }
                        if (p.a) {
                            p.a("AdMonitorManager", "after delete obsolete ad monitor record remains=" + this.a.e());
                        }
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                    } catch (Exception e) {
                        e = e;
                        cursor = cursorQuery;
                        try {
                            p.d("AdMonitorManager", "remove obsolete ad monitor failed with " + e);
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
