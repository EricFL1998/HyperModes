package miuix.navigation.utils;

import android.graphics.Point;

/* JADX INFO: loaded from: classes.dex */
public class Utils {
    public static float getNaviRatio(Point point) {
        return (getNaviWidth(point) * 1.0f) / point.x;
    }

    public static int getNaviWidth(Point point) {
        float naviCandidateRatio = getNaviCandidateRatio(point.x);
        float naviCandidateRatio2 = getNaviCandidateRatio(point.y);
        int i = (int) (point.x * naviCandidateRatio);
        int i2 = (int) (point.y * naviCandidateRatio2);
        return Math.abs(i - i2) < 20 ? Math.max(i, i2) : i;
    }

    private static float getNaviCandidateRatio(int i) {
        double dMin;
        double d;
        float f = i - 750;
        if (f < 0.0f) {
            dMin = ((double) Math.min((-f) / 100.0f, 1.0f)) * (-0.020000000000000018d);
            d = 0.44d;
        } else {
            dMin = ((double) Math.min(f / 600.0f, 1.0f)) * (-0.09000000000000002d);
            d = 0.39d;
        }
        return (float) (dMin + d);
    }
}
