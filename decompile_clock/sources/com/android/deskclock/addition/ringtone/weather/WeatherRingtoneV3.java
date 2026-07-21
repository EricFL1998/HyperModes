package com.android.deskclock.addition.ringtone.weather;

import android.text.TextUtils;
import com.android.deskclock.addition.weather.WeatherInfo;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class WeatherRingtoneV3 extends WeatherRingtoneV2 {
    private final String TIME_AXIS_FILE = "alarm/weather_alarm_time_axis_v2.json";
    public final int WEATHER_REMARK_LINE = 25;
    private final String SPEECH_REMARKS_SUNNY_HIGH = "High";
    private final String SPEECH_REMARKS_SUNNY_LOW = "Low";

    @Override // com.android.deskclock.addition.ringtone.weather.WeatherRingtoneV2, com.android.deskclock.addition.ringtone.weather.WeatherRingtoneBase
    protected int getVersion() {
        return 3;
    }

    @Override // com.android.deskclock.addition.ringtone.weather.WeatherRingtoneV2
    protected String getAxis() {
        return "alarm/weather_alarm_time_axis_v2.json";
    }

    @Override // com.android.deskclock.addition.ringtone.weather.WeatherRingtoneV2
    protected String getAlarmResource(String str, WeatherInfo weatherInfo) {
        String weatherType;
        if (TextUtils.equals(str, WeatherRingtoneBase.ResourceType.REGARD.mValue) || TextUtils.equals(str, WeatherRingtoneBase.ResourceType.REACH.mValue) || TextUtils.equals(str, WeatherRingtoneBase.ResourceType.REMARKS.mValue) || TextUtils.equals(str, WeatherRingtoneBase.ResourceType.TEMP_PREF.mValue) || TextUtils.equals(str, WeatherRingtoneBase.ResourceType.WEATHER.mValue)) {
            weatherType = getWeatherType(weatherInfo);
            if (TextUtils.equals(str, WeatherRingtoneBase.ResourceType.REMARKS.mValue)) {
                str = getResourceRemarksPref(weatherType, str, weatherInfo.tempHigh);
            } else {
                str = getResourcePref(weatherType, str);
            }
        } else {
            weatherType = getMiddleStrByWeather(getWeatherType(weatherInfo));
            if (TextUtils.equals(str, WeatherRingtoneBase.ResourceType.HOUR.mValue)) {
                str = getPrefPath(str) + getResourcePref(weatherType, getHourResource());
            } else if (TextUtils.equals(str, WeatherRingtoneBase.ResourceType.MINUTE.mValue)) {
                str = getPrefPath(str) + getResourcePref(weatherType, getMinuteResource());
            } else if (TextUtils.equals(str, WeatherRingtoneBase.ResourceType.TEMP_LOW.mValue)) {
                str = getPrefPath(str) + getResourcePref(weatherType, getTempLowResource(weatherInfo));
            } else if (TextUtils.equals(str, WeatherRingtoneBase.ResourceType.TEMP_HIGH.mValue)) {
                str = getPrefPath(str) + getResourcePref(weatherType, getTempHighResource(weatherInfo));
            } else if (!TextUtils.equals(str, WeatherRingtoneBase.ResourceType.BIRTHDAY.mValue)) {
                str = null;
            }
        }
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return appendString(getExternalPath(), File.separator, "dynamic_alarm", File.separator, weatherType, File.separator, addSuffix(str));
    }

    private String getResourceRemarksPref(String str, String str2, int i) {
        return appendString("Alarm", "_", str, "_", str2, "_", i > 25 ? "High" : "Low");
    }
}
