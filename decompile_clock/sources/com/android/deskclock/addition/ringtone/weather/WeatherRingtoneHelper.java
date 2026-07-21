package com.android.deskclock.addition.ringtone.weather;

import android.media.RingtoneManager;
import android.net.Uri;
import com.android.deskclock.addition.MiuiTheme;
import com.android.deskclock.addition.resource.ExternalResourceUtils;
import com.android.deskclock.addition.ringtone.RingtoneConstants;
import com.android.deskclock.addition.ringtone.RingtoneUriCompat;
import com.android.deskclock.addition.weather.WeatherInfo;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.FileUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.Util;
import java.util.ArrayList;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class WeatherRingtoneHelper {
    private static final String DEFAULT_DYNAMIC_ALARM_RINGTONE = "/system/media/audio/ui/dynamic_alarm_speech.ogg";
    private static final String TAG = "DC:WeatherAlarm";

    public static int getVersion() {
        return PrefUtil.getMiuiResourceVersion();
    }

    public static String getWeatherRingtonePath() {
        return "/system/media/audio/ui/dynamic_alarm_speech.ogg";
    }

    public static boolean isWeatherRingtoneExist() {
        return FileUtil.isFileExist("/system/media/audio/ui/dynamic_alarm_speech.ogg");
    }

    public static Uri getWeatherRingtoneUri() {
        if (RingtoneUriCompat.atLeastU()) {
            return RingtoneConstants.RINGTONE_URI_WEATHER_NEW;
        }
        return RingtoneConstants.RINGTONE_URI_WEATHER;
    }

    public static boolean isRomSupport() {
        try {
            if (!isWeatherRingtoneExist()) {
                Log.i(TAG, "not support weather ringtone: resource is not exist in rom");
                return false;
            }
            if (MiuiTheme.supportWeatherRingtone()) {
                return true;
            }
            Log.i(TAG, "not support weather ringtone: MiuiTheme not support");
            return false;
        } catch (Exception e) {
            Log.e(TAG, "not support weather ringtone: " + e.getMessage());
            return false;
        }
    }

    public static boolean isDeskclockSupport() {
        return ExternalResourceUtils.supportWeatherRingtone();
    }

    public static boolean isSupport() {
        return isRomSupport() && isDeskclockSupport();
    }

    public static boolean isWeatherRingtone(Uri uri) {
        if (uri == null) {
            Log.e("isWeatherRingtone, null alert");
            return false;
        }
        try {
            Uri uri2 = RingtoneConstants.RINGTONE_URI_WEATHER;
            Uri uri3 = RingtoneConstants.RINGTONE_URI_WEATHER_NEW;
            Uri defaultAlarmRingtone = uri.equals(RingtoneManager.getDefaultUri(4)) ? AlarmRingtoneUtil.getDefaultAlarmRingtone() : uri;
            if (defaultAlarmRingtone == null) {
                return false;
            }
            boolean zContains = defaultAlarmRingtone.toString().contains("title=Alarm_Sunny_Instrument");
            Log.i("isWeatherRingtone, actualWeekRingtoneUri : " + uri2 + "    defaultDynamicUriNew: " + uri3 + "   tempAlert: " + defaultAlarmRingtone);
            return uri2.equals(defaultAlarmRingtone) || uri3.equals(defaultAlarmRingtone) || zContains;
        } catch (Exception e) {
            Log.e("isWeatherRingtone error, alert=" + uri, e);
            return false;
        }
    }

    public static boolean shouldPlayWeatherRingtoneVoice(int i) {
        if (Util.isInternational()) {
            return false;
        }
        if (i == -2) {
            Log.i("shouldPlayWeatherRingtoneVoice false, timer_alarm");
            return false;
        }
        try {
            int i2 = Calendar.getInstance().get(10);
            return i2 >= 4 && i2 < 12 && Calendar.getInstance().get(9) == 0;
        } catch (Exception e) {
            Log.e("shouldPlayWeatherRingtoneVoice error", e);
        }
    }

    public static String getWeatherRingtoneBackground(int i, WeatherInfo weatherInfo) {
        WeatherRingtoneBase weatherRingtoneV2;
        Log.i(TAG, "create weather alarm voice with version: " + i);
        if (i == 2) {
            weatherRingtoneV2 = new WeatherRingtoneV2();
        } else if (i == 3) {
            weatherRingtoneV2 = new WeatherRingtoneV3();
        } else if (i != 4) {
            weatherRingtoneV2 = i != 5 ? null : new WeatherRingtoneV5();
        } else {
            weatherRingtoneV2 = new WeatherRingtoneV4();
        }
        if (weatherRingtoneV2 != null) {
            return weatherRingtoneV2.getWeatherAlarmBackground(weatherInfo);
        }
        return null;
    }

    public static ArrayList<WeatherRingtonePiece> getWeatherRingtoneModel(int i, WeatherInfo weatherInfo) {
        WeatherRingtoneBase weatherRingtoneV2;
        if (i == 2) {
            weatherRingtoneV2 = new WeatherRingtoneV2();
        } else if (i == 3) {
            weatherRingtoneV2 = new WeatherRingtoneV3();
        } else if (i != 4) {
            weatherRingtoneV2 = i != 5 ? null : new WeatherRingtoneV5();
        } else {
            weatherRingtoneV2 = new WeatherRingtoneV4();
        }
        if (weatherRingtoneV2 != null) {
            return weatherRingtoneV2.getWeatherAlarmModel(weatherInfo);
        }
        return null;
    }

    public static String createWeatherRingtoneVoice(int i, WeatherInfo weatherInfo, ArrayList<WeatherRingtonePiece> arrayList) {
        WeatherRingtoneBase weatherRingtoneV2;
        Log.i(TAG, "create weather alarm voice with version: " + i);
        if (i == 2) {
            weatherRingtoneV2 = new WeatherRingtoneV2();
        } else if (i == 3) {
            weatherRingtoneV2 = new WeatherRingtoneV3();
        } else if (i != 4) {
            weatherRingtoneV2 = i != 5 ? null : new WeatherRingtoneV5();
        } else {
            weatherRingtoneV2 = new WeatherRingtoneV4();
        }
        if (weatherRingtoneV2 != null) {
            return weatherRingtoneV2.createWeatherAlarm(weatherInfo, arrayList);
        }
        return null;
    }
}
