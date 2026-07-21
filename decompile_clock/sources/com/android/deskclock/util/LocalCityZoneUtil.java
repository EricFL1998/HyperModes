package com.android.deskclock.util;

import android.database.Cursor;
import android.net.Uri;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.worldclock.CityObj;
import com.android.deskclock.worldclock.WorldClockEditActivity;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class LocalCityZoneUtil {
    private static final String TAG = "DC:LocalCityZoneUtil";
    private static final String ZONE_DISPLAY_NAME = "zone_timename";
    private static final String ZONE_ID = "zone_id";
    public static String mZoneDispalyName;
    private static String mZoneId;
    private static final Uri LOCAL_CITY_ZONE_URI = Uri.parse("content://com.android.settings.provider.MiuiSettingsDataProvider/zone_info");
    private static String mDefaultName = DeskClockApp.getAppDEContext().getString(R.string.world_edit_local_default);

    private static String getLocalCityZone() {
        try {
            Cursor cursorQuery = DeskClockApp.getAppDEContext().getContentResolver().query(LOCAL_CITY_ZONE_URI, null, null, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToNext()) {
                        mZoneId = cursorQuery.getString(cursorQuery.getColumnIndex(ZONE_ID));
                        mZoneDispalyName = cursorQuery.getString(cursorQuery.getColumnIndex(ZONE_DISPLAY_NAME));
                        android.util.Log.d(TAG, "getLocalCityZone mZoneId : " + mZoneId + "  mZoneDispalyName :" + mZoneDispalyName);
                        return mZoneDispalyName;
                    }
                } finally {
                    cursorQuery.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDefaultName;
    }

    public static CityObj getLocalTimeZone() {
        String id = TimeZone.getDefault().getID();
        String localCityZone = getLocalCityZone();
        android.util.Log.d(TAG, "tz:" + TimeZone.getDefault());
        return new CityObj(localCityZone, id, WorldClockEditActivity.LOCAL_CITY_ID, "");
    }
}
