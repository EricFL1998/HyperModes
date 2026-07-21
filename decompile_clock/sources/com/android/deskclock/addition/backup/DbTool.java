package com.android.deskclock.addition.backup;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmDataHelper;
import com.android.deskclock.alarm.shiftalarm.ShiftAlarmTable;
import com.android.deskclock.timer.CommonTimerTableNew;
import com.xiaomi.onetrack.api.as;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class DbTool {
    public static final boolean GOLOG = true;
    public static final String LOGTAG = "DBTOOL";

    public static int json2db(SQLiteDatabase sQLiteDatabase, JSONObject jSONObject) {
        new JSONArray();
        int i = 0;
        try {
            Log.d(LOGTAG, "TABLE NUM : " + Integer.parseInt(jSONObject.getString("tables_num")));
            JSONArray jSONArray = jSONObject.getJSONArray("tables");
            int iJson2table = 0;
            while (i < jSONArray.length()) {
                try {
                    iJson2table += json2table(sQLiteDatabase, jSONArray.getJSONObject(i));
                    i++;
                } catch (JSONException e) {
                    e = e;
                    i = iJson2table;
                    Log.e(LOGTAG, "error in json2db", e);
                    return i;
                }
            }
            return iJson2table;
        } catch (JSONException e2) {
            e = e2;
        }
    }

    public static int jsonAlarmAndClockdb(SQLiteDatabase sQLiteDatabase, JSONObject jSONObject) {
        new JSONArray();
        int i = 0;
        try {
            Log.d(LOGTAG, "TABLE NUM : " + Integer.parseInt(jSONObject.getString("tables_num")));
            JSONArray jSONArray = jSONObject.getJSONArray("tables");
            int iJsonAlarmAndClockTable = 0;
            while (i < jSONArray.length()) {
                try {
                    iJsonAlarmAndClockTable += jsonAlarmAndClockTable(sQLiteDatabase, jSONArray.getJSONObject(i));
                    i++;
                } catch (JSONException e) {
                    e = e;
                    i = iJsonAlarmAndClockTable;
                    Log.e(LOGTAG, "error in json2db", e);
                    return i;
                }
            }
            ShiftAlarmDataHelper.handleRestoreShiftAlarms(sQLiteDatabase);
            return iJsonAlarmAndClockTable;
        } catch (JSONException e2) {
            e = e2;
        }
    }

    public static int json2table(SQLiteDatabase sQLiteDatabase, JSONObject jSONObject) {
        new JSONArray();
        new JSONArray();
        int i = 0;
        try {
            try {
                sQLiteDatabase.beginTransaction();
                String string = jSONObject.getString("table_name");
                sQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + string);
                Log.d(LOGTAG, "TABLE NAME : " + string);
                sQLiteDatabase.execSQL(jSONObject.getString("table_sql"));
                JSONArray jSONArray = jSONObject.getJSONArray("cols_name");
                JSONArray jSONArray2 = jSONObject.getJSONArray("rows");
                for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                    JSONArray jSONArray3 = jSONArray2.getJSONArray(i2);
                    ContentValues contentValues = new ContentValues();
                    for (int i3 = 0; i3 < jSONArray3.length(); i3++) {
                        contentValues.put(jSONArray.getString(i3), jSONArray3.getString(i3));
                    }
                    sQLiteDatabase.insert(string, null, contentValues);
                    Log.d(LOGTAG, "INSERT IN " + string + " ID=" + jSONArray3.getString(0));
                }
                i = 1;
                sQLiteDatabase.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e(LOGTAG, "error in json2table", e);
            }
            return i;
        } finally {
            sQLiteDatabase.endTransaction();
        }
    }

    public static int jsonAlarmAndClockTable(SQLiteDatabase sQLiteDatabase, JSONObject jSONObject) {
        new JSONArray();
        new JSONArray();
        int i = 0;
        try {
            try {
                sQLiteDatabase.beginTransaction();
                String string = jSONObject.getString("table_name");
                sQLiteDatabase.execSQL("DELETE FROM " + string);
                Log.d(LOGTAG, "TABLE NAME : " + string);
                ArrayList<String> tableColumns = getTableColumns(sQLiteDatabase, string);
                if (tableColumns != null && !tableColumns.isEmpty()) {
                    Log.d(LOGTAG, string + " current colums ：" + tableColumns.toString());
                    JSONArray jSONArray = jSONObject.getJSONArray("cols_name");
                    JSONArray jSONArray2 = jSONObject.getJSONArray("rows");
                    for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                        JSONArray jSONArray3 = jSONArray2.getJSONArray(i2);
                        ContentValues contentValues = new ContentValues();
                        for (int i3 = 0; i3 < jSONArray3.length(); i3++) {
                            String string2 = jSONArray.getString(i3);
                            Log.d(LOGTAG, "back sColName:" + string2);
                            if (tableColumns.contains(string2)) {
                                contentValues.put(jSONArray.getString(i3), jSONArray3.getString(i3));
                            }
                        }
                        contentValues.remove("_id");
                        sQLiteDatabase.insert(string, null, contentValues);
                        Log.d(LOGTAG, "INSERT IN " + string + " ID=" + jSONArray3.getString(0));
                    }
                    i = 1;
                    sQLiteDatabase.setTransactionSuccessful();
                    return i;
                }
                Log.d(LOGTAG, string + " colums is empty ");
                return 0;
            } catch (Exception e) {
                Log.e(LOGTAG, "error in json2table", e);
            }
        } finally {
            sQLiteDatabase.endTransaction();
        }
    }

    public static JSONObject db2json(SQLiteDatabase sQLiteDatabase, String str) {
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        JSONArray jSONArray2 = new JSONArray();
        Cursor cursorRawQuery = sQLiteDatabase.rawQuery("select * from sqlite_master", null);
        int i = 0;
        while (cursorRawQuery.moveToNext()) {
            String string = cursorRawQuery.getString(cursorRawQuery.getColumnIndex(as.a));
            String string2 = cursorRawQuery.getString(cursorRawQuery.getColumnIndex("sql"));
            Log.d(LOGTAG, "TABLE NAME : " + string);
            if (string.equals("alarms") || string.equals("worldclocks") || string.equals(CommonTimerTableNew.TABLE_NAME) || string.equals(ShiftAlarmTable.TABLE_NAME)) {
                JSONObject jSONObjectTable2json = table2json(sQLiteDatabase, string, string2);
                if (jSONObjectTable2json != null) {
                    i++;
                    jSONArray.put(string);
                    jSONArray2.put(jSONObjectTable2json);
                }
            }
        }
        cursorRawQuery.close();
        try {
            jSONObject.put("jsondb_format", "1");
            if (str == null || str.isEmpty()) {
                jSONObject.put("db_name", "database.sqlite");
            } else {
                jSONObject.put("db_name", str);
            }
            jSONObject.put("tables_num", String.valueOf(i));
            jSONObject.put("tables_name", jSONArray);
            jSONObject.put("tables", jSONArray2);
        } catch (JSONException e) {
            Log.e(LOGTAG, "error in db2json", e);
        }
        return jSONObject;
    }

    public static JSONObject table2json(SQLiteDatabase sQLiteDatabase, String str, String str2) {
        Cursor cursorRawQuery;
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        JSONArray jSONArray2 = new JSONArray();
        String str3 = "select * from " + str;
        if (str.equals("alarms")) {
            str3 = "select * from " + str + " where type=0";
        }
        try {
            cursorRawQuery = sQLiteDatabase.rawQuery(str3, null);
        } catch (SQLiteException e) {
            Log.e(LOGTAG, "failed to find table" + str, e);
            cursorRawQuery = null;
        }
        if (cursorRawQuery == null) {
            return null;
        }
        int i = -1;
        while (cursorRawQuery.moveToNext()) {
            int i2 = 0;
            if (i == -1) {
                for (int i3 = 0; i3 < cursorRawQuery.getColumnCount(); i3++) {
                    jSONArray2.put(cursorRawQuery.getColumnName(i3));
                }
            }
            JSONArray jSONArray3 = new JSONArray();
            while (i2 < cursorRawQuery.getColumnCount()) {
                jSONArray3.put(cursorRawQuery.getString(i2));
                i2++;
            }
            jSONArray.put(jSONArray3);
            i = i2;
        }
        try {
            cursorRawQuery.close();
        } catch (Exception e2) {
            Log.e(LOGTAG, "failed to close cursor", e2);
        }
        try {
            jSONObject.put("table_name", str);
            if (str2 != null && !str2.isEmpty()) {
                jSONObject.put("table_sql", str2);
            }
            jSONObject.put("cols_name", jSONArray2);
            jSONObject.put("rows", jSONArray);
        } catch (JSONException e3) {
            Log.e(LOGTAG, "error in table2json", e3);
        }
        return jSONObject;
    }

    private static ArrayList<String> getTableColumns(SQLiteDatabase sQLiteDatabase, String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursorRawQuery = null;
        try {
            cursorRawQuery = sQLiteDatabase.rawQuery("PRAGMA table_info(" + str + ")", null);
            if (cursorRawQuery != null && cursorRawQuery.moveToFirst()) {
                do {
                    arrayList.add(cursorRawQuery.getString(cursorRawQuery.getColumnIndexOrThrow(as.a)));
                } while (cursorRawQuery.moveToNext());
            }
            return arrayList;
        } finally {
            if (cursorRawQuery != null) {
                cursorRawQuery.close();
            }
        }
    }
}
