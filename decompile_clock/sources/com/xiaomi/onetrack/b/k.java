package com.xiaomi.onetrack.b;

import android.database.Cursor;
import android.text.TextUtils;
import com.xiaomi.onetrack.util.p;
import java.util.concurrent.Callable;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
class k implements Callable<l> {
    final /* synthetic */ String a;
    final /* synthetic */ h b;

    k(h hVar, String str) {
        this.b = hVar;
        this.a = str;
    }

    /* JADX WARN: Code duplicated, block: B:39:0x0092 A[EXC_TOP_SPLITTER, PHI: r3
  0x0092: PHI (r3v6 android.database.Cursor) = (r3v5 android.database.Cursor), (r3v10 android.database.Cursor) binds: [B:24:0x0090, B:15:0x007d] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:42:0x009d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @Override // java.util.concurrent.Callable
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public l call() throws Exception {
        Cursor cursorQuery;
        Throwable th;
        try {
            cursorQuery = this.b.b.getWritableDatabase().query(g.b, null, "app_id=?", new String[]{this.a}, null, null, null);
            try {
                try {
                    int columnIndex = cursorQuery.getColumnIndex(g.d);
                    int columnIndex2 = cursorQuery.getColumnIndex(g.e);
                    int columnIndex3 = cursorQuery.getColumnIndex(g.f);
                    int columnIndex4 = cursorQuery.getColumnIndex("timestamp");
                    if (cursorQuery.moveToNext()) {
                        l lVar = new l();
                        lVar.a = cursorQuery.getString(columnIndex);
                        String string = cursorQuery.getString(columnIndex2);
                        if (!TextUtils.isEmpty(string)) {
                            lVar.e = new JSONObject(string);
                        }
                        lVar.b = h.b(lVar.e);
                        lVar.d = cursorQuery.getString(columnIndex3);
                        lVar.c = cursorQuery.getLong(columnIndex4);
                        if (cursorQuery != null) {
                            try {
                                cursorQuery.close();
                            } catch (Exception unused) {
                                p.a("ConfigDbManager", "getConfig  cursor.close");
                            }
                        }
                        return lVar;
                    }
                    if (cursorQuery != null) {
                        try {
                            cursorQuery.close();
                        } catch (Exception unused2) {
                            p.a("ConfigDbManager", "getConfig  cursor.close");
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (cursorQuery != null) {
                        try {
                            cursorQuery.close();
                        } catch (Exception unused3) {
                            p.a("ConfigDbManager", "getConfig  cursor.close");
                        }
                    }
                    throw th;
                }
            } catch (Exception e) {
                e = e;
                p.a("ConfigDbManager", e.getMessage());
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            }
        } catch (Exception e2) {
            e = e2;
            cursorQuery = null;
        } catch (Throwable th3) {
            cursorQuery = null;
            th = th3;
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            throw th;
        }
        return null;
    }
}
