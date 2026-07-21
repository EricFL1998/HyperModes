package com.android.deskclock.addition.weather;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageInstallObserver2;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import com.android.deskclock.Alarm;
import com.android.deskclock.BuildConfig;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.ringtone.weather.WeatherRingtoneHelper;
import com.android.deskclock.alarm.lifepost.LifePostUtils;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.stat.OneTrackStatHelper;
import com.android.deskclock.util.stat.StatHelper;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class WeatherUtils {
    private static final String AQILEVEL = "aqilevel";
    private static final String CITY_NAME = "city_name";
    public static final int FBE_WEATHER_VERSION_CODE = 10000200;
    private static final String INSTALL_START = "install_start";
    public static final int INTENT_ACTION_WEATHER_VERSION_CODE = 10000902;
    private static final String LOCAL_WEATHER_CONTENT_NEW = "content://weather/actualWeatherData";
    private static final String NORMAL_START = "normal_start";
    public static final String PREFERENCE_WEATHER_PUBLISH_TIME_FLAG = "weather_publish_time_flag";
    private static final String PUBLISH_TIME = "publish_time";
    private static final long PUBLISH_TIME_VALID_DURATION = 75600000;
    private static final long PUBLISH_TIME_VALID_DURATION_DYNAMIC_ALARM = 43200000;
    private static final long RANDOM_UPDATE_WEATHER_TIME_DURATION = 3000000;
    private static final String TAG = "DC:WeatherUtils";
    private static final String TEMPERATURE = "temperature";
    private static final String TEMPERATURE_RANGE = "temperature_range";
    private static final String TEMPHIGH = "tmphighs";
    private static final String TEMPLOW = "tmplows";
    private static final String UPDATE_WEATHER_DATA = "com.miui.weather2.PRE_UPDATE_WEATHER_DATA";
    private static final String WEATHER_DESC = "description";
    private static final String WEATHER_INTENT_ACTION = "miui.intent.action.weather";
    private static final String WEATHER_PACKAGE_NAME = "com.miui.weather2";
    public static final boolean WEATHER_PUBLISH_TIME_FLAG = false;
    private static final String WEATHER_TEMP_PATTERN = "-*\\d+(\\.\\d+)?";
    private static final String WEATHER_TYPE = "weather_type";
    private static final String WIND = "wind";
    private static final Uri LOCAL_WEATHER_CONTENT_URI = Uri.parse("content://weather/weather");
    public static String DATA_TYPE_OF_LIFE_POST = "1";
    public static String DATA_TYPE_OF_DYNAMIC_ALARM = "2";
    public static int NEW_INTERFACE_WEATHER_VERSION_CODE = 10010200;
    private static String LAST_UPDATE_WEATHER_TIME = "last_update_weather_time";
    private static Pair<Integer, Integer> mWeatherDayRange = new Pair<>(1, 5);

    public static WeatherInfo getLocalWeather(Context context, int i, String str, boolean z) {
        Time time = new Time();
        time.setToNow();
        int julianDay = i - Time.getJulianDay(time.toMillis(true), time.gmtoff);
        int i2 = julianDay + 1;
        Log.d(TAG, "getLocalWeather(), julianDayDelta:" + i2 + ",dataType:" + str);
        try {
            if (i2 < ((Integer) mWeatherDayRange.first).intValue() || i2 > ((Integer) mWeatherDayRange.second).intValue()) {
                return null;
            }
            Cursor cursorQuery = context.getContentResolver().query(getWeatherContentUri(str), null, null, null, null);
            if (cursorQuery != null) {
                try {
                    if (!cursorQuery.moveToPosition(julianDay)) {
                        cursorQuery.close();
                        return null;
                    }
                    String string = cursorQuery.getString(cursorQuery.getColumnIndex(CITY_NAME));
                    String string2 = cursorQuery.getString(cursorQuery.getColumnIndex(TEMPERATURE));
                    String string3 = cursorQuery.getString(cursorQuery.getColumnIndex("description"));
                    String string4 = cursorQuery.getString(cursorQuery.getColumnIndex(TEMPERATURE_RANGE));
                    String string5 = cursorQuery.getString(cursorQuery.getColumnIndex(WIND));
                    int i3 = cursorQuery.getInt(cursorQuery.getColumnIndex(AQILEVEL));
                    long j = cursorQuery.getLong(cursorQuery.getColumnIndex(PUBLISH_TIME));
                    int i4 = cursorQuery.getInt(cursorQuery.getColumnIndex(WEATHER_TYPE));
                    int i5 = cursorQuery.getInt(cursorQuery.getColumnIndex(TEMPLOW));
                    int i6 = cursorQuery.getInt(cursorQuery.getColumnIndex(TEMPHIGH));
                    StatHelper.recordWeatherPublishTimeStringProperty(StatHelper.CATEGORY_LIFE_POST, StatHelper.KEY_LIFE_POST_WEATHER_PUBLISH_TIME, j);
                    OneTrackStatHelper.trackNumEvent((int) ((System.currentTimeMillis() - j) / 60000), OneTrackStatHelper.LIFE_POST_WEATHER_PUBLISH_TIME_DIFF);
                    Log.d(TAG, "getLocalWeather(), weatherType:" + i4 + " ,publishTime:" + Util.formatTimeForLog(j));
                    if (!isWeatherInValidOldInterface(j)) {
                        if (z) {
                            updateWeatherPublishTimeFlag(context, isWeatherValidDynamicAlarm(j));
                            if (!isWeatherValidDynamicAlarm(j)) {
                                cursorQuery.close();
                                return null;
                            }
                        }
                        WeatherInfo weatherInfo = new WeatherInfo();
                        weatherInfo.cityName = string;
                        weatherInfo.weather = string3;
                        weatherInfo.wind = string5;
                        weatherInfo.temperature = string2;
                        weatherInfo.temperatureRange = string4;
                        weatherInfo.aqilevel = i3;
                        weatherInfo.weatherType = i4;
                        weatherInfo.tempLow = i5;
                        weatherInfo.tempHigh = i6;
                        Pattern patternCompile = Pattern.compile(WEATHER_TEMP_PATTERN);
                        if (!TextUtils.isEmpty(string2)) {
                            try {
                                Matcher matcher = patternCompile.matcher(string2);
                                if (matcher.find()) {
                                    weatherInfo.temp = Integer.parseInt(matcher.group(0));
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "get temp error", e);
                                weatherInfo.temp = 0;
                            }
                        }
                        if (!TextUtils.isEmpty(string4)) {
                            try {
                                Matcher matcher2 = patternCompile.matcher(string4);
                                matcher2.find();
                                weatherInfo.tempLow = Integer.parseInt(matcher2.group());
                                matcher2.find();
                                int i7 = Integer.parseInt(matcher2.group());
                                if (weatherInfo.tempLow > i7) {
                                    weatherInfo.tempHigh = weatherInfo.tempLow;
                                    weatherInfo.tempLow = i7;
                                } else {
                                    weatherInfo.tempHigh = i7;
                                }
                            } catch (Exception e2) {
                                Log.e(TAG, "get temp range error range=" + string4, e2);
                                int i8 = weatherInfo.temp;
                                weatherInfo.tempHigh = i8;
                                weatherInfo.tempLow = i8;
                            }
                        }
                        Log.d(TAG, "temp = " + weatherInfo.temp + ", tempHigh=" + weatherInfo.tempHigh + ", tempLow=" + weatherInfo.tempLow);
                        cursorQuery.close();
                        return weatherInfo;
                    }
                    cursorQuery.close();
                    return null;
                } catch (Throwable th) {
                    cursorQuery.close();
                    throw th;
                }
            }
            Log.d(TAG, "getLocalWeather(): cursor is null");
            return null;
        } catch (Exception e3) {
            Log.e(TAG, "getLocalWeather(): get local weather failed", e3);
            return null;
        }
    }

    private static Uri getWeatherContentUri(String str) {
        if (Util.getPackageCode(WEATHER_PACKAGE_NAME) >= NEW_INTERFACE_WEATHER_VERSION_CODE) {
            return Uri.parse(LOCAL_WEATHER_CONTENT_NEW).buildUpon().appendPath(str).build();
        }
        return LOCAL_WEATHER_CONTENT_URI;
    }

    public static void updateWeatherPublishTimeFlag(Context context, boolean z) {
        SharedPreferences.Editor editorEdit = FBEUtil.getDefaultSharedPreferences(context).edit();
        editorEdit.putBoolean(PREFERENCE_WEATHER_PUBLISH_TIME_FLAG, z);
        editorEdit.apply();
    }

    private static boolean isWeatherInValidOldInterface(long j) {
        return Util.getPackageCode(WEATHER_PACKAGE_NAME) < NEW_INTERFACE_WEATHER_VERSION_CODE && !isWeatherValidOldInterface(j);
    }

    private static boolean isWeatherValidDynamicAlarm(long j) {
        return System.currentTimeMillis() - j < PUBLISH_TIME_VALID_DURATION_DYNAMIC_ALARM;
    }

    private static boolean isWeatherValidOldInterface(long j) {
        return System.currentTimeMillis() - j < PUBLISH_TIME_VALID_DURATION;
    }

    private static Intent getWeatherIntentWithoutCheck(Context context) {
        return context.getPackageManager().getLaunchIntentForPackage(WEATHER_PACKAGE_NAME);
    }

    private static boolean isWeatherExist(Context context) {
        try {
            context.getPackageManager().getApplicationInfo(WEATHER_PACKAGE_NAME, 8192);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static void startWeatherOrInstall(Context context) {
        if (startWeather(context, NORMAL_START)) {
            return;
        }
        installWeatherAppRef(context);
    }

    private static boolean startWeather(Context context, String str) {
        Intent weatherIntentWithoutCheck;
        boolean z = false;
        if (!isWeatherExist(context)) {
            return false;
        }
        if (Util.getPackageCode(WEATHER_PACKAGE_NAME) >= 10000902) {
            try {
                Intent intent = new Intent(WEATHER_INTENT_ACTION);
                intent.putExtra("miref", context.getPackageName() + str);
                intent.setFlags(268435456);
                context.startActivity(intent);
                z = true;
            } catch (ActivityNotFoundException unused) {
                Log.e(TAG, "Not found weather activity!");
            }
        }
        if (z || (weatherIntentWithoutCheck = getWeatherIntentWithoutCheck(context)) == null) {
            return z;
        }
        context.startActivity(weatherIntentWithoutCheck);
        return true;
    }

    private static void installWeatherAppRef(final Context context) {
        try {
            Method[] methods = Class.forName("miui.content.pm.PreloadedAppPolicy").getMethods();
            if (methods != null) {
                for (Method method : methods) {
                    if (method.getName().equals("installPreloadedDataApp")) {
                        Object[] objArr = new Object[4];
                        objArr[0] = context;
                        objArr[1] = WEATHER_PACKAGE_NAME;
                        if (Util.isBuildVersionAboveOrEqualP()) {
                            objArr[2] = new IPackageInstallObserver2() { // from class: com.android.deskclock.addition.weather.WeatherUtils.1
                                @Override // android.os.IInterface
                                public IBinder asBinder() {
                                    return null;
                                }

                                @Override // android.content.pm.IPackageInstallObserver2
                                public void onPackageInstalled(String str, int i, String str2, Bundle bundle) throws RemoteException {
                                    Log.d(WeatherUtils.TAG, "IPackageInstallObserver2, weather app installed.");
                                    WeatherUtils.packageInstall(context);
                                }
                            };
                        } else {
                            objArr[2] = new IPackageInstallObserver() { // from class: com.android.deskclock.addition.weather.WeatherUtils.2
                                @Override // android.os.IInterface
                                public IBinder asBinder() {
                                    return null;
                                }

                                @Override // android.content.pm.IPackageInstallObserver
                                public void packageInstalled(String str, int i) throws RemoteException {
                                    Log.d(WeatherUtils.TAG, "IPackageInstallObserver, weather app installed.");
                                    WeatherUtils.packageInstall(context);
                                }
                            };
                        }
                        objArr[3] = 0;
                        method.invoke(null, objArr);
                        Log.d(TAG, "install weather app.");
                        Toast.makeText(DeskClockApp.getAppDEContext(), R.string.install_weather_app_loading, 0).show();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "install error", e);
            Toast.makeText(DeskClockApp.getAppDEContext(), R.string.install_weather_app_error, 0).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void packageInstall(Context context) {
        if (startWeather(context, INSTALL_START)) {
            return;
        }
        Toast.makeText(DeskClockApp.getAppDEContext(), R.string.install_weather_app_error, 0).show();
    }

    public static void updateWeatherBroadcast(Alarm alarm, Context context, boolean z) {
        if (shouldUpdateWeather(alarm, context, z)) {
            long jCurrentTimeMillis = alarm.time - System.currentTimeMillis();
            Intent intent = new Intent(UPDATE_WEATHER_DATA);
            intent.putExtra("miref", BuildConfig.APPLICATION_ID);
            intent.putExtra("update_now", z);
            intent.putExtra("pre_time", jCurrentTimeMillis);
            intent.setClassName(WEATHER_PACKAGE_NAME, "com.miui.weather2.receiver.UpdateWeatherReceiver");
            context.sendBroadcast(intent);
            FBEUtil.getDefaultSharedPreferences(context).edit().putLong(LAST_UPDATE_WEATHER_TIME, System.currentTimeMillis()).apply();
            Log.d(TAG, "UpdateWeather：" + Util.formatTimeForLog(System.currentTimeMillis()));
        }
    }

    private static boolean shouldUpdateWeather(Alarm alarm, Context context, boolean z) {
        HashMap map = new HashMap();
        long jCurrentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(jCurrentTimeMillis);
        String strValueOf = String.valueOf(calendar.get(11));
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(context);
        if (System.currentTimeMillis() - defaultSharedPreferences.getLong(LAST_UPDATE_WEATHER_TIME, 0L) < AlarmHelper.UPDATE_WEATHER_DURATION) {
            Log.d(TAG, "LastUpdateWeatherTime less than 30 minutes：" + Util.formatTimeForLog(defaultSharedPreferences.getLong(LAST_UPDATE_WEATHER_TIME, 0L)));
            return false;
        }
        if (LifePostUtils.isShowLifePost(context, alarm)) {
            Log.d(TAG, "updateWeatherBroadcast for showLifePost");
            map.put(OneTrackStatHelper.PARAM_UPDATE_WEATHER_TYPE, OneTrackStatHelper.UPDATE_FOR_LIFE_POST);
            map.put(OneTrackStatHelper.PARAM_UPDATE_WEATHER_NOW, Boolean.valueOf(z));
            map.put(OneTrackStatHelper.PARAM_UPDATE_WEATHER_TIME, strValueOf);
            OneTrackStatHelper.trackMultiParamEvent(OneTrackStatHelper.EVENT_UPDATE_WEATHER, map, OneTrackStatHelper.TIP_UPDATE_WEATHER);
            return true;
        }
        if (!WeatherRingtoneHelper.isWeatherRingtone(alarm.alert)) {
            return false;
        }
        Log.d(TAG, "updateWeatherBroadcast for weatherRingtone");
        map.put(OneTrackStatHelper.PARAM_UPDATE_WEATHER_TYPE, OneTrackStatHelper.UPDATE_FOR_WEATHER_RINGTONE);
        map.put(OneTrackStatHelper.PARAM_UPDATE_WEATHER_NOW, Boolean.valueOf(z));
        map.put(OneTrackStatHelper.PARAM_UPDATE_WEATHER_TIME, strValueOf);
        OneTrackStatHelper.trackMultiParamEvent(OneTrackStatHelper.EVENT_UPDATE_WEATHER, map, OneTrackStatHelper.TIP_UPDATE_WEATHER);
        return true;
    }

    public static boolean isFBEWeather() {
        return Util.getPackageCode(WEATHER_PACKAGE_NAME) >= 10000200;
    }
}
