package com.android.deskclock.addition.ringtone.weather;

import android.text.TextUtils;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.addition.resource.ExternalResourceUtils;
import com.android.deskclock.addition.weather.WeatherInfo;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.TimeUtil;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import com.android.deskclock.worldclock.WorldClockEditActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public abstract class WeatherRingtoneBase {
    public final String TAG = "DC:WeatherRingtone";
    protected final String SOURCE_PREFIX = "Alarm";
    protected final String SOURCE_SEPARATION = "_";
    protected final String SPEECH_REGARD = "Regard";
    protected final String SPEECH_HOUR_PREFIX = "Hour";
    protected final String SPEECH_MIN_PREFIX = "Min";
    protected final String SPEECH_WEATHER_PREFIX = "Weather";
    protected final String SPEECH_WEATHER_UNKNOWN = "UNKNOWN";
    protected final String SPEECH_WEATHER_RAIN = "Rain";
    protected final String SPEECH_WEATHER_SUNNY = "Sunny";
    protected final String SPEECH_WEATHER_OVERCAST = "Overcast";
    protected final String SPEECH_WEATHER_WIND = "Wind";
    protected final String SPEECH_WEATHER_SNOW = "Snow";
    protected final String SPEECH_WEATHER_CLOUDY = "Cloudy";
    protected final String SPEECH_TEMP_PREFIX = "Temp";
    protected final String SPEECH_TEMP_LOW_PREFIX = "TempF";
    protected final String SPEECH_TEMP_HIGH_PREFIX = "TempS";
    protected final String PREFIX_MINUS = WorldClockEditActivity.LOCAL_CITY_ID;
    protected final String SPEECH_SUFFIX = ".mp3";
    protected final String ROOT_PATH = "dynamic_alarm";
    protected final String RESOURCE_MIDDLE_NORMAL = "Normal";
    protected final String RESOURCE_MIDDLE_FAST = "Fast";
    protected final String TIME_AXIS_DATA_TYPE = "type";
    protected final String TIME_AXIS_DATA_TIME = "time";

    public abstract String createWeatherAlarm(WeatherInfo weatherInfo, ArrayList<WeatherRingtonePiece> arrayList);

    protected abstract int getVersion();

    public abstract String getWeatherAlarmBackground(WeatherInfo weatherInfo);

    public abstract ArrayList<WeatherRingtonePiece> getWeatherAlarmModel(WeatherInfo weatherInfo);

    public enum ResourceType {
        REGARD("Regard"),
        HOUR("Hour"),
        MINUTE("Min"),
        WEATHER("Weather"),
        TEMP_PREF("Temp"),
        TEMP_LOW("TempF"),
        REACH("Reach"),
        TEMP_HIGH("TempS"),
        REMARKS("Remarks"),
        BACKGROUND("Background"),
        BIRTHDAY("Birthday");

        public String mValue;

        ResourceType(String str) {
            this.mValue = str;
        }
    }

    protected void addWeatherStatistics(String str) {
        try {
            StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.KEY_WEATHER_RINGTONE, str);
            StatHelper.trackEvent(StatHelper.CATEGORY_DESKCLOCK_COMMON, StatHelper.KEY_WEATHER_RINGTONE_VERSION, String.valueOf(getVersion()));
            OneTrackStatHelper.trackStringEvent(String.valueOf(getVersion()), OneTrackStatHelper.WEATHER_RINGTONE_VERSION);
        } catch (Exception e) {
            Log.d("statistics weather failed: " + e.getMessage());
        }
    }

    protected String getExternalPath() {
        try {
            File file = new File(DeskClockApp.getAppDEContext().getFilesDir().getAbsolutePath() + File.separator + ExternalResourceUtils.getModuleNameWithVersion(getVersion()) + File.separator + ExternalResourceUtils.EXTERNAL_PATH_ASSERT);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("getRootPath error", e);
            return null;
        }
    }

    protected String appendString(String... strArr) {
        StringBuilder sb = new StringBuilder();
        for (String str : strArr) {
            sb.append(str);
        }
        return sb.toString();
    }

    protected String getMiddleStrByWeather(String str) {
        if (!TextUtils.isEmpty(str)) {
            return TextUtils.equals(str, "Sunny") ? "Fast" : "Normal";
        }
        Log.e("getMiddleStrByWeather error, null weather");
        return "Normal";
    }

    protected String getResourcePref(String str, String str2) {
        return appendString("Alarm", "_", str, "_", str2);
    }

    protected String getPrefPath(String str) {
        return str + File.separator;
    }

    protected String getHourResource() {
        int i = Calendar.getInstance().get(10);
        Log.d("getHourResource=" + i);
        return appendString("Hour", "_", i + "");
    }

    protected String getMinuteResource() {
        int minute = TimeUtil.getMinute();
        Log.d("getMinuteResource=" + minute);
        return appendString("Min", "_", minute + "");
    }

    protected String getTempLowResource(WeatherInfo weatherInfo) {
        int i;
        if (weatherInfo == null) {
            Log.e("getTempLowResource null info");
            i = 0;
        } else {
            i = weatherInfo.tempLow;
        }
        return appendString("TempF", "_", getTempNumber(i));
    }

    protected String getTempHighResource(WeatherInfo weatherInfo) {
        int i;
        if (weatherInfo == null) {
            Log.e("getTempHighResource null info");
            i = 0;
        } else {
            i = weatherInfo.tempHigh;
        }
        return appendString("TempS", "_", getTempNumber(i));
    }

    protected String getTempNumber(int i) {
        StringBuilder sb;
        StringBuilder sbAppend;
        if (i < 0) {
            sb = new StringBuilder(WorldClockEditActivity.LOCAL_CITY_ID);
            sbAppend = sb.append(Math.abs(i));
        } else {
            sb = new StringBuilder();
            sbAppend = sb.append(i).append("");
        }
        return sbAppend.toString();
    }

    protected String addSuffix(String str) {
        return str + ".mp3";
    }
}
