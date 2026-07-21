package com.android.deskclock.worldclock;

import android.content.SharedPreferences;
import com.android.deskclock.util.AlarmHelper;
import java.util.Calendar;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class CityObj {
    private static final String CITY_ID = "city_id_";
    private static final String CITY_NAME = "city_name_";
    private static final String CITY_TIME_ZONE = "city_tz_";
    private static final String COUNTRY_NAME = "country_name_";
    public int id;
    public boolean isHeader;
    public String mCityId;
    public String mCityName;
    public String mCountryName;
    public String mTimeZone;
    private String mTimeZoneDisplay;

    public CityObj(String str, String str2, String str3, String str4) {
        this.mCityName = str;
        this.mTimeZone = str2;
        this.mCityId = str3;
        this.mCountryName = str4;
    }

    public String toString() {
        return "CityObj{name=" + this.mCityName + ", timezone=" + this.mTimeZone + ", id=" + this.mCityId + ", country=" + this.mCountryName + '}';
    }

    public CityObj(SharedPreferences sharedPreferences, int i) {
        this.mCityName = sharedPreferences.getString(CITY_NAME + i, null);
        this.mTimeZone = sharedPreferences.getString(CITY_TIME_ZONE + i, null);
        this.mCityId = sharedPreferences.getString(CITY_ID + i, null);
        this.mCountryName = sharedPreferences.getString(COUNTRY_NAME + i, null);
    }

    public void saveCityToSharedPrefs(SharedPreferences.Editor editor, int i) {
        editor.putString(CITY_NAME + i, this.mCityName);
        editor.putString(CITY_TIME_ZONE + i, this.mTimeZone);
        editor.putString(CITY_ID + i, this.mCityId);
        editor.putString(COUNTRY_NAME + i, this.mCountryName);
    }

    public String getTimezoneDisplay() {
        if (this.mTimeZoneDisplay == null) {
            this.mTimeZoneDisplay = generateTimezoneName(this.mTimeZone);
        }
        return this.mTimeZoneDisplay;
    }

    private String generateTimezoneName(String str) {
        int offset = TimeZone.getTimeZone(str).getOffset(Calendar.getInstance().getTimeInMillis());
        int iAbs = Math.abs(offset);
        StringBuilder sb = new StringBuilder("GMT");
        if (offset < 0) {
            sb.append('-');
        } else {
            sb.append('+');
        }
        long j = iAbs;
        sb.append(j / AlarmHelper.ARRIVING_ALARM_DURATION);
        sb.append(':');
        long j2 = (j / 60000) % 60;
        if (j2 < 10) {
            sb.append('0');
        }
        sb.append(j2);
        return sb.toString();
    }
}
