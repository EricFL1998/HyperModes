package com.android.deskclock.util;

import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.addition.MiuiSdk;

/* JADX INFO: loaded from: classes.dex */
public class ResidentCityUtils {
    public static final String AUTO_DUAL_CLOCK = "auto_dual_clock";
    public static final String RESIDENT_ID = "resident_id";
    public static final String RESIDENT_TIMEZONE = "resident_timezone";
    private static final String ZONE_DISPLAY_NAME = "zone_displayname";
    private static final String ZONE_ID = "zone_id";
    private static String mZoneDispalyName;
    private static String mZoneId;
    private static final Uri LOCAL_RESIDENT_CITY_URI = Uri.parse("content://com.android.settings.provider.MiuiSettingsDataProvider/dual_zone_info");
    public static String IS_SHOW_RESIDENT_CITY = "is_show_resident_city";

    public static String getResidentCity() {
        try {
            Cursor cursorQuery = DeskClockApp.getAppDEContext().getContentResolver().query(LOCAL_RESIDENT_CITY_URI, null, null, null, null);
            if (cursorQuery != null) {
                try {
                    if (!cursorQuery.moveToNext()) {
                        return null;
                    }
                    mZoneId = cursorQuery.getString(cursorQuery.getColumnIndex(ZONE_ID));
                    String string = cursorQuery.getString(cursorQuery.getColumnIndex(ZONE_DISPLAY_NAME));
                    mZoneDispalyName = string;
                    return string;
                } finally {
                    cursorQuery.close();
                }
            }
            Log.d("getResidentCity(): cursor is null");
            return null;
        } catch (Exception e) {
            Log.e("getResidentCity(): get resident city failed", e);
            return null;
        }
    }

    public static String getZoneId() {
        return mZoneId;
    }

    public static boolean isSupportResidentCity() {
        return (MiuiSdk.isSuperLiteMode() || MiuiSdk.isLiteV1StockMode() || !isResidentCity()) ? false : true;
    }

    public static boolean isResidentCity() {
        return Settings.System.getInt(DeskClockApp.getAppDEContext().getContentResolver(), AUTO_DUAL_CLOCK, 0) == 1;
    }

    public static boolean isShowResidentCity(boolean z, String str) {
        boolean z2 = !TextUtils.equals(str, mZoneId) && z;
        FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext()).edit().putBoolean(IS_SHOW_RESIDENT_CITY, !TextUtils.equals(str, mZoneId) && isResidentCity()).apply();
        return z2;
    }
}
