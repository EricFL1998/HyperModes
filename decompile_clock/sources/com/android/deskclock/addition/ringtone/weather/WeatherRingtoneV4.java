package com.android.deskclock.addition.ringtone.weather;

import android.content.SharedPreferences;
import android.text.TextUtils;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.addition.weather.WeatherInfo;
import com.android.deskclock.util.AudioUtil;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class WeatherRingtoneV4 extends WeatherRingtoneBase {
    public static final String KEY_WEATHER_V4_AXIS_CLOUDY = "weather_v4_axis_cloudy";
    public static final String KEY_WEATHER_V4_AXIS_CLOUDY_UPDATE_TIME = "weather_v4_axis_cloudy_update_time";
    public static final String KEY_WEATHER_V4_AXIS_OVERCAST = "weather_v4_axis_overcast";
    public static final String KEY_WEATHER_V4_AXIS_OVERCAST_UPDATE_TIME = "weather_v4_axis_overcast_update_time";
    public static final String KEY_WEATHER_V4_AXIS_RAIN = "weather_v4_axis_rain";
    public static final String KEY_WEATHER_V4_AXIS_RAIN_UPDATE_TIME = "weather_v4_axis_rain_update_time";
    public static final String KEY_WEATHER_V4_AXIS_SNOW = "weather_v4_axis_snow";
    public static final String KEY_WEATHER_V4_AXIS_SNOW_UPDATE_TIME = "weather_v4_axis_snow_update_time";
    public static final String KEY_WEATHER_V4_AXIS_SUNNY = "weather_v4_axis_sunny";
    public static final String KEY_WEATHER_V4_AXIS_SUNNY_UPDATE_TIME = "weather_v4_axis_sunny_update_time";
    private static final String TIME_AXIS_DATA_TIME_AXIS_ONE = "time_axis_one";
    private static final String TIME_AXIS_DATA_TIME_AXIS_TWO = "time_axis_two";
    private static final String TIME_AXIS_FILE = "alarm/weather_alarm_time_axis_v4.json";
    public static final int VALUE_DEFAULT_UPDATE_TIME = 0;
    public static final int VALUE_WEATHER_V4_AXIS_ONE = 1;
    public static final int VALUE_WEATHER_V4_AXIS_TWO = 2;
    private static JSONObject sTimeAxisOneJsonObject;
    private static JSONObject sTimeAxisTwoJsonObject;

    private int changeAxisFromOld(int i) {
        return i == 1 ? 2 : 1;
    }

    @Override // com.android.deskclock.addition.ringtone.weather.WeatherRingtoneBase
    protected int getVersion() {
        return 4;
    }

    @Override // com.android.deskclock.addition.ringtone.weather.WeatherRingtoneBase
    public String getWeatherAlarmBackground(WeatherInfo weatherInfo) {
        if (weatherInfo == null) {
            return null;
        }
        String weatherType = getWeatherType(weatherInfo);
        if (TextUtils.isEmpty(weatherType)) {
            return null;
        }
        addWeatherStatistics(weatherType);
        if (weatherType.equals("UNKNOWN")) {
            Log.i("DC:WeatherRingtone", "unknown weather: play snow background");
            weatherType = "Snow";
        }
        updateAxisForWeather(weatherType);
        return getAlarmBackgroundResource(weatherType, getAxisForWeather(weatherType));
    }

    @Override // com.android.deskclock.addition.ringtone.weather.WeatherRingtoneBase
    public ArrayList<WeatherRingtonePiece> getWeatherAlarmModel(WeatherInfo weatherInfo) {
        JSONArray jSONArray;
        if (weatherInfo == null) {
            return null;
        }
        try {
            String weatherType = getWeatherType(weatherInfo);
            if (TextUtils.isEmpty(weatherType)) {
                return null;
            }
            if (weatherType.equals("UNKNOWN")) {
                Log.e("DC:WeatherRingtone", "unknown weather: do not play weather ringtone voice");
                return null;
            }
            if (getAxisForWeather(weatherType) == 2) {
                if (sTimeAxisTwoJsonObject == null) {
                    sTimeAxisTwoJsonObject = buildTimeAxisJsonArray(2);
                }
                JSONObject jSONObject = sTimeAxisTwoJsonObject;
                if (jSONObject == null) {
                    return null;
                }
                jSONArray = jSONObject.getJSONArray(weatherType);
            } else {
                if (sTimeAxisOneJsonObject == null) {
                    sTimeAxisOneJsonObject = buildTimeAxisJsonArray(1);
                }
                JSONObject jSONObject2 = sTimeAxisOneJsonObject;
                if (jSONObject2 == null) {
                    return null;
                }
                jSONArray = jSONObject2.getJSONArray(weatherType);
            }
            if (jSONArray != null && jSONArray.length() != 0) {
                ArrayList<WeatherRingtonePiece> arrayList = new ArrayList<>();
                int length = jSONArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jSONObject3 = jSONArray.getJSONObject(i);
                    WeatherRingtonePiece weatherRingtonePiece = new WeatherRingtonePiece();
                    weatherRingtonePiece.type = jSONObject3.optString("type");
                    weatherRingtonePiece.time = jSONObject3.optLong("time");
                    weatherRingtonePiece.path = getAlarmResource(weatherRingtonePiece.type, weatherInfo);
                    arrayList.add(weatherRingtonePiece);
                }
                return arrayList;
            }
            Log.e("getTimeAxis error, null array");
            return null;
        } catch (Exception e) {
            Log.e("getTimeAxis error: " + e.getMessage());
            return null;
        }
    }

    @Override // com.android.deskclock.addition.ringtone.weather.WeatherRingtoneBase
    public String createWeatherAlarm(WeatherInfo weatherInfo, ArrayList<WeatherRingtonePiece> arrayList) {
        if (arrayList == null || arrayList.size() == 0) {
            Log.e("DC:AlarmService", "prepareDynamicResource error, null resource");
            return null;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            WeatherRingtonePiece weatherRingtonePiece = arrayList.get(i);
            if (weatherRingtonePiece != null && (TextUtils.equals(weatherRingtonePiece.type, WeatherRingtoneBase.ResourceType.HOUR.mValue) || TextUtils.equals(weatherRingtonePiece.type, WeatherRingtoneBase.ResourceType.MINUTE.mValue))) {
                weatherRingtonePiece.path = getAlarmResource(weatherRingtonePiece.type, weatherInfo);
            }
        }
        return AudioUtil.getDynamicAlarmResource(arrayList);
    }

    /* JADX WARN: Code duplicated, block: B:44:0x0076 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Not initialized variable reg: 3, insn: 0x0073: MOVE (r2 I:??[OBJECT, ARRAY]) = (r3 I:??[OBJECT, ARRAY]), block:B:37:0x0073 */
    private JSONObject buildTimeAxisJsonArray(int i) throws Throwable {
        BufferedReader bufferedReader;
        BufferedReader bufferedReader2;
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader3 = null;
        try {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(DeskClockApp.getAppDEContext().getAssets().open(TIME_AXIS_FILE)));
                while (true) {
                    try {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        sb.append(line);
                    } catch (Exception e) {
                        e = e;
                        Log.e("getTimeAxis error", e);
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e2) {
                                Log.e("close reader error", e2);
                            }
                        }
                        return null;
                    }
                }
                JSONObject jSONObject = new JSONObject(sb.toString());
                JSONObject jSONObjectOptJSONObject = i == 2 ? jSONObject.optJSONObject(TIME_AXIS_DATA_TIME_AXIS_TWO) : jSONObject.optJSONObject(TIME_AXIS_DATA_TIME_AXIS_ONE);
                if (jSONObjectOptJSONObject != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e3) {
                        Log.e("close reader error", e3);
                    }
                    return jSONObjectOptJSONObject;
                }
                Log.e("getTimeAxis error, null typeObject");
                try {
                    bufferedReader.close();
                } catch (IOException e4) {
                    Log.e("close reader error", e4);
                }
                return null;
            } catch (Exception e5) {
                e = e5;
                bufferedReader = null;
            } catch (Throwable th) {
                th = th;
                if (bufferedReader3 != null) {
                    try {
                        bufferedReader3.close();
                    } catch (IOException e6) {
                        Log.e("close reader error", e6);
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            bufferedReader3 = bufferedReader2;
            if (bufferedReader3 != null) {
                bufferedReader3.close();
            }
            throw th;
        }
    }

    private String getAlarmBackgroundResource(String str, int i) {
        return appendString(getExternalPath(), File.separator, "dynamic_alarm", File.separator, str, File.separator, addSuffix(getResourcePref(str, WeatherRingtoneBase.ResourceType.BACKGROUND.mValue + String.valueOf(i))));
    }

    private String getAlarmResource(String str, WeatherInfo weatherInfo) {
        String weatherType;
        String resourcePref;
        if (TextUtils.equals(str, WeatherRingtoneBase.ResourceType.REGARD.mValue) || TextUtils.equals(str, WeatherRingtoneBase.ResourceType.REACH.mValue) || TextUtils.equals(str, WeatherRingtoneBase.ResourceType.TEMP_PREF.mValue) || TextUtils.equals(str, WeatherRingtoneBase.ResourceType.WEATHER.mValue)) {
            weatherType = getWeatherType(weatherInfo);
            resourcePref = getResourcePref(weatherType, str);
        } else {
            weatherType = getMiddleStrByWeather(getWeatherType(weatherInfo));
            if (TextUtils.equals(str, WeatherRingtoneBase.ResourceType.HOUR.mValue)) {
                resourcePref = getPrefPath(str) + getResourcePref(weatherType, getHourResource());
            } else if (TextUtils.equals(str, WeatherRingtoneBase.ResourceType.MINUTE.mValue)) {
                resourcePref = getPrefPath(str) + getResourcePref(weatherType, getMinuteResource());
            } else if (TextUtils.equals(str, WeatherRingtoneBase.ResourceType.TEMP_LOW.mValue)) {
                resourcePref = getPrefPath(str) + getResourcePref(weatherType, getTempLowResource(weatherInfo));
            } else {
                resourcePref = TextUtils.equals(str, WeatherRingtoneBase.ResourceType.TEMP_HIGH.mValue) ? getPrefPath(str) + getResourcePref(weatherType, getTempHighResource(weatherInfo)) : null;
            }
        }
        if (TextUtils.isEmpty(resourcePref)) {
            return null;
        }
        return appendString(getExternalPath(), File.separator, "dynamic_alarm", File.separator, weatherType, File.separator, addSuffix(resourcePref));
    }

    private String getWeatherType(WeatherInfo weatherInfo) {
        if (weatherInfo == null) {
            Log.e("getWeatherResource error, null info");
            return "UNKNOWN";
        }
        int i = weatherInfo.weatherType;
        if (i == 0) {
            return "Sunny";
        }
        if (i == 1) {
            return "Cloudy";
        }
        if (i == 2 || i == 3 || i == 24) {
            return "Overcast";
        }
        if ((i >= 4 && i <= 11) || i == 22 || i == 25) {
            return "Rain";
        }
        if (i >= 12 && i <= 17) {
            return "Snow";
        }
        Log.i("getWeatherResource error, unknown weather type =" + i);
        return "UNKNOWN";
    }

    private int getAxisForWeather(String str) {
        if (TextUtils.isEmpty(str)) {
            return 1;
        }
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext());
        if (str.equals("Rain")) {
            return defaultSharedPreferences.getInt(KEY_WEATHER_V4_AXIS_RAIN, 1);
        }
        if (str.equals("Sunny")) {
            return defaultSharedPreferences.getInt(KEY_WEATHER_V4_AXIS_SUNNY, 1);
        }
        if (str.equals("Cloudy")) {
            return defaultSharedPreferences.getInt(KEY_WEATHER_V4_AXIS_CLOUDY, 1);
        }
        if (str.equals("Overcast")) {
            return defaultSharedPreferences.getInt(KEY_WEATHER_V4_AXIS_OVERCAST, 1);
        }
        if (str.equals("Snow")) {
            return defaultSharedPreferences.getInt(KEY_WEATHER_V4_AXIS_SNOW, 1);
        }
        return 1;
    }

    private void updateAxisForWeather(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext());
        long jStartTimeToday = Util.startTimeToday();
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (str.equals("Rain")) {
            if (defaultSharedPreferences.getLong(KEY_WEATHER_V4_AXIS_RAIN_UPDATE_TIME, 0L) < jStartTimeToday) {
                defaultSharedPreferences.edit().putInt(KEY_WEATHER_V4_AXIS_RAIN, changeAxisFromOld(defaultSharedPreferences.getInt(KEY_WEATHER_V4_AXIS_RAIN, 1))).apply();
                defaultSharedPreferences.edit().putLong(KEY_WEATHER_V4_AXIS_RAIN_UPDATE_TIME, jCurrentTimeMillis).apply();
                return;
            }
            return;
        }
        if (str.equals("Sunny")) {
            if (defaultSharedPreferences.getLong(KEY_WEATHER_V4_AXIS_SUNNY_UPDATE_TIME, 0L) < jStartTimeToday) {
                defaultSharedPreferences.edit().putInt(KEY_WEATHER_V4_AXIS_SUNNY, changeAxisFromOld(defaultSharedPreferences.getInt(KEY_WEATHER_V4_AXIS_SUNNY, 1))).apply();
                defaultSharedPreferences.edit().putLong(KEY_WEATHER_V4_AXIS_SUNNY_UPDATE_TIME, jCurrentTimeMillis).apply();
                return;
            }
            return;
        }
        if (str.equals("Cloudy")) {
            if (defaultSharedPreferences.getLong(KEY_WEATHER_V4_AXIS_CLOUDY_UPDATE_TIME, 0L) < jStartTimeToday) {
                defaultSharedPreferences.edit().putInt(KEY_WEATHER_V4_AXIS_CLOUDY, changeAxisFromOld(defaultSharedPreferences.getInt(KEY_WEATHER_V4_AXIS_CLOUDY, 1))).apply();
                defaultSharedPreferences.edit().putLong(KEY_WEATHER_V4_AXIS_CLOUDY_UPDATE_TIME, jCurrentTimeMillis).apply();
                return;
            }
            return;
        }
        if (str.equals("Overcast")) {
            if (defaultSharedPreferences.getLong(KEY_WEATHER_V4_AXIS_OVERCAST_UPDATE_TIME, 0L) < jStartTimeToday) {
                defaultSharedPreferences.edit().putInt(KEY_WEATHER_V4_AXIS_OVERCAST, changeAxisFromOld(defaultSharedPreferences.getInt(KEY_WEATHER_V4_AXIS_OVERCAST, 1))).apply();
                defaultSharedPreferences.edit().putLong(KEY_WEATHER_V4_AXIS_OVERCAST_UPDATE_TIME, jCurrentTimeMillis).apply();
                return;
            }
            return;
        }
        if (!str.equals("Snow") || defaultSharedPreferences.getLong(KEY_WEATHER_V4_AXIS_SNOW_UPDATE_TIME, 0L) >= jStartTimeToday) {
            return;
        }
        defaultSharedPreferences.edit().putInt(KEY_WEATHER_V4_AXIS_SNOW, changeAxisFromOld(defaultSharedPreferences.getInt(KEY_WEATHER_V4_AXIS_SNOW, 1))).apply();
        defaultSharedPreferences.edit().putLong(KEY_WEATHER_V4_AXIS_SNOW_UPDATE_TIME, jCurrentTimeMillis).apply();
    }
}
