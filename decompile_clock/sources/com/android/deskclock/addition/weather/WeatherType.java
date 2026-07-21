package com.android.deskclock.addition.weather;

import com.android.deskclock.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class WeatherType {
    public static final int CLOUDY = 1;
    public static final int FLOAT_DIRT = 23;
    public static final int FOG = 3;
    public static final int FREEZING_RAIN = 25;
    public static final int HEAVY_RAIN = 9;
    public static final int HEAVY_SAND = 19;
    public static final int HEAVY_SNOW = 15;
    public static final int HEAVY_STORM = 5;
    public static final int ICE_RAIN = 22;
    public static final int MIDDLE_RAIN = 10;
    public static final int MIDDLE_SAND = 20;
    public static final int MIDDLE_SNOW = 16;
    public static final int NULLDATA = 99;
    public static final int OVERCAST = 2;
    public static final int PARTLY_SNOW = 14;
    public static final int PARTLY_STORM = 8;
    public static final int PM_DIRT = 24;
    public static final int RAIN_AND_SNOW = 12;
    public static final int SMALL_RAIN = 11;
    public static final int SMALL_SAND = 21;
    public static final int SMALL_SNOW = 17;
    public static final int STORM = 6;
    public static final int SUNNY = 0;
    public static final int SUPER_SAND = 18;
    public static final int SUPER_SNOW = 13;
    public static final int SUPER_STORM = 4;
    public static final int T_STORM = 7;

    public static final class NEW_WEATHER_TYPE {
        public static final int CLOUDY = 1;
        public static final int FOG = 5;
        public static final int HAZE = 6;
        public static final int NULLDATA = -1;
        public static final int OVERCAST = 2;
        public static final int RAIN = 3;
        public static final int SMALL_SAND = 7;
        public static final int SNOW = 4;
        public static final int SUNNY = 0;
    }

    public static final class WEATHER_TIPS_TYPE {
        public static final int FOG = 3;
        public static final int FREEZING_RAIN = 1;
        public static final int ICE_RAIN = 7;
        public static final int NORMAL = 9;
        public static final int PMDIRT = 4;
        public static final int POLLUTION = 0;
        public static final int RAIN = 5;
        public static final int SAND = 8;
        public static final int SNOW = 6;
        public static final int T_STORM = 2;
    }

    public static boolean isFogOrPmdirt(int i) {
        return i == 3 || (i >= 18 && i <= 24 && i != 22);
    }

    public static int getShownWeatherType(int i) {
        Log.d("getShownWeatherType:" + i);
        if (i >= 0 && i <= 25) {
            if (i == 0) {
                return 0;
            }
            if (i == 1) {
                return 1;
            }
            if (i == 2) {
                return 2;
            }
            if ((i >= 4 && i <= 12) || i == 25) {
                return 3;
            }
            if ((i >= 13 && i <= 17) || i == 22) {
                return 4;
            }
            if (i == 3) {
                return 5;
            }
            if (i == 24) {
                return 6;
            }
            if ((i >= 18 && i <= 21) || i == 23) {
                return 6;
            }
        }
        return -1;
    }
}
