package com.android.deskclock.addition.weather;

import android.content.Context;
import com.android.deskclock.R;

/* JADX INFO: loaded from: classes.dex */
public class WeatherAQIData {
    private static final int AQILEVEL_DANGEROUS = 4;
    private static final int AQILEVEL_GOOD = 0;
    private static final int AQILEVEL_HAZARDOUS = 5;
    private static final int AQILEVEL_LIGHT_POLLUTION = 2;
    private static final int AQILEVEL_MODERATE = 1;
    private static final int AQILEVEL_UNHEALTHY = 3;

    private static int getAQILevelIndex(int i) {
        if (i >= 0 && i <= 50) {
            return 0;
        }
        if (i > 50 && i <= 100) {
            return 1;
        }
        if (i > 100 && i <= 150) {
            return 2;
        }
        if (i > 150 && i <= 200) {
            return 3;
        }
        if (i <= 200 || i > 300) {
            return i > 300 ? 5 : -1;
        }
        return 4;
    }

    public static String getAqiValue(Context context, int i) {
        int aQILevelIndex = getAQILevelIndex(i);
        if (aQILevelIndex == -1) {
            return "";
        }
        String[] stringArray = context.getResources().getStringArray(R.array.aqi_value);
        if (aQILevelIndex >= stringArray.length) {
            return "";
        }
        return stringArray[aQILevelIndex];
    }
}
