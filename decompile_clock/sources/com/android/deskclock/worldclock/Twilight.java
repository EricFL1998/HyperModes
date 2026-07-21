package com.android.deskclock.worldclock;

import com.android.deskclock.util.Log;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class Twilight {
    public static final int DEFAULT_TWILIGHT_POINT_QUARTER_COUNT = 90;
    private static final float HOUR_OF_DAY = 24.0f;
    public static final float MAX_DEGREE_OF_CIRCLE = 360.0f;
    private static final float MAX_OF_DIRECT_POINT_LAT = 23.26f;
    private static final float MILLIS_OF_DAY = 8.64E7f;
    public static final float MILLIS_OF_HOUR = 3600000.0f;
    private static final String TAG = "Twilight: ";

    public static double getAngle(double d) {
        return (d * 180.0d) / 3.141592653589793d;
    }

    public static double getRadians(double d) {
        return (d / 180.0d) * 3.141592653589793d;
    }

    public static class TwilightPoint implements Comparable<TwilightPoint> {
        public float x;
        public float y;

        public TwilightPoint() {
        }

        public TwilightPoint(float f, float f2) {
            this.x = f;
            this.y = f2;
        }

        public final boolean equals(float f, float f2) {
            return this.x == f && this.y == f2;
        }

        public boolean equals(TwilightPoint twilightPoint) {
            if (twilightPoint == null) {
                return false;
            }
            if (this == twilightPoint) {
                return true;
            }
            return equals(twilightPoint.x, twilightPoint.y);
        }

        public String toString() {
            return "TwilightPoint(" + this.x + ", " + this.y + ")";
        }

        @Override // java.lang.Comparable
        public int compareTo(TwilightPoint twilightPoint) {
            float f = this.x - twilightPoint.x;
            if (f > 0.0f) {
                return 1;
            }
            return f < 0.0f ? -1 : 0;
        }
    }

    private static boolean isZeroLat(Calendar calendar, Calendar calendar2) {
        try {
            return calendar.get(1) == calendar2.get(1) && calendar.get(2) == calendar2.get(2) && calendar.get(5) == calendar2.get(5);
        } catch (Exception e) {
            Log.e("dayOffset error", e);
            return false;
        }
    }

    public static double dayOffset(Calendar calendar, Calendar calendar2) {
        try {
            return Math.ceil((calendar.getTimeInMillis() - calendar2.getTimeInMillis()) / MILLIS_OF_DAY);
        } catch (Exception e) {
            Log.e("dayOffset error", e);
            return 0.0d;
        }
    }
}
