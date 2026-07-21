package com.android.deskclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.android.deskclock.addition.AppInfoUtils;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.CityZoneHelper;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.LocalCityZoneUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.worldclock.CityObj;
import com.android.deskclock.worldclock.TimezoneModel;
import com.android.deskclock.worldclock.WorldClock;
import com.android.deskclock.worldclock.WorldClockEditActivity;
import com.xiaomi.onetrack.util.z;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class AdditionUtil {
    public static String HEALTH_PKG_NAME = "com.mi.health";
    private static final String LAST_WORLDCLOCK_CITY_ID = "last_worldclock_city_id";
    private static String MIHOME_PKG_NAME = "com.xiaomi.smarthome";
    private static String MIHOME_SIGNAL_SHA1 = "1da20e2bdfa678ae888e81e5ca0ec4744ebbbbbd";
    private static int MIHOME_SUPPORT_VERSION = 63401;
    public static String MIWEARABLE_PKG_NAME = "com.xiaomi.wearable";
    private static String MIWEARABLE_SIGNAL_SHA1 = "1da20e2bdfa678ae888e81e5ca0ec4744ebbbbbd";
    private static String TAG = "DC:AdditionUtil";
    private static final String WIDGET_RESOURCE_KEY_MAPPING = "widget_localId_cityId_mapping";
    public static boolean isJumpToCheckCity = false;
    private static final String CITY_NAME = "city_name";
    private static final String CITY_TIME_ZONE = "city_tz";
    private static final String GAP = "city_gap";
    private static final String TIMEZONE_DIFF = "timezone_diff";
    public static final String[] TIMEZONE_PROJECTION = {CITY_NAME, CITY_TIME_ZONE, GAP, TIMEZONE_DIFF};
    private static final String CITY_ID = "city_id";
    private static final String[] TIMEZONE_SEARCH_PROJECTION = {CITY_ID, CITY_NAME, CITY_TIME_ZONE, GAP, TIMEZONE_DIFF};
    public static final String[] RESIDENT_PROJECTION = {"isSupport"};

    public static boolean isPermissionAllow(String str) {
        String singInfo = AppInfoUtils.getSingInfo(DeskClockApp.getAppDEContext(), str, AppInfoUtils.SHA1);
        if (MIWEARABLE_PKG_NAME.equals(str) && MIWEARABLE_SIGNAL_SHA1.equals(singInfo)) {
            return true;
        }
        return (MIHOME_PKG_NAME.equals(str) && MIHOME_SIGNAL_SHA1.equals(singInfo)) || HEALTH_PKG_NAME.equals(str);
    }

    public static boolean isMiHomeSupport() {
        return Util.getPackageCode(MIHOME_PKG_NAME) >= MIHOME_SUPPORT_VERSION;
    }

    public static boolean isMiWearableSupport() {
        return Util.checkApkExist(MIWEARABLE_PKG_NAME) || Util.checkApkExist(HEALTH_PKG_NAME);
    }

    public static MatrixCursor getWorldClockCities(Cursor cursor, Context context, int i) {
        MatrixCursor matrixCursor = new MatrixCursor(TIMEZONE_PROJECTION);
        int i2 = WorldClockEditActivity.getmShowListSize(context);
        if (i2 != -1) {
            i = i2;
        }
        if (cursor == null || i == 0) {
            return matrixCursor;
        }
        ArrayList arrayList = new ArrayList();
        try {
            if (cursor.moveToFirst()) {
                int i3 = 0;
                do {
                    arrayList.add(new WorldClock(cursor));
                    i3++;
                    if (!cursor.moveToNext()) {
                        break;
                    }
                } while (i3 < i);
            }
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception unused) {
                }
            }
            List<CityObj> cityById = TimezoneModel.getCityById(arrayList);
            if (cityById.size() == 0) {
                cityById.add(LocalCityZoneUtil.getLocalTimeZone());
            }
            for (CityObj cityObj : cityById) {
                int offset = TimeZone.getTimeZone(cityObj.mTimeZone).getOffset(Calendar.getInstance().getTimeInMillis()) - TimeZone.getDefault().getOffset(Calendar.getInstance().getTimeInMillis());
                matrixCursor.addRow(new Object[]{cityObj.mCityName, cityObj.mTimeZone, getGapStrByGap(context, offset), Integer.valueOf(offset / 60000)});
            }
            return matrixCursor;
        } catch (Throwable th) {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception unused2) {
                }
            }
            throw th;
        }
    }

    /* JADX WARN: Code duplicated, block: B:13:0x0059  */
    public static MatrixCursor getAddWorldClockCity(Context context, Uri uri) {
        String widgetCityId;
        CityObj localTimeZone;
        MatrixCursor matrixCursor = new MatrixCursor(TIMEZONE_SEARCH_PROJECTION);
        String queryParameter = uri.getQueryParameter("localid");
        if (TextUtils.isEmpty(queryParameter)) {
            widgetCityId = null;
        } else if (!queryParameter.contains(z.b)) {
            widgetCityId = getWidgetCityId(context, queryParameter, LocalCityZoneUtil.getLocalTimeZone().mCityId);
        } else {
            String str = queryParameter.split(z.b)[0];
            if (TextUtils.isEmpty(str)) {
                widgetCityId = null;
            } else {
                String widgetCityId2 = getWidgetCityId(context, str, "");
                if (!TextUtils.isEmpty(widgetCityId2)) {
                    removeLocalIdMapping(context, str);
                    putWidgetCityId(context, queryParameter, widgetCityId2);
                    putWidgetCityId(context, getNewLocalId(queryParameter), widgetCityId2);
                    widgetCityId = widgetCityId2;
                } else {
                    widgetCityId = getWidgetCityId(context, queryParameter, LocalCityZoneUtil.getLocalTimeZone().mCityId);
                }
            }
        }
        if (!TextUtils.isEmpty(widgetCityId)) {
            if (CityZoneHelper.mCityTimezoneItems == null) {
                CityZoneHelper.init();
            }
            localTimeZone = CityZoneHelper.getCityTimezoneItemById(widgetCityId);
        } else {
            localTimeZone = LocalCityZoneUtil.getLocalTimeZone();
        }
        Log.d(TAG, "CityName: " + localTimeZone.mCityName);
        int offset = TimeZone.getTimeZone(localTimeZone.mTimeZone).getOffset(Calendar.getInstance().getTimeInMillis()) - TimeZone.getDefault().getOffset(Calendar.getInstance().getTimeInMillis());
        matrixCursor.addRow(new Object[]{localTimeZone.mCityId, localTimeZone.mCityName, localTimeZone.mTimeZone, getGapStrByGap(context, offset), Integer.valueOf(offset / 60000)});
        return matrixCursor;
    }

    private static void removeLocalIdMapping(Context context, String str) {
        SharedPreferences.Editor editorEdit = FBEUtil.getSharedPreferences(context, WIDGET_RESOURCE_KEY_MAPPING, 0).edit();
        editorEdit.remove(str);
        editorEdit.apply();
    }

    public static String getWidgetCityId(Context context, String str, String str2) {
        return FBEUtil.getSharedPreferences(context, WIDGET_RESOURCE_KEY_MAPPING, 0).getString(str, str2);
    }

    public static void putWidgetCityId(Context context, String str, String str2) {
        SharedPreferences.Editor editorEdit = FBEUtil.getSharedPreferences(context, WIDGET_RESOURCE_KEY_MAPPING, 0).edit();
        editorEdit.putString(str, str2);
        editorEdit.apply();
    }

    private static String getGapStrByGap(Context context, int i) {
        int i2;
        String str;
        String str2;
        String str3;
        if (i >= 0) {
            i2 = R.plurals.worldcolock_timezone_gap_fast;
            str = "+";
        } else {
            i = -i;
            i2 = R.plurals.worldcolock_timezone_gap_slow;
            str = "-";
        }
        if (i == 0) {
            return "";
        }
        long j = i;
        if (j % AlarmHelper.ARRIVING_ALARM_DURATION == 0) {
            int i3 = (int) (j / AlarmHelper.ARRIVING_ALARM_DURATION);
            if (Util.isEnglish(context)) {
                if (i3 > 1) {
                    str3 = i3 + " hrs";
                } else {
                    str3 = i3 + " hr";
                }
                return str + str3;
            }
            return context.getResources().getQuantityString(i2, i3, Integer.valueOf(i3));
        }
        float f = i / 3600000.0f;
        if (Util.isEnglish(context)) {
            if (f > 1.0f) {
                str2 = f + " hrs";
            } else {
                str2 = f + " hr";
            }
            return str + str2;
        }
        return context.getResources().getQuantityString(i2, (int) f, Float.valueOf(f));
    }

    public static String getNewLocalId(String str) {
        if (!TextUtils.isEmpty(str) && str.indexOf(44) != -1) {
            return str.substring(str.indexOf(44) + 1);
        }
        return "";
    }
}
