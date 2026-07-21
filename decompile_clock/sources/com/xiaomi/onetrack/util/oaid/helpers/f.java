package com.xiaomi.onetrack.util.oaid.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/* JADX INFO: loaded from: classes2.dex */
public class f {
    public String a(Context context) {
        String strA = "";
        try {
            Cursor cursorQuery = context.getContentResolver().query(Uri.parse("content://com.meizu.flyme.openidsdk/"), null, null, new String[]{"oaid"}, null);
            strA = a(cursorQuery);
            cursorQuery.close();
            return strA;
        } catch (Throwable th) {
            th.printStackTrace();
            return strA;
        }
    }

    private String a(Cursor cursor) {
        if (cursor == null || cursor.isClosed()) {
            return null;
        }
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(com.xiaomi.onetrack.api.g.p);
        String string = columnIndex > 0 ? cursor.getString(columnIndex) : null;
        int columnIndex2 = cursor.getColumnIndex("code");
        if (columnIndex2 > 0) {
            cursor.getInt(columnIndex2);
        }
        int columnIndex3 = cursor.getColumnIndex("expired");
        if (columnIndex3 > 0) {
            cursor.getLong(columnIndex3);
        }
        return string;
    }
}
