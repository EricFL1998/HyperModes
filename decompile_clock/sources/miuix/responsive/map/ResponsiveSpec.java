package miuix.responsive.map;

/* JADX INFO: loaded from: classes3.dex */
public class ResponsiveSpec {
    public static final int EFFECTIVE_SCREEN_ORIENTATION_BOTH = 3;
    public static final int EFFECTIVE_SCREEN_ORIENTATION_LANDSCAPE = 2;
    public static final int EFFECTIVE_SCREEN_ORIENTATION_NONE = 0;
    public static final int EFFECTIVE_SCREEN_ORIENTATION_PORTRAIT = 1;
    public static final int HIDE_MODE_HALF_SCREEN = 2;
    public static final int HIDE_MODE_INVALID = 0;
    public static final int HIDE_MODE_NONE = 7;
    public static final int HIDE_MODE_ONE_THIRD_SCREEN = 1;
    public static final int HIDE_MODE_TWO_THIRD_SCREEN = 4;

    public static boolean isScreenOrientationMatch(int i, int i2) {
        return i2 == 3 || i == i2;
    }
}
