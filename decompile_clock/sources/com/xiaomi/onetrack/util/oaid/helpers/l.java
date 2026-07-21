package com.xiaomi.onetrack.util.oaid.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.xiaomi.onetrack.util.p;

/* JADX INFO: loaded from: classes2.dex */
public class l {
    private static final String a = "VivoDeviceIDHelper";

    public String a(Context context) {
        String string = "";
        try {
            Cursor cursorQuery = context.getContentResolver().query(Uri.parse("content://com.vivo.vms.IdProvider/IdentifierId/OAID"), null, null, null, null);
            if (cursorQuery != null) {
                if (cursorQuery.moveToNext()) {
                    string = cursorQuery.getString(cursorQuery.getColumnIndex(com.xiaomi.onetrack.api.g.p));
                }
                cursorQuery.close();
            }
        } catch (Exception e) {
            p.a(a, e.getMessage());
        }
        return string;
    }
}
