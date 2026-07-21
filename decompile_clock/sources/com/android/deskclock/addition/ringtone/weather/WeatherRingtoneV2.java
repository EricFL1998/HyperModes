package com.android.deskclock.addition.ringtone.weather;

import android.text.TextUtils;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.addition.weather.WeatherInfo;
import com.android.deskclock.util.AudioUtil;
import com.android.deskclock.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class WeatherRingtoneV2 extends WeatherRingtoneBase {
    private final String TIME_AXIS_FILE = "alarm/weather_alarm_time_axis_v2.json";
    private final String TIME_AXIS_DATA_TIME_AXIS_ONE = "time_axis_one";
    private JSONObject sTimeAxisJsonObject = null;

    @Override // com.android.deskclock.addition.ringtone.weather.WeatherRingtoneBase
    protected int getVersion() {
        return 2;
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
            Log.i("DC:WeatherRingtone", "unknown weather: play sunny background");
            weatherType = "Sunny";
        }
        return getAlarmBackgroundResource(weatherType);
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

    @Override // com.android.deskclock.addition.ringtone.weather.WeatherRingtoneBase
    public ArrayList<WeatherRingtonePiece> getWeatherAlarmModel(WeatherInfo weatherInfo) {
        if (this.sTimeAxisJsonObject == null) {
            this.sTimeAxisJsonObject = buildTimeAxisJsonArray();
        }
        if (this.sTimeAxisJsonObject == null) {
            return null;
        }
        String weatherType = getWeatherType(weatherInfo);
        if (TextUtils.isEmpty(weatherType)) {
            Log.e("getTimeAxis error, null weather");
            return null;
        }
        if (weatherType.equals("UNKNOWN")) {
            Log.e("DC:WeatherRingtone", "unknown weather: do not play weather ringtone voice");
            return null;
        }
        try {
            JSONArray jSONArray = this.sTimeAxisJsonObject.getJSONArray(weatherType);
            if (jSONArray != null && jSONArray.length() != 0) {
                ArrayList<WeatherRingtonePiece> arrayList = new ArrayList<>();
                int length = jSONArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                    WeatherRingtonePiece weatherRingtonePiece = new WeatherRingtonePiece();
                    weatherRingtonePiece.type = jSONObject.optString("type");
                    weatherRingtonePiece.time = jSONObject.optLong("time");
                    weatherRingtonePiece.time2 = 0L;
                    weatherRingtonePiece.path = getAlarmResource(weatherRingtonePiece.type, weatherInfo);
                    Log.d("type=" + weatherRingtonePiece.type + ",path=" + weatherRingtonePiece.path);
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

    protected String getAxis() {
        return "alarm/weather_alarm_time_axis_v2.json";
    }

    /* JADX WARN: Code duplicated, block: B:45:0x006e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Not initialized variable reg: 3, insn: 0x006b: MOVE (r2 I:??[OBJECT, ARRAY]) = (r3 I:??[OBJECT, ARRAY]), block:B:34:0x006b */
    protected JSONObject buildTimeAxisJsonArray() throws Throwable {
        BufferedReader bufferedReader;
        BufferedReader bufferedReader2;
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader3 = null;
        try {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(DeskClockApp.getAppDEContext().getAssets().open(getAxis())));
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
                JSONObject jSONObjectOptJSONObject = new JSONObject(sb.toString()).optJSONObject("time_axis_one");
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

    public String getAlarmBackgroundResource(String str) {
        return appendString(getExternalPath(), File.separator, "dynamic_alarm", File.separator, str, File.separator, addSuffix(getResourcePref(str, WeatherRingtoneBase.ResourceType.BACKGROUND.mValue)));
    }

    protected String getAlarmResource(String str, WeatherInfo weatherInfo) {
        String weatherType;
        if (TextUtils.equals(str, WeatherRingtoneBase.ResourceType.REGARD.mValue) || TextUtils.equals(str, WeatherRingtoneBase.ResourceType.REACH.mValue) || TextUtils.equals(str, WeatherRingtoneBase.ResourceType.REMARKS.mValue) || TextUtils.equals(str, WeatherRingtoneBase.ResourceType.TEMP_PREF.mValue) || TextUtils.equals(str, WeatherRingtoneBase.ResourceType.WEATHER.mValue)) {
            weatherType = getWeatherType(weatherInfo);
            str = getResourcePref(weatherType, str);
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

    public String getWeatherType(WeatherInfo weatherInfo) {
        if (weatherInfo == null) {
            Log.e("getWeatherResource error, null info");
            return "UNKNOWN";
        }
        int i = weatherInfo.weatherType;
        if (i == 0) {
            return "Sunny";
        }
        if (i == 2 || i == 1) {
            return "Cloudy";
        }
        if ((i <= 11 && i >= 4) || i == 25) {
            return "Rain";
        }
        if (i <= 17 && i >= 13) {
            return "Snow";
        }
        if (i <= 23 && i >= 18 && i != 22) {
            return "Wind";
        }
        Log.i("getWeatherResource error, unknown weather type =" + i);
        return "UNKNOWN";
    }
}
