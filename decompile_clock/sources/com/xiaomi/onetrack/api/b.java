package com.xiaomi.onetrack.api;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes2.dex */
class b implements Callable<String> {
    final /* synthetic */ Intent a;
    final /* synthetic */ a b;

    b(a aVar, Intent intent) {
        this.b = aVar;
        this.a = intent;
    }

    /* JADX WARN: Code duplicated, block: B:21:0x00d0 A[PHI: r8 r9
  0x00d0: PHI (r8v4 java.lang.String) = (r8v2 java.lang.String), (r8v11 java.lang.String) binds: [B:34:0x00ee, B:20:0x00ce] A[DONT_GENERATE, DONT_INLINE]
  0x00d0: PHI (r9v4 android.database.Cursor) = (r9v3 android.database.Cursor), (r9v7 android.database.Cursor) binds: [B:34:0x00ee, B:20:0x00ce] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // java.util.concurrent.Callable
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public String call() throws Exception {
        Cursor cursor;
        Cursor cursorQuery;
        String string = "";
        try {
            try {
                try {
                    if (!a.f()) {
                        return "";
                    }
                    String stringExtra = this.a.getStringExtra("package");
                    String stringExtra2 = this.a.getStringExtra("installer");
                    String stringExtra3 = this.a.getStringExtra("miuiActiveId");
                    long longExtra = this.a.getLongExtra("miuiActiveTime", -1L);
                    long longExtra2 = this.a.getLongExtra("activeTime", -1L);
                    int intExtra = this.a.getIntExtra("userId", -1);
                    String strValueOf = String.valueOf(System.currentTimeMillis());
                    try {
                        Uri.Builder builderBuildUpon = Uri.parse("content://com.miui.analytics.OneTrackProvider/traceId").buildUpon();
                        try {
                            builderBuildUpon.appendQueryParameter("pkg", a.j);
                            builderBuildUpon.appendQueryParameter("sign", com.xiaomi.onetrack.d.a.a("traceId" + a.j + strValueOf));
                            builderBuildUpon.appendQueryParameter("package", stringExtra);
                            builderBuildUpon.appendQueryParameter("installer", stringExtra2);
                            builderBuildUpon.appendQueryParameter("miuiActiveId", stringExtra3);
                            builderBuildUpon.appendQueryParameter("miuiActiveTime", String.valueOf(longExtra));
                            builderBuildUpon.appendQueryParameter("activeTime", String.valueOf(longExtra2));
                            builderBuildUpon.appendQueryParameter("userId", String.valueOf(intExtra));
                            builderBuildUpon.appendQueryParameter("queryTime", strValueOf);
                            cursorQuery = this.b.i.getContentResolver().query(builderBuildUpon.build(), null, null, null, null);
                            string = "";
                            if (cursorQuery != null) {
                                while (cursorQuery.moveToNext()) {
                                    try {
                                        string = cursorQuery.getString(0);
                                    } catch (Exception e) {
                                        e = e;
                                        com.xiaomi.onetrack.util.p.b("AppActiveBroadcastManager", "exception while getTraceId", e);
                                        if (cursorQuery != null) {
                                        }
                                        return string;
                                    }
                                }
                            }
                            if (cursorQuery != null) {
                                cursorQuery.close();
                            }
                        } catch (Exception e2) {
                            e = e2;
                            string = "";
                            cursorQuery = null;
                            com.xiaomi.onetrack.util.p.b("AppActiveBroadcastManager", "exception while getTraceId", e);
                            if (cursorQuery != null) {
                                cursorQuery.close();
                            }
                            return string;
                        } catch (Throwable th) {
                            th = th;
                        }
                    } catch (Exception e3) {
                        e = e3;
                    }
                    return string;
                } catch (Exception e4) {
                    e = e4;
                }
            } catch (Throwable th2) {
                th = th2;
            }
            cursor = null;
        } catch (Throwable th3) {
            th = th3;
        }
        if (0 != 0) {
            cursor.close();
        }
        throw th;
    }
}
